package it.unicam.cs.mpgc.rpg132025.modello.pupo;

import it.unicam.cs.mpgc.rpg132025.modello.entita.VistaEntita;

import java.util.Optional;

/**
 * Vista in sola lettura del pupo.
 */
public interface VistaPupo extends VistaEntita {

    /** @return il consiglio attivo, se c'è */
    Optional<Suggerimento> suggerimento();

    /** @return la frase che sta dicendo (da mostrare nel fumetto), se c'è */
    Optional<String> frase();
}
