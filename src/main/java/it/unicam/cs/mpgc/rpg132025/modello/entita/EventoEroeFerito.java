package it.unicam.cs.mpgc.rpg132025.modello.entita;

import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

/**
 * L'eroe ha perso punti vita.
 *
 * @param danno     mezzi cuori persi
 * @param posizione dove si trovava
 */
public record EventoEroeFerito(int danno, Posizione posizione) implements EventoGioco {
}
