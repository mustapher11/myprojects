package com.example.carhire;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.carhire.adapter.MyExpandableListAdapter;
import com.example.carhire.asynctasks.AddDriverTask;
import com.example.carhire.asynctasks.ManageCarTask;
import com.example.carhire.asynctasks.NetworkManager;
import com.example.carhire.interfaces.ManageCarInterface;
import com.example.carhire.interfaces.ManageDriverInterface;
import com.example.carhire.interfaces.NetworkConnectionInterface;
import com.example.carhire.sessions.AdminSession;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddProducts extends AppCompatActivity implements NetworkConnectionInterface, ManageCarInterface, ManageDriverInterface {

    ActionBar actionBar;
    ExpandableListView expandableListView;
    List<String> parent, labels;
    Map<String, List<String>> children;
    RadioGroup radioGroup;
    RadioButton radioButton;
    ProgressDialog progressDialog;

    MyExpandableListAdapter adapter;
    AdminSession adminSession;

    String buttonText;
    boolean result;

    RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            radioButton = group.findViewById(checkedId);
            buttonText = radioButton.getText().toString();
        }
    };

    ActivityResultLauncher<Intent> resultLauncher;
    Bitmap bitmap;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);

        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Admin Section");

        expandableListView = findViewById(R.id.admin_panel);
        progressDialog = new ProgressDialog(this);

        progressDialog.setCanceledOnTouchOutside(false);

        parent = new ArrayList<>();
        parent.add("Manage Car");
        parent.add("Manage Driver");

        labels = new ArrayList<>();
        labels.add("");

        children = new HashMap<>();
        children.put(parent.get(0), labels);
        children.put(parent.get(1), labels);

        adapter = new MyExpandableListAdapter(this, parent, children);
        expandableListView.setAdapter(adapter);

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null){
                        Uri uri = result.getData().getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            Log.d("resultImage", "" + bitmap);
                            adapter.getCar_image().setImageBitmap(bitmap);

                        } catch (IOException e) {
                            Log.d("Error", e.getMessage());
                        }
                    }
                });

        // Events Handling.
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previous_position = -1;
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onGroupExpand(int groupPosition) {
                if (previous_position != groupPosition){
                    expandableListView.collapseGroup(previous_position);
                }
                previous_position = groupPosition;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.admin_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == android.R.id.home){
            onBackPressed();

        }else if (item.getItemId() == R.id.logout){
            logOutButton();
            moveToLogin();

        }else if (item.getItemId() == R.id.reservations){
            reservationType();

        }else if (item.getItemId() == R.id.cars){
            item.setIntent(new Intent(this, CarsActivity.class));

        }else if (item.getItemId() == R.id.customers){
            item.setIntent(new Intent(this, CustomersActivity.class));

        }else if (item.getItemId() == R.id.drivers){
            item.setIntent(new Intent(this, DriversActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public void logOutButton(){
        AdminSession adminSession = new AdminSession(this);
        adminSession.removeSession();
    }

    public void moveToLogin(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @SuppressLint("InflateParams")
    public void reservationType(){
        View view = LayoutInflater.from(this).inflate(R.layout.reservations, null);
        radioGroup = view.findViewById(R.id.reserve);

        radioGroup.setOnCheckedChangeListener(listener);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Reservation Type").setMessage("Which reservations do you wish to view?")
                .setView(view)
                .setPositiveButton("Ok", (dialog, which) -> getRadioButtonText())
                .setNegativeButton("Cancel", null).setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void getRadioButtonText(){
        Log.d("Text", buttonText);
        moveToReservations();
    }

    public void moveToReservations(){
        Intent intent = new Intent(this, ReservationsActivity.class);
        intent.putExtra("type", buttonText);
        startActivity(intent);
    }

    public void launchGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        resultLauncher.launch(Intent.createChooser(intent, "Select an image"));
    }

    public String getStringImage(){
        byte[] imageBytes = new byte[0];

        if (bitmap != null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            imageBytes = byteArrayOutputStream.toByteArray();
        }

        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public void addCar(View view){
        adminSession = new AdminSession(this);
        String imageString = getStringImage();
        String car_name = Objects.requireNonNull(adapter.getCarName().getText()).toString().trim();
        String car_model = Objects.requireNonNull(adapter.getCarModel().getText()).toString().trim();
        String car_number = Objects.requireNonNull(adapter.getCarNumber().getText()).toString().trim().replace(" ", "");
        String car_seats = Objects.requireNonNull(adapter.getCarSeats().getText()).toString().trim();
        String hiring_cost = Objects.requireNonNull(adapter.getHiringCost().getText()).toString().trim().replace(" ", "");
        String admin_id = adminSession.getAdminData();

         if (result){
             if (bitmap == null && car_name.isEmpty() && car_model.isEmpty() && car_number.isEmpty() && car_seats.isEmpty()
                     && hiring_cost.isEmpty()){
                 Toast.makeText(this, "Please select an image!", Toast.LENGTH_LONG).show();
                 adapter.getCarName().setError("This field must be filled!");
                 adapter.getCarModel().setError("This field must be filled!");
                 adapter.getCarNumber().setError("This field must be filled!");
                 adapter.getCarSeats().setError("This field must be filled!");
                 adapter.getHiringCost().setError("This field must be filled!");

             }else if (bitmap == null){
                 Toast.makeText(this, "Please select an image!", Toast.LENGTH_LONG).show();

             }else if (car_name.isEmpty()){
                 adapter.getCarName().setError("This field must be filled!");

             }else if (car_model.isEmpty()){
                 adapter.getCarModel().setError("This field must be filled!");

             }else if (car_number.isEmpty()){
                 adapter.getCarNumber().setError("This field must be filled!");

             }else if (car_seats.isEmpty()){
                 adapter.getCarSeats().setError("This field must be filled!");

             }else if (hiring_cost.isEmpty()){
                 adapter.getHiringCost().setError("This field must be filled!");

             }else if (checkNumber(hiring_cost)){
                 adapter.getHiringCost().setError("Only numbers are required!");

             }else {
                 ManageCarTask manageCarTask = new ManageCarTask(this, this, progressDialog);
                 manageCarTask.execute(imageString, car_name, car_model, car_number, car_seats, hiring_cost, admin_id);
             }

         }else {
             Toast.makeText(this, "Please check your internet connection and try again!", Toast.LENGTH_LONG).show();
         }
    }

    @Override
    public void pingResult(boolean result) {
        this.result = result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkManager networkManager = new NetworkManager(this, this);
        networkManager.execute();
    }

    @Override
    public void addCarResult(String result) {
        if (result.equals("1")){
            progressDialog.dismiss();
            bitmap = null;
            Objects.requireNonNull(adapter.getCarName().getText()).clear();
            Objects.requireNonNull(adapter.getCarModel().getText()).clear();
            Objects.requireNonNull(adapter.getCarNumber().getText()).clear();
            Objects.requireNonNull(adapter.getCarSeats().getText()).clear();
            Objects.requireNonNull(adapter.getHiringCost().getText()).clear();
            Toast.makeText(this, "Car added successfully!", Toast.LENGTH_SHORT).show();

        }else {
            progressDialog.dismiss();
            Objects.requireNonNull(adapter.getCarModel().getText()).clear();
            Objects.requireNonNull(adapter.getCarNumber().getText()).clear();
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        }
    }

    public void addDriver(View view){
        adminSession = new AdminSession(this);
        String driverFirstName = Objects.requireNonNull(adapter.getDriverFirstName().getText()).toString().trim();
        String driverLastName = Objects.requireNonNull(adapter.getDriverLastName().getText()).toString().trim();
        String driverPhoneNUmber = Objects.requireNonNull(adapter.getDriverPhone().getText()).toString().trim().replace(" ", "");
        String driverIdNumber = Objects.requireNonNull(adapter.getDriverId().getText()).toString().trim().replace(" ", "");
        String adminId = adminSession.getAdminData();

        if (result){
            if (driverFirstName.isEmpty() && driverLastName.isEmpty() && driverPhoneNUmber.isEmpty() && driverIdNumber.isEmpty()){
                adapter.getDriverFirstName().setError("This field must be filled!");
                adapter.getDriverLastName().setError("This field must be filled!");
                adapter.getDriverId().setError("This field must be filled!");
                adapter.getDriverPhone().setError("This field must be filled!");

            }else if (driverFirstName.isEmpty()){
                adapter.getDriverFirstName().setError("This field must be filled!");

            }else if (driverLastName.isEmpty()){
                adapter.getDriverLastName().setError("This field must be filled!");

            }else if (driverPhoneNUmber.isEmpty()){
                adapter.getDriverPhone().setError("This field must be filled!");

            }else if (checkNumber(driverPhoneNUmber)){
                adapter.getDriverPhone().setError("Only numbers are required!");

            }else if ((!driverPhoneNUmber.startsWith("0") && driverPhoneNUmber.length() > 10) ||
                    ((!driverPhoneNUmber.startsWith("0") && driverPhoneNUmber.length() < 10))){
                adapter.getDriverPhone().setError("Invalid phone number!");

            }else if (driverIdNumber.isEmpty()){
                adapter.getDriverId().setError("This field must be filled!");

            }else if (checkNumber(driverIdNumber)){
                adapter.getDriverId().setError("Only numbers are required!");

            }else if (driverIdNumber.length() != 8){
                adapter.getDriverId().setError("Invalid id number!");

            }else {
                AddDriverTask addDriverTask = new AddDriverTask(this, this,  progressDialog);
                addDriverTask.execute(driverFirstName, driverLastName, driverIdNumber, driverPhoneNUmber, adminId);
            }
        }else {
            Toast.makeText(this, "Please check your internet connection and try again!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void addDriverResult(String response) {
        if (response.equals("1")){
            progressDialog.dismiss();
            Objects.requireNonNull(adapter.getDriverFirstName().getText()).clear();
            Objects.requireNonNull(adapter.getDriverLastName().getText()).clear();
            Objects.requireNonNull(adapter.getDriverId().getText()).clear();
            Objects.requireNonNull(adapter.getDriverPhone().getText()).clear();
            Toast.makeText(this, "Driver details added successfully!", Toast.LENGTH_SHORT).show();

        }else {
            progressDialog.dismiss();
            Objects.requireNonNull(adapter.getDriverId().getText()).clear();
            Objects.requireNonNull(adapter.getDriverPhone().getText()).clear();
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void searchDriverResult(List<String> driverDetails) {
        if (driverDetails != null) {
            progressDialog.dismiss();
            adapter.getDriverFirstName().setText(driverDetails.get(0));
            adapter.getDriverLastName().setText(driverDetails.get(1));
            adapter.getDriverId().setText(driverDetails.get(2));
            adapter.getDriverPhone().setText(driverDetails.get(3));

        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "No results found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateDriverResult(String response) {
        if (response != null){
            progressDialog.dismiss();
            Objects.requireNonNull(adapter.getDriverFirstName().getText()).clear();
            Objects.requireNonNull(adapter.getDriverLastName().getText()).clear();
            Objects.requireNonNull(adapter.getDriverId().getText()).clear();
            Objects.requireNonNull(adapter.getDriverPhone().getText()).clear();
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkNumber(String number){
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(number);

        return !matcher.matches();
    }
}