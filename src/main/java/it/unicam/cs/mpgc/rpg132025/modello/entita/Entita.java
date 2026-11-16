package it.unicam.cs.mpgc.rpg132025.modello.entita;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

import java.util.Objects;

/**
 * Qualsiasi cosa che occupa una casella e ha un orientamento.
 * <p>
 * Gestisce anche lo spostamento animato: la posizione logica cambia subito,
 * mentre la posizione "visiva" scorre dalla casella precedente alla nuova
 * nella durata del passo. Così la logica resta a caselle e la grafica è fluida.
 */
public abstract class Entita implements VistaEntita {

    private Posizione posizione;
    private Posizione posizionePrecedente;
    private Direzione direzione;
    private double durataPasso;
    private double tempoPasso;

    /**
     * @param posizione casella iniziale
     * @param direzione orientamento iniziale
     */
    protected Entita(Posizione posizione, Direzione direzione) {
        this.posizione = Objects.requireNonNull(posizione, "posizione");
        this.posizionePrecedente = posizione;
        this.direzione = Objects.requireNonNull(direzione, "direzione");
    }

    @Override
    public Posizione posizione() {
        return posizione;
    }

    @Override
    public Direzione direzione() {
        return direzione;
    }

    /**
     * Cambia l'orientamento senza muoversi.
     *
     * @param nuovaDirezione dove guardare
     */
    public void orienta(Direzione nuovaDirezione) {
        this.direzione = Objects.requireNonNull(nuovaDirezione, "direzione");
    }

    /**
     * Sposta istantaneamente l'entità, interrompendo un eventuale passo.
     *
     * @param nuovaPosizione casella di destinazione
     */
    public void colloca(Posizione nuovaPosizione) {
        this.posizione = Objects.requireNonNull(nuovaPosizione, "posizione");
        this.posizionePrecedente = nuovaPosizione;
        this.durataPasso = 0;
        this.tempoPasso = 0;
    }

    /**
     * Avvia uno spostamento animato verso la destinazione.
     *
     * @param destinazione casella di arrivo
     * @param durata       secondi impiegati per lo spostamento
     */
    protected void spostaIn(Posizione destinazione, double durata) {
        if (durata <= 0) {
            throw new IllegalArgumentException("La durata dello spostamento deve essere positiva");
        }
        this.posizionePrecedente = posizione;
        this.posizione = Objects.requireNonNull(destinazione, "destinazione");
        this.durataPasso = durata;
        this.tempoPasso = 0;
    }

    /**
     * Fa avanzare l'animazione del passo in corso.
     *
     * @param dt secondi trascorsi
     */
    protected void aggiornaPasso(double dt) {
        if (inMovimento()) {
            tempoPasso = Math.min(durataPasso, tempoPasso + dt);
        }
    }

    @Override
    public boolean inMovimento() {
        return tempoPasso < durataPasso;
    }

    @Override
    public double progressoPasso() {
        return durataPasso == 0 ? 1.0 : tempoPasso / durataPasso;
    }

    @Override
    public double xVisivo() {
        return interpola(posizionePrecedente.x(), posizione.x());
    }

    @Override
    public double yVisivo() {
        return interpola(posizionePrecedente.y(), posizione.y());
    }

    private double interpola(int da, int a) {
        return da + (a - da) * progressoPasso();
    }
}
