package it.unicam.cs.mpgc.rpg132025.modello.combattimento;

import java.util.Objects;

/**
 * Un aumento (o una riduzione) del danno ottenuto in un colpo.
 *
 * @param etichetta    testo da mostrare al giocatore, usato anche nelle statistiche
 * @param moltiplicatore fattore applicato al danno
 */
public record Bonus(String etichetta, double moltiplicatore) {

    public Bonus {
        Objects.requireNonNull(etichetta, "etichetta");
        if (moltiplicatore <= 0) {
            throw new IllegalArgumentException("Il moltiplicatore deve essere positivo");
        }
    }
}
