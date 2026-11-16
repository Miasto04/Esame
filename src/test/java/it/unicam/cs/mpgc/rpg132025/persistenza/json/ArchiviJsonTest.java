package it.unicam.cs.mpgc.rpg132025.persistenza.json;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.OggettoATerra;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.TipoOggetto;
import it.unicam.cs.mpgc.rpg132025.modello.partita.DatiDrago;
import it.unicam.cs.mpgc.rpg132025.modello.partita.DatiEroe;
import it.unicam.cs.mpgc.rpg132025.modello.partita.InstantaneaPartita;
import it.unicam.cs.mpgc.rpg132025.modello.statistiche.RiepilogoPartita;
import it.unicam.cs.mpgc.rpg132025.modello.statistiche.StatisticheGlobali;
import it.unicam.cs.mpgc.rpg132025.persistenza.PersistenzaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchiviJsonTest {

    @TempDir
    Path cartella;

    private static InstantaneaPartita esempio() {
        return new InstantaneaPartita(InstantaneaPartita.VERSIONE_CORRENTE, "/livelli/isola.txt",
                new DatiEroe(new Posizione(3, 4), Direzione.SINISTRA, 7, Map.of(TipoOggetto.POZIONE, 2)),
                new Posizione(5, 4),
                new DatiDrago(new Posizione(12, 9), Direzione.GIU, 80, true, true),
                List.of(new OggettoATerra(TipoOggetto.BACCA, new Posizione(8, 8))),
                612.25,
                new RiepilogoPartita(42.5, 6, 60, 3, 1, 4, Map.of("CONSIGLIO DEL PUPO", 3)));
    }

    @Test
    void salvaERicaricaUnaPartita() {
        ArchivioPartiteJson archivio = new ArchivioPartiteJson(cartella.resolve("sotto").resolve("partita.json"));
        assertFalse(archivio.esisteSalvataggio());
        assertEquals(Optional.empty(), archivio.carica());

        archivio.salva(esempio());
        assertTrue(archivio.esisteSalvataggio());
        assertEquals(Optional.of(esempio()), archivio.carica());

        archivio.elimina();
        assertFalse(archivio.esisteSalvataggio());
    }

    @Test
    void ilFileELeggibileAMano() throws IOException {
        Path file = cartella.resolve("partita.json");
        new ArchivioPartiteJson(file).salva(esempio());
        String contenuto = Files.readString(file, StandardCharsets.UTF_8);
        assertTrue(contenuto.contains("\"POZIONE\": 2"));
        assertTrue(contenuto.contains("\"livello\": \"/livelli/isola.txt\""));
    }

    @Test
    void unFileRovinatoDaUnErroreDiPersistenza() throws IOException {
        Path file = cartella.resolve("partita.json");
        Files.writeString(file, "{ questo non è json", StandardCharsets.UTF_8);
        ArchivioPartiteJson archivio = new ArchivioPartiteJson(file);
        assertThrows(PersistenzaException.class, archivio::carica);

        Files.writeString(file, "{\"versione\": 1}", StandardCharsets.UTF_8);
        assertThrows(PersistenzaException.class, archivio::carica);
    }

    @Test
    void statistiche() {
        ArchivioStatisticheJson archivio = new ArchivioStatisticheJson(cartella.resolve("statistiche.json"));
        assertEquals(StatisticheGlobali.vuote(), archivio.carica());
        StatisticheGlobali dopoUnaPartita = StatisticheGlobali.vuote()
                .conPartita(true, new RiepilogoPartita(90, 8, 150, 2, 0, 5, Map.of("CRITICO", 2)));
        archivio.salva(dopoUnaPartita);
        assertEquals(dopoUnaPartita, archivio.carica());
    }
}
