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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
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

public class Fragment_Select_Location1 extends Fragment
{
    LinearLayout lblconfirm,greenlayout,blacklayout;
    TextView TotalChargeAmount,txt_Discard,txtvehDesc;
    RelativeLayout rlTaxDetails;
    ImageView backarrow,CarImage,ArrowSummaryofCharges,ArrowSummaryofChargesdown;
    Bundle BookingBundle,VehicleBundle;
    public static Context context;
    public String id = "";
    Handler handler = new Handler();
    String VehImage="";
    JSONArray summaryOfCharges = new JSONArray();

    TextView txt_PickupDate,txt_PickupTime,txt_ReturnDate,txt_ReturnTIme,txt_Seats,txt_Bags,txt_Automatic,txt_Doors,txt_Days,
            txt_vehiclE_NAME,txt_vehiclE_TYPE_NAME,txtpayNow,txtPayLater,txt_taxrate,txtpickuplocName,txtreturnLocName;

    Bundle returnLocationBundle, locationBundle;
    Boolean locationType, initialSelect;
    ImageLoader imageLoader;
    String serverpath="";

    public static void initImageLoader(Context context)
    {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.MAX_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(100 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_location1, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        try {
            super.onViewCreated(view, savedInstanceState);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            initImageLoader(getActivity());
            imageLoader = ImageLoader.getInstance();

            TotalChargeAmount = view.findViewById(R.id.txt_TotalChargeAmount);
            txt_Discard = view.findViewById(R.id.txt_DiscardSelectLoc);

            lblconfirm = view.findViewById(R.id.lbl_confirm);
            backarrow = view.findViewById(R.id.backbtn);
            greenlayout = view.findViewById(R.id.green_layout);
            blacklayout = view.findViewById(R.id.black_layout);
            rlTaxDetails = view.findViewById(R.id.rl_tax_details);
            txtvehDesc= view.findViewById(R.id.txtvehDesc);
            ArrowSummaryofCharges= view.findViewById(R.id.ArrowSummaryofCharges);
            ArrowSummaryofChargesdown= view.findViewById(R.id.ArrowSummaryofChargesDown);

            txt_PickupDate = view.findViewById(R.id.txt_PickupDate);
            txt_PickupTime = view.findViewById(R.id.txt_PickupTime);
            txt_ReturnDate = view.findViewById(R.id.txt_ReturnDate);
            txt_ReturnTIme = view.findViewById(R.id.txt_ReturnTime);
            txt_Seats = view.findViewById(R.id.txt_Seats);
            txt_Bags = view.findViewById(R.id.txt_Bags);
            txt_Automatic = view.findViewById(R.id.txt_Automatic);
            txt_Doors = view.findViewById(R.id.txt_Doors);
            txt_Days = view.findViewById(R.id.txt_TotalDays);
            txt_taxrate = view.findViewById(R.id.txt_rate);
            txtpickuplocName = view.findViewById(R.id.pickuplocName);
            txtreturnLocName = view.findViewById(R.id.returnLocName);

            txt_vehiclE_TYPE_NAME = view.findViewById(R.id.txt_vahTypeName);
            txt_vehiclE_NAME = view.findViewById(R.id.txt_VehicleName);

            txtpayNow = view.findViewById(R.id.txt_paymentNow);
            txtPayLater = view.findViewById(R.id.txt_PaymentLater);

            CarImage = view.findViewById(R.id.Veh_image_bg);

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            serverpath = sp.getString("serverPath", "");

            BookingBundle = getArguments().getBundle("BookingBundle");
            VehicleBundle = getArguments().getBundle("VehicleBundle");

            returnLocationBundle = getArguments().getBundle("returnLocation");
            locationBundle = getArguments().getBundle("location");
            locationType = getArguments().getBoolean("locationType");
            initialSelect = getArguments().getBoolean("initialSelect");

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

            txtpickuplocName.setText(BookingBundle.getString("PickupLocName"));
            txtreturnLocName.setText(BookingBundle.getString("ReturnLocName"));

            String DayStr=VehicleBundle.getString("totaL_DAYS");
            DayStr=DayStr.substring(0,DayStr.length()-2);
            txt_Days.setText(DayStr);

            String Seats=VehicleBundle.getString("vehiclE_SEAT_NO");
            txt_Seats.setText(Seats+"Seats");

            String Bags=VehicleBundle.getString("veh_bags");
            txt_Bags.setText(Bags+"Bags");

            txt_Automatic.setText(VehicleBundle.getString("transmission_Name"));

            String doors=VehicleBundle.getString("doors");
            txt_Doors.setText(doors+"Doors");

            txtvehDesc.setText(VehicleBundle.getString("vehDescription"));

            txt_vehiclE_NAME.setText(VehicleBundle.getString("vehiclE_NAME"));
            txt_vehiclE_TYPE_NAME.setText(VehicleBundle.getString("vehiclE_TYPE_NAME"));

            txt_taxrate.setText(VehicleBundle.getString("rate_ID"));

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
                                    NavHostFragment.findNavController(Fragment_Select_Location1.this)
                                            .navigate(R.id.action_Select_location1_to_Search_activity);
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
            lblconfirm.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
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
                    NavHostFragment.findNavController(Fragment_Select_Location1.this)
                            .navigate(R.id.action_Select_location1_to_Select_addtional_options, Booking);
                }
            });
            backarrow.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle Booking = new Bundle();
                    BookingBundle.putInt("BookingStep", 1);
                    BookingBundle.putInt("VehicleID", 0);
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    System.out.println(Booking);
                    NavHostFragment.findNavController(Fragment_Select_Location1.this)
                            .navigate(R.id.action_Select_location1_to_Vehicles_Available, Booking);
                }
            });
            final Boolean[] isOnePressed = {true};
            final Boolean[] isSecondPlace = {true};
            greenlayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View arg0) {
                    isOnePressed[0] = true;
                    greenlayout.setBackground(getResources().getDrawable(R.drawable.ic_rectangle_yellow_green));
                    if (isSecondPlace[0]) {
                        blacklayout.setBackground(getResources().getDrawable(R.drawable.ic_rectangle_blackbox));
                        isSecondPlace[0] = false;
                    }
                }
            });
            blacklayout.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View arg0) {
                    blacklayout.setBackground(getResources().getDrawable(R.drawable.ic_rectangle_yellow_green));
                    isSecondPlace[0] = true;
                    if (isOnePressed[0]) {
                        greenlayout.setBackground(getResources().getDrawable(R.drawable.ic_rectangle_blackbox));
                        isOnePressed[0] = false;
                    }
                }
            });
            ArrowSummaryofCharges.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    if (rlTaxDetails.getVisibility() == View.GONE)
                    {
                        rlTaxDetails.setVisibility(View.VISIBLE);
                        ArrowSummaryofChargesdown.setVisibility(View.VISIBLE);
                        ArrowSummaryofCharges.setVisibility(View.GONE);
                    }
                }
            });
            ArrowSummaryofChargesdown.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(rlTaxDetails.getVisibility() == View.VISIBLE)
                    {
                        rlTaxDetails.setVisibility(View.GONE);
                        ArrowSummaryofCharges.setVisibility(View.VISIBLE);
                        ArrowSummaryofChargesdown.setVisibility(View.GONE);
                    }
                }
            });
            AndroidNetworking.initialize(getActivity());
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

                String dateFormatPickupDate = (BookingBundle.getString("PickupDate"));
                String strPickUpTime1 = (BookingBundle.getString("PickupTime"));
                String PickupDateTime=dateFormatPickupDate+"T"+strPickUpTime1;

                bodyParam.accumulate("PickupDate",PickupDateTime);

                String dateFormatReturnDate = (BookingBundle.getString("ReturnDate"));
                String strReturnTime1 = (BookingBundle.getString("ReturnTime"));
                String ReturnDateTime=dateFormatReturnDate+"T"+strReturnTime1;

                bodyParam.accumulate("ReturnDate", ReturnDateTime);

                bodyParam.accumulate("FilterTransmission", BookingBundle.getInt("FilterTransmission"));
                bodyParam.accumulate("FilterPassengers", BookingBundle.getInt("FilterPassengers"));
                bodyParam.accumulate("BookingStep", BookingBundle.getInt("BookingStep"));
                bodyParam.accumulate("BookingType", BookingBundle.getInt("BookingType"));
                System.out.println(bodyParam);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            ApiService ApiService = new ApiService(getTaxtDetails, RequestType.POST,
                    BOOKING, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
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

                                final JSONArray getTaxDetails = resultSet.getJSONArray("taxModel");
                                BookingBundle.putString("TaxList", getTaxDetails.toString());

                                final JSONArray getIncludeDetails = resultSet.getJSONArray("includesItem");
                                RelativeLayout rlIncludeDetails = getActivity().findViewById(R.id.rl_includeDetails);
                                //getsummaryOfCharges
                                final JSONArray getsummaryOfCharges = resultSet.getJSONArray("summaryOfCharges");
                                final RelativeLayout rlVehicleTaxDetails = getActivity().findViewById(R.id.rl_tax_details);

                                int len;
                                len = getsummaryOfCharges.length();

                                for (int j = 0; j < len; j++)
                                {
                                    final JSONObject test = (JSONObject) getsummaryOfCharges.get(j);

                                    final  int sortId = test.getInt("sortId");
                                    final  String chargeCode = test.getString("chargeCode");
                                    final  String chargeName = test.getString("chargeName");
                                    final  double chargeAmount=test.getDouble("chargeAmount");

                                    JSONObject summaryOfChargesObj = new JSONObject();

                                    summaryOfChargesObj.put("sortId",sortId);
                                    summaryOfChargesObj.put("chargeCode",chargeCode);
                                    summaryOfChargesObj.put("chargeName",chargeName);
                                    summaryOfChargesObj.put("chargeAmount",chargeAmount);

                                    summaryOfCharges.put(summaryOfChargesObj);

                                    if(chargeName.equals("Estimated Total"))
                                    {
                                        TotalChargeAmount.setText((String.format(Locale.US,"%.2f",chargeAmount)));
                                    }
                                    if (sortId==1)
                                    {
                                        txtpayNow.setText((String.format(Locale.US,"%.2f",chargeAmount)));
                                        txtPayLater.setText((String.format(Locale.US,"%.2f",chargeAmount)));
                                    }

                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                    lp.setMargins(0, 0, 0, 0);

                                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.vehicle_tax_details, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                    linearLayout.setId(200 + j);
                                    linearLayout.setLayoutParams(lp);

                                    TextView txt_charge,txt_chargeName;
                                    LinearLayout arrowIcon=(LinearLayout)linearLayout.findViewById(R.id.arrow_icon);

                                    if(chargeName.equals("Taxes And Fees"))
                                    {
                                        arrowIcon.setVisibility(View.VISIBLE);

                                        arrowIcon.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                Bundle Booking = new Bundle();
                                                Booking.putBundle("VehicleBundle", VehicleBundle);
                                                Booking.putBundle("BookingBundle", BookingBundle);
                                                Booking.putBundle("returnLocation", returnLocationBundle);
                                                Booking.putBundle("location", locationBundle);
                                                Booking.putBoolean("locationType", locationType);
                                                Booking.putBoolean("initialSelect", initialSelect);
                                                Booking.putInt("TaxType",1);
                                                Booking.putString("taxModel",getTaxDetails.toString());
                                                NavHostFragment.findNavController(Fragment_Select_Location1.this)
                                                        .navigate(R.id.action_Select_location1_to_Total_tax_fee_details,Booking);
                                            }
                                        });

                                    }


                                    txt_charge= (TextView)linearLayout.findViewById(R.id.lbl_chargeAmount);
                                    txt_chargeName=(TextView)linearLayout.findViewById(R.id.lblchargeName);

                                    String str=String.format(Locale.US,"%.2f",chargeAmount);
                                    txt_charge.setText("USD$ "+str);
                                    txt_chargeName.setText(chargeName);

                                    rlVehicleTaxDetails.addView(linearLayout);

                                    if(chargeName.equals("Discount Applied"))
                                    {
                                        txt_charge.setTextColor(getResources().getColor(R.color.btn_bg_color_2));
                                    }
                                }
                                //getIncludeDetails
                                int len1;
                                len1 = getIncludeDetails.length();
                                for (int j = 0; j < len1;)
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
                                    txt_InsurancePro =(TextView) linearLayout1.findViewById(R.id.lbl_InsurancePro);
                                    txt_carMaintanance = (TextView)linearLayout1.findViewById(R.id.lbl_carMaintanance);
                                    txt_roadsideAssistance =(TextView) linearLayout1.findViewById(R.id.lbl_roadsideAssistance);


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
