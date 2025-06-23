package com.bonofacil.platform.bonos.interfaces.rest.transform;

import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import com.bonofacil.platform.bonos.domain.model.valueobjects.DuracionConvexidad;
import com.bonofacil.platform.bonos.interfaces.rest.resources.DuracionConvexidadResource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Ensamblador para transformar información de duración y convexidad a recursos REST.
 */
@Component
public class DuracionConvexidadResourceAssembler {

    /**
     * Convierte los datos de duración y convexidad a su representación como recurso REST.
     *
     * @param bono Entidad bono con datos de duración y convexidad
     * @param cambioPuntosPorcentuales Cambio en puntos porcentuales para estimar impacto en precio
     * @return Recurso REST con información de duración y convexidad
     */
    public DuracionConvexidadResource toResource(Bono bono, BigDecimal cambioPuntosPorcentuales) {
        if (bono == null) {
            return null;
        }

        // Calcular la estimación de cambio en precio usando duración y convexidad
        BigDecimal duracion = bono.getDuracion();
        BigDecimal convexidad = bono.getConvexidad();
        BigDecimal tasaDescuento = bono.getTasaDescuento();
        
        // Cálculo aproximado del cambio en precio usando la fórmula de aproximación de segundo orden
        BigDecimal cambioPorcentualPrecio = null;
        if (duracion != null && convexidad != null && tasaDescuento != null) {
            // ΔP/P ≈ -D × Δy + 0.5 × C × (Δy)²
            BigDecimal primerTermino = duracion.negate().multiply(cambioPuntosPorcentuales);
            BigDecimal segundoTermino = convexidad.multiply(cambioPuntosPorcentuales.pow(2)).multiply(BigDecimal.valueOf(0.5));
            cambioPorcentualPrecio = primerTermino.add(segundoTermino)
                    .setScale(4, RoundingMode.HALF_UP);
        }

        return DuracionConvexidadResource.builder()
                .duracion(duracion)
                .convexidad(convexidad)
                .tasaMercado(tasaDescuento)
                .cambioPuntosPorcentuales(cambioPuntosPorcentuales)
                .cambioPorcentualPrecio(cambioPorcentualPrecio)
                .build();
    }
    
    /**
     * Convierte un objeto DuracionConvexidad a su representación como recurso REST.
     *
     * @param metricas Objeto con métricas de duración y convexidad
     * @param cambioPuntosPorcentuales Cambio en puntos porcentuales para estimar impacto en precio
     * @return Recurso REST con información de duración y convexidad
     */
    public DuracionConvexidadResource toResource(DuracionConvexidad metricas, BigDecimal cambioPuntosPorcentuales) {
        if (metricas == null) {
            return null;
        }

        // Calcular el cambio porcentual estimado en el precio
        BigDecimal cambioPorcentualPrecio = metricas.estimarCambioPorcentualPrecio(cambioPuntosPorcentuales);
        
        return DuracionConvexidadResource.builder()
                .duracion(metricas.getDuracion())
                .convexidad(metricas.getConvexidad())
                .tasaMercado(metricas.getTasaMercado())
                .cambioPuntosPorcentuales(cambioPuntosPorcentuales)
                .cambioPorcentualPrecio(cambioPorcentualPrecio)
                .build();
    }
} 