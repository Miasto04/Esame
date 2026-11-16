package it.unicam.cs.mpgc.rpg132025.vista.schermate;

import it.unicam.cs.mpgc.rpg132025.vista.Schermo;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.FontPixel;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Grafica;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Pennello;

import java.util.Objects;

import static it.unicam.cs.mpgc.rpg132025.vista.grafica.Tavolozza.*;

/**
 * Vista del menu principale: titolo, panoramica dell'isola, pulsanti e una riga in basso con
 * un suggerimento oppure un avviso.
 */
public final class SchermataTitolo implements Schermata {

    private static final String TITOLO = "LA TANA DEL DRAGO";

    private final Grafica grafica;
    private final SfondoAnimato sfondo;
    private final DatiMenu menu;
    private String avviso;

    /**
     * @param grafica risorse grafiche
     * @param sfondo  panoramica animata dell'isola
     * @param menu    pulsanti da disegnare
     */
    public SchermataTitolo(Grafica grafica, SfondoAnimato sfondo, DatiMenu menu) {
        this.grafica = Objects.requireNonNull(grafica, "grafica");
        this.sfondo = Objects.requireNonNull(sfondo, "sfondo");
        this.menu = Objects.requireNonNull(menu, "menu");
    }

    /**
     * @param testo avviso da mostrare in basso, oppure {@code null} per tornare al suggerimento
     */
    public void mostraAvviso(String testo) {
        this.avviso = testo;
    }

    @Override
    public void disegna(Pennello pennello) {
        FontPixel font = grafica.font();
        sfondo.disegna(pennello);
        int centro = Schermo.LARGHEZZA / 2;
        int ondeggio = (int) Math.round(Math.sin(sfondo.tempo() * 1.5) * 1.5);
        font.scriviCentrato(pennello, TITOLO, centro + 1, 19 + ondeggio, CONTORNO, CONTORNO, 2);
        font.scriviCentrato(pennello, TITOLO, centro, 18 + ondeggio, ORO, BOCCA, 2);
        font.scriviCentrato(pennello, "Un'avventura con il pupo", centro, 40, BIANCO, CONTORNO, 1);
        DisegnatoreMenu.disegna(pennello, font, menu, sfondo.tempo());
        if (avviso != null) {
            font.scriviCentrato(pennello, avviso, centro, 168, ROSSO_DANNO, CONTORNO, 1);
        } else {
            font.scriviCentrato(pennello, "Frecce e Invio per scegliere, oppure il mouse", centro, 168,
                    PANNELLO_CHIARO, CONTORNO, 1);
        }
    }
}
