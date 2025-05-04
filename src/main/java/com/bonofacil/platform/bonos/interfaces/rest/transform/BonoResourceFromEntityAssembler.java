package com.bonofacil.platform.bonos.interfaces.rest.transform;

import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import com.bonofacil.platform.bonos.interfaces.rest.resources.BonoResource;
import com.bonofacil.platform.bonos.interfaces.rest.resources.CreateBonoResource;

public class BonoResourceFromEntityAssembler {

    public static BonoResource toResourceFromEntity(Bono entity) {
        BonoResource resource = new BonoResource();
        resource.setId(entity.getId());
        resource.setNombre(entity.getNombre());
        resource.setDescripcion(entity.getDescripcion());
        resource.setValorNominal(entity.getValorNominal());
        resource.setTasaCupon(entity.getTasaCupon());
        resource.setPlazoAnios(entity.getPlazoAnios());
        resource.setFrecuenciaPagos(entity.getFrecuenciaPagos());
        resource.setMoneda(entity.getMoneda());
        resource.setFechaEmision(entity.getFechaEmision());
        resource.setPlazosGraciaTotal(entity.getPlazosGraciaTotal());
        resource.setPlazosGraciaParcial(entity.getPlazosGraciaParcial());
        resource.setTcea(entity.getTcea());
        resource.setDuracion(entity.getDuracion());
        resource.setConvexidad(entity.getConvexidad());
        resource.setEmisorUsername(entity.getEmisorUsername());
        resource.setMetodoAmortizacion(entity.getMetodoAmortizacion());
        return resource;
    }

    public static Bono toEntityFromCreateResource(CreateBonoResource resource) {
        Bono entity = new Bono();
        entity.setNombre(resource.getNombre());
        entity.setDescripcion(resource.getDescripcion());
        entity.setValorNominal(resource.getValorNominal());
        entity.setTasaCupon(resource.getTasaCupon());
        entity.setPlazoAnios(resource.getPlazoAnios());
        entity.setFrecuenciaPagos(resource.getFrecuenciaPagos());
        entity.setMoneda(resource.getMoneda());
        entity.setFechaEmision(resource.getFechaEmision());
        entity.setPlazosGraciaTotal(resource.getPlazosGraciaTotal());
        entity.setPlazosGraciaParcial(resource.getPlazosGraciaParcial());
        entity.setTasaDescuento(resource.getTasaDescuento());
        entity.setMetodoAmortizacion(resource.getMetodoAmortizacion());
        return entity;
    }
}