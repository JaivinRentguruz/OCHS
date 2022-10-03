package com.ochs.app.flexiicar_app.login;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.R;
import com.ochs.app.RentOption;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import com.ochs.app.flexiicar_app.booking.Fragment_Add_credit_card;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_SETTINGS;
import static com.ochs.app.apicall.ApiEndPoint.GETCOUNTRYLIST;
import static com.ochs.app.apicall.ApiEndPoint.STATELIST;

public class Fragment_Driver_Profile_1 extends Fragment
{
    private static final int RESULT_CANCELED = 0;
    ImageView Back;
    Handler handler = new Handler();
    public static Context context;
    public String id = "";
    Spinner spCountryList,spStateList;
    public String[] Country,State;
    public int[] CountyId,StateId;
    TextView lblDiscard,lblNext;
    EditText edtcust_Firstname,edtcust_Lastname,edt_CustStreet,edt_CustUnitNo,edt_CustZipCode,edtcust_cityName;
    HashMap<String, Integer> countryhashmap=new HashMap<String, Integer>();
    HashMap<String,Integer>Statehashmap=new HashMap<>();

    private Geocoder geocoder;
    private final int REQUEST_PLACE_ADDRESS = 40;

    ArrayList<String> scanData;
    Bundle RegistrationBundle;

