package it.unicam.cs.mpgc.rpg132025.controllore;

import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Pennello;

import java.util.Objects;
import java.util.Optional;

/**
 * Tiene il controllore attivo e gestisce il passaggio da una schermata all'altra.
 */
public final class GestoreControllori {

    private Controllore corrente;

    /**
     * Rende attivo un nuovo controllore al posto di quello attuale.
     *
     * @param nuovo controllore da attivare
     */
    public void mostra(Controllore nuovo) {
        Objects.requireNonNull(nuovo, "controllore");
        if (corrente != null) {
            corrente.esci();
        }
        corrente = nuovo;
        nuovo.entra();
    }

    /** @return il controllore attivo, se ce n'è uno */
    public Optional<Controllore> corrente() {
        return Optional.ofNullable(corrente);
    }

    /**
     * Fa lavorare il controllore attivo.
     *
     * @param dt    secondi trascorsi
     * @param input stato dei comandi
     */
    public void aggiorna(double dt, StatoInput input) {
        if (corrente != null) {
            corrente.aggiorna(dt, input);
        }
    }

    /**
     * Disegna la vista del controllore attivo.
     *
     * @param pennello superficie su cui disegnare
     */
    public void disegna(Pennello pennello) {
        if (corrente != null) {
            corrente.vista().disegna(pennello);
        }
    }
}
