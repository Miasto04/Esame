package it.unicam.cs.mpgc.rpg132025.modello.pupo;

import it.unicam.cs.mpgc.rpg132025.modello.drago.VistaDrago;
import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

/**
 * Ciò che il pupo e le sue strategie possono osservare del combattimento.
 * Tutto è in sola lettura, tranne la pubblicazione di eventi.
 */
public interface SituazioneCombattimento {

    /** @return casella in cui si trova l'eroe */
    Posizione posizioneEroe();

    /** @return il drago, in sola lettura */
    VistaDrago drago();

    /**
     * @param casella casella da controllare
     * @return {@code true} se ci si può camminare (terreno libero e non occupato dal drago)
     */
    boolean percorribile(Posizione casella);

    /**
     * @param casella casella da controllare
     * @return {@code true} se un attacco del drago sta per colpirla o la sta colpendo
     */
    boolean pericolosa(Posizione casella);

    /**
     * Pubblica un evento.
     *
     * @param evento l'evento
     */
    void pubblica(EventoGioco evento);
}
