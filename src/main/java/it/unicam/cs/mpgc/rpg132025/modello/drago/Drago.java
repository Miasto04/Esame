package it.unicam.cs.mpgc.rpg132025.modello.drago;

import it.unicam.cs.mpgc.rpg132025.modello.entita.Creatura;
import it.unicam.cs.mpgc.rpg132025.modello.entita.PuntiVita;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Il drago, nemico finale del livello.
 * <p>
 * Occupa 2x2 caselle: la sua {@link #posizione()} è l'angolo in alto a sinistra.
 * Il comportamento è delegato allo {@link StatoDrago} corrente; questa classe
 * custodisce i dati (vita, ricariche degli attacchi, furia) e conosce la
 * geometria del proprio corpo. I metodi che lo muovono sono visibili solo agli
 * stati, nello stesso package.
 */
public final class Drago extends Creatura implements VistaDrago {

    /** Punti vita del drago. */
    public static final int VITA_MASSIMA = 150;
    /** Sotto questa frazione di vita il drago diventa furioso. */
    static final double SOGLIA_FURIA = 0.4;
    /** Quando è furioso tutti i suoi tempi si accorciano di questo fattore. */
    static final double MOLTIPLICATORE_FURIA = 0.75;

    private final EnumMap<TipoAttacco, Double> ricariche = new EnumMap<>(TipoAttacco.class);
    private StatoDrago stato;
    private boolean furioso;
    private boolean colpitoDiRecente;
    private boolean furiaDaAnnunciare;

    /**
     * Crea un drago addormentato con la vita piena.
     *
     * @param angolo    angolo in alto a sinistra del corpo
     * @param direzione dove guarda
     */
    public Drago(Posizione angolo, Direzione direzione) {
        this(angolo, direzione, VITA_MASSIMA, false, false);
    }

    /**
     * Crea un drago in uno stato preciso (usato quando si carica una partita).
     *
     * @param angolo    angolo in alto a sinistra del corpo
     * @param direzione dove guarda
     * @param vita      punti vita attuali
     * @param sveglio   {@code false} per farlo partire addormentato
     * @param furioso   {@code true} se è già furioso
     */
    public Drago(Posizione angolo, Direzione direzione, int vita, boolean sveglio, boolean furioso) {
        super(angolo, direzione, new PuntiVita(vita, VITA_MASSIMA));
        this.furioso = furioso;
        if (!isViva()) {
            this.stato = new StatoSconfitto();
        } else if (sveglio) {
            this.stato = new StatoInseguimento();
        } else {
            this.stato = new StatoAddormentato();
        }
    }

    /**
     * Fa agire il drago per un intervallo di tempo.
     *
     * @param dt       secondi trascorsi
     * @param contesto il mondo circostante
     */
    public void aggiorna(double dt, ContestoDrago contesto) {
        Objects.requireNonNull(contesto, "contesto");
        aggiornaPasso(dt);
        ricariche.replaceAll((_, residuo) -> Math.max(0.0, residuo - dt));
        if (furiaDaAnnunciare) {
            furiaDaAnnunciare = false;
            contesto.pubblica(new EventoDragoFurioso());
        }
        if (colpitoDiRecente) {
            colpitoDiRecente = false;
            passaA(stato.dopoColpo(this), contesto);
        }
        passaA(stato.aggiorna(this, contesto, dt), contesto);
    }

    private void passaA(StatoDrago nuovo, ContestoDrago contesto) {
        if (nuovo != stato) {
            stato = Objects.requireNonNull(nuovo, "stato");
            nuovo.entra(this, contesto);
        }
    }

    // ---------------------------------------------------------------- vista in sola lettura

    @Override
    public AspettoDrago aspetto() {
        return stato.aspetto();
    }

    @Override
    public LatoDrago latoVulnerabile() {
        return stato.latoVulnerabile();
    }

    @Override
    public boolean isSveglio() {
        return stato.sveglio();
    }

    @Override
    public boolean isFurioso() {
        return furioso;
    }

    @Override
    public boolean inVolo() {
        return stato.inVolo();
    }

    @Override
    public double altitudine() {
        return stato.altitudine();
    }

    @Override
    public boolean occupa(Posizione casella) {
        return !inVolo() && copre(posizione(), casella);
    }

    @Override
    public List<Posizione> celleCorpo() {
        return celleCorpoIn(posizione());
    }

    @Override
    public List<Posizione> celleLato(LatoDrago lato) {
        return switch (lato) {
            case TESTA -> celleAdiacenti(direzione());
            case CODA -> celleAdiacenti(direzione().opposta());
            case FIANCHI -> {
                List<Posizione> fianchi = new ArrayList<>(celleAdiacenti(direzione().ruotataOraria()));
                fianchi.addAll(celleAdiacenti(direzione().ruotataAntioraria()));
                yield List.copyOf(fianchi);
            }
            case NESSUNO -> List.of();
        };
    }

    @Override
    public Optional<LatoDrago> latoDi(Posizione casella) {
        for (Direzione lato : Direzione.values()) {
            if (celleAdiacenti(lato).contains(casella)) {
                return Optional.of(latoRelativo(lato));
            }
        }
        return Optional.empty();
    }

    @Override
    public int distanzaDa(Posizione casella) {
        int dx = distanzaSuAsse(casella.x(), posizione().x());
        int dy = distanzaSuAsse(casella.y(), posizione().y());
        return Math.max(dx, dy);
    }

    // ---------------------------------------------------------------- geometria

    /**
     * @param angolo angolo in alto a sinistra
     * @return le quattro caselle di un corpo con quell'angolo
     */
    public static List<Posizione> celleCorpoIn(Posizione angolo) {
        return List.of(angolo, angolo.traslata(1, 0), angolo.traslata(0, 1), angolo.traslata(1, 1));
    }

    /**
     * @param angolo  angolo in alto a sinistra di un corpo
     * @param casella casella da controllare
     * @return {@code true} se il corpo con quell'angolo copre la casella
     */
    public static boolean copre(Posizione angolo, Posizione casella) {
        return casella.x() >= angolo.x() && casella.x() < angolo.x() + DIMENSIONE
                && casella.y() >= angolo.y() && casella.y() < angolo.y() + DIMENSIONE;
    }

    /**
     * @param lato lato assoluto del corpo
     * @return le due caselle adiacenti a quel lato
     */
    public List<Posizione> celleAdiacenti(Direzione lato) {
        Posizione a = posizione();
        return switch (lato) {
            case SU -> List.of(a.traslata(0, -1), a.traslata(1, -1));
            case GIU -> List.of(a.traslata(0, DIMENSIONE), a.traslata(1, DIMENSIONE));
            case SINISTRA -> List.of(a.traslata(-1, 0), a.traslata(-1, 1));
            case DESTRA -> List.of(a.traslata(DIMENSIONE, 0), a.traslata(DIMENSIONE, 1));
        };
    }

    /**
     * @param casella casella da raggiungere
     * @return la direzione prevalente dal centro del corpo verso la casella
     */
    public Direzione direzioneVerso(Posizione casella) {
        return Direzione.verso(casella.x() - centroX(), casella.y() - centroY()).orElse(direzione());
    }

    /** Area 4x4 attorno a un corpo con l'angolo indicato, usata dallo schianto dell'atterraggio. */
    static List<Posizione> areaAttorno(Posizione angolo) {
        List<Posizione> area = new ArrayList<>();
        for (int dy = -1; dy <= DIMENSIONE; dy++) {
            for (int dx = -1; dx <= DIMENSIONE; dx++) {
                area.add(angolo.traslata(dx, dy));
            }
        }
        return area;
    }

    /**
     * Caselle davanti alla bocca, sempre più larghe man mano che ci si allontana.
     *
     * @param lunghezza quante file di caselle copre la fiammata
     */
    List<Posizione> conoDiFuoco(int lunghezza) {
        Direzione avanti = direzione();
        Direzione lato = avanti.ruotataOraria();
        Set<Posizione> cono = new LinkedHashSet<>();
        for (int fila = 0; fila < lunghezza; fila++) {
            int allargamento = (fila + 1) / 2;
            for (Posizione base : celleAdiacenti(avanti)) {
                for (int scarto = -allargamento; scarto <= allargamento; scarto++) {
                    cono.add(base.traslata(avanti.dx() * fila + lato.dx() * scarto,
                            avanti.dy() * fila + lato.dy() * scarto));
                }
            }
        }
        return List.copyOf(cono);
    }

    /** Caselle spazzate dalla coda: la metà posteriore dell'anello attorno al corpo più due caselle dietro. */
    List<Posizione> zonaColpoDiCoda() {
        Set<Posizione> zona = new LinkedHashSet<>();
        for (Posizione casella : areaAttorno(posizione())) {
            if (!copre(posizione(), casella) && profondita(casella) < 0) {
                zona.add(casella);
            }
        }
        Direzione dietro = direzione().opposta();
        for (Posizione casella : celleAdiacenti(dietro)) {
            zona.add(casella.spostata(dietro));
        }
        return List.copyOf(zona);
    }

    /** @return quanto la casella è avanti (positivo) o dietro (negativo) rispetto al centro del corpo */
    double profondita(Posizione casella) {
        return (casella.x() - centroX()) * direzione().dx() + (casella.y() - centroY()) * direzione().dy();
    }

    /** @return {@code true} se la casella è oltre il lato posteriore del corpo */
    boolean eDietro(Posizione casella) {
        return profondita(casella) <= -1.5;
    }

    // ---------------------------------------------------------------- azioni usate dagli stati

    /** Fa un passo di una casella, girandosi in quella direzione. */
    void avanza(Direzione verso, double durata) {
        orienta(verso);
        spostaIn(posizione().spostata(verso), durata);
    }

    /** Sposta il corpo (in volo) verso un nuovo angolo, nel tempo indicato. */
    void voliVerso(Posizione angolo, double durata) {
        spostaIn(angolo, durata);
    }

    /** @return fattore per cui moltiplicare le durate degli attacchi */
    double moltiplicatoreTempi() {
        return furioso ? MOLTIPLICATORE_FURIA : 1.0;
    }

    /** @return {@code true} se l'attacco ha finito di ricaricarsi */
    boolean prontoPer(TipoAttacco tipo) {
        return ricariche.getOrDefault(tipo, 0.0) <= 0;
    }

    /** Impedisce di ripetere l'attacco per il tempo indicato (più breve se furioso). */
    void avviaRicarica(TipoAttacco tipo, double secondi) {
        ricariche.put(tipo, secondi * moltiplicatoreTempi());
    }

    /** @return lo stato attuale (per i test del package) */
    StatoDrago stato() {
        return stato;
    }

    @Override
    protected boolean puoEssereColpita() {
        return super.puoEssereColpita() && !stato.inVolo();
    }

    @Override
    protected void dopoDanno(int danno) {
        colpitoDiRecente = true;
        if (!isViva()) {
            stato = new StatoSconfitto();
            return;
        }
        if (!furioso && frazioneVita() <= SOGLIA_FURIA) {
            furioso = true;
            furiaDaAnnunciare = true;
        }
    }

    private LatoDrago latoRelativo(Direzione latoAssoluto) {
        if (latoAssoluto == direzione()) {
            return LatoDrago.TESTA;
        }
        if (latoAssoluto == direzione().opposta()) {
            return LatoDrago.CODA;
        }
        return LatoDrago.FIANCHI;
    }

    private double centroX() {
        return posizione().x() + (DIMENSIONE - 1) / 2.0;
    }

    private double centroY() {
        return posizione().y() + (DIMENSIONE - 1) / 2.0;
    }

    private static int distanzaSuAsse(int valore, int inizio) {
        int fine = inizio + DIMENSIONE - 1;
        if (valore < inizio) {
            return inizio - valore;
        }
        return valore > fine ? valore - fine : 0;
    }
}
