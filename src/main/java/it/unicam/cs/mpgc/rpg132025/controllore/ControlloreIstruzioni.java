package it.unicam.cs.mpgc.rpg132025.controllore;

import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.input.Tasto;
import it.unicam.cs.mpgc.rpg132025.vista.Schermo;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.Schermata;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.SchermataIstruzioni;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.SfondoAnimato;

import java.util.List;
import java.util.Objects;

/**
 * Controllore delle istruzioni: si torna al menu con il pulsante o con Esc.
 */
public final class ControlloreIstruzioni implements Controllore {

    private final ContestoApplicazione contesto;
    private final SfondoAnimato sfondo;
    private final Menu menu;
    private final SchermataIstruzioni vista;

    /**
     * @param contesto dipendenze condivise
     * @param sfondo   panoramica animata dei menu
     */
    public ControlloreIstruzioni(ContestoApplicazione contesto, SfondoAnimato sfondo) {
        this.contesto = Objects.requireNonNull(contesto, "contesto");
        this.sfondo = Objects.requireNonNull(sfondo, "sfondo");
        this.menu = new Menu(Schermo.LARGHEZZA / 2, 158, 100, List.of(Menu.Voce.sempre("Indietro", this::indietro)));
        this.vista = new SchermataIstruzioni(contesto.grafica(), sfondo, menu);
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
