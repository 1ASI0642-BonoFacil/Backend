package com.bonofacil.platform.bonos.infrastructure.persistence.jpa.repositories;

import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio JPA para la entidad Bono.
 */
@Repository
public interface BonoRepository extends JpaRepository<Bono, Long> {
    
    /**
     * Busca bonos cuyo nombre contiene la cadena especificada (insensible a mayúsculas/minúsculas).
     * 
     * @param nombre Cadena a buscar en el nombre del bono
     * @return Lista de bonos que coinciden con el criterio
     */
    List<Bono> findByNombreContainingIgnoreCase(String nombre);

    List<Bono> findByEmisorUsername(String emisorUsername);
    List<Bono> findByMoneda(String moneda);
    List<Bono> findByTasaCuponBetween(BigDecimal min, BigDecimal max);
    List<Bono> findByTasaCuponGreaterThanEqual(BigDecimal min);
}