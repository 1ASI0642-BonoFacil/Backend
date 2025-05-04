package com.bonofacil.platform.bonos.interfaces.rest.transform;

import com.bonofacil.platform.bonos.domain.model.entities.Calculo;
import com.bonofacil.platform.bonos.interfaces.rest.resources.CalculoResource;

public class CalculoResourceFromEntityAssembler {

    public static CalculoResource toResourceFromEntity(Calculo entity) {
        CalculoResource resource = new CalculoResource();
        resource.setId(entity.getId());
        resource.setBonoId(entity.getBonoId());
        resource.setBonoNombre(entity.getBonoNombre());
        resource.setInversorUsername(entity.getInversorUsername());
        resource.setTasaEsperada(entity.getTasaEsperada());
        resource.setTrea(entity.getTrea());
        resource.setPrecioMaximo(entity.getPrecioMaximo());
        resource.setFechaCalculo(entity.getFechaCalculo());
        resource.setInformacionAdicional(entity.getInformacionAdicional());
        return resource;
    }
}