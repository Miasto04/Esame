package it.unicam.cs.mpgc.rpg132025.modello.livello;

import it.unicam.cs.mpgc.rpg132025.modello.drago.Drago;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Mappa;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Terreno;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.OggettoATerra;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.TipoOggetto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Legge un livello da un file di testo, una riga della mappa per riga del file.
 * <p>
 * Ogni carattere è un {@link Terreno} oppure un segnaposto: {@code @} l'eroe,
 * {@code p} il pupo, {@code D} l'angolo in alto a sinistra del drago e i simboli
 * di {@link TipoOggetto} per gli oggetti. Sotto i segnaposto c'è erba. Le righe
 * vuote e quelle che iniziano con {@code ;} sono ignorate. Disegnare un nuovo
 * livello non richiede di scrivere codice.
 */
public final class CaricatoreLivello {

    /** Segnaposto dell'eroe. */
    public static final char SIMBOLO_EROE = '@';
    /** Segnaposto del pupo. */
    public static final char SIMBOLO_PUPO = 'p';
    /** Segnaposto del drago. */
    public static final char SIMBOLO_DRAGO = 'D';
    private static final char COMMENTO = ';';

    /**
     * Carica un livello dalle risorse dell'applicazione.
     *
     * @param risorsa percorso della risorsa, per esempio {@code /livelli/isola.txt}
     * @return il livello letto
     * @throws LivelloNonValidoException se la risorsa manca o non è valida
     */
    public DefinizioneLivello carica(String risorsa) {
        Objects.requireNonNull(risorsa, "risorsa");
        try (InputStream ingresso = CaricatoreLivello.class.getResourceAsStream(risorsa)) {
            if (ingresso == null) {
                throw new LivelloNonValidoException("Livello non trovato: " + risorsa);
            }
            BufferedReader lettore = new BufferedReader(new InputStreamReader(ingresso, StandardCharsets.UTF_8));
            return interpreta(lettore.lines().toList());
        } catch (IOException e) {
            throw new LivelloNonValidoException("Impossibile leggere il livello " + risorsa, e);
        }
    }

    /**
     * Interpreta le righe di un livello.
     *
     * @param righe contenuto del file
     * @return il livello
     * @throws LivelloNonValidoException se il contenuto non è valido
     */
    public DefinizioneLivello interpreta(List<String> righe) {
        List<String> griglia = righe.stream()
                .map(String::stripTrailing)
                .filter(riga -> !riga.isEmpty() && riga.charAt(0) != COMMENTO)
                .toList();
        if (griglia.isEmpty()) {
            throw new LivelloNonValidoException("Il livello è vuoto");
        }
        int larghezza = griglia.getFirst().length();
        Terreno[][] celle = new Terreno[griglia.size()][larghezza];
        Posizione eroe = null;
        Posizione pupo = null;
        Posizione drago = null;
        List<OggettoATerra> oggetti = new ArrayList<>();
        for (int y = 0; y < griglia.size(); y++) {
            String riga = griglia.get(y);
            if (riga.length() != larghezza) {
                throw new LivelloNonValidoException("La riga " + (y + 1) + " è lunga " + riga.length()
                        + " caratteri invece di " + larghezza);
            }
            for (int x = 0; x < larghezza; x++) {
                char simbolo = riga.charAt(x);
                Posizione posizione = new Posizione(x, y);
                Optional<Terreno> terreno = Terreno.daSimbolo(simbolo);
                if (terreno.isPresent()) {
                    celle[y][x] = terreno.get();
                    continue;
                }
                celle[y][x] = Terreno.ERBA;
                if (simbolo == SIMBOLO_EROE) {
                    eroe = unico(eroe, posizione, "l'eroe");
                } else if (simbolo == SIMBOLO_PUPO) {
                    pupo = unico(pupo, posizione, "il pupo");
                } else if (simbolo == SIMBOLO_DRAGO) {
                    drago = unico(drago, posizione, "il drago");
                } else {
                    oggetti.add(new OggettoATerra(oggetto(simbolo, posizione), posizione));
                }
            }
        }
        Mappa mappa = new Mappa(celle);
        controllaPresenza(eroe, "l'eroe (" + SIMBOLO_EROE + ")");
        controllaPresenza(pupo, "il pupo (" + SIMBOLO_PUPO + ")");
        controllaPresenza(drago, "il drago (" + SIMBOLO_DRAGO + ")");
        for (Posizione casella : Drago.celleCorpoIn(drago)) {
            if (!mappa.percorribile(casella)) {
                throw new LivelloNonValidoException("Il drago occupa 2x2 caselle: la casella " + casella
                        + " deve essere percorribile");
            }
        }
        return new DefinizioneLivello(mappa, eroe, pupo, drago, oggetti);
    }

    private static TipoOggetto oggetto(char simbolo, Posizione posizione) {
        Optional<TipoOggetto> tipo = TipoOggetto.daSimbolo(simbolo);
        if (tipo.isEmpty()) {
            throw new LivelloNonValidoException("Simbolo sconosciuto '" + simbolo + "' alla riga "
                    + (posizione.y() + 1) + ", colonna " + (posizione.x() + 1));
        }
        return tipo.get();
    }

    private static Posizione unico(Posizione esistente, Posizione nuova, String cosa) {
        if (esistente != null) {
            throw new LivelloNonValidoException("Il livello contiene più volte " + cosa);
        }
        return nuova;
    }

    private static void controllaPresenza(Posizione posizione, String cosa) {
        if (posizione == null) {
            throw new LivelloNonValidoException("Nel livello manca " + cosa);
        }
    }
}
