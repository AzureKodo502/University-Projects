package com.example.progetto_ecommerce_v3.Controller;

import com.example.progetto_ecommerce_v3.Controller.Handler.ScarpaHandler;
import com.example.progetto_ecommerce_v3.Database.ConnessioneDatabase;
import com.example.progetto_ecommerce_v3.Database.Inizializzazione;
import com.example.progetto_ecommerce_v3.Model.CollegamentiModel;
import com.example.progetto_ecommerce_v3.Model.Scarpe;
import com.example.progetto_ecommerce_v3.Model.Utente;
import com.example.progetto_ecommerce_v3.View.SceneHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import com.example.progetto_ecommerce_v3.Message;

import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CarrelloController {

    @FXML
    private Label carrelloVuoto;

    @FXML
    private Text Taglia1, Taglia2, Taglia3, Taglia4, Taglia5, Taglia6;

    @FXML
    private ImageView ImCarrello1, ImCarrello2, ImCarrello3, ImCarrello4, ImCarrello5, ImCarrello6;

    @FXML
    private Text PrezzoTotale;

    @FXML
    private Button ProcediOrdine_Button;

    @FXML
    private Text Riepilogo_Text;

    @FXML
    private Button RimuoviButton1, RimuoviButton2, RimuoviButton3, RimuoviButton4, RimuoviButton5, RimuoviButton6;

    @FXML
    private Text TitoloProdotto1, TitoloProdotto2, TitoloProdotto3, TitoloProdotto4, TitoloProdotto5, TitoloProdotto6;

    @FXML
    private HBox hbox1, hbox2, hbox3, hbox4, hbox5, hbox6;

    @FXML
    private Text prezzo1, prezzo2, prezzo3, prezzo4, prezzo5, prezzo6;

    @FXML
    private ComboBox<Integer> quantitaBox1, quantitaBox2, quantitaBox3, quantitaBox4, quantitaBox5, quantitaBox6;

    @FXML
    private VBox riepilogo_VBOX;

    ComboBox[] comboboxs;
    Text[] prezzotexts;
    Text[] titoloprodottotexts;
    Text[] preferititexts;
    Button[] rimuovibuttons;
    ImageView[] ImCarrelloview;
    HBox[] hBoxes;

    void initializeObject() {
        ImCarrelloview = new ImageView[]{ImCarrello1, ImCarrello2, ImCarrello3, ImCarrello4, ImCarrello5, ImCarrello6};
        titoloprodottotexts = new Text[]{TitoloProdotto1, TitoloProdotto2, TitoloProdotto3, TitoloProdotto4, TitoloProdotto5, TitoloProdotto6};
        prezzotexts = new Text[]{prezzo1, prezzo2, prezzo3, prezzo4, prezzo5, prezzo6};
        rimuovibuttons = new Button[]{RimuoviButton1, RimuoviButton2, RimuoviButton3, RimuoviButton4, RimuoviButton5, RimuoviButton6};
        comboboxs = new ComboBox[]{quantitaBox1, quantitaBox2, quantitaBox3, quantitaBox4, quantitaBox5, quantitaBox6};
        hBoxes = new HBox[]{hbox1, hbox2, hbox3, hbox4, hbox5, hbox6};
        preferititexts = new Text[]{Taglia1, Taglia2, Taglia3, Taglia4, Taglia5, Taglia6};
    }

    private void initializeHbox() {
        for (HBox hBox : hBoxes) {
            if (hBox != null) {
                hBox.setVisible(false);
            }
        }
    }

    void AggiornamentoPrezzoTotale(String email) throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        String PrezzoTotaleCarrello = ConnessioneDatabase.getInstance().getPrezzoTotaleCarrello(email, "Carrello");
        PrezzoTotaleCarrello = PrezzoTotaleCarrello + "€";
        PrezzoTotale.setText(PrezzoTotaleCarrello);
    }

    void RimuoviElementi(String id, String email) {
        for (HBox hBox : hBoxes) {
            if (hBox != null) {
                hBox.setVisible(false);
            }
        }
        ConnessioneDatabase.getInstance().RimuoviDalCarrelloLaScarpa(id, email);
        Platform.runLater(() -> {
            try {
                initialize();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    void initializeCarrello() throws Exception {
        String email = (Inizializzazione.getInstance().getUtente() != null) ?
                Inizializzazione.getInstance().getUtente().Email() : "null";

        CompletableFuture<ArrayList<String>> future;
        if ("null".equals(email)) {
            future = ConnessioneDatabase.getInstance().getCarrello(null, "Carrello");
        } else {
            future = ConnessioneDatabase.getInstance().getCarrello(email, "Carrello");
        }

        ArrayList<String> idScarpe = future.get(10, TimeUnit.SECONDS);

        if (idScarpe == null || idScarpe.isEmpty()) {
            ProcediOrdine_Button.setVisible(false);
            carrelloVuoto.setVisible(true);
            riepilogo_VBOX.setVisible(false);
            return;
        } else {
            carrelloVuoto.setVisible(false);
        }

        ArrayList<Scarpe> scarpe = ConnessioneDatabase.getInstance().getottieniInfScarpeNelCarrello(idScarpe);

        int size = Math.min(scarpe.size(), ImCarrelloview.length);
        for (int i = 0; i < size; i++) {
            String url = "/immaginiScarpe/" + scarpe.get(i).Id() + ".png";

            URL imageUrl = getClass().getResource(url);
            if (imageUrl == null) {
                continue;
            }

            InputStream imageStream = getClass().getResourceAsStream(url);
            if (imageStream == null) {
                continue;
            }

            Image image = new Image(imageStream);
            ImCarrelloview[i].setImage(image);
            titoloprodottotexts[i].setText(scarpe.get(i).Nome());
            prezzotexts[i].setText(scarpe.get(i).Prezzo() + "€");

            String id = scarpe.get(i).Id();

            CompletableFuture<Integer> futureQuantity = ConnessioneDatabase.getInstance().ControlloQuantitaDelleScarpe(email, id);
            Integer quantity = futureQuantity.get(7, TimeUnit.SECONDS);

            comboboxs[i].setValue(quantity);

            int tmp = i;
            comboboxs[i].setOnAction(event -> {
                try {
                    Object selectedQuantity = comboboxs[tmp].getValue();
                    ConnessioneDatabase.getInstance().AggiornamentoQuanditaScarpa(email, id, selectedQuantity, "Carrello");
                    AggiornamentoPrezzoTotale(email);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            int t = i;
            CompletableFuture<String> futureTaglia = ConnessioneDatabase.getInstance().getTagliaScarpaDalCarrello(email, id);
            futureTaglia.thenAccept(taglia -> {
                preferititexts[t].setText(taglia);  // Imposta la taglia
            });

            rimuovibuttons[i].setOnMouseClicked(mouseEvent -> {
                try {
                    RimuoviElementi(id, email);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            hBoxes[i].setVisible(true);
        }

        AggiornamentoPrezzoTotale(email);
    }

    @FXML
    void initialize() throws Exception {
        initializeObject();
        initializeHbox();
        initializequantita();
        initializeCarrello();

        Riepilogo_Text.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                ProcediOrdine_Button.fire(); // Simula un click sul pulsante
            }
        });


        aggiungiScorciatoieTastiera();

        Platform.runLater(() -> {
            Riepilogo_Text.requestFocus();
        });

        Utente utente = Inizializzazione.getInstance().getUtente();
        if (utente != null) {
            String email = utente.Email();
            AggiornamentoPrezzoTotale(email);
        }
    }

    @FXML
    private void initializequantita() {
        Integer[] quantities = {1, 2, 3, 4, 5, 6};
        quantitaBox1.getItems().addAll(quantities);
        quantitaBox2.getItems().addAll(quantities);
        quantitaBox3.getItems().addAll(quantities);
        quantitaBox4.getItems().addAll(quantities);
        quantitaBox5.getItems().addAll(quantities);
        quantitaBox6.getItems().addAll(quantities);
    }

    private void aggiungiScorciatoieTastiera() {
        Riepilogo_Text.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    Utente utente = Inizializzazione.getInstance().getUtente();
                    boolean seUtenteLoggato = utente != null;

                    if (new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN).match(event)) {
                        SceneHandler.getInstance().loadHomeWindow();
                    }
                    if (new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN).match(event) && !seUtenteLoggato) {
                        SceneHandler.getInstance().loadLoginWindow();
                    }
                    if (new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN).match(event) && !seUtenteLoggato) {
                        SceneHandler.getInstance().loadRegistrazioneWindow();
                    }
                    if (new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN).match(event) && seUtenteLoggato) {
                        SceneHandler.getInstance().loadSchermataUtente();
                    }
                    if (new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN).match(event) && seUtenteLoggato) {
                        SceneHandler.getInstance().loadPreferitiWindow();
                    }
                });
            }
        });
    }

    @FXML
    void procediOrdineAction() {
        Utente utente = Inizializzazione.getInstance().getUtente();
        if (utente == null) {
            SceneHandler.getInstance().Errore(Message.Errore_Ordine);
        } else {
            caricaPagamento();
        }
    }

    @FXML
    void loadHome(MouseEvent event) {
        caricahome();
    }

    void caricaPagamento() {
        try {
            SceneHandler.getInstance().loadPagamento();
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Della_Pagina_Pagamento);
        }
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
}


