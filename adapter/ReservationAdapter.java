package com.example.carhire.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carhire.R;
import com.example.carhire.ReservationsActivity;
import com.example.carhire.interfaces.ItemClickListener;
import com.example.carhire.models.Reservation;

import java.util.ArrayList;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    Context context;
    ArrayList<Reservation> reservations;

    public ReservationAdapter(Context context, ArrayList<Reservation> reservations) {
        this.context = context;
        this.reservations = reservations;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notify(ArrayList<Reservation> reservations){
        this.reservations = reservations;
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
        View view = inflater.inflate(R.layout.custom_reservation_recyclerview, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageBitmap(reservations.get(position).getImage());
        holder.userId.setText(reservations.get(position).getUserId());
        holder.cost.setText(reservations.get(position).getCost());
        holder.code.setText(reservations.get(position).getCode());
        holder.date.setText(reservations.get(position).getDate());

        holder.setItemClickListener((view, position1) -> ReservationsActivity.reservationsActivity.moveToReservationDetails(reservations, position1));
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView userId, cost, code, date, time;
        ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.car_image);
            userId = itemView.findViewById(R.id.user_id);
            cost = itemView.findViewById(R.id.cost);
            code = itemView.findViewById(R.id.code);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v, getLayoutPosition());
        }
    }
}