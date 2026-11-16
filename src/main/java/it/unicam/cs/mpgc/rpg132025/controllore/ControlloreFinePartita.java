package it.unicam.cs.mpgc.rpg132025.controllore;

import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.modello.partita.Partita;
import it.unicam.cs.mpgc.rpg132025.modello.partita.StatoPartita;
import it.unicam.cs.mpgc.rpg132025.modello.statistiche.RiepilogoPartita;
import it.unicam.cs.mpgc.rpg132025.persistenza.PersistenzaException;
import it.unicam.cs.mpgc.rpg132025.vista.Schermo;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.Schermata;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.SchermataFinePartita;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Controllore di fine partita: rigiocare, riprovare dal salvataggio o tornare al menu.
 */
public final class ControlloreFinePartita implements Controllore {

    private static final System.Logger REGISTRO = System.getLogger(ControlloreFinePartita.class.getName());

    private final ContestoApplicazione contesto;
    private final Menu menu;
    private final SchermataFinePartita vista;
    private final boolean salvataggioPresente;

    /**
     * @param contesto  dipendenze condivise
     * @param gioco     vista della partita finita, mostrata sullo sfondo
     * @param esito     vittoria o sconfitta
     * @param riepilogo statistiche della partita
     * @param avviso    eventuale problema da segnalare, oppure {@code null}
     */
    public ControlloreFinePartita(ContestoApplicazione contesto, Schermata gioco, StatoPartita esito,
                                  RiepilogoPartita riepilogo, String avviso) {
        this.contesto = Objects.requireNonNull(contesto, "contesto");
        this.salvataggioPresente = esito == StatoPartita.SCONFITTA && contesto.servizio().esisteSalvataggio();
        this.menu = new Menu(Schermo.LARGHEZZA / 2, 118, 150, List.of(
                Menu.Voce.sempre("Gioca ancora", () -> contesto.controllori()
                        .mostra(new ControlloreGioco(contesto, contesto.servizio().nuovaPartita()))),
                new Menu.Voce("Riprova dal salvataggio", () -> salvataggioPresente, this::riprova),
                Menu.Voce.sempre("Menu principale",
                        () -> contesto.controllori().mostra(new ControlloreTitolo(contesto)))));
        this.vista = new SchermataFinePartita(contesto.grafica(), gioco, esito, riepilogo, menu);
        vista.mostraAvviso(avviso);
    }

    @Override
    public void aggiorna(double dt, StatoInput input) {
        vista.aggiorna(dt);
        menu.aggiorna(input);
    }

    @Override
    public Schermata vista() {
        return vista;
    }

    private void riprova() {
        try {
            Optional<Partita> partita = contesto.servizio().caricaPartita();
            if (partita.isPresent()) {
                contesto.controllori().mostra(new ControlloreGioco(contesto, partita.get()));
            } else {
                vista.mostraAvviso("Non c'è nessuna partita salvata");
            }
        } catch (PersistenzaException e) {
            REGISTRO.log(System.Logger.Level.WARNING, "Caricamento non riuscito", e);
            vista.mostraAvviso("Il salvataggio non si può leggere");
        }
    }
}
