package com.uk.policeapp.data;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


//table and class for the police officer search
@Entity(tableName = "PoliceOfficer")
public class PoliceOfficer {
    //name of officer
    private String name;
    //rank of officer
    private String rank;
    //force (Used for the database search)
    private String force;

    //primary key random number
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int uid;

    //getters and setters for the variables
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getForce() {
        return force;
    }

    public void setForce(String force) {
        this.force = force;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }


}
