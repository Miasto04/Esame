package it.unicam.cs.mpgc.rpg132025.vista.grafica;

import it.unicam.cs.mpgc.rpg132025.modello.combattimento.Bonus;
import it.unicam.cs.mpgc.rpg132025.modello.combattimento.EventoColpoInflitto;
import it.unicam.cs.mpgc.rpg132025.modello.drago.EventoDragoAtterrato;
import it.unicam.cs.mpgc.rpg132025.modello.drago.EventoDragoFurioso;
import it.unicam.cs.mpgc.rpg132025.modello.drago.EventoDragoRisvegliato;
import it.unicam.cs.mpgc.rpg132025.modello.entita.EventoEroeCurato;
import it.unicam.cs.mpgc.rpg132025.modello.entita.EventoEroeFerito;
import it.unicam.cs.mpgc.rpg132025.modello.eventi.AscoltatoreEventi;
import it.unicam.cs.mpgc.rpg132025.modello.eventi.EventoGioco;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.EventoOggettoNonUsabile;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.EventoOggettoRaccolto;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.EventoOggettoUsato;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.TipoOggetto;
import it.unicam.cs.mpgc.rpg132025.modello.partita.EventoPartitaConclusa;
import it.unicam.cs.mpgc.rpg132025.modello.partita.StatoPartita;
import it.unicam.cs.mpgc.rpg132025.modello.partita.VistaPartita;
import it.unicam.cs.mpgc.rpg132025.vista.Schermo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static it.unicam.cs.mpgc.rpg132025.vista.grafica.Tavolozza.*;

/**
 * Effetti grafici che reagiscono agli eventi della partita: numeri del danno,
 * scritte dei bonus, lampi, scosse dell'inquadratura e messaggi.
 * <p>
 * È un osservatore della partita: il modello non sa che esiste.
 */
public final class EffettiVisivi {

    private static final double DURATA_TESTO = 1.1;
    /** Pixel di cui sale una scritta fluttuante durante la sua vita. */
    private static final int SALITA_TESTO = 14;
    /** Distanza verticale minima tra due scritte fluttuanti vicine. */
    private static final int DISTANZA_TESTI = 9;
    private static final double DURATA_MESSAGGIO = 2.8;
    private static final double DURATA_BONUS = 1.4;
    private static final int MESSAGGI_VISIBILI = 2;
    private static final int CASELLA = Schermo.CASELLA;

    private final Camera camera;
    private final List<TestoFluttuante> testi = new ArrayList<>();
    private final Deque<Messaggio> messaggi = new ArrayDeque<>();
    private final AscoltatoreEventi ascoltatore = this::reagisci;
    private boolean collegati;
    private double tempo;
    private double lampoDrago;
    private double lampoFerita;
    private double tempoDallaVittoria = -1;
    private String bonus = "";
    private double durataBonus;

    /**
     * @param camera inquadratura da far tremare
     */
    public EffettiVisivi(Camera camera) {
        this.camera = camera;
    }

    /**
     * Inizia ad ascoltare gli eventi di una partita.
     *
     * @param partita la partita da osservare
     */
    public void collega(VistaPartita partita) {
        if (!collegati) {
            partita.iscrivi(ascoltatore);
            collegati = true;
        }
    }

    /**
     * Smette di ascoltare la partita.
     *
     * @param partita la partita osservata
     */
    public void scollega(VistaPartita partita) {
        partita.annullaIscrizione(ascoltatore);
        collegati = false;
    }

    /**
     * Fa avanzare le animazioni degli effetti.
     *
     * @param dt secondi trascorsi
     */
    public void aggiorna(double dt) {
        tempo += dt;
        lampoDrago = Math.max(0, lampoDrago - dt);
        lampoFerita = Math.max(0, lampoFerita - dt);
        durataBonus = Math.max(0, durataBonus - dt);
        if (tempoDallaVittoria >= 0) {
            tempoDallaVittoria += dt;
        }
        Iterator<TestoFluttuante> iteratore = testi.iterator();
        while (iteratore.hasNext()) {
            TestoFluttuante testo = iteratore.next();
            testo.eta += dt;
            if (testo.eta >= DURATA_TESTO) {
                iteratore.remove();
            }
        }
        Messaggio primo = messaggi.peekFirst();
        if (primo != null) {
            primo.eta += dt;
            if (primo.eta >= DURATA_MESSAGGIO) {
                messaggi.pollFirst();
            }
        }
    }

