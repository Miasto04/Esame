package it.unicam.cs.mpgc.rpg132025.persistenza;

import it.unicam.cs.mpgc.rpg132025.modello.statistiche.StatisticheGlobali;

/**
 * Dove vengono custodite le statistiche di tutte le partite (pattern Repository).
 */
public interface ArchivioStatistiche {

    /**
     * @return le statistiche salvate, oppure statistiche vuote se non ce ne sono ancora
     * @throws PersistenzaException se i dati esistono ma non si possono leggere
     */
    StatisticheGlobali carica();

    /**
     * @param statistiche statistiche da salvare al posto delle precedenti
     * @throws PersistenzaException se il salvataggio non riesce
     */
    void salva(StatisticheGlobali statistiche);
}
