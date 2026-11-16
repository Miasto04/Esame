package it.unicam.cs.mpgc.rpg132025.modello.combattimento;

import it.unicam.cs.mpgc.rpg132025.modello.pupo.Suggerimento;

import java.util.Optional;

/**
 * Raddoppia il danno quando l'eroe colpisce stando nella casella indicata dal pupo.
 */
public final class ModificatorePosizioneSuggerita implements ModificatoreDanno {

    /** Etichetta mostrata al giocatore. */
    public static final String ETICHETTA = "CONSIGLIO DEL PUPO";
    /** Moltiplicatore predefinito. */
    public static final double MOLTIPLICATORE = 2.0;

    @Override
    public Optional<Bonus> valuta(ContestoColpo contesto) {
        return contesto.suggerimentoAttivo()
                .filter(Suggerimento::premiaAttacco)
                .filter(suggerimento -> suggerimento.posizione().equals(contesto.posizioneAttaccante()))
                .map(suggerimento -> new Bonus(ETICHETTA, MOLTIPLICATORE));
    }
}
