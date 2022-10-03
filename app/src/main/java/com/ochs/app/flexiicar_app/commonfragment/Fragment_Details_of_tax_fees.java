package com.ochs.app.flexiicar_app.commonfragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

public class Fragment_Details_of_tax_fees extends Fragment
{
    LinearLayout backLayout;
    ImageView backArrow;
    public static Context context;
    Handler handler = new Handler();
    int TaxType;
    TextView lblDone, txt_totaltaxfees;
    Bundle BookingBundle,VehicleBundle,returnLocationBundle,locationBundle;
    Boolean locationType,initialSelect;
    JSONArray taxModelArray = new JSONArray();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details_of_tax_fee_applicable, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        lblDone =view.findViewById(R.id.txt_Discard_Tax);
        txt_totaltaxfees=view.findViewById(R.id.txt_totaltaxfees);
        backLayout=view.findViewById(R.id.lblback1);
        backArrow=view.findViewById(R.id.backimg_Taxandfees);
        TaxType = getArguments().getInt("TaxType");

        try {
            taxModelArray = new JSONArray(getArguments().getString("taxModel"));
            System.out.println(taxModelArray);

            Double TaxesAndFeesAmount = getArguments().getDouble("TaxesAndFeesAmount");

            txt_totaltaxfees.setText("USD$ " + TaxesAndFeesAmount);

            final RelativeLayout rlVehicleTaxDetails = getActivity().findViewById(R.id.rl_TotalTaxFees);

            int len;
            len = taxModelArray.length();
            Double totalTax = 0.0;
            for (int j = 0; j < len; j++)
            {
                final JSONObject test = (JSONObject) taxModelArray.get(j);

                final int taxSetupId = test.getInt("taxSetupId");
                final String taxName = test.getString("taxName");
                final String taxDesc = test.getString("taxDesc");
                final String basicValue = test.getString("basicValue");
                final String quantity = test.getString("quantity");
                final double taxAmount = test.getDouble("taxAmount");

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                lp.setMargins(0, 20, 0, 0);

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.vehicle_tax_and_fees, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                linearLayout.setId(200 + j);
                linearLayout.setLayoutParams(lp);

                TextView txt_Name, txt_DEsc, lbl_basicValue, lbl_quantity, lbl_taxTotalAmount;

                txt_Name = linearLayout.findViewById(R.id.txt_taxName);
                txt_DEsc = linearLayout.findViewById(R.id.tax_Desc);
                lbl_basicValue = linearLayout.findViewById(R.id.lbl_basicValue);
                lbl_quantity = linearLayout.findViewById(R.id.lbl_quantity);
                lbl_taxTotalAmount = linearLayout.findViewById(R.id.lbl_taxTotalAmount);

                txt_Name.setText(taxName);
                txt_DEsc.setText(taxDesc);
               // lbl_basicValue.setText(basicValue);
               // lbl_quantity.setText(quantity);

                String taxTotalAmount = (String.format(Locale.US, "%.2f", taxAmount));
                lbl_taxTotalAmount.setText("US$ " + taxTotalAmount);

                totalTax += test.getDouble("taxAmount");
                String TotalAmountStr = (String.format(Locale.US, "%.2f", totalTax));

                txt_totaltaxfees.setText("USD$ " + TaxesAndFeesAmount);

                rlVehicleTaxDetails.addView(linearLayout);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        backArrow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //For Booking Select Location1
                if(TaxType==1)
                {
                    BookingBundle=getArguments().getBundle("BookingBundle");
                    VehicleBundle=getArguments().getBundle("VehicleBundle");
                    returnLocationBundle = getArguments().getBundle("returnLocation");
                    locationBundle = getArguments().getBundle("location");
                    locationType = getArguments().getBoolean("locationType");
                    initialSelect = getArguments().getBoolean("initialSelect");

                    Bundle Booking = new Bundle();
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putInt("BookingStep", 2);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_Select_location1, Booking);
                }

                //For Booking Finalize Rental
                if(TaxType==2)
                {
                    BookingBundle=getArguments().getBundle("BookingBundle");
                    VehicleBundle=getArguments().getBundle("VehicleBundle");
                    returnLocationBundle = getArguments().getBundle("returnLocation");
                    locationBundle = getArguments().getBundle("location");
                    locationType = getArguments().getBoolean("locationType");
                    initialSelect = getArguments().getBoolean("initialSelect");

                    Bundle Booking = new Bundle();
                    BookingBundle.putInt("BookingStep", 4);
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    Booking.putString("taxModel", taxModelArray.toString());
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_Finalize_your_rental, Booking);
                }

