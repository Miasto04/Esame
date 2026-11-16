package it.unicam.cs.mpgc.rpg132025.controllore;

import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.input.Tasto;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Riquadro;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MenuTest {

    private final List<String> scelte = new ArrayList<>();
    private final StatoInput input = new StatoInput();
    private final Menu menu = new Menu(100, 50, 80, List.of(
            Menu.Voce.sempre("Nuova partita", () -> scelte.add("nuova")),
            new Menu.Voce("Continua", () -> false, () -> scelte.add("continua")),
            Menu.Voce.sempre("Esci", () -> scelte.add("esci"))));

    private void premi(Tasto tasto) {
        input.premi(tasto);
        menu.aggiorna(input);
        input.rilascia(tasto);
        input.fineAggiornamento();
    }

    @Test
    void conLaTastieraSaltaLeVociSpente() {
        premi(Tasto.GIU);
        premi(Tasto.CONFERMA);
        assertEquals(List.of("esci"), scelte);
        premi(Tasto.GIU);
        premi(Tasto.AZIONE);
        assertEquals(List.of("esci", "nuova"), scelte);
    }

    @Test
    void conIlMouseSiCliccaLaVoce() {
        int yTerzaVoce = 50 + 2 * (Menu.ALTEZZA_VOCE + 4) + 3;
        input.premiPuntatore(100, yTerzaVoce);
        menu.aggiorna(input);
        assertEquals(List.of("esci"), scelte);
    }

    @Test
    void unaVoceSpentaNonSiPuoCliccare() {
        int ySecondaVoce = 50 + (Menu.ALTEZZA_VOCE + 4) + 3;
        input.premiPuntatore(100, ySecondaVoce);
        menu.aggiorna(input);
        assertEquals(List.of(), scelte);
    }

    @Test
    void laVistaVedeLeStesseVociDelControllore() {
        assertEquals(3, menu.numeroVoci());
        assertEquals("Continua", menu.testo(1));
        assertFalse(menu.attiva(1));
        assertEquals(new Riquadro(60, 50 + Menu.ALTEZZA_VOCE + 4, 80, Menu.ALTEZZA_VOCE), menu.riquadro(1));
        premi(Tasto.GIU);
        assertEquals(2, menu.selezionata());
    }

    @Test
    void serveAlmenoUnaVoce() {
        assertThrows(IllegalArgumentException.class, () -> new Menu(0, 0, 10, List.of()));
    }
}
