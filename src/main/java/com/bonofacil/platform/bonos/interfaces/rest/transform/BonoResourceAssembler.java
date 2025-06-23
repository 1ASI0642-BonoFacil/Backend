package com.bonofacil.platform.bonos.interfaces.rest.transform;

import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import com.bonofacil.platform.bonos.domain.model.valueobjects.Moneda;
import com.bonofacil.platform.bonos.domain.model.valueobjects.PlazoGracia;
import com.bonofacil.platform.bonos.domain.model.valueobjects.TasaInteres;
import com.bonofacil.platform.bonos.interfaces.rest.resources.BonoResource;
import com.bonofacil.platform.bonos.interfaces.rest.resources.MonedaResource;
import com.bonofacil.platform.bonos.interfaces.rest.resources.PlazoGraciaResource;
import com.bonofacil.platform.bonos.interfaces.rest.resources.TasaInteresResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase para convertir entre entidades de dominio y DTOs para bonos.
 */
@Component
public class BonoResourceAssembler {
    
    private static final Logger logger = LoggerFactory.getLogger(BonoResourceAssembler.class);
    
    /**
     * Convierte una entidad Bono a un DTO BonoResource.
     * 
     * @param bono Entidad de dominio
     * @return DTO para la API
     */
    public BonoResource toResource(Bono bono) {
        logger.info("Transformando entidad a DTO: id={}, descripcion={}, tasaDescuento={}, metodoAmortizacion={}", 
                bono.getId(), bono.getDescripcion(), bono.getTasaDescuento(), bono.getMetodoAmortizacion());
                
        BonoResource resource = BonoResource.builder()
                .id(bono.getId().toString())
                .nombre(bono.getNombre())
                .descripcion(bono.getDescripcion())
                .valorNominal(bono.getValorNominal())
                .tasaCupon(bono.getTasaCupon())
                .plazoAnios(bono.getPlazoAnios())
                .frecuenciaPagos(bono.getFrecuenciaPagos())
                .fechaEmision(bono.getFechaEmision())
                .tasaDescuento(bono.getTasaDescuento())
                .metodoAmortizacion(bono.getMetodoAmortizacion())
                .plazoGracia(toPlazoGraciaResource(bono.getPlazoGracia()))
                .moneda(toMonedaResource(bono.getMonedaObj()))
                .tasaInteres(toTasaInteresResource(bono.getTasaInteres()))
                .build();
                
        logger.info("DTO después de transformación: id={}, descripcion={}, tasaDescuento={}, metodoAmortizacion={}", 
                resource.getId(), resource.getDescripcion(), resource.getTasaDescuento(), resource.getMetodoAmortizacion());
                
        return resource;
    }
    
    /**
     * Convierte una lista de entidades Bono a una lista de DTOs BonoResource.
     * 
     * @param bonos Lista de entidades de dominio
     * @return Lista de DTOs para la API
     */
    public List<BonoResource> toResourceList(List<Bono> bonos) {
        return bonos.stream()
                .map(this::toResource)
                .collect(Collectors.toList());
    }
    
    /**
     * Convierte un value object PlazoGracia a un DTO PlazoGraciaResource.
     * 
     * @param plazoGracia Value object de dominio
     * @return DTO para la API
     */
    public PlazoGraciaResource toPlazoGraciaResource(PlazoGracia plazoGracia) {
        if (plazoGracia == null) {
            return new PlazoGraciaResource(PlazoGraciaResource.TipoPlazoGracia.NINGUNO, 0);
        }
        return PlazoGraciaResource.builder()
                .tipo(PlazoGraciaResource.TipoPlazoGracia.valueOf(plazoGracia.getTipo().name()))
                .periodos(plazoGracia.getPeriodos())
                .build();
    }
    
    /**
     * Convierte un DTO PlazoGraciaResource a un value object PlazoGracia.
     * 
     * @param resource DTO de la API
     * @return Value object de dominio
     */
    public PlazoGracia toDomainPlazoGracia(PlazoGraciaResource resource) {
        if (resource == null) {
            return new PlazoGracia(PlazoGracia.TipoPlazoGracia.NINGUNO, 0);
        }
        PlazoGracia.TipoPlazoGracia tipo = PlazoGracia.TipoPlazoGracia.valueOf(resource.getTipo().name());
        return new PlazoGracia(tipo, resource.getPeriodos());
    }
    
    /**
     * Convierte un value object Moneda a un DTO MonedaResource.
     * 
     * @param moneda Value object de dominio
     * @return DTO para la API
     */
    public MonedaResource toMonedaResource(Moneda moneda) {
        if (moneda == null) {
            return new MonedaResource("PEN", "Soles", "S/");
        }
        return new MonedaResource(moneda.getCodigo(), moneda.getNombre(), moneda.getSimbolo());
    }
    
    /**
     * Convierte un código de moneda String a un DTO MonedaResource.
     * 
     * @param codigoMoneda Código de la moneda
     * @return DTO para la API
     */
    public MonedaResource toMonedaResourceFromString(String codigoMoneda) {
        if (codigoMoneda == null || codigoMoneda.isEmpty()) {
            return new MonedaResource("PEN", "Soles", "S/");
        }
        // Aquí deberíamos obtener la moneda de un servicio o repositorio
        switch (codigoMoneda) {
            case "USD":
                return new MonedaResource("USD", "Dólares", "$");
            case "EUR":
                return new MonedaResource("EUR", "Euros", "€");
            default:
                return new MonedaResource("PEN", "Soles", "S/");
        }
    }
    
    /**
     * Convierte un DTO MonedaResource a un value object Moneda.
     * 
     * @param resource DTO de la API
     * @return Value object de dominio
     */
    public Moneda toDomainMoneda(MonedaResource resource) {
        if (resource == null) {
            return new Moneda("PEN", "Soles", "S/");
        }
        return new Moneda(resource.getCodigo(), resource.getNombre(), resource.getSimbolo());
    }
    
    /**
     * Convierte un value object TasaInteres a un DTO TasaInteresResource.
     * 
     * @param tasaInteres Value object de dominio
     * @return DTO para la API
     */
    public TasaInteresResource toTasaInteresResource(TasaInteres tasaInteres) {
        if (tasaInteres == null) {
            return new TasaInteresResource(BigDecimal.valueOf(0.05), TasaInteresResource.TipoTasa.EFECTIVA, 0);
        }
        return TasaInteresResource.builder()
                .valor(tasaInteres.getValor())
                .tipo(TasaInteresResource.TipoTasa.valueOf(tasaInteres.getTipo().name()))
                .frecuenciaCapitalizacion(tasaInteres.getFrecuenciaCapitalizacion())
                .build();
    }
    
    /**
     * Convierte un DTO TasaInteresResource a un value object TasaInteres.
     * 
     * @param resource DTO de la API
     * @return Value object de dominio
     */
    public TasaInteres toDomainTasaInteres(TasaInteresResource resource) {
        if (resource == null) {
            return new TasaInteres(BigDecimal.valueOf(0.05), TasaInteres.TipoTasa.EFECTIVA, 1);
        }
        TasaInteres.TipoTasa tipo = TasaInteres.TipoTasa.valueOf(resource.getTipo().name());
        return new TasaInteres(resource.getValor(), tipo, resource.getFrecuenciaCapitalizacion());
    }
} 