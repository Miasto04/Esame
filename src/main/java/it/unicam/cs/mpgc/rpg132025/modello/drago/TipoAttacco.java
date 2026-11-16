package it.unicam.cs.mpgc.rpg132025.modello.drago;

/**
 * Gli attacchi del drago, con il danno inflitto all'eroe (in mezzi cuori).
 */
public enum TipoAttacco {
    MORSO("Morso", 3),
    SOFFIO_DI_FUOCO("Fiammata", 3),
    COLPO_DI_CODA("Colpo di coda", 2),
    ATTERRAGGIO("Schianto", 3);

    private final String nome;
    private final int danno;

    TipoAttacco(String nome, int danno) {
        this.nome = nome;
        this.danno = danno;
    }

    /** @return nome leggibile dell'attacco */
    public String nome() {
        return nome;
    }

    /** @return mezzi cuori tolti all'eroe */
    public int danno() {
        return danno;
    }
}
