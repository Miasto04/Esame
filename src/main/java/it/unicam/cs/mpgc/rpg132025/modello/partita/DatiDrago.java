package it.unicam.cs.mpgc.rpg132025.modello.partita;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

import java.util.Objects;

/**
 * Stato salvato del drago. L'attacco in corso non viene salvato: al caricamento
 * il drago riparte dall'inseguimento (o dal sonno, se dormiva).
 *
 * @param posizione angolo in alto a sinistra del corpo
 * @param direzione orientamento
 * @param vita      punti vita rimasti
 * @param sveglio   {@code false} se dormiva
 * @param furioso   {@code true} se era furioso
 */
public record DatiDrago(Posizione posizione, Direzione direzione, int vita, boolean sveglio, boolean furioso) {

    public DatiDrago {
        Objects.requireNonNull(posizione, "posizione");
        Objects.requireNonNull(direzione, "direzione");
    }
}
