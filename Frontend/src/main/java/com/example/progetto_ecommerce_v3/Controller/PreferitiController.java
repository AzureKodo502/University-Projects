package com.example.progetto_ecommerce_v3.Controller;

import com.example.progetto_ecommerce_v3.Database.ConnessioneDatabase;
import com.example.progetto_ecommerce_v3.Database.SessionManager;
import com.example.progetto_ecommerce_v3.HelloApplication;
import com.example.progetto_ecommerce_v3.Model.CollegamentiModel;
import com.example.progetto_ecommerce_v3.Model.Scarpe;
import com.example.progetto_ecommerce_v3.Model.Utente;
import com.example.progetto_ecommerce_v3.Message;
import com.example.progetto_ecommerce_v3.View.SceneHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PreferitiController {

    @FXML
    private Label preferitiVuoto;

    @FXML
    private Button AggiungiCarrelloPreferitiButton1, AggiungiCarrelloPreferitiButton2, AggiungiCarrelloPreferitiButton3, AggiungiCarrelloPreferitiButton4, AggiungiCarrelloPreferitiButton5, AggiungiCarrelloPreferitiButton6;

    @FXML
    private HBox HBox1, HBox2, HBox3, HBox4, HBox5, HBox6;

    @FXML
    private ImageView ImmagPreferiti1, ImmagPreferiti2, ImmagPreferiti3, ImmagPreferiti4, ImmagPreferiti5, ImmagPreferiti6;

    @FXML
    private Text Prezzo_Preferiti1, Prezzo_Preferiti2, Prezzo_Preferiti3, Prezzo_Preferiti4, Prezzo_Preferiti5, Prezzo_Preferiti6;

    @FXML
    private Button RimuoviPreferitiButton1, RimuoviPreferitiButton2, RimuoviPreferitiButton3, RimuoviPreferitiButton4, RimuoviPreferitiButton5, RimuoviPreferitiButton6;

    @FXML
    private ComboBox<Integer> TagliaBoxPreferiti1, TagliaBoxPreferiti2, TagliaBoxPreferiti3, TagliaBoxPreferiti4, TagliaBoxPreferiti5, TagliaBoxPreferiti6;

    @FXML
    private Text TextAggiungiAlCarrello;

    @FXML
    private Text TitoloProdottoPreferiti1, TitoloProdottoPreferiti2, TitoloProdottoPreferiti3, TitoloProdottoPreferiti4, TitoloProdottoPreferiti5, TitoloProdottoPreferiti6;

    private HBox[] hboxes;
    private Button[] RimuoviPreferitiButtons, AggiungiCarrelloPreferitiButtons;
    private ImageView[] imageViews;
    private Text[] TitoloProdottoPreferitis;
    private Text[] Prezzo_Preferitis;
    private ComboBox<Integer>[] TagliaBoxPreferitis;

    @FXML
    void initializeOggetti() {
        imageViews = new ImageView[]{ImmagPreferiti1, ImmagPreferiti2, ImmagPreferiti3, ImmagPreferiti4, ImmagPreferiti5, ImmagPreferiti6};
        hboxes = new HBox[]{HBox1, HBox2, HBox3, HBox4, HBox5, HBox6};
        RimuoviPreferitiButtons = new Button[]{RimuoviPreferitiButton1, RimuoviPreferitiButton2, RimuoviPreferitiButton3, RimuoviPreferitiButton4, RimuoviPreferitiButton5, RimuoviPreferitiButton6};
        AggiungiCarrelloPreferitiButtons = new Button[]{AggiungiCarrelloPreferitiButton1, AggiungiCarrelloPreferitiButton2, AggiungiCarrelloPreferitiButton3, AggiungiCarrelloPreferitiButton4, AggiungiCarrelloPreferitiButton5, AggiungiCarrelloPreferitiButton6};
        TitoloProdottoPreferitis = new Text[]{TitoloProdottoPreferiti1, TitoloProdottoPreferiti2, TitoloProdottoPreferiti3, TitoloProdottoPreferiti4, TitoloProdottoPreferiti5, TitoloProdottoPreferiti6};
        TagliaBoxPreferitis = new ComboBox[]{TagliaBoxPreferiti1, TagliaBoxPreferiti2, TagliaBoxPreferiti3, TagliaBoxPreferiti4, TagliaBoxPreferiti5, TagliaBoxPreferiti6};
        Prezzo_Preferitis = new Text[]{Prezzo_Preferiti1, Prezzo_Preferiti2, Prezzo_Preferiti3, Prezzo_Preferiti4, Prezzo_Preferiti5, Prezzo_Preferiti6};
    }

    @FXML
    private void CaricaDatiUtente(Utente utente) throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        CaricaPreferiti(utente.Email());
    }

    void RimuoviElementi(String id, String email) {
        for (HBox hBox : hboxes) {
            if (hBox != null) {
                hBox.setVisible(false);
            }
        }
        ConnessioneDatabase.getInstance().RimuoviDaiPreferiti(id, email);
        Platform.runLater(() -> {
            try {
                initialize();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void inserisciNelCarrello_Preferiti(String idScarpa, String email, int taglia) throws Exception {
        try {
            // Verifica se la scarpa è già nel carrello
            ArrayList<String> idScarpeNelCarrello = ConnessioneDatabase.getInstance().getCarrello(email, "Carrello").get(7, TimeUnit.SECONDS);
            ArrayList<Scarpe> scarpeNelCarrello = ConnessioneDatabase.getInstance().getottieniInfScarpeNelCarrello(idScarpeNelCarrello);

            for (Scarpe scarpa : scarpeNelCarrello) {
                if (scarpa.Id().equals(idScarpa)) {
                    SceneHandler.getInstance().Errore(Message.Errore_Impossibile_Aggiungere_La_Stessa_Scarpa_Nel_Carrello);
                    Platform.runLater(() -> SceneHandler.getInstance().loadPreferitiWindow());
                    return;
                }
            }
            ConnessioneDatabase.getInstance().inserisciProdottiNelCarrello(email, idScarpa, 1, taglia);
            mostraMessaggioCarrello();
        } catch (SQLException e) {
            SceneHandler.getInstance().Errore(Message.Errore_inserisci_Nel_Carrello);
        }
    }

    void CaricaPreferiti(String email) throws ExecutionException, InterruptedException, TimeoutException, SQLException {
        CompletableFuture<ArrayList<Scarpe>> future = ConnessioneDatabase.getInstance().getWishlist(email);
        ArrayList<Scarpe> scarpe = future.get(5, TimeUnit.SECONDS);

        if (scarpe == null) {
            return;
        }

        for (int i = 0; i < hboxes.length; i++) {
            hboxes[i].setVisible(false);  // Nascondiamo tutti gli elementi per iniziare
            preferitiVuoto.setVisible(true);
        }

        for (int i = 0; i < scarpe.size() && i < hboxes.length; i++) {
            Scarpe scarpa = scarpe.get(i);

            String url = "immaginiScarpe/" + scarpa.Id() + ".png";
            InputStream imageStream = HelloApplication.class.getResourceAsStream("/" + url);

            Image image;
            image = new Image(Objects.requireNonNull(imageStream));

            imageViews[i].setImage(image);

            TitoloProdottoPreferitis[i].setText(scarpa.Nome());
            Prezzo_Preferitis[i].setText(scarpa.Prezzo() + "€");

            String id = scarpa.Id();
            int index = i;
            preferitiVuoto.setVisible(false);
            RimuoviPreferitiButtons[i].setOnMouseClicked(mouseEvent -> {
                RimuoviElementi(id, email);
            });

            AggiungiCarrelloPreferitiButtons[i].setOnMouseClicked(mouseEvent -> {
                try {
                    Integer taglia = (TagliaBoxPreferitis[index].getValue());
                    if (taglia != null) {
                        ConnessioneDatabase.getInstance().AggiornamentoTagliaScarpa(id, taglia);
                        inserisciNelCarrello_Preferiti(id, email, taglia);
                    } else {
                        SceneHandler.getInstance().Errore(Message.Errore_Taglia);
                        TextAggiungiAlCarrello.setVisible(false);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            hboxes[i].setVisible(true);
        }
    }

    @FXML
    void mostraMessaggioCarrello() {
        new Thread(() -> {
            try {
                Platform.runLater(() -> TextAggiungiAlCarrello.setVisible(true));
                Thread.sleep(3000);  // Mostra il messaggio per 3 secondi
                Platform.runLater(() -> TextAggiungiAlCarrello.setVisible(false));
            } catch (InterruptedException e) {
                SceneHandler.getInstance().Errore(Message.Thread_Error);
            }
        }).start();
    }

    private void initializeTagliaPreferiti() {
        Integer[] Taglia = {36, 37, 38, 39, 40, 41, 42, 43, 44, 45};
        TagliaBoxPreferiti1.getItems().addAll(Taglia);
        TagliaBoxPreferiti2.getItems().addAll(Taglia);
        TagliaBoxPreferiti3.getItems().addAll(Taglia);
        TagliaBoxPreferiti4.getItems().addAll(Taglia);
        TagliaBoxPreferiti5.getItems().addAll(Taglia);
        TagliaBoxPreferiti6.getItems().addAll(Taglia);
    }

    @FXML
    void initialize() throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        initializeOggetti();
        initializeTagliaPreferiti();
        Utente utente = SessionManager.getInstance().getUtente();
        CaricaDatiUtente(utente);
        TextAggiungiAlCarrello.setVisible(false);

        aggiungiScorciatoieTastiera();

        Platform.runLater(() -> {
            HBox1.requestFocus();
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

    private void aggiungiScorciatoieTastiera() {
        HBox1.sceneProperty().addListener((observable, oldScene, newScene) -> {
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
                });
            }
        });
    }
}



