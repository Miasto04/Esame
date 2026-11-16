package it.unicam.cs.mpgc.rpg132025.modello.oggetti;

/**
 * Chi può usare gli oggetti dell'inventario.
 * <p>
 * Gli effetti degli oggetti dipendono solo da questa interfaccia e non dalla
 * classe concreta del personaggio (Dependency Inversion).
 */
public interface UtilizzatoreOggetti {

    /**
     * Recupera punti vita, senza superare il massimo.
     *
     * @param quantita punti vita da recuperare
     * @return i punti vita effettivamente recuperati
     */
    int recuperaVita(int quantita);
}
