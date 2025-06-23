package com.bonofacil.platform.bonos.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value object que representa una tasa de interés, ya sea efectiva o nominal.
 * Incluye métodos para conversión entre diferentes tipos de tasas.
 */
public class TasaInteres {
    public enum TipoTasa {
        EFECTIVA,
        NOMINAL
    }

    private final BigDecimal valor;
    private final TipoTasa tipo;
    private final int frecuenciaCapitalizacion;
    private static final MathContext MC = new MathContext(12, RoundingMode.HALF_UP);

    /**
     * Constructor para TasaInteres.
     * 
     * @param valor Valor de la tasa (en formato decimal, ej: 0.05 para 5%)
     * @param tipo Tipo de tasa (EFECTIVA o NOMINAL)
     * @param frecuenciaCapitalizacion Número de capitalizaciones por año (para tasa nominal)
     */
    public TasaInteres(BigDecimal valor, TipoTasa tipo, int frecuenciaCapitalizacion) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La tasa de interés debe ser mayor que cero");
        }
        
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de tasa no puede ser nulo");
        }
        
        if (tipo == TipoTasa.NOMINAL && frecuenciaCapitalizacion <= 0) {
            throw new IllegalArgumentException("La frecuencia de capitalización debe ser mayor que cero para tasas nominales");
        }
        
        this.valor = valor;
        this.tipo = tipo;
        this.frecuenciaCapitalizacion = frecuenciaCapitalizacion;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public TipoTasa getTipo() {
        return tipo;
    }

    public int getFrecuenciaCapitalizacion() {
        return frecuenciaCapitalizacion;
    }
    
    /**
     * Convierte esta tasa a una tasa efectiva anual.
     * 
     * @return La tasa efectiva anual equivalente
     */
    public BigDecimal convertirATasaEfectivaAnual() {
        if (tipo == TipoTasa.EFECTIVA) {
            return valor;
        } else {
            // Fórmula: TEA = (1 + j/m)^m - 1
            // Donde: j = tasa nominal, m = frecuencia de capitalización
            BigDecimal tasaPorPeriodo = valor.divide(
                    BigDecimal.valueOf(frecuenciaCapitalizacion), MC);
            
            return BigDecimal.ONE.add(tasaPorPeriodo)
                    .pow(frecuenciaCapitalizacion, MC)
                    .subtract(BigDecimal.ONE);
        }
    }
    
    /**
     * Convierte esta tasa a una tasa efectiva por período (según la frecuencia especificada).
     * 
     * @param periodosPorAnio Número de períodos por año
     * @return La tasa efectiva por período
     */
    public BigDecimal convertirATasaEfectivaPorPeriodo(int periodosPorAnio) {
        if (periodosPorAnio <= 0) {
            throw new IllegalArgumentException("El número de períodos por año debe ser positivo");
        }
        
        BigDecimal tasaEfectivaAnual = convertirATasaEfectivaAnual();
        
        // Fórmula: (1 + TEA)^(1/m) - 1
        // Donde: m = número de períodos por año
        BigDecimal exponente = BigDecimal.ONE.divide(
                BigDecimal.valueOf(periodosPorAnio), MC);
        
        return potencia(BigDecimal.ONE.add(tasaEfectivaAnual), exponente)
                .subtract(BigDecimal.ONE);
    }
    
    /**
     * Convierte esta tasa a una tasa nominal con la frecuencia de capitalización especificada.
     * 
     * @param frecuenciaCapitalizacionDestino Frecuencia de capitalización deseada
     * @return La tasa nominal equivalente
     */
    public BigDecimal convertirATasaNominal(int frecuenciaCapitalizacionDestino) {
        if (frecuenciaCapitalizacionDestino <= 0) {
            throw new IllegalArgumentException("La frecuencia de capitalización debe ser positiva");
        }
        
        BigDecimal tea = convertirATasaEfectivaAnual();
        
        // Primero convertimos a tasa efectiva por período
        BigDecimal tasaPorPeriodo = potencia(
                BigDecimal.ONE.add(tea), 
                BigDecimal.ONE.divide(BigDecimal.valueOf(frecuenciaCapitalizacionDestino), MC)
            ).subtract(BigDecimal.ONE);
        
        // Luego multiplicamos por la frecuencia para obtener la tasa nominal
        return tasaPorPeriodo.multiply(BigDecimal.valueOf(frecuenciaCapitalizacionDestino));
    }
    
    /**
     * Método auxiliar para calcular potencias con exponentes decimales.
     * 
     * @param base La base
     * @param exponente El exponente
     * @return El resultado de base^exponente
     */
    private BigDecimal potencia(BigDecimal base, BigDecimal exponente) {
        // Usamos la función exponencial: x^y = e^(y*ln(x))
        double result = Math.pow(base.doubleValue(), exponente.doubleValue());
        return new BigDecimal(result, MC);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TasaInteres that = (TasaInteres) o;
        return frecuenciaCapitalizacion == that.frecuenciaCapitalizacion &&
               valor.compareTo(that.valor) == 0 &&
               tipo == that.tipo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor, tipo, frecuenciaCapitalizacion);
    }

    @Override
    public String toString() {
        return valor.multiply(BigDecimal.valueOf(100)).setScale(4, RoundingMode.HALF_UP) + "% " + 
               (tipo == TipoTasa.NOMINAL ? "nominal (m=" + frecuenciaCapitalizacion + ")" : "efectiva");
    }
} 