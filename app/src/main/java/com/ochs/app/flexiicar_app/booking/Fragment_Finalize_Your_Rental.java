package com.ochs.app.flexiicar_app.booking;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.adapters.Helper;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.BOOKING;

public class Fragment_Finalize_Your_Rental extends Fragment
{
    ImageView backarrow,SummaryOfChargeArrowdown,SummaryOfChargeArrow,CarImage;
    Handler handler = new Handler();
    public static Context context;
    Bundle BookingBundle,VehicleBundle;
    LinearLayout TermCondition_layout,lblpay,SummaryOfChargeLayout,FlightDetailsMainLayout;
    TextView txt_PickLocName,txt_ReturnLocName,txt_PickupDate,txt_ReturnDate,txt_PickupTime,txt_ReturnTIme,txtDays,txt_vehicletype,
            txt_vehName,txtMileage,txt_rate,txtTotalAmount,txt_Seats,txt_Bags,txt_Automatic,txt_Doors,txt_Discard,txtvehDesc,currencySymbol;

    ImageView driverdetails_icon1,arrowflight_details;

    Bundle returnLocationBundle, locationBundle;
    Boolean locationType, initialSelect;

    ImageLoader imageLoader;
    String serverpath="",VehImage="";
    String id="";
    TextView driverName,driverPhone,driverEmail;
    TextView txt_driverdetails,txt_AsGuestdriver;
    double totalMilesAllowed;
    int cmP_DISTANCE;
    String StrFirstName,StrLastName;

