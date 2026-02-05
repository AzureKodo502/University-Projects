package com.example.progetto_ecommerce_v3.daoimpl;

import com.example.progetto_ecommerce_v3.dao.UtenteDAO;
import com.example.progetto_ecommerce_v3.Database.DBManager;
import com.example.progetto_ecommerce_v3.Model.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtenteDAOImpl implements UtenteDAO {

    @Override
    public Utente login(String email) {
        String sql = "SELECT * FROM Utenti WHERE Email = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Utente(
                            rs.getString("Nome"),
                            rs.getString("Cognome"),
                            rs.getString("Email")
                    );
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public String getPasswordHash(String email) {
        String sql = "SELECT Password FROM Utenti WHERE Email = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("Password");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean esisteEmail(String email) {
        String sql = "SELECT 1 FROM Utenti WHERE Email = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // True se c'è un email già esistente
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean registra(String nome, String cognome, String email, String passwordHash) {
        String sql = "INSERT INTO Utenti (Nome, Cognome, Email, Password) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            pstmt.setString(2, cognome);
            pstmt.setString(3, email);
            pstmt.setString(4, passwordHash);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}