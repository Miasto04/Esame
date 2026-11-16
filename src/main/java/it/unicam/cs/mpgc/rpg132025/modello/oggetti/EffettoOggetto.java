package it.unicam.cs.mpgc.rpg132025.modello.oggetti;

/**
 * Effetto prodotto dall'uso di un oggetto (pattern Strategy).
 * <p>
 * Nuovi oggetti con comportamenti diversi si ottengono scrivendo nuove
 * implementazioni, senza toccare il codice che le usa.
 */
@FunctionalInterface
public interface EffettoOggetto {

    /**
     * Applica l'effetto.
     *
     * @param utilizzatore chi usa l'oggetto
     * @return {@code true} se l'effetto ha avuto luogo e l'oggetto va consumato
     */
    boolean applicaA(UtilizzatoreOggetti utilizzatore);
}
