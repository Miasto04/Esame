package it.unicam.cs.mpgc.rpg132025.vista.grafica;

import it.unicam.cs.mpgc.rpg132025.input.ComandiTouch;
import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.modello.drago.VistaDrago;
import it.unicam.cs.mpgc.rpg132025.modello.entita.VistaEroe;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.TipoOggetto;
import it.unicam.cs.mpgc.rpg132025.modello.partita.Orario;
import it.unicam.cs.mpgc.rpg132025.modello.partita.VistaPartita;
import it.unicam.cs.mpgc.rpg132025.vista.Schermo;

import java.util.Optional;

import static it.unicam.cs.mpgc.rpg132025.vista.grafica.Tavolozza.*;

/**
 * Disegna l'interfaccia sopra al mondo: barra superiore con cuori, oggetti e
 * orologio, vita del drago (in basso al centro), bussola e comandi touch.
 */
public final class DisegnatoreHud {

    private static final int ALTEZZA_BARRA = 13;
    private static final int CUORI = 6;
    private static final TipoOggetto[] OGGETTI = {TipoOggetto.POZIONE, TipoOggetto.BACCA};

    private final Grafica grafica;
    private final ComandiTouch comandi;

    /**
     * @param grafica risorse grafiche
     * @param comandi posizione dei comandi touch da disegnare
     */
    public DisegnatoreHud(Grafica grafica, ComandiTouch comandi) {
        this.grafica = grafica;
        this.comandi = comandi;
    }

    /**
     * Disegna tutta l'interfaccia di gioco.
     *
     * @param pennello superficie
     * @param partita  partita in corso
     * @param input    stato dei comandi (per evidenziare i pulsanti premuti)
     * @param tempo    orologio delle animazioni
     */
    public void disegna(Pennello pennello, VistaPartita partita, StatoInput input, double tempo) {
        disegnaBarraSuperiore(pennello, partita);
        disegnaVitaDrago(pennello, partita.drago(), tempo);
        disegnaBussola(pennello, partita);
        disegnaComandiTouch(pennello, partita.eroe(), input);
    }

    private void disegnaBarraSuperiore(Pennello pennello, VistaPartita partita) {
        FontPixel font = grafica.font();
        pennello.rettangolo(0, 0, Schermo.LARGHEZZA, ALTEZZA_BARRA, PANNELLO);
        pennello.rettangolo(0, 0, Schermo.LARGHEZZA, 1, PANNELLO_CHIARO);
        pennello.rettangolo(0, ALTEZZA_BARRA, Schermo.LARGHEZZA, 1, PANNELLO_BORDO_SCURO);
        pennello.rettangolo(0, ALTEZZA_BARRA + 1, Schermo.LARGHEZZA, 1, conAlfa(NERO, 60));

        pennello.rettangolo(ComandiTouch.MENU_X, ComandiTouch.MENU_Y, ComandiTouch.MENU_LARGHEZZA,
                ComandiTouch.MENU_ALTEZZA, PANNELLO_BORDO_SCURO);
        pennello.rettangolo(ComandiTouch.MENU_X + 1, ComandiTouch.MENU_Y + 1, ComandiTouch.MENU_LARGHEZZA - 2,
                ComandiTouch.MENU_ALTEZZA - 2, PANNELLO_BORDO);
        for (int i = 0; i < 3; i++) {
            pennello.rettangolo(ComandiTouch.MENU_X + 3, ComandiTouch.MENU_Y + 3 + i * 2, 6, 1, PANNELLO_CHIARO);
        }

        VistaEroe eroe = partita.eroe();
        for (int i = 0; i < CUORI; i++) {
            int resto = eroe.vita() - i * 2;
            SpriteInterfaccia.Cuore stato = resto >= 2 ? SpriteInterfaccia.Cuore.PIENO
                    : resto == 1 ? SpriteInterfaccia.Cuore.MEZZO : SpriteInterfaccia.Cuore.VUOTO;
            pennello.sprite(grafica.interfaccia().cuore(stato), 18 + i * 10, 3);
        }

        int larghezzaSlot = 17;
        int inizioX = Schermo.LARGHEZZA / 2 - (larghezzaSlot * 3 + 3) / 2;
        Pannelli.pannello(pennello, inizioX, 0, larghezzaSlot * 3 + 3, 19);
        disegnaSlot(pennello, inizioX + 2, grafica.personaggi().spada(), -1);
        for (int i = 0; i < OGGETTI.length; i++) {
            disegnaSlot(pennello, inizioX + 2 + (i + 1) * larghezzaSlot,
                    grafica.personaggi().oggetto(OGGETTI[i]), eroe.quantitaOggetto(OGGETTI[i]));
        }

        Orario orario = partita.orario();
        pennello.sprite(orario.notte() ? grafica.interfaccia().luna() : grafica.interfaccia().sole(), 284, 2);
        font.scrivi(pennello, orario.formattato(), 295, 3, TESTO);
    }

