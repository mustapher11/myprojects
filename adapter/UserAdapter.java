package com.example.carhire.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carhire.R;
import com.example.carhire.interfaces.ItemClickListener;
import com.example.carhire.models.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context context;
    ArrayList<User> users;

    public UserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notify(ArrayList<User> users){
        this.users = users;
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
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_user_recyclerview, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.userFirstName.setText(users.get(position).getFirstName().toUpperCase());
        holder.userLastName.setText(users.get(position).getLastName());
        holder.userId.setText(users.get(position).getIdNumber());
        holder.userPhone.setText(users.get(position).getPhoneNumber());

        holder.setItemClickListener((view, position1) -> {

        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView userFirstName, userLastName, userId, userPhone;
        ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userFirstName = itemView.findViewById(R.id.user_firstName);
            userLastName = itemView.findViewById(R.id.user_lastName);
            userId = itemView.findViewById(R.id.userIdNumber);
            userPhone = itemView.findViewById(R.id.userPhoneNumber);

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