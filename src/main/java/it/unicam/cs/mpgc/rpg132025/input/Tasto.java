package it.unicam.cs.mpgc.rpg132025.input;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;

import java.util.Optional;

/**
 * I comandi logici del gioco, indipendenti dal dispositivo.
 * <p>
 * Ogni piattaforma decide quali tasti fisici (o gesti) corrispondono a ciascun
 * comando: i controllori ragionano solo in termini di questi valori.
 */
public enum Tasto {
    SU(Direzione.SU),
    GIU(Direzione.GIU),
    SINISTRA(Direzione.SINISTRA),
    DESTRA(Direzione.DESTRA),
    AZIONE(null),
    POZIONE(null),
    BACCA(null),
    PAUSA(null),
    CONFERMA(null);

    private final Direzione direzione;

    Tasto(Direzione direzione) {
        this.direzione = direzione;
    }

    /** @return la direzione associata, se è un tasto di movimento */
    public Optional<Direzione> direzione() {
        return Optional.ofNullable(direzione);
    }
}
