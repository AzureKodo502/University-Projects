package com.example.progetto_ecommerce_v3.Controller;

import com.example.progetto_ecommerce_v3.Controller.Handler.ScarpaHandler;
import com.example.progetto_ecommerce_v3.HelloApplication;
import com.example.progetto_ecommerce_v3.Message;
import com.example.progetto_ecommerce_v3.Model.ProductViewRadioModel;
import com.example.progetto_ecommerce_v3.Model.Scarpe;
import com.example.progetto_ecommerce_v3.View.SceneHandler;
import com.example.progetto_ecommerce_v3.service.CarrelloService;
import com.example.progetto_ecommerce_v3.Database.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;

public class ProductViewController {

    // SERVICE PER IL BACKEND
    private final CarrelloService carrelloService = new CarrelloService();

    @FXML BorderPane prodViewBorder;
    @FXML private TextArea DesrizioneProdotto;
    @FXML private ImageView ImmagineProdotto;
    @FXML private ImageView ImmagineScarpeSimili1, ImmagineScarpeSimili2, ImmagineScarpeSimili3;
    @FXML private Text ModelloScarpa1, ModelloScarpa2, ModelloScarpa3;
    @FXML private ImageView PreferitiIcon;
    @FXML private Text Prezzo1, Prezzo2, Prezzo3;
    @FXML private Label PrezzoScarpa;
    @FXML private Label ScarpaLabel;
    @FXML private VBox ScarpeSimiliVbox1, ScarpeSimiliVbox2, ScarpeSimiliVbox3;
    @FXML private RadioButton radio36, radio37, radio38, radio39, radio40, radio41, radio42, radio43, radio44;
    @FXML private ToggleGroup radioGroup = new ToggleGroup();

    private ProductViewRadioModel p;

    @FXML public RadioButton getRadio36() { return radio36; }
    @FXML public RadioButton getRadio37() { return radio37; }
    @FXML public RadioButton getRadio38() { return radio38; }
    @FXML public RadioButton getRadio39() { return radio39; }
    @FXML public RadioButton getRadio40() { return radio40; }
    @FXML public RadioButton getRadio41() { return radio41; }
    @FXML public RadioButton getRadio42() { return radio42; }
    @FXML public RadioButton getRadio43() { return radio43; }
    @FXML public RadioButton getRadio44() { return radio44; }

    @FXML private void radio36Action() { if (p != null) p.radio36(); }
    @FXML private void radio37Action() { if (p != null) p.radio37(); }
    @FXML private void radio38Action() { if (p != null) p.radio38(); }
    @FXML private void radio39Action() { if (p != null) p.radio39(); }
    @FXML private void radio40Action() { if (p != null) p.radio40(); }
    @FXML private void radio41Action() { if (p != null) p.radio41(); }
    @FXML private void radio42Action() { if (p != null) p.radio42(); }
    @FXML private void radio43Action() { if (p != null) p.radio43(); }
    @FXML private void radio44Action() { if (p != null) p.radio44(); }

    @FXML
    public String getSelectedRadioValue() {
        RadioButton selectedRadioButton = (RadioButton) radioGroup.getSelectedToggle();
        if (selectedRadioButton != null) {
            return selectedRadioButton.getText();
        }
        return null;
    }

    ImageView[] ImmagineScarpeSimilin;
    Text[] Prezzon;
    Text[] ModelloScarpen;
    VBox[] ScarpeSimiliVbox;

    void InizializeArrays() {
        ImmagineScarpeSimilin = new ImageView[]{ImmagineScarpeSimili1, ImmagineScarpeSimili2, ImmagineScarpeSimili3};
        Prezzon = new Text[]{Prezzo1, Prezzo2, Prezzo3};
        ModelloScarpen = new Text[]{ModelloScarpa1, ModelloScarpa2, ModelloScarpa3};
        ScarpeSimiliVbox = new VBox[]{ScarpeSimiliVbox1, ScarpeSimiliVbox2, ScarpeSimiliVbox3};
    }

    // METODO AGGIORNA CARRELLO
    @FXML
    void AggiungiAlCarrello(MouseEvent event) {
        if (!SessionManager.getInstance().isLogged()) {
            SceneHandler.getInstance().Errore(Message.Non_Sei_Loggato);
            return;
        }

        String userId = SessionManager.getInstance().getUtente().Id();
        String selectedValue = getSelectedRadioValue();

        if (selectedValue == null) {
            SceneHandler.getInstance().Errore(Message.Errore_Taglia);
            return;
        }

        int taglia;
        try {
            taglia = Integer.parseInt(selectedValue);
        } catch (NumberFormatException e) {
            return;
        }

        String idScarpa = ScarpaHandler.getInstance().getScarpe().Id();

        // Chiamata al Backend (Via Service)
        carrelloService.aggiungiAlCarrello(userId, idScarpa, taglia, 1)
                .thenAccept(risultato -> {
                    Platform.runLater(() -> {
                        if ("OK".equals(risultato)) {
                            SceneHandler.getInstance().Successo(Message.Scarpa_Aggiunta_Al_Carrello);
                            caricahome();
                        } else {
                            SceneHandler.getInstance().Errore(Message.Errore_inserisci_Nel_Carrello);
                        }
                    });
                });
    }

