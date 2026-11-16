package it.unicam.cs.mpgc.rpg132025.modello.drago;

import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;

/**
 * Il drago ha iniziato un attacco.
 *
 * @param tipo attacco iniziato
 */
public record EventoAttaccoDrago(TipoAttacco tipo) implements EventoGioco {
}
