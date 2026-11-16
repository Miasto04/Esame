package it.unicam.cs.mpgc.rpg132025.vista.grafica;

/**
 * Rettangolo in pixel dello schermo virtuale.
 *
 * @param x         ascissa dell'angolo in alto a sinistra
 * @param y         ordinata dell'angolo in alto a sinistra
 * @param larghezza larghezza in pixel
 * @param altezza   altezza in pixel
 */
public record Riquadro(int x, int y, int larghezza, int altezza) {

    public Riquadro {
        if (larghezza < 0 || altezza < 0) {
            throw new IllegalArgumentException("Le dimensioni non possono essere negative");
        }
    }

    /**
     * @param altro un altro rettangolo
     * @return {@code true} se i due rettangoli hanno almeno un pixel in comune
     */
    public boolean interseca(Riquadro altro) {
        return x < altro.x + altro.larghezza && altro.x < x + larghezza
                && y < altro.y + altro.altezza && altro.y < y + altezza;
    }

    /**
     * @param px ascissa del punto
     * @param py ordinata del punto
     * @return {@code true} se il punto cade dentro il rettangolo
     */
    public boolean contiene(double px, double py) {
        return px >= x && px < x + larghezza && py >= y && py < y + altezza;
    }

    /**
     * @param larghezzaSchermo larghezza dello schermo
     * @param altezzaSchermo   altezza dello schermo
     * @return {@code true} se il rettangolo è tutto dentro lo schermo
     */
    public boolean dentro(int larghezzaSchermo, int altezzaSchermo) {
        return x >= 0 && y >= 0 && x + larghezza <= larghezzaSchermo && y + altezza <= altezzaSchermo;
    }
}
