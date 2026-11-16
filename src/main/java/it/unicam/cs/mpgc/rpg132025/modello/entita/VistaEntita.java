package it.unicam.cs.mpgc.rpg132025.modello.entita;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

/**
 * Vista in sola lettura di un'entità sulla mappa.
 * <p>
 * È ciò che vedono la grafica e le strategie del pupo: possono interrogare lo
 * stato ma non modificarlo.
 */
public interface VistaEntita {

    /** @return casella occupata (durante un passo è già quella di arrivo) */
    Posizione posizione();

    /** @return dove sta guardando */
    Direzione direzione();

    /** @return {@code true} mentre si sta spostando da una casella all'altra */
    boolean inMovimento();

    /** @return avanzamento del passo corrente, da 0 a 1 (1 se fermo) */
    double progressoPasso();

    /** @return colonna "continua" da usare per disegnare lo spostamento in modo fluido */
    double xVisivo();

    /** @return riga "continua" da usare per disegnare lo spostamento in modo fluido */
    double yVisivo();
}
