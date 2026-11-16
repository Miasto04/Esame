package it.unicam.cs.mpgc.rpg132025;

import it.unicam.cs.mpgc.rpg132025.piattaforma.javafx.ApplicazioneJavaFX;
import javafx.application.Application;

/**
 * Punto di avvio dell'applicazione desktop.
 * <p>
 * È separato da {@link ApplicazioneJavaFX} perché una classe di avvio che non
 * estende {@code Application} funziona anche quando JavaFX è sul classpath
 * (per esempio in un JAR unico).
 */
public final class Main {

    private Main() {
    }

    /**
     * @param argomenti argomenti della riga di comando, passati a JavaFX
     */
    public static void main(String[] argomenti) {
        Application.launch(ApplicazioneJavaFX.class, argomenti);
    }
}
