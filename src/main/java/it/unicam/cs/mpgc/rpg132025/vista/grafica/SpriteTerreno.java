package it.unicam.cs.mpgc.rpg132025.vista.grafica;

import it.unicam.cs.mpgc.rpg132025.vista.Schermo;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static it.unicam.cs.mpgc.rpg132025.vista.grafica.Tavolozza.*;

/**
 * Gli sprite del paesaggio, disegnati via codice in stile pixel art.
 * <p>
 * Le caselle che cambiano aspetto in base alle vicine (riva dell'acqua,
 * sentiero, bordo dell'altopiano) ricevono una maschera di bit con i lati
 * interessati e vengono generate la prima volta che servono.
 */
public final class SpriteTerreno {

    /** Bit della maschera per il lato superiore. */
    public static final int NORD = 1;
    /** Bit della maschera per il lato destro. */
    public static final int EST = 2;
    /** Bit della maschera per il lato inferiore. */
    public static final int SUD = 4;
    /** Bit della maschera per il lato sinistro. */
    public static final int OVEST = 8;

    private static final int LATO = Schermo.CASELLA;

    private final Sprite[] erba = new Sprite[4];
    private final Sprite[] fiori = new Sprite[2];
    private final Sprite[] sabbia = new Sprite[2];
    private final Sprite[] falo = new Sprite[3];
    private final Sprite[] fuoco = new Sprite[3];
    private final Map<Integer, Sprite> erbaScura = new HashMap<>();
    private final Map<Integer, Sprite> acqua = new HashMap<>();
    private final Map<Integer, Sprite> sentiero = new HashMap<>();
    private final Map<Integer, Sprite> scogliera = new HashMap<>();
    private final Map<Integer, Sprite> altopiano = new HashMap<>();
    private final Sprite grotta;
    private final Sprite ombraScogliera;
    private final Sprite albero;
    private final Sprite palma;
    private final Sprite roccia;
    private final Sprite felce;
    private final Sprite bagliore;

    /** Disegna gli sprite fissi; quelli con maschera verranno creati quando servono. */
    public SpriteTerreno() {
        for (int i = 0; i < erba.length; i++) {
            erba[i] = creaErba(11 + i, ERBA, ERBA_SCURA, ERBA_CHIARA, 3);
        }
        for (int i = 0; i < fiori.length; i++) {
            fiori[i] = creaFiori(51 + i);
        }
        for (int i = 0; i < sabbia.length; i++) {
            sabbia[i] = creaSabbia(61 + i);
        }
        for (int i = 0; i < falo.length; i++) {
            falo[i] = creaFalo(i);
            fuoco[i] = creaFuoco(i);
        }
        grotta = creaGrotta();
        ombraScogliera = creaOmbraScogliera();
        albero = creaAlbero();
        palma = creaPalma();
        roccia = creaRoccia();
        felce = creaFelce();
        bagliore = creaBagliore();
    }

    /** @return un prato, la variante sceglie il disegno */
    public Sprite erba(int variante) {
        return erba[Math.floorMod(variante, erba.length)];
    }

    /**
     * @param variante variante del disegno
     * @param confini  maschera dei lati che non confinano con altra erba scura (lì il bordo è irregolare)
     * @return una macchia di prato più scuro
     */
    public Sprite erbaScura(int variante, int confini) {
        int chiave = Math.floorMod(variante, 2) * 16 + confini;
        return erbaScura.computeIfAbsent(chiave, k -> creaErbaScura(k / 16, k % 16));
    }

    /** @return un prato fiorito */
    public Sprite fiori(int variante) {
        return fiori[Math.floorMod(variante, fiori.length)];
    }

    /** @return la sabbia */
    public Sprite sabbia(int variante) {
        return sabbia[Math.floorMod(variante, sabbia.length)];
    }

    /**
     * @param fotogramma fotogramma dell'animazione delle onde (0 o 1)
     * @param terraVicina maschera dei lati che toccano terra (schiuma)
     * @return la casella d'acqua
     */
    public Sprite acqua(int fotogramma, int terraVicina) {
        int chiave = Math.floorMod(fotogramma, 2) * 16 + terraVicina;
        return acqua.computeIfAbsent(chiave, k -> creaAcqua(k / 16, k % 16));
    }

