package it.unicam.cs.mpgc.rpg132025.modello.partita;

import it.unicam.cs.mpgc.rpg132025.modello.combattimento.CalcolatoreDanni;
import it.unicam.cs.mpgc.rpg132025.modello.combattimento.ModificatoreAlleSpalle;
import it.unicam.cs.mpgc.rpg132025.modello.combattimento.ModificatoreCritico;
import it.unicam.cs.mpgc.rpg132025.modello.combattimento.ModificatorePosizioneSuggerita;
import it.unicam.cs.mpgc.rpg132025.modello.drago.Drago;
import it.unicam.cs.mpgc.rpg132025.modello.entita.Eroe;
import it.unicam.cs.mpgc.rpg132025.modello.livello.CaricatoreLivello;
import it.unicam.cs.mpgc.rpg132025.modello.livello.DefinizioneLivello;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Mappa;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.Inventario;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.OggettoATerra;
import it.unicam.cs.mpgc.rpg132025.modello.pupo.Pupo;
import it.unicam.cs.mpgc.rpg132025.modello.pupo.StrategiaFugaDalPericolo;
import it.unicam.cs.mpgc.rpg132025.modello.pupo.StrategiaLatoVulnerabile;
import it.unicam.cs.mpgc.rpg132025.modello.statistiche.StatistichePartita;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

/**
 * Crea le partite: nuove, dai livelli, oppure ricostruite da un salvataggio (pattern Factory).
 * <p>
 * È l'unico punto in cui si decide quali strategie usa il pupo e quali regole
 * di danno valgono: cambiare le regole del gioco significa cambiare questa classe,
 * non la partita.
 */
public final class FabbricaPartita {

    /** Il livello giocato da una nuova partita. */
    public static final String LIVELLO_PREDEFINITO = "/livelli/isola.txt";
    /** Ora di inizio di una nuova partita (le 7 del mattino), in minuti. */
    public static final double ORA_DI_INIZIO = 7 * 60.0;
    /** Probabilità che un colpo sia critico. */
    public static final double PROBABILITA_CRITICO = 0.1;

    private final CaricatoreLivello caricatore;
    private final Supplier<RandomGenerator> generatori;

    /** Crea una fabbrica che legge i livelli dalle risorse e usa numeri casuali veri. */
    public FabbricaPartita() {
        this(new CaricatoreLivello(), Random::new);
    }

    /**
     * @param caricatore lettore dei livelli
     * @param generatori fornisce una sorgente di numeri casuali per ogni partita
     */
    public FabbricaPartita(CaricatoreLivello caricatore, Supplier<RandomGenerator> generatori) {
        this.caricatore = Objects.requireNonNull(caricatore, "caricatore");
        this.generatori = Objects.requireNonNull(generatori, "generatori");
    }

    /** @return una nuova partita sul livello predefinito */
    public Partita nuovaPartita() {
        return nuovaPartita(LIVELLO_PREDEFINITO);
    }

    /**
     * @param risorsaLivello percorso della risorsa del livello
     * @return una nuova partita su quel livello
     */
    public Partita nuovaPartita(String risorsaLivello) {
        return nuovaPartita(risorsaLivello, caricatore.carica(risorsaLivello));
    }

    /**
     * @param nomeLivello nome con cui il livello verrà salvato
     * @param livello     contenuto del livello
     * @return una nuova partita
     */
    public Partita nuovaPartita(String nomeLivello, DefinizioneLivello livello) {
        RandomGenerator casuale = generatori.get();
        return new Partita.Costruttore()
                .livello(nomeLivello, livello.mappa())
                .personaggi(new Eroe(livello.inizioEroe()), creaPupo(livello.inizioPupo()),
                        new Drago(livello.inizioDrago(), Direzione.GIU))
                .oggetti(livello.oggetti())
                .orario(ORA_DI_INIZIO)
                .regole(creaCalcolatore(casuale), casuale)
                .crea();
    }

    /**
     * Ricostruisce una partita salvata.
     *
     * @param instantanea dati salvati
     * @return la partita, pronta per continuare
     * @throws IllegalArgumentException se il salvataggio non è valido
     */
    public Partita ripristina(InstantaneaPartita instantanea) {
        if (instantanea.versione() > InstantaneaPartita.VERSIONE_CORRENTE) {
            throw new IllegalArgumentException("Il salvataggio è stato creato da una versione più recente del gioco");
        }
        Mappa mappa = caricatore.carica(instantanea.livello()).mappa();
        DatiEroe datiEroe = instantanea.eroe();
        DatiDrago datiDrago = instantanea.drago();
        controllaSullaMappa(mappa, datiEroe.posizione(), "l'eroe");
        controllaSullaMappa(mappa, instantanea.pupo(), "il pupo");
        controllaSullaMappa(mappa, datiDrago.posizione(), "il drago");
        for (OggettoATerra oggetto : instantanea.oggetti()) {
            controllaSullaMappa(mappa, oggetto.posizione(), "un oggetto");
        }
        RandomGenerator casuale = generatori.get();
        Eroe eroe = new Eroe(datiEroe.posizione(), datiEroe.direzione(), datiEroe.vita(),
                Inventario.da(datiEroe.inventario()));
        Drago drago = new Drago(datiDrago.posizione(), datiDrago.direzione(), datiDrago.vita(),
                datiDrago.sveglio(), datiDrago.furioso());
        return new Partita.Costruttore()
                .livello(instantanea.livello(), mappa)
                .personaggi(eroe, creaPupo(instantanea.pupo()), drago)
                .oggetti(instantanea.oggetti())
                .orario(instantanea.minutiDelGiorno())
                .statistiche(StatistichePartita.da(instantanea.statistiche()))
                .regole(creaCalcolatore(casuale), casuale)
                .crea();
    }

    private static Pupo creaPupo(Posizione posizione) {
        return new Pupo(posizione, List.of(new StrategiaFugaDalPericolo(), new StrategiaLatoVulnerabile()));
    }

    private static CalcolatoreDanni creaCalcolatore(RandomGenerator casuale) {
        return new CalcolatoreDanni(List.of(
                new ModificatorePosizioneSuggerita(),
                new ModificatoreAlleSpalle(),
                new ModificatoreCritico(casuale, PROBABILITA_CRITICO)));
    }

    private static void controllaSullaMappa(Mappa mappa, Posizione posizione, String cosa) {
        if (!mappa.contiene(posizione)) {
            throw new IllegalArgumentException("Nel salvataggio " + cosa + " è fuori dalla mappa: " + posizione);
        }
    }
}
