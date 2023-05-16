package com.uk.policeapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link nearestStation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class nearestStation extends Fragment implements View.OnClickListener {
    //Tag for the log message
    private static final String TAG = "NearestStationFragment";

    private ActivityResultLauncher<String[]> mLocationPermissionRequest;
    private Boolean mFineLocationGranted = null;
    private Boolean mCoarseLocationGranted = null;
    private FusedLocationProviderClient mFusedLocationClient;

    //to save the users location for use within the url
    private Double mLatitude;
    private Double mLongitude;

    //to store the selected force and neighbourhood the user searched for use to locate nearest station
    private String mForce;
    private String mNeighbourhood;

    public nearestStation() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment nearestStation.
     */
    public static nearestStation newInstance() {
        nearestStation fragment = new nearestStation();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        //registers for a location check to see if permission is given to use location services
        registerForLocationPermissionCheck();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearest_station, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Button listeners
        Button btnGetGps = view.findViewById(R.id.btnGetGpsStation);
        btnGetGps.setOnClickListener(this);

        Button btnSearch = view.findViewById(R.id.btnSearchStation);
        btnSearch.setOnClickListener(this);

        //if permission is not granted ask for it
        if(checkIfLocationGranted()){ } else { requestLocationPermission(); }
    }

    public void onClick(View v) {
        //when the get gps button is clicked get the longitude and latitud for location
        if (v.getId() == R.id.btnGetGpsStation) {
            if(checkIfLocationGranted()){
                getLocation();
            } else {
                requestLocationPermission();
            }
            //if the search station button is clicked search for the nearest station
        } else if (v.getId() == R.id.btnSearchStation){
            getNearestStation();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationPermissionRequest.unregister();
    }

    private void registerForLocationPermissionCheck() {
        mLocationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mFineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        mCoarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    } else {
                        mFineLocationGranted = (ContextCompat.checkSelfPermission(
                                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED);
                        mCoarseLocationGranted = (ContextCompat.checkSelfPermission(
                                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED);
                    }
                        });
    }

    private boolean checkIfLocationGranted(){
        if (mFineLocationGranted != null && mFineLocationGranted) {
            // Precise location access granted.
            Log.d(TAG, "Fine location granted");
            return true;
        } else if (mCoarseLocationGranted != null && mCoarseLocationGranted) {
            // Only approximate location access granted.
            Log.d(TAG, "Course location granted");
            return true;
        } else {
            // No location access granted.
            Log.d(TAG, "no location granted");
            return false;
        }
    }

    private void requestLocationPermission() {
        mLocationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        //use google play to get users location
        GoogleApiAvailability gaa = new GoogleApiAvailability();
        if (ConnectionResult.SUCCESS == gaa.isGooglePlayServicesAvailable(getContext())) {
            Executor executer = Executors.newSingleThreadExecutor();
            mFusedLocationClient.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(executer, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                Log.d(TAG, "Google Play Location " + location.getLatitude() + location.getLongitude());
                                mLatitude = location.getLatitude();
                                mLongitude = location.getLongitude();
                                getNeighbourhood();
                            } else {
                                Log.d(TAG, "Google play did not return a location");

                            }
                        }
                    });
        } else {
            //use location manager to get location if google play is unavailable
            LocationManager locationManager = (LocationManager)getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gpsEnabled){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    locationManager.getCurrentLocation(LocationManager.GPS_PROVIDER, null,
                            Executors.newSingleThreadExecutor(),
                            new Consumer<Location>() {
                                @Override
                                public void accept(Location location) {
                                    if (location != null) {
                                        // Logic to handle location object
                                        mLatitude = location.getLatitude();
                                        mLongitude = location.getLongitude();
                                        getNeighbourhood();
                                        Log.d(TAG, "LocationManager Location " + location.getLatitude() + ", " + location.getLongitude());
                                    } else {
                                        Log.d(TAG, "LocationManager did not return a GPS location ");
                                    }
                                }
                            });
                }
            } else {
                Log.d(TAG, "Location not found");
            }
        }
    }

    //gets the neighbourhood id and force name for use to search for nearest station
    public void getNeighbourhood() {
        Uri uri = Uri.parse("https://data.police.uk/api/locate-neighbourhood");
        Uri.Builder uriBuilder = uri.buildUpon();
        //uses the longitude and latitude from the users location services to search
        uriBuilder.appendQueryParameter("q", mLatitude+","+mLongitude);
        // create the final URL
        uri = uriBuilder.build();

        // volley request
        StringRequest request = new StringRequest(
                Request.Method.GET,
                uri.toString(),
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);

                        try {
                            JSONObject rootObject = new JSONObject(response);
                            mForce = rootObject.getString("force");
                            mNeighbourhood = rootObject.getString("neighbourhood");

                            //updates text views
                            TextView tvForce = getView().findViewById(R.id.tvForce);
                            tvForce.setText(mForce);
                            TextView tvNeighbourhood = getView().findViewById(R.id.tvNeighbourhood);
                            tvNeighbourhood.setText(mNeighbourhood);


                        } catch (JSONException e) {
                            Log.d(TAG, e.getLocalizedMessage());

                            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.requestFailed), Toast.LENGTH_LONG);
                        }
                        
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

    //once the neighbourhood and force are found based on the location another request is made to get the nearest station from the api
    public void getNearestStation(){
        Uri uri = Uri.parse("https://data.police.uk/api/"+mForce+"/"+mNeighbourhood);
        Uri.Builder uriBuilder = uri.buildUpon();
        uri = uriBuilder.build();

        // uses Volley to make the request
        StringRequest request = new StringRequest(
                Request.Method.GET,
                uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        try {

                            JSONObject rootObject = new JSONObject(response);
                            String website = rootObject.getString("url_force");
                            String name = rootObject.getString("name");
                            JSONObject contactDetails = rootObject.getJSONObject("contact_details");
                            String email = null;
                            if (contactDetails.has("email")){
                                email = contactDetails.getString("email");
                            }



                            JSONArray locations = rootObject.getJSONArray("locations");
                            //defaults these values to none
                            //sometimes the api does not have all of these values for each search
                            String address = null;
                            String postcode = null;
                            String type = null;
                            if (locations.length() != 0) {
                                JSONObject topLocation = locations.getJSONObject(0);
                                 address = topLocation.getString("address");
                                 postcode = topLocation.getString("postcode");
                                 type = topLocation.getString("type");
                            }
                            //error message
                            String error = "Not Found";
                            //finds the text vies to be updated
                            TextView tvWebsite = getView().findViewById(R.id.tvWebsite);
                            TextView tvName = getView().findViewById(R.id.tvName);
                            TextView tvEmail = getView().findViewById(R.id.tvEmail);
                            TextView tvAddress = getView().findViewById(R.id.tvAddress);
                            TextView tvPostcode = getView().findViewById(R.id.tvPostcode);
                            TextView tvType = getView().findViewById(R.id.tvType);

                            //if these values are not returned from the api then set the text to the error message
                            if (website != null) { tvWebsite.setText(website); } else { tvWebsite.setText(error);}
                            if (name != null) {tvName.setText(name);} else {tvName.setText(error);}
                            if (email != null) {tvEmail.setText(email);} else {tvEmail.setText(error);}
                            if (address != null) {tvAddress.setText(address);} else {tvAddress.setText(error);}
                            if (postcode != null) {tvPostcode.setText(postcode);} else {tvPostcode.setText(error);}
                            if (type != null) {tvType.setText(type);} else {tvType.setText(error);}
                        } catch (JSONException e) {
                            Log.d(TAG, e.getLocalizedMessage());
                            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.requestFailed), Toast.LENGTH_LONG);
                        }
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
