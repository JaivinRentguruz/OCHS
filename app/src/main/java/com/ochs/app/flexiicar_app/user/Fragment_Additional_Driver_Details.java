package com.ochs.app.flexiicar_app.user;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;

import org.json.JSONObject;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.ADD_DRIVER;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_CUSTOMER;

public class Fragment_Additional_Driver_Details extends Fragment
{
    ImageView Back;
    EditText FirstName,LastName,Email,ContactNo;
    TextView lblDiscard,lblSubmit;
    public String id = "";
    Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_additional_driver_details, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            ((User_Profile) getActivity()).BottomnavInVisible();

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            id = sp.getString(getString(R.string.id), "");

            lblSubmit = view.findViewById(R.id.lblSubmit);
            Back = view.findViewById(R.id.Backimg);
            FirstName=view.findViewById(R.id.GuestDriverName);
            LastName=view.findViewById(R.id.GuestDriverLastName);
            Email=view.findViewById(R.id.GuestDriverEmail);
            ContactNo=view.findViewById(R.id.GuestDriverPhoneNo);
            lblDiscard=view.findViewById(R.id.lblDiscard);

            lblSubmit.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
                        if (FirstName.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(), "Enter First Name.", 1);
                        else if (LastName.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(), "Enter Last Name.", 1);
                        else if (Email.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(), "Enter Your Email.", 1);
                        else if (ContactNo.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(), "Enter Your Contact No.", 1);
                        else {
                            JSONObject bodyParam = new JSONObject();
                            try {
                                bodyParam.accumulate("CustomerID", Integer.parseInt(id));
                                bodyParam.accumulate("Driver_Name", FirstName.getText().toString());
                                bodyParam.accumulate("Driver_Last_Name", LastName.getText().toString());
                                bodyParam.accumulate("Phone_Num", ContactNo.getText().toString());
                                bodyParam.accumulate("DriverEmail", Email.getText().toString());
                                ApiService ApiService = new ApiService(AddDriver, RequestType.POST,
                                        ADD_DRIVER, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            Back.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle Reservation=getArguments().getBundle("ReservationBundle");
                    Bundle ReservationBundle = new Bundle();
                    ReservationBundle.putBundle("ReservationBundle", Reservation);
                    NavHostFragment.findNavController(Fragment_Additional_Driver_Details.this)
                            .navigate(R.id.action_Add_Additional_Driver_Details_to_Additional_Driver_List,ReservationBundle);
                }
            });

            lblDiscard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle Reservation=getArguments().getBundle("ReservationBundle");
                    Bundle ReservationBundle = new Bundle();
                    ReservationBundle.putBundle("ReservationBundle", Reservation);
                    NavHostFragment.findNavController(Fragment_Additional_Driver_Details.this)
                            .navigate(R.id.action_Add_Additional_Driver_Details_to_Additional_Driver_List,ReservationBundle);
                }
            });

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    OnResponseListener AddDriver = new OnResponseListener()
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
                            String msg = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),msg,0);
                            Bundle Reservation=getArguments().getBundle("ReservationBundle");
                            Bundle ReservationBundle = new Bundle();
                            ReservationBundle.putBundle("ReservationBundle", Reservation);
                            NavHostFragment.findNavController(Fragment_Additional_Driver_Details.this)
                                    .navigate(R.id.action_Add_Additional_Driver_Details_to_Additional_Driver_List,ReservationBundle);
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
}
