package com.ochs.app.fleetowner.registration;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.R;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Fragment_Registration_Profile_1 extends Fragment
{
    private static final int RESULT_CANCELED = 0;
    ImageView Back;
    Handler handler = new Handler();
    public static Context context;
    public String id = "";
    Spinner spCountryList,spStateList;
    public String[] Country,State;
    public int[] CountyId,StateId;
    EditText edtcust_Fullname,edt_CustStreet,edt_CustUnitNo,edt_CustZipCode,edtcust_cityName;
    HashMap<String, Integer> countryhashmap=new HashMap<String, Integer>();
    HashMap<String,Integer>Statehashmap=new HashMap<>();

    private Geocoder geocoder;
    private final int REQUEST_PLACE_ADDRESS = 40;

    ArrayList<String> scanData;
    TextView lblNext,lblDiscard;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_driver_profile_1, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        scanData = getActivity().getIntent().getStringArrayListExtra("scanData");

        try
        {
            super.onViewCreated(view, savedInstanceState);

            edtcust_Fullname = view.findViewById(R.id.edtcust_Firstname);
            edt_CustStreet = view.findViewById(R.id.edt_CustStreet);
            edt_CustUnitNo = view.findViewById(R.id.edt_CustUnitNo);
            edt_CustZipCode = view.findViewById(R.id.edt_CustZipCode);
            edtcust_cityName = view.findViewById(R.id.cust_cityName);
            countryhashmap = new HashMap<String, Integer>();
            lblNext = view.findViewById(R.id.lblNext1);
            Back= view.findViewById(R.id.Backimg);
            spCountryList = view.findViewById(R.id.sp_Countrylist);
            spStateList = view.findViewById(R.id.Sp_Statelist);
            lblDiscard= view.findViewById(R.id.lblDiscard1);

            Back.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    NavHostFragment.findNavController(Fragment_Registration_Profile_1.this)
                            .navigate(R.id.action_DriverProfile_to_CreateProfile);
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
                         //   Places.initialize(getActivity(), getString(R.string.map_api_key), Locale.US);
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
            lblNext.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    try {
                       /* if (edtcust_Fullname.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Your Name.", Toast.LENGTH_LONG).show();
                        else if (edt_CustStreet.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Your Street NO & Name", Toast.LENGTH_LONG).show();
                       // else if (spCountryList.getSelectedItem().toString().equals(""))
                           // Toast.makeText(getActivity(), "Please Select Your Country", Toast.LENGTH_LONG).show();
                     //   else if (spStateList.getSelectedItem().toString().equals(""))
                           // Toast.makeText(getActivity(), "Please Select Your State", Toast.LENGTH_LONG).show();
                        else if (edt_CustZipCode.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Pin/Zip Code", Toast.LENGTH_LONG).show();
                        else if (edtcust_cityName.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Your City Name", Toast.LENGTH_LONG).show();
                        else
                        {
                            Bundle RegistrationBundle=new Bundle();
                            RegistrationBundle.putString("Cust_FName",edtcust_Fullname.getText().toString());
                         //   RegistrationBundle.putString("Cust_Country_Name",spCountryList.getSelectedItem().toString());
                           // RegistrationBundle.putString("Cust_State_Name",spStateList.getSelectedItem().toString());
                            RegistrationBundle.putString("Cust_Street",edt_CustStreet.getText().toString());
                            RegistrationBundle.putString("Cust_UnitNo",edt_CustUnitNo.getText().toString());
                            RegistrationBundle.putString("Cust_ZipCode",edt_CustZipCode.getText().toString());
                            RegistrationBundle.putString("Cust_City",edtcust_cityName.getText().toString());

                            Bundle Registration=new Bundle();
                            Registration.putBundle("RegistrationBundle",RegistrationBundle);

                            System.out.println(Registration);
                            }*/
                            NavHostFragment.findNavController(Fragment_Registration_Profile_1.this)
                                    .navigate(R.id.action_DriverProfile_to_DriverProfile2);

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            AndroidNetworking.initialize(getActivity());
            Fragment_Registration_Profile_1.context = getActivity();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}