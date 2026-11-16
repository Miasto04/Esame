package it.unicam.cs.mpgc.rpg132025.modello.comandi;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.partita.Comando;
import it.unicam.cs.mpgc.rpg132025.modello.partita.Partita;

/**
 * Indica in che direzione l'eroe deve camminare, o che deve fermarsi.
 *
 * @param direzione direzione richiesta, {@code null} per fermarsi
 */
public record ComandoMovimento(Direzione direzione) implements Comando {

    /** @return il comando che ferma l'eroe */
    public static ComandoMovimento fermati() {
        return new ComandoMovimento(null);
    }

    @Override
    public void eseguiSu(Partita partita) {
        partita.impostaMovimentoEroe(direzione);
    }
}
