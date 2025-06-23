package com.bonofacil.platform.bonos.application.internal.services;

import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import com.bonofacil.platform.bonos.domain.model.valueobjects.*;
import com.bonofacil.platform.bonos.domain.services.IBonoService;
import com.bonofacil.platform.bonos.infrastructure.persistence.jpa.repositories.BonoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de los servicios de gestión de bonos.
 */
@Service
public class BonoServiceImpl implements IBonoService {
    
    private static final Logger logger = LoggerFactory.getLogger(BonoServiceImpl.class);
    
    private final BonoRepository bonoRepository;
    
    public BonoServiceImpl(BonoRepository bonoRepository) {
        this.bonoRepository = bonoRepository;
    }
    
    @Override
    @Transactional
    public Long crearBono(String nombre, BigDecimal valorNominal, BigDecimal tasaCupon,
                         int plazoAnios, int frecuenciaPagos, PlazoGracia plazoGracia,
                         Moneda moneda, TasaInteres tasaInteres, LocalDate fechaEmision,
                         String descripcion, BigDecimal tasaDescuento, String metodoAmortizacion) {
        
        logger.info("Creando bono en servicio: nombre={}, descripcion={}, tasaDescuento={}, metodoAmortizacion={}", 
                nombre, descripcion, tasaDescuento, metodoAmortizacion);
                
        // Crear un nuevo bono
        Bono bono = new Bono();
        bono.setNombre(nombre);
        bono.setValorNominal(valorNominal);
        bono.setTasaCupon(tasaCupon);
        bono.setPlazoAnios(plazoAnios);
        bono.setFrecuenciaPagos(frecuenciaPagos);
        bono.setPlazoGracia(plazoGracia);
        bono.setMoneda(moneda.getCodigo());
        bono.setTasaInteres(tasaInteres);
        bono.setFechaEmision(fechaEmision);
        bono.setDescripcion(descripcion);
        bono.setTasaDescuento(tasaDescuento);
        bono.setMetodoAmortizacion(metodoAmortizacion != null ? metodoAmortizacion : "AMERICANO");
        
        logger.info("Bono previa persistencia: descripcion={}, tasaDescuento={}, metodoAmortizacion={}", 
                bono.getDescripcion(), bono.getTasaDescuento(), bono.getMetodoAmortizacion());
        
        // Generar flujo de caja inicial con tasa de descuento proporcionada o valor por defecto
        bono.generarFlujoCajaMetodoAmericano(tasaDescuento != null ? tasaDescuento : new BigDecimal("0.08"));
        
        // Guardar el bono
        Bono savedBono = bonoRepository.save(bono);
        
        logger.info("Bono guardado: id={}, descripcion={}, tasaDescuento={}, metodoAmortizacion={}", 
                savedBono.getId(), savedBono.getDescripcion(), savedBono.getTasaDescuento(), savedBono.getMetodoAmortizacion());
        
        return savedBono.getId();
    }
    
    @Override
    @Transactional
    public boolean actualizarBono(Long id, String nombre, BigDecimal valorNominal, BigDecimal tasaCupon,
                                int plazoAnios, int frecuenciaPagos, PlazoGracia plazoGracia,
                                Moneda moneda, TasaInteres tasaInteres, LocalDate fechaEmision,
                                String descripcion, BigDecimal tasaDescuento, String metodoAmortizacion) {
        
        Optional<Bono> bonoOptional = bonoRepository.findById(id);
        
        if (bonoOptional.isPresent()) {
            Bono bonoExistente = bonoOptional.get();
            
            // Actualizar los datos del bono existente
            bonoExistente.setNombre(nombre);
            bonoExistente.setValorNominal(valorNominal);
            bonoExistente.setTasaCupon(tasaCupon);
            bonoExistente.setPlazoAnios(plazoAnios);
            bonoExistente.setFrecuenciaPagos(frecuenciaPagos);
            bonoExistente.setPlazoGracia(plazoGracia);
            bonoExistente.setMoneda(moneda.getCodigo());
            bonoExistente.setTasaInteres(tasaInteres);
            bonoExistente.setFechaEmision(fechaEmision);
            bonoExistente.setDescripcion(descripcion);
            bonoExistente.setTasaDescuento(tasaDescuento);
            bonoExistente.setMetodoAmortizacion(metodoAmortizacion);
            
            // Generar flujo de caja actualizado con la tasa de descuento proporcionada
            bonoExistente.generarFlujoCajaMetodoAmericano(tasaDescuento != null ? tasaDescuento : new BigDecimal("0.08"));
            
            // Guardar el bono actualizado
            bonoRepository.save(bonoExistente);
            
            return true;
        }
        
        return false;
    }
    
    @Override
    @Transactional
    public boolean eliminarBono(Long id) {
        if (bonoRepository.existsById(id)) {
            bonoRepository.deleteById(id);
            return true;
        }
        
        return false;
    }
    
    @Override
    public Optional<Bono> buscarBonoPorId(Long id) {
        return bonoRepository.findById(id);
    }
    
    @Override
    public List<Bono> listarBonos() {
        return bonoRepository.findAll();
    }
    
    @Override
    public List<Bono> buscarBonosPorNombre(String nombre) {
        return bonoRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    @Override
    public List<Bono> buscarBonosPorMoneda(String codigoMoneda) {
        return bonoRepository.findByMoneda(codigoMoneda);
    }
} 