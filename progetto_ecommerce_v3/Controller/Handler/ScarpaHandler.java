package com.example.progetto_ecommerce_v3.Controller.Handler;

import com.example.progetto_ecommerce_v3.Model.Scarpe;

public class ScarpaHandler {
    private static  ScarpaHandler instance = null;
    private Scarpe scarpe = null;

    private ScarpaHandler() {}

    public static ScarpaHandler getInstance() {
        if(instance == null)
            instance = new ScarpaHandler();
        return instance;
    }

    public void setScarpa(Scarpe scarpe) {
        this.scarpe = scarpe;
    }

    public Scarpe getScarpe() {
        return scarpe;
    }

    public void setScarpaNull(){this.scarpe = null;}
}

