package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.AddToCartRequest;
import com.ecommerce.backend.model.*;
import com.ecommerce.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.logging.Logger;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ScarpaRepository scarpaRepository;

    public List<CartItem> getCarrelloByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        return cartItemRepository.findByUser(user);
    }

    public CartItem aggiungiAlCarrello(AddToCartRequest request) {

        // Estrazione dati
        Long userId = request.getUserId();
        Long scarpaId = request.getScarpaId();
        int quantita = request.getQuantita();
        int taglia = request.getTaglia();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        Scarpa scarpa = scarpaRepository.findById(scarpaId)
                .orElseThrow(() -> new RuntimeException("Scarpa non trovata"));

        // Controllo se esiste già User + Scarpa + Taglia
        Optional<CartItem> esistente = cartItemRepository.findByUserAndScarpaAndTaglia(user, scarpa, taglia);

        if (esistente.isPresent()) {
            // Aggiorna quantità
            CartItem item = esistente.get();
            item.setQuantita(item.getQuantita() + quantita);
            return cartItemRepository.save(item);
        } else {
            // Crea nuovo
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setScarpa(scarpa);
            newItem.setQuantita(quantita);
            newItem.setTaglia(taglia);
            return cartItemRepository.save(newItem);
        }
    }

    public void rimuoviDalCarrello(Long userId, Long scarpaId, Integer taglia) {
        Logger logger = Logger.getLogger(CartService.class.getName());
        logger.info("🔪 INIZIO RIMOZIONE -> User: " + userId + " | Scarpa: " + scarpaId + " | Taglia richiesta: " + taglia);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        List<CartItem> items = cartItemRepository.findByUser(user);

        boolean rimosso = false;

        // TENTATIVO 1: Corrispondenza Esatta (ID + Taglia)
        for (CartItem item : items) {
            boolean stessaScarpa = item.getScarpa().getId().equals(scarpaId);
            boolean stessaTaglia = item.getTaglia().equals(taglia);

            if (stessaScarpa && stessaTaglia) {
                cartItemRepository.delete(item);
                logger.info(" RIMOZIONE EFFETTUATA: Elemento ID " + item.getId() + " eliminato.");
                rimosso = true;
                break;
            }
        }

        // TENTATIVO 2: Se non trovo la taglia esatta, cerco solo la scarpa
        // Utile se frontend e backend sono disallineati sulla taglia (es. 40 vs 39)
        if (!rimosso) {
            logger.warning("Match esatto fallito. Cerco corrispondenza solo per ID Scarpa...");
            for (CartItem item : items) {
                if (item.getScarpa().getId().equals(scarpaId)) {
                    logger.info("RIMOZIONE FALLBACK: Trovata scarpa ID " + scarpaId + " con taglia diversa (" + item.getTaglia() + "). La cancello comunque!");
                    cartItemRepository.delete(item);
                    rimosso = true;
                    break;
                }
            }
        }

        if (!rimosso) {
            logger.severe("IMPOSSIBILE RIMUOVERE: La scarpa ID " + scarpaId + " non è nel carrello di questo utente.");
        }
    }
}