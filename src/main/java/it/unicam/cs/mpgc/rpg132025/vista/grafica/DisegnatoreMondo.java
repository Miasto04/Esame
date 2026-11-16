package it.unicam.cs.mpgc.rpg132025.vista.grafica;

import it.unicam.cs.mpgc.rpg132025.modello.drago.AspettoDrago;
import it.unicam.cs.mpgc.rpg132025.modello.drago.TipoAttacco;
import it.unicam.cs.mpgc.rpg132025.modello.drago.VistaDrago;
import it.unicam.cs.mpgc.rpg132025.modello.drago.ZonaPericolo;
import it.unicam.cs.mpgc.rpg132025.modello.entita.VistaEntita;
import it.unicam.cs.mpgc.rpg132025.modello.entita.VistaEroe;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Mappa;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Posizione;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Terreno;
import it.unicam.cs.mpgc.rpg132025.modello.oggetti.OggettoATerra;
import it.unicam.cs.mpgc.rpg132025.modello.partita.VistaPartita;
import it.unicam.cs.mpgc.rpg132025.modello.pupo.Suggerimento;
import it.unicam.cs.mpgc.rpg132025.modello.pupo.VistaPupo;
import it.unicam.cs.mpgc.rpg132025.vista.Schermo;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.SpritePersonaggi.PosaDrago;

import java.util.ArrayList;
import java.util.List;

import static it.unicam.cs.mpgc.rpg132025.vista.grafica.Tavolozza.*;

/**
 * Disegna il mondo di gioco inquadrato dalla camera.
 * <p>
 * Si disegnano solo le caselle visibili. L'ordine è: terreno, ombre e grotte,
 * segnali a terra (attacchi in arrivo, consiglio del pupo), poi riga per riga
 * gli oggetti alti e i personaggi, così chi sta più in basso copre chi sta
 * dietro; infine ciò che vola, il buio della notte e il fumetto del pupo.
 */
public final class DisegnatoreMondo {

    private static final int C = Schermo.CASELLA;
    private static final int ALTEZZA_VOLO = 26;
    private static final String RUGGITO = "GRAAAH!";
    /** Opacità di un albero che sta davanti a un personaggio. */
    private static final double OPACITA_DAVANTI = 0.45;

    private final Grafica grafica;

    /**
     * @param grafica risorse grafiche
     */
    public DisegnatoreMondo(Grafica grafica) {
        this.grafica = grafica;
    }

    /**
     * Disegna la partita.
     *
     * @param pennello superficie
     * @param partita  partita da mostrare
     * @param camera   inquadratura
     * @param effetti  orologio delle animazioni ed effetti attivi
     */
    public void disegna(Pennello pennello, VistaPartita partita, Camera camera, EffettiVisivi effetti) {
        Inquadratura vista = new Inquadratura(camera.x(), camera.y(), pennello.larghezza(), pennello.altezza());
        double tempo = effetti.tempo();
        Mappa mappa = partita.mappa();
        disegnaSuolo(pennello, mappa, vista, tempo);
        disegnaZone(pennello, partita, vista, tempo);
        partita.pupo().suggerimento().ifPresent(s -> disegnaMarcatore(pennello, s, vista, tempo));
        if (partita.drago().inVolo()) {
            disegnaOmbraDrago(pennello, partita.drago(), vista);
        }
        disegnaRighe(pennello, partita, vista, tempo, effetti);
        if (partita.drago().inVolo()) {
            disegnaDrago(pennello, partita.drago(), vista, tempo, effetti);
        }
        partita.pupo().suggerimento().ifPresent(s -> disegnaFreccia(pennello, s, vista, tempo));
        disegnaNotte(pennello, partita, vista);
        disegnaFumetto(pennello, partita, vista);
    }

    /**
     * @param minutiDelGiorno ora del giorno in minuti
     * @return quanto è buio, da 0 (giorno) a 0,55 (notte fonda)
     */
    static double oscurita(double minutiDelGiorno) {
        double ore = minutiDelGiorno / 60.0;
        double massimo = 0.55;
        if (ore >= 7 && ore <= 18) {
            return 0;
        }
        if (ore > 18 && ore < 20.5) {
            return massimo * (ore - 18) / 2.5;
        }
        if (ore >= 5 && ore < 7) {
            return massimo * (7 - ore) / 2;
        }
        return massimo;
    }

    // ---------------------------------------------------------------- terreno

