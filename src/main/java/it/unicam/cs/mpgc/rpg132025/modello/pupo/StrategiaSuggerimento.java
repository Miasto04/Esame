package it.unicam.cs.mpgc.rpg132025.modello.pupo;

import java.util.Optional;

/**
 * Un modo di decidere quale posizione consigliare al giocatore (pattern Strategy).
 * <p>
 * Il pupo interroga le sue strategie in ordine di priorità e usa il primo
 * consiglio disponibile (una catena di responsabilità): per insegnargli un
 * nuovo trucco basta scrivere una nuova strategia e aggiungerla alla lista.
 */
@FunctionalInterface
public interface StrategiaSuggerimento {

    /**
     * Propone un consiglio per la situazione attuale.
     *
     * @param situazione cosa sta succedendo
     * @param attuale    il consiglio dato in precedenza, oppure {@code null}; se è ancora valido
     *                   conviene restituirlo, così la casella indicata non cambia di continuo
     * @return il consiglio, oppure vuoto se la strategia non ha niente da dire
     */
    Optional<Suggerimento> suggerisci(SituazioneCombattimento situazione, Suggerimento attuale);
}
