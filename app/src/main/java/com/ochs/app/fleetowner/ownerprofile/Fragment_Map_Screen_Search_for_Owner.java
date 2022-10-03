package com.ochs.app.fleetowner.ownerprofile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Fragment_Map_Screen_Search_for_Owner extends Fragment implements OnMapReadyCallback
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

    TextView txtStreet,txtcity,txtstate,txtpostalCode,txtcountry;

    RelativeLayout location_layout;

    Button btnSelectAddress;

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

            close_symbol=view.findViewById(R.id.close_symbol);
          //  txtStreet= view.findViewById(R.id.txtStreet);
            txtcity=view.findViewById(R.id.lblcity);
           // txtstate= view.findViewById(R.id.txtstate);
           // txtpostalCode= view.findViewById(R.id.txtpostalCode);
           // txtcountry= view.findViewById(R.id.txtcountry);


            close_symbol.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    location_layout.setVisibility(View.GONE);
                    location_layout.removeAllViews();
                }
            });

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

            location_layout = view.findViewById(R.id.location_layout);
            btnSelectAddress = view.findViewById(R.id.btnSelectAddress);

            btnSelectAddress.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle Location = new Bundle();
                    Location.putString("Street",txtStreet.getText().toString());
                    Location.putString("City",txtcity.getText().toString());
                    Location.putString("State",txtstate.getText().toString());
                    Location.putString("PostalCode",txtpostalCode.getText().toString());
                    Location.putString("Country",txtcountry.getText().toString());
                    Location.putBoolean("FromMap",true);
                    System.out.println(Location);
                    NavHostFragment.findNavController(Fragment_Map_Screen_Search_for_Owner.this)
                       .navigate(R.id.action_MapScreenSearch_to_ListYourCar_4, Location);

                }
            });
            Fragment_Map_Screen_Search_for_Owner.context = getActivity();

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
                        Toast.makeText(getActivity(), "Please provide the permission", Toast.LENGTH_LONG).show();
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
                    addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String Street=addresses.get(0).getFeatureName();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();

                    txtStreet.setText(Street+" ,");
                    txtcity.setText(city+" ,");
                    txtstate.setText(state);
                    txtpostalCode.setText(postalCode+" ,");
                    txtcountry.setText(country);

                    Log.e("Address: ", "" + address);
                    Log.e("City: ", "" + city);
                    Log.e("Street: ", "" + Street);
                    Log.e("State: ", "" + state);
                    Log.e("Country: ", "" + country);
                    Log.e("APostalCode: ", "" + postalCode);

                    LatLng locationPin = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationPin));

                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(locationPin)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.gray_marker)));

                    location_layout.setVisibility(View.VISIBLE);

                }
                catch (IOException e)
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