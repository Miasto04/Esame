package it.unicam.cs.mpgc.rpg132025.vista.grafica;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CameraTest {

    private static final int VISTA = 180;
    private static final int INIZIO = 20;
    private static final int FINE = 156;

    @Test
    void restaSullEroeSeIlDragoSiVedeGia() {
        assertEquals(100, Camera.centroPerDue(100, 10, 120, 20, VISTA, INIZIO, FINE), 1e-9);
    }

    @Test
    void siSpostaQuantoBastaPerMostrareAncheIlDrago() {
        double centro = Camera.centroPerDue(500, 10, 396, 20, VISTA, INIZIO, FINE);
        double eroeSulloSchermo = 500 - centro + VISTA / 2.0;
        double dragoSulloSchermo = 396 - centro + VISTA / 2.0;
        assertTrue(dragoSulloSchermo - 20 >= INIZIO);
        assertTrue(eroeSulloSchermo + 10 <= FINE);
    }

    @Test
    void seSonoTroppoLontaniVinceLEroe() {
        double centro = Camera.centroPerDue(500, 10, 100, 20, VISTA, INIZIO, FINE);
        assertEquals(FINE - 10, 500 - centro + VISTA / 2.0, 1e-9);
    }

    @Test
    void nonEsceDaiBordiDellaMappa() {
        Camera camera = new Camera();
        camera.centraSu(0, 0, 320, 180, 768, 512);
        assertEquals(0, camera.x());
        assertEquals(0, camera.y());
        camera.centraSu(10_000, 10_000, 320, 180, 768, 512);
        assertEquals(768 - 320, camera.x());
        assertEquals(512 - 180, camera.y());
        camera.segui(384, 256, 320, 180, 768, 512, 10);
        assertEquals(384 - 160, camera.x());
        assertEquals(256 - 90, camera.y());
    }
}
