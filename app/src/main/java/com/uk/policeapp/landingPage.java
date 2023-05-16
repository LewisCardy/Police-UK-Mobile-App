package com.uk.policeapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link landingPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class landingPage extends Fragment implements  View.OnClickListener{

    public landingPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment landingPage.
     */
    public static landingPage newInstance() {
        landingPage fragment = new landingPage();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_landing_page, container, false);
        //add button listeners for all buttons on landing page
        LinearLayout crimeLocationButton = v.findViewById(R.id.llCrimeLocation);
        crimeLocationButton.setOnClickListener(this);

        LinearLayout crimeForceButton = v.findViewById(R.id.llCrimeForce);
        crimeForceButton.setOnClickListener(this);

        LinearLayout forceButton = v.findViewById(R.id.llForce);
        forceButton.setOnClickListener(this);

        LinearLayout nearestStationButton = v.findViewById(R.id.llNearestStation);
        nearestStationButton.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        //if the button is clicked navigate to relevant page
        if (v.getId() == R.id.llCrimeLocation){
            Navigation.findNavController(v).navigate(R.id.action_landingPage_to_crimeLocationInput);
        }

        if (v.getId() == R.id.llCrimeForce){
            Navigation.findNavController(v).navigate(R.id.action_landingPage_to_crimeForceInput);
        }

        if (v.getId() == R.id.llNearestStation){
            Navigation.findNavController(v).navigate(R.id.action_landingPage_to_nearestStation);
        }

        if (v.getId() == R.id.llForce){
            Navigation.findNavController(v).navigate(R.id.action_landingPage_to_crimeForce);
        }

    }
}