package com.example.carhire.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.widget.TextViewCompat;

import com.example.carhire.R;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MyExpandableListAdapter extends BaseExpandableListAdapter  {

    Context context;
    List<String> parent;
    Map<String, List<String>> children;

    ImageView car_image;
    EditText carName, carModel, carNumber, carSeats, hiringCost, driverFirstName, driverLastName, driverPhone, driverId;
    Button add, addDriver;

    int GROUP_TYPE_1 = 0;
    int GROUP_TYPE_2 = 1;
    int GROUP_TYPE_NULL = 2;

    int CHILD_TYPE_1 = 0;
    int CHILD_TYPE_2 = 1;
    int CHILD_TYPE_NULL = 2;

    public MyExpandableListAdapter(Context context, List<String> parent, Map<String, List<String>> children) {
        this.context = context;
        this.parent = parent;
        this.children = children;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        int childType;

        if (groupPosition == GROUP_TYPE_1) {
            if (childPosition == 0) {
                childType = CHILD_TYPE_1;
            } else {
                childType = CHILD_TYPE_NULL;
            }

        } else if (groupPosition == GROUP_TYPE_2) {
            if (childPosition == 0) {
                childType = CHILD_TYPE_2;
            } else {
                childType = CHILD_TYPE_NULL;
            }

        } else {
            childType = CHILD_TYPE_NULL;
        }

        return childType;
    }

    @Override
    public int getChildTypeCount() {
        return 2;
    }

    @Override
    public int getGroupType(int groupPosition) {
        if (groupPosition == 0) {
            return GROUP_TYPE_1;

        } else if (groupPosition == 1) {
            return GROUP_TYPE_2;

        } else {
            return GROUP_TYPE_NULL;
        }
    }

    @Override
    public int getGroupTypeCount() {
        return 2;
    }

    @Override
    public int getGroupCount() {
        return parent.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(children.get(parent.get(groupPosition))).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parent.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(children.get(parent.get(groupPosition))).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint({"InflateParams", "ResourceType"})
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String parent_title = (String) getGroup(groupPosition);
        int id = getGroupType(groupPosition);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            if (id == GROUP_TYPE_1) {
                convertView = inflater.inflate(R.layout.parent_title1, null);

            } else if (id == GROUP_TYPE_2) {
                convertView = inflater.inflate(R.layout.parent_title2, null);
            }
        }

        if (id == GROUP_TYPE_1) {
            TextView title = convertView.findViewById(R.id.title1);
            title.setText(parent_title);

            if (isExpanded) {
                title.setTextColor(context.getResources().getColor(R.color.purple_700));

            } else {
                TextViewCompat.setTextAppearance(title, R.style.text_color);
            }

        } else if (id == GROUP_TYPE_2) {
            TextView title = convertView.findViewById(R.id.title2);
            title.setText(parent_title);

            if (isExpanded) {
                title.setTextColor(context.getResources().getColor(R.color.purple_700));

            } else {
                TextViewCompat.setTextAppearance(title, R.style.text_color);
            }
        }

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        int id = getChildType(groupPosition, childPosition);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            if (id == CHILD_TYPE_1) {
                convertView = inflater.inflate(R.layout.add_car, null);

            } else if (id == CHILD_TYPE_2) {
                convertView = inflater.inflate(R.layout.add_driver, null);
            }
        }

        if (id == CHILD_TYPE_1) {
            car_image = convertView.findViewById(R.id.car_image);
            carName = convertView.findViewById(R.id.car_name);
            carModel = convertView.findViewById(R.id.model_name);
            carNumber = convertView.findViewById(R.id.plate_number);
            carSeats = convertView.findViewById(R.id.seats);
            hiringCost = convertView.findViewById(R.id.hiring_cost);

            add = convertView.findViewById(R.id.add_car);

        }else if (id == CHILD_TYPE_2){
            driverFirstName = convertView.findViewById(R.id.driver_firstName);
            driverLastName = convertView.findViewById(R.id.driver_lastName);
            driverPhone = convertView.findViewById(R.id.driverPhoneNumber);
            driverId = convertView.findViewById(R.id.driverIdNumber);

            addDriver = convertView.findViewById(R.id.add_driver);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public ImageView getCar_image(){
        return car_image;
    }

    public EditText getCarName(){
        return carName;
    }

    public EditText getCarModel(){
        return carModel;
    }

    public EditText getCarNumber(){
        return carNumber;
    }

    public EditText getCarSeats(){
        return carSeats;
    }

    public EditText getHiringCost(){
        return hiringCost;
    }

    public EditText getDriverFirstName(){
        return driverFirstName;
    }

    public EditText getDriverLastName(){
        return driverLastName;
    }

    public EditText getDriverPhone(){
        return driverPhone;
    }

    public EditText getDriverId(){
        return driverId;
    }
}