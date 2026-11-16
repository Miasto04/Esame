package it.unicam.cs.mpgc.rpg132025.modello.partita;

/**
 * Ora del giorno nel mondo di gioco.
 *
 * @param ore    da 0 a 23
 * @param minuti da 0 a 59
 */
public record Orario(int ore, int minuti) {

    /** Minuti in una giornata. */
    public static final int MINUTI_AL_GIORNO = 24 * 60;

    public Orario {
        if (ore < 0 || ore > 23 || minuti < 0 || minuti > 59) {
            throw new IllegalArgumentException("Orario non valido: " + ore + ":" + minuti);
        }
    }

    /**
     * @param minutiDallaMezzanotte minuti trascorsi dalla mezzanotte (anche oltre un giorno)
     * @return l'orario corrispondente
     */
    public static Orario daMinuti(double minutiDallaMezzanotte) {
        int totale = Math.floorMod((int) Math.floor(minutiDallaMezzanotte), MINUTI_AL_GIORNO);
        return new Orario(totale / 60, totale % 60);
    }

    /** @return l'orario nel formato {@code HH:MM} */
    public String formattato() {
        return String.format("%02d:%02d", ore, minuti);
    }

    /** @return {@code true} tra le 20 e le 6 */
    public boolean notte() {
        return ore >= 20 || ore < 6;
    }
}
