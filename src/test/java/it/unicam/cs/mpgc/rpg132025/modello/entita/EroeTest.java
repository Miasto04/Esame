package it.unicam.cs.mpgc.rpg132025.modello.entita;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.Inventario;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.TipoOggetto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EroeTest {

    private Eroe eroe;

    @BeforeEach
    void preparaEroe() {
        eroe = new Eroe(new Posizione(5, 5));
    }

    @Test
    void puntiVitaRestanoNeiLimiti() {
        PuntiVita vita = new PuntiVita(4, 10);
        assertEquals(4, vita.subisci(9));
        assertTrue(vita.esauriti());
        assertEquals(10, vita.cura(50));
        assertTrue(vita.pieni());
        assertThrows(IllegalArgumentException.class, () -> new PuntiVita(11, 10));
        assertThrows(IllegalArgumentException.class, () -> vita.subisci(-1));
    }

    @Test
    void dopoUnColpoRestaInvulnerabilePerUnPo() {
        assertEquals(3, eroe.subisciDanno(3));
        assertTrue(eroe.isInvulnerabile());
        assertEquals(0, eroe.subisciDanno(3));
        eroe.aggiorna(Eroe.DURATA_INVULNERABILITA + 0.01);
        assertFalse(eroe.isInvulnerabile());
        assertEquals(3, eroe.subisciDanno(3));
        assertEquals(Eroe.VITA_MASSIMA - 6, eroe.vita());
    }

    @Test
    void laSpadaHaBisognoDiRicaricarsi() {
        assertTrue(eroe.puoAttaccare());
        eroe.avviaAttacco();
        assertTrue(eroe.staAttaccando());
        assertFalse(eroe.puoAttaccare());
        assertThrows(IllegalStateException.class, eroe::avviaAttacco);
        eroe.aggiorna(Eroe.RICARICA_ATTACCO);
        assertTrue(eroe.puoAttaccare());
    }

    @Test
    void camminaUnaCasellaAllaVoltaConMovimentoFluido() {
        eroe.cammina(new Posizione(6, 5));
        assertEquals(new Posizione(6, 5), eroe.posizione());
        assertTrue(eroe.inMovimento());
        eroe.aggiorna(Eroe.DURATA_PASSO / 2);
        assertEquals(5.5, eroe.xVisivo(), 1e-9);
        eroe.aggiorna(Eroe.DURATA_PASSO);
        assertFalse(eroe.inMovimento());
        assertEquals(6.0, eroe.xVisivo(), 1e-9);
        assertThrows(IllegalArgumentException.class, () -> eroe.cammina(new Posizione(9, 9)));
    }

    @Test
    void laCasellaDavantiDipendeDallOrientamento() {
        eroe.orienta(Direzione.SINISTRA);
        assertEquals(new Posizione(4, 5), eroe.casellaDavanti());
    }

    @Test
    void recuperaVitaSoloSeFerito() {
        assertEquals(0, eroe.recuperaVita(4));
        eroe.subisciDanno(5);
        assertEquals(4, eroe.recuperaVita(4));
        assertEquals(Eroe.VITA_MASSIMA - 1, eroe.vita());
    }

    @Test
    void inventario() {
        Inventario inventario = new Inventario();
        inventario.aggiungi(TipoOggetto.POZIONE);
        inventario.aggiungi(TipoOggetto.POZIONE, 2);
        assertEquals(3, inventario.quantita(TipoOggetto.POZIONE));
        assertTrue(inventario.rimuovi(TipoOggetto.POZIONE));
        assertFalse(inventario.rimuovi(TipoOggetto.BACCA));
        assertEquals(2, Inventario.da(inventario.contenuto()).quantita(TipoOggetto.POZIONE));
        assertThrows(IllegalArgumentException.class, () -> inventario.aggiungi(TipoOggetto.BACCA, -1));
    }
}
