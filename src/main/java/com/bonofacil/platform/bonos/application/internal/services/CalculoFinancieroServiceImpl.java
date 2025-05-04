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
        BigDecimal valorNominal = BigDecimal.valueOf(bono.getValorNominal());
        BigDecimal tasaCupon = BigDecimal.valueOf(bono.getTasaCupon()).divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        int plazoAnios = bono.getPlazoAnios();
        int frecuenciaPagos = bono.getFrecuenciaPagos();
        int totalPeriodos = plazoAnios * frecuenciaPagos;
        int plazosGraciaTotal = bono.getPlazosGraciaTotal();
        int plazosGraciaParcial = bono.getPlazosGraciaParcial();
        LocalDate fechaEmision = bono.getFechaEmision();
        
        // Identificar el método de amortización real
        String metodoReal = identificarMetodoAmortizacion(bono);

        List<FlujoFinanciero> flujos = new ArrayList<>();

        // Período 0 - Desembolso inicial
        FlujoFinanciero flujoInicial = new FlujoFinanciero();
        flujoInicial.setBono(bono);
        flujoInicial.setPeriodo(0);
        flujoInicial.setFecha(fechaEmision);
        flujoInicial.setCuota(BigDecimal.ZERO);
        flujoInicial.setAmortizacion(BigDecimal.ZERO);
        flujoInicial.setInteres(BigDecimal.ZERO);
        flujoInicial.setSaldo(valorNominal);
        flujoInicial.setFlujo(valorNominal.negate());
        flujos.add(flujoInicial);

        // Calcular tasa periódica
        BigDecimal tasaPeriodica = tasaCupon.divide(BigDecimal.valueOf(frecuenciaPagos), SCALE, ROUNDING_MODE);
        
        // Generar flujos para cada período
        for (int i = 1; i <= totalPeriodos; i++) {
            FlujoFinanciero flujo = new FlujoFinanciero();
            flujo.setBono(bono);
            flujo.setPeriodo(i);
            
            // Calcular fecha del período
            LocalDate fechaPeriodo = fechaEmision.plus(i * 12 / frecuenciaPagos, ChronoUnit.MONTHS);
            flujo.setFecha(fechaPeriodo);

            // Determinar si estamos en periodo de gracia
            boolean esGraciaTotal = i <= plazosGraciaTotal;
            boolean esGraciaParcial = i > plazosGraciaTotal && i <= (plazosGraciaTotal + plazosGraciaParcial);
            
            // Obtener saldo del periodo anterior
            BigDecimal saldoAnterior = flujos.get(i - 1).getSaldo();

            // Interés del periodo basado en el saldo anterior
            BigDecimal interesPeriodo = saldoAnterior.multiply(tasaPeriodica, MC);
            
            BigDecimal amortizacion = BigDecimal.ZERO;
            BigDecimal cuota = BigDecimal.ZERO;
            BigDecimal flujoEfectivo = BigDecimal.ZERO;
            BigDecimal nuevoSaldo;

            // Método Americano con manejo de períodos de gracia
            if (esGraciaTotal) {
                // En gracia total: no hay pagos, el interés se capitaliza
                BigDecimal interesCapitalizado = interesPeriodo;
                nuevoSaldo = saldoAnterior.add(interesCapitalizado);
                flujoEfectivo = BigDecimal.ZERO;
                amortizacion = BigDecimal.ZERO;
                cuota = BigDecimal.ZERO;
            } else if (esGraciaParcial) {
                // En gracia parcial: solo se paga interés
                flujoEfectivo = interesPeriodo;
                amortizacion = BigDecimal.ZERO;
                cuota = interesPeriodo;
                nuevoSaldo = saldoAnterior;
            } else if (i == totalPeriodos) {
                // Último período: capital + interés
                amortizacion = saldoAnterior;
                cuota = amortizacion.add(interesPeriodo);
                flujoEfectivo = cuota;
                nuevoSaldo = BigDecimal.ZERO;
            } else {
                // Períodos normales: solo interés
                amortizacion = BigDecimal.ZERO;
                cuota = interesPeriodo;
                flujoEfectivo = cuota;
                nuevoSaldo = saldoAnterior;
            }

            flujo.setCuota(cuota);
            flujo.setAmortizacion(amortizacion);
            flujo.setInteres(interesPeriodo);
            flujo.setSaldo(nuevoSaldo);
            flujo.setFlujo(flujoEfectivo);

            flujos.add(flujo);
        }

        return flujos;
    }

    @Override
    public BigDecimal calcularTCEA(Bono bono) {
        BigDecimal tasaCupon = BigDecimal.valueOf(bono.getTasaCupon()).divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
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
        
        // Fórmula correcta: TEP = (1 + TEA)^(1/m) - 1
        double tasaAnualDouble = tasaAnualDecimal.doubleValue();
        double tasaPeriodicaDouble = Math.pow(1.0 + tasaAnualDouble, 1.0/frecuenciaPagos) - 1.0;
        
        return BigDecimal.valueOf(tasaPeriodicaDouble).setScale(SCALE, ROUNDING_MODE);
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
            if (flujo.getFlujo().compareTo(BigDecimal.ZERO) > 0) {
                // Usar el periodo exacto
                BigDecimal factorTiempo = BigDecimal.valueOf(flujo.getPeriodo());
                
                // Factor de descuento: 1/(1+tasaPeriodica)^periodo
                BigDecimal factorDescuento = BigDecimal.ONE
                        .divide(BigDecimal.ONE.add(tasaPeriodica, MC).pow(flujo.getPeriodo(), MC), SCALE, ROUNDING_MODE);
                
                BigDecimal valorActual = flujo.getFlujo().multiply(factorDescuento, MC);
                
                // Acumular para el cálculo de duración: t * VA(flujo)
                sumaPonderada = sumaPonderada.add(factorTiempo.multiply(valorActual, MC), MC);
                precio = precio.add(valorActual, MC);
            }
        }
        
        // Evitar división por cero
        if (precio.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        // Duración en periodos: Σ(t * VA(flujo)) / Precio
        BigDecimal duracionPeriodos = sumaPonderada.divide(precio, SCALE, ROUNDING_MODE);
        
        // Convertir duración de periodos a años: D_periodos / frecuencia_pagos
        BigDecimal duracionAnios = duracionPeriodos.divide(BigDecimal.valueOf(frecuenciaPagos), SCALE, ROUNDING_MODE);
        
        return duracionAnios.setScale(4, ROUNDING_MODE);
    }
    
    @Override
    public BigDecimal calcularDuracion(Bono bono) {
        BigDecimal tcea = BigDecimal.valueOf(bono.getTcea());
        List<FlujoFinanciero> flujos = bono.getFlujos();
        if (flujos == null || flujos.isEmpty()) {
            flujos = calcularFlujoFinanciero(bono);
        }
        return calcularDuracion(flujos, tcea);
    }

    @Override
    public BigDecimal calcularConvexidad(List<FlujoFinanciero> flujos, BigDecimal tcea) {
        // Validaciones iniciales
        if (flujos == null || flujos.size() <= 1) {
            return BigDecimal.ZERO;
        }
        
        // Obtener información relevante del bono
        Bono bono = null;
        if (!flujos.isEmpty() && flujos.get(0).getBono() != null) {
            bono = flujos.get(0).getBono();
        }
        
        int frecuenciaPagos = (bono != null) ? bono.getFrecuenciaPagos() : 2;
        
        // Asegurarse que la tasa esté en formato decimal
        BigDecimal tceaDecimal = tcea;
        if (tcea.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tceaDecimal = tcea.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        // Calcular la tasa periódica
        BigDecimal tasaPeriodica = calcularTasaEfectivaPeriodica(tceaDecimal, frecuenciaPagos);
        
        // Obtener precio del bono como suma de flujos descontados
        BigDecimal precioBono = BigDecimal.ZERO;
        for (int i = 1; i < flujos.size(); i++) {
            FlujoFinanciero ff = flujos.get(i);
            BigDecimal flujo = ff.getFlujo();
            if (flujo != null && flujo.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal factorDescuento = BigDecimal.ONE
                        .divide(BigDecimal.ONE.add(tasaPeriodica, MC).pow(i, MC), SCALE, ROUNDING_MODE);
                BigDecimal flujoDescontado = flujo.multiply(factorDescuento, MC);
                precioBono = precioBono.add(flujoDescontado, MC);
            }
        }
        
        // Si el precio es cero o cercano a cero, no podemos calcular la convexidad
        if (precioBono.compareTo(BigDecimal.valueOf(0.0001)) < 0) {
            return BigDecimal.valueOf(1); // Valor por defecto
        }
        
        // Calcular la convexidad usando la fórmula estándar
        // Convexidad = (1/P) * Σ[ (t*(t+1)*Ct) / ((1+r)^(t+2)) ]
        BigDecimal numerador = BigDecimal.ZERO;
        
        for (int i = 1; i < flujos.size(); i++) {
            FlujoFinanciero ff = flujos.get(i);
            BigDecimal flujo = ff.getFlujo();
            
            if (flujo != null && flujo.compareTo(BigDecimal.ZERO) > 0) {
                // Factor t*(t+1) para el flujo en el periodo t
                BigDecimal t = BigDecimal.valueOf(i);
                BigDecimal tPlusOne = BigDecimal.valueOf(i + 1);
                BigDecimal factorTiempo = t.multiply(tPlusOne, MC);
                
                // Factor de descuento = (1+r)^(t+2)
                BigDecimal factorDescuento = BigDecimal.ONE
                        .add(tasaPeriodica, MC)
                        .pow(i + 2, MC);
                
                // Flujo ponderado por tiempo y descontado
                BigDecimal termino = flujo
                        .multiply(factorTiempo, MC)
                        .divide(factorDescuento, SCALE, ROUNDING_MODE);
                
                numerador = numerador.add(termino, MC);
            }
        }
        
        // Ajuste para frecuencia: convexidad en años = convexidad en períodos / m^2
        BigDecimal convexidadPeriodos = numerador.divide(precioBono, SCALE, ROUNDING_MODE);
        BigDecimal convexidadAnual = convexidadPeriodos
                .divide(BigDecimal.valueOf(frecuenciaPagos * frecuenciaPagos), SCALE, ROUNDING_MODE);
        
        return convexidadAnual.setScale(2, ROUNDING_MODE);
    }
    
    @Override
    public BigDecimal calcularConvexidad(Bono bono) {
        List<FlujoFinanciero> flujos = bono.getFlujos();
        if (flujos == null || flujos.isEmpty()) {
            flujos = calcularFlujoFinanciero(bono);
        }
        BigDecimal tcea = BigDecimal.valueOf(bono.getTcea());
        return calcularConvexidad(flujos, tcea);
    }

    @Override
    public BigDecimal calcularPrecioMaximo(List<FlujoFinanciero> flujos, BigDecimal trea) {
        // Verificar si hay al menos un flujo después del periodo 0
        if (flujos.size() <= 1) {
            return BigDecimal.valueOf(0);
        }
        
        // Convertir TREA a formato decimal
        BigDecimal treaDecimal = trea;
        if (trea.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            treaDecimal = trea.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        // Obtener información del bono
        Bono bono = null;
        if (!flujos.isEmpty() && flujos.get(0).getBono() != null) {
            bono = flujos.get(0).getBono();
        }
        
        // Obtener datos relevantes del bono
        BigDecimal valorNominal = flujos.get(0).getSaldo();
        int frecuenciaPagos = (bono != null) ? bono.getFrecuenciaPagos() : 2;
        
        // Calcular tasa periódica para descuento correctamente
        BigDecimal tasaPeriodica = calcularTasaEfectivaPeriodica(treaDecimal, frecuenciaPagos);
        
        // Cálculo del precio como suma de flujos descontados
        BigDecimal precioMaximo = BigDecimal.ZERO;
        
        // Procesar cada flujo existente
        for (int i = 1; i < flujos.size(); i++) {
            FlujoFinanciero ff = flujos.get(i);
            BigDecimal flujo = ff.getFlujo();
            
            // Solo calcular VP si hay flujo positivo
            if (flujo != null && flujo.compareTo(BigDecimal.ZERO) > 0) {
                // Factor de descuento: 1/(1+tasaPeriodica)^periodo
                BigDecimal factorDescuento = BigDecimal.ONE
                        .divide(BigDecimal.ONE.add(tasaPeriodica, MC).pow(i, MC), SCALE, ROUNDING_MODE);
                
                // Valor presente del flujo
                BigDecimal flujoDescontado = flujo.multiply(factorDescuento, MC);
                precioMaximo = precioMaximo.add(flujoDescontado, MC);
            }
        }
        
        return precioMaximo.setScale(2, ROUNDING_MODE);
    }
    
    @Override
    public BigDecimal calcularPrecioMaximo(Bono bono, BigDecimal trea) {
        List<FlujoFinanciero> flujos = bono.getFlujos();
        if (flujos == null || flujos.isEmpty()) {
            flujos = calcularFlujoFinanciero(bono);
        }
        return calcularPrecioMaximo(flujos, trea);
    }

    /**
     * Documenta si el bono usa un método americano puro o una variante con períodos de gracia
     * @param bono El bono a evaluar
     * @return Una cadena que describe el método de amortización real
     */
    @Override
    public String identificarMetodoAmortizacion(Bono bono) {
        if (bono == null) {
            return "DESCONOCIDO";
        }
        
        String metodo = bono.getMetodoAmortizacion();
        if (metodo == null || metodo.isEmpty()) {
            return "DESCONOCIDO";
        }
        
        // Verificar si es un método americano puro o una variante
        if ("AMERICANO".equalsIgnoreCase(metodo)) {
            if (bono.getPlazosGraciaTotal() > 0 || bono.getPlazosGraciaParcial() > 0) {
                return "AMERICANO_CON_GRACIA";
            } else {
                return "AMERICANO_PURO";
            }
        }
        
        return metodo.toUpperCase();
    }

    @Override
    public void procesarCalculosBono(Bono bono) {
        // Validar el bono
        if (bono == null) {
            throw new IllegalArgumentException("El bono no puede ser nulo");
        }
        
        // Calcular TCEA según el método de amortización
        BigDecimal tcea = calcularTCEA(bono);
        bono.setTcea(tcea.doubleValue());
        
        // Calcular Flujo Financiero
        List<FlujoFinanciero> flujos = calcularFlujoFinanciero(bono);
        
        // Guardar los flujos en el bono
        if (bono.getFlujos() == null) {
            bono.setFlujos(new ArrayList<>());
        } else {
            bono.getFlujos().clear();
        }
        bono.getFlujos().addAll(flujos);
        
        // Calcular la Duración de Macaulay
        BigDecimal duracion = calcularDuracion(flujos, tcea);
        bono.setDuracion(duracion.doubleValue());
        
        // Calcular la Convexidad
        BigDecimal convexidad = calcularConvexidad(flujos, tcea);
        bono.setConvexidad(convexidad.doubleValue());
    }

    @Override
    public Calculo calcularInversion(Bono bono, BigDecimal tasaEsperada) {
        // Validar el bono
        if (bono == null) {
            throw new IllegalArgumentException("El bono no puede ser nulo");
        }
        
        // Verificar que haya flujos disponibles o calcularlos
        List<FlujoFinanciero> flujos = bono.getFlujos();
        if (flujos == null || flujos.isEmpty()) {
            flujos = calcularFlujoFinanciero(bono);
        }
        
        // Asegurar que la tasa esperada esté en formato decimal
        BigDecimal tasaEsperadaDecimal = tasaEsperada;
        if (tasaEsperada.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tasaEsperadaDecimal = tasaEsperada.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        // Calcular TREA igual a tasa esperada
        BigDecimal trea = tasaEsperadaDecimal;
        
        // Calcular precio máximo con la tasa esperada
        BigDecimal precioMaximo = calcularPrecioMaximo(flujos, trea);
        
        // Crear el objeto de cálculo
        Calculo calculo = new Calculo();
        calculo.setBono(bono);
        calculo.setTasaEsperada(tasaEsperadaDecimal.doubleValue());
        calculo.setTrea(trea.doubleValue());
        calculo.setPrecioMaximo(precioMaximo.doubleValue());
        calculo.setFechaCalculo(LocalDate.now());
        
        return calculo;
    }
    
    @Override
    public void procesarCalculosInversor(Calculo calculo) {
        Bono bono = calculo.getBono();
        BigDecimal tasaEsperada = BigDecimal.valueOf(calculo.getTasaEsperada());
        
        // Asegurar que la tasa esperada esté en formato decimal
        if (tasaEsperada.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tasaEsperada = tasaEsperada.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        List<FlujoFinanciero> flujos = bono.getFlujos();
        if (flujos == null || flujos.isEmpty()) {
            flujos = calcularFlujoFinanciero(bono);
        }
        
        BigDecimal precioMaximo = calcularPrecioMaximo(flujos, tasaEsperada);
        calculo.setPrecioMaximo(precioMaximo.doubleValue());
        calculo.setTrea(tasaEsperada.doubleValue());
    }

    @Override
    public BigDecimal calcularTREA(Bono bono, BigDecimal tasaEsperada) {
        // Para bonos americanos, la TREA es igual a la tasa esperada por el inversor
        return tasaEsperada;
    }

    @Override
    public BigDecimal convertirTasaNominalAEfectiva(BigDecimal tn, int capitalizaciones, int periodoTotal) {
        // Si la tasa viene en porcentaje, convertirla a decimal
        BigDecimal tasaNominalDecimal = tn;
        if (tn.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tasaNominalDecimal = tn.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        BigDecimal m = BigDecimal.valueOf(capitalizaciones);
        return BigDecimal.ONE.add(tasaNominalDecimal.divide(m, MC))
                       .pow(periodoTotal)
                       .subtract(BigDecimal.ONE);
    }
    
    @Override
    public BigDecimal convertirTasa(BigDecimal tasaOrigen, String tipoOrigen, String tipoDestino, int capitalizaciones) {
        // Si la tasa viene en porcentaje, convertirla a decimal
        BigDecimal tasaOrigenDecimal = tasaOrigen;
        if (tasaOrigen.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tasaOrigenDecimal = tasaOrigen.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        // Implementación de conversiones
        if (tipoOrigen.equals("TNA") && tipoDestino.equals("TEA")) {
            return BigDecimal.ONE.add(tasaOrigenDecimal.divide(BigDecimal.valueOf(capitalizaciones), MC))
                           .pow(capitalizaciones)
                           .subtract(BigDecimal.ONE);
        } else if (tipoOrigen.equals("TEA") && tipoDestino.equals("TNA")) {
            // TEA a TNA aproximada: m * ((1 + TEA)^(1/m) - 1)
            double tasaAnualDouble = tasaOrigenDecimal.doubleValue();
            double tasaPeriodicaDouble = Math.pow(1 + tasaAnualDouble, 1.0/capitalizaciones) - 1;
            return BigDecimal.valueOf(tasaPeriodicaDouble * capitalizaciones);
        } else if (tipoOrigen.equals("TEA") && tipoDestino.equals("TEP")) {
            // TEA a Tasa Efectiva Periódica: (1 + TEA)^(1/m) - 1
            return calcularTasaEfectivaPeriodica(tasaOrigenDecimal, capitalizaciones);
        } else if (tipoOrigen.equals("TEP") && tipoDestino.equals("TEA")) {
            // TEP a TEA: (1 + TEP)^m - 1
            return BigDecimal.ONE.add(tasaOrigenDecimal)
                           .pow(capitalizaciones)
                           .subtract(BigDecimal.ONE);
        }
        
        // Si no se encuentra la conversión, devolver la tasa original
        return tasaOrigenDecimal;
    }
    
    @Override
    public BigDecimal calcularValorFuturo(BigDecimal capital, BigDecimal tasa, int periodos) {
        // Si la tasa viene en porcentaje, convertirla a decimal
        BigDecimal tasaDecimal = tasa;
        if (tasa.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tasaDecimal = tasa.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        return capital.multiply(BigDecimal.ONE.add(tasaDecimal).pow(periodos, MC));
    }
    
    @Override
    public BigDecimal calcularValorPresente(BigDecimal montoFuturo, BigDecimal tasa, int periodos) {
        // Si la tasa viene en porcentaje, convertirla a decimal
        BigDecimal tasaDecimal = tasa;
        if (tasa.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tasaDecimal = tasa.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        return montoFuturo.divide(BigDecimal.ONE.add(tasaDecimal).pow(periodos, MC), MC);
    }
    
    @Override
    public BigDecimal calcularEcuacionEquivalente(List<BigDecimal> montos, List<Integer> periodos, BigDecimal tasa) {
        // Validar que las listas tengan el mismo tamaño
        if (montos.size() != periodos.size()) {
            throw new IllegalArgumentException("Las listas de montos y períodos deben tener el mismo tamaño");
        }
        
        // Si la tasa viene en porcentaje, convertirla a decimal
        BigDecimal tasaDecimal = tasa;
        if (tasa.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            tasaDecimal = tasa.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        BigDecimal resultado = BigDecimal.ZERO;
        for (int i = 0; i < montos.size(); i++) {
            BigDecimal factor = BigDecimal.ONE.divide(
                BigDecimal.ONE.add(tasaDecimal).pow(periodos.get(i), MC), MC
            );
            resultado = resultado.add(montos.get(i).multiply(factor, MC));
        }
        return resultado;
    }

    @Override
    public String validarCalculoPrecio(Bono bono, BigDecimal trea) {
        StringBuilder resultado = new StringBuilder();
        
        // Obtener parámetros del bono
        double valorNominal = bono.getValorNominal();
        double tasaCupon = bono.getTasaCupon();
        int frecuenciaPagos = bono.getFrecuenciaPagos();
        int totalPeriodos = bono.getPlazoAnios() * frecuenciaPagos;
        int plazosGraciaTotal = bono.getPlazosGraciaTotal();
        int plazosGraciaParcial = bono.getPlazosGraciaParcial();
        
        // Convertir TREA a formato decimal
        BigDecimal treaDecimal = trea;
        if (trea.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            treaDecimal = trea.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE);
        }
        
        // Calcular tasa periódica
        BigDecimal tasaPeriodica = calcularTasaEfectivaPeriodica(treaDecimal, frecuenciaPagos);
        
        // Calcular flujo de cupón periódico
        double cuponPeriodico = (valorNominal * tasaCupon / 100) / frecuenciaPagos;
        
        resultado.append("Parámetros:\n");
        resultado.append(String.format("- Valor Nominal: %.2f\n", valorNominal));
        resultado.append(String.format("- Tasa Cupón: %.2f%%\n", tasaCupon));
        resultado.append(String.format("- Frecuencia de Pagos: %d\n", frecuenciaPagos));
        resultado.append(String.format("- Períodos Totales: %d\n", totalPeriodos));
        resultado.append(String.format("- Períodos Gracia Total: %d\n", plazosGraciaTotal));
        resultado.append(String.format("- Períodos Gracia Parcial: %d\n", plazosGraciaParcial));
        resultado.append(String.format("- TREA: %.2f%%\n", treaDecimal.doubleValue() * 100));
        resultado.append(String.format("- Tasa Periódica: %.4f%%\n", tasaPeriodica.doubleValue() * 100));
        resultado.append(String.format("- Cupón Periódico: %.2f\n", cuponPeriodico));
        
        // Calcular flujos y precio
        List<FlujoFinanciero> flujos = calcularFlujoFinanciero(bono);
        BigDecimal precioCalculado = calcularPrecioMaximo(flujos, trea);
        resultado.append(String.format("\nPrecio Calculado: %.2f\n", precioCalculado));
        
        return resultado.toString();
    }

    @Override
    public Bono corregirCalculosBonoAmericano(Long bonoId) {
        throw new UnsupportedOperationException("Método no implementado");
    }
}