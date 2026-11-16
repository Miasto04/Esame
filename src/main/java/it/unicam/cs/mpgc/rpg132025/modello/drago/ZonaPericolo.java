package it.unicam.cs.mpgc.rpg132025.modello.drago;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * Insieme di caselle che un attacco del drago sta per colpire.
 * <p>
 * Ogni zona ha un preavviso (le caselle lampeggiano e il giocatore può
 * scappare) seguito da un intervallo in cui fa davvero male. È immutabile:
 * la fase si calcola dal tempo di gioco ricevuto.
 *
 * @param tipo        attacco che ha generato la zona
 * @param celle       caselle coinvolte
 * @param danno       mezzi cuori tolti all'eroe che si trova dentro
 * @param inizio      istante di creazione, in secondi di gioco
 * @param inizioColpo istante in cui finisce il preavviso
 * @param fine        istante in cui la zona smette di fare danno
 */
public record ZonaPericolo(TipoAttacco tipo, Set<Posizione> celle, int danno,
                           double inizio, double inizioColpo, double fine) {

    public ZonaPericolo {
        Objects.requireNonNull(tipo, "tipo");
        celle = Set.copyOf(celle);
        if (danno < 0) {
            throw new IllegalArgumentException("Il danno non può essere negativo");
        }
        if (inizio > inizioColpo || inizioColpo > fine) {
            throw new IllegalArgumentException("Gli istanti della zona devono essere in ordine");
        }
    }

    /**
     * Crea una zona che parte adesso.
     *
     * @param tipo      attacco
     * @param celle     caselle coinvolte
     * @param adesso    tempo di gioco attuale
     * @param preavviso secondi prima del colpo
     * @param durata    secondi in cui il colpo è attivo
     * @return la nuova zona, con il danno tipico dell'attacco
     */
    public static ZonaPericolo crea(TipoAttacco tipo, Collection<Posizione> celle, double adesso,
                                    double preavviso, double durata) {
        return new ZonaPericolo(tipo, Set.copyOf(celle), tipo.danno(), adesso, adesso + preavviso,
                adesso + preavviso + durata);
    }

    /**
     * @param casella casella da controllare
     * @return {@code true} se la casella fa parte della zona
     */
    public boolean contiene(Posizione casella) {
        return celle.contains(casella);
    }

    /**
     * @param tempo tempo di gioco
     * @return {@code true} se il colpo non è ancora arrivato
     */
    public boolean inPreavviso(double tempo) {
        return tempo < inizioColpo;
    }

    /**
     * @param tempo tempo di gioco
     * @return {@code true} se in questo istante la zona fa danno
     */
    public boolean attiva(double tempo) {
        return tempo >= inizioColpo && tempo < fine;
    }

    /**
     * @param tempo tempo di gioco
     * @return {@code true} se la zona non serve più
     */
    public boolean esaurita(double tempo) {
        return tempo >= fine;
    }

    /**
     * @param tempo tempo di gioco
     * @return quanto è passato del preavviso, da 0 a 1
     */
    public double progressoPreavviso(double tempo) {
        double durata = inizioColpo - inizio;
        if (durata <= 0) {
            return 1.0;
        }
        return Math.clamp((tempo - inizio) / durata, 0.0, 1.0);
    }
}
