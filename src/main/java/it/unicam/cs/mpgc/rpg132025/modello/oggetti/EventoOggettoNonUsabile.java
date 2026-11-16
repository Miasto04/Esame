package it.unicam.cs.mpgc.rpg132025.modello.oggetti;

import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;

/**
 * L'eroe ha provato a usare un oggetto, ma non è servito a niente.
 *
 * @param tipo   oggetto richiesto
 * @param motivo perché non è stato usato
 */
public record EventoOggettoNonUsabile(TipoOggetto tipo, Motivo motivo) implements EventoGioco {

    /** Perché un oggetto non è stato usato. */
    public enum Motivo {
        /** L'inventario non ne contiene. */
        NON_POSSEDUTO,
        /** L'effetto non avrebbe cambiato nulla (per esempio vita già piena). */
        NESSUN_EFFETTO
    }
}
