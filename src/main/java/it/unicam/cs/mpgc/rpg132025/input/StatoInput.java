package it.unicam.cs.mpgc.rpg132025.input;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Optional;

/**
 * Lo stato dei comandi del giocatore in un certo istante: tasti tenuti premuti,
 * tasti appena premuti e posizione del puntatore (mouse o dito), in coordinate
 * dello schermo virtuale.
 * <p>
 * La piattaforma lo aggiorna quando arrivano gli eventi; i controllori lo
 * leggono a ogni aggiornamento (la vista solo per evidenziare i pulsanti premuti).
 * Gli eventi "appena premuto" valgono per un solo aggiornamento.
 */
public final class StatoInput {

    private final EnumSet<Tasto> premuti = EnumSet.noneOf(Tasto.class);
    private final EnumSet<Tasto> appenaPremuti = EnumSet.noneOf(Tasto.class);
    private final Deque<Tasto> direzioniPremute = new ArrayDeque<>();
    private double puntatoreX = -1000;
    private double puntatoreY = -1000;
    private boolean puntatoreGiu;
    private boolean pressioneAppenaAvvenuta;

    /** Registra la pressione di un tasto. */
    public void premi(Tasto tasto) {
        if (premuti.add(tasto)) {
            appenaPremuti.add(tasto);
            if (tasto.direzione().isPresent()) {
                direzioniPremute.remove(tasto);
                direzioniPremute.addLast(tasto);
            }
        }
    }

    /** Registra il rilascio di un tasto. */
    public void rilascia(Tasto tasto) {
        premuti.remove(tasto);
        direzioniPremute.remove(tasto);
    }

    /** Registra uno spostamento del puntatore. */
    public void muoviPuntatore(double x, double y) {
        puntatoreX = x;
        puntatoreY = y;
    }

    /** Registra la pressione del puntatore (clic o tocco). */
    public void premiPuntatore(double x, double y) {
        muoviPuntatore(x, y);
        if (!puntatoreGiu) {
            pressioneAppenaAvvenuta = true;
        }
        puntatoreGiu = true;
    }

    /** Registra il rilascio del puntatore. */
    public void rilasciaPuntatore(double x, double y) {
        muoviPuntatore(x, y);
        puntatoreGiu = false;
    }

    /** Rilascia tutto, per esempio quando la finestra perde il fuoco. */
    public void azzera() {
        premuti.clear();
        direzioniPremute.clear();
        puntatoreGiu = false;
    }

    /** Chiamato dopo ogni aggiornamento: gli eventi "appena premuto" sono stati consumati. */
    public void fineAggiornamento() {
        appenaPremuti.clear();
        pressioneAppenaAvvenuta = false;
    }

    /** @return {@code true} se il tasto è tenuto premuto */
    public boolean premuto(Tasto tasto) {
        return premuti.contains(tasto);
    }

    /** @return {@code true} se il tasto è stato premuto dopo l'ultimo aggiornamento */
    public boolean appenaPremuto(Tasto tasto) {
        return appenaPremuti.contains(tasto);
    }

    /** @return la direzione dell'ultimo tasto di movimento ancora premuto */
    public Optional<Direzione> direzione() {
        Tasto ultimo = direzioniPremute.peekLast();
        return ultimo == null ? Optional.empty() : ultimo.direzione();
    }

    /** @return {@code true} mentre il puntatore è premuto */
    public boolean puntatoreAttivo() {
        return puntatoreGiu;
    }

    /** @return {@code true} se il puntatore è stato premuto dopo l'ultimo aggiornamento */
    public boolean pressioneAppenaAvvenuta() {
        return pressioneAppenaAvvenuta;
    }

    /** @return ascissa del puntatore in pixel virtuali */
    public double puntatoreX() {
        return puntatoreX;
    }

    /** @return ordinata del puntatore in pixel virtuali */
    public double puntatoreY() {
        return puntatoreY;
    }
}
