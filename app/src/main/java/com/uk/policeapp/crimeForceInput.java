package com.uk.policeapp;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uk.policeapp.data.ForceCrime;
import com.uk.policeapp.databases.ForceCrimeDAO;
import com.uk.policeapp.databases.ForceCrimeDatabase;
import com.uk.policeapp.parsers.CrimeForceParser;

import org.json.JSONException;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link crimeForceInput#newInstance} factory method to
 * create an instance of this fragment.
 */
public class crimeForceInput extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    //tag for log messages
    private static final String TAG = "CrimeForceInputFrag";
    //force selected by user
    private String force;

    //new list of crimes by force
    private List<ForceCrime> forceCrimes;
    //adapter for recycler view
    private policeForceCrimeRecyclerViewAdapter adapter;

    //dao for database
    private ForceCrimeDAO forceCrimeDAO;


    public crimeForceInput() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment crimeForceInput.
     */
    public static crimeForceInput newInstance() {
        crimeForceInput fragment = new crimeForceInput();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        forceCrimes = new ArrayList<>();
        //gets the database and sets the access object
        ForceCrimeDatabase forceCrimeDatabase = ForceCrimeDatabase.getForceCrimeDatabase(getContext());
        forceCrimeDAO = forceCrimeDatabase.forceCrimeDAO();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crime_force_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //sets up the spinner with the list of police forces form the string array
        Spinner spCategories = view.findViewById(R.id.spForceCrime);
        ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(getContext(), R.array.ListOfForces, android.R.layout.simple_spinner_item);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategories.setAdapter(spAdapter);
        spCategories.setOnItemSelectedListener(this);

        //the button click listeners
        Button btnGetCrimes = view.findViewById(R.id.btnGetCrimeForce);
        btnGetCrimes.setOnClickListener(this);

        Button btnRedownloadForceCrimes = view.findViewById(R.id.btnRedownloadForceCrimes);
        btnRedownloadForceCrimes.setOnClickListener(this);

        //gets the adapter and sets up the recycler view with it
        adapter = new policeForceCrimeRecyclerViewAdapter(getContext(), forceCrimes);
        RecyclerView rvForceCrime = view.findViewById(R.id.rvCrimeForceInput);

        rvForceCrime.setLayoutManager(new LinearLayoutManager(getContext()));
        rvForceCrime.setAdapter(adapter);



    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnGetCrimeForce){
            //checks to see if there are already crimes in database and if there isn't download them.
            List<ForceCrime> cachedForceCrimes = forceCrimeDAO.findByForce(force);
            Log.d(TAG, "onClick: "+cachedForceCrimes);
            if (cachedForceCrimes.size() > 0){
                forceCrimes.clear();
                forceCrimes.addAll(cachedForceCrimes);
                adapter.notifyDataSetChanged();
            } else {
                downloadForceCrimes();
            }
            //if the re-download button is clicked delete what is already there then perform the request again
        } else if (view.getId() == R.id.btnRedownloadOfficers){
            forceCrimeDAO.deleteByForce(force);
            downloadForceCrimes();
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        force = adapterView.getItemAtPosition(i).toString();
        Log.d(TAG, force);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //downloads the crimes based on the selected police force
    private void downloadForceCrimes(){
        Uri uri = Uri.parse("https://data.police.uk/api/crimes-no-location?category=all-crime&force="+force);
        Uri.Builder uriBuilder = uri.buildUpon();
        uri = uriBuilder.build();
        Log.d(TAG, "downloadForceCrimes: "+uri);

        StringRequest request = new StringRequest(
                Request.Method.GET,
                uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: "+response);
                        forceCrimes.clear();
                        CrimeForceParser parser = new CrimeForceParser();
                        try {
                            //uses parser to create the list of crimes by police force
                            List<ForceCrime> crimes = parser.convertForceCrimeJson(response, force);
                            forceCrimes.addAll(crimes);

                            //adds item to database on background thread
                            Executor executor = Executors.newSingleThreadExecutor();
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    forceCrimeDAO.insert(crimes);
                                }
                            });
                        } catch (JSONException | ParseException e){
                            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.requestFailed), Toast.LENGTH_LONG);
                            Log.e(TAG, e.getLocalizedMessage());
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //makes the request
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

}