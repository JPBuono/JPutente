package it.uniba.di.ivu.sms16.gruppo10.justpizza.Entity;

/**
 * Created by Luca on 04/07/2016.
 */
public class Pizzerie {

    public final int idPizzeria;
    public final String nomePizzeria;
    public final double posizionePizzeria;
    public final double votoPizzeria;

    public Pizzerie(int idPizzeria, String nomePizzeria, double posizionePizzeria, double votoPizzeria) {
        this.idPizzeria = idPizzeria;
        this.nomePizzeria = nomePizzeria;
        this.posizionePizzeria = posizionePizzeria;
        this.votoPizzeria = votoPizzeria;
    }


}
