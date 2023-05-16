package com.uk.policeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uk.policeapp.data.PoliceOfficer;

import java.util.List;

public class policeForceOfficerRecyclerViewAdapter extends RecyclerView.Adapter<policeForceOfficerRecyclerViewAdapter.PoliceOfficerViewHolder> {

    private final Context context;
    private final List<PoliceOfficer> policeOfficers;

    policeForceOfficerRecyclerViewAdapter(Context context, List<PoliceOfficer> policeOfficers){
        super();
        this.context = context;
        this.policeOfficers = policeOfficers;
    }
    @NonNull
    @Override
    public PoliceOfficerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout for the crimes at location
        View view = LayoutInflater.from(this.context).inflate(R.layout.police_force_list_item, parent, false);
        PoliceOfficerViewHolder viewHolder = new PoliceOfficerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PoliceOfficerViewHolder holder, int position) {
        //get the police officers to display
        PoliceOfficer policeOfficer = this.policeOfficers.get(position);

        //update view from holder with the crimes
        View itemView = holder.itemView;

        //update the text views to show details for each police officer
        ((TextView)itemView.findViewById(R.id.tvPersonName))
                .setText(policeOfficer.getName());

        ((TextView)itemView.findViewById(R.id.tvRank))
                .setText(policeOfficer.getRank());

    }


    @Override
    public int getItemCount() {
        return this.policeOfficers.size();
    }

    class PoliceOfficerViewHolder extends RecyclerView.ViewHolder{

        public PoliceOfficerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
