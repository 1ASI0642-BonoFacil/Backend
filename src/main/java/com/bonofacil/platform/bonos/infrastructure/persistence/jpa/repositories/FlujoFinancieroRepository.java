package com.bonofacil.platform.bonos.infrastructure.persistence.jpa.repositories;

import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import com.bonofacil.platform.bonos.domain.model.entities.FlujoFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlujoFinancieroRepository extends JpaRepository<FlujoFinanciero, Long> {
    List<FlujoFinanciero> findByBono(Bono bono);
    List<FlujoFinanciero> findByBonoOrderByPeriodo(Bono bono);
    void deleteByBono(Bono bono);
}