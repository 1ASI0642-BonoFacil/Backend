package com.bonofacil.platform.bonos.domain.model.entities;

import com.bonofacil.platform.shared.domain.model.entities.AuditableModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Entidad que representa un flujo de caja para un bono.
 * Contiene información sobre pagos de cupón, amortización y valores presentes.
 */
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
    
    // Información del periodo
    private Integer periodo;
    private LocalDate fecha;
    
    // Componentes del flujo
    @Column(precision = 19, scale = 6)
    private BigDecimal cupon = BigDecimal.ZERO;
    
    @Column(precision = 19, scale = 6)
    private BigDecimal amortizacion = BigDecimal.ZERO;
    
    @Column(precision = 19, scale = 6)
    private BigDecimal flujoTotal = BigDecimal.ZERO;
    
    @Column(precision = 19, scale = 6)
    private BigDecimal saldoInsoluto = BigDecimal.ZERO;
    
    // Valores de descuento
    @Column(precision = 19, scale = 6)
    private BigDecimal valorPresente = BigDecimal.ZERO;
    
    // Campos adicionales para compatibilidad con transformadores y servicios
    @Column(precision = 19, scale = 6)
    private BigDecimal interes = BigDecimal.ZERO;
    
    @Column(precision = 19, scale = 6)
    private BigDecimal cuota = BigDecimal.ZERO;
    
    @Column(precision = 19, scale = 6)
    private BigDecimal saldo = BigDecimal.ZERO;
    
    @Column(precision = 19, scale = 6)
    private BigDecimal flujo = BigDecimal.ZERO;
    
    @Column(precision = 19, scale = 6)
    private BigDecimal factorDescuento = BigDecimal.ONE;
    
    @Column(precision = 19, scale = 6)
    private BigDecimal valorActual = BigDecimal.ZERO;
    
    @Column(precision = 19, scale = 6)
    private BigDecimal factorTiempo = BigDecimal.ZERO;
    
    /**
     * Calcula el flujo total como la suma del cupón y la amortización.
     * 
     * @return El flujo total
     */
    public BigDecimal calcularFlujoTotal() {
        this.flujoTotal = this.cupon.add(this.amortizacion);
        this.flujo = this.flujoTotal; // Para compatibilidad
        return this.flujoTotal;
    }
    
    /**
     * Actualiza todos los campos derivados para mantener consistencia.
     */
    public void actualizarCamposDerivados() {
        // Actualiza flujo total
        this.flujoTotal = this.cupon.add(this.amortizacion);
        
        // Actualiza campos para compatibilidad
        this.flujo = this.flujoTotal;
        this.cuota = this.flujoTotal;
        this.interes = this.cupon;
        this.saldo = this.saldoInsoluto;
        this.valorActual = this.valorPresente;
    }
    
    /**
     * Establece el factor de descuento y recalcula el valor presente.
     * 
     * @param factorDescuento El factor de descuento a aplicar
     */
    public void setFactorDescuentoYCalcularValorPresente(BigDecimal factorDescuento) {
        this.factorDescuento = factorDescuento;
        if (factorDescuento != null && factorDescuento.compareTo(BigDecimal.ZERO) > 0) {
            this.valorPresente = this.flujoTotal.divide(factorDescuento, 10, RoundingMode.HALF_UP);
            this.valorActual = this.valorPresente;
        }
    }
    
    @Override
    public String toString() {
        return "Flujo [P" + periodo + "] - " +
               "Fecha: " + fecha + ", " +
               "Cupón: " + cupon + ", " +
               "Amort.: " + amortizacion + ", " +
               "Total: " + flujoTotal + ", " +
               "Saldo: " + saldoInsoluto;
    }
}