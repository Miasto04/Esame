package it.unicam.cs.mpgc.rpg132025.modello.oggetti;

/**
 * Effetto che fa recuperare punti vita. Non viene consumato se la vita è già piena.
 *
 * @param quantita punti vita recuperati (metà cuore = 1)
 */
public record EffettoCura(int quantita) implements EffettoOggetto {

    /**
     * @throws IllegalArgumentException se la quantità non è positiva
     */
    public EffettoCura {
        if (quantita <= 0) {
            throw new IllegalArgumentException("La cura deve essere positiva");
        }
    }

    @Override
    public boolean applicaA(UtilizzatoreOggetti utilizzatore) {
        return utilizzatore.recuperaVita(quantita) > 0;
    }
}
