package com.ochs.app.flexiicar_app.commonfragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.adapters.Location;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_SETTINGS;
import static com.ochs.app.apicall.ApiEndPoint.CALCULATE_DISTANCE;

public class Fragment_Map_Screen_Search extends Fragment implements OnMapReadyCallback
{
    private MapView mapView;
    private GoogleMap googleMap;
    ImageView close_symbol;
    public static Context context;
    public String id = "";
    Handler handler = new Handler();
    Marker previousMarker;
    private final int REQUEST_PLACE_ADDRESS = 40;
    private Geocoder geocoder;

    ArrayList<Location> locationList = new ArrayList<>();

    TextView txtAddress, txtMiles, txtPerMiles, txtAmount;

    RelativeLayout location_layout;
    Bundle BookingBundle,VehicleBundle;
    Bundle returnLocationBundle, locationBundle;
    Boolean locationType, initialSelect, option;

    Button btnSelectAddress;

    Double distance;

    double latitudedelivery,latitudepickup,longitudedelivery,longitudepickup;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_map_screen_search, container, false);
        return v;
    }

    @Override
    public void onResume()
    {
        try
        {
            super.onResume();
            mapView.onResume();
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
            if(mapView != null)
            {
                mapView.onDestroy();
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
            mapView.onLowMemory();
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

            BookingBundle = getArguments().getBundle("BookingBundle");
            VehicleBundle = getArguments().getBundle("VehicleBundle");

            returnLocationBundle = getArguments().getBundle("returnLocation");
            locationBundle = getArguments().getBundle("location");
            locationType = getArguments().getBoolean("locationType");
            initialSelect = getArguments().getBoolean("initialSelect");
            option = getArguments().getBoolean("option");
            close_symbol=view.findViewById(R.id.close_symbol);

            close_symbol.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    location_layout.setVisibility(View.GONE);
                    location_layout.removeAllViews();
                }
            });

            latitudedelivery = getArguments().getDouble("latitudedelivery");
            latitudepickup = getArguments().getDouble("latitudepickup");
            longitudedelivery = getArguments().getDouble("longitudedelivery");
            longitudepickup = getArguments().getDouble("longitudepickup");

            System.out.println(option);

            mapView = (MapView) view.findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);

            MapsInitializer.initialize(this.getActivity());

            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]
                        {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
            else
            {
                mapView.getMapAsync(this);
            }

            txtAddress = view.findViewById(R.id.txtAddress);
            txtMiles = view.findViewById(R.id.txtMiles);
            txtPerMiles = view.findViewById(R.id.txtPerMiles);
            txtAmount = view.findViewById(R.id.txtAmount);

            location_layout = view.findViewById(R.id.location_layout);
            btnSelectAddress = view.findViewById(R.id.btnSelectAddress);

            btnSelectAddress.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(option)
                    {
                        Bundle Booking = new Bundle();
                        Booking.putBundle("BookingBundle", BookingBundle);
                        Booking.putBundle("VehicleBundle", VehicleBundle);
                        Booking.putBundle("returnLocation", returnLocationBundle);
                        Booking.putBundle("location", locationBundle);
                        Booking.putBoolean("locationType", locationType);
                        Booking.putBoolean("initialSelect", initialSelect);
                        Booking.putBoolean("option", true);
                        Booking.putDouble("deliveryDistance", distance);
                        Booking.putBoolean("fromMap", true);
                        Booking.putDouble("latitudedelivery",latitudedelivery);
                        Booking.putDouble("longitudedelivery",longitudedelivery);
                        Booking.putString("address",txtAddress.getText().toString());
                        System.out.println(Booking);
                        NavHostFragment.findNavController(Fragment_Map_Screen_Search.this)
                                .navigate(R.id.action_MapScreenSearchAddress_to_FilterByVehicleClass, Booking);

                    }
                    if(!option)
                    {
                        try {
                            String address = getArguments().getString("address");
                            Bundle Booking = new Bundle();
                            Booking.putBundle("BookingBundle", BookingBundle);
                            Booking.putBundle("VehicleBundle", VehicleBundle);
                            Booking.putBundle("returnLocation", returnLocationBundle);
                            Booking.putBundle("location", locationBundle);
                            Booking.putBoolean("locationType", locationType);
                            Booking.putBoolean("initialSelect", initialSelect);
                            Booking.putBoolean("option", false);
                            Booking.putDouble("deliveryDistance", distance);
                            Booking.putBoolean("fromMap", true);
                            Booking.putString("address", address);
                            Booking.putDouble("latitudepickup", latitudepickup);
                            Booking.putDouble("longitudepickup", longitudepickup);
                            System.out.println(Booking);
                            NavHostFragment.findNavController(Fragment_Map_Screen_Search.this)
                                    .navigate(R.id.action_MapScreenSearchAddress_to_FilterByVehicleClass, Booking);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            id = sp.getString(getString(R.string.id), "");

            System.out.println("ID :- " + id);
            Fragment_Map_Screen_Search.context = getActivity();

            EditText edtSearchLocation = view.findViewById(R.id.edtSearchLocation);
            edtSearchLocation.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    try
                    {
                        if(!Places.isInitialized())
                        {
                            Places.initialize(getActivity(), getString(R.string.map_api_key), Locale.US);
                        }
                        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Arrays.asList( Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)).build(context);
                        startActivityForResult(intent, REQUEST_PLACE_ADDRESS);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
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
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    OnResponseListener LocationList = new OnResponseListener()
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
                            final JSONObject Locationlist = resultSet.getJSONObject("distanceModel");

                            distance = Locationlist.getDouble("distance");

                            txtMiles.setText("Total Miles "+Math.round(distance)+"");
                            if(option)
                            {
                                txtPerMiles.setText("USD $"+locationBundle.getDouble("pickLocChargePerMile") + "/Mile");
                                txtAmount.setText("USD $"+(Math.round(distance) * locationBundle.getDouble("pickLocChargePerMile")) + "");
                            }
                            else{
                                txtPerMiles.setText("USD $"+returnLocationBundle.getDouble("pickLocChargePerMile") + "/Mile");
                                txtAmount.setText("USD $"+(Math.round(distance) * returnLocationBundle.getDouble("pickLocChargePerMile")) + "");
                            }
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
        public void onError(String error) {
            System.out.println("Error-" + error);
        }
    };
    final int LOCATION_REQUEST_CODE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        try {
            switch (requestCode)
            {
                case LOCATION_REQUEST_CODE:
                    {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {

                        mapView.getMapAsync(this);

                    } else {
                        CustomToast.showToast(getActivity(),"Please provide the permission",1);
                    }
                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PLACE_ADDRESS && resultCode == Activity.RESULT_OK)
        {
            Place place = Autocomplete.getPlaceFromIntent(data);
            // Log.i(TAG, "Place city and postal code: " + place.getAddress().subSequence(place.getName().length(),place.getAddress().length()));
            try {
                List<Address> addresses;
                geocoder = new Geocoder(context, Locale.getDefault());

                try {

                    JSONObject bodyParam = new JSONObject();
                    try
                    {
                        if(option)
                        {
                            bodyParam.put("lat1", locationBundle.getDouble("latitude"));
                            bodyParam.put("lon1", locationBundle.getDouble("longitude"));

                            latitudedelivery = place.getLatLng().latitude;
                            longitudedelivery = place.getLatLng().longitude;
                        }
                        else{
                            bodyParam.put("lat1", returnLocationBundle.getDouble("latitude"));
                            bodyParam.put("lon1", returnLocationBundle.getDouble("longitude"));

                            latitudepickup = place.getLatLng().latitude;
                            longitudepickup = place.getLatLng().longitude;
                        }
                        bodyParam.put("lat2", place.getLatLng().latitude);
                        bodyParam.put("lon2", place.getLatLng().longitude);
                        bodyParam.put("unit", 1);


                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    AndroidNetworking.initialize(getActivity());

                    ApiService ApiService = new ApiService(LocationList, RequestType.POST,
                            CALCULATE_DISTANCE, BASE_URL_SETTINGS, new HashMap<String, String>(), bodyParam);

                    addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String UnitNo=addresses.get(0).getFeatureName();
                    String Street=addresses.get(0).getThoroughfare();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();

                    Log.e("Address: ", "" + address);
                    Log.e("City: ", "" + city);
                    Log.e("Street: ", "" + Street);
                    Log.e("State: ", "" + state);
                    Log.e("Country: ", "" + country);
                    Log.e("APostalCode: ", "" + postalCode);
                    Log.e("UnitNO: ", "" + UnitNo);


                    String StreetUnit=UnitNo+" "+Street;
                    txtAddress.setText(StreetUnit+", "+city+", "+state+", "+postalCode+", "+country);

                    LatLng locationPin = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationPin));

                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(locationPin)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.gray_marker)));

                    location_layout.setVisibility(View.VISIBLE);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                //setMarker(latLng);
            }
        }
    }
}