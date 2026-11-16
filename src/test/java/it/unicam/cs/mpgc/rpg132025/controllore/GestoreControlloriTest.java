package it.unicam.cs.mpgc.rpg132025.controllore;

import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.vista.schermate.Schermata;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GestoreControlloriTest {

    /** Controllore che annota in un registro comune quando entra, esce, lavora e si disegna. */
    private static final class Annotatore implements Controllore {
        private final String nome;
        private final List<String> registro;

        Annotatore(String nome, List<String> registro) {
            this.nome = nome;
            this.registro = registro;
        }

        @Override
        public void entra() {
            registro.add("entra " + nome);
        }

        @Override
        public void esci() {
            registro.add("esci " + nome);
        }

        @Override
        public void aggiorna(double dt, StatoInput input) {
            registro.add("aggiorna " + nome);
        }

        @Override
        public Schermata vista() {
            return pennello -> registro.add("disegna " + nome);
        }
    }

    private final List<String> registro = new ArrayList<>();
    private final GestoreControllori controllori = new GestoreControllori();

    @Test
    void passandoAUnAltraSchermataIlVecchioControlloreEsceEIlNuovoEntra() {
        Annotatore menu = new Annotatore("menu", registro);
        Annotatore gioco = new Annotatore("gioco", registro);
        controllori.mostra(menu);
        controllori.mostra(gioco);
        controllori.aggiorna(0.1, new StatoInput());
        controllori.disegna(null);
        assertEquals(List.of("entra menu", "esci menu", "entra gioco", "aggiorna gioco", "disegna gioco"),
                registro);
        assertSame(gioco, controllori.corrente().orElseThrow());
    }

    @Test
    void senzaControlloreAttivoNonSuccedeNulla() {
        controllori.aggiorna(0.1, new StatoInput());
        controllori.disegna(null);
        assertFalse(controllori.corrente().isPresent());
        assertThrows(NullPointerException.class, () -> controllori.mostra(null));
    }
}
