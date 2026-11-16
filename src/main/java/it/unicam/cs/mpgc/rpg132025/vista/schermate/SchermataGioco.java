package it.unicam.cs.mpgc.rpg132025.vista.schermate;

import it.unicam.cs.mpgc.rpg132025.input.ComandiTouch;
import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.modello.drago.VistaDrago;
import it.unicam.cs.mpgc.rpg132025.modello.partita.VistaPartita;
import it.unicam.cs.mpgc.rpg132025.vista.Schermo;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Camera;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.DisegnatoreHud;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.DisegnatoreMondo;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.EffettiVisivi;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Grafica;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Pennello;

import java.util.Objects;

/**
 * Vista della partita: il mondo inquadrato dalla camera, gli effetti e l'interfaccia sopra.
 * <p>
 * Legge la partita solo come {@link VistaPartita} e ne osserva gli eventi con gli
 * {@link EffettiVisivi}; non la modifica mai. Sceglie anche l'inquadratura: segue l'eroe e,
 * durante il combattimento, si sposta quanto basta per mostrare anche il drago.
 */
public final class SchermataGioco implements Schermata {

    private static final int C = Schermo.CASELLA;
    /** Entro questa distanza (in caselle) l'inquadratura cerca di mostrare anche il drago. */
    private static final int DISTANZA_INQUADRATURA = 9;
    /** Metà dell'ingombro dell'eroe e del drago, in pixel, per tenerli dentro lo schermo. */
    private static final double RAGGIO_EROE = 10;
    private static final double RAGGIO_DRAGO = 20;
    /** Fascia dello schermo non coperta dall'interfaccia, in pixel. */
    private static final int MARGINE_LATI = 8;
    private static final int INIZIO_UTILE_Y = 20;
    private static final int FINE_UTILE_Y = 156;

    private final Grafica grafica;
    private final VistaPartita partita;
    private final Camera camera = new Camera();
    private final EffettiVisivi effetti = new EffettiVisivi(camera);
    private final DisegnatoreMondo mondo;
    private final DisegnatoreHud hud;
    private StatoInput comandiPremuti = new StatoInput();

    /**
     * @param grafica      risorse grafiche
     * @param partita      la partita da mostrare, in sola lettura
     * @param comandiTouch posizione dei comandi sullo schermo
     */
    public SchermataGioco(Grafica grafica, VistaPartita partita, ComandiTouch comandiTouch) {
        this.grafica = Objects.requireNonNull(grafica, "grafica");
        this.partita = Objects.requireNonNull(partita, "partita");
        this.mondo = new DisegnatoreMondo(grafica);
        this.hud = new DisegnatoreHud(grafica, comandiTouch);
        Punto punto = puntoDaInquadrare();
        camera.centraSu(punto.x(), punto.y(), Schermo.LARGHEZZA, Schermo.ALTEZZA, larghezzaMondo(), altezzaMondo());
    }

    /** Inizia a osservare gli eventi della partita (numeri del danno, messaggi, scosse). */
    public void collega() {
        effetti.collega(partita);
    }

    /** Smette di osservare gli eventi della partita. */
    public void scollega() {
        effetti.scollega(partita);
    }

    /**
     * Fa avanzare animazioni, effetti e inquadratura.
     *
     * @param dt      secondi trascorsi
     * @param comandi stato dei comandi, usato solo per evidenziare i pulsanti premuti
     */
    public void aggiorna(double dt, StatoInput comandi) {
        this.comandiPremuti = Objects.requireNonNull(comandi, "comandi");
        effetti.aggiorna(dt);
        Punto punto = puntoDaInquadrare();
        camera.segui(punto.x(), punto.y(), Schermo.LARGHEZZA, Schermo.ALTEZZA, larghezzaMondo(), altezzaMondo(),
                dt);
    }

    @Override
    public void disegna(Pennello pennello) {
        mondo.disegna(pennello, partita, camera, effetti);
        effetti.disegnaNelMondo(pennello, grafica.font());
        hud.disegna(pennello, partita, comandiPremuti, effetti.tempo());
        effetti.disegnaSopra(pennello, grafica.font());
    }

    /**
     * Il punto da tenere al centro dello schermo: l'eroe, ma durante il combattimento spostato
     * quanto basta perché si veda anche il drago.
     */
    private Punto puntoDaInquadrare() {
        double eroeX = partita.eroe().xVisivo() * C + C / 2.0;
        double eroeY = partita.eroe().yVisivo() * C + C / 2.0;
        VistaDrago drago = partita.drago();
        if (!drago.isSveglio() || !drago.isViva()
                || drago.distanzaDa(partita.eroe().posizione()) > DISTANZA_INQUADRATURA) {
            return new Punto(eroeX, eroeY);
        }
        double dragoX = (drago.xVisivo() + VistaDrago.DIMENSIONE / 2.0) * C;
        double dragoY = (drago.yVisivo() + VistaDrago.DIMENSIONE / 2.0) * C;
        return new Punto(
                Camera.centroPerDue(eroeX, RAGGIO_EROE, dragoX, RAGGIO_DRAGO, Schermo.LARGHEZZA, MARGINE_LATI,
                        Schermo.LARGHEZZA - MARGINE_LATI),
                Camera.centroPerDue(eroeY, RAGGIO_EROE, dragoY, RAGGIO_DRAGO, Schermo.ALTEZZA, INIZIO_UTILE_Y,
                        FINE_UTILE_Y));
    }

    private int larghezzaMondo() {
        return partita.mappa().larghezza() * C;
    }

    private int altezzaMondo() {
        return partita.mappa().altezza() * C;
    }

    /** Un punto del mondo in pixel. */
    private record Punto(double x, double y) {
    }
}