    /**
     * @param collegamenti maschera dei lati verso cui il sentiero continua
     * @return la casella di sentiero
     */
    public Sprite sentiero(int collegamenti) {
        return sentiero.computeIfAbsent(collegamenti, SpriteTerreno::creaSentiero);
    }

    /**
     * @param variante   variante del disegno
     * @param erbaSopra  {@code true} se sopra c'è l'altopiano (bordo erboso)
     * @param baseSotto  {@code true} se sotto c'è il terreno (base più scura)
     * @return la parete della scogliera
     */
    public Sprite scogliera(int variante, boolean erbaSopra, boolean baseSotto) {
        int chiave = Math.floorMod(variante, 3) * 4 + (erbaSopra ? 2 : 0) + (baseSotto ? 1 : 0);
        return scogliera.computeIfAbsent(chiave, k -> creaScogliera(k / 4, (k & 2) != 0, (k & 1) != 0));
    }

    /**
     * @param variante    variante del disegno
     * @param bordoMare   maschera dei lati che danno sul mare
     * @return la sommità dell'altopiano
     */
    public Sprite altopiano(int variante, int bordoMare) {
        int chiave = Math.floorMod(variante, 2) * 16 + bordoMare;
        return altopiano.computeIfAbsent(chiave, k -> creaAltopiano(k / 16, k % 16));
    }

    /** @return l'ingresso della grotta, grande 2x2 caselle */
    public Sprite grotta() {
        return grotta;
    }

    /** @return l'ombra proiettata dalla scogliera sul terreno sottostante */
    public Sprite ombraScogliera() {
        return ombraScogliera;
    }

    /** @return un albero (sporge sopra la propria casella) */
    public Sprite albero() {
        return albero;
    }

    /** @return una palma (sporge sopra la propria casella) */
    public Sprite palma() {
        return palma;
    }

    /** @return un masso */
    public Sprite roccia() {
        return roccia;
    }

    /** @return una felce */
    public Sprite felce() {
        return felce;
    }

    /** @return il falò, fotogramma dell'animazione delle fiamme */
    public Sprite falo(int fotogramma) {
        return falo[Math.floorMod(fotogramma, falo.length)];
    }

    /** @return le fiamme della fiammata del drago su una casella */
    public Sprite fuoco(int fotogramma) {
        return fuoco[Math.floorMod(fotogramma, fuoco.length)];
    }

    /** @return l'alone di luce del falò, visibile di notte */
    public Sprite bagliore() {
        return bagliore;
    }

    // ---------------------------------------------------------------- caselle

    private static Sprite creaErba(long seme, int base, int scuro, int chiaro, int ciuffi) {
        TelaPixel tela = new TelaPixel(LATO, LATO);
        Random casuale = new Random(seme);
        tela.rettangolo(0, 0, LATO, LATO, base);
        for (int i = 0; i < 16; i++) {
            tela.punto(casuale.nextInt(LATO), casuale.nextInt(LATO), casuale.nextInt(3) == 0 ? chiaro : scuro);
        }
        for (int i = 0; i < ciuffi; i++) {
            int x = 2 + casuale.nextInt(LATO - 4);
            int y = 3 + casuale.nextInt(LATO - 5);
            tela.punto(x, y, scuro);
            tela.punto(x - 1, y - 1, scuro);
            tela.punto(x + 1, y - 1, scuro);
            tela.punto(x - 1, y - 2, chiaro);
            tela.punto(x + 1, y - 2, chiaro);
        }
        return tela.crea();
    }

    private static Sprite creaErbaScura(int variante, int confini) {
        Sprite chiara = creaErba(11L + variante, ERBA, ERBA_SCURA, ERBA_CHIARA, 3);
        Sprite scura = creaErba(31L + variante, ERBA_SCURA, ERBA_OMBRA, ERBA, 4);
        Random casuale = new Random(131L + variante * 16L + confini);
        int[] nord = margini(casuale, (confini & NORD) != 0);
        int[] sud = margini(casuale, (confini & SUD) != 0);
        int[] ovest = margini(casuale, (confini & OVEST) != 0);
        int[] est = margini(casuale, (confini & EST) != 0);
        TelaPixel tela = new TelaPixel(LATO, LATO);
        for (int y = 0; y < LATO; y++) {
            for (int x = 0; x < LATO; x++) {
                boolean dentro = x >= ovest[y] && x < LATO - est[y] && y >= nord[x] && y < LATO - sud[x];
                tela.punto(x, y, dentro ? scura.pixel(x, y) : chiara.pixel(x, y));
            }
        }
        return tela.crea();
    }

