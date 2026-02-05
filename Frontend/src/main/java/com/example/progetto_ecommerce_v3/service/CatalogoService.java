package com.example.progetto_ecommerce_v3.service;

import com.example.progetto_ecommerce_v3.Database.ConnessioneDatabase;
import com.example.progetto_ecommerce_v3.Model.Scarpe;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CatalogoService {

    public CatalogoService() {
        // Vuoto
    }

    // METODO PER CARICARE PRODOTTI PER MARCA
    public CompletableFuture<Boolean> caricaProdottiPerMarca(String marca) {
        // Chiamata HTTP al backend: GET /api/products/brand/{marca}
        return ApiClient.getInstance().get("/products/brand/" + marca)
                .thenApply(json -> {
                    // Trasformiamo il JSON ricevuto in oggetti Scarpe
                    List<Scarpe> scarpe = JsonParser.parseScarpeList(json);

                    if (!scarpe.isEmpty()) {
                        ConnessioneDatabase.getInstance().setCategorieScarpe(new ArrayList<>(scarpe));
                        return true;
                    }
                    return false;
                })
                .exceptionally(e -> {
                    System.err.println("Errore API Brand: " + e.getMessage());
                    return false;
                });
    }

    // METODO RICERCA
    public CompletableFuture<Boolean> cercaProdotti(String query) {
        // Chiamata HTTP al backend: GET /api/products/search?q={query}
        return ApiClient.getInstance().get("/products/search?q=" + query)
                .thenApply(json -> {
                    List<Scarpe> scarpe = JsonParser.parseScarpeList(json);

                    if (!scarpe.isEmpty()) {
                        ConnessioneDatabase.getInstance().setRicercaScarpe(new ArrayList<>(scarpe));

                        String[] ids = scarpe.stream().map(Scarpe::Id).toArray(String[]::new);
                        ConnessioneDatabase.getInstance().addSearchedProducts(ids);

                        return true;
                    }
                    return false;
                })
                .exceptionally(e -> {
                    System.err.println("Errore API Ricerca: " + e.getMessage());
                    return false;
                });
    }
}