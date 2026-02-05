package com.example.progetto_ecommerce_v3.Controller;

import com.example.progetto_ecommerce_v3.Controller.Handler.ScarpaHandler;
import com.example.progetto_ecommerce_v3.HelloApplication;
import com.example.progetto_ecommerce_v3.Message;
import com.example.progetto_ecommerce_v3.Model.Scarpe;
import com.example.progetto_ecommerce_v3.Model.Utente;
import com.example.progetto_ecommerce_v3.View.SceneHandler;
import com.example.progetto_ecommerce_v3.service.CarrelloService;
import com.example.progetto_ecommerce_v3.Database.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;

public class BoxPageController {

    private final CarrelloService carrelloService = new CarrelloService();

    @FXML private Label NomeScarpa1, NomeScarpa2, NomeScarpa3, NomeScarpa4, NomeScarpa5, NomeScarpa6, NomeScarpa7, NomeScarpa8, NomeScarpa9, NomeScarpa10, NomeScarpa11, NomeScarpa12;
    @FXML private ImageView Preferiti1, Preferiti2, Preferiti3, Preferiti4, Preferiti5, Preferiti6, Preferiti7, Preferiti8, Preferiti9, Preferiti10, Preferiti11, Preferiti12;
    @FXML private Label Prezzo1, Prezzo2, Prezzo3, Prezzo4, Prezzo5, Prezzo6, Prezzo7, Prezzo8, Prezzo9, Prezzo10, Prezzo11, Prezzo12;
    @FXML private VBox VBox1, VBox2, VBox3, VBox4, VBox5, VBox6, VBox7, VBox8, VBox9, VBox10, VBox11, VBox12;
    @FXML private ImageView immagineProdotto1, immagineProdotto2, immagineProdotto3, immagineProdotto4, immagineProdotto5, immagineProdotto6, immagineProdotto7, immagineProdotto8, immagineProdotto9, immagineProdotto10, immagineProdotto11, immagineProdotto12;

    ImageView[] immagineProdotti;
    Label[] nomeProdotti;
    Label[] prezzoProdotti;
    ImageView[] preferitiProdotti;
    VBox[] VBoxes;

    @FXML
    void arrayInitialize() {
        immagineProdotti = new ImageView[]{immagineProdotto1, immagineProdotto2, immagineProdotto3, immagineProdotto4, immagineProdotto5, immagineProdotto6, immagineProdotto7, immagineProdotto8, immagineProdotto9, immagineProdotto10, immagineProdotto11, immagineProdotto12};
        nomeProdotti = new Label[]{NomeScarpa1, NomeScarpa2, NomeScarpa3, NomeScarpa4, NomeScarpa5, NomeScarpa6, NomeScarpa7, NomeScarpa8, NomeScarpa9, NomeScarpa10, NomeScarpa11, NomeScarpa12};
        prezzoProdotti = new Label[]{Prezzo1, Prezzo2, Prezzo3, Prezzo4, Prezzo5, Prezzo6, Prezzo7, Prezzo8, Prezzo9, Prezzo10, Prezzo11, Prezzo12};
        preferitiProdotti = new ImageView[]{Preferiti1, Preferiti2, Preferiti3, Preferiti4, Preferiti5, Preferiti6, Preferiti7, Preferiti8, Preferiti9, Preferiti10, Preferiti11, Preferiti12};
        VBoxes = new VBox[]{VBox1, VBox2, VBox3, VBox4, VBox5, VBox6, VBox7, VBox8, VBox9, VBox10, VBox11, VBox12};
    }

    @FXML
    private void LoadHomePageImage() {
        // --- CHIAMATA AL BACKEND (Spring Boot) ---
        carrelloService.recuperaProdottiDalBackend()
                .thenAccept(prodotti -> {
                    Platform.runLater(() -> {
                        if (prodotti == null || prodotti.isEmpty()) {
                            System.out.println("Nessun prodotto trovato nel backend.");
                            return;
                        }

                        // Mischia i prodotti per variare la Home
                        Collections.shuffle(prodotti);

                        int limite = Math.min(prodotti.size(), 12);

                        for (int i = 0; i < limite; i++) {
                            Scarpe scarpa = prodotti.get(i);

                            // 1. Carica Immagine (Uso ImageUrl dal Backend)
                            String nomeFile = (scarpa.ImageUrl() != null && !scarpa.ImageUrl().isEmpty())
                                    ? scarpa.ImageUrl()
                                    : scarpa.Id() + ".png";

                            String url = "immaginiScarpe/" + nomeFile;

                            try (InputStream stream = HelloApplication.class.getResourceAsStream("/" + url)) {
                                if (stream != null) {
                                    immagineProdotti[i].setImage(new Image(stream));
                                } else {
                                    System.err.println("Immagine non trovata: " + url);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // Imposta Testi e Dati
                            nomeProdotti[i].setText(scarpa.Nome());
                            prezzoProdotti[i].setText(scarpa.Prezzo() + "€");

                            // Salviamo l'oggetto Scarpa dentro l'immagine per recuperarlo al click
                            immagineProdotti[i].setUserData(scarpa);
                            preferitiProdotti[i].setUserData(scarpa);

                            // Eventi Click
                            immagineProdotti[i].setOnMouseClicked(event -> loadProductViewPage(scarpa));
                            preferitiProdotti[i].setOnMouseClicked(this::addToWishlist);

                            // (Opzionale) Reset icona preferiti visiva
                            preferitiProdotti[i].setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/homePageIcons/Preferiti.png")).toExternalForm()));
                        }

                        // Nascondi le box vuote se ci sono meno di 12 prodotti
                        for (int i = limite; i < 12; i++) {
                            VBoxes[i].setVisible(false);
                        }
                    });
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    Platform.runLater(() -> SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Delle_Scarpe));
                    return null;
                });
    }

    @FXML
    private void loadProductViewPage(Scarpe scarpa) {
        ScarpaHandler.getInstance().setScarpa(scarpa);

        caricahome();
    }

    @FXML
    void addToWishlist(MouseEvent event) {
        ImageView clickedImageView = (ImageView) event.getSource();
        Utente utente = SessionManager.getInstance().getUtente();

        if (utente == null) {
            SceneHandler.getInstance().Errore(Message.Non_Sei_Loggato);
            return;
        }

        Image currentImage = clickedImageView.getImage();
        String urlRosso = Objects.requireNonNull(getClass().getResource("/images/homePageIcons/preferitirosso.png")).toExternalForm();

        if (currentImage.getUrl() != null && currentImage.getUrl().equals(urlRosso)) {
            clickedImageView.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/homePageIcons/Preferiti.png")).toExternalForm()));
        } else {
            clickedImageView.setImage(new Image(urlRosso));
        }
    }

    @FXML
    void initialize() {
        arrayInitialize();
        LoadHomePageImage();
    }

    void caricahome(){
        try{
            SceneHandler.getInstance().loadHomeWindow();
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Home);
        }
    }
}