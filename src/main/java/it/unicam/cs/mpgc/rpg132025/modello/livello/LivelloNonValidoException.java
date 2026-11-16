package it.unicam.cs.mpgc.rpg132025.modello.livello;

/**
 * Il file di un livello manca o contiene errori.
 */
public class LivelloNonValidoException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    /**
     * @param messaggio descrizione dell'errore
     */
    public LivelloNonValidoException(String messaggio) {
        super(messaggio);
    }

    /**
     * @param messaggio descrizione dell'errore
     * @param causa     eccezione originale
     */
    public LivelloNonValidoException(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }
}
