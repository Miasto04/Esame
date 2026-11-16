package it.unicam.cs.mpgc.rpg132025.servizio;

import it.unicam.cs.mpgc.rpg132025.modello.comandi.ComandoAttacco;
import it.unicam.cs.mpgc.rpg132025.modello.livello.CaricatoreLivello;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.partita.DatiDrago;
import it.unicam.cs.mpgc.rpg132025.modello.partita.DatiEroe;
import it.unicam.cs.mpgc.rpg132025.modello.partita.FabbricaPartita;
import it.unicam.cs.mpgc.rpg132025.modello.partita.InstantaneaPartita;
import it.unicam.cs.mpgc.rpg132025.modello.partita.Partita;
import it.unicam.cs.mpgc.rpg132025.modello.partita.StatoPartita;
import it.unicam.cs.mpgc.rpg132025.modello.statistiche.RiepilogoPartita;
import it.unicam.cs.mpgc.rpg132025.modello.statistiche.StatisticheGlobali;
import it.unicam.cs.mpgc.rpg132025.persistenza.ArchiviInMemoria;
import it.unicam.cs.mpgc.rpg132025.persistenza.PersistenzaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServizioGiocoTest {

    private ArchiviInMemoria.Partite partite;
    private ArchiviInMemoria.Statistiche statistiche;
    private ServizioGioco servizio;

    @BeforeEach
    void preparaServizio() {
        partite = new ArchiviInMemoria.Partite();
        statistiche = new ArchiviInMemoria.Statistiche();
        servizio = new ServizioGioco(new FabbricaPartita(new CaricatoreLivello(), () -> () -> Long.MAX_VALUE),
                partite, statistiche);
    }

    @Test
    void salvaERiprendeUnaPartita() {
        Partita partita = servizio.nuovaPartita();
        assertFalse(servizio.esisteSalvataggio());
        servizio.salva(partita);
        assertTrue(servizio.esisteSalvataggio());
        Partita ripresa = servizio.caricaPartita().orElseThrow();
        assertEquals(partita.creaInstantanea(), ripresa.creaInstantanea());
    }

    @Test
    void senzaSalvataggioNonCarica() {
        assertEquals(Optional.empty(), servizio.caricaPartita());
    }

    @Test
    void unSalvataggioDanneggiatoDiventaUnErroreDiPersistenza() {
        partite.salva(new InstantaneaPartita(1, "/livelli/che-non-esiste.txt",
                new DatiEroe(new Posizione(1, 1), Direzione.GIU, 12, Map.of()), new Posizione(2, 2),
                new DatiDrago(new Posizione(5, 5), Direzione.GIU, 100, false, false), List.of(), 0, null));
        assertThrows(PersistenzaException.class, servizio::caricaPartita);
    }

    @Test
    void laVittoriaAggiornaLeStatisticheECancellaIlSalvataggio() {
        partite.salva(new InstantaneaPartita(1, FabbricaPartita.LIVELLO_PREDEFINITO,
                new DatiEroe(new Posizione(12, 8), Direzione.GIU, 12, Map.of()), new Posizione(9, 8),
                new DatiDrago(new Posizione(12, 9), Direzione.GIU, 1, false, false), List.of(), 420,
                RiepilogoPartita.vuoto()));
        Partita partita = servizio.caricaPartita().orElseThrow();
        assertThrows(IllegalStateException.class, () -> servizio.registraFinePartita(partita));

        partita.inviaComando(new ComandoAttacco());
        partita.aggiorna(1.0 / 60);
        assertEquals(StatoPartita.VITTORIA, partita.stato());
        assertThrows(IllegalStateException.class, () -> servizio.salva(partita));

        StatisticheGlobali aggiornate = servizio.registraFinePartita(partita);
        assertEquals(1, aggiornate.vittorie());
        assertEquals(aggiornate, servizio.statistiche());
        assertFalse(servizio.esisteSalvataggio());
    }
}
