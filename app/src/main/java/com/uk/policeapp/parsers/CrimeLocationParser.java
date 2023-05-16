package com.uk.policeapp.parsers;

import com.uk.policeapp.data.LocationCrime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CrimeLocationParser {
    /**
     *
     * @param jsonString The JSON String returned by the crime at location request
     * @return A {@link List} of {@link LocationCrime} objects parsed from the jsonString.
     * @throws JSONException if any error occurs with processing the jsonString
     * @throws ParseException if any error occurs with processing the jsonString
     */

    public List<LocationCrime> convertLocationCrimeJson(String jsonString, String date, Float longitude, Float latitude) throws JSONException, ParseException{

        List<LocationCrime> locationCrimes = new ArrayList<LocationCrime>();

        //gets the data from the api then updates the values in the class
        try{
            JSONArray rootObj = new JSONArray(jsonString);
            for (int i = 0, j=rootObj.length(); i<j; i++){
                //object for each crime
                JSONObject crimeObj = rootObj.getJSONObject(i);
                //type of crime
                String type = crimeObj.getString("category");

                //location object
                JSONObject locationObj = crimeObj.getJSONObject("location");
                //latitude
                Double crimeLatitude = locationObj.getDouble("latitude");
                //longitude
                Double crimeLongitude = locationObj.getDouble("longitude");
                //street object
                JSONObject streetObj = locationObj.getJSONObject("street");
                //street name
                String name = streetObj.getString("name");
                //outcome object
                JSONObject outcomeObj = crimeObj.getJSONObject("outcome_status");
                //outcome of the crime
                String outcome = outcomeObj.getString("category");

                //creates a new LocationCrime with all of the values from api
                LocationCrime lc = new LocationCrime();
                lc.setLatitude(crimeLatitude);
                lc.setLongitude(crimeLongitude);
                lc.setType(type);
                lc.setOutcome(outcome);
                lc.setLocation(name);
                lc.setDate(date);
                lc.setSearchedLatitude(latitude);
                lc.setSearchedLongitude(longitude);
                locationCrimes.add(lc);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            throw e;
        }
        return locationCrimes;
    }
}
