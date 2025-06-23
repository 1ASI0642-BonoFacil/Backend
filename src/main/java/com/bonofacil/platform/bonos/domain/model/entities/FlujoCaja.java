package com.bonofacil.platform.bonos.domain.model.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad que representa un flujo de caja en la proyección de un bono.
 * Contiene información sobre cupones, amortizaciones y fechas de pago.
 */
public class FlujoCaja {
    private UUID id;
    private int numeroPeriodo;
    private LocalDate fecha;
    private BigDecimal cupon;
    private BigDecimal amortizacion;
    private BigDecimal flujoTotal;
    private BigDecimal valorPresente;
    private BigDecimal factorDescuento;
    private BigDecimal saldoInsoluto;
    private boolean periodoGracia;
    private boolean capitalizaInteres;

    /**
     * Constructor para FlujoCaja.
     * 
     * @param id Identificador único
     * @param numeroPeriodo Número de período del flujo
     * @param fecha Fecha de pago
     * @param cupon Monto del cupón (interés)
     * @param amortizacion Monto de amortización de capital
     * @param flujoTotal Suma de cupón y amortización
     * @param valorPresente Valor presente del flujo
     * @param factorDescuento Factor de descuento aplicado
     * @param saldoInsoluto Saldo pendiente de pago después del flujo
     * @param periodoGracia Indica si este flujo está en período de gracia
     * @param capitalizaInteres Indica si los intereses se capitalizan
     */
    public FlujoCaja(UUID id, int numeroPeriodo, LocalDate fecha, BigDecimal cupon,
                    BigDecimal amortizacion, BigDecimal flujoTotal, BigDecimal valorPresente,
                    BigDecimal factorDescuento, BigDecimal saldoInsoluto, 
                    boolean periodoGracia, boolean capitalizaInteres) {
        this.id = id;
        this.numeroPeriodo = numeroPeriodo;
        this.fecha = fecha;
        this.cupon = cupon;
        this.amortizacion = amortizacion;
        this.flujoTotal = flujoTotal;
        this.valorPresente = valorPresente;
        this.factorDescuento = factorDescuento;
        this.saldoInsoluto = saldoInsoluto;
        this.periodoGracia = periodoGracia;
        this.capitalizaInteres = capitalizaInteres;
    }
    
    /**
     * Constructor para crear un flujo de caja con ID generado automáticamente.
     */
    public static FlujoCaja crear(int numeroPeriodo, LocalDate fecha, BigDecimal cupon,
                              BigDecimal amortizacion, BigDecimal flujoTotal, BigDecimal valorPresente,
                              BigDecimal factorDescuento, BigDecimal saldoInsoluto, 
                              boolean periodoGracia, boolean capitalizaInteres) {
        return new FlujoCaja(
            UUID.randomUUID(), numeroPeriodo, fecha, cupon, amortizacion, flujoTotal,
            valorPresente, factorDescuento, saldoInsoluto, periodoGracia, capitalizaInteres
        );
    }

    public UUID getId() {
        return id;
    }

    public int getNumeroPeriodo() {
        return numeroPeriodo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public BigDecimal getCupon() {
        return cupon;
    }

    public BigDecimal getAmortizacion() {
        return amortizacion;
    }

    public BigDecimal getFlujoTotal() {
        return flujoTotal;
    }

    public BigDecimal getValorPresente() {
        return valorPresente;
    }
    
    public BigDecimal getFactorDescuento() {
        return factorDescuento;
    }

    public BigDecimal getSaldoInsoluto() {
        return saldoInsoluto;
    }

    public boolean isPeriodoGracia() {
        return periodoGracia;
    }

    public boolean isCapitalizaInteres() {
        return capitalizaInteres;
    }
    
    /**
     * Actualiza el valor presente del flujo con una nueva tasa de descuento.
     * 
     * @param nuevaTasaDescuento Nueva tasa de descuento (por período)
     * @param periodoActual Período actual desde el que se calcula el descuento
     * @return Un nuevo FlujoCaja con el valor presente actualizado
     */
    public FlujoCaja actualizarValorPresente(BigDecimal nuevaTasaDescuento, int periodoActual) {
        int periodosDiferencia = this.numeroPeriodo - periodoActual;
        
        if (periodosDiferencia < 0) {
            // Los flujos pasados no cambian su valor presente
            return this;
        }
        
        // Calculamos el nuevo factor de descuento: 1 / (1 + r)^n
        BigDecimal nuevoFactor = BigDecimal.ONE.divide(
            BigDecimal.ONE.add(nuevaTasaDescuento).pow(periodosDiferencia),
            10, RoundingMode.HALF_UP
        );
        
        // Calculamos el nuevo valor presente
        BigDecimal nuevoValorPresente = this.flujoTotal.multiply(nuevoFactor);
        
        // Creamos un nuevo flujo con el valor presente actualizado
        return new FlujoCaja(
            this.id, this.numeroPeriodo, this.fecha, this.cupon, 
            this.amortizacion, this.flujoTotal, nuevoValorPresente, 
            nuevoFactor, this.saldoInsoluto, this.periodoGracia, this.capitalizaInteres
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlujoCaja flujoCaja = (FlujoCaja) o;
        return Objects.equals(id, flujoCaja.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Flujo " + numeroPeriodo + " [" + fecha + "] - " +
               "Cupón: " + cupon + ", " +
               "Amortización: " + amortizacion + ", " +
               "Total: " + flujoTotal;
    }
} 