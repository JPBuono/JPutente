package it.uniba.di.ivu.sms16.gruppo10.justpizza;

import android.os.Bundle;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import it.uniba.di.ivu.sms16.gruppo10.justpizza.Fragment.FragmentListaPizzerie;
import it.uniba.di.ivu.sms16.gruppo10.justpizza.util.ViewUtility;

/**
 * Created by Luca on 04/07/2016.
 */
public class UtenteActivity extends AppCompatActivity {

    private Toolbar toolbar;
    FrameLayout frameLayout;
    FragmentListaPizzerie fragmentListaPizzerie=new FragmentListaPizzerie();
    FragmentTransaction transaction;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utente);
        toolbar= ViewUtility.findViewById(this,R.id.app_bar);
        setSupportActionBar(toolbar);
        frameLayout=ViewUtility.findViewById(this,R.id.fragment_container);
        transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,fragmentListaPizzerie,"ListaPizzeria");
        transaction.commit();
    }


}
