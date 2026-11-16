package it.unicam.cs.mpgc.rpg132025.modello.partita;

/**
 * A che punto è una partita.
 */
public enum StatoPartita {
    IN_CORSO,
    VITTORIA,
    SCONFITTA;

    /** @return {@code true} se la partita è finita, comunque sia andata */
    public boolean conclusa() {
        return this != IN_CORSO;
    }
}
