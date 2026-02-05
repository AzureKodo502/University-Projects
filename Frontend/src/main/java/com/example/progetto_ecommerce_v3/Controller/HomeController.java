package com.example.progetto_ecommerce_v3.Controller;

import com.example.progetto_ecommerce_v3.Controller.Handler.ScarpaHandler;
import com.example.progetto_ecommerce_v3.HelloApplication;
import com.example.progetto_ecommerce_v3.Message;
import com.example.progetto_ecommerce_v3.Model.CollegamentiModel;
import com.example.progetto_ecommerce_v3.Model.Utente;
import com.example.progetto_ecommerce_v3.Model.cambioScenaModel;
import com.example.progetto_ecommerce_v3.View.SceneHandler;
import com.example.progetto_ecommerce_v3.service.CatalogoService;
import com.example.progetto_ecommerce_v3.Database.SessionManager;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
import java.util.Objects;

public class HomeController {

    // Usiamo questo per parlare con Spring Boot
    private final CatalogoService catalogoService = new CatalogoService();

    @FXML private AnchorPane pane;
    @FXML private ImageView lameloBall;
    @FXML private Text benvenutoTxt;
    @FXML private Label oppureLabel;
    @FXML private Text nomeTxt;
    @FXML private Text cognomeTxt;
    @FXML private Label RegistratiTxt;
    @FXML private Button AccediButton;
    @FXML private ToggleButton tema;
    @FXML private TextField BarraDiRicerca;
    @FXML private HBox bottomPane;
    @FXML private ImageView account;
    @FXML private ImageView closeMenu;
    @FXML private ImageView Logout;
    @FXML private Pane menuPane;
    @FXML private ImageView openMenu;
    @FXML private ImageView preferiti;
    @FXML private ImageView menuArrow;

    @FXML
    void initialize() {
        // Setup Grafico Iniziale
        tema.getStyleClass().add("tema");
        Circle thumbCircle = (Circle) tema.getGraphic();
        thumbCircle.getStyleClass().add("thumb");

        menuPane.setTranslateX(1251);
        menuPane.setVisible(true);
        closeMenu.setVisible(false);
        menuArrow.setVisible(false);

        // Gestione Utente (Se Loggato o No)
        boolean isLogged = SessionManager.getInstance().isLogged();
        if (isLogged) {
            mostraInterfacciaUtenteLoggato(SessionManager.getInstance().getUtente());
        } else {
            mostraInterfacciaUtenteNonLoggato();
        }

        caricaContenutoCentrale();

        tema.setSelected(cambioScenaModel.getInstance().isToggleState());
        aggiungiScorciatoieTastiera(isLogged);

        Platform.runLater(() -> pane.requestFocus());
    }

