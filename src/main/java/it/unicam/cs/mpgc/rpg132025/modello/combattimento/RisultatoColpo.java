package it.unicam.cs.mpgc.rpg132025.modello.combattimento;

import java.util.List;

/**
 * Esito del calcolo del danno di un colpo.
 *
 * @param danno punti vita tolti al bersaglio
 * @param bonus bonus ottenuti, nell'ordine in cui sono stati applicati
 */
public record RisultatoColpo(int danno, List<Bonus> bonus) {

    public RisultatoColpo {
        if (danno < 0) {
            throw new IllegalArgumentException("Il danno non può essere negativo");
        }
        bonus = List.copyOf(bonus);
    }

    /**
     * @param etichetta etichetta di un bonus
     * @return {@code true} se il colpo ha ottenuto quel bonus
     */
    public boolean haBonus(String etichetta) {
        return bonus.stream().anyMatch(b -> b.etichetta().equals(etichetta));
    }

    /**
     * @param dannoEffettivo danno davvero inflitto (può essere minore se la vita era quasi finita)
     * @return lo stesso risultato con il danno corretto
     */
    public RisultatoColpo conDanno(int dannoEffettivo) {
        return new RisultatoColpo(dannoEffettivo, bonus);
    }
}