    void caricahome(){
        try{
            SceneHandler.getInstance().loadHomeWindow();
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Home);
        }
    }

    // WiSHLIST
    @FXML
    void addToWishlist(MouseEvent event) {
        if (!SessionManager.getInstance().isLogged()) {
            SceneHandler.getInstance().Errore(Message.Non_Sei_Loggato);
            return;
        }

        // Logica visiva (Toggle Immagine)
        if (PreferitiIcon.getImage().getUrl().contains("preferitirosso")) {
            PreferitiIcon.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/homePageIcons/Preferiti.png")).toExternalForm()));
        } else {
            PreferitiIcon.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/homePageIcons/preferitirosso.png")).toExternalForm()));
        }
    }

    // CARICAMENTO DETTAGLIO SCARPA
    @FXML
    void CaricaScarpa() {
        Scarpe scarpe = ScarpaHandler.getInstance().getScarpe();
        if (scarpe == null) return;

        // Recupera nome file (con fallback se nullo)
        String nomeFile = (scarpe.ImageUrl() != null && !scarpe.ImageUrl().isEmpty())
                ? scarpe.ImageUrl()
                : scarpe.Id() + ".png";

        String url = "immaginiScarpe/" + nomeFile;

        // Caricamento sicuro dell'immagine (evita il crash se manca il file)
        try (InputStream stream = HelloApplication.class.getResourceAsStream("/" + url)) {
            if (stream != null) {
                ImmagineProdotto.setImage(new Image(stream));
            } else {
                System.err.println("Immagine non trovata: " + url);
                // Mettiamo un immagine di default oppure vuota senza interrompere il programma
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ScarpaLabel.setText(scarpe.Nome());
        DesrizioneProdotto.setText(scarpe.Descrizione());
        PrezzoScarpa.setText(scarpe.Prezzo() + "€");

        // Reset icona preferiti (default vuota)
        PreferitiIcon.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/homePageIcons/Preferiti.png")).toExternalForm()));
    }


    // CARICAMENTO SCARPE SIMILI
    @FXML
    void CaricaScarpeSimili() {
        // Scarichiamo prodotti random dal backend per riempire i box
        carrelloService.recuperaProdottiDalBackend()
                .thenAccept(lista -> {
                    Platform.runLater(() -> {
                        if (lista == null || lista.isEmpty()) return;
                        Collections.shuffle(lista);

                        int count = Math.min(lista.size(), 3);
                        for (int i = 0; i < count; i++) {
                            Scarpe s = lista.get(i);

                            // Set Dati
                            ModelloScarpen[i].setText(s.Nome());
                            Prezzon[i].setText(s.Prezzo() + "€");

                            // Set Immagine
                            String nomeFile = (s.ImageUrl() != null && !s.ImageUrl().isEmpty()) ? s.ImageUrl() : s.Id() + ".png";
                            try (InputStream stream = HelloApplication.class.getResourceAsStream("/immaginiScarpe/" + nomeFile)) {
                                if (stream != null) ImmagineScarpeSimilin[i].setImage(new Image(stream));
                            } catch (Exception e) {}

                            // Click per navigare
                            ScarpeSimiliVbox[i].setOnMouseClicked(event -> {
                                ScarpaHandler.getInstance().setScarpa(s);
                                Platform.runLater(this::CaricaScarpa); // Ricarica la pagina con la nuova scarpa
                            });
                        }
                    });
                });
    }

    private void aggiungiScorciatoieTastiera() {
        prodViewBorder.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    boolean logged = SessionManager.getInstance().isLogged();
                    if (new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN).match(event)) SceneHandler.getInstance().loadCarrelloWindow();
                    if (new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN).match(event)) SceneHandler.getInstance().loadHomeWindow();
                });
            }
        });
    }

    @FXML
    public void initialize() {
        p = new ProductViewRadioModel(this);

        radio36.setToggleGroup(radioGroup);
        radio37.setToggleGroup(radioGroup);
        radio38.setToggleGroup(radioGroup);
        radio39.setToggleGroup(radioGroup);
        radio40.setToggleGroup(radioGroup);
        radio41.setToggleGroup(radioGroup);
        radio42.setToggleGroup(radioGroup);
        radio43.setToggleGroup(radioGroup);
        radio44.setToggleGroup(radioGroup);

        InizializeArrays();
        CaricaScarpa();
        CaricaScarpeSimili();

        aggiungiScorciatoieTastiera();

        Platform.runLater(() -> prodViewBorder.requestFocus());
    }
}