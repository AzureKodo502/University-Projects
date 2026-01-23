package com.example.progetto_ecommerce_v3.Controller;

import com.example.progetto_ecommerce_v3.Controller.Handler.ScarpaHandler;
import com.example.progetto_ecommerce_v3.Database.ConnessioneDatabase;
import com.example.progetto_ecommerce_v3.Database.Inizializzazione;
import com.example.progetto_ecommerce_v3.HelloApplication;
import com.example.progetto_ecommerce_v3.Message;
import com.example.progetto_ecommerce_v3.Model.Scarpe;
import com.example.progetto_ecommerce_v3.Model.Utente;
import com.example.progetto_ecommerce_v3.View.SceneHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BoxPageController {

    @FXML
    private Label NomeScarpa1, NomeScarpa2, NomeScarpa3, NomeScarpa4, NomeScarpa5, NomeScarpa6, NomeScarpa7, NomeScarpa8, NomeScarpa9, NomeScarpa10, NomeScarpa11, NomeScarpa12;

    @FXML
    private ImageView Preferiti1, Preferiti2, Preferiti3, Preferiti4, Preferiti5, Preferiti6, Preferiti7, Preferiti8, Preferiti9, Preferiti10, Preferiti11, Preferiti12;

    @FXML
    private Label Prezzo1, Prezzo2, Prezzo3, Prezzo4, Prezzo5, Prezzo6, Prezzo7, Prezzo8, Prezzo9, Prezzo10, Prezzo11, Prezzo12;

    @FXML
    private VBox VBox1, VBox2, VBox3, VBox4, VBox5, VBox6, VBox7, VBox8, VBox9, VBox10, VBox11, VBox12;

    @FXML
    private ImageView immagineProdotto1, immagineProdotto2, immagineProdotto3, immagineProdotto4, immagineProdotto5, immagineProdotto6, immagineProdotto7, immagineProdotto8, immagineProdotto9, immagineProdotto10, immagineProdotto11, immagineProdotto12;

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
        try {
            CompletableFuture<ArrayList<Scarpe>> future = ConnessioneDatabase.getInstance().AggiungiAllaPaginaHomeScarpe();
            ArrayList<Scarpe> prodotti = future.get(7, TimeUnit.SECONDS);
            List<Integer> randomNumber = new ArrayList<>();
            for (int i = 0; i < prodotti.size(); i++) {
                randomNumber.add(i);
            }
            Collections.shuffle(randomNumber);
            for (int i = 0; i < 12; i++) {
                String url = "immaginiScarpe/" + prodotti.get(randomNumber.get(i)).Id() + ".png";
                try (InputStream stream = HelloApplication.class.getResourceAsStream("/" + url)) {
                    if (stream != null) {
                        Image image = new Image(stream);
                        immagineProdotti[i].setImage(image);
                    }
                }

                Scarpe scarpa = prodotti.get(randomNumber.get(i));
                nomeProdotti[i].setText(scarpa.Nome());
                prezzoProdotti[i].setText(scarpa.Prezzo() + "€");
                immagineProdotti[i].setUserData(scarpa);
                preferitiProdotti[i].setUserData(scarpa);
                immagineProdotti[i].setOnMouseClicked(event -> loadProductViewPage(scarpa.Id(), scarpa.Marchio()));
                preferitiProdotti[i].setOnMouseClicked(this::addToWishlist);

                // Aggiorna lo stato del cuore dei preferiti
                updateWishlistIcon(preferitiProdotti[i], scarpa);
            }
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Delle_Scarpe);
            e.printStackTrace();
        }
    }

    // Metodo per aggiornare l'icona della wishlist
    private void updateWishlistIcon(ImageView imageView, Scarpe scarpa) {
        Utente utente = Inizializzazione.getInstance().getUtente();

        if (utente == null) {
            // Se l'utente non è loggato, lasciamo l'icona invariata
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

    // Evento per aggiungere una scarpa ai preferiti
    @FXML
    void addToWishlist(MouseEvent event) {
        ImageView clickedImageView = (ImageView) event.getSource();
        Scarpe scarpa = (Scarpe) clickedImageView.getUserData();
        Utente utente = Inizializzazione.getInstance().getUtente();

        if (utente == null) {
            SceneHandler.getInstance().Errore(Message.Non_Sei_Loggato);
            return;
        }

        if (scarpa != null) {
            String email = utente.Email();
            String id_Scarpa = scarpa.Id();

            // Verifica se l'elemento è già nella wishlist
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
                // Rimuove dalla wishlist e cambia l'icona a Cuore vuoto
                clickedImageView.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/homePageIcons/Preferiti.png")).toExternalForm()));
                ConnessioneDatabase.getInstance().RimuoviDaiPreferiti(id_Scarpa, email);
            } else {
                // Aggiunge alla wishlist e cambia l'icona a Cuore rosso
                clickedImageView.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/homePageIcons/preferitirosso.png")).toExternalForm()));
                ConnessioneDatabase.getInstance().insertWishlistProductIntoDB(email, id_Scarpa);
            }
        }
    }

    @FXML
    // Metodo per caricare il singolo prodotto
    private void loadProductViewPage(String id, String marchio) {
        try {
            CompletableFuture<Scarpe> future = ConnessioneDatabase.getInstance().getScarpa(id);
            Scarpe s = future.get(7, TimeUnit.SECONDS);
            ScarpaHandler.getInstance().setScarpa(s);
            ConnessioneDatabase.getInstance().AggiungiScarpeSimili(id, marchio);
            Platform.runLater(this::caricahome);
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Delle_Scarpe);
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
