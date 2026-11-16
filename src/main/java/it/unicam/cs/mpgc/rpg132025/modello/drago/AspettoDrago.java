package it.unicam.cs.mpgc.rpg132025.modello.drago;

/**
 * Posa del drago in un certo momento.
 * <p>
 * Il modello dice <em>cosa</em> sta facendo il drago, la vista decide
 * <em>come</em> disegnarlo: così la grafica può cambiare senza toccare la logica.
 */
public enum AspettoDrago {
    DORME,
    RUGGISCE,
    VEGLIA,
    CARICA_FUOCO,
    SOFFIA_FUOCO,
    MORDE,
    COLPO_DI_CODA,
    STANCO,
    VOLA,
    SCONFITTO
}
