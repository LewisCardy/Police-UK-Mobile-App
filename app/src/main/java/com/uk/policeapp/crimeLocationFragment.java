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
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uk.policeapp.data.LocationCrime;
import com.uk.policeapp.databases.LocationCrimeDAO;
import com.uk.policeapp.databases.LocationCrimeDatabase;
import com.uk.policeapp.parsers.CrimeLocationParser;

import org.json.JSONException;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link crimeLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class crimeLocationFragment extends Fragment implements View.OnClickListener{
    //tag for log messages
    private static final String TAG = "crimeLocationFrag";
    //gets the variables sent in the bundle
    private static final String ARG_LONGITUDE = "longitude";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_DATE = "date";
    private static final String ARG_CATEGORY= "category";

    //variables from bundle
    private float mLongitude;
    private float mLatitude;
    private String mDate;
    private String mCategory;

    //list of crimes by location
    private List<LocationCrime> locationCrimes;
    //set the view adapter for recycler view
    private locationCrimeRecyclerViewAdapter adapter;
    //set the DAO
    private LocationCrimeDAO locationCrimeDAO;

    public crimeLocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param longitude The longitude of the location
     * @param latitude The latitude of the location
     * @param date the date
     * @param category the category selected from the spinner
     * @return A new instance of fragment crimeLocation.
     */
    public static crimeLocationFragment newInstance(float latitude, float longitude, String date, String category) {
        crimeLocationFragment fragment = new crimeLocationFragment();
        Bundle args = new Bundle();
        args.putFloat(ARG_LONGITUDE, longitude);
        args.putFloat(ARG_LATITUDE, latitude);
        args.putString(ARG_DATE, date);
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLongitude = getArguments().getFloat(ARG_LONGITUDE);
            mLatitude = getArguments().getFloat(ARG_LATITUDE);
            mDate = getArguments().getString(ARG_DATE);
            mCategory = getArguments().getString(ARG_CATEGORY);
            Log.d(TAG, mLongitude + mLatitude + mCategory + mDate);
        }
        //new list of location crimes
        locationCrimes = new ArrayList<>();

        //sets up the database with the access object
        LocationCrimeDatabase locationCrimeDatabase = LocationCrimeDatabase.getLocationCrimeDatabase(getContext());

        locationCrimeDAO = locationCrimeDatabase.locationCrimeDAO();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crime_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //creates adapter for recycler view
        adapter = new locationCrimeRecyclerViewAdapter(getContext(), locationCrimes);
        //gets the recycler view
        RecyclerView rvLocationCrimes = view.findViewById(R.id.rvCrimeLocation);
        //connects he adapter to the recycler view
        rvLocationCrimes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLocationCrimes.setAdapter(adapter);

        //button click listener
        Button btnRedownloadLocationCrimes = view.findViewById(R.id.btnRedownloadLocationCrimes);
        btnRedownloadLocationCrimes.setOnClickListener(this);

        //creates a list of cached crimes to check if the database already before downloading new crimes
        List<LocationCrime> cachedLocationCrimes = locationCrimeDAO.findBySearchValues(mDate, mLongitude, mLatitude);
        if (cachedLocationCrimes.size() > 0) {
            locationCrimes.clear();
            locationCrimes.addAll(cachedLocationCrimes);
            adapter.notifyDataSetChanged();
        } else {
            downloadLocationCrimes();
        }
    }
    @Override
    public void onClick(View view) {
        //if the re-download button is clicked delete what is there then re-download the location crimes
        if (view.getId() == R.id.btnRedownloadLocationCrimes){
            locationCrimeDAO.DeleteBySearchValues(mDate, mLongitude, mLatitude);
            downloadLocationCrimes();
        }
    }

    private void downloadLocationCrimes(){
        Uri uri = Uri.parse("https://data.police.uk/api/crimes-at-location?"+"date="+mDate+"&lat="+mLatitude+"&lng="+mLongitude);
        Uri.Builder uriBuilder = uri.buildUpon();
        uri = uriBuilder.build();

        StringRequest request = new StringRequest(
                Request.Method.GET,
                uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        locationCrimes.clear();
                        CrimeLocationParser parser = new CrimeLocationParser();

                        try {
                            //uses the parser to create a list of all crimes then add them to the main list
                            List<LocationCrime> crimes = parser.convertLocationCrimeJson(response, mDate, mLongitude, mLatitude);
                            locationCrimes.addAll(crimes);

                            //storing the crimes in the database on background thread
                            Executor executor = Executors.newSingleThreadExecutor();
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    locationCrimeDAO.insert(crimes);
                                }
                            });

                        } catch (JSONException | ParseException e) {
                            Log.d(TAG, e.getLocalizedMessage());
                            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.requestFailed), Toast.LENGTH_LONG);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, getString(R.string.requestFailed));
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.requestFailed), Toast.LENGTH_LONG).show();

            }
        });
        //makes the request
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }


}