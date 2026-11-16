package it.unicam.cs.mpgc.rpg132025.modello.pupo;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

import java.util.Objects;

/**
 * Una posizione indicata dal pupo al giocatore, con la frase che la accompagna.
 *
 * @param posizione casella consigliata
 * @param messaggio cosa dice il pupo
 * @param tipo      se serve ad attaccare o a scappare
 */
public record Suggerimento(Posizione posizione, String messaggio, TipoSuggerimento tipo) {

    public Suggerimento {
        Objects.requireNonNull(posizione, "posizione");
        Objects.requireNonNull(messaggio, "messaggio");
        Objects.requireNonNull(tipo, "tipo");
    }

    /** @return {@code true} se colpire dalla casella consigliata dà il bonus al danno */
    public boolean premiaAttacco() {
        return tipo == TipoSuggerimento.ATTACCO;
    }
}
