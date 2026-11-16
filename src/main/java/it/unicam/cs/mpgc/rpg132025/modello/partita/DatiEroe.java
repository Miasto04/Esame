package it.unicam.cs.mpgc.rpg132025.modello.partita;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.TipoOggetto;

import java.util.Map;
import java.util.Objects;

/**
 * Stato salvato dell'eroe.
 *
 * @param posizione  casella occupata
 * @param direzione  orientamento
 * @param vita       mezzi cuori rimasti
 * @param inventario oggetti posseduti
 */
public record DatiEroe(Posizione posizione, Direzione direzione, int vita, Map<TipoOggetto, Integer> inventario) {

    public DatiEroe {
        Objects.requireNonNull(posizione, "posizione");
        Objects.requireNonNull(direzione, "direzione");
        inventario = inventario == null ? Map.of() : Map.copyOf(inventario);
    }
}
