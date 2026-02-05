package com.example.progetto_ecommerce_v3.daoimpl;

import com.example.progetto_ecommerce_v3.dao.ScarpeDAO;
import com.example.progetto_ecommerce_v3.Database.DBManager;
import com.example.progetto_ecommerce_v3.Model.Scarpe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScarpeDAOImpl implements ScarpeDAO {

    @Override
    public List<Scarpe> findAll() {
        return executeQuery("SELECT * FROM Scarpa");
    }

    @Override
    public List<Scarpe> findByMarca(String marca) {
        String sql = "SELECT * FROM Scarpa WHERE Marchio = ?";
        return executeQuery(sql, marca);
    }

    @Override
    public List<Scarpe> findByNome(String testo) {
        // Cerca se il nome o il marchio contengono il testo
        String sql = "SELECT * FROM Scarpa WHERE lower(Nome) LIKE ? OR lower(Marchio) LIKE ?";
        String parametroDiRicerca = "%" + testo.toLowerCase() + "%";
        return executeQuery(sql, parametroDiRicerca, parametroDiRicerca);
    }

    private List<Scarpe> executeQuery(String sql, Object... params) {
        List<Scarpe> lista = new ArrayList<>();

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Imposta i parametri nella query
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Recupero dati dal DB
                    String id = rs.getString("Id");
                    String nome = rs.getString("Nome");
                    String prezzo = rs.getString("Prezzo");
                    String descrizione = rs.getString("Descrizione");
                    String marchio = rs.getString("Marchio");
                    String taglia = rs.getString("Taglia");
                    String modello = rs.getString("Modello");

                    // Creazione oggetto Scarpe
                    Scarpe s = new Scarpe(id, nome, prezzo, descrizione, marchio, taglia, modello);
                    lista.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}