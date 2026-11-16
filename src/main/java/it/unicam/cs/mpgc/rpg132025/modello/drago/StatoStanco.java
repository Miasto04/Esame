package it.unicam.cs.mpgc.rpg132025.modello.drago;

/**
 * Dopo il colpo di coda il drago resta stordito per un attimo, con la testa bassa.
 */
final class StatoStanco implements StatoDrago {

    static final double DURATA = 1.6;

    private double trascorso;

    @Override
    public StatoDrago aggiorna(Drago drago, ContestoDrago contesto, double dt) {
        trascorso += dt;
        return trascorso >= DURATA * drago.moltiplicatoreTempi() ? new StatoInseguimento(0.5) : this;
    }

    @Override
    public LatoDrago latoVulnerabile() {
        return LatoDrago.TESTA;
    }

    @Override
    public AspettoDrago aspetto() {
        return AspettoDrago.STANCO;
    }
}
