package com.ochs.app.flexiicar_app.currentbookinglist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.util.Log;

import com.ochs.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Current_Booking_Activity extends AppCompatActivity {
    public static String model_Name;
    public static int agreement;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_booking);
        Bundle extras = getIntent().getExtras();
        Bundle bundle = new Bundle();
        getIntent().getExtras().getInt("currentbooking");

        try {
            JSONObject jsonObject = new JSONObject(getIntent().getExtras().getString("data"));
            JSONArray jsonArray = new JSONArray();
            jsonArray = jsonObject.getJSONArray("v0200_Agreement_Details");

            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject test = (JSONObject)jsonArray.get(i);
                final int vehiclE_ID = test.getInt("vehicle_ID");
               // final String make_Name = test.getString("make_Name");
                model_Name = test.getString("veh_Full_Name");
                agreement = test.getInt("agreement_ID");
                bundle.putInt("agreement_ID", agreement);
                //extras.putString("make_Name", make_Name);
                bundle.putString("model_Name", model_Name);
                bundle.getInt("selfCheckOut", test.getInt("selfCheckOut"));
                bundle.putString("lattitude" ,test.getString("lattitude"));
                bundle.putString("longitude",test.getString("longitude"));
                bundle.putString("lic_Num", test.getString("lic_Num"));
                bundle.putString("veh_Img_Path",test.getString("veh_Img_Path"));
            }

            NavHostFragment hostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            NavController navController = hostFragment.getNavController();
            NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph_currentbooking);
            if (extras.getInt("currentbooking")>1){
                navGraph.setStartDestination(R.id.BookingList);
            } else {
                navGraph.setStartDestination(R.id.LocationAndKey);
            }
            navController.setGraph(navGraph, bundle);
            //jsonArray = getIntent().getExtras().getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("TAG", "onCreate: "+ getIntent().getExtras().getInt("currentbooking") );
    }
}