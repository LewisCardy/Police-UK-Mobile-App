package com.uk.policeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        BottomNavigationView bnv = findViewById(R.id.bottomNavigationView);
        bnv.setOnItemSelectedListener(this);
        return true;
    }

    @Override

    public boolean onNavigationItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(findViewById(R.id.conNavHost));
        int currentFragmentId = navController.getCurrentDestination().getId();

        if(item.getItemId() == R.id.miBottomNavAbout){
            if (currentFragmentId != R.id.aboutFragment){
                navController.navigate(R.id.aboutFragment);
            }
            return true;

        } else if (item.getItemId() == R.id.miBottomNavHome){
            if (currentFragmentId != R.id.landingPage){
                navController.navigate(R.id.landingPage);
            }
            return true;
        }
        return false;
    }
}