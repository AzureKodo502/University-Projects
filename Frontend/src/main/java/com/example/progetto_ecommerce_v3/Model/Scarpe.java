package com.example.progetto_ecommerce_v3.Model;

public class Scarpe {
    private String Id;
    private String Nome;
    private String Prezzo;
    private String Descrizione;
    private String Marchio;
    private String Taglia;
    private String Modello;
    private String ImageUrl;

    // Costruttore usato dal JsonParser
    public Scarpe(String Id, String Nome, String Prezzo, String Descrizione, String Marchio, String Taglia, String Modello, String ImageUrl) {
        this.Id = Id;
        this.Nome = Nome;
        this.Prezzo = Prezzo;
        this.Descrizione = Descrizione;
        this.Marchio = Marchio;
        this.Taglia = Taglia;
        this.Modello = Modello;
        this.ImageUrl = ImageUrl;
    }

    // Costruttore Vecchio
    // Chiama quello nuovo passando null all'immagine.
    public Scarpe(String Id, String Nome, String Prezzo, String Descrizione, String Marchio, String Taglia, String Modello) {
        this(Id, Nome, Prezzo, Descrizione, Marchio, Taglia, Modello, null);
    }

    // Questi metodi permettono al vecchio codice di funzionare senza modifiche
    public String Id() { return Id; }
    public String Nome() { return Nome; }
    public String Prezzo() { return Prezzo; }
    public String Descrizione() { return Descrizione; }
    public String Marchio() { return Marchio; }
    public String Taglia() { return Taglia; }
    public String Modello() { return Modello; }
    public String ImageUrl() { return ImageUrl; }

    @Override
    public String toString() {
        return Id + ";" + Nome + ";" + Prezzo + ";" + Descrizione + ";" + Marchio + ";" + Taglia + ";" + Modello + ";";
    }
}
