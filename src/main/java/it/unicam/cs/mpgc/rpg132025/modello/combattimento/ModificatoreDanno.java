package it.unicam.cs.mpgc.rpg132025.modello.combattimento;

import java.util.Optional;

/**
 * Una regola che può modificare il danno di un colpo.
 * <p>
 * Il {@link CalcolatoreDanni} applica in sequenza tutti i modificatori ricevuti:
 * una nuova regola di gioco (armi, debolezze, difficoltà...) si aggiunge con una
 * nuova implementazione, senza modificare il calcolatore (Open/Closed).
 */
@FunctionalInterface
public interface ModificatoreDanno {

    /**
     * @param contesto circostanze del colpo
     * @return il bonus da applicare, oppure vuoto se la regola non si applica
     */
    Optional<Bonus> valuta(ContestoColpo contesto);
}
