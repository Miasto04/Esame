package it.unicam.cs.mpgc.rpg132025.modello.eventi;

/**
 * Riceve gli eventi pubblicati su un {@link BusEventi}.
 */
@FunctionalInterface
public interface AscoltatoreEventi {

    /**
     * Chiamato una volta per ogni evento pubblicato.
     *
     * @param evento l'evento appena accaduto
     */
    void suEvento(EventoGioco evento);
}
