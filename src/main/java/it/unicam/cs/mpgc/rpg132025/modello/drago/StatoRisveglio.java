package it.unicam.cs.mpgc.rpg132025.modello.drago;

/**
 * Il drago si è appena svegliato: si gira verso l'eroe e ruggisce prima di attaccare.
 */
final class StatoRisveglio implements StatoDrago {

    /** Durata del ruggito in secondi. */
    static final double DURATA = 1.0;

    private double trascorso;

    @Override
    public void entra(Drago drago, ContestoDrago contesto) {
        drago.orienta(drago.direzioneVerso(contesto.posizioneEroe()));
        contesto.pubblica(new EventoDragoRisvegliato());
    }

    @Override
    public StatoDrago aggiorna(Drago drago, ContestoDrago contesto, double dt) {
        trascorso += dt;
        return trascorso >= DURATA ? new StatoInseguimento() : this;
    }

    @Override
    public LatoDrago latoVulnerabile() {
        return LatoDrago.NESSUNO;
    }

    @Override
    public AspettoDrago aspetto() {
        return AspettoDrago.RUGGISCE;
    }
}
