package it.unicam.cs.mpgc.rpg132025.piattaforma.javafx;

import it.unicam.cs.mpgc.rpg132025.avvio.ComposizioneGioco;
import it.unicam.cs.mpgc.rpg132025.controllore.CicloDiGioco;
import it.unicam.cs.mpgc.rpg132025.controllore.ContestoApplicazione;
import it.unicam.cs.mpgc.rpg132025.controllore.ControlloreTitolo;
import it.unicam.cs.mpgc.rpg132025.controllore.GestoreControllori;
import it.unicam.cs.mpgc.rpg132025.input.StatoInput;
import it.unicam.cs.mpgc.rpg132025.modello.mondo.Direzione;
import it.unicam.cs.mpgc.rpg132025.vista.Schermo;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.SpritePersonaggi;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * La versione desktop del gioco, realizzata con JavaFX.
 * <p>
 * Questa classe fa solo da ponte: crea la finestra e il canvas, traduce gli
 * eventi di tastiera e mouse in {@link StatoInput} e a ogni fotogramma fa
 * avanzare il {@link CicloDiGioco}. Modello, viste e controllori stanno nei
 * package indipendenti dalla piattaforma.
 */
public final class ApplicazioneJavaFX extends Application {

    private static final String TITOLO = "La Tana del Drago";

    @Override
    public void start(Stage finestra) {
        StatoInput input = new StatoInput();
        GestoreControllori controllori = new GestoreControllori();
        ContestoApplicazione contesto = ComposizioneGioco.creaContesto(controllori, Platform::exit);
        controllori.mostra(new ControlloreTitolo(contesto));
        CicloDiGioco ciclo = new CicloDiGioco(controllori, input);

        int scala = scalaIniziale();
        Canvas canvas = new Canvas(Schermo.LARGHEZZA * scala, Schermo.ALTEZZA * scala);
        PennelloJavaFX pennello = new PennelloJavaFX(canvas.getGraphicsContext2D());
        StackPane radice = new StackPane(canvas);
        Scene scena = new Scene(radice, canvas.getWidth(), canvas.getHeight(), Color.BLACK);

        collegaTastiera(scena, input, finestra);
        collegaPuntatore(canvas, pennello, input);
        finestra.focusedProperty().addListener((_, _, adesso) -> {
            if (!adesso) {
                input.azzera();
            }
        });

        finestra.setTitle(TITOLO);
        finestra.setScene(scena);
        finestra.setMinWidth(Schermo.LARGHEZZA + 40.0);
        finestra.setMinHeight(Schermo.ALTEZZA + 60.0);
        finestra.getIcons().add(pennello.immagine(
                contesto.grafica().personaggi().drago(Direzione.GIU, SpritePersonaggi.PosaDrago.ALI_ALZATE)));
        finestra.show();

        new AnimationTimer() {
            private long precedente = -1;

            @Override
            public void handle(long adesso) {
                double trascorsi = precedente < 0 ? 0 : (adesso - precedente) / 1_000_000_000.0;
                precedente = adesso;
                adattaCanvas(canvas, scena);
                ciclo.avanza(trascorsi);
                pennello.iniziaFotogramma(canvas.getWidth(), canvas.getHeight());
                ciclo.disegna(pennello);
                pennello.terminaFotogramma();
            }
        }.start();
    }

    /** Il canvas occupa sempre tutta la finestra, anche quando viene ridimensionata. */
    private static void adattaCanvas(Canvas canvas, Scene scena) {
        if (canvas.getWidth() != scena.getWidth()) {
            canvas.setWidth(scena.getWidth());
        }
        if (canvas.getHeight() != scena.getHeight()) {
            canvas.setHeight(scena.getHeight());
        }
    }

    private static void collegaTastiera(Scene scena, StatoInput input, Stage finestra) {
        scena.setOnKeyPressed((KeyEvent evento) -> {
            if (evento.getCode() == KeyCode.F11) {
                finestra.setFullScreen(!finestra.isFullScreen());
                evento.consume();
                return;
            }
            TastieraJavaFX.comando(evento.getCode()).ifPresent(tasto -> {
                input.premi(tasto);
                evento.consume();
            });
        });
        scena.setOnKeyReleased((KeyEvent evento) -> TastieraJavaFX.comando(evento.getCode()).ifPresent(tasto -> {
            input.rilascia(tasto);
            evento.consume();
        }));
    }

    private static void collegaPuntatore(Canvas canvas, PennelloJavaFX pennello, StatoInput input) {
        canvas.setOnMouseMoved((MouseEvent evento) ->
                input.muoviPuntatore(pennello.xVirtuale(evento.getX()), pennello.yVirtuale(evento.getY())));
        canvas.setOnMouseDragged((MouseEvent evento) ->
                input.muoviPuntatore(pennello.xVirtuale(evento.getX()), pennello.yVirtuale(evento.getY())));
        canvas.setOnMousePressed((MouseEvent evento) ->
                input.premiPuntatore(pennello.xVirtuale(evento.getX()), pennello.yVirtuale(evento.getY())));
        canvas.setOnMouseReleased((MouseEvent evento) ->
                input.rilasciaPuntatore(pennello.xVirtuale(evento.getX()), pennello.yVirtuale(evento.getY())));
    }

    /** Sceglie l'ingrandimento intero più grande che fa stare la finestra nello schermo. */
    private static int scalaIniziale() {
        Rectangle2D area = Screen.getPrimary().getVisualBounds();
        int scala = (int) Math.floor(Math.min(area.getWidth() * 0.9 / Schermo.LARGHEZZA,
                area.getHeight() * 0.9 / Schermo.ALTEZZA));
        return Math.clamp(scala, 2, 6);
    }
}
