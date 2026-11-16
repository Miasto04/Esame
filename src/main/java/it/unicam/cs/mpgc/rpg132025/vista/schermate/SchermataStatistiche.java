package it.unicam.cs.mpgc.rpg132025.vista.schermate;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.statistiche.StatisticheGlobali;
import it.unicam.cs.mpgc.rpg132025.vista.Formati;
import it.unicam.cs.mpgc.rpg132025.vista.Schermo;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.FontPixel;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Grafica;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Pannelli;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Pennello;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Sprite;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.SpritePersonaggi.PosaDrago;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static it.unicam.cs.mpgc.rpg132025.vista.grafica.Tavolozza.*;

/**
 * Vista delle statistiche di tutte le partite giocate.
 */
public final class SchermataStatistiche implements Schermata {

    private final Grafica grafica;
    private final SfondoAnimato sfondo;
    private final DatiMenu menu;
    private List<String> righe = List.of();
    private boolean nessunaPartita;

    /**
     * @param grafica risorse grafiche
     * @param sfondo  panoramica animata dell'isola
     * @param menu    pulsanti da disegnare
     */
    public SchermataStatistiche(Grafica grafica, SfondoAnimato sfondo, DatiMenu menu) {
        this.grafica = Objects.requireNonNull(grafica, "grafica");
        this.sfondo = Objects.requireNonNull(sfondo, "sfondo");
        this.menu = Objects.requireNonNull(menu, "menu");
    }

    /**
     * @param statistiche le statistiche da mostrare
     */
    public void mostra(StatisticheGlobali statistiche) {
        righe = righe(statistiche);
        nessunaPartita = statistiche.partiteGiocate() == 0;
    }

    /** Mostra che le statistiche non si sono potute leggere. */
    public void mostraErrore() {
        righe = List.of("Non è stato possibile leggere le statistiche.");
        nessunaPartita = false;
    }

    @Override
    public void disegna(Pennello pennello) {
        FontPixel font = grafica.font();
        sfondo.disegna(pennello);
        int centro = Schermo.LARGHEZZA / 2;
        Pannelli.pannello(pennello, centro - 105, 8, 210, 136);
        font.scriviCentrato(pennello, "STATISTICHE", centro, 15, TESTO, PANNELLO_CHIARO, 2);
        for (int i = 0; i < righe.size(); i++) {
            font.scrivi(pennello, righe.get(i), centro - 92, 36 + i * 10, TESTO);
        }
        Sprite drago = grafica.personaggi().drago(Direzione.GIU, PosaDrago.RIPOSO);
        pennello.sprite(drago, centro + 96 - drago.larghezza(), 140 - drago.altezza());
        if (nessunaPartita) {
            font.scrivi(pennello, "Il drago ti aspetta!", centro - 92, 124, rgb(120, 60, 20));
        }
        DisegnatoreMenu.disegna(pennello, font, menu, sfondo.tempo());
    }

    private static List<String> righe(StatisticheGlobali statistiche) {
        List<String> righe = new ArrayList<>();
        righe.add("Partite giocate: " + statistiche.partiteGiocate());
        righe.add("Vittorie: " + statistiche.vittorie() + "    Sconfitte: " + statistiche.sconfitte());
        righe.add("Vittoria più rapida: " + (statistiche.migliorTempo().isPresent()
                ? Formati.tempo(statistiche.migliorTempo().getAsDouble()) : "-"));
        righe.add("Colpi a segno: " + statistiche.colpiASegno());
        righe.add("Danno inflitto ai draghi: " + statistiche.dannoInflitto());
        for (Map.Entry<String, Integer> bonus : statistiche.bonus().entrySet()) {
            righe.add("Bonus " + bonus.getKey().toLowerCase(Locale.ITALIAN) + ": " + bonus.getValue());
        }
        return righe.size() > 10 ? righe.subList(0, 10) : righe;
    }
}
