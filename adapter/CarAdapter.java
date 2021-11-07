package com.example.carhire.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carhire.CarsActivity;
import com.example.carhire.Hire;
import com.example.carhire.R;
import com.example.carhire.interfaces.ItemClickListener;
import com.example.carhire.models.Car;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {
    ArrayList<Car> carArrayList;
    Context context;
    RadioButton update, delete;

    public CarAdapter(Context context, ArrayList<Car> cars){
        this.carArrayList = cars;
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notify(ArrayList<Car> updatedCarArrayList){
        this.carArrayList = updatedCarArrayList;
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
        View convertView = inflater.inflate(R.layout.custom_list_view, parent, false);
        return new ViewHolder(convertView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //bind values to your views
        ViewHolder.car_name.setText(carArrayList.get(position).getCar_name().toUpperCase());
        ViewHolder.car_model.setText(carArrayList.get(position).getCar_model());
        ViewHolder.car_number.setText(carArrayList.get(position).getCar_number());
        ViewHolder.hiring_cost.setText(carArrayList.get(position).getHiring_cost());
        ViewHolder.imageView.setImageBitmap(carArrayList.get(position).getBitmap());

        holder.setItemClickListener((view, position1) -> {
            if (context == CarsActivity.carsActivity){
                manageCar(carArrayList.get(position1).getCar_name(), carArrayList.get(position1).getCar_model(), position1);

            }else {
                Hire.hireActivity.assignDriver(getImageString(position1), carArrayList.get(position1).getCar_name(), carArrayList.get(position1).getCar_model(), carArrayList.get(position1).getCar_number(), carArrayList.get(position1).getHiring_cost());
            }
        });
    }

    @Override
    public int getItemCount() {
        return carArrayList.size();
    }

    @SuppressWarnings({"FieldMayBeFinal", "InnerClassMayBeStatic"})
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @SuppressLint("StaticFieldLeak")
        private static TextView car_name, car_model, car_number, hiring_cost;
        @SuppressLint("StaticFieldLeak")
        private static ImageView imageView;
        ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            car_name = itemView.findViewById(R.id.car_name);
            car_model = itemView.findViewById(R.id.car_model);
            car_number = itemView.findViewById(R.id.car_number);
            hiring_cost = itemView.findViewById(R.id.hiring_cost);
            imageView = itemView.findViewById(R.id.custom_list_image);

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

    public void manageCar(String car_name, String car_model, int position){
        View view = LayoutInflater.from(context).inflate(R.layout.manage, null);
        update = view.findViewById(R.id.update);
        delete = view.findViewById(R.id.delete);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Manage Car").setView(view)
                .setMessage("What do you want to do to " + car_name + " " + car_model + "?")
                .setPositiveButton("Ok", (dialog, which) -> {
                    if (update.isChecked()){
                        CarsActivity.carsActivity.updateCar(car_name, car_model, carArrayList, position);

                    }else {
                        CarsActivity.carsActivity.confirmation(car_name, car_model, carArrayList.get(position).getCar_number());
                    }

                })
                .setNegativeButton("cancel", null)
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public String getImageString(int position){
        byte[] imageBytes = new byte[0];

        Bitmap bitmap = carArrayList.get(position).getBitmap();

        if (bitmap != null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            imageBytes = byteArrayOutputStream.toByteArray();
        }

        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}