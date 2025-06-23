package com.bonofacil.platform.bonos.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value object que encapsula la configuración para los cálculos relacionados con bonos.
 */
public class ConfiguracionCalculo {
    private final int diasAnio;
    private final int decimalesCalculo;
    private final int decimalesMuestra;
    private final RoundingMode modoRedondeo;
    private final Moneda monedaPredeterminada;
    
    /**
     * Constructor para ConfiguracionCalculo.
     * 
     * @param diasAnio Días considerados en un año (360 o 365)
     * @param decimalesCalculo Número de decimales para cálculos internos
     * @param decimalesMuestra Número de decimales para mostrar resultados
     * @param modoRedondeo Modo de redondeo para los cálculos
     * @param monedaPredeterminada Moneda predeterminada para los bonos
     */
    public ConfiguracionCalculo(int diasAnio, int decimalesCalculo, int decimalesMuestra, 
                               RoundingMode modoRedondeo, Moneda monedaPredeterminada) {
        if (diasAnio != 360 && diasAnio != 365 && diasAnio != 366) {
            throw new IllegalArgumentException("Los días del año deben ser 360, 365 o 366");
        }
        
        if (decimalesCalculo < 6) {
            throw new IllegalArgumentException("El número de decimales para cálculos debe ser al menos 6");
        }
        
        if (decimalesMuestra < 2) {
            throw new IllegalArgumentException("El número de decimales para mostrar debe ser al menos 2");
        }
        
        if (modoRedondeo == null) {
            throw new IllegalArgumentException("El modo de redondeo no puede ser nulo");
        }
        
        if (monedaPredeterminada == null) {
            throw new IllegalArgumentException("La moneda predeterminada no puede ser nula");
        }
        
        this.diasAnio = diasAnio;
        this.decimalesCalculo = decimalesCalculo;
        this.decimalesMuestra = decimalesMuestra;
        this.modoRedondeo = modoRedondeo;
        this.monedaPredeterminada = monedaPredeterminada;
    }
    
    /**
     * Crea una configuración estándar con valores predeterminados.
     * 
     * @param monedaPredeterminada Moneda predeterminada
     * @return Una nueva instancia con configuración estándar
     */
    public static ConfiguracionCalculo configuracionEstandar(Moneda monedaPredeterminada) {
        return new ConfiguracionCalculo(360, 8, 4, RoundingMode.HALF_UP, monedaPredeterminada);
    }
    
    /**
     * Crea una configuración exacta con mayor precisión para cálculos.
     * 
     * @param monedaPredeterminada Moneda predeterminada
     * @return Una nueva instancia con configuración de alta precisión
     */
    public static ConfiguracionCalculo configuracionExacta(Moneda monedaPredeterminada) {
        return new ConfiguracionCalculo(365, 12, 6, RoundingMode.HALF_EVEN, monedaPredeterminada);
    }

    public int getDiasAnio() {
        return diasAnio;
    }

    public int getDecimalesCalculo() {
        return decimalesCalculo;
    }

    public int getDecimalesMuestra() {
        return decimalesMuestra;
    }

    public RoundingMode getModoRedondeo() {
        return modoRedondeo;
    }

    public Moneda getMonedaPredeterminada() {
        return monedaPredeterminada;
    }
    
    /**
     * Formatea un valor BigDecimal para mostrar al usuario.
     * 
     * @param valor El valor a formatear
     * @return El valor formateado
     */
    public BigDecimal formatearParaMostrar(BigDecimal valor) {
        return valor.setScale(decimalesMuestra, modoRedondeo);
    }
    
    /**
     * Formatea un valor BigDecimal para cálculos internos.
     * 
     * @param valor El valor a formatear
     * @return El valor formateado
     */
    public BigDecimal formatearParaCalculo(BigDecimal valor) {
        return valor.setScale(decimalesCalculo, modoRedondeo);
    }
    
    /**
     * Calcula la tasa diaria a partir de una tasa anual.
     * 
     * @param tasaAnual Tasa anual efectiva
     * @return La tasa diaria equivalente
     */
    public BigDecimal calcularTasaDiaria(BigDecimal tasaAnual) {
        // Fórmula: (1 + TEA)^(1/diasAnio) - 1
        double exponente = 1.0 / diasAnio;
        double resultado = Math.pow(1.0 + tasaAnual.doubleValue(), exponente) - 1.0;
        return new BigDecimal(resultado).setScale(decimalesCalculo, modoRedondeo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfiguracionCalculo that = (ConfiguracionCalculo) o;
        return diasAnio == that.diasAnio &&
               decimalesCalculo == that.decimalesCalculo &&
               decimalesMuestra == that.decimalesMuestra &&
               modoRedondeo == that.modoRedondeo &&
               Objects.equals(monedaPredeterminada, that.monedaPredeterminada);
    }

    @Override
    public int hashCode() {
        return Objects.hash(diasAnio, decimalesCalculo, decimalesMuestra, modoRedondeo, monedaPredeterminada);
    }

    @Override
    public String toString() {
        return "Configuración [" +
               "Días/Año: " + diasAnio + ", " +
               "Decimales: " + decimalesCalculo + "/" + decimalesMuestra + ", " +
               "Moneda: " + monedaPredeterminada.getCodigo() +
               "]";
    }
} 