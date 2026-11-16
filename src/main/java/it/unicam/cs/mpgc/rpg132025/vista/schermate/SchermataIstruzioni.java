package it.unicam.cs.mpgc.rpg132025.vista.schermate;

import it.unicam.cs.mpgc.rpg132025.vista.Schermo;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.FontPixel;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Grafica;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Pannelli;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Pennello;

import java.util.List;
import java.util.Objects;

import static it.unicam.cs.mpgc.rpg132025.vista.grafica.Tavolozza.*;

/**
 * Vista delle istruzioni: i comandi e il meccanismo del pupo.
 */
public final class SchermataIstruzioni implements Schermata {

    private static final List<String> COMANDI = List.of(
            "Muoversi: frecce o W A S D (o la croce in basso)",
            "Attaccare: Spazio o J (o il pulsante con la spada)",
            "Pozione: Q o K     Bacca: E o L",
            "Pausa e salvataggio: Esc o P     Schermo intero: F11");
    private static final List<String> REGOLE = List.of(
            "Il drago dorme davanti alla grotta, a nord.",
            "Il pupo ti segue e osserva il drago: quando un",
            "lato resta scoperto, segna a terra un riquadro",
            "dorato. Mettiti lì, girati verso il drago e colpisci:",
            "il danno raddoppia! Colpire alle spalle fa più male.",
            "Riquadro azzurro: scappa lì, sta per arrivare un",
            "attacco. Il falò cura le ferite.");

    private final Grafica grafica;
    private final SfondoAnimato sfondo;
    private final DatiMenu menu;

    /**
     * @param grafica risorse grafiche
     * @param sfondo  panoramica animata dell'isola
     * @param menu    pulsanti da disegnare
     */
    public SchermataIstruzioni(Grafica grafica, SfondoAnimato sfondo, DatiMenu menu) {
        this.grafica = Objects.requireNonNull(grafica, "grafica");
        this.sfondo = Objects.requireNonNull(sfondo, "sfondo");
        this.menu = Objects.requireNonNull(menu, "menu");
    }

    @Override
    public void disegna(Pennello pennello) {
        FontPixel font = grafica.font();
        sfondo.disegna(pennello);
        int centro = Schermo.LARGHEZZA / 2;
        Pannelli.pannello(pennello, 6, 4, Schermo.LARGHEZZA - 12, 148);
        font.scriviCentrato(pennello, "COME SI GIOCA", centro, 10, TESTO, PANNELLO_CHIARO, 2);
        int y = 30;
        for (String riga : COMANDI) {
            font.scrivi(pennello, riga, 16, y, TESTO);
            y += 9;
        }
        y += 4;
        for (String riga : REGOLE) {
            font.scrivi(pennello, riga, 16, y, rgb(120, 60, 20));
            y += 9;
        }
        DisegnatoreMenu.disegna(pennello, font, menu, sfondo.tempo());
    }
}
