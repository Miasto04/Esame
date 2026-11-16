package it.unicam.cs.mpgc.rpg132025.modello.pupo;

import it.unicam.cs.mpgc.rpg132025.modello.drago.LatoDrago;
import it.unicam.cs.mpgc.rpg132025.modello.drago.VistaDrago;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Indica la casella accanto al lato che il drago tiene scoperto, scegliendo
 * quella sicura più vicina all'eroe. Colpire da lì vale doppio.
 */
public final class StrategiaLatoVulnerabile implements StrategiaSuggerimento {

    /** Finché il drago dorme, il pupo dà consigli solo quando l'eroe è a questa distanza. */
    public static final int DISTANZA_AVVICINAMENTO = 9;

    @Override
    public Optional<Suggerimento> suggerisci(SituazioneCombattimento situazione, Suggerimento attuale) {
        VistaDrago drago = situazione.drago();
        Posizione eroe = situazione.posizioneEroe();
        if (!drago.isViva() || drago.inVolo()) {
            return Optional.empty();
        }
        if (!drago.isSveglio() && drago.distanzaDa(eroe) > DISTANZA_AVVICINAMENTO) {
            return Optional.empty();
        }
        LatoDrago lato = drago.latoVulnerabile();
        if (lato == LatoDrago.NESSUNO) {
            return Optional.empty();
        }
        List<Posizione> candidate = drago.celleLato(lato).stream()
                .filter(situazione::percorribile)
                .filter(casella -> !situazione.pericolosa(casella))
                .toList();
        if (candidate.isEmpty()) {
            return Optional.empty();
        }
        String messaggio = messaggio(lato, drago.isSveglio());
        if (attuale != null && attuale.premiaAttacco() && attuale.messaggio().equals(messaggio)
                && candidate.contains(attuale.posizione())) {
            return Optional.of(attuale);
        }
        Posizione migliore = candidate.stream()
                .min(Comparator.comparingInt(casella -> casella.distanzaManhattan(eroe)))
                .orElseThrow();
        return Optional.of(new Suggerimento(migliore, messaggio, TipoSuggerimento.ATTACCO));
    }

    private static String messaggio(LatoDrago lato, boolean dragoSveglio) {
        if (!dragoSveglio) {
            return "Shh! Colpiscilo da dietro!";
        }
        return switch (lato) {
            case CODA -> "Corri dietro la coda!";
            case FIANCHI -> "Colpiscilo sul fianco!";
            case TESTA -> "Ora! Mira alla testa!";
            case NESSUNO -> "";
        };
    }
}
