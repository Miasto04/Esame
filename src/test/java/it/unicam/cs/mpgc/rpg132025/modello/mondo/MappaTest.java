package it.unicam.cs.mpgc.rpg132025.modello.mondo;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MappaTest {

    private static final Terreno E = Terreno.ERBA;
    private static final Terreno A = Terreno.ACQUA;
    private static final Terreno R = Terreno.ROCCIA;

    private final Mappa mappa = new Mappa(new Terreno[][] {
        {E, E, E, E},
        {E, R, R, E},
        {E, E, A, E},
    });

    @Test
    void conosceDimensioniETerreni() {
        assertEquals(4, mappa.larghezza());
        assertEquals(3, mappa.altezza());
        assertEquals(R, mappa.terreno(new Posizione(1, 1)));
        assertTrue(mappa.percorribile(new Posizione(0, 0)));
        assertFalse(mappa.percorribile(new Posizione(2, 2)));
    }

    @Test
    void fuoriDallaMappaCEIlMare() {
        Posizione fuori = new Posizione(-1, 5);
        assertFalse(mappa.contiene(fuori));
        assertEquals(Terreno.ACQUA, mappa.terreno(fuori));
        assertFalse(mappa.percorribile(fuori));
    }

    @Test
    void trovaLePosizioniDiUnTerreno() {
        assertEquals(List.of(new Posizione(1, 1), new Posizione(2, 1)), mappa.posizioniDi(R));
    }

    @Test
    void rifiutaGriglieNonRettangolari() {
        Terreno[][] storta = {{E, E}, {E}};
        assertThrows(IllegalArgumentException.class, () -> new Mappa(storta));
        assertThrows(IllegalArgumentException.class, () -> new Mappa(new Terreno[0][0]));
    }

    @Test
    void ogniTerrenoHaUnSimboloUnico() {
        for (Terreno terreno : Terreno.values()) {
            assertEquals(Optional.of(terreno), Terreno.daSimbolo(terreno.simbolo()));
        }
        assertEquals(Optional.empty(), Terreno.daSimbolo('?'));
    }

    @Test
    void ricercaPercorsoAggiraGliOstacoli() {
        Posizione partenza = new Posizione(1, 0);
        Posizione arrivo = new Posizione(1, 2);
        Optional<Direzione> passo = RicercaPercorso.primoPassoVerso(partenza, arrivo::equals,
                mappa::percorribile, 100);
        assertTrue(passo.isPresent());
        assertEquals(Direzione.SINISTRA, passo.get());
    }

    @Test
    void ricercaPercorsoSenzaStrada() {
        Optional<Direzione> passo = RicercaPercorso.primoPassoVerso(new Posizione(0, 0),
                new Posizione(2, 2)::equals, mappa::percorribile, 100);
        assertEquals(Optional.empty(), passo);
    }

    @Test
    void casellaPiuVicinaCheRispettaUnaCondizione() {
        Optional<Posizione> trovata = RicercaPercorso.piuVicina(new Posizione(0, 0),
                casella -> casella.y() == 2, mappa::percorribile, 100);
        assertEquals(Optional.of(new Posizione(0, 2)), trovata);
    }
}
