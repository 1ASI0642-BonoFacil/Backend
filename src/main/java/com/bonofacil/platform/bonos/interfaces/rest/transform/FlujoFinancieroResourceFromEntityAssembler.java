package com.bonofacil.platform.bonos.interfaces.rest.transform;

import com.bonofacil.platform.bonos.domain.model.entities.FlujoFinanciero;
import com.bonofacil.platform.bonos.interfaces.rest.resources.FlujoFinancieroResource;

public class FlujoFinancieroResourceFromEntityAssembler {

    public static FlujoFinancieroResource toResourceFromEntity(FlujoFinanciero entity) {
        FlujoFinancieroResource resource = new FlujoFinancieroResource();
        resource.setId(entity.getId());
        resource.setPeriodo(entity.getPeriodo());
        resource.setFecha(entity.getFecha());
        
        // Ajustamos campos para coincidir con la entidad
        resource.setCuota(entity.getFlujoTotal());
        resource.setAmortizacion(entity.getAmortizacion());
        resource.setInteres(entity.getCupon());
        resource.setSaldo(entity.getSaldoInsoluto());
        resource.setFlujo(entity.getFlujoTotal());
        resource.setFactorDescuento(entity.getFactorDescuento());
        resource.setValorActual(entity.getValorPresente());
        resource.setFactorTiempo(entity.getFactorTiempo());
        
        return resource;
    }
}