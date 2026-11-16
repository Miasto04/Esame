package it.unicam.cs.mpgc.rpg132025.modello.drago;

/**
 * Il drago è stato sconfitto e non fa più nulla.
 */
final class StatoSconfitto implements StatoDrago {

    @Override
    public StatoDrago aggiorna(Drago drago, ContestoDrago contesto, double dt) {
        return this;
    }

    @Override
    public LatoDrago latoVulnerabile() {
        return LatoDrago.NESSUNO;
    }

    @Override
    public AspettoDrago aspetto() {
        return AspettoDrago.SCONFITTO;
    }
}
