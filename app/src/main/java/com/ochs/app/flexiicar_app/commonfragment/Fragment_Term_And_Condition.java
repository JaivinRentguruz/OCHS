package com.ochs.app.flexiicar_app.commonfragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.GETTERMSCONDITION;

public class Fragment_Term_And_Condition extends Fragment
{
    Handler handler = new Handler();
    public static Context context;
    public String id = "";
    TextView txt_TCName,txt_TCDesc,txt_Cancel;
    int termcondition;
    ImageView Back_arrow;
    Bundle BookingBundle,VehicleBundle,returnLocationBundle,locationBundle;
    Boolean locationType,initialSelect;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        txt_TCName=view.findViewById(R.id.txt_terms_Cond_Name);
        txt_TCDesc=view.findViewById(R.id.txt_term_Cond_Desc);
        Back_arrow=view.findViewById(R.id.Back_arrowTC);
        txt_Cancel=view.findViewById(R.id.txt_Cancel);

        termcondition=getArguments().getInt("termcondition");

        SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
        id = sp.getString(getString(R.string.id), "");

        txt_Cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //For Booking
                if(termcondition==1)
                {
                    BookingBundle = getArguments().getBundle("BookingBundle");
                    VehicleBundle = getArguments().getBundle("VehicleBundle");
                    returnLocationBundle = getArguments().getBundle("returnLocation");
                    locationBundle = getArguments().getBundle("location");
                    locationType = getArguments().getBoolean("locationType");
                    initialSelect = getArguments().getBoolean("initialSelect");

                    Bundle Booking = new Bundle();
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle",VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    BookingBundle.putInt("BookingStep", 4);
                    NavHostFragment.findNavController(Fragment_Term_And_Condition.this)
                            .navigate(R.id.action_Term_and_Condtion_to_Finalize_your_rental,Booking);
                }