    private void disegnaSuolo(Pennello pennello, Mappa mappa, Inquadratura vista, double tempo) {
        SpriteTerreno terreno = grafica.terreno();
        for (int riga = vista.primaRiga(); riga <= vista.ultimaRiga(); riga++) {
            for (int colonna = vista.primaColonna(); colonna <= vista.ultimaColonna(); colonna++) {
                disegnaCasella(pennello, mappa, colonna, riga, vista.x(colonna), vista.y(riga), tempo);
            }
        }
        for (int riga = vista.primaRiga(); riga <= vista.ultimaRiga(); riga++) {
            for (int colonna = vista.primaColonna(); colonna <= vista.ultimaColonna(); colonna++) {
                Terreno qui = mappa.terreno(new Posizione(colonna, riga));
                Terreno sopra = mappa.terreno(new Posizione(colonna, riga - 1));
                if (eParete(sopra) && !eParete(qui) && qui != Terreno.ALTOPIANO && qui != Terreno.ACQUA) {
                    pennello.sprite(terreno.ombraScogliera(), vista.x(colonna), vista.y(riga));
                }
                if (qui == Terreno.GROTTA && mappa.terreno(new Posizione(colonna - 1, riga)) != Terreno.GROTTA
                        && sopra != Terreno.GROTTA) {
                    pennello.sprite(terreno.grotta(), vista.x(colonna), vista.y(riga));
                }
            }
        }
    }

    private void disegnaCasella(Pennello pennello, Mappa mappa, int colonna, int riga, int x, int y, double tempo) {
        SpriteTerreno terreno = grafica.terreno();
        int variante = variante(colonna, riga);
        switch (mappa.terreno(new Posizione(colonna, riga))) {
            case ACQUA -> pennello.sprite(terreno.acqua((int) (tempo * 1.6), terraVicina(mappa, colonna, riga)), x, y);
            case SABBIA -> pennello.sprite(terreno.sabbia(variante), x, y);
            case SENTIERO -> pennello.sprite(terreno.sentiero(vicini(mappa, colonna, riga, Terreno.SENTIERO)), x, y);
            case ERBA_SCURA -> pennello.sprite(terreno.erbaScura(variante,
                    15 & ~vicini(mappa, colonna, riga, Terreno.ERBA_SCURA)), x, y);
            case FIORI -> pennello.sprite(terreno.fiori(variante), x, y);
            case ALTOPIANO -> pennello.sprite(terreno.altopiano(variante, vicini(mappa, colonna, riga, Terreno.ACQUA)),
                    x, y);
            case SCOGLIERA, GROTTA -> pennello.sprite(terreno.scogliera(variante,
                    mappa.terreno(new Posizione(colonna, riga - 1)) == Terreno.ALTOPIANO,
                    !eParete(mappa.terreno(new Posizione(colonna, riga + 1)))), x, y);
            case PALMA -> pennello.sprite(vicini(mappa, colonna, riga, Terreno.SABBIA) != 0
                    ? terreno.sabbia(variante) : terreno.erba(variante), x, y);
            default -> pennello.sprite(terreno.erba(variante), x, y);
        }
    }

    private static boolean eParete(Terreno terreno) {
        return terreno == Terreno.SCOGLIERA || terreno == Terreno.GROTTA;
    }

    private static int terraVicina(Mappa mappa, int colonna, int riga) {
        int maschera = 0;
        for (Direzione direzione : Direzione.values()) {
            if (mappa.terreno(new Posizione(colonna + direzione.dx(), riga + direzione.dy())) != Terreno.ACQUA) {
                maschera |= bit(direzione);
            }
        }
        return maschera;
    }

    private static int vicini(Mappa mappa, int colonna, int riga, Terreno cercato) {
        int maschera = 0;
        for (Direzione direzione : Direzione.values()) {
            if (mappa.terreno(new Posizione(colonna + direzione.dx(), riga + direzione.dy())) == cercato) {
                maschera |= bit(direzione);
            }
        }
        return maschera;
    }

    private static int bit(Direzione direzione) {
        return switch (direzione) {
            case SU -> SpriteTerreno.NORD;
            case DESTRA -> SpriteTerreno.EST;
            case GIU -> SpriteTerreno.SUD;
            case SINISTRA -> SpriteTerreno.OVEST;
        };
    }

    /** Variante pseudo-casuale ma stabile di una casella, per evitare un prato tutto uguale. */
    private static int variante(int colonna, int riga) {
        int h = colonna * 374_761_393 + riga * 668_265_263;
        h = (h ^ (h >>> 13)) * 1_274_126_177;
        return (h ^ (h >>> 16)) & 0xFF;
    }

