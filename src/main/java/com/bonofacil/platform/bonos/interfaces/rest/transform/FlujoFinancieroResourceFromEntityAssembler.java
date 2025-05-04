package com.bonofacil.platform.bonos.interfaces.rest.transform;

import com.bonofacil.platform.bonos.domain.model.entities.FlujoFinanciero;
import com.bonofacil.platform.bonos.interfaces.rest.resources.FlujoFinancieroResource;

public class FlujoFinancieroResourceFromEntityAssembler {

    public static FlujoFinancieroResource toResourceFromEntity(FlujoFinanciero entity) {
        FlujoFinancieroResource resource = new FlujoFinancieroResource();
        resource.setId(entity.getId());
        resource.setPeriodo(entity.getPeriodo());
        resource.setFecha(entity.getFecha());
        resource.setCuota(entity.getCuota());
        resource.setAmortizacion(entity.getAmortizacion());
        resource.setInteres(entity.getInteres());
        resource.setSaldo(entity.getSaldo());
        resource.setFlujo(entity.getFlujo());
        resource.setFactorDescuento(entity.getFactorDescuento());
        resource.setValorActual(entity.getValorActual());
        resource.setFactorTiempo(entity.getFactorTiempo());
        return resource;
    }
}