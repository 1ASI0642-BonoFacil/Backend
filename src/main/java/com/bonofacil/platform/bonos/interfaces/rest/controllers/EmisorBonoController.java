package com.bonofacil.platform.bonos.interfaces.rest.controllers;

import com.bonofacil.platform.bonos.application.services.BonoService;
import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import com.bonofacil.platform.bonos.domain.model.entities.FlujoFinanciero;
import com.bonofacil.platform.bonos.domain.services.CalculoFinancieroService;
import com.bonofacil.platform.bonos.interfaces.rest.resources.BonoResource;
import com.bonofacil.platform.bonos.interfaces.rest.resources.CreateBonoResource;
import com.bonofacil.platform.bonos.interfaces.rest.transform.BonoResourceFromEntityAssembler;
import com.bonofacil.platform.iam.domain.model.aggregates.User;
import com.bonofacil.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/emisor/bonos", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Emisor Bonos", description = "Endpoints para manejo de bonos por emisores")
public class EmisorBonoController {

    private final BonoService bonoService;
    private final UserRepository userRepository;
    private final CalculoFinancieroService calculoFinancieroService;

    @Autowired
    public EmisorBonoController(BonoService bonoService, UserRepository userRepository, CalculoFinancieroService calculoFinancieroService) {
        this.bonoService = bonoService;
        this.userRepository = userRepository;
        this.calculoFinancieroService = calculoFinancieroService;
    }

    @GetMapping
    public ResponseEntity<List<BonoResource>> obtenerMisBonos() {
        String username = obtenerUsernameAutenticado();
        List<Bono> bonos = bonoService.obtenerBonosPorEmisor(username);
        List<BonoResource> resources = bonos.stream()
                .map(BonoResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BonoResource> obtenerBonoPorId(@PathVariable Long id) {
        String username = obtenerUsernameAutenticado();
        return bonoService.obtenerBonoPorId(id)
                .filter(bono -> bono.getEmisorUsername().equals(username))
                .map(BonoResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BonoResource> crearBono(@RequestBody CreateBonoResource resource) {
        String username = obtenerUsernameAutenticado();

        Bono bono = BonoResourceFromEntityAssembler.toEntityFromCreateResource(resource);
        bono.setEmisorUsername(username);

        Bono bonoCreado = bonoService.crearBono(bono);
        calculoFinancieroService.procesarCalculosBono(bonoCreado);

        bonoCreado = bonoService.actualizarBono(bonoCreado.getId(), bonoCreado);

        BonoResource bonoResource = BonoResourceFromEntityAssembler.toResourceFromEntity(bonoCreado);
        return new ResponseEntity<>(bonoResource, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BonoResource> actualizarBono(@PathVariable Long id, @RequestBody CreateBonoResource resource) {
        String username = obtenerUsernameAutenticado();

        return bonoService.obtenerBonoPorId(id)
                .filter(bono -> bono.getEmisorUsername().equals(username))
                .map(bono -> {
                    Bono bonoActualizado = BonoResourceFromEntityAssembler.toEntityFromCreateResource(resource);
                    bonoActualizado.setId(id);
                    bonoActualizado.setEmisorUsername(username);

                    Bono resultado = bonoService.actualizarBono(id, bonoActualizado);
                    calculoFinancieroService.procesarCalculosBono(resultado);
                    resultado = bonoService.actualizarBono(id, resultado);

                    return ResponseEntity.ok(BonoResourceFromEntityAssembler.toResourceFromEntity(resultado));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBono(@PathVariable Long id) {
        String username = obtenerUsernameAutenticado();

        return bonoService.obtenerBonoPorId(id)
                .filter(bono -> bono.getEmisorUsername().equals(username))
                .map(bono -> {
                    bonoService.eliminarBono(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/flujo")
    public ResponseEntity<List<FlujoFinanciero>> obtenerFlujoFinanciero(@PathVariable Long id) {
        String username = obtenerUsernameAutenticado();

        return bonoService.obtenerBonoPorId(id)
                .filter(bono -> bono.getEmisorUsername().equals(username))
                .map(bono -> {
                    List<FlujoFinanciero> flujo = bono.getFlujos();
                    if (flujo == null || flujo.isEmpty()) {
                        flujo = calculoFinancieroService.calcularFlujoFinanciero(bono);
                    }
                    return ResponseEntity.ok(flujo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private String obtenerUsernameAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}