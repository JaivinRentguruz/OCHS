package com.ochs.app.flexiicar_app.booking;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
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

public class Fragment_Select_addition_Options extends Fragment
{
    LinearLayout lblconfirm,LayoutdeliveryPickupservice,RatingLayout;
    ImageView backarrow, CarImage;
    Bundle BookingBundle,VehicleBundle;
    ToggleButton SwitchVehicleDelivery,SwitchVehicleDeliver2,insuranceDecline;
    TextView lbl_PickupLoc,lbl_PickupDate,lbl_PickupTime,lbl_returnLoc,lbl_ReturnDate,lbl_ReturnTime,txt_vehicletype,txt_vehName,
            txtDays,txtTotalAmount,txt_Discard,txtMileage,defaultIns,currencySymbol,refund;
    Handler handler = new Handler();
    public static Context context;

    JSONArray EquipmentList;

    JSONArray MiscList;
    JSONArray SummaryOfCharges;

    int cmP_DISTANCE;
    Bundle returnLocationBundle, locationBundle;
    Boolean locationType, initialSelect,IsSelected;

    String DeliveryAndPickupModel = "";

    Double DeliveryChargeAmount=0.0, PickupChargeAmount=0.0;
    int DeliveryChargeLocID=0, PickupChargeLocID=0;

    ImageLoader imageLoader;
    String serverpath="";
    public String id = "";
    JSONArray getInsuranceDEtails;
    double TotalValue;
    JSONArray SelectedMiscList;
    JSONArray SelectedEquipmentList;
    RelativeLayout rlInsuranceDetails,rlInsuranceCover2;

    public static void initImageLoader(Context context)
    {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.MAX_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(100 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs();
        ImageLoader.getInstance().init(config.build());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_select_additional_option, container, false);
    }

