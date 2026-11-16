package it.unicam.cs.mpgc.rpg132025.vista.grafica;

/**
 * La porzione di mondo inquadrata, in pixel del mondo.
 * <p>
 * Segue un punto con un leggero ritardo, non esce dai bordi della mappa e può
 * tremare per qualche istante (colpi, atterraggi del drago).
 */
public final class Camera {

    private static final double MORBIDEZZA = 8.0;

    private double x;
    private double y;
    private double intensitaScossa;
    private double durataScossa;
    private double residuoScossa;
    private double fase;

    /**
     * Centra subito la camera su un punto.
     *
     * @param centroX       ascissa del punto in pixel del mondo
     * @param centroY       ordinata del punto in pixel del mondo
     * @param larghezzaVista larghezza inquadrata
     * @param altezzaVista   altezza inquadrata
     * @param larghezzaMondo larghezza del mondo
     * @param altezzaMondo   altezza del mondo
     */
    public void centraSu(double centroX, double centroY, int larghezzaVista, int altezzaVista,
                         int larghezzaMondo, int altezzaMondo) {
        x = limita(centroX - larghezzaVista / 2.0, larghezzaMondo - larghezzaVista);
        y = limita(centroY - altezzaVista / 2.0, altezzaMondo - altezzaVista);
    }

    /**
     * Avvicina gradualmente la camera a un punto.
     *
     * @param centroX       ascissa del punto da seguire
     * @param centroY       ordinata del punto da seguire
     * @param larghezzaVista larghezza inquadrata
     * @param altezzaVista   altezza inquadrata
     * @param larghezzaMondo larghezza del mondo
     * @param altezzaMondo   altezza del mondo
     * @param dt            secondi trascorsi
     */
    public void segui(double centroX, double centroY, int larghezzaVista, int altezzaVista,
                      int larghezzaMondo, int altezzaMondo, double dt) {
        double obiettivoX = limita(centroX - larghezzaVista / 2.0, larghezzaMondo - larghezzaVista);
        double obiettivoY = limita(centroY - altezzaVista / 2.0, altezzaMondo - altezzaVista);
        double fattore = 1 - Math.exp(-MORBIDEZZA * dt);
        x += (obiettivoX - x) * fattore;
        y += (obiettivoY - y) * fattore;
        aggiornaScossa(dt);
    }

    /**
     * Fa tremare l'inquadratura.
     *
     * @param intensita ampiezza massima in pixel
     * @param durata    secondi
     */
    public void scuoti(double intensita, double durata) {
        if (intensita >= intensitaScossa * residuoScossa / Math.max(durataScossa, 1e-9)) {
            intensitaScossa = intensita;
            durataScossa = durata;
            residuoScossa = durata;
        }
    }

    /** @return bordo sinistro dell'inquadratura, già comprensivo della scossa */
    public int x() {
        return (int) Math.round(x + scostamento(0));
    }

    /** @return bordo superiore dell'inquadratura, già comprensivo della scossa */
    public int y() {
        return (int) Math.round(y + scostamento(1.7));
    }

    private void aggiornaScossa(double dt) {
        fase += dt * 45;
        residuoScossa = Math.max(0, residuoScossa - dt);
    }

    private double scostamento(double sfasamento) {
        if (residuoScossa <= 0 || durataScossa <= 0) {
            return 0;
        }
        return Math.sin(fase + sfasamento) * intensitaScossa * (residuoScossa / durataScossa);
    }

    /**
     * Calcola, lungo un asse, dove centrare l'inquadratura perché due oggetti stiano entrambi nella
     * fascia utile dello schermo (quella non coperta dall'interfaccia). Finché è possibile resta
     * centrata sul primo; se i due sono troppo lontani il primo ha la precedenza e l'inquadratura si
     * sposta il più possibile verso il secondo.
     *
     * @param primo         centro del primo oggetto, in pixel del mondo
     * @param raggioPrimo   metà della dimensione del primo oggetto
     * @param secondo       centro del secondo oggetto, in pixel del mondo
     * @param raggioSecondo metà della dimensione del secondo oggetto
     * @param vista         dimensione dell'inquadratura lungo l'asse
     * @param inizioUtile   inizio della fascia utile, in pixel dello schermo
     * @param fineUtile     fine della fascia utile, in pixel dello schermo
     * @return il centro dell'inquadratura lungo l'asse, in pixel del mondo
     */
    public static double centroPerDue(double primo, double raggioPrimo, double secondo, double raggioSecondo,
                                      int vista, int inizioUtile, int fineUtile) {
        double meta = vista / 2.0;
        double minimoPrimo = primo + meta - fineUtile + raggioPrimo;
        double massimoPrimo = primo + meta - inizioUtile - raggioPrimo;
        double minimo = Math.max(minimoPrimo, secondo + meta - fineUtile + raggioSecondo);
        double massimo = Math.min(massimoPrimo, secondo + meta - inizioUtile - raggioSecondo);
        if (minimo <= massimo) {
            return Math.clamp(primo, minimo, massimo);
        }
        return Math.max(minimoPrimo, Math.min(massimoPrimo, secondo));
    }

    private static double limita(double valore, double massimo) {
        if (massimo <= 0) {
            return massimo / 2;
        }
        return Math.clamp(valore, 0, massimo);
    }
}
