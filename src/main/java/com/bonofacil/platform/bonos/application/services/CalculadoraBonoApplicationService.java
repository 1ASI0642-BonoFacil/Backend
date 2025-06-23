package com.bonofacil.platform.bonos.application.services;

import com.bonofacil.platform.bonos.domain.model.entities.FlujoFinanciero;
import com.bonofacil.platform.bonos.domain.model.valueobjects.DuracionConvexidad;
import com.bonofacil.platform.bonos.domain.model.valueobjects.PrecioMercado;
import com.bonofacil.platform.bonos.domain.model.valueobjects.Rendimiento;
import com.bonofacil.platform.bonos.domain.services.ICalculadoraBonoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de aplicación que hace de fachada para los servicios de cálculo de bonos.
 */
@Service
public class CalculadoraBonoApplicationService {
    
    private final ICalculadoraBonoService calculadoraBonoService;
    
    public CalculadoraBonoApplicationService(ICalculadoraBonoService calculadoraBonoService) {
        this.calculadoraBonoService = calculadoraBonoService;
    }
    
    /**
     * Genera el flujo de caja de un bono.
     */
    public List<FlujoFinanciero> generarFlujoCaja(String bonoId, BigDecimal tasaDescuento) {
        return calculadoraBonoService.generarFlujoCaja(bonoId, tasaDescuento);
    }
    
    /**
     * Calcula las métricas de duración, duración modificada y convexidad.
     */
    public DuracionConvexidad calcularMetricas(String bonoId, BigDecimal tasaMercado) {
        return calculadoraBonoService.calcularMetricas(bonoId, tasaMercado);
    }
    
    /**
     * Calcula el precio del bono.
     */
    public BigDecimal calcularPrecio(String bonoId, BigDecimal tasaMercado) {
        return calculadoraBonoService.calcularPrecio(bonoId, tasaMercado);
    }
    
    /**
     * Calcula la TCEA (Tasa de Coste Efectivo Anual) desde la perspectiva del emisor.
     */
    public Rendimiento calcularTCEA(String bonoId, BigDecimal costosEmision) {
        return calculadoraBonoService.calcularTCEA(bonoId, costosEmision);
    }
    
    /**
     * Calcula la TREA (Tasa de Rendimiento Efectivo Anual) desde la perspectiva del inversor.
     */
    public Rendimiento calcularTREA(String bonoId, BigDecimal precioCompra) {
        return calculadoraBonoService.calcularTREA(bonoId, precioCompra);
    }
    
    /**
     * Calcula el precio máximo que el mercado estaría dispuesto a pagar por el bono.
     */
    public PrecioMercado calcularPrecioMercado(String bonoId, BigDecimal tasaMercado) {
        return calculadoraBonoService.calcularPrecioMercado(bonoId, tasaMercado);
    }
} 