    // ---------------------------------------------------------------- segnali a terra

    private void disegnaZone(Pennello pennello, VistaPartita partita, Inquadratura vista, double tempo) {
        double adesso = partita.tempo();
        for (ZonaPericolo zona : partita.zonePericolo()) {
            if (zona.inPreavviso(adesso)) {
                double progresso = zona.progressoPreavviso(adesso);
                double pulsazione = 0.5 + 0.5 * Math.sin(tempo * (10 + 22 * progresso));
                int alfa = (int) (45 + 95 * progresso * (0.55 + 0.45 * pulsazione));
                int bordo = conAlfa(FUOCO_ROSSO, 120 + (int) (120 * pulsazione));
                for (Posizione casella : zona.celle()) {
                    int x = vista.x(casella.x());
                    int y = vista.y(casella.y());
                    pennello.rettangolo(x + 1, y + 1, C - 2, C - 2, conAlfa(ROSSO_DANNO, alfa));
                    pennello.rettangolo(x + 1, y + 1, C - 2, 1, bordo);
                    pennello.rettangolo(x + 1, y + C - 2, C - 2, 1, bordo);
                    pennello.rettangolo(x + 1, y + 2, 1, C - 4, bordo);
                    pennello.rettangolo(x + C - 2, y + 2, 1, C - 4, bordo);
                }
            } else if (zona.attiva(adesso)) {
                for (Posizione casella : zona.celle()) {
                    if (zona.tipo() == TipoAttacco.SOFFIO_DI_FUOCO) {
                        int fotogramma = (int) (tempo * 12) + casella.x() + casella.y();
                        pennello.sprite(grafica.terreno().fuoco(fotogramma), vista.x(casella.x()), vista.y(casella.y()));
                    } else {
                        pennello.rettangolo(vista.x(casella.x()), vista.y(casella.y()), C, C, conAlfa(BIANCO, 120));
                    }
                }
            }
        }
    }

    private void disegnaMarcatore(Pennello pennello, Suggerimento suggerimento, Inquadratura vista, double tempo) {
        Posizione casella = suggerimento.posizione();
        pennello.sprite(grafica.interfaccia().marcatore((int) (tempo * 4), suggerimento.premiaAttacco()),
                vista.x(casella.x()), vista.y(casella.y()));
    }

    private void disegnaFreccia(Pennello pennello, Suggerimento suggerimento, Inquadratura vista, double tempo) {
        Posizione casella = suggerimento.posizione();
        int salto = (int) Math.round(Math.abs(Math.sin(tempo * 5)) * 3);
        pennello.sprite(grafica.interfaccia().freccia(suggerimento.premiaAttacco()),
                vista.x(casella.x()) + 4, vista.y(casella.y()) - 8 - salto);
    }

    // ---------------------------------------------------------------- oggetti e personaggi, riga per riga

    private void disegnaRighe(Pennello pennello, VistaPartita partita, Inquadratura vista, double tempo,
                              EffettiVisivi effetti) {
        VistaEroe eroe = partita.eroe();
        VistaPupo pupo = partita.pupo();
        VistaDrago drago = partita.drago();
        int rigaEroe = (int) Math.round(eroe.yVisivo());
        int rigaPupo = (int) Math.round(pupo.yVisivo());
        int rigaDrago = drago.inVolo() ? Integer.MIN_VALUE : (int) Math.round(drago.yVisivo()) + 1;
        List<Sagoma> sagome = List.of(
                new Sagoma(rigaEroe, new Riquadro(vista.xVisivo(eroe.xVisivo()), vista.yVisivo(eroe.yVisivo()) - 2,
                        C, C)),
                new Sagoma(rigaPupo, new Riquadro(vista.xVisivo(pupo.xVisivo()), vista.yVisivo(pupo.yVisivo()) - 1,
                        C, C)),
                new Sagoma(rigaDrago, new Riquadro(vista.xVisivo(drago.xVisivo()), vista.yVisivo(drago.yVisivo()),
                        2 * C, 2 * C)));
        Mappa mappa = partita.mappa();
        for (int riga = vista.primaRiga() - 1; riga <= vista.ultimaRiga() + 3; riga++) {
            for (int colonna = vista.primaColonna() - 1; colonna <= vista.ultimaColonna() + 1; colonna++) {
                disegnaElementoAlto(pennello, mappa, colonna, riga, vista, tempo, sagome);
            }
            for (OggettoATerra oggetto : partita.oggettiATerra()) {
                if (oggetto.posizione().y() == riga) {
                    disegnaOggetto(pennello, oggetto, vista, tempo);
                }
            }
            if (riga == rigaDrago) {
                disegnaDrago(pennello, drago, vista, tempo, effetti);
            }
            if (riga == rigaPupo) {
                disegnaPupo(pennello, pupo, vista);
            }
            if (riga == rigaEroe) {
                disegnaEroe(pennello, eroe, vista, tempo);
            }
        }
    }

