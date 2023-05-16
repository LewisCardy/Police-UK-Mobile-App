package com.uk.policeapp.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.uk.policeapp.data.PoliceOfficer;


//Database for the police officers
@Database(entities = {PoliceOfficer.class}, version = 1)
public abstract class PoliceOfficerDatabase extends RoomDatabase {

    public abstract PoliceOfficerDAO policeOfficerDAO();
    private static PoliceOfficerDatabase INSTANCE;

    public static PoliceOfficerDatabase getPoliceOfficerDatabase(Context context){
        if(INSTANCE == null){
            synchronized (PoliceOfficerDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            PoliceOfficerDatabase.class,
                            "police_officer_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
