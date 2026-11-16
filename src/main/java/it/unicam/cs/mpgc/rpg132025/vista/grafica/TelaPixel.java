package it.unicam.cs.mpgc.rpg132025.vista.grafica;

import java.util.HashMap;
import java.util.Map;

/**
 * Tela modificabile su cui si compongono gli sprite (pattern Builder).
 * <p>
 * Offre le primitive della pixel art: punti, rettangoli, ellissi, poligoni,
 * tratti e disegni scritti come griglie di caratteri. I punti fuori dalla tela
 * vengono ignorati.
 */
public final class TelaPixel {

    private final int larghezza;
    private final int altezza;
    private final int[] pixel;

    /**
     * @param larghezza larghezza in pixel
     * @param altezza   altezza in pixel
     */
    public TelaPixel(int larghezza, int altezza) {
        if (larghezza <= 0 || altezza <= 0) {
            throw new IllegalArgumentException("Dimensioni della tela non valide");
        }
        this.larghezza = larghezza;
        this.altezza = altezza;
        this.pixel = new int[larghezza * altezza];
    }

    /**
     * Costruisce una tavolozza per {@link #disegna}: il carattere i-esimo di {@code simboli}
     * corrisponde al colore i-esimo.
     *
     * @param simboli caratteri usati nel disegno
     * @param colori  colori ARGB, uno per carattere
     * @return la mappa carattere-colore
     */
    public static Map<Character, Integer> colori(String simboli, int... colori) {
        if (simboli.length() != colori.length) {
            throw new IllegalArgumentException("Serve un colore per ogni simbolo");
        }
        Map<Character, Integer> mappa = new HashMap<>();
        for (int i = 0; i < colori.length; i++) {
            mappa.put(simboli.charAt(i), colori[i]);
        }
        return mappa;
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
     * @return il colore del pixel, trasparente se fuori dalla tela
     */
    public int colore(int x, int y) {
        return dentro(x, y) ? pixel[y * larghezza + x] : Tavolozza.TRASPARENTE;
    }

    /**
     * Colora un pixel.
     *
     * @param x      colonna
     * @param y      riga
     * @param colore colore ARGB (0 per cancellare)
     */
    public void punto(int x, int y, int colore) {
        if (dentro(x, y)) {
            pixel[y * larghezza + x] = colore;
        }
    }

    /** Riempie un rettangolo. */
    public void rettangolo(int x, int y, int larghezzaRettangolo, int altezzaRettangolo, int colore) {
        for (int riga = y; riga < y + altezzaRettangolo; riga++) {
            for (int colonna = x; colonna < x + larghezzaRettangolo; colonna++) {
                punto(colonna, riga, colore);
            }
        }
    }

    /** Riempie un'ellisse di centro (cx, cy) e semiassi rx, ry. */
    public void ellisse(double cx, double cy, double rx, double ry, int colore) {
        dipingiEllisse(cx, cy, rx, ry, colore, false);
    }

    /** Come {@link #ellisse}, ma colora solo i pixel ancora trasparenti (utile per le ombre). */
    public void ellisseSotto(double cx, double cy, double rx, double ry, int colore) {
        dipingiEllisse(cx, cy, rx, ry, colore, true);
    }

    /**
     * Riempie un poligono dati i vertici in ordine.
     *
     * @param xs ascisse dei vertici
     * @param ys ordinate dei vertici
     * @param colore colore ARGB
     */
    public void poligono(double[] xs, double[] ys, int colore) {
        if (xs.length != ys.length || xs.length < 3) {
            throw new IllegalArgumentException("Un poligono ha almeno tre vertici");
        }
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        for (int i = 0; i < xs.length; i++) {
            minX = Math.min(minX, xs[i]);
            maxX = Math.max(maxX, xs[i]);
            minY = Math.min(minY, ys[i]);
            maxY = Math.max(maxY, ys[i]);
        }
        for (int y = (int) Math.floor(minY); y <= (int) Math.ceil(maxY); y++) {
            for (int x = (int) Math.floor(minX); x <= (int) Math.ceil(maxX); x++) {
                if (contienePunto(xs, ys, x + 0.5, y + 0.5)) {
                    punto(x, y, colore);
                }
            }
        }
    }

    /** Disegna una linea sottile (algoritmo di Bresenham). */
    public void linea(int x0, int y0, int x1, int y1, int colore) {
        int dx = Math.abs(x1 - x0);
        int dy = -Math.abs(y1 - y0);
        int passoX = x0 < x1 ? 1 : -1;
        int passoY = y0 < y1 ? 1 : -1;
        int errore = dx + dy;
        int x = x0;
        int y = y0;
        while (true) {
            punto(x, y, colore);
            if (x == x1 && y == y1) {
                return;
            }
            int doppio = 2 * errore;
            if (doppio >= dy) {
                errore += dy;
                x += passoX;
            }
            if (doppio <= dx) {
                errore += dx;
                y += passoY;
            }
        }
    }

    /**
     * Disegna un tratto spesso che si assottiglia da un raggio all'altro (code, ali, foglie).
     */
    public void tratto(double x0, double y0, double x1, double y1, double raggioIniziale, double raggioFinale,
                       int colore) {
        double lunghezza = Math.hypot(x1 - x0, y1 - y0);
        int passi = Math.max(1, (int) Math.ceil(lunghezza * 2));
        for (int i = 0; i <= passi; i++) {
            double t = (double) i / passi;
            double raggio = raggioIniziale + (raggioFinale - raggioIniziale) * t;
            ellisse(x0 + (x1 - x0) * t, y0 + (y1 - y0) * t, raggio, raggio, colore);
        }
    }

    /**
     * Copia sulla tela un disegno scritto con caratteri; '.' e ' ' sono trasparenti.
     *
     * @param righe  righe del disegno
     * @param colori colore di ogni carattere
     * @param x      colonna in cui inizia il disegno
     * @param y      riga in cui inizia il disegno
     */
    public void disegna(String[] righe, Map<Character, Integer> colori, int x, int y) {
        for (int riga = 0; riga < righe.length; riga++) {
            String testo = righe[riga];
            for (int colonna = 0; colonna < testo.length(); colonna++) {
                char simbolo = testo.charAt(colonna);
                if (simbolo == '.' || simbolo == ' ') {
                    continue;
                }
                Integer colore = colori.get(simbolo);
                if (colore == null) {
                    throw new IllegalArgumentException("Nessun colore per il simbolo '" + simbolo + "'");
                }
                punto(x + colonna, y + riga, colore);
            }
        }
    }

    /** Aggiunge un contorno di un pixel attorno a tutto ciò che è già disegnato. */
    public void contorno(int colore) {
        int[] originali = pixel.clone();
        for (int y = 0; y < altezza; y++) {
            for (int x = 0; x < larghezza; x++) {
                if (originali[y * larghezza + x] != 0) {
                    continue;
                }
                if (pieno(originali, x - 1, y) || pieno(originali, x + 1, y)
                        || pieno(originali, x, y - 1) || pieno(originali, x, y + 1)) {
                    pixel[y * larghezza + x] = colore;
                }
            }
        }
    }

    /** Sostituisce un colore con un altro in tutta la tela. */
    public void sostituisci(int vecchio, int nuovo) {
        for (int i = 0; i < pixel.length; i++) {
            if (pixel[i] == vecchio) {
                pixel[i] = nuovo;
            }
        }
    }

    /** @return lo sprite con il contenuto attuale della tela */
    public Sprite crea() {
        return new Sprite(larghezza, altezza, pixel);
    }

    private void dipingiEllisse(double cx, double cy, double rx, double ry, int colore, boolean soloVuoti) {
        if (rx <= 0 || ry <= 0) {
            return;
        }
        for (int y = (int) Math.floor(cy - ry); y <= (int) Math.ceil(cy + ry); y++) {
            for (int x = (int) Math.floor(cx - rx); x <= (int) Math.ceil(cx + rx); x++) {
                double dx = (x + 0.5 - cx) / rx;
                double dy = (y + 0.5 - cy) / ry;
                if (dx * dx + dy * dy <= 1.0 && (!soloVuoti || colore(x, y) == Tavolozza.TRASPARENTE)) {
                    punto(x, y, colore);
                }
            }
        }
    }

    private boolean pieno(int[] valori, int x, int y) {
        return dentro(x, y) && valori[y * larghezza + x] != 0;
    }

    private boolean dentro(int x, int y) {
        return x >= 0 && y >= 0 && x < larghezza && y < altezza;
    }

    private static boolean contienePunto(double[] xs, double[] ys, double px, double py) {
        boolean dentro = false;
        for (int i = 0, j = xs.length - 1; i < xs.length; j = i++) {
            boolean attraversa = (ys[i] > py) != (ys[j] > py)
                    && px < (xs[j] - xs[i]) * (py - ys[i]) / (ys[j] - ys[i]) + xs[i];
            if (attraversa) {
                dentro = !dentro;
            }
        }
        return dentro;
    }
}
