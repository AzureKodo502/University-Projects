package com.example.progetto_ecommerce_v3.Controller;

import com.example.progetto_ecommerce_v3.Controller.Handler.ScarpaHandler;
import com.example.progetto_ecommerce_v3.Database.ConnessioneDatabase;
import com.example.progetto_ecommerce_v3.Database.Inizializzazione;
import com.example.progetto_ecommerce_v3.HelloApplication;
import com.example.progetto_ecommerce_v3.Message;
import com.example.progetto_ecommerce_v3.Model.ProductViewRadioModel;
import com.example.progetto_ecommerce_v3.Model.Scarpe;
import com.example.progetto_ecommerce_v3.Model.Utente;
import com.example.progetto_ecommerce_v3.View.SceneHandler;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ProductViewController {

    @FXML
    BorderPane prodViewBorder;

    @FXML
    private TextArea DesrizioneProdotto;

    @FXML
    private ImageView ImmagineProdotto;

    @FXML
    private ImageView ImmagineScarpeSimili1, ImmagineScarpeSimili2, ImmagineScarpeSimili3;

    @FXML
    private Text ModelloScarpa1, ModelloScarpa2, ModelloScarpa3;

    @FXML
    private ImageView PreferitiIcon;

    @FXML
    private Text Prezzo1, Prezzo2, Prezzo3;

    @FXML
    private Label PrezzoScarpa;

    @FXML
    private Label ScarpaLabel;

    @FXML
    private VBox ScarpeSimiliVbox1, ScarpeSimiliVbox2, ScarpeSimiliVbox3;

    @FXML
    private RadioButton radio36, radio37, radio38, radio39, radio40, radio41, radio42, radio43, radio44;

    @FXML
    private ToggleGroup radioGroup = new ToggleGroup();

    private ProductViewRadioModel p;

    private String principaleMarchio;  // Nuovo campo per il marchio principale

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

    @FXML
    void AggiungiAlCarrello(MouseEvent event) {
        boolean find = false;
        String emailUtente;
        if (Inizializzazione.getInstance().SetUtente()) {
            emailUtente = Inizializzazione.getInstance().getUtente().Email();
        } else {
            emailUtente = "null";
        }

        String selectedValue = getSelectedRadioValue();
        if (selectedValue == null) {
            SceneHandler.getInstance().Errore(Message.Errore_Taglia);
            return;
        }

        Integer taglia;
        try {
            taglia = Integer.valueOf(selectedValue);
        } catch (NumberFormatException e) {
            return;
        }

        String idScarpa = ScarpaHandler.getInstance().getScarpe().Id();
        if (taglia != null) {
            find = true;
        }

        if (find) {
            try {
                // Verifica se la scarpa è già nel carrello
                ArrayList<String> idScarpeNelCarrello = ConnessioneDatabase.getInstance().getCarrello(emailUtente, "Carrello").get(7, TimeUnit.SECONDS);
                ArrayList<Scarpe> scarpeNelCarrello = ConnessioneDatabase.getInstance().getottieniInfScarpeNelCarrello(idScarpeNelCarrello);

                for (Scarpe scarpa : scarpeNelCarrello) {
                    if (scarpa.Id().equals(idScarpa)) {
                        SceneHandler.getInstance().Errore(Message.Errore_Impossibile_Aggiungere_La_Stessa_Scarpa_Nel_Carrello);
                        Platform.runLater(this::caricahome);
                        return;
                    }
                }

                // Aggiorna la taglia della scarpa se necessario
                ConnessioneDatabase.getInstance().AggiornamentoTagliaScarpa(idScarpa, taglia);

                // Aggiungi la scarpa al carrello
                Integer quantita = 1;
                ConnessioneDatabase.getInstance().inserisciProdottiNelCarrello(emailUtente, idScarpa, quantita, taglia);
            } catch (SQLException | ExecutionException | InterruptedException | TimeoutException e) {
                SceneHandler.getInstance().Errore(Message.Errore_inserisci_Nel_Carrello);
                return;
            }
            SceneHandler.getInstance().Successo(Message.Scarpa_Aggiunta_Al_Carrello);
            Platform.runLater(this::caricahome);
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
    void addToWishlist(MouseEvent event) {
        String idScarpa = ScarpaHandler.getInstance().getScarpe().Id();
        ImageView clickedImageView = (ImageView) event.getSource();
        Utente utente = Inizializzazione.getInstance().getUtente();

        if (utente == null) {
            SceneHandler.getInstance().Errore(Message.Non_Sei_Loggato);
            return;
        }

        if (idScarpa != null) {
            String email = utente.Email();
            String id_Scarpa = idScarpa;

            // Verifica se l'elemento è già nei preferiti
            CompletableFuture<ArrayList<Scarpe>> future = ConnessioneDatabase.getInstance().getWishlist(email);
            ArrayList<Scarpe> wishlistScarpe;
            try {
                wishlistScarpe = future.get(5, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                SceneHandler.getInstance().Errore(Message.Errore_Nel_Recuperare_I_Preferiti);
                return;
            }

            boolean isInWishlist = wishlistScarpe.stream().anyMatch(s -> s.Id().equals(id_Scarpa));

            if (isInWishlist) {
                // Rimuove dai preferiti e cambia l'icona a nero
                clickedImageView.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/homePageIcons/Preferiti.png")).toExternalForm()));
                ConnessioneDatabase.getInstance().RimuoviDaiPreferiti(id_Scarpa, email);
            } else {
                // Aggiunge ai preferiti e cambia l'icona a rosso
                clickedImageView.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/homePageIcons/preferitirosso.png")).toExternalForm()));
                ConnessioneDatabase.getInstance().insertWishlistProductIntoDB(email, id_Scarpa);
            }
        }
    }

    @FXML
    void CaricaScarpa() {
        Scarpe scarpe = ScarpaHandler.getInstance().getScarpe();
        String url = "immaginiScarpe/" + scarpe.Id() + ".png";
        Image immagine = new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("/" + url)));
        ImmagineProdotto.setImage(immagine);
        ScarpaLabel.setText(scarpe.Nome());
        DesrizioneProdotto.setText(scarpe.Descrizione());
        PrezzoScarpa.setText(scarpe.Prezzo() + "€");

        updateWishlistIcon(PreferitiIcon, scarpe);
    }

    // Nuovo metodo per aggiornare l'icona dei preferiti
    private void updateWishlistIcon(ImageView imageView, Scarpe scarpa) {
        Utente utente = Inizializzazione.getInstance().getUtente();

        if (utente == null) {
            // Se l'utente non è loggato, lasciamo il cuore invariato
            return;
        }

        String email = utente.Email();
        CompletableFuture<ArrayList<Scarpe>> future = ConnessioneDatabase.getInstance().getWishlist(email);
        future.thenAccept(wishlistScarpe -> {
            boolean isInWishlist = wishlistScarpe.stream().anyMatch(s -> s.Id().equals(scarpa.Id()));

            Platform.runLater(() -> {
                if (isInWishlist) {
                    imageView.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/homePageIcons/preferitirosso.png")).toExternalForm()));
                } else {
                    imageView.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/homePageIcons/Preferiti.png")).toExternalForm()));
                }
            });
        }).exceptionally(e -> {
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Recuperare_I_Preferiti);
            return null;
        });
    }

    @FXML
    void CaricaScarpeSimiliProductView(String id, String Marchio) {
        try {
            CompletableFuture<Scarpe> future = ConnessioneDatabase.getInstance().getScarpa(id);
            Scarpe scarpe = future.get(7, TimeUnit.SECONDS);

            if (principaleMarchio == null) {
                principaleMarchio = scarpe.Marchio();  // Imposta il marchio principale alla prima esecuzione
            }

            ConnessioneDatabase.getInstance().AggiungiScarpeSimili(id, principaleMarchio).thenRun(() -> {
                ScarpaHandler.getInstance().setScarpa(scarpe);
                Platform.runLater(() -> {
                    CaricaScarpa();
                    CaricaScarpeSimili();
                });
            });

        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Della_Product_View);
        }
    }

    @FXML
    void CaricaScarpeSimili() {
        ArrayList<Scarpe> scarpe = ConnessioneDatabase.getInstance().getScarpeSimili();

        int numeroScarpe = Math.min(scarpe.size(), ImmagineScarpeSimilin.length);

        // Svuota le immagini e i testi delle scarpe simili
        for (int i = 0; i < ImmagineScarpeSimilin.length; i++) {
            ImmagineScarpeSimilin[i].setImage(null);
            ModelloScarpen[i].setText("");
            Prezzon[i].setText("");
        }

        for (int i = 0; i < numeroScarpe; i++) {
            String url = "immaginiScarpe/" + scarpe.get(i).Id() + ".png";
            Image image = new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("/" + url)));
            ImmagineScarpeSimilin[i].setImage(image);
            ModelloScarpen[i].setText(scarpe.get(i).Nome());
            Prezzon[i].setText(scarpe.get(i).Prezzo() + "€");

            String id = scarpe.get(i).Id();
            String marchio = scarpe.get(i).Modello();
            ScarpeSimiliVbox[i].setOnMouseClicked(event -> CaricaScarpeSimiliProductView(id, marchio));
        }
    }

    private void aggiungiScorciatoieTastiera() {
        prodViewBorder.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    Utente utente = Inizializzazione.getInstance().getUtente();
                    boolean seUtenteLoggato = utente != null;

                    if (new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN).match(event) && seUtenteLoggato) {
                        SceneHandler.getInstance().loadSchermataUtente();
                    }
                    if (new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN).match(event) && seUtenteLoggato) {
                        SceneHandler.getInstance().loadPreferitiWindow();
                    }
                    if (new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN).match(event)) {
                        SceneHandler.getInstance().loadCarrelloWindow();
                    }
                    if (new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN).match(event) && !seUtenteLoggato) {
                        SceneHandler.getInstance().loadLoginWindow();
                    }
                    if (new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN).match(event) && !seUtenteLoggato) {
                        SceneHandler.getInstance().loadRegistrazioneWindow();
                    }
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