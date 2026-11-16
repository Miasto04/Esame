package it.unicam.cs.mpgc.rpg132025.modello.drago;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Il drago insegue l'eroe e sceglie l'attacco adatto alla situazione:
 * morso se l'eroe è davanti, colpo di coda se è dietro o di lato, fiammata se è
 * a media distanza, volo se è lontano. In questa fase non ha lati scoperti.
 */
final class StatoInseguimento implements StatoDrago {

    /** Secondi per fare un passo. */
    static final double DURATA_PASSO = 0.42;
    /** Pausa dopo essersi girato, prima di decidere di nuovo. */
    static final double PAUSA_ROTAZIONE = 0.3;
    /** Pausa quando la strada è bloccata. */
    static final double PAUSA_SE_BLOCCATO = 0.5;
    /** Oltre questa distanza il drago preferisce volare addosso all'eroe. */
    static final int DISTANZA_PER_VOLARE = 7;
    /** Probabilità di usare la coda contro un eroe che sta sul fianco. */
    static final double PROBABILITA_CODA_SUI_FIANCHI = 0.5;

    private double attesa;

    StatoInseguimento() {
        this(0.4);
    }

    StatoInseguimento(double attesaIniziale) {
        this.attesa = attesaIniziale;
    }

    @Override
    public StatoDrago aggiorna(Drago drago, ContestoDrago contesto, double dt) {
        attesa -= dt;
        if (drago.inMovimento() || attesa > 0) {
            return this;
        }
        Posizione eroe = contesto.posizioneEroe();
        Optional<LatoDrago> lato = drago.latoDi(eroe);
        if (lato.isPresent()) {
            return attaccoRavvicinato(drago, contesto, lato.get());
        }
        int distanza = drago.distanzaDa(eroe);
        int portataFuoco = StatoSoffioDiFuoco.portata(drago);
        if (drago.prontoPer(TipoAttacco.SOFFIO_DI_FUOCO) && distanza <= portataFuoco) {
            Direzione versoEroe = drago.direzioneVerso(eroe);
            if (drago.direzione() != versoEroe) {
                return ruota(drago, versoEroe);
            }
            if (drago.conoDiFuoco(portataFuoco).contains(eroe)) {
                return new StatoSoffioDiFuoco();
            }
        }
        if (drago.prontoPer(TipoAttacco.ATTERRAGGIO) && distanza >= DISTANZA_PER_VOLARE) {
            return new StatoVolo();
        }
        if (!avvicinati(drago, contesto, eroe)) {
            attesa = PAUSA_SE_BLOCCATO;
            if (drago.prontoPer(TipoAttacco.ATTERRAGGIO) && distanza > 2) {
                return new StatoVolo();
            }
        }
        return this;
    }

    @Override
    public LatoDrago latoVulnerabile() {
        return LatoDrago.NESSUNO;
    }

    @Override
    public AspettoDrago aspetto() {
        return AspettoDrago.VEGLIA;
    }

    private StatoDrago attaccoRavvicinato(Drago drago, ContestoDrago contesto, LatoDrago lato) {
        if (lato == LatoDrago.TESTA) {
            return new StatoMorso();
        }
        boolean codaPronta = drago.prontoPer(TipoAttacco.COLPO_DI_CODA);
        boolean usaLaCoda = lato == LatoDrago.CODA
                || contesto.casuale().nextDouble() < PROBABILITA_CODA_SUI_FIANCHI;
        if (codaPronta && usaLaCoda) {
            return new StatoColpoDiCoda();
        }
        return ruota(drago, drago.direzioneVerso(contesto.posizioneEroe()));
    }

    private StatoDrago ruota(Drago drago, Direzione verso) {
        drago.orienta(verso);
        attesa = PAUSA_ROTAZIONE * drago.moltiplicatoreTempi();
        return this;
    }

    private boolean avvicinati(Drago drago, ContestoDrago contesto, Posizione eroe) {
        for (Direzione direzione : direzioniVerso(drago, eroe)) {
            Posizione nuovoAngolo = drago.posizione().spostata(direzione);
            if (contesto.terrenoLibero(nuovoAngolo) && !Drago.copre(nuovoAngolo, eroe)) {
                drago.avanza(direzione, DURATA_PASSO * drago.moltiplicatoreTempi());
                return true;
            }
        }
        return false;
    }

    /** Prima l'asse su cui l'eroe è più lontano, poi l'altro (se serve davvero). */
    private static List<Direzione> direzioniVerso(Drago drago, Posizione eroe) {
        double dx = eroe.x() - (drago.posizione().x() + 0.5);
        double dy = eroe.y() - (drago.posizione().y() + 0.5);
        Direzione orizzontale = dx >= 0 ? Direzione.DESTRA : Direzione.SINISTRA;
        Direzione verticale = dy >= 0 ? Direzione.GIU : Direzione.SU;
        List<Direzione> ordine = new ArrayList<>(2);
        if (Math.abs(dx) >= Math.abs(dy)) {
            ordine.add(orizzontale);
            if (Math.abs(dy) >= 1) {
                ordine.add(verticale);
            }
        } else {
            ordine.add(verticale);
            if (Math.abs(dx) >= 1) {
                ordine.add(orizzontale);
            }
        }
        return ordine;
    }
}
