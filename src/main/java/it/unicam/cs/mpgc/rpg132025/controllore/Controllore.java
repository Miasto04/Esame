package it.unicam.cs.mpgc.rpg132025.controllore;

import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.Schermata;

/**
 * Il Controllore di una schermata, nel pattern MVC.
 * <p>
 * Legge i comandi del giocatore, li traduce in operazioni sul Modello (la partita, il
 * servizio di gioco), aggiorna la sua Vista e decide quando passare a un'altra schermata.
 * La Vista non conosce il controllore: è il controllore che la crea e le dice cosa mostrare.
 */
public interface Controllore {

    /** Chiamato quando il controllore diventa attivo. */
    default void entra() {
        // nessuna preparazione predefinita
    }

    /** Chiamato quando il controllore viene sostituito da un altro. */
    default void esci() {
        // nessuna pulizia predefinita
    }

    /**
     * Gestisce i comandi e fa avanzare modello e vista.
     *
     * @param dt    secondi trascorsi
     * @param input stato dei comandi del giocatore
     */
    void aggiorna(double dt, StatoInput input);

    /** @return la vista da disegnare */
    Schermata vista();
}
