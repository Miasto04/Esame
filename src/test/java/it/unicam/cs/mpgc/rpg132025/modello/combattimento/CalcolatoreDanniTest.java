package it.unicam.cs.mpgc.rpg132025.modello.combattimento;

import it.unicam.cs.mpgc.rpg132025.modello.drago.LatoDrago;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.pupo.Suggerimento;
import it.unicam.cs.mpgc.rpg132025.modello.pupo.TipoSuggerimento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalcolatoreDanniTest {

    /** Restituisce sempre 0,5: mai critico con probabilità 0,1. */
    private static final RandomGenerator MAI_CRITICO = () -> Long.MAX_VALUE;
    /** Restituisce sempre 0: sempre critico. */
    private static final RandomGenerator SEMPRE_CRITICO = () -> 0L;

    private static final Posizione QUI = new Posizione(3, 3);

    private final CalcolatoreDanni calcolatore = new CalcolatoreDanni(List.of(
            new ModificatorePosizioneSuggerita(), new ModificatoreAlleSpalle(),
            new ModificatoreCritico(MAI_CRITICO, 0.1)));

    @Test
    void senzaBonusIlDannoEQuelloBase() {
        RisultatoColpo risultato = calcolatore.calcola(6, new ContestoColpo(QUI, LatoDrago.FIANCHI, null));
        assertEquals(6, risultato.danno());
        assertTrue(risultato.bonus().isEmpty());
    }

    @Test
    @DisplayName("Colpire dalla casella indicata dal pupo raddoppia il danno")
    void ilConsiglioDelPupoRaddoppia() {
        Suggerimento consiglio = new Suggerimento(QUI, "Colpiscilo sul fianco!", TipoSuggerimento.ATTACCO);
        RisultatoColpo risultato = calcolatore.calcola(6, new ContestoColpo(QUI, LatoDrago.FIANCHI, consiglio));
        assertEquals(12, risultato.danno());
        assertTrue(risultato.haBonus(ModificatorePosizioneSuggerita.ETICHETTA));
    }

    @Test
    void ilConsiglioValeSoloNellaCasellaGiusta() {
        Suggerimento consiglio = new Suggerimento(new Posizione(9, 9), "...", TipoSuggerimento.ATTACCO);
        RisultatoColpo risultato = calcolatore.calcola(6, new ContestoColpo(QUI, LatoDrago.FIANCHI, consiglio));
        assertEquals(6, risultato.danno());
    }

    @Test
    void unConsiglioDiFugaNonDaBonus() {
        Suggerimento fuga = new Suggerimento(QUI, "Attento!", TipoSuggerimento.FUGA);
        RisultatoColpo risultato = calcolatore.calcola(6, new ContestoColpo(QUI, LatoDrago.FIANCHI, fuga));
        assertFalse(risultato.haBonus(ModificatorePosizioneSuggerita.ETICHETTA));
    }

    @Test
    void iBonusSiMoltiplicanoTraLoro() {
        Suggerimento consiglio = new Suggerimento(QUI, "Dietro la coda!", TipoSuggerimento.ATTACCO);
        RisultatoColpo risultato = calcolatore.calcola(6, new ContestoColpo(QUI, LatoDrago.CODA, consiglio));
        assertEquals(18, risultato.danno());
        assertEquals(2, risultato.bonus().size());
    }

    @Test
    void ilCriticoDipendeDalCaso() {
        CalcolatoreDanni fortunato = new CalcolatoreDanni(List.of(new ModificatoreCritico(SEMPRE_CRITICO, 0.1)));
        RisultatoColpo risultato = fortunato.calcola(6, new ContestoColpo(QUI, LatoDrago.TESTA, null));
        assertEquals(9, risultato.danno());
        assertTrue(risultato.haBonus(ModificatoreCritico.ETICHETTA));
    }

    @Test
    void nuoveRegoleSiAggiungonoSenzaToccareIlCalcolatore() {
        ModificatoreDanno armaturaDiScaglie = contesto -> Optional.of(new Bonus("SCAGLIE", 0.5));
        CalcolatoreDanni conArmatura = new CalcolatoreDanni(List.of(armaturaDiScaglie));
        assertEquals(3, conArmatura.calcola(6, new ContestoColpo(QUI, LatoDrago.TESTA, null)).danno());
    }

    @Test
    void valoriNonValidi() {
        assertThrows(IllegalArgumentException.class,
                () -> calcolatore.calcola(-1, new ContestoColpo(QUI, LatoDrago.TESTA, null)));
        assertThrows(IllegalArgumentException.class, () -> new ModificatoreCritico(MAI_CRITICO, 2));
        assertThrows(IllegalArgumentException.class, () -> new Bonus("X", 0));
    }
}
