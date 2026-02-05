package com.example.progetto_ecommerce_v3.Database;

import com.example.progetto_ecommerce_v3.View.SceneHandler;
import javafx.application.Platform;
import com.example.progetto_ecommerce_v3.Message;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Convalida {

    private static Convalida instance = null;

    private Convalida(){}

    public static Convalida getInstance(){
        if (instance == null){
            instance = new Convalida();
        }
        return instance;
    }

    public CompletableFuture<Boolean> ControlloRegistrazione(String Nome, String Cognome, String Email, String Password, String RipetiPassword) throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        if (Nome.isEmpty() || Cognome.isEmpty() || Email.isEmpty() || Password.isEmpty() || RipetiPassword.isEmpty()){
            SceneHandler.getInstance().Info(Message.Alcuni_Campi_Vuoti);
            return CompletableFuture.completedFuture(false);
        }
        if (!Email.contains("@")){
            SceneHandler.getInstance().Errore(Message.Errore_Durante_La_registrazione_Mail);
            return CompletableFuture.completedFuture(false);
        }
        if (!Password.equals(RipetiPassword)){
            SceneHandler.getInstance().Errore(Message.Errore_Durante_La_registrazione_PasswordNE);
            return CompletableFuture.completedFuture(false);
        }
        if (Password.length() < 6 ){
            SceneHandler.getInstance().Errore(Message.Errore_Durante_La_registrazione_Password);
            return CompletableFuture.completedFuture(false);
        }
        CompletableFuture<Boolean> future = ConnessioneDatabase.getInstance().VerificaSeEsisteEmail(Email);
        Boolean valid = future.get(7, TimeUnit.SECONDS);
        if (valid){
            Platform.runLater(() -> SceneHandler.getInstance().Info(Message.Email_Esistente));
            CompletableFuture.completedFuture(false);
        }
        return CompletableFuture.completedFuture(true);
    }

    // metodo che verifica sia che la password sia uguale alla password ripetuta sia che la lunghezza sia maggiore di 6
    public boolean VerificaPassword(String Password, String RipetiPassword){
        if (!Password.equals(RipetiPassword)){
            SceneHandler.getInstance().Errore(Message.Errore_Durante_La_registrazione_PasswordNE);
            return false;
        }
        if (Password.length() < 6 ){
            SceneHandler.getInstance().Errore(Message.Errore_Durante_La_registrazione_Password);
            return false;
        }
        return true;
    }
}

