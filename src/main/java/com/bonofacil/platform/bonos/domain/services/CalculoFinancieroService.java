package com.bonofacil.platform.bonos.domain.services;

import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import com.bonofacil.platform.bonos.domain.model.entities.Calculo;
import com.bonofacil.platform.bonos.domain.model.entities.FlujoFinanciero;

import java.math.BigDecimal;
import java.util.List;

public interface CalculoFinancieroService {

    // Calcula el cronograma de pagos método americano con plazos de gracia
    List<FlujoFinanciero> calcularFlujoFinanciero(Bono bono);

    // Calcula la TCEA del bono
    BigDecimal calcularTCEA(Bono bono);

    // Calcula la TREA para un inversor
    BigDecimal calcularTREA(Bono bono, BigDecimal tasaEsperada);

    // Calcula la duración de Macaulay
    BigDecimal calcularDuracion(Bono bono);

    // Calcula la convexidad
    BigDecimal calcularConvexidad(Bono bono);

    // Calcula el precio máximo que un inversor estaría dispuesto a pagar
    BigDecimal calcularPrecioMaximo(Bono bono, BigDecimal tasaEsperada);

    // Procesa todos los cálculos para un bono
    void procesarCalculosBono(Bono bono);

    // Procesa los cálculos para un inversor
    void procesarCalculosInversor(Calculo calculo);

    BigDecimal calcularTasaEfectivaPeriodica(BigDecimal tasaAnual, int frecuenciaPagos);

    BigDecimal calcularDuracion(List<FlujoFinanciero> flujos, BigDecimal tcea);

    BigDecimal calcularConvexidad(List<FlujoFinanciero> flujos, BigDecimal tcea);

    BigDecimal calcularPrecioMaximo(List<FlujoFinanciero> flujos, BigDecimal trea);

    Calculo calcularInversion(Bono bono, BigDecimal tasaEsperada);
    
    // Convierte una tasa nominal a efectiva
    BigDecimal convertirTasaNominalAEfectiva(BigDecimal tn, int capitalizaciones, int periodoTotal);
    
    // Convierte entre diferentes tipos de tasas
    BigDecimal convertirTasa(BigDecimal tasaOrigen, String tipoOrigen, String tipoDestino, int capitalizaciones);
    
    // Calcula el valor futuro de un capital
    BigDecimal calcularValorFuturo(BigDecimal capital, BigDecimal tasa, int periodos);
    
    // Calcula el valor presente de un capital futuro
    BigDecimal calcularValorPresente(BigDecimal capitalFuturo, BigDecimal tasa, int periodos);
    
    // Calcula el resultado de una ecuación de valor equivalente
    BigDecimal calcularEcuacionEquivalente(List<BigDecimal> montos, List<Integer> periodos, BigDecimal tasa);
    
    // Método para validar el cálculo del precio máximo (depuración)
    String validarCalculoPrecio(Bono bono, BigDecimal trea);
    
    // Identifica el método de amortización real del bono (considerando períodos de gracia)
    String identificarMetodoAmortizacion(Bono bono);

    // Corrige cálculos de bonos americanos existentes
    Bono corregirCalculosBonoAmericano(Long bonoId);
}