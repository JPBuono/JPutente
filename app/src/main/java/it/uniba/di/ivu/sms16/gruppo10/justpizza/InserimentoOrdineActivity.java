package it.uniba.di.ivu.sms16.gruppo10.justpizza;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.widget.ViewUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.uniba.di.ivu.sms16.gruppo10.justpizza.Entity.Pizze;
import it.uniba.di.ivu.sms16.gruppo10.justpizza.util.ViewUtility;

/**
 * Created by Luca on 06/07/2016.
 */
public class InserimentoOrdineActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    List<Pizze> listaPizze=new ArrayList<Pizze>();



    int idPizzeria;

    private final static String TAG = InserimentoOrdineActivity.class.getSimpleName();

    List<String> tipiOggetto=new ArrayList<String>();
    Spinner spinnerTipoOggetto;
    List<String> nomiPizza=new ArrayList<String>();
    Spinner spinnerPizza;
    private RequestQueue requestQueue;
   // come abbiamo fatto per l'app lato pizzeria ho inserito tutte le pizze all'interno del List<Pizze> mModel
    //riprendendo anche il costruttore Pizze che c'era già nel app lato pizzeria perchè il costruttore presente su questa app
    //conteneva parametri errati più in basso c'è una spazio vuoto ( riga 134 )  dove puoi utilizzare i dati che ti ho preso negli
    // adapter che hai creato
    // p.s il valore che prende idPizzeria è -1 quindi ho messo un valore temporaneo a idPizzeria al rigo 73
    private static final String URL = "http://justpizza.000webhostapp.com/pizzeCheck/PizzeCheck.php";
    private List<Pizze> mModel = new LinkedList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_ordine);
        spinnerTipoOggetto=ViewUtility.findViewById(this,R.id.tipo_oggetto_spinner);
        spinnerPizza=ViewUtility.findViewById(this,R.id.pizza_spinner);
        spinnerPizza.setVisibility(View.GONE);

        Intent intent=getIntent();
        // così prende id pizzeria -1 quindi ho messo sotto commento
        //idPizzeria=intent.getIntExtra("idPizzeria",-1);
        idPizzeria=3;
        tipiOggetto.add("Scegli oggetto");
        tipiOggetto.add("Pizza");
        ArrayAdapter<String> tipoOggettoAdapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,tipiOggetto);
        tipoOggettoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoOggetto.setAdapter(tipoOggettoAdapter);
        spinnerTipoOggetto.setOnItemSelectedListener(this);


        nomiPizza.add("margherita");
        nomiPizza.add("capricciosa");
        ArrayAdapter<String> pizzaAdapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,nomiPizza);
        pizzaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPizza.setAdapter(pizzaAdapter);
        spinnerPizza.setOnItemSelectedListener(this);


        requestQueue = Volley.newRequestQueue(this);


        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    // convertiamo la stringa ricevuta in un json object ,
                    JSONObject obj = new JSONObject(response);
                    // il JSONObject a sua volta viene convertito in un json array per scansionare ogni elemento ricevuto
                    JSONArray jsonArray = obj.getJSONArray("pizze");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        // creiamo un oggetto JSONObject per ogni pizza dal quale andremo a prendere i valori
                        JSONObject pizza = jsonArray.getJSONObject(i);

                        // prendiamo i valori ricevuti dal JSON e li salviamo come stringhe


                        int id = pizza.getInt("idPizza");
                        String nomePizza = pizza.getString("nomePizza");
                        double Prezzo = pizza.getDouble("Prezzo");
                        boolean Glutine = pizza.getBoolean("Glutine");
                        //String NoGlutine=pizza.getString("NoGlutine");
                        //String SovrapprezzoNoGlutine=pizza.getString("SovrapprezzoNoGlutine");
                        boolean Gigante = pizza.getBoolean("Gigante");
                        double prezzoGigante = pizza.getDouble("SovraprezzoGigante");
                        //String GiganteNoGlutine=pizza.getString("GiganteNoGlutine");
                        //String SovrapprezzoGiganteNoGlutine=pizza.getString("SovrapprezzoGiganteNoGlutine");
                        String Ingredienti = pizza.getString("Ingredienti");


                        // inseriamo i valori all'interno di un oggetto temporaneo di tipo pizza
                        Pizze temp = new Pizze(id, nomePizza, Ingredienti, Prezzo, prezzoGigante, Gigante, Glutine);


                        // agguiungiamo il nostro oggetto " pizza temporanea " , all'interno dell'arraylist di pizze
                        mModel.add(temp);

                    }


                    // luca qua puoi aggiungere il codice che ti serve ( credo l'adapter )






                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", "ERROR");

            }
        })


        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("idPizzeria",String.valueOf(idPizzeria)); // getString(idPizzeria)
                return hashMap;
            }
        };


        requestQueue.add(request);




    }





    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       Spinner spinner= (Spinner) parent;

        if(spinner.getId()==R.id.tipo_oggetto_spinner){
            if(spinner.getItemAtPosition(position).toString().equalsIgnoreCase("Scegli oggetto")){
                spinnerPizza.setVisibility(View.GONE);
            }
            if(spinner.getItemAtPosition(position).toString().equalsIgnoreCase("Pizza")){
                spinnerPizza.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
