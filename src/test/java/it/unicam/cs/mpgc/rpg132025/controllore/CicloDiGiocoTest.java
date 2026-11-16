package it.unicam.cs.mpgc.rpg132025.controllore;

import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.input.Tasto;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.Schermata;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CicloDiGiocoTest {

    /** Controllore che conta gli aggiornamenti, i tasti ricevuti e i disegni della sua vista. */
    private static final class Contatore implements Controllore {
        private int aggiornamenti;
        private int pressioni;
        private int disegni;
        private final Schermata vista = pennello -> disegni++;

        @Override
        public void aggiorna(double dt, StatoInput input) {
            aggiornamenti++;
            if (input.appenaPremuto(Tasto.AZIONE)) {
                pressioni++;
            }
        }

        @Override
        public Schermata vista() {
            return vista;
        }
    }

    private final GestoreControllori controllori = new GestoreControllori();
    private final Contatore contatore = new Contatore();
    private final StatoInput input = new StatoInput();
    private final CicloDiGioco ciclo = new CicloDiGioco(controllori, input);

    @Test
    void passoFissoIndipendenteDaiFotogrammi() {
        controllori.mostra(contatore);
        ciclo.avanza(0.1);
        assertEquals(6, contatore.aggiornamenti);
        for (int i = 0; i < 10; i++) {
            ciclo.avanza(0.01);
        }
        assertEquals(12, contatore.aggiornamenti);
        ciclo.avanza(5);
        assertEquals(27, contatore.aggiornamenti);
    }

    @Test
    void unaPressioneVieneVistaUnaVolta() {
        controllori.mostra(contatore);
        input.premi(Tasto.AZIONE);
        ciclo.avanza(0.1);
        assertEquals(1, contatore.pressioni);
    }

    @Test
    void siDisegnaLaVistaDelControlloreAttivo() {
        controllori.mostra(contatore);
        ciclo.disegna(null);
        ciclo.disegna(null);
        assertEquals(2, contatore.disegni);
    }
}
