package it.unicam.cs.mpgc.rpg132025.modello.statistiche;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Fotografia immutabile delle statistiche di una partita.
 *
 * @param tempoSecondi     durata della partita (arrotondata al centesimo di secondo)
 * @param colpiASegno      fendenti che hanno ferito il drago
 * @param dannoInflitto    punti vita tolti al drago
 * @param dannoSubito      mezzi cuori persi dall'eroe
 * @param oggettiUsati     oggetti consumati
 * @param consigliRicevuti consigli dati dal pupo
 * @param bonus            quante volte è stato ottenuto ciascun bonus, per etichetta
 */
public record RiepilogoPartita(double tempoSecondi, int colpiASegno, int dannoInflitto, int dannoSubito,
                               int oggettiUsati, int consigliRicevuti, Map<String, Integer> bonus) {

    public RiepilogoPartita {
        if (tempoSecondi < 0 || colpiASegno < 0 || dannoInflitto < 0 || dannoSubito < 0
                || oggettiUsati < 0 || consigliRicevuti < 0) {
            throw new IllegalArgumentException("Le statistiche non possono essere negative");
        }
        tempoSecondi = Math.round(tempoSecondi * 100) / 100.0;
        bonus = bonus == null ? Map.of() : Collections.unmodifiableMap(new TreeMap<>(bonus));
    }

    /** @return il riepilogo di una partita appena iniziata */
    public static RiepilogoPartita vuoto() {
        return new RiepilogoPartita(0, 0, 0, 0, 0, 0, Map.of());
    }
}
