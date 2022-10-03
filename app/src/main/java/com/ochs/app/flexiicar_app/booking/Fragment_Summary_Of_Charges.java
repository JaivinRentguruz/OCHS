package com.ochs.app.flexiicar_app.booking;

import android.content.Context;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.flexiicar_app.user.Fragment_Summary_Of_Charges_For_Reservation;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.CANCELBOOKING;

public class Fragment_Summary_Of_Charges extends Fragment
{
    Handler handler = new Handler();
    public static Context context;
    ImageView SummaryOfChargeArrow,SummaryOfChargeArrowDown,CarImage;
    RelativeLayout rlSummaryOfCharge;
    TextView txt_PickLocName, txt_ReturnLocName, txt_PickupDate, txt_ReturnDate, txt_PickupTime, txt_ReturnTIme, txtDays, txt_vehicletype,
            txt_vehName, txt_rate, txt_Seats, txt_Bags, txt_Automatic, txt_Doors, txt_conformationNo,txt_Home,txt_createdDate;
    ImageView QR_Image, arrowSelfcheckOut, driverdetails_icon;
    LinearLayout lblterm_condition1, Layout_SelfCheckout;
    LinearLayout llCancelBooking, llModify;
    JSONArray summaryOfCharges = new JSONArray();
    LinearLayout llHome;
    ImageLoader imageLoader;
    String domainName="",serverPath="",VehImage="";
    LinearLayout AddCalender;