    /** @return secondi trascorsi, usati come orologio per tutte le animazioni */
    public double tempo() {
        return tempo;
    }

    /** @return {@code true} per un istante dopo che il drago è stato colpito */
    public boolean lampoDrago() {
        return lampoDrago > 0;
    }

    /** @return secondi trascorsi dalla sconfitta del drago, negativo se è ancora vivo */
    public double tempoDallaVittoria() {
        return tempoDallaVittoria;
    }

    /** Disegna gli effetti che appartengono al mondo (si muovono con la camera). */
    public void disegnaNelMondo(Pennello pennello, FontPixel font) {
        for (TestoFluttuante testo : testi) {
            double avanzamento = testo.eta / DURATA_TESTO;
            int x = (int) Math.round(testo.x) - camera.x() - font.larghezza(testo.testo) / 2;
            int y = (int) Math.round(testo.y - SALITA_TESTO * avanzamento) - camera.y();
            pennello.opacita(avanzamento < 0.7 ? 1.0 : (1 - avanzamento) / 0.3);
            font.scriviBordato(pennello, testo.testo, x, y, testo.colore, CONTORNO);
        }
        pennello.opacita(1.0);
    }

    /** Disegna gli effetti che stanno sopra a tutto (lampo rosso, bonus del colpo e messaggi). */
    public void disegnaSopra(Pennello pennello, FontPixel font) {
        if (lampoFerita > 0) {
            pennello.rettangolo(0, 0, pennello.larghezza(), pennello.altezza(),
                    conAlfa(ROSSO_DANNO, (int) (90 * lampoFerita / 0.3)));
        }
        if (durataBonus > 0) {
            int larghezza = font.larghezza(bonus) + 10;
            int x = (pennello.larghezza() - larghezza) / 2;
            pennello.opacita(Math.min(1.0, durataBonus / 0.4));
            Pannelli.pannelloScuro(pennello, x, 22, larghezza, 11);
            font.scrivi(pennello, bonus, x + 5, 24, ORO);
            pennello.opacita(1.0);
        }
        int indice = 0;
        for (Messaggio messaggio : messaggi) {
            if (indice >= MESSAGGI_VISIBILI) {
                break;
            }
            int larghezza = font.larghezza(messaggio.testo) + 10;
            int x = (pennello.larghezza() - larghezza) / 2;
            int y = 151 - indice * 13;
            Pannelli.pannelloScuro(pennello, x, y, larghezza, 11);
            font.scrivi(pennello, messaggio.testo, x + 5, y + 2, BIANCO);
            indice++;
        }
    }

    // ---------------------------------------------------------------- reazioni agli eventi

    /** Sceglie la reazione in base al tipo di evento (e ai suoi dati, con i record pattern). */
    private void reagisci(EventoGioco evento) {
        switch (evento) {
            case EventoColpoInflitto colpo -> colpoInflitto(colpo);
            case EventoEroeFerito(int danno, Posizione posizione) -> eroeFerito(danno, posizione);
            case EventoEroeCurato(int quantita, Posizione posizione) ->
                    testo("+" + quantita, centro(posizione), VERDE_CURA);
            case EventoOggettoRaccolto(TipoOggetto tipo, _) -> messaggio("Hai trovato: " + tipo.nome());
            case EventoOggettoUsato(TipoOggetto tipo) -> messaggio("Hai usato: " + tipo.nome());
            case EventoOggettoNonUsabile(TipoOggetto tipo, EventoOggettoNonUsabile.Motivo motivo) ->
                    oggettoNonUsabile(tipo, motivo);
            case EventoDragoRisvegliato _ -> {
                messaggio("Il drago si è svegliato!");
                camera.scuoti(3, 0.7);
            }
            case EventoDragoFurioso _ -> {
                messaggio("Il drago è furioso!");
                camera.scuoti(3, 0.5);
            }
            case EventoDragoAtterrato _ -> camera.scuoti(4, 0.4);
            case EventoPartitaConclusa(StatoPartita esito) -> partitaConclusa(esito);
            default -> {
                // gli altri eventi non hanno effetti grafici
            }
        }
    }

