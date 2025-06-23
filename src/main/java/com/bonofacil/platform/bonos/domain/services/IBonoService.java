package com.bonofacil.platform.bonos.domain.services;

import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import com.bonofacil.platform.bonos.domain.model.valueobjects.Moneda;
import com.bonofacil.platform.bonos.domain.model.valueobjects.PlazoGracia;
import com.bonofacil.platform.bonos.domain.model.valueobjects.TasaInteres;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz para servicios relacionados con la gestión de bonos.
 * Define operaciones CRUD y otras funcionalidades para gestionar bonos.
 */
public interface IBonoService {
    
    /**
     * Crea un nuevo bono con los parámetros proporcionados.
     * 
     * @param nombre Nombre del bono
     * @param valorNominal Valor nominal del bono
     * @param tasaCupon Tasa cupón del bono
     * @param plazoAnios Plazo en años
     * @param frecuenciaPagos Frecuencia de pagos por año
     * @param plazoGracia Objeto que representa el plazo de gracia
     * @param moneda Moneda del bono
     * @param tasaInteres Tasa de interés y su tipo
     * @param fechaEmision Fecha de emisión del bono
     * @param descripcion Descripción del bono
     * @param tasaDescuento Tasa de descuento del bono
     * @param metodoAmortizacion Método de amortización del bono
     * @return ID del bono creado
     */
    Long crearBono(String nombre, BigDecimal valorNominal, BigDecimal tasaCupon,
                  int plazoAnios, int frecuenciaPagos, PlazoGracia plazoGracia,
                  Moneda moneda, TasaInteres tasaInteres, LocalDate fechaEmision,
                  String descripcion, BigDecimal tasaDescuento, String metodoAmortizacion);
    
    /**
     * Actualiza un bono existente.
     * 
     * @param id ID del bono a actualizar
     * @param nombre Nombre del bono
     * @param valorNominal Valor nominal del bono
     * @param tasaCupon Tasa cupón del bono
     * @param plazoAnios Plazo en años
     * @param frecuenciaPagos Frecuencia de pagos por año
     * @param plazoGracia Objeto que representa el plazo de gracia
     * @param moneda Moneda del bono
     * @param tasaInteres Tasa de interés y su tipo
     * @param fechaEmision Fecha de emisión del bono
     * @param descripcion Descripción del bono
     * @param tasaDescuento Tasa de descuento del bono
     * @param metodoAmortizacion Método de amortización del bono
     * @return true si la actualización fue exitosa
     */
    boolean actualizarBono(Long id, String nombre, BigDecimal valorNominal, BigDecimal tasaCupon,
                         int plazoAnios, int frecuenciaPagos, PlazoGracia plazoGracia,
                         Moneda moneda, TasaInteres tasaInteres, LocalDate fechaEmision,
                         String descripcion, BigDecimal tasaDescuento, String metodoAmortizacion);
    
    /**
     * Elimina un bono por su ID.
     * 
     * @param id ID del bono a eliminar
     * @return true si la eliminación fue exitosa
     */
    boolean eliminarBono(Long id);
    
    /**
     * Busca un bono por su ID.
     * 
     * @param id ID del bono a buscar
     * @return Optional con el bono si existe, vacío si no
     */
    Optional<Bono> buscarBonoPorId(Long id);
    
    /**
     * Lista todos los bonos disponibles.
     * 
     * @return Lista de bonos
     */
    List<Bono> listarBonos();
    
    /**
     * Busca bonos por nombre (parcial o completo).
     * 
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de bonos que coinciden con el criterio
     */
    List<Bono> buscarBonosPorNombre(String nombre);
    
    /**
     * Busca bonos por moneda.
     * 
     * @param codigoMoneda Código de la moneda a buscar
     * @return Lista de bonos en la moneda especificada
     */
    List<Bono> buscarBonosPorMoneda(String codigoMoneda);
} 