    Bundle BookingBundle, VehicleBundle;
    Bundle returnLocationBundle, locationBundle;
    Boolean locationType, initialSelect;
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_summary_of_charges, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        try {
            super.onViewCreated(view, savedInstanceState);

            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            initImageLoader(getActivity());
            imageLoader = ImageLoader.getInstance();

            AddCalender=view.findViewById(R.id.lblAddCalender);
            llHome=view.findViewById(R.id.llHome);
            lblterm_condition1 = view.findViewById(R.id.term_condition);
            arrowSelfcheckOut = view.findViewById(R.id.arrow_Selfcheckout);
            Layout_SelfCheckout = view.findViewById(R.id.Layout_SelfCheckout);
            txt_conformationNo= view.findViewById(R.id.lbl_conformationNo);
            llCancelBooking = view.findViewById(R.id.llCancelBooking);
            llModify = view.findViewById(R.id.llModify);

            SummaryOfChargeArrowDown = view.findViewById(R.id.ArrowDownForSummary);
            SummaryOfChargeArrow= view.findViewById(R.id.ArrowForSummary);

            rlSummaryOfCharge = view.findViewById(R.id.rl_Summaryofcharge);
            QR_Image = view.findViewById(R.id.QR_Image);
            txt_createdDate= view.findViewById(R.id.lbl_createdDate);

            txt_PickLocName = view.findViewById(R.id.lblPic_LocName);
            txt_PickupDate = view.findViewById(R.id.lblPic_LocDate);
            txt_PickupTime = view.findViewById(R.id.txtPic_LocTime);
            txt_ReturnLocName = view.findViewById(R.id.lblReturn_LocName);
            txt_ReturnDate = view.findViewById(R.id.lblReturn_Date);
            txt_ReturnTIme = view.findViewById(R.id.txtReturn_Time);

            txtDays = view.findViewById(R.id.lbl_TotalDay1);
            txt_rate = view.findViewById(R.id.txt_Vehiclerate1);
            txt_Seats = view.findViewById(R.id.textView_Seats1);
            txt_Bags = view.findViewById(R.id.textView_Bags1);
            txt_Automatic = view.findViewById(R.id.lblTransmissionName);
            txt_Doors = view.findViewById(R.id.txt_door1);

            CarImage=view.findViewById(R.id.Veh_image_bg4);

            txt_vehName = view.findViewById(R.id.vehiclE_NAME);
            txt_vehicletype = view.findViewById(R.id.txtvehiclE_TYPE_NAME);
            llHome.setVisibility(View.VISIBLE);

            BookingBundle = getArguments().getBundle("BookingBundle");
            VehicleBundle = getArguments().getBundle("VehicleBundle");

            returnLocationBundle = getArguments().getBundle("returnLocation");
            locationBundle = getArguments().getBundle("location");
            locationType = getArguments().getBoolean("locationType");
            initialSelect = getArguments().getBoolean("initialSelect");

            txt_PickLocName.setText(getArguments().getBundle("BookingBundle").getString("PickupLocName"));
            txt_ReturnLocName.setText(getArguments().getBundle("BookingBundle").getString("ReturnLocName"));


            String StrPickupDate = (BookingBundle.getString("PickupDate"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(StrPickupDate);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String PickUpDateStr = sdf.format(date);
            txt_PickupDate.setText(PickUpDateStr);


            String StrReturnDate = (BookingBundle.getString("ReturnDate"));
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = dateFormat1.parse(StrReturnDate);
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

            txt_rate.setText(VehicleBundle.getString("rate_ID"));
            VehImage=VehicleBundle.getString("img_Path");

            final LinearLayout driverdetails1 = view.findViewById(R.id.driver_details);

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            serverPath = sp.getString("serverPath", "");
            domainName= sp.getString("domainName", "");
            String id = sp.getString(getString(R.string.id), "");
            String cust_FName = sp.getString("cust_FName", "");
            String cust_LName = sp.getString("cust_LName", "");
            String cust_Email = sp.getString("cust_Email", "");
            String cust_Phoneno = sp.getString("cust_MobileNo", "");

            String url1 = serverPath+VehImage;
            imageLoader.displayImage(url1,CarImage);

            TextView driverName = view.findViewById(R.id.textV_driverName);
            TextView driverPhone = view.findViewById(R.id.TextV_DriverPhoneno);
            TextView driverEmail = view.findViewById(R.id.TextV_DriverEmail);

            driverName.setText(cust_FName + " " + cust_LName);
            driverPhone.setText(cust_Phoneno);
            driverEmail.setText(cust_Email);

            driverdetails_icon = view.findViewById(R.id.driverdetails_icon);
            driverdetails_icon.setOnClickListener(new View.OnClickListener()
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
            });

            AddCalender.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Calendar calendarEvent = Calendar.getInstance();
                    Intent i = new Intent(Intent.ACTION_EDIT);
                    i.setType("vnd.android.cursor.item/event");
                    i.putExtra("Date",calendarEvent.getTimeInMillis());
                    i.putExtra("allDay", true);
                    i.putExtra("rule", "FREQ=YEARLY");
                    i.putExtra("endTime", calendarEvent.getTimeInMillis() + 60 * 60 * 1000);
                    i.putExtra("title", "Calendar Event");
                    startActivity(i);
                }
            });

            arrowSelfcheckOut.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    arrowSelfcheckOut.setVisibility(View.INVISIBLE);
                    if(Layout_SelfCheckout.getVisibility()== View.GONE)
                    {
                        Layout_SelfCheckout.setVisibility(View.VISIBLE);

                    }
                    else
                        Layout_SelfCheckout.setVisibility(View.GONE);
                }
            });


            lblterm_condition1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle Booking = new Bundle();
                    Booking.putInt("termcondition", 4);
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    NavHostFragment.findNavController(Fragment_Summary_Of_Charges.this)
                            .navigate(R.id.action_Summary_Of_Charges_to_Term_and_Condtion,Booking);
                }
            });
            txt_Home=view.findViewById(R.id.txt_Home);
            txt_Home.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    NavHostFragment.findNavController(Fragment_Summary_Of_Charges.this)
                            .navigate(R.id.action_Summary_Of_Charges_to_Search_activity);
                }
            });

            SummaryOfChargeArrow.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    if (rlSummaryOfCharge.getVisibility() == View.GONE)
                    {
                        rlSummaryOfCharge.setVisibility(View.VISIBLE);
                        SummaryOfChargeArrowDown.setVisibility(View.VISIBLE);
                        SummaryOfChargeArrow.setVisibility(View.GONE);
                    }
                }
            });
            SummaryOfChargeArrowDown.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(rlSummaryOfCharge.getVisibility() == View.VISIBLE)
                    {
                        rlSummaryOfCharge.setVisibility(View.GONE);
                        SummaryOfChargeArrow.setVisibility(View.VISIBLE);
                        SummaryOfChargeArrowDown.setVisibility(View.GONE);
                    }
                }
            });

            try {
                JSONObject bodyParam = new JSONObject();
                try {
                    bodyParam.accumulate("ForTransId",BookingBundle.getInt("ForTransId"));
                    bodyParam.accumulate("BookingStep",BookingBundle.getInt("BookingStep"));
                    System.out.println(bodyParam);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AndroidNetworking.initialize(getActivity());
                Fragment_Summary_Of_Charges.context = getActivity();

                 ApiService ApiService = new ApiService(getTaxtDetails, RequestType.POST,
                         BOOKING, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            llCancelBooking.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    JSONObject bodyParam = new JSONObject();
                    try {
                        bodyParam.accumulate("bookingId", Integer.parseInt(BookingBundle.getString("transactionId")));
                        System.out.println(bodyParam);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AndroidNetworking.initialize(getActivity());
                    Fragment_Summary_Of_Charges_For_Reservation.context = getActivity();

                    ApiService ApiService = new ApiService(CancelBooking, RequestType.GET,
                       CANCELBOOKING, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
                }
            });

            llModify.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle Booking = new Bundle();
                    BookingBundle.putInt("BookingStep", 3);
                    BookingBundle.putString("SummaryOfCharges", summaryOfCharges.toString());
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    Booking.putString("DeliveryAndPickupModel", "");
                    NavHostFragment.findNavController(Fragment_Summary_Of_Charges.this)
                            .navigate(R.id.action_Summary_Of_Charges_to_Select_addtional_options,Booking);
                }
            });

        } catch (Exception e)
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
                                final RelativeLayout rlSummaryOfCharge = getActivity().findViewById(R.id.rl_Summaryofcharge);

                                final JSONArray taxModel = resultSet.getJSONArray("taxModel");

                                JSONObject ConformationBooking = resultSet.getJSONObject("t0050_Documents");
                                int doc_ID=ConformationBooking.getInt("doc_ID");
                                String trans_ID=ConformationBooking.getString("trans_ID");

                                String doc_Details=ConformationBooking.getString("doc_Details");
                                String doc_Name=ConformationBooking.getString("doc_Name");
                                int doc_Status=ConformationBooking.getInt("doc_Status");
                                String created_Date=ConformationBooking.getString("created_Date");

                                String url2 = serverPath+doc_Details.substring(2);
                              ///  url2=url2.substring(0,url2.lastIndexOf("/")+1)+doc_Name;
                                imageLoader.displayImage(url2, QR_Image);

                                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                                Date date2 = dateFormat2.parse(created_Date);
                                SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy,HH:mm aa",Locale.US);
                                String Issue_DateStr = sdf2.format(date2);

                                txt_createdDate.setText("SEE YOU ON "+Issue_DateStr);
                                txt_conformationNo.setText(trans_ID);

                                //VehicleModel
                                JSONObject vehicleModel = resultSet.getJSONObject("vehicleModel");
                                int vehiclE_ID=vehicleModel.getInt("vehiclE_ID");
                                int vehiclE_TYPE_ID=vehicleModel.getInt("vehiclE_TYPE_ID");
                                String vehiclE_NAME=vehicleModel.getString("vehiclE_NAME");
                                String vehiclE_TYPE_NAME=vehicleModel.getString("vehiclE_TYPE_NAME");
                                int vehiclE_SEAT_NO=vehicleModel.getInt("vehiclE_SEAT_NO");
                                double totaL_DAYS=vehicleModel.getDouble("totaL_DAYS");
                                int veh_bags=vehicleModel.getInt("veh_bags");
                                int doors=vehicleModel.getInt("doors");
                                String transmission_Name=vehicleModel.getString("transmission_Name");
                                final double hourlyMilesAllowed = vehicleModel.getDouble("hourlyMilesAllowed");
                                final double halfDayMilesAllowed = vehicleModel.getDouble("halfDayMilesAllowed");
                                final double dailyMilesAllowed = vehicleModel.getDouble("dailyMilesAllowed");
                                final double weeklyMilesAllowed = vehicleModel.getDouble("weeklyMilesAllowed");
                                final double monthlyMilesAllowed = vehicleModel.getDouble("monthlyMilesAllowed");
                                final double totalMilesAllowed = vehicleModel.getDouble("totalMilesAllowed");
                                final int lockKey = vehicleModel.getInt("lockKey");

                                txtDays.setText(String.valueOf(totaL_DAYS));

                                txt_vehName.setText(vehiclE_NAME);
                                txt_vehicletype.setText(vehiclE_TYPE_NAME);
                                String Seats=String.valueOf(vehiclE_SEAT_NO);
                                txt_Seats.setText(Seats+" Seats");
                                String Bags=String.valueOf(veh_bags);
                                txt_Bags.setText(Bags+" Bags");
                                txt_Automatic.setText(transmission_Name);
                                String door=String.valueOf(doors);
                                txt_Doors.setText(door+" Doors");

                                //Summary Of Charges
                                int len;
                                len = getsummaryOfCharges.length();

                                for (int j = 0; j < len; j++)
                                {
                                    final JSONObject test = (JSONObject) getsummaryOfCharges.get(j);

                                    final int sortId = test.getInt("sortId");
                                    final String chargeCode = test.getString("chargeCode");
                                    final String chargeName = test.getString("chargeName");
                                    final Double chargeAmount = test.getDouble("chargeAmount");

                                    JSONObject summaryOfChargesObj = new JSONObject();

                                    summaryOfChargesObj.put("sortId", sortId);
                                    summaryOfChargesObj.put("chargeCode", chargeCode);
                                    summaryOfChargesObj.put("chargeName", chargeName);
                                    summaryOfChargesObj.put("chargeAmount", chargeAmount);

                                    summaryOfCharges.put(summaryOfChargesObj);

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
                                            Booking.putBundle("VehicleBundle", VehicleBundle);
                                            Booking.putBundle("BookingBundle", BookingBundle);
                                            BookingBundle.putInt("BookingStep", 6);
                                            Booking.putInt("TaxType",5);
                                            Booking.putDouble("TaxesAndFeesAmount", chargeAmount);
                                            Booking.putString("taxModel",taxModel.toString());
                                            NavHostFragment.findNavController(Fragment_Summary_Of_Charges.this)
                                             .navigate(R.id.action_Summary_Of_Charges_to_Total_tax_fee_details,Booking);
                                        }
                                    });

                                    lbl_chargeAmount = (TextView) linearLayout.findViewById(R.id.lbl_chargeAmount);
                                    lblchargeName = (TextView) linearLayout.findViewById(R.id.lblchargeName);

                                    String strChargeAmount=(String.format(Locale.US,"%.2f",chargeAmount));
                                    lbl_chargeAmount.setText("US$ "+strChargeAmount);

                                    lblchargeName.setText(chargeName);

                                    rlSummaryOfCharge.addView(linearLayout);
                                }
                               /* if(resultSet.equals("includesItem"))
                                {
                                    try {
                                        final JSONArray getIncludeDetails = resultSet.getJSONArray("includesItem");
                                        RelativeLayout rlIncludeDetails = getActivity().findViewById(R.id.rl_includeDetails1);

                                        int len1;
                                        len1 = getIncludeDetails.length();
                                        for (int j = 0; j < len1; )
                                        {

                                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                            lp.addRule(RelativeLayout.BELOW, (200 + (j / 3) - 1));
                                            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                            lp.setMargins(0, 0, 0, 0);

                                            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            LinearLayout linearLayout1 = (LinearLayout) inflater.inflate(R.layout.include_details, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                            linearLayout1.setId(200 + (j / 3));
                                            linearLayout1.setLayoutParams(lp);

                                            TextView txt_InsurancePro, txt_carMaintanance, txt_roadsideAssistance;
                                            txt_InsurancePro = (TextView) linearLayout1.findViewById(R.id.txt_InsurancePro);
                                            txt_carMaintanance = (TextView) linearLayout1.findViewById(R.id.txt_carMaintanance);
                                            txt_roadsideAssistance = (TextView) linearLayout1.findViewById(R.id.txt_roadsideAssistance);


                                            if (j < len1 && (j % 3) == 0)
                                            {
                                                final JSONObject test = (JSONObject) getIncludeDetails.get(j);
                                                final int includesId = test.getInt("includesId");
                                                final String includesName = test.getString("includesName");

                                                txt_InsurancePro.setText(includesName);
                                                j++;
                                            }
                                            if (j < len1 && (j % 3) == 1)
                                            {
                                                final JSONObject test = (JSONObject) getIncludeDetails.get(j);
                                                final int includesId = test.getInt("includesId");
                                                final String includesName = test.getString("includesName");

                                                txt_carMaintanance.setText(includesName);
                                                j++;
                                            }
                                            if (j < len1 && (j % 3) == 2)
                                            {
                                                final JSONObject test = (JSONObject) getIncludeDetails.get(j);
                                                final int includesId = test.getInt("includesId");
                                                final String includesName = test.getString("includesName");

                                                txt_roadsideAssistance.setText(includesName);
                                                j++;
                                            }
                                            rlIncludeDetails.addView(linearLayout1);
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }*/

                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
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
        public void onError(String error) {
            System.out.println("Error-" + error);
        }
    };

    OnResponseListener CancelBooking = new OnResponseListener()
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
                        } else {
                            String msg = responseJSON.getString(    "message");
                            CustomToast.showToast(getActivity(),msg,1);
                        }
                    } catch (Exception e)
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
