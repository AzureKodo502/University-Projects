package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.AddToCartRequest;
import com.ecommerce.backend.model.*;
import com.ecommerce.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.logging.Logger;

import java.util.List;
import java.util.Optional;

/**
 * Servizio core per la gestione del carrello acquisti.
 * Implementa la logica di business per l'aggiunta incrementale di prodotti,
 * il recupero degli articoli per utente e strategie di rimozione.
 */
@Service
public class CartService {

    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ScarpaRepository scarpaRepository;

    /**
     * Recupera il contenuto del carrello per un determinato utente.
     * @param userId ID dell'utente proprietario del carrello.
     * @return Lista di {@link CartItem} associati all'utente.
     * @throws RuntimeException se l'utente non viene trovato nel database.
     */
    public List<CartItem> getCarrelloByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        return cartItemRepository.findByUser(user);
    }

    /**
     * Gestisce l'aggiunta di un prodotto al carrello.
     * Se l'articolo (stessa scarpa e taglia) esiste già,
     * ne incrementa la quantità; altrimenti, crea una nuova voce.
     * @param request DTO contenente i dettagli dell'articolo da aggiungere.
     * @return L'entità {@link CartItem} creata o aggiornata.
     */
    public CartItem aggiungiAlCarrello(AddToCartRequest request) {
        Long userId = request.getUserId();
        Long scarpaId = request.getScarpaId();
        int quantita = request.getQuantita();
        int taglia = request.getTaglia();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        Scarpa scarpa = scarpaRepository.findById(scarpaId)
                .orElseThrow(() -> new RuntimeException("Scarpa non trovata"));

        // Ricerca di un elemento pre-esistente con le medesime caratteristiche (Modello + Taglia)
        Optional<CartItem> esistente = cartItemRepository.findByUserAndScarpaAndTaglia(user, scarpa, taglia);

        if (esistente.isPresent()) {
            CartItem item = esistente.get();
            item.setQuantita(item.getQuantita() + quantita);
            return cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setScarpa(scarpa);
            newItem.setQuantita(quantita);
            newItem.setTaglia(taglia);
            return cartItemRepository.save(newItem);
        }
    }

    /**
     * Rimuove un articolo dal carrello applicando una logica di ricerca a due livelli.
     * Tenta prima la rimozione per corrispondenza esatta (ID scarpa e taglia).
     * In caso di fallimento, applica una politica di fallback rimuovendo la prima scarpa
     * con ID corrispondente a prescindere dalla taglia, per garantire la coerenza della UI.
     * Ho Utilizzato questo approccio perché mi inbattevo in disallineamenti tra Backend e Frontend
     * (es. A volte travava ID Esatto + Taglia Esatta, altre volte ID Esatto + Taglia Errata)
     * @param userId ID dell'utente.
     * @param scarpaId ID della scarpa da rimuovere.
     * @param taglia Taglia selezionata dall'utente.
     */
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

        // TENTATIVO 2: Logica di Fallback
        if (!rimosso) {
            logger.warning("Match esatto fallito. Cerco corrispondenza solo per ID Scarpa...");
            for (CartItem item : items) {
                if (item.getScarpa().getId().equals(scarpaId)) {
                    logger.info("RIMOZIONE FALLBACK: Trovata scarpa ID " + scarpaId + " con taglia diversa. Eliminazione forzata.");
                    cartItemRepository.delete(item);
                    rimosso = true;
                    break;
                }
            }
        }

        if (!rimosso) {
            logger.severe("IMPOSSIBILE RIMUOVERE: Prodotto non presente nel carrello.");
        }
    }
}