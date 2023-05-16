package com.uk.policeapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link crimeLocationInput#newInstance} factory method to
 * create an instance of this fragment.
 */
public class crimeLocationInput extends Fragment implements View.OnClickListener , AdapterView.OnItemSelectedListener {
    //tag used for log
    private static final String TAG = "CrimeLocationInputFrag";

    //category of crime selected from spinner
    private String category;
    //for the location services
    private ActivityResultLauncher<String[]> mLocationPermissionRequest;
    private Boolean mFineLocationGranted = null;
    private Boolean mCoarseLocationGranted = null;
    private FusedLocationProviderClient mFusedLocationClient;

    //users location from location services
    private float latitude = 0f;
    private float longitude = 0f;

    private String date = "";


    public crimeLocationInput() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment crimeLocationInput.
     */
    public static crimeLocationInput newInstance() {
        crimeLocationInput fragment = new crimeLocationInput();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        //register for location services
        registerForLocationPermissionCheck();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_crime_location_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //update the spinner with the string array for the crime categories
        Spinner spCategories = view.findViewById(R.id.spCrimeType);
        ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(getContext(), R.array.listOfCrimecategories, android.R.layout.simple_spinner_item);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategories.setAdapter(spAdapter);
        spCategories.setOnItemSelectedListener(this);

        //button click listeners
        Button getCrimes = view.findViewById(R.id.btnSearchLocationCrime);
        getCrimes.setOnClickListener(this);

        Button getGps = view.findViewById(R.id.btnGetGpsCrimeLocation);
        getGps.setOnClickListener(this);

        //checks if location is granted if not ask again
        if(checkIfLocationGranted()){ } else { requestLocationPermission(); }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSearchLocationCrime){
            EditText etDate = getView().findViewById(R.id.etDate);
            date = etDate.getText().toString();

            EditText etLatitude = getView().findViewById(R.id.etLatitude);
            EditText etLongitude = getView().findViewById(R.id.etLongitude);

            if (String.valueOf(etLatitude.getText()).isEmpty()){} else {latitude = Float.parseFloat(String.valueOf(etLatitude.getText()));}
            if (String.valueOf(etLongitude.getText()).isEmpty()){} else {longitude = Float.parseFloat(String.valueOf(etLongitude.getText()));}


            if (latitude == 0f || longitude == 0f || date.equals("")){
                Toast.makeText(getContext().getApplicationContext(), "Please enter data into the boxes", Toast.LENGTH_LONG).show();
            } else {

                //creates a new bundle to be sent to the crimeLocationFragment
                Bundle args = new Bundle();
                args.putFloat("latitude", latitude);
                args.putFloat("longitude", longitude);
                args.putString("date", date);
                args.putString("category", category);
                //navigates to crimeLocationFragment with the bundle
                Navigation.findNavController(v).navigate(R.id.action_crimeLocationInput_to_crimeLocation, args);
            }
            //gets the values the user entered into the edit texts



            
        } else if (v.getId() == R.id.btnGetGpsCrimeLocation){
            if(checkIfLocationGranted()){
                getLocation();

                //handler used for a 5 second delay before updating edit texts with the location received from the gps button
                //this is because it takes about 5 seconds for google play services to retrieve location.
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EditText etLongitude = getView().findViewById(R.id.etLongitude);
                        etLongitude.setText(String.valueOf(longitude));

                        EditText etLatitude = getView().findViewById(R.id.etLatitude);
                        etLatitude.setText(String.valueOf(latitude));
                    }
                }, 5000);

            } else {
                requestLocationPermission();
            }
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
        //uses google play services to get location
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
                                latitude = (float) location.getLatitude();
                                longitude = (float) location.getLongitude();

                                //EditText etLongitude = getView().findViewById(R.id.etLongitude);
                               // etLongitude.setText(String.valueOf(longitude));

                                //EditText etLatitude = getView().findViewById(R.id.etLatitude);
                                //etLatitude.setText(String.valueOf(latitude));
                            } else {
                                Log.d(TAG, "Google play did not return a location");

                            }
                        }
                    });
        } else {
            //uses location manager to get location
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
                                        latitude = (float) location.getLatitude();
                                        longitude = (float) location.getLongitude();

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
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //when the spinner is changed change the category variable
        category = adapterView.getItemAtPosition(i).toString();
        Log.d(TAG, category);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}