package it.unicam.cs.mpgc.rpg132025.vista;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormatiTest {

    @Test
    void ilTempoSiMostraInMinutiESecondi() {
        assertEquals("0:07", Formati.tempo(7.9));
        assertEquals("2:05", Formati.tempo(125));
    }
}
