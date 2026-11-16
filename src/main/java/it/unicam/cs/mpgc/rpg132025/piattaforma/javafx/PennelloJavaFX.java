package it.unicam.cs.mpgc.rpg132025.piattaforma.javafx;

import it.unicam.cs.mpgc.rpg132025.vista.Schermo;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Pennello;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Sprite;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Implementazione di {@link Pennello} su un Canvas JavaFX (pattern Adapter).
 * <p>
 * Lo schermo virtuale 320x180 viene ingrandito del massimo fattore intero che
 * entra nella finestra e centrato; con lo smussamento disattivato i pixel
 * restano netti. Ogni {@link Sprite} viene convertito in immagine JavaFX una
 * sola volta e i colori sono tenuti in cache.
 */
final class PennelloJavaFX implements Pennello {

    private final GraphicsContext contesto;
    private final Map<Sprite, Image> immagini = new IdentityHashMap<>();
    private final Map<Integer, Color> colori = new HashMap<>();
    private double scala = 1;
    private double origineX;
    private double origineY;

    /**
     * @param contesto contesto grafico del canvas
     */
    PennelloJavaFX(GraphicsContext contesto) {
        this.contesto = contesto;
    }

    /**
     * Prepara un nuovo fotogramma: pulisce il canvas e imposta la trasformazione.
     *
     * @param larghezza larghezza del canvas in pixel
     * @param altezza   altezza del canvas in pixel
     */
    void iniziaFotogramma(double larghezza, double altezza) {
        double adatta = Math.min(larghezza / Schermo.LARGHEZZA, altezza / Schermo.ALTEZZA);
        scala = adatta >= 1 ? Math.floor(adatta) : Math.max(adatta, 0.1);
        origineX = Math.floor((larghezza - Schermo.LARGHEZZA * scala) / 2);
        origineY = Math.floor((altezza - Schermo.ALTEZZA * scala) / 2);
        contesto.setGlobalAlpha(1.0);
        contesto.setFill(Color.BLACK);
        contesto.fillRect(0, 0, larghezza, altezza);
        contesto.save();
        contesto.translate(origineX, origineY);
        contesto.scale(scala, scala);
        contesto.setImageSmoothing(false);
    }

    /** Chiude il fotogramma ripristinando la trasformazione. */
    void terminaFotogramma() {
        contesto.restore();
        contesto.setGlobalAlpha(1.0);
    }

    /** @return l'ascissa virtuale corrispondente a un punto del canvas */
    double xVirtuale(double xCanvas) {
        return (xCanvas - origineX) / scala;
    }

    /** @return l'ordinata virtuale corrispondente a un punto del canvas */
    double yVirtuale(double yCanvas) {
        return (yCanvas - origineY) / scala;
    }

    /**
     * @param sprite sprite da convertire
     * @return l'immagine JavaFX corrispondente (creata una sola volta)
     */
    Image immagine(Sprite sprite) {
        return immagini.computeIfAbsent(sprite, PennelloJavaFX::converti);
    }

    @Override
    public int larghezza() {
        return Schermo.LARGHEZZA;
    }

    @Override
    public int altezza() {
        return Schermo.ALTEZZA;
    }

    @Override
    public void rettangolo(int x, int y, int larghezza, int altezza, int colore) {
        contesto.setFill(colore(colore));
        contesto.fillRect(x, y, larghezza, altezza);
    }

    @Override
    public void sprite(Sprite sprite, int x, int y) {
        contesto.drawImage(immagine(sprite), x, y);
    }

    @Override
    public void sprite(Sprite sprite, int x, int y, int ingrandimento) {
        contesto.drawImage(immagine(sprite), x, y, sprite.larghezza() * (double) ingrandimento,
                sprite.altezza() * (double) ingrandimento);
    }

    @Override
    public void opacita(double opacita) {
        contesto.setGlobalAlpha(Math.clamp(opacita, 0.0, 1.0));
    }

    private Color colore(int argb) {
        return colori.computeIfAbsent(argb, valore -> Color.rgb((valore >> 16) & 0xFF, (valore >> 8) & 0xFF,
                valore & 0xFF, ((valore >>> 24) & 0xFF) / 255.0));
    }

    private static Image converti(Sprite sprite) {
        WritableImage immagine = new WritableImage(sprite.larghezza(), sprite.altezza());
        PixelWriter scrittore = immagine.getPixelWriter();
        for (int y = 0; y < sprite.altezza(); y++) {
            for (int x = 0; x < sprite.larghezza(); x++) {
                scrittore.setArgb(x, y, sprite.pixel(x, y));
            }
        }
        return immagine;
    }
}
