package com.bonofacil.platform.bonos.domain.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value object que representa el rendimiento de un bono, ya sea desde
 * la perspectiva del emisor (TCEA) o del inversor (TREA).
 */
@Getter
@Setter
@NoArgsConstructor
public class Rendimiento {
    private BigDecimal tasaRendimiento;
    private BigDecimal precio;
    
    /**
     * Constructor para Rendimiento.
     * 
     * @param tasaRendimiento Tasa de rendimiento
     * @param precio Precio o monto asociado
     */
    public Rendimiento(BigDecimal tasaRendimiento, BigDecimal precio) {
        this.tasaRendimiento = tasaRendimiento;
        this.precio = precio;
    }
    
    /**
     * Obtiene el valor del rendimiento como porcentaje formateado.
     * 
     * @param escala Número de decimales
     * @return El rendimiento formateado como porcentaje
     */
    public String getValorPorcentaje(int escala) {
        if (tasaRendimiento == null) {
            return "N/A";
        }
        return tasaRendimiento.multiply(BigDecimal.valueOf(100))
                .setScale(escala, RoundingMode.HALF_UP)
                .toString() + "%";
    }

    @Override
    public String toString() {
        return "Rendimiento: " + getValorPorcentaje(4) + 
               ", Precio: " + (precio != null ? precio.setScale(2, RoundingMode.HALF_UP) : "N/A");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rendimiento that = (Rendimiento) o;
        return Objects.equals(tasaRendimiento, that.tasaRendimiento) &&
               Objects.equals(precio, that.precio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasaRendimiento, precio);
    }
} 