                //For User Reservation SummaryOfCharges
                if(TaxType==3)
                {
                    Bundle Reservation=getArguments().getBundle("ReservationBundle");
                    Bundle ReservationBundle=new Bundle();
                    ReservationBundle.putBundle("ReservationBundle",Reservation);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_SummaryOfCharges, ReservationBundle);
                }

                //For User Finalize Your Rental
                if(TaxType==4)
                {
                    Bundle Reservation=getArguments().getBundle("ReservationBundle");
                    Bundle ReservationBundle=new Bundle();
                    ReservationBundle.putBundle("ReservationBundle",Reservation);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_Finalize_your_rental, ReservationBundle);
                }

                //For Booking Summary Of Charges
                if(TaxType==5)
                {
                    Bundle BookingBundle=getArguments().getBundle("BookingBundle");
                    Bundle VehicleBundle=getArguments().getBundle("VehicleBundle");

                    Bundle Booking = new Bundle();
                    BookingBundle.putInt("BookingStep", 6);
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_SummaryOfCharges, Booking);
                }

                //For User SummaryOfCharges For Agreements
                if(TaxType==6)
                {
                    Bundle AgreementsBundle=getArguments().getBundle("AgreementsBundle");
                    Bundle Agreements =new Bundle();
                    Agreements.putBundle("AgreementsBundle",AgreementsBundle);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_SummaryOfChargesForAgreements,Agreements);
                }

                //For User Agreements SummaryOfCharge For SelfCheckI
                if(TaxType==7)
                {
                    Bundle AgreementsBundle=getArguments().getBundle("AgreementsBundle");
                    Bundle Agreements=new Bundle();
                    Agreements.putBundle("AgreementsBundle",AgreementsBundle);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_SummaryOfChargeForSelfCheckIn,Agreements);
                }
            }
        });

        backLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //For Booking Select Location1
                if(TaxType==1)
                {
                    BookingBundle=getArguments().getBundle("BookingBundle");
                    VehicleBundle=getArguments().getBundle("VehicleBundle");
                    returnLocationBundle = getArguments().getBundle("returnLocation");
                    locationBundle = getArguments().getBundle("location");
                    locationType = getArguments().getBoolean("locationType");
                    initialSelect = getArguments().getBoolean("initialSelect");

                    Bundle Booking = new Bundle();
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    BookingBundle.putInt("BookingStep", 2);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_Select_location1, Booking);
                }
                //For Booking Finalize Rental
                if(TaxType==2)
                {
                    BookingBundle=getArguments().getBundle("BookingBundle");
                    VehicleBundle=getArguments().getBundle("VehicleBundle");
                    returnLocationBundle = getArguments().getBundle("returnLocation");
                    locationBundle = getArguments().getBundle("location");
                    locationType = getArguments().getBoolean("locationType");
                    initialSelect = getArguments().getBoolean("initialSelect");

                    Bundle Booking = new Bundle();
                    BookingBundle.putInt("BookingStep", 4);
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    Booking.putString("taxModel", taxModelArray.toString());
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_Finalize_your_rental, Booking);
                }
                //For User Reservation SummaryOfCharges
                if(TaxType==3)
                {
                    Bundle Reservation=getArguments().getBundle("ReservationBundle");

                    Bundle ReservationBundle=new Bundle();
                    ReservationBundle.putBundle("ReservationBundle",Reservation);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_SummaryOfCharges, ReservationBundle);
                }
                //For User Finalize Your Rental
                if(TaxType==4)
                {
                    Bundle Reservation=getArguments().getBundle("ReservationBundle");

                    Bundle ReservationBundle=new Bundle();
                    ReservationBundle.putBundle("ReservationBundle",Reservation);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_Finalize_your_rental, ReservationBundle);
                }
                //For Booking Summary Of Charges
                if(TaxType==5)
                {
                    Bundle BookingBundle=getArguments().getBundle("BookingBundle");
                    Bundle VehicleBundle=getArguments().getBundle("VehicleBundle");

                    Bundle Booking = new Bundle();
                    BookingBundle.putInt("BookingStep", 6);
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_SummaryOfCharges, Booking);
                }
                //For User SummaryOfCharges For Agreements
                if(TaxType==6)
                {
                    Bundle Agreements=getArguments().getBundle("AgreementsBundle");
                    Bundle AgreementsBundle=new Bundle();
                    AgreementsBundle.putBundle("AgreementsBundle",Agreements);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_SummaryOfChargesForAgreements,AgreementsBundle);
                }
                //For User Agreements SummaryOfCharge For SelfCheckI
                if(TaxType==7)
                {
                    Bundle AgreementsBundle=getArguments().getBundle("AgreementsBundle");
                    Bundle Agreements=new Bundle();
                    Agreements.putBundle("AgreementsBundle",AgreementsBundle);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_SummaryOfChargeForSelfCheckIn,Agreements);
                }
            }
        });

        lblDone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //For Booking Select Location1
                if (TaxType == 1)
                {
                    BookingBundle = getArguments().getBundle("BookingBundle");
                    VehicleBundle = getArguments().getBundle("VehicleBundle");
                    returnLocationBundle = getArguments().getBundle("returnLocation");
                    locationBundle = getArguments().getBundle("location");
                    locationType = getArguments().getBoolean("locationType");
                    initialSelect = getArguments().getBoolean("initialSelect");

                    Bundle Booking = new Bundle();
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putInt("BookingStep", 2);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_Select_location1, Booking);
                }

                //For Booking Finalize Rental
                if (TaxType == 2)
                {
                    BookingBundle = getArguments().getBundle("BookingBundle");
                    VehicleBundle = getArguments().getBundle("VehicleBundle");
                    returnLocationBundle = getArguments().getBundle("returnLocation");
                    locationBundle = getArguments().getBundle("location");
                    locationType = getArguments().getBoolean("locationType");
                    initialSelect = getArguments().getBoolean("initialSelect");

                    Bundle Booking = new Bundle();
                    BookingBundle.putInt("BookingStep", 4);
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    Booking.putString("taxModel", taxModelArray.toString());
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_Finalize_your_rental, Booking);
                }

                //For User Reservation SummaryOfCharges
                if (TaxType == 3)
                {
                    Bundle Reservation = getArguments().getBundle("ReservationBundle");
                    Bundle ReservationBundle = new Bundle();
                    ReservationBundle.putBundle("ReservationBundle", Reservation);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_SummaryOfCharges, ReservationBundle);
                }

                //For User Finalize Your Rental
                if (TaxType == 4)
                {
                    Bundle Reservation = getArguments().getBundle("ReservationBundle");
                    Bundle ReservationBundle = new Bundle();
                    ReservationBundle.putBundle("ReservationBundle", Reservation);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_Finalize_your_rental, ReservationBundle);
                }

                //For Booking Summary Of Charges
                if (TaxType == 5)
                {
                    Bundle BookingBundle = getArguments().getBundle("BookingBundle");
                    Bundle VehicleBundle = getArguments().getBundle("VehicleBundle");

                    Bundle Booking = new Bundle();
                    BookingBundle.putInt("BookingStep", 6);
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_SummaryOfCharges, Booking);
                }

                //For User SummaryOfCharges For Agreements
                if (TaxType == 6)
                {
                    Bundle AgreementsBundle = getArguments().getBundle("AgreementsBundle");
                    Bundle Agreements = new Bundle();
                    Agreements.putBundle("AgreementsBundle", AgreementsBundle);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_SummaryOfChargesForAgreements, Agreements);
                }

                //For User Agreements SummaryOfCharge For SelfCheckI
                if (TaxType == 7)
                {
                    Bundle AgreementsBundle = getArguments().getBundle("AgreementsBundle");
                    Bundle Agreements = new Bundle();
                    Agreements.putBundle("AgreementsBundle", AgreementsBundle);
                    NavHostFragment.findNavController(Fragment_Details_of_tax_fees.this)
                            .navigate(R.id.action_Total_tax_fee_details_to_SummaryOfChargeForSelfCheckIn, Agreements);
                }
            }

        });
    }
}
