package it.unicam.cs.mpgc.rpg132025.modello.mondo;

/**
 * Coordinate intere di una casella della mappa: {@code x} è la colonna, {@code y} la riga.
 * L'asse y cresce verso il basso, come sullo schermo.
 * <p>
 * È un valore immutabile: ogni operazione restituisce una nuova posizione.
 *
 * @param x colonna
 * @param y riga
 */
public record Posizione(int x, int y) {

    /**
     * Restituisce la casella adiacente nella direzione indicata.
     *
     * @param direzione direzione dello spostamento
     * @return la casella vicina
     */
    public Posizione spostata(Direzione direzione) {
        return new Posizione(x + direzione.dx(), y + direzione.dy());
    }

    /**
     * Restituisce la posizione traslata del vettore indicato.
     *
     * @param dx spostamento sulle colonne
     * @param dy spostamento sulle righe
     * @return la nuova posizione
     */
    public Posizione traslata(int dx, int dy) {
        return new Posizione(x + dx, y + dy);
    }

    /**
     * Distanza "a passi" tra due caselle, senza diagonali.
     *
     * @param altra l'altra posizione
     * @return somma delle differenze sui due assi
     */
    public int distanzaManhattan(Posizione altra) {
        return Math.abs(x - altra.x) + Math.abs(y - altra.y);
    }

    /**
     * Distanza in cui anche un passo in diagonale vale uno (metrica di Chebyshev).
     *
     * @param altra l'altra posizione
     * @return la massima differenza tra i due assi
     */
    public int distanzaChebyshev(Posizione altra) {
        return Math.max(Math.abs(x - altra.x), Math.abs(y - altra.y));
    }
}
