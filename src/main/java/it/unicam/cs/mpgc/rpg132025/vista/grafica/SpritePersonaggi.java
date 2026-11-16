package it.unicam.cs.mpgc.rpg132025.vista.grafica;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.TipoOggetto;

import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;

import static it.unicam.cs.mpgc.rpg132025.vista.grafica.Tavolozza.*;

/**
 * Gli sprite dei personaggi e degli oggetti.
 * <p>
 * Eroe e pupo sono disegnati come griglie di caratteri (busto) più le gambe,
 * che cambiano nei fotogrammi della camminata; la vista di destra è lo
 * specchio di quella di sinistra. Il drago è visto dall'alto ed è disegnato
 * con forme geometriche rivolto verso l'alto, poi ruotato nelle altre direzioni.
 */
public final class SpritePersonaggi {

    /** Pose disponibili per il drago. */
    public enum PosaDrago {
        ALI_ALZATE,
        ALI_ABBASSATE,
        RIPOSO,
        FAUCI_APERTE
    }

    private static final String[] EROE_FRONTE = {
        "......kkkk......",
        "....kkhhhhkk....",
        "...khhhhhhhhk...",
        "..khhHhhhhHhhk..",
        "..khhhhhhhhhhk..",
        "..khhsssssshhk..",
        "..khssessesshk..",
        "..khssessesshk..",
        "...kssssssssk...",
        "....kSssssSk....",
        "...kwwwwwwwwk...",
        "..kswwwwwwwwsk..",
        "..kkWbbbbbbWkk..",
    };
    private static final String[] EROE_RETRO = {
        "......kkkk......",
        "....kkhhhhkk....",
        "...khhhhhhhhk...",
        "..khhHhhhhHhhk..",
        "..khhhhhhhhhhk..",
        "..khhhhhhhhhhk..",
        "..khhhhHHhhhhk..",
        "..kshhhhhhhhsk..",
        "...khhhhhhhhk...",
        "....kSssssSk....",
        "...kwwwwwwwwk...",
        "..kswWwwwwWwsk..",
        "..kkWbbbbbbWkk..",
    };
    private static final String[] EROE_LATO = {
        "......kkkk......",
        "....kkhhhhkk....",
        "...khhhhhhhhk...",
        "..khhhhhhHhhhk..",
        "..khhhhhhhhhhk..",
        "..kssshhhhhhhk..",
        "..ksesshhhhhhk..",
        ".kssssshhhhhhk..",
        "..kSssshhhhhk...",
        ".....kSssk......",
        "....kwwwwwwk....",
        "....kwwwswwk....",
        "....kbbbbbbk....",
    };
    private static final String[][] EROE_GAMBE = {
        {"...kpppkkpppk...", "...kfffkkfffk...", "....kkk..kkk...."},
        {"...kfffkkpppk...", "....kkk.kfffk...", ".........kkk...."},
        {"...kpppkkfffk...", "...kfffk.kkk....", "....kkk........."},
    };
    private static final String[][] EROE_GAMBE_LATO = {
        {".....kppppk.....", ".....kffffk.....", "......kkkk......"},
        {"....kppkppk.....", "...kffk.kffk....", "...kk....kk....."},
        {".....kppppk.....", ".....kffffk.....", "......kkkk......"},
    };

    private static final String[] PUPO_FRONTE = {
        "................",
        "................",
        "................",
        "......kkkk......",
        ".....krrrrk.....",
        "....krrrrrrk....",
        "...kRRRRRRRRk...",
        "...kssessessk...",
        "...kcssssssck...",
        "....kSssssSk....",
        "...ksyyyyyysk...",
        "...kkyYyyYykk...",
        "....kggggggk....",
    };
    private static final String[] PUPO_RETRO = {
        "................",
        "................",
        "................",
        "......kkkk......",
        ".....krrrrk.....",
        "....krrrrrrk....",
        "...kRrrrrrrRk...",
        "...kRRRRRRRRk...",
        "...khhhhhhhhk...",
        "....kSssssSk....",
        "...ksyyyyyysk...",
        "...kkyYYYYykk...",
        "....kggggggk....",
    };
    private static final String[] PUPO_LATO = {
        "................",
        "................",
        "................",
        "......kkkk......",
        ".....krrrrk.....",
        "....krrrrrrk....",
        "..kRRRRRRrrk....",
        "...ksesshhk.....",
        "...kcsssshk.....",
        "....kSssk.......",
        "....kyysyk......",
        "....kyYyyk......",
        "....kggggk......",
    };
    private static final String[][] PUPO_GAMBE = {
        {"....ksk..ksk....", "....kfk..kfk....", "....kkk..kkk...."},
        {"....kfk..ksk....", "....kkk..kfk....", ".........kkk...."},
        {"....ksk..kfk....", "....kfk..kkk....", "....kkk........."},
    };
    private static final String[][] PUPO_GAMBE_LATO = {
        {".....ksksk......", ".....kfkfk......", ".....kkkkk......"},
        {"....kskksk......", "...kfk..kfk.....", "...kk....kk....."},
        {".....ksksk......", ".....kfkfk......", ".....kkkkk......"},
    };

