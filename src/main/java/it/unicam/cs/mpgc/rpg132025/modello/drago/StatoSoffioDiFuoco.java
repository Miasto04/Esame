package it.unicam.cs.mpgc.rpg132025.modello.drago;

/**
 * Il drago carica e poi sputa fuoco a cono davanti a sé. Resta fermo e girato
 * dalla stessa parte: è il momento di corrergli alle spalle.
 */
final class StatoSoffioDiFuoco implements StatoDrago {

    static final double CARICA = 0.85;
    static final double DURATA_FIAMMA = 1.4;
    static final double RECUPERO = 0.5;
    static final double RICARICA = 4.5;

    private double trascorso;
    private double fineCarica;
    private double durataTotale;

    /**
     * @param drago il drago
     * @return quante file di caselle raggiunge la fiammata
     */
    static int portata(Drago drago) {
        return drago.isFurioso() ? 5 : 4;
    }

    @Override
    public void entra(Drago drago, ContestoDrago contesto) {
        double fattore = drago.moltiplicatoreTempi();
        fineCarica = CARICA * fattore;
        durataTotale = fineCarica + DURATA_FIAMMA + RECUPERO * fattore;
        contesto.aggiungiZona(ZonaPericolo.crea(TipoAttacco.SOFFIO_DI_FUOCO,
                drago.conoDiFuoco(portata(drago)).stream().filter(contesto::percorribile).toList(),
                contesto.tempo(), fineCarica, DURATA_FIAMMA));
        drago.avviaRicarica(TipoAttacco.SOFFIO_DI_FUOCO, RICARICA);
        contesto.pubblica(new EventoAttaccoDrago(TipoAttacco.SOFFIO_DI_FUOCO));
    }

    @Override
    public StatoDrago aggiorna(Drago drago, ContestoDrago contesto, double dt) {
        trascorso += dt;
        return trascorso >= durataTotale ? new StatoInseguimento(0.3) : this;
    }

    @Override
    public LatoDrago latoVulnerabile() {
        return LatoDrago.CODA;
    }

    @Override
    public AspettoDrago aspetto() {
        if (trascorso < fineCarica) {
            return AspettoDrago.CARICA_FUOCO;
        }
        return trascorso < fineCarica + DURATA_FIAMMA ? AspettoDrago.SOFFIA_FUOCO : AspettoDrago.VEGLIA;
    }
}
