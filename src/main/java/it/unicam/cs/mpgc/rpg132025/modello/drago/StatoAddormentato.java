package it.unicam.cs.mpgc.rpg132025.modello.drago;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

/**
 * Il drago dorme davanti alla sua tana.
 * <p>
 * Si sveglia se l'eroe gli si avvicina dal davanti o dai fianchi, oppure se viene
 * colpito. Chi arriva da dietro non lo sveglia: è il primo consiglio del pupo.
 */
final class StatoAddormentato implements StatoDrago {

    /** Distanza entro cui il drago sente arrivare l'eroe (se non arriva da dietro). */
    static final int RAGGIO_RISVEGLIO = 4;

    @Override
    public StatoDrago aggiorna(Drago drago, ContestoDrago contesto, double dt) {
        Posizione eroe = contesto.posizioneEroe();
        boolean vicino = drago.distanzaDa(eroe) <= RAGGIO_RISVEGLIO;
        return vicino && !drago.eDietro(eroe) ? new StatoRisveglio() : this;
    }

    @Override
    public StatoDrago dopoColpo(Drago drago) {
        return new StatoRisveglio();
    }

    @Override
    public LatoDrago latoVulnerabile() {
        return LatoDrago.CODA;
    }

    @Override
    public AspettoDrago aspetto() {
        return AspettoDrago.DORME;
    }

    @Override
    public boolean sveglio() {
        return false;
    }
}
