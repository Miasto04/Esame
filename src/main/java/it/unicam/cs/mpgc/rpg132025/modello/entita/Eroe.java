package it.unicam.cs.mpgc.rpg132025.modello.entita;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.Inventario;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.TipoOggetto;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.UtilizzatoreOggetti;

import java.util.Objects;
import java.util.Optional;

/**
 * Il personaggio controllato dal giocatore.
 * <p>
 * Conosce i propri tempi (passo, ricarica della spada, invulnerabilità dopo un
 * colpo) ma non la mappa: è la partita a decidere se una casella è libera.
 * La vita si misura in mezzi cuori.
 */
public final class Eroe extends Creatura implements VistaEroe, UtilizzatoreOggetti {

    /** Vita massima in mezzi cuori (sei cuori). */
    public static final int VITA_MASSIMA = 12;
    /** Danno di un fendente prima dei bonus. */
    public static final int DANNO_BASE = 6;
    /** Secondi per attraversare una casella. */
    public static final double DURATA_PASSO = 0.16;
    /** Secondi tra un fendente e il successivo. */
    public static final double RICARICA_ATTACCO = 0.35;
    /** Secondi di invulnerabilità dopo aver subito un colpo. */
    public static final double DURATA_INVULNERABILITA = 1.0;
    /** Durata dell'animazione del fendente. */
    public static final double DURATA_FENDENTE = 0.2;

    private final Inventario inventario;
    private Direzione direzioneDesiderata;
    private double ricaricaAttacco;
    private double invulnerabilita;
    private double fendente;

    /**
     * Crea un eroe con vita piena e inventario vuoto, rivolto verso il basso.
     *
     * @param posizione casella iniziale
     */
    public Eroe(Posizione posizione) {
        this(posizione, Direzione.GIU, VITA_MASSIMA, new Inventario());
    }

    /**
     * @param posizione  casella iniziale
     * @param direzione  orientamento iniziale
     * @param vita       vita iniziale in mezzi cuori
     * @param inventario oggetti posseduti
     */
    public Eroe(Posizione posizione, Direzione direzione, int vita, Inventario inventario) {
        super(posizione, direzione, new PuntiVita(vita, VITA_MASSIMA));
        this.inventario = Objects.requireNonNull(inventario, "inventario");
    }

    /**
     * Fa scorrere il tempo per i timer dell'eroe.
     *
     * @param dt secondi trascorsi
     */
    public void aggiorna(double dt) {
        aggiornaPasso(dt);
        ricaricaAttacco = Math.max(0, ricaricaAttacco - dt);
        invulnerabilita = Math.max(0, invulnerabilita - dt);
        fendente = Math.max(0, fendente - dt);
    }

    /**
     * Imposta la direzione in cui il giocatore vuole camminare.
     *
     * @param direzione direzione richiesta, {@code null} per stare fermi
     */
    public void impostaDirezioneDesiderata(Direzione direzione) {
        this.direzioneDesiderata = direzione;
    }

    /** @return la direzione richiesta dal giocatore, se ce n'è una */
    public Optional<Direzione> direzioneDesiderata() {
        return Optional.ofNullable(direzioneDesiderata);
    }

    /**
     * Avvia un passo verso una casella adiacente (già verificata come libera).
     *
     * @param destinazione casella adiacente
     */
    public void cammina(Posizione destinazione) {
        if (posizione().distanzaManhattan(destinazione) != 1) {
            throw new IllegalArgumentException("L'eroe si muove di una casella alla volta");
        }
        spostaIn(destinazione, DURATA_PASSO);
    }

    /** @return {@code true} se la spada è pronta */
    public boolean puoAttaccare() {
        return isViva() && ricaricaAttacco <= 0;
    }

    /**
     * Sferra un fendente e fa partire la ricarica.
     *
     * @throws IllegalStateException se la spada non è pronta
     */
    public void avviaAttacco() {
        if (!puoAttaccare()) {
            throw new IllegalStateException("La spada non è ancora pronta");
        }
        ricaricaAttacco = RICARICA_ATTACCO;
        fendente = DURATA_FENDENTE;
    }

    /** @return la casella colpita dal fendente */
    public Posizione casellaDavanti() {
        return posizione().spostata(direzione());
    }

    /** @return danno di un colpo senza bonus */
    public int dannoBase() {
        return DANNO_BASE;
    }

    /** @return gli oggetti posseduti */
    public Inventario inventario() {
        return inventario;
    }

    @Override
    public boolean isInvulnerabile() {
        return invulnerabilita > 0;
    }

    @Override
    public boolean staAttaccando() {
        return fendente > 0;
    }

    @Override
    public double progressoAttacco() {
        return 1.0 - fendente / DURATA_FENDENTE;
    }

    @Override
    public int quantitaOggetto(TipoOggetto tipo) {
        return inventario.quantita(tipo);
    }

    @Override
    public int recuperaVita(int quantita) {
        return recuperaPuntiVita(quantita);
    }

    @Override
    protected boolean puoEssereColpita() {
        return super.puoEssereColpita() && !isInvulnerabile();
    }

    @Override
    protected void dopoDanno(int danno) {
        invulnerabilita = DURATA_INVULNERABILITA;
    }
}
