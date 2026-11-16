package it.unicam.cs.mpgc.rpg132025.modello.comandi;

import it.unicam.cs.mpgc.rpg132025.modello.oggetti.TipoOggetto;
import it.unicam.cs.mpgc.rpg132025.modello.partita.Comando;
import it.unicam.cs.mpgc.rpg132025.modello.partita.Partita;

import java.util.Objects;

/**
 * L'eroe usa un oggetto dell'inventario.
 *
 * @param tipo oggetto da usare
 */
public record ComandoUsaOggetto(TipoOggetto tipo) implements Comando {

    public ComandoUsaOggetto {
        Objects.requireNonNull(tipo, "tipo");
    }

    @Override
    public void eseguiSu(Partita partita) {
        partita.usaOggetto(tipo);
    }
}
