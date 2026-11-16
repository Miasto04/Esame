package it.unicam.cs.mpgc.rpg132025.modello.mondo;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Ricerche in ampiezza (BFS) sulla griglia, usate dai personaggi controllati dal gioco.
 * <p>
 * Le ricerche esplorano al massimo {@code limite} caselle, così il costo resta
 * basso anche su mappe grandi.
 */
public final class RicercaPercorso {

    private static final Direzione[] DIREZIONI = Direzione.values();

    private RicercaPercorso() {
    }

    /**
     * Calcola il primo passo del percorso più breve verso una casella che soddisfa l'obiettivo.
     *
     * @param partenza     casella di partenza
     * @param obiettivo    condizione che la casella di arrivo deve rispettare
     * @param percorribile condizione per attraversare una casella
     * @param limite       numero massimo di caselle da esplorare
     * @return la direzione del primo passo, vuota se si è già arrivati o se non esiste un percorso
     */
    public static Optional<Direzione> primoPassoVerso(Posizione partenza, Predicate<Posizione> obiettivo,
                                                      Predicate<Posizione> percorribile, int limite) {
        if (obiettivo.test(partenza)) {
            return Optional.empty();
        }
        Map<Posizione, Direzione> primoPasso = new HashMap<>();
        Deque<Posizione> coda = new ArrayDeque<>();
        for (Direzione direzione : DIREZIONI) {
            Posizione vicina = partenza.spostata(direzione);
            if (percorribile.test(vicina)) {
                primoPasso.put(vicina, direzione);
                coda.add(vicina);
            }
        }
        int esplorate = 0;
        while (!coda.isEmpty() && esplorate < limite) {
            Posizione corrente = coda.poll();
            esplorate++;
            Direzione iniziale = primoPasso.get(corrente);
            if (obiettivo.test(corrente)) {
                return Optional.of(iniziale);
            }
            for (Direzione direzione : DIREZIONI) {
                Posizione vicina = corrente.spostata(direzione);
                if (!vicina.equals(partenza) && !primoPasso.containsKey(vicina) && percorribile.test(vicina)) {
                    primoPasso.put(vicina, iniziale);
                    coda.add(vicina);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Trova la casella più vicina (in numero di passi) che soddisfa l'obiettivo.
     *
     * @param partenza     casella di partenza, controllata per prima
     * @param obiettivo    condizione cercata
     * @param percorribile condizione per attraversare una casella
     * @param limite       numero massimo di caselle da esplorare
     * @return la casella trovata, vuota se non ce n'è una raggiungibile
     */
    public static Optional<Posizione> piuVicina(Posizione partenza, Predicate<Posizione> obiettivo,
                                                Predicate<Posizione> percorribile, int limite) {
        Deque<Posizione> coda = new ArrayDeque<>();
        Set<Posizione> visitate = new HashSet<>();
        coda.add(partenza);
        visitate.add(partenza);
        int esplorate = 0;
        while (!coda.isEmpty() && esplorate < limite) {
            Posizione corrente = coda.poll();
            esplorate++;
            if (obiettivo.test(corrente)) {
                return Optional.of(corrente);
            }
            for (Direzione direzione : DIREZIONI) {
                Posizione vicina = corrente.spostata(direzione);
                if (percorribile.test(vicina) && visitate.add(vicina)) {
                    coda.add(vicina);
                }
            }
        }
        return Optional.empty();
    }
}
