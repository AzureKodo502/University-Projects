package com.example.progetto_ecommerce_v3.Database;

import com.example.progetto_ecommerce_v3.Model.Utente;

public class SessionManager {

    private static SessionManager instance;
    private Utente utenteLoggato;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void Login(Utente utente) {
        this.utenteLoggato = utente;
        System.out.println("Utente loggato: " + utente.Email()); // Log utile per debug
    }

    public void logout() {
        this.utenteLoggato = null;
    }

    public boolean isLogged() {
        return utenteLoggato != null;
    }

    public Utente getUtente() {
        return utenteLoggato;
    }
}
