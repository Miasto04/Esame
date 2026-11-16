package it.unicam.cs.mpgc.rpg132025.modello.combattimento;

import it.unicam.cs.mpgc.rpg132025.modello.drago.LatoDrago;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.pupo.Suggerimento;

import java.util.Objects;
import java.util.Optional;

/**
 * Le circostanze di un colpo inferto al drago, valutate dai modificatori di danno.
 *
 * @param posizioneAttaccante casella da cui l'eroe colpisce
 * @param latoColpito         lato del drago raggiunto dal colpo
 * @param suggerimento        consiglio del pupo attivo in quel momento, oppure {@code null}
 */
public record ContestoColpo(Posizione posizioneAttaccante, LatoDrago latoColpito, Suggerimento suggerimento) {

    public ContestoColpo {
        Objects.requireNonNull(posizioneAttaccante, "posizioneAttaccante");
        Objects.requireNonNull(latoColpito, "latoColpito");
    }

    /** @return il consiglio del pupo, se ce n'era uno */
    public Optional<Suggerimento> suggerimentoAttivo() {
        return Optional.ofNullable(suggerimento);
    }
}
