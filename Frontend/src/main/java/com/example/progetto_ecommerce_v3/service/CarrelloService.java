package com.example.progetto_ecommerce_v3.service;

import com.example.progetto_ecommerce_v3.Model.Scarpe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CarrelloService {

    public CarrelloService() {}

    // METODO DI AGGIUNTA AL CARRELLO
    public CompletableFuture<String> aggiungiAlCarrello(String userId, String scarpaId, int taglia, int quantita) {

        // Costruiamo il JSON. userId e scarpaId sono numeri, senza virgolette
        String jsonBody = String.format("{\"userId\":%s, \"scarpaId\":%s, \"quantita\":%d, \"taglia\":%d}",
                userId, scarpaId, quantita, taglia);

        System.out.println("DEBUG JSON INVIATO: " + jsonBody);

        return ApiClient.getInstance().post("/cart/add", jsonBody)
                .thenApply(response -> "OK")
                .exceptionally(e -> {
                    System.err.println("Errore Aggiunta Carrello: " + e.getMessage());
                    return "ERRORE";
                });
    }

    // METODO RECUPERA CARRELLO
    public CompletableFuture<List<Scarpe>> recuperaCarrello(String userId) {
        return ApiClient.getInstance().get("/cart/" + userId)
                .thenApply(json -> {
                    System.out.println("JSON Carrello: " + json);
                    return JsonParser.parseCarrello(json);
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return new ArrayList<>();
                });
    }

    // METODO RIMOZIONE DAL CARRELLO
    public CompletableFuture<Boolean> rimuoviDalCarrello(String userId, String scarpaId, String taglia) {
        // Pulizia taglia
        String tagliaPulita = taglia.replaceAll("[^0-9]", "");

        String jsonBody = String.format("{\"userId\":%s, \"scarpaId\":%s, \"taglia\":%s, \"quantita\":1}",
                userId, scarpaId, tagliaPulita);

        System.out.println("DEBUG REMOVE JSON: " + jsonBody);

        return ApiClient.getInstance().post("/cart/remove", jsonBody)
                .thenApply(response -> true)
                .exceptionally(e -> {
                    System.err.println("Errore Rimozione: " + e.getMessage());
                    return false;
                });
    }

    public CompletableFuture<List<Scarpe>> recuperaProdottiDalBackend() {
        return ApiClient.getInstance().get("/products")
                .thenApply(JsonParser::parseScarpeList)
                .exceptionally(e -> new ArrayList<>());
    }

    public double calcolaTotale(List<Scarpe> scarpe) {
        double totale = 0;
        for (Scarpe s : scarpe) {
            try {
                if (s.Prezzo() == null) continue;
                String prezzoPulito = s.Prezzo().replaceAll("[^0-9.,]", "").replace(",", ".");
                totale += Double.parseDouble(prezzoPulito);
            } catch (Exception e) {}
        }
        return totale;
    }
}