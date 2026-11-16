package it.unicam.cs.mpgc.rpg132025.persistenza;

import it.unicam.cs.mpgc.rpg132025.modello.partita.InstantaneaPartita;
import it.unicam.cs.mpgc.rpg132025.modello.statistiche.StatisticheGlobali;

import java.util.Optional;

/**
 * Archivi in memoria per i test: si comportano come quelli veri, ma senza file. Chi li usa
 * (il servizio di gioco, i controllori) non si accorge della differenza.
 */
public final class ArchiviInMemoria {

    private ArchiviInMemoria() {
    }

    /** La partita salvata, tenuta in memoria. */
    public static final class Partite implements ArchivioPartite {
        private InstantaneaPartita salvata;

        @Override
        public void salva(InstantaneaPartita instantanea) {
            salvata = instantanea;
        }

        @Override
        public Optional<InstantaneaPartita> carica() {
            return Optional.ofNullable(salvata);
        }

        @Override
        public boolean esisteSalvataggio() {
            return salvata != null;
        }

        @Override
        public void elimina() {
            salvata = null;
        }
    }

    /** Le statistiche, tenute in memoria. */
    public static final class Statistiche implements ArchivioStatistiche {
        private StatisticheGlobali statistiche = StatisticheGlobali.vuote();

        @Override
        public StatisticheGlobali carica() {
            return statistiche;
        }

        @Override
        public void salva(StatisticheGlobali nuove) {
            statistiche = nuove;
        }
    }
}
