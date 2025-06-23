package com.bonofacil.platform.bonos.interfaces.rest.controllers;

import com.bonofacil.platform.bonos.application.services.CalculadoraBonoApplicationService;
import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import com.bonofacil.platform.bonos.domain.model.entities.Calculo;
import com.bonofacil.platform.bonos.domain.model.valueobjects.DuracionConvexidad;
import com.bonofacil.platform.bonos.domain.model.valueobjects.PrecioMercado;
import com.bonofacil.platform.bonos.domain.model.valueobjects.Rendimiento;
import com.bonofacil.platform.bonos.interfaces.rest.resources.*;
import com.bonofacil.platform.bonos.interfaces.rest.transform.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para cálculos financieros de bonos.
 */
@RestController
@RequestMapping("/api/bonos/{bonoId}/calculos")
public class CalculadoraBonoController {
    
    private final CalculadoraBonoApplicationService calculadoraBonoService;
    private final FlujoCajaResourceAssembler flujoCajaAssembler;
    private final DuracionConvexidadResourceAssembler duracionConvexidadAssembler;
    private final RendimientoResourceAssembler rendimientoAssembler;
    private final PrecioMercadoResourceAssembler precioMercadoAssembler;
    
    public CalculadoraBonoController(
            CalculadoraBonoApplicationService calculadoraBonoService,
            FlujoCajaResourceAssembler flujoCajaAssembler,
            DuracionConvexidadResourceAssembler duracionConvexidadAssembler,
            RendimientoResourceAssembler rendimientoAssembler,
            PrecioMercadoResourceAssembler precioMercadoAssembler) {
        this.calculadoraBonoService = calculadoraBonoService;
        this.flujoCajaAssembler = flujoCajaAssembler;
        this.duracionConvexidadAssembler = duracionConvexidadAssembler;
        this.rendimientoAssembler = rendimientoAssembler;
        this.precioMercadoAssembler = precioMercadoAssembler;
    }
    
    /**
     * Genera el flujo de caja de un bono.
     * 
     * @param bonoId ID del bono
     * @param tasaDescuento Tasa de descuento para calcular valores presentes
     * @return Lista de flujos de caja proyectados
     */
    @GetMapping("/flujo-caja")
    public ResponseEntity<List<FlujoCajaResource>> generarFlujoCaja(
            @PathVariable String bonoId,
            @RequestParam(defaultValue = "0.08") BigDecimal tasaDescuento) {
        
        return ResponseEntity.ok(
            flujoCajaAssembler.toResourceList(
                calculadoraBonoService.generarFlujoCaja(bonoId, tasaDescuento)
            )
        );
    }
    
    /**
     * Calcula las métricas de duración, duración modificada y convexidad.
     * 
     * @param bonoId ID del bono
     * @param tasaMercado Tasa de mercado para el descuento
     * @param cambioPuntosPorcentuales Cambio en puntos porcentuales para estimar el impacto en precio
     * @return Objeto con métricas calculadas
     */
    @GetMapping("/metricas")
    public ResponseEntity<DuracionConvexidadResource> calcularMetricas(
            @PathVariable String bonoId,
            @RequestParam(defaultValue = "0.08") BigDecimal tasaMercado,
            @RequestParam(defaultValue = "0.01") BigDecimal cambioPuntosPorcentuales) {
        
        // Obtener las métricas del servicio
        DuracionConvexidad metricas = calculadoraBonoService.calcularMetricas(bonoId, tasaMercado);
        
        // Convertir el valor de dominio a un recurso antes de devolverlo
        return ResponseEntity.ok(
            duracionConvexidadAssembler.toResource(metricas, cambioPuntosPorcentuales)
        );
    }
    
    /**
     * Calcula el precio del bono.
     * 
     * @param bonoId ID del bono
     * @param tasaMercado Tasa de mercado para el descuento
     * @return Precio calculado
     */
    @GetMapping("/precio")
    public ResponseEntity<BigDecimal> calcularPrecio(
            @PathVariable String bonoId,
            @RequestParam(defaultValue = "0.08") BigDecimal tasaMercado) {
        
        return ResponseEntity.ok(
            calculadoraBonoService.calcularPrecio(bonoId, tasaMercado)
        );
    }
    
