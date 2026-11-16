package it.unicam.cs.mpgc.rpg132025.controllore;

import it.unicam.cs.mpgc.rpg132025.piattaforma.Piattaforma;
import it.unicam.cs.mpgc.rpg132025.servizio.ServizioGioco;
import it.unicam.cs.mpgc.rpg132025.vista.grafica.Grafica;

import java.util.Objects;

/**
 * Le dipendenze condivise da tutti i controllori, passate dall'esterno.
 *
 * @param servizio    operazioni sulle partite e sulle statistiche (il Modello)
 * @param controllori navigazione tra le schermate
 * @param grafica     risorse grafiche con cui creare le viste
 * @param piattaforma servizi della piattaforma (per esempio uscire)
 */
public record ContestoApplicazione(ServizioGioco servizio, GestoreControllori controllori, Grafica grafica,
                                   Piattaforma piattaforma) {

    public ContestoApplicazione {
        Objects.requireNonNull(servizio, "servizio");
        Objects.requireNonNull(controllori, "controllori");
        Objects.requireNonNull(grafica, "grafica");
        Objects.requireNonNull(piattaforma, "piattaforma");
    }
}
