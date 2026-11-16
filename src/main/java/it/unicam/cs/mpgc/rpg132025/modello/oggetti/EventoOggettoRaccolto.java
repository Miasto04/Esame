package it.unicam.cs.mpgc.rpg132025.modello.oggetti;

import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

/**
 * L'eroe ha raccolto un oggetto da terra.
 *
 * @param tipo      oggetto raccolto
 * @param posizione casella in cui si trovava
 */
public record EventoOggettoRaccolto(TipoOggetto tipo, Posizione posizione) implements EventoGioco {
}