    @Override
    public void setInitialSavedState(@Nullable @org.jetbrains.annotations.Nullable SavedState state) {
        super.setInitialSavedState(state);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            initImageLoader(getActivity());
            imageLoader = ImageLoader.getInstance();

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            id = sp.getString(getString(R.string.id), "");
            serverpath = sp.getString("serverPath", "");
            cmP_DISTANCE = sp.getInt("cmP_DISTANCE", 0);

            SummaryOfCharges = new JSONArray();
            EquipmentList = new JSONArray();
            MiscList = new JSONArray();
            getInsuranceDEtails= new JSONArray();
            SelectedMiscList = new JSONArray();
            SelectedEquipmentList = new JSONArray();

            lblconfirm = view.findViewById(R.id.lbl_confirm_2);
            backarrow = view.findViewById(R.id.backbtn1);
            LayoutdeliveryPickupservice = view.findViewById(R.id.layout_deliverypickupservice);
            txt_vehicletype = view.findViewById(R.id.txt_vehicleTypeName);
            txt_vehName = view.findViewById(R.id.txt_VehicleMOdelName);
            txtTotalAmount = view.findViewById(R.id.txt_TotalAmount);
            currencySymbol= view.findViewById(R.id.currencySymbol);
            currencySymbol.setText(Helper.currencySymbol);
            lbl_PickupLoc = view.findViewById(R.id.lbl_PickupLoc);
            lbl_PickupDate= view.findViewById(R.id.lbl_PickupDate);
            lbl_PickupTime= view.findViewById(R.id.lbl_PickupTime);
            lbl_returnLoc = view.findViewById(R.id.lbl_returnLoc);
            lbl_ReturnDate=view.findViewById(R.id.lbl_ReturnDate);
            lbl_ReturnTime=view.findViewById(R.id.lbl_ReturnTime);
            txtDays = view.findViewById(R.id.Txt_Days);
            txt_Discard = view.findViewById(R.id.txt_DiscardSAO);
            CarImage = view.findViewById(R.id.Veh_image_bg1);
            txtMileage = view.findViewById(R.id.txtMileage);
            RatingLayout= view.findViewById(R.id.RatingLayout);
            defaultIns = view.findViewById(R.id.defaultIns);
            RatingLayout.setVisibility(View.GONE);
            insuranceDecline=view.findViewById(R.id.insuranceDecline);
            rlInsuranceCover2=view.findViewById(R.id.rlInsuranceCover2);
            refund=view.findViewById(R.id.refund);
            rlInsuranceDetails = view.findViewById(R.id.rlInsuranceCover);

            if (sp.getString("deliveryPickupModule"," ").matches("1")){
                LayoutdeliveryPickupservice.setVisibility(View.GONE);
            }


            SwitchVehicleDelivery =view.findViewById(R.id.switch_VehicleDelivery);
            SwitchVehicleDeliver2 =view.findViewById(R.id.switch_VehicleDelivery2);

            BookingBundle = getArguments().getBundle("BookingBundle");
            VehicleBundle = getArguments().getBundle("VehicleBundle");

            returnLocationBundle = getArguments().getBundle("returnLocation");
            locationBundle = getArguments().getBundle("location");
            locationType = getArguments().getBoolean("locationType");
            initialSelect = getArguments().getBoolean("initialSelect");
            IsSelected = getArguments().getBoolean("IsSelected");
            BookingBundle.putInt("VehicleClassWise",BookingBundle.getInt("VehicleClassWise"));
            DeliveryAndPickupModel = getArguments().getString("DeliveryAndPickupModel");
            txtTotalAmount.setText("0");

            //refund.setText(String.valueOf(VehicleBundle.getDouble("price")));

            if (!DeliveryAndPickupModel.equals(""))
            {
                try {
                    JSONArray DPArray = new JSONArray(DeliveryAndPickupModel);

                    if (DPArray.length() > 0)
                    {
                        TextView deliveryCharge = view.findViewById(R.id.deliveryCharge);

                        deliveryCharge.setText(DPArray.getJSONObject(0).getDouble("chargeAmount") + "");

                        DeliveryChargeLocID = DPArray.getJSONObject(0).getInt("businessLocID");
                        DeliveryChargeAmount = DPArray.getJSONObject(0).getDouble("chargeAmount");

                        SwitchVehicleDelivery.setChecked(true);

                    }

                    if (DPArray.length() > 1)
                    {
                        TextView pickupCharge = view.findViewById(R.id.pickupCharge);

                        pickupCharge.setText(DPArray.getJSONObject(1).getDouble("chargeAmount") + "");

                        PickupChargeLocID = DPArray.getJSONObject(1).getInt("businessLocID");
                        PickupChargeAmount = DPArray.getJSONObject(1).getDouble("chargeAmount");

                        SwitchVehicleDeliver2.setChecked(true);

                    }

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

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
                                    NavHostFragment.findNavController(Fragment_Select_addition_Options.this)
                                            .navigate(R.id.action_Select_addtional_options_to_Search_activity);
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
                    BookingBundle.putInt("BookingStep", 4);
                    BookingBundle.putString("EquipmentList", EquipmentList.toString());
                    BookingBundle.putString("MiscList", MiscList.toString());
                    BookingBundle.putInt("DeliveryChargeLocID", DeliveryChargeLocID);
                    BookingBundle.putDouble("DeliveryChargeAmount", DeliveryChargeAmount);
                    BookingBundle.putInt("PickupChargeLocID", PickupChargeLocID);
                    BookingBundle.putDouble("PickupChargeAmount", PickupChargeAmount);
                    BookingBundle.putString("SummaryOfCharges", SummaryOfCharges.toString());
                    Bundle Booking = new Bundle();
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    Booking.putString("submitmile",txtMileage.getText().toString());
                    System.out.println(Booking);
                    Log.e("TAG", "onClick: " + Booking );
                    NavHostFragment.findNavController(Fragment_Select_addition_Options.this)
                            .navigate(R.id.action_Select_addtional_options_to_Finalize_your_rental, Booking);
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
                    // Booking.putBundle("VehicleBundle", VehicleBundle);
                    System.out.println(Booking);
                    NavHostFragment.findNavController(Fragment_Select_addition_Options.this)
                            .navigate(R.id.action_Select_addtional_options_to_Vehicles_Available, Booking);
                }
            });

