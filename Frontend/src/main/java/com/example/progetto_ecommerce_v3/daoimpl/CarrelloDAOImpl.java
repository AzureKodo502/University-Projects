package com.example.progetto_ecommerce_v3.daoimpl;

import com.example.progetto_ecommerce_v3.dao.CarrelloDAO;
import com.example.progetto_ecommerce_v3.Database.DBManager;
import com.example.progetto_ecommerce_v3.Model.Scarpe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarrelloDAOImpl implements CarrelloDAO {

    @Override
    public List<String> findIdsByUtente(String email) {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT Id_Scarpa FROM Carrello WHERE Id_Cliente = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) ids.add(rs.getString("Id_Scarpa"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ids;
    }

    @Override
    public boolean aggiungi(String email, String idScarpa, int quantita, int taglia) {
        String sql = "INSERT INTO Carrello (Id_Cliente, Id_Scarpa, Quantita, Taglia) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, idScarpa);
            pstmt.setInt(3, quantita);
            pstmt.setInt(4, taglia);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public void aggiornaTaglia(String idScarpa, int nuovaTaglia) {
        String sql = "UPDATE Scarpa SET Taglia = ? WHERE Id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nuovaTaglia);
            pstmt.setString(2, idScarpa);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<Scarpe> getCarrelloCompleto(String email) {
        List<Scarpe> lista = new ArrayList<>();
        String sql = "SELECT s.*, c.Taglia AS TagliaSelezionata " +
                "FROM Scarpa s " +
                "JOIN Carrello c ON s.Id = c.Id_Scarpa " +
                "WHERE c.Id_Cliente = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Creiamo l'oggetto Scarpe inserendo la taglia scelta dall'utente
                    Scarpe s = new Scarpe(
                            rs.getString("Id"),
                            rs.getString("Nome"),
                            rs.getString("Prezzo"),
                            rs.getString("Descrizione"),
                            rs.getString("Marchio"),
                            rs.getString("TagliaSelezionata"),
                            rs.getString("Modello")
                    );
                    lista.add(s);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    @Override
    public void rimuovi(String email, String idScarpa, String taglia) {
        String sql = "DELETE FROM Carrello WHERE Id_Cliente = ? AND Id_Scarpa = ? AND Taglia = ?";

        // Usa il try-with-resources per chiudere AUTOMATICAMENTE la connessione
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, idScarpa);
            pstmt.setString(3, taglia);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}