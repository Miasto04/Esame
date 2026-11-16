package it.unicam.cs.mpgc.rpg132025.vista;

/**
 * Formattazione dei valori mostrati al giocatore.
 */
public final class Formati {

    private Formati() {
    }

    /**
     * @param secondi durata in secondi
     * @return la durata nel formato {@code m:ss}
     */
    public static String tempo(double secondi) {
        long totale = (long) Math.floor(Math.max(0, secondi));
        return String.format("%d:%02d", totale / 60, totale % 60);
    }
}
