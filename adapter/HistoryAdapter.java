package com.example.carhire.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carhire.History;
import com.example.carhire.R;
import com.example.carhire.interfaces.ItemClickListener;
import com.example.carhire.models.HiringHistory;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    Context context;
    ArrayList<HiringHistory> hiringHistoryArrayList;

    public HistoryAdapter(Context context, ArrayList<HiringHistory> hiringHistoryArrayList) {
        this.context = context;
        this.hiringHistoryArrayList = hiringHistoryArrayList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notify(ArrayList<HiringHistory> hiringHistoryArrayList){
        this.hiringHistoryArrayList = hiringHistoryArrayList;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_history_recyclerview, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        holder.id.setText("Id: #" + hiringHistoryArrayList.get(position).getId());
        holder.status.setText("Status: " +hiringHistoryArrayList.get(position).getStatus());
        holder.time.setText(hiringHistoryArrayList.get(position).getTime());

        holder.setItemClickListener((view, position1) -> History.history.moveToHistoryDetails(hiringHistoryArrayList, position));
    }

    @Override
    public int getItemCount() {
        return hiringHistoryArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView id, status, time;
        ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id);
            status = itemView.findViewById(R.id.status);
            time = itemView.findViewById(R.id.date);

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