    private void caricaContenutoCentrale() {
        try {
            if (ScarpaHandler.getInstance().getScarpe() != null) {
                // Se c'è una scarpa selezionata, mostra i dettagli
                bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource("FXMLs/ProductView2.fxml"))));
            } else {
                // Altrimenti mostra la Home con i Box
                bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource("FXMLs/BoxPage.fxml"))));
            }
        } catch (Exception e) {
            SceneHandler.getInstance().Errore(Message.box_page_error);
            e.printStackTrace();
        }
    }

    // Gestione Marchi
    private void caricaPaginaBrand(String brand) {
        catalogoService.cercaProdotti(brand)
                .thenAccept(success -> Platform.runLater(() -> {
                    if (success) {
                        try {
                            // Carichiamo la vista dei risultati (CategoriaBox)
                            bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource("FXMLs/CategoriaBox.fxml"))));
                        } catch (IOException e) {
                            SceneHandler.getInstance().Errore(Message.Errore_Generico);
                            e.printStackTrace();
                        }
                    } else {
                        SceneHandler.getInstance().Info("Nessun prodotto trovato per " + brand);
                    }
                }));
    }

    @FXML void Nike(MouseEvent event) { caricaPaginaBrand("Nike"); }
    @FXML void Adidas(MouseEvent event) { caricaPaginaBrand("Adidas"); }
    @FXML void Puma(MouseEvent event) { caricaPaginaBrand("Puma"); }
    @FXML void Jordan(MouseEvent event) { caricaPaginaBrand("Jordan"); }
    @FXML void Asics(MouseEvent event) { caricaPaginaBrand("Asics"); }
    @FXML void Reebok(MouseEvent event) { caricaPaginaBrand("Reebok"); }

    @FXML
    void searchButtonAction(MouseEvent event) { eseguiRicerca(); }

    @FXML
    void searchKeyAction(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) eseguiRicerca();
    }

    private void eseguiRicerca() {
        String testo = BarraDiRicerca.getText();
        if (testo == null || testo.trim().isEmpty()) return;

        catalogoService.cercaProdotti(testo)
                .thenAccept(success -> Platform.runLater(() -> {
                    if (success) {
                        try {
                            bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource("FXMLs/CategoriaBox.fxml"))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        SceneHandler.getInstance().Errore(Message.Ricerca_Fallita);
                        caricahome(); // Ricarica la home pulita
                    }
                    BarraDiRicerca.setText("");
                }));
    }


    // Gestione Interfaccia Utente

    private void mostraInterfacciaUtenteLoggato(Utente utente) {
        RegistratiTxt.setVisible(false);
        AccediButton.setVisible(false);
        oppureLabel.setVisible(false);

        benvenutoTxt.setVisible(true);
        nomeTxt.setVisible(true);
        cognomeTxt.setVisible(true);

        nomeTxt.setText(utente.Nome());
        if (utente.Cognome() != null) cognomeTxt.setText(utente.Cognome());

        if (Logout != null) Logout.setVisible(true);
        if (account != null) account.setVisible(true);
        if (preferiti != null) preferiti.setVisible(true);
    }

    private void mostraInterfacciaUtenteNonLoggato() {
        RegistratiTxt.setVisible(true);
        AccediButton.setVisible(true);
        oppureLabel.setVisible(true);

        benvenutoTxt.setVisible(false);
        nomeTxt.setVisible(false);
        cognomeTxt.setVisible(false);
        nomeTxt.setText("");
        cognomeTxt.setText("");

        if (Logout != null) Logout.setVisible(false);
        if (account != null) account.setVisible(false);
        if (preferiti != null) preferiti.setVisible(false);
    }

    @FXML
    void loadLogin(MouseEvent event) {
        if (!SessionManager.getInstance().isLogged()) {
            ScarpaHandler.getInstance().setScarpaNull();
            caricaLogin();
        }
    }

    @FXML
    void logoutAction(MouseEvent event) {
        SessionManager.getInstance().logout();
        ScarpaHandler.getInstance().setScarpaNull();
        loadHome(event);
    }


    //METODI UI SECONDARI

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

    public void toggleButtonAction() {
        SceneHandler.getInstance().changeTheme();
        cambioScenaModel.getInstance().setToggleState(tema.isSelected());
    }

    void caricahome(){ try{ SceneHandler.getInstance().loadHomeWindow(); } catch (Exception e) { SceneHandler.getInstance().Errore(Message.Errore_Home); } }
    void caricaLogin(){ try{ SceneHandler.getInstance().loadLoginWindow(); } catch (Exception e) { SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Login); } }

    @FXML void loadHome(MouseEvent event) { ScarpaHandler.getInstance().setScarpa(null); caricahome(); }
    @FXML void loadRegistrazione(MouseEvent event) { try { SceneHandler.getInstance().loadRegistrazioneWindow(); } catch(Exception e){ SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Registrazione); caricahome(); } }
    @FXML void loadPaginaUtente(MouseEvent event) { try { SceneHandler.getInstance().loadSchermataUtente(); } catch(Exception e){ SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Utente); caricahome(); } }
    @FXML void loadCarrello(MouseEvent event) { try { SceneHandler.getInstance().loadCarrelloWindow(); } catch(Exception e){ SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Carrello); caricahome(); } }
    @FXML void loadPreferiti(MouseEvent event) { try { SceneHandler.getInstance().loadPreferitiWindow(); } catch(Exception e){ SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Preferiti); caricahome(); } }

    @FXML void instaClicked(MouseEvent event) { try { CollegamentiModel.collegamentiSocial("https://instagram.com"); } catch(Exception e){} }
    @FXML void twitterClicked(MouseEvent event) { try { CollegamentiModel.collegamentiSocial("https://twitter.com"); } catch(Exception e){} }
    @FXML void facebookClicked(MouseEvent event) { try { CollegamentiModel.collegamentiSocial("https://facebook.com"); } catch(Exception e){} }

    @FXML void ChiSiamo(MouseEvent e) { try{SceneHandler.getInstance().loadchisiamonoi();}catch(Exception ex){SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Chi_Siamo_Noi);caricahome();} }
    @FXML void InfoEPrivacy(MouseEvent e) { try{SceneHandler.getInstance().loadinfoeprivacy();}catch(Exception ex){SceneHandler.getInstance().Errore(Message.Errore_Caricamento_Pagina_Info_E_Privacy);caricahome();} }

    @FXML void lameloBallClicked(MouseEvent e) { try{SceneHandler.getInstance().loadlameloBallWindow();}catch(Exception ex){caricahome();} }
    @FXML void offWhiteClicked(MouseEvent e) { try{SceneHandler.getInstance().loadoffWhiteWindow();}catch(Exception ex){caricahome();} }
    @FXML void travisScottClicked(MouseEvent e) { try{SceneHandler.getInstance().loadtravisScottWindow();}catch(Exception ex){caricahome();} }
    @FXML void badBunnyClicked(MouseEvent e) { try{SceneHandler.getInstance().loadbadBunnyWindow();}catch(Exception ex){caricahome();} }
    @FXML void tiffanyClicked(MouseEvent e) { try{SceneHandler.getInstance().loadTiffanyWindow();}catch(Exception ex){caricahome();} }
    @FXML void infoUtilizzoClicked(MouseEvent e) { try{SceneHandler.getInstance().loadinfoUtilizzoWindow();}catch(Exception ex){caricahome();} }

    private void aggiungiScorciatoieTastiera(boolean seUtenteLoggato) {
        pane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN).match(event)) SceneHandler.getInstance().loadHomeWindow();
                    if (new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN).match(event) && !seUtenteLoggato) SceneHandler.getInstance().loadLoginWindow();
                    if (new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN).match(event) && !seUtenteLoggato) SceneHandler.getInstance().loadRegistrazioneWindow();
                    if (new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN).match(event) && seUtenteLoggato) SceneHandler.getInstance().loadSchermataUtente();
                    if (new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN).match(event)) SceneHandler.getInstance().loadCarrelloWindow();
                    if (new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN).match(event) && seUtenteLoggato) SceneHandler.getInstance().loadPreferitiWindow();
                });
            }
        });
    }
}