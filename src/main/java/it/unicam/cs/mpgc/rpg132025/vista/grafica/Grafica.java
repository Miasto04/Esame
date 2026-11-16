package it.unicam.cs.mpgc.rpg132025.vista.grafica;

/**
 * Raccoglie tutte le risorse grafiche del gioco (pattern Facade). Va creata una
 * sola volta all'avvio e condivisa tra le schermate.
 */
public final class Grafica {

    private final FontPixel font = new FontPixel();
    private final SpriteTerreno terreno = new SpriteTerreno();
    private final SpritePersonaggi personaggi = new SpritePersonaggi();
    private final SpriteInterfaccia interfaccia = new SpriteInterfaccia();

    /** @return il carattere per i testi */
    public FontPixel font() {
        return font;
    }

    /** @return gli sprite del paesaggio */
    public SpriteTerreno terreno() {
        return terreno;
    }

    /** @return gli sprite di personaggi e oggetti */
    public SpritePersonaggi personaggi() {
        return personaggi;
    }

    /** @return gli sprite dell'interfaccia */
    public SpriteInterfaccia interfaccia() {
        return interfaccia;
    }
}
