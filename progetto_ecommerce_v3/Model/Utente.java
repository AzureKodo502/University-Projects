package com.example.progetto_ecommerce_v3.Model;

public record Utente (String Nome, String Cognome, String Email){
    @Override
    public String toString(){return Nome + ";" + Cognome + ";" + Email;}
}
