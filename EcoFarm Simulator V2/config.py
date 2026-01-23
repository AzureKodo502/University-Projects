class Config:
    """
    FILE DI CONFIGURAZIONE
    Contiene tutti i parametri globali della simulazione.
    Separare i dati dalla logica permette di calibrare il modello senza toccare il codice.
    """

    # --- IMPOSTAZIONI SISTEMA ---
    SEED = None            # Imposta un numero intero (es. 42) per ripetere la STESSA simulazione. None = Casuale.
    FILE_LOG = "report_dati_AzureCorp.csv" # Nome del file Excel/CSV di output
    SOGLIA_BANCAROTTA = 0.0 # Sotto questa cifra sei tecnicamente fallito

    # --- SCENARIO GENERALE ---
    Giorni = 365           # Durata temporale (1 anno fiscale)
    Budget = 20000.0       # Capitale di rischio iniziale
    Meteo = 0.7            # Volatilità climatica (0.0 stabile, 1.0 caotico)

    # --- PARAMETRI MACROECONOMICI ---
    Tasso_Interesse_Passivo = 0.20  # 20% annuo (penalità per il debito)
    Probabilita_Evento = 0.04       # 4% probabilità giornaliera di Black Swan (Cigni Neri)
    
    # Costi strutturali fissi (Burn Rate)
    Affitto_e_Tasse_Giornaliero = 150.0 

    # --- RISORSE AZIENDALI ---
    Ettari = 100
    Trattori = 3
    Operai = 6
    Stipendio_Giornaliero_Totale = Operai * 80.0  # Costo lavoro fisso

    # --- COSTI VARIABILI ---
    Costo_Base_Carburante_Trattore = 100.0
    Manutenzione = 50.0

    # --- SETTORE 1: CEREALICOLO (Grano) ---
    Ettari_Grano = 60
    Costo_Semina_Grano = 250.0
    Resa_Ettaro_Grano = 6000.0      # Kg/Ettaro ottimali
    Prezzo_Base_Grano = 0.28        # €/Kg
    Giorni_Necessari_Grano = 150    # Ciclo biologico

    # --- SETTORE 2: ZOOTECNICO (Latte) ---
    Mucche = 40
    Costo_Mangime = 15.0            # €/Capo/Giorno
    Litri_Prodotti = 28.0           # Litri/Capo/Giorno
    Prezzo_Base_Latte = 0.70        # €/Litro
    Costo_Manutenzione_Stalla = 80.0

    # --- SETTORE 3: VITIVINICOLO (Vino) ---
    Ettari_Vino = 20
    Resa_Ettaro_Vino = 6000.0
    Conversione_Vino = 0.65         # Resa uva -> vino
    Costo_Imbottigliamento_Vino = 2.5
    Prezzo_Base_Vino = 6.0
    Giorno_Inizio_Vendemmia = 240   # Inizio Autunno
