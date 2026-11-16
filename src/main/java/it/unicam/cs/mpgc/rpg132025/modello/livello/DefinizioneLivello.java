package it.unicam.cs.mpgc.rpg132025.modello.livello;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Mappa;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.OggettoATerra;

import java.util.List;
import java.util.Objects;

/**
 * Il contenuto iniziale di un livello: la mappa e dove si trovano personaggi e oggetti.
 *
 * @param mappa       terreni del livello
 * @param inizioEroe  casella di partenza dell'eroe
 * @param inizioPupo  casella di partenza del pupo
 * @param inizioDrago angolo in alto a sinistra del drago
 * @param oggetti     oggetti sparsi sulla mappa
 */
public record DefinizioneLivello(Mappa mappa, Posizione inizioEroe, Posizione inizioPupo,
                                 Posizione inizioDrago, List<OggettoATerra> oggetti) {

    public DefinizioneLivello {
        Objects.requireNonNull(mappa, "mappa");
        Objects.requireNonNull(inizioEroe, "inizioEroe");
        Objects.requireNonNull(inizioPupo, "inizioPupo");
        Objects.requireNonNull(inizioDrago, "inizioDrago");
        oggetti = List.copyOf(oggetti);
    }
}
