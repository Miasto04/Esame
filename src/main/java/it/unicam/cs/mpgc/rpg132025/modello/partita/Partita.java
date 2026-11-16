package it.unicam.cs.mpgc.rpg132025.modello.partita;

import it.unicam.cs.mpgc.rpg132025.modello.combattimento.CalcolatoreDanni;
import it.unicam.cs.mpgc.rpg132025.modello.combattimento.ContestoColpo;
import it.unicam.cs.mpgc.rpg132025.modello.combattimento.EventoColpoInflitto;
import it.unicam.cs.mpgc.rpg132025.modello.combattimento.RisultatoColpo;
import it.unicam.cs.mpgc.rpg132025.modello.drago.ContestoDrago;
import it.unicam.cs.mpgc.rpg132025.modello.drago.Drago;
import it.unicam.cs.mpgc.rpg132025.modello.drago.LatoDrago;
import it.unicam.cs.mpgc.rpg132025.modello.drago.VistaDrago;
import it.unicam.cs.mpgc.rpg132025.modello.drago.ZonaPericolo;
import it.unicam.cs.mpgc.rpg132025.modello.entita.Eroe;
import it.unicam.cs.mpgc.rpg132025.modello.entita.EventoEroeCurato;
import it.unicam.cs.mpgc.rpg132025.modello.entita.EventoEroeFerito;
import it.unicam.cs.mpgc.rpg132025.modello.entita.VistaEroe;
import it.unicam.cs.mpgc.rpg132025.modello.eventi.AscoltatoreEventi;
import it.unicam.cs.mpgc.rpg132025.modello.eventi.BusEventi;
import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Mappa;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.RicercaPercorso;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.EventoOggettoNonUsabile;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.EventoOggettoRaccolto;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.EventoOggettoUsato;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.OggettoATerra;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.TipoOggetto;
import it.unicam.cs.mpgc.rpg132025.modello.pupo.Pupo;
import it.unicam.cs.mpgc.rpg132025.modello.pupo.SituazioneCombattimento;
import it.unicam.cs.mpgc.rpg132025.modello.pupo.VistaPupo;
import it.unicam.cs.mpgc.rpg132025.modello.statistiche.RiepilogoPartita;
import it.unicam.cs.mpgc.rpg132025.modello.statistiche.StatistichePartita;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;

/**
 * Una partita in corso: il punto d'ingresso del modello di gioco.
 * <p>
 * Coordina personaggi, oggetti e attacchi a ogni aggiornamento, applica le
 * regole che li coinvolgono insieme (collisioni, raccolta oggetti, danni) e
 * pubblica gli eventi. Non sa nulla di grafica, input o file: può essere
 * guidata da qualsiasi interfaccia (desktop, mobile, web) e dai test.
 * <p>
 * All'esterno espone i personaggi solo attraverso viste in sola lettura;
 * l'unico modo di agire è inviare {@link Comando}. Nel pattern MVC è il Modello: la
 * Vista la legge come {@link VistaPartita}, il controllore le manda i comandi.
 */
public final class Partita implements VistaPartita {

    /** Minuti di gioco che passano in un secondo reale. */
    public static final double MINUTI_DI_GIOCO_AL_SECONDO = 2.0;
    /** Ogni quanti secondi il falò cura mezzo cuore. */
    static final double INTERVALLO_CURA_FALO = 1.5;
    private static final int LIMITE_RICERCA = 80;

    private final String livello;
    private final Mappa mappa;
    private final Eroe eroe;
    private final Pupo pupo;
    private final Drago drago;
    private final List<OggettoATerra> oggetti;
    private final List<ZonaPericolo> zone = new ArrayList<>();
    private final StatistichePartita statistiche;
    private final CalcolatoreDanni calcolatoreDanni;
    private final RandomGenerator casuale;
    private final BusEventi eventi = new BusEventi();
    private final Deque<Comando> comandi = new ArrayDeque<>();
    private final ContestoDrago contestoDrago = new ContestoPerIlDrago();
    private final SituazioneCombattimento situazione = new SituazionePerIlPupo();
    private double minutiDelGiorno;
    private double tempo;
    private double attesaCura = INTERVALLO_CURA_FALO;
    private StatoPartita stato = StatoPartita.IN_CORSO;

    private Partita(Costruttore costruttore) {
        this.livello = Objects.requireNonNull(costruttore.livello, "livello");
        this.mappa = Objects.requireNonNull(costruttore.mappa, "mappa");
        this.eroe = Objects.requireNonNull(costruttore.eroe, "eroe");
        this.pupo = Objects.requireNonNull(costruttore.pupo, "pupo");
        this.drago = Objects.requireNonNull(costruttore.drago, "drago");
        this.oggetti = new ArrayList<>(costruttore.oggetti);
        this.minutiDelGiorno = costruttore.minutiDelGiorno;
        this.statistiche = Objects.requireNonNull(costruttore.statistiche, "statistiche");
        this.calcolatoreDanni = Objects.requireNonNull(costruttore.calcolatoreDanni, "calcolatoreDanni");
        this.casuale = Objects.requireNonNull(costruttore.casuale, "casuale");
        eventi.iscrivi(statistiche);
    }

