package com.ecommerce.backend.service;

import com.ecommerce.backend.model.*;
import com.ecommerce.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servizio deputato alla gestione del checkout e della cronologia acquisti.
 * Coordina la transizione dei dati dal carrello all'ordine definitivo
 * garantendo la coerenza finanziaria e l'integrità del database.
 */
@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private UserRepository userRepository;

    /**
     * Esegue il processo di checkout completo per un utente.
     * Calcola il totale, genera la testata dell'ordine, sposta gli articoli del carrello
     * nei dettagli dell'ordine (OrderItem) e infine pulisce il carrello.
     * * @param userId Identificativo dell'utente acquirente.
     * @return L'oggetto {@link Order} creato e persistito.
     * @throws RuntimeException se l'utente non esiste o se il carrello risulta vuoto.
     */
    @Transactional
    public Order creaOrdine(Long userId) {
        // Recupero dell'utente per l'associazione dell'ordine
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Recupero del carrello corrente. Se vuoto, l'operazione viene interrotta.
        List<CartItem> carrello = cartItemRepository.findByUser(user);
        if (carrello.isEmpty()) {
            throw new RuntimeException("Il carrello è vuoto!");
        }

        // Calcolo dinamico del totale dell'ordine basato sui prezzi correnti del catalogo
        double totale = 0;
        for (CartItem item : carrello) {
            totale += item.getScarpa().getPrezzo() * item.getQuantita();
        }

        // Creazione e salvataggio della "testata" dell'ordine
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setDataCreazione(LocalDateTime.now());
        newOrder.setStato("CONFERMATO");
        newOrder.setTotale(totale);
        Order ordineSalvato = orderRepository.save(newOrder);

        // Migrazione dei dati: trasformazione di ogni CartItem in un OrderItem persistente.
        // In questa fase viene congelato il prezzo di vendita attuale.
        for (CartItem cartItem : carrello) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(ordineSalvato);
            orderItem.setScarpa(cartItem.getScarpa());
            orderItem.setQuantita(cartItem.getQuantita());
            orderItem.setPrezzoAlMomentoDellAcquisto(cartItem.getScarpa().getPrezzo());

            orderItemRepository.save(orderItem);
        }

        // Operazione finale: svuotamento del carrello dell'utente
        cartItemRepository.deleteAll(carrello);

        return ordineSalvato;
    }

    /**
     * Recupera lo storico degli ordini effettuati da un utente.
     * @param userId ID dell'utente.
     * @return Lista di ordini associati.
     */
    public List<Order> getOrdiniUtente(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return orderRepository.findByUser(user);
    }
}