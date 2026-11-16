package it.unicam.cs.mpgc.rpg132025.modello.partita;

/**
 * Un'azione richiesta dal giocatore (pattern Command).
 * <p>
 * Tastiera, comandi touch o un futuro client di rete producono comandi senza
 * conoscere i dettagli della partita; la partita li mette in coda e li esegue
 * all'inizio dell'aggiornamento successivo, sempre nello stesso ordine.
 */
@FunctionalInterface
public interface Comando {

    /**
     * Esegue il comando.
     *
     * @param partita la partita su cui agire
     */
    void eseguiSu(Partita partita);
}
