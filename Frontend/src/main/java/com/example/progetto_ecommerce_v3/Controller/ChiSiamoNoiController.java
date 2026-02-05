package com.example.progetto_ecommerce_v3.Controller;

import com.example.progetto_ecommerce_v3.Message;
import com.example.progetto_ecommerce_v3.Model.CollegamentiModel;
import com.example.progetto_ecommerce_v3.View.SceneHandler;
import javafx.fxml.FXML;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class ChiSiamoNoiController {

    @FXML
    private AnchorPane anchorpane;

    @FXML
    private Pane pane;

    @FXML
    private BorderPane borderpane;

    @FXML
    void initialize() {
        anchorpane.requestFocus();
        pane.requestFocus();
        borderpane.requestFocus();

        aggiungiScorciatoieTastiera();
    }

    private void aggiungiScorciatoieTastiera() {
        borderpane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN).match(event)) {
                        try {
                            SceneHandler.getInstance().loadHomeWindow();
                        } catch (Exception e) {
                            SceneHandler.getInstance().Errore(Message.Errore_Home);
                        }
                    }
                    if (new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN).match(event)) {
                        try {
                            SceneHandler.getInstance().loadinfoeprivacy();
                        } catch (Exception e) {
                            SceneHandler.getInstance().Errore(Message.Errore_Home);
                        }
                    }
                });
            }
        });
    }

    @FXML
    public void KeyPressed(KeyEvent a) {
        if (a.getCode() == KeyCode.H) {
            try {
                SceneHandler.getInstance().loadHomeWindow();
            } catch (Exception e) {
                SceneHandler.getInstance().Errore(Message.Errore_Home);
            }
        }
    }

    void caricahome(){
        try{
            SceneHandler.getInstance().loadHomeWindow();
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Home);
        }
    }

    @FXML
    void instaClicked(MouseEvent event) {
        try { CollegamentiModel.collegamentiSocial("https://www.instagram.com/accounts/login/");
        } catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_Instagram);
            caricahome();
        }
    }

    @FXML
    void twitterClicked(MouseEvent event) {
        try { CollegamentiModel.collegamentiSocial("https://x.com/i/flow/login?input_flow_data=%7B%22requested_variant%22%3A%22eyJsYW5nIjoiaXQiLCJteCI6IjIifQ%3D%3D%22%7D");
        } catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_Twitter);
            caricahome();
        }
    }

    @FXML
    void facebookClicked(MouseEvent event) {
        try { CollegamentiModel.collegamentiSocial("https://m.facebook.com/login/?locale=it_IT");
        } catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_Facebook);
            caricahome();
        }
    }

    @FXML
    void infoClicked(MouseEvent event) {
        try { SceneHandler.getInstance().loadinfoeprivacy();
        } catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Info_E_Privacy);
            caricahome();
        }
    }

    @FXML
    void loadHome(MouseEvent event) {
        caricahome();
    }
}