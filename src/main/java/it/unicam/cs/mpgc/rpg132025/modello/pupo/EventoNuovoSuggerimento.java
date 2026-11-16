package it.unicam.cs.mpgc.rpg132025.modello.pupo;

import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;

/**
 * Il pupo ha dato un nuovo consiglio.
 *
 * @param suggerimento il consiglio
 */
public record EventoNuovoSuggerimento(Suggerimento suggerimento) implements EventoGioco {
}
