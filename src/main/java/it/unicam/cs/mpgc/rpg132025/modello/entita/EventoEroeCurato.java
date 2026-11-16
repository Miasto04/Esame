package it.unicam.cs.mpgc.rpg132025.modello.entita;

import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

/**
 * L'eroe ha recuperato punti vita.
 *
 * @param quantita  mezzi cuori recuperati
 * @param posizione dove si trovava
 */
public record EventoEroeCurato(int quantita, Posizione posizione) implements EventoGioco {
}
