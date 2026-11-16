package it.unicam.cs.mpgc.rpg132025.vista.grafica;

/**
 * Superficie su cui la vista disegna, in coordinate dello schermo virtuale.
 * <p>
 * È l'unico punto di contatto tra la grafica del gioco e la tecnologia che la
 * mostra davvero: JavaFX oggi, in futuro un Canvas Android o HTML. Per portare
 * il gioco su una nuova piattaforma basta implementare questa interfaccia.
 * I colori sono interi nel formato ARGB ({@code 0xAARRGGBB}).
 */
public interface Pennello {

    /** @return larghezza della superficie in pixel virtuali */
    int larghezza();

    /** @return altezza della superficie in pixel virtuali */
    int altezza();

    /**
     * Riempie un rettangolo.
     *
     * @param x         bordo sinistro
     * @param y         bordo superiore
     * @param larghezza larghezza
     * @param altezza   altezza
     * @param colore    colore ARGB (la trasparenza è rispettata)
     */
    void rettangolo(int x, int y, int larghezza, int altezza, int colore);

    /**
     * Disegna uno sprite a grandezza naturale.
     *
     * @param sprite immagine da disegnare
     * @param x      bordo sinistro
     * @param y      bordo superiore
     */
    void sprite(Sprite sprite, int x, int y);

    /**
     * Disegna uno sprite ingrandito, mantenendo i pixel netti.
     *
     * @param sprite immagine da disegnare
     * @param x      bordo sinistro
     * @param y      bordo superiore
     * @param scala  fattore di ingrandimento intero
     */
    void sprite(Sprite sprite, int x, int y, int scala);

    /**
     * Imposta l'opacità dei disegni successivi.
     *
     * @param opacita da 0 (invisibile) a 1 (pieno)
     */
    void opacita(double opacita);
}
