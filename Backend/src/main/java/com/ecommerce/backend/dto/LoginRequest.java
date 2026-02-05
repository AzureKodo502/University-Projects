package com.ecommerce.backend.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) dedicato alla cattura delle credenziali utente.
 * Viene utilizzato durante la fase di autenticazione per veicolare l'email
 * e la password dal client verso il server in modo strutturato.
 */
@Data
public class LoginRequest {

    /**
     * Identificativo univoco dell'utente (indirizzo e-mail).
     */
    private String email;

    /**
     * Credenziale di accesso.
     * In un ambiente di produzione, questo valore viene trasportato
     * su protocollo HTTPS per garantirne la riservatezza.
     */
    private String password;
}