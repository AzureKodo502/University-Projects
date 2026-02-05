package com.example.progetto_ecommerce_v3.Model;

public record Utente (String Id, String Nome, String Cognome, String Email) {

    public Utente(String Nome, String Cognome, String Email) {
        this("-1", Nome, Cognome, Email);
    }

    @Override
    public String toString() {
        return Id + ";" + Nome + ";" + Cognome + ";" + Email;
    }
}
