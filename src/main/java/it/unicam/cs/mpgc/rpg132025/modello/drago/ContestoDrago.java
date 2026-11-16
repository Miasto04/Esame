package it.unicam.cs.mpgc.rpg132025.modello.drago;

import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

import java.util.random.RandomGenerator;

/**
 * Ciò che il drago può sapere e fare del mondo che lo circonda.
 * <p>
 * Gli stati del drago dipendono da questa interfaccia e non dalla partita
 * concreta: si possono provare con un contesto finto e il drago può essere
 * riusato in altri livelli.
 */
public interface ContestoDrago {

    /** @return casella in cui si trova l'eroe */
    Posizione posizioneEroe();

    /**
     * @param casella casella da controllare
     * @return {@code true} se il terreno della casella è percorribile
     */
    boolean percorribile(Posizione casella);

    /**
     * Controlla se il corpo del drago (2x2) può stare con l'angolo in alto a sinistra nella casella indicata.
     *
     * @param angolo angolo in alto a sinistra
     * @return {@code true} se tutte e quattro le caselle sono percorribili
     */
    default boolean terrenoLibero(Posizione angolo) {
        for (Posizione casella : Drago.celleCorpoIn(angolo)) {
            if (!percorribile(casella)) {
                return false;
            }
        }
        return true;
    }

    /** @return tempo di gioco in secondi */
    double tempo();

    /**
     * Registra una zona di pericolo generata da un attacco.
     *
     * @param zona la zona
     */
    void aggiungiZona(ZonaPericolo zona);

    /** @return sorgente di numeri casuali (sostituibile nei test) */
    RandomGenerator casuale();

    /**
     * Pubblica un evento.
     *
     * @param evento l'evento
     */
    void pubblica(EventoGioco evento);
}
