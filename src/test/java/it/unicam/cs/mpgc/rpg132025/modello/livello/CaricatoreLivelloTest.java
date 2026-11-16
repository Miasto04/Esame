package it.unicam.cs.mpgc.rpg132025.modello.livello;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Terreno;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.OggettoATerra;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.TipoOggetto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CaricatoreLivelloTest {

    private final CaricatoreLivello caricatore = new CaricatoreLivello();

    @Test
    void leggeTerreniPersonaggiEOggetti() {
        DefinizioneLivello livello = caricatore.interpreta("""
                ; un commento
                ~~~~~~
                ~@..p~
                ~.D.!~
                ~T..b~
                ~~~~~~
                """.lines().toList());
        assertEquals(6, livello.mappa().larghezza());
        assertEquals(5, livello.mappa().altezza());
        assertEquals(new Posizione(1, 1), livello.inizioEroe());
        assertEquals(new Posizione(4, 1), livello.inizioPupo());
        assertEquals(new Posizione(2, 2), livello.inizioDrago());
        assertEquals(Terreno.ALBERO, livello.mappa().terreno(new Posizione(1, 3)));
        assertEquals(Terreno.ERBA, livello.mappa().terreno(new Posizione(1, 1)));
        assertEquals(List.of(new OggettoATerra(TipoOggetto.POZIONE, new Posizione(4, 2)),
                new OggettoATerra(TipoOggetto.BACCA, new Posizione(4, 3))), livello.oggetti());
    }

    @Test
    void segnalaGliErrori() {
        assertThrows(LivelloNonValidoException.class, () -> caricatore.interpreta(List.of()));
        assertThrows(LivelloNonValidoException.class, () -> caricatore.interpreta(List.of("~p.D.~", "~....~")));
        assertThrows(LivelloNonValidoException.class,
                () -> caricatore.interpreta(List.of("@p.D.~", "~...?~", "~....~")));
        assertThrows(LivelloNonValidoException.class,
                () -> caricatore.interpreta(List.of("@p.D.", "~...~", "~....~")));
        assertThrows(LivelloNonValidoException.class,
                () -> caricatore.interpreta(List.of("@@.D.", "p....", ".....")));
        assertThrows(LivelloNonValidoException.class,
                () -> caricatore.interpreta(List.of("@p..D", ".....", ".....")));
    }

    @Test
    void caricaIlLivelloDelGioco() {
        DefinizioneLivello isola = caricatore.carica("/livelli/isola.txt");
        assertTrue(isola.mappa().larghezza() > 20);
        assertTrue(isola.mappa().percorribile(isola.inizioEroe()));
        assertEquals(3, isola.oggetti().stream().filter(o -> o.tipo() == TipoOggetto.POZIONE).count());
    }

    @Test
    void livelloInesistente() {
        assertThrows(LivelloNonValidoException.class, () -> caricatore.carica("/livelli/nessuno.txt"));
    }
}
