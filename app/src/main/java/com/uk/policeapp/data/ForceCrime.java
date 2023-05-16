package com.uk.policeapp.data;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//table and class for the crime by force search
@Entity(tableName = "ForceCrime")
public class ForceCrime {
    //type of the crime
    private String type;
    //date of the crime
    private String date;
    //outcome
    private String outcome;
    //police forced used in search
    private String force;

    //random number for primary key
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int uid;

    //getters and setters for the variables
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getForce() {
        return force;
    }

    public void setForce(String force) {
        this.force = force;
    }

}