    private void disegnaSlot(Pennello pennello, int x, Sprite icona, int quantita) {
        pennello.rettangolo(x, 2, 16, 15, PANNELLO_INCAVO);
        pennello.sprite(icona, x, 1);
        if (quantita >= 0) {
            String testo = Integer.toString(quantita);
            grafica.font().scriviBordato(pennello, testo, x + 15 - grafica.font().larghezza(testo), 10,
                    quantita > 0 ? BIANCO : TESTO_SPENTO, CONTORNO);
        }
    }

    private void disegnaVitaDrago(Pennello pennello, VistaDrago drago, double tempo) {
        if (!drago.isSveglio() || !drago.isViva()) {
            return;
        }
        FontPixel font = grafica.font();
        int larghezza = 118;
        int x = Schermo.LARGHEZZA / 2 - larghezza / 2;
        int y = Schermo.ALTEZZA - 14;
        Pannelli.pannelloScuro(pennello, x, y, larghezza, 11);
        boolean furia = drago.isFurioso();
        String nome = furia ? "FURIA!" : "DRAGO";
        int colore = furia && ((int) (tempo * 6)) % 2 == 0 ? ROSSO_DANNO : BIANCO;
        font.scrivi(pennello, nome, x + 4, y + 2, colore);
        Pannelli.barra(pennello, x + 40, y + 3, larghezza - 44, 6, drago.frazioneVita(), furia ? FUOCO_ARANCIO : CUORE);
    }

    /** La bussola punta verso il drago: utile quando è fuori dallo schermo. */
    private void disegnaBussola(Pennello pennello, VistaPartita partita) {
        int x = Schermo.LARGHEZZA - 18;
        int y = 17;
        pennello.sprite(grafica.interfaccia().bussola(), x, y);
        VistaDrago drago = partita.drago();
        double dx = drago.xVisivo() + 1 - partita.eroe().xVisivo();
        double dy = drago.yVisivo() + 1 - partita.eroe().yVisivo();
        double distanza = Math.hypot(dx, dy);
        if (distanza < 0.5 || !drago.isViva()) {
            return;
        }
        for (int passo = 1; passo <= 5; passo++) {
            int px = x + 7 + (int) Math.round(dx / distanza * passo);
            int py = y + 7 + (int) Math.round(dy / distanza * passo);
            pennello.rettangolo(px, py, 1, 1, passo <= 1 ? TESTO : ROSSO_DANNO);
        }
        pennello.rettangolo(x + 7, y + 7, 1, 1, CONTORNO);
    }

    private void disegnaComandiTouch(Pennello pennello, VistaEroe eroe, StatoInput input) {
        SpriteInterfaccia interfaccia = grafica.interfaccia();
        Optional<Direzione> premuta = comandi.direzione(input);
        int cx = ComandiTouch.CROCE_X;
        int cy = ComandiTouch.CROCE_Y;
        int braccio = ComandiTouch.CROCE_BRACCIO;
        int raggio = ComandiTouch.CROCE_RAGGIO;
        int fondo = conAlfa(rgb(30, 90, 30), 120);
        pennello.rettangolo(cx - braccio / 2, cy - raggio, braccio, raggio * 2, fondo);
        pennello.rettangolo(cx - raggio, cy - braccio / 2, raggio * 2, braccio, fondo);
        for (Direzione direzione : Direzione.values()) {
            int centroX = cx + direzione.dx() * (raggio - braccio / 2);
            int centroY = cy + direzione.dy() * (raggio - braccio / 2);
            if (premuta.isPresent() && premuta.get() == direzione) {
                pennello.rettangolo(centroX - braccio / 2, centroY - braccio / 2, braccio, braccio,
                        conAlfa(BIANCO, 90));
            }
            Sprite freccia = interfaccia.frecciaCroce(direzione);
            pennello.sprite(freccia, centroX - freccia.larghezza() / 2, centroY - freccia.altezza() / 2);
        }

        disegnaPulsante(pennello, ComandiTouch.ATTACCO_X, ComandiTouch.ATTACCO_Y, ComandiTouch.ATTACCO_RAGGIO,
                grafica.personaggi().spada(), -1);
        disegnaPulsante(pennello, ComandiTouch.POZIONE_X, ComandiTouch.POZIONE_Y, ComandiTouch.POZIONE_RAGGIO,
                grafica.personaggi().oggetto(TipoOggetto.POZIONE), eroe.quantitaOggetto(TipoOggetto.POZIONE));
        disegnaPulsante(pennello, ComandiTouch.BACCA_X, ComandiTouch.BACCA_Y, ComandiTouch.BACCA_RAGGIO,
                grafica.personaggi().oggetto(TipoOggetto.BACCA), eroe.quantitaOggetto(TipoOggetto.BACCA));
    }

    private void disegnaPulsante(Pennello pennello, int cx, int cy, int raggio, Sprite icona, int quantita) {
        Sprite fondo = grafica.interfaccia().pulsanteRotondo(raggio * 2);
        pennello.sprite(fondo, cx - raggio, cy - raggio);
        pennello.sprite(icona, cx - icona.larghezza() / 2, cy - icona.altezza() / 2);
        if (quantita >= 0) {
            String testo = Integer.toString(quantita);
            grafica.font().scriviBordato(pennello, testo, cx + raggio - 4, cy + raggio - 6, BIANCO, CONTORNO);
        }
    }
}
