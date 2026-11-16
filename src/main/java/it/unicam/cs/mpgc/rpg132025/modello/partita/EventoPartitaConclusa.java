package it.unicam.cs.mpgc.rpg132025.modello.partita;

import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;

/**
 * La partita è finita.
 *
 * @param esito vittoria o sconfitta
 */
public record EventoPartitaConclusa(StatoPartita esito) implements EventoGioco {
}
