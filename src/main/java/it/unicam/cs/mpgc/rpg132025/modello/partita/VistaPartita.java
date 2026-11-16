package it.unicam.cs.mpgc.rpg132025.modello.partita;

import it.unicam.cs.mpgc.rpg132025.modello.drago.VistaDrago;
import it.unicam.cs.mpgc.rpg132025.modello.drago.ZonaPericolo;
import it.unicam.cs.mpgc.rpg132025.modello.entita.VistaEroe;
import it.unicam.cs.mpgc.rpg132025.modello.eventi.AscoltatoreEventi;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Mappa;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.OggettoATerra;
import it.unicam.cs.mpgc.rpg132025.modello.pupo.VistaPupo;
import it.unicam.cs.mpgc.rpg132025.modello.statistiche.RiepilogoPartita;

import java.util.List;

/**
 * Vista in sola lettura di una partita: è ciò che il modello mostra alla Vista del pattern MVC.
 * <p>
 * Permette di leggere lo stato della partita e di osservarne gli eventi, ma non di
 * modificarla: i cambiamenti passano dal controllore, che manda alla {@link Partita} i
 * {@link Comando} del giocatore.
 */
public interface VistaPartita {

    /** @return la risorsa del livello giocato */
    String livello();

    /** @return la mappa */
    Mappa mappa();

    /** @return l'eroe, in sola lettura */
    VistaEroe eroe();

    /** @return il pupo, in sola lettura */
    VistaPupo pupo();

    /** @return il drago, in sola lettura */
    VistaDrago drago();

    /** @return gli oggetti ancora da raccogliere */
    List<OggettoATerra> oggettiATerra();

    /** @return gli attacchi del drago in preavviso o in corso */
    List<ZonaPericolo> zonePericolo();

    /** @return secondi di gioco trascorsi dall'inizio (o dal caricamento) */
    double tempo();

    /** @return minuti dalla mezzanotte nell'orologio di gioco */
    double minutiDelGiorno();

    /** @return l'ora del giorno nel gioco */
    Orario orario();

    /** @return lo stato della partita */
    StatoPartita stato();

    /** @return le statistiche raccolte finora */
    RiepilogoPartita riepilogo();

    /**
     * Iscrive un osservatore agli eventi della partita.
     *
     * @param ascoltatore l'osservatore
     */
    void iscrivi(AscoltatoreEventi ascoltatore);

    /**
     * @param ascoltatore osservatore da rimuovere
     */
    void annullaIscrizione(AscoltatoreEventi ascoltatore);
}
