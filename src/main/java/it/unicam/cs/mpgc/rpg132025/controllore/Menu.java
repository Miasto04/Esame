package it.unicam.cs.mpgc.rpg132025.controllore;

import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.input.Tasto;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Riquadro;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.DatiMenu;

import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;

/**
 * Un menu verticale di pulsanti, usabile con tastiera, mouse e tocco: è la parte di
 * controllo (quale voce è selezionata, quale viene scelta e che cosa succede). La vista lo
 * disegna leggendolo come {@link DatiMenu}.
 * <p>
 * Le voci non attive non si possono selezionare né scegliere.
 */
public final class Menu implements DatiMenu {

    /** Altezza di un pulsante. */
    public static final int ALTEZZA_VOCE = 15;
    private static final int SPAZIO = 4;

    /**
     * Una voce del menu.
     *
     * @param testo  etichetta
     * @param attiva dice se la voce si può scegliere in questo momento
     * @param azione cosa fare quando viene scelta
     */
    public record Voce(String testo, BooleanSupplier attiva, Runnable azione) {

        public Voce {
            Objects.requireNonNull(testo, "testo");
            Objects.requireNonNull(attiva, "attiva");
            Objects.requireNonNull(azione, "azione");
        }

        /**
         * @param testo  etichetta
         * @param azione cosa fare quando viene scelta
         * @return una voce sempre attiva
         */
        public static Voce sempre(String testo, Runnable azione) {
            return new Voce(testo, () -> true, azione);
        }
    }

    private final List<Voce> voci;
    private final int centroX;
    private final int primaY;
    private final int larghezza;
    private int selezionata;
    private double ultimoPuntatoreX = Double.NaN;
    private double ultimoPuntatoreY = Double.NaN;

    /**
     * @param centroX   centro orizzontale dei pulsanti
     * @param primaY    bordo superiore del primo pulsante
     * @param larghezza larghezza dei pulsanti
     * @param voci      voci, dall'alto in basso
     */
    public Menu(int centroX, int primaY, int larghezza, List<Voce> voci) {
        if (voci.isEmpty()) {
            throw new IllegalArgumentException("Un menu ha almeno una voce");
        }
        this.voci = List.copyOf(voci);
        this.centroX = centroX;
        this.primaY = primaY;
        this.larghezza = larghezza;
    }

    /** @return l'altezza complessiva occupata dal menu */
    public int altezza() {
        return voci.size() * (ALTEZZA_VOCE + SPAZIO) - SPAZIO;
    }

    /**
     * Gestisce selezione e scelta.
     *
     * @param input stato dei comandi
     */
    public void aggiorna(StatoInput input) {
        if (!attiva(selezionata)) {
            sposta(1);
        }
        if (input.appenaPremuto(Tasto.SU)) {
            sposta(-1);
        }
        if (input.appenaPremuto(Tasto.GIU)) {
            sposta(1);
        }
        int sottoAlPuntatore = voceIn(input.puntatoreX(), input.puntatoreY());
        boolean puntatoreMosso = input.puntatoreX() != ultimoPuntatoreX || input.puntatoreY() != ultimoPuntatoreY;
        ultimoPuntatoreX = input.puntatoreX();
        ultimoPuntatoreY = input.puntatoreY();
        if (sottoAlPuntatore >= 0 && attiva(sottoAlPuntatore)
                && (puntatoreMosso || input.pressioneAppenaAvvenuta())) {
            selezionata = sottoAlPuntatore;
            if (input.pressioneAppenaAvvenuta()) {
                scegli();
                return;
            }
        }
        if (input.appenaPremuto(Tasto.CONFERMA) || input.appenaPremuto(Tasto.AZIONE)) {
            scegli();
        }
    }

    @Override
    public int numeroVoci() {
        return voci.size();
    }

    @Override
    public String testo(int indice) {
        return voci.get(indice).testo();
    }

    @Override
    public boolean attiva(int indice) {
        return voci.get(indice).attiva().getAsBoolean();
    }

    @Override
    public int selezionata() {
        return selezionata;
    }

    @Override
    public Riquadro riquadro(int indice) {
        return new Riquadro(centroX - larghezza / 2, primaY + indice * (ALTEZZA_VOCE + SPAZIO), larghezza,
                ALTEZZA_VOCE);
    }

    private void scegli() {
        if (attiva(selezionata)) {
            voci.get(selezionata).azione().run();
        }
    }

    private void sposta(int verso) {
        for (int tentativi = 0; tentativi < voci.size(); tentativi++) {
            selezionata = Math.floorMod(selezionata + verso, voci.size());
            if (attiva(selezionata)) {
                return;
            }
        }
    }

    private int voceIn(double x, double y) {
        for (int i = 0; i < voci.size(); i++) {
            if (riquadro(i).contiene(x, y)) {
                return i;
            }
        }
        return -1;
    }
}
