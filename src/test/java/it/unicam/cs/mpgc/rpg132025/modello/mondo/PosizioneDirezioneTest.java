package it.unicam.cs.mpgc.rpg132025.modello.mondo;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PosizioneDirezioneTest {

    @Test
    void spostataSegueLaDirezione() {
        Posizione origine = new Posizione(3, 4);
        assertEquals(new Posizione(3, 3), origine.spostata(Direzione.SU));
        assertEquals(new Posizione(3, 5), origine.spostata(Direzione.GIU));
        assertEquals(new Posizione(2, 4), origine.spostata(Direzione.SINISTRA));
        assertEquals(new Posizione(4, 4), origine.spostata(Direzione.DESTRA));
    }

    @Test
    void distanze() {
        Posizione a = new Posizione(1, 1);
        Posizione b = new Posizione(4, 3);
        assertEquals(5, a.distanzaManhattan(b));
        assertEquals(3, a.distanzaChebyshev(b));
    }

    @Test
    void rotazioniEOpposte() {
        assertEquals(Direzione.GIU, Direzione.SU.opposta());
        assertEquals(Direzione.DESTRA, Direzione.SU.ruotataOraria());
        assertEquals(Direzione.SINISTRA, Direzione.SU.ruotataAntioraria());
        for (Direzione direzione : Direzione.values()) {
            assertEquals(direzione, direzione.ruotataOraria().ruotataAntioraria());
            assertEquals(direzione, direzione.opposta().opposta());
        }
        assertTrue(Direzione.DESTRA.orizzontale());
        assertFalse(Direzione.GIU.orizzontale());
    }

    @Test
    void versoSceglieLAsseDominante() {
        Posizione centro = new Posizione(5, 5);
        assertEquals(Optional.of(Direzione.DESTRA), Direzione.verso(centro, new Posizione(9, 6)));
        assertEquals(Optional.of(Direzione.SU), Direzione.verso(centro, new Posizione(4, 1)));
        assertEquals(Optional.empty(), Direzione.verso(centro, centro));
    }
}
