package com.example.progetto_ecommerce_v3.Controller;

import com.example.progetto_ecommerce_v3.Database.Inizializzazione;
import com.example.progetto_ecommerce_v3.HelloApplication;
import com.example.progetto_ecommerce_v3.Message;
import com.example.progetto_ecommerce_v3.Controller.Handler.ScarpaHandler;
import com.example.progetto_ecommerce_v3.Database.ConnessioneDatabase;
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

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CategoriaBoxController {

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

    void arrayInitialize() {
        immagineProdotti = new ImageView[]{immagineProdotto1, immagineProdotto2, immagineProdotto3, immagineProdotto4, immagineProdotto5, immagineProdotto6, immagineProdotto7, immagineProdotto8, immagineProdotto9, immagineProdotto10, immagineProdotto11, immagineProdotto12};
        nomeProdotti = new Label[]{NomeScarpa1, NomeScarpa2, NomeScarpa3, NomeScarpa4, NomeScarpa5, NomeScarpa6, NomeScarpa7, NomeScarpa8, NomeScarpa9, NomeScarpa10, NomeScarpa11, NomeScarpa12};
        preferitiProdotti = new ImageView[]{Preferiti1, Preferiti2, Preferiti3, Preferiti4, Preferiti5, Preferiti6, Preferiti7, Preferiti8, Preferiti9, Preferiti10, Preferiti11, Preferiti12};
        prezzoProdotti = new Label[]{Prezzo1, Prezzo2, Prezzo3, Prezzo4, Prezzo5, Prezzo6, Prezzo7, Prezzo8, Prezzo9, Prezzo10, Prezzo11, Prezzo12};
        VBoxes = new VBox[]{VBox1, VBox2, VBox3, VBox4, VBox5, VBox6, VBox7, VBox8, VBox9, VBox10, VBox11, VBox12};
    }

    // Metodo per rendere invisibili i VBox
    void rimuoviVboxInEccesso(int size) {
        for (int i = size; i < 12; i++) {
            VBoxes[i].setVisible(false);
        }
    }

    // Metodo per pulire le visualizzazioni dei prodotti
    void clearProductViews() {
        for (int i = 0; i < 12; i++) {
            immagineProdotti[i].setImage(null);
            nomeProdotti[i].setText("");
            prezzoProdotti[i].setText("");
            VBoxes[i].setVisible(true);
            VBoxes[i].setOnMouseClicked(null);
        }
    }

    // Metodo per caricare il singolo prodotto
    private void loadProductViewPage(String id, String marchio) {
        try {
            CompletableFuture<Scarpe> future = ConnessioneDatabase.getInstance().getScarpa(id);
            Scarpe s = future.get(7, TimeUnit.SECONDS);
            ScarpaHandler.getInstance().setScarpa(s);
            ConnessioneDatabase.getInstance().AggiungiScarpeSimili(id, marchio);
            Platform.runLater(() -> SceneHandler.getInstance().loadHomeWindow());
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Della_Product_View);
        }
    }

    // Metodo per caricare le varie scarpe
    void AggiungiLeScarpe(ArrayList<Scarpe> scarpe) {
        int maxSize = Math.min(scarpe.size(), immagineProdotti.length);

        for (int i = 0; i < maxSize; i++) {
            String url = "immaginiScarpe/" + scarpe.get(i).Id() + ".png";
            try {
                Image image = new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("/" + url)));
                immagineProdotti[i].setImage(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
            nomeProdotti[i].setText(scarpe.get(i).Nome());
            prezzoProdotti[i].setText(scarpe.get(i).Prezzo() + "€");

            String id = scarpe.get(i).Id();
            String marchio = scarpe.get(i).Marchio();
            Scarpe currentScarpa = scarpe.get(i);

            immagineProdotti[i].setUserData(currentScarpa);
            preferitiProdotti[i].setUserData(currentScarpa);

            immagineProdotti[i].setOnMouseClicked(event -> loadProductViewPage(id, marchio));
            preferitiProdotti[i].setOnMouseClicked(this::addToWishlist); // Imposta l'evento per il click sul cuore
            // Aggiorna lo stato del cuore dei preferiti
            updateWishlistIcon(preferitiProdotti[i], scarpe.get(i));
        }
        rimuoviVboxInEccesso(maxSize);
    }

    // Metodo per aggiornare l'icona del Cuore
    private void updateWishlistIcon(ImageView imageView, Scarpe scarpa) {
        Utente utente = Inizializzazione.getInstance().getUtente();

        if (utente == null) {
            // Se l'utente non è loggato, lasciamo l'icona cuore vuoto
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

    // Metodo per aggiungere la scarpa ai preferiti
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
                // Aggiunge alla wishlist e cambia l'icona a rosso
                clickedImageView.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/homePageIcons/preferitirosso.png")).toExternalForm()));
                ConnessioneDatabase.getInstance().insertWishlistProductIntoDB(email, id_Scarpa);
            }
        } else {
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Delle_Scarpe);
        }
    }

    @FXML
    private void initialize() {
        arrayInitialize();
        try {
            clearProductViews(); // Resettiamo la visualizzazione prima di caricare nuove Scarpe

            ArrayList<Scarpe> scarpe = new ArrayList<>();
            if (ConnessioneDatabase.getInstance().getRicercaScarpe().isEmpty()) {
                scarpe = ConnessioneDatabase.getInstance().getAggiungiScarpePerMarchio();
            } else {
                scarpe = ConnessioneDatabase.getInstance().getRicercaScarpe();
            }
            if (scarpe != null) {
                rimuoviVboxInEccesso(scarpe.size());
                AggiungiLeScarpe(scarpe);
                ConnessioneDatabase.getInstance().SvuotaListaScarpe();
            } else {
                rimuoviVboxInEccesso(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Delle_Scarpe);
        }
    }
}





