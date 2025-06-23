package com.bonofacil.platform.bonos.application.services;

import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import com.bonofacil.platform.bonos.domain.model.entities.FlujoFinanciero;
import com.bonofacil.platform.bonos.infrastructure.persistence.jpa.repositories.BonoRepository;
import com.bonofacil.platform.bonos.infrastructure.persistence.jpa.repositories.FlujoFinancieroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BonoService {

    private final BonoRepository bonoRepository;
    private final FlujoFinancieroRepository flujoFinancieroRepository;

    @Autowired
    public BonoService(BonoRepository bonoRepository, FlujoFinancieroRepository flujoFinancieroRepository) {
        this.bonoRepository = bonoRepository;
        this.flujoFinancieroRepository = flujoFinancieroRepository;
    }

    public Bono crearBono(Bono bono) {
        validarBono(bono);
        return bonoRepository.save(bono);
    }

    public Optional<Bono> obtenerBonoPorId(Long id) {
        return bonoRepository.findById(id);
    }

    public List<Bono> obtenerTodosLosBonos() {
        return bonoRepository.findAll();
    }

    public List<Bono> obtenerBonosPorEmisor(String emisorUsername) {
        return bonoRepository.findByEmisorUsername(emisorUsername);
    }

    public List<Bono> obtenerBonosPorMoneda(String moneda) {
        return bonoRepository.findByMoneda(moneda);
    }

    public List<Bono> obtenerBonosPorRangoTasa(double tasaMinima, double tasaMaxima) {
        BigDecimal minTasa = BigDecimal.valueOf(tasaMinima);
        
        if (tasaMaxima == Double.MAX_VALUE) {
            // Si no se especificó tasa máxima, buscar bonos con tasa mayor o igual a la mínima
            return bonoRepository.findByTasaCuponGreaterThanEqual(minTasa);
        } else {
            BigDecimal maxTasa = BigDecimal.valueOf(tasaMaxima);
            return bonoRepository.findByTasaCuponBetween(minTasa, maxTasa);
        }
    }

    public Bono actualizarBono(Long id, Bono bono) {
        validarBono(bono);
        return bonoRepository.findById(id)
                .map(existingBono -> {
                    bono.setId(id);
                    return bonoRepository.save(bono);
                })
                .orElseThrow(() -> new IllegalArgumentException("Bono no encontrado"));
    }

    public void eliminarBono(Long id) {
        bonoRepository.deleteById(id);
    }

    public void validarBono(Bono bono) {
        // Implementación básica
        if (bono.getValorNominal() == null || bono.getValorNominal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El valor nominal debe ser positivo");
        }
        
        if (bono.getTasaCupon() == null) {
            throw new IllegalArgumentException("La tasa cupón no puede ser nula");
        }
        
        if (bono.getPlazoAnios() <= 0) {
            throw new IllegalArgumentException("El plazo en años debe ser positivo");
        }
        
        if (bono.getFrecuenciaPagos() <= 0) {
            throw new IllegalArgumentException("La frecuencia de pagos debe ser positiva");
        }
        
        if (bono.getFechaEmision() == null) {
            throw new IllegalArgumentException("La fecha de emisión no puede ser nula");
        }
    }

    public List<FlujoFinanciero> obtenerFlujoFinancieroBono(Long id) {
        return bonoRepository.findById(id)
                .map(flujoFinancieroRepository::findByBonoOrderByPeriodo)
                .orElse(null);
    }
}

