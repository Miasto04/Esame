package it.unicam.cs.mpgc.rpg132025.vista.schermate;

import it.unicam.cs.mpgc.rpg132025.vista.grafica.FontPixel;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Pennello;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Riquadro;

import static it.unicam.cs.mpgc.rpg132025.vista.grafica.Tavolozza.*;

/**
 * Disegna i pulsanti di un menu nello stile del gioco: le voci spente sono più chiare,
 * quella selezionata è dorata e indicata da due frecce che oscillano.
 */
final class DisegnatoreMenu {

    private DisegnatoreMenu() {
    }

    /**
     * @param pennello superficie
     * @param font     carattere
     * @param menu     voci da disegnare
     * @param tempo    orologio delle animazioni
     */
    static void disegna(Pennello pennello, FontPixel font, DatiMenu menu, double tempo) {
        for (int i = 0; i < menu.numeroVoci(); i++) {
            Riquadro pulsante = menu.riquadro(i);
            int x = pulsante.x();
            int y = pulsante.y();
            int larghezza = pulsante.larghezza();
            int altezza = pulsante.altezza();
            boolean scelta = i == menu.selezionata();
            boolean attiva = menu.attiva(i);
            pennello.rettangolo(x + 1, y + altezza, larghezza - 1, 2, conAlfa(NERO, 70));
            pennello.rettangolo(x, y, larghezza, altezza, scelta ? ORO_SCURO : PANNELLO_BORDO_SCURO);
            pennello.rettangolo(x + 1, y + 1, larghezza - 2, altezza - 2, scelta ? ORO : PANNELLO_BORDO);
            pennello.rettangolo(x + 2, y + 2, larghezza - 4, altezza - 4,
                    !attiva ? PANNELLO_INCAVO : scelta ? PANNELLO_CHIARO : PANNELLO);
            String testo = menu.testo(i);
            font.scrivi(pennello, testo, x + larghezza / 2 - font.larghezza(testo) / 2, y + 4,
                    attiva ? TESTO : TESTO_SPENTO);
            if (scelta) {
                int scarto = (int) Math.round(Math.sin(tempo * 6) * 1.5);
                font.scrivi(pennello, ">", x + 5 + scarto, y + 4, TESTO);
                font.scrivi(pennello, "<", x + larghezza - 8 - scarto, y + 4, TESTO);
            }
        }
    }
}
