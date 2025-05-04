package com.bonofacil.platform.bonos.application.services;

import com.bonofacil.platform.bonos.domain.model.entities.Bono;
import com.bonofacil.platform.bonos.domain.model.entities.FlujoFinanciero;
import com.bonofacil.platform.bonos.infrastructure.persistence.jpa.repositories.BonoRepository;
import com.bonofacil.platform.bonos.infrastructure.persistence.jpa.repositories.FlujoFinancieroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Bono> obtenerBonosPorRangoTasa(double tasaMinima, Double tasaMaxima) {
        return bonoRepository.findByTasaCuponBetween(tasaMinima, tasaMaxima);
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
    }

    public List<FlujoFinanciero> obtenerFlujoFinancieroBono(Long id) {
        return bonoRepository.findById(id)
                .map(flujoFinancieroRepository::findByBonoOrderByPeriodo)
                .orElse(null);
    }
}

