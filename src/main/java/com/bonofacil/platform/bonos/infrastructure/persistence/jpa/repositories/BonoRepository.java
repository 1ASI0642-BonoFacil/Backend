package com.bonofacil.platform.bonos.infrastructure.persistence.jpa.repositories;

import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BonoRepository extends JpaRepository<Bono, Long> {
    List<Bono> findByEmisorUsername(String emisorUsername);
    List<Bono> findByMoneda(String moneda);
    List<Bono> findByTasaCuponBetween(double min, double max);
    List<Bono> findByTasaCuponGreaterThanEqual(double min);
}