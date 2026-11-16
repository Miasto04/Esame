package it.unicam.cs.mpgc.rpg132025.modello.drago;

/**
 * Il drago spazza con la coda la zona alle sue spalle e ai lati posteriori.
 * Girato com'è, lascia la testa scoperta; dopo il colpo resta stordito.
 */
final class StatoColpoDiCoda implements StatoDrago {

    static final double PREAVVISO = 0.6;
    static final double DURATA_COLPO = 0.2;
    static final double RICARICA = 2.5;

    private double trascorso;
    private double durataTotale;

    @Override
    public void entra(Drago drago, ContestoDrago contesto) {
        double preavviso = PREAVVISO * drago.moltiplicatoreTempi();
        durataTotale = preavviso + DURATA_COLPO;
        contesto.aggiungiZona(ZonaPericolo.crea(TipoAttacco.COLPO_DI_CODA,
                drago.zonaColpoDiCoda().stream().filter(contesto::percorribile).toList(),
                contesto.tempo(), preavviso, DURATA_COLPO));
        drago.avviaRicarica(TipoAttacco.COLPO_DI_CODA, RICARICA);
        contesto.pubblica(new EventoAttaccoDrago(TipoAttacco.COLPO_DI_CODA));
    }

    @Override
    public StatoDrago aggiorna(Drago drago, ContestoDrago contesto, double dt) {
        trascorso += dt;
        return trascorso >= durataTotale ? new StatoStanco() : this;
    }

    @Override
    public LatoDrago latoVulnerabile() {
        return LatoDrago.TESTA;
    }

    @Override
    public AspettoDrago aspetto() {
        return AspettoDrago.COLPO_DI_CODA;
    }
}
