package com.bonofacil.platform.bonos.application.internal.services;

import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import com.bonofacil.platform.bonos.domain.model.entities.FlujoFinanciero;
import com.bonofacil.platform.bonos.domain.model.valueobjects.DuracionConvexidad;
import com.bonofacil.platform.bonos.domain.model.valueobjects.PrecioMercado;
import com.bonofacil.platform.bonos.domain.model.valueobjects.Rendimiento;
import com.bonofacil.platform.bonos.domain.services.ICalculadoraBonoService;
import com.bonofacil.platform.bonos.infrastructure.persistence.jpa.repositories.BonoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Implementación de los servicios de cálculo para bonos.
 */
@Service
public class CalculadoraBonoServiceImpl implements ICalculadoraBonoService {
    
    private final BonoRepository bonoRepository;
    
    public CalculadoraBonoServiceImpl(BonoRepository bonoRepository) {
        this.bonoRepository = bonoRepository;
    }
    
    @Override
    public List<FlujoFinanciero> generarFlujoCaja(String bonoId, BigDecimal tasaDescuento) {
        Long id = Long.parseLong(bonoId);
        return bonoRepository.findById(id)
                .map(bono -> bono.generarFlujoCajaMetodoAmericano(tasaDescuento))
                .orElse(Collections.emptyList());
    }
    
    @Override
    public DuracionConvexidad calcularMetricas(String bonoId, BigDecimal tasaMercado) {
        Long id = Long.parseLong(bonoId);
        return bonoRepository.findById(id)
                .map(bono -> bono.calcularMetricas(tasaMercado))
                .orElseThrow(() -> new IllegalArgumentException("Bono no encontrado con ID: " + bonoId));
    }
    
    @Override
    public BigDecimal calcularPrecio(String bonoId, BigDecimal tasaMercado) {
        Long id = Long.parseLong(bonoId);
        return bonoRepository.findById(id)
                .map(bono -> bono.calcularPrecio(tasaMercado))
                .orElseThrow(() -> new IllegalArgumentException("Bono no encontrado con ID: " + bonoId));
    }
    
    @Override
    public Rendimiento calcularTCEA(String bonoId, BigDecimal costosEmision) {
        Long id = Long.parseLong(bonoId);
        return bonoRepository.findById(id)
                .map(bono -> bono.calcularTCEA(costosEmision))
                .orElseThrow(() -> new IllegalArgumentException("Bono no encontrado con ID: " + bonoId));
    }
    
    @Override
    public Rendimiento calcularTREA(String bonoId, BigDecimal precioCompra) {
        Long id = Long.parseLong(bonoId);
        return bonoRepository.findById(id)
                .map(bono -> bono.calcularTREA(precioCompra))
                .orElseThrow(() -> new IllegalArgumentException("Bono no encontrado con ID: " + bonoId));
    }
    
    @Override
    public PrecioMercado calcularPrecioMercado(String bonoId, BigDecimal tasaMercado) {
        Long id = Long.parseLong(bonoId);
        return bonoRepository.findById(id)
                .map(bono -> bono.calcularPrecioMercado(tasaMercado))
                .orElseThrow(() -> new IllegalArgumentException("Bono no encontrado con ID: " + bonoId));
    }
} 