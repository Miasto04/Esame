package it.unicam.cs.mpgc.rpg132025.vista;

/**
 * Misure dello schermo virtuale su cui disegna tutta la vista.
 * <p>
 * Il gioco disegna sempre su 320x180 pixel "grossi", con caselle di 16 pixel;
 * ogni piattaforma ingrandisce l'immagine per riempire la propria finestra.
 * Così la grafica è identica su desktop, mobile o web.
 */
public final class Schermo {

    /** Larghezza dello schermo virtuale in pixel. */
    public static final int LARGHEZZA = 320;
    /** Altezza dello schermo virtuale in pixel. */
    public static final int ALTEZZA = 180;
    /** Lato di una casella della mappa in pixel. */
    public static final int CASELLA = 16;

    private Schermo() {
    }
}
