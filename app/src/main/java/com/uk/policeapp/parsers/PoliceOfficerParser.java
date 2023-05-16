package com.uk.policeapp.parsers;

import com.uk.policeapp.data.PoliceOfficer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class PoliceOfficerParser {
    /**
     *
     * @param jsonString The JSON String returned by the police officer request
     * @return A {@link List} of {@link PoliceOfficer} objects parsed from the jsonString.
     * @throws JSONException if any error occurs with processing the jsonString
     * @throws ParseException if any error occurs with processing the jsonString
     */

    public List<PoliceOfficer> convertPoliceForceJson(String jsonString, String force) throws JSONException, ParseException{
        List<PoliceOfficer> policeOfficers = new ArrayList<PoliceOfficer>();
        //gets the data returned from the api and puts it into the corresponding values within PoliceOfficer
        try{
            JSONArray rootObj = new JSONArray(jsonString);
            for (int i = 0, j=rootObj.length();i<j;i++){
                JSONObject officerObj = rootObj.getJSONObject(i);
                //name of officer
                String name = officerObj.getString("name");
                //rank of officer
                String rank = officerObj.getString("rank");

                //new police officer with the name and rank
                PoliceOfficer po = new PoliceOfficer();
                po.setName(name);
                po.setRank(rank);
                po.setForce(force);

                policeOfficers.add(po);
            }
        } catch (JSONException e){
            e.printStackTrace();
            throw e;
        }
        return policeOfficers;
    }
}
