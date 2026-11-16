package it.unicam.cs.mpgc.rpg132025.modello.drago;

/**
 * Comportamento del drago in una fase del combattimento (pattern State).
 * <p>
 * Ogni stato decide cosa fa il drago e quale stato viene dopo. Aggiungere un
 * nuovo attacco significa scrivere un nuovo stato e decidere da dove si entra,
 * senza modificare la classe {@link Drago}.
 */
public interface StatoDrago {

    /**
     * Chiamato una sola volta quando il drago entra nello stato.
     *
     * @param drago    il drago
     * @param contesto il mondo circostante
     */
    default void entra(Drago drago, ContestoDrago contesto) {
        // la maggior parte degli stati non ha bisogno di preparazione
    }

    /**
     * Fa avanzare lo stato.
     *
     * @param drago    il drago
     * @param contesto il mondo circostante
     * @param dt       secondi trascorsi
     * @return lo stato successivo, oppure {@code this} per restare
     */
    StatoDrago aggiorna(Drago drago, ContestoDrago contesto, double dt);

    /**
     * Reazione a un colpo dell'eroe.
     *
     * @param drago il drago colpito
     * @return lo stato successivo, oppure {@code this} per ignorare il colpo
     */
    default StatoDrago dopoColpo(Drago drago) {
        return this;
    }

    /** @return il lato che il drago lascia scoperto in questo stato */
    LatoDrago latoVulnerabile();

    /** @return la posa da mostrare */
    AspettoDrago aspetto();

    /** @return {@code false} solo mentre dorme */
    default boolean sveglio() {
        return true;
    }

    /** @return {@code true} se è in aria (non può essere colpito e non occupa caselle) */
    default boolean inVolo() {
        return false;
    }

    /** @return altezza da terra per la grafica, da 0 (a terra) a 1 */
    default double altitudine() {
        return 0.0;
    }
}
