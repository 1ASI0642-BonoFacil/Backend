package com.bonofacil.platform.bonos.domain.model.entities;

import com.bonofacil.platform.shared.domain.model.entities.AuditableModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "calculos")
public class Calculo extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "bono_id")
    private Bono bono;
    
    private String inversorUsername;
    
    @Column(precision = 19, scale = 6)
    private BigDecimal tasaEsperada;
    
    @Column(precision = 19, scale = 6)
    private BigDecimal trea;
    
    @Column(precision = 19, scale = 4)
    private BigDecimal precioMaximo;
    
    private LocalDate fechaCalculo;
    
    // Información adicional sobre el cálculo (como tipo de valoración)
    @Column(name = "informacion_adicional", length = 255)
    private String informacionAdicional;
    
    public Long getBonoId() {
        return bono != null ? bono.getId() : null;
    }
    
    public String getBonoNombre() {
        return bono != null ? bono.getNombre() : null;
    }
    
    /**
     * Métodos de utilidad para convertir entre BigDecimal y double
     */
    public double getTasaEsperadaAsDouble() {
        return tasaEsperada != null ? tasaEsperada.doubleValue() : 0.0;
    }
    
    public double getTreaAsDouble() {
        return trea != null ? trea.doubleValue() : 0.0;
    }
    
    public double getPrecioMaximoAsDouble() {
        return precioMaximo != null ? precioMaximo.doubleValue() : 0.0;
    }
    
    public void setTasaEsperadaFromDouble(double value) {
        this.tasaEsperada = BigDecimal.valueOf(value);
    }
    
    public void setTreaFromDouble(double value) {
        this.trea = BigDecimal.valueOf(value);
    }
    
    public void setPrecioMaximoFromDouble(double value) {
        this.precioMaximo = BigDecimal.valueOf(value);
    }
}