package it.unicam.cs.mpgc.rpg132025.persistenza.json;

import it.unicam.cs.mpgc.rpg132025.modello.partita.InstantaneaPartita;
import it.unicam.cs.mpgc.rpg132025.persistenza.ArchivioPartite;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Salva la partita in un file JSON leggibile anche a mano.
 */
public final class ArchivioPartiteJson implements ArchivioPartite {

    private final FileJson file;

    /**
     * @param percorso file del salvataggio
     */
    public ArchivioPartiteJson(Path percorso) {
        this.file = new FileJson(percorso);
    }

    @Override
    public void salva(InstantaneaPartita instantanea) {
        file.scrivi(Objects.requireNonNull(instantanea, "instantanea"));
    }

    @Override
    public Optional<InstantaneaPartita> carica() {
        return file.leggi(InstantaneaPartita.class);
    }

    @Override
    public boolean esisteSalvataggio() {
        return file.esiste();
    }

    @Override
    public void elimina() {
        file.elimina();
    }
}