    private void disegnaElementoAlto(Pennello pennello, Mappa mappa, int colonna, int riga, Inquadratura vista,
                                     double tempo, List<Sagoma> sagome) {
        SpriteTerreno terreno = grafica.terreno();
        int x = vista.x(colonna);
        int y = vista.y(riga);
        switch (mappa.terreno(new Posizione(colonna, riga))) {
            case ALBERO -> ancorato(pennello, terreno.albero(), x, y, riga, sagome);
            case PALMA -> ancorato(pennello, terreno.palma(), x, y, riga, sagome);
            case ROCCIA -> pennello.sprite(terreno.roccia(), x, y);
            case FELCE -> pennello.sprite(terreno.felce(), x, y);
            case FALO -> pennello.sprite(terreno.falo((int) (tempo * 8) + colonna), x, y + C - 20);
            default -> {
                // il terreno piatto è già stato disegnato
            }
        }
    }

    /**
     * Disegna uno sprite più grande di una casella con la base appoggiata sul fondo della casella.
     * Se copre un personaggio che gli sta dietro diventa semitrasparente, così non lo nasconde.
     */
    private static void ancorato(Pennello pennello, Sprite sprite, int x, int y, int riga, List<Sagoma> sagome) {
        Riquadro area = new Riquadro(x + C / 2 - sprite.larghezza() / 2, y + C - sprite.altezza() + 2,
                sprite.larghezza(), sprite.altezza());
        boolean copre = sagome.stream().anyMatch(sagoma -> sagoma.riga() < riga && sagoma.area().interseca(area));
        pennello.opacita(copre ? OPACITA_DAVANTI : 1.0);
        pennello.sprite(sprite, area.x(), area.y());
        pennello.opacita(1.0);
    }

    private void disegnaOggetto(Pennello pennello, OggettoATerra oggetto, Inquadratura vista, double tempo) {
        Posizione casella = oggetto.posizione();
        int ondeggio = (int) Math.round(Math.sin(tempo * 3 + casella.x()) * 1.5);
        int x = vista.x(casella.x());
        int y = vista.y(casella.y());
        pennello.sprite(grafica.personaggi().ombraPiccola(), x + 2, y + 12);
        pennello.sprite(grafica.personaggi().oggetto(oggetto.tipo()), x, y - 3 + ondeggio);
        if (((int) (tempo * 2 + casella.x() * 0.37)) % 3 == 0) {
            pennello.sprite(grafica.interfaccia().stellina(), x + 10, y - 2 + ondeggio);
        }
    }

    private void disegnaEroe(Pennello pennello, VistaEroe eroe, Inquadratura vista, double tempo) {
        SpritePersonaggi personaggi = grafica.personaggi();
        int x = vista.xVisivo(eroe.xVisivo());
        int y = vista.yVisivo(eroe.yVisivo());
        pennello.sprite(personaggi.ombraPiccola(), x + 2, y + 12);
        boolean visibile = !eroe.isInvulnerabile() || ((int) (tempo * 16)) % 2 == 0;
        if (visibile) {
            pennello.sprite(personaggi.eroe(eroe.direzione(), fotogrammaCamminata(eroe)), x, y - 2);
        }
        if (eroe.staAttaccando()) {
            Direzione direzione = eroe.direzione();
            pennello.sprite(personaggi.fendente(direzione, eroe.progressoAttacco() < 0.5 ? 0 : 1),
                    x + direzione.dx() * 12, y - 2 + direzione.dy() * 12);
        }
    }

    private void disegnaPupo(Pennello pennello, VistaPupo pupo, Inquadratura vista) {
        SpritePersonaggi personaggi = grafica.personaggi();
        int x = vista.xVisivo(pupo.xVisivo());
        int y = vista.yVisivo(pupo.yVisivo());
        pennello.sprite(personaggi.ombraPiccola(), x + 2, y + 12);
        pennello.sprite(personaggi.pupo(pupo.direzione(), fotogrammaCamminata(pupo)), x, y - 1);
    }

