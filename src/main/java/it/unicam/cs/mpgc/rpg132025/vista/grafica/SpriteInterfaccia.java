package it.unicam.cs.mpgc.rpg132025.vista.grafica;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static it.unicam.cs.mpgc.rpg132025.vista.grafica.Tavolozza.*;

/**
 * Gli sprite dell'interfaccia: cuori, icone, pulsanti e indicatori.
 */
public final class SpriteInterfaccia {

    /** Stato di un cuore della barra della vita. */
    public enum Cuore {
        VUOTO,
        MEZZO,
        PIENO
    }

    private static final String[] CUORE = {
        ".kk...kk.",
        "kRRk.kRRk",
        "kRLRkRRRk",
        "kRRRRRRRk",
        ".kRRRRRk.",
        "..kRRRk..",
        "...kRk...",
        "....k....",
    };
    private static final String[] SOLE = {
        "....y....",
        ".y.....y.",
        "...yyy...",
        "..yYYyy..",
        "y.yYyyy.y",
        "..yyyyy..",
        "...yyy...",
        ".y.....y.",
        "....y....",
    };
    private static final String[] LUNA = {
        "...kkk...",
        "..kmmk...",
        ".kmmk....",
        ".kmk.....",
        ".kmk.....",
        ".kmmk....",
        "..kmmkk..",
        "...kkk...",
    };
    private static final String[] FRECCIA_GIU = {
        "kkkkkkk",
        "kyyyyyk",
        ".kyyyk.",
        "..kyk..",
        "...k...",
    };
    private static final String[] FRECCIA_CROCE = {
        "..w..",
        ".www.",
        "wwwww",
    };
    private static final String[] STELLINA = {
        "..y..",
        "..y..",
        "yyWyy",
        "..y..",
        "..y..",
    };

    private final EnumMap<Cuore, Sprite> cuori = new EnumMap<>(Cuore.class);
    private final EnumMap<Direzione, Sprite> frecceCroce = new EnumMap<>(Direzione.class);
    private final Map<Integer, Sprite> pulsanti = new HashMap<>();
    private final Sprite[] marcatoreAttacco = new Sprite[2];
    private final Sprite[] marcatoreFuga = new Sprite[2];
    private final Sprite frecciaAttacco;
    private final Sprite frecciaFuga;
    private final Sprite sole;
    private final Sprite luna;
    private final Sprite stellina;
    private final Sprite bussola;

    /** Disegna tutti gli elementi dell'interfaccia. */
    public SpriteInterfaccia() {
        Sprite pieno = disegno(CUORE, TelaPixel.colori("kRL", CONTORNO, Tavolozza.CUORE, CUORE_LUCE));
        cuori.put(Cuore.PIENO, pieno);
        cuori.put(Cuore.VUOTO, disegno(CUORE, TelaPixel.colori("kRL", CONTORNO, CUORE_VUOTO, CUORE_VUOTO)));
        TelaPixel mezzo = new TelaPixel(pieno.larghezza(), pieno.altezza());
        mezzo.disegna(CUORE, TelaPixel.colori("kRL", CONTORNO, Tavolozza.CUORE, CUORE_LUCE), 0, 0);
        for (int y = 0; y < pieno.altezza(); y++) {
            for (int x = 5; x < pieno.larghezza(); x++) {
                if (mezzo.colore(x, y) != CONTORNO && mezzo.colore(x, y) != TRASPARENTE) {
                    mezzo.punto(x, y, CUORE_VUOTO);
                }
            }
        }
        cuori.put(Cuore.MEZZO, mezzo.crea());

        Sprite frecciaSu = disegno(FRECCIA_CROCE, TelaPixel.colori("w", conAlfa(BIANCO, 200)));
        for (Direzione direzione : Direzione.values()) {
            frecceCroce.put(direzione, frecciaSu.ruotata(direzione.ordinal()));
        }
        for (int i = 0; i < 2; i++) {
            marcatoreAttacco[i] = creaMarcatore(1 + i, ORO, CONTORNO);
            marcatoreFuga[i] = creaMarcatore(1 + i, AZZURRO, CONTORNO);
        }
        frecciaAttacco = disegno(FRECCIA_GIU, TelaPixel.colori("ky", CONTORNO, ORO));
        frecciaFuga = disegno(FRECCIA_GIU, TelaPixel.colori("ky", CONTORNO, AZZURRO));
        sole = disegno(SOLE, TelaPixel.colori("yY", ORO, FUOCO_GIALLO));
        luna = disegno(LUNA, TelaPixel.colori("km", CONTORNO, PIETRA_CHIARA));
        stellina = disegno(STELLINA, TelaPixel.colori("yW", ORO, BIANCO));
        bussola = creaBussola();
    }

