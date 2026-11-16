package it.unicam.cs.mpgc.rpg132025.controllore;

import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Pennello;

import java.util.Objects;

/**
 * Il ciclo principale del gioco, con passo di aggiornamento fisso.
 * <p>
 * La piattaforma chiama {@link #avanza(double)} a ogni fotogramma con il tempo
 * reale trascorso; il ciclo fa lavorare il controllore attivo tante volte quanti
 * passi da 1/60 di secondo sono trascorsi, poi {@link #disegna} ne mostra la vista.
 * Così la logica si comporta allo stesso modo con qualsiasi frequenza dello schermo
 * e su qualsiasi dispositivo.
 */
public final class CicloDiGioco {

    /** Durata di un aggiornamento in secondi. */
    public static final double PASSO = 1.0 / 60.0;
    private static final double RITARDO_MASSIMO = 0.25;

    private final GestoreControllori controllori;
    private final StatoInput input;
    private double accumulatore;

    /**
     * @param controllori controllori da far lavorare (con le loro viste)
     * @param input       stato dei comandi, riempito dalla piattaforma
     */
    public CicloDiGioco(GestoreControllori controllori, StatoInput input) {
        this.controllori = Objects.requireNonNull(controllori, "controllori");
        this.input = Objects.requireNonNull(input, "input");
    }

    /**
     * Esegue gli aggiornamenti corrispondenti al tempo reale trascorso.
     *
     * @param secondiReali tempo dall'ultimo fotogramma (dopo una pausa lunga viene limitato)
     */
    public void avanza(double secondiReali) {
        accumulatore += Math.clamp(secondiReali, 0, RITARDO_MASSIMO);
        while (accumulatore >= PASSO) {
            controllori.aggiorna(PASSO, input);
            input.fineAggiornamento();
            accumulatore -= PASSO;
        }
    }

    /**
     * Disegna la vista del controllore attivo.
     *
     * @param pennello superficie della piattaforma
     */
    public void disegna(Pennello pennello) {
        controllori.disegna(pennello);
    }
}
