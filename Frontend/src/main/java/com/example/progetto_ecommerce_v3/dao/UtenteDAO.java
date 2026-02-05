package com.example.progetto_ecommerce_v3.dao;

import com.example.progetto_ecommerce_v3.Model.Utente;

public interface UtenteDAO {
    Utente login(String email);
    String getPasswordHash(String email); // Verifica della password criptata
    boolean esisteEmail(String email);
    boolean registra(String nome, String cognome, String email, String passwordHash);
}
