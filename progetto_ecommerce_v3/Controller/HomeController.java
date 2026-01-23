package com.example.progetto_ecommerce_v3.Controller;
import com.example.progetto_ecommerce_v3.Controller.Handler.ScarpaHandler;
import com.example.progetto_ecommerce_v3.Database.ConnessioneDatabase;
import com.example.progetto_ecommerce_v3.Database.Inizializzazione;
import com.example.progetto_ecommerce_v3.HelloApplication;
import com.example.progetto_ecommerce_v3.Message;
import com.example.progetto_ecommerce_v3.Model.CollegamentiModel;
import com.example.progetto_ecommerce_v3.Model.Utente;
import com.example.progetto_ecommerce_v3.Model.cambioScenaModel;
import com.example.progetto_ecommerce_v3.View.SceneHandler;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.*;

public class HomeController {

    @FXML
    private AnchorPane pane;

    @FXML
    private ImageView lameloBall;

    @FXML
    private Text benvenutoTxt;

    @FXML
    private Label oppureLabel;

    @FXML
    private Text nomeTxt;

    @FXML
    private Text cognomeTxt;

    @FXML
    private Label RegistratiTxt;

    @FXML
    private Button AccediButton;

    @FXML
    private ToggleButton tema;

    @FXML
    private TextField BarraDiRicerca;

    @FXML
    private HBox bottomPane;

    @FXML
    private ImageView account;

    @FXML
    private ImageView closeMenu;

    @FXML
    private ImageView Logout;

    @FXML
    private Pane menuPane;

    @FXML
    private ImageView openMenu;

    @FXML
    private ImageView preferiti;

    @FXML
    private ImageView menuArrow;

