package it.unicam.cs.mpgc.rpg132025.modello.oggetti;

import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;

/**
 * L'eroe ha usato (e consumato) un oggetto.
 *
 * @param tipo oggetto usato
 */
public record EventoOggettoUsato(TipoOggetto tipo) implements EventoGioco {
}