                //For User Reservation
                if(termcondition==2)
                {
                    Bundle Reservation=getArguments().getBundle("ReservationBundle");

                    Bundle ReservationBundle = new Bundle();
                    ReservationBundle.putBundle("ReservationBundle", Reservation);
                    NavHostFragment.findNavController(Fragment_Term_And_Condition.this)
                            .navigate(R.id.action_TermAndCondition_to_SummaryOfCharges,ReservationBundle);
                }
                //For User Agreements
                if(termcondition==3)
                {
                    Bundle Agreements=getArguments().getBundle("AgreementsBundle");

                    Bundle AgreementsBundle = new Bundle();
                    AgreementsBundle.putBundle("AgreementsBundle", Agreements);
                    NavHostFragment.findNavController(Fragment_Term_And_Condition.this)
                            .navigate(R.id.action_TermAndCondition_to_SummaryOfChargesForAgreements,AgreementsBundle);
                }
                //For Booking
                if(termcondition==4)
                {
                    Bundle BookingBundle = getArguments().getBundle("BookingBundle");
                    Bundle VehicleBundle = getArguments().getBundle("VehicleBundle");
                    Bundle Booking = new Bundle();
                    Booking.putInt("BookingStep", 6);
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    NavHostFragment.findNavController(Fragment_Term_And_Condition.this)
                            .navigate(R.id.action_Term_and_Condtion_to_Summary_Of_Charges,Booking);
                }
                //For User Reservation Finalize Your Rental
                if(termcondition==5)
                {
                    Bundle Reservation=getArguments().getBundle("ReservationBundle");

                    Bundle ReservationBundle = new Bundle();
                    ReservationBundle.putBundle("ReservationBundle", Reservation);
                    NavHostFragment.findNavController(Fragment_Term_And_Condition.this)
                            .navigate(R.id.action_TermAndCondition_to_Finalize_your_rental,ReservationBundle);
                }
                //For Summary Of Charges SelfCheckIn
                if(termcondition==6)
                {

                    Bundle AgreementsBundle = getArguments().getBundle("AgreementsBundle");
                    Bundle Agreements=new Bundle();
                    Agreements.putBundle("AgreementsBundle",AgreementsBundle);
                    NavHostFragment.findNavController(Fragment_Term_And_Condition.this)
                            .navigate(R.id.action_TermAndCondition_to_SummaryOfChargeForSelfCheckIn,Agreements);
                }
            }
        });

        Back_arrow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //For Booking
                if(termcondition==1)
                {
                    BookingBundle = getArguments().getBundle("BookingBundle");
                    VehicleBundle = getArguments().getBundle("VehicleBundle");
                    returnLocationBundle = getArguments().getBundle("returnLocation");
                    locationBundle = getArguments().getBundle("location");
                    locationType = getArguments().getBoolean("locationType");
                    initialSelect = getArguments().getBoolean("initialSelect");

                    Bundle Booking = new Bundle();
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle",VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    BookingBundle.putInt("BookingStep", 4);
                    NavHostFragment.findNavController(Fragment_Term_And_Condition.this)
                            .navigate(R.id.action_Term_and_Condtion_to_Finalize_your_rental,Booking);
                }

                //For User Reservation
                if(termcondition==2)
                {
                    Bundle Reservation=getArguments().getBundle("ReservationBundle");

                    Bundle ReservationBundle = new Bundle();
                    ReservationBundle.putBundle("ReservationBundle", Reservation);
                    NavHostFragment.findNavController(Fragment_Term_And_Condition.this)
                            .navigate(R.id.action_TermAndCondition_to_SummaryOfCharges,ReservationBundle);
                }
                //For User Agreements
                if(termcondition==3)
                {
                    Bundle Agreements=getArguments().getBundle("AgreementsBundle");

                    Bundle AgreementsBundle = new Bundle();
                    AgreementsBundle.putBundle("AgreementsBundle", Agreements);
                    NavHostFragment.findNavController(Fragment_Term_And_Condition.this)
                            .navigate(R.id.action_TermAndCondition_to_SummaryOfChargesForAgreements,AgreementsBundle);
                }
                //For Booking
                if(termcondition==4)
                {
                    Bundle BookingBundle = getArguments().getBundle("BookingBundle");
                    Bundle VehicleBundle = getArguments().getBundle("VehicleBundle");
                    Bundle Booking = new Bundle();
                    Booking.putInt("BookingStep", 6);
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    NavHostFragment.findNavController(Fragment_Term_And_Condition.this)
                            .navigate(R.id.action_Term_and_Condtion_to_Summary_Of_Charges,Booking);
                }
                //For User Reservation Finalize Your Rental
                if(termcondition==5)
                {
                    Bundle Reservation=getArguments().getBundle("ReservationBundle");

                    Bundle ReservationBundle = new Bundle();
                    ReservationBundle.putBundle("ReservationBundle", Reservation);
                    NavHostFragment.findNavController(Fragment_Term_And_Condition.this)
                            .navigate(R.id.action_TermAndCondition_to_Finalize_your_rental,ReservationBundle);
                }
                //For Summary Of Charges SelfCheckIn
                if(termcondition==6)
                {

                    Bundle AgreementsBundle = getArguments().getBundle("AgreementsBundle");
                    Bundle Agreements=new Bundle();
                    Agreements.putBundle("AgreementsBundle",AgreementsBundle);
                    NavHostFragment.findNavController(Fragment_Term_And_Condition.this)
                            .navigate(R.id.action_TermAndCondition_to_SummaryOfChargeForSelfCheckIn,Agreements);
                }

            }
        });

        JSONObject bodyParam = new JSONObject();
        try
        {
            bodyParam.accumulate("customerId",id);
            System.out.println(bodyParam);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        AndroidNetworking.initialize(getActivity());
        Fragment_Term_And_Condition.context = getActivity();

        ApiService ApiService = new ApiService(GetTermsCondition, RequestType.GET,
                GETTERMSCONDITION, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
    }
    OnResponseListener GetTermsCondition = new OnResponseListener()
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
                            try {
                                JSONObject resultSet = responseJSON.getJSONObject("resultSet");
                                final JSONArray getInsuranceDEtails = resultSet.getJSONArray("t0040_Terms_Condition_Master");

                                int len;
                                len = getInsuranceDEtails.length();

                                for (int j = 0; j < len; j++)
                                {
                                    final JSONObject test = (JSONObject) getInsuranceDEtails.get(j);

                                    int terms_Cond_ID=test.getInt("terms_Cond_ID");
                                    String terms_Cond_Name=test.getString("terms_Cond_Name");
                                    String term_Cond_Desc=test.getString("term_Cond_Desc");

                                    txt_TCName.setText(terms_Cond_Name);
                                    txt_TCDesc.setText(term_Cond_Desc);
                                }

                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
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

}


