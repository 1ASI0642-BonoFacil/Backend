package com.bonofacil.platform.bonos.interfaces.rest.transform;

import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import com.bonofacil.platform.bonos.domain.model.valueobjects.Moneda;
import com.bonofacil.platform.bonos.domain.model.valueobjects.PlazoGracia;
import com.bonofacil.platform.bonos.domain.model.valueobjects.TasaInteres;
import com.bonofacil.platform.bonos.interfaces.rest.resources.BonoResource;
import com.bonofacil.platform.bonos.interfaces.rest.resources.CreateBonoResource;
import com.bonofacil.platform.bonos.interfaces.rest.resources.MonedaResource;
import com.bonofacil.platform.bonos.interfaces.rest.resources.PlazoGraciaResource;
import com.bonofacil.platform.bonos.interfaces.rest.resources.TasaInteresResource;

import java.math.BigDecimal;

public class BonoResourceFromEntityAssembler {

    public static BonoResource toResourceFromEntity(Bono entity) {
        BonoResource resource = new BonoResource();
        resource.setId(entity.getId().toString());
        resource.setNombre(entity.getNombre());
        
        // Configurar campos básicos
        resource.setValorNominal(entity.getValorNominal());
        resource.setTasaCupon(entity.getTasaCupon());
        resource.setPlazoAnios(entity.getPlazoAnios());
        resource.setFrecuenciaPagos(entity.getFrecuenciaPagos());
        resource.setFechaEmision(entity.getFechaEmision());
        
        // Añadir campos faltantes
        resource.setDescripcion(entity.getDescripcion());
        resource.setTasaDescuento(entity.getTasaDescuento());
        resource.setMetodoAmortizacion(entity.getMetodoAmortizacion());
        
        // Configurar Moneda
        MonedaResource monedaResource = new MonedaResource();
        monedaResource.setCodigo(entity.getMoneda());
        monedaResource.setNombre(entity.getMoneda());
        monedaResource.setSimbolo(entity.getMoneda().substring(0, 1));
        resource.setMoneda(monedaResource);
        
        // Configurar PlazoGracia
        PlazoGraciaResource plazoGraciaResource = new PlazoGraciaResource();
        PlazoGraciaResource.TipoPlazoGracia tipo;
        int periodos = 0;
        
        if (entity.getPlazosGraciaTotal() > 0) {
            tipo = PlazoGraciaResource.TipoPlazoGracia.TOTAL;
            periodos = entity.getPlazosGraciaTotal();
        } else if (entity.getPlazosGraciaParcial() > 0) {
            tipo = PlazoGraciaResource.TipoPlazoGracia.PARCIAL;
            periodos = entity.getPlazosGraciaParcial();
        } else {
            tipo = PlazoGraciaResource.TipoPlazoGracia.NINGUNO;
            periodos = 0;
        }
        
        plazoGraciaResource.setTipo(tipo);
        plazoGraciaResource.setPeriodos(periodos);
        resource.setPlazoGracia(plazoGraciaResource);
        
        // Configurar TasaInteres
        TasaInteresResource tasaInteresResource = new TasaInteresResource();
        tasaInteresResource.setValor(entity.getTasaCupon());
        tasaInteresResource.setTipo(TasaInteresResource.TipoTasa.EFECTIVA);
        tasaInteresResource.setFrecuenciaCapitalizacion(entity.getFrecuenciaPagos());
        resource.setTasaInteres(tasaInteresResource);
        
        return resource;
    }

    public static Bono toEntityFromCreateResource(CreateBonoResource resource) {
        Bono entity = new Bono();
        entity.setNombre(resource.getNombre());
        entity.setDescripcion(resource.getDescripcion());
        entity.setValorNominalFromDouble(resource.getValorNominal());
        entity.setTasaCuponFromDouble(resource.getTasaCupon());
        entity.setPlazoAnios(resource.getPlazoAnios());
        entity.setFrecuenciaPagos(resource.getFrecuenciaPagos());
        
        // Establecer moneda
        entity.setMoneda(resource.getMoneda());
        
        // Crear objeto de valor Moneda
        Moneda moneda = new Moneda(
            resource.getMoneda(),
            resource.getMoneda(),
            resource.getMoneda().substring(0, 1)
        );
        entity.setMonedaObj(moneda);
        
        entity.setFechaEmision(resource.getFechaEmision());
        
        // Establecer plazos de gracia
        entity.setPlazosGraciaTotal(resource.getPlazosGraciaTotal());
        entity.setPlazosGraciaParcial(resource.getPlazosGraciaParcial());
        
        // Crear objeto de valor PlazoGracia
        if (resource.getPlazosGraciaTotal() > 0) {
            entity.setPlazoGracia(PlazoGracia.plazoGraciaTotal(resource.getPlazosGraciaTotal()));
        } else if (resource.getPlazosGraciaParcial() > 0) {
            entity.setPlazoGracia(PlazoGracia.plazoGraciaParcial(resource.getPlazosGraciaParcial()));
        } else {
            entity.setPlazoGracia(PlazoGracia.sinPlazoGracia());
        }
        
        // Establecer tasa de interés
        entity.setTasaCuponFromDouble(resource.getTasaCupon());
        
        // Crear objeto de valor TasaInteres
        TasaInteres tasaInteres = new TasaInteres(
            BigDecimal.valueOf(resource.getTasaCupon()),
            TasaInteres.TipoTasa.EFECTIVA,
            resource.getFrecuenciaPagos()
        );
        entity.setTasaInteres(tasaInteres);
        
        entity.setTasaDescuentoFromDouble(resource.getTasaDescuento());
        entity.setMetodoAmortizacion(resource.getMetodoAmortizacion());
        
        return entity;
    }
}