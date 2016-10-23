package it.uniba.di.ivu.sms16.gruppo10.justpizza;

import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
// dal 2013 google ha introdotto la libreria volley , che costituisce un'alternativa alle classi HttpUrlConnection.
// grazie a questa libreria si evita l'utilizzo dell'AsyncTask in background per evitare il freeze della gui ,
// inoltre la volley  gestisce automaticamente le richieste http
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

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText emailForm, passwordForm ;
    private Button loginButton;
    private CheckBox checkBox;
    private static final String URL = "http://justpizza.000webhostapp.com/utenteCheck/UserCheck.php";
    private RequestQueue requestQueue;
    private StringRequest request;
    // utilizziamo la classe DB_Controller creata da noi  che è in grado di utilizzare tutti i metodi di scrittura
    // e cancellazione del db che ci servono per gestire il login automatico
    // abbiamo voluto implementare la funzione di login automatico sia per la sua efficienza ,
    // sia per provare ad usare anche il DB locale con SQLite e non utilizzare solo un DB su sever
    public DB_Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // dichiariamo un oggetto di tipo ConnectivityManager
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        // richiamiamo un metodo del nostro oggetto ConnectivityManager che ci da informazioni riguardo la connessione
        NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
        // in base alle informazioni ricevute dall'active netword istanziamo una variabile booleana
        boolean isConnected1 = activeNetwork != null && activeNetwork.isConnectedOrConnecting();


        //inizializziamo un oggetto DB_Controller
        controller = new DB_Controller(getApplicationContext(), "", null, 1);

        // inizializiamo un oggetto requestQueue con il metodo newRequestQueue
        requestQueue = Volley.newRequestQueue(this);
        // di default diamo valore nullo ai campi mail e password
        String mailB = null;
        String passwordB = null;

        // effettuiamo un controllo sul database , se username e password sono già stati salvati , la connessione avviene in automatico
        // se l'utente non si riesce a connettersi 2 casi : 1)non vi è connessione i dati vengono cancellati ,+
        // i dati salvati non corrsipondono con quelli sul server i dati sul db locale vengono cancellati

        // un oggetto di tipo cursor permette l'accesso alle tabelle del dbLocale
        Cursor loginData;
        // popolizamo l'oggetto di tipo cursor con i dati presenti nel DB
        loginData = controller.checkDB();
        // abbiamo inserito il while per evitare un'errore di null point exception ,
        // in ogni caso per come abbiamo strutturato l'applicazione ,la tabella del DB conterrà una sola riga
        while (loginData.moveToNext()) {
            // assegnamo alle variabili mailB e passwordB gli eventuali valori salvati nel DB
            mailB = loginData.getString(0);
            passwordB = loginData.getString(1);
        }

        // abbiamo dovuto inizializzare delle variabili final con i valori di mailB e passwordB , in quanto andando avanti nel codice
        // il metododo hasmap.put ci richiedeva di passargli dei valori final appunto
        final String mailBack = mailB;
        final String passwordBack = passwordB;

        if (mailBack != null && passwordBack != null) {

            // prima di fare una richiesta al server , controlliamo che sia attiva la connessione
            if (isConnected1)

            {

                //inizializziamo un nuovo oggetto request di tipo StringRequest con i relativi parametri e metodi annessi
                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //se la prima stringa che ci restituisce il file JSON è "loginEffettuato",
                            // il login sarà avvenuto con successo e quindi passiamo  alla schermata successiva
                            // alla quale verrà passata la mail inserita dall'utente
                            if (jsonObject.names().get(0).equals("loginEffettuato")) {

                                // creo un jsonArray dal jsonObject ricevuto
                                JSONArray jsonArray = jsonObject.getJSONArray("loginEffettuato");
                                // prendo la posizione ( che solitamente è i , in questo caso mi serve solo la prima posizione quindi prendo 0)
                                // e lo metto all'interno di un jsonObject
                                JSONObject utente = jsonArray.getJSONObject(0);
                                // prendo il valore id all'interno del jsonObject n°0
                                String idUtente = utente.getString("idUtente");

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                // passiamo all'activity ClientMainActivity la mail inserita dall'utente
                                //  intent.putExtra("mail", emailForm.getText().toString());
                                // passiamo anche l'id dell'account che ha fatto il login
                                // intent.putExtra("idPizzeria",idPizzeria);
                                // se l'utente spunta la checkBox salviamo i dati

                                // passo la chiave id alle alla successiva activity ,
                                // in modo da identificare qualsiasi azione faccia all'interno del db

                                intent.putExtra("id",idUtente);


                                // passiamo all'activity ClientMainActivity la mail inserita dall'utente così da mostrargli un toast all'accesso
                                //intent.putExtra("mail", mailBack);

                                Toast.makeText(getApplicationContext(), "Benvenuto " + mailBack, Toast.LENGTH_LONG).show();
                                startActivity(intent);
                                // chiudiamo l'activity loggin
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("errore"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })


                {
                    @Override
                    // implementiamo il metodo getParams che  inizializza un oggetto HashMap
                    // col quale formuliamo la richiesta di post
                    // con i parametri email e password inseriti dall'utente che vengono inoltrati alla pagina php
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("emailUtente", mailBack);
                        hashMap.put("passwordUtente", passwordBack);

                        return hashMap;
                    }
                };


                requestQueue.add(request);
            } else {
                // se la connessione è assente stampiamo un messaggio di errore
                Toast.makeText(getApplicationContext(), "Impossibile connettersi alla rete", Toast.LENGTH_SHORT).show();

            }// qui termina l'inizializzazione di request


        }
        // se il login automatico è andato a buon fine  grazie al metodo finish() che abbiamo inserito subito dopo l'intent
        // il compilatore non arriverà nemmeno a questa porzione di codice.
        //Altrimenti in caso di logout automatico fallito a causa della mancanza di connessione , potremo procedere al login manuale

        // istanziamo il layout con i relativi bottoni ed EditText
        setContentView(R.layout.activity_login);
        emailForm = (EditText) findViewById(R.id.EmailForm);
        passwordForm = (EditText) findViewById(R.id.PasswordForm);
        loginButton = (Button) findViewById(R.id.loginButton);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //inseriamo nuovamente il controllo della connessione in quanto dall'avvio dell'applicazione al momento di
                // "on click" potrebbe essera cambiata la connessione ad internet
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                // questa volta username e password li prendiamo dagli input inseriti dall'utente negli editText
                final String username = emailForm.getText().toString();
                final String password = passwordForm.getText().toString();

                if (username.equals("") && password.equals("")) {
                    Toast.makeText(getApplicationContext(), "Inserisci Username e Password ", Toast.LENGTH_SHORT).show();
                    // emailForm.setBackgroundColor(Color.parseColor("#00ff00"));
                } else if (emailForm.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "Username mancante", Toast.LENGTH_SHORT).show();
                else if (passwordForm.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "Password mancante", Toast.LENGTH_SHORT).show();
                else if (isConnected) {
                    //inizializziamo un nuovo oggetto request di tipo StringRequest con i relativi parametri e metodi annessi
                    request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        public void onResponse(String response) {
                            try {
                                // creiamo un nuovo oggetto JSON dalla risposta del server
                                JSONObject jsonObject = new JSONObject(response);
                                //se la prima stringa che ci restituisce il file JSON è "loginEffettuato",
                                // il login sarà avvenuto con successo e quindi passiamo  alla schermata successiva
                                // alla quale verrà passata la mail inserita dall'utente
                                if (jsonObject.names().get(0).equals("loginEffettuato")) {

                                    // creo un jsonArray dal jsonObject ricevuto
                                    JSONArray jsonArray = jsonObject.getJSONArray("loginEffettuato");
                                    // prendo la posizione ( che solitamente è i , in questo caso mi serve solo la prima posizione quindi prendo 0)
                                    // e lo metto all'interno di un jsonObject
                                    JSONObject utente = jsonArray.getJSONObject(0);
                                    // prendo il valore id all'interno del jsonObject n°0
                                    String idUtente = utente.getString("idUtente");

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    // passiamo all'activity ClientMainActivity la mail inserita dall'utente
                                    //  intent.putExtra("mail", emailForm.getText().toString());
                                    // passiamo anche l'id dell'account che ha fatto il login
                                    // intent.putExtra("idPizzeria",idPizzeria);
                                    // se l'utente spunta la checkBox salviamo i dati

                                    // passo la chiave id alle alla successiva activity ,
                                    // in modo da identificare qualsiasi azione faccia all'interno del db

                                   intent.putExtra("id",idUtente);
                                    if (checkBox.isChecked()){
                                        controller.insertUser(username, password);
                                    } else controller.dropUser();
                                    //  per sicurezza cancelliamo eventuali credenziali di login salvate sul DB
                                    // in modo che se tentassimo di connetterci dopo che ci è strato mostrato all'avvio della connessione l'errore di rete ,
                                    // magari nonostante non salviamo i nuovi dati con i quali stiamo facendo il login , nel db potrebbero essere ancora presenti delle vecchie credenziali ,
                                    // e quindi una volta chiusa e riaperta l'app potremmo effettuare l'accesso con un altro account che aveva fatto il login in precedenza

                                    startActivity(intent);
                                    // terminiamo l'activity login , se l'utente dopo essersi loggato vorrà tornare a questa schermata dovrà fare il logout ,
                                    // altrimenti con il tasto back chiude l'applicazione
                                    finish();
                                } else {
                                    // se entrambi i campi password e mail sono stati inseriti , ma generano un erore ,l'errore viene gestito in php
                                    // e ci restituirà la stringa con la relativa motivazione che verrà mostrata all'utente attraverso un toast
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("errore"), Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    })


                    {
                        @Override
                        // implementiamo il metodo getParams che  inizializza un oggetto HashMap
                        // col quale formuliamo la richiesta di post
                        // con i parametri email e password inseriti dall'utente da inoltrare alla pagina php
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("emailUtente", emailForm.getText().toString());
                            hashMap.put("passwordUtente", passwordForm.getText().toString());

                            return hashMap;
                        }
                    };


                    requestQueue.add(request);
                } else {
                    Toast.makeText(getApplicationContext(), "Impossibile connettersi alla rete", Toast.LENGTH_SHORT).show();
                }// qui termina l'inizializzazione di request

            }
        }); // qui termina on click
    }
}



