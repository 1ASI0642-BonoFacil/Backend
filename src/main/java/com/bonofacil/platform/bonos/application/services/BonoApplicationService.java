package com.bonofacil.platform.bonos.application.services;

import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import com.bonofacil.platform.bonos.domain.model.valueobjects.*;
import com.bonofacil.platform.bonos.domain.services.IBonoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación que hace de fachada para los servicios de dominio relacionados con bonos.
 */
@Service
public class BonoApplicationService {
    
    private final IBonoService bonoService;
    
    public BonoApplicationService(IBonoService bonoService) {
        this.bonoService = bonoService;
    }
    
    /**
     * Crea un nuevo bono.
     */
    public Long crearBono(String nombre, BigDecimal valorNominal, BigDecimal tasaCupon,
                        int plazoAnios, int frecuenciaPagos, PlazoGracia plazoGracia,
                        Moneda moneda, TasaInteres tasaInteres, LocalDate fechaEmision,
                        String descripcion, BigDecimal tasaDescuento, String metodoAmortizacion) {
        
        return bonoService.crearBono(
            nombre, valorNominal, tasaCupon, plazoAnios, frecuenciaPagos,
            plazoGracia, moneda, tasaInteres, fechaEmision, descripcion,
            tasaDescuento, metodoAmortizacion
        );
    }
    
    /**
     * Actualiza un bono existente.
     */
    public boolean actualizarBono(Long id, String nombre, BigDecimal valorNominal, BigDecimal tasaCupon,
                               int plazoAnios, int frecuenciaPagos, PlazoGracia plazoGracia,
                               Moneda moneda, TasaInteres tasaInteres, LocalDate fechaEmision,
                               String descripcion, BigDecimal tasaDescuento, String metodoAmortizacion) {
        
        return bonoService.actualizarBono(
            id, nombre, valorNominal, tasaCupon, plazoAnios, frecuenciaPagos,
            plazoGracia, moneda, tasaInteres, fechaEmision, descripcion,
            tasaDescuento, metodoAmortizacion
        );
    }
    
    /**
     * Elimina un bono.
     */
    public boolean eliminarBono(Long id) {
        return bonoService.eliminarBono(id);
    }
    
    /**
     * Busca un bono por su ID.
     */
    public Optional<Bono> buscarBonoPorId(Long id) {
        return bonoService.buscarBonoPorId(id);
    }
    
    /**
     * Lista todos los bonos.
     */
    public List<Bono> listarBonos() {
        return bonoService.listarBonos();
    }
    
    /**
     * Busca bonos por nombre.
     */
    public List<Bono> buscarBonosPorNombre(String nombre) {
        return bonoService.buscarBonosPorNombre(nombre);
    }
    
    /**
     * Busca bonos por moneda.
     */
    public List<Bono> buscarBonosPorMoneda(String codigoMoneda) {
        return bonoService.buscarBonosPorMoneda(codigoMoneda);
    }
} 