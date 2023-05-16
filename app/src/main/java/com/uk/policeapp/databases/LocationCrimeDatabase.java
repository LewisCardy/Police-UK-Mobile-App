package com.uk.policeapp.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.uk.policeapp.data.LocationCrime;

//database for the crimes by loaction
@Database(entities = {LocationCrime.class}, version = 4)
public abstract class LocationCrimeDatabase extends RoomDatabase {

    public abstract LocationCrimeDAO locationCrimeDAO();

    private static LocationCrimeDatabase INSTANCE;


    public static LocationCrimeDatabase getLocationCrimeDatabase(Context context){
        if(INSTANCE == null){
            synchronized (LocationCrimeDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            LocationCrimeDatabase.class,
                            "location_crime_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
