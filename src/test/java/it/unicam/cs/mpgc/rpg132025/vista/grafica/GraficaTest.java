package it.unicam.cs.mpgc.rpg132025.vista.grafica;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.TipoOggetto;
import it.unicam.cs.mpgc.rpg132025.vista.Schermo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraficaTest {

    private static final int R = Tavolozza.rgb(255, 0, 0);
    private static final int B = Tavolozza.rgb(0, 0, 255);

    @Test
    void rotazioneESpecchio() {
        Sprite sprite = new Sprite(2, 1, new int[] {R, B});
        Sprite ruotato = sprite.ruotata();
        assertEquals(1, ruotato.larghezza());
        assertEquals(2, ruotato.altezza());
        assertEquals(R, ruotato.pixel(0, 0));
        assertEquals(B, ruotato.pixel(0, 1));
        assertEquals(B, sprite.specchiata().pixel(0, 0));
        assertEquals(R, sprite.ruotata(4).pixel(0, 0));
        assertEquals(Tavolozza.BIANCO, sprite.tinta(Tavolozza.BIANCO).pixel(1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Sprite(2, 2, new int[3]));
    }

    @Test
    void telaPixel() {
        TelaPixel tela = new TelaPixel(5, 5);
        tela.punto(2, 2, R);
        tela.punto(9, 9, R);
        tela.contorno(B);
        assertEquals(B, tela.colore(2, 1));
        assertEquals(Tavolozza.TRASPARENTE, tela.colore(1, 1));
        tela.disegna(new String[] {"x.", ".x"}, TelaPixel.colori("x", R), 0, 0);
        assertEquals(R, tela.colore(1, 1));
        assertThrows(IllegalArgumentException.class,
                () -> tela.disegna(new String[] {"?"}, TelaPixel.colori("x", R), 0, 0));
    }

    @Test
    void carattereAPixel() {
        FontPixel font = new FontPixel();
        assertEquals(0, font.larghezza(""));
        assertTrue(font.larghezza("i") < font.larghezza("m"));
        assertEquals(font.larghezza("drago") * 2, font.larghezza("drago", 2));
        List<String> righe = font.aCapo("Il pupo ti dirà dove colpire il drago", 60);
        assertTrue(righe.size() > 1);
        for (String riga : righe) {
            assertTrue(font.larghezza(riga) <= 60);
        }
    }

    @Test
    void ilFumettoNonCopreLEroe() {
        Riquadro sopra = Pannelli.posizioneFumetto(60, 20, 160, 80, 95, List.of());
        assertTrue(sopra.y() + sopra.altezza() < 80);

        Riquadro eroeSopra = new Riquadro(152, 60, 16, 18);
        Riquadro sotto = Pannelli.posizioneFumetto(60, 20, 160, 80, 95, List.of(eroeSopra));
        assertFalse(sotto.interseca(eroeSopra));
        assertTrue(sotto.y() > 95);

        Riquadro eroeASinistra = new Riquadro(120, 60, 16, 18);
        Riquadro diLato = Pannelli.posizioneFumetto(60, 20, 160, 80, 95, List.of(eroeASinistra));
        assertFalse(diLato.interseca(eroeASinistra));
        assertTrue(diLato.y() < 80);
        assertTrue(diLato.dentro(Schermo.LARGHEZZA, Schermo.ALTEZZA));
    }

    @Test
    void gliSpriteVengonoCreatiUnaVoltaSola() {
        Grafica grafica = new Grafica();
        SpritePersonaggi personaggi = grafica.personaggi();
        assertSame(personaggi.eroe(Direzione.GIU, 1), personaggi.eroe(Direzione.GIU, 1));
        assertSame(grafica.terreno().acqua(0, 5), grafica.terreno().acqua(0, 5));
        Sprite drago = personaggi.drago(Direzione.SU, SpritePersonaggi.PosaDrago.ALI_ALZATE);
        assertSame(personaggi.sagomaBianca(drago), personaggi.sagomaBianca(drago));
        for (TipoOggetto tipo : TipoOggetto.values()) {
            assertNotNull(personaggi.oggetto(tipo));
        }
    }
}
