package com.bonofacil.platform.bonos.domain.services;

import com.bonofacil.platform.bonos.domain.model.entities.FlujoFinanciero;
import com.bonofacil.platform.bonos.domain.model.valueobjects.DuracionConvexidad;
import com.bonofacil.platform.bonos.domain.model.valueobjects.PrecioMercado;
import com.bonofacil.platform.bonos.domain.model.valueobjects.Rendimiento;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interfaz para servicios de cálculo relacionados con bonos.
 * Define operaciones para cálculos financieros como flujos de caja, 
 * métricas (duración, convexidad) y rendimientos (TCEA, TREA).
 */
public interface ICalculadoraBonoService {
    
    /**
     * Genera el flujo de caja de un bono según el método americano.
     * 
     * @param bonoId ID del bono
     * @param tasaDescuento Tasa de descuento para calcular valores presentes
     * @return Lista de flujos de caja proyectados
     */
    List<FlujoFinanciero> generarFlujoCaja(String bonoId, BigDecimal tasaDescuento);
    
    /**
     * Calcula las métricas de duración, duración modificada y convexidad.
     * 
     * @param bonoId ID del bono
     * @param tasaMercado Tasa de mercado para el descuento
     * @return Objeto con métricas calculadas
     */
    DuracionConvexidad calcularMetricas(String bonoId, BigDecimal tasaMercado);
    
    /**
     * Calcula el precio del bono dado una tasa de mercado.
     * 
     * @param bonoId ID del bono
     * @param tasaMercado Tasa de mercado para el descuento
     * @return Precio calculado
     */
    BigDecimal calcularPrecio(String bonoId, BigDecimal tasaMercado);
    
    /**
     * Calcula la TCEA (Tasa de Coste Efectivo Anual) desde la perspectiva del emisor.
     * 
     * @param bonoId ID del bono
     * @param costosEmision Costos de emisión del bono
     * @return Objeto con la TCEA calculada
     */
    Rendimiento calcularTCEA(String bonoId, BigDecimal costosEmision);
    
    /**
     * Calcula la TREA (Tasa de Rendimiento Efectivo Anual) desde la perspectiva del inversor.
     * 
     * @param bonoId ID del bono
     * @param precioCompra Precio de compra del bono
     * @return Objeto con la TREA calculada
     */
    Rendimiento calcularTREA(String bonoId, BigDecimal precioCompra);
    
    /**
     * Calcula el precio máximo que el mercado estaría dispuesto a pagar por el bono.
     * 
     * @param bonoId ID del bono
     * @param tasaMercado Tasa de rendimiento requerida por el mercado
     * @return Objeto con el precio máximo calculado
     */
    PrecioMercado calcularPrecioMercado(String bonoId, BigDecimal tasaMercado);
} 