    public static void initImageLoader(Context context)
    {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.MAX_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(100 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        ImageLoader.getInstance().init(config.build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_finalize_your_rental, container, false);
    }

    @Override
    public void setInitialSavedState(@Nullable @org.jetbrains.annotations.Nullable SavedState state) {
        super.setInitialSavedState(state);
    }

    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            initImageLoader(getActivity());
            imageLoader = ImageLoader.getInstance();

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            id = sp.getString(getString(R.string.id), "");
            serverpath = sp.getString("serverPath", "");
            cmP_DISTANCE=sp.getInt("cmP_DISTANCE",0);

            TermCondition_layout = view.findViewById(R.id.lblterm_condition);
            lblpay = view.findViewById(R.id.layout_payment);
            backarrow = view.findViewById(R.id.backbtn2);
            SummaryOfChargeArrowdown = view.findViewById(R.id.SummaryofChargesArrowDown);
            SummaryOfChargeArrow= view.findViewById(R.id.img_bottomArrow);
            SummaryOfChargeLayout = view.findViewById(R.id.LinearL_SummaryOfCharges);
            FlightDetailsMainLayout= view.findViewById(R.id.FlightDetailsMainLayout);
            txt_PickLocName = view.findViewById(R.id.txt_PickupLocation);
            txt_PickupDate = view.findViewById(R.id.txt_PickupLoationDate);
            txt_PickupTime = view.findViewById(R.id.txt_PickupLocationTime);
            txt_ReturnLocName = view.findViewById(R.id.txt_ReturnLocation);
            txt_ReturnDate = view.findViewById(R.id.txt_ReturnLocationDate);
            txt_ReturnTIme = view.findViewById(R.id.txt_ReturnLocationTime);
            txtDays = view.findViewById(R.id.textView_TotalDays);
            txt_rate = view.findViewById(R.id.txt_Vehiclerate);
            txt_Seats = view.findViewById(R.id.textView_Seats);
            txt_Bags = view.findViewById(R.id.textView_Bags);
            txt_Automatic = view.findViewById(R.id.textView_Automatic);
            txt_Doors = view.findViewById(R.id.textView_Doors);
            txtTotalAmount = view.findViewById(R.id.textview_TotalAmount);
            txt_Discard = view.findViewById(R.id.DiscardFinalizeRen);
            txtvehDesc=view.findViewById(R.id.txtvehDesciption);
            txt_vehName = view.findViewById(R.id.textV_VehicleModelName);
            txt_vehicletype = view.findViewById(R.id.txtV_TypeName);
            currencySymbol = view.findViewById(R.id.currencySymbol);
            currencySymbol.setText(Helper.currencySymbol);
            txtMileage= view.findViewById(R.id.txtMileage);

            BookingBundle = getArguments().getBundle("BookingBundle");
            String data = sp.getString("vehicleClassWise", "");
            BookingBundle.putString("vehicleClassWise",data);
            VehicleBundle = getArguments().getBundle("VehicleBundle");

            returnLocationBundle = getArguments().getBundle("returnLocation");
            locationBundle = getArguments().getBundle("location");
            locationType = getArguments().getBoolean("locationType");
            initialSelect = getArguments().getBoolean("initialSelect");

            txt_PickLocName.setText(BookingBundle.getString("PickupLocName"));
            txt_ReturnLocName.setText(BookingBundle.getString("ReturnLocName"));

            totalMilesAllowed=(VehicleBundle.getDouble("totalMilesAllowed"));
            txtMileage.setText(getArguments().getString("submitmile",""));
        /*    if(cmP_DISTANCE==1)
            {
                String Miles=((String.format(Locale.US,"%.0f",totalMilesAllowed)));
                txtMileage.setText(Miles+" MILES");
            }
            else {
                String Miles=(String.valueOf(totalMilesAllowed));
              //  txtMileage.setText(Miles+"kms");
                txtMileage.setText(Miles+" MILES");
            }*/

            String StrPickupDate = (BookingBundle.getString("PickupDate"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(StrPickupDate);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String PickUpDateStr = sdf.format(date);
            txt_PickupDate.setText(PickUpDateStr);

            String StrReturnDate = (BookingBundle.getString("ReturnDate"));
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = dateFormat1.parse(StrReturnDate);
            System.out.println(date1);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            String ReturnDateStr = sdf1.format(date1);
            txt_ReturnDate.setText(ReturnDateStr);

            String strPickUpTime = (BookingBundle.getString("PickupTime"));
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
            Date date2 = dateFormat2.parse(strPickUpTime);
            SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa", Locale.US);
            String PickUpTimeStr = sdf2.format(date2);
            txt_PickupTime.setText(PickUpTimeStr);

            String strReturntime = (BookingBundle.getString("ReturnTime"));
            SimpleDateFormat dateFormat3 = new SimpleDateFormat("HH:mm");
            Date date3 = dateFormat3.parse(strReturntime);
            SimpleDateFormat sdf3 = new SimpleDateFormat("hh:mm aa", Locale.US);
            String ReturntimeStr = sdf3.format(date3);
            txt_ReturnTIme.setText(ReturntimeStr);

            Double totaL_DAYS=VehicleBundle.getDouble("totaL_DAYS");
            // DayStr=DayStr.substring(0,DayStr.length()-2);
            txtDays.setText(String.valueOf(totaL_DAYS));

            txt_vehName.setText(VehicleBundle.getString("vehiclE_NAME"));
            txt_vehicletype.setText(VehicleBundle.getString("vehiclE_TYPE_NAME"));

            String Seats=VehicleBundle.getString("vehiclE_SEAT_NO");
            txt_Seats.setText(Seats+" Seats");
            String veh_bags=VehicleBundle.getString("veh_bags");
            txt_Bags.setText(veh_bags+" Bags");
            txt_Automatic.setText(VehicleBundle.getString("transmission_Name"));

            String doors=VehicleBundle.getString("doors");
            txt_Doors.setText(doors+" Doors");

            txt_rate.setText(VehicleBundle.getString("rate_ID"));
            txtvehDesc.setText(VehicleBundle.getString("vehDescription"));

            driverName = view.findViewById(R.id.driverName);
            driverPhone = view.findViewById(R.id.driverPhone);
            driverEmail = view.findViewById(R.id.driverEmail);

            CarImage = view.findViewById(R.id.Veh_image_bg3);

            VehImage = VehicleBundle.getString("img_Path");

            String url1 = serverpath + VehImage;
            imageLoader.displayImage(url1, CarImage);

            txt_Discard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are You Sure You Want To Cancel?");
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    NavHostFragment.findNavController(Fragment_Finalize_Your_Rental.this)
                                            .navigate(R.id.action_Finalize_your_rental_to_Search_activity);
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

            backarrow.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int DeductibleCoverId=BookingBundle.getInt("DeductibleCoverId");
                    double DeductibleCharge=BookingBundle.getDouble("DeductibleCharge");

                    Bundle Booking = new Bundle();
                    BookingBundle.putInt("BookingStep", 3);
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    Booking.putString("DeliveryAndPickupModel", "");
                    Booking.putInt("DeductibleCover_Id", DeductibleCoverId);
                    Booking.putDouble("Deductible_Charge", DeductibleCharge);
                    Booking.putBoolean("IsSelected", true);
                    NavHostFragment.findNavController(Fragment_Finalize_Your_Rental.this)
                            .navigate(R.id.action_Finalize_your_rental_to_Select_addtional_options, Booking);
                }
            });

