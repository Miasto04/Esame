package it.unicam.cs.mpgc.rpg132025.controllore;

import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.input.Tasto;
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
import it.unicam.cs.mpgc.rpg132025.persistenza.ArchiviInMemoria;
import it.unicam.cs.mpgc.rpg132025.servizio.ServizioGioco;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Grafica;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Il controllore della partita: dai comandi del giocatore al modello, e da una schermata all'altra. */
class ControlloreGiocoTest {

    private final GestoreControllori controllori = new GestoreControllori();
    private final ArchiviInMemoria.Partite salvataggi = new ArchiviInMemoria.Partite();
    private final ServizioGioco servizio = new ServizioGioco(
            new FabbricaPartita(new CaricatoreLivello(), () -> () -> Long.MAX_VALUE),
            salvataggi, new ArchiviInMemoria.Statistiche());
    private final ContestoApplicazione contesto =
            new ContestoApplicazione(servizio, controllori, new Grafica(), () -> { });
    private final StatoInput input = new StatoInput();

    private ControlloreGioco gioca(Partita partita) {
        ControlloreGioco gioco = new ControlloreGioco(contesto, partita);
        controllori.mostra(gioco);
        return gioco;
    }

    private void passi(int quanti) {
        for (int i = 0; i < quanti; i++) {
            controllori.aggiorna(CicloDiGioco.PASSO, input);
            input.fineAggiornamento();
        }
    }

    private void premi(Tasto tasto) {
        input.premi(tasto);
        passi(1);
        input.rilascia(tasto);
    }

    @Test
    void iTastiDiventanoComandiPerLaPartita() {
        Partita partita = servizio.nuovaPartita();
        gioca(partita);
        Posizione partenza = partita.eroe().posizione();
        premi(Tasto.DESTRA);
        passi(30);
        assertEquals(partenza.spostata(Direzione.DESTRA), partita.eroe().posizione());
        premi(Tasto.AZIONE);
        assertTrue(partita.eroe().staAttaccando());
    }

    @Test
    void inPausaLaPartitaSiFermaESiPuoSalvare() {
        Partita partita = servizio.nuovaPartita();
        ControlloreGioco gioco = gioca(partita);
        premi(Tasto.PAUSA);
        assertTrue(controllori.corrente().orElseThrow() instanceof ControllorePausa);
        double tempo = partita.tempo();
        passi(30);
        assertEquals(tempo, partita.tempo(), 1e-9);

        premi(Tasto.GIU);
        premi(Tasto.CONFERMA);
        assertTrue(salvataggi.esisteSalvataggio());
        premi(Tasto.PAUSA);
        assertSame(gioco, controllori.corrente().orElseThrow());
    }

    @Test
    void aPartitaVintaRegistraLeStatisticheEMostraIlRiepilogo() {
        salvataggi.salva(new InstantaneaPartita(1, FabbricaPartita.LIVELLO_PREDEFINITO,
                new DatiEroe(new Posizione(12, 8), Direzione.GIU, 12, Map.of()), new Posizione(9, 8),
                new DatiDrago(new Posizione(12, 9), Direzione.GIU, 1, false, false), List.of(), 420,
                RiepilogoPartita.vuoto()));
        Partita partita = servizio.caricaPartita().orElseThrow();
        gioca(partita);
        premi(Tasto.AZIONE);
        assertEquals(StatoPartita.VITTORIA, partita.stato());
        passi(200);
        assertTrue(controllori.corrente().orElseThrow() instanceof ControlloreFinePartita);
        assertEquals(1, servizio.statistiche().vittorie());
        assertFalse(salvataggi.esisteSalvataggio());
    }
}
