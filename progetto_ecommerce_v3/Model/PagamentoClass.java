package com.example.progetto_ecommerce_v3.Model;

import com.example.progetto_ecommerce_v3.Controller.PagamentoController;
import javafx.scene.control.Alert;
import java.util.regex.Pattern;

public class PagamentoClass {

    public void alertPagamentoErratoCarta() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText("Inserire i dati correttamente");
        alert.showAndWait();
    }

    public boolean controllaCampi(PagamentoController p){
            return controllanumcarta(p.getNumcarta().getText()) && controllaMMAA(p.getMMAA().getText()) && controllaCVC(p.getCVC().getText());
    }

    public boolean controllanumcarta(String numcarta){
        return Pattern.matches("^[\\d\\s]{19}$",numcarta);
    }

    public boolean controllaMMAA(String MMAA){
        if(Pattern.matches("[\\d/]{5}",MMAA)) {
            StringBuilder str = new StringBuilder();
            str.append(MMAA);
            int c0 = str.charAt(0) - '0';
            int c1 = str.charAt(1) - '0';
            int c3 = str.charAt(3) - '0';
            int c4 = str.charAt(4) - '0';
            if(c0 != 0 && c0 != 1)
                return false;

            if(c0 == 0) {
                if(c1 == 0)
                    return false;
            }

            else {
                if(c1 != 0 && c1 != 1 && c1 != 2 )
                    return false;
            }

            if(c3 == 2) {
                if(c4 < 5)
                    return false;
            }
            else if(c3 < 2)
                return false;

            return true;
        }
        return false;
    }

    public boolean controllaCVC(String CVC){
        return Pattern.matches("^\\d{3}",CVC);
    }

    public boolean controllaCampiIndirizzo(String numCivico, String citta, String cap){
        if(numCivico.equals("") || citta.equals("") || cap.equals("")){
            return false;
        }
        return true;
    }
}