    @FXML
    void loadLogin(MouseEvent event) {
        try {
            if (!Inizializzazione.getInstance().SetUtente()) {
                ScarpaHandler.getInstance().setScarpaNull();
                caricaLogin();
            } else {
                bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource("FXMLs/Login.fxml"))));
            }
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Login);
        }
    }

    void caricaLogin(){
        try{
            SceneHandler.getInstance().loadLoginWindow();
        }catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Login);
        }
    }
    @FXML
    void logoutAction(MouseEvent event) throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        ScarpaHandler.getInstance().setScarpaNull();
        Inizializzazione.getInstance().esci();
        initialize();
        loadHome(event);
    }

    // Metodo initialize che chiama la product view nel bottomPane
    @FXML
    void initialize() throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        tema.getStyleClass().add("tema");
        Circle thumbCircle = (Circle) tema.getGraphic();
        thumbCircle.getStyleClass().add("thumb");

        menuPane.setTranslateX(1251);
        menuPane.setVisible(true);

        closeMenu.setVisible(false);
        menuArrow.setVisible(false);

        Logout();

        // Gestione della visualizzazione della pagina prodotto o box
        try {
            if (ScarpaHandler.getInstance().getScarpe() != null) {
                bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource("FXMLs/ProductView2.fxml"))));
            } else {
                bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource("FXMLs/BoxPage.fxml"))));
            }
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.box_page_error);
            e.printStackTrace();
        }

        Utente utente = Inizializzazione.getInstance().getUtente();
        boolean seUtenteLoggato = utente != null;

        if (seUtenteLoggato) {
            mostraInterfacciaUtenteLoggato(utente);
        } else {
            mostraInterfacciaUtenteNonLoggato();
        }

        tema.setSelected(cambioScenaModel.getInstance().isToggleState());


        aggiungiScorciatoieTastiera(seUtenteLoggato);

        // Richiesta del Focus sul Pane (utile per non avere problemi con le shortcut)
        Platform.runLater(() -> {
            pane.requestFocus();
        });
    }


    private void mostraInterfacciaUtenteLoggato(Utente utente) {
        RegistratiTxt.setVisible(false);
        AccediButton.setVisible(false);
        oppureLabel.setVisible(false);
        benvenutoTxt.setVisible(true);
        nomeTxt.setVisible(true);
        cognomeTxt.setVisible(true);
        CaricaDatiUtente(utente);

        try {
            login();
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Login);
            e.printStackTrace();
        }
    }

    private void mostraInterfacciaUtenteNonLoggato() {
        RegistratiTxt.setVisible(true);
        AccediButton.setVisible(true);
        oppureLabel.setVisible(true);
        benvenutoTxt.setVisible(false);
        nomeTxt.setVisible(false);
        cognomeTxt.setVisible(false);
    }

    private void aggiungiScorciatoieTastiera(boolean seUtenteLoggato) {
        pane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
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
                    if (new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN).match(event)) {
                        SceneHandler.getInstance().loadCarrelloWindow();
                    }
                    if (new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN).match(event) && seUtenteLoggato) {
                        SceneHandler.getInstance().loadPreferitiWindow();
                    }
                });
            }
        });
    }

    @FXML
    void menuApri() {
        menuPane.setTranslateX(1251);
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.4));
        slide.setNode(menuPane);

        slide.setToX(0);
        slide.play();
        lameloBall.setVisible(false);
        menuPane.setTranslateX(1251);

        slide.setOnFinished((ActionEvent e) -> {
            openMenu.setVisible(false);
            closeMenu.setVisible(true);
            menuArrow.setVisible(true);
        });

    }

    @FXML
    void menuChiudi() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.4));
        slide.setNode(menuPane);

        slide.setToX(1251);
        slide.play();

        menuPane.setTranslateX(0);
        menuArrow.setVisible(false);
        lameloBall.setVisible(true);

        slide.setOnFinished((ActionEvent e) -> {
            openMenu.setVisible(true);
            closeMenu.setVisible(false);
        });
    }

    void login() throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        Logout.setVisible(true);
        account.setVisible(true);
        preferiti.setVisible(true);
        RegistratiTxt.setVisible(false);
        AccediButton.setVisible(false);

        String email = Inizializzazione.getInstance().getUtente().Email();
        Future<?> future = ConnessioneDatabase.getInstance().updateNullProduct(email);
        future.get(5, TimeUnit.SECONDS);
    }

    void Logout() {
        Logout.setVisible(false);
        account.setVisible(false);
        preferiti.setVisible(false);
        RegistratiTxt.setVisible(true);
        AccediButton.setVisible(true);
    }

    // Metodo per cercare le Scarpe Premendo sulla Lente D'Ingrandimento
    @FXML
    void searchButtonAction(MouseEvent event) {
        try {
            searchAction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metodo per cercare le Scarpe Premendo il Tasto INVIO
    @FXML
    void searchKeyAction(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                searchAction();  // Esegui solo se il tasto premuto è INVIO
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per eseguire la ricerca delle scarpe
    @FXML
    private void searchAction() throws ExecutionException, InterruptedException, TimeoutException, IOException {
        String text = BarraDiRicerca.getText().toLowerCase();
        if (!text.isEmpty()) {
            CompletableFuture<Label> future = ConnessioneDatabase.getInstance().CercaScarpe(text);
            Label cercaScarpe = future.get(7, TimeUnit.SECONDS);
            String searchedText = cercaScarpe.getText();
            String[] scarpe = searchedText.split(";");

            ConnessioneDatabase.getInstance().addSearchedProducts(scarpe);

            if (scarpe.length > 1) {

                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(HelloApplication.class.getResource("FXMLs/CategoriaBox.fxml")));
                Parent root = loader.load();
                bottomPane.getChildren().clear(); // Svuota il pannello prima di aggiungere la nuova vista
                bottomPane.getChildren().setAll(root); // Aggiunge la nuova vista

                // Dopo aver caricato la nuova vista, svuota e aggiorna la lista delle scarpe cercate nel database
                ConnessioneDatabase.getInstance().SvuotaLaListaDiRicerca();
            }else{
                SceneHandler.getInstance().Errore(Message.Ricerca_Fallita);
                caricahome();
            }
        }
        BarraDiRicerca.setText(""); // Pulisce il campo di ricerca dopo aver completato l'azione di ricerca
    }

    public void toggleButtonAction() {
        SceneHandler.getInstance().changeTheme();
        cambioScenaModel.getInstance().setToggleState(tema.isSelected());
    }

    @FXML
    private void CaricaDatiUtente(Utente utente) {
        nomeTxt.setText(utente.Nome());
        cognomeTxt.setText(utente.Cognome());
    }

    @FXML
    void Nike(MouseEvent event) {
        try {
            ConnessioneDatabase.getInstance().AggiungiScarpePerMarchio("Nike");
            bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource("FXMLs/CategoriaBox.fxml"))));
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_Nike);
            caricahome();
        }
    }

    @FXML
    void Adidas(MouseEvent event) {
        try {
            ConnessioneDatabase.getInstance().AggiungiScarpePerMarchio("Adidas");
            bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource("FXMLs/CategoriaBox.fxml"))));
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_Adidas);
            caricahome();
        }
    }

    @FXML
    void Puma(MouseEvent event) {
        try {
            ConnessioneDatabase.getInstance().AggiungiScarpePerMarchio("Puma");
            bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource("FXMLs/CategoriaBox.fxml"))));
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_Puma);
            caricahome();
        }
    }

    @FXML
    void Jordan(MouseEvent event) {
        try {
            ConnessioneDatabase.getInstance().AggiungiScarpePerMarchio("Jordan");
            bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource("FXMLs/CategoriaBox.fxml"))));
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_Jordan);
            caricahome();
        }
    }

    @FXML
    void Asics(MouseEvent event) {
        try {
            ConnessioneDatabase.getInstance().AggiungiScarpePerMarchio("Asics");
            bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource("FXMLs/CategoriaBox.fxml"))));
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_Asics);
            caricahome();
        }
    }

    @FXML
    void Reebok(MouseEvent event) {
        try {
            ConnessioneDatabase.getInstance().AggiungiScarpePerMarchio("Reebok");
            bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource("FXMLs/CategoriaBox.fxml"))));
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_Reebok);
            caricahome();
        }
    }

    @FXML
    void loadRegistrazione(MouseEvent event) {
        try {
            SceneHandler.getInstance().loadRegistrazioneWindow();
        }catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Registrazione);
            caricahome();
        }
    }

    @FXML
    void loadPaginaUtente(MouseEvent event) {
        try {
            SceneHandler.getInstance().loadSchermataUtente();
        }catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Utente);
            caricahome();
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
    void loadHome(MouseEvent event) {
        ScarpaHandler.getInstance().setScarpa(null);
        caricahome();
    }

    @FXML
    void loadPreferiti(MouseEvent event) {
        try{
            SceneHandler.getInstance().loadPreferitiWindow();
        }catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Preferiti);
            caricahome();
        }
    }

    @FXML
    void instaClicked(MouseEvent event) {
        try {
            CollegamentiModel.collegamentiSocial("https://www.instagram.com/accounts/login/");
        } catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_Instagram);
        }
    }

    @FXML
    void twitterClicked(MouseEvent event) {
        try {
            CollegamentiModel.collegamentiSocial("https://x.com/i/flow/login?input_flow_data=%7B%22requested_variant%22%3A%22eyJsYW5nIjoiaXQiLCJteCI6IjIifQ%3D%3D%22%7D");
        } catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_Twitter);
        }
    }

    @FXML
    void facebookClicked(MouseEvent event) {
        try {
            CollegamentiModel.collegamentiSocial("https://m.facebook.com/login/?locale=it_IT");
        } catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_Facebook);
        }
    }

    @FXML
    void loadCarrello(MouseEvent event) {
        try {
            SceneHandler.getInstance().loadCarrelloWindow();
        }catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Carrello);
            caricahome();
        }
    }

    @FXML
    void ChiSiamo(MouseEvent event) {
        try { SceneHandler.getInstance().loadchisiamonoi();
        } catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Chi_Siamo_Noi);
            caricahome();
        }
    }

    @FXML
    void InfoEPrivacy(MouseEvent event) {
        try {
            SceneHandler.getInstance().loadinfoeprivacy();
        } catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Info_E_Privacy);
            caricahome();
        }
    }

    @FXML
    void  lameloBallClicked(MouseEvent event){
        try { SceneHandler.getInstance().loadlameloBallWindow();
        } catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_lameloBall);
            caricahome();
        }
    }

    @FXML
    void  offWhiteClicked(MouseEvent event){
        try { SceneHandler.getInstance().loadoffWhiteWindow();
        } catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_offWhite);
            caricahome();
        }
    }

    @FXML
    void  travisScottClicked(MouseEvent event){
        try { SceneHandler.getInstance().loadtravisScottWindow();
        } catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_travisScott);
            caricahome();
        }
    }

    @FXML
    void  badBunnyClicked(MouseEvent event){
        try { SceneHandler.getInstance().loadbadBunnyWindow();
        } catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_badBunny);
            caricahome();
        }
    }

    @FXML
    void  tiffanyClicked(MouseEvent event){
        try { SceneHandler.getInstance().loadTiffanyWindow();
        } catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_tiffany);
            caricahome();
        }
    }

    @FXML
    void  infoUtilizzoClicked(MouseEvent event){
        try { SceneHandler.getInstance().loadinfoUtilizzoWindow();
        } catch (Exception e){
            SceneHandler.getInstance().Errore(Message.Errore_Nel_Caricamento_Della_Pagina_infoUtilizzo);
            caricahome();
        }
    }
}
