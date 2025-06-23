package com.bonofacil.platform.bonos.interfaces.rest.transform;

import com.bonofacil.platform.bonos.domain.model.entities.Calculo;
import com.bonofacil.platform.bonos.domain.model.valueobjects.PrecioMercado;
import com.bonofacil.platform.bonos.interfaces.rest.resources.PrecioMercadoResource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Ensamblador para transformar información de precio de mercado a recursos REST.
 */
@Component
public class PrecioMercadoResourceAssembler {

    /**
     * Convierte los datos de un cálculo de precio de mercado a su representación como recurso REST.
     *
     * @param calculo Entidad con datos de precio de mercado
     * @return Recurso REST con información de precio de mercado
     */
    public PrecioMercadoResource toResource(Calculo calculo) {
        if (calculo == null) {
            return null;
        }

        BigDecimal precio = calculo.getPrecioMaximo();
        BigDecimal valorNominal = calculo.getBono().getValorNominal();
        
        // Calcular precio como porcentaje del valor nominal
        BigDecimal precioPorcentaje = null;
        if (precio != null && valorNominal != null && valorNominal.compareTo(BigDecimal.ZERO) > 0) {
            precioPorcentaje = precio.multiply(BigDecimal.valueOf(100))
                    .divide(valorNominal, 2, RoundingMode.HALF_UP);
        }

        return PrecioMercadoResource.builder()
                .precio(precio)
                .tasaMercado(calculo.getTasaEsperada())
                .valorNominal(valorNominal)
                .precioPorcentaje(precioPorcentaje)
                .build();
    }
    
    /**
     * Convierte un objeto PrecioMercado a su representación como recurso REST.
     *
     * @param precioMercado Objeto de valor con datos de precio de mercado
     * @return Recurso REST con información de precio de mercado
     */
    public PrecioMercadoResource toResource(PrecioMercado precioMercado) {
        if (precioMercado == null) {
            return null;
        }

        return PrecioMercadoResource.builder()
                .precio(precioMercado.getPrecio())
                .tasaMercado(precioMercado.getTasaMercado())
                .valorNominal(precioMercado.getValorNominal())
                .precioPorcentaje(precioMercado.getPrecioPorcentaje())
                .build();
    }
} 