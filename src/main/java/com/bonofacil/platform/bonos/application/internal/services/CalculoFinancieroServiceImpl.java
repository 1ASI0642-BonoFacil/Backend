package com.bonofacil.platform.bonos.application.internal.services;

import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import com.bonofacil.platform.bonos.domain.model.entities.Calculo;
import com.bonofacil.platform.bonos.domain.model.entities.FlujoFinanciero;
import com.bonofacil.platform.bonos.domain.services.CalculoFinancieroService;
import com.bonofacil.platform.bonos.infrastructure.persistence.jpa.repositories.CalculoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CalculoFinancieroServiceImpl implements CalculoFinancieroService {

    private static final int SCALE = 10;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final MathContext MC = new MathContext(SCALE, ROUNDING_MODE);

    private final CalculoRepository calculoRepository;

    public CalculoFinancieroServiceImpl(CalculoRepository calculoRepository) {
        this.calculoRepository = calculoRepository;
    }

    @Override
    public List<FlujoFinanciero> calcularFlujoFinanciero(Bono bono) {
        BigDecimal valorNominal = bono.getValorNominal();
        BigDecimal tasaCupon = bono.getTasaCupon().divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        int plazoAnios = bono.getPlazoAnios();
        int frecuenciaPagos = bono.getFrecuenciaPagos();
        int totalPeriodos = plazoAnios * frecuenciaPagos;
        // Variables eliminadas - lógica simplificada sin períodos de gracia complejos
        LocalDate fechaEmision = bono.getFechaEmision();
        
        // Identificar el método de amortización real
        String metodoReal = identificarMetodoAmortizacion(bono);

        List<FlujoFinanciero> flujos = new ArrayList<>();

        // Período 0 - Desembolso inicial
        FlujoFinanciero flujoInicial = new FlujoFinanciero();
        flujoInicial.setBono(bono);
        flujoInicial.setPeriodo(0);
        flujoInicial.setFecha(fechaEmision);
        flujoInicial.setCupon(BigDecimal.ZERO);
        flujoInicial.setAmortizacion(BigDecimal.ZERO);
        flujoInicial.setInteres(BigDecimal.ZERO);
        flujoInicial.setSaldoInsoluto(valorNominal);
        flujoInicial.setSaldo(valorNominal);
        flujoInicial.setFlujoTotal(valorNominal.negate());
        flujoInicial.setFlujo(valorNominal.negate());
        flujos.add(flujoInicial);

        // Calcular tasa periódica
        BigDecimal tasaPeriodica = tasaCupon.divide(BigDecimal.valueOf(frecuenciaPagos), SCALE, ROUNDING_MODE);
        
        // NUEVA LÓGICA SIMPLE Y ROBUSTA para MÉTODO AMERICANO
        for (int i = 1; i <= totalPeriodos; i++) {
            FlujoFinanciero flujo = new FlujoFinanciero();
            flujo.setBono(bono);
            flujo.setPeriodo(i);
            
            // Calcular fecha del período
            LocalDate fechaPeriodo = fechaEmision.plus(i * 12 / frecuenciaPagos, ChronoUnit.MONTHS);
            flujo.setFecha(fechaPeriodo);

            // LÓGICA SIMPLE: Cupón constante siempre, amortización solo al final
            BigDecimal cuponPeriodo = valorNominal.multiply(tasaPeriodica, MC);
            BigDecimal amortizacion;
            BigDecimal flujoTotal;
            BigDecimal saldoRestante;
            
            if (i == totalPeriodos) {
                // ÚLTIMO PERÍODO: Cupón + Valor Nominal completo
                amortizacion = valorNominal;
                flujoTotal = cuponPeriodo.add(amortizacion);
                saldoRestante = BigDecimal.ZERO;
            } else {
                // PERÍODOS INTERMEDIOS: Solo cupón
                amortizacion = BigDecimal.ZERO;
                flujoTotal = cuponPeriodo;
                saldoRestante = valorNominal;
            }

            // Asignar valores al flujo
            flujo.setCupon(cuponPeriodo);
            flujo.setInteres(cuponPeriodo);
            flujo.setAmortizacion(amortizacion);
            flujo.setCuota(flujoTotal);
            flujo.setFlujoTotal(flujoTotal);
            flujo.setFlujo(flujoTotal);
            flujo.setSaldoInsoluto(saldoRestante);
            flujo.setSaldo(saldoRestante);

            flujos.add(flujo);
        }

        return flujos;
    }

    @Override
    public BigDecimal calcularTCEA(Bono bono) {
        BigDecimal tasaCupon = bono.getTasaCupon().divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        int frecuenciaPagos = bono.getFrecuenciaPagos();
        
        // Fórmula: (1 + j/m)^m - 1
        BigDecimal tasaEfectivaAnual = BigDecimal.ONE
                .add(tasaCupon.divide(BigDecimal.valueOf(frecuenciaPagos), MC))
                .pow(frecuenciaPagos)
                .subtract(BigDecimal.ONE);
                
        return tasaEfectivaAnual.setScale(8, ROUNDING_MODE);
    }

    @Override
    public BigDecimal calcularTasaEfectivaPeriodica(BigDecimal tasaAnual, int frecuenciaPagos) {
        // Si la tasa viene en porcentaje, convertirla a decimal
        BigDecimal tasaAnualDecimal = tasaAnual;
        if (tasaAnual.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tasaAnualDecimal = tasaAnual.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        // Validar frecuencia de pagos
        if (frecuenciaPagos <= 0) {
            throw new IllegalArgumentException("La frecuencia de pagos debe ser un valor positivo");
        }
        
        // Calculamos la tasa periódica usando logaritmos y exponenciales para mayor precisión
        double tasaAnualDouble = tasaAnualDecimal.doubleValue();
        double tasaPeriodicaDouble = Math.pow(1.0 + tasaAnualDouble, 1.0/frecuenciaPagos) - 1.0;
        
        return new BigDecimal(tasaPeriodicaDouble).setScale(SCALE, ROUNDING_MODE);
    }
    
    @Override
    public BigDecimal calcularDuracion(List<FlujoFinanciero> flujos, BigDecimal tcea) {
        BigDecimal sumaPonderada = BigDecimal.ZERO;
        BigDecimal precio = BigDecimal.ZERO;
        
        // Convertir TCEA a decimal si viene en porcentaje
        BigDecimal tceaDecimal = tcea;
        if (tcea.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tceaDecimal = tcea.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        // Obtener información del bono para calcular correctamente
        Bono bono = null;
        if (!flujos.isEmpty() && flujos.get(0).getBono() != null) {
            bono = flujos.get(0).getBono();
        }
        
        int frecuenciaPagos = (bono != null) ? bono.getFrecuenciaPagos() : 2;
        
        // Calcular tasa periódica para descuento
        BigDecimal tasaPeriodica = calcularTasaEfectivaPeriodica(tceaDecimal, frecuenciaPagos);
        
        // Saltar el periodo 0 (desembolso inicial)
        for (int i = 1; i < flujos.size(); i++) {
            FlujoFinanciero flujo = flujos.get(i);
            
            // Solo considerar flujos positivos reales
            BigDecimal flujoValor = flujo.getFlujoTotal();
            if (flujoValor == null) {
                flujoValor = flujo.getFlujo();
            }
            
            if (flujoValor.compareTo(BigDecimal.ZERO) > 0) {
                // Usar el periodo exacto
                BigDecimal factorTiempo = new BigDecimal(flujo.getPeriodo());
                flujo.setFactorTiempo(factorTiempo);
                
                // Factor de descuento: 1/(1+tasaPeriodica)^periodo
                BigDecimal factorDescuento = BigDecimal.ONE
                        .divide(BigDecimal.ONE.add(tasaPeriodica, MC).pow(flujo.getPeriodo(), MC), SCALE, ROUNDING_MODE);
                flujo.setFactorDescuento(factorDescuento);
                
                BigDecimal valorActual = flujoValor.multiply(factorDescuento, MC);
                flujo.setValorActual(valorActual);
                flujo.setValorPresente(valorActual);
                
                // Acumular para el cálculo de duración: t * VA(flujo)
                sumaPonderada = sumaPonderada.add(factorTiempo.multiply(valorActual, MC), MC);
                
                // Acumular el precio total (suma de valores actuales)
                precio = precio.add(valorActual, MC);
            }
        }
        
        // Duracion = Suma(t * VA(flujo)) / Precio
        if (precio.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal duracion = sumaPonderada.divide(precio, SCALE, ROUNDING_MODE);
            
            // Convertir duracion a años si está en periodos
            BigDecimal duracionAnios = duracion.divide(BigDecimal.valueOf(frecuenciaPagos), SCALE, ROUNDING_MODE);
            
            return duracionAnios;
        }
        
        return BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal calcularDuracion(Bono bono) {
        List<FlujoFinanciero> flujos = calcularFlujoFinanciero(bono);
        return calcularDuracion(flujos, bono.getTasaCupon());
    }

    @Override
    public BigDecimal calcularConvexidad(List<FlujoFinanciero> flujos, BigDecimal tcea) {
        BigDecimal sumaConvexidad = BigDecimal.ZERO;
        BigDecimal precio = BigDecimal.ZERO;
        
        // Convertir TCEA a decimal si viene en porcentaje
        BigDecimal tceaDecimal = tcea;
        if (tcea.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tceaDecimal = tcea.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        // Obtener información del bono para calcular correctamente
        Bono bono = null;
        if (!flujos.isEmpty() && flujos.get(0).getBono() != null) {
            bono = flujos.get(0).getBono();
        }
        
        int frecuenciaPagos = (bono != null) ? bono.getFrecuenciaPagos() : 2;
        
        // Calcular tasa periódica para descuento
        BigDecimal tasaPeriodica = calcularTasaEfectivaPeriodica(tceaDecimal, frecuenciaPagos);
        
        // Saltar el periodo 0 (desembolso inicial)
        for (int i = 1; i < flujos.size(); i++) {
            FlujoFinanciero ff = flujos.get(i);
            
            // Solo considerar flujos positivos reales
            BigDecimal flujoValor = ff.getFlujoTotal();
            if (flujoValor == null) {
                flujoValor = ff.getFlujo();
            }
            
            if (flujoValor.compareTo(BigDecimal.ZERO) > 0) {
                // Usar el periodo exacto
                int periodo = ff.getPeriodo();
                BigDecimal t = new BigDecimal(periodo);
                BigDecimal tMasUno = t.add(BigDecimal.ONE);
                
                // Factor de descuento: 1/(1+tasaPeriodica)^periodo
                BigDecimal factorDescuento = BigDecimal.ONE
                        .divide(BigDecimal.ONE.add(tasaPeriodica, MC).pow(periodo, MC), SCALE, ROUNDING_MODE);
                
                BigDecimal valorActual = flujoValor.multiply(factorDescuento, MC);
                
                // Fórmula de convexidad: t * (t + 1) * VA(flujo) / (1 + r)^2
                BigDecimal contribucionConvexidad = t.multiply(tMasUno, MC)
                        .multiply(valorActual, MC);
                
                sumaConvexidad = sumaConvexidad.add(contribucionConvexidad, MC);
                
                // Acumular el precio total (suma de valores actuales)
                precio = precio.add(valorActual, MC);
            }
        }
        
        // Convexidad = Suma(t * (t+1) * VA(flujo)) / (Precio * (1+r)^2)
        if (precio.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal divisor = precio.multiply(
                BigDecimal.ONE.add(tasaPeriodica).pow(2, MC), MC
            );
            
            BigDecimal convexidad = sumaConvexidad.divide(divisor, SCALE, ROUNDING_MODE);
            
            // Normalizamos para convertir de periodos a años
            BigDecimal m = new BigDecimal(frecuenciaPagos);
            BigDecimal convexidadAnual = convexidad.divide(m.pow(2), SCALE, ROUNDING_MODE);
            
            return convexidadAnual;
        }
        
        return BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal calcularConvexidad(Bono bono) {
        List<FlujoFinanciero> flujos = calcularFlujoFinanciero(bono);
        return calcularConvexidad(flujos, bono.getTasaCupon());
    }

    @Override
    public BigDecimal calcularPrecioMaximo(List<FlujoFinanciero> flujos, BigDecimal trea) {
        // Asegurarse de que la tasa esté en formato decimal (ej: 0.05 para 5%)
        BigDecimal tasaDecimal = trea;
        if (trea.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tasaDecimal = trea.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        if (flujos == null || flujos.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Obtener la información del bono para determinar la frecuencia de pagos
        Bono bono = flujos.get(0).getBono();
        int frecuenciaPagos = (bono != null) ? bono.getFrecuenciaPagos() : 2;
        
        // Calcular la tasa periódica
        BigDecimal tasaPeriodica = calcularTasaEfectivaPeriodica(tasaDecimal, frecuenciaPagos);
        
        BigDecimal precioMaximo = BigDecimal.ZERO;
        
        // Calcular el valor presente de todos los flujos futuros usando la tasa esperada
        // Saltar el flujo inicial (período 0) que es el desembolso
        for (int i = 1; i < flujos.size(); i++) {
            FlujoFinanciero flujo = flujos.get(i);
            
            // Obtener el flujo total
            BigDecimal flujoValor = flujo.getFlujoTotal();
            if (flujoValor == null || flujoValor.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            
            // Calcular el factor de descuento: 1 / (1 + r)^n
            BigDecimal factorDescuento = BigDecimal.ONE
                    .divide(BigDecimal.ONE.add(tasaPeriodica).pow(flujo.getPeriodo(), MC), SCALE, ROUNDING_MODE);
            
            // Calcular el valor presente de este flujo
            BigDecimal valorPresente = flujoValor.multiply(factorDescuento, MC);
            
            // Acumular al precio máximo
            precioMaximo = precioMaximo.add(valorPresente);
        }
        
        // Redondear a 2 decimales para mostrar como precio
        return precioMaximo.setScale(2, ROUNDING_MODE);
    }
    
    @Override
    public BigDecimal calcularPrecioMaximo(Bono bono, BigDecimal tasaEsperada) {
        List<FlujoFinanciero> flujos = calcularFlujoFinanciero(bono);
        return calcularPrecioMaximo(flujos, tasaEsperada);
    }

    @Override
    public String identificarMetodoAmortizacion(Bono bono) {
        String metodoExplicito = bono.getMetodoAmortizacion();
        
        if (metodoExplicito != null && !metodoExplicito.isEmpty()) {
            return metodoExplicito;
        }
        
        // Por defecto, asumimos método americano (solo amortización al final)
        return "AMERICANO";
    }

    @Override
    public void procesarCalculosBono(Bono bono) {
        // 1. Calculamos el TCEA
        BigDecimal tcea = calcularTCEA(bono);
        bono.setTcea(tcea);
        
        // 2. Generamos flujos financieros
        List<FlujoFinanciero> flujos = calcularFlujoFinanciero(bono);
        
        // 3. Calculamos duración y convexidad
        BigDecimal duracion = calcularDuracion(flujos, bono.getTasaCupon());
        BigDecimal convexidad = calcularConvexidad(flujos, bono.getTasaCupon());
        
        bono.setDuracion(duracion);
        bono.setConvexidad(convexidad);
        
        // 4. Calculamos el precio máximo usando una tasa de mercado por defecto
        // Por ejemplo, podríamos usar TCEA + 1% como tasa de mercado para inversores
        BigDecimal tasaMercado = tcea.add(new BigDecimal("0.01"));
        BigDecimal precioMaximo = calcularPrecioMaximo(flujos, tasaMercado);
        
        // 5. Guardamos la tasa de descuento utilizada
        bono.setTasaDescuento(tasaMercado);
    }

    @Override
    public Calculo calcularInversion(Bono bono, BigDecimal tasaEsperada) {
        // Asegurarse de que la tasa esté en formato decimal (ej: 0.05 para 5%)
        BigDecimal tasaDecimal = tasaEsperada;
        if (tasaEsperada.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tasaDecimal = tasaEsperada.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        Calculo calculo = new Calculo();
        calculo.setBono(bono);
        calculo.setTasaEsperada(tasaEsperada); // Guardamos la tasa en su formato original
        calculo.setFechaCalculo(LocalDate.now());
        
        // Calculamos el precio máximo que debería pagar para obtener la tasa esperada
        BigDecimal precioMaximo = calcularPrecioMaximo(bono, tasaDecimal);
        calculo.setPrecioMaximo(precioMaximo);
        
        // Calculamos la TREA real basada en ese precio máximo
        BigDecimal trea = calcularTREA(bono, precioMaximo);
        calculo.setTrea(trea);
        
        return calculo;
    }

    @Override
    public void procesarCalculosInversor(Calculo calculo) {
        Bono bono = calculo.getBono();
        BigDecimal tasaEsperada = calculo.getTasaEsperada();
        
        // Asegurarse de que la tasa esté en formato decimal
        BigDecimal tasaDecimal = tasaEsperada;
        if (tasaEsperada.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tasaDecimal = tasaEsperada.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        // Calculamos el precio máximo que debería pagar para obtener la tasa esperada
        BigDecimal precioMaximo = calcularPrecioMaximo(bono, tasaDecimal);
        calculo.setPrecioMaximo(precioMaximo);
        
        // Calculamos la TREA real basada en ese precio máximo
        BigDecimal trea = calcularTREA(bono, precioMaximo);
        calculo.setTrea(trea);
        
        // Actualizamos el cálculo
        calculoRepository.save(calculo);
    }

    @Override
    public BigDecimal calcularTREA(Bono bono, BigDecimal precioCompra) {
        // Asegurarse de que el precio de compra esté en formato correcto
        BigDecimal precioCompraDecimal = precioCompra;
        if (precioCompra.compareTo(BigDecimal.valueOf(100)) <= 0) {
            // Si está en formato porcentual del valor nominal, convertir
            precioCompraDecimal = bono.getValorNominal().multiply(precioCompra.divide(BigDecimal.valueOf(100), MC));
        }
        
        // Genera flujos financieros del bono
        List<FlujoFinanciero> flujos = calcularFlujoFinanciero(bono);
        
        // Calcula la TREA como TIR de la inversión
        return calcularTIR(flujos, precioCompraDecimal, bono);
    }
    
    /**
     * Calcula la TIR (Tasa Interna de Retorno) dado un precio de compra
     */
    private BigDecimal calcularTIR(List<FlujoFinanciero> flujos, BigDecimal precioCompra, Bono bono) {
        // Para bonos simples, usar método analítico más preciso
        if (bono.getPlazoAnios() <= 3) {
            return calcularTIRAnalitica(precioCompra, bono);
        }
        
        // Para bonos complejos, usar bisección que es más estable
        return calcularTIRBiseccion(flujos, precioCompra, bono);
    }
    
    /**
     * Calcula TIR analíticamente para bonos simples (hasta 3 años)
     */
    private BigDecimal calcularTIRAnalitica(BigDecimal precioCompra, Bono bono) {
        BigDecimal valorNominal = bono.getValorNominal(); 
        BigDecimal tasaCupon = bono.getTasaCupon().divide(BigDecimal.valueOf(100), MC); // Convertir a decimal
        BigDecimal cuponAnual = valorNominal.multiply(tasaCupon);
        int plazoAnios = bono.getPlazoAnios();
        
        // Para 1 año: TIR = (Cupón + Valor Nominal) / Precio Compra - 1
        if (plazoAnios == 1) {
            BigDecimal flujoTotal = cuponAnual.add(valorNominal);
            BigDecimal tir = flujoTotal.divide(precioCompra, MC).subtract(BigDecimal.ONE);
            return tir.multiply(BigDecimal.valueOf(100)).setScale(2, ROUNDING_MODE);
        }
        
        // Para 2 años: usar fórmula cuadrática
        if (plazoAnios == 2) {
            return calcularTIR2Anios(precioCompra, cuponAnual, valorNominal);
        }
        
        // Para 3 años: usar bisección simplificada
        return calcularTIRBiseccionSimple(precioCompra, cuponAnual, valorNominal, plazoAnios);
    }
    
    /**
     * Calcula TIR para bono de 2 años usando fórmula cuadrática
     */
    private BigDecimal calcularTIR2Anios(BigDecimal precio, BigDecimal cupon, BigDecimal valorNominal) {
        // Ecuación: 0 = -P + C/(1+r) + (C+VN)/(1+r)²
        // Reorganizando: P(1+r)² = C(1+r) + (C+VN)
        // P(1+r)² - C(1+r) - (C+VN) = 0
        // Sustituyendo x = (1+r): Px² - Cx - (C+VN) = 0
        
        BigDecimal a = precio;
        BigDecimal b = cupon.negate();
        BigDecimal c = cupon.add(valorNominal).negate();
        
        // Fórmula cuadrática: x = (-b ± √(b²-4ac)) / 2a
        BigDecimal discriminante = b.pow(2).subtract(a.multiply(c).multiply(BigDecimal.valueOf(4)));
        
        if (discriminante.compareTo(BigDecimal.ZERO) < 0) {
            // No hay solución real, usar bisección
            return calcularTIRBiseccionSimple(precio, cupon, valorNominal, 2);
        }
        
        BigDecimal raizDiscriminante = new BigDecimal(Math.sqrt(discriminante.doubleValue()));
        BigDecimal x1 = b.negate().add(raizDiscriminante).divide(a.multiply(BigDecimal.valueOf(2)), MC);
        BigDecimal x2 = b.negate().subtract(raizDiscriminante).divide(a.multiply(BigDecimal.valueOf(2)), MC);
        
        // Elegir la solución positiva y mayor que 1 (ya que x = 1+r)
        BigDecimal x = (x1.compareTo(BigDecimal.ONE) > 0) ? x1 : x2;
        BigDecimal tir = x.subtract(BigDecimal.ONE);
        
        return tir.multiply(BigDecimal.valueOf(100)).setScale(2, ROUNDING_MODE);
    }
    
    /**
     * Calcula TIR usando bisección simplificada
     */
    private BigDecimal calcularTIRBiseccionSimple(BigDecimal precio, BigDecimal cupon, BigDecimal valorNominal, int plazo) {
        BigDecimal tirMin = new BigDecimal("-0.5"); // -50%
        BigDecimal tirMax = new BigDecimal("2.0");   // 200%
        BigDecimal precision = new BigDecimal("0.0001"); // 0.01%
        int maxIteraciones = 100;
        
        for (int i = 0; i < maxIteraciones; i++) {
            BigDecimal tirMedio = tirMin.add(tirMax).divide(BigDecimal.valueOf(2), MC);
            BigDecimal van = calcularVANSimple(precio, cupon, valorNominal, plazo, tirMedio);
            
            if (van.abs().compareTo(precision) < 0) {
                return tirMedio.multiply(BigDecimal.valueOf(100)).setScale(2, ROUNDING_MODE);
            }
            
            if (van.compareTo(BigDecimal.ZERO) > 0) {
                tirMin = tirMedio;
            } else {
                tirMax = tirMedio;
            }
        }
        
        // Si no converge, devolver el punto medio
        BigDecimal tirFinal = tirMin.add(tirMax).divide(BigDecimal.valueOf(2), MC);
        return tirFinal.multiply(BigDecimal.valueOf(100)).setScale(2, ROUNDING_MODE);
    }
    
    /**
     * Calcula TIR usando bisección para bonos complejos
     */
    private BigDecimal calcularTIRBiseccion(List<FlujoFinanciero> flujos, BigDecimal precioCompra, Bono bono) {
        BigDecimal tirMin = new BigDecimal("-0.5"); // -50%
        BigDecimal tirMax = new BigDecimal("2.0");   // 200%
        BigDecimal precision = new BigDecimal("0.0001"); // 0.01%
        int maxIteraciones = 100;
        
        for (int i = 0; i < maxIteraciones; i++) {
            BigDecimal tirMedio = tirMin.add(tirMax).divide(BigDecimal.valueOf(2), MC);
            BigDecimal van = calcularVANParaBiseccion(flujos, precioCompra, tirMedio, bono);
            
            if (van.abs().compareTo(precision) < 0) {
                return tirMedio.multiply(BigDecimal.valueOf(100)).setScale(2, ROUNDING_MODE);
            }
            
            if (van.compareTo(BigDecimal.ZERO) > 0) {
                tirMin = tirMedio;
            } else {
                tirMax = tirMedio;
            }
        }
        
        // Si no converge, devolver el punto medio
        BigDecimal tirFinal = tirMin.add(tirMax).divide(BigDecimal.valueOf(2), MC);
        return tirFinal.multiply(BigDecimal.valueOf(100)).setScale(2, ROUNDING_MODE);
    }
    
    /**
     * Calcula VAN simple para bonos regulares
     */
    private BigDecimal calcularVANSimple(BigDecimal precio, BigDecimal cupon, BigDecimal valorNominal, int plazo, BigDecimal tir) {
        BigDecimal van = precio.negate(); // Inversión inicial negativa
        BigDecimal unMasTir = BigDecimal.ONE.add(tir);
        
        // Sumar cupones descontados
        for (int t = 1; t <= plazo; t++) {
            BigDecimal factorDescuento = unMasTir.pow(t, MC);
            if (t == plazo) {
                // Último período: cupón + valor nominal
                BigDecimal flujoFinal = cupon.add(valorNominal);
                van = van.add(flujoFinal.divide(factorDescuento, MC));
            } else {
                // Períodos intermedios: solo cupón
                van = van.add(cupon.divide(factorDescuento, MC));
            }
        }
        
        return van;
    }
    
    /**
     * Calcula VAN para bisección con flujos complejos
     */
    private BigDecimal calcularVANParaBiseccion(List<FlujoFinanciero> flujos, BigDecimal precioCompra, BigDecimal tir, Bono bono) {
        BigDecimal van = precioCompra.negate(); // Inversión inicial negativa
        BigDecimal unMasTir = BigDecimal.ONE.add(tir);
        
        // Sumar flujos futuros descontados (saltar el período 0)
        for (int i = 1; i < flujos.size(); i++) {
            FlujoFinanciero flujo = flujos.get(i);
            BigDecimal flujoValor = flujo.getFlujoTotal();
            
            if (flujoValor != null && flujoValor.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal factorDescuento = unMasTir.pow(flujo.getPeriodo(), MC);
                BigDecimal valorPresente = flujoValor.divide(factorDescuento, MC);
                van = van.add(valorPresente);
            }
        }
        
        return van;
    }

    @Override
    public BigDecimal convertirTasaNominalAEfectiva(BigDecimal tn, int capitalizaciones, int periodoTotal) {
        // Convertimos tasa nominal a decimal si viene en porcentaje
        BigDecimal tasaNominal = tn;
        if (tn.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tasaNominal = tn.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        // Fórmula: (1 + tn/m)^m - 1
        BigDecimal tasaPorCapitalizacion = tasaNominal.divide(BigDecimal.valueOf(capitalizaciones), MC);
        BigDecimal tasaEfectiva = BigDecimal.ONE
                .add(tasaPorCapitalizacion)
                .pow(capitalizaciones)
                .subtract(BigDecimal.ONE);
                
        return tasaEfectiva.setScale(SCALE, ROUNDING_MODE);
    }
    
    @Override
    public BigDecimal convertirTasa(BigDecimal tasaOrigen, String tipoOrigen, String tipoDestino, int capitalizaciones) {
        // Convertimos tasa origen a decimal si viene en porcentaje
        BigDecimal tasaOrigenDecimal = tasaOrigen;
        if (tasaOrigen.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tasaOrigenDecimal = tasaOrigen.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        // Caso base: mismos tipos
        if (tipoOrigen.equals(tipoDestino)) {
            return tasaOrigenDecimal;
        }
        
        // Conversión de nominal a efectiva
        if (tipoOrigen.equals("NOMINAL") && tipoDestino.equals("EFECTIVA")) {
            return convertirTasaNominalAEfectiva(tasaOrigenDecimal, capitalizaciones, capitalizaciones);
        }
        
        // Conversión de efectiva a nominal
        if (tipoOrigen.equals("EFECTIVA") && tipoDestino.equals("NOMINAL")) {
            // Fórmula: m * ((1 + TEA)^(1/m) - 1)
            
            double tasaEfectivaDouble = tasaOrigenDecimal.doubleValue();
            double potencia = Math.pow(1.0 + tasaEfectivaDouble, 1.0/capitalizaciones) - 1.0;
            BigDecimal tasaPeriodica = new BigDecimal(potencia);
            
            return tasaPeriodica.multiply(new BigDecimal(capitalizaciones))
                    .setScale(SCALE, ROUNDING_MODE);
        }
        
        // Por defecto, devolvemos la misma tasa
        return tasaOrigenDecimal;
    }
    
    @Override
    public BigDecimal calcularValorFuturo(BigDecimal capital, BigDecimal tasa, int periodos) {
        // Convertimos tasa a decimal si viene en porcentaje
        BigDecimal tasaDecimal = tasa;
        if (tasa.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tasaDecimal = tasa.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        // Fórmula: VF = VA * (1 + r)^n
        return capital.multiply(
            BigDecimal.ONE.add(tasaDecimal).pow(periodos, MC)
        ).setScale(SCALE, ROUNDING_MODE);
    }
    
    @Override
    public BigDecimal calcularValorPresente(BigDecimal montoFuturo, BigDecimal tasa, int periodos) {
        // Convertimos tasa a decimal si viene en porcentaje
        BigDecimal tasaDecimal = tasa;
        if (tasa.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tasaDecimal = tasa.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        // Fórmula: VP = VF / (1 + r)^n
        BigDecimal factorDescuento = BigDecimal.ONE.add(tasaDecimal).pow(periodos, MC);
        return montoFuturo.divide(factorDescuento, SCALE, ROUNDING_MODE);
    }
    
    @Override
    public BigDecimal calcularEcuacionEquivalente(List<BigDecimal> montos, List<Integer> periodos, BigDecimal tasa) {
        // Validar entradas
        if (montos.size() != periodos.size()) {
            throw new IllegalArgumentException("La cantidad de montos debe ser igual a la cantidad de periodos");
        }
        
        // Convertimos tasa a decimal si viene en porcentaje
        BigDecimal tasaDecimal = tasa;
        if (tasa.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tasaDecimal = tasa.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        // Calculamos el valor presente de cada monto
        BigDecimal valorPresenteTotal = BigDecimal.ZERO;
        
        for (int i = 0; i < montos.size(); i++) {
            BigDecimal monto = montos.get(i);
            int periodo = periodos.get(i);
            
            BigDecimal valorPresente = calcularValorPresente(monto, tasaDecimal, periodo);
            valorPresenteTotal = valorPresenteTotal.add(valorPresente);
        }
        
        return valorPresenteTotal.setScale(SCALE, ROUNDING_MODE);
    }

    @Override
    public String validarCalculoPrecio(Bono bono, BigDecimal trea) {
        // Validamos que el bono y la tasa no sean nulos
        if (bono == null) {
            return "El bono no puede ser nulo";
        }
        
        if (trea == null) {
            return "La tasa esperada de rendimiento no puede ser nula";
        }
        
        // Verificamos que el valor nominal del bono sea positivo
        if (bono.getValorNominal() == null || bono.getValorNominal().compareTo(BigDecimal.ZERO) <= 0) {
            return "El valor nominal del bono debe ser positivo";
        }
        
        // Verificamos que la tasa cupón sea positiva
        if (bono.getTasaCupon() == null || bono.getTasaCupon().compareTo(BigDecimal.ZERO) < 0) {
            return "La tasa cupón del bono debe ser positiva o cero";
        }
        
        // Verificamos que el plazo en años sea positivo
        if (bono.getPlazoAnios() <= 0) {
            return "El plazo en años debe ser positivo";
        }
        
        // Verificamos que la frecuencia de pagos sea válida
        if (bono.getFrecuenciaPagos() <= 0) {
            return "La frecuencia de pagos debe ser positiva";
        }
        
        // Verificamos que la fecha de emisión no sea nula
        if (bono.getFechaEmision() == null) {
            return "La fecha de emisión no puede ser nula";
        }
        
        // Verificamos que la tasa esperada sea positiva
        BigDecimal treaDecimal = trea;
        if (trea.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            treaDecimal = trea.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        if (treaDecimal.compareTo(BigDecimal.ZERO) < 0) {
            return "La tasa esperada de rendimiento debe ser positiva o cero";
        }
        
        return null; // Todo válido
    }

    @Override
    public Bono corregirCalculosBonoAmericano(Long bonoId) {
        // Esta implementación es solo un ejemplo y debe adaptarse según las necesidades
        return null;
    }
}