package it.unicam.cs.mpgc.rpg132025.controllore;

import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.input.Tasto;
import it.unicam.cs.mpgc.rpg132025.modello.livello.CaricatoreLivello;
import it.unicam.cs.mpgc.rpg132025.modello.partita.FabbricaPartita;
import it.unicam.cs.mpgc.rpg132025.persistenza.ArchiviInMemoria;
import it.unicam.cs.mpgc.rpg132025.servizio.ServizioGioco;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Grafica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** La navigazione del menu principale. */
class ControlloreTitoloTest {

    private final GestoreControllori controllori = new GestoreControllori();
    private final ServizioGioco servizio = new ServizioGioco(
            new FabbricaPartita(new CaricatoreLivello(), () -> () -> Long.MAX_VALUE),
            new ArchiviInMemoria.Partite(), new ArchiviInMemoria.Statistiche());
    private final StatoInput input = new StatoInput();
    private boolean uscito;

    @BeforeEach
    void mostraIlMenu() {
        ContestoApplicazione contesto =
                new ContestoApplicazione(servizio, controllori, new Grafica(), () -> uscito = true);
        controllori.mostra(new ControlloreTitolo(contesto));
    }

    private void premi(Tasto tasto) {
        input.premi(tasto);
        controllori.aggiorna(CicloDiGioco.PASSO, input);
        input.rilascia(tasto);
        input.fineAggiornamento();
    }

    @Test
    void nuovaPartitaApreIlGioco() {
        premi(Tasto.CONFERMA);
        assertTrue(controllori.corrente().orElseThrow() instanceof ControlloreGioco);
    }

    @Test
    void senzaSalvataggioContinuaVieneSaltata() {
        premi(Tasto.GIU);
        premi(Tasto.CONFERMA);
        assertTrue(controllori.corrente().orElseThrow() instanceof ControlloreStatistiche);
        premi(Tasto.PAUSA);
        assertTrue(controllori.corrente().orElseThrow() instanceof ControlloreTitolo);
    }

    @Test
    void esciChiedeAllaPiattaformaDiChiudere() {
        premi(Tasto.SU);
        premi(Tasto.CONFERMA);
        assertTrue(uscito);
    }
}
