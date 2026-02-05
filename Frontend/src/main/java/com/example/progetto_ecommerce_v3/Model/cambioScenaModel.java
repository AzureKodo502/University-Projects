package com.example.progetto_ecommerce_v3.Model;

public class cambioScenaModel  {

    private static cambioScenaModel instance = new cambioScenaModel();
    private boolean toggleState = false;

    private cambioScenaModel() {}

    public static cambioScenaModel getInstance() {
        return instance;
    }

    public boolean isToggleState() {
        return toggleState;
    }

    public void setToggleState(boolean toggleState) {
        this.toggleState = toggleState;
    }

}
