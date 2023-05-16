package com.uk.policeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uk.policeapp.data.ForceCrime;

import java.util.List;

public class policeForceCrimeRecyclerViewAdapter extends RecyclerView.Adapter<policeForceCrimeRecyclerViewAdapter.CrimeForceViewHolder> {

    private final Context context;
    private final List<ForceCrime> forceCrimes;

    policeForceCrimeRecyclerViewAdapter(Context context, List<ForceCrime> forceCrimes){
        super();
        this.context = context;
        this.forceCrimes = forceCrimes;
    }



    @NonNull
    @Override
    public CrimeForceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.crime_force_list_item, parent, false);
        CrimeForceViewHolder viewHolder = new CrimeForceViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CrimeForceViewHolder holder, int position) {
        //get the crimes by force to display and update the view with details of forceCrime
        ForceCrime forceCrime = this.forceCrimes.get(position);

        View itemView = holder.itemView;

        //update the text views with information for each crime.
        ((TextView)itemView.findViewById(R.id.tvCrimeForceType))
                .setText(forceCrime.getType());
        ((TextView)itemView.findViewById(R.id.tvCrimeForceOutcome))
                .setText(forceCrime.getOutcome());
        ((TextView)itemView.findViewById(R.id.tvCrimeForceDate))
                .setText(forceCrime.getDate());
    }

    @Override
    public int getItemCount() {
        return this.forceCrimes.size();
    }
    class CrimeForceViewHolder extends RecyclerView.ViewHolder{

        public CrimeForceViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
