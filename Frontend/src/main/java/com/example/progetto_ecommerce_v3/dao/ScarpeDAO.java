package com.example.progetto_ecommerce_v3.dao;

import com.example.progetto_ecommerce_v3.Model.Scarpe;
import java.util.List;

public interface ScarpeDAO {
    List<Scarpe> findAll();
    List<Scarpe> findByMarca(String marca);
    List<Scarpe> findByNome(String testo);
}