package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.Scarpa;
import com.ecommerce.backend.service.ScarpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ScarpaController {

    @Autowired
    private ScarpaService scarpaService;

    // GET http://localhost:8080/api/products
    @GetMapping
    public List<Scarpa> getListaScarpe() {
        return scarpaService.getAllScarpe();
    }

    // POST http://localhost:8080/api/products
    @PostMapping
    public Scarpa aggiungiScarpa(@RequestBody Scarpa scarpa) {
        return scarpaService.salvaScarpa(scarpa);
    }

    // GET http://localhost:8080/api/products/search?q=nike
    @GetMapping("/search")
    public List<Scarpa> ricerca(@RequestParam String q) {
        return scarpaService.cercaScarpe(q);
    }

    // GET http://localhost:8080/api/products/brand/Nike
    @GetMapping("/brand/{marchio}")
    public List<Scarpa> filtraPerMarchio(@PathVariable String marchio) {
        return scarpaService.getScarpeByMarchio(marchio);
    }
}