package it.uniba.di.ivu.sms16.gruppo10.justpizza.Entity;

import android.content.Intent;

/**
 * Created by Luca on 24/06/2016.
 */


public class Ordini {

    public final int idOrdine;
    public final int idUtente;
    public final boolean statOrdine;

    public final int idPizza ;
    public final String nomePizza;
    public final double Prezzo ;
    public final int QtaOrdinata;
    public final String data;



    public Ordini(int idOrdine, int idUtente,boolean statOrdine, int idPizza,
                  String nomePizza, double Prezzo,int QtaOrdinata, String data ) {
        this.idOrdine = idOrdine;
        this.idUtente = idUtente;
        this.statOrdine =statOrdine;
        this.idPizza =idPizza;
        this.nomePizza = nomePizza;
        this.Prezzo = Prezzo;
        this.QtaOrdinata = QtaOrdinata;
        this.data = data ;

    }

    public static class Builder{
        private int mIdOrdine;
        private int mIdUtente;
        private boolean mstatOrdine;

        int midPizza ;
        String mnomePizza;
        double mPrezzo ;
        int mQtaOrdinata;
        String mdata;


        private Builder(final int idOrdine,final  int idUtente,final boolean statOrdine,
                        final  int idPizza,final  String nomePizza,final  double Prezzo, final int QtaOrdinata,final  String data){

            this.mIdOrdine=idOrdine;
            this.mIdUtente=idUtente;
            this.mstatOrdine =statOrdine;
            this.midPizza =idPizza;
            this.mnomePizza = nomePizza;
            this.mPrezzo = Prezzo;
            this.mQtaOrdinata = QtaOrdinata;
            this.mdata = data ;
        }
        public static Builder create(int idOrdine, int idUtente,boolean statOrdine,
                                     int idPizza, String nomePizza, double Prezzo,int QtaOrdinata, String data){
            return new Builder(idOrdine,idUtente,statOrdine,idPizza,nomePizza,Prezzo,QtaOrdinata,data);
        }


        public Ordini build(){
            return new Ordini(mIdOrdine,mIdUtente,mstatOrdine,midPizza,mnomePizza,mPrezzo,mQtaOrdinata,mdata);
        }
    }

    public interface Keys{
        String ID_ORDINE="idOrdine";
        String ID_UTENTE="idUtente";
        String STAT_ORDINE = "statOrdine";
        String ID_PIZZA = "idPizza";
        String NOME_PIZZA = "nomePizza";
        String PREZZO = "Prezzo";
        String QTA_ORDINATA = "QtaOrdinata";
        String DATA = "Data";
    }

    public void toIntent(final Intent intent) {
        intent.putExtra(Keys.ID_ORDINE, idOrdine);
        intent.putExtra(Keys.ID_UTENTE, idUtente);
        intent.putExtra(Keys.STAT_ORDINE, statOrdine);
        intent.putExtra(Keys.ID_PIZZA ,idPizza );
        intent.putExtra(Keys.NOME_PIZZA , nomePizza );
        intent.putExtra(Keys.PREZZO ,Prezzo );
        intent.putExtra(Keys.QTA_ORDINATA ,QtaOrdinata );
        intent.putExtra(Keys.DATA , data );
    }
 // luca controlla questa cosa
    public static Ordini fromIntent(final Intent inputIntent){
        boolean stat = false;
        final int idOrdine=inputIntent.getIntExtra(Keys.ID_ORDINE,-1);
        final int idUtente=inputIntent.getIntExtra(Keys.ID_UTENTE,-1);
        final boolean statOrdine=inputIntent.getBooleanExtra(Keys.STAT_ORDINE,stat);;
        final int idPizza = inputIntent.getIntExtra(Keys.ID_PIZZA,-1) ;
        final String nomePizza = inputIntent.getStringExtra(Keys.ID_ORDINE);
        final double Prezzo = inputIntent.getIntExtra(Keys.PREZZO,-1);;
        final int QtaOrdinata=inputIntent.getIntExtra(Keys.QTA_ORDINATA,-1);;
        final String data= inputIntent.getStringExtra(Keys.DATA);

        final Ordini ordini= Builder.create(idOrdine,idUtente,statOrdine,idPizza,nomePizza,Prezzo,QtaOrdinata,data)
               .build();


        return ordini;
    }
}
