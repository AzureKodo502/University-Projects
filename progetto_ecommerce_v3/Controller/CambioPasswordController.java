package com.example.progetto_ecommerce_v3.Controller;

import com.example.progetto_ecommerce_v3.Database.ConnessioneDatabase;
import com.example.progetto_ecommerce_v3.Database.Convalida;
import com.example.progetto_ecommerce_v3.Database.Inizializzazione;
import com.example.progetto_ecommerce_v3.Model.Utente;
import com.example.progetto_ecommerce_v3.View.SceneHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.example.progetto_ecommerce_v3.Model.CollegamentiModel;
import com.example.progetto_ecommerce_v3.Message;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class CambioPasswordController {

    @FXML
    private Text EmailText;

    @FXML
    private TextField EmailField;

    @FXML
    private PasswordField ConfermaField;

    @FXML
    private BorderPane borderpane;

    @FXML
    private PasswordField PasswordField;

    @FXML
    private Button confpassbutton;

    @FXML
    private String CaricaDatiUtente(Utente utente) {
        EmailField.setText(utente.Email());
        return utente.Email();
    }

    @FXML
    void CambiaPasswodAction(ActionEvent event) {
        if (Inizializzazione.getInstance().getUtente() == null) {
            String Password = PasswordField.getText();
            String ConfermaPassword = ConfermaField.getText();
            String Email = EmailField.getText();

            ConnessioneDatabase.getInstance().VerificaSeEsisteEmail(Email).thenAccept(emailExists -> {
                if (emailExists) {
                    boolean valid = Convalida.getInstance().VerificaPassword(Password, ConfermaPassword);
                    if (valid) {
                        String encryptedPassword = ConnessioneDatabase.getInstance().PasswordCriptografata(Password);
                        ConnessioneDatabase.getInstance().AggiornamentoPassword(Email, encryptedPassword);
                        PasswordField.clear();
                        ConfermaField.clear();
                        SceneHandler.getInstance().Info(Message.Aggiornamento_Della_Password);
                    } else {
                        SceneHandler.getInstance().Errore(Message.Errore_Durante_La_registrazione_PasswordNE);
                    }
                } else {
                    SceneHandler.getInstance().Errore(Message.Errore_Durante_La_registrazione_Mail);
                }
            });
        } else {
            Utente utente = Inizializzazione.getInstance().getUtente();
            String Password = PasswordField.getText();
            String ConfermaPassword = ConfermaField.getText();
            String Email = CaricaDatiUtente(utente);

            boolean valid = Convalida.getInstance().VerificaPassword(Password, ConfermaPassword);
            if (valid) {
                String encryptedPassword = ConnessioneDatabase.getInstance().PasswordCriptografata(Password);
                ConnessioneDatabase.getInstance().AggiornamentoPassword(Email, encryptedPassword);
                PasswordField.clear();
                ConfermaField.clear();
                SceneHandler.getInstance().Successo(Message.Aggiornamento_Della_Password);
                caricahome();
            } else {
                SceneHandler.getInstance().Errore(Message.Errore_Durante_La_registrazione_PasswordNE);
            }
        }
    }

    @FXML
    void initialize() {
        borderpane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confpassbutton.fire();
            }
        });

        if (Inizializzazione.getInstance().getUtente() == null) {
            EmailField.setVisible(true);
            EmailText.setVisible(true);
        } else {
            EmailField.setVisible(false);
            EmailText.setVisible(false);
        }

        aggiungiScorciatoieTastiera();

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
    void loadChiSiamo(MouseEvent event) {
        try {
            SceneHandler.getInstance().loadchisiamonoi();
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Chi_Siamo_Noi);
            caricahome();
        }
    }

    @FXML
    void FacebookClicked(MouseEvent event) {
        try {
            CollegamentiModel.collegamentiSocial("https://m.facebook.com/login/?locale=it_IT");
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_Facebook);
            caricahome();
        }
    }

    @FXML
    void LoadInfoPrivacy(MouseEvent event) {
        try {
            SceneHandler.getInstance().loadinfoeprivacy();
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Info_E_Privacy);
            caricahome();
        }
    }

    @FXML
    void InstagramClicked(MouseEvent event) {
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
    void loadHome(MouseEvent event) {
        caricahome();
    }

    private void aggiungiScorciatoieTastiera() {
        borderpane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN).match(event)) {
                        SceneHandler.getInstance().loadHomeWindow();
                    }
                    if (new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN).match(event)) {
                        SceneHandler.getInstance().loadSchermataUtente();
                    }
                    if (new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN).match(event)) {
                        SceneHandler.getInstance().loadCarrelloWindow();
                    }
                    if (new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN).match(event)) {
                        SceneHandler.getInstance().loadPreferitiWindow();
                    }
                });
            }
        });
    }
}

