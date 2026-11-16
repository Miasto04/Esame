package it.unicam.cs.mpgc.rpg132025.controllore;

import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.input.Tasto;
import it.unicam.cs.mpgc.rpg132025.persistenza.PersistenzaException;
import it.unicam.cs.mpgc.rpg132025.vista.Schermo;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.Schermata;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.SchermataStatistiche;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.SfondoAnimato;

import java.util.List;
import java.util.Objects;

/**
 * Controllore delle statistiche: le legge dal servizio di gioco e le passa alla vista.
 */
public final class ControlloreStatistiche implements Controllore {

    private static final System.Logger REGISTRO = System.getLogger(ControlloreStatistiche.class.getName());

    private final ContestoApplicazione contesto;
    private final SfondoAnimato sfondo;
    private final Menu menu;
    private final SchermataStatistiche vista;

    /**
     * @param contesto dipendenze condivise
     * @param sfondo   panoramica animata dei menu
     */
    public ControlloreStatistiche(ContestoApplicazione contesto, SfondoAnimato sfondo) {
        this.contesto = Objects.requireNonNull(contesto, "contesto");
        this.sfondo = Objects.requireNonNull(sfondo, "sfondo");
        this.menu = new Menu(Schermo.LARGHEZZA / 2, 150, 100, List.of(Menu.Voce.sempre("Indietro", this::indietro)));
        this.vista = new SchermataStatistiche(contesto.grafica(), sfondo, menu);
    }

    @Override
    public void entra() {
        try {
            vista.mostra(contesto.servizio().statistiche());
        } catch (PersistenzaException e) {
            REGISTRO.log(System.Logger.Level.WARNING, "Statistiche illeggibili", e);
            vista.mostraErrore();
        }
    }

    @Override
    public void aggiorna(double dt, StatoInput input) {
        sfondo.aggiorna(dt);
        if (input.appenaPremuto(Tasto.PAUSA)) {
            indietro();
            return;
        }
        menu.aggiorna(input);
    }

    @Override
    public Schermata vista() {
        return vista;
    }

    private void indietro() {
        contesto.controllori().mostra(new ControlloreTitolo(contesto, sfondo));
    }
}
