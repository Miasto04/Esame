package it.unicam.cs.mpgc.rpg132025.vista.schermate;

import it.unicam.cs.mpgc.rpg132025.modello.partita.StatoPartita;
import it.unicam.cs.mpgc.rpg132025.modello.statistiche.RiepilogoPartita;
import it.unicam.cs.mpgc.rpg132025.vista.Formati;
import it.unicam.cs.mpgc.rpg132025.vista.Schermo;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.FontPixel;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Grafica;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Pannelli;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Pennello;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static it.unicam.cs.mpgc.rpg132025.vista.grafica.Tavolozza.*;

/**
 * Vista di fine partita: vittoria o sconfitta, con le statistiche della partita appena
 * finita e il menu per continuare.
 */
public final class SchermataFinePartita implements Schermata {

    private static final int BONUS_MOSTRATI = 3;

    private final Grafica grafica;
    private final Schermata gioco;
    private final StatoPartita esito;
    private final List<String> dati;
    private final List<String> bonus;
    private final DatiMenu menu;
    private String avviso;
    private double tempo;

    /**
     * @param grafica   risorse grafiche
     * @param gioco     vista della partita finita, disegnata sotto
     * @param esito     vittoria o sconfitta
     * @param riepilogo statistiche della partita
     * @param menu      pulsanti da disegnare
     */
    public SchermataFinePartita(Grafica grafica, Schermata gioco, StatoPartita esito, RiepilogoPartita riepilogo,
                                DatiMenu menu) {
        this.grafica = Objects.requireNonNull(grafica, "grafica");
        this.gioco = Objects.requireNonNull(gioco, "gioco");
        this.esito = Objects.requireNonNull(esito, "esito");
        this.menu = Objects.requireNonNull(menu, "menu");
        this.dati = dati(riepilogo);
        this.bonus = bonus(riepilogo);
    }

    /**
     * @param testo problema da segnalare in basso, oppure {@code null} per non mostrare nulla
     */
    public void mostraAvviso(String testo) {
        this.avviso = testo;
    }

    /**
     * Fa avanzare le animazioni.
     *
     * @param dt secondi trascorsi
     */
    public void aggiorna(double dt) {
        tempo += dt;
    }

    @Override
    public void disegna(Pennello pennello) {
        FontPixel font = grafica.font();
        gioco.disegna(pennello);
        pennello.rettangolo(0, 0, pennello.larghezza(), pennello.altezza(), conAlfa(rgb(10, 10, 30), 150));
        int centro = Schermo.LARGHEZZA / 2;
        Pannelli.pannello(pennello, centro - 122, 6, 244, 168);
        boolean vittoria = esito == StatoPartita.VITTORIA;
        font.scriviCentrato(pennello, vittoria ? "VITTORIA!" : "SCONFITTA", centro, 13,
                vittoria ? ORO_SCURO : ROSSO_DANNO, CONTORNO, 2);
        font.scriviCentrato(pennello, vittoria ? "Hai sconfitto il drago!" : "Il drago ha avuto la meglio...",
                centro, 31, TESTO, PANNELLO_CHIARO, 1);
        int y = 44;
        for (int i = 0; i < dati.size(); i++) {
            font.scrivi(pennello, dati.get(i), i % 2 == 0 ? centro - 112 : centro + 4, y + (i / 2) * 10, TESTO);
        }
        y += ((dati.size() + 1) / 2) * 10 + 2;
        for (String riga : bonus) {
            font.scrivi(pennello, riga, centro - 112, y, ORO_SCURO);
            y += 10;
        }
        DisegnatoreMenu.disegna(pennello, font, menu, tempo);
        if (avviso != null) {
            font.scriviCentrato(pennello, avviso, centro, 176 - 8, ROSSO_DANNO, PANNELLO_CHIARO, 1);
        }
    }

    private static List<String> dati(RiepilogoPartita riepilogo) {
        return List.of(
                "Tempo: " + Formati.tempo(riepilogo.tempoSecondi()),
                "Colpi a segno: " + riepilogo.colpiASegno(),
                "Danno al drago: " + riepilogo.dannoInflitto(),
                "Ferite subite: " + riepilogo.dannoSubito(),
                "Oggetti usati: " + riepilogo.oggettiUsati(),
                "Consigli del pupo: " + riepilogo.consigliRicevuti());
    }

    private static List<String> bonus(RiepilogoPartita riepilogo) {
        List<String> righe = new ArrayList<>();
        for (Map.Entry<String, Integer> voce : riepilogo.bonus().entrySet()) {
            if (righe.size() < BONUS_MOSTRATI) {
                righe.add("Bonus " + voce.getKey().toLowerCase(Locale.ITALIAN) + ": " + voce.getValue());
            }
        }
        return righe;
    }
}
