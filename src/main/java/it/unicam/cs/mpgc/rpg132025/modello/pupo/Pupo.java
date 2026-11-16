package it.unicam.cs.mpgc.rpg132025.modello.pupo;

import it.unicam.cs.mpgc.rpg132025.modello.drago.VistaDrago;
import it.unicam.cs.mpgc.rpg132025.modello.entita.Entita;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.RicercaPercorso;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Il pupo: il bambino che accompagna l'eroe.
 * <p>
 * Segue l'eroe stando lontano dai pericoli e dal drago, osserva il combattimento
 * e indica la posizione migliore da cui colpire. Come scegliere il consiglio non
 * è scritto qui: è delegato alle {@link StrategiaSuggerimento} ricevute.
 */
public final class Pupo extends Entita implements VistaPupo {

    static final double DURATA_PASSO = 0.22;
    static final double INTERVALLO_VALUTAZIONE = 0.15;
    /** Per quanto resta valido un consiglio d'attacco dopo che il drago ha cambiato mossa. */
    static final double GRAZIA_SUGGERIMENTO = 0.8;
    static final double DURATA_FRASE = 2.8;
    static final double INTERVALLO_CHIACCHIERE = 9.0;
    static final int DISTANZA_DALL_EROE = 2;
    static final int DISTANZA_DAL_DRAGO = 2;
    static final int DISTANZA_RAGGIUNGIMENTO = 10;
    static final int LIMITE_RICERCA = 400;
    static final String FRASE_VITTORIA = "Evviva! Ce l'hai fatta!";

    private static final List<String> FRASI_ESPLORAZIONE = List.of(
            "Il drago dorme davanti alla grotta!",
            "Arriva da dietro, non ti sentirà!",
            "Il falò ti cura le ferite.",
            "Bevi una pozione se sei ferito!",
            "Io ti dirò dove colpirlo!");
    private static final List<String> FRASI_COMBATTIMENTO = List.of(
            "Aspetta che attacchi...",
            "Quando attacca si scopre!",
            "Tieniti pronto!");

    private final List<StrategiaSuggerimento> strategie;
    private Suggerimento suggerimento;
    private String frase;
    private double tempoFrase;
    private double attesaValutazione;
    private double graziaResidua;
    private double attesaChiacchiere = 2.0;
    private int prossimaFrase;
    private boolean vittoriaFesteggiata;

    /**
     * @param posizione casella iniziale
     * @param strategie strategie di consiglio, in ordine di priorità
     */
    public Pupo(Posizione posizione, List<StrategiaSuggerimento> strategie) {
        super(posizione, Direzione.GIU);
        this.strategie = List.copyOf(strategie);
    }

    /**
     * Fa agire il pupo per un intervallo di tempo.
     *
     * @param dt         secondi trascorsi
     * @param situazione cosa sta succedendo
     */
    public void aggiorna(double dt, SituazioneCombattimento situazione) {
        aggiornaPasso(dt);
        aggiornaFrase(dt);
        graziaResidua = Math.max(0, graziaResidua - dt);
        VistaDrago drago = situazione.drago();
        if (!drago.isViva()) {
            festeggia();
            return;
        }
        if (!inMovimento()) {
            seguiEroe(situazione);
        }
        attesaValutazione -= dt;
        if (attesaValutazione <= 0) {
            attesaValutazione = INTERVALLO_VALUTAZIONE;
            valutaSuggerimenti(situazione);
        }
        if (suggerimento == null) {
            chiacchiera(dt, drago.isSveglio());
        }
    }

    @Override
    public Optional<Suggerimento> suggerimento() {
        return Optional.ofNullable(suggerimento);
    }

    @Override
    public Optional<String> frase() {
        return Optional.ofNullable(frase);
    }

    private void valutaSuggerimenti(SituazioneCombattimento situazione) {
        Suggerimento nuovo = proponi(situazione);
        if (nuovo == null && mantieniUnAttimo(situazione)) {
            return;
        }
        if (nuovo != null && nuovo.premiaAttacco()) {
            graziaResidua = GRAZIA_SUGGERIMENTO;
        }
        if (Objects.equals(nuovo, suggerimento)) {
            return;
        }
        suggerimento = nuovo;
        if (nuovo != null) {
            dici(nuovo.messaggio());
            situazione.pubblica(new EventoNuovoSuggerimento(nuovo));
        }
    }

