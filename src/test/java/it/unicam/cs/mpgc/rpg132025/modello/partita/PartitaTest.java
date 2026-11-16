package it.unicam.cs.mpgc.rpg132025.modello.partita;

import it.unicam.cs.mpgc.rpg132025.modello.comandi.ComandoAttacco;
import it.unicam.cs.mpgc.rpg132025.modello.comandi.ComandoMovimento;
import it.unicam.cs.mpgc.rpg132025.modello.comandi.ComandoUsaOggetto;
import it.unicam.cs.mpgc.rpg132025.modello.combattimento.EventoColpoInflitto;
import it.unicam.cs.mpgc.rpg132025.modello.combattimento.ModificatoreAlleSpalle;
import it.unicam.cs.mpgc.rpg132025.modello.combattimento.ModificatorePosizioneSuggerita;
import it.unicam.cs.mpgc.rpg132025.modello.drago.Drago;
import it.unicam.cs.mpgc.rpg132025.modello.entita.Eroe;
import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;
import it.unicam.cs.mpgc.rpg132025.modello.livello.CaricatoreLivello;
import it.unicam.cs.mpgc.rpg132025.modello.livello.DefinizioneLivello;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.EventoOggettoNonUsabile;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.TipoOggetto;
import it.unicam.cs.mpgc.rpg132025.modello.statistiche.RiepilogoPartita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PartitaTest {

    private static final double PASSO = 1.0 / 60;
    private static final String ISOLA = "/livelli/isola.txt";
    private static final RandomGenerator MAI_CRITICO = () -> Long.MAX_VALUE;
    private static final List<String> ARENA = """
            ~~~~~~~~~~
            ~........~
            ~.@....p.~
            ~........~
            ~...D....~
            ~........~
            ~..!.....~
            ~~~~~~~~~~
            """.lines().toList();

    private final FabbricaPartita fabbrica = new FabbricaPartita(new CaricatoreLivello(), () -> MAI_CRITICO);
    private final List<EventoGioco> eventi = new ArrayList<>();
    private Partita partita;

    @BeforeEach
    void nuovaArena() {
        DefinizioneLivello arena = new CaricatoreLivello().interpreta(ARENA);
        partita = fabbrica.nuovaPartita("arena", arena);
        partita.iscrivi(eventi::add);
    }

    private void avanza(Partita p, double secondi) {
        for (int i = 0; i < Math.round(secondi / PASSO); i++) {
            p.aggiorna(PASSO);
        }
    }

    private void passo(Partita p, Direzione direzione) {
        p.inviaComando(new ComandoMovimento(direzione));
        p.aggiorna(PASSO);
        p.inviaComando(ComandoMovimento.fermati());
        avanza(p, Eroe.DURATA_PASSO + 0.02);
    }

    private <E extends EventoGioco> List<E> eventi(Class<E> tipo) {
        return eventi.stream().filter(tipo::isInstance).map(tipo::cast).toList();
    }

    private Partita ripristinata(DatiEroe eroe, DatiDrago drago) {
        InstantaneaPartita instantanea = new InstantaneaPartita(InstantaneaPartita.VERSIONE_CORRENTE, ISOLA, eroe,
                new Posizione(14, 25), drago, List.of(), FabbricaPartita.ORA_DI_INIZIO, RiepilogoPartita.vuoto());
        Partita ripristinata = fabbrica.ripristina(instantanea);
        ripristinata.iscrivi(eventi::add);
        return ripristinata;
    }

    @Test
    void lEroeSiMuoveEFermaControLAcqua() {
        passo(partita, Direzione.DESTRA);
        assertEquals(new Posizione(3, 2), partita.eroe().posizione());
        passo(partita, Direzione.SU);
        passo(partita, Direzione.SU);
        assertEquals(new Posizione(3, 1), partita.eroe().posizione());
        assertEquals(Direzione.SU, partita.eroe().direzione());
    }

    @Test
    void raccoglieGliOggettiCamminandoci() {
        passo(partita, Direzione.GIU);
        passo(partita, Direzione.GIU);
        passo(partita, Direzione.GIU);
        passo(partita, Direzione.GIU);
        passo(partita, Direzione.DESTRA);
        assertEquals(new Posizione(3, 6), partita.eroe().posizione());
        assertEquals(1, partita.eroe().quantitaOggetto(TipoOggetto.POZIONE));
        assertTrue(partita.oggettiATerra().isEmpty());
    }

    @Test
    @DisplayName("Seguendo il pupo alle spalle del drago addormentato il colpo vale il triplo")
    void colpoGuidatoAlleSpalle() {
        avanza(partita, 0.1);
        assertEquals(new Posizione(4, 3), partita.pupo().suggerimento().orElseThrow().posizione());
        passo(partita, Direzione.DESTRA);
        passo(partita, Direzione.GIU);
        passo(partita, Direzione.DESTRA);
        assertFalse(partita.drago().isSveglio());
        passo(partita, Direzione.GIU);
        assertEquals(new Posizione(4, 3), partita.eroe().posizione());
        assertEquals(Direzione.GIU, partita.eroe().direzione());

        partita.inviaComando(new ComandoAttacco());
        partita.aggiorna(PASSO);

        EventoColpoInflitto colpo = eventi(EventoColpoInflitto.class).getFirst();
        assertEquals(Eroe.DANNO_BASE * 3, colpo.risultato().danno());
        assertTrue(colpo.risultato().haBonus(ModificatorePosizioneSuggerita.ETICHETTA));
        assertTrue(colpo.risultato().haBonus(ModificatoreAlleSpalle.ETICHETTA));
        assertEquals(Drago.VITA_MASSIMA - Eroe.DANNO_BASE * 3, partita.drago().vita());
        assertEquals(1, partita.riepilogo().colpiASegno());
    }

    @Test
    void colpireIlVuotoNonFaNiente() {
        partita.inviaComando(new ComandoAttacco());
        partita.aggiorna(PASSO);
        assertTrue(partita.eroe().staAttaccando());
        assertTrue(eventi(EventoColpoInflitto.class).isEmpty());
    }

    @Test
    void usaUnaPozioneSoloSeServe() {
        Partita ferito = ripristinata(new DatiEroe(new Posizione(12, 25), Direzione.SU, 4,
                Map.of(TipoOggetto.POZIONE, 1)), new DatiDrago(new Posizione(12, 9), Direzione.GIU, 150, false, false));
        ferito.inviaComando(new ComandoUsaOggetto(TipoOggetto.POZIONE));
        ferito.aggiorna(PASSO);
        assertEquals(10, ferito.eroe().vita());
        assertEquals(0, ferito.eroe().quantitaOggetto(TipoOggetto.POZIONE));

        ferito.inviaComando(new ComandoUsaOggetto(TipoOggetto.POZIONE));
        ferito.aggiorna(PASSO);
        EventoOggettoNonUsabile rifiuto = eventi(EventoOggettoNonUsabile.class).getFirst();
        assertEquals(EventoOggettoNonUsabile.Motivo.NON_POSSEDUTO, rifiuto.motivo());
    }

    @Test
    void ilFaloCura() {
        Partita vicinoAlFuoco = ripristinata(new DatiEroe(new Posizione(10, 24), Direzione.SU, 6, Map.of()),
                new DatiDrago(new Posizione(12, 9), Direzione.GIU, 150, false, false));
        avanza(vicinoAlFuoco, Partita.INTERVALLO_CURA_FALO + 0.1);
        assertEquals(7, vicinoAlFuoco.eroe().vita());
    }

    @Test
    @DisplayName("Restare davanti al drago sveglio fa perdere la partita")
    void sconfitta() {
        Partita davanti = ripristinata(new DatiEroe(new Posizione(12, 11), Direzione.SU, 3, Map.of()),
                new DatiDrago(new Posizione(12, 9), Direzione.GIU, 150, true, false));
        for (int i = 0; i < 600 && !davanti.stato().conclusa(); i++) {
            davanti.aggiorna(PASSO);
        }
        assertEquals(StatoPartita.SCONFITTA, davanti.stato());
        assertEquals(List.of(new EventoPartitaConclusa(StatoPartita.SCONFITTA)), eventi(EventoPartitaConclusa.class));
    }

    @Test
    void vittoria() {
        Partita quasiVinta = ripristinata(new DatiEroe(new Posizione(12, 8), Direzione.GIU, 12, Map.of()),
                new DatiDrago(new Posizione(12, 9), Direzione.GIU, 1, false, false));
        quasiVinta.inviaComando(new ComandoAttacco());
        quasiVinta.aggiorna(PASSO);
        assertEquals(StatoPartita.VITTORIA, quasiVinta.stato());
        double tempo = quasiVinta.tempo();
        quasiVinta.aggiorna(1);
        assertEquals(tempo, quasiVinta.tempo(), 1e-9);
    }

    @Test
    void instantaneaERipristinoCoincidono() {
        Partita partitaVera = fabbrica.nuovaPartita();
        partitaVera.inviaComando(new ComandoMovimento(Direzione.SU));
        avanza(partitaVera, 0.5);
        InstantaneaPartita salvata = partitaVera.creaInstantanea();
        Partita ricaricata = fabbrica.ripristina(salvata);
        assertEquals(salvata, ricaricata.creaInstantanea());
        assertEquals(partitaVera.eroe().posizione(), ricaricata.eroe().posizione());
    }

    @Test
    void salvataggiNonValidi() {
        InstantaneaPartita futura = new InstantaneaPartita(99, ISOLA,
                new DatiEroe(new Posizione(12, 25), Direzione.SU, 12, Map.of()), new Posizione(14, 25),
                new DatiDrago(new Posizione(12, 9), Direzione.GIU, 150, false, false), List.of(), 0, null);
        assertThrows(IllegalArgumentException.class, () -> fabbrica.ripristina(futura));
        InstantaneaPartita fuoriMappa = new InstantaneaPartita(1, ISOLA,
                new DatiEroe(new Posizione(-5, 25), Direzione.SU, 12, Map.of()), new Posizione(14, 25),
                new DatiDrago(new Posizione(12, 9), Direzione.GIU, 150, false, false), List.of(), 0, null);
        assertThrows(IllegalArgumentException.class, () -> fabbrica.ripristina(fuoriMappa));
        assertThrows(IllegalArgumentException.class, () -> partita.aggiorna(-1));
    }

    @Test
    void orologioDiGioco() {
        assertEquals("07:00", partita.orario().formattato());
        avanza(partita, 30);
        assertEquals(new Orario(8, 0), partita.orario());
        assertEquals(new Orario(23, 59), Orario.daMinuti(-1));
        assertTrue(new Orario(22, 0).notte());
    }
}
