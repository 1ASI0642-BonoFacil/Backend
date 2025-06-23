package com.bonofacil.platform.bonos.interfaces.rest.transform;

import com.bonofacil.platform.bonos.domain.model.entities.FlujoFinanciero;
import com.bonofacil.platform.bonos.interfaces.rest.resources.FlujoCajaResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Ensamblador para transformar entidades FlujoFinanciero a FlujoCajaResource y viceversa.
 */
@Component
public class FlujoCajaResourceAssembler {

    /**
     * Convierte una entidad FlujoFinanciero a su representación como recurso REST.
     *
     * @param flujoFinanciero Entidad de flujo financiero
     * @return Recurso REST de flujo de caja
     */
    public FlujoCajaResource toResource(FlujoFinanciero flujoFinanciero) {
        if (flujoFinanciero == null) {
            return null;
        }

        return FlujoCajaResource.builder()
                .periodo(flujoFinanciero.getPeriodo())
                .fecha(flujoFinanciero.getFecha())
                .cupon(flujoFinanciero.getCupon())
                .amortizacion(flujoFinanciero.getAmortizacion())
                .flujoTotal(flujoFinanciero.getFlujoTotal())
                .saldoInsoluto(flujoFinanciero.getSaldoInsoluto())
                .valorPresente(flujoFinanciero.getValorPresente())
                .factorDescuento(flujoFinanciero.getFactorDescuento())
                .build();
    }

    /**
     * Convierte una lista de entidades FlujoFinanciero a una lista de recursos REST.
     *
     * @param flujos Lista de entidades de flujo financiero
     * @return Lista de recursos REST de flujo de caja
     */
    public List<FlujoCajaResource> toResourceList(List<FlujoFinanciero> flujos) {
        if (flujos == null) {
            return List.of();
        }

        return flujos.stream()
                .map(this::toResource)
                .collect(Collectors.toList());
    }
} 