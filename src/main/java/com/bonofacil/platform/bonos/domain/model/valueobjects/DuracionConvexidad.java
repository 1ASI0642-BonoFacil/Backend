package com.bonofacil.platform.bonos.domain.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value object que encapsula las métricas de duración, duración modificada y convexidad de un bono.
 * Estas métricas miden la sensibilidad del precio del bono ante cambios en las tasas de interés.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DuracionConvexidad {
    private BigDecimal duracion;
    private BigDecimal duracionModificada;
    private BigDecimal convexidad;
    private BigDecimal tasaMercado;

    /**
     * Estima el cambio porcentual en el precio del bono para un cambio dado en la tasa de interés,
     * considerando tanto la duración modificada como la convexidad.
     * 
     * @param cambioPuntosPorcentuales Cambio en la tasa en puntos porcentuales (p.ej., 0.01 para 1%)
     * @return El cambio porcentual estimado en el precio
     */
    public BigDecimal estimarCambioPorcentualPrecio(BigDecimal cambioPuntosPorcentuales) {
        if (cambioPuntosPorcentuales == null) {
            throw new IllegalArgumentException("El cambio en puntos porcentuales no puede ser nulo");
        }
        
        // Efecto de primer orden (duración modificada)
        BigDecimal efectoDuracion = duracionModificada.negate()
                .multiply(cambioPuntosPorcentuales);
        
        // Efecto de segundo orden (convexidad)
        BigDecimal efectoConvexidad = convexidad
                .multiply(cambioPuntosPorcentuales.pow(2))
                .divide(BigDecimal.valueOf(2), 8, RoundingMode.HALF_UP);
        
        // Cambio porcentual total
        return efectoDuracion.add(efectoConvexidad);
    }
    
    /**
     * Calcula el precio estimado después de un cambio en la tasa de interés,
     * dado un precio actual.
     * 
     * @param precioActual Precio actual del bono
     * @param cambioPuntosPorcentuales Cambio en la tasa en puntos porcentuales
     * @return El precio estimado después del cambio
     */
    public BigDecimal calcularPrecioEstimado(BigDecimal precioActual, BigDecimal cambioPuntosPorcentuales) {
        if (precioActual == null || precioActual.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio actual debe ser positivo");
        }
        
        BigDecimal cambioPorcentual = estimarCambioPorcentualPrecio(cambioPuntosPorcentuales);
        return precioActual.multiply(BigDecimal.ONE.add(cambioPorcentual));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DuracionConvexidad that = (DuracionConvexidad) o;
        return duracion.compareTo(that.duracion) == 0 &&
               duracionModificada.compareTo(that.duracionModificada) == 0 &&
               convexidad.compareTo(that.convexidad) == 0 &&
               tasaMercado.compareTo(that.tasaMercado) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(duracion, duracionModificada, convexidad, tasaMercado);
    }

    @Override
    public String toString() {
        return "Duración: " + (duracion != null ? duracion.setScale(4, RoundingMode.HALF_UP) : "N/A") + " años, " +
               "Duración Modificada: " + (duracionModificada != null ? duracionModificada.setScale(4, RoundingMode.HALF_UP) : "N/A") + ", " +
               "Convexidad: " + (convexidad != null ? convexidad.setScale(4, RoundingMode.HALF_UP) : "N/A");
    }
} 