    private final EnumMap<Direzione, Sprite[]> eroe = new EnumMap<>(Direzione.class);
    private final EnumMap<Direzione, Sprite[]> pupo = new EnumMap<>(Direzione.class);
    private final EnumMap<Direzione, Map<PosaDrago, Sprite>> drago = new EnumMap<>(Direzione.class);
    private final EnumMap<Direzione, Sprite[]> fendente = new EnumMap<>(Direzione.class);
    private final EnumMap<TipoOggetto, Sprite> oggetti = new EnumMap<>(TipoOggetto.class);
    private final Map<Sprite, Sprite> sagomeBianche = new IdentityHashMap<>();
    private final Map<Sprite, Sprite> sagomeRosse = new IdentityHashMap<>();
    private final Sprite ombraPiccola;
    private final Sprite ombraGrande;
    private final Sprite spada;
    private final Sprite oggettoSconosciuto;

    /** Disegna tutti i personaggi e gli oggetti. */
    public SpritePersonaggi() {
        Map<Character, Integer> coloriEroe = TelaPixel.colori("khHsSewWbpPf", CONTORNO, CAPELLI, CAPELLI_LUCE,
                PELLE, PELLE_OMBRA, OCCHI, CAMICIA, CAMICIA_OMBRA, CINTURA, PANTALONI, PANTALONI_OMBRA, STIVALI);
        Map<Character, Integer> coloriPupo = TelaPixel.colori("krRsSechyYgf", CONTORNO, BERRETTO, BERRETTO_OMBRA,
                PELLE, PELLE_OMBRA, OCCHI, GUANCE, CAPELLI_PUPO, MAGLIA_PUPO, MAGLIA_PUPO_OMBRA, CALZONI_PUPO, STIVALI);
        preparaPersonaggio(eroe, EROE_FRONTE, EROE_RETRO, EROE_LATO, EROE_GAMBE, EROE_GAMBE_LATO, coloriEroe);
        preparaPersonaggio(pupo, PUPO_FRONTE, PUPO_RETRO, PUPO_LATO, PUPO_GAMBE, PUPO_GAMBE_LATO, coloriPupo);

        Map<PosaDrago, Sprite> versoAlto = new EnumMap<>(PosaDrago.class);
        for (PosaDrago posa : PosaDrago.values()) {
            versoAlto.put(posa, creaDrago(posa));
        }
        Sprite[] fendenteADestra = creaFendente();
        for (Direzione direzione : Direzione.values()) {
            int rotazioni = direzione.ordinal();
            Map<PosaDrago, Sprite> ruotati = new EnumMap<>(PosaDrago.class);
            versoAlto.forEach((posa, sprite) -> ruotati.put(posa, sprite.ruotata(rotazioni)));
            drago.put(direzione, ruotati);
            fendente.put(direzione, new Sprite[] {
                fendenteADestra[0].ruotata(rotazioni - 1), fendenteADestra[1].ruotata(rotazioni - 1)});
        }
        ombraPiccola = creaOmbra(12, 4);
        ombraGrande = creaOmbra(30, 9);
        spada = creaSpada();
        oggetti.put(TipoOggetto.POZIONE, creaPozione());
        oggetti.put(TipoOggetto.BACCA, creaBacca());
        oggettoSconosciuto = creaOggettoSconosciuto();
    }

    /**
     * @param direzione  dove guarda
     * @param fotogramma 0 fermo, 1 e 2 camminata
     * @return lo sprite dell'eroe
     */
    public Sprite eroe(Direzione direzione, int fotogramma) {
        return eroe.get(direzione)[Math.floorMod(fotogramma, 3)];
    }

    /**
     * @param direzione  dove guarda
     * @param fotogramma 0 fermo, 1 e 2 camminata
     * @return lo sprite del pupo
     */
    public Sprite pupo(Direzione direzione, int fotogramma) {
        return pupo.get(direzione)[Math.floorMod(fotogramma, 3)];
    }

