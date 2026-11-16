package it.unicam.cs.mpgc.rpg132025.modello.entita;

/**
 * Vista in sola lettura di un'entità dotata di punti vita.
 */
public interface VistaCreatura extends VistaEntita {

    /** @return punti vita attuali */
    int vita();

    /** @return punti vita massimi */
    int vitaMassima();

    /** @return rapporto tra vita attuale e massima, da 0 a 1 */
    double frazioneVita();

    /** @return {@code true} finché ha almeno un punto vita */
    boolean isViva();
}
