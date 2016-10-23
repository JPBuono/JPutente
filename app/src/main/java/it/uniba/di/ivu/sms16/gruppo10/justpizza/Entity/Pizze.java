package it.uniba.di.ivu.sms16.gruppo10.justpizza.Entity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luca on 21/06/2016.
 */
public final class Pizze implements Parcelable {


    //Flag per valori nelle variabili presenti o assenti
    private static final byte PRESENT = 1;
    private static final byte NOT_PRESENT = 0;


    /**
     * legge i dati parcellizzati ricevuti e , le informazioni parcellizzate, vengono salvate nel
     * dato
     *
     * @param in
     */
    private Pizze(Parcel in) {
        id = in.readInt();
        nome = in.readString();
        boolean present = in.readByte() == PRESENT;
        if (present) {
            descrizione = in.readString();
        } else {
            descrizione = null;
        }
        present = in.readByte() == PRESENT;
        if (present) {
            prezzo = in.readDouble();
            prezzoGigante = in.readDouble();
        } else {
            prezzo = 0;
            prezzoGigante = 0;
        }
        gigante = in.readByte() != 0;
        glutine = in.readByte() != 0;
    }

    /**
     * Contiene due costruttori che utilizza come parametro l'oggetto di tipo Parcel da cui leggere
     */
    public static final Creator<Pizze> CREATOR = new Creator<Pizze>() {
        @Override
        public Pizze createFromParcel(Parcel in) {
            return new Pizze(in);
        }

        @Override
        public Pizze[] newArray(int size) {
            return new Pizze[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * in caso di valori obbligatori, la parcellizzazione avviene obbligatoriamente.
     * In caso di valori che possono anche non esserci, nel caso ci siano avviene la parcellizzazione,
     * ed inviata una costante che ne attesta la presenza,altrimenti viene inviato un byte che ne attesta
     * l'assenza
     *
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nome);
        if (descrizione != null) {
            dest.writeByte(PRESENT);
            dest.writeString(descrizione);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        if (prezzo != 0) {
            dest.writeByte(PRESENT);
            dest.writeDouble(prezzo);
            dest.writeDouble(prezzoGigante);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        if (gigante == true) {
            dest.writeByte(PRESENT);
            dest.writeByte((byte) 1);
        } else {
            dest.writeByte(NOT_PRESENT);
            dest.writeByte((byte) 0);
        }
        if (glutine == true) {
            dest.writeByte(PRESENT);
            dest.writeByte((byte) 1);
        } else {
            dest.writeByte(NOT_PRESENT);
            dest.writeByte((byte) 0);
        }


    }


    public final int id;
    public final String nome;
    public final String descrizione;
    public final double prezzo;
    public final double prezzoGigante;
    public final boolean gigante;
    public final boolean glutine;


    public Pizze(int id, String nome, String descrizione, double prezzo, double prezzoGigante, boolean gigante, boolean glutine) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.prezzoGigante = prezzoGigante;
        this.gigante = gigante;
        this.glutine = glutine;
    }


    public static class Builder {
        private int mId;
        private String mNome;
        private String mDescrizione;
        private double mPrezzo;
        private double mPrezzoGigante;
        private boolean mGigante;
        private boolean mGlutine;

        private Builder(final String nome) {;
            this.mNome = nome;
        }


        public static Builder create(final String nome) {
            return new Builder(nome);
        }

        public Builder withDescription(final String descrizione) {
            this.mDescrizione = descrizione;
            return this;
        }

        public Builder withPrices(double prezzo, double prezzoGigante) {
            this.mPrezzo = prezzo;
            this.mPrezzoGigante = prezzoGigante;
            return this;
        }

        public Builder isGigante(boolean gigante) {
            this.mGigante = gigante;
            return this;
        }

        public Builder isGlutenFree(boolean glutine) {
            this.mGlutine = glutine;
            return this;
        }

        public Pizze build() {
            return new Pizze(mId, mNome, mDescrizione, mPrezzo, mPrezzoGigante, mGigante, mGlutine);
        }

    }

    public interface Keys {
        String ID = "id";
        String NOME = "nome";
        String DESCRIZIONE = "descrizione";
        String PREZZO = "prezzo";
        String PREZZOGIGANTE = "prezzoGigante";
        String ISGIGANTE = "isGigante";
        String ISGLUTENFREE = "isGlutenFree";
    }

    public void toIntent(final Intent intent) {
        intent.putExtra(Keys.ID, id);
        intent.putExtra(Keys.NOME, nome);
        intent.putExtra(Keys.DESCRIZIONE, descrizione);
        intent.putExtra(Keys.PREZZO, prezzo);
        intent.putExtra(Keys.PREZZOGIGANTE, prezzoGigante);
        intent.putExtra(Keys.ISGIGANTE, gigante);
        intent.putExtra(Keys.ISGLUTENFREE, glutine);

    }

    public static Pizze fromIntent(final Intent inputIntent) {
        final int id = inputIntent.getIntExtra(Keys.ID, -1);
        final String nome = inputIntent.getStringExtra(Keys.NOME);
        final String descrizione = inputIntent.getStringExtra(Keys.DESCRIZIONE);
        final double prezzo = inputIntent.getDoubleExtra(Keys.PREZZO, -1);
        final double prezzoGigante = inputIntent.getDoubleExtra(Keys.PREZZOGIGANTE, -1);
        final boolean isGigante = inputIntent.getBooleanExtra(Keys.ISGIGANTE, false);
        final boolean isGlutenFree = inputIntent.getBooleanExtra(Keys.ISGLUTENFREE, false);
        final Pizze pizza = new Pizze(id,nome,descrizione,prezzo,prezzoGigante,isGigante,isGlutenFree);
        return pizza;
    }


}