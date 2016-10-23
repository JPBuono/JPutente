package it.uniba.di.ivu.sms16.gruppo10.justpizza.Fragment;

/**
 * Created by Utente on 31/08/2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.uniba.di.ivu.sms16.gruppo10.justpizza.MainActivity;
import it.uniba.di.ivu.sms16.gruppo10.justpizza.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    /**
     * The Tag for this Fragment
     */
    public static final String TAG = MainFragment.class.getSimpleName();

    private MainActivity mActivity;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}

