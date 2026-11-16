package it.unicam.cs.mpgc.rpg132025.controllore;

import it.unicam.cs.mpgc.rpg132025.input.ComandiTouch;
import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.input.Tasto;
import it.unicam.cs.mpgc.rpg132025.modello.comandi.ComandoAttacco;
import it.unicam.cs.mpgc.rpg132025.modello.comandi.ComandoMovimento;
import it.unicam.cs.mpgc.rpg132025.modello.comandi.ComandoUsaOggetto;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.TipoOggetto;
import it.unicam.cs.mpgc.rpg132025.modello.partita.Partita;
import it.unicam.cs.mpgc.rpg132025.persistenza.PersistenzaException;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.Schermata;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.SchermataGioco;

import java.util.Objects;
import java.util.Optional;

/**
 * Controllore della partita: è l'unico che modifica il Modello mentre si gioca.
 * <p>
 * A ogni passo traduce tastiera, mouse e tocchi in {@code Comando} per la {@link Partita},
 * la fa avanzare e aggiorna la sua vista. Apre la pausa e, a partita finita, registra le
 * statistiche e passa alla schermata finale.
 */
public final class ControlloreGioco implements Controllore {

    private static final System.Logger REGISTRO = System.getLogger(ControlloreGioco.class.getName());
    /** Secondi in cui si vede ancora la partita finita prima della schermata finale. */
    private static final double ATTESA_DOPO_LA_FINE = 2.5;

    private final ContestoApplicazione contesto;
    private final Partita partita;
    private final ComandiTouch comandiTouch = new ComandiTouch();
    private final SchermataGioco vista;
    private Direzione direzioneInviata;
    private double attesaFine = ATTESA_DOPO_LA_FINE;

    /**
     * @param contesto dipendenze condivise
     * @param partita  partita da giocare
     */
    public ControlloreGioco(ContestoApplicazione contesto, Partita partita) {
        this.contesto = Objects.requireNonNull(contesto, "contesto");
        this.partita = Objects.requireNonNull(partita, "partita");
        this.vista = new SchermataGioco(contesto.grafica(), partita, comandiTouch);
    }

    @Override
    public void entra() {
        vista.collega();
        direzioneInviata = null;
        partita.inviaComando(ComandoMovimento.fermati());
    }

    @Override
    public void esci() {
        vista.scollega();
    }

    @Override
    public void aggiorna(double dt, StatoInput input) {
        if (!partita.stato().conclusa()
                && (input.appenaPremuto(Tasto.PAUSA) || comandiTouch.menu(input))) {
            contesto.controllori().mostra(new ControllorePausa(contesto, this));
            return;
        }
        inviaComandi(input);
        partita.aggiorna(dt);
        vista.aggiorna(dt, input);
        if (partita.stato().conclusa()) {
            attesaFine -= dt;
            if (attesaFine <= 0) {
                concludi();
            }
        }
    }

    @Override
    public Schermata vista() {
        return vista;
    }

    /** @return la partita controllata (la pausa la usa per salvarla) */
    Partita partita() {
        return partita;
    }

    private void inviaComandi(StatoInput input) {
        Optional<Direzione> richiesta = comandiTouch.direzione(input).or(input::direzione);
        Direzione direzione = richiesta.orElse(null);
        if (direzione != direzioneInviata) {
            partita.inviaComando(new ComandoMovimento(direzione));
            direzioneInviata = direzione;
        }
        if (input.appenaPremuto(Tasto.AZIONE) || comandiTouch.attacco(input)) {
            partita.inviaComando(new ComandoAttacco());
        }
        if (input.appenaPremuto(Tasto.POZIONE) || comandiTouch.pozione(input)) {
            partita.inviaComando(new ComandoUsaOggetto(TipoOggetto.POZIONE));
        }
        if (input.appenaPremuto(Tasto.BACCA) || comandiTouch.bacca(input)) {
            partita.inviaComando(new ComandoUsaOggetto(TipoOggetto.BACCA));
        }
    }

    private void concludi() {
        String avviso = null;
        try {
            contesto.servizio().registraFinePartita(partita);
        } catch (PersistenzaException e) {
            REGISTRO.log(System.Logger.Level.WARNING, "Statistiche non salvate", e);
            avviso = "Le statistiche non sono state salvate";
        }
        contesto.controllori().mostra(
                new ControlloreFinePartita(contesto, vista, partita.stato(), partita.riepilogo(), avviso));
    }
}
