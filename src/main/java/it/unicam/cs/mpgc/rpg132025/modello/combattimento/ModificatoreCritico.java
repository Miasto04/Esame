package it.unicam.cs.mpgc.rpg132025.modello.combattimento;

import java.util.Objects;
import java.util.Optional;
import java.util.random.RandomGenerator;

/**
 * Con una certa probabilità un colpo diventa critico.
 * <p>
 * La sorgente di casualità è passata dall'esterno, così nei test si può rendere
 * il risultato prevedibile.
 */
public final class ModificatoreCritico implements ModificatoreDanno {

    /** Etichetta mostrata al giocatore. */
    public static final String ETICHETTA = "CRITICO";
    /** Moltiplicatore di un colpo critico. */
    public static final double MOLTIPLICATORE = 1.5;

    private final RandomGenerator casuale;
    private final double probabilita;

    /**
     * @param casuale     sorgente di numeri casuali
     * @param probabilita probabilità di un critico, tra 0 e 1
     */
    public ModificatoreCritico(RandomGenerator casuale, double probabilita) {
        if (probabilita < 0 || probabilita > 1) {
            throw new IllegalArgumentException("La probabilità deve essere tra 0 e 1");
        }
        this.casuale = Objects.requireNonNull(casuale, "casuale");
        this.probabilita = probabilita;
    }

    @Override
    public Optional<Bonus> valuta(ContestoColpo contesto) {
        if (casuale.nextDouble() < probabilita) {
            return Optional.of(new Bonus(ETICHETTA, MOLTIPLICATORE));
        }
        return Optional.empty();
    }
}
