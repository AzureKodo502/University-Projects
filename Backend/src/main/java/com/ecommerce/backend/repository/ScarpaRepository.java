package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Scarpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScarpaRepository extends JpaRepository<Scarpa, Long> {

    // Cerca scarpe che contengono la stringa (ignorando maiuscole/minuscole)
    // SQL automatico: SELECT * FROM scarpe WHERE lower(nome) LIKE lower('%query%')
    List<Scarpa> findByNomeContainingIgnoreCase(String query);

    // Cerca scarpe per marchio esatto
    // SQL automatico: SELECT * FROM scarpe WHERE marchio = 'marchio'
    List<Scarpa> findByMarchio(String marchio);
}