    // ---------------------------------------------------------------- avanzamento

    /**
     * Mette in coda un comando, che verrà eseguito all'inizio del prossimo aggiornamento.
     *
     * @param comando il comando
     */
    public void inviaComando(Comando comando) {
        comandi.add(Objects.requireNonNull(comando, "comando"));
    }

    /**
     * Fa avanzare la partita. Va chiamato con passi brevi e regolari (per esempio 1/60 di secondo).
     *
     * @param dt secondi trascorsi
     */
    public void aggiorna(double dt) {
        if (dt < 0) {
            throw new IllegalArgumentException("Il tempo non può tornare indietro");
        }
        if (stato.conclusa()) {
            return;
        }
        tempo += dt;
        minutiDelGiorno = (minutiDelGiorno + dt * MINUTI_DI_GIOCO_AL_SECONDO) % Orario.MINUTI_AL_GIORNO;
        statistiche.aggiungiTempo(dt);
        eseguiComandi();
        aggiornaEroe(dt);
        drago.aggiorna(dt, contestoDrago);
        allontanaEroeDalDrago();
        applicaZone();
        pupo.aggiorna(dt, situazione);
        curaVicinoAlFalo(dt);
        controllaFine();
    }

    // ---------------------------------------------------------------- azioni (usate dai comandi)

    /**
     * Imposta la direzione in cui l'eroe deve camminare.
     *
     * @param direzione direzione, oppure {@code null} per fermarsi
     */
    public void impostaMovimentoEroe(Direzione direzione) {
        eroe.impostaDirezioneDesiderata(direzione);
    }

    /**
     * L'eroe sferra un fendente verso la casella che ha davanti. Se lì c'è il
     * drago, il danno viene calcolato tenendo conto dei bonus.
     */
    public void attacca() {
        if (stato.conclusa() || !eroe.puoAttaccare()) {
            return;
        }
        eroe.avviaAttacco();
        Posizione bersaglio = eroe.casellaDavanti();
        Optional<LatoDrago> lato = drago.latoDi(eroe.posizione());
        if (!drago.occupa(bersaglio) || lato.isEmpty()) {
            return;
        }
        ContestoColpo contesto = new ContestoColpo(eroe.posizione(), lato.get(), pupo.suggerimento().orElse(null));
        RisultatoColpo risultato = calcolatoreDanni.calcola(eroe.dannoBase(), contesto);
        int inflitto = drago.subisciDanno(risultato.danno());
        if (inflitto > 0) {
            eventi.pubblica(new EventoColpoInflitto(risultato.conDanno(inflitto), bersaglio));
        }
    }

    /**
     * L'eroe usa un oggetto del proprio inventario.
     *
     * @param tipo oggetto da usare
     */
    public void usaOggetto(TipoOggetto tipo) {
        Objects.requireNonNull(tipo, "tipo");
        if (stato.conclusa() || !eroe.isViva()) {
            return;
        }
        if (eroe.inventario().quantita(tipo) == 0) {
            eventi.pubblica(new EventoOggettoNonUsabile(tipo, EventoOggettoNonUsabile.Motivo.NON_POSSEDUTO));
            return;
        }
        int vitaPrima = eroe.vita();
        if (!tipo.effetto().applicaA(eroe)) {
            eventi.pubblica(new EventoOggettoNonUsabile(tipo, EventoOggettoNonUsabile.Motivo.NESSUN_EFFETTO));
            return;
        }
        eroe.inventario().rimuovi(tipo);
        eventi.pubblica(new EventoOggettoUsato(tipo));
        int curati = eroe.vita() - vitaPrima;
        if (curati > 0) {
            eventi.pubblica(new EventoEroeCurato(curati, eroe.posizione()));
        }
    }

    // ---------------------------------------------------------------- consultazione

    /** @return la risorsa del livello giocato */
    @Override
    public String livello() {
        return livello;
    }

    /** @return la mappa */
    @Override
    public Mappa mappa() {
        return mappa;
    }

    /** @return l'eroe, in sola lettura */
    @Override
    public VistaEroe eroe() {
        return eroe;
    }

    /** @return il pupo, in sola lettura */
    @Override
    public VistaPupo pupo() {
        return pupo;
    }

    /** @return il drago, in sola lettura */
    @Override
    public VistaDrago drago() {
        return drago;
    }

