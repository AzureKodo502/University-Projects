import random
from config import Config

class UnitaProduttiva:
    """
    Classe Astratta Base.
    Rappresenta un generico centro di costo/profitto aziendale.
    Implementa le logiche comuni di bilancio e gestione tempo.
    """
    def __init__(self, nome):
        self.nome = nome
        self.soldi_spesi = 0.0
        self.soldi_guadagnati = 0.0
        self.quantita_prodotta = 0.0
        self.tempo_impiegato = 0
        self.ricavo_oggi = 0.0
        self.spesa_oggi = 0.0

    def calcola_giornata(self, meteo, risorse, giorno_anno, molt_prezzi, molt_prod):
        """Metodo polimorfico sovrascritto dalle sottoclassi"""
        pass

    def get_flusso_cassa_odierno(self):
        """Restituisce il Net Cash Flow giornaliero (Ricavi - Spese Variabili)"""
        return self.ricavo_oggi - self.spesa_oggi

    def genera_quantita_random(self, base_output, meteo):
        """
        Applica una variazione stocastica alla produzione base.
        Include un controllo (clipping) per evitare produzioni negative.
        """
        # Il meteo influenza la produttività (distribuzione uniforme)
        fattore = random.uniform(meteo, 1.2)
        
        # Robustezza: Evitiamo valori estremi negativi
        if fattore < 0.1: fattore = 0.1
        
        quantita = base_output * fattore
        return max(0.0, quantita) # Clipping a zero per sicurezza

    def get_profitto(self):
        return self.soldi_guadagnati - self.soldi_spesi


class CampoGrano(UnitaProduttiva):
    """
    Implementa la logica 'Ciclica': Costi iniziali -> Attesa -> Ricavo finale.
    """
    def __init__(self):
        super().__init__("Campo di Grano")
        self.giorni_passati = 0
        self.raccolto_avvenuto = False
        # Investimento iniziale (Semina)
        self.soldi_spesi += Config.Costo_Semina_Grano * Config.Ettari_Grano

    def calcola_giornata(self, meteo, risorse, giorno_anno, molt_prezzi, molt_prod):
        self.ricavo_oggi = 0.0
        self.spesa_oggi = 0.0 

        if self.raccolto_avvenuto: 
            return "Riposo stagionale."

        self.giorni_passati += 1
        self.tempo_impiegato = self.giorni_passati

        # Verifica completamento ciclo biologico
        if self.giorni_passati >= Config.Giorni_Necessari_Grano:
            if risorse['trattori_disponibili'] > 0:
                risorse['trattori_disponibili'] -= 1
                
                # Calcolo Resa con Fattori di Stress (Meteo + Eventi)
                resa_teorica = Config.Ettari_Grano * Config.Resa_Ettaro_Grano
                quantita_base = self.genera_quantita_random(resa_teorica, meteo)
                kg_reali = quantita_base * molt_prod['grano']
                
                # Calcolo Economico
                prezzo = Config.Prezzo_Base_Grano * molt_prezzi['grano']
                costo_carb = Config.Costo_Base_Carburante_Trattore * molt_prezzi['carburante']
                
                self.ricavo_oggi = kg_reali * prezzo
                self.spesa_oggi = costo_carb
                
                # Commit dei dati
                self.quantita_prodotta += kg_reali
                self.soldi_guadagnati += self.ricavo_oggi
                self.soldi_spesi += self.spesa_oggi
                self.raccolto_avvenuto = True
                
                perc_danno = (1 - molt_prod['grano']) * 100
                return f"RACCOLTO: {kg_reali:.0f} kg (Efficienza: {100-perc_danno:.0f}%). Ricavo: {self.ricavo_oggi:.2f}€"
            else:
                return "Coltura pronta, ma risorse (trattori) insufficienti."
        return "Fase di maturazione..."


class Stalla(UnitaProduttiva):
    """
    Implementa la logica 'Continua': Produzione e costi giornalieri costanti.
    """
    def __init__(self):
        super().__init__("Stalla (Latte)")
    
    def calcola_giornata(self, meteo, risorse, giorno_anno, molt_prezzi, molt_prod):
        self.tempo_impiegato += 1
        
        # 1. Calcolo Costi Variabili (Sensibili all'inflazione)
        costo_cibo = Config.Mucche * Config.Costo_Mangime * molt_prezzi['costi_vari']
        manutenzione = Config.Costo_Manutenzione_Stalla * molt_prezzi['costi_vari']
        self.spesa_oggi = costo_cibo + manutenzione

        # 2. Calcolo Produzione (Sensibile a stress e meteo)
        prod_base = self.genera_quantita_random(Config.Mucche * Config.Litri_Prodotti, meteo)
        litri_reali = prod_base * molt_prod['latte']

        # 3. Calcolo Ricavi
        prezzo = Config.Prezzo_Base_Latte * molt_prezzi['latte']
        self.ricavo_oggi = litri_reali * prezzo
        
        # Aggiornamento storico
        self.soldi_spesi += self.spesa_oggi
        self.soldi_guadagnati += self.ricavo_oggi
        self.quantita_prodotta += litri_reali
        
        return f"Produzione: {litri_reali:.0f} L. Margine: {self.ricavo_oggi - self.spesa_oggi:.0f}€"


class Vigneto(UnitaProduttiva):
    """
    Implementa la logica 'Ibrida': Fase agricola + Fase trasformazione (Cantina).
    """
    def __init__(self):
        super().__init__("Vigneto & Cantina")
        self.uva_in_magazzino = 0.0
    
    def calcola_giornata(self, meteo, risorse, giorno_anno, molt_prezzi, molt_prod):
        self.tempo_impiegato += 1
        self.ricavo_oggi = 0.0
        
        # Spese fisse di manutenzione vigna
        self.spesa_oggi = 30.0 * molt_prezzi['costi_vari']
        
        messaggio = ""
        
        # FASE 1: VENDEMMIA (Finestra temporale limitata)
        fine_vendemmia = Config.Giorno_Inizio_Vendemmia + 30
        if Config.Giorno_Inizio_Vendemmia <= giorno_anno < fine_vendemmia:
            if risorse['operai_disponibili'] >= 2:
                risorse['operai_disponibili'] -= 2
                
                base_uva = (Config.Ettari_Vino * Config.Resa_Ettaro_Vino) / 30
                uva_reale = self.genera_quantita_random(base_uva, meteo) * molt_prod['vino']
                
                self.uva_in_magazzino += uva_reale
                messaggio = f"Vendemmia: {uva_reale:.0f} kg. "

        # FASE 2: CANTINA (Trasformazione asincrona)
        if self.uva_in_magazzino > 50:
            lavorazione = min(self.uva_in_magazzino, 400.0) # Capacità max lavorazione
            self.uva_in_magazzino -= lavorazione
            
            # Conversione fisica
            bottiglie = int((lavorazione * Config.Conversione_Vino) / 0.75)
            
            # Economia
            costo_prod = bottiglie * Config.Costo_Imbottigliamento_Vino * molt_prezzi['costi_vari']
            ricavo = bottiglie * Config.Prezzo_Base_Vino * molt_prezzi['vino']
            
            self.spesa_oggi += costo_prod
            self.ricavo_oggi += ricavo
            self.quantita_prodotta += bottiglie
            
            messaggio += f"Cantina: {bottiglie} bott. "
            
        self.soldi_spesi += self.spesa_oggi
        self.soldi_guadagnati += self.ricavo_oggi
        
        if messaggio == "": return "Maturazione vegetativa."
        return messaggio
