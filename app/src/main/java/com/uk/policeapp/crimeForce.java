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
import com.uk.policeapp.data.PoliceOfficer;
import com.uk.policeapp.databases.PoliceOfficerDAO;
import com.uk.policeapp.databases.PoliceOfficerDatabase;
import com.uk.policeapp.parsers.PoliceOfficerParser;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link crimeForce#newInstance} factory method to
 * create an instance of this fragment.
 */
public class crimeForce extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    //tag for log messages
    private static final String TAG = "CrimeForceFrag";
    //variable for the selected force from spinner
    private String force;

    //list of police officers
    private List<PoliceOfficer> policeOfficers;
    //recycler view adapter
    private policeForceOfficerRecyclerViewAdapter adapter;

    private PoliceOfficerDAO policeOfficerDAO;

    public crimeForce() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment crimeForce.
     */
    public static crimeForce newInstance() {
        crimeForce fragment = new crimeForce();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        policeOfficers = new ArrayList<>();

        //sets up the database with the access object
        PoliceOfficerDatabase policeOfficerDatabase = PoliceOfficerDatabase.getPoliceOfficerDatabase(getContext());
        policeOfficerDAO = policeOfficerDatabase.policeOfficerDAO();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crime_force, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //sets up the spinner using the string array for police forces
        Spinner spCategories = view.findViewById(R.id.spForces);
        ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(getContext(), R.array.ListOfForces, android.R.layout.simple_spinner_item);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategories.setAdapter(spAdapter);
        spCategories.setOnItemSelectedListener(this);

        //on click listeners
        Button btnGetOfficers = view.findViewById(R.id.btnGetForces);
        btnGetOfficers.setOnClickListener(this);

        Button btnRedownloadOfficers = view.findViewById(R.id.btnRedownloadOfficers);
        btnRedownloadOfficers.setOnClickListener(this);

        //sets up the recycler view to get the police officers using the adapter
        adapter = new policeForceOfficerRecyclerViewAdapter(getContext(), policeOfficers);
        RecyclerView rvCrimeForce = view.findViewById(R.id.rvCrimeForce);
        rvCrimeForce.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCrimeForce.setAdapter(adapter);
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnGetForces){
            //checks if there is data in the database if not re download data
            List<PoliceOfficer> cachedPoliceOfficers = policeOfficerDAO.findByForce(force);
            Log.d(TAG, "onClick: "+cachedPoliceOfficers);
            if (cachedPoliceOfficers.size() > 0){
                policeOfficers.clear();
                policeOfficers.addAll(cachedPoliceOfficers);
                adapter.notifyDataSetChanged();
            } else {
                downloadPoliceOfficers();
            }
            //if the re-download button is clicked delete what is there then re-download the data.
        } else if(view.getId() == R.id.btnRedownloadOfficers){
            policeOfficerDAO.deleteByForce(force);
            downloadPoliceOfficers();
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // the selected item on the spinner
        force = adapterView.getItemAtPosition(i).toString();
        Log.d(TAG, force);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //downloads the police officers from the api and stores them in the database
    private void downloadPoliceOfficers(){
        Uri uri = Uri.parse("https://data.police.uk/api/forces/"+force+"/people");
        Uri.Builder uriBuilder = uri.buildUpon();
        uri = uriBuilder.build();
        Log.d(TAG, "downloadPoliceOfficers: "+uri);

        StringRequest request = new StringRequest(
                Request.Method.GET,
                uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG, "onResponse: "+response);
                        policeOfficers.clear();
                        PoliceOfficerParser parser = new PoliceOfficerParser();
                        try{
                            List<PoliceOfficer> officers = parser.convertPoliceForceJson(response, force);
                            policeOfficers.addAll(officers);

                            //stores in database on background thread
                            Executor executor = Executors.newSingleThreadExecutor();
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    policeOfficerDAO.insert(officers);
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
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.requestFailed), Toast.LENGTH_LONG);
                Log.e(TAG, error.getLocalizedMessage());
            }
        });
        //makes the request
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }


}