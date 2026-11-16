package it.unicam.cs.mpgc.rpg132025.persistenza.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import it.unicam.cs.mpgc.rpg132025.persistenza.PersistenzaException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

/**
 * Un file che contiene un oggetto in formato JSON, letto e scritto con Gson.
 * <p>
 * La scrittura avviene su un file temporaneo che poi sostituisce l'originale:
 * se il programma si chiude a metà, il vecchio salvataggio resta integro.
 */
final class FileJson {

    private final Path percorso;
    private final Gson gson;

    /**
     * @param percorso file da usare (la cartella viene creata se manca)
     */
    FileJson(Path percorso) {
        this.percorso = Objects.requireNonNull(percorso, "percorso");
        this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }

    /**
     * @param tipo classe dell'oggetto contenuto
     * @param <T>  tipo dell'oggetto
     * @return l'oggetto letto, oppure vuoto se il file non esiste
     */
    <T> Optional<T> leggi(Class<T> tipo) {
        if (!esiste()) {
            return Optional.empty();
        }
        T valore;
        try (Reader lettore = Files.newBufferedReader(percorso, StandardCharsets.UTF_8)) {
            valore = gson.fromJson(lettore, tipo);
        } catch (IOException | RuntimeException e) {
            // Gson segnala JSON malformati e campi incoerenti con diverse eccezioni non controllate:
            // per chi usa l'archivio sono tutte "file illeggibile".
            throw new PersistenzaException("Impossibile leggere " + percorso + ": " + e.getMessage(), e);
        }
        if (valore == null) {
            throw new PersistenzaException("Il file " + percorso + " è vuoto");
        }
        return Optional.of(valore);
    }

    /**
     * @param valore oggetto da scrivere al posto del contenuto attuale
     */
    void scrivi(Object valore) {
        Objects.requireNonNull(valore, "valore");
        try {
            Path cartella = percorso.toAbsolutePath().getParent();
            if (cartella != null) {
                Files.createDirectories(cartella);
            }
            Path temporaneo = percorso.resolveSibling(percorso.getFileName() + ".tmp");
            try (Writer scrittore = Files.newBufferedWriter(temporaneo, StandardCharsets.UTF_8)) {
                gson.toJson(valore, scrittore);
            }
            sostituisci(temporaneo, percorso);
        } catch (IOException | JsonParseException e) {
            throw new PersistenzaException("Impossibile scrivere " + percorso + ": " + e.getMessage(), e);
        }
    }

    /** @return {@code true} se il file esiste */
    boolean esiste() {
        return Files.isRegularFile(percorso);
    }

    /** Cancella il file, se esiste. */
    void elimina() {
        try {
            Files.deleteIfExists(percorso);
        } catch (IOException e) {
            throw new PersistenzaException("Impossibile cancellare " + percorso + ": " + e.getMessage(), e);
        }
    }

    private static void sostituisci(Path sorgente, Path destinazione) throws IOException {
        try {
            Files.move(sorgente, destinazione, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException _) {
            Files.move(sorgente, destinazione, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
