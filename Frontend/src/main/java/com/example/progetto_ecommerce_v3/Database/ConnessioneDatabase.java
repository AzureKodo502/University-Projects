package com.example.progetto_ecommerce_v3.Database;

import com.example.progetto_ecommerce_v3.Message;
import com.example.progetto_ecommerce_v3.Model.Scarpe;
import com.example.progetto_ecommerce_v3.Model.Utente;
import com.example.progetto_ecommerce_v3.View.SceneHandler;
import javafx.application.Platform;
import javafx.scene.control.Label;
import org.springframework.security.crypto.bcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;
import static java.sql.DriverManager.getConnection;
import java.util.logging.Logger;
import java.util.logging.Level;


public class ConnessioneDatabase {
    private static ConnessioneDatabase instance = null;

    private Connection connessione = null;

    private ArrayList<Scarpe> CategorieScarpe;
    private ArrayList<Scarpe> ScarpeSimili;
    private ArrayList<Scarpe> RicercaScarpe = new ArrayList<>();

    private Label Risultati;

    private Scarpe scarpe;

    private final ExecutorService service = Executors.newCachedThreadPool();

    private ConnessioneDatabase(){}

    private static final Logger logger = Logger.getLogger(ConnessioneDatabase.class.getName());


    public static ConnessioneDatabase getInstance(){
        if (instance == null) {
            instance = new ConnessioneDatabase();
        }
        return instance;
    }

    public void creaConnessioneDB() throws SQLException{
        String url = "jdbc:sqlite:DatabaseProgetto.db";
        connessione = DBManager.getConnection();
        if (connessione != null && !connessione.isClosed()){
            System.out.println("Connesso!");
        }
    }

    public void Disconnesione() throws SQLException{
        if (connessione != null){
            connessione.close();
        }
        connessione = null;
    }

    public void close(){
        service.shutdownNow();
    }

