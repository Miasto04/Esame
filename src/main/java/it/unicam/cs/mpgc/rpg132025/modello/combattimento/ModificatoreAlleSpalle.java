package it.unicam.cs.mpgc.rpg132025.modello.combattimento;

import it.unicam.cs.mpgc.rpg132025.modello.drago.LatoDrago;

import java.util.Optional;

/**
 * I colpi inferti da dietro fanno più male.
 */
public final class ModificatoreAlleSpalle implements ModificatoreDanno {

    /** Etichetta mostrata al giocatore. */
    public static final String ETICHETTA = "ALLE SPALLE";
    /** Moltiplicatore predefinito. */
    public static final double MOLTIPLICATORE = 1.5;

    @Override
    public Optional<Bonus> valuta(ContestoColpo contesto) {
        if (contesto.latoColpito() == LatoDrago.CODA) {
            return Optional.of(new Bonus(ETICHETTA, MOLTIPLICATORE));
        }
        return Optional.empty();
    }
}
