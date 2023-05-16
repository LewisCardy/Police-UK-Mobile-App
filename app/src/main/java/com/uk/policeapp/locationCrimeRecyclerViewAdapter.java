package com.uk.policeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uk.policeapp.data.LocationCrime;

import java.util.List;

public class locationCrimeRecyclerViewAdapter extends RecyclerView.Adapter<locationCrimeRecyclerViewAdapter.LocationCrimeViewHolder> {

    private final Context context;
    private final List<LocationCrime> locationCrimes;

    locationCrimeRecyclerViewAdapter(Context context, List<LocationCrime> locationCrimes){
        super();
        this.context = context;
        this.locationCrimes = locationCrimes;
    }
    @NonNull
    @Override
    public LocationCrimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout for the crimes at location
        View view = LayoutInflater.from(this.context).inflate(R.layout.crime_location_list_item, parent, false);
        LocationCrimeViewHolder viewHolder = new LocationCrimeViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocationCrimeViewHolder holder, int position) {
        //get the crimes at location to display
        LocationCrime locationCrime = this.locationCrimes.get(position);

        //update view from holder with the crimes
        View itemView = holder.itemView;

        //update the text views with the details for each crime
        ((TextView)itemView.findViewById(R.id.tvCrimeLocationCategory))
                .setText(locationCrime.getType());

        ((TextView)itemView.findViewById(R.id.tvCrimeLocationOutcome))
                .setText(locationCrime.getOutcome());

        ((TextView)itemView.findViewById(R.id.tvCrimeLocationStreetName))
                .setText(locationCrime.getLocation());

        ((TextView)itemView.findViewById(R.id.tvCrimeLocationLatitude))
                .setText(locationCrime.getLatitude().toString());

        ((TextView)itemView.findViewById(R.id.tvCrimeLocationLongitude))
                .setText(locationCrime.getLongitude().toString());
    }


    @Override
    public int getItemCount() {
        return this.locationCrimes.size();
    }

    class LocationCrimeViewHolder extends RecyclerView.ViewHolder{

        public LocationCrimeViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
