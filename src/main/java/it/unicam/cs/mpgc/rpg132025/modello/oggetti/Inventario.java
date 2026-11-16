package it.unicam.cs.mpgc.rpg132025.modello.oggetti;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Le quantità di oggetti possedute da un personaggio.
 */
public final class Inventario {

    private final EnumMap<TipoOggetto, Integer> quantita = new EnumMap<>(TipoOggetto.class);

    /**
     * Crea un inventario con il contenuto indicato (usato quando si carica una partita).
     *
     * @param contenuto quantità per tipo di oggetto
     * @return il nuovo inventario
     */
    public static Inventario da(Map<TipoOggetto, Integer> contenuto) {
        Inventario inventario = new Inventario();
        contenuto.forEach(inventario::aggiungi);
        return inventario;
    }

    /**
     * Aggiunge un oggetto.
     *
     * @param tipo tipo di oggetto
     */
    public void aggiungi(TipoOggetto tipo) {
        aggiungi(tipo, 1);
    }

    /**
     * Aggiunge più oggetti dello stesso tipo.
     *
     * @param tipo     tipo di oggetto
     * @param numero   quanti aggiungerne (zero è ammesso)
     * @throws IllegalArgumentException se il numero è negativo
     */
    public void aggiungi(TipoOggetto tipo, int numero) {
        Objects.requireNonNull(tipo, "tipo");
        if (numero < 0) {
            throw new IllegalArgumentException("Il numero di oggetti non può essere negativo");
        }
        if (numero > 0) {
            quantita.merge(tipo, numero, Integer::sum);
        }
    }

    /**
     * Toglie un oggetto, se presente.
     *
     * @param tipo tipo di oggetto
     * @return {@code true} se l'oggetto c'era ed è stato tolto
     */
    public boolean rimuovi(TipoOggetto tipo) {
        int presenti = quantita(tipo);
        if (presenti == 0) {
            return false;
        }
        if (presenti == 1) {
            quantita.remove(tipo);
        } else {
            quantita.put(tipo, presenti - 1);
        }
        return true;
    }

    /**
     * @param tipo tipo di oggetto
     * @return quanti oggetti di quel tipo sono presenti
     */
    public int quantita(TipoOggetto tipo) {
        return quantita.getOrDefault(tipo, 0);
    }

    /** @return una copia non modificabile del contenuto */
    public Map<TipoOggetto, Integer> contenuto() {
        return Collections.unmodifiableMap(new EnumMap<>(quantita));
    }
}
