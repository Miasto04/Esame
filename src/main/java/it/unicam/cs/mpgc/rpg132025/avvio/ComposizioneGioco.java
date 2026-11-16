package it.unicam.cs.mpgc.rpg132025.avvio;

import it.unicam.cs.mpgc.rpg132025.controllore.ContestoApplicazione;
import it.unicam.cs.mpgc.rpg132025.controllore.GestoreControllori;
import it.unicam.cs.mpgc.rpg132025.modello.partita.FabbricaPartita;
import it.unicam.cs.mpgc.rpg132025.persistenza.json.ArchivioPartiteJson;
import it.unicam.cs.mpgc.rpg132025.persistenza.json.ArchivioStatisticheJson;
import it.unicam.cs.mpgc.rpg132025.piattaforma.Piattaforma;
import it.unicam.cs.mpgc.rpg132025.servizio.ServizioGioco;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Grafica;

import java.nio.file.Path;

/**
 * Il punto in cui si assemblano gli oggetti dell'applicazione (composition root).
 * <p>
 * Qui, e solo qui, si scelgono le implementazioni concrete: archivi JSON nella
 * cartella dell'utente, fabbrica delle partite, grafica. Ogni piattaforma lo
 * usa per ottenere un {@link ContestoApplicazione} già pronto per i controllori:
 * per salvare su un database, per esempio, basta cambiare questa classe.
 */
public final class ComposizioneGioco {

    /** Nome della cartella dei dati, dentro la cartella dell'utente. */
    public static final String CARTELLA_DATI = ".tana-del-drago";
    private static final String FILE_SALVATAGGIO = "salvataggio.json";
    private static final String FILE_STATISTICHE = "statistiche.json";

    private ComposizioneGioco() {
    }

    /**
     * @param controllori gestore dei controllori (la navigazione tra le schermate)
     * @param piattaforma servizi della piattaforma
     * @return il contesto con tutte le dipendenze dei controllori
     */
    public static ContestoApplicazione creaContesto(GestoreControllori controllori, Piattaforma piattaforma) {
        return new ContestoApplicazione(creaServizio(cartellaDatiPredefinita()), controllori, new Grafica(),
                piattaforma);
    }

    /**
     * @param cartellaDati cartella in cui tenere salvataggio e statistiche
     * @return il servizio di gioco con gli archivi JSON
     */
    public static ServizioGioco creaServizio(Path cartellaDati) {
        return new ServizioGioco(new FabbricaPartita(),
                new ArchivioPartiteJson(cartellaDati.resolve(FILE_SALVATAGGIO)),
                new ArchivioStatisticheJson(cartellaDati.resolve(FILE_STATISTICHE)));
    }

    /** @return la cartella dei dati nella home dell'utente */
    public static Path cartellaDatiPredefinita() {
        return Path.of(System.getProperty("user.home"), CARTELLA_DATI);
    }
}
