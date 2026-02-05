package com.example.progetto_ecommerce_v3.Controller;

import com.example.progetto_ecommerce_v3.Model.CollegamentiModel;
import com.example.progetto_ecommerce_v3.Message;
import com.example.progetto_ecommerce_v3.View.SceneHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


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
    void initialize() {
        initializeOggetti();
        TextAggiungiAlCarrello.setVisible(false);
        aggiungiScorciatoieTastiera();

        // Work in Progress Alert
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Funzionalità in Arrivo");
            alert.setHeaderText("Work in Progress");
            alert.setContentText("La sezione Preferiti sarà disponibile nella versione 2.0!");
            alert.showAndWait();

            // Reindirizza alla Home dopo l'avviso per evitare schermate vuote
            try {
                SceneHandler.getInstance().loadHomeWindow();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        if(HBox1.getScene() != null) {
            HBox1.getScene().setOnKeyPressed(event -> {
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
    }
}