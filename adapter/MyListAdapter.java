package com.example.carhire.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.carhire.R;
import com.example.carhire.models.Car;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class MyListAdapter extends ArrayAdapter<String> {

    Context context;
    List cars;
    List<List> car_details;
    ArrayList<Car> carArrayList = new ArrayList<>();

    public MyListAdapter(@NonNull @NotNull Context context, List cars, List<List> car_details) {
        super(context, R.layout.custom_list_view, cars);
        this.context = context;
        this.car_details = car_details;
        this.cars = cars;
    }


    @SuppressLint({"InflateParams", "ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.custom_list_view, null);

        TextView car_name = convertView.findViewById(R.id.car_name);
        TextView car_model = convertView.findViewById(R.id.car_model);
        TextView car_number = convertView.findViewById(R.id.car_number);
        TextView hiring_cost = convertView.findViewById(R.id.hiring_cost);
        ImageView imageView = convertView.findViewById(R.id.custom_list_image);

        imageView.setImageBitmap((Bitmap) car_details.get(0).get(position));
        car_name.setText("Name: " + cars.get(position));
        car_model.setText("Model: " + car_details.get(2).get(position));
        car_number.setText("Plate Number: " + car_details.get(3).get(position));
        hiring_cost.setText("Cost: " + car_details.get(4).get(position));

        return convertView;
    }
}