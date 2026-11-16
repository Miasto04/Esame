package it.unicam.cs.mpgc.rpg132025.modello.drago;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

/**
 * Il drago si alza in volo e piomba addosso all'eroe. In aria non può essere
 * colpito; l'ombra a terra mostra dove atterrerà.
 */
final class StatoVolo implements StatoDrago {

    static final double DECOLLO = 0.5;
    static final double VOLO = 1.3;
    static final double DURATA_SCHIANTO = 0.25;
    static final double DISCESA = 0.3;
    static final double RICARICA = 9.0;

    private double trascorso;
    private Posizione destinazione;
    private boolean partito;
    private boolean annullato;

    @Override
    public void entra(Drago drago, ContestoDrago contesto) {
        drago.avviaRicarica(TipoAttacco.ATTERRAGGIO, RICARICA);
        Optional<Posizione> scelta = scegliDestinazione(contesto);
        if (scelta.isEmpty()) {
            annullato = true;
            return;
        }
        destinazione = scelta.get();
        contesto.aggiungiZona(ZonaPericolo.crea(TipoAttacco.ATTERRAGGIO,
                Drago.areaAttorno(destinazione).stream().filter(contesto::percorribile).toList(),
                contesto.tempo(), DECOLLO + VOLO, DURATA_SCHIANTO));
        contesto.pubblica(new EventoAttaccoDrago(TipoAttacco.ATTERRAGGIO));
    }

    @Override
    public StatoDrago aggiorna(Drago drago, ContestoDrago contesto, double dt) {
        if (annullato) {
            return new StatoInseguimento(0.3);
        }
        trascorso += dt;
        if (!partito && trascorso >= DECOLLO) {
            partito = true;
            drago.voliVerso(destinazione, VOLO);
        }
        if (trascorso >= DECOLLO + VOLO) {
            drago.orienta(drago.direzioneVerso(contesto.posizioneEroe()));
            contesto.pubblica(new EventoDragoAtterrato(destinazione));
            return new StatoInseguimento(0.6);
        }
        return this;
    }

    @Override
    public boolean inVolo() {
        return !annullato && trascorso < DECOLLO + VOLO;
    }

    @Override
    public double altitudine() {
        if (annullato) {
            return 0.0;
        }
        if (trascorso < DECOLLO) {
            return trascorso / DECOLLO;
        }
        double allAtterraggio = DECOLLO + VOLO - trascorso;
        return Math.clamp(allAtterraggio / DISCESA, 0.0, 1.0);
    }

    @Override
    public LatoDrago latoVulnerabile() {
        return LatoDrago.NESSUNO;
    }

    @Override
    public AspettoDrago aspetto() {
        return AspettoDrago.VOLA;
    }

    /** Prova ad atterrare proprio sopra l'eroe; se non c'è spazio, il più vicino possibile. */
    private static Optional<Posizione> scegliDestinazione(ContestoDrago contesto) {
        Posizione eroe = contesto.posizioneEroe();
        List<Posizione> sopraLEroe = new ArrayList<>();
        for (int dy = -1; dy <= 0; dy++) {
            for (int dx = -1; dx <= 0; dx++) {
                sopraLEroe.add(eroe.traslata(dx, dy));
            }
        }
        mescola(sopraLEroe, contesto.casuale());
        for (Posizione angolo : sopraLEroe) {
            if (contesto.terrenoLibero(angolo)) {
                return Optional.of(angolo);
            }
        }
        for (int raggio = 1; raggio <= 3; raggio++) {
            for (int dy = -raggio - 1; dy <= raggio; dy++) {
                for (int dx = -raggio - 1; dx <= raggio; dx++) {
                    Posizione angolo = eroe.traslata(dx, dy);
                    if (contesto.terrenoLibero(angolo)) {
                        return Optional.of(angolo);
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static void mescola(List<Posizione> lista, RandomGenerator casuale) {
        for (int i = lista.size() - 1; i > 0; i--) {
            Collections.swap(lista, i, casuale.nextInt(i + 1));
        }
    }
}
