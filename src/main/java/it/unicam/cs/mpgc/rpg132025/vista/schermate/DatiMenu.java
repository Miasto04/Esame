package it.unicam.cs.mpgc.rpg132025.vista.schermate;

import it.unicam.cs.mpgc.rpg132025.vista.grafica.Riquadro;

/**
 * Ciò che la vista deve sapere di un menu per disegnarlo: le voci, dove stanno, quali sono
 * attive e quale è selezionata.
 * <p>
 * Lo implementa il menu del controllore: così la vista non dipende dal controllore, ma il
 * controllore usa un'interfaccia della vista (inversione delle dipendenze).
 */
public interface DatiMenu {

    /** @return quante voci ha il menu */
    int numeroVoci();

    /**
     * @param indice posizione della voce, dall'alto
     * @return il testo della voce
     */
    String testo(int indice);

    /**
     * @param indice posizione della voce, dall'alto
     * @return {@code true} se in questo momento la voce si può scegliere
     */
    boolean attiva(int indice);

    /** @return la posizione della voce selezionata */
    int selezionata();

    /**
     * @param indice posizione della voce, dall'alto
     * @return il pulsante della voce sullo schermo
     */
    Riquadro riquadro(int indice);
}
