package it.unicam.cs.mpgc.rpg132025.modello.pupo;

import it.unicam.cs.mpgc.rpg132025.modello.drago.Drago;
import it.unicam.cs.mpgc.rpg132025.modello.drago.VistaDrago;
import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategieSuggerimentoTest {

    /** Una situazione finta: tutto percorribile tranne il drago, pericoli scelti dal test. */
    private static final class SituazioneFinta implements SituazioneCombattimento {
        private final Drago drago;
        private final Set<Posizione> pericoli = new HashSet<>();
        private final List<EventoGioco> eventi = new ArrayList<>();
        private Posizione eroe;

        SituazioneFinta(Drago drago, Posizione eroe) {
            this.drago = drago;
            this.eroe = eroe;
        }

        @Override
        public Posizione posizioneEroe() {
            return eroe;
        }

        @Override
        public VistaDrago drago() {
            return drago;
        }

        @Override
        public boolean percorribile(Posizione casella) {
            return !drago.occupa(casella);
        }

        @Override
        public boolean pericolosa(Posizione casella) {
            return pericoli.contains(casella);
        }

        @Override
        public void pubblica(EventoGioco evento) {
            eventi.add(evento);
        }
    }

    private Drago dragoAddormentato;
    private final StrategiaLatoVulnerabile latoVulnerabile = new StrategiaLatoVulnerabile();
    private final StrategiaFugaDalPericolo fuga = new StrategiaFugaDalPericolo();

    @BeforeEach
    void preparaDrago() {
        dragoAddormentato = new Drago(new Posizione(10, 10), Direzione.GIU);
    }

    @Test
    @DisplayName("Con il drago addormentato il pupo indica la casella dietro la coda più vicina")
    void indicaLaCodaDelDragoAddormentato() {
        SituazioneFinta situazione = new SituazioneFinta(dragoAddormentato, new Posizione(9, 6));
        Optional<Suggerimento> consiglio = latoVulnerabile.suggerisci(situazione, null);
        assertTrue(consiglio.isPresent());
        assertEquals(new Posizione(10, 9), consiglio.get().posizione());
        assertEquals(TipoSuggerimento.ATTACCO, consiglio.get().tipo());
    }

    @Test
    void troppoLontanoNonDiceNulla() {
        SituazioneFinta situazione = new SituazioneFinta(dragoAddormentato, new Posizione(40, 40));
        assertEquals(Optional.empty(), latoVulnerabile.suggerisci(situazione, null));
    }

    @Test
    void nonIndicaCasellePericolose() {
        SituazioneFinta situazione = new SituazioneFinta(dragoAddormentato, new Posizione(9, 6));
        situazione.pericoli.add(new Posizione(10, 9));
        assertEquals(new Posizione(11, 9), latoVulnerabile.suggerisci(situazione, null).orElseThrow().posizione());
    }

    @Test
    @DisplayName("Un consiglio ancora valido non cambia, così la casella indicata resta ferma")
    void ilConsiglioValidoResta() {
        SituazioneFinta situazione = new SituazioneFinta(dragoAddormentato, new Posizione(9, 6));
        Suggerimento precedente = new Suggerimento(new Posizione(11, 9), "Shh! Colpiscilo da dietro!",
                TipoSuggerimento.ATTACCO);
        assertEquals(precedente, latoVulnerabile.suggerisci(situazione, precedente).orElseThrow());
    }

    @Test
    void nessunConsiglioDAttaccoConIlDragoSconfitto() {
        dragoAddormentato.subisciDanno(Drago.VITA_MASSIMA);
        SituazioneFinta situazione = new SituazioneFinta(dragoAddormentato, new Posizione(10, 8));
        assertEquals(Optional.empty(), latoVulnerabile.suggerisci(situazione, null));
    }

    @Test
    @DisplayName("Se l'eroe è in pericolo il pupo indica la casella sicura più vicina")
    void fugaVersoLaCasellaSicura() {
        SituazioneFinta situazione = new SituazioneFinta(dragoAddormentato, new Posizione(5, 5));
        situazione.pericoli.addAll(List.of(new Posizione(5, 5), new Posizione(5, 6), new Posizione(6, 5)));
        Suggerimento consiglio = fuga.suggerisci(situazione, null).orElseThrow();
        assertEquals(TipoSuggerimento.FUGA, consiglio.tipo());
        assertFalse(situazione.pericolosa(consiglio.posizione()));
        assertEquals(1, consiglio.posizione().distanzaManhattan(situazione.posizioneEroe()));
        assertFalse(consiglio.premiaAttacco());
    }

    @Test
    void senzaPericoloNessunaFuga() {
        SituazioneFinta situazione = new SituazioneFinta(dragoAddormentato, new Posizione(5, 5));
        assertEquals(Optional.empty(), fuga.suggerisci(situazione, null));
    }

    @Test
    @DisplayName("Il pupo segue le strategie in ordine e annuncia il consiglio")
    void ilPupoUsaLePrioritaEAnnuncia() {
        SituazioneFinta situazione = new SituazioneFinta(dragoAddormentato, new Posizione(9, 6));
        Pupo pupo = new Pupo(new Posizione(6, 6), List.of(fuga, latoVulnerabile));
        pupo.aggiorna(1.0 / 60, situazione);
        assertEquals(new Posizione(10, 9), pupo.suggerimento().orElseThrow().posizione());
        assertTrue(pupo.frase().isPresent());
        assertTrue(situazione.eventi.stream().anyMatch(EventoNuovoSuggerimento.class::isInstance));

        situazione.pericoli.add(situazione.posizioneEroe());
        pupo.aggiorna(Pupo.INTERVALLO_VALUTAZIONE + 0.01, situazione);
        assertEquals(TipoSuggerimento.FUGA, pupo.suggerimento().orElseThrow().tipo());
    }

    @Test
    void ilPupoSegueLEroe() {
        SituazioneFinta situazione = new SituazioneFinta(dragoAddormentato, new Posizione(2, 2));
        Pupo pupo = new Pupo(new Posizione(2, 9), List.of());
        for (int i = 0; i < 300; i++) {
            pupo.aggiorna(1.0 / 60, situazione);
        }
        assertTrue(pupo.posizione().distanzaChebyshev(situazione.posizioneEroe()) <= Pupo.DISTANZA_DALL_EROE);
        assertFalse(pupo.posizione().equals(situazione.posizioneEroe()));
    }
}
