package com.uk.policeapp.databases;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.uk.policeapp.data.ForceCrime;

//database for crimes by police force
@Database(entities = {ForceCrime.class}, version = 1)
public abstract class ForceCrimeDatabase extends RoomDatabase {

    public abstract ForceCrimeDAO forceCrimeDAO();

    private static ForceCrimeDatabase INSTANCE;

    public static ForceCrimeDatabase getForceCrimeDatabase(Context context){
        if(INSTANCE == null){
            synchronized (ForceCrimeDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            ForceCrimeDatabase.class,
                            "force_crime_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