    private static int fotogrammaCamminata(VistaEntita entita) {
        if (!entita.inMovimento()) {
            return 0;
        }
        Posizione posizione = entita.posizione();
        return Math.floorMod(posizione.x() + posizione.y(), 2) == 0 ? 1 : 2;
    }

    private void disegnaOmbraDrago(Pennello pennello, VistaDrago drago, Inquadratura vista) {
        pennello.opacita(0.5 + 0.5 * (1 - drago.altitudine()));
        pennello.sprite(grafica.personaggi().ombraGrande(), vista.xVisivo(drago.xVisivo()) + 1,
                vista.yVisivo(drago.yVisivo()) + 22);
        pennello.opacita(1.0);
    }

    private void disegnaDrago(Pennello pennello, VistaDrago drago, Inquadratura vista, double tempo,
                              EffettiVisivi effetti) {
        SpritePersonaggi personaggi = grafica.personaggi();
        int baseX = vista.xVisivo(drago.xVisivo());
        int baseY = vista.yVisivo(drago.yVisivo());
        int quota = (int) Math.round(drago.altitudine() * ALTEZZA_VOLO);
        if (!drago.inVolo()) {
            pennello.sprite(personaggi.ombraGrande(), baseX + 1, baseY + 22);
        }
        Sprite sprite = personaggi.drago(drago.direzione(), posa(drago, tempo));
        int x = baseX - 8;
        int y = baseY - 8 - quota;
        if (drago.aspetto() == AspettoDrago.SCONFITTO) {
            double trascorso = Math.max(0, effetti.tempoDallaVittoria());
            double opacita = Math.max(0, 1 - trascorso / 1.8);
            if (opacita > 0 && ((int) (trascorso * 14)) % 2 == 0) {
                pennello.opacita(opacita);
                pennello.sprite(sprite, x, y);
                pennello.opacita(1.0);
            }
            return;
        }
        pennello.sprite(effetti.lampoDrago() ? personaggi.sagomaBianca(sprite) : sprite, x, y);
        if (drago.isFurioso()) {
            pennello.opacita(0.22 + 0.12 * Math.sin(tempo * 8));
            pennello.sprite(personaggi.sagomaRossa(sprite), x, y);
            pennello.opacita(1.0);
        }
        disegnaDettagliDrago(pennello, drago, x, y, tempo);
    }

    private static PosaDrago posa(VistaDrago drago, double tempo) {
        return switch (drago.aspetto()) {
            case DORME, STANCO, SCONFITTO -> PosaDrago.RIPOSO;
            case RUGGISCE, CARICA_FUOCO, SOFFIA_FUOCO, MORDE -> PosaDrago.FAUCI_APERTE;
            case COLPO_DI_CODA -> PosaDrago.ALI_ABBASSATE;
            case VOLA -> ((int) (tempo * 9)) % 2 == 0 ? PosaDrago.ALI_ALZATE : PosaDrago.ALI_ABBASSATE;
            case VEGLIA -> ((int) (tempo * 3)) % 2 == 0 ? PosaDrago.ALI_ALZATE : PosaDrago.ALI_ABBASSATE;
        };
    }

    /** Zeta del sonno, ruggito, stelline dello stordimento, scintille prima della fiammata. */
    private void disegnaDettagliDrago(Pennello pennello, VistaDrago drago, int x, int y, double tempo) {
        FontPixel font = grafica.font();
        int[] testa = ruota(24, 8, drago.direzione());
        int[] bocca = ruota(24, 0, drago.direzione());
        switch (drago.aspetto()) {
            case DORME -> {
                double ciclo = (tempo * 0.8) % 1.0;
                pennello.opacita(1 - ciclo * 0.6);
                font.scriviBordato(pennello, "z", x + testa[0] + 6, y + testa[1] - 8 - (int) (ciclo * 8), BIANCO,
                        CONTORNO);
                font.scriviBordato(pennello, "Z", x + testa[0] + 11, y + testa[1] - 14 - (int) (ciclo * 8),
                        BIANCO, CONTORNO);
                pennello.opacita(1.0);
            }
            case STANCO -> {
                for (int i = 0; i < 3; i++) {
                    double angolo = tempo * 5 + i * 2.1;
                    pennello.sprite(grafica.interfaccia().stellina(),
                            x + testa[0] - 2 + (int) Math.round(Math.cos(angolo) * 8),
                            y + testa[1] - 10 + (int) Math.round(Math.sin(angolo) * 3));
                }
            }
            case CARICA_FUOCO -> {
                for (int i = 0; i < 4; i++) {
                    double angolo = tempo * 13 + i * 1.6;
                    pennello.rettangolo(x + bocca[0] + (int) Math.round(Math.cos(angolo) * 3),
                            y + bocca[1] + (int) Math.round(Math.sin(angolo) * 3), 2, 2,
                            i % 2 == 0 ? FUOCO_GIALLO : FUOCO_ARANCIO);
                }
            }
            case RUGGISCE -> {
                int tremito = ((int) (tempo * 30)) % 2;
                font.scriviBordato(pennello, RUGGITO, x + 24 - font.larghezza(RUGGITO) / 2 + tremito, y - 20,
                        ROSSO_DANNO, CONTORNO);
            }
            default -> {
                // nessun dettaglio per le altre pose
            }
        }
    }