            //Pickup Location time date
            String StrPickupDate = (BookingBundle.getString("PickupDate"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(StrPickupDate);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String PickUpDateStr = sdf.format(date);

            String strPickUpTime = (BookingBundle.getString("PickupTime"));
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
            Date date2 = dateFormat2.parse(strPickUpTime);
            SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa", Locale.US);
            String PickUpTimeStr = sdf2.format(date2);
            lbl_PickupLoc.setText(BookingBundle.getString("PickupLocName"));
            lbl_PickupDate.setText(PickUpDateStr);
            lbl_PickupTime.setText(PickUpTimeStr);

            //Return Location time date
            String StrReturnDate = (BookingBundle.getString("ReturnDate"));
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = dateFormat1.parse(StrReturnDate);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            String ReturnDateStr = sdf1.format(date1);

            String strReturntime = (BookingBundle.getString("ReturnTime"));
            SimpleDateFormat dateFormat3 = new SimpleDateFormat("HH:mm");
            Date date3 = dateFormat3.parse(strReturntime);
            SimpleDateFormat sdf3 = new SimpleDateFormat("hh:mm aa", Locale.US);
            String ReturntimeStr = sdf3.format(date3);

            lbl_returnLoc.setText(BookingBundle.getString("ReturnLocName"));
            lbl_ReturnDate.setText(ReturnDateStr);
            lbl_ReturnTime.setText(ReturntimeStr);

            insuranceDecline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        rlInsuranceDetails.setVisibility(View.GONE);
                        rlInsuranceCover2.setVisibility(View.VISIBLE);

                        declineInsurance();
                        BookingBundle.putInt("DeductibleCoverId", 0);
                        BookingBundle.putDouble("DeductibleCharge", 0);

                    } else {
                        rlInsuranceDetails.setVisibility(View.VISIBLE);
                        rlInsuranceCover2.setVisibility(View.GONE);
                    }
                }
            });

            /*insuranceDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (insuranceDecline.isChecked()) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("key", 1);
                        NavHostFragment.findNavController(Fragment_Select_addition_Options.this)
                                .navigate(R.id.action_Select_addtional_options_to_insurance_policy_list, bundle);
                    } else {

                    }
                }
            });*/
            insuranceDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (insuranceDecline.isChecked()) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("key", 1);
                        NavHostFragment.findNavController(Fragment_Select_addition_Options.this)
                                .navigate(R.id.action_Select_addtional_options_to_insurance_policy_list, bundle);
                    }
                }
            });

            txt_Discard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    NavHostFragment.findNavController(Fragment_Select_addition_Options.this)
                            .navigate(R.id.action_Select_location1_to_Search_activity);
                }
            });

            SwitchVehicleDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
                {
                    if (SwitchVehicleDelivery.isChecked())
                    {
                        BookingBundle.putString("EquipmentList", EquipmentList.toString());
                        BookingBundle.putString("MiscList", MiscList.toString());
                        Bundle Booking = new Bundle();
                        Booking.putBundle("BookingBundle", BookingBundle);
                        Booking.putBundle("VehicleBundle", VehicleBundle);
                        Booking.putBundle("returnLocation", returnLocationBundle);
                        Booking.putBundle("location", locationBundle);
                        Booking.putBoolean("locationType", locationType);
                        Booking.putBoolean("initialSelect", initialSelect);
                        Booking.putBoolean("fromMap", false);
                        Booking.putString("DeliveryAndPickupModel", DeliveryAndPickupModel);
                        NavHostFragment.findNavController(Fragment_Select_addition_Options.this)
                                .navigate(R.id.action_Select_addtional_options_to_FilterByVehicleClass, Booking);
                    } else {
                        DeliveryChargeAmount = 0.0;
                        DeliveryChargeLocID = 0;
                    }
                }
            });

            SwitchVehicleDeliver2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
                {
                    if (SwitchVehicleDeliver2.isChecked())
                    {
                        //SwitchVehicleDeliver2.setEnabled(false);
                    } else {
                        PickupChargeLocID = 0;
                        PickupChargeAmount = 0.0;
                    }
                }
            });

            if (!id.equals(""))
            {
                JSONObject bodyParam = new JSONObject();
                try {
                    bodyParam.accumulate("ForTransId", BookingBundle.getInt("ForTransId"));
                    bodyParam.accumulate("PickupLocId", BookingBundle.getInt("PickupLocId"));
                    bodyParam.accumulate("ReturnLocId", BookingBundle.getInt("ReturnLocId"));
                    bodyParam.accumulate("CustomerId", BookingBundle.getInt("CustomerId"));
                    bodyParam.accumulate("VehicleTypeId", BookingBundle.getInt("vehiclE_TYPE_ID"));
                    bodyParam.accumulate("RateId", BookingBundle.getInt("rate_ID"));
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
                    bodyParam.accumulate("BookingType", BookingBundle.getInt("BookingType"));
                    bodyParam.accumulate("VehicleClassWise",BookingBundle.getInt("VehicleClassWise"));
                    System.out.println(bodyParam);

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                AndroidNetworking.initialize(getActivity());
                Fragment_Select_addition_Options.context = getActivity();

                ApiService ApiService = new ApiService(getTaxtDetails, RequestType.POST,
                        BOOKING, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
            }
            else {
                JSONObject bodyParam = new JSONObject();
                try {
                    bodyParam.accumulate("ForTransId", BookingBundle.getInt("ForTransId"));
                    bodyParam.accumulate("PickupLocId", BookingBundle.getInt("PickupLocId"));
                    bodyParam.accumulate("ReturnLocId", BookingBundle.getInt("ReturnLocId"));
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
                    bodyParam.accumulate("BookingType", BookingBundle.getInt("BookingType"));
                    bodyParam.accumulate("VehicleClassWise",BookingBundle.getInt("VehicleClassWise"));
                    System.out.println(bodyParam);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                AndroidNetworking.initialize(getActivity());
                Fragment_Select_addition_Options.context = getActivity();
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

                                //Vehicle Model
                                JSONObject vehicleModel =resultSet.getJSONObject("vehicleModel");

                                final int vehiclE_ID = vehicleModel.getInt("vehiclE_ID");
                                final String vehiclE_NAME = vehicleModel.getString("vehiclE_NAME");
                                final int vehiclE_TYPE_ID = vehicleModel.getInt("vehiclE_TYPE_ID");
                                final String vehiclE_TYPE_NAME = vehicleModel.getString("vehiclE_TYPE_NAME");
                                final String vehiclE_SEAT_NO = vehicleModel.getString("vehiclE_SEAT_NO");
                                final String transmission = vehicleModel.getString("transmission");
                                final String v_CURR_ODOM = vehicleModel.getString("v_CURR_ODOM");
                                final String img_Path = vehicleModel.getString("img_Path");
                                final String vehiclE_OPTIONS_IDS = vehicleModel.getString("vehiclE_OPTIONS_IDS");
                                final String rate_ID = vehicleModel.getString("rate_ID");
                                final String rate_Name = vehicleModel.getString("rate_Name");
                                final int package_ID = vehicleModel.getInt("package_ID");
                                final double totaL_DAYS = vehicleModel.getDouble("totaL_DAYS");
                                final String veh_bags = vehicleModel.getString("veh_bags");
                                final String doors = vehicleModel.getString("doors");
                                final String fuel_Name = vehicleModel.getString("fuel_Name");
                                final String transmission_Name = vehicleModel.getString("transmission_Name");
                                final String veh_Name = vehicleModel.getString("veh_Name");
                                final String year_Name = vehicleModel.getString("year_Name");
                                final String lateR_PRICE = vehicleModel.getString("lateR_PRICE");
                                final int lateR_RATE_ID = vehicleModel.getInt("lateR_RATE_ID");
                                final String lateR_RATE_NAME = vehicleModel.getString("lateR_RATE_NAME");
                                final double daily_Price = vehicleModel.getDouble("daily_Price");
                                final String available_QTY = vehicleModel.getString("available_QTY");
                                final String vehDescription = vehicleModel.getString("vehDescription");
                                final int isUnlimited = vehicleModel.getInt("isUnlimited");
                                final String vehicle_Make_Model_Name = vehicleModel.getString("vehicle_Make_Model_Name");
                                final int isDepositMandatory = vehicleModel.getInt("isDepositMandatory");
                                final double securityDeposit = vehicleModel.getDouble("securityDeposit");
                                final double hourlyMilesAllowed = vehicleModel.getDouble("hourlyMilesAllowed");
                                final double halfDayMilesAllowed = vehicleModel.getDouble("halfDayMilesAllowed");
                                final double dailyMilesAllowed = vehicleModel.getDouble("dailyMilesAllowed");
                                final double weeklyMilesAllowed = vehicleModel.getDouble("weeklyMilesAllowed");
                                final double monthlyMilesAllowed = vehicleModel.getDouble("monthlyMilesAllowed");
                                final double totalMilesAllowed = vehicleModel.getDouble("totalMilesAllowed");
                                final int lockKey = vehicleModel.getInt("lockKey");

                                String url1 = serverpath + img_Path.substring(2);
                                imageLoader.displayImage(url1, CarImage);

                                txtDays.setText(String.valueOf(totaL_DAYS));
                                txt_vehicletype.setText(vehiclE_TYPE_NAME);
                                txt_vehName.setText(vehiclE_NAME);

                                String Miles;
                                if(isUnlimited==1)
                                {
                                    //  String Miles=(String.valueOf(totalMilesAllowed));
                                    Miles = ("Unlimited");
                                }
                                else {
                                   // Miles = ((String.format(Locale.US, "%.0f", totalMilesAllowed)));
                                    Miles = Helper.getDistance(String.valueOf(totalMilesAllowed));
                                }
                                txtMileage.setText(Miles);

                                //getsummaryOfCharges
                                final JSONArray getsummaryOfCharges = resultSet.getJSONArray("summaryOfCharges");
                                int len = getsummaryOfCharges.length();

                                for (int j = 0; j < len; j++)
                                {
                                    final JSONObject test = (JSONObject) getsummaryOfCharges.get(j);

                                    final  int sortId = test.getInt("sortId");
                                    final  double chargeAmount=test.getDouble("chargeAmount");
                                    String chargeCode = "";

                                    if(test.has("chargeCode"))
                                    {
                                        chargeCode = test.getString("chargeCode");
                                    }

                                    final  String chargeName = test.getString("chargeName");

                                    JSONObject summaryOfChargesObj = new JSONObject();
                                    summaryOfChargesObj.put("sortId",sortId);
                                    summaryOfChargesObj.put("chargeCode",chargeCode);
                                    summaryOfChargesObj.put("chargeName",chargeName);
                                    summaryOfChargesObj.put("chargeAmount",chargeAmount);

                                    SummaryOfCharges.put(summaryOfChargesObj);

                                    if(chargeName.equals("Estimated Total"))
                                    {
                                        txtTotalAmount.setText((String.format(Locale.US,"%.2f",chargeAmount)));
                                        System.out.println(txtTotalAmount.getText().toString());
                                    }
                                }

                                //miscellaneousModel
                                final JSONArray getmiscellaneousModel = resultSet.getJSONArray("miscellaneousModel");
                                final RelativeLayout rlmiscellaneousModel = getActivity().findViewById(R.id.rl_miscellaneousModel);

                                int len2;
                                len2 = getmiscellaneousModel.length();

                                for (int j = 0; j < len2; j++)
                                {
                                    final JSONObject test = (JSONObject) getmiscellaneousModel.get(j);
                                    final  int miscId = test.getInt("miscId");
                                    final String miscName = test.getString("miscName");
                                    final String miscDesc=test.getString("miscDesc");
                                    final double basicValue=test.getDouble("basicValue");
                                    final int quantity=test.getInt("quantity");
                                    final double miscAmount=test.getDouble("miscAmount");
                                    final int taxableAmount=test.getInt("taxableAmount");
                                    int isOptional=test.getInt("isOptional");

                                    final JSONObject miscObj = new JSONObject();
                                    miscObj.put("miscId",miscId);
                                    miscObj.put("miscName",miscName);
                                    miscObj.put("miscDesc",miscDesc);
                                    miscObj.put("basicValue",basicValue);
                                    miscObj.put("quantity",quantity);
                                    miscObj.put("miscAmount",miscAmount);
                                    miscObj.put("taxableAmount",taxableAmount);
                                    miscObj.put("isOptional",isOptional);

                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                    lp.setMargins(0, 0, 0, 0);

                                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.miscellaneous_tax_details, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                    linearLayout.setId(200 + j);
                                    linearLayout.setLayoutParams(lp);

                                    final TextView txt_miscName,txt_miscDesc,txt_miscAmount;
                                    final ToggleButton s2=linearLayout.findViewById(R.id.switch1);

                                    txt_miscName= (TextView)linearLayout.findViewById(R.id.lbl_miscName);
                                    txt_miscDesc=(TextView)linearLayout.findViewById(R.id.lbl_miscDesc);
                                    txt_miscAmount=(TextView)linearLayout.findViewById(R.id.lbl_miscAmount);
                                    txt_miscName.setText(miscName);
                                    txt_miscDesc.setText(miscDesc);

                                    String strmiscAmount=((String.format(Locale.US,"%.2f",miscAmount)));
                                    txt_miscAmount.setText("USD$ "+strmiscAmount);

                                    if (isOptional == 1)
                                    {
                                        s2.setChecked(true);
                                        s2.setEnabled(false);
                                        s2.setBackgroundResource(R.drawable.toggle_selector_red);

                                        miscObj.put("miscId", miscId);
                                        miscObj.put("miscAmount", miscAmount);
                                        MiscList.put(miscObj);
                                    } else {
                                        s2.setChecked(false);
                                    }

                                    if(IsSelected)
                                    {
                                        SelectedMiscList = new JSONArray(BookingBundle.getString("MiscList"));
                                        System.out.println("SelectedMiscList"+SelectedMiscList);
                                        int count = SelectedMiscList.length();

                                        for (int i = 0; i < count; i++)
                                        {
                                            JSONObject obj = SelectedMiscList.getJSONObject(i);

                                            if (obj.getInt("miscId")==miscId)
                                            {
                                                if (isOptional == 1)
                                                {
                                                    s2.setChecked(true);
                                                    s2.setEnabled(false);
                                                    s2.setBackgroundResource(R.drawable.toggle_selector_red);
                                                }
                                                else {
                                                    s2.setChecked(true);
                                                    s2.setBackgroundResource(R.drawable.toggle_selector_green);

                                                    String totalAmount = txtTotalAmount.getText().toString();
                                                    TotalValue =Double.parseDouble(totalAmount)+(miscAmount);
                                                    txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));
                                                }
                                            }
                                        }
                                    }

                                    s2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                                    {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
                                        {
                                            try {
                                                if (isChecked)
                                                {
                                                    if (isOptional == 1)
                                                    {
                                                        s2.setChecked(true);
                                                        s2.setEnabled(false);
                                                    }
                                                    else {
                                                        String totalAmount = txtTotalAmount.getText().toString();
                                                        TotalValue =Double.parseDouble(totalAmount)+(miscAmount);
                                                        txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));

                                                        for (int i=0;i<MiscList.length();i++)
                                                        {
                                                            JSONObject temp = (JSONObject) MiscList.get(i);
                                                            if(temp.get("miscId").equals(miscId))
                                                            {
                                                                MiscList.remove(i);
                                                                break;
                                                            }
                                                        }
                                                        miscObj.put("miscId",miscId);
                                                        miscObj.put("miscAmount",miscAmount);
                                                        MiscList.put(miscObj);
                                                        System.out.println(MiscList);
                                                    }
                                                }
                                                else
                                                {
                                                    if (isOptional == 1)
                                                    {
                                                        s2.setChecked(true);
                                                        s2.setEnabled(false);
                                                    }
                                                    else {
                                                        String totalAmount = txtTotalAmount.getText().toString();
                                                        TotalValue = (Double.parseDouble(totalAmount) - miscAmount) <= 0 ? 0 : Double.parseDouble(totalAmount) - miscAmount;//Double.parseDouble(totalAmount)-Double.parseDouble(TotalequipmentAmount);
                                                        txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));

                                                        for (int i = 0; i < MiscList.length(); i++)
                                                        {
                                                            JSONObject temp = (JSONObject) MiscList.get(i);
                                                            if (temp.get("miscId").equals(miscId))
                                                            {
                                                                MiscList.remove(i);
                                                                break;
                                                            }
                                                        }
                                                        System.out.println(MiscList);
                                                    }
                                                }
                                            }
                                            catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    rlmiscellaneousModel.addView(linearLayout);
                                }

                                //equipmentModel
                                final JSONArray getEquipmentdetails = resultSet.getJSONArray("equipmentModel");
                                final RelativeLayout rlEquipmentDetails = getActivity().findViewById(R.id.rl_EquipmentExtra);

                                int len1;
                                len1 = getEquipmentdetails.length();

                                for (int j = 0; j < len1; j++)
                                {
                                    final JSONObject test = (JSONObject) getEquipmentdetails.get(j);
                                    final  int equipmentTypeId = test.getInt("equipmentTypeId");
                                    final String equipmentImagePath = test.getString("equipmentImagePath");
                                    final String equipmentName=test.getString("equipmentName");
                                    final String equipmentDesc=test.getString("equipmentDesc");
                                    final int equipmentQty=test.getInt("equipmentQty");
                                    final int taxValue=test.getInt("taxValue");
                                    final int taxAmount=test.getInt("taxAmount");
                                    final double equipmentAmount=test.getDouble("equipmentAmount");

                                    final JSONObject equipmentObj = new JSONObject();
                                    equipmentObj.put("equipmentTypeId", equipmentTypeId);
                                    equipmentObj.put("equipmentImagePath", equipmentImagePath);
                                    equipmentObj.put("equipmentName", equipmentName);
                                    equipmentObj.put("equipmentDesc", equipmentDesc);
                                    equipmentObj.put("equipmentQty", equipmentQty);
                                    equipmentObj.put("taxValue", taxValue);
                                    equipmentObj.put("taxAmount", taxAmount);
                                    equipmentObj.put("equipmentAmount", equipmentAmount+"");

                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                    lp.setMargins(0, 0, 0, 0);

                                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    LinearLayout linearLayout2 = (LinearLayout) inflater.inflate(R.layout.tax_details_list, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                    linearLayout2.setId(200 + j);
                                    linearLayout2.setLayoutParams(lp);

                                    final TextView txt_equipmentName,txt_equipmentDescc,txt_equipmentAmount;

                                    txt_equipmentName= (TextView) linearLayout2.findViewById(R.id.lbl_InsuranceName);
                                    txt_equipmentDescc=(TextView)linearLayout2.findViewById(R.id.lbl_insuranceDesc);
                                    txt_equipmentAmount=(TextView)linearLayout2.findViewById(R.id.lbl_totalCharge);
                                    txt_equipmentName.setText(equipmentName);
                                    txt_equipmentDescc.setText(equipmentDesc);

                                   /* String strequipmentAmount=((String.format(Locale.US,"%.2f",equipmentAmount)));
                                    txt_equipmentAmount.setText("USD$ "+strequipmentAmount);*/

                                    txt_equipmentAmount.setText( Helper.getAmtount(equipmentAmount, true));

                                    final ToggleButton s1=linearLayout2.findViewById(R.id.switch2);

                                    if(IsSelected)
                                    {
                                        SelectedEquipmentList = new JSONArray(BookingBundle.getString("EquipmentList"));
                                        System.out.println("SelectedEquipmentList"+SelectedEquipmentList);

                                        int count = SelectedEquipmentList.length();

                                        for (int i = 0; i < count; i++)
                                        {
                                            JSONObject obj = SelectedEquipmentList.getJSONObject(i);

                                            if (obj.getInt("equipmentTypeId")==equipmentTypeId)
                                            {
                                                s1.setChecked(true);

                                                String totalAmount= txtTotalAmount.getText().toString();
                                                TotalValue=Double.parseDouble(totalAmount)+equipmentAmount;
                                                System.out.println(TotalValue);
                                                txtTotalAmount.setText((String.format(Locale.US,"%.2f",TotalValue)));

                                                equipmentObj.put("equipmentQty", 1);
                                                EquipmentList.put(equipmentObj);
                                                System.out.println(EquipmentList);
                                            }
                                        }
                                    }

                                    s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                                    {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
                                        {
                                            try {
                                                if (isChecked)
                                                {
                                                    String totalAmount= txtTotalAmount.getText().toString();
                                                    TotalValue=Double.parseDouble(totalAmount)+equipmentAmount;
                                                    txtTotalAmount.setText((String.format(Locale.US,"%.2f",TotalValue)));

                                                    equipmentObj.put("equipmentQty", 1);
                                                    EquipmentList.put(equipmentObj);
                                                    System.out.println(EquipmentList);
                                                }
                                                else
                                                {
                                                    //  equipmentObj.put("equipmentQty", 0);
                                                    int count=EquipmentList.length();
                                                    for (int i=0;i<count;i++)
                                                    {
                                                        JSONObject obj=EquipmentList.getJSONObject(i);

                                                        if(obj.getInt("equipmentTypeId")==equipmentTypeId)
                                                        {
                                                            String totalAmount= txtTotalAmount.getText().toString();
                                                            TotalValue=(Double.parseDouble(totalAmount)-equipmentAmount)<=0?0:Double.parseDouble(totalAmount)-equipmentAmount;//Double.parseDouble(totalAmount)-equipmentAmount;
                                                            txtTotalAmount.setText((String.format(Locale.US,"%.2f",TotalValue)));

                                                            EquipmentList.remove(i);
                                                            i--;
                                                            count--;
                                                            System.out.println(EquipmentList);
                                                        }
                                                    }
                                                }
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    rlEquipmentDetails.addView(linearLayout2);
                                }

                                //insuranceModel
                                getInsuranceDEtails = resultSet.getJSONArray("insuranceModel");
                              //  final RelativeLayout rlInsuranceDetails = getActivity().findViewById(R.id.rlInsuranceCover);




                                //Helper.VISIBLE
                                    int len4;
                                    len4 = getInsuranceDEtails.length();
                                    for (int j = 0; j < len4; j++) {
                                        final JSONObject test = (JSONObject) getInsuranceDEtails.get(j);

                                        final int transId = test.getInt("transId");
                                        final int deductableId = test.getInt("deductableId");
                                        final String insuranceName = test.getString("insuranceName");
                                        final String insuranceDesc = test.getString("insuranceDesc");
                                        final int isSelected = test.getInt("isSelected");
                                        final double excessCharge = test.getDouble("excessCharge");
                                        final double perDayCharge = test.getDouble("perDayCharge");
                                        final double totalCharge = test.getDouble("totalCharge");

                                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                        lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                        lp.setMargins(0, 10, 0, 0);

                                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        final LinearLayout l1 = (LinearLayout) inflater.inflate(R.layout.tax_details_list, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                        l1.setId(200 + j);
                                        l1.setLayoutParams(lp);

                                        final TextView txt_insuranceName, txt_insuranceDesc, txt_totalCharge;
                                        txt_insuranceName = (TextView) l1.findViewById(R.id.lbl_InsuranceName);
                                        txt_insuranceDesc = (TextView) l1.findViewById(R.id.lbl_insuranceDesc);
                                        txt_totalCharge = (TextView) l1.findViewById(R.id.lbl_totalCharge);

                                        txt_insuranceName.setText(insuranceName);
                                        txt_insuranceDesc.setText(insuranceDesc);
                                        //txt_totalCharge.setText(((String.format(Locale.US, "%.2f", totalCharge))));
                                        txt_totalCharge.setText( Helper.getAmtount(totalCharge, true));
                                        final ToggleButton s3 = l1.findViewById(R.id.switch2);

                                        if (isSelected == 1) {
                                            s3.setChecked(true);

                                       /*     String totalAmount = txtTotalAmount.getText().toString();
                                            TotalValue = Double.parseDouble(totalAmount) + totalCharge;
                                            txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));*/

                                            BookingBundle.putInt("DeductibleCoverId", deductableId);
                                            BookingBundle.putDouble("DeductibleCharge", totalCharge);
                                        } /*else {
                                            s3.setChecked(false);
                                            BookingBundle.putInt("DeductibleCoverId", 0);
                                            BookingBundle.putDouble("DeductibleCharge", 0);
                                        }*/

                                        if (IsSelected) {
                                            if (isSelected == 1) {
                                                s3.setChecked(false);
                                                String totalAmount = txtTotalAmount.getText().toString();
                                                TotalValue = (Double.parseDouble(totalAmount) - totalCharge) <= 0 ? 0 : Double.parseDouble(totalAmount) - totalCharge;//Double.parseDouble(totalAmount)-totalCharge;
                                                txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));
                                                BookingBundle.putInt("DeductibleCoverId", 0);
                                                BookingBundle.putDouble("DeductibleCharge", 0);
                                            }

                                            int DeductibleCoverId = getArguments().getInt("DeductibleCover_Id");
                                            double DeductibleCharge = getArguments().getDouble("Deductible_Charge");

                                            System.out.println("DeductibleCoverId:=" + DeductibleCoverId);
                                            System.out.println("deductableId:=" + deductableId);

                                            if (DeductibleCoverId == deductableId) {
                                                System.out.println(DeductibleCoverId == deductableId);

                                                s3.setChecked(true);
                                                String totalAmount = txtTotalAmount.getText().toString();
                                                TotalValue = Double.parseDouble(totalAmount) + DeductibleCharge;
                                                txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));

                                                BookingBundle.putInt("DeductibleCoverId", deductableId);
                                                BookingBundle.putDouble("DeductibleCharge", totalCharge);
                                            }
                                        }

                                        s3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                                try {
                                                    if (isChecked) {
                                                        System.out.println(transId + "-" + isChecked);

                                                        String totalAmount = txtTotalAmount.getText().toString();
                                                        TotalValue = Double.parseDouble(totalAmount) + totalCharge;
                                                        txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));

                                                        BookingBundle.putInt("DeductibleCoverId", deductableId);
                                                        BookingBundle.putDouble("DeductibleCharge", totalCharge);

                                                        int insuranceCount = getInsuranceDEtails.length();

                                                        for (int j = 0; j < insuranceCount; j++) {
                                                            int transIdTemp = ((JSONObject) getInsuranceDEtails.get(j)).getInt("transId");

                                                            if (transId != transIdTemp) {
                                                                LinearLayout llTemp = (LinearLayout) rlInsuranceDetails.getChildAt(j);
                                                                ToggleButton tbTemp = llTemp.findViewById(R.id.switch2);

                                                                if (tbTemp.isChecked()) {
                                                                    tbTemp.setChecked(false);
                                                                    BookingBundle.putInt("DeductibleCoverId", 0);
                                                                    BookingBundle.putDouble("DeductibleCharge", 0);
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        String totalAmount = txtTotalAmount.getText().toString();
                                                        TotalValue = (Double.parseDouble(totalAmount) - totalCharge) <= 0 ? 0 : Double.parseDouble(totalAmount) - totalCharge;//Double.parseDouble(totalAmount)-totalCharge;
                                                        txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));
                                                        BookingBundle.putInt("DeductibleCoverId", 0);
                                                        BookingBundle.putDouble("DeductibleCharge", 0);

                                                    }


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });




                                        rlInsuranceDetails.addView(l1);
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
        public void onError(String error)
        {
            System.out.println("Error-" + error);
        }
    };

    public void declineInsurance(){
        if (Helper.VISIBLE){
            int datalist = 3;
            rlInsuranceCover2.removeAllViews();
            for (int j = 0; j < datalist; j++){

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                lp.setMargins(0, 10, 0, 0);

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.decline_insurance_detail, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                linearLayout.setId(200 + j);
                linearLayout.setLayoutParams(lp);

                TextView lbl_chargeAmount, lblchargeName;
                lbl_chargeAmount = (TextView) linearLayout.findViewById(R.id.value);
                lblchargeName = (TextView) linearLayout.findViewById(R.id.name);

                if (j == 0) {
                    lblchargeName.setText("Insurance Company Name");
                    lbl_chargeAmount.setText(Helper.insuranceModel.insurance_Cmp_Name);
                } if (j==1){
                    lblchargeName.setText("Insurance Policy No.");
                    lbl_chargeAmount.setText(Helper.insuranceModel.policy_No);
                }if (j==2){
                    lblchargeName.setText("Insurance Expiry Date ");
                    lbl_chargeAmount.setText(Helper.insuranceModel.expiryDate);
                }

                rlInsuranceCover2.addView(linearLayout);

            }
        }
    }

}


    /* String dateFormatPickupDate = (BookingBundle.getString("PickupDate"));
                    String strPickUpTime1 = (BookingBundle.getString("PickupTime"));
                    String PickupDateTime = dateFormatPickupDate + "T" + strPickUpTime1;
                    bodyParam.accumulate("PickupDate", PickupDateTime);
                    String dateFormatReturnDate = (BookingBundle.getString("ReturnDate"));
                    String strReturnTime1 = (BookingBundle.getString("ReturnTime"));
                    String ReturnDateTime = dateFormatReturnDate + "T" + strReturnTime1;
                    bodyParam.accumulate("ReturnDate", ReturnDateTime);*/

              /*  backarrow.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Bundle Booking = new Bundle();
                    BookingBundle.putInt("BookingStep", 2);
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    NavHostFragment.findNavController(Fragment_Select_addition_Options.this)
                            .navigate(R.id.action_Select_addtional_options_to_Select_location1, Booking);
                }
            });*/
