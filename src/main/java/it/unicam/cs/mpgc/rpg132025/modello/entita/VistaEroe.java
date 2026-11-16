package it.unicam.cs.mpgc.rpg132025.modello.entita;

import it.unicam.cs.mpgc.rpg132025.modello.oggetti.TipoOggetto;

/**
 * Vista in sola lettura dell'eroe controllato dal giocatore.
 */
public interface VistaEroe extends VistaCreatura {

    /** @return {@code true} nei momenti successivi a un colpo subito, quando non può essere ferito */
    boolean isInvulnerabile();

    /** @return {@code true} mentre il fendente della spada è visibile */
    boolean staAttaccando();

    /** @return avanzamento dell'animazione del fendente, da 0 a 1 */
    double progressoAttacco();

    /**
     * @param tipo tipo di oggetto
     * @return quanti oggetti di quel tipo possiede
     */
    int quantitaOggetto(TipoOggetto tipo);
}
