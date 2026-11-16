package it.unicam.cs.mpgc.rpg132025.modello.eventi;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Canale publish/subscribe (pattern Observer) con cui il modello notifica ciò che accade
 * senza conoscere chi lo ascolta: statistiche, effetti grafici e futuri moduli
 * si collegano dall'esterno.
 */
public final class BusEventi {

    private final List<AscoltatoreEventi> ascoltatori = new CopyOnWriteArrayList<>();

    /**
     * Registra un ascoltatore che riceverà tutti gli eventi.
     *
     * @param ascoltatore l'ascoltatore da aggiungere
     */
    public void iscrivi(AscoltatoreEventi ascoltatore) {
        ascoltatori.add(Objects.requireNonNull(ascoltatore, "ascoltatore"));
    }

    /**
     * Registra un gestore interessato a un solo tipo di evento.
     *
     * @param tipo    classe degli eventi da ricevere
     * @param gestore azione da eseguire per ogni evento di quel tipo
     * @param <E>     tipo dell'evento
     * @return l'ascoltatore creato, da passare a {@link #annullaIscrizione} per staccarsi
     */
    public <E extends EventoGioco> AscoltatoreEventi iscrivi(Class<E> tipo, Consumer<? super E> gestore) {
        Objects.requireNonNull(tipo, "tipo");
        Objects.requireNonNull(gestore, "gestore");
        AscoltatoreEventi filtro = evento -> {
            if (tipo.isInstance(evento)) {
                gestore.accept(tipo.cast(evento));
            }
        };
        iscrivi(filtro);
        return filtro;
    }

    /**
     * Rimuove un ascoltatore registrato in precedenza.
     *
     * @param ascoltatore l'ascoltatore da rimuovere
     */
    public void annullaIscrizione(AscoltatoreEventi ascoltatore) {
        ascoltatori.remove(ascoltatore);
    }

    /**
     * Consegna l'evento a tutti gli ascoltatori, nell'ordine di iscrizione.
     *
     * @param evento l'evento da pubblicare
     */
    public void pubblica(EventoGioco evento) {
        Objects.requireNonNull(evento, "evento");
        for (AscoltatoreEventi ascoltatore : ascoltatori) {
            ascoltatore.suEvento(evento);
        }
    }
}
