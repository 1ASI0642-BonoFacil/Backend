package com.bonofacil.platform.bonos.domain.model.valueobjects;

import java.util.Objects;

/**
 * Value object que representa un plazo de gracia para un bono.
 * El plazo de gracia puede ser parcial (solo se pagan intereses) o total (no se paga nada).
 */
public class PlazoGracia {
    public enum TipoPlazoGracia {
        NINGUNO,    // No hay período de gracia
        PARCIAL,    // Solo se pagan intereses, no capital
        TOTAL       // No se paga nada, se capitaliza
    }
    
    private final TipoPlazoGracia tipo;
    private final int periodos;
    
    /**
     * Constructor para PlazoGracia.
     * 
     * @param tipo Tipo de plazo de gracia
     * @param periodos Número de períodos de gracia
     */
    public PlazoGracia(TipoPlazoGracia tipo, int periodos) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de plazo de gracia no puede ser nulo");
        }
        
        if (tipo != TipoPlazoGracia.NINGUNO && periodos <= 0) {
            throw new IllegalArgumentException("El número de períodos debe ser mayor que cero");
        }
        
        if (tipo == TipoPlazoGracia.NINGUNO && periodos != 0) {
            throw new IllegalArgumentException("Para tipo NINGUNO, el número de períodos debe ser cero");
        }
        
        this.tipo = tipo;
        this.periodos = periodos;
    }
    
    public static PlazoGracia sinPlazoGracia() {
        return new PlazoGracia(TipoPlazoGracia.NINGUNO, 0);
    }
    
    public static PlazoGracia plazoGraciaParcial(int periodos) {
        return new PlazoGracia(TipoPlazoGracia.PARCIAL, periodos);
    }
    
    public static PlazoGracia plazoGraciaTotal(int periodos) {
        return new PlazoGracia(TipoPlazoGracia.TOTAL, periodos);
    }

    public TipoPlazoGracia getTipo() {
        return tipo;
    }

    public int getPeriodos() {
        return periodos;
    }
    
    public boolean esPlazoGraciaTotal() {
        return tipo == TipoPlazoGracia.TOTAL;
    }
    
    public boolean esPlazoGraciaParcial() {
        return tipo == TipoPlazoGracia.PARCIAL;
    }
    
    public boolean tieneGracia() {
        return tipo != TipoPlazoGracia.NINGUNO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlazoGracia that = (PlazoGracia) o;
        return periodos == that.periodos && tipo == that.tipo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipo, periodos);
    }

    @Override
    public String toString() {
        switch (tipo) {
            case NINGUNO:
                return "Sin plazo de gracia";
            case PARCIAL:
                return "Plazo de gracia parcial de " + periodos + " períodos";
            case TOTAL:
                return "Plazo de gracia total de " + periodos + " períodos";
            default:
                return "Plazo de gracia desconocido";
        }
    }
} 