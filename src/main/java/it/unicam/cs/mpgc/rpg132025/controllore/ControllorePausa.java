package it.unicam.cs.mpgc.rpg132025.controllore;

import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.input.Tasto;
import it.unicam.cs.mpgc.rpg132025.persistenza.PersistenzaException;
import it.unicam.cs.mpgc.rpg132025.vista.Schermo;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.Schermata;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.SchermataPausa;

import java.util.List;
import java.util.Objects;

/**
 * Controllore della pausa: riprendere, salvare la partita o tornare al menu principale.
 * Mentre è attivo la partita non avanza.
 */
public final class ControllorePausa implements Controllore {

    private static final System.Logger REGISTRO = System.getLogger(ControllorePausa.class.getName());

    private final ContestoApplicazione contesto;
    private final ControlloreGioco gioco;
    private final Menu menu;
    private final SchermataPausa vista;

    /**
     * @param contesto dipendenze condivise
     * @param gioco    controllore della partita in pausa, a cui si torna riprendendo
     */
    public ControllorePausa(ContestoApplicazione contesto, ControlloreGioco gioco) {
        this.contesto = Objects.requireNonNull(contesto, "contesto");
        this.gioco = Objects.requireNonNull(gioco, "gioco");
        this.menu = new Menu(Schermo.LARGHEZZA / 2, 66, 120, List.of(
                Menu.Voce.sempre("Riprendi", this::riprendi),
                Menu.Voce.sempre("Salva partita", this::salva),
                Menu.Voce.sempre("Menu principale",
                        () -> contesto.controllori().mostra(new ControlloreTitolo(contesto)))));
        this.vista = new SchermataPausa(contesto.grafica(), gioco.vista(), menu);
    }

    @Override
    public void aggiorna(double dt, StatoInput input) {
        vista.aggiorna(dt);
        if (input.appenaPremuto(Tasto.PAUSA)) {
            riprendi();
            return;
        }
        menu.aggiorna(input);
    }

    @Override
    public Schermata vista() {
        return vista;
    }

    private void riprendi() {
        contesto.controllori().mostra(gioco);
    }

    private void salva() {
        try {
            contesto.servizio().salva(gioco.partita());
            vista.mostraMessaggio("Partita salvata!", false);
        } catch (PersistenzaException | IllegalStateException e) {
            REGISTRO.log(System.Logger.Level.WARNING, "Salvataggio non riuscito", e);
            vista.mostraMessaggio("Salvataggio non riuscito", true);
        }
    }
}
