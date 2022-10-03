package com.ochs.app.flexiicar_app.user;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.GETDELIVERYLIST;
import static com.ochs.app.apicall.ApiEndPoint.GETPICKUPLIST;

public class Fragment_Filter_By_Vehicle_Class_For_User extends Fragment implements OnMapReadyCallback
{
    private static final int LOCATION_REQUEST_CODE = 20;
    private MapView mapViewDelivery, mapViewPickUP;
    private GoogleMap googleMap;
    ImageView backarrow;
    Bundle ReservationBundle;
    Boolean fromMap, option;
    Handler handler = new Handler();

    String[] pickupArray = new String[0];
    String[] deliveryArray = new String[0];

    HashMap<String, Integer> pickupHashmap = new HashMap<>();
    HashMap<String, Integer> deliveryHashmap = new HashMap<>();

    Spinner spinnerPickup, spinnerDelivery;

    TextView txtPickUpCharge1, txtPickUpCharge2, txtDeliveryCharge1, txtDeliveryCharge2,txt_Apply;

    LinearLayout llDelivery1, llDelivery2, llPickUp1, llPickUp2, selectDeliveryLocation;

    JSONArray deliveryJSONArray, pickupJSONArray;

    ToggleButton switchPickUp;

    JSONObject deliveryLoc, pickUpLoc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filterby_vehicle_class, container, false);
    }

    @Override
    public void onResume()
    {
        try
        {
            super.onResume();
            mapViewDelivery.onResume();
            mapViewPickUP.onResume();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy()
    {
        try
        {
            if(mapViewDelivery != null)
            {
                mapViewDelivery.onDestroy();
            }
            if(mapViewPickUP != null)
            {
                mapViewPickUP.onDestroy();
            }
            super.onDestroy();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //mapView.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        try {
            super.onLowMemory();
            mapViewDelivery.onLowMemory();
            mapViewPickUP.onLowMemory();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        try {
            super.onViewCreated(view, savedInstanceState);

            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            mapViewDelivery = (MapView) view.findViewById(R.id.mapViewDelivery);
            mapViewPickUP = (MapView) view.findViewById(R.id.mapViewPickUp);
            mapViewDelivery.onCreate(savedInstanceState);
            mapViewPickUP.onCreate(savedInstanceState);


            ReservationBundle = getArguments().getBundle("ReservationBundle");

            fromMap = getArguments().getBoolean("fromMap");

            txtDeliveryCharge1 = view.findViewById(R.id.txtDeliveryCharge1);
            txtDeliveryCharge2 = view.findViewById(R.id.txtDeliveryCharge2);
            txtPickUpCharge1 = view.findViewById(R.id.txtPickUpCharge1);
            txtPickUpCharge2 = view.findViewById(R.id.txtPickUpCharge2);

            llDelivery1 = view.findViewById(R.id.llDelivery1);
            llDelivery2 = view.findViewById(R.id.llDelivery2);
            llPickUp1 = view.findViewById(R.id.llPickUp1);
            llPickUp2 = view.findViewById(R.id.llPickUp2);
            selectDeliveryLocation = view.findViewById(R.id.selectDeliveryLocation);

            switchPickUp = view.findViewById(R.id.switchPickUp);
            switchPickUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                {

                    if(b){
                        llPickUp1.setVisibility(View.VISIBLE);
                        llPickUp2.setVisibility(View.VISIBLE);
                        mapViewPickUP.setVisibility(View.VISIBLE);
                    }
                    else{
                        llPickUp1.setVisibility(View.GONE);
                        llPickUp2.setVisibility(View.GONE);
                        mapViewPickUP.setVisibility(View.GONE);
                    }

                }
            });

            txtDeliveryCharge1.setText("US$ "+ReservationBundle.getDouble("pickLocChargePerMile")+" / FIX");
            txtDeliveryCharge2.setText("US$ "+ReservationBundle.getDouble("pickLocChargePerMile")+" / FIX");

            txtPickUpCharge1.setText("US$ "+ReservationBundle.getDouble("pickLocChargePerMile")+" / FIX");
            txtPickUpCharge2.setText("US$ "+ReservationBundle.getDouble("pickLocChargePerMile")+" / FIX");

            if(fromMap)
            {
                option = getArguments().getBoolean("option");
                if(option)
                {
                    txtDeliveryCharge2.setText("US$ "+ReservationBundle.getDouble("pickLocChargePerMile")+"/Mile");

                    /*LatLng locationPin = new LatLng(getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationPin));

                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(locationPin)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.gray_marker)));*/
                }
                else{
                    txtPickUpCharge2.setText("US$ "+ReservationBundle.getDouble("pickLocChargePerMile")+"/Mile");
                    /*LatLng locationPin = new LatLng(getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationPin));

                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(locationPin)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.gray_marker)));*/
                }
            }

            MapsInitializer.initialize(this.getActivity());

            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]
                        {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
            else
            {
                mapViewDelivery.getMapAsync(this);
                mapViewPickUP.getMapAsync(this);
            }

            spinnerPickup = view.findViewById(R.id.spinnerPickup);
            spinnerDelivery = view.findViewById(R.id.spinnerDelivery);

            txt_Apply=view.findViewById(R.id.txt_Apply);
            txt_Apply.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle Booking = new Bundle();
                    Booking.putBundle("ReservationBundle", ReservationBundle);

                    if(deliveryLoc != null)
                    {

                        JSONArray DeliveryAndPickupModel = new JSONArray();
                        DeliveryAndPickupModel.put(deliveryLoc);

                        if(pickUpLoc != null)
                            DeliveryAndPickupModel.put(pickUpLoc);

                        Booking.putString("DeliveryAndPickupModel", DeliveryAndPickupModel.toString());

                    }

                    NavHostFragment.findNavController(Fragment_Filter_By_Vehicle_Class_For_User.this)
                            .navigate(R.id.action_FilterByVehicleClass_to_Select_addtional_options, Booking);
                }
            });

            backarrow = view.findViewById(R.id.img11_backarrow);
            backarrow.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle Booking = new Bundle();
                    Booking.putBundle("ReservationBundle", ReservationBundle);

                    if(deliveryLoc != null)
                    {

                        JSONArray DeliveryAndPickupModel = new JSONArray();
                        DeliveryAndPickupModel.put(deliveryLoc);

                        if(pickUpLoc != null)
                            DeliveryAndPickupModel.put(pickUpLoc);

                        Booking.putString("DeliveryAndPickupModel", DeliveryAndPickupModel.toString());

                    }

                    NavHostFragment.findNavController(Fragment_Filter_By_Vehicle_Class_For_User.this)
                            .navigate(R.id.action_FilterByVehicleClass_to_Select_addtional_options, Booking);
                }
            });

            selectDeliveryLocation.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle Booking = new Bundle();
                    Booking.putBundle("ReservationBundle", ReservationBundle);
                    Booking.putBoolean("option", true);
                    NavHostFragment.findNavController(Fragment_Filter_By_Vehicle_Class_For_User.this)
                            .navigate(R.id.action_FilterByVehicleClass_to_MapScreenSearchAddress, Booking);
                }
            });

            try
            {
                ApiService ApiService = new ApiService(PickUpList, RequestType.GET,
                        GETPICKUPLIST, BASE_URL_BOOKING, new HashMap<String, String>(), "");
            } catch (Exception e)
            {
                e.printStackTrace();
            }


            try {
                ApiService ApiService = new ApiService(DeliveryList, RequestType.GET,
                        GETDELIVERYLIST, BASE_URL_BOOKING, new HashMap<String, String>(), "");
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap gMap)
    {
        try {

            googleMap = gMap;
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            googleMap.getUiSettings().setTiltGesturesEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setScrollGesturesEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);

            if(fromMap)
            {
                if (option)
                {

                    LatLng locationPin = new LatLng(getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationPin));

                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(locationPin)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.gray_marker)));

                    llDelivery1.setVisibility(View.GONE);

                    deliveryLoc = new JSONObject();
                    deliveryLoc.put("latitude ",getArguments().getDouble("latitude"));
                    deliveryLoc.put("longitude ",getArguments().getDouble("longitude"));

                } else {

                    LatLng locationPin = new LatLng(getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationPin));

                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(locationPin)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.gray_marker)));

                    llPickUp1.setVisibility(View.GONE);

                    pickUpLoc = new JSONObject();
                    pickUpLoc.put("latitude ",getArguments().getDouble("latitude"));
                    pickUpLoc.put("longitude ",getArguments().getDouble("longitude"));
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    OnResponseListener PickUpList = new OnResponseListener()
    {
        @Override
        public void onSuccess(final String response, HashMap<String, String> headers)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        System.out.println("Success");
                        System.out.println(response);

                        JSONObject responseJSON = new JSONObject(response);
                        Boolean status = responseJSON.getBoolean("status");

                        if (status)
                        {

                            JSONObject resultSet = responseJSON.getJSONObject("resultSet");
                            pickupJSONArray = resultSet.getJSONArray("v0040_Delivery_Pickup_Charge");

                            int len;
                            len = pickupJSONArray.length();

                            pickupArray = new String[len+1];
                            pickupArray[0] = "";

                            for (int j = 1; j < len+1; j++)
                            {
                                final JSONObject test = (JSONObject) pickupJSONArray.get(j-1);
                                //final int country_ID = test.getInt("country_ID");
                                //final String country_Code = test.getString("country_Code");
                                final String locationName = test.getString("locationName");
                                // final String phone_Code = test.getString("phone_Code");
                                pickupArray[j] = locationName;


                            }

                            ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, R.id.text1, pickupArray);
                            spinnerPickup.setAdapter(adapterCategories);
                            //spinnerPickup.setSelection(0);

                            spinnerPickup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                    try {
                                        final Double chargeAmount = ((JSONObject) pickupJSONArray.getJSONObject(i)).getDouble("chargeAmount");
                                        final Double lat = ((JSONObject) pickupJSONArray.getJSONObject(i)).getDouble("latitude");
                                        final Double lon = ((JSONObject) pickupJSONArray.getJSONObject(i)).getDouble("longitude");

                                        LatLng locationPin = new LatLng(lat,lon);

                                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationPin));

                                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                                .position(locationPin));

                                        txtPickUpCharge1.setText("US$ " + chargeAmount + " / FIX");
                                        llPickUp2.setVisibility(View.GONE);

                                        pickUpLoc = pickupJSONArray.getJSONObject(i);

                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
                        }
                        else
                        {
                            String errorString = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),errorString,1);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
        @Override
        public void onError(String error)
        {
            System.out.println("Error-" + error);
        }
    };


    OnResponseListener DeliveryList = new OnResponseListener()
    {
        @Override
        public void onSuccess(final String response, HashMap<String, String> headers)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        System.out.println("Success");
                        System.out.println(response);

                        JSONObject responseJSON = new JSONObject(response);
                        Boolean status = responseJSON.getBoolean("status");

                        if (status)
                        {

                            JSONObject resultSet = responseJSON.getJSONObject("resultSet");
                            deliveryJSONArray = resultSet.getJSONArray("v0040_Delivery_Pickup_Charge");

                            int len;
                            len = deliveryJSONArray.length();

                            deliveryArray = new String[len+1];
                            deliveryArray[0] = "";

                            for (int j = 1; j < len+1; j++)
                            {
                                final JSONObject test = (JSONObject) deliveryJSONArray.get(j-1);
                                //final int country_ID = test.getInt("country_ID");
                                //final String country_Code = test.getString("country_Code");
                                final String locationName = test.getString("locationName");
                                final Double chargeAmount = test.getDouble("chargeAmount");
                                final Double latitude = test.getDouble("latitude");
                                final Double longitude = test.getDouble("longitude");
                                final int isFree = test.getInt("isFree");
                                final int businessLocID = test.getInt("businessLocID");
                                // final String phone_Code = test.getString("phone_Code");
                                deliveryArray[j] = locationName;
                            }

                            ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, R.id.text1, deliveryArray);
                            spinnerDelivery.setAdapter(adapterCategories);
                            //spinnerPickup.setSelection(0);

                            spinnerDelivery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                    try {
                                        final Double chargeAmount = ((JSONObject) deliveryJSONArray.getJSONObject(i)).getDouble("chargeAmount");
                                        final Double lat = ((JSONObject) deliveryJSONArray.getJSONObject(i)).getDouble("latitude");
                                        final Double lon = ((JSONObject) deliveryJSONArray.getJSONObject(i)).getDouble("longitude");

                                        LatLng locationPin = new LatLng(lat,lon);

                                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationPin));

                                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                                .position(locationPin));

                                        txtDeliveryCharge1.setText("US$ " + chargeAmount + " / FIX");
                                        llDelivery2.setVisibility(View.GONE);

                                        deliveryLoc = deliveryJSONArray.getJSONObject(i);
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });

                        }
                        else
                        {
                            String errorString = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),errorString,1);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
        @Override
        public void onError(String error)
        {
            System.out.println("Error-" + error);
        }
    };
}