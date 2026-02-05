package com.example.progetto_ecommerce_v3.Database;

import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {

    // Usiamo la stringa di connessione per SQLite (file locale)
    private static final String URL = "jdbc:sqlite:DatabaseProgetto.db";

    static {
        try {
            // Carichiamo il driver per SQLite
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore: Driver SQLite non trovato! Controlla le dipendenze Maven.");
        }
    }

    // In DBManager.java

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            config.setBusyTimeout(30000);
            config.setJournalMode(SQLiteConfig.JournalMode.TRUNCATE);
            config.enforceForeignKeys(true);

            return DriverManager.getConnection("jdbc:sqlite:DatabaseProgetto.db", config.toProperties());

        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver SQLite non trovato", e);
        }
    }
}