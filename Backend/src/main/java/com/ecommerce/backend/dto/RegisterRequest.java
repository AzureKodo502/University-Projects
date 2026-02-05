package com.ecommerce.backend.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) per la gestione della registrazione di nuovi utenti.
 * Incapsula le informazioni anagrafiche e le credenziali necessarie per creare
 * un nuovo profilo nel sistema.
 * * L'utilizzo di un DTO specifico permette di validare i dati di input separatamente
 * dalla logica di persistenza dell'entità User.
 */
@Data
public class RegisterRequest {

    /**
     * Nome del nuovo utente.
     */
    private String nome;

    /**
     * Cognome del nuovo utente.
     */
    private String cognome;

    /**
     * Indirizzo e-mail che fungerà da identificativo univoco per l'accesso.
     */
    private String email;

    /**
     * Password scelta dall'utente.
     * In fase di salvataggio nel database, questo valore dovrà essere opportunamente
     * criptato (tramite BCrypt) nel Service dedicato.
     */
    private String password;
}