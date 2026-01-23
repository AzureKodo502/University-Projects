package com.example.progetto_ecommerce_v3.Model;

public record Scarpe (String Id, String Nome, String Prezzo, String Descrizione, String Marchio, String Taglia, String Modello){
    @Override
    public String toString(){
        return Id + ";" + Nome + ";" + Prezzo + ";" + Descrizione + ";" + Marchio + ";"+ Taglia + ";" + Modello + ";";
    }
}
