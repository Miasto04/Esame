package it.unicam.cs.mpgc.rpg132025.modello.statistiche;

import java.util.Collections;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.TreeMap;

/**
 * Statistiche accumulate in tutte le partite giocate. È immutabile: ogni
 * partita conclusa produce un nuovo valore.
 *
 * @param partiteGiocate      partite concluse
 * @param vittorie            partite vinte
 * @param sconfitte           partite perse
 * @param migliorTempoSecondi durata della vittoria più rapida, 0 se non ce ne sono
 * @param colpiASegno         colpi andati a segno in totale
 * @param dannoInflitto       danno inflitto in totale
 * @param bonus               bonus ottenuti in totale, per etichetta
 */
public record StatisticheGlobali(int partiteGiocate, int vittorie, int sconfitte, double migliorTempoSecondi,
                                 int colpiASegno, int dannoInflitto, Map<String, Integer> bonus) {

    public StatisticheGlobali {
        if (partiteGiocate < 0 || vittorie < 0 || sconfitte < 0 || migliorTempoSecondi < 0
                || colpiASegno < 0 || dannoInflitto < 0) {
            throw new IllegalArgumentException("Le statistiche non possono essere negative");
        }
        bonus = bonus == null ? Map.of() : Collections.unmodifiableMap(new TreeMap<>(bonus));
    }

    /** @return statistiche di chi non ha ancora giocato */
    public static StatisticheGlobali vuote() {
        return new StatisticheGlobali(0, 0, 0, 0, 0, 0, Map.of());
    }

    /**
     * Aggiunge una partita conclusa.
     *
     * @param vittoria {@code true} se il drago è stato sconfitto
     * @param partita  statistiche della partita
     * @return le statistiche aggiornate
     */
    public StatisticheGlobali conPartita(boolean vittoria, RiepilogoPartita partita) {
        double migliore = migliorTempoSecondi;
        if (vittoria && (migliore == 0 || partita.tempoSecondi() < migliore)) {
            migliore = partita.tempoSecondi();
        }
        Map<String, Integer> totali = new TreeMap<>(bonus);
        partita.bonus().forEach((etichetta, volte) -> totali.merge(etichetta, volte, Integer::sum));
        return new StatisticheGlobali(partiteGiocate + 1, vittorie + (vittoria ? 1 : 0),
                sconfitte + (vittoria ? 0 : 1), migliore, colpiASegno + partita.colpiASegno(),
                dannoInflitto + partita.dannoInflitto(), totali);
    }

    /** @return il tempo della vittoria più rapida, se c'è stata almeno una vittoria */
    public OptionalDouble migliorTempo() {
        return migliorTempoSecondi > 0 ? OptionalDouble.of(migliorTempoSecondi) : OptionalDouble.empty();
    }
}
