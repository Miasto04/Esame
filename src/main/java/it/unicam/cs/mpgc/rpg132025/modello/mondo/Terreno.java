package it.unicam.cs.mpgc.rpg132025.modello.mondo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * I tipi di casella che compongono la mappa.
 * <p>
 * Ogni terreno ha il simbolo con cui compare nei file dei livelli e sa se ci si
 * può camminare sopra. Per aggiungere un terreno basta un nuovo valore (e il
 * relativo disegno nella vista).
 */
public enum Terreno {
    ERBA('.', true),
    ERBA_SCURA(',', true),
    FIORI('*', true),
    SENTIERO('_', true),
    SABBIA(':', true),
    ACQUA('~', false),
    SCOGLIERA('#', false),
    ALTOPIANO('^', false),
    GROTTA('G', false),
    ALBERO('T', false),
    PALMA('Y', false),
    ROCCIA('o', false),
    FELCE('&', false),
    FALO('F', false);

    private static final Map<Character, Terreno> PER_SIMBOLO = new HashMap<>();

    static {
        for (Terreno terreno : values()) {
            PER_SIMBOLO.put(terreno.simbolo, terreno);
        }
    }

    private final char simbolo;
    private final boolean percorribile;

    Terreno(char simbolo, boolean percorribile) {
        this.simbolo = simbolo;
        this.percorribile = percorribile;
    }

    /** @return il carattere che rappresenta il terreno nei file dei livelli */
    public char simbolo() {
        return simbolo;
    }

    /** @return {@code true} se un personaggio può camminarci sopra */
    public boolean percorribile() {
        return percorribile;
    }

    /** @return {@code true} se chi sta nelle caselle vicine recupera vita (il falò) */
    public boolean curaChiStaVicino() {
        return this == FALO;
    }

    /**
     * Cerca il terreno associato a un simbolo.
     *
     * @param simbolo carattere letto dal file del livello
     * @return il terreno, oppure vuoto se il simbolo non è un terreno
     */
    public static Optional<Terreno> daSimbolo(char simbolo) {
        return Optional.ofNullable(PER_SIMBOLO.get(simbolo));
    }
}
