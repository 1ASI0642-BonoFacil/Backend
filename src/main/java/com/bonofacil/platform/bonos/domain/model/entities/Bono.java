package com.bonofacil.platform.bonos.domain.model.entities;

import com.bonofacil.platform.shared.domain.model.entities.AuditableModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bonos")
public class Bono  extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String descripcion;
    private double valorNominal;
    private double tasaCupon;
    private int plazoAnios;
    private int frecuenciaPagos;
    private String moneda;
    private LocalDate fechaEmision;
    private int plazosGraciaTotal;
    private int plazosGraciaParcial;
    private double tcea;
    private double duracion;
    private double convexidad;
    private double tasaDescuento;
    private String metodoAmortizacion = "AMERICANO";
    
    private String emisorUsername;
    
    @Transient
    private List<FlujoFinanciero> flujos = new ArrayList<>();
    
    @OneToMany(mappedBy = "bono", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Calculo> calculos = new ArrayList<>();
    
    public String getEmisorUsername() {
        return emisorUsername;
    }
    
    public void setEmisor(Object emisor) {
        if (emisor != null) {
            try {
                // Intenta obtener el username mediante reflexión o cast
                this.emisorUsername = emisor.toString();
            } catch (Exception e) {
                this.emisorUsername = "unknown";
            }
        }
    }
}