package it.unicam.cs.mpgc.rpg132025.vista.grafica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Carattere bitmap 5x7 a larghezza variabile, con lettere accentate italiane.
 * <p>
 * Il testo è disegnato con gli sprite, quindi appare identico su ogni
 * piattaforma e non dipende dai font installati. Gli sprite di ogni coppia
 * carattere-colore vengono creati una sola volta.
 */
public final class FontPixel {

    /** Altezza delle maiuscole e della riga di base in pixel (a scala 1). */
    public static final int ALTEZZA = 7;
    /** Pixel sotto la riga di base occupati dalle lettere con la gamba (g, j, p, q, y). */
    public static final int DISCENDENZA = 2;
    private static final int SPAZIATURA = 1;
    private static final int LARGHEZZA_SPAZIO = 3;

    /**
     * Ogni voce: carattere, due punti, 7 byte esadecimali (una riga per byte, 5 bit per riga).
     * Le lettere con la gamba hanno 9 righe: le ultime due stanno sotto la riga di base.
     */
    private static final String[] DEFINIZIONI = {
        "A:0E11111F111111", "B:1E11111E11111E", "C:0E11101010110E", "D:1C12111111121C",
        "E:1F10101E10101F", "F:1F10101E101010", "G:0E11101711110F", "H:1111111F111111",
        "I:0E04040404040E", "J:0702020202120C", "K:11121418141211", "L:1010101010101F",
        "M:111B1515111111", "N:11111915131111", "O:0E11111111110E", "P:1E11111E101010",
        "Q:0E11111115120D", "R:1E11111E141211", "S:0F10100E01011E", "T:1F040404040404",
        "U:1111111111110E", "V:11111111110A04", "W:1111111515150A", "X:11110A040A1111",
        "Y:1111110A040404", "Z:1F01020408101F",
        "a:00000E010F110F", "b:1010161911111E", "c:00000E1010110E", "d:01010D1311110F",
        "e:00000E111F100E", "f:0609081C080808", "g:00000F1111110F010E", "h:10101619111111",
        "i:04000C0404040E", "j:02000602020202120C", "k:10101214181412", "l:0C04040404040E",
        "m:00001A15151111", "n:00001619111111", "o:00000E1111110E", "p:00001E1111111E1010",
        "q:00000F1111110F0101", "r:00001619101010", "s:00000E100E011E", "t:08081C08080906",
        "u:0000111111130D", "v:00001111110A04", "w:0000111115150A", "x:0000110A040A11",
        "y:0000111111110F010E", "z:00001F0204081F",
        "à:08040E010F110F", "è:08040E111F100E", "é:02040E111F100E", "ì:0804000C04040E",
        "ò:08040E1111110E", "ù:0804111111130D", "È:08041F101E101F",
        "0:0E11131519110E", "1:040C040404040E", "2:0E11010204081F", "3:1F02040201110E",
        "4:02060A121F0202", "5:1F101E0101110E", "6:0608101E11110E", "7:1F010204080808",
        "8:0E11110E11110E", "9:0E11110F01020C",
        ".:00000000000C0C", ",:000000000C0408", "::000C0C000C0C00", ";:000C0C000C0408",
        "!:04040404040004", "?:0E110102040004", "':04040800000000", "\":0A0A0A00000000",
        "-:0000000E000000", "+:0004041F040400", "/:00010204081000", "(:02040808080402",
        "):08040202020408", "%:18190204081303", "*:0004150E150400", "<:02040810080402",
        ">:08040201020408", "=:00001F001F0000", "_:0000000000001F", "#:0A0A1F0A1F0A0A"
    };

    private final Map<Character, Glifo> glifi = new HashMap<>();
    private final Map<Long, Sprite> sprite = new HashMap<>();

    /** Crea il carattere leggendo le definizioni dei glifi. */
    public FontPixel() {
        for (String definizione : DEFINIZIONI) {
            int[] righe = new int[(definizione.length() - 2) / 2];
            for (int i = 0; i < righe.length; i++) {
                righe[i] = Integer.parseInt(definizione.substring(2 + i * 2, 4 + i * 2), 16);
            }
            glifi.put(definizione.charAt(0), Glifo.da(righe));
        }
    }

    /**
     * @param testo testo da misurare
     * @return larghezza in pixel a scala 1
     */
    public int larghezza(String testo) {
        int totale = 0;
        for (int i = 0; i < testo.length(); i++) {
            totale += larghezzaCarattere(testo.charAt(i)) + SPAZIATURA;
        }
        return Math.max(0, totale - SPAZIATURA);
    }

    /**
     * @param testo testo da misurare
     * @param scala ingrandimento
     * @return larghezza in pixel alla scala indicata
     */
    public int larghezza(String testo, int scala) {
        return larghezza(testo) * scala;
    }

    /** Scrive un testo a scala 1. */
    public void scrivi(Pennello pennello, String testo, int x, int y, int colore) {
        scrivi(pennello, testo, x, y, colore, 1);
    }

