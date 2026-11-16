package it.unicam.cs.mpgc.rpg132025.vista.schermate;

import it.unicam.cs.mpgc.rpg132025.vista.grafica.Pennello;

/**
 * Una schermata dell'applicazione vista come Vista del pattern MVC: sa disegnarsi e nient'altro.
 * <p>
 * Non legge i comandi del giocatore e non modifica il modello: legge lo stato del modello
 * (attraverso le sue viste in sola lettura) e mostra ciò che il suo controllore le comunica.
 * Non dipende da nessuna tecnologia grafica, perché disegna su un {@link Pennello}.
 */
public interface Schermata {

    /**
     * Disegna la schermata.
     *
     * @param pennello superficie su cui disegnare
     */
    void disegna(Pennello pennello);
}
