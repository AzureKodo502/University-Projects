package com.ecommerce.backend.service;

import com.ecommerce.backend.model.Scarpa;
import com.ecommerce.backend.repository.ScarpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servizio di business logic per la gestione del catalogo prodotti.
 * Funge da layer sopra lo {@link ScarpaRepository}, gestendo
 * le operazioni di recupero, inserimento e ricerca filtrata delle Scarpe.
 */
@Service
public class ScarpaService {

    /**
     * Repository per l'accesso ai dati delle scarpe, iniettato via Dependency Injection.
     */
    @Autowired
    private ScarpaRepository scarpaRepository;

    /**
     * Estrae l'intero catalogo di prodotti presenti nel sistema.
     * @return Una lista completa di oggetti {@link Scarpa}.
     */
    public List<Scarpa> getAllScarpe() {
        return scarpaRepository.findAll();
    }

    /**
     * Persiste una nuova calzatura o aggiorna una esistente nel database.
     * @param scarpa L'entità da salvare.
     * @return L'oggetto salvato con l'identificativo generato.
     */
    public Scarpa salvaScarpa(Scarpa scarpa) {
        return scarpaRepository.save(scarpa);
    }

    /**
     * Ricerca una specifica scarpa tramite il suo identificativo univoco.
     * @param id L'identificativo del prodotto.
     * @return Un {@link Optional} contenente la scarpa se presente, altrimenti vuoto.
     */
    public Optional<Scarpa> getScarpaById(Long id) {
        return scarpaRepository.findById(id);
    }

    /**
     * Gestisce la logica di ricerca testuale dinamica.
     * @param query Stringa di ricerca inserita dall'utente (es. parte del nome).
     * @return Lista di prodotti che corrispondono ai criteri di ricerca.
     */
    public List<Scarpa> cercaScarpe(String query) {
        return scarpaRepository.findByNomeContainingIgnoreCase(query);
    }

    /**
     * Filtra il catalogo per marchio specifico.
     * @param marchio Il brand che si cerca (es. Nike, Adidas).
     * @return Lista di calzature appartenenti al marchio selezionato.
     */
    public List<Scarpa> getScarpeByMarchio(String marchio) {
        return scarpaRepository.findByMarchio(marchio);
    }
}