package it.uniba.di.ivu.sms16.gruppo10.justpizza;


import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import it.uniba.di.ivu.sms16.gruppo10.justpizza.Entity.Pizzerie;
import it.uniba.di.ivu.sms16.gruppo10.justpizza.Fragment.MainFragment;
import it.uniba.di.ivu.sms16.gruppo10.justpizza.Fragment.ProgressDialogFragment;
import it.uniba.di.ivu.sms16.gruppo10.justpizza.util.ViewUtility;

/**
 * MainActivity which uses the Google Play Services
 */
public class MainActivity extends AppCompatActivity
        implements it.uniba.di.ivu.sms16.gruppo10.justpizza.util.LocationProvider  {




    //  V   A   R   I   A   B   I   L   I
    /** 1
     * Creiamo la nostra istanza di GoogleApiClient
     */
    private GoogleApiClient mGoogleApiClient;


    /**
     *The Tag for the Log
     */
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * We need this to test if the app is already resolving an error
     */
    private boolean mResolvingError = false;

    /**
     * The current Location
     *
     * The volatile keyword is used to say to the jvm "Warning, this variable may be modified
     * in an other Thread".
     */
    private volatile Location mCurrentLocation;

    /**
     * This is the request code for the resolve error Intent
     */
    private static final int REQUEST_RESOLVE_ERROR = 1001;

    /**
     * THe Request code to access location permission
     */
    private static final int REQUEST_ACCESS_LOCATION = 2;

    /**
     * The Key to use in Bundle for state
     *  Ci serve per memorizzare lo stato della variabile booleana mResolvingError in caso
     *  di kill della activity, in maniera tale che si riprenda tale stato
     */
    private static final String RESOLVING_ERROR_STATE_KEY = "RESOLVING_ERROR_STATE_KEY";


    /**
     * The Tag for the error dialog
     */
    private static final String DIALOG_ERROR_TAG = "dialog_error";

    /**
     * The duration for the expiration of the Location
     */
    private static final long LOCATION_DURATION_TIME = 5000;


    /**
     * The Max number of addresses we want from the Geocoder
     */
    private final static int MAX_GEOCODE_RESULTS = 1;






    /** 5
     * The ConnectionCallbacks implementation
     *
     * Si tratta dei metodi che verranno richiamati rispettivamente in seguito all'avvenuta connessione
     * o sospensione della connessione ai Google Play Services
     */
    private final GoogleApiClient.ConnectionCallbacks mConnectionCallbacks =
            new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    manageLocationPermission();
                }

                @Override
                public void onConnectionSuspended(int i) {
                    Log.d(TAG, "");
                }
            };


    /** 4
     * The OnConnectionFailedListener implementation
     */
    private final GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener =
            //INTERFACCIA (onConnectionFailedListener)
            new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                //Unica operazione implementata. Si occupa di gestire errori o problemi legati
                //al ciclo di vita dell'oggetto GoogleApiClient
                //N.B.: le informazioni verranno incapsulate in un oggetto ConnectionResult
                //che ci consentirà anche di correggere gli errori (in alcuni casi).
                public void onConnectionFailed(ConnectionResult connectionResult) {
                    if (mResolvingError) {
                        // Se è già in corso un'operazione di risoluzione usciamo subito (return)
                        //per non cadere in un loop
                        return;
                    } else if (connectionResult.hasResolution()) {
                        // A questo punto ci siamo accertati che non ci sono operazioni di risoluzione in corso
                        //e quindi controlliamo se il nostro oggetto connectionResult ha la soluzione al problema.
                        try {
                            // Starting resolution
                            mResolvingError = true;
                            // We launch the Intent using a request id and activity
                            connectionResult.startResolutionForResult(MainActivity.this, REQUEST_RESOLVE_ERROR);
                        } catch (IntentSender.SendIntentException e) {
                            // If we have an error during resolution we can start again.
                            mGoogleApiClient.connect();
                        }
                    } else {
                        // connectionResult non ha la soluzione al problema ma ci fornisce, tramite
                        // getErrorCode() un codice di errore che ci occuperemo di mostrare
                        // all'utente
                        showErrorDialog(connectionResult.getErrorCode());
                        // Starting resolution
                        mResolvingError = true;
                    }
                }
            };



    /**
     * We use this method to dismiss error dialog and stop resolving phase
     */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR_TAG, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), DIALOG_ERROR_TAG);
    }




    /** 4.1
     * This is the class we use to create a Dialog for the error. It's static to prevent memory leak
     */
    public static class ErrorDialogFragment extends DialogFragment {
        //Gestione della finestra di dialogo relativa all'errore dei GooglePlayServices
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR_TAG);
            //GoogleApiAvailability è un singleton che permette di accedere a diversi oggetti
            // di utilità generale come getErrorDialog che crea una Dialog per la visualizzazione
            // di errori
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MainActivity) getActivity()).onDialogDismissed();
        }
    }


    //elementi grafici
    private TextView lblLocation;
    private Button btnShowLocation
            ,btnStartLocationUpdates;


    //  F   I   N   E         V   A   R   I   A   B   I   L   I





    //          O N   C R E A T E

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /** 4.4
         * Ripristino del valore salvato di mResolvingError
         */
        if (savedInstanceState == null) {
            final MainFragment mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.anchor_point, mainFragment, MainFragment.TAG)
                    .commit();
        } else {
            mResolvingError = savedInstanceState.getBoolean(RESOLVING_ERROR_STATE_KEY, false);
        }



        //######
        /** 2
         * Inizializziamo la connessione ai Google Play services
         */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .build();



        lblLocation = (TextView) findViewById(R.id.lblLocation);


        btnStartLocationUpdates = (Button) findViewById(R.id.buttonLocationUpdates);
        btnShowLocation = (Button) findViewById(R.id.buttonShowLocation);


        btnShowLocation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if (getLocation()!=null) {

                    geoCodeLocation(getLocation());

                }else{
                    if(isGpsEnabled()==true) {
                        startLocationListener();
                        //temporizzatore e poi append


                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                geoCodeLocation(getLocation());
                            }
                        }, 4000);


                    }else{
                        showGpsAlert();
                    }
                }
            }
        });





    }  //           F I N E   O N   C R E A T E




    /**
     * 3
     */
    //Serve ad iniziare la connessione ai GoogleApiClient istanziati in precedenza (mGoogleApiClient)
    @Override
    protected void onStart() {
        super.onStart();
        //Questo if serve a capire se si arriva all'onStart mediante metodo di risoluzione per Play Services
        //o no
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
    }

    //disconnessione
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    /** 4.2
     * Risoluzione automatica dell'errore con conseguente connessione ai GPS in caso di esito positivo
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }


    /** 4.3
     * Memorizza lo stato della variabile mResolvingError in caso di kill
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(RESOLVING_ERROR_STATE_KEY, mResolvingError);
    }


    /** 5.1
     * Implementazione del metodo manageLocalPermission() che si occupa di verificare
     * i permessi relativi alla geolocalizzazione. Lanciato da onConnected()
     * Se possiamo, mostriamo una finestra di dialogo (se rationale) in cui spieghiamo perchè
     * ci serve tale permesso. Altrimenti procediaamo semplicemente con la richiesta.
     *
     * Segue il metodo onRequestPermissionResult() che si occuperà di gestire le azioni di consenso
     * o dissenso dell'utente per le Permission richieste.
     *
     * Contains the logic to access the current Location
     */
    private void manageLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // We show a message which explain the reason
                // Here we show the Dialog for the message
                new AlertDialog.Builder(this)
                        .setTitle(R.string.permission_reason_title)
                        .setMessage(R.string.permission_reason_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // We retry to acquire the permission
                                ActivityCompat.requestPermissions(
                                        MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_ACCESS_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ACCESS_LOCATION);
            }
        } else {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mCurrentLocation==null && isGpsEnabled()==false ){

                showGpsAlert();

            }else{startLocationListener();}

        }
    }


    /** 5.2
     * Gestiamo la risposta di richiesta dei permessi inviata al 5.1 in base al requestcode
     * visto che è un servizio asincrono fornito da Google
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationListener();
            } else {
                // In this case we cannot manage location so the app is not working
                new AlertDialog.Builder(this)
                        .setTitle(R.string.no_location_permission_title)
                        .setMessage(R.string.no_location_permission_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // We exit from the application
                                finish();
                            }
                        })
                        .create()
                        .show();
            }
        }
    }


    /** 6
     * Questo metodo ci permetterà di ricevere un aggiornamento MANUALE della posizione
     *
     * @return
     */
    private void updateLocation() {


        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(1)
                .setExpirationDuration(LOCATION_DURATION_TIME);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {


            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, locationRequest, new
                            LocationListener() {

                                @Override
                                public void onLocationChanged(Location location) {

                                    // We update the location with the

                                    mCurrentLocation = location;

                                    //Per far comparire in automatico la posizione senza pressione del tasto!
                                    // geoCodeLocation(location);
                                }
                            });

        }
    }


    /** 6.1
     * metodo di utilità che richiama il metodo updateLocation()
     * @return
     */
    private void startLocationListener() {
        updateLocation();
    }


    /** 7  METODO GEOCODELOCATION()
     * Metodo che controlla: 1) Se Geocoder è disponibile, 2) Toast se non è disponibile (offline o nessuna risposta)
     * 3) se la location passata al metodo è NULL
     * @return
     */
    private void geoCodeLocation(final Location location){
        if (location != null){
            if(Geocoder.isPresent()){
                //Let's start the geocode asyncronous task.
                final GeoCoderAsyncTask geoCoderAsyncTask= new GeoCoderAsyncTask(this, MAX_GEOCODE_RESULTS);
                geoCoderAsyncTask.execute(location);
            }else{ //geocoder not available
                Toast.makeText(this, R.string.my_location_geocoder_not_available, Toast.LENGTH_SHORT).show();
            }
        } else { //no location to geocode
            Log.w(TAG, "No location to geocode!");
            Toast.makeText(this, R.string.no_location_available, Toast.LENGTH_SHORT).show();
        }
    }



    private boolean isGpsEnabled(){
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        return enabled;
    }

    private void showGpsAlert(){
        new AlertDialog.Builder(this)
                .setTitle("Geolocalizzazione disattivata")
                .setMessage("Non è stato possibile trovare la tua posizione. Desideri attivare la Geolocalizzazione?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        startLocationListener();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Impossibile utilizzare i servizi di Localizzazione", Toast.LENGTH_LONG).show();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    /**
     * Utility method to update the address for the current Location
     *
     * @param addressText The address if available
     */
    private void updateDrawerHeader(final String addressText) {
        final TextView headerTextView = ViewUtility.findViewById(this, R.id.lblLocation);
        if (!TextUtils.isEmpty(addressText)) {
            headerTextView.setText(addressText);
        } else {
            headerTextView.setText(R.string.no_info);
        }
    }





    /** 8  IMPLEMENTAZIONE DELLA CLASSE GEOCODERASYNCTASK
     *
     * This is the AsyncTask that uses Geocoder to geocode an Address in Background
     */
    private static class GeoCoderAsyncTask extends AsyncTask<Location, Void, List<Address>> {

        /**
         * The Tag for the ProgressDialog
         */
        private static final String PROGRESS_TAG = "PROGRESS_TAG";

        /**
         * The Reference to the MyLocationFragment
         */
        private final WeakReference<MainActivity> mActivityRef;

        /**
         * The Max number of result in this AsyncTask
         */
        private final int mMaxResult;

        /**
         * The ProgressDialog for the GeoCoding
         */
        private ProgressDialogFragment mProgressDialog;

        /**
         * Constructor for the GeoCoderAsyncTask
         *
         * @param activity The GPSMainActivity to use later
         */
        private GeoCoderAsyncTask(final MainActivity activity, final int maxResult) {
            // We create a WeakReference to the Context
            this.mActivityRef = new WeakReference<MainActivity>(activity);
            // The max result for the geocoding
            this.mMaxResult = maxResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final MainActivity activity = mActivityRef.get();
            activity.updateDrawerHeader(activity.getString(R.string.loading_message));
        }

        @Override
        protected List<Address> doInBackground(Location... params) {
            // If the context is not available we skip
            final MainActivity activity = mActivityRef.get();
            if (activity == null) {
                Log.w(TAG, "Context is null!");
                return null;
            }
            // We have to create the Geocoder instance
            final Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
            // We get the Location to geocode
            final Location location = params[0];
            // We get the Addresses from the Location
            List<Address> geoAddresses = null;
            try {
                geoAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), mMaxResult);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error getting Addressed from Location: " + location);
            }
            return geoAddresses;
        }


        @Override
        protected void onPostExecute(List<Address> addresses) {
            super.onPostExecute(addresses);
            // If the context is not available we skip
            final MainActivity activity = mActivityRef.get();
            if (activity != null && addresses != null && addresses.size() > 0) {
                final Address address = addresses.get(0);
                // We compose the String for the GeoCoder
                final StringBuilder geoCoding = new StringBuilder();
                final int maxIndex = address.getMaxAddressLineIndex();


                /**
                 for (int i = 0; i <= maxIndex; i++) {
                 geoCoding.append(address.getAddressLine(i));
                 if (i < maxIndex) {
                 geoCoding.append(", ");
                 }
                 }*/


                geoCoding.append(address.getAddressLine(1));

                String result = geoCoding.append(address.getAddressLine(1)).toString();

                String arr[]= result.split(" ");

                activity.updateDrawerHeader(arr[0]);


                            //Volley per mandare zip al server

                            activity.sendLocation(arr[0]);


            } else {
                Log.w(TAG, "Geocode data not available");
                if (activity != null) {
                    Toast.makeText(activity, R.string.my_location_geo_coding_not_available, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    @Nullable
    @Override
    public Location getLocation() {
        return mCurrentLocation;
    }


/*-------------------------------------------------------------------------------------------------*/


    private static final String URL = "http://justpizza.000webhostapp.com/location/pizzeriaFinder.php";
    private RequestQueue requestQueue;
    private StringRequest request;



    //Inviamo Latitudine Longitudine e Zip Code al server
    private void sendLocation(String zipCode){
        final String latitudeUser= String.valueOf(mCurrentLocation.getLatitude());
        final String longitudeUser= String.valueOf(mCurrentLocation.getLongitude());
        final String zipUser = zipCode;

// dichiariamo un oggetto di tipo ConnectivityManager
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        // richiamiamo un metodo del nostro oggetto ConnectivityManager che ci da informazioni riguardo la connessione
        NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
        // in base alle informazioni ricevute dall'active netword istanziamo una variabile booleana
        boolean isConnected1 = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // inizializiamo un oggetto requestQueue con il metodo newRequestQueue
        requestQueue = Volley.newRequestQueue(this);
        // di default diamo valore nullo ai campi mail e password
        /*
        final String latitude = mCurrentLocation.getLatitude(); ?????????????????????????????????????????
        final String longitude = mCurrentLocation.getLongitude();
        final String zip = zipCode; */



        if (isConnected1)

        {

            //inizializziamo un nuovo oggetto request di tipo StringRequest con i relativi parametri e metodi annessi
            request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                public void onResponse(String response) {

                    Log.d(TAG, response.toString());
                    try {
                        // convertiamo la stringa ricevuta in un json object ,
                        JSONObject obj = new JSONObject(response);
                        if (obj.names().get(0).equals("pizzerie")){
                        // il JSONObject a sua volta viene convertito in un json array per scansionare ogni elemento ricevuto
                        JSONArray jsonArray = obj.getJSONArray("pizzerie");

                            int i;

                        for (i=0; i < jsonArray.length(); i++) {

                            // creiamo un oggetto JSONObject per ogni pizza dal quale andremo a prendere i valori
                            JSONObject pizzeria = jsonArray.getJSONObject(i);
                            // prendiamo i valori ricevuti dal JSON e li salviamo come variabili locali

                            String nomePizzeria = pizzeria.getString("name");
                            int id = pizzeria.getInt("id");
                            Double rate = pizzeria.getDouble("rate");
                            Double distance = pizzeria.getDouble("distance");
                            //istanziamo un oggetto di tipo Pizzeria con le variabili ricevute dal server
                            Pizzerie temp = new Pizzerie(id,nomePizzeria,distance,rate);
                        }
                            // stampa grazie ai plurals quante pizzerie sono state trovate
                            Toast.makeText(getApplicationContext(),getResources().getQuantityString(R.plurals
                                    .numero_pizzerie,i), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), obj.getString("errore"), Toast.LENGTH_SHORT).show();
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
                // con i parametri zipUser , latitudeUser e longitude user
                // rilevati tramite la posizione dell'utente
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("zipUser", zipUser);
                    hashMap.put("latitudeUser", latitudeUser);
                    hashMap.put("longitudeUser", longitudeUser);

                    return hashMap;
                }
            };
            requestQueue.add(request);
        } else {
            // se la connessione è assente stampiamo un messaggio di errore
            Toast.makeText(getApplicationContext(), "Impossibile connettersi alla rete", Toast.LENGTH_SHORT).show();
        }

    }
}