    /**
     * Scrive un testo.
     *
     * @param pennello superficie
     * @param testo    testo da scrivere (i caratteri sconosciuti diventano '?')
     * @param x        bordo sinistro
     * @param y        bordo superiore
     * @param colore   colore ARGB
     * @param scala    ingrandimento intero
     */
    public void scrivi(Pennello pennello, String testo, int x, int y, int colore, int scala) {
        int cursore = x;
        for (int i = 0; i < testo.length(); i++) {
            char carattere = testo.charAt(i);
            if (carattere != ' ') {
                Sprite glifo = sprite(carattere, colore);
                if (scala == 1) {
                    pennello.sprite(glifo, cursore, y);
                } else {
                    pennello.sprite(glifo, cursore, y, scala);
                }
            }
            cursore += (larghezzaCarattere(carattere) + SPAZIATURA) * scala;
        }
    }

    /** Scrive un testo con un'ombra spostata di un pixel in basso a destra. */
    public void scriviConOmbra(Pennello pennello, String testo, int x, int y, int colore, int ombra, int scala) {
        scrivi(pennello, testo, x + scala, y + scala, ombra, scala);
        scrivi(pennello, testo, x, y, colore, scala);
    }

    /** Scrive un testo circondato da un bordo, leggibile su qualsiasi sfondo. */
    public void scriviBordato(Pennello pennello, String testo, int x, int y, int colore, int bordo) {
        scrivi(pennello, testo, x - 1, y, bordo);
        scrivi(pennello, testo, x + 1, y, bordo);
        scrivi(pennello, testo, x, y - 1, bordo);
        scrivi(pennello, testo, x, y + 1, bordo);
        scrivi(pennello, testo, x, y, colore);
    }

    /** Scrive un testo centrato orizzontalmente su {@code centroX}, con ombra. */
    public void scriviCentrato(Pennello pennello, String testo, int centroX, int y, int colore, int ombra, int scala) {
        int x = centroX - larghezza(testo, scala) / 2;
        scriviConOmbra(pennello, testo, x, y, colore, ombra, scala);
    }

    /**
     * Divide un testo in righe che non superano la larghezza indicata.
     *
     * @param testo             testo da dividere
     * @param larghezzaMassima  larghezza massima di una riga in pixel
     * @return le righe
     */
    public List<String> aCapo(String testo, int larghezzaMassima) {
        List<String> righe = new ArrayList<>();
        StringBuilder riga = new StringBuilder();
        for (String parola : testo.split(" ")) {
            String prova = riga.length() == 0 ? parola : riga + " " + parola;
            if (larghezza(prova) > larghezzaMassima && riga.length() > 0) {
                righe.add(riga.toString());
                riga = new StringBuilder(parola);
            } else {
                riga = new StringBuilder(prova);
            }
        }
        if (riga.length() > 0) {
            righe.add(riga.toString());
        }
        return righe;
    }

    private int larghezzaCarattere(char carattere) {
        return carattere == ' ' ? LARGHEZZA_SPAZIO : glifo(carattere).larghezza();
    }

    private Glifo glifo(char carattere) {
        Glifo glifo = glifi.get(carattere);
        if (glifo == null) {
            glifo = glifi.get(sostituto(carattere));
        }
        return glifo != null ? glifo : glifi.get('?');
    }

    private static char sostituto(char carattere) {
        return switch (carattere) {
            case '’', '‘', '`' -> '\'';
            case '×' -> 'x';
            case 'À' -> 'A';
            case 'É' -> 'E';
            case 'Ì' -> 'I';
            case 'Ò' -> 'O';
            case 'Ù' -> 'U';
            default -> '?';
        };
    }

    private Sprite sprite(char carattere, int colore) {
        long chiave = ((colore & 0xFFFFFFFFL) << 16) | carattere;
        return sprite.computeIfAbsent(chiave, _ -> glifo(carattere).sprite(colore));
    }

    /** Un glifo: le righe di bit e la parte di colonne effettivamente usata. */
    private static final class Glifo {

        private final int[] righe;
        private final int inizio;
        private final int larghezza;

        private Glifo(int[] righe, int inizio, int larghezza) {
            this.righe = righe.clone();
            this.inizio = inizio;
            this.larghezza = larghezza;
        }

        int larghezza() {
            return larghezza;
        }

        static Glifo da(int[] righe) {
            int minimo = 5;
            int massimo = -1;
            for (int riga : righe) {
                for (int colonna = 0; colonna < 5; colonna++) {
                    if ((riga & (0x10 >> colonna)) != 0) {
                        minimo = Math.min(minimo, colonna);
                        massimo = Math.max(massimo, colonna);
                    }
                }
            }
            if (massimo < 0) {
                return new Glifo(righe, 0, LARGHEZZA_SPAZIO);
            }
            return new Glifo(righe, minimo, massimo - minimo + 1);
        }

        Sprite sprite(int colore) {
            TelaPixel tela = new TelaPixel(larghezza, righe.length);
            for (int y = 0; y < righe.length; y++) {
                for (int x = 0; x < larghezza; x++) {
                    if ((righe[y] & (0x10 >> (x + inizio))) != 0) {
                        tela.punto(x, y, colore);
                    }
                }
            }
            return tela.crea();
        }
    }
}
