package com.example.progetto_ecommerce_v3;


import com.example.progetto_ecommerce_v3.Database.ConnessioneDatabase;
import com.example.progetto_ecommerce_v3.View.SceneHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;



public class HelloApplication extends Application {



    @Override
    public void start(Stage primaryStage) {

        //serve a creare icona dell'app
        Image icon = new Image(getClass().getResourceAsStream("/images/Logo/Logo_Stride_Style.png"));
        primaryStage.getIcons().add(icon);
        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);

        // Blocca le dimensioni minime e massime
        primaryStage.setMinWidth(1920);
        primaryStage.setMinHeight(1080);
        primaryStage.setMaxWidth(1920);
        primaryStage.setMaxHeight(1080);


        try {
            ConnessioneDatabase.getInstance().creaConnessioneDB();
            SceneHandler.getInstance().init(primaryStage);


        } catch(Exception e) {
            e.printStackTrace();
        }

        primaryStage.setOnCloseRequest(event -> {
            Platform.runLater(() -> {
                try {
                    Future<?> future = ConnessioneDatabase.getInstance().SvuotaCarrello();
                    future.get();
                    ConnessioneDatabase.getInstance().Disconnesione();
                    ConnessioneDatabase.getInstance().close();
                } catch (SQLException | InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    public static void main(String[] args) {
        launch();
    }
}