package com.bonofacil.platform.bonos.interfaces.rest.controllers;

import com.bonofacil.platform.bonos.application.services.BonoService;
import com.bonofacil.platform.bonos.application.services.CalculoService;
import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import com.bonofacil.platform.bonos.domain.model.entities.Calculo;
import com.bonofacil.platform.bonos.domain.model.entities.FlujoFinanciero;
import com.bonofacil.platform.bonos.domain.services.CalculoFinancieroService;
import com.bonofacil.platform.bonos.interfaces.rest.resources.*;
import com.bonofacil.platform.bonos.interfaces.rest.transform.BonoResourceFromEntityAssembler;
import com.bonofacil.platform.bonos.interfaces.rest.transform.CalculoResourceFromEntityAssembler;
import com.bonofacil.platform.bonos.interfaces.rest.transform.FlujoFinancieroResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/inversor", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Inversor", description = "Endpoints para análisis de bonos por inversores")

public class InversorBonoController {

    private final BonoService bonoService;
    private final CalculoService calculoService;
    private final CalculoFinancieroService calculoFinancieroService;

    @Autowired
    public InversorBonoController(BonoService bonoService, CalculoService calculoService,
                                  CalculoFinancieroService calculoFinancieroService) {
        this.bonoService = bonoService;
        this.calculoService = calculoService;
        this.calculoFinancieroService = calculoFinancieroService;
    }

    @GetMapping("/bonos/catalogo")
    @Operation(summary = "Obtener catálogo completo de bonos disponibles")
    public ResponseEntity<List<BonoResource>> obtenerCatalogoBonos() {
        List<Bono> bonos = bonoService.obtenerTodosLosBonos();
        List<BonoResource> resources = bonos.stream()
                .map(BonoResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/bonos/catalogo/{id}")
    @Operation(summary = "Obtener detalles de un bono específico")
    public ResponseEntity<BonoResource> obtenerBonoPorId(@PathVariable Long id) {
        return bonoService.obtenerBonoPorId(id)
                .map(BonoResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bonos/catalogo/moneda/{moneda}")
    @Operation(summary = "Filtrar bonos por tipo de moneda")
    public ResponseEntity<List<BonoResource>> obtenerBonosPorMoneda(
            @Parameter(description = "Código de moneda (ej: USD, PEN)") @PathVariable String moneda) {
        List<Bono> bonos = bonoService.obtenerBonosPorMoneda(moneda);
        List<BonoResource> resources = bonos.stream()
                .map(BonoResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/bonos/catalogo/tasa")
    @Operation(summary = "Filtrar bonos por rango de tasa cupón")
    public ResponseEntity<List<BonoResource>> obtenerBonosPorRangoTasa(
            @Parameter(description = "Tasa mínima (ej: 5.0)") @RequestParam double tasaMinima,
            @Parameter(description = "Tasa máxima (opcional)") @RequestParam(required = false) Double tasaMaxima) {
        
        double maxTasa = tasaMaxima != null ? tasaMaxima : Double.MAX_VALUE;
        List<Bono> bonos = bonoService.obtenerBonosPorRangoTasa(tasaMinima, maxTasa);
        List<BonoResource> resources = bonos.stream()
                .map(BonoResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/bonos/{id}/flujo")
    @Operation(summary = "Obtener el flujo financiero de un bono")
    public ResponseEntity<List<FlujoFinancieroResource>> obtenerFlujoFinanciero(@PathVariable Long id) {
        return bonoService.obtenerBonoPorId(id)
                .map(bono -> {
                    List<FlujoFinanciero> flujos = bonoService.obtenerFlujoFinancieroBono(id);
                    
                    // Si no hay flujos en la base de datos, los generamos automáticamente
                    if (flujos == null || flujos.isEmpty()) {
                        flujos = calculoFinancieroService.calcularFlujoFinanciero(bono);
                    }
                    
                    List<FlujoFinancieroResource> resources = flujos.stream()
                            .map(FlujoFinancieroResourceFromEntityAssembler::toResourceFromEntity)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(resources);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/calculos")
    @Operation(summary = "Realizar cálculo de inversión (TREA y precio máximo)")
    public ResponseEntity<CalculoResource> calcularInversion(@RequestBody CreateCalculoResource resource) {
        String username = obtenerUsernameAutenticado();
        Bono bono = bonoService.obtenerBonoPorId(resource.getBonoId())
                .orElseThrow(() -> new IllegalArgumentException("Bono no encontrado"));
        
        // Calcular inversión directamente con la tasa en porcentaje
        Calculo calculo = calculoFinancieroService.calcularInversion(bono, resource.getTasaEsperada());
        calculo.setInversorUsername(username);
        
        Calculo calculoGuardado = calculoService.guardarCalculo(calculo);
        return new ResponseEntity<>(CalculoResourceFromEntityAssembler.toResourceFromEntity(calculoGuardado), HttpStatus.CREATED);
    }

    @GetMapping("/calculos")
    @Operation(summary = "Obtener todos mis cálculos de inversión")
    public ResponseEntity<List<CalculoResource>> obtenerMisCalculos() {
        String username = obtenerUsernameAutenticado();
        List<Calculo> calculos = calculoService.obtenerCalculosPorInversor(username);
        List<CalculoResource> resources = calculos.stream()
                .map(CalculoResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/calculos/{id}")
    @Operation(summary = "Obtener detalle de un cálculo específico")
    public ResponseEntity<CalculoResource> obtenerCalculoPorId(@PathVariable Long id) {
        String username = obtenerUsernameAutenticado();
        return calculoService.obtenerCalculoPorId(id)
                .filter(calculo -> calculo.getInversorUsername().equals(username))
                .map(CalculoResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/calculos/{id}")
    @Operation(summary = "Eliminar un cálculo")
    public ResponseEntity<Void> eliminarCalculo(@PathVariable Long id) {
        String username = obtenerUsernameAutenticado();
        return calculoService.obtenerCalculoPorId(id)
                .filter(calculo -> calculo.getInversorUsername().equals(username))
                .map(calculo -> {
                    calculoService.eliminarCalculo(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private String obtenerUsernameAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}