    /** Chiede alle strategie, in ordine di priorità, e restituisce il primo consiglio ({@code null} se nessuno). */
    private Suggerimento proponi(SituazioneCombattimento situazione) {
        for (StrategiaSuggerimento strategia : strategie) {
            Optional<Suggerimento> proposta = strategia.suggerisci(situazione, suggerimento);
            if (proposta.isPresent()) {
                return proposta.get();
            }
        }
        return null;
    }

    /**
     * Un consiglio d'attacco resta valido per un breve momento anche dopo che il drago
     * ha cambiato mossa: il giocatore ha il tempo di sfruttare la posizione appena raggiunta.
     */
    private boolean mantieniUnAttimo(SituazioneCombattimento situazione) {
        return suggerimento != null && suggerimento.premiaAttacco() && graziaResidua > 0
                && !situazione.pericolosa(suggerimento.posizione());
    }

    private void seguiEroe(SituazioneCombattimento situazione) {
        Posizione eroe = situazione.posizioneEroe();
        Predicate<Posizione> buonPosto = casella -> !casella.equals(eroe)
                && casella.distanzaChebyshev(eroe) <= DISTANZA_DALL_EROE
                && !situazione.pericolosa(casella)
                && situazione.drago().distanzaDa(casella) >= DISTANZA_DAL_DRAGO
                && (suggerimento == null || !suggerimento.posizione().equals(casella));
        if (buonPosto.test(posizione())) {
            guardaVerso(situazione);
            return;
        }
        Optional<Direzione> passo = RicercaPercorso.primoPassoVerso(posizione(), buonPosto,
                situazione::percorribile, LIMITE_RICERCA);
        if (passo.isPresent()) {
            orienta(passo.get());
            spostaIn(posizione().spostata(passo.get()), DURATA_PASSO);
        } else if (posizione().distanzaChebyshev(eroe) > DISTANZA_RAGGIUNGIMENTO) {
            raggiungiEroe(situazione, eroe);
        }
    }

    /** Se è rimasto troppo indietro (per esempio dietro un ostacolo) ricompare vicino all'eroe. */
    private void raggiungiEroe(SituazioneCombattimento situazione, Posizione eroe) {
        RicercaPercorso.piuVicina(eroe,
                        casella -> !casella.equals(eroe) && situazione.percorribile(casella)
                                && !situazione.pericolosa(casella),
                        situazione::percorribile, LIMITE_RICERCA)
                .ifPresent(this::colloca);
    }

    private void guardaVerso(SituazioneCombattimento situazione) {
        VistaDrago drago = situazione.drago();
        Posizione bersaglio = drago.isSveglio() ? drago.posizione() : situazione.posizioneEroe();
        Direzione.verso(posizione(), bersaglio).ifPresent(this::orienta);
    }

    private void chiacchiera(double dt, boolean dragoSveglio) {
        attesaChiacchiere -= dt;
        if (attesaChiacchiere > 0 || frase != null) {
            return;
        }
        attesaChiacchiere = INTERVALLO_CHIACCHIERE;
        List<String> frasi = dragoSveglio ? FRASI_COMBATTIMENTO : FRASI_ESPLORAZIONE;
        dici(frasi.get(prossimaFrase % frasi.size()));
        prossimaFrase++;
    }

    private void festeggia() {
        suggerimento = null;
        if (!vittoriaFesteggiata) {
            vittoriaFesteggiata = true;
            dici(FRASE_VITTORIA);
        }
    }

    private void dici(String testo) {
        frase = testo;
        tempoFrase = DURATA_FRASE;
    }

    private void aggiornaFrase(double dt) {
        if (frase != null) {
            tempoFrase -= dt;
            if (tempoFrase <= 0) {
                frase = null;
            }
        }
    }
}
