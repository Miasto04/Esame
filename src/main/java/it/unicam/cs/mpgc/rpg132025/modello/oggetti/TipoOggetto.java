package it.unicam.cs.mpgc.rpg132025.modello.oggetti;

import java.util.Optional;

/**
 * Gli oggetti che si possono trovare sulla mappa e usare.
 * <p>
 * Ogni tipo ha un nome da mostrare, il simbolo con cui compare nei file dei
 * livelli e il suo {@link EffettoOggetto}. Il nome della costante viene usato
 * nei salvataggi, quindi non va cambiato.
 */
public enum TipoOggetto {
    POZIONE("Pozione", '!', new EffettoCura(6)),
    BACCA("Bacca", 'b', new EffettoCura(2));

    private final String nome;
    private final char simboloMappa;
    private final EffettoOggetto effetto;

    TipoOggetto(String nome, char simboloMappa, EffettoOggetto effetto) {
        this.nome = nome;
        this.simboloMappa = simboloMappa;
        this.effetto = effetto;
    }

    /** @return nome leggibile dell'oggetto */
    public String nome() {
        return nome;
    }

    /** @return carattere che rappresenta l'oggetto nei file dei livelli */
    public char simboloMappa() {
        return simboloMappa;
    }

    /** @return l'effetto prodotto dall'uso */
    public EffettoOggetto effetto() {
        return effetto;
    }

    /**
     * @param simbolo carattere letto da un livello
     * @return il tipo di oggetto corrispondente, se esiste
     */
    public static Optional<TipoOggetto> daSimbolo(char simbolo) {
        for (TipoOggetto tipo : values()) {
            if (tipo.simboloMappa == simbolo) {
                return Optional.of(tipo);
            }
        }
        return Optional.empty();
    }
}
