package com.ochs.app.flexiicar_app.booking;

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

public class Fragment_Filter_By_Vehicle_Class extends Fragment
{
    private static final int LOCATION_REQUEST_CODE = 20;
    private MapView mapViewDelivery, mapViewPickUP;
    private GoogleMap googleMap;
    ImageView backarrow;
    Bundle BookingBundle,VehicleBundle;
    Bundle returnLocationBundle, locationBundle;
    Boolean locationType, initialSelect, fromMap, option;
    Handler handler = new Handler();
    TextView txtApply,txtfullAddress;
    LinearLayout lblAddress;
    Marker marker;

    String[] pickupArray = new String[0];
    String[] deliveryArray = new String[0];

    HashMap<String, Integer> pickupHashmap = new HashMap<>();
    HashMap<String, Integer> deliveryHashmap = new HashMap<>();

    Spinner spinnerPickup, spinnerDelivery;

    TextView txtPickUpCharge1, txtPickUpCharge2, txtDeliveryCharge1, txtDeliveryCharge2;

    LinearLayout llDelivery1, llDelivery2, llPickUp1, llPickUp2, selectDeliveryLocation,selectPickupLocation;

    JSONArray deliveryJSONArray, pickupJSONArray;

    ToggleButton switchPickUp;

    JSONObject deliveryLoc, pickUpLoc;