    /** Ruota un punto dello sprite del drago (disegnato rivolto in su) secondo la direzione. */
    private static int[] ruota(int x, int y, Direzione direzione) {
        int px = x;
        int py = y;
        for (int i = 0; i < direzione.ordinal(); i++) {
            int nuovaX = 47 - py;
            py = px;
            px = nuovaX;
        }
        return new int[] {px, py};
    }

    // ---------------------------------------------------------------- sopra a tutto

    private void disegnaNotte(Pennello pennello, VistaPartita partita, Inquadratura vista) {
        double buio = oscurita(partita.minutiDelGiorno());
        if (buio <= 0.01) {
            return;
        }
        pennello.rettangolo(0, 0, pennello.larghezza(), pennello.altezza(), conAlfa(NOTTE, (int) (buio * 255)));
        Sprite bagliore = grafica.terreno().bagliore();
        for (int riga = vista.primaRiga() - 2; riga <= vista.ultimaRiga() + 2; riga++) {
            for (int colonna = vista.primaColonna() - 2; colonna <= vista.ultimaColonna() + 2; colonna++) {
                if (partita.mappa().terreno(new Posizione(colonna, riga)) == Terreno.FALO) {
                    pennello.sprite(bagliore, vista.x(colonna) + C / 2 - bagliore.larghezza() / 2,
                            vista.y(riga) + C / 2 - bagliore.altezza() / 2);
                }
            }
        }
    }

    /** Il fumetto del pupo, senza coprire l'eroe né il riquadro che il pupo sta indicando. */
    private void disegnaFumetto(Pennello pennello, VistaPartita partita, Inquadratura vista) {
        VistaPupo pupo = partita.pupo();
        pupo.frase().ifPresent(frase -> {
            VistaEroe eroe = partita.eroe();
            List<Riquadro> libere = new ArrayList<>(2);
            libere.add(new Riquadro(vista.xVisivo(eroe.xVisivo()), vista.yVisivo(eroe.yVisivo()) - 2, C, C + 2));
            pupo.suggerimento().ifPresent(suggerimento -> libere.add(new Riquadro(
                    vista.x(suggerimento.posizione().x()), vista.y(suggerimento.posizione().y()), C, C)));
            int x = vista.xVisivo(pupo.xVisivo());
            int y = vista.yVisivo(pupo.yVisivo());
            Pannelli.fumetto(pennello, grafica.font(), frase, x + C / 2, y + 1, y + C, libere);
        });
    }

    /** Dove sta un personaggio sullo schermo e in quale riga viene disegnato. */
    private record Sagoma(int riga, Riquadro area) {
    }

    /** Conversioni tra caselle e pixel dello schermo per l'inquadratura corrente. */
    private record Inquadratura(int cameraX, int cameraY, int larghezza, int altezza) {

        int primaColonna() {
            return Math.floorDiv(cameraX, C);
        }

        int ultimaColonna() {
            return Math.floorDiv(cameraX + larghezza, C);
        }

        int primaRiga() {
            return Math.floorDiv(cameraY, C);
        }

        int ultimaRiga() {
            return Math.floorDiv(cameraY + altezza, C);
        }

        int x(int colonna) {
            return colonna * C - cameraX;
        }

        int y(int riga) {
            return riga * C - cameraY;
        }

        int xVisivo(double colonna) {
            return (int) Math.round(colonna * C) - cameraX;
        }

        int yVisivo(double riga) {
            return (int) Math.round(riga * C) - cameraY;
        }
    }
}
