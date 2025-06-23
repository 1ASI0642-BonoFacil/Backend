package com.bonofacil.platform.bonos.interfaces.rest.controllers;

import com.bonofacil.platform.bonos.application.services.BonoApplicationService;
import com.bonofacil.platform.bonos.interfaces.rest.resources.BonoResource;
import com.bonofacil.platform.bonos.interfaces.rest.transform.BonoResourceAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador REST para la gestión de bonos.
 */
@RestController
@RequestMapping("/api/bonos")
public class BonoController {
    
    private static final Logger logger = LoggerFactory.getLogger(BonoController.class);
    
    private final BonoApplicationService bonoService;
    private final BonoResourceAssembler bonoAssembler;
    
    public BonoController(BonoApplicationService bonoService, BonoResourceAssembler bonoAssembler) {
        this.bonoService = bonoService;
        this.bonoAssembler = bonoAssembler;
    }
    
    /**
     * Crea un nuevo bono.
     * 
     * @param bonoResource Recursos con datos del bono
     * @return El bono creado
     */
    @PostMapping
    public ResponseEntity<BonoResource> crearBono(@Valid @RequestBody BonoResource bonoResource) {
        logger.info("Creando bono: nombre={}, descripcion={}, tasaDescuento={}, metodoAmortizacion={}", 
                bonoResource.getNombre(), bonoResource.getDescripcion(), 
                bonoResource.getTasaDescuento(), bonoResource.getMetodoAmortizacion());
        
        Long bonoId = bonoService.crearBono(
            bonoResource.getNombre(),
            bonoResource.getValorNominal(),
            bonoResource.getTasaCupon(),
            bonoResource.getPlazoAnios(),
            bonoResource.getFrecuenciaPagos(),
            bonoAssembler.toDomainPlazoGracia(bonoResource.getPlazoGracia()),
            bonoAssembler.toDomainMoneda(bonoResource.getMoneda()),
            bonoAssembler.toDomainTasaInteres(bonoResource.getTasaInteres()),
            bonoResource.getFechaEmision(),
            bonoResource.getDescripcion(),
            bonoResource.getTasaDescuento(),
            bonoResource.getMetodoAmortizacion()
        );
        
        var bono = bonoService.buscarBonoPorId(bonoId).orElseThrow();
        logger.info("Bono creado: id={}, descripcion={}, tasaDescuento={}, metodoAmortizacion={}", 
                bono.getId(), bono.getDescripcion(), bono.getTasaDescuento(), bono.getMetodoAmortizacion());
        
        var resource = bonoAssembler.toResource(bono);
        logger.info("BonoResource creado: id={}, descripcion={}, tasaDescuento={}, metodoAmortizacion={}", 
                resource.getId(), resource.getDescripcion(), resource.getTasaDescuento(), resource.getMetodoAmortizacion());
                
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(resource);
    }
    
    /**
     * Actualiza un bono existente.
     * 
     * @param id ID del bono a actualizar
     * @param bonoResource Recursos con datos actualizados
     * @return El bono actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<BonoResource> actualizarBono(
            @PathVariable String id, 
            @Valid @RequestBody BonoResource bonoResource) {
        
        Long bonoId = Long.parseLong(id);
        
        boolean actualizado = bonoService.actualizarBono(
            bonoId,
            bonoResource.getNombre(),
            bonoResource.getValorNominal(),
            bonoResource.getTasaCupon(),
            bonoResource.getPlazoAnios(),
            bonoResource.getFrecuenciaPagos(),
            bonoAssembler.toDomainPlazoGracia(bonoResource.getPlazoGracia()),
            bonoAssembler.toDomainMoneda(bonoResource.getMoneda()),
            bonoAssembler.toDomainTasaInteres(bonoResource.getTasaInteres()),
            bonoResource.getFechaEmision(),
            bonoResource.getDescripcion(),
            bonoResource.getTasaDescuento(),
            bonoResource.getMetodoAmortizacion()
        );
        
        if (actualizado) {
            return ResponseEntity.ok(
                bonoAssembler.toResource(bonoService.buscarBonoPorId(bonoId).orElseThrow())
            );
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Elimina un bono.
     * 
     * @param id ID del bono a eliminar
     * @return Respuesta vacía con código 204 si la eliminación fue exitosa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBono(@PathVariable String id) {
        Long bonoId = Long.parseLong(id);
        
        boolean eliminado = bonoService.eliminarBono(bonoId);
        
        return eliminado ? 
            ResponseEntity.noContent().build() : 
            ResponseEntity.notFound().build();
    }
    
    /**
     * Obtiene un bono por su ID.
     * 
     * @param id ID del bono a obtener
     * @return El bono solicitado
     */
    @GetMapping("/{id}")
    public ResponseEntity<BonoResource> obtenerBono(@PathVariable String id) {
        Long bonoId = Long.parseLong(id);
        
        return bonoService.buscarBonoPorId(bonoId)
            .map(bono -> ResponseEntity.ok(bonoAssembler.toResource(bono)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Lista todos los bonos disponibles.
     * 
     * @return Lista de bonos
     */
    @GetMapping
    public ResponseEntity<List<BonoResource>> listarBonos() {
        return ResponseEntity.ok(
            bonoAssembler.toResourceList(bonoService.listarBonos())
        );
    }
    
    /**
     * Busca bonos por nombre.
     * 
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de bonos que coinciden con el criterio
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<BonoResource>> buscarBonosPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(
            bonoAssembler.toResourceList(bonoService.buscarBonosPorNombre(nombre))
        );
    }
    
    /**
     * Busca bonos por moneda.
     * 
     * @param moneda Código de la moneda a buscar
     * @return Lista de bonos en la moneda especificada
     */
    @GetMapping("/moneda/{moneda}")
    public ResponseEntity<List<BonoResource>> buscarBonosPorMoneda(@PathVariable String moneda) {
        return ResponseEntity.ok(
            bonoAssembler.toResourceList(bonoService.buscarBonosPorMoneda(moneda))
        );
    }
} 