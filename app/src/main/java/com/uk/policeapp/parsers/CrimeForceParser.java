package com.uk.policeapp.parsers;

import com.uk.policeapp.data.ForceCrime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CrimeForceParser {
    /**
     *
     * @param jsonString The JSON String returned by the crime force request
     * @return A {@link List} of {@link ForceCrime} objects parsed from the jsonString.
     * @throws JSONException if any error occurs with processing the jsonString
     * @throws ParseException if any error occurs with processing the jsonString
     */
    public List<ForceCrime> convertForceCrimeJson(String jsonString, String force) throws JSONException, ParseException{
        List<ForceCrime> forceCrimes = new ArrayList<>();

        //gets the data from the api then sets the values in ForceCrime
        try{
            JSONArray rootObj = new JSONArray(jsonString);
            for (int i=0, j=rootObj.length(); i<j; i++) {
                JSONObject crimeObj = rootObj.getJSONObject(i);
                //category of crime
                String type = crimeObj.getString("category");
                //outcome object
                JSONObject outcomeObj = crimeObj.getJSONObject("outcome_status");
                //outcome of the crime
                String outcome = outcomeObj.getString("category");
                //month crime commited
                String date = crimeObj.getString("month");
                //new force crime with the data from the api
                ForceCrime fc = new ForceCrime();
                fc.setType(type);
                fc.setOutcome(outcome);
                fc.setDate(date);
                fc.setForce(force);
                forceCrimes.add(fc);
            }

        } catch (JSONException e){
            e.printStackTrace();
            throw e;
        }
        return forceCrimes;
    }
}