    double latitudedelivery,latitudepickup,longitudedelivery,longitudepickup;

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
                mapViewDelivery.onStop();
            }
            if(mapViewPickUP != null)
            {
                mapViewPickUP.onStop();
            }
            super.onDestroy();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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

            BookingBundle = getArguments().getBundle("BookingBundle");
            VehicleBundle = getArguments().getBundle("VehicleBundle");

            returnLocationBundle = getArguments().getBundle("returnLocation");
            locationBundle = getArguments().getBundle("location");
            locationType = getArguments().getBoolean("locationType");
            initialSelect = getArguments().getBoolean("initialSelect");
            fromMap = getArguments().getBoolean("fromMap");

            txtDeliveryCharge1 = view.findViewById(R.id.txtDeliveryCharge1);
            txtDeliveryCharge2 = view.findViewById(R.id.txtDeliveryCharge2);
            txtPickUpCharge1 = view.findViewById(R.id.txtPickUpCharge1);
            txtPickUpCharge2 = view.findViewById(R.id.txtPickUpCharge2);

            txtfullAddress = view.findViewById(R.id.txtfullAddress);
            lblAddress = view.findViewById(R.id.lblAddress);

            llDelivery1 = view.findViewById(R.id.llDelivery1);
            llDelivery2 = view.findViewById(R.id.llDelivery2);

            llPickUp1 = view.findViewById(R.id.llPickUp1);
            llPickUp2 = view.findViewById(R.id.llPickUp2);
            selectDeliveryLocation = view.findViewById(R.id.selectDeliveryLocation);
            selectPickupLocation=view.findViewById(R.id.selectPickupLocation);

            switchPickUp = view.findViewById(R.id.switchPickUp);

            switchPickUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                {
                    if(b)
                    {
                        llPickUp1.setVisibility(View.VISIBLE);
                        llPickUp2.setVisibility(View.VISIBLE);
                        mapViewPickUP.setVisibility(View.VISIBLE);
                    }
                    else{
                        // llPickUp1.setVisibility(View.GONE);
                        //  llPickUp2.setVisibility(View.GONE);
                        //  mapViewPickUP.setVisibility(View.GONE);
                    }

                }
            });

            txtDeliveryCharge1.setText("US$ "+locationBundle.getDouble("pickLocChargePerMile")+" / FIX");
            txtDeliveryCharge2.setText("US$ "+locationBundle.getDouble("pickLocChargePerMile")+" / FIX");

            txtPickUpCharge1.setText("US$ "+locationBundle.getDouble("pickLocChargePerMile")+" / FIX");
            txtPickUpCharge2.setText("US$ "+locationBundle.getDouble("pickLocChargePerMile")+" / FIX");

            if(fromMap)
            {
                option = getArguments().getBoolean("option");

                if(option)
                {
                    txtDeliveryCharge2.setText("US$ "+locationBundle.getDouble("pickLocChargePerMile")+"/Mile");

                    /*LatLng locationPin = new LatLng(getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationPin));

                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(locationPin)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.gray_marker)));*/
                }
                else{
                    txtPickUpCharge2.setText("US$ "+returnLocationBundle.getDouble("pickLocChargePerMile")+"/Mile");

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
                mapViewDelivery.getMapAsync(onMapReadyCallback1());
                mapViewPickUP.getMapAsync(onMapReadyCallback2());

                //  mapViewDelivery.getMapAsync(this);
                // mapViewPickUP.getMapAsync(this);

            }

            spinnerPickup = view.findViewById(R.id.spinnerPickup);
            spinnerDelivery = view.findViewById(R.id.spinnerDelivery);

            txtApply=view.findViewById(R.id.txt_Apply);
            txtApply.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle Booking = new Bundle();
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    Booking.putString("DeliveryAndPickupModel", "");
                    if(deliveryLoc != null)
                    {
                        JSONArray DeliveryAndPickupModel = new JSONArray();
                        DeliveryAndPickupModel.put(deliveryLoc);

                        if(pickUpLoc != null)
                            DeliveryAndPickupModel.put(pickUpLoc);

                        Booking.putString("DeliveryAndPickupModel", DeliveryAndPickupModel.toString());

                    }

                    NavHostFragment.findNavController(Fragment_Filter_By_Vehicle_Class.this)
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
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    Booking.putString("DeliveryAndPickupModel", "");
                    if(deliveryLoc != null)
                    {

                        JSONArray DeliveryAndPickupModel = new JSONArray();
                        DeliveryAndPickupModel.put(deliveryLoc);

                        if(pickUpLoc != null)
                            DeliveryAndPickupModel.put(pickUpLoc);

                        Booking.putString("DeliveryAndPickupModel", DeliveryAndPickupModel.toString());

                    }

                    NavHostFragment.findNavController(Fragment_Filter_By_Vehicle_Class.this)
                            .navigate(R.id.action_FilterByVehicleClass_to_Select_addtional_options, Booking);
                }
            });

            selectDeliveryLocation.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle Booking = new Bundle();
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    Booking.putBoolean("option",true);
                    Booking.putDouble("latitudedelivery",latitudedelivery);
                    Booking.putDouble("longitudedelivery",longitudedelivery);
                    Booking.putDouble("latitudepickup",latitudepickup);
                    Booking.putDouble("longitudepickup",longitudepickup);
                    System.out.println(Booking);
                    NavHostFragment.findNavController(Fragment_Filter_By_Vehicle_Class.this)
                            .navigate(R.id.action_FilterByVehicleClass_to_MapScreenSearchAddress, Booking);
                }
            });
            selectPickupLocation.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle Booking = new Bundle();
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    Booking.putBoolean("option",false);
                    Booking.putString("address",txtfullAddress.getText().toString());
                    Booking.putDouble("latitudedelivery",latitudedelivery);
                    Booking.putDouble("longitudedelivery",longitudedelivery);
                    Booking.putDouble("latitudepickup",latitudepickup);
                    Booking.putDouble("longitudepickup",longitudepickup);
                    System.out.println(Booking);
                    NavHostFragment.findNavController(Fragment_Filter_By_Vehicle_Class.this)
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

    public OnMapReadyCallback onMapReadyCallback1()
    {
        return new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(GoogleMap mMap)
            {
                try {
                    googleMap=mMap;
                    mMap=googleMap;
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.getUiSettings().setZoomGesturesEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.getUiSettings().setRotateGesturesEnabled(true);
                    mMap.getUiSettings().setTiltGesturesEnabled(true);
                    mMap.getUiSettings().setCompassEnabled(true);
                    mMap.getUiSettings().setScrollGesturesEnabled(true);
                    mMap.getUiSettings().setAllGesturesEnabled(true);
                    mMap.getUiSettings().setIndoorLevelPickerEnabled(true);

                    if (fromMap)
                    {
                        option = getArguments().getBoolean("option");
                        if(option)
                        {
                            latitudedelivery=getArguments().getDouble("latitudedelivery");
                            longitudedelivery=getArguments().getDouble("longitudedelivery");
                            // llDelivery2.setVisibility(View.GONE);
                            lblAddress.setVisibility(View.VISIBLE);

                            String Address=getArguments().getString("address");
                            txtfullAddress.setText(Address);

                            LatLng locationPin = new LatLng(44.833328, -0.56667);
                            System.out.println(latitudedelivery);
                            System.out.println(longitudedelivery);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(locationPin));

                            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(locationPin)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_marker)));
                            //llDelivery1.setVisibility(View.GONE);
                            System.out.println("position"+marker.getPosition());

                            deliveryLoc = new JSONObject();
                            deliveryLoc.put("latitude ", getArguments().getDouble("latitude"));
                            deliveryLoc.put("longitude ", getArguments().getDouble("longitude"));
                        }
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
    }

    public OnMapReadyCallback onMapReadyCallback2()
    {
        return new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(GoogleMap gmap)
            {
                try {
                    googleMap=gmap;
                    gmap=googleMap;
                    gmap.getUiSettings().setZoomControlsEnabled(true);
                    gmap.getUiSettings().setZoomGesturesEnabled(true);
                    gmap.getUiSettings().setMyLocationButtonEnabled(true);
                    gmap.getUiSettings().setRotateGesturesEnabled(true);
                    gmap.getUiSettings().setTiltGesturesEnabled(true);
                    gmap.getUiSettings().setCompassEnabled(true);
                    gmap.getUiSettings().setScrollGesturesEnabled(true);
                    gmap.getUiSettings().setAllGesturesEnabled(true);
                    gmap.getUiSettings().setIndoorLevelPickerEnabled(true);

                    if (fromMap)
                    {
                        option = getArguments().getBoolean("option");
                        if (!option)
                        {
                            latitudepickup=getArguments().getDouble("latitudepickup");
                            longitudepickup=getArguments().getDouble("longitudepickup");
                            LatLng locationPin = new LatLng(47.66, -2.75);
                            System.out.println(latitudepickup);
                            System.out.println(longitudepickup);

                            String Address=getArguments().getString("address");

                            txtfullAddress.setText(Address);
                            lblAddress.setVisibility(View.VISIBLE);

                            gmap.moveCamera(CameraUpdateFactory.newLatLng(locationPin));

                            gmap.animateCamera(CameraUpdateFactory.zoomTo(10));

                            Marker marker = gmap.addMarker(new MarkerOptions()
                                    .position(locationPin)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_marker)));

                            // llPickUp1.setVisibility(View.GONE);
                            llPickUp1.setVisibility(View.VISIBLE);
                            llPickUp2.setVisibility(View.VISIBLE);
                            mapViewPickUP.setVisibility(View.VISIBLE);

                            pickUpLoc = new JSONObject();
                            pickUpLoc.put("latitude ", getArguments().getDouble("latitude"));
                            pickUpLoc.put("longitude ", getArguments().getDouble("longitude"));
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
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
                            spinnerPickup.setSelection(0);

                            spinnerPickup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                            {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                                {

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
                                        //llPickUp2.setVisibility(View.GONE);

                                        pickUpLoc = pickupJSONArray.getJSONObject(i);

                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView)
                                {

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
                            spinnerDelivery.setSelection(0);

                            spinnerDelivery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                            {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                                {
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
                                        // llDelivery2.setVisibility(View.GONE);

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