    /** Margini irregolari (2-4 pixel) su un lato di confine, nulli altrimenti. */
    private static int[] margini(Random casuale, boolean confine) {
        int[] margini = new int[LATO];
        if (confine) {
            int valore = 3;
            for (int i = 0; i < LATO; i++) {
                valore = Math.clamp(valore + casuale.nextInt(3) - 1, 2, 4);
                margini[i] = valore;
            }
        }
        return margini;
    }

    private static Sprite creaFiori(long seme) {
        TelaPixel tela = new TelaPixel(LATO, LATO);
        Random casuale = new Random(seme);
        tela.rettangolo(0, 0, LATO, LATO, ERBA);
        for (int i = 0; i < 12; i++) {
            tela.punto(casuale.nextInt(LATO), casuale.nextInt(LATO), ERBA_SCURA);
        }
        int[] petali = {FIORE_BIANCO, FIORE_ROSA, FIORE_BIANCO};
        for (int i = 0; i < 3; i++) {
            int x = 2 + casuale.nextInt(LATO - 4);
            int y = 2 + casuale.nextInt(LATO - 4);
            int petalo = petali[i];
            tela.punto(x - 1, y, petalo);
            tela.punto(x + 1, y, petalo);
            tela.punto(x, y - 1, petalo);
            tela.punto(x, y + 1, petalo);
            tela.punto(x, y, FIORE_GIALLO);
            tela.punto(x, y + 2, ERBA_OMBRA);
        }
        return tela.crea();
    }

    private static Sprite creaSabbia(long seme) {
        TelaPixel tela = new TelaPixel(LATO, LATO);
        Random casuale = new Random(seme);
        tela.rettangolo(0, 0, LATO, LATO, SABBIA);
        for (int i = 0; i < 22; i++) {
            tela.punto(casuale.nextInt(LATO), casuale.nextInt(LATO), SABBIA_SCURA);
        }
        for (int i = 0; i < 5; i++) {
            tela.punto(casuale.nextInt(LATO), casuale.nextInt(LATO), FIORE_BIANCO);
        }
        return tela.crea();
    }

    private static Sprite creaAcqua(int fotogramma, int terraVicina) {
        TelaPixel tela = new TelaPixel(LATO, LATO);
        Random casuale = new Random(77);
        tela.rettangolo(0, 0, LATO, LATO, ACQUA);
        for (int i = 0; i < 14; i++) {
            tela.punto(casuale.nextInt(LATO), casuale.nextInt(LATO), ACQUA_PROFONDA);
        }
        int[][] onde = {{1, 3}, {9, 7}, {4, 12}};
        for (int[] onda : onde) {
            int inizio = onda[0] + fotogramma * 2;
            for (int i = 0; i < 4; i++) {
                tela.punto(Math.floorMod(inizio + i, LATO), onda[1], ACQUA_CHIARA);
            }
            tela.punto(Math.floorMod(inizio + 1, LATO), onda[1] - 1, ACQUA_CHIARA);
            tela.punto(Math.floorMod(inizio + 2, LATO), onda[1] - 1, ACQUA_CHIARA);
        }
        Random schiuma = new Random(13L + fotogramma);
        for (int i = 0; i < LATO; i++) {
            if ((terraVicina & NORD) != 0) {
                tela.punto(i, 0, SCHIUMA);
                tela.punto(i, 1, schiuma.nextInt(3) == 0 ? SCHIUMA : ACQUA_CHIARA);
            }
            if ((terraVicina & SUD) != 0) {
                tela.punto(i, LATO - 1, SCHIUMA);
                tela.punto(i, LATO - 2, schiuma.nextInt(3) == 0 ? SCHIUMA : ACQUA_CHIARA);
            }
            if ((terraVicina & OVEST) != 0) {
                tela.punto(0, i, SCHIUMA);
                tela.punto(1, i, schiuma.nextInt(3) == 0 ? SCHIUMA : ACQUA_CHIARA);
            }
            if ((terraVicina & EST) != 0) {
                tela.punto(LATO - 1, i, SCHIUMA);
                tela.punto(LATO - 2, i, schiuma.nextInt(3) == 0 ? SCHIUMA : ACQUA_CHIARA);
            }
        }
        return tela.crea();
    }

