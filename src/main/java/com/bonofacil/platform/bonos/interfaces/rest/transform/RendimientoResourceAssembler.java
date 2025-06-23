package com.bonofacil.platform.bonos.interfaces.rest.transform;

import com.bonofacil.platform.bonos.domain.model.entities.Calculo;
import com.bonofacil.platform.bonos.domain.model.valueobjects.Rendimiento;
import com.bonofacil.platform.bonos.interfaces.rest.resources.RendimientoResource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Ensamblador para transformar información de rendimiento a recursos REST.
 */
@Component
public class RendimientoResourceAssembler {

    /**
     * Convierte los datos de un cálculo de rendimiento a su representación como recurso REST.
     *
     * @param calculo Entidad con datos de rendimiento
     * @return Recurso REST con información de rendimiento
     */
    public RendimientoResource toResource(Calculo calculo) {
        if (calculo == null) {
            return null;
        }

        return RendimientoResource.builder()
                .tasaRendimiento(calculo.getTrea())
                .precio(calculo.getPrecioMaximo())
                .build();
    }
    
    /**
     * Convierte un objeto Rendimiento a su representación como recurso REST.
     *
     * @param rendimiento Objeto de valor con datos de rendimiento
     * @return Recurso REST con información de rendimiento
     */
    public RendimientoResource toResource(Rendimiento rendimiento) {
        if (rendimiento == null) {
            return null;
        }

        return RendimientoResource.builder()
                .tasaRendimiento(rendimiento.getTasaRendimiento())
                .precio(rendimiento.getPrecio())
                .build();
    }
} 