    public void InserisciUtenti(String Nome, String Cognome, String Email, String Password) {
        service.submit(createDaemonThread(() -> {
            try {
                if (connessione == null || connessione.isClosed()) {
                    System.err.println("Errore: Connessione al database non valida o chiusa.");
                    return;
                }

                // Controlla che i parametri non siano nulli o vuoti
                if (Nome == null || Nome.isEmpty() ||
                        Cognome == null || Cognome.isEmpty() ||
                        Email == null || Email.isEmpty() ||
                        Password == null || Password.isEmpty()) {
                    System.err.println("Errore: Parametri nulli o vuoti.");
                    return;
                }

                // Prepara la query di inserimento
                String query = "INSERT INTO Utenti (Nome, Cognome, Email, Password) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = connessione.prepareStatement(query);
                stmt.setString(1, Nome);
                stmt.setString(2, Cognome);
                stmt.setString(3, Email);
                stmt.setString(4, Password);

                // Esegui l'operazione di inserimento dei dati
                stmt.executeUpdate();

                // Committa la transazione
                connessione.commit();

                // Chiudi la dichiarazione
                stmt.close();
            } catch (SQLException e) {
                // Gestisci l'eccezione stampando l'errore
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }));
    }

    private Thread createDaemonThread(Runnable runnable) {
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        return t;
    }

    public Future<?> VerificaLogin(String Email, String Password) throws SQLException {
        return service.submit(createDaemonThread(() -> {
            try {
                if (connessione == null || connessione.isClosed())
                    return;
                String query = "select * from Utenti where Email=?";
                PreparedStatement stmt = connessione.prepareStatement(query);
                stmt.setString(1, Email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    boolean check = BCrypt.checkpw(Password, rs.getString("password"));
                    if (check)
                        System.out.println("Password CONVALIDATA");
                    else
                        throw new SQLException();
                } else {
                    throw new SQLException();
                }
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public CompletableFuture<Utente> setUtente(String Email) {
        CompletableFuture<Utente> future = new CompletableFuture<>();
        service.submit(createDaemonThread(() -> {
            try {
                Utente utente;
                if (connessione == null || connessione.isClosed())
                    future.complete(null);
                String query = "select * from Utenti where Email=?;";
                PreparedStatement stmt = connessione.prepareStatement(query);
                stmt.setString(1, Email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    utente = new Utente(rs.getString("Nome"), rs.getString("Cognome"), rs.getString("Email"));
                    future.complete(utente);
                }
                stmt.close();
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        }));
        return future;
    }

    public String PasswordCriptografata(String password) {
        String generatedSecuredPasswordHash = BCrypt.hashpw(password, BCrypt.gensalt(12));
        return generatedSecuredPasswordHash;
    }

    public CompletableFuture<ArrayList<Scarpe>> AggiungiAllaPaginaHomeScarpe() {
        CompletableFuture<ArrayList<Scarpe>> future = new CompletableFuture<>();
        service.submit(createDaemonThread(() -> {
            try {
                if (this.connessione != null && !this.connessione.isClosed()) {
                    String query = "SELECT * FROM Scarpa";
                    PreparedStatement stmt = this.connessione.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();

                    ArrayList<Scarpe> scarpeList = new ArrayList<>();
                    while (rs.next()) {
                        Scarpe scarpe = new Scarpe(
                                rs.getString("Id"),
                                rs.getString("Nome"),
                                rs.getString("Prezzo"),
                                rs.getString("Descrizione"),
                                rs.getString("Marchio"),
                                rs.getString("Taglia"),
                                rs.getString("Modello")
                        );
                        scarpeList.add(scarpe);
                    }
                    future.complete(scarpeList);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        }));
        return future;
    }

    public void AggiungiScarpePerMarchio(String Marchio) {
        service.submit(createDaemonThread(() -> {
            try {
                CategorieScarpe = new ArrayList<>();
                if (this.connessione != null && !this.connessione.isClosed()) {
                    String query = "select * from Scarpa where Marchio=?;";
                    PreparedStatement stmt = this.connessione.prepareStatement(query);
                    stmt.setString(1, Marchio);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        Scarpe p = new Scarpe(rs.getString("Id"), rs.getString("Nome"), rs.getString("Prezzo"), rs.getString("Descrizione"), rs.getString("Marchio"), rs.getString("Taglia"), rs.getString("Modello"));
                        CategorieScarpe.add(p);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public ArrayList<Scarpe> getAggiungiScarpePerMarchio() {
        return CategorieScarpe;
    }

    public void SvuotaListaScarpe() {
        if (CategorieScarpe != null) {
            CategorieScarpe.clear();
        }
    }


    public CompletableFuture<Boolean> VerificaSeEsisteEmail(String Email) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        service.submit(createDaemonThread(() -> {
            try {
                if (this.connessione != null && !this.connessione.isClosed()) {
                    String query = "SELECT * from Utenti WHERE Email =?;";
                    PreparedStatement stmt = this.connessione.prepareStatement(query);
                    stmt.setString(1, Email);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        future.complete(true);
                    } else {
                        future.complete(false);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
        return future;
    }

    public CompletableFuture<Scarpe> getScarpa(String Id) {
        CompletableFuture<Scarpe> future = new CompletableFuture<>();
        service.submit(createDaemonThread(() -> {
            try {
                if (this.connessione != null && !this.connessione.isClosed()) {
                    String query = "select * from Scarpa where Id=?;";
                    PreparedStatement stmt = this.connessione.prepareStatement(query);
                    stmt.setString(1, Id);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        scarpe = new Scarpe(rs.getString("Id"), rs.getString("Nome"), rs.getString("Prezzo"), rs.getString("Descrizione"), rs.getString("Marchio"), rs.getString("Taglia"), rs.getString("Modello"));
                        future.complete(scarpe);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
        return future;
    }

    public CompletableFuture<Void> AggiungiScarpeSimili(String Id, String Marchio) {
        return CompletableFuture.runAsync(() -> {
            try {
                System.out.println("Inizio caricamento scarpe simili per Id: " + Id + " e Marchio: " + Marchio);

                // Estrai solo la parte principale del marchio (la prima parola)
                String principaleMarchio = Marchio.split(" ")[0];
                System.out.println("Utilizzo del marchio principale: " + principaleMarchio);

                ScarpeSimili = new ArrayList<>();
                if (this.connessione != null && !this.connessione.isClosed()) {
                    String query = "select * from Scarpa where Marchio LIKE ?;";
                    PreparedStatement stmt = this.connessione.prepareStatement(query);
                    stmt.setString(1, "%" + principaleMarchio + "%");
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next() && ScarpeSimili.size() < 5) {
                        if (!rs.getString("Id").equals(Id)) {
                            Scarpe scarpe = new Scarpe(rs.getString("Id"), rs.getString("Nome"), rs.getString("Prezzo"), rs.getString("Descrizione"), rs.getString("Marchio"), rs.getString("Taglia"), rs.getString("Modello"));
                            ScarpeSimili.add(scarpe);
                            System.out.println("Aggiunta scarpa simile: " + scarpe.Id());
                        }
                    }
                }
                System.out.println("Caricamento scarpe simili completato. Numero di scarpe simili trovate: " + ScarpeSimili.size());
            } catch (SQLException e) {
                System.err.println("Errore durante il caricamento delle scarpe simili: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    public ArrayList<Scarpe> getScarpeSimili() {
        return ScarpeSimili;
    }

    public void inserisciProdottiNelCarrello(String Email, String Id_Scarpa, int Quantita, int Taglia) throws SQLException {
        service.submit(createDaemonThread(() -> {
            try {
                // Verifica preliminare della connessione
                if (connessione == null || connessione.isClosed())
                    return;

                // Ottenere il carrello in modo asincrono
                CompletableFuture<ArrayList<String>> future = getCarrello(Email, "Carrello");
                ArrayList<String> nProd = future.get(7, TimeUnit.SECONDS);

                // Controllo del numero di prodotti nel carrello
                if (nProd.size() < 6) {
                    // Preparazione della query per inserire il prodotto nel carrello
                    String insertQuery = "INSERT INTO Carrello  VALUES (?, ?, ?, ?, ?, ?);";
                    try (PreparedStatement stmt = connessione.prepareStatement(insertQuery)) {
                        stmt.setString(1, Email);
                        stmt.setString(2, Id_Scarpa);
                        stmt.setInt(3, Quantita);
                        stmt.setInt(4, 0);
                        stmt.setInt(5, Taglia);
                        stmt.setString(6, "Carrello");

                        stmt.executeUpdate();
                    }
                } else {
                    // Mostra un avviso se ci sono già 6 o più prodotti nel carrello
                    SceneHandler.getInstance().Info(Message.Aggiungi_Al_Carrello_INFO);
                }
            } catch (SQLException | ExecutionException | InterruptedException | TimeoutException e) {
                // Gestione delle eccezioni e completamento eccezionale del future
                throw new RuntimeException(e);
            }
        }));
    }


    public CompletableFuture<Integer> QuantitaProdotto(String email, String idScarpa, String tipo) throws SQLException {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        service.submit(createDaemonThread(() -> {
            try {
                int quantita = 1;
                if (this.connessione != null && !this.connessione.isClosed()) {
                    String query = "SELECT Quantita FROM Carrello WHERE Id_Cliente=? AND Id_Scarpa=? AND Tipo=?";
                    PreparedStatement stmt = this.connessione.prepareStatement(query);
                    stmt.setString(1, email);
                    stmt.setString(2, idScarpa);
                    stmt.setString(3, tipo);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        quantita = rs.getInt("Quantita");
                    }
                    stmt.close();
                }
                future.complete(quantita);
            } catch (SQLException e) {
                future.completeExceptionally(e);
                throw new RuntimeException(e);
            }
        }));
        return future;
    }

    public void AggiornamentoQuanditaScarpa(String Email, String Id_Scarpa, Object Quantita, String Tipo) throws SQLException {
        service.submit(createDaemonThread(() -> {
            try {
                if (this.connessione != null && !this.connessione.isClosed()) {
                    String query = "update Carrello set Quantita = ? where Id_Cliente=? AND Id_SCarpa=? AND Tipo=?;";
                    PreparedStatement stmt = this.connessione.prepareStatement(query);
                    stmt.setInt(1, (Integer) Quantita);
                    stmt.setString(2, Email);
                    stmt.setString(3, Id_Scarpa);
                    stmt.setString(4, Tipo);
                    stmt.execute();
                    stmt.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public Future<?> SvuotaCarrello() throws SQLException {
        return service.submit(createDaemonThread(() -> {
            try {
                if (connessione == null || connessione.isClosed())
                    return;
                String query = "DELETE FROM Carrello where Id_Cliente=?;";
                PreparedStatement stmt = connessione.prepareStatement(query);
                stmt.setString(1, "Null");
                stmt.execute();
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public CompletableFuture<Label> CercaScarpe(String searchText) {
        CompletableFuture<Label> future = new CompletableFuture<>();
        service.submit(createDaemonThread(() -> {
            try {
                Risultati = new Label();
                if (this.connessione != null && !this.connessione.isClosed()) {
                    String query = "SELECT * FROM Scarpa WHERE Filtro LIKE ?;";
                    PreparedStatement stmt = connessione.prepareStatement(query);
                    stmt.setString(1, "%" + searchText + "%");

                    ResultSet resultSet = stmt.executeQuery();
                    StringBuilder resultBuilder = new StringBuilder();
                    while (resultSet.next()) {
                        String columnValue = resultSet.getString("Id");
                        resultBuilder.append(columnValue + ";");
                    }

                    Risultati.setText(resultBuilder.toString());
                    future.complete(Risultati);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
        return future;
    }

    public void addSearchedProducts(String[] products) {
        service.submit(createDaemonThread(() -> {
            try {
                if (this.connessione != null && !this.connessione.isClosed()) {
                    for (String id : products) {
                        String query = "select * from Scarpa where Id= ?;";
                        PreparedStatement stmt = this.connessione.prepareStatement(query);
                        stmt.setString(1, id);

                        ResultSet rs = stmt.executeQuery();

                        while (rs.next()) {
                            Scarpe  p = new Scarpe(rs.getString("Id"), rs.getString("Nome"), rs.getString("Prezzo"), rs.getString("Descrizione"), rs.getString("Marchio"), rs.getString("Taglia"), rs.getString("Modello"));
                            RicercaScarpe.add(p);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }
    public ArrayList<Scarpe> getRicercaScarpe() {
        return RicercaScarpe;
    }

    public void SvuotaLaListaDiRicerca() {
        if (RicercaScarpe != null) {
            RicercaScarpe.clear();
        }
    }

    public CompletableFuture<ArrayList<String>> getCarrello(String email, String Tipo) {
        CompletableFuture<ArrayList<String>> future = new CompletableFuture<>();
        service.submit(createDaemonThread(() -> {
            try {
                logger.log(Level.INFO, "Inizio recupero carrello per email: {0} e tipo: {1}", new Object[]{email, "Carrello"});
                ArrayList<String> id_scarpa = new ArrayList<>();

                if (connessione == null || connessione.isClosed()) {
                    logger.log(Level.WARNING, "Connessione al database non disponibile o chiusa");
                    future.complete(id_scarpa);
                    return;
                }

                String query;
                PreparedStatement stmt;
                if (email != null) {
                    query = "SELECT Id_Scarpa FROM Carrello WHERE Tipo = 'Carrello' AND Id_Cliente = ?";
                    stmt = connessione.prepareStatement(query);
                    stmt.setString(1, email);
                } else {
                    query = "SELECT Id_Scarpa FROM Carrello WHERE Tipo = 'Carrello' AND Id_Cliente = 'null'";
                    stmt = connessione.prepareStatement(query);
                }

                logger.log(Level.INFO, "Esecuzione query: {0} con parametri email: {1}, tipo: Carrello", new Object[]{query, email});
                ResultSet rs = stmt.executeQuery();

                if (!rs.isBeforeFirst()) {
                    logger.log(Level.INFO, "Nessun risultato trovato per email: {0} e tipo: Carrello", new Object[]{email});
                }

                while (rs.next()) {
                    id_scarpa.add(rs.getString("Id_Scarpa")); // Assicurati di utilizzare il nome corretto della colonna
                    logger.log(Level.INFO, "Scarpa aggiunta al carrello: {0}", rs.getString("Id_Scarpa"));
                }
                future.complete(id_scarpa);
                logger.log(Level.INFO, "Recupero carrello completato con successo");
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Errore durante il recupero del carrello", e);
                future.completeExceptionally(e);
            }
        }));
        return future;
    }

    public ArrayList getottieniInfScarpeNelCarrello(ArrayList<String> id_scarpa) throws ExecutionException, InterruptedException, TimeoutException {
        ArrayList<Scarpe> SCARPA = new ArrayList<>();
        for (String id : id_scarpa) {
            CompletableFuture<Scarpe> future = getScarpa(id);
            Scarpe scarpe = future.get(7, TimeUnit.SECONDS); // Attendere il completamento del CompletableFuture
            SCARPA.add(scarpe);
        }
        System.out.println("Informazioni sulle scarpe nel carrello ottenute con successo.");
        return SCARPA;
    }

    public void RimuoviDalCarrelloLaScarpa(String Id, String Email) {
        service.submit(createDaemonThread(() -> {
            try {
                if (connessione == null || connessione.isClosed())
                    return;
                String query = "DELETE FROM Carrello where Id_Cliente=? and Id_Scarpa = ?;";
                PreparedStatement stmt = connessione.prepareStatement(query);
                stmt.setString(1, Email);
                stmt.setString(2, Id);

                stmt.execute();
                stmt.close();
                System.out.println("Scarpa rimossa dal carrello con successo: " + Id + " per l'utente: " + Email);
            } catch (SQLException e) {
                System.err.println("Errore durante la rimozione della scarpa dal carrello: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }));
    }

    public double ControlloPrezzoTotale(String email, String tipo) throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<ArrayList<String>> future = getCarrello(email, tipo);
        ArrayList<String> id_scarpa = future.get(7, TimeUnit.SECONDS);
        ArrayList<Scarpe> scarpe = getottieniInfScarpeNelCarrello(id_scarpa);
        double totalPriceQuantity = 0.00;

        for (int i = 0; i < id_scarpa.size(); i++) {
            CompletableFuture<Integer> future1 = QuantitaProdotto(email, id_scarpa.get(i), tipo);
            Integer q = future1.get(7, TimeUnit.SECONDS);
            String Prezzo = scarpe.get(i).Prezzo();
            Prezzo = Prezzo.replace(",", ".");  // Usa il punto come separatore decimale
            double tmp = q * Double.parseDouble(Prezzo);
            totalPriceQuantity += tmp;

            // Log per ogni scarpa
            System.out.println("ID Scarpe: " + id_scarpa.get(i) + ", Quantità: " + q + ", Prezzo: " + Prezzo + ", Subtotale: " + tmp);
        }

        System.out.println("Prezzo totale del carrello controllato con successo per l'utente: " + email + " e tipo: " + tipo);
        return totalPriceQuantity;
    }

    public String getPrezzoTotaleCarrello(String email, String tipo) throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        double tmpTotalCartPrice = ControlloPrezzoTotale(email, tipo);
        String str = String.format("%.2f", tmpTotalCartPrice).replace(".", ",");  // Format per la visualizzazione con virgola
        System.out.println("Prezzo totale del carrello ottenuto con successo per l'utente: " + email + " e tipo: " + tipo + ", Prezzo: " + str);
        return str;
    }


    public Future<?> updateNullProduct(String Email) {
        return service.submit(createDaemonThread(() -> {
            try {
                CompletableFuture<ArrayList<String>> future = getCarrello("null", "Carrello");
                ArrayList<String> id_scarpa = future.get(7, TimeUnit.SECONDS);

                for (String Id : id_scarpa) {
                    // Recupera quantità e taglia separatamente
                    CompletableFuture<Integer> futureQuantita = QuantitaProdotto("null", Id, "Carrello");
                    Integer quantita = futureQuantita.get(7, TimeUnit.SECONDS);

                    CompletableFuture<String> futureTaglia = getTagliaScarpaDalCarrello("null", Id);
                    String tagliaStr = futureTaglia.get(7, TimeUnit.SECONDS);

                    // Converti la taglia da String a Integer, gestendo eventuali errori di conversione
                    Integer taglia;
                    try {
                        taglia = Integer.parseInt(tagliaStr);
                    } catch (NumberFormatException e) {
                        taglia = 1; // Valore di fallback in caso di errore
                    }

                    // Inserisci il prodotto nel carrello con quantità e taglia corretti
                    inserisciProdottiNelCarrello(Email, Id, quantita, taglia);
                }

                if (this.connessione != null && !this.connessione.isClosed()) {
                    String query = "DELETE FROM Carrello WHERE Id_Cliente = ?;";
                    PreparedStatement stmt = this.connessione.prepareStatement(query);
                    stmt.setString(1, "null");
                    stmt.execute();
                    stmt.close();
                }
            } catch (SQLException | ExecutionException | InterruptedException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public CompletableFuture<ArrayList<Scarpe>> getWishlist(String Email) {
        CompletableFuture<ArrayList<Scarpe>> future = new CompletableFuture<>();
        service.submit(createDaemonThread(() -> {
            try {
                ArrayList<Scarpe> SCARPA = new ArrayList<>();
                if (connessione == null || connessione.isClosed())
                    future.complete(SCARPA);
                String query = "SELECT * from Wishlist where Id_Utente=?";
                PreparedStatement stmt = connessione.prepareStatement(query);
                stmt.setString(1, Email);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    CompletableFuture<Scarpe> future1 = getScarpa(rs.getString(2));
                    Scarpe p = future1.get(7, TimeUnit.SECONDS);
                    SCARPA.add(p);
                }
                future.complete(SCARPA);
            } catch (SQLException | ExecutionException | InterruptedException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }));
        return future;
    }

    public void insertWishlistProductIntoDB(String Email, String Id_Scarpa) {
        service.submit(createDaemonThread(() -> {
            try {
                CompletableFuture<ArrayList<Scarpe>> future = getWishlist(Email);
                ArrayList<Scarpe> nSCARPE = future.get(7, TimeUnit.SECONDS);
                boolean find = false;
                for (Scarpe Id : nSCARPE) {
                    if (Id.Id().equals(Id_Scarpa)) {
                        find = true;
                    }
                }
                if (nSCARPE.size() < 6 && !find) {
                    if (connessione == null || connessione.isClosed())
                        return;
                    PreparedStatement stmt = connessione.prepareStatement("INSERT INTO Wishlist VALUES(?, ?);");
                    stmt.setString(1, Email);
                    stmt.setString(2, Id_Scarpa);
                    stmt.execute();
                    stmt.close();
                }

                if (nSCARPE.size() >= 6) {
                    Platform.runLater(() -> SceneHandler.getInstance().Info(Message.Capienza_Massima_Wishlist));

                }
                if (find) {
                    Platform.runLater(() -> SceneHandler.getInstance().Info(Message.Gia_Presente_Nella_wishlist));
                }
            } catch (SQLException | ExecutionException | InterruptedException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }));
    }



    public void RimuoviDaiPreferiti(String Id, String Email) {
        service.submit(createDaemonThread(() -> {
            try {
                if (connessione == null || connessione.isClosed())
                    return;
                String query = "DELETE FROM Wishlist where Id_Utente=? and Id_Scarpa = ?;";
                PreparedStatement stmt = connessione.prepareStatement(query);
                stmt.setString(1, Email);
                stmt.setString(2, Id);
                stmt.execute();
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public void AggiornamentoPassword(String Email, String Password) {
        service.submit(createDaemonThread(() -> {
            try {
                if (this.connessione != null && !this.connessione.isClosed()) {
                    String query = "update Utenti set Password = ? where Email=?;";
                    PreparedStatement stmt = this.connessione.prepareStatement(query);
                    stmt.setString(1, Password);
                    stmt.setString(2, Email);
                    stmt.execute();
                    stmt.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public void aggiornaTipoCarrello(String email) {
        service.submit(createDaemonThread(() -> {
            try {
                System.out.println("Inizio aggiornamento del tipo di carrello per l'utente: " + email);
                if (connessione != null && !connessione.isClosed()) {
                    String query = "UPDATE Carrello SET Tipo = 'Ordine' WHERE Id_Cliente = ? AND Tipo = 'Carrello'";
                    PreparedStatement stmt = connessione.prepareStatement(query);
                    stmt.setString(1, email);
                    int rowsUpdated = stmt.executeUpdate();
                    System.out.println("Aggiornamento completato per l'utente: " + email + ". Righe aggiornate: " + rowsUpdated);
                } else {
                    System.out.println("Connessione al database non disponibile o chiusa");
                }
            } catch (SQLException e) {
                System.out.println("Errore durante l'aggiornamento del tipo di carrello: " + e.getMessage());
            }
        }));
    }

    public void checkOut(String Email, String Totale) {
        service.submit(createDaemonThread(() -> {
            try {
                if (this.connessione != null && !this.connessione.isClosed()) {
                    System.out.println("Inizio checkout per l'utente: " + Email + " con totale: " + Totale);

                    // Aggiorna il campo Totale e il Tipo per il cliente specificato
                    String query = "UPDATE Carrello SET Tipo = ?, Totale = ? WHERE Id_Cliente = ? AND Tipo = 'Carrello'";
                    PreparedStatement stmt = this.connessione.prepareStatement(query);
                    stmt.setString(1, "Ordine");
                    stmt.setString(2, Totale);
                    stmt.setString(3, Email);
                    int rowsUpdated = stmt.executeUpdate();
                    System.out.println("Righe aggiornate a 'Ordine': " + rowsUpdated);
                    stmt.close();

                    System.out.println("Checkout completato per l'utente: " + Email);
                } else {
                    System.out.println("Connessione al database non disponibile o chiusa durante il checkout");
                }
            } catch (SQLException e) {
                System.out.println("Errore durante il checkout: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }));
    }


    public CompletableFuture<ArrayList<ArrayList<Scarpe>>> getOrdine(String Email) {
        CompletableFuture<ArrayList<ArrayList<Scarpe>>> future = new CompletableFuture<>();
        service.submit(createDaemonThread(() -> {
            ArrayList<ArrayList<Scarpe>> ordini = new ArrayList<>();
            try {
                if (connessione == null || connessione.isClosed()) {
                    future.complete(ordini);
                    return; // Termina il thread se la connessione è chiusa o null
                }

                String query = "SELECT Id_Scarpa, Totale FROM Carrello WHERE Id_Cliente = ? and Tipo = ?;";
                PreparedStatement stmt = connessione.prepareStatement(query);
                stmt.setString(1, Email);
                stmt.setString(2, "Ordine");
                ResultSet rs = stmt.executeQuery();

                // Usare una mappa temporanea per raggruppare gli ordini
                Map<String, ArrayList<String>> ordineMap = new LinkedHashMap<>();

                int ordineIndex = 1;
                while (rs.next()) {
                    String idScarpa = rs.getString("Id_Scarpa");
                    String ordineKey = "ordine_" + ordineIndex; // Genera una chiave fittizia per ogni ordine
                    ordineMap.computeIfAbsent(ordineKey, k -> new ArrayList<>()).add(idScarpa);

                    // Incrementare l'indice per ogni nuova riga letta
                    ordineIndex++;
                }

                // Chiudi ResultSet e PreparedStatement
                rs.close();
                stmt.close();

                // Ottieni le informazioni delle scarpe per ogni ordine
                for (ArrayList<String> idScarpeList : ordineMap.values()) {
                    ArrayList<Scarpe> products = getottieniInfScarpeNelCarrello(idScarpeList);
                    ordini.add(products);
                }

                // Completa il future con il risultato
                future.complete(ordini);
            } catch (SQLException | ExecutionException | InterruptedException | TimeoutException e) {
                future.completeExceptionally(e);
            }
        }));
        return future;
    }

    public CompletableFuture<ArrayList<String>> getPrezzoOrdine(String Email) {
        CompletableFuture<ArrayList<String>> future = new CompletableFuture<>();
        service.submit(createDaemonThread(() -> {
            try {
                ArrayList<String> total_price = new ArrayList<>();
                if (connessione == null || connessione.isClosed())
                    future.complete(total_price);
                PreparedStatement stmt = connessione.prepareStatement("SELECT Totale FROM Carrello WHERE Id_Cliente = ? and Tipo = ?;");
                stmt.setString(1, Email);
                stmt.setString(2, "Ordine");

                ResultSet rs = stmt.executeQuery();

                // Usare una mappa temporanea per raggruppare i prezzi
                Map<String, String> prezzoMap = new LinkedHashMap<>();
                int ordineIndex = 1;

                while (rs.next()) {
                    String totale = rs.getString("Totale");
                    String ordineKey = "ordine_" + ordineIndex; // Genera una chiave fittizia per ogni ordine
                    prezzoMap.put(ordineKey, totale);

                    // Incrementare l'indice per ogni nuova riga letta
                    ordineIndex++;
                }

                // Aggiungere i prezzi alla lista totale_price
                total_price.addAll(prezzoMap.values());

                future.complete(total_price);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
        return future;
    }

    public CompletableFuture<Integer> ControlloQuantitaDelleScarpe(String Email, String Id_Prod) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        service.submit(createDaemonThread(() -> {
            try {
                int q = 0;
                if (connessione == null || connessione.isClosed()) {
                    System.out.println("Connessione al database è null o chiusa.");
                    future.complete(q);
                    return;
                }

                // Costruisci la query in base alla presenza dell'email
                String query = Email != null ?
                        "SELECT Quantita FROM Carrello WHERE Id_Cliente = ? AND Id_Scarpa = ? AND Tipo = 'Carrello';" :
                        "SELECT Quantita FROM Carrello WHERE Id_Scarpa = ? AND Tipo = 'Carrello';";

                System.out.println("Eseguo query per controllare la quantità per Email: " + Email + ", Id_Prod: " + Id_Prod);
                PreparedStatement stmt = connessione.prepareStatement(query);

                // Imposta i parametri della query in base alla presenza dell'email
                if (Email != null) {
                    stmt.setString(1, Email);
                    stmt.setString(2, Id_Prod);
                } else {
                    stmt.setString(1, Id_Prod);
                }

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    q = rs.getInt("Quantita");
                    System.out.println("Quantità trovata: " + q + " per Email: " + Email + ", Id_Prod: " + Id_Prod);
                } else {
                    System.out.println("Nessuna quantità trovata per Email: " + Email + ", Id_Prod: " + Id_Prod);
                }
                rs.close();
                stmt.close();
                future.complete(q);
            } catch (SQLException e) {
                System.out.println("Errore durante l'esecuzione della query: " + e.getMessage());
                future.completeExceptionally(e);
            }
        }));
        return future;
    }

    public CompletableFuture<String> AggiornamentoTagliaScarpa(String idscarpa, int nuovaTaglia) {
        CompletableFuture<String> future = new CompletableFuture<>();
        service.submit(createDaemonThread(() -> {
            try {
                if (connessione == null || connessione.isClosed()) {
                    future.completeExceptionally(new SQLException("Connection is closed or null"));
                    return;
                }
                PreparedStatement stmt = connessione.prepareStatement("UPDATE Scarpa SET Taglia = ? WHERE Id = ?");
                stmt.setInt(1, nuovaTaglia);  // Prima il valore della nuova taglia
                stmt.setString(2, idscarpa);     // Poi l'ID della scarpa

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    future.complete(String.valueOf(nuovaTaglia));
                } else {
                    future.completeExceptionally(new SQLException("No rows updated"));
                }
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        }));
        return future;
    }

    public CompletableFuture<String> getTagliaScarpaDalCarrello(String email, String idProd) {
        CompletableFuture<String> future = new CompletableFuture<>();
        service.submit(createDaemonThread(() -> {
            try {
                if (connessione == null || connessione.isClosed()) {
                    future.completeExceptionally(new SQLException("Connection is closed or null"));
                    return;
                }
                String query = "SELECT Taglia FROM Carrello WHERE Id_Scarpa = ? AND Id_Cliente = ?";
                PreparedStatement stmt = connessione.prepareStatement(query);
                stmt.setString(1, idProd);
                stmt.setString(2, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String taglia = rs.getString("Taglia");
                    future.complete(taglia);
                } else {
                    future.completeExceptionally(new SQLException("No matching record found"));
                }
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        }));
        return future;
    }

    // NUOVO: Serve per iniettare le scarpe scaricate dall'API (Categorie)
    public void setCategorieScarpe(ArrayList<Scarpe> scarpe) {
        this.CategorieScarpe = scarpe;
    }

    // NUOVO: Serve per iniettare le scarpe cercate dall'API (Ricerca)
    public void setRicercaScarpe(ArrayList<Scarpe> scarpe) {
        this.RicercaScarpe = scarpe;
    }
}