    /**
     * @param direzione dove guarda
     * @param posa      posa da mostrare
     * @return lo sprite del drago (48x48, centrato sul corpo 2x2)
     */
    public Sprite drago(Direzione direzione, PosaDrago posa) {
        return drago.get(direzione).get(posa);
    }

    /** @return lo sprite tutto bianco, usato per il lampo quando viene colpito */
    public Sprite sagomaBianca(Sprite sprite) {
        return sagomeBianche.computeIfAbsent(sprite, s -> s.tinta(BIANCO));
    }

    /** @return lo sprite tutto rosso, usato per la furia del drago */
    public Sprite sagomaRossa(Sprite sprite) {
        return sagomeRosse.computeIfAbsent(sprite, s -> s.tinta(rgb(255, 40, 30)));
    }

    /**
     * @param direzione  direzione del colpo
     * @param fotogramma 0 o 1
     * @return la scia della spada
     */
    public Sprite fendente(Direzione direzione, int fotogramma) {
        return fendente.get(direzione)[Math.floorMod(fotogramma, 2)];
    }

    /** @return un'ombra ellittica per i personaggi piccoli (12x4) */
    public Sprite ombraPiccola() {
        return ombraPiccola;
    }

    /** @return un'ombra ellittica per il drago (30x9) */
    public Sprite ombraGrande() {
        return ombraGrande;
    }

    /** @return l'icona della spada */
    public Sprite spada() {
        return spada;
    }

    /**
     * @param tipo tipo di oggetto
     * @return il suo sprite (un punto interrogativo per i tipi che non hanno ancora un disegno)
     */
    public Sprite oggetto(TipoOggetto tipo) {
        return oggetti.getOrDefault(tipo, oggettoSconosciuto);
    }

    // ---------------------------------------------------------------- eroe e pupo

    private static void preparaPersonaggio(EnumMap<Direzione, Sprite[]> destinazione, String[] fronte,
                                           String[] retro, String[] lato, String[][] gambe, String[][] gambeLato,
                                           Map<Character, Integer> colori) {
        Sprite[] sinistra = fotogrammi(lato, gambeLato, colori);
        Sprite[] destra = new Sprite[sinistra.length];
        for (int i = 0; i < sinistra.length; i++) {
            destra[i] = sinistra[i].specchiata();
        }
        destinazione.put(Direzione.GIU, fotogrammi(fronte, gambe, colori));
        destinazione.put(Direzione.SU, fotogrammi(retro, gambe, colori));
        destinazione.put(Direzione.SINISTRA, sinistra);
        destinazione.put(Direzione.DESTRA, destra);
    }

    private static Sprite[] fotogrammi(String[] busto, String[][] gambe, Map<Character, Integer> colori) {
        Sprite[] risultato = new Sprite[gambe.length];
        for (int i = 0; i < gambe.length; i++) {
            TelaPixel tela = new TelaPixel(16, 16);
            tela.disegna(busto, colori, 0, 0);
            tela.disegna(gambe[i], colori, 0, busto.length);
            risultato[i] = tela.crea();
        }
        return risultato;
    }

    // ---------------------------------------------------------------- drago

