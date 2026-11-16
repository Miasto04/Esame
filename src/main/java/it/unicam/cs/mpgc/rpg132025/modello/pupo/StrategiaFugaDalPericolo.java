package it.unicam.cs.mpgc.rpg132025.modello.pupo;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.RicercaPercorso;

import java.util.Optional;

/**
 * Se l'eroe si trova dove sta per arrivare un attacco, indica la casella sicura più vicina.
 * Va messa prima delle altre strategie: salvarsi viene prima di colpire.
 */
public final class StrategiaFugaDalPericolo implements StrategiaSuggerimento {

    /** Frase pronunciata dal pupo. */
    public static final String MESSAGGIO = "Attento! Spostati!";
    private static final int LIMITE_RICERCA = 120;

    @Override
    public Optional<Suggerimento> suggerisci(SituazioneCombattimento situazione, Suggerimento attuale) {
        Posizione eroe = situazione.posizioneEroe();
        if (!situazione.pericolosa(eroe)) {
            return Optional.empty();
        }
        if (attuale != null && attuale.tipo() == TipoSuggerimento.FUGA && sicura(situazione, attuale.posizione())) {
            return Optional.of(attuale);
        }
        return RicercaPercorso.piuVicina(eroe, casella -> sicura(situazione, casella),
                        situazione::percorribile, LIMITE_RICERCA)
                .map(casella -> new Suggerimento(casella, MESSAGGIO, TipoSuggerimento.FUGA));
    }

    private static boolean sicura(SituazioneCombattimento situazione, Posizione casella) {
        return situazione.percorribile(casella) && !situazione.pericolosa(casella);
    }
}
