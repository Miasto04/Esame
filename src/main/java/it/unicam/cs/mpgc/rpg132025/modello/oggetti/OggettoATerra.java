package it.unicam.cs.mpgc.rpg132025.modello.oggetti;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

import java.util.Objects;

/**
 * Un oggetto appoggiato su una casella, in attesa di essere raccolto.
 *
 * @param tipo      cosa è
 * @param posizione dove si trova
 */
public record OggettoATerra(TipoOggetto tipo, Posizione posizione) {

    public OggettoATerra {
        Objects.requireNonNull(tipo, "tipo");
        Objects.requireNonNull(posizione, "posizione");
    }
}
