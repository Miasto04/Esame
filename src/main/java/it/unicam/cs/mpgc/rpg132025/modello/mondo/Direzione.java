package it.unicam.cs.mpgc.rpg132025.modello.mondo;

import java.util.Optional;

/**
 * Le quattro direzioni cardinali in cui le entità si muovono e guardano.
 * <p>
 * I valori sono dichiarati in senso orario: le rotazioni diventano semplici
 * operazioni sull'indice del valore.
 */
public enum Direzione {
    SU(0, -1),
    DESTRA(1, 0),
    GIU(0, 1),
    SINISTRA(-1, 0);

    private static final Direzione[] VALORI = values();

    private final int dx;
    private final int dy;

    Direzione(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /** @return spostamento sulle colonne (-1, 0 o 1) */
    public int dx() {
        return dx;
    }

    /** @return spostamento sulle righe (-1, 0 o 1) */
    public int dy() {
        return dy;
    }

    /** @return la direzione opposta */
    public Direzione opposta() {
        return VALORI[(ordinal() + 2) % VALORI.length];
    }

    /** @return la direzione ottenuta ruotando di 90 gradi in senso orario */
    public Direzione ruotataOraria() {
        return VALORI[(ordinal() + 1) % VALORI.length];
    }

    /** @return la direzione ottenuta ruotando di 90 gradi in senso antiorario */
    public Direzione ruotataAntioraria() {
        return VALORI[(ordinal() + VALORI.length - 1) % VALORI.length];
    }

    /** @return {@code true} per DESTRA e SINISTRA */
    public boolean orizzontale() {
        return dx != 0;
    }

    /**
     * Direzione prevalente per andare da una casella a un'altra.
     *
     * @param da casella di partenza
     * @param a  casella di arrivo
     * @return la direzione lungo l'asse con la differenza maggiore, vuota se le caselle coincidono
     */
    public static Optional<Direzione> verso(Posizione da, Posizione a) {
        return verso(a.x() - da.x(), a.y() - da.y());
    }

    /**
     * Direzione prevalente del vettore indicato.
     *
     * @param dx componente orizzontale
     * @param dy componente verticale
     * @return la direzione dominante, vuota per il vettore nullo
     */
    public static Optional<Direzione> verso(double dx, double dy) {
        if (dx == 0 && dy == 0) {
            return Optional.empty();
        }
        if (Math.abs(dx) >= Math.abs(dy)) {
            return Optional.of(dx > 0 ? DESTRA : SINISTRA);
        }
        return Optional.of(dy > 0 ? GIU : SU);
    }
}