    private static Sprite creaSentiero(int collegamenti) {
        TelaPixel tela = new TelaPixel(LATO, LATO);
        Random casuale = new Random(91L + collegamenti);
        tela.rettangolo(0, 0, LATO, LATO, ERBA);
        for (int i = 0; i < 10; i++) {
            tela.punto(casuale.nextInt(LATO), casuale.nextInt(LATO), ERBA_SCURA);
        }
        int margineNord = (collegamenti & NORD) != 0 ? 0 : 3;
        int margineSud = (collegamenti & SUD) != 0 ? 0 : 3;
        int margineOvest = (collegamenti & OVEST) != 0 ? 0 : 3;
        int margineEst = (collegamenti & EST) != 0 ? 0 : 3;
        for (int y = 0; y < LATO; y++) {
            int sinistra = margineOvest == 0 ? 0 : margineOvest + casuale.nextInt(2) - 1;
            int destra = LATO - (margineEst == 0 ? 0 : margineEst + casuale.nextInt(2) - 1);
            for (int x = Math.max(0, sinistra); x < Math.min(LATO, destra); x++) {
                int alto = margineNord == 0 ? 0 : margineNord + (x % 5 == 0 ? 1 : 0);
                int basso = LATO - (margineSud == 0 ? 0 : margineSud + (x % 4 == 1 ? 1 : 0));
                if (y >= alto && y < basso) {
                    tela.punto(x, y, TERRA);
                }
            }
        }
        for (int i = 0; i < 18; i++) {
            int x = casuale.nextInt(LATO);
            int y = casuale.nextInt(LATO);
            if (tela.colore(x, y) == TERRA) {
                tela.punto(x, y, casuale.nextInt(4) == 0 ? PIETRA_CHIARA : TERRA_SCURA);
            }
        }
        return tela.crea();
    }

    private static Sprite creaScogliera(int variante, boolean erbaSopra, boolean baseSotto) {
        TelaPixel tela = new TelaPixel(LATO, LATO);
        Random casuale = new Random(40L + variante);
        tela.rettangolo(0, 0, LATO, LATO, SCOGLIERA);
        for (int x = casuale.nextInt(2); x < LATO; x += 3 + casuale.nextInt(2)) {
            int inizio = casuale.nextInt(5);
            int lunghezza = 6 + casuale.nextInt(9);
            for (int y = inizio; y < Math.min(LATO, inizio + lunghezza); y++) {
                tela.punto(x, y, SCOGLIERA_SCURA);
            }
            tela.punto(x + 1, inizio, SCOGLIERA_CHIARA);
            tela.punto(x + 1, inizio + 1, SCOGLIERA_CHIARA);
        }
        int fascia = 7 + casuale.nextInt(3);
        for (int x = 0; x < LATO; x++) {
            tela.punto(x, fascia, SCOGLIERA_SCURA);
            if (casuale.nextBoolean()) {
                tela.punto(x, fascia - 1, SCOGLIERA_CHIARA);
            }
        }
        if (erbaSopra) {
            tela.rettangolo(0, 0, LATO, 3, ALTOPIANO);
            tela.rettangolo(0, 0, LATO, 1, ERBA_CHIARA);
            for (int x = 0; x < LATO; x++) {
                boolean goccia = casuale.nextInt(3) == 0;
                tela.punto(x, 2, ERBA_SCURA);
                if (goccia) {
                    tela.punto(x, 3, ERBA_SCURA);
                }
                tela.punto(x, goccia ? 4 : 3, SCOGLIERA_SCURA);
            }
        }
        if (baseSotto) {
            tela.rettangolo(0, LATO - 2, LATO, 2, SCOGLIERA_SCURA);
        }
        return tela.crea();
    }

