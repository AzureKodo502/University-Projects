package com.example.progetto_ecommerce_v3.Controller;

import com.example.progetto_ecommerce_v3.Database.ConnessioneDatabase;
import com.example.progetto_ecommerce_v3.Database.SessionManager;
import com.example.progetto_ecommerce_v3.Message;
import com.example.progetto_ecommerce_v3.Model.CollegamentiModel;
import com.example.progetto_ecommerce_v3.Model.PagamentoClass;
import com.example.progetto_ecommerce_v3.Model.Utente;
import com.example.progetto_ecommerce_v3.View.SceneHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class PagamentoController extends PagamentoClass {

    @FXML
    BorderPane borderpane;

    @FXML
    private TextField citta;

    @FXML
    private TextField cap;

    @FXML
    private TextField numCivico;

    @FXML
    private TextField CVC;

    @FXML
    private TextField MMAA;

    @FXML
    private Button confpagamento;

    @FXML
    private Button confpagamento2;

    @FXML
    private CheckBox mycheckbox;

    @FXML
    private CheckBox mycheckbox2;

    @FXML
    private TextField numcarta;

    @FXML
    private Text CognomeText;

    @FXML
    private Text EmailText;

    @FXML
    private Text NomeText;

    @FXML
    public TextField getNumcarta(){
        return numcarta;
    }

    @FXML
    public TextField getMMAA(){
        return MMAA;
    }

    @FXML
    public TextField getCVC(){
        return CVC;
    }


    @FXML
    private void handleBox1Action(){
        if (mycheckbox.isSelected()){
            mycheckbox2.setSelected(false);
            confpagamento.setVisible(true);
            confpagamento2.setVisible(false);
            numcarta.setVisible(false);
            MMAA.setVisible(false);
            CVC.setVisible(false);
        }else{
            confpagamento.setVisible(false);
        }
    }

    @FXML
    private void handleBox2Action(){
        if (mycheckbox2.isSelected()){
            confpagamento.setVisible(false);
            mycheckbox.setSelected(false);
            confpagamento2.setVisible(true);
            numcarta.setVisible(true);
            MMAA.setVisible(true);
            CVC.setVisible(true);
        }else{
            confpagamento2.setVisible(false);
            numcarta.setVisible(false);
            MMAA.setVisible(false);
            CVC.setVisible(false);
        }

    }

    @FXML
    private String CaricaDatiUtente(Utente utente) {
        NomeText.setText(utente.Nome());
        CognomeText.setText(utente.Cognome());
        EmailText.setText(utente.Email());
        return utente.Email();
    }

    @FXML
    void Pagamento(MouseEvent event) throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        Utente utente = SessionManager.getInstance().getUtente();
        String Email = CaricaDatiUtente(utente);
        String Totale = ConnessioneDatabase.getInstance().getPrezzoTotaleCarrello(Email, "Carrello");

        Totale = Totale.replace(",",".");
        double PrezzoTotale = Double.parseDouble(Totale);

        ConnessioneDatabase.getInstance().checkOut(Email, String.valueOf(PrezzoTotale));

        // Aggiorna da tipo 'Carrello' a tipo 'Ordine'
        ConnessioneDatabase.getInstance().aggiornaTipoCarrello(Email);

        SceneHandler.getInstance().Successo(Message.Ordine_Completato);
    }

    @FXML
    public void initialize() {
        // Aggiunto un listener per monitorare i cambiamenti nel testo
        MMAA.textProperty().addListener((observable, oldValue, newValue) -> {
            // Rimozione di tutte le barre di separazione esistenti
            String cleanText = newValue.replaceAll("/", "");

            // Costruzione del Testo Formattato
            StringBuilder formattedText = new StringBuilder();
            for (int i = 0; i<cleanText.length() && i < 4; i++) {
                if (i > 0 && i % 2 == 0) {
                    formattedText.append("/");
                }
                formattedText.append(cleanText.charAt(i));
            }

            // Aggiorna il testo del TextField senza spostare il cursore
            MMAA.setText(formattedText.toString());
            MMAA.positionCaret(formattedText.length());
        });


        // Aggiunto un listener per monitorare i cambiamenti nel testo
        numcarta.textProperty().addListener((observable, oldValue, newValue) -> {
            // Rimozione di tutti gli spazi esistenti
            String cleanText = newValue.replaceAll(" ", "");

            // Limita il numero massimo di cifre a 16
            if (cleanText.length() > 16) {
                cleanText = cleanText.substring(0, 16);
            }

            // Costruzione del testo formattato con gli spazi
            StringBuilder formattedText = new StringBuilder();
            for (int i = 0; i < cleanText.length(); i++) {
                if (i > 0 && i % 4 == 0) {
                    formattedText.append(" ");
                }
                formattedText.append(cleanText.charAt(i));
            }

            // Aggiorna il testo del TextField senza spostare il cursore
            numcarta.setText(formattedText.toString());
            numcarta.positionCaret(formattedText.length());
        });

        CVC.textProperty().addListener((observable, oldValue, newValue) -> {
            // Verifica se il nuovo testo sia composto solo da cifre e abbia al massimo 3 caratteri
            if (!newValue.matches("\\d*") || newValue.length() > 3) {
                // Ripristina il vecchio valore se il nuovo valore non è valido
                CVC.setText(oldValue);
            }
        });

        try {
            Utente utente = SessionManager.getInstance().getUtente(); // Otteniamo l'utente
            if (utente == null) {
                // Mostra l'errore generico
                SceneHandler.getInstance().Errore(Message.Errore_Generico);
                // Carica la home page dopo aver mostrato l'errore
                caricahome();
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

        aggiungiScorciatoieTastiera();

        Platform.runLater(() -> borderpane.requestFocus());
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

    @FXML
    void handleButtonEvent(MouseEvent event) throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        if (controllaCampiIndirizzo(numCivico.getText(),citta.getText(),cap.getText())){
            Pagamento(event);
            loadHome(event);
        }else{
            alertPagamentoErratoCarta();
        }
    }

    @FXML
    private void handleButton2(MouseEvent event) throws SQLException, ExecutionException, InterruptedException, TimeoutException{
        if (controllaCampi(this) && controllaCampiIndirizzo(numCivico.getText(),citta.getText(),cap.getText())){
            Pagamento(event);
            loadHome(event);
        }else{
            alertPagamentoErratoCarta();
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
    void chisiamoClicked(MouseEvent event) {
        try { SceneHandler.getInstance().loadchisiamonoi();
        } catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Chi_Siamo_Noi);
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