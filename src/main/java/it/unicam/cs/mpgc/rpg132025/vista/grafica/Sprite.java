package it.unicam.cs.mpgc.rpg132025.vista.grafica;

/**
 * Un'immagine immutabile di pixel ARGB, indipendente dalla piattaforma.
 * <p>
 * Ogni piattaforma la converte una sola volta nel proprio formato e la tiene in
 * cache: per questo gli sprite vanno creati una volta e riusati.
 */
public final class Sprite {

    private final int larghezza;
    private final int altezza;
    private final int[] pixel;

    /**
     * @param larghezza larghezza in pixel
     * @param altezza   altezza in pixel
     * @param pixel     colori ARGB riga per riga (viene copiato)
     */
    public Sprite(int larghezza, int altezza, int[] pixel) {
        if (larghezza <= 0 || altezza <= 0 || pixel.length != larghezza * altezza) {
            throw new IllegalArgumentException("Dimensioni dello sprite non valide");
        }
        this.larghezza = larghezza;
        this.altezza = altezza;
        this.pixel = pixel.clone();
    }

    /** @return larghezza in pixel */
    public int larghezza() {
        return larghezza;
    }

    /** @return altezza in pixel */
    public int altezza() {
        return altezza;
    }

    /**
     * @param x colonna
     * @param y riga
     * @return il colore ARGB del pixel
     */
    public int pixel(int x, int y) {
        return pixel[y * larghezza + x];
    }

    /** @return una copia di tutti i pixel, riga per riga */
    public int[] copiaPixel() {
        return pixel.clone();
    }

    /** @return lo sprite specchiato da sinistra a destra */
    public Sprite specchiata() {
        int[] nuovi = new int[pixel.length];
        for (int y = 0; y < altezza; y++) {
            for (int x = 0; x < larghezza; x++) {
                nuovi[y * larghezza + x] = pixel[y * larghezza + (larghezza - 1 - x)];
            }
        }
        return new Sprite(larghezza, altezza, nuovi);
    }

    /** @return lo sprite ruotato di 90 gradi in senso orario */
    public Sprite ruotata() {
        int[] nuovi = new int[pixel.length];
        for (int y = 0; y < altezza; y++) {
            for (int x = 0; x < larghezza; x++) {
                int nuovaX = altezza - 1 - y;
                nuovi[x * altezza + nuovaX] = pixel[y * larghezza + x];
            }
        }
        return new Sprite(altezza, larghezza, nuovi);
    }

    /**
     * @param volte quante rotazioni di 90 gradi in senso orario applicare
     * @return lo sprite ruotato
     */
    public Sprite ruotata(int volte) {
        Sprite risultato = this;
        for (int i = 0; i < Math.floorMod(volte, 4); i++) {
            risultato = risultato.ruotata();
        }
        return risultato;
    }

    /**
     * @param colore colore RGB da usare (l'alfa di ogni pixel resta quello originale)
     * @return la sagoma dello sprite in tinta unita
     */
    public Sprite tinta(int colore) {
        int[] nuovi = new int[pixel.length];
        for (int i = 0; i < pixel.length; i++) {
            int alfa = pixel[i] >>> 24;
            nuovi[i] = alfa == 0 ? 0 : (alfa << 24) | (colore & 0x00FFFFFF);
        }
        return new Sprite(larghezza, altezza, nuovi);
    }
}
