package it.unicam.cs.mpgc.rpg132025.modello.entita;

import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;

import java.util.Objects;

/**
 * Entità che può essere ferita e curata.
 * <p>
 * {@link #subisciDanno(int)} è un template method: le sottoclassi decidono
 * quando possono essere colpite e cosa succede dopo un colpo, senza ripetere
 * la gestione dei punti vita.
 */
public abstract class Creatura extends Entita implements VistaCreatura {

    private final PuntiVita vita;

    /**
     * @param posizione casella iniziale
     * @param direzione orientamento iniziale
     * @param vita      punti vita iniziali
     */
    protected Creatura(Posizione posizione, Direzione direzione, PuntiVita vita) {
        super(posizione, direzione);
        this.vita = Objects.requireNonNull(vita, "vita");
    }

    @Override
    public int vita() {
        return vita.correnti();
    }

    @Override
    public int vitaMassima() {
        return vita.massimi();
    }

    @Override
    public double frazioneVita() {
        return vita.frazione();
    }

    @Override
    public boolean isViva() {
        return !vita.esauriti();
    }

    /**
     * Applica un danno, se in questo momento la creatura può essere colpita.
     *
     * @param danno punti vita da togliere
     * @return i punti vita effettivamente tolti (0 se il colpo non ha effetto)
     */
    public final int subisciDanno(int danno) {
        if (danno < 0) {
            throw new IllegalArgumentException("Il danno non può essere negativo");
        }
        if (danno == 0 || !puoEssereColpita()) {
            return 0;
        }
        int effettivo = vita.subisci(danno);
        if (effettivo > 0) {
            dopoDanno(effettivo);
        }
        return effettivo;
    }

    /** @return {@code true} se un colpo in questo momento avrebbe effetto */
    protected boolean puoEssereColpita() {
        return isViva();
    }

    /**
     * Reazione a un colpo andato a segno. Non fa nulla per impostazione predefinita.
     *
     * @param danno punti vita persi
     */
    protected void dopoDanno(int danno) {
        // punto di estensione per le sottoclassi
    }

    /**
     * Recupera punti vita se la creatura è ancora viva.
     *
     * @param quantita punti da recuperare
     * @return i punti effettivamente recuperati
     */
    protected int recuperaPuntiVita(int quantita) {
        return isViva() ? vita.cura(quantita) : 0;
    }
}
