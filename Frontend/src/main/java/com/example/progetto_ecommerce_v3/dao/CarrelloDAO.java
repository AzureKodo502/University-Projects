package com.example.progetto_ecommerce_v3.dao;

import com.example.progetto_ecommerce_v3.Model.Scarpe;
import java.util.List;

public interface CarrelloDAO {
    // Recupera ID
    List<String> findIdsByUtente(String email);

    // Aggiunge al carrello
    boolean aggiungi(String email, String idScarpa, int quantita, int taglia);

    // Aggiorna taglia
    void aggiornaTaglia(String idScarpa, int nuovaTaglia);

    // Recupera Scarpa + Taglia Scelta
    List<Scarpe> getCarrelloCompleto(String email);

    // Rimuove un elemento specifico
    void rimuovi(String email, String idScarpa, String taglia);
}