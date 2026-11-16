package it.unicam.cs.mpgc.rpg132025.modello.statistiche;

import it.unicam.cs.mpgc.rpg132025.modello.combattimento.Bonus;
import it.unicam.cs.mpgc.rpg132025.modello.combattimento.EventoColpoInflitto;
import it.unicam.cs.mpgc.rpg132025.modello.combattimento.RisultatoColpo;
import it.unicam.cs.mpgc.rpg132025.modello.entita.EventoEroeFerito;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.EventoOggettoUsato;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.TipoOggetto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatisticheTest {

    private static final Posizione QUI = new Posizione(1, 1);

    @Test
    void contaGliEventiDellaPartita() {
        StatistichePartita statistiche = new StatistichePartita();
        statistiche.aggiungiTempo(12.5);
        statistiche.suEvento(new EventoColpoInflitto(new RisultatoColpo(12, List.of(new Bonus("PUPO", 2))), QUI));
        statistiche.suEvento(new EventoColpoInflitto(new RisultatoColpo(6, List.of()), QUI));
        statistiche.suEvento(new EventoEroeFerito(3, QUI));
        statistiche.suEvento(new EventoOggettoUsato(TipoOggetto.BACCA));
        RiepilogoPartita riepilogo = statistiche.riepilogo();
        assertEquals(12.5, riepilogo.tempoSecondi(), 1e-9);
        assertEquals(2, riepilogo.colpiASegno());
        assertEquals(18, riepilogo.dannoInflitto());
        assertEquals(3, riepilogo.dannoSubito());
        assertEquals(1, riepilogo.oggettiUsati());
        assertEquals(Map.of("PUPO", 1), riepilogo.bonus());
        assertEquals(riepilogo, StatistichePartita.da(riepilogo).riepilogo());
        assertThrows(IllegalArgumentException.class, () -> statistiche.aggiungiTempo(-1));
    }

    @Test
    void accumulaLePartite() {
        RiepilogoPartita vinta = new RiepilogoPartita(100, 10, 150, 4, 1, 8, Map.of("PUPO", 7));
        RiepilogoPartita persa = new RiepilogoPartita(60, 3, 40, 12, 0, 2, Map.of("PUPO", 1, "CRITICO", 1));
        RiepilogoPartita piuVeloce = new RiepilogoPartita(80, 9, 150, 2, 0, 6, Map.of());

        StatisticheGlobali totali = StatisticheGlobali.vuote()
                .conPartita(true, vinta)
                .conPartita(false, persa);
        assertEquals(2, totali.partiteGiocate());
        assertEquals(1, totali.vittorie());
        assertEquals(1, totali.sconfitte());
        assertEquals(OptionalDouble.of(100), totali.migliorTempo());
        assertEquals(Map.of("PUPO", 8, "CRITICO", 1), totali.bonus());

        assertEquals(OptionalDouble.of(80), totali.conPartita(true, piuVeloce).migliorTempo());
        assertEquals(OptionalDouble.empty(), StatisticheGlobali.vuote().migliorTempo());
    }
}