    private static Sprite creaAltopiano(int variante, int bordoMare) {
        TelaPixel tela = new TelaPixel(LATO, LATO);
        Random casuale = new Random(70L + variante);
        tela.rettangolo(0, 0, LATO, LATO, ALTOPIANO);
        for (int i = 0; i < 16; i++) {
            tela.punto(casuale.nextInt(LATO), casuale.nextInt(LATO), casuale.nextBoolean() ? ERBA : ERBA_CHIARA);
        }
        if ((bordoMare & NORD) != 0) {
            tela.rettangolo(0, 0, LATO, 3, SCOGLIERA_SCURA);
            tela.rettangolo(0, 2, LATO, 1, SCOGLIERA);
        }
        if ((bordoMare & SUD) != 0) {
            tela.rettangolo(0, LATO - 3, LATO, 3, SCOGLIERA);
            tela.rettangolo(0, LATO - 1, LATO, 1, SCOGLIERA_SCURA);
        }
        if ((bordoMare & OVEST) != 0) {
            tela.rettangolo(0, 0, 3, LATO, SCOGLIERA_SCURA);
            tela.rettangolo(2, 0, 1, LATO, SCOGLIERA);
        }
        if ((bordoMare & EST) != 0) {
            tela.rettangolo(LATO - 3, 0, 3, LATO, SCOGLIERA_SCURA);
            tela.rettangolo(LATO - 3, 0, 1, LATO, SCOGLIERA);
        }
        return tela.crea();
    }

    private static Sprite creaOmbraScogliera() {
        TelaPixel tela = new TelaPixel(LATO, 6);
        tela.rettangolo(0, 0, LATO, 2, conAlfa(NERO, 100));
        tela.rettangolo(0, 2, LATO, 2, conAlfa(NERO, 60));
        tela.rettangolo(0, 4, LATO, 2, conAlfa(NERO, 25));
        return tela.crea();
    }

    private static Sprite creaGrotta() {
        TelaPixel tela = new TelaPixel(2 * LATO, 2 * LATO);
        tela.ellisse(16, 33, 14.5, 26, SCOGLIERA_SCURA);
        tela.ellisse(16, 34, 12.5, 23, BUIO);
        tela.ellisse(16, 36, 8.5, 18, BUIO_PROFONDO);
        int[] colonne = {7, 10, 14, 18, 21, 24};
        int[] lunghezze = {2, 4, 3, 4, 3, 2};
        for (int i = 0; i < colonne.length; i++) {
            double relativa = (colonne[i] + 1 - 16) / 12.5;
            int cima = (int) Math.ceil(34 - 23 * Math.sqrt(Math.max(0, 1 - relativa * relativa)));
            tela.rettangolo(colonne[i], cima, 2, lunghezze[i], SCOGLIERA_SCURA);
            tela.punto(colonne[i], cima + lunghezze[i], SCOGLIERA_SCURA);
        }
        tela.rettangolo(6, 29, 20, 3, rgb(62, 42, 34));
        tela.ellisse(4, 29, 4.5, 3.5, SCOGLIERA);
        tela.ellisse(28, 29, 4.5, 3.5, SCOGLIERA);
        tela.ellisse(3, 28, 2, 1.5, SCOGLIERA_CHIARA);
        tela.ellisse(27, 28, 2, 1.5, SCOGLIERA_CHIARA);
        return tela.crea();
    }

    // ---------------------------------------------------------------- oggetti alti

