package it.unicam.cs.mpgc.rpg132025.modello.combattimento;

import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

/**
 * L'eroe ha colpito il drago.
 *
 * @param risultato danno inflitto e bonus ottenuti
 * @param bersaglio casella del corpo del drago che è stata colpita
 */
public record EventoColpoInflitto(RisultatoColpo risultato, Posizione bersaglio) implements EventoGioco {
}
