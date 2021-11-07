package com.example.carhire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModelActivity extends AppCompatActivity {

    ActionBar actionBar;
    ListView model_list;
    List<String> models;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_models);

        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Car Models");

        model_list = findViewById(R.id.models);

        models = new ArrayList<>();
        models.add("6AA-ZWR80W");
        models.add("3BA-ZRR80W");
        models.add("DAA-ZWR80G");
        models.add("DBA-ZRR80G");
        models.add("DBA-ZRR85G");
        models.add("DBA-ZRR85W");

        ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, models);
        model_list.setAdapter(listAdapter);

        //Events Handling.
        model_list.setOnItemClickListener((parent, view, position, id) -> moveToCarDetails());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void moveToCarDetails(){
        Intent intent = new Intent(this, CarDetails.class);
        startActivity(intent);
    }
}