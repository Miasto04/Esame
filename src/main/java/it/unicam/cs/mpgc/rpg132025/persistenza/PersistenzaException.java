package it.unicam.cs.mpgc.rpg132025.persistenza;

/**
 * Errore durante il salvataggio o la lettura dei dati del gioco.
 * <p>
 * È un'eccezione non controllata: le implementazioni degli archivi traducono in
 * questa eccezione gli errori specifici della tecnologia usata (file, JSON,
 * database...), così chi le usa non dipende da quella tecnologia.
 */
public class PersistenzaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param messaggio descrizione dell'errore
     */
    public PersistenzaException(String messaggio) {
        super(messaggio);
    }

    /**
     * @param messaggio descrizione dell'errore
     * @param causa     eccezione originale
     */
    public PersistenzaException(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }
}
