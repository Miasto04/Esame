package it.unicam.cs.mpgc.rpg132025.modello.entita;

/**
 * Punti vita di una creatura, sempre compresi tra zero e il massimo.
 */
public final class PuntiVita {

    private final int massimi;
    private int correnti;

    /**
     * Crea punti vita pieni.
     *
     * @param massimi valore massimo, positivo
     */
    public PuntiVita(int massimi) {
        this(massimi, massimi);
    }

    /**
     * @param correnti valore iniziale, tra 0 e {@code massimi}
     * @param massimi  valore massimo, positivo
     * @throws IllegalArgumentException se i valori non sono coerenti
     */
    public PuntiVita(int correnti, int massimi) {
        if (massimi <= 0) {
            throw new IllegalArgumentException("I punti vita massimi devono essere positivi");
        }
        if (correnti < 0 || correnti > massimi) {
            throw new IllegalArgumentException("Punti vita fuori intervallo: " + correnti + "/" + massimi);
        }
        this.massimi = massimi;
        this.correnti = correnti;
    }

    /**
     * Toglie punti vita, senza scendere sotto zero.
     *
     * @param danno punti da togliere, non negativi
     * @return i punti effettivamente tolti
     */
    public int subisci(int danno) {
        if (danno < 0) {
            throw new IllegalArgumentException("Il danno non può essere negativo");
        }
        int effettivo = Math.min(danno, correnti);
        correnti -= effettivo;
        return effettivo;
    }

    /**
     * Aggiunge punti vita, senza superare il massimo.
     *
     * @param quantita punti da aggiungere, non negativi
     * @return i punti effettivamente aggiunti
     */
    public int cura(int quantita) {
        if (quantita < 0) {
            throw new IllegalArgumentException("La cura non può essere negativa");
        }
        int effettiva = Math.min(quantita, massimi - correnti);
        correnti += effettiva;
        return effettiva;
    }

    /** @return {@code true} se i punti vita sono a zero */
    public boolean esauriti() {
        return correnti == 0;
    }

    /** @return {@code true} se i punti vita sono al massimo */
    public boolean pieni() {
        return correnti == massimi;
    }

    /** @return frazione rimasta, da 0 a 1 */
    public double frazione() {
        return (double) correnti / massimi;
    }

    /** @return valore attuale */
    public int correnti() {
        return correnti;
    }

    /** @return valore massimo */
    public int massimi() {
        return massimi;
    }
}
