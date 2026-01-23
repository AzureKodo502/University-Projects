package com.example.progetto_ecommerce_v3.Controller;

import com.example.progetto_ecommerce_v3.Database.ConnessioneDatabase;
import com.example.progetto_ecommerce_v3.Database.*;
import com.example.progetto_ecommerce_v3.Model.CollegamentiModel;
import com.example.progetto_ecommerce_v3.Model.Utente;
import com.example.progetto_ecommerce_v3.Model.cambioScenaModel;
import com.example.progetto_ecommerce_v3.View.SceneHandler;
import com.example.progetto_ecommerce_v3.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.util.concurrent.*;

public class LoginController {

    @FXML
    private BorderPane borderpane;

    @FXML
    private Text ErroreLogin;

    @FXML
    private PasswordField Password_field;

    @FXML
    private TextField email_field;

    @FXML
    private Button login_button;

    @FXML
    void login(ActionEvent event) {
        try {
            Future<?> future = ConnessioneDatabase.getInstance().VerificaLogin(email_field.getText(), Password_field.getText());
            future.get();
            CompletableFuture<Utente> futuree = ConnessioneDatabase.getInstance().setUtente(email_field.getText());
            Utente utente = futuree.get(5, TimeUnit.SECONDS);
            Inizializzazione.getInstance().Login(utente);
            SceneHandler.getInstance().Successo(Message.Successo_Login);
            caricahome();
        } catch (SQLException | InterruptedException | TimeoutException | ExecutionException e) {
            ErroreLogin.setVisible(true);
        }
        email_field.clear();
        Password_field.clear();
    }

    @FXML
    void initialize() {
        ErroreLogin.setVisible(false);
        borderpane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                login_button.fire(); // Simula un click sul pulsante
            }
        });

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
    void loadRegistrazione(MouseEvent event) {
        try {
            SceneHandler.getInstance().loadRegistrazioneWindow();
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Registrazione);
            caricahome();
        }
    }

    @FXML
    void LoadPasswordDimenticata(MouseEvent event) {
        try {
            SceneHandler.getInstance().loadCambiaPassword();
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Cambio_Password);
            caricahome();
        }
    }

    private void aggiungiScorciatoieTastiera() {
        borderpane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN).match(event)) {
                        SceneHandler.getInstance().loadHomeWindow();
                    }
                    if (new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN).match(event)) {
                        SceneHandler.getInstance().loadRegistrazioneWindow();
                    }
                    if (new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN).match(event)) {
                        SceneHandler.getInstance().loadCarrelloWindow();
                    }
                });
            }
        });
    }
}

