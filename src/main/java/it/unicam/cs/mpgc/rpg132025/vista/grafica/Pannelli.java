package it.unicam.cs.mpgc.rpg132025.vista.grafica;

import it.unicam.cs.mpgc.rpg132025.vista.Schermo;

import java.util.ArrayList;
import java.util.List;

import static it.unicam.cs.mpgc.rpg132025.vista.grafica.Tavolozza.*;

/**
 * Elementi grafici ricorrenti dell'interfaccia: pannelli, barre e fumetti,
 * disegnati con semplici rettangoli nello stile arancione del gioco.
 */
public final class Pannelli {

    /** Spazio tra il fumetto e chi parla, occupato dalla punta. */
    private static final int DISTANZA_FUMETTO = 3;

    private Pannelli() {
    }

    /** Pannello chiaro con bordo e ombra, per menu e finestre. */
    public static void pannello(Pennello pennello, int x, int y, int larghezza, int altezza) {
        pennello.rettangolo(x + 2, y + altezza, larghezza - 2, 2, conAlfa(NERO, 70));
        pennello.rettangolo(x, y, larghezza, altezza, PANNELLO_BORDO_SCURO);
        pennello.rettangolo(x + 1, y + 1, larghezza - 2, altezza - 2, PANNELLO_BORDO);
        pennello.rettangolo(x + 2, y + 2, larghezza - 4, altezza - 4, PANNELLO);
        pennello.rettangolo(x + 2, y + 2, larghezza - 4, 1, PANNELLO_CHIARO);
    }

    /** Pannello scuro e semitrasparente, per informazioni sopra al mondo. */
    public static void pannelloScuro(Pennello pennello, int x, int y, int larghezza, int altezza) {
        pennello.rettangolo(x, y, larghezza, altezza, conAlfa(rgb(20, 14, 12), 170));
        pennello.rettangolo(x, y, larghezza, 1, conAlfa(BIANCO, 60));
    }

    /**
     * Barra di avanzamento (per esempio la vita del drago).
     *
     * @param frazione parte piena, da 0 a 1
     */
    public static void barra(Pennello pennello, int x, int y, int larghezza, int altezza, double frazione,
                             int colore) {
        int piena = (int) Math.round((larghezza - 2) * Math.clamp(frazione, 0.0, 1.0));
        pennello.rettangolo(x, y, larghezza, altezza, CONTORNO);
        pennello.rettangolo(x + 1, y + 1, larghezza - 2, altezza - 2, rgb(70, 24, 24));
        pennello.rettangolo(x + 1, y + 1, piena, altezza - 2, colore);
        pennello.rettangolo(x + 1, y + 1, piena, 1, conAlfa(BIANCO, 90));
    }

    /**
     * Fumetto con un testo, attaccato a chi parla. Di solito sta sopra la sua testa; se lì
     * coprirebbe una delle zone da lasciare libere (per esempio l'eroe o il riquadro indicato dal
     * pupo) si sposta di lato o sotto ai piedi.
     *
     * @param font    carattere
     * @param testo   frase
     * @param centroX ascissa del centro di chi parla
     * @param testaY  ordinata della testa di chi parla
     * @param piediY  ordinata dei piedi di chi parla
     * @param libere  zone da non coprire, dalla più importante alla meno importante
     */
    public static void fumetto(Pennello pennello, FontPixel font, String testo, int centroX, int testaY,
                               int piediY, List<Riquadro> libere) {
        List<String> righe = font.aCapo(testo, 120);
        int larghezzaTesto = 0;
        for (String riga : righe) {
            larghezzaTesto = Math.max(larghezzaTesto, font.larghezza(riga));
        }
        int interlinea = FontPixel.ALTEZZA + FontPixel.DISCENDENZA + 1;
        Riquadro corpo = posizioneFumetto(larghezzaTesto + 8, righe.size() * interlinea + 5, centroX, testaY,
                piediY, libere);
        int x = corpo.x();
        int y = corpo.y();
        int larghezza = corpo.larghezza();
        int altezza = corpo.altezza();
        pennello.rettangolo(x + 1, y, larghezza - 2, altezza, CONTORNO);
        pennello.rettangolo(x, y + 1, larghezza, altezza - 2, CONTORNO);
        pennello.rettangolo(x + 1, y + 1, larghezza - 2, altezza - 2, BIANCO);
        boolean sopra = y < testaY;
        int base = sopra ? y + altezza - 1 : y;
        int verso = sopra ? 1 : -1;
        pennello.rettangolo(centroX - 2, base, 5, 1, BIANCO);
        pennello.rettangolo(centroX - 1, base + verso, 3, 1, BIANCO);
        pennello.rettangolo(centroX - 3, base, 1, 1, CONTORNO);
        pennello.rettangolo(centroX + 3, base, 1, 1, CONTORNO);
        pennello.rettangolo(centroX - 2, base + verso, 1, 1, CONTORNO);
        pennello.rettangolo(centroX + 2, base + verso, 1, 1, CONTORNO);
        pennello.rettangolo(centroX - 1, base + 2 * verso, 3, 1, CONTORNO);
        for (int i = 0; i < righe.size(); i++) {
            font.scrivi(pennello, righe.get(i), x + 4, y + 3 + i * interlinea, TESTO);
        }
    }

    /**
     * Sceglie dove mettere il corpo del fumetto: sopra o sotto chi parla, centrato o spostato di
     * lato (ma sempre con la punta sotto al corpo), dentro lo schermo e lontano dalle zone da
     * lasciare libere. Se non si possono evitare tutte, rinuncia prima alle meno importanti.
     *
     * @return il rettangolo del corpo del fumetto, punta esclusa
     */
    static Riquadro posizioneFumetto(int larghezza, int altezza, int centroX, int testaY, int piediY,
                                     List<Riquadro> libere) {
        List<Riquadro> candidati = new ArrayList<>(6);
        int[] ordinate = {testaY - DISTANZA_FUMETTO - altezza, piediY + DISTANZA_FUMETTO};
        int[] ascisse = {centroX - larghezza / 2, centroX - 5, centroX - larghezza + 6};
        for (int y : ordinate) {
            for (int x : ascisse) {
                int dentroLoSchermo = Math.max(2, Math.min(Schermo.LARGHEZZA - larghezza - 2, x));
                candidati.add(new Riquadro(dentroLoSchermo, y, larghezza, altezza));
            }
        }
        for (int quante = libere.size(); quante >= 0; quante--) {
            List<Riquadro> daEvitare = libere.subList(0, quante);
            for (Riquadro candidato : candidati) {
                if (candidato.dentro(Schermo.LARGHEZZA, Schermo.ALTEZZA)
                        && daEvitare.stream().noneMatch(candidato::interseca)) {
                    return candidato;
                }
            }
        }
        return candidati.getFirst();
    }
}
