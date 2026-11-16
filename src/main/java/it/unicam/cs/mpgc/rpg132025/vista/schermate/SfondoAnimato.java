package it.unicam.cs.mpgc.rpg132025.vista.schermate;

import it.unicam.cs.mpgc.rpg132025.modello.partita.VistaPartita;
import it.unicam.cs.mpgc.rpg132025.vista.Schermo;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Camera;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.DisegnatoreMondo;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.EffettiVisivi;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Grafica;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Pennello;

import static it.unicam.cs.mpgc.rpg132025.vista.grafica.Tavolozza.*;

/**
 * Sfondo dei menu: una panoramica lenta dell'isola, con il drago che dorme.
 * È condiviso tra le schermate dei menu, così passando dall'una all'altra
 * l'inquadratura non ricomincia.
 */
public final class SfondoAnimato {

    private final VistaPartita partita;
    private final Camera camera = new Camera();
    private final EffettiVisivi effetti = new EffettiVisivi(camera);
    private final DisegnatoreMondo mondo;
    private double tempo;

    /**
     * @param grafica  risorse grafiche
     * @param panorama partita (mai aggiornata) da usare come scenario, in sola lettura
     */
    public SfondoAnimato(Grafica grafica, VistaPartita panorama) {
        this.partita = panorama;
        this.mondo = new DisegnatoreMondo(grafica);
        posizionaCamera(0, true);
    }

    /**
     * @param dt secondi trascorsi
     */
    public void aggiorna(double dt) {
        tempo += dt;
        effetti.aggiorna(dt);
        posizionaCamera(dt, false);
    }

    /**
     * Disegna lo scenario leggermente scurito, per far risaltare i menu.
     *
     * @param pennello superficie
     */
    public void disegna(Pennello pennello) {
        mondo.disegna(pennello, partita, camera, effetti);
        pennello.rettangolo(0, 0, pennello.larghezza(), pennello.altezza(), conAlfa(rgb(10, 12, 30), 120));
    }

    /** @return orologio delle animazioni */
    public double tempo() {
        return tempo;
    }

    private void posizionaCamera(double dt, boolean subito) {
        int larghezzaMondo = partita.mappa().larghezza() * Schermo.CASELLA;
        int altezzaMondo = partita.mappa().altezza() * Schermo.CASELLA;
        double centroX = larghezzaMondo / 2.0 + Math.sin(tempo * 0.05) * (larghezzaMondo / 2.0 - Schermo.LARGHEZZA / 2.0);
        double centroY = 150 + Math.sin(tempo * 0.037) * 90;
        if (subito) {
            camera.centraSu(centroX, centroY, Schermo.LARGHEZZA, Schermo.ALTEZZA, larghezzaMondo, altezzaMondo);
        } else {
            camera.segui(centroX, centroY, Schermo.LARGHEZZA, Schermo.ALTEZZA, larghezzaMondo, altezzaMondo, dt);
        }
    }
}
