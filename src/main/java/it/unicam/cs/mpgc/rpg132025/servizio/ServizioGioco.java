package it.unicam.cs.mpgc.rpg132025.servizio;

import it.unicam.cs.mpgc.rpg132025.modello.partita.FabbricaPartita;
import it.unicam.cs.mpgc.rpg132025.modello.partita.InstantaneaPartita;
import it.unicam.cs.mpgc.rpg132025.modello.partita.Partita;
import it.unicam.cs.mpgc.rpg132025.modello.partita.StatoPartita;
import it.unicam.cs.mpgc.rpg132025.modello.statistiche.StatisticheGlobali;
import it.unicam.cs.mpgc.rpg132025.persistenza.ArchivioPartite;
import it.unicam.cs.mpgc.rpg132025.persistenza.ArchivioStatistiche;
import it.unicam.cs.mpgc.rpg132025.persistenza.PersistenzaException;

import java.util.Objects;
import java.util.Optional;

/**
 * Le operazioni che l'interfaccia grafica può chiedere al gioco (pattern Facade):
 * iniziare, salvare e riprendere una partita, registrare l'esito e leggere le statistiche.
 * <p>
 * Dipende solo da interfacce per la persistenza, che riceve dal costruttore
 * (Dependency Injection): nei test si possono usare archivi in memoria.
 */
public final class ServizioGioco {

    private final FabbricaPartita fabbrica;
    private final ArchivioPartite archivioPartite;
    private final ArchivioStatistiche archivioStatistiche;

    /**
     * @param fabbrica            crea le partite
     * @param archivioPartite     dove salvare la partita
     * @param archivioStatistiche dove salvare le statistiche
     */
    public ServizioGioco(FabbricaPartita fabbrica, ArchivioPartite archivioPartite,
                         ArchivioStatistiche archivioStatistiche) {
        this.fabbrica = Objects.requireNonNull(fabbrica, "fabbrica");
        this.archivioPartite = Objects.requireNonNull(archivioPartite, "archivioPartite");
        this.archivioStatistiche = Objects.requireNonNull(archivioStatistiche, "archivioStatistiche");
    }

    /** @return una nuova partita */
    public Partita nuovaPartita() {
        return fabbrica.nuovaPartita();
    }

    /** @return {@code true} se c'è una partita salvata da riprendere */
    public boolean esisteSalvataggio() {
        return archivioPartite.esisteSalvataggio();
    }

    /**
     * @return la partita salvata, oppure vuoto se non ce n'è una
     * @throws PersistenzaException se il salvataggio non si può leggere o è danneggiato
     */
    public Optional<Partita> caricaPartita() {
        Optional<InstantaneaPartita> salvataggio = archivioPartite.carica();
        if (salvataggio.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(fabbrica.ripristina(salvataggio.get()));
        } catch (IllegalArgumentException e) {
            throw new PersistenzaException("Il salvataggio è danneggiato: " + e.getMessage(), e);
        }
    }

    /**
     * Salva la partita in corso.
     *
     * @param partita partita da salvare
     * @throws IllegalStateException se la partita è già conclusa
     * @throws PersistenzaException  se il salvataggio non riesce
     */
    public void salva(Partita partita) {
        if (partita.stato().conclusa()) {
            throw new IllegalStateException("Una partita conclusa non si può salvare");
        }
        archivioPartite.salva(partita.creaInstantanea());
    }

    /**
     * @return le statistiche di tutte le partite
     * @throws PersistenzaException se le statistiche non si possono leggere
     */
    public StatisticheGlobali statistiche() {
        return archivioStatistiche.carica();
    }

    /**
     * Aggiunge alle statistiche una partita conclusa. Dopo una vittoria il
     * salvataggio non serve più e viene cancellato; dopo una sconfitta resta,
     * così si può riprovare.
     *
     * @param partita partita conclusa
     * @return le statistiche aggiornate
     * @throws IllegalStateException se la partita non è conclusa
     * @throws PersistenzaException  se le statistiche non si possono salvare
     */
    public StatisticheGlobali registraFinePartita(Partita partita) {
        if (!partita.stato().conclusa()) {
            throw new IllegalStateException("La partita non è ancora finita");
        }
        boolean vittoria = partita.stato() == StatoPartita.VITTORIA;
        StatisticheGlobali aggiornate = archivioStatistiche.carica().conPartita(vittoria, partita.riepilogo());
        archivioStatistiche.salva(aggiornate);
        if (vittoria && archivioPartite.esisteSalvataggio()) {
            archivioPartite.elimina();
        }
        return aggiornate;
    }
}
