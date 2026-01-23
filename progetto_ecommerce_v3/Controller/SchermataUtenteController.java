package com.example.progetto_ecommerce_v3.Controller;

import com.example.progetto_ecommerce_v3.Database.ConnessioneDatabase;
import com.example.progetto_ecommerce_v3.Database.Inizializzazione;
import com.example.progetto_ecommerce_v3.Message;
import com.example.progetto_ecommerce_v3.Model.CollegamentiModel;
import com.example.progetto_ecommerce_v3.Model.Scarpe;
import com.example.progetto_ecommerce_v3.Model.Utente;
import com.example.progetto_ecommerce_v3.View.SceneHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SchermataUtenteController {

    @FXML
    private BorderPane borderpane;

    @FXML
    private Button CambiaPasswordButton;

    @FXML
    private Text EmailText;

    @FXML
    private Text CognomeText;

    @FXML
    private Text NomeText;

    @FXML
    private Text TitoloProdotto1, TitoloProdotto2, TitoloProdotto3, TitoloProdotto4, TitoloProdotto5, TitoloProdotto6;

    @FXML
    private HBox hbox1, hbox2, hbox3, hbox4, hbox5, hbox6;

    @FXML
    private Text prezzo1, prezzo2, prezzo3, prezzo4, prezzo5, prezzo6;

    private Text[] titoloprodottos;
    private Text[] prezzoprodottos;
    private HBox[] hBoxes;

    private void InizializeArrey() {
        titoloprodottos = new Text[]{TitoloProdotto1, TitoloProdotto2, TitoloProdotto3, TitoloProdotto4, TitoloProdotto5, TitoloProdotto6};
        prezzoprodottos = new Text[]{prezzo1, prezzo2, prezzo3, prezzo4, prezzo5, prezzo6};
        hBoxes = new HBox[]{hbox1, hbox2, hbox3, hbox4, hbox5, hbox6};
    }

    @FXML
    private void CaricaDatiUtente(Utente utente) throws ExecutionException, InterruptedException, TimeoutException {
        NomeText.setText(utente.Nome());
        CognomeText.setText(utente.Cognome());
        EmailText.setText(utente.Email());
        CaricaOrdini(utente.Email());
    }

    @FXML
    private void CaricaOrdini(String email) throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<ArrayList<ArrayList<Scarpe>>> future = ConnessioneDatabase.getInstance().getOrdine(email);
        ArrayList<ArrayList<Scarpe>> ordini = future.get(7, TimeUnit.SECONDS);
        CompletableFuture<ArrayList<String>> future1 = ConnessioneDatabase.getInstance().getPrezzoOrdine(email);
        ArrayList<String> ordineprezzo = future1.get(7, TimeUnit.SECONDS);

        if (ordini.isEmpty() || ordineprezzo.isEmpty()) {
            return;
        }

        for (HBox hBox : hBoxes) {
            hBox.setVisible(false);
        }

        for (int i = 0; i < ordini.size() && i < hBoxes.length; i++) {
            StringBuilder concatenamento = new StringBuilder();

            for (Scarpe scarpa : ordini.get(i)) {
                concatenamento.append(scarpa.Nome()).append("\n");
            }

            String price = ordineprezzo.get(i);
            price = price.replace(".", ",");

            titoloprodottos[i].setText(concatenamento.toString());
            prezzoprodottos[i].setText(price + "€");
            hBoxes[i].setVisible(true);
        }
    }

    @FXML
    void initialize() {
        borderpane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                CambiaPasswordButton.fire(); // Simula un click sul pulsante
            }
        });
        InizializeArrey();

        try {
            Utente utente = Inizializzazione.getInstance().getUtente();
            if (utente == null) {
                // Mostra l'errore generico
                SceneHandler.getInstance().Errore(Message.Errore_Utente_null);
                // Carica la home page dopo aver mostrato l'errore
                SceneHandler.getInstance().loadHomeWindow();
                return; // Interrompe l'esecuzione se l'utente è null
            } else {
                CaricaDatiUtente(utente);
            }
        } catch (Exception e) {
            // Mostra l'errore di inizializzazione
            SceneHandler.getInstance().Errore(Message.Errore_Generico);
            // Carica la home page dopo aver mostrato l'errore
            caricahome();
        }

        // Aggiungi le scorciatoie da tastiera
        aggiungiScorciatoieTastiera();

        // Assicurati che il borderpane abbia il focus
        Platform.runLater(() -> {
            borderpane.requestFocus();
        });
    }

    void caricahome() {
        try {
            SceneHandler.getInstance().loadHomeWindow();
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Home);
        }
    }

    @FXML
    void instaClicked(MouseEvent event) {
        try {
            CollegamentiModel.collegamentiSocial("https://www.instagram.com/accounts/login/");
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_Instagram);
            caricahome();
        }
    }

    @FXML
    void twitterClicked(MouseEvent event) {
        try {
            CollegamentiModel.collegamentiSocial("https://x.com/i/flow/login?input_flow_data=%7B%22requested_variant%22%3A%22eyJsYW5nIjoiaXQiLCJteCI6IjIifQ%3D%3D%22%7D");
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_Twitter);
            caricahome();
        }
    }

    @FXML
    void facebookClicked(MouseEvent event) {
        try {
            CollegamentiModel.collegamentiSocial("https://m.facebook.com/login/?locale=it_IT");
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_Facebook);
            caricahome();
        }
    }

    @FXML
    void chisiamoClicked(MouseEvent event) {
        try {
            SceneHandler.getInstance().loadchisiamonoi();
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Chi_Siamo_Noi);
            caricahome();
        }
    }

    @FXML
    void infoClicked(MouseEvent event) {
        try {
            SceneHandler.getInstance().loadinfoeprivacy();
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Info_E_Privacy);
            caricahome();
        }
    }

    @FXML
    void loadHome(MouseEvent event) {
        caricahome();
    }

    @FXML
    void cambiaPassword(MouseEvent event) {
        try {
            SceneHandler.getInstance().loadCambiaPassword();
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Load_Pagina_Cambio_Password);
        }
    }

    private void aggiungiScorciatoieTastiera() {
        borderpane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN).match(event)) {
                        SceneHandler.getInstance().loadCambiaPassword();
                    }
                    if (new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN).match(event)) {
                        SceneHandler.getInstance().loadHomeWindow();
                    }
                    if (new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN).match(event)) {
                        SceneHandler.getInstance().loadPreferitiWindow();
                    }
                    if (new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN).match(event)) {
                        SceneHandler.getInstance().loadCarrelloWindow();
                    }
                });
            }
        });
    }
}