    /** @return gli oggetti ancora da raccogliere */
    @Override
    public List<OggettoATerra> oggettiATerra() {
        return Collections.unmodifiableList(oggetti);
    }

    /** @return gli attacchi del drago in preavviso o in corso */
    @Override
    public List<ZonaPericolo> zonePericolo() {
        return Collections.unmodifiableList(zone);
    }

    /** @return secondi di gioco trascorsi dall'inizio (o dal caricamento) */
    @Override
    public double tempo() {
        return tempo;
    }

    /** @return minuti dalla mezzanotte nell'orologio di gioco */
    @Override
    public double minutiDelGiorno() {
        return minutiDelGiorno;
    }

    /** @return l'ora del giorno nel gioco */
    @Override
    public Orario orario() {
        return Orario.daMinuti(minutiDelGiorno);
    }

    /** @return lo stato della partita */
    @Override
    public StatoPartita stato() {
        return stato;
    }

    /** @return le statistiche raccolte finora */
    @Override
    public RiepilogoPartita riepilogo() {
        return statistiche.riepilogo();
    }

    /**
     * Iscrive un ascoltatore agli eventi della partita.
     *
     * @param ascoltatore l'ascoltatore
     */
    @Override
    public void iscrivi(AscoltatoreEventi ascoltatore) {
        eventi.iscrivi(ascoltatore);
    }

    /**
     * Iscrive un gestore a un solo tipo di evento.
     *
     * @param tipo    classe degli eventi
     * @param gestore azione da eseguire
     * @param <E>     tipo dell'evento
     * @return l'ascoltatore creato, per annullare l'iscrizione
     */
    public <E extends EventoGioco> AscoltatoreEventi iscrivi(Class<E> tipo, Consumer<? super E> gestore) {
        return eventi.iscrivi(tipo, gestore);
    }

    /**
     * @param ascoltatore ascoltatore da rimuovere
     */
    @Override
    public void annullaIscrizione(AscoltatoreEventi ascoltatore) {
        eventi.annullaIscrizione(ascoltatore);
    }

    /** @return una fotografia della partita da salvare */
    public InstantaneaPartita creaInstantanea() {
        return new InstantaneaPartita(InstantaneaPartita.VERSIONE_CORRENTE, livello,
                new DatiEroe(eroe.posizione(), eroe.direzione(), eroe.vita(), eroe.inventario().contenuto()),
                pupo.posizione(),
                new DatiDrago(drago.posizione(), drago.direzione(), drago.vita(), drago.isSveglio(), drago.isFurioso()),
                List.copyOf(oggetti), minutiDelGiorno, statistiche.riepilogo());
    }

    // ---------------------------------------------------------------- regole interne

    private void eseguiComandi() {
        while (!comandi.isEmpty()) {
            comandi.poll().eseguiSu(this);
        }
    }

    private void aggiornaEroe(double dt) {
        eroe.aggiorna(dt);
        if (eroe.isViva() && !eroe.inMovimento()) {
            eroe.direzioneDesiderata().ifPresent(this::provaAMuovereEroe);
        }
    }

    private void provaAMuovereEroe(Direzione direzione) {
        eroe.orienta(direzione);
        Posizione destinazione = eroe.posizione().spostata(direzione);
        if (liberaPerEroe(destinazione)) {
            eroe.cammina(destinazione);
            raccogliOggettiIn(destinazione);
        }
    }

    private boolean liberaPerEroe(Posizione casella) {
        return mappa.percorribile(casella) && !drago.occupa(casella);
    }

    private void raccogliOggettiIn(Posizione casella) {
        Iterator<OggettoATerra> iteratore = oggetti.iterator();
        while (iteratore.hasNext()) {
            OggettoATerra oggetto = iteratore.next();
            if (oggetto.posizione().equals(casella)) {
                iteratore.remove();
                eroe.inventario().aggiungi(oggetto.tipo());
                eventi.pubblica(new EventoOggettoRaccolto(oggetto.tipo(), casella));
            }
        }
    }

    /** Se il drago è atterrato addosso all'eroe, l'eroe viene spinto nella casella libera più vicina. */
    private void allontanaEroeDalDrago() {
        if (!drago.occupa(eroe.posizione())) {
            return;
        }
        RicercaPercorso.piuVicina(eroe.posizione(), this::liberaPerEroe,
                casella -> mappa.percorribile(casella) || drago.occupa(casella), LIMITE_RICERCA)
                .ifPresent(eroe::colloca);
    }

