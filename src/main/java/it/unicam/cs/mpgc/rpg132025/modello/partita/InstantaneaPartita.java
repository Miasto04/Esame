package it.unicam.cs.mpgc.rpg132025.modello.partita;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.OggettoATerra;
import it.unicam.cs.mpgc.rpg132025.modello.statistiche.RiepilogoPartita;

import java.util.List;
import java.util.Objects;

/**
 * Fotografia di una partita, da cui la si può ricostruire (pattern Memento).
 * <p>
 * Contiene solo dati semplici: è indipendente sia dalle classi interne del
 * modello sia dal formato con cui viene salvata. La mappa non viene copiata,
 * perché si ricarica dal livello indicato.
 *
 * @param versione       versione del formato, per poter leggere in futuro i salvataggi vecchi
 * @param livello        risorsa del livello giocato
 * @param eroe           stato dell'eroe
 * @param pupo           casella del pupo
 * @param drago          stato del drago
 * @param oggetti        oggetti ancora da raccogliere
 * @param minutiDelGiorno ora del giorno nel gioco, in minuti dalla mezzanotte (al centesimo)
 * @param statistiche    statistiche della partita fino a quel momento
 */
public record InstantaneaPartita(int versione, String livello, DatiEroe eroe, Posizione pupo, DatiDrago drago,
                                 List<OggettoATerra> oggetti, double minutiDelGiorno,
                                 RiepilogoPartita statistiche) {

    /** Versione del formato scritta dai salvataggi di questa release. */
    public static final int VERSIONE_CORRENTE = 1;

    public InstantaneaPartita {
        Objects.requireNonNull(livello, "livello");
        Objects.requireNonNull(eroe, "eroe");
        Objects.requireNonNull(pupo, "pupo");
        Objects.requireNonNull(drago, "drago");
        oggetti = oggetti == null ? List.of() : List.copyOf(oggetti);
        minutiDelGiorno = Math.round(minutiDelGiorno * 100) / 100.0;
        statistiche = statistiche == null ? RiepilogoPartita.vuoto() : statistiche;
    }
}
