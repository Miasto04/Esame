package it.unicam.cs.mpgc.rpg132025.piattaforma.javafx;

import it.unicam.cs.mpgc.rpg132025.input.Tasto;
import javafx.scene.input.KeyCode;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Associa i tasti fisici della tastiera ai comandi logici del gioco.
 * Per cambiare i tasti basta modificare questa tabella.
 */
final class TastieraJavaFX {

    private static final Map<KeyCode, Tasto> TASTI = new EnumMap<>(KeyCode.class);

    static {
        TASTI.put(KeyCode.UP, Tasto.SU);
        TASTI.put(KeyCode.W, Tasto.SU);
        TASTI.put(KeyCode.DOWN, Tasto.GIU);
        TASTI.put(KeyCode.S, Tasto.GIU);
        TASTI.put(KeyCode.LEFT, Tasto.SINISTRA);
        TASTI.put(KeyCode.A, Tasto.SINISTRA);
        TASTI.put(KeyCode.RIGHT, Tasto.DESTRA);
        TASTI.put(KeyCode.D, Tasto.DESTRA);
        TASTI.put(KeyCode.SPACE, Tasto.AZIONE);
        TASTI.put(KeyCode.J, Tasto.AZIONE);
        TASTI.put(KeyCode.Q, Tasto.POZIONE);
        TASTI.put(KeyCode.K, Tasto.POZIONE);
        TASTI.put(KeyCode.E, Tasto.BACCA);
        TASTI.put(KeyCode.L, Tasto.BACCA);
        TASTI.put(KeyCode.ESCAPE, Tasto.PAUSA);
        TASTI.put(KeyCode.P, Tasto.PAUSA);
        TASTI.put(KeyCode.ENTER, Tasto.CONFERMA);
    }

    private TastieraJavaFX() {
    }

    /**
     * @param codice tasto fisico premuto
     * @return il comando corrispondente, se il tasto è usato dal gioco
     */
    static Optional<Tasto> comando(KeyCode codice) {
        return Optional.ofNullable(TASTI.get(codice));
    }
}
