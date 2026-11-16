package it.unicam.cs.mpgc.rpg132025.input;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;

import java.util.Optional;

/**
 * I comandi sullo schermo: croce direzionale, pulsante d'attacco, pulsanti
 * degli oggetti e del menu. Funzionano con il mouse e, sui dispositivi touch,
 * con le dita.
 * <p>
 * Questa classe conosce la loro posizione e capisce quale è stato toccato: il
 * controllore la usa per leggere i comandi, la vista per disegnarli con le stesse
 * misure, così non possono disallinearsi.
 */
public final class ComandiTouch {

    // Tutte le misure sono in pixel dello schermo virtuale.

    // croce direzionale: centro, raggio e larghezza dei bracci
    public static final int CROCE_X = 30;
    public static final int CROCE_Y = 146;
    public static final int CROCE_RAGGIO = 21;
    public static final int CROCE_BRACCIO = 13;
    // pulsanti rotondi: centro e raggio
    public static final int ATTACCO_X = 292;
    public static final int ATTACCO_Y = 150;
    public static final int ATTACCO_RAGGIO = 15;
    public static final int POZIONE_X = 261;
    public static final int POZIONE_Y = 162;
    public static final int POZIONE_RAGGIO = 10;
    public static final int BACCA_X = 262;
    public static final int BACCA_Y = 137;
    public static final int BACCA_RAGGIO = 9;
    // pulsante del menu nella barra in alto: angolo e dimensioni
    public static final int MENU_X = 2;
    public static final int MENU_Y = 1;
    public static final int MENU_LARGHEZZA = 12;
    public static final int MENU_ALTEZZA = 11;

    /** Vicino al centro della croce non si sceglie nessuna direzione. */
    private static final double ZONA_MORTA = 3;

    /**
     * @param input stato dei comandi
     * @return la direzione indicata sulla croce, se il puntatore la sta premendo
     */
    public Optional<Direzione> direzione(StatoInput input) {
        if (!input.puntatoreAttivo()) {
            return Optional.empty();
        }
        double dx = input.puntatoreX() - CROCE_X;
        double dy = input.puntatoreY() - CROCE_Y;
        double distanza = Math.hypot(dx, dy);
        if (distanza < ZONA_MORTA || distanza > CROCE_RAGGIO + 4) {
            return Optional.empty();
        }
        return Direzione.verso(dx, dy);
    }

    /** @return {@code true} se è appena stato premuto il pulsante d'attacco */
    public boolean attacco(StatoInput input) {
        return premutoNelCerchio(input, ATTACCO_X, ATTACCO_Y, ATTACCO_RAGGIO);
    }

    /** @return {@code true} se è appena stato premuto il pulsante della pozione */
    public boolean pozione(StatoInput input) {
        return premutoNelCerchio(input, POZIONE_X, POZIONE_Y, POZIONE_RAGGIO);
    }

    /** @return {@code true} se è appena stato premuto il pulsante della bacca */
    public boolean bacca(StatoInput input) {
        return premutoNelCerchio(input, BACCA_X, BACCA_Y, BACCA_RAGGIO);
    }

    /** @return {@code true} se è appena stato premuto il pulsante del menu */
    public boolean menu(StatoInput input) {
        return input.pressioneAppenaAvvenuta()
                && input.puntatoreX() >= MENU_X && input.puntatoreX() < MENU_X + MENU_LARGHEZZA
                && input.puntatoreY() >= MENU_Y && input.puntatoreY() < MENU_Y + MENU_ALTEZZA;
    }

    private static boolean premutoNelCerchio(StatoInput input, int cx, int cy, int raggio) {
        return input.pressioneAppenaAvvenuta()
                && Math.hypot(input.puntatoreX() - cx, input.puntatoreY() - cy) <= raggio + 2;
    }
}
