package com.uk.policeapp.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


//table and class for the crime search by location
@Entity(tableName = "LocationCrime")
public class LocationCrime {

    //type of crime
    private String type;
    //outcome of the crime
    private String outcome;
    //street name for the crime
    private String location;
    //longitude
    private Double longitude;
    //latitude
    private Double latitude;
    //date crime was committed
    private String date;
    //longitude and latitude used in the search
    private Float searchedLongitude;
    private Float searchedLatitude;

    //random number for the primary key
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int uid;

    //getters and setters for the variables
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public LocationCrime() {
        super();
    }

    public String getType(){
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Float getSearchedLongitude() {
        return searchedLongitude;
    }

    public void setSearchedLongitude(Float searchedLongitude) {
        this.searchedLongitude = searchedLongitude;
    }

    public Float getSearchedLatitude() {
        return searchedLatitude;
    }

    public void setSearchedLatitude(Float searchedLatitude) {
        this.searchedLatitude = searchedLatitude;
    }

    @Override
    public String toString() {
        return "LocationCrime{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", outcome='" + outcome +
                ", location'" + location +
                ", type" + type + '\'' +
                '}';
    }
}
