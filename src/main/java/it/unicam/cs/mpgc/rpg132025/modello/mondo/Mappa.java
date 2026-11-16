package it.unicam.cs.mpgc.rpg132025.modello.mondo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Griglia immutabile di terreni su cui si svolge il gioco.
 * <p>
 * Tutto ciò che sta fuori dalla griglia è considerato mare: non è percorribile
 * e viene disegnato come acqua.
 */
public final class Mappa {

    private final Terreno[][] celle;
    private final int larghezza;
    private final int altezza;

    /**
     * Crea una mappa copiando la griglia ricevuta.
     *
     * @param celle griglia indicizzata come {@code celle[riga][colonna]}; tutte le righe devono avere la stessa lunghezza
     * @throws IllegalArgumentException se la griglia è vuota o non rettangolare
     */
    public Mappa(Terreno[][] celle) {
        Objects.requireNonNull(celle, "celle");
        if (celle.length == 0 || celle[0].length == 0) {
            throw new IllegalArgumentException("La mappa non può essere vuota");
        }
        this.altezza = celle.length;
        this.larghezza = celle[0].length;
        this.celle = new Terreno[altezza][];
        for (int y = 0; y < altezza; y++) {
            if (celle[y].length != larghezza) {
                throw new IllegalArgumentException("Tutte le righe della mappa devono avere la stessa lunghezza");
            }
            this.celle[y] = Arrays.copyOf(celle[y], larghezza);
            for (Terreno terreno : this.celle[y]) {
                Objects.requireNonNull(terreno, "La mappa contiene una casella vuota");
            }
        }
    }

    /** @return numero di colonne */
    public int larghezza() {
        return larghezza;
    }

    /** @return numero di righe */
    public int altezza() {
        return altezza;
    }

    /**
     * @param posizione casella da controllare
     * @return {@code true} se la casella è dentro la mappa
     */
    public boolean contiene(Posizione posizione) {
        return posizione.x() >= 0 && posizione.y() >= 0 && posizione.x() < larghezza && posizione.y() < altezza;
    }

    /**
     * @param posizione casella richiesta
     * @return il terreno della casella, {@link Terreno#ACQUA} se è fuori dalla mappa
     */
    public Terreno terreno(Posizione posizione) {
        return contiene(posizione) ? celle[posizione.y()][posizione.x()] : Terreno.ACQUA;
    }

    /**
     * @param posizione casella da controllare
     * @return {@code true} se la casella esiste e il suo terreno è percorribile
     */
    public boolean percorribile(Posizione posizione) {
        return contiene(posizione) && celle[posizione.y()][posizione.x()].percorribile();
    }

    /**
     * @param terreno terreno cercato
     * @return tutte le caselle con quel terreno, per righe
     */
    public List<Posizione> posizioniDi(Terreno terreno) {
        List<Posizione> trovate = new ArrayList<>();
        for (int y = 0; y < altezza; y++) {
            for (int x = 0; x < larghezza; x++) {
                if (celle[y][x] == terreno) {
                    trovate.add(new Posizione(x, y));
                }
            }
        }
        return trovate;
    }
}
