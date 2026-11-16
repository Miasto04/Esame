package it.unicam.cs.mpgc.rpg132025.persistenza.json;

import it.unicam.cs.mpgc.rpg132025.modello.statistiche.StatisticheGlobali;
import it.unicam.cs.mpgc.rpg132025.persistenza.ArchivioStatistiche;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Salva le statistiche di tutte le partite in un file JSON.
 */
public final class ArchivioStatisticheJson implements ArchivioStatistiche {

    private final FileJson file;

    /**
     * @param percorso file delle statistiche
     */
    public ArchivioStatisticheJson(Path percorso) {
        this.file = new FileJson(percorso);
    }

    @Override
    public StatisticheGlobali carica() {
        return file.leggi(StatisticheGlobali.class).orElseGet(StatisticheGlobali::vuote);
    }

    @Override
    public void salva(StatisticheGlobali statistiche) {
        file.scrivi(Objects.requireNonNull(statistiche, "statistiche"));
    }
}
