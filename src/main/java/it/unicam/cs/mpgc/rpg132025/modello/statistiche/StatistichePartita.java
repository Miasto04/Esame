package it.unicam.cs.mpgc.rpg132025.modello.statistiche;

import it.unicam.cs.mpgc.rpg132025.modello.combattimento.Bonus;
import it.unicam.cs.mpgc.rpg132025.modello.combattimento.EventoColpoInflitto;
import it.unicam.cs.mpgc.rpg132025.modello.combattimento.RisultatoColpo;
import it.unicam.cs.mpgc.rpg132025.modello.entita.EventoEroeFerito;
import it.unicam.cs.mpgc.rpg132025.modello.eventi.AscoltatoreEventi;
import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.EventoOggettoUsato;
import it.unicam.cs.mpgc.rpg132025.modello.pupo.EventoNuovoSuggerimento;

import java.util.Map;
import java.util.TreeMap;

/**
 * Conta ciò che succede durante una partita ascoltando gli eventi del modello.
 * <p>
 * Nessuna classe del gioco deve ricordarsi di aggiornare le statistiche: basta
 * iscrivere questo oggetto al bus degli eventi.
 */
public final class StatistichePartita implements AscoltatoreEventi {

    private final Map<String, Integer> bonus = new TreeMap<>();
    private double tempoSecondi;
    private int colpiASegno;
    private int dannoInflitto;
    private int dannoSubito;
    private int oggettiUsati;
    private int consigliRicevuti;

    /**
     * Riprende i conteggi da un riepilogo salvato.
     *
     * @param riepilogo statistiche salvate
     * @return nuove statistiche con gli stessi valori
     */
    public static StatistichePartita da(RiepilogoPartita riepilogo) {
        StatistichePartita statistiche = new StatistichePartita();
        statistiche.tempoSecondi = riepilogo.tempoSecondi();
        statistiche.colpiASegno = riepilogo.colpiASegno();
        statistiche.dannoInflitto = riepilogo.dannoInflitto();
        statistiche.dannoSubito = riepilogo.dannoSubito();
        statistiche.oggettiUsati = riepilogo.oggettiUsati();
        statistiche.consigliRicevuti = riepilogo.consigliRicevuti();
        statistiche.bonus.putAll(riepilogo.bonus());
        return statistiche;
    }

    /**
     * @param secondi tempo di gioco da aggiungere
     */
    public void aggiungiTempo(double secondi) {
        if (secondi < 0) {
            throw new IllegalArgumentException("Il tempo non può essere negativo");
        }
        tempoSecondi += secondi;
    }

    @Override
    public void suEvento(EventoGioco evento) {
        switch (evento) {
            case EventoColpoInflitto(RisultatoColpo risultato, _) -> registraColpo(risultato);
            case EventoEroeFerito(int danno, _) -> dannoSubito += danno;
            case EventoOggettoUsato _ -> oggettiUsati++;
            case EventoNuovoSuggerimento _ -> consigliRicevuti++;
            default -> {
                // gli altri eventi non cambiano le statistiche
            }
        }
    }

    /** @return i valori attuali */
    public RiepilogoPartita riepilogo() {
        return new RiepilogoPartita(tempoSecondi, colpiASegno, dannoInflitto, dannoSubito, oggettiUsati,
                consigliRicevuti, bonus);
    }

    private void registraColpo(RisultatoColpo risultato) {
        colpiASegno++;
        dannoInflitto += risultato.danno();
        for (Bonus ottenuto : risultato.bonus()) {
            bonus.merge(ottenuto.etichetta(), 1, Integer::sum);
        }
    }
}
