package it.unicam.cs.mpgc.rpg132025.modello.drago;

import it.unicam.cs.mpgc.rpg132025.modello.entita.VistaCreatura;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

import java.util.List;
import java.util.Optional;

/**
 * Vista in sola lettura del drago, usata dalla grafica e dalle strategie del pupo.
 * La {@link #posizione()} è l'angolo in alto a sinistra del corpo 2x2.
 */
public interface VistaDrago extends VistaCreatura {

    /** Lato del corpo in caselle. */
    int DIMENSIONE = 2;

    /** @return la posa attuale */
    AspettoDrago aspetto();

    /** @return il lato scoperto in questo momento */
    LatoDrago latoVulnerabile();

    /** @return {@code false} finché dorme */
    boolean isSveglio();

    /** @return {@code true} quando, ferito gravemente, attacca più in fretta */
    boolean isFurioso();

    /** @return {@code true} mentre è in aria */
    boolean inVolo();

    /** @return altezza da terra per la grafica, da 0 a 1 */
    double altitudine();

    /**
     * @param casella casella da controllare
     * @return {@code true} se il corpo del drago (a terra) occupa la casella
     */
    boolean occupa(Posizione casella);

    /** @return le quattro caselle del corpo */
    List<Posizione> celleCorpo();

    /**
     * @param lato lato relativo
     * @return le caselle adiacenti a quel lato del corpo
     */
    List<Posizione> celleLato(LatoDrago lato);

    /**
     * @param casella casella da controllare
     * @return il lato del drago a cui la casella è adiacente (gli angoli non contano)
     */
    Optional<LatoDrago> latoDi(Posizione casella);

    /**
     * @param casella casella da controllare
     * @return distanza (in diagonale vale uno) tra la casella e il corpo; 0 se è sotto il drago
     */
    int distanzaDa(Posizione casella);
}
