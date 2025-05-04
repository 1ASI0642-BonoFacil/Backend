package com.bonofacil.platform.bonos.domain.model.entities;

import com.bonofacil.platform.shared.domain.model.entities.AuditableModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
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
    
    private double tasaEsperada;
    private double trea;
    private double precioMaximo;
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
}