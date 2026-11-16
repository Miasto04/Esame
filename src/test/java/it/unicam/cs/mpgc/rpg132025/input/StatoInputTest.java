package it.unicam.cs.mpgc.rpg132025.input;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatoInputTest {

    private final StatoInput input = new StatoInput();
    private final ComandiTouch comandi = new ComandiTouch();

    @Test
    void vinceLUltimaDirezionePremuta() {
        input.premi(Tasto.SU);
        input.premi(Tasto.DESTRA);
        assertEquals(Optional.of(Direzione.DESTRA), input.direzione());
        input.rilascia(Tasto.DESTRA);
        assertEquals(Optional.of(Direzione.SU), input.direzione());
        input.rilascia(Tasto.SU);
        assertEquals(Optional.empty(), input.direzione());
    }

    @Test
    void appenaPremutoDuraUnSoloAggiornamento() {
        input.premi(Tasto.AZIONE);
        assertTrue(input.appenaPremuto(Tasto.AZIONE));
        input.fineAggiornamento();
        assertFalse(input.appenaPremuto(Tasto.AZIONE));
        assertTrue(input.premuto(Tasto.AZIONE));
        input.azzera();
        assertFalse(input.premuto(Tasto.AZIONE));
    }

    @Test
    void laCroceDirezionaleSulloSchermo() {
        input.premiPuntatore(ComandiTouch.CROCE_X + 15, ComandiTouch.CROCE_Y + 1);
        assertEquals(Optional.of(Direzione.DESTRA), comandi.direzione(input));
        input.muoviPuntatore(ComandiTouch.CROCE_X, ComandiTouch.CROCE_Y - 15);
        assertEquals(Optional.of(Direzione.SU), comandi.direzione(input));
        input.rilasciaPuntatore(ComandiTouch.CROCE_X, ComandiTouch.CROCE_Y - 15);
        assertEquals(Optional.empty(), comandi.direzione(input));
    }

    @Test
    void iPulsantiReagisconoSoloAlTocco() {
        input.premiPuntatore(ComandiTouch.ATTACCO_X, ComandiTouch.ATTACCO_Y);
        assertTrue(comandi.attacco(input));
        assertFalse(comandi.pozione(input));
        input.fineAggiornamento();
        assertFalse(comandi.attacco(input));
        input.rilasciaPuntatore(ComandiTouch.ATTACCO_X, ComandiTouch.ATTACCO_Y);
        input.premiPuntatore(ComandiTouch.MENU_X + 2, ComandiTouch.MENU_Y + 2);
        assertTrue(comandi.menu(input));
    }
}
