package it.unicam.cs.mpgc.rpg132025.vista.grafica;

/**
 * I colori del gioco, in formato ARGB. Tenerli in un solo posto rende lo stile coerente
 * e permette di cambiarlo facilmente.
 */
public final class Tavolozza {

    public static final int TRASPARENTE = 0;
    public static final int NERO = rgb(0, 0, 0);
    public static final int BIANCO = rgb(255, 255, 255);
    public static final int CONTORNO = rgb(40, 28, 24);
    public static final int OMBRA = conAlfa(NERO, 72);

    // terreno
    public static final int ERBA = rgb(98, 184, 60);
    public static final int ERBA_CHIARA = rgb(136, 210, 80);
    public static final int ERBA_SCURA = rgb(72, 150, 48);
    public static final int ERBA_OMBRA = rgb(54, 120, 42);
    public static final int ALTOPIANO = rgb(120, 198, 72);
    public static final int ACQUA = rgb(60, 192, 228);
    public static final int ACQUA_CHIARA = rgb(128, 226, 244);
    public static final int ACQUA_PROFONDA = rgb(44, 162, 212);
    public static final int SCHIUMA = rgb(234, 251, 253);
    public static final int SABBIA = rgb(240, 218, 152);
    public static final int SABBIA_SCURA = rgb(216, 190, 124);
    public static final int TERRA = rgb(180, 124, 72);
    public static final int TERRA_SCURA = rgb(142, 94, 52);
    public static final int SCOGLIERA = rgb(178, 112, 64);
    public static final int SCOGLIERA_SCURA = rgb(128, 76, 42);
    public static final int SCOGLIERA_CHIARA = rgb(208, 148, 90);
    public static final int PIETRA = rgb(162, 162, 174);
    public static final int PIETRA_SCURA = rgb(112, 112, 126);
    public static final int PIETRA_CHIARA = rgb(208, 208, 218);
    public static final int BUIO = rgb(32, 22, 20);
    public static final int BUIO_PROFONDO = rgb(14, 9, 8);

    // vegetazione
    public static final int FOGLIA = rgb(54, 142, 54);
    public static final int FOGLIA_SCURA = rgb(32, 100, 42);
    public static final int FOGLIA_CHIARA = rgb(102, 190, 74);
    public static final int TRONCO = rgb(124, 80, 44);
    public static final int TRONCO_SCURO = rgb(88, 54, 30);
    public static final int PALMA_FOGLIA = rgb(76, 174, 62);
    public static final int PALMA_FOGLIA_SCURA = rgb(44, 120, 46);
    public static final int PALMA_TRONCO = rgb(188, 136, 78);
    public static final int PALMA_TRONCO_SCURO = rgb(150, 100, 56);
    public static final int FIORE_BIANCO = rgb(250, 250, 240);
    public static final int FIORE_GIALLO = rgb(255, 214, 64);
    public static final int FIORE_ROSA = rgb(244, 120, 160);

    // fuoco
    public static final int FUOCO_GIALLO = rgb(255, 230, 96);
    public static final int FUOCO_ARANCIO = rgb(255, 150, 40);
    public static final int FUOCO_ROSSO = rgb(228, 66, 30);

    // personaggi
    public static final int PELLE = rgb(246, 198, 154);
    public static final int PELLE_OMBRA = rgb(216, 158, 118);
    public static final int CAPELLI = rgb(42, 34, 32);
    public static final int CAPELLI_LUCE = rgb(84, 70, 64);
    public static final int CAPELLI_PUPO = rgb(120, 72, 36);
    public static final int OCCHI = rgb(30, 22, 20);
    public static final int GUANCE = rgb(250, 150, 140);
    public static final int CAMICIA = rgb(246, 238, 216);
    public static final int CAMICIA_OMBRA = rgb(208, 196, 164);
    public static final int CINTURA = rgb(122, 76, 42);
    public static final int PANTALONI = rgb(72, 108, 164);
    public static final int PANTALONI_OMBRA = rgb(54, 80, 126);
    public static final int STIVALI = rgb(94, 58, 34);
    public static final int BERRETTO = rgb(222, 52, 48);
    public static final int BERRETTO_OMBRA = rgb(162, 32, 36);
    public static final int MAGLIA_PUPO = rgb(255, 206, 66);
    public static final int MAGLIA_PUPO_OMBRA = rgb(224, 162, 42);
    public static final int CALZONI_PUPO = rgb(98, 142, 72);

    // drago
    public static final int DRAGO = rgb(206, 46, 50);
    public static final int DRAGO_SCURO = rgb(142, 26, 38);
    public static final int DRAGO_CHIARO = rgb(242, 98, 82);
    public static final int ALA = rgb(150, 26, 42);
    public static final int ALA_MEMBRANA = rgb(234, 92, 72);
    public static final int CORNO = rgb(250, 236, 200);
    public static final int OCCHIO_DRAGO = rgb(255, 232, 64);
    public static final int BOCCA = rgb(96, 14, 22);

    // oggetti
    public static final int POZIONE = rgb(226, 44, 92);
    public static final int POZIONE_SCURA = rgb(164, 24, 70);
    public static final int VETRO = rgb(214, 240, 248);
    public static final int BACCA = rgb(112, 62, 184);
    public static final int BACCA_CHIARA = rgb(156, 108, 224);
    public static final int ACCIAIO = rgb(196, 206, 220);
    public static final int ACCIAIO_CHIARO = rgb(244, 248, 255);
    public static final int ORO = rgb(255, 212, 62);
    public static final int ORO_SCURO = rgb(204, 142, 22);
    public static final int AZZURRO = rgb(120, 220, 255);

    // interfaccia
    public static final int PANNELLO = rgb(250, 216, 142);
    public static final int PANNELLO_CHIARO = rgb(255, 238, 194);
    public static final int PANNELLO_BORDO = rgb(188, 108, 44);
    public static final int PANNELLO_BORDO_SCURO = rgb(122, 64, 26);
    public static final int PANNELLO_INCAVO = rgb(226, 178, 104);
    public static final int TESTO = rgb(78, 44, 22);
    public static final int TESTO_SPENTO = rgb(170, 130, 90);
    public static final int CUORE = rgb(232, 42, 58);
    public static final int CUORE_LUCE = rgb(255, 146, 156);
    public static final int CUORE_VUOTO = rgb(118, 72, 72);
    public static final int VERDE_CURA = rgb(110, 240, 110);
    public static final int ROSSO_DANNO = rgb(255, 80, 70);
    public static final int NOTTE = rgb(12, 14, 46);

    private Tavolozza() {
    }

    /**
     * @param r rosso, 0-255
     * @param g verde, 0-255
     * @param b blu, 0-255
     * @return il colore opaco
     */
    public static int rgb(int r, int g, int b) {
        return 0xFF000000 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    /**
     * @param colore colore di partenza
     * @param alfa   opacità, 0-255
     * @return lo stesso colore con l'opacità indicata
     */
    public static int conAlfa(int colore, int alfa) {
        return (Math.clamp(alfa, 0, 255) << 24) | (colore & 0x00FFFFFF);
    }
}