    private void colpoInflitto(EventoColpoInflitto evento) {
        double[] punto = centro(evento.bersaglio());
        List<Bonus> ottenuti = evento.risultato().bonus();
        testo("-" + evento.risultato().danno(), punto, ottenuti.isEmpty() ? BIANCO : ORO);
        if (!ottenuti.isEmpty()) {
            StringBuilder descrizione = new StringBuilder();
            for (Bonus ottenuto : ottenuti) {
                if (descrizione.length() > 0) {
                    descrizione.append("  ");
                }
                descrizione.append(ottenuto.etichetta()).append(' ').append(moltiplicatore(ottenuto.moltiplicatore()));
            }
            bonus = descrizione.toString();
            durataBonus = DURATA_BONUS;
        }
        lampoDrago = 0.12;
        camera.scuoti(1.5, 0.12);
    }

    private void eroeFerito(int danno, Posizione posizione) {
        testo("-" + danno, centro(posizione), ROSSO_DANNO);
        lampoFerita = 0.3;
        camera.scuoti(3, 0.3);
    }

    private void oggettoNonUsabile(TipoOggetto tipo, EventoOggettoNonUsabile.Motivo motivo) {
        if (motivo == EventoOggettoNonUsabile.Motivo.NON_POSSEDUTO) {
            messaggio("Nessuna " + tipo.nome().toLowerCase(Locale.ITALIAN) + " nello zaino");
        } else {
            messaggio("Sei già in piena salute");
        }
    }

    private void partitaConclusa(StatoPartita esito) {
        if (esito == StatoPartita.VITTORIA) {
            tempoDallaVittoria = 0;
            messaggio("Hai sconfitto il drago!");
            camera.scuoti(4, 0.8);
        } else {
            messaggio("Sei stato sconfitto...");
        }
    }

    /** Aggiunge una scritta fluttuante, più in alto se nello stesso punto ce n'è già un'altra. */
    private void testo(String testo, double[] punto, int colore) {
        double y = punto[1];
        boolean spostata = true;
        while (spostata) {
            spostata = false;
            for (TestoFluttuante altro : testi) {
                double altezzaAttuale = altro.y - SALITA_TESTO * altro.eta / DURATA_TESTO;
                if (Math.abs(altro.x - punto[0]) < CASELLA && Math.abs(altezzaAttuale - y) < DISTANZA_TESTI) {
                    y = altezzaAttuale - DISTANZA_TESTI;
                    spostata = true;
                }
            }
        }
        testi.add(new TestoFluttuante(testo, punto[0], y, colore));
    }

    private void messaggio(String testo) {
        messaggi.addLast(new Messaggio(testo));
        while (messaggi.size() > 4) {
            messaggi.pollFirst();
        }
    }

    private static double[] centro(Posizione posizione) {
        return new double[] {posizione.x() * CASELLA + CASELLA / 2.0, posizione.y() * CASELLA - 2.0};
    }

    private static String moltiplicatore(double valore) {
        if (valore == Math.rint(valore)) {
            return "x" + (int) valore;
        }
        return String.format(Locale.ITALIAN, "x%.1f", valore);
    }

    /** Una scritta che sale e svanisce. */
    private static final class TestoFluttuante {
        private final String testo;
        private final double x;
        private final double y;
        private final int colore;
        private double eta;

        private TestoFluttuante(String testo, double x, double y, int colore) {
            this.testo = testo;
            this.x = x;
            this.y = y;
            this.colore = colore;
        }
    }

    /** Un messaggio mostrato per qualche secondo in basso. */
    private static final class Messaggio {
        private final String testo;
        private double eta;

        private Messaggio(String testo) {
            this.testo = testo;
        }
    }
}