    /** @return lo sprite di un cuore (9x8) */
    public Sprite cuore(Cuore stato) {
        return cuori.get(stato);
    }

    /** @return la freccia bianca disegnata su un braccio della croce direzionale */
    public Sprite frecciaCroce(Direzione direzione) {
        return frecceCroce.get(direzione);
    }

    /**
     * @param diametro diametro in pixel
     * @return un pulsante rotondo semitrasparente
     */
    public Sprite pulsanteRotondo(int diametro) {
        return pulsanti.computeIfAbsent(diametro, SpriteInterfaccia::creaPulsante);
    }

    /**
     * @param fotogramma 0 o 1 (il segno pulsa)
     * @param attacco    {@code true} per il consiglio d'attacco (oro), {@code false} per la fuga (azzurro)
     * @return il segno che il pupo mette sulla casella consigliata
     */
    public Sprite marcatore(int fotogramma, boolean attacco) {
        Sprite[] serie = attacco ? marcatoreAttacco : marcatoreFuga;
        return serie[Math.floorMod(fotogramma, serie.length)];
    }

    /** @return la freccia che indica la casella consigliata */
    public Sprite freccia(boolean attacco) {
        return attacco ? frecciaAttacco : frecciaFuga;
    }

    /** @return l'icona del sole */
    public Sprite sole() {
        return sole;
    }

    /** @return l'icona della luna */
    public Sprite luna() {
        return luna;
    }

    /** @return una stellina (per il drago stordito e i luccichii) */
    public Sprite stellina() {
        return stellina;
    }

    /** @return il quadrante della bussola (15x15) */
    public Sprite bussola() {
        return bussola;
    }

    private static Sprite disegno(String[] righe, Map<Character, Integer> colori) {
        TelaPixel tela = new TelaPixel(righe[0].length(), righe.length);
        tela.disegna(righe, colori, 0, 0);
        return tela.crea();
    }

    private static Sprite creaMarcatore(int rientro, int colore, int bordo) {
        TelaPixel tela = new TelaPixel(16, 16);
        int a = rientro;
        int b = 15 - rientro;
        tela.rettangolo(a, a, 5, 2, colore);
        tela.rettangolo(a, a, 2, 5, colore);
        tela.rettangolo(b - 4, a, 5, 2, colore);
        tela.rettangolo(b - 1, a, 2, 5, colore);
        tela.rettangolo(a, b - 1, 5, 2, colore);
        tela.rettangolo(a, b - 4, 2, 5, colore);
        tela.rettangolo(b - 4, b - 1, 5, 2, colore);
        tela.rettangolo(b - 1, b - 4, 2, 5, colore);
        tela.contorno(bordo);
        return tela.crea();
    }

    private static Sprite creaPulsante(int diametro) {
        TelaPixel tela = new TelaPixel(diametro, diametro);
        double raggio = diametro / 2.0;
        tela.ellisse(raggio, raggio, raggio, raggio, conAlfa(BIANCO, 170));
        tela.ellisse(raggio, raggio, raggio - 1, raggio - 1, conAlfa(rgb(40, 40, 48), 120));
        tela.ellisse(raggio, raggio - 1, raggio - 3, raggio - 4, conAlfa(rgb(90, 90, 100), 70));
        return tela.crea();
    }

    private static Sprite creaBussola() {
        TelaPixel tela = new TelaPixel(15, 15);
        tela.ellisse(7.5, 7.5, 7.5, 7.5, PANNELLO_BORDO_SCURO);
        tela.ellisse(7.5, 7.5, 6.5, 6.5, PANNELLO_BORDO);
        tela.ellisse(7.5, 7.5, 5.5, 5.5, PANNELLO_CHIARO);
        tela.punto(7, 1, ROSSO_DANNO);
        tela.punto(7, 13, TESTO);
        tela.punto(1, 7, TESTO);
        tela.punto(13, 7, TESTO);
        return tela.crea();
    }
}
