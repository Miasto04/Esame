package it.unicam.cs.mpgc.rpg132025.modello.combattimento;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Calcola il danno di un colpo applicando in sequenza tutti i {@link ModificatoreDanno}.
 * I moltiplicatori dei bonus si combinano tra loro: per esempio, consiglio del
 * pupo (x2) più colpo alle spalle (x1,5) fa triplicare il danno.
 */
public final class CalcolatoreDanni {

    private final List<ModificatoreDanno> modificatori;

    /**
     * @param modificatori regole da applicare, nell'ordine dato
     */
    public CalcolatoreDanni(List<ModificatoreDanno> modificatori) {
        this.modificatori = List.copyOf(modificatori);
    }

    /**
     * @param dannoBase danno dell'arma prima dei bonus
     * @param contesto  circostanze del colpo
     * @return danno finale e bonus ottenuti
     */
    public RisultatoColpo calcola(int dannoBase, ContestoColpo contesto) {
        if (dannoBase < 0) {
            throw new IllegalArgumentException("Il danno base non può essere negativo");
        }
        double moltiplicatore = 1.0;
        List<Bonus> ottenuti = new ArrayList<>();
        for (ModificatoreDanno modificatore : modificatori) {
            Optional<Bonus> bonus = modificatore.valuta(contesto);
            if (bonus.isPresent()) {
                ottenuti.add(bonus.get());
                moltiplicatore *= bonus.get().moltiplicatore();
            }
        }
        int danno = (int) Math.round(dannoBase * moltiplicatore);
        return new RisultatoColpo(danno, ottenuti);
    }
}
