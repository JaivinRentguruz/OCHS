package com.ochs.app.flexiicar_app.login;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import com.ochs.app.flexiicar_app.booking.Booking_Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_LOGIN;
import static com.ochs.app.apicall.ApiEndPoint.LOGIN_VERIFICATION;

public class Fragment_Register_Completed extends Fragment
{
    LinearLayout LayoutNotNow,AllowAccess,lblexplore;
    RelativeLayout relativeLayout;
    int count = 0;
    Handler handler=new Handler();
    public String id = "";
    Bundle RegistrationBundle;
    String CustEmail,Password;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_completed, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        lblexplore = view.findViewById(R.id.lblexplore);
        relativeLayout=view.findViewById(R.id.sucessfull_regi);
        AllowAccess=view.findViewById(R.id.AllowAccess);
        LayoutNotNow=view.findViewById(R.id.layoutNotNow);

        SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
        id = sp.getString(getString(R.string.id), "");

        RegistrationBundle = getArguments().getBundle("RegistrationBundle");

        CustEmail=RegistrationBundle.getString("Cust_Email");
        Password=RegistrationBundle.getString("PasswordHash");

        lblexplore.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lblexplore.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        });

        LayoutNotNow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                relativeLayout.setVisibility(View.GONE);
                lblexplore.setVisibility(View.VISIBLE);
            }
        });

        AndroidNetworking.initialize(getActivity());

        AllowAccess.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                else
                    {
                        JSONObject bodyParam = new JSONObject();
                        try
                        {
                            bodyParam.accumulate("Cust_Email",CustEmail);
                            bodyParam.accumulate("PasswordHash",Password);
                            System.out.println(bodyParam);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        ApiService ApiService = new ApiService(doLogin, RequestType.POST,
                                LOGIN_VERIFICATION, BASE_URL_LOGIN,  new HashMap<String, String>(), bodyParam);
                }
            }
        });

    }
    OnResponseListener doLogin = new OnResponseListener()
    {
        @Override
        public void onSuccess(final String response, HashMap<String, String> headers)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                        System.out.println("Success");
                        System.out.println(response);

                        JSONObject responseJSON = new JSONObject(response);
                        Boolean status = responseJSON.getBoolean("status");

                        if (status)
                        {

                            JSONObject resultSet = responseJSON.getJSONObject("resultSet");
                            JSONObject obj = resultSet.getJSONObject("t0040_Customer_Master");
                            System.out.println(obj);

                            id = obj.getString("customer_ID");
                            String cust_FName=obj.getString("cust_FName");
                            String cust_LName=obj.getString("cust_LName");
                            String cust_Street=obj.getString("cust_Street");
                            String cust_UnitNo=obj.getString("cust_UnitNo");
                            String cust_City=obj.getString("cust_City");
                            String cust_State_ID=obj.getString("cust_State_ID");
                            String cust_Country_ID=obj.getString("cust_Country_ID");
                            String cust_ZipCode=obj.getString("cust_ZipCode");
                            String cust_DOB=obj.getString("cust_DOB");
                            String cust_Email=obj.getString("cust_Email");
                            String cust_Phoneno=obj.getString("cust_Phoneno");
                            String cust_MobileNo=obj.getString("cust_MobileNo");
                            String cmp_Name=obj.getString("cmp_Name");
                            String cmp_Street=obj.getString("cmp_Street");
                            String cmp_UnitNo=obj.getString("cmp_UnitNo");
                            String cmp_City=obj.getString("cmp_City");
                            int cmp_State_ID=obj.getInt("cmp_State_ID");
                            String cmp_Country_ID=obj.getString("cmp_Country_ID");
                            String cmp_ZipCode=obj.getString("cmp_ZipCode");
                            String cmp_Phoneno=obj.getString("cmp_Phoneno");
                            String licence_No=obj.getString("licence_No");
                            String lIssue_Date=obj.getString("lIssue_Date");
                            String lExpiry_Date=obj.getString("lExpiry_Date");
                            String lIssue_By=obj.getString("lIssue_By");
                            String passport_No=obj.getString("passport_No");
                            String pIssue_Date=obj.getString("pIssue_Date");
                            String pExpiry_Date=obj.getString("pExpiry_Date");
                            String pIssue_By=obj.getString("pIssue_By");
                            String cmp_ID=obj.getString("cmp_ID");
                            String created_By=obj.getString("created_By");
                            String created_Date=obj.getString("created_Date");
                            String cust_Gender=obj.getString("cust_Gender");
                            String cust_Country_Name=obj.getString("cust_Country_Name");
                            String cust_State_Name=obj.getString("cust_State_Name");
                            String cmp_Country_Name=obj.getString("cmp_Country_Name");
                            String cmp_State_Name=obj.getString("cmp_State_Name");

                            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar",MODE_PRIVATE);
                            SharedPreferences.Editor editor= sp.edit();

                            editor.putString(getString(R.string.id), id);
                            editor.putString("cust_FName" ,cust_FName);
                            editor.putString("cust_LName" ,cust_LName);
                            editor.putString("cust_Street" ,cust_Street);
                            editor.putString("cust_UnitNo" ,cust_UnitNo);
                            editor.putString("cust_City" ,cust_City);
                            editor.putString("cust_State_ID" ,cust_State_ID);
                            editor.putString("cust_Country_ID" ,cust_Country_ID);
                            editor.putString("cust_ZipCode" ,cust_ZipCode);
                            editor.putString("cust_DOB" ,cust_DOB);
                            editor.putString("cust_Email" ,cust_Email);
                            editor.putString("cust_Phoneno" ,cust_Phoneno);
                            editor.putString("cust_MobileNo" ,cust_MobileNo);
                            editor.putString("cmp_Name" ,cmp_Name);
                            editor.putString("cmp_Street" ,cmp_Street);
                            editor.putString("cmp_UnitNo" ,cmp_UnitNo);
                            editor.putString("cmp_City" ,cmp_City);
                            editor.putInt("cmp_State_ID" ,cmp_State_ID);
                            editor.putString("cmp_Country_ID" ,cmp_Country_ID);
                            editor.putString("cmp_ZipCode" ,cmp_ZipCode);
                            editor.putString("cmp_Phoneno" ,cmp_Phoneno);
                            editor.putString("licence_No" ,licence_No);
                            editor.putString("lIssue_Date" ,lIssue_Date);
                            editor.putString("lIssue_By" ,lIssue_By);
                            editor.putString("passport_No" ,passport_No);
                            editor.putString("pIssue_Date" ,pIssue_Date);
                            editor.putString("pExpiry_Date" ,pExpiry_Date);
                            editor.putString("pIssue_By" ,pIssue_By);
                            editor.putString("cmp_ID" ,cmp_ID);
                            editor.putString("lExpiry_Date" ,lExpiry_Date);
                            editor.putString("created_By" ,created_By);
                            editor.putString("created_Date" ,created_Date);
                            editor.putString("cust_Gender" ,cust_Gender);
                            editor.putString("cust_Country_Name" ,cust_Country_Name);
                            editor.putString("cust_State_Name" ,cust_State_Name);
                            editor.putString("cmp_Country_Name" ,cmp_Country_Name);
                            editor.putString("cmp_State_Name" ,cmp_State_Name);

                            editor.commit();

                            String msg = "Permission Granted.";
                            CustomToast.showToast(getActivity(),msg,0);

                            Intent i = new Intent(getActivity(), Booking_Activity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
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
            System.out.println("Error"+error);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
            {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    CustomToast.showToast(getActivity(),"Permission Granted",0);
                }
                else
                {
                    CustomToast.showToast(getActivity(),"Permission Denied",1);
                }
            }
            break;
        }
    }
}

 /*if (count == 0)
          {
          lblexplore.setVisibility(View.INVISIBLE);
          relativeLayout.setVisibility(View.VISIBLE);
          count++;
          }
          else
          {
          Intent i = new Intent(getActivity(), Booking_Activity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        count = 0;
        }*/