package it.unicam.cs.mpgc.rpg132025.modello.comandi;

import it.unicam.cs.mpgc.rpg132025.modello.partita.Comando;
import it.unicam.cs.mpgc.rpg132025.modello.partita.Partita;

/**
 * L'eroe sferra un fendente nella direzione in cui guarda.
 */
public record ComandoAttacco() implements Comando {

    @Override
    public void eseguiSu(Partita partita) {
        partita.attacca();
    }
}