    private static Sprite creaDrago(PosaDrago posa) {
        TelaPixel tela = new TelaPixel(48, 48);
        double cx = 23.5;
        boolean riposo = posa == PosaDrago.RIPOSO;
        double apertura = switch (posa) {
            case ALI_ALZATE, FAUCI_APERTE -> 1.0;
            case ALI_ABBASSATE -> 0.7;
            case RIPOSO -> 0.3;
        };
        disegnaAli(tela, cx, apertura);
        disegnaCoda(tela, cx, riposo);
        for (int lato = -1; lato <= 1; lato += 2) {
            tela.ellisse(cx + lato * 7, 21, 2.6, 2.2, DRAGO_SCURO);
            tela.ellisse(cx + lato * 7, 30.5, 2.6, 2.4, DRAGO_SCURO);
            tela.punto((int) Math.round(cx + lato * 9.5), 20, CORNO);
            tela.punto((int) Math.round(cx + lato * 9.5), 31, CORNO);
        }
        tela.ellisse(cx, 25.5, 6.5, 9, DRAGO);
        tela.ellisse(cx - 1.8, 23, 3, 6, DRAGO_CHIARO);
        for (int y = 18; y <= 32; y += 3) {
            tela.rettangolo(23, y, 2, 2, DRAGO_SCURO);
        }
        tela.ellisse(cx, 15, 3.6, 4.5, DRAGO);
        tela.ellisse(cx, 9.5, 4.6, 4.6, DRAGO);
        tela.ellisse(cx, 5, 3, 3.6, DRAGO);
        tela.ellisse(cx - 1.5, 8, 1.6, 2.2, DRAGO_CHIARO);
        tela.tratto(19.5, 10.5, 17, 15, 1, 0.6, CORNO);
        tela.tratto(27.5, 10.5, 30, 15, 1, 0.6, CORNO);
        if (riposo) {
            tela.rettangolo(20, 8, 2, 1, CONTORNO);
            tela.rettangolo(26, 8, 2, 1, CONTORNO);
        } else {
            tela.rettangolo(20, 7, 2, 2, OCCHIO_DRAGO);
            tela.rettangolo(26, 7, 2, 2, OCCHIO_DRAGO);
            tela.punto(21, 8, CONTORNO);
            tela.punto(26, 8, CONTORNO);
        }
        tela.punto(22, 3, DRAGO_SCURO);
        tela.punto(25, 3, DRAGO_SCURO);
        if (posa == PosaDrago.FAUCI_APERTE) {
            tela.poligono(new double[] {20, 27, 23.5}, new double[] {0, 0, 6}, BOCCA);
            tela.punto(21, 1, BIANCO);
            tela.punto(26, 1, BIANCO);
            tela.punto(22, 3, BIANCO);
            tela.punto(25, 3, BIANCO);
        }
        tela.contorno(CONTORNO);
        return tela.crea();
    }

    private static void disegnaAli(TelaPixel tela, double cx, double apertura) {
        double chiusura = 1 - apertura;
        for (int lato = -1; lato <= 1; lato += 2) {
            double radiceX = cx + lato * 5;
            double gomitoX = cx + lato * (5 + 10 * apertura);
            double gomitoY = 11 + 4 * chiusura;
            double puntaX = cx + lato * (5 + 19 * apertura);
            double puntaY = 14 + 7 * chiusura;
            double[] bordoX = {cx + lato * (5 + 15 * apertura), cx + lato * (5 + 10 * apertura),
                cx + lato * (5 + 5 * apertura)};
            double[] bordoY = {22 + 2 * chiusura, 25, 27};
            double[] xs = {radiceX, gomitoX, puntaX, bordoX[0], bordoX[1], bordoX[2], radiceX};
            double[] ys = {18, gomitoY, puntaY, bordoY[0], bordoY[1], bordoY[2], 29};
            tela.poligono(xs, ys, ALA_MEMBRANA);
            for (int i = 0; i < 2; i++) {
                tela.ellisse((bordoX[i] + bordoX[i + 1]) / 2, (bordoY[i] + bordoY[i + 1]) / 2 + 0.9, 2.2, 1.4,
                        TRASPARENTE);
            }
            tela.tratto(radiceX, 18.5, gomitoX, gomitoY, 1.3, 0.9, ALA);
            tela.tratto(gomitoX, gomitoY, puntaX, puntaY, 0.9, 0.5, ALA);
            for (int i = 0; i < bordoX.length; i++) {
                tela.tratto(gomitoX, gomitoY, bordoX[i], bordoY[i], 0.6, 0.45, ALA);
            }
        }
    }

    private static void disegnaCoda(TelaPixel tela, double cx, boolean arrotolata) {
        double[][] punti = arrotolata
                ? new double[][] {{cx, 31}, {cx + 3, 36}, {cx + 8, 38.5}, {cx + 12.5, 35.5}, {cx + 13, 31}}
                : new double[][] {{cx, 31}, {cx + 1.5, 35.5}, {cx + 0.5, 40}, {cx - 1.5, 43}};
        double[] raggi = arrotolata ? new double[] {3.4, 2.8, 2.1, 1.5, 1.0} : new double[] {3.4, 2.6, 1.8, 1.1};
        for (int i = 0; i < punti.length - 1; i++) {
            tela.tratto(punti[i][0], punti[i][1], punti[i + 1][0], punti[i + 1][1], raggi[i], raggi[i + 1], DRAGO);
        }
        double[] fine = punti[punti.length - 1];
        double[] prima = punti[punti.length - 2];
        double lunghezza = Math.hypot(fine[0] - prima[0], fine[1] - prima[1]);
        double dx = (fine[0] - prima[0]) / lunghezza;
        double dy = (fine[1] - prima[1]) / lunghezza;
        tela.poligono(
                new double[] {fine[0] + dx * 3.5, fine[0] - dy * 2.4, fine[0] - dx, fine[0] + dy * 2.4},
                new double[] {fine[1] + dy * 3.5, fine[1] + dx * 2.4, fine[1] - dy, fine[1] - dx * 2.4},
                DRAGO_SCURO);
        for (int i = 0; i < punti.length - 1; i++) {
            tela.punto((int) Math.round(punti[i][0]), (int) Math.round(punti[i][1]), DRAGO_SCURO);
        }
    }

