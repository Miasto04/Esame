package it.unicam.cs.mpgc.rpg132025.vista.schermate;

import it.unicam.cs.mpgc.rpg132025.vista.Schermo;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.FontPixel;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Grafica;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Pannelli;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Pennello;

import java.util.Objects;

import static it.unicam.cs.mpgc.rpg132025.vista.grafica.Tavolozza.*;

/**
 * Vista della pausa: la partita ferma sullo sfondo, scurita, con il menu della pausa e un
 * eventuale messaggio (per esempio l'esito del salvataggio).
 */
public final class SchermataPausa implements Schermata {

    private static final int VERDE_SUCCESSO = rgb(40, 120, 40);

    private final Grafica grafica;
    private final Schermata gioco;
    private final DatiMenu menu;
    private String messaggio;
    private boolean errore;
    private double tempo;

    /**
     * @param grafica risorse grafiche
     * @param gioco   vista della partita in pausa, disegnata sotto
     * @param menu    pulsanti da disegnare
     */
    public SchermataPausa(Grafica grafica, Schermata gioco, DatiMenu menu) {
        this.grafica = Objects.requireNonNull(grafica, "grafica");
        this.gioco = Objects.requireNonNull(gioco, "gioco");
        this.menu = Objects.requireNonNull(menu, "menu");
    }

    /**
     * @param testo     messaggio da mostrare sotto al menu
     * @param negativo  {@code true} se descrive un problema (viene mostrato in rosso)
     */
    public void mostraMessaggio(String testo, boolean negativo) {
        this.messaggio = testo;
        this.errore = negativo;
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
        pennello.rettangolo(0, 0, pennello.larghezza(), pennello.altezza(), conAlfa(rgb(10, 10, 30), 140));
        Pannelli.pannello(pennello, Schermo.LARGHEZZA / 2 - 80, 34, 160, 106);
        font.scriviCentrato(pennello, "PAUSA", Schermo.LARGHEZZA / 2, 42, TESTO, PANNELLO_CHIARO, 2);
        DisegnatoreMenu.disegna(pennello, font, menu, tempo);
        if (messaggio != null) {
            font.scriviCentrato(pennello, messaggio, Schermo.LARGHEZZA / 2, 128,
                    errore ? ROSSO_DANNO : VERDE_SUCCESSO, PANNELLO_CHIARO, 1);
        }
    }
}
