package it.unicam.cs.mpgc.rpg132025.modello.drago;

import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DragoTest {

    private static final double PASSO = 1.0 / 60;

    /** Un mondo finto e controllabile, tutto percorribile. */
    private static final class ContestoFinto implements ContestoDrago {
        private final List<ZonaPericolo> zone = new ArrayList<>();
        private final List<EventoGioco> eventi = new ArrayList<>();
        private Posizione eroe = new Posizione(0, 0);
        private double tempo;

        @Override
        public Posizione posizioneEroe() {
            return eroe;
        }

        @Override
        public boolean percorribile(Posizione casella) {
            return true;
        }

        @Override
        public double tempo() {
            return tempo;
        }

        @Override
        public void aggiungiZona(ZonaPericolo zona) {
            zone.add(zona);
        }

        @Override
        public RandomGenerator casuale() {
            return () -> Long.MAX_VALUE;
        }

        @Override
        public void pubblica(EventoGioco evento) {
            eventi.add(evento);
        }

        boolean haPubblicato(Class<? extends EventoGioco> tipo) {
            return eventi.stream().anyMatch(tipo::isInstance);
        }
    }

    private ContestoFinto contesto;

    @BeforeEach
    void preparaContesto() {
        contesto = new ContestoFinto();
    }

    private void avanza(Drago drago, double secondi) {
        for (int i = 0; i < Math.round(secondi / PASSO); i++) {
            contesto.tempo += PASSO;
            drago.aggiorna(PASSO, contesto);
        }
    }

    @Test
    void geometriaDelCorpo() {
        Drago drago = new Drago(new Posizione(10, 10), Direzione.GIU);
        assertTrue(drago.occupa(new Posizione(11, 11)));
        assertFalse(drago.occupa(new Posizione(12, 10)));
        assertEquals(List.of(new Posizione(10, 12), new Posizione(11, 12)), drago.celleLato(LatoDrago.TESTA));
        assertEquals(List.of(new Posizione(10, 9), new Posizione(11, 9)), drago.celleLato(LatoDrago.CODA));
        assertEquals(4, drago.celleLato(LatoDrago.FIANCHI).size());
        assertEquals(Optional.of(LatoDrago.FIANCHI), drago.latoDi(new Posizione(12, 10)));
        assertEquals(Optional.empty(), drago.latoDi(new Posizione(12, 12)));
        assertEquals(3, drago.distanzaDa(new Posizione(14, 10)));
        assertEquals(0, drago.distanzaDa(new Posizione(10, 11)));
    }

    @Test
    void laFiammataSiAllargaDavantiAllaBocca() {
        Drago drago = new Drago(new Posizione(10, 10), Direzione.DESTRA);
        List<Posizione> cono = drago.conoDiFuoco(4);
        assertTrue(cono.contains(new Posizione(12, 10)));
        assertTrue(cono.contains(new Posizione(15, 8)));
        assertFalse(cono.contains(new Posizione(9, 10)));
    }

    @Test
    @DisplayName("Chi arriva da dietro non sveglia il drago")
    void dormeSeLEroeArrivaDaDietro() {
        Drago drago = new Drago(new Posizione(10, 10), Direzione.GIU);
        contesto.eroe = new Posizione(10, 8);
        avanza(drago, 1);
        assertFalse(drago.isSveglio());
        assertEquals(AspettoDrago.DORME, drago.aspetto());
        assertEquals(LatoDrago.CODA, drago.latoVulnerabile());
    }

    @Test
    void siSvegliaSeLEroeArrivaDaDavanti() {
        Drago drago = new Drago(new Posizione(10, 10), Direzione.GIU);
        contesto.eroe = new Posizione(10, 14);
        avanza(drago, PASSO);
        assertEquals(AspettoDrago.RUGGISCE, drago.aspetto());
        assertTrue(contesto.haPubblicato(EventoDragoRisvegliato.class));
        avanza(drago, StatoRisveglio.DURATA + 0.1);
        assertTrue(drago.isSveglio());
    }

    @Test
    void siSvegliaSeVieneColpito() {
        Drago drago = new Drago(new Posizione(10, 10), Direzione.GIU);
        contesto.eroe = new Posizione(10, 8);
        assertEquals(10, drago.subisciDanno(10));
        avanza(drago, PASSO);
        assertTrue(drago.isSveglio());
    }

    @Test
    @DisplayName("Morde chi gli sta davanti e intanto scopre i fianchi")
    void mordeEScopreIFianchi() {
        Drago drago = new Drago(new Posizione(10, 10), Direzione.GIU, Drago.VITA_MASSIMA, true, false);
        contesto.eroe = new Posizione(10, 12);
        avanza(drago, 0.5);
        assertEquals(AspettoDrago.MORDE, drago.aspetto());
        assertEquals(LatoDrago.FIANCHI, drago.latoVulnerabile());
        assertEquals(1, contesto.zone.size());
        ZonaPericolo morso = contesto.zone.getFirst();
        assertEquals(TipoAttacco.MORSO, morso.tipo());
        assertTrue(morso.contiene(new Posizione(10, 12)));
        assertTrue(morso.inPreavviso(contesto.tempo));
    }

    @Test
    @DisplayName("Mentre sputa fuoco il drago lascia scoperta la coda")
    void sputaFuocoEScopreLaCoda() {
        Drago drago = new Drago(new Posizione(10, 10), Direzione.GIU, Drago.VITA_MASSIMA, true, false);
        contesto.eroe = new Posizione(10, 14);
        avanza(drago, 0.5);
        assertEquals(AspettoDrago.CARICA_FUOCO, drago.aspetto());
        assertEquals(LatoDrago.CODA, drago.latoVulnerabile());
        assertTrue(contesto.zone.getFirst().contiene(contesto.eroe));
        avanza(drago, StatoSoffioDiFuoco.CARICA);
        assertEquals(AspettoDrago.SOFFIA_FUOCO, drago.aspetto());
    }

    @Test
    void sottoLaSogliaDiventaFurioso() {
        Drago drago = new Drago(new Posizione(10, 10), Direzione.GIU, Drago.VITA_MASSIMA, true, false);
        contesto.eroe = new Posizione(30, 30);
        drago.subisciDanno((int) (Drago.VITA_MASSIMA * 0.7));
        assertTrue(drago.isFurioso());
        avanza(drago, PASSO);
        assertTrue(contesto.haPubblicato(EventoDragoFurioso.class));
    }

    @Test
    @DisplayName("Se l'eroe è lontano vola e gli atterra addosso")
    void volaVersoLEroeLontano() {
        Drago drago = new Drago(new Posizione(10, 10), Direzione.GIU, Drago.VITA_MASSIMA, true, false);
        contesto.eroe = new Posizione(10, 25);
        avanza(drago, 0.6);
        assertTrue(drago.inVolo());
        assertEquals(0, drago.subisciDanno(10));
        assertEquals(TipoAttacco.ATTERRAGGIO, contesto.zone.getFirst().tipo());
        avanza(drago, StatoVolo.DECOLLO + StatoVolo.VOLO);
        assertFalse(drago.inVolo());
        assertTrue(drago.occupa(contesto.eroe));
        assertTrue(contesto.haPubblicato(EventoDragoAtterrato.class));
    }

    @Test
    void sconfitto() {
        Drago drago = new Drago(new Posizione(10, 10), Direzione.GIU);
        drago.subisciDanno(Drago.VITA_MASSIMA);
        assertFalse(drago.isViva());
        assertEquals(AspettoDrago.SCONFITTO, drago.aspetto());
        assertEquals(LatoDrago.NESSUNO, drago.latoVulnerabile());
    }

    @Test
    void zonaDiPericoloNelTempo() {
        ZonaPericolo zona = ZonaPericolo.crea(TipoAttacco.MORSO, List.of(new Posizione(1, 1)), 10, 0.5, 0.2);
        assertTrue(zona.inPreavviso(10.2));
        assertEquals(0.4, zona.progressoPreavviso(10.2), 1e-9);
        assertTrue(zona.attiva(10.6));
        assertTrue(zona.esaurita(10.8));
        assertEquals(TipoAttacco.MORSO.danno(), zona.danno());
    }
}
