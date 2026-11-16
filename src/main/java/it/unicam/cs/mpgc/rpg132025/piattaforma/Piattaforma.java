package it.unicam.cs.mpgc.rpg132025.piattaforma;

/**
 * I servizi che solo la piattaforma su cui gira il gioco può offrire.
 * <p>
 * I controllori li usano senza sapere se sono su desktop, mobile o web: ogni
 * piattaforma fornisce la propria implementazione.
 */
@FunctionalInterface
public interface Piattaforma {

    /** Chiude l'applicazione. */
    void esci();
}