    int Cust_Country_ID,Cust_State_ID;
    Boolean back = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_driver_profile_1, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        try
        {
            super.onViewCreated(view, savedInstanceState);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            AndroidNetworking.initialize(getActivity());
            Fragment_Driver_Profile_1.context = getActivity();

            scanData = getActivity().getIntent().getStringArrayListExtra("scanData");

            edtcust_Firstname = view.findViewById(R.id.edtcust_Firstname);
            edtcust_Lastname = view.findViewById(R.id.edtcust_Lastname);
            edt_CustStreet = view.findViewById(R.id.edt_CustStreet);
            edt_CustUnitNo = view.findViewById(R.id.edt_CustUnitNo);
            edt_CustZipCode = view.findViewById(R.id.edt_CustZipCode);
            edtcust_cityName = view.findViewById(R.id.cust_cityName);
            countryhashmap = new HashMap<String, Integer>();
            lblNext = view.findViewById(R.id.lblNext1);
            Back= view.findViewById(R.id.Backimg);
            spCountryList = view.findViewById(R.id.sp_Countrylist);
            spStateList = view.findViewById(R.id.Sp_Statelist);
            lblDiscard=view.findViewById(R.id.lblDiscard1);
            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            id = sp.getString(getString(R.string.id), "");

            if(getArguments()!=null)
            {
                RegistrationBundle=getArguments().getBundle("RegistrationBundle");
                System.out.println(RegistrationBundle);

                String Cust_FName=RegistrationBundle.getString("Cust_FName");
                edtcust_Firstname.setText(Cust_FName);

                String Cust_Street=RegistrationBundle.getString("Cust_Street");
                edt_CustStreet.setText(Cust_Street);

                String Cust_UnitNo=RegistrationBundle.getString("Cust_UnitNo");
                edt_CustUnitNo.setText(Cust_UnitNo);

                String Cust_ZipCode=RegistrationBundle.getString("Cust_ZipCode");
                edt_CustZipCode.setText(Cust_ZipCode);

                String Cust_City=RegistrationBundle.getString("Cust_City");
                edtcust_cityName.setText(Cust_City);

                Cust_Country_ID=RegistrationBundle.getInt("Cust_Country_ID");

                Cust_State_ID=RegistrationBundle.getInt("Cust_State_ID");

                back = true;
            }

            Back.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    NavHostFragment.findNavController(Fragment_Driver_Profile_1.this)
                            .navigate(R.id.action_DriverProfile_to_CreateProfile);
                }
            });

            lblDiscard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are You Sure You Want To Discard?");
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Intent i = new Intent(getActivity(), Login.class);
                                    startActivity(i);
                                }
                            });
                    builder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            });

                    final AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            edt_CustStreet.setOnClickListener(new View.OnClickListener()
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
                        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Arrays.asList( Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)).build(getActivity());
                        startActivityForResult(intent, REQUEST_PLACE_ADDRESS);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            lblNext.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    try {
                        if (edtcust_Firstname.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Enter Driver's Name.",1);
                        else if (edt_CustStreet.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Enter Street NO & Name",1);
                        else if (spCountryList.getSelectedItem().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Select Your Country",1);
                        else if (spStateList.getSelectedItem().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Select Your State",1);
                        else if (edt_CustZipCode.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Enter Pin/Zip Code",1);
                        else if (edtcust_cityName.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Enter Your City Name",1);
                        else
                        {
                            Bundle RegistrationBundle=new Bundle();
                            RegistrationBundle.putString("Cust_FName",edtcust_Firstname.getText().toString());
                            int s = countryhashmap.get(spCountryList.getSelectedItem());
                            RegistrationBundle.putInt("Cust_Country_ID",s);
                            RegistrationBundle.putString("Cust_Country_Name",spCountryList.getSelectedItem().toString());
                            int s1 = Statehashmap.get(spStateList.getSelectedItem());
                            RegistrationBundle.putInt("Cust_State_ID",s1);
                            RegistrationBundle.putString("Cust_State_Name",spStateList.getSelectedItem().toString());
                            RegistrationBundle.putString("Cust_Street",edt_CustStreet.getText().toString());
                            RegistrationBundle.putString("Cust_UnitNo",edt_CustUnitNo.getText().toString());
                            RegistrationBundle.putString("Cust_ZipCode",edt_CustZipCode.getText().toString());
                            RegistrationBundle.putString("Cust_City",edtcust_cityName.getText().toString());

                            Bundle Registration=new Bundle();
                            Registration.putBundle("RegistrationBundle",RegistrationBundle);
                            Registration.putBoolean("BackTo", back);
                            System.out.println(Registration);
                            NavHostFragment.findNavController(Fragment_Driver_Profile_1.this)
                                    .navigate(R.id.action_DriverProfile_to_DriverProfile2,Registration);
                        }
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

        try
        {
            String bodyParam = "";
            ApiService ApiService = new ApiService(CountryList, RequestType.GET,
                    GETCOUNTRYLIST, BASE_URL_SETTINGS, new HashMap<String, String>(), bodyParam);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(scanData != null)
        {
            for (String data : scanData)
            {
                String[] datas = data.split(":");
                if (datas[0].equals("Given Name"))
                    edtcust_Firstname.setText(datas[1]);
                else if (datas[0].equals("Address Line 1"))
                    edt_CustStreet.setText(datas[1]);
                else if (datas[0].equals("Address City"))
                    edtcust_cityName.setText(datas[1]);
                else if (datas[0].equals("Zipcode"))
                    edt_CustZipCode.setText(datas[1]);
            }
        }
    }

    //Countrylist
    OnResponseListener CountryList = new OnResponseListener()
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
                            final JSONArray Countrylist = resultSet.getJSONArray("t0040_Country_Master");

                            System.out.println(Countrylist);

                            int len;
                            len = Countrylist.length();

                            Country = new String[len];
                            CountyId = new int[len];
                            int position = 0;
                            for (int j = 0; j < len; j++)
                            {
                                final JSONObject test = (JSONObject) Countrylist.get(j);
                                final int country_ID = test.getInt("country_ID");
                                final String country_Name = test.getString("country_Name");
                                Country[j] = country_Name;
                                CountyId[j] = country_ID;

                                countryhashmap.put(country_Name,country_ID);

                                if(country_ID==Cust_Country_ID)
                                {
                                    position = j;
                                }
                            }
                            ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(getActivity().getApplication(), R.layout.spinner_layout, R.id.text1, Country);
                            spCountryList.setAdapter(adapterCategories);
                            spCountryList.setSelection(position);

                            spCountryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                            {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                                {
                                    try {
                                        int s = countryhashmap.get(spCountryList.getSelectedItem());
                                        System.out.println(spCountryList.getSelectedItem());
                                        System.out.println(s + "");

                                        try {
                                            String bodyParam1 = "countryId=" + s;
                                            ApiService ApiService = new ApiService(StateList, RequestType.GET,
                                                    STATELIST, BASE_URL_SETTINGS, new HashMap<String, String>(), bodyParam1);
                                        } catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    } catch (Exception e) {
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
    //StateList
    OnResponseListener StateList = new OnResponseListener()
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
                            final JSONArray StateList = resultSet.getJSONArray("t0040_State_Master");

                            int len;
                            len = StateList.length();

                            StateId = new int[len];
                            State = new String[len];
                            int position = 0;

                            for (int j = 0; j < len; j++)
                            {
                                final JSONObject test = (JSONObject) StateList.get(j);
                                final int state_ID = test.getInt("state_ID");
                                final String state_Name = test.getString("state_Name");

                                State[j] = state_Name;
                                StateId[j] = state_ID;

                                Statehashmap.put(state_Name,state_ID);


                                if(state_ID==Cust_State_ID)
                                {
                                    position = j;
                                }
                            }

                            ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, R.id.text1, State);
                            spStateList.setAdapter(adapterCategories);
                            spStateList.setSelection(position);
                        }
                        else
                        {
                            String msg = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),msg,1);
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
                    Log.e("PostalCode: ", "" + postalCode);
                    Log.e("UnitNO: ", "" + UnitNo);

                    edt_CustStreet.setText(Street);
                    edtcust_cityName.setText(city);
                    edt_CustZipCode.setText(postalCode);
                    edt_CustUnitNo.setText(UnitNo);

                    for(int k=0;k<State.length;k++)
                    {

                        if(State[k].equals(state))
                        {
                            spStateList.setSelection(k);
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
                    }

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