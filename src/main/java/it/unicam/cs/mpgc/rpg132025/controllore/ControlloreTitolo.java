package it.unicam.cs.mpgc.rpg132025.controllore;

import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.modello.partita.Partita;
import it.unicam.cs.mpgc.rpg132025.persistenza.PersistenzaException;
import it.unicam.cs.mpgc.rpg132025.vista.Schermo;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.Schermata;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.SchermataTitolo;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.SfondoAnimato;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Controllore del menu principale: nuova partita, continua, statistiche, istruzioni, esci.
 */
public final class ControlloreTitolo implements Controllore {

    private static final System.Logger REGISTRO = System.getLogger(ControlloreTitolo.class.getName());

    private final ContestoApplicazione contesto;
    private final SfondoAnimato sfondo;
    private final Menu menu;
    private final SchermataTitolo vista;
    private boolean salvataggioPresente;

    /**
     * @param contesto dipendenze condivise
     */
    public ControlloreTitolo(ContestoApplicazione contesto) {
        this(contesto, new SfondoAnimato(contesto.grafica(), contesto.servizio().nuovaPartita()));
    }

    /**
     * @param contesto dipendenze condivise
     * @param sfondo   panoramica animata da riusare, così tornando al menu non ricomincia
     */
    public ControlloreTitolo(ContestoApplicazione contesto, SfondoAnimato sfondo) {
        this.contesto = Objects.requireNonNull(contesto, "contesto");
        this.sfondo = Objects.requireNonNull(sfondo, "sfondo");
        this.menu = new Menu(Schermo.LARGHEZZA / 2, 70, 126, List.of(
                Menu.Voce.sempre("Nuova partita", this::nuovaPartita),
                new Menu.Voce("Continua", () -> salvataggioPresente, this::continua),
                Menu.Voce.sempre("Statistiche",
                        () -> contesto.controllori().mostra(new ControlloreStatistiche(contesto, sfondo))),
                Menu.Voce.sempre("Come si gioca",
                        () -> contesto.controllori().mostra(new ControlloreIstruzioni(contesto, sfondo))),
                Menu.Voce.sempre("Esci", () -> contesto.piattaforma().esci())));
        this.vista = new SchermataTitolo(contesto.grafica(), sfondo, menu);
    }

    @Override
    public void entra() {
        salvataggioPresente = contesto.servizio().esisteSalvataggio();
    }

    @Override
    public void aggiorna(double dt, StatoInput input) {
        sfondo.aggiorna(dt);
        menu.aggiorna(input);
    }

    @Override
    public Schermata vista() {
        return vista;
    }

    private void nuovaPartita() {
        contesto.controllori().mostra(new ControlloreGioco(contesto, contesto.servizio().nuovaPartita()));
    }

    private void continua() {
        try {
            Optional<Partita> partita = contesto.servizio().caricaPartita();
            if (partita.isPresent()) {
                contesto.controllori().mostra(new ControlloreGioco(contesto, partita.get()));
            } else {
                salvataggioPresente = false;
                vista.mostraAvviso("Non c'è nessuna partita salvata");
            }
        } catch (PersistenzaException e) {
            REGISTRO.log(System.Logger.Level.WARNING, "Caricamento non riuscito", e);
            vista.mostraAvviso("Il salvataggio non si può leggere");
        }
    }
}
