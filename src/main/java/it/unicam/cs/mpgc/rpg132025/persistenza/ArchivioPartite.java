package it.unicam.cs.mpgc.rpg132025.persistenza;

import it.unicam.cs.mpgc.rpg132025.modello.partita.InstantaneaPartita;

import java.util.Optional;

/**
 * Dove viene custodita la partita salvata (pattern Repository).
 * <p>
 * Il resto dell'applicazione conosce solo questa interfaccia: per salvare su un
 * database o su un server basta una nuova implementazione.
 */
public interface ArchivioPartite {

    /**
     * Salva la partita, sostituendo il salvataggio precedente.
     *
     * @param instantanea la partita da salvare
     * @throws PersistenzaException se il salvataggio non riesce
     */
    void salva(InstantaneaPartita instantanea);

    /**
     * @return la partita salvata, oppure vuoto se non ce n'è una
     * @throws PersistenzaException se il salvataggio esiste ma non si può leggere
     */
    Optional<InstantaneaPartita> carica();

    /** @return {@code true} se esiste una partita salvata */
    boolean esisteSalvataggio();

    /**
     * Cancella la partita salvata, se esiste.
     *
     * @throws PersistenzaException se la cancellazione non riesce
     */
    void elimina();
}
