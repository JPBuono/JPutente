package it.uniba.di.ivu.sms16.gruppo10.justpizza.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.uniba.di.ivu.sms16.gruppo10.justpizza.Entity.Pizze;
import it.uniba.di.ivu.sms16.gruppo10.justpizza.Entity.Pizzerie;
import it.uniba.di.ivu.sms16.gruppo10.justpizza.InserimentoOrdineActivity;
import it.uniba.di.ivu.sms16.gruppo10.justpizza.R;
import it.uniba.di.ivu.sms16.gruppo10.justpizza.util.ViewUtility;

/**
 * Created by Luca on 04/07/2016.
 */
public class FragmentListaPizzerie extends Fragment {



    private final static String TAG = FragmentListaPizzerie.class.getSimpleName();
    public final static class PizzerieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private WeakReference<onItemClickListener> mOnItemClickListener;


        public interface onItemClickListener {
            void onItemClicked(int position);
        }

        public void setOnIntemClickListener(final onItemClickListener onIntemClickListener) {
            this.mOnItemClickListener = new WeakReference<onItemClickListener>(onIntemClickListener);
        }

        private TextView nomePizzeria;
        private TextView distanzaPizzeria;
        private RatingBar ratingPizzeria;

        public PizzerieViewHolder(View itemView) {
            super(itemView);
            nomePizzeria = ViewUtility.findViewById(itemView, R.id.nomePizzeria);
            distanzaPizzeria = ViewUtility.findViewById(itemView, R.id.distanzaPizzeriaTextView);
            ratingPizzeria = ViewUtility.findViewById(itemView, R.id.ratinBarPizzeria);
            itemView.setOnClickListener(this);

        }

        public void bind(Pizzerie pizzeria) {
            nomePizzeria.setText(pizzeria.nomePizzeria);
            distanzaPizzeria.setText(String.valueOf(pizzeria.posizionePizzeria));
            ratingPizzeria.setRating((float) pizzeria.votoPizzeria);

        }

        @Override
        public void onClick(View v) {
            onItemClickListener listener;
            if (mOnItemClickListener != null && (listener = mOnItemClickListener.get()) != null) {
                listener.onItemClicked(getLayoutPosition());
            }
        }

    }

    public final static class PizzerieAdapter extends RecyclerView.Adapter<PizzerieViewHolder> implements PizzerieViewHolder.onItemClickListener {

        private WeakReference<OnOrdiniListener> mOnOrdiniListener;

        public interface OnOrdiniListener {
            void onPizzeriaClicked(Pizzerie pizzerie, int position);
        }

        public void setmOnOrdiniListener(final OnOrdiniListener onOrdiniListener) {
            this.mOnOrdiniListener = new WeakReference<OnOrdiniListener>(onOrdiniListener);
        }

        private final List<Pizzerie> mModel;

        public PizzerieAdapter(List<Pizzerie> mModel) {

            this.mModel = mModel;
        }


        @Override
        public PizzerieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_pizzerie_row, parent, false);
            return new PizzerieViewHolder(layout);
        }

        @Override
        public void onBindViewHolder(PizzerieViewHolder holder, int position) {
            holder.bind(mModel.get(position));
            holder.setOnIntemClickListener(this);
        }

        @Override
        public int getItemCount() {
            return mModel.size();
        }

        @Override
        public void onItemClicked(int position) {
            OnOrdiniListener listener;
            if (mOnOrdiniListener != null &&
                    (listener = mOnOrdiniListener.get()) != null) {
                listener.onPizzeriaClicked(mModel.get(position), position);
            }
        }
    }


    private PizzerieAdapter adapter;

    private List<Pizzerie> mModel = new LinkedList<>();

    private RecyclerView mRecyclerView;
    private RequestQueue requestQueue;
    String idUtente ;
    private static final String URL = " http://justpizza.000webhostapp.com/pizzeriaCheck/listaPizzerieLatoUtente.php";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // idUtente = getArguments().getString("idUtente");
        View layout = inflater.inflate(R.layout.lista_pizzerie_fragment, container, false);
        mRecyclerView = ViewUtility.findViewById(layout, R.id.lista_pizzerie);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        //mModel = popolaModel(mModel);
       /* mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PizzerieAdapter(mModel);
        mRecyclerView.setAdapter(adapter);
        adapter.setmOnOrdiniListener(new PizzerieAdapter.OnOrdiniListener() {
            @Override
            public void onPizzeriaClicked(Pizzerie pizzerie, int position) {
                Intent intent=new Intent(getContext(), InserimentoOrdineActivity.class);
                intent.putExtra("IdPizzeria",pizzerie.idPizzeria);
                startActivity(intent);

            }
        });
        return layout;
    }*/

        // appena torna dichiara requestQueueeu , url e tag
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());


        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    // convertiamo la stringa ricevuta in un json object ,
                    JSONObject obj = new JSONObject(response);
                    // il JSONObject a sua volta viene convertito in un json array per scansionare ogni elemento ricevuto
                    JSONArray jsonArray = obj.getJSONArray("Pizzerie");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        // creiamo un oggetto JSONObject per ogni pizza dal quale andremo a prendere i valori
                        JSONObject pizzeria = jsonArray.getJSONObject(i);

                        // prendiamo i valori ricevuti dal JSON e li salviamo come stringhe

                        int idPizzeria = pizzeria.getInt("idPizzeria");
                        String nomePizzeria = pizzeria.getString("nomePizzeria");
                        double posizionePizzeria = pizzeria.getDouble("posizionePizzeria");
                        double votoPizzeria = pizzeria.getDouble("votoPizzeria");

                        // inseriamo i valori all'interno di un oggetto temporaneo di tipo pizza
                        Pizzerie temp = new Pizzerie(idPizzeria, nomePizzeria, posizionePizzeria, votoPizzeria);

                        // agguiungiamo il nostro oggetto " pizza temporanea " , all'interno dell'arraylist di pizze
                        mModel.add(temp);

                    }

                   /* una volta presi tutti i dati possiamo chiamare l'adapter con il realtivo fragment con tutti i dati completi */
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    adapter = new PizzerieAdapter(mModel);
                    mRecyclerView.setAdapter(adapter);
                    adapter.setmOnOrdiniListener(new PizzerieAdapter.OnOrdiniListener() {
                        @Override
                        public void onPizzeriaClicked(Pizzerie pizzerie, int position) {
                            Intent intent=new Intent(getContext(), InserimentoOrdineActivity.class);
                            intent.putExtra("IdPizzeria",pizzerie.idPizzeria);
                            startActivity(intent);

                        }
                    });



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
                // nell'hasMap passeremo la nostra posizione probabilmente
                //hashMap.put("idUtente",idUtente);
                return hashMap;
            }
        };


        requestQueue.add(request);


        return layout;
    }


    static List<Pizzerie> popolaModel(List<Pizzerie> model) {
        Pizzerie pizzeria1 = new Pizzerie(1, "Giamaica", 3, 3);
        model.add(pizzeria1);
        Pizzerie pizzeria2 = new Pizzerie(2, "Giamaica2", 3, 3);
        model.add(pizzeria2);
        return model;

    }
}

