package com.example.carhire.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carhire.DriversActivity;
import com.example.carhire.R;
import com.example.carhire.interfaces.ItemClickListener;
import com.example.carhire.models.Driver;

import java.util.ArrayList;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.ViewHolder> {

    Context context;
    ArrayList<Driver> driverArrayList;

    public DriverAdapter(Context context, ArrayList<Driver> driverArrayList) {
        this.context = context;
        this.driverArrayList = driverArrayList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notify(ArrayList<Driver> driverArrayList){
        this.driverArrayList = driverArrayList;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View convertView = inflater.inflate(R.layout.custom_driver_recyclerview, parent, false);

        return new ViewHolder(convertView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.driverFirstName.setText("First Name: " + driverArrayList.get(position).getFirstName().toUpperCase());
        holder.driverLastName.setText("Last Name: " + driverArrayList.get(position).getLastName());
        holder.driverIdNumber.setText("ID Number: " + driverArrayList.get(position).getIdNumber());
        holder.driverPhoneNumber.setText("Phone Number: " + driverArrayList.get(position).getPhoneNumber());

        holder.setItemClickListener((view, position1) -> DriversActivity.driversActivity.manageDriver(driverArrayList.get(position).getFirstName(), driverArrayList.get(position).getLastName(), driverArrayList.get(position).getIdNumber()));
    }

    @Override
    public int getItemCount() {
        return driverArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView driverFirstName, driverLastName, driverIdNumber, driverPhoneNumber;
        ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            driverFirstName = itemView.findViewById(R.id.driver_firstName);
            driverLastName = itemView.findViewById(R.id.driver_lastName);
            driverIdNumber = itemView.findViewById(R.id.driverIdNumber);
            driverPhoneNumber = itemView.findViewById(R.id.driverPhoneNumber);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v, getLayoutPosition());
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }
    }

}