    private static Sprite creaAlbero() {
        TelaPixel tela = new TelaPixel(32, 40);
        Random casuale = new Random(5);
        tela.rettangolo(13, 24, 6, 13, TRONCO);
        tela.rettangolo(17, 24, 2, 13, TRONCO_SCURO);
        tela.rettangolo(11, 35, 2, 2, TRONCO);
        tela.rettangolo(19, 35, 2, 2, TRONCO_SCURO);
        double[][] chioma = {{16, 13, 11}, {8.5, 17, 7}, {23.5, 17, 7}, {11, 8.5, 7}, {21, 8.5, 7}, {16, 20, 8}};
        for (double[] cerchio : chioma) {
            tela.ellisse(cerchio[0], cerchio[1], cerchio[2], cerchio[2], FOGLIA_SCURA);
        }
        for (double[] cerchio : chioma) {
            tela.ellisse(cerchio[0] - 1, cerchio[1] - 1.5, cerchio[2] - 1.6, cerchio[2] - 1.6, FOGLIA);
        }
        tela.ellisse(11, 7.5, 4, 2.6, FOGLIA_CHIARA);
        tela.ellisse(19.5, 6.5, 3, 2, FOGLIA_CHIARA);
        tela.ellisse(7, 14.5, 2.4, 1.8, FOGLIA_CHIARA);
        for (int i = 0; i < 70; i++) {
            int x = casuale.nextInt(32);
            int y = casuale.nextInt(30);
            if (tela.colore(x, y) == FOGLIA) {
                tela.punto(x, y, casuale.nextInt(3) == 0 ? FOGLIA_CHIARA : FOGLIA_SCURA);
            }
        }
        tela.contorno(CONTORNO);
        tela.ellisseSotto(16, 37, 11, 2.8, OMBRA);
        return tela.crea();
    }

    private static Sprite creaPalma() {
        TelaPixel tela = new TelaPixel(32, 44);
        for (int i = 0; i <= 30; i++) {
            double t = i / 30.0;
            double x = 14 + 4.5 * t * t + Math.sin(t * 3.0) * 1.2;
            double y = 41 - 29 * t;
            tela.ellisse(x, y, 2.3, 1.4, (i / 2) % 2 == 0 ? PALMA_TRONCO : PALMA_TRONCO_SCURO);
        }
        double cx = 18.5;
        double cy = 11;
        double[] angoli = {180, 215, 260, 300, 335, 0, 150, 30};
        for (double gradi : angoli) {
            double angolo = Math.toRadians(gradi);
            double lunghezza = 12;
            double precedenteX = cx;
            double precedenteY = cy;
            for (int passo = 1; passo <= 12; passo++) {
                double s = passo / 12.0;
                double x = cx + Math.cos(angolo) * lunghezza * s;
                double y = cy + Math.sin(angolo) * lunghezza * 0.6 * s + 6 * s * s;
                tela.tratto(precedenteX, precedenteY, x, y, 2.2 - 1.6 * s, 2.2 - 1.6 * s, PALMA_FOGLIA);
                precedenteX = x;
                precedenteY = y;
            }
            for (int passo = 1; passo <= 10; passo++) {
                double s = passo / 12.0;
                double x = cx + Math.cos(angolo) * lunghezza * s;
                double y = cy + Math.sin(angolo) * lunghezza * 0.6 * s + 6 * s * s;
                tela.punto((int) Math.round(x), (int) Math.round(y), PALMA_FOGLIA_SCURA);
            }
        }
        tela.ellisse(16.8, 12.8, 1.8, 1.8, TRONCO);
        tela.ellisse(20, 13.2, 1.8, 1.8, TRONCO_SCURO);
        tela.contorno(CONTORNO);
        tela.ellisseSotto(15, 42, 7, 1.8, OMBRA);
        return tela.crea();
    }

    private static Sprite creaRoccia() {
        TelaPixel tela = new TelaPixel(LATO, LATO);
        tela.ellisse(8, 9.5, 7, 5.5, PIETRA_SCURA);
        tela.ellisse(7.4, 8.8, 6, 4.6, PIETRA);
        tela.ellisse(5.8, 7.2, 2.8, 1.9, PIETRA_CHIARA);
        tela.linea(9, 7, 11, 10, PIETRA_SCURA);
        tela.punto(4, 11, PIETRA_SCURA);
        tela.contorno(CONTORNO);
        tela.ellisseSotto(8, 14.5, 7, 1.4, OMBRA);
        return tela.crea();
    }