            SummaryOfChargeLayout.setVisibility(View.VISIBLE);
            SummaryOfChargeArrow.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    if (SummaryOfChargeLayout.getVisibility() == View.GONE)
                    {
                        SummaryOfChargeLayout.setVisibility(View.VISIBLE);
                        SummaryOfChargeArrowdown.setVisibility(View.VISIBLE);
                        SummaryOfChargeArrow.setVisibility(View.GONE);
                    }
                }
            });
            SummaryOfChargeArrowdown.setVisibility(View.VISIBLE);
            SummaryOfChargeArrow.setVisibility(View.GONE);
            SummaryOfChargeArrowdown.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(SummaryOfChargeLayout.getVisibility() == View.VISIBLE)
                    {
                        SummaryOfChargeLayout.setVisibility(View.GONE);
                        SummaryOfChargeArrow.setVisibility(View.VISIBLE);
                        SummaryOfChargeArrowdown.setVisibility(View.GONE);
                    }
                }
            });

            final LinearLayout flight_details_layout = view.findViewById(R.id.flight_details_layout);

            final TextView flightName = view.findViewById(R.id.flightName);
            final TextView flightNumber = view.findViewById(R.id.flightNumber);
            final TextView flightTime = view.findViewById(R.id.flightTime);

            arrowflight_details = view.findViewById(R.id.arrowflight_details);

            if(!id.equals(""))
            {
                arrowflight_details.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (flight_details_layout.getVisibility() == View.GONE)
                        {
                            flight_details_layout.setVisibility(View.VISIBLE);
                        } else
                            flight_details_layout.setVisibility(View.GONE);
                    }
                });

                try {
                    String temp = BookingBundle.getString("airPortModel");
                    if (temp != null)
                    {
                        flight_details_layout.setVisibility(View.VISIBLE);

                        JSONObject airPortModel = new JSONObject(temp);
                        flightName.setText(airPortModel.getString("airLine"));
                        flightNumber.setText(airPortModel.getString("flightNumber"));
                        String flightTimeStr = airPortModel.getString("arrivalDateTime");
                        flightTime.setText(airPortModel.getString("arrivalDateTime"));

                        flight_details_layout.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Bundle Booking = new Bundle();
                                Booking.putBundle("BookingBundle", BookingBundle);
                                Booking.putBundle("VehicleBundle", VehicleBundle);
                                Booking.putBundle("returnLocation", returnLocationBundle);
                                Booking.putBundle("location", locationBundle);
                                Booking.putBoolean("locationType", locationType);
                                Booking.putBoolean("initialSelect", initialSelect);
                                Booking.putInt("forflight", 2);
                                NavHostFragment.findNavController(Fragment_Finalize_Your_Rental.this)
                                        .navigate(R.id.action_Finalize_your_rental_to_flight_details, Booking);
                            }
                        });

                    } else {
                        arrowflight_details.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Bundle Booking = new Bundle();
                                Booking.putBundle("BookingBundle", BookingBundle);
                                Booking.putBundle("VehicleBundle", VehicleBundle);
                                Booking.putBundle("returnLocation", returnLocationBundle);
                                Booking.putBundle("location", locationBundle);
                                Booking.putBoolean("locationType", locationType);
                                Booking.putBoolean("initialSelect", initialSelect);
                                Booking.putInt("forflight", 1);
                                NavHostFragment.findNavController(Fragment_Finalize_Your_Rental.this)
                                        .navigate(R.id.action_Finalize_your_rental_to_flight_details, Booking);
                            }
                        });
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else {
                FlightDetailsMainLayout.setVisibility(View.GONE);
            }

            TermCondition_layout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle Booking = new Bundle();
                    Booking.putInt("termcondition", 1);
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    NavHostFragment.findNavController(Fragment_Finalize_Your_Rental.this)
                            .navigate(R.id.action_Finalize_your_rental_to_Term_and_Condtion, Booking);
                }
            });

            lblpay.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(!id.equals(""))
                    {
                        Bundle Booking = new Bundle();
                        BookingBundle.putInt("BookingStep", 5);
                        Booking.putBundle("BookingBundle", BookingBundle);
                        Booking.putBundle("VehicleBundle", VehicleBundle);
                        Booking.putBundle("returnLocation", returnLocationBundle);
                        Booking.putBundle("location", locationBundle);
                        Booking.putBoolean("locationType", locationType);
                        Booking.putBoolean("initialSelect", initialSelect);
                        Booking.putBoolean("isDefaultCard", true);
                        System.out.println("id not null"+Booking);
                        NavHostFragment.findNavController(Fragment_Finalize_Your_Rental.this)
                                .navigate(R.id.action_Finalize_your_rental_to_Payment_checkout, Booking);
                    }

                    else
                    {
                        String temp = BookingBundle.getString("GuestDriverDetails");
                        System.out.println(temp);
                        if (temp != null)
                        {
                            try {
                                Bundle Booking = new Bundle();
                                BookingBundle.putInt("BookingStep", 5);
                                Booking.putBundle("BookingBundle", BookingBundle);
                                Booking.putBundle("VehicleBundle", VehicleBundle);
                                Booking.putBundle("returnLocation", returnLocationBundle);
                                Booking.putBundle("location", locationBundle);
                                Booking.putBoolean("locationType", locationType);
                                Booking.putBoolean("initialSelect", initialSelect);
                                Booking.putBoolean("isDefaultCard", true);
                                System.out.println("id null"+Booking);
                                NavHostFragment.findNavController(Fragment_Finalize_Your_Rental.this)
                                        .navigate(R.id.action_Finalize_your_rental_to_Payment_checkout, Booking);
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }

                        else
                        {
                            String msgString = "Please add guest driver details";
                            CustomToast.showToast(getActivity(), msgString, 1);

                            Bundle Booking = new Bundle();
                            Booking.putBundle("BookingBundle", BookingBundle);
                            Booking.putBundle("VehicleBundle", VehicleBundle);
                            Booking.putBundle("returnLocation", returnLocationBundle);
                            Booking.putBundle("location", locationBundle);
                            Booking.putBoolean("locationType", locationType);
                            Booking.putBoolean("initialSelect", initialSelect);
                            System.out.println(Booking);
                            NavHostFragment.findNavController(Fragment_Finalize_Your_Rental.this)
                                    .navigate(R.id.action_Finalize_your_rental_to_Additional_Driver_List, Booking);
                        }
                    }
                }
            });

            final LinearLayout driverdetails1 = view.findViewById(R.id.driverdetails1);
            final LinearLayout DriverDetails = view.findViewById(R.id.DriverDetails);
            driverdetails_icon1 = view.findViewById(R.id.driverdetails_icon1);
            txt_driverdetails= view.findViewById(R.id.txt_driverdetails);
            txt_AsGuestdriver= view.findViewById(R.id.txt_AsGuestdriver);

            LinearLayout AdditionalDriverDetails=view.findViewById(R.id.AdditionalDriverDetails);
            LinearLayout AdditonalDriverLayout=view.findViewById(R.id.AdditonalDriverLayout);

            TextView AdditionalDriverName=view.findViewById(R.id.AdditionalDriverName);
            TextView AdditionalDriverPhone=view.findViewById(R.id.AdditionalDriverPhone);
            TextView AdditionalDriverEmail=view.findViewById(R.id.AdditionalDriverEmail);
            ImageView driverdetails_icon2=view.findViewById(R.id.driverdetails_icon2);


            if(!id.equals(""))
            {
                txt_driverdetails.setVisibility(View.VISIBLE);
                txt_AsGuestdriver.setVisibility(View.GONE);

                driverdetails1.setVisibility(View.VISIBLE);

                SharedPreferences sp1 = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
                String cust_FName = sp1.getString("cust_FName", "");
                String cust_LName = sp1.getString("cust_LName", "");
                String cust_Email = sp1.getString("cust_Email", "");
                String cust_Phoneno = sp1.getString("cust_MobileNo", "");

                driverName.setText(cust_FName + " " + cust_LName);
                driverPhone.setText(cust_Phoneno);
                driverEmail.setText(cust_Email);

                driverdetails_icon1.setVisibility(View.GONE);

                driverdetails1.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        try {
                            Bundle Booking = new Bundle();
                            Booking.putBundle("BookingBundle", BookingBundle);
                            Booking.putBundle("VehicleBundle", VehicleBundle);
                            Booking.putBundle("returnLocation", returnLocationBundle);
                            Booking.putBundle("location", locationBundle);
                            Booking.putBoolean("locationType", locationType);
                            Booking.putBoolean("initialSelect", initialSelect);
                            System.out.println(Booking);
                            NavHostFragment.findNavController(Fragment_Finalize_Your_Rental.this)
                                    .navigate(R.id.action_Finalize_your_rental_to_Additional_Driver_List, Booking);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });


                String temp = BookingBundle.getString("AdditionalDriverModel");
                System.out.println(temp);

                if (temp != null)
                {
                    AdditionalDriverDetails.setVisibility(View.VISIBLE);
                    JSONObject AdditionalDriverModel = new JSONObject(temp);

                    StrFirstName = AdditionalDriverModel.getString("Driver_Name");
                    StrLastName = AdditionalDriverModel.getString("Driver_Last_Name");
                    String Fullname = StrFirstName + StrLastName;

                    AdditionalDriverName.setText(Fullname);
                    AdditionalDriverPhone.setText(AdditionalDriverModel.getString("DriverPhoneNo"));
                    AdditionalDriverEmail.setText(AdditionalDriverModel.getString("DriverEmail"));
                    System.out.println(temp);
                    driverdetails_icon2.setVisibility(View.GONE);

                    AdditonalDriverLayout.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                Bundle Booking = new Bundle();
                                Booking.putBundle("BookingBundle", BookingBundle);
                                Booking.putBundle("VehicleBundle", VehicleBundle);
                                Booking.putBundle("returnLocation", returnLocationBundle);
                                Booking.putBundle("location", locationBundle);
                                Booking.putBoolean("locationType", locationType);
                                Booking.putBoolean("initialSelect", initialSelect);
                                System.out.println(Booking);
                                NavHostFragment.findNavController(Fragment_Finalize_Your_Rental.this)
                                        .navigate(R.id.action_Finalize_your_rental_to_Additional_Driver_List, Booking);
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
            else {
                txt_AsGuestdriver.setVisibility(View.VISIBLE);
                txt_driverdetails.setVisibility(View.GONE);
                driverdetails1.setVisibility(View.VISIBLE);

                String temp = BookingBundle.getString("AdditionalDriverModel");
                System.out.println(temp);

                if (temp != null)
                {
                    JSONObject AdditionalDriverModel = new JSONObject(temp);

                    StrFirstName = AdditionalDriverModel.getString("Driver_Name");
                    StrLastName = AdditionalDriverModel.getString("Driver_Last_Name");
                    String Fullname = StrFirstName + StrLastName;
                    driverName.setText(Fullname);
                    driverPhone.setText(AdditionalDriverModel.getString("DriverPhoneNo"));
                    driverEmail.setText(AdditionalDriverModel.getString("DriverEmail"));
                    System.out.println(temp);
                }
                else
                {
                    driverdetails_icon1.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            try {
                                Bundle Booking = new Bundle();
                                Booking.putBundle("BookingBundle", BookingBundle);
                                Booking.putBundle("VehicleBundle", VehicleBundle);
                                Booking.putBundle("returnLocation", returnLocationBundle);
                                Booking.putBundle("location", locationBundle);
                                Booking.putBoolean("locationType", locationType);
                                Booking.putBoolean("initialSelect", initialSelect);
                                System.out.println(Booking);
                                NavHostFragment.findNavController(Fragment_Finalize_Your_Rental.this)
                                        .navigate(R.id.action_Finalize_your_rental_to_Additional_Driver_List, Booking);
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            if (!id.equals(""))
            {
                JSONObject bodyParam = new JSONObject();
                try {
                    bodyParam.accumulate("ForTransId", BookingBundle.getInt("ForTransId"));
                    bodyParam.accumulate("PickupLocId", BookingBundle.getInt("PickupLocId"));
                    bodyParam.accumulate("ReturnLocId", BookingBundle.getInt("ReturnLocId"));
                    bodyParam.accumulate("CustomerId", BookingBundle.getInt("CustomerId"));
                    bodyParam.accumulate("VehicleTypeId", BookingBundle.getInt("vehiclE_TYPE_ID"));
                    bodyParam.accumulate("VehicleID", BookingBundle.getInt("VehicleID"));
                    bodyParam.accumulate("StrFilterVehicleTypeIds", BookingBundle.getString("StrFilterVehicleTypeIds"));
                    bodyParam.accumulate("StrFilterVehicleOptionIds", BookingBundle.getString("StrFilterVehicleOptionIds"));

                    bodyParam.accumulate("PickupDate", BookingBundle.getString("PickupDate"));
                    bodyParam.accumulate("ReturnDate", BookingBundle.getString("ReturnDate"));
                    bodyParam.accumulate("PickupTime", BookingBundle.getString("PickupTime"));
                    bodyParam.accumulate("ReturnTime", BookingBundle.getString("ReturnTime"));

                    bodyParam.accumulate("FilterTransmission", BookingBundle.getInt("FilterTransmission"));
                    bodyParam.accumulate("FilterPassengers", BookingBundle.getInt("FilterPassengers"));
                    bodyParam.accumulate("BookingStep", BookingBundle.getInt("BookingStep"));

                    bodyParam.accumulate("VehicleClassWise", BookingBundle.getInt("VehicleClassWise"));
                    bodyParam.accumulate("DeliveryChargeLocID", BookingBundle.getInt("DeliveryChargeLocID"));
                    bodyParam.accumulate("DeliveryChargeAmount", BookingBundle.getDouble("DeliveryChargeAmount"));
                    bodyParam.accumulate("PickupChargeLocID", BookingBundle.getInt("PickupChargeLocID"));
                    bodyParam.accumulate("PickupChargeAmount", BookingBundle.getDouble("PickupChargeAmount"));
                    bodyParam.accumulate("DeductibleCoverId", BookingBundle.getInt("DeductibleCoverId"));
                    bodyParam.accumulate("DeductibleCharge", BookingBundle.getDouble("DeductibleCharge"));
                    bodyParam.accumulate("EquipmentList", new JSONArray(BookingBundle.getString("EquipmentList")));
                    bodyParam.accumulate("MiscList", new JSONArray(BookingBundle.getString("MiscList")));
                    bodyParam.accumulate("SummaryOfCharges", new JSONArray(BookingBundle.getString("SummaryOfCharges")));
                    // bodyParam.accumulate("DeliveryAndPickupModel", new JSONArray(BookingBundle.getString("DeliveryAndPickupModel")));
                    System.out.println(bodyParam);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                AndroidNetworking.initialize(getActivity());
                Fragment_Finalize_Your_Rental.context = getActivity();

                ApiService ApiService = new ApiService(getTaxtDetails, RequestType.POST,
                        BOOKING, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
            }
            else {
                JSONObject bodyParam = new JSONObject();
                try {
                    bodyParam.accumulate("ForTransId", BookingBundle.getInt("ForTransId"));
                    bodyParam.accumulate("BookingType", BookingBundle.getInt("BookingType"));

                    bodyParam.accumulate("PickupDate", BookingBundle.getString("PickupDate"));
                    bodyParam.accumulate("ReturnDate", BookingBundle.getString("ReturnDate"));
                    bodyParam.accumulate("PickupTime", BookingBundle.getString("PickupTime"));
                    bodyParam.accumulate("ReturnTime", BookingBundle.getString("ReturnTime"));

                    bodyParam.accumulate("PickupLocId", BookingBundle.getInt("PickupLocId"));
                    bodyParam.accumulate("ReturnLocId", BookingBundle.getInt("ReturnLocId"));
                    bodyParam.accumulate("VehicleClassWise",BookingBundle.getInt("VehicleClassWise"));
                    // bodyParam.accumulate("CustomerId", 0);
                    bodyParam.accumulate("FirstName", StrFirstName);
                    bodyParam.accumulate("LastName", StrLastName);
                    bodyParam.accumulate("OfficialEmail", driverEmail.getText().toString());
                    bodyParam.accumulate("MobileNumber", driverPhone.getText().toString());
                    bodyParam.accumulate("VehicleID", BookingBundle.getInt("VehicleID"));
                    bodyParam.accumulate("VehicleTypeId", BookingBundle.getInt("vehiclE_TYPE_ID"));
                    bodyParam.accumulate("DeductibleCoverId", BookingBundle.getInt("DeductibleCoverId"));
                    bodyParam.accumulate("DeductibleCharge", BookingBundle.getDouble("DeductibleCharge"));
                    bodyParam.accumulate("BookingStep", BookingBundle.getInt("BookingStep"));
                    //  bodyParam.accumulate("taxModel", new JSONArray(BookingBundle.getString("taxModel")));
                    bodyParam.accumulate("EquipmentList", new JSONArray(BookingBundle.getString("EquipmentList")));
                    bodyParam.accumulate("MiscList", new JSONArray(BookingBundle.getString("MiscList")));
                    bodyParam.accumulate("SummaryOfCharges", new JSONArray(BookingBundle.getString("SummaryOfCharges")));
                    System.out.println(bodyParam);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AndroidNetworking.initialize(getActivity());
                Fragment_Finalize_Your_Rental.context = getActivity();

                ApiService ApiService = new ApiService(getTaxtDetails, RequestType.POST,
                        BOOKING, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    OnResponseListener getTaxtDetails = new OnResponseListener()
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
                            try
                            {
                                JSONObject resultSet = responseJSON.getJSONObject("resultSet");
                                final JSONArray getsummaryOfCharges = resultSet.getJSONArray("summaryOfCharges");

                                final JSONArray taxModel = resultSet.getJSONArray("taxModel");

                                final RelativeLayout rlSummaryOfCharge = getActivity().findViewById(R.id.rl_SummaryOfCharges);

                                int len;
                                len = getsummaryOfCharges.length();

                                for (int j = 0; j < len; j++)
                                {
                                    final JSONObject test = (JSONObject) getsummaryOfCharges.get(j);
                                    final int sortId = test.getInt("sortId");
                                    final int isdisplay = test.getInt("isDisplay");
                                    if(test.has("chargeCode"))
                                    {
                                        final String chargeCode = test.getString("chargeCode");
                                    }

                                    final String chargeName = test.getString("chargeName");
                                    final Double chargeAmount = test.getDouble("chargeAmount");

                                    if (chargeName.equals("Estimated Total"))
                                    {
                                        txtTotalAmount.setText(((String.format(Locale.US,"%.2f",chargeAmount))));
                                        BookingBundle.putDouble("total",chargeAmount);
                                    }

                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                    lp.setMargins(0, 0, 0, 0);

                                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.vehicle_tax_details, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                    linearLayout.setId(200 + j);
                                    linearLayout.setLayoutParams(lp);

                                    TextView lbl_chargeAmount, lblchargeName;
                                    LinearLayout arrowIcon = (LinearLayout) linearLayout.findViewById(R.id.arrow_icon);
                                    lbl_chargeAmount = (TextView) linearLayout.findViewById(R.id.lbl_chargeAmount);
                                    lblchargeName = (TextView) linearLayout.findViewById(R.id.lblchargeName);

                                    if (sortId == 10)
                                    {
                                        arrowIcon.setVisibility(View.VISIBLE);
                                    }

                                    arrowIcon.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            Bundle Booking = new Bundle();
                                            BookingBundle.putInt("BookingStep", 4);
                                            Booking.putBundle("VehicleBundle", VehicleBundle);
                                            Booking.putBundle("BookingBundle", BookingBundle);
                                            Booking.putBundle("returnLocation", returnLocationBundle);
                                            Booking.putBundle("location", locationBundle);
                                            Booking.putBoolean("locationType", locationType);
                                            Booking.putBoolean("initialSelect", initialSelect);
                                            Booking.putInt("TaxType",2);
                                            Booking.putDouble("TaxesAndFeesAmount", chargeAmount);
                                            Booking.putString("taxModel",taxModel.toString());
                                            System.out.println(Booking);
                                            NavHostFragment.findNavController(Fragment_Finalize_Your_Rental.this)
                                                    .navigate(R.id.action_Finalize_your_rental_to_Total_tax_fee_details, Booking);
                                        }
                                    });

                                    String str=String.format(Locale.US,"%.2f",chargeAmount);

                                    if(chargeName.equals("Discount Applied"))
                                    {
                                        lbl_chargeAmount.setTextColor(getResources().getColor(R.color.btn_bg_color_2));
                                    }

                                    if (chargeName.equals("Estimated Total"))
                                    {
                                        lbl_chargeAmount.setTextColor(getResources().getColor(R.color.selected_dot));
                                        lblchargeName.setTextColor(getResources().getColor(R.color.selected_dot));
                                    }

                                    lbl_chargeAmount.setText("USD$ "+str);
                                    lblchargeName.setText(chargeName);
                                    if (isdisplay!=1){
                                        linearLayout.setVisibility(View.GONE);
                                    }

                                    rlSummaryOfCharge.addView(linearLayout);
                                }

                                final JSONArray getIncludeDetails = resultSet.getJSONArray("includesItem");
                                RelativeLayout rlIncludeDetails = getActivity().findViewById(R.id.rl_includeDetailsItem);
                                int len1;
                                len1 = getIncludeDetails.length();

                                for (int k = 0; k < len1; k++)
                                {
                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp.addRule(RelativeLayout.BELOW, (200 + (k / 3) - 1));
                                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                    lp.setMargins(0, 0, 0, 0);

                                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    LinearLayout linearLayout1 = (LinearLayout) inflater.inflate(R.layout.include_details, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                    linearLayout1.setId(200 + (k / 3));
                                    linearLayout1.setLayoutParams(lp);

                                    TextView txt_InsurancePro, txt_carMaintanance, txt_roadsideAssistance;

                                    txt_InsurancePro = (TextView) linearLayout1.findViewById(R.id.lbl_InsurancePro);
                                    txt_carMaintanance = (TextView) linearLayout1.findViewById(R.id.lbl_carMaintanance);
                                    txt_roadsideAssistance = (TextView) linearLayout1.findViewById(R.id.lbl_roadsideAssistance);

                                    if (k < len1 && (k % 3) == 0)
                                    {
                                        final JSONObject test = (JSONObject) getIncludeDetails.get(k);
                                        final int includesId = test.getInt("includesId");
                                        final String includesName = test.getString("includesName");

                                        txt_InsurancePro.setText(includesName);
                                        k++;
                                    }
                                    if (k < len1 && (k% 3) == 1)
                                    {
                                        final JSONObject test = (JSONObject) getIncludeDetails.get(k);
                                        final int includesId = test.getInt("includesId");
                                        final String includesName = test.getString("includesName");

                                        txt_carMaintanance.setText(includesName);
                                        k++;
                                    }
                                    if (k < len1 && (k % 3) == 2)
                                    {
                                        final JSONObject test = (JSONObject) getIncludeDetails.get(k);
                                        final int includesId = test.getInt("includesId");
                                        final String includesName = test.getString("includesName");

                                        txt_roadsideAssistance.setText(includesName);
                                        k++;
                                    }


                                    rlIncludeDetails.addView(linearLayout1);
                                }

                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
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

      /* String dateFormatPickupDate = (BookingBundle.getString("PickupDate"));
                    String strPickUpTime1 = (BookingBundle.getString("PickupTime"));
                    String PickupDateTime = dateFormatPickupDate + "T" + strPickUpTime1;
                    bodyParam.accumulate("PickupDate", PickupDateTime);
                    String dateFormatReturnDate = (BookingBundle.getString("ReturnDate"));
                    String strReturnTime1 = (BookingBundle.getString("ReturnTime"));
                    String ReturnDateTime = dateFormatReturnDate + "T" + strReturnTime1;*/
                     /* driverdetails_icon1.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (driverdetails1.getVisibility() == View.GONE)
                        {
                            driverdetails1.setVisibility(View.VISIBLE);
                        } else
                            driverdetails1.setVisibility(View.GONE);
                    }
                });*/

