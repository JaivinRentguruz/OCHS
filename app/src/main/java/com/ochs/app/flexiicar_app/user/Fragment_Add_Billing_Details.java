package com.ochs.app.flexiicar_app.user;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.ochs.app.apicall.ApiEndPoint.ADD_BILLING;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_SETTINGS;
import static com.ochs.app.apicall.ApiEndPoint.GETCOUNTRYLIST;
import static com.ochs.app.apicall.ApiEndPoint.STATELIST;

public class Fragment_Add_Billing_Details extends Fragment
{
    ImageView Back;
    Spinner sp_Countrylist,Sp_Statelist;
    EditText BillingName,EmailId,StreetNo,City,Unit_No,ZipCode,ClaimNo;
    TextView lbl_Submit,ClaimDate;
    HashMap<String, Integer> countryhashmap=new HashMap<String, Integer>();
    HashMap<String,Integer>Statehashmap=new HashMap<>();
    public String[] Country,State;
    public int[] CountyId,StateId;
    Handler handler = new Handler();
    private final int REQUEST_PLACE_ADDRESS=40;
    private Geocoder geocoder;
    public static Context context;
    Bundle ReservationBundle;
    int cDay, cMonth, cYear,hour1,minutes1;
    String BillClaimDateTime;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_billing_details, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        try {
            super.onViewCreated(view, savedInstanceState);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            ((User_Profile) getActivity()).BottomnavInVisible();

            ReservationBundle = getArguments().getBundle("ReservationBundle");

            sp_Countrylist = view.findViewById(R.id.sp_Countrylist);
            Sp_Statelist = view.findViewById(R.id.Sp_Statelist);
            BillingName = view.findViewById(R.id.BillingName);
            EmailId = view.findViewById(R.id.EmailId);
            StreetNo = view.findViewById(R.id.StreetNo);
            City = view.findViewById(R.id.City);
            Unit_No = view.findViewById(R.id.UnitNo);
            ZipCode = view.findViewById(R.id.ZipCode);
            ClaimNo = view.findViewById(R.id.ClaimNo);
            ClaimDate = view.findViewById(R.id.ClaimDate);
            lbl_Submit = view.findViewById(R.id.lbl_Submit);

            Fragment_Add_Billing_Details.context = getActivity();

            Back = view.findViewById(R.id.Back);
            Back.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle Reservation = new Bundle();
                    Reservation.putBundle("ReservationBundle", ReservationBundle);
                    NavHostFragment.findNavController(Fragment_Add_Billing_Details.this).
                            navigate(R.id.action_AddBillingDetails_to_SummaryOfCharges, Reservation);
                }
            });

            ClaimDate.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Calendar getDate=Calendar.getInstance();
                    cDay = getDate.get (Calendar.DAY_OF_MONTH);
                    cMonth = getDate.get(Calendar.MONTH);
                    cYear = getDate.get(Calendar.YEAR);

                    DatePickerDialog datePicker = new DatePickerDialog(getActivity(),R.style.DialogTheme, new DatePickerDialog.OnDateSetListener()
                    {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                        {
                            cYear = year;
                            cMonth = month;
                            cDay = dayOfMonth;

                            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener()
                            {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                                {

                                    final String AM_PM;
                                    String mm_precede = "";
                                    String hh_precede = "";
                                    String dd_Precede=" ";

                                    if (hourOfDay > 12)
                                    {
                                        hourOfDay -= 12;
                                        AM_PM = " PM";
                                    }
                                    else if (hourOfDay==0)
                                    {
                                        hourOfDay += 12;
                                        AM_PM = " AM";
                                    }
                                    else if (hourOfDay == 12)
                                        AM_PM = " PM";
                                    else
                                        AM_PM = " AM";

                                    if (minute <10)
                                    {
                                        mm_precede = "0";
                                    }
                                    if (hourOfDay<10)
                                    {
                                        hh_precede = "0";
                                    }

                                    hour1 = hourOfDay;
                                    minutes1 = minute;
                                    if (cDay <10)
                                    {
                                        dd_Precede = "0";
                                    }
                                    String monthName;
                                    if(cMonth<9)
                                        monthName="0"+(cMonth+1);
                                    else
                                        monthName = (cMonth+1)+"";

                                    ClaimDate.setText(cYear+ "-" +monthName+ "-" +dd_Precede+cDay+ " "+hh_precede +hour1 + ":" + mm_precede +minutes1 + AM_PM);
                                }

                            },hour1,minutes1,false);
                            timePickerDialog.show();
                        }
                    },cYear,cMonth,cDay);
                    datePicker.show();
                }
            });

            lbl_Submit.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (BillingName.getText().toString().equals(""))
                        CustomToast.showToast(getActivity(), "Please Enter Billing Name!", 1);
                    else if (EmailId.getText().toString().equals(""))
                        CustomToast.showToast(getActivity(), "Please Enter Email id!", 1);
                    else if (City.getText().toString().equals(""))
                        CustomToast.showToast(getActivity(), "Please Enter City!", 1);
                    else if (ClaimNo.getText().toString().equals(""))
                        CustomToast.showToast(getActivity(), "Please Enter Bill Claim Number!", 1);
                    else if (ClaimDate.getText().toString().equals(""))
                        CustomToast.showToast(getActivity(), "Please Enter Bill Claim Date!", 1);
                    else {
                        JSONObject bodyParam = new JSONObject();
                        try {

                            try {
                                String ClaimDateTime= ClaimDate.getText().toString();
                                SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
                                Date date1 = dateFormat1.parse(ClaimDateTime);
                                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                                BillClaimDateTime = sdf1.format(date1);
                                System.out.println(BillClaimDateTime);
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                            String country = String.valueOf(sp_Countrylist.getSelectedItem());
                            String state = String.valueOf(Sp_Statelist.getSelectedItem());
                            String street= StreetNo.getText().toString();
                            String city= City.getText().toString();
                            String zipcode= ZipCode.getText().toString();

                            bodyParam.accumulate("Agreement_ID",ReservationBundle.getInt("reservation_ID"));
                            bodyParam.accumulate("Billing_by",0);
                            bodyParam.accumulate("Name", BillingName.getText().toString());
                            bodyParam.accumulate("Address",street+" "+city+" "+state+" "+country+" "+zipcode);
                            bodyParam.accumulate("Email", EmailId.getText().toString());
                            bodyParam.accumulate("Billing_Street", StreetNo.getText().toString());
                            bodyParam.accumulate("Billing_UnitNo", Unit_No.getText().toString());
                            bodyParam.accumulate("Billing_City", City.getText().toString());
                            bodyParam.accumulate("Billing_ZipCode", ZipCode.getText().toString());

                            int s = countryhashmap.get(sp_Countrylist.getSelectedItem());
                            int s1 = Statehashmap.get(Sp_Statelist.getSelectedItem());

                            bodyParam.accumulate("Billing_State_ID", s1);
                            bodyParam.accumulate("Billing_Country_ID", s);

                            bodyParam.accumulate("BillClaimNumber", ClaimNo.getText().toString());
                            bodyParam.accumulate("BillClaimDate",BillClaimDateTime);
                            System.out.println(bodyParam);
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        ApiService ApiService1 = new ApiService(AddBilling, RequestType.POST,
                                ADD_BILLING, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
                    }
                }
            });

            StreetNo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    try {
                        if (!Places.isInitialized())
                        {
                            Places.initialize(getActivity(), getString(R.string.map_api_key), Locale.US);
                        }
                        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)).build(context);
                        startActivityForResult(intent, REQUEST_PLACE_ADDRESS);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            try {
                String bodyParam = "";
                ApiService ApiService = new ApiService(CountryList, RequestType.GET,
                        GETCOUNTRYLIST, BASE_URL_SETTINGS, new HashMap<String, String>(), bodyParam);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
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

                            int len;
                            len = Countrylist.length();

                            Country = new String[len];
                            CountyId = new int[len];
                            for (int j = 0; j < len; j++)
                            {
                                final JSONObject test = (JSONObject) Countrylist.get(j);
                                final int country_ID = test.getInt("country_ID");
                                final String country_Name = test.getString("country_Name");
                                Country[j] = country_Name;
                                CountyId[j] = country_ID;

                                countryhashmap.put(country_Name,country_ID);
                            }

                            ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(getContext().getApplicationContext(), R.layout.spinner_layout, R.id.text1, Country);
                            sp_Countrylist.setAdapter(adapterCategories);
                            sp_Countrylist.setSelection(0);

                            sp_Countrylist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                            {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                                {
                                    try
                                    {
                                        int s = countryhashmap.get(sp_Countrylist.getSelectedItem());
                                        System.out.println(sp_Countrylist.getSelectedItem());
                                        System.out.println(s + "");
                                        try
                                        {
                                            String bodyParam1 = "countryId="+s;
                                            ApiService ApiService = new ApiService(StateList, RequestType.GET,
                                                    STATELIST, BASE_URL_SETTINGS, new HashMap<String, String>(), bodyParam1);
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

                            for (int j = 0; j < len; j++)
                            {
                                final JSONObject test = (JSONObject) StateList.get(j);
                                final int state_ID = test.getInt("state_ID");
                                final String state_Name = test.getString("state_Name");

                                State[j] = state_Name;
                                StateId[j] = state_ID;
                                Statehashmap.put(state_Name,state_ID);
                            }

                            ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(getContext().getApplicationContext(), R.layout.spinner_layout, R.id.text1, State);
                            Sp_Statelist.setAdapter(adapterCategories);
                            Sp_Statelist.setSelection(0);
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

    OnResponseListener AddBilling = new OnResponseListener()
    {
        @Override
        public void onSuccess(final String response, HashMap<String, String> headers)
        {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Success");
                        System.out.println(response);

                        JSONObject responseJSON = new JSONObject(response);
                        Boolean status = responseJSON.getBoolean("status");

                        if (status)
                        {
                            String msg = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),msg,0);

                            Bundle Reservation= new Bundle();
                            Reservation.putBundle("ReservationBundle", ReservationBundle);
                            NavHostFragment.findNavController(Fragment_Add_Billing_Details.this).
                                    navigate(R.id.action_AddBillingDetails_to_SummaryOfCharges,Reservation);
                        }
                        else
                        {
                            String msg = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),msg,1);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        @Override
        public void onError(String error)
        {
            System.out.println("Error" + error);
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
                    Log.e("APostalCode: ", "" + postalCode);
                    Log.e("UnitNO: ", "" + UnitNo);

                    StreetNo.setText(Street);
                    City.setText(city);
                    ZipCode.setText(postalCode);
                    Unit_No.setText(UnitNo);

                    for(int i=0;i<State.length;i++)
                    {

                        if(State[i].equals(state))
                        {
                            Sp_Statelist.setSelection(i);
                            break;
                        }
                    }

                    for(int j=0;j<Country.length;j++)
                    {

                        if(Country[j].equals(country))
                        {
                            sp_Countrylist.setSelection(j);
                            break;
                        }
                    }

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                //setMarker(latLng);
            }
        }
    }
}