    // ---------------------------------------------------------------- effetti e oggetti

    private static Sprite[] creaFendente() {
        Sprite[] fotogrammi = new Sprite[2];
        for (int fotogramma = 0; fotogramma < 2; fotogramma++) {
            TelaPixel tela = new TelaPixel(16, 16);
            double inizio = fotogramma == 0 ? -75 : -65;
            double fine = fotogramma == 0 ? 5 : 75;
            for (double gradi = inizio; gradi <= fine; gradi += 4) {
                double angolo = Math.toRadians(gradi);
                int x = (int) Math.round(-2 + Math.cos(angolo) * 12);
                int y = (int) Math.round(8 + Math.sin(angolo) * 11);
                tela.punto(x, y, BIANCO);
                tela.punto(x - 1, y, ACCIAIO);
                if (fotogramma == 1) {
                    tela.punto(x - 2, y, conAlfa(AZZURRO, 150));
                }
            }
            fotogrammi[fotogramma] = tela.crea();
        }
        return fotogrammi;
    }

    private static Sprite creaOmbra(int larghezza, int altezza) {
        TelaPixel tela = new TelaPixel(larghezza, altezza);
        tela.ellisse(larghezza / 2.0, altezza / 2.0, larghezza / 2.0, altezza / 2.0, OMBRA);
        return tela.crea();
    }

    private static Sprite creaSpada() {
        TelaPixel tela = new TelaPixel(16, 16);
        tela.tratto(5.5, 10.5, 13, 3, 1.2, 0.7, ACCIAIO);
        tela.linea(6, 9, 12, 3, ACCIAIO_CHIARO);
        tela.tratto(3, 8.5, 7.5, 13, 0.9, 0.9, ORO);
        tela.tratto(2.5, 13.5, 4.5, 11.5, 1, 1, TRONCO);
        tela.punto(1, 14, ORO);
        tela.contorno(CONTORNO);
        return tela.crea();
    }

    private static Sprite creaPozione() {
        TelaPixel tela = new TelaPixel(16, 16);
        tela.ellisse(8, 10.5, 4.6, 4.3, VETRO);
        tela.ellisse(8, 11.2, 3.8, 3.3, POZIONE);
        tela.ellisse(8.6, 12.4, 2.6, 1.8, POZIONE_SCURA);
        tela.rettangolo(7, 4, 3, 3, VETRO);
        tela.rettangolo(6, 2, 5, 2, TRONCO);
        tela.punto(6, 9, BIANCO);
        tela.punto(6, 10, BIANCO);
        tela.contorno(CONTORNO);
        return tela.crea();
    }

    private static Sprite creaBacca() {
        TelaPixel tela = new TelaPixel(16, 16);
        tela.linea(8, 6, 10, 3, TRONCO);
        tela.ellisse(11.5, 3.5, 2.8, 1.4, FOGLIA);
        double[][] chicchi = {{5, 11}, {10.5, 11}, {7.8, 7.2}};
        for (double[] chicco : chicchi) {
            tela.ellisse(chicco[0], chicco[1], 2.7, 2.7, BACCA);
            tela.ellisse(chicco[0] + 0.7, chicco[1] + 0.8, 1.6, 1.4, rgb(78, 40, 140));
            tela.punto((int) chicco[0] - 1, (int) chicco[1] - 1, BACCA_CHIARA);
        }
        tela.punto(4, 10, BIANCO);
        tela.contorno(CONTORNO);
        return tela.crea();
    }

    private static Sprite creaOggettoSconosciuto() {
        TelaPixel tela = new TelaPixel(16, 16);
        tela.ellisse(8, 8, 5, 5, PANNELLO);
        tela.rettangolo(7, 5, 2, 4, TESTO);
        tela.rettangolo(7, 10, 2, 2, TESTO);
        tela.contorno(CONTORNO);
        return tela.crea();
    }
}