    private static Sprite creaFelce() {
        TelaPixel tela = new TelaPixel(LATO, LATO);
        double[] angoli = {-165, -140, -115, -90, -65, -40, -15};
        for (double gradi : angoli) {
            double angolo = Math.toRadians(gradi);
            double lunghezza = Math.abs(gradi + 90) > 60 ? 7 : 8.5;
            for (int passo = 0; passo <= 8; passo++) {
                double s = lunghezza * passo / 8.0;
                int x = (int) Math.round(7.5 + Math.cos(angolo) * s);
                int y = (int) Math.round(13.5 + Math.sin(angolo) * s * 0.95);
                tela.punto(x, y, FOGLIA_SCURA);
                if (passo % 2 == 1 && passo < 8) {
                    tela.punto(x + (int) Math.round(-Math.sin(angolo)), y + (int) Math.round(Math.cos(angolo)), FOGLIA);
                    tela.punto(x - (int) Math.round(-Math.sin(angolo)), y - (int) Math.round(Math.cos(angolo)), FOGLIA);
                }
            }
        }
        tela.rettangolo(6, 13, 4, 2, FOGLIA_SCURA);
        tela.contorno(CONTORNO);
        tela.ellisseSotto(8, 15, 6, 1.2, OMBRA);
        return tela.crea();
    }

    private static Sprite creaFalo(int fotogramma) {
        TelaPixel tela = new TelaPixel(LATO, 20);
        double[][] pietre = {{3, 16.5}, {6.5, 18}, {10.5, 18}, {13.5, 16.5}, {8.5, 14.5}};
        for (double[] pietra : pietre) {
            tela.ellisse(pietra[0], pietra[1], 2.2, 1.6, PIETRA);
            tela.punto((int) pietra[0] - 1, (int) pietra[1] - 1, PIETRA_CHIARA);
        }
        tela.tratto(3, 16, 13, 12.5, 1.2, 1.2, TRONCO);
        tela.tratto(3, 12.5, 13, 16, 1.2, 1.2, TRONCO_SCURO);
        tela.contorno(CONTORNO);
        double[] altezze = {8, 9.5, 7};
        double[] oscillazioni = {0, 0.8, -0.8};
        double altezza = altezze[fotogramma];
        double scarto = oscillazioni[fotogramma];
        tela.ellisse(8 + scarto * 0.5, 13 - altezza / 2, 3.6, altezza / 2, FUOCO_ROSSO);
        tela.ellisse(8 + scarto, 13.5 - altezza / 2 + 1, 2.6, altezza / 2 - 1, FUOCO_ARANCIO);
        tela.ellisse(8 + scarto * 0.5, 12, 1.5, 2.6, FUOCO_GIALLO);
        tela.punto(5 + fotogramma * 3, (int) (12 - altezza), FUOCO_ARANCIO);
        tela.punto(11 - fotogramma * 2, (int) (11 - altezza / 1.5), FUOCO_GIALLO);
        return tela.crea();
    }

    private static Sprite creaFuoco(int fotogramma) {
        TelaPixel tela = new TelaPixel(LATO, LATO);
        Random casuale = new Random(200L + fotogramma);
        for (int i = 0; i < 4; i++) {
            double x = 3 + casuale.nextInt(10);
            double altezza = 6 + casuale.nextInt(6);
            tela.ellisse(x, 15 - altezza / 2, 3, altezza / 2, conAlfa(FUOCO_ROSSO, 220));
        }
        for (int i = 0; i < 4; i++) {
            double x = 3 + casuale.nextInt(10);
            double altezza = 4 + casuale.nextInt(5);
            tela.ellisse(x, 15 - altezza / 2, 2, altezza / 2, conAlfa(FUOCO_ARANCIO, 235));
        }
        for (int i = 0; i < 3; i++) {
            double x = 4 + casuale.nextInt(8);
            tela.ellisse(x, 13, 1.3, 2, FUOCO_GIALLO);
        }
        return tela.crea();
    }

    private static Sprite creaBagliore() {
        int diametro = 56;
        TelaPixel tela = new TelaPixel(diametro, diametro);
        double raggio = diametro / 2.0;
        for (int y = 0; y < diametro; y++) {
            for (int x = 0; x < diametro; x++) {
                double distanza = Math.hypot(x + 0.5 - raggio, y + 0.5 - raggio) / raggio;
                if (distanza < 1) {
                    int alfa = (int) Math.round(90 * (1 - distanza) * (1 - distanza));
                    tela.punto(x, y, conAlfa(rgb(255, 176, 70), alfa));
                }
            }
        }
        return tela.crea();
    }
}
