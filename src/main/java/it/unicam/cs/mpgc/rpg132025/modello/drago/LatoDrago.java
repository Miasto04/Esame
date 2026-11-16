package it.unicam.cs.mpgc.rpg132025.modello.drago;

/**
 * Lati del drago, relativi alla direzione in cui guarda.
 * <p>
 * Durante ogni attacco il drago lascia scoperto un lato diverso: è proprio
 * quello che il pupo indica al giocatore.
 */
public enum LatoDrago {
    /** Davanti, dove ci sono le fauci. */
    TESTA,
    /** Dietro, dove c'è la coda. */
    CODA,
    /** Ai due lati del corpo. */
    FIANCHI,
    /** Nessun lato è scoperto (per esempio mentre vola). */
    NESSUNO
}
