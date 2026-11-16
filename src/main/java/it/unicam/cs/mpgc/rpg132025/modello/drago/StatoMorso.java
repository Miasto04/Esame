package it.unicam.cs.mpgc.rpg132025.modello.drago;

/**
 * Il drago morde le due caselle davanti alla testa. Mentre carica il morso
 * espone i fianchi.
 */
final class StatoMorso implements StatoDrago {

    static final double PREAVVISO = 0.7;
    static final double DURATA_COLPO = 0.2;
    static final double RECUPERO = 0.7;

    private double trascorso;
    private double durataTotale;

    @Override
    public void entra(Drago drago, ContestoDrago contesto) {
        double fattore = drago.moltiplicatoreTempi();
        contesto.aggiungiZona(ZonaPericolo.crea(TipoAttacco.MORSO,
                drago.celleLato(LatoDrago.TESTA).stream().filter(contesto::percorribile).toList(),
                contesto.tempo(), PREAVVISO * fattore, DURATA_COLPO));
        durataTotale = (PREAVVISO + RECUPERO) * fattore + DURATA_COLPO;
        contesto.pubblica(new EventoAttaccoDrago(TipoAttacco.MORSO));
    }

    @Override
    public StatoDrago aggiorna(Drago drago, ContestoDrago contesto, double dt) {
        trascorso += dt;
        return trascorso >= durataTotale ? new StatoInseguimento(0.2) : this;
    }

    @Override
    public LatoDrago latoVulnerabile() {
        return LatoDrago.FIANCHI;
    }

    @Override
    public AspettoDrago aspetto() {
        return AspettoDrago.MORDE;
    }
}