    /**
     * Calcula la TCEA (Tasa de Coste Efectivo Anual) desde la perspectiva del emisor.
     * 
     * @param bonoId ID del bono
     * @param costosEmision Costos de emisión del bono
     * @return Objeto con la TCEA calculada
     */
    @GetMapping("/tcea")
    public ResponseEntity<RendimientoResource> calcularTCEA(
            @PathVariable String bonoId,
            @RequestParam BigDecimal costosEmision) {
        
        // VALIDACIÓN TEMPRANA: Verificar que los costos de emisión sean válidos
        if (costosEmision == null) {
            throw new IllegalArgumentException("Los costos de emisión no pueden ser nulos");
        }
        
        if (costosEmision.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                "Los costos de emisión no pueden ser negativos. Valor recibido: " + costosEmision
            );
        }
        
        if (costosEmision.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException(
                "Los costos de emisión deben ser mayor a 0 para calcular la TCEA. " +
                "Un valor de 0 causaría división por cero en el cálculo financiero. " +
                "Valor recibido: " + costosEmision
            );
        }
        
        // Obtener el rendimiento del servicio
        Rendimiento rendimiento = calculadoraBonoService.calcularTCEA(bonoId, costosEmision);
        
        // Convertir el valor de dominio a un recurso antes de devolverlo
        return ResponseEntity.ok(
            rendimientoAssembler.toResource(rendimiento)
        );
    }
    
    /**
     * Calcula la TREA (Tasa de Rendimiento Efectivo Anual) desde la perspectiva del inversor.
     * 
     * @param bonoId ID del bono
     * @param precioCompra Precio de compra del bono
     * @return Objeto con la TREA calculada
     */
    @GetMapping("/trea")
    public ResponseEntity<RendimientoResource> calcularTREA(
            @PathVariable String bonoId,
            @RequestParam BigDecimal precioCompra) {
        
        // VALIDACIÓN TEMPRANA: Verificar que el precio de compra sea válido
        if (precioCompra == null) {
            throw new IllegalArgumentException("El precio de compra no puede ser nulo");
        }
        
        if (precioCompra.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "El precio de compra debe ser mayor a 0 para calcular la TREA. " +
                "Un valor de 0 o negativo causaría división por cero en el cálculo financiero. " +
                "Valor recibido: " + precioCompra
            );
        }
        
        // Obtener el rendimiento del servicio
        Rendimiento rendimiento = calculadoraBonoService.calcularTREA(bonoId, precioCompra);
        
        // Convertir el valor de dominio a un recurso antes de devolverlo
        return ResponseEntity.ok(
            rendimientoAssembler.toResource(rendimiento)
        );
    }
    
    /**
     * Calcula el precio máximo que el mercado estaría dispuesto a pagar por el bono.
     * 
     * @param bonoId ID del bono
     * @param tasaMercado Tasa de rendimiento requerida por el mercado
     * @return Objeto con el precio máximo calculado
     */
    @GetMapping("/precio-mercado")
    public ResponseEntity<PrecioMercadoResource> calcularPrecioMercado(
            @PathVariable String bonoId,
            @RequestParam(defaultValue = "0.08") BigDecimal tasaMercado) {
        
        // Obtener el precio de mercado del servicio
        PrecioMercado precioMercado = calculadoraBonoService.calcularPrecioMercado(bonoId, tasaMercado);
        
        // Convertir el valor de dominio a un recurso antes de devolverlo
        return ResponseEntity.ok(
            precioMercadoAssembler.toResource(precioMercado)
        );
    }

    /**
     * Maneja las excepciones de argumentos inválidos y devuelve una respuesta HTTP 400.
     * 
     * @param ex La excepción capturada
     * @return ResponseEntity con detalles del error
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("statusCode", 400);
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("description", "Parámetros de entrada inválidos");
        errorResponse.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
} 