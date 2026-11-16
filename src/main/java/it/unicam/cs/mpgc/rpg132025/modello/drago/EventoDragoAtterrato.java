package it.unicam.cs.mpgc.rpg132025.modello.drago;

import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

/**
 * Il drago è atterrato dopo un volo.
 *
 * @param angolo angolo in alto a sinistra del corpo dopo l'atterraggio
 */
public record EventoDragoAtterrato(Posizione angolo) implements EventoGioco {
}
