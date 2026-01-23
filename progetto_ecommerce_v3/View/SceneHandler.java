package com.example.progetto_ecommerce_v3.View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SceneHandler {

    private static final String CSS_PATH="/css/";
    private Stage stage;
    private Scene scene;
    private String theme = "LightMode";

    private Alert alert = new Alert(Alert.AlertType.INFORMATION);

    private static SceneHandler instance = null;

    private SceneHandler() {
    }

    public void init(Stage primaryStage){
        if (this.stage != null) {
            return;
        }
        this.stage = primaryStage;
        this.scene = new Scene(load("com/example/progetto_ecommerce_v3/FXMLs/Home.fxml"), 1535, 800);
        stage.setTitle("Stride Style!");
        this.stage.setMaximized(true);
        this.stage.setMinWidth(1370);
        this.stage.setMinHeight(700);
        setCSSForScene(scene);
        stage.setScene(scene);
        this.stage.show();
    }

    public static SceneHandler getInstance() {
        if (instance == null)
            instance = new SceneHandler();
        return instance;
    }

    private <T> T load(String fxmlFile) {
        FXMLLoader fxmlLoader = new FXMLLoader(SceneHandler.class.getResource("/" + fxmlFile)); // Aggiungi il prefisso "/"
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            Errore("Error loading FXML file: " + fxmlFile);
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private List<String> loadCSS() {
        List<String> resources = new ArrayList<>();
        for (String style : List.of( CSS_PATH+theme+ ".css" ,  CSS_PATH + "File.css")) {
            String resource = Objects.requireNonNull(SceneHandler.class.getResource(style)).toExternalForm();
            resources.add(resource);
        }
        return resources;
    }

    public void changeTheme() {
        if("DarkMode".equals(theme))
            theme = "LightMode";
        else
            theme = "DarkMode";
        changedTheme();
    }

    private void changedTheme() {
        setCSSForScene(scene);
        setCSSForAlert(alert);
    }

    private void setCSSForAlert(Alert alert) {
        Objects.requireNonNull(alert, "Alert cannot be null");
        List<String> resources = loadCSS();
        alert.getDialogPane().getStylesheets().clear();
        for(String resource : resources)
            alert.getDialogPane().getStylesheets().add(resource);
    }

    private void setCSSForScene(Scene scene) {
        Objects.requireNonNull(scene, "Scene cannot be null");
        List<String> resources = loadCSS();
        scene.getStylesheets().clear();
        for(String resource : resources)
            scene.getStylesheets().add(resource);
    }

    public void Successo(String message) {
        Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
        alert2.setHeaderText("SUCCESSO");
        alert2.setContentText(message);
        alert2.showAndWait();
    }

    public void Info(String message){
        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
        alert1.setHeaderText(" INFO ");
        alert1.setContentText(message);
        alert1.showAndWait();
    }

    public void Errore(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(" ERRORE ");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void loadHomeWindow() {
        if (this.scene != null){
            this.scene.setRoot(load("com/example/progetto_ecommerce_v3/FXMLs/Home.fxml"));
            this.stage.setMaximized(true);
        }
    }

    public void loadLoginWindow() {
        if (this.scene != null) {
            this.scene.setRoot(load("com/example/progetto_ecommerce_v3/FXMLs/Login.fxml"));
            this.stage.setMaximized(true);
            this.stage.setMinWidth(1370);
            this.stage.setMinHeight(700);
            stage.setResizable(true);
        }
    }

    public void loadCarrelloWindow() {
        if (this.scene != null) {
            this.scene.setRoot(load("com/example/progetto_ecommerce_v3/FXMLs/Carrello.fxml"));
            this.stage.setMaximized(true);
            this.stage.setMinWidth(1370);
            this.stage.setMinHeight(700);
            stage.setResizable(true);
        }
    }

    public void loadPreferitiWindow() {
        if (this.scene != null) {
            this.scene.setRoot(load("com/example/progetto_ecommerce_v3/FXMLs/Preferiti.fxml"));
            this.stage.setMaximized(true);
            this.stage.setMinWidth(1370);
            this.stage.setMinHeight(700);
            stage.setResizable(true);
        }
    }

    public void loadRegistrazioneWindow() {
        if (this.scene != null) {
            this.scene.setRoot(load("com/example/progetto_ecommerce_v3/FXMLs/Registrazione.fxml"));
            this.stage.setMaximized(true);
            this.stage.setMinWidth(1370);
            this.stage.setMinHeight(700);
            stage.setResizable(true);
        }
    }

    public void loadinfoeprivacy() {
        if (this.scene != null) {
            this.scene.setRoot(load("com/example/progetto_ecommerce_v3/FXMLs/infoeprivacy.fxml"));
            this.stage.setMaximized(true);
            this.stage.setMinWidth(1370);
            this.stage.setMinHeight(700);
            stage.setResizable(true);
        }
    }

    public void loadchisiamonoi() {
        if (this.scene != null) {
            this.scene.setRoot(load("com/example/progetto_ecommerce_v3/FXMLs/chisiamonoi.fxml"));
            this.stage.setMaximized(true);
            this.stage.setMinWidth(1370);
            this.stage.setMinHeight(700);
            stage.setResizable(true);
        }
    }

    public void loadCambiaPassword() {
        if (this.scene != null) {
            this.scene.setRoot(load("com/example/progetto_ecommerce_v3/FXMLs/CambioPassword.fxml"));
            this.stage.setMaximized(true);
            this.stage.setMinWidth(1370);
            this.stage.setMinHeight(700);
            stage.setResizable(true);
        }
    }

    public void loadSchermataUtente() {
        if (this.scene != null) {
            this.scene.setRoot(load("com/example/progetto_ecommerce_v3/FXMLs/SchermataUtente.fxml"));
            this.stage.setMaximized(true);
            this.stage.setMinWidth(1370);
            this.stage.setMinHeight(700);
            stage.setResizable(true);
        }
    }

    public void loadPagamento() {
        if (this.scene != null) {
            this.scene.setRoot(load("com/example/progetto_ecommerce_v3/FXMLs/Pagamento.fxml"));
            this.stage.setMaximized(true);
            this.stage.setMinWidth(1370);
            this.stage.setMinHeight(700);
            stage.setResizable(true);
        }
    }

    public void loadlameloBallWindow(){
        if (this.scene != null) {
            this.scene.setRoot(load("com/example/progetto_ecommerce_v3/FXMLs/lameloBall.fxml"));
            this.stage.setMaximized(true);
            this.stage.setMinWidth(1370);
            this.stage.setMinHeight(700);
            stage.setResizable(true);
        }
    }

    public void loadbadBunnyWindow(){
        if (this.scene != null) {
            this.scene.setRoot(load("com/example/progetto_ecommerce_v3/FXMLs/badBunny (2).fxml"));
            this.stage.setMaximized(true);
            this.stage.setMinWidth(1370);
            this.stage.setMinHeight(700);
            stage.setResizable(true);
        }
    }

    public void loadtravisScottWindow(){
        if (this.scene != null) {
            this.scene.setRoot(load("com/example/progetto_ecommerce_v3/FXMLs/travisScott.fxml"));
            this.stage.setMaximized(true);
            this.stage.setMinWidth(1370);
            this.stage.setMinHeight(700);
            stage.setResizable(true);
        }
    }

    public void loadoffWhiteWindow(){
        if (this.scene != null) {
            this.scene.setRoot(load("com/example/progetto_ecommerce_v3/FXMLs/offWhite.fxml"));
            this.stage.setMaximized(true);
            this.stage.setMinWidth(1370);
            this.stage.setMinHeight(700);
            stage.setResizable(true);
        }
    }

    public void loadTiffanyWindow(){
        if (this.scene != null) {
            this.scene.setRoot(load("com/example/progetto_ecommerce_v3/FXMLs/tiffany.fxml"));
            this.stage.setMaximized(true);
            this.stage.setMinWidth(1370);
            this.stage.setMinHeight(700);
            stage.setResizable(true);
        }
    }

    public void loadinfoUtilizzoWindow(){
        if (this.scene != null) {
            this.scene.setRoot(load("com/example/progetto_ecommerce_v3/FXMLs/infoUtilizzo.fxml"));
            this.stage.setMaximized(true);
            this.stage.setMinWidth(1370);
            this.stage.setMinHeight(700);
            stage.setResizable(true);
        }
    }
}