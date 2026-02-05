package com.example.progetto_ecommerce_v3.service;

import com.example.progetto_ecommerce_v3.Model.Utente;
import com.example.progetto_ecommerce_v3.Database.SessionManager;
import java.util.concurrent.CompletableFuture;

public class AuthService {

    public CompletableFuture<Boolean> login(String email, String password) {
        // Creazione del JSON
        String jsonBody = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);

        return ApiClient.getInstance().post("/auth/login", jsonBody)
                .thenApply(responseJson -> {
                    // Parse dell'utente ricevuto dal server
                    Utente utente = JsonParser.parseUtente(responseJson);

                    if (utente != null) {
                        // Salviamo l'utente nella Sessione
                        SessionManager.getInstance().Login(utente);
                        return true;
                    }
                    return false;
                })
                .exceptionally(e -> {
                    System.out.println("Login fallito: " + e.getMessage());
                    return false;
                });
    }

    public CompletableFuture<Boolean> registrazione(String nome, String cognome, String email, String password) {
        String jsonBody = String.format(
                "{\"nome\":\"%s\", \"cognome\":\"%s\", \"email\":\"%s\", \"password\":\"%s\"}",
                nome, cognome, email, password
        );

        return ApiClient.getInstance().post("/auth/register", jsonBody)
                .thenApply(responseJson -> {
                    // La registrazione restituisce l'utente creato, quindi facciamo login automatico
                    Utente utente = JsonParser.parseUtente(responseJson);
                    if (utente != null) {
                        SessionManager.getInstance().Login(utente);
                        return true;
                    }
                    return false;
                })
                .exceptionally(e -> {
                    System.out.println("Registrazione fallita: " + e.getMessage());
                    return false;
                });
    }
}
