package com.bonofacil.platform.bonos.infrastructure.persistence.jpa.repositories;

import com.bonofacil.platform.bonos.domain.model.entities.Calculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalculoRepository extends JpaRepository<Calculo, Long> {
    List<Calculo> findByInversorUsername(String inversorUsername);
    List<Calculo> findByBono_Id(Long bonoId);
}