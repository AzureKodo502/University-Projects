package com.example.progetto_ecommerce_v3.Controller;

import com.example.progetto_ecommerce_v3.Message;
import com.example.progetto_ecommerce_v3.Model.CollegamentiModel;
import com.example.progetto_ecommerce_v3.Model.Scarpe;
import com.example.progetto_ecommerce_v3.View.SceneHandler;
import com.example.progetto_ecommerce_v3.service.CarrelloService;
import com.example.progetto_ecommerce_v3.Database.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.InputStream;
import java.util.List;

public class CarrelloController {

    @FXML private BorderPane borderpane;
    @FXML private ScrollPane scroll_pane;
    @FXML private VBox vbox;
    @FXML private Text PrezzoTotale;
    @FXML private Button ProcediOrdine_Button;
    @FXML private Label carrelloVuoto;

    private final CarrelloService carrelloService = new CarrelloService();

    @FXML
    void initialize() {
        if (vbox == null && scroll_pane != null) {
            Node content = scroll_pane.getContent();
            if (content instanceof VBox) {
                vbox = (VBox) content;
            }
        }

        if (vbox != null) {
            vbox.getChildren().clear();
        }

        Platform.runLater(() -> {
            if (borderpane != null) borderpane.requestFocus();
        });

        aggiungiScorciatoieTastiera();

        // CONTROLLO LOGIN
        if (!SessionManager.getInstance().isLogged()) {
            try { SceneHandler.getInstance().loadLoginWindow(); } catch (Exception e) {}
            return;
        }

        caricaDatiCarrello();
    }

    private void caricaDatiCarrello() {
        // Usiamo l'ID numerico dell'utente
        String userId = SessionManager.getInstance().getUtente().Id();

        carrelloService.recuperaCarrello(userId)
                .thenAccept(listaScarpe -> {
                    Platform.runLater(() -> {
                        popolaInterfaccia(listaScarpe, userId);
                    });
                });
    }

    private void popolaInterfaccia(List<Scarpe> listaScarpe, String userId) {
        if (vbox == null) return;
        vbox.getChildren().clear();

        boolean isVuoto = listaScarpe.isEmpty();

        if (carrelloVuoto != null) carrelloVuoto.setVisible(isVuoto);
        if (ProcediOrdine_Button != null) ProcediOrdine_Button.setVisible(!isVuoto);

        if (isVuoto) {
            if (PrezzoTotale != null) PrezzoTotale.setText("0 €");
            return;
        }

        // CALCOLO TOTALE
        double totale = carrelloService.calcolaTotale(listaScarpe);
        if (PrezzoTotale != null) {
            PrezzoTotale.setText(String.format("%.2f €", totale));
        }

        // DISEGNO DINAMICO PRODOTTI
        for (Scarpe s : listaScarpe) {
            HBox rigaProdotto = creaElementoGrafico(s, userId);
            vbox.getChildren().add(rigaProdotto);
        }
    }

    private HBox creaElementoGrafico(Scarpe s, String userId) {
        HBox hBox = new HBox(20);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setStyle("-fx-border-color: #ddd; -fx-border-width: 0 0 1 0; -fx-padding: 15; -fx-background-color: white;");

        ImageView imageView = new ImageView();
        try {
            // Logica robusta per l'immagine
            String nomeFile = (s.ImageUrl() != null && !s.ImageUrl().equals("N/A")) ? s.ImageUrl() : s.Id() + ".png";
            String path = "/immaginiScarpe/" + nomeFile;
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) imageView.setImage(new Image(is));
        } catch (Exception e) { }
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);
        imageView.setPreserveRatio(true);

        // Testi
        VBox testi = new VBox(5);
        Label lblNome = new Label(s.Nome());
        lblNome.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label lblTaglia = new Label("Taglia: " + s.Taglia());
        Label lblPrezzo = new Label(s.Prezzo() + " €");
        testi.getChildren().addAll(lblNome, lblTaglia, lblPrezzo);

        // Spaziatore
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Bottone Rimuovi
        Button btnRimuovi = new Button("RIMUOVI");
        btnRimuovi.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");

        btnRimuovi.setOnAction(e -> rimuoviProdotto(userId, s));

        hBox.getChildren().addAll(imageView, testi, spacer, btnRimuovi);
        return hBox;
    }

    private void rimuoviProdotto(String userId, Scarpe s) {
        // Convertiamo la taglia in stringa in modo sicuro per evitare errori
        String tagliaStr = String.valueOf(s.Taglia());

        carrelloService.rimuoviDalCarrello(userId, s.Id(), tagliaStr)
                .thenAccept(success -> {
                    Platform.runLater(() -> {
                        // Se il server dice OK, ricarichiamo la pagina
                        if (success) {
                            caricaDatiCarrello(); // Ricarica la lista aggiornata
                            SceneHandler.getInstance().Info(Message.Prodotto_Rimosso_Dal_Carrello);
                        } else {
                            // Se fallisce (es. server spento), avvisiamo
                            SceneHandler.getInstance().Errore("Impossibile rimuovere il prodotto.");
                        }
                    });
                });
    }

    @FXML
    void procediOrdineAction(ActionEvent event) {
        if (PrezzoTotale != null && PrezzoTotale.getText().startsWith("0")) {
            SceneHandler.getInstance().Info(Message.Carrello_Vuoto);
            return;
        }

        // MODIFICA MVP: Alert invece di cambio scena
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Funzionalità in Arrivo");
        alert.setHeaderText("Integrazione Pagamenti");
        alert.setContentText("Il modulo di pagamento (Stripe/PayPal) sarà integrato prossimamente.\nQuesta è una versione dimostrativa (MVP).");
        alert.showAndWait();
    }

    @FXML void loadHome(MouseEvent event) { caricahome(); }

    private void caricahome() {
        try { SceneHandler.getInstance().loadHomeWindow(); }
        catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void facebookClicked(MouseEvent event) { CollegamentiModel.collegamentiSocial("https://facebook.com"); }
    @FXML void twitterClicked(MouseEvent event) { CollegamentiModel.collegamentiSocial("https://twitter.com"); }
    @FXML void instaClicked(MouseEvent event) { CollegamentiModel.collegamentiSocial("https://instagram.com"); }
    @FXML void chisiamoClicked(MouseEvent event) { try { SceneHandler.getInstance().loadchisiamonoi(); } catch(Exception e){} }
    @FXML void infoClicked(MouseEvent event) { try { SceneHandler.getInstance().loadinfoeprivacy(); } catch(Exception e){} }

    private void aggiungiScorciatoieTastiera() {
        if(borderpane == null) return;
        borderpane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN).match(event)) {
                        caricahome();
                    }
                });
            }
        });
    }
}