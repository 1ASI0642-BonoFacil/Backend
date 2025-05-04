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
@Table(name = "flujos_financieros")
public class FlujoFinanciero extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bono_id")
    private Bono bono;
    
    private Integer periodo;
    private LocalDate fecha;
    private BigDecimal cuota;
    private BigDecimal amortizacion;
    private BigDecimal interes;
    private BigDecimal saldo;
    private BigDecimal flujo;
    
    @Transient
    private BigDecimal factorTiempo;
    
    @Transient
    private BigDecimal factorDescuento;
    
    @Transient
    private BigDecimal valorActual;
}