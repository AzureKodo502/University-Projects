package com.ecommerce.backend.service;

import com.ecommerce.backend.model.Scarpa;
import com.ecommerce.backend.repository.ScarpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Logica di Business
public class ScarpaService {

    @Autowired // Iniezione automatica del Repository
    private ScarpaRepository scarpaRepository;

    // Recupera tutte le scarpe
    public List<Scarpa> getAllScarpe() {
        return scarpaRepository.findAll();
    }

    // Aggiunge una nuova scarpa
    public Scarpa salvaScarpa(Scarpa scarpa) {
        return scarpaRepository.save(scarpa);
    }

    // Cerca per ID
    public Optional<Scarpa> getScarpaById(Long id) {
        return scarpaRepository.findById(id);
    }

    public List<Scarpa> cercaScarpe(String query) {
        return scarpaRepository.findByNomeContainingIgnoreCase(query);
    }

    public List<Scarpa> getScarpeByMarchio(String marchio) {
        return scarpaRepository.findByMarchio(marchio);
    }
}