    private void applicaZone() {
        zone.removeIf(zona -> zona.esaurita(tempo));
        if (!drago.isViva()) {
            zone.clear();
            return;
        }
        for (ZonaPericolo zona : zone) {
            if (zona.attiva(tempo) && zona.contiene(eroe.posizione())) {
                int danno = eroe.subisciDanno(zona.danno());
                if (danno > 0) {
                    eventi.pubblica(new EventoEroeFerito(danno, eroe.posizione()));
                }
            }
        }
    }

    private void curaVicinoAlFalo(double dt) {
        if (!eroe.isViva() || eroe.vita() == eroe.vitaMassima() || !vicinoAlFalo(eroe.posizione())) {
            attesaCura = INTERVALLO_CURA_FALO;
            return;
        }
        attesaCura -= dt;
        if (attesaCura <= 0) {
            attesaCura = INTERVALLO_CURA_FALO;
            int curati = eroe.recuperaVita(1);
            if (curati > 0) {
                eventi.pubblica(new EventoEroeCurato(curati, eroe.posizione()));
            }
        }
    }

    private boolean vicinoAlFalo(Posizione casella) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (mappa.terreno(casella.traslata(dx, dy)).curaChiStaVicino()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void controllaFine() {
        if (!drago.isViva()) {
            concludi(StatoPartita.VITTORIA);
        } else if (!eroe.isViva()) {
            concludi(StatoPartita.SCONFITTA);
        }
    }

    private void concludi(StatoPartita esito) {
        stato = esito;
        zone.clear();
        comandi.clear();
        eroe.impostaDirezioneDesiderata(null);
        eventi.pubblica(new EventoPartitaConclusa(esito));
    }

    private boolean pericolosa(Posizione casella) {
        for (ZonaPericolo zona : zone) {
            if (!zona.esaurita(tempo) && zona.contiene(casella)) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------- adattatori per drago e pupo

    /** Ciò che il drago vede della partita. */
    private final class ContestoPerIlDrago implements ContestoDrago {

        @Override
        public Posizione posizioneEroe() {
            return eroe.posizione();
        }

        @Override
        public boolean percorribile(Posizione casella) {
            return mappa.percorribile(casella);
        }

        @Override
        public double tempo() {
            return Partita.this.tempo;
        }

        @Override
        public void aggiungiZona(ZonaPericolo zona) {
            zone.add(Objects.requireNonNull(zona, "zona"));
        }

        @Override
        public RandomGenerator casuale() {
            return casuale;
        }

        @Override
        public void pubblica(EventoGioco evento) {
            eventi.pubblica(evento);
        }
    }

    /** Ciò che il pupo vede della partita. */
    private final class SituazionePerIlPupo implements SituazioneCombattimento {

        @Override
        public Posizione posizioneEroe() {
            return eroe.posizione();
        }

        @Override
        public VistaDrago drago() {
            return drago;
        }

        @Override
        public boolean percorribile(Posizione casella) {
            return mappa.percorribile(casella) && !drago.occupa(casella);
        }

        @Override
        public boolean pericolosa(Posizione casella) {
            return Partita.this.pericolosa(casella);
        }

        @Override
        public void pubblica(EventoGioco evento) {
            eventi.pubblica(evento);
        }
    }

    // ---------------------------------------------------------------- costruzione

    /**
     * Raccoglie le parti di una partita (pattern Builder). È usato da
     * {@link FabbricaPartita} e dai test, che stanno nello stesso package.
     */
    static final class Costruttore {

        private String livello;
        private Mappa mappa;
        private Eroe eroe;
        private Pupo pupo;
        private Drago drago;
        private List<OggettoATerra> oggetti = List.of();
        private double minutiDelGiorno;
        private StatistichePartita statistiche = new StatistichePartita();
        private CalcolatoreDanni calcolatoreDanni;
        private RandomGenerator casuale;

        Costruttore livello(String risorsa, Mappa mappaDelLivello) {
            this.livello = risorsa;
            this.mappa = mappaDelLivello;
            return this;
        }

        Costruttore personaggi(Eroe nuovoEroe, Pupo nuovoPupo, Drago nuovoDrago) {
            this.eroe = nuovoEroe;
            this.pupo = nuovoPupo;
            this.drago = nuovoDrago;
            return this;
        }

        Costruttore oggetti(List<OggettoATerra> oggettiATerra) {
            this.oggetti = List.copyOf(oggettiATerra);
            return this;
        }

        Costruttore orario(double minuti) {
            this.minutiDelGiorno = minuti;
            return this;
        }

        Costruttore statistiche(StatistichePartita statisticheIniziali) {
            this.statistiche = statisticheIniziali;
            return this;
        }

        Costruttore regole(CalcolatoreDanni calcolatore, RandomGenerator generatore) {
            this.calcolatoreDanni = calcolatore;
            this.casuale = generatore;
            return this;
        }

        Partita crea() {
            return new Partita(this);
        }
    }
}
