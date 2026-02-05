package com.example.progetto_ecommerce_v3.service;

import com.example.progetto_ecommerce_v3.Model.Utente;
import java.util.concurrent.CompletableFuture;

public class UtenteService {

    // Ritorna l'oggetto Utente se login OK, altrimenti null
    public CompletableFuture<Utente> login(String email, String password) {
        // Creiamo il JSON per la richiesta
        String jsonBody = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);

        return ApiClient.getInstance().post("/auth/login", jsonBody)
                .thenApply(responseJson -> {
                    // Se la risposta è vuota o errore, parseUtente ritorna null
                    return JsonParser.parseUtente(responseJson);
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    // Registrazione
    public CompletableFuture<Utente> registrazione(String nome, String cognome, String email, String password) {
        String jsonBody = String.format(
                "{\"nome\":\"%s\", \"cognome\":\"%s\", \"email\":\"%s\", \"password\":\"%s\"}",
                nome, cognome, email, password
        );

        return ApiClient.getInstance().post("/auth/register", jsonBody)
                .thenApply(JsonParser::parseUtente)
                .exceptionally(e -> null);
    }
}