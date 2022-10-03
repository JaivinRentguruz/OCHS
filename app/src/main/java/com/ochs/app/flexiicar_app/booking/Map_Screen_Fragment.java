package com.ochs.app.flexiicar_app.booking;

import android.Manifest;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.adapters.Location;
import com.ochs.app.flexiicar_app.commonfragment.CustomInfoWindowAdapter;
import com.ochs.app.flexiicar_app.user.User_Profile;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.LOCATION_LIST;
import static com.ochs.app.apicall.ApiEndPoint.LOCATION_SEARCH_BY_DISTANCE;

public class Map_Screen_Fragment extends Fragment implements OnMapReadyCallback
{
    private MapView mapView;
    private GoogleMap googleMap;
    LinearLayout profile_icon;
    public static Context context;
    public String id = "";
    Handler handler = new Handler();
    Marker previousMarker;
    private final int REQUEST_PLACE_ADDRESS = 40;
    private Geocoder geocoder;
    final int LOCATION_REQUEST_CODE = 1;
    RelativeLayout lblmenuicon;
    ArrayList<Location> locationList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_map_screen, container, false);
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

            mapView = (MapView) view.findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);

            Map_Screen_Fragment.context = getActivity();
            MapsInitializer.initialize(this.getActivity());

            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE}, LOCATION_REQUEST_CODE);
            }
            else
            {
                mapView.getMapAsync(this);
            }

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            id = sp.getString(getString(R.string.id), "");

            System.out.println("ID :- " + id);

            lblmenuicon= view.findViewById(R.id.media_mixer_icon);
            profile_icon = view.findViewById(R.id.profile_icon);
            ImageView homeiconimage=view.findViewById(R.id.homeiconimage);
            homeiconimage.setImageResource(R.drawable.ic_tab_home_icon_green);

            profile_icon.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (!id.equals(""))
                    {
                        Intent i = new Intent(getActivity(), User_Profile.class);
                        startActivity(i);
                    }
                    else {
                        CustomToast.showToast(getActivity(),"Please Login.",1);
                    }
                }
            });

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

            JSONObject bodyParam = new JSONObject();
            try
            {
                bodyParam.accumulate("createdById", "0");
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            AndroidNetworking.initialize(getActivity());

            ApiService ApiService = new ApiService(LocationList, RequestType.GET,
                    LOCATION_LIST, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
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
                            final JSONArray Locationlist = resultSet.getJSONArray("v0040_Location_Master");

                            int len;
                            len = Locationlist.length();

                            for (int j = 0; j < len; j++)
                            {
                                final JSONObject test = (JSONObject) Locationlist.get(j);

                                Location location = new Location();

                                location.setLatitude(test.getDouble("latitude"));
                                location.setLongitude(test.getDouble("longitude"));
                                location.setLocationName(test.getString("location_Name"));
                                location.setStateName(test.getString("state_Name"));
                                location.setZipcode(test.getString("zipcode"));
                                location.setCity(test.getString("city"));
                                location.setStreet(test.getString("street"));
                                location.setStateId(test.getInt("state_ID"));
                                location.setCmpId(test.getInt("cmp_ID"));
                                location.setActive(test.getString("active"));
                                location.setIsVirtualName(test.getString("isVirtual_Name"));
                                location.setEmail(test.getString("email"));
                                location.setCountryId(test.getInt("country_ID"));
                                location.setCountryName(test.getString("country_Name"));
                                location.setPhoneno(test.getString("phoneno"));
                                location.setContactName(test.getString("contactName"));
                                location.setLocationId(test.getInt("location_ID"));
                                location.setLocationCount(test.getInt("locationCount"));
                                location.setUnitNo(test.getString("unitNo"));
                                location.setPickLocChargePerMile(test.getDouble("pickLocChargePerMile"));

                                LatLng locationPin = new LatLng(location.getLatitude(), location.getLongitude());

                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                               // googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                                Marker marker = googleMap.addMarker(new MarkerOptions()
                                        .position(locationPin)
                                        .title(location.getLocationName())
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.gray_marker))
                                        .snippet(location.getStateName()));
                                if(j==0)
                                {
                                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.green_marker));
                                    previousMarker=marker;
                                    CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(getActivity());
                                    googleMap.setInfoWindowAdapter(adapter);
                                    marker.showInfoWindow();

                                }
                                locationList.add(location);
                            }
                            if(locationList.size()>0)
                            {
                                final Location location = locationList.get(0);
                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                                final String locationName = location.getLocationName();
                                final String Street = location.getStreet();
                                final String city=location.getCity();
                                final String country_Name = location.getCountryName();
                                final String zipcode = location.getZipcode();
                                final String phoneno = location.getPhoneno();

                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                LinearLayout view = (LinearLayout) inflater.inflate(R.layout.location_list_layout, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                view.setLayoutParams(lp);

                                final TextView lbllocationName,lblstreet, lblcity, lblzip, lblCountary, lbl_Phone_no;
                                lbllocationName=view.findViewById(R.id.lbl_locTitle);
                                lblstreet = view.findViewById(R.id.lblstreet);
                                lblcity = view.findViewById(R.id.lblcity);
                                lblzip = view.findViewById(R.id.lblzip);
                                lblCountary = view.findViewById(R.id.lblCountary);
                                lbl_Phone_no = view.findViewById(R.id.lbl_Phone_no);
                                LinearLayout selectlocation = (LinearLayout) view.findViewById(R.id.txt_Select_layout);

                                selectlocation.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Bundle locationBundle = new Bundle();
                                        locationBundle.putString("location_Name", location.getLocationName());
                                        locationBundle.putString("street",location.getStreet());
                                        locationBundle.putString("city",location.getCity());
                                        locationBundle.putString("zipcode",location.getZipcode());
                                        locationBundle.putInt("location_ID",location.getLocationId());
                                        locationBundle.putDouble("latitude", location.getLatitude());
                                        locationBundle.putDouble("longitude", location.getLongitude());
                                        locationBundle.putDouble("pickLocChargePerMile", location.getPickLocChargePerMile());

                                        Bundle returnLocationBundle = new Bundle();
                                        returnLocationBundle.putString("location_Name", location.getLocationName());
                                        returnLocationBundle.putString("street",location.getStreet());
                                        returnLocationBundle.putString("city",location.getCity());
                                        returnLocationBundle.putString("zipcode",location.getZipcode());
                                        returnLocationBundle.putInt("location_ID",location.getLocationId());
                                        returnLocationBundle.putDouble("latitude", location.getLatitude());
                                        returnLocationBundle.putDouble("longitude", location.getLongitude());
                                        returnLocationBundle.putDouble("pickLocChargePerMile", location.getPickLocChargePerMile());

                                        Bundle locations = new Bundle();
                                        locations.putBundle("location",locationBundle);
                                        locations.putBundle("returnLocation",returnLocationBundle);
                                        locations.putBoolean("initialSelect",true);
                                        locations.putDouble("latitude", location.getLatitude());
                                        locations.putDouble("longitude", location.getLongitude());
                                        locations.putDouble("pickLocChargePerMile", location.getPickLocChargePerMile());

                                        NavHostFragment.findNavController(Map_Screen_Fragment.this)
                                                .navigate(R.id.action_Search_activity_to_Selected_location, locations);
                                    }
                                });

                                lblmenuicon.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        Bundle locationBundle = new Bundle();
                                        locationBundle.putInt("location_ID", location.getLocationId());
                                        locationBundle.putString("location_Name", location.getLocationName());
                                        locationBundle.putString("street", location.getStreet());
                                        locationBundle.putString("city", location.getCity());
                                        locationBundle.putString("zipcode", location.getZipcode());

                                        Bundle returnLocationBundle = new Bundle();
                                        returnLocationBundle.putString("location_Name", location.getLocationName());
                                        returnLocationBundle.putString("street",location.getStreet());
                                        returnLocationBundle.putString("city",location.getCity());
                                        returnLocationBundle.putString("zipcode",location.getZipcode());
                                        returnLocationBundle.putInt("location_ID",location.getLocationId());

                                        Bundle locations = new Bundle();
                                        locations.putBundle("location", locationBundle);
                                        locations.putBundle("returnLocation", returnLocationBundle);
                                        locations.putBoolean("initialSelect", true);
                                        System.out.println(locations);
                                        NavHostFragment.findNavController(Map_Screen_Fragment.this)
                                                .navigate(R.id.action_Search_activity_to_Available_location, locations);
                                    }
                                });

                                final RelativeLayout location_layout = getActivity().findViewById(R.id.location_layout);

                                location_layout.addView(view);
                                lbllocationName.setText(locationName);
                                lblstreet.setText(Street);
                                lblcity.setText(city);
                                lblzip.setText(zipcode);
                               // txtCountaryname.setText(country_Name);
                                lbl_Phone_no.setText(phoneno);
                            }

                            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
                            {
                                @Override
                                public boolean onMarkerClick(Marker marker)
                                {
                                    final RelativeLayout location_layout = getActivity().findViewById(R.id.location_layout);
                                    if (previousMarker != null)
                                    {
                                        previousMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.gray_marker));
                                    }
                                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.green_marker));
                                    previousMarker = marker;

                                    location_layout.removeAllViews();

                                    for (final Location location:locationList)
                                    {
                                        if(location.getLatitude() == marker.getPosition().latitude && location.getLongitude() == marker.getPosition().longitude)
                                        {
                                            final String locationName = location.getLocationName();
                                            final String Street = location.getStreet();
                                            final String city=location.getCity();
                                            final String country_Name = location.getCountryName();
                                            final String zipcode = location.getZipcode();
                                            final String phoneno = location.getPhoneno();

                                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            LinearLayout view = (LinearLayout) inflater.inflate(R.layout.location_list_layout, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                            view.setLayoutParams(lp);

                                            final TextView txtlocationName,txtStreet, txtCity, txtZipcode, txtCountaryname, txtPhoneNo;
                                            txtlocationName=view.findViewById(R.id.lbl_locTitle);
                                            txtStreet = view.findViewById(R.id.lblstreet);
                                            txtCity = view.findViewById(R.id.lblcity);
                                            txtZipcode = view.findViewById(R.id.lblzip);
                                            txtCountaryname = view.findViewById(R.id.lblCountary);
                                            txtPhoneNo = view.findViewById(R.id.lbl_Phone_no);

                                            LinearLayout selectlocation = (LinearLayout) view.findViewById(R.id.txt_Select_layout);
                                            selectlocation.setOnClickListener(new View.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(View view)
                                                {
                                                    Bundle locationBundle = new Bundle();
                                                    locationBundle.putString("location_Name", location.getLocationName());
                                                    locationBundle.putString("street",location.getStreet());
                                                    locationBundle.putString("city",location.getCity());
                                                    locationBundle.putString("zipcode",location.getZipcode());
                                                    locationBundle.putInt("location_ID",location.getLocationId());

                                                    Bundle returnLocationBundle = new Bundle();
                                                    returnLocationBundle.putString("location_Name", location.getLocationName());
                                                    returnLocationBundle.putString("street",location.getStreet());
                                                    returnLocationBundle.putString("city",location.getCity());
                                                    returnLocationBundle.putString("zipcode",location.getZipcode());
                                                    returnLocationBundle.putInt("location_ID",location.getLocationId());

                                                    Bundle locations = new Bundle();
                                                    locations.putBundle("location",locationBundle);
                                                    locations.putBundle("returnLocation",returnLocationBundle);
                                                    locations.putBoolean("initialSelect",true);
                                                    NavHostFragment.findNavController(Map_Screen_Fragment.this)
                                                            .navigate(R.id.action_Search_activity_to_Selected_location, locations);

                                                }
                                            });
                                            location_layout.addView(view);
                                            txtlocationName.setText(locationName);
                                            txtStreet.setText(Street);
                                            txtCity.setText(city);
                                            txtZipcode.setText(zipcode);
                                            txtCountaryname.setText(country_Name);
                                            txtPhoneNo.setText(phoneno);
                                        }
                                    }
                                    return false;
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
                    } else
                        {
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
                        bodyParam.put("startlat", place.getLatLng().latitude);
                        bodyParam.put("startlng", place.getLatLng().longitude);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    AndroidNetworking.initialize(getActivity());

                    ApiService ApiService = new ApiService(LocationList, RequestType.POST,
                            LOCATION_SEARCH_BY_DISTANCE, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);

                    /*addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String Street=addresses.get(0).getFeatureName();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();

                    Log.e("Address: ", "" + address);
                    Log.e("City: ", "" + city);
                    Log.e("Street: ", "" + Street);
                    Log.e("State: ", "" + state);
                    Log.e("Country: ", "" + country);
                    Log.e("APostalCode: ", "" + postalCode);

                    edt_CustStreet.setText(Street);
                    edtcust_cityName.setText(city);
                    edt_CustZipCode.setText(postalCode);

                    for(int i=0;i<State.length;i++)
                    {

                        if(State[i].equals(state))
                        {
                            spStateList.setSelection(i);
                            break;
                        }
                    }

                    for(int j=0;j<Country.length;j++)
                    {

                        if(Country[j].equals(country))
                        {
                            spCountryList.setSelection(j);
                            break;
                        }
                    }*/
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}