package com.example.progetto_ecommerce_v3.Database;
import com.example.progetto_ecommerce_v3.Model.Utente;
public class Inizializzazione {
    private static Inizializzazione instance = null;

    private Utente utente = null;

    private Inizializzazione(){}

    public static Inizializzazione getInstance(){
        if (instance == null){
            instance = new Inizializzazione();
        }
        return instance;
    }

    public void Login(Utente utente){
        this.utente = utente;
    }

    public boolean SetUtente(){
        return !(this.utente == null);
    }

    public void esci(){
        this.utente = null;
    }

    public Utente getUtente() {
        if (utente != null){
            return utente;
        }
        else{
            return null;
        }
    }
}
