package com.bonofacil.platform.bonos.domain.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value object que representa el precio máximo que el mercado estaría dispuesto
 * a pagar por un bono, dado una tasa de rendimiento requerida.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrecioMercado {
    private BigDecimal precio;
    private BigDecimal tasaMercado;
    private BigDecimal valorNominal;
    private BigDecimal precioPorcentaje;
    
    /**
     * Constructor para PrecioMercado.
     * 
     * @param precio Precio calculado
     * @param tasaRendimientoRequerida Tasa de rendimiento requerida por el mercado
     * @param valorNominal Valor nominal del bono
     */
    public PrecioMercado(BigDecimal precio, BigDecimal tasaRendimientoRequerida, BigDecimal valorNominal) {
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser positivo");
        }
        
        if (tasaRendimientoRequerida == null || tasaRendimientoRequerida.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La tasa de rendimiento requerida debe ser positiva");
        }
        
        if (valorNominal == null || valorNominal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El valor nominal debe ser positivo");
        }
        
        this.precio = precio;
        this.tasaMercado = tasaRendimientoRequerida;
        this.valorNominal = valorNominal;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public BigDecimal getTasaMercado() {
        return tasaMercado;
    }

    public BigDecimal getValorNominal() {
        return valorNominal;
    }
    
    /**
     * Calcula la prima o descuento respecto al valor nominal.
     * 
     * @return La prima (positiva) o descuento (negativo)
     */
    public BigDecimal calcularPrimaODescuento() {
        if (precio == null || valorNominal == null) {
            return BigDecimal.ZERO;
        }
        return precio.subtract(valorNominal);
    }
    
    /**
     * Calcula el porcentaje de prima o descuento respecto al valor nominal.
     * 
     * @return El porcentaje de prima (positivo) o descuento (negativo)
     */
    public BigDecimal calcularPorcentajePrimaODescuento() {
        if (valorNominal == null || valorNominal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return calcularPrimaODescuento()
                .divide(valorNominal, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    
    /**
     * Determina si el bono cotiza con prima.
     * 
     * @return true si el precio es mayor que el valor nominal
     */
    public boolean cotizaConPrima() {
        return precio != null && valorNominal != null && precio.compareTo(valorNominal) > 0;
    }
    
    /**
     * Determina si el bono cotiza con descuento.
     * 
     * @return true si el precio es menor que el valor nominal
     */
    public boolean cotizaConDescuento() {
        return precio != null && valorNominal != null && precio.compareTo(valorNominal) < 0;
    }
    
    /**
     * Determina si el bono cotiza a la par.
     * 
     * @return true si el precio es igual al valor nominal
     */
    public boolean cotizaALaPar() {
        return precio != null && valorNominal != null && precio.compareTo(valorNominal) == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrecioMercado that = (PrecioMercado) o;
        return precio.compareTo(that.precio) == 0 &&
               tasaMercado.compareTo(that.tasaMercado) == 0 &&
               valorNominal.compareTo(that.valorNominal) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(precio, tasaMercado, valorNominal);
    }

    @Override
    public String toString() {
        String primaDescuento;
        if (cotizaConPrima()) {
            primaDescuento = "Prima: " + calcularPorcentajePrimaODescuento().setScale(2, RoundingMode.HALF_UP) + "%";
        } else if (cotizaConDescuento()) {
            primaDescuento = "Descuento: " + calcularPorcentajePrimaODescuento().abs().setScale(2, RoundingMode.HALF_UP) + "%";
        } else {
            primaDescuento = "A la par";
        }
        
        return "Precio: " + (precio != null ? precio.setScale(2, RoundingMode.HALF_UP) : "N/A") + 
               " (" + primaDescuento + "), " +
               "Yield: " + (tasaMercado != null ? tasaMercado.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) : "N/A") + "%";
    }
} 