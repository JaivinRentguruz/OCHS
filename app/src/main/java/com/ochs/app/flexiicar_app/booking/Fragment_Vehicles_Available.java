package com.ochs.app.flexiicar_app.booking;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.BOOKING;

public class Fragment_Vehicles_Available extends Fragment
{
    LinearLayout filter_icon;
    ImageView Back;
    public static Context context;
    public String id = "";
    Bundle BookingBundle;
    Handler handler = new Handler();
    ImageLoader imageLoader;
    String serverpath="";
    Bundle returnLocationBundle, locationBundle;
    Boolean locationType, initialSelect;
    Bundle VehicleBundle;
    EditText edt_searchVehicleName;
    TextView lblDiscard;
    Double Totalprice;
    JSONArray VehicleList = new JSONArray();
    Boolean isFilter;
    RelativeLayout rlVehicleAvilablelist;

    public static void initImageLoader(Context context)
    {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.MAX_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(100 * 1024 * 1024);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs();
        ImageLoader.getInstance().init(config.build());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_vehicles_available, container, false);
    }

    @Override
    public void setInitialSavedState(@Nullable @org.jetbrains.annotations.Nullable SavedState state) {
        super.setInitialSavedState(state);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        try {
            initImageLoader(getActivity());
            imageLoader = ImageLoader.getInstance();

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            id = sp.getString(getString(R.string.id), "");
            serverpath = sp.getString("serverPath", "");

            lblDiscard = view.findViewById(R.id.lblDiscard);

            returnLocationBundle = getArguments().getBundle("returnLocation");
            locationBundle = getArguments().getBundle("location");

            locationType = getArguments().getBoolean("locationType");
            initialSelect = getArguments().getBoolean("initialSelect");

            isFilter=getArguments().getBoolean("isFilter");
            System.out.println(isFilter);

            Back = view.findViewById(R.id.Back);
            edt_searchVehicleName = view.findViewById(R.id.edt_searchVehicleName);
            BookingBundle = getArguments().getBundle("BookingBundle");

            lblDiscard.setOnClickListener(new View.OnClickListener()
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
                                    NavHostFragment.findNavController(Fragment_Vehicles_Available.this)
                                            .navigate(R.id.action_Vehicles_Available_to_Search_activity);
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

            filter_icon = view.findViewById(R.id.filter_icon);
            filter_icon.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(isFilter)
                    {
                        Bundle Booking = new Bundle();
                        Booking.putBundle("BookingBundle", BookingBundle);
                        Booking.putBundle("returnLocation", returnLocationBundle);
                        Booking.putBundle("location", locationBundle);
                        Booking.putBoolean("locationType", locationType);
                        Booking.putBoolean("initialSelect", initialSelect);
                        Booking.putBoolean("isFilter", true);
                        System.out.println(Booking);
                        NavHostFragment.findNavController(Fragment_Vehicles_Available.this)
                                .navigate(R.id.action_Vehicles_Available_to_Vehicles_FilterList, Booking);
                    }
                    else {
                        Bundle Booking = new Bundle();
                        Booking.putBundle("BookingBundle", BookingBundle);
                        Booking.putBundle("returnLocation", returnLocationBundle);
                        Booking.putBundle("location", locationBundle);
                        Booking.putBoolean("locationType", locationType);
                        Booking.putBoolean("initialSelect", initialSelect);
                        Booking.putBoolean("isFilter", false);
                        System.out.println(Booking);
                        NavHostFragment.findNavController(Fragment_Vehicles_Available.this)
                                .navigate(R.id.action_Vehicles_Available_to_Vehicles_FilterList, Booking);
                    }
                }
            });

            Back.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle Booking = new Bundle();
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    NavHostFragment.findNavController(Fragment_Vehicles_Available.this)
                            .navigate(R.id.action_Vehicles_Available_to_Selected_location, Booking);
                }
            });

            Fragment_Vehicles_Available.context = getActivity();
            AndroidNetworking.initialize(getActivity());

            if (!id.equals(""))
            {
                JSONObject bodyParam = new JSONObject();
                try {
                    bodyParam.accumulate("ForTransId", BookingBundle.getInt("ForTransId"));
                    bodyParam.accumulate("PickupLocId", BookingBundle.getInt("PickupLocId"));
                    bodyParam.accumulate("ReturnLocId", BookingBundle.getInt("ReturnLocId"));
                    bodyParam.accumulate("CustomerId", BookingBundle.getInt("CustomerId"));
                    bodyParam.accumulate("VehicleTypeId", BookingBundle.getInt("VehicleTypeId"));
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
                ApiService ApiService = new ApiService(getVehicleList, RequestType.POST,
                        BOOKING, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);

                rlVehicleAvilablelist = getActivity().findViewById(R.id.relative_vehicle_available);
                rlVehicleAvilablelist.removeAllViews();

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final RelativeLayout reservationlayout = (RelativeLayout) inflater.inflate(R.layout.dialog_progress, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                rlVehicleAvilablelist.addView(reservationlayout);

            }
            else {
                JSONObject bodyParam = new JSONObject();
                try {
                    bodyParam.accumulate("ForTransId", BookingBundle.getInt("ForTransId"));
                    bodyParam.accumulate("PickupLocId", BookingBundle.getInt("PickupLocId"));
                    bodyParam.accumulate("ReturnLocId", BookingBundle.getInt("ReturnLocId"));
                    bodyParam.accumulate("VehicleTypeId", BookingBundle.getInt("VehicleTypeId"));
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
                ApiService ApiService = new ApiService(getVehicleList, RequestType.POST,
                        BOOKING, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);

                rlVehicleAvilablelist = getActivity().findViewById(R.id.relative_vehicle_available);
                rlVehicleAvilablelist.removeAllViews();

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final RelativeLayout reservationlayout = (RelativeLayout) inflater.inflate(R.layout.dialog_progress, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                rlVehicleAvilablelist.addView(reservationlayout);
            }

            edt_searchVehicleName.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {

                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                    try {
                        final RelativeLayout rlVehicleAvilable = getActivity().findViewById(R.id.relative_vehicle_available);
                        int len;
                        len = VehicleList.length();
                        rlVehicleAvilable.removeAllViews();

                        for (i = 0; i < len; i++)
                        {
                            final JSONObject test = (JSONObject) VehicleList.get(i);
                            final String vehiclE_NAME = test.getString("vehiclE_NAME");

                            if (vehiclE_NAME.contains(charSequence))
                            {
                                final String img_Path = test.getString("img_Path");
                                final int vehiclE_ID = test.getInt("vehiclE_ID");
                                final int vehiclE_TYPE_ID = test.getInt("vehiclE_TYPE_ID");
                                final String vehiclE_TYPE_NAME = test.getString("vehiclE_TYPE_NAME");
                                final String vehiclE_SEAT_NO = test.getString("vehiclE_SEAT_NO");
                                final String transmission = test.getString("transmission");
                                final String v_CURR_ODOM = test.getString("v_CURR_ODOM");
                                final String vehiclE_OPTIONS_IDS = test.getString("vehiclE_OPTIONS_IDS");
                                final String rate_ID = test.getString("rate_ID");
                                final String rate_Name = test.getString("rate_Name");
                                final int package_ID = test.getInt("package_ID");
                                final double totaL_DAYS = test.getDouble("totaL_DAYS");
                                final String veh_bags = test.getString("veh_bags");
                                final String doors = test.getString("doors");
                                final String fuel_Name = test.getString("fuel_Name");
                                final String transmission_Name = test.getString("transmission_Name");
                                final String veh_Name = test.getString("veh_Name");
                                final String year_Name = test.getString("year_Name");
                                final String lateR_PRICE = test.getString("lateR_PRICE");
                                final int lateR_RATE_ID = test.getInt("lateR_RATE_ID");
                                final String lateR_RATE_NAME = test.getString("lateR_RATE_NAME");
                                final double daily_Price = test.getDouble("daily_Price");
                                final String available_QTY = test.getString("available_QTY");
                                final String vehDescription = test.getString("vehDescription");
                                final String vehicle_Make_Model_Name = test.getString("vehicle_Make_Model_Name");
                                final int isDepositMandatory = test.getInt("isDepositMandatory");
                                final double securityDeposit = test.getDouble("securityDeposit");
                                final double hourlyMilesAllowed = test.getDouble("hourlyMilesAllowed");
                                final double halfDayMilesAllowed = test.getDouble("halfDayMilesAllowed");
                                final double dailyMilesAllowed = test.getDouble("dailyMilesAllowed");
                                final double weeklyMilesAllowed = test.getDouble("weeklyMilesAllowed");
                                final double monthlyMilesAllowed = test.getDouble("monthlyMilesAllowed");
                                final double totalMilesAllowed = test.getDouble("totalMilesAllowed");
                                final int lockKey = test.getInt("lockKey");

                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                lp.addRule(RelativeLayout.BELOW, (200 + i - 1));
                                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                lp.setMargins(0, 10, 0, 0);

                                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final LinearLayout vehiclelayoutlist = (LinearLayout) inflater.inflate(R.layout.vehicle_available_list, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                vehiclelayoutlist.setId(200 + i);
                                vehiclelayoutlist.setLayoutParams(lp);

                                final ImageView imageview = (ImageView) vehiclelayoutlist.findViewById(R.id.car_imgview);

                                String url1 = serverpath+img_Path.substring(2);
                                imageLoader.displayImage(url1, imageview);

                                TextView lbl_vehName, lbl_availableqty, lbl_VehTypeName,
                                        lbl_transmission_Name, lbl_seats, lbl_doors, lbl_bags, lbl_perDayAmount, lbl_DepositeAmount,lblfuel_Name,lbl_Totalprice;

                                lbl_vehName = vehiclelayoutlist.findViewById(R.id.lbl_vehName);
                                lbl_availableqty = vehiclelayoutlist.findViewById(R.id.lbl_availableqty);
                                lbl_VehTypeName = vehiclelayoutlist.findViewById(R.id.lbl_VehTypeName);
                                lbl_transmission_Name = vehiclelayoutlist.findViewById(R.id.lbl_transmission_Name);
                                lbl_seats = vehiclelayoutlist.findViewById(R.id.lbl_seats);
                                lbl_doors = vehiclelayoutlist.findViewById(R.id.lbl_doors);
                                lbl_bags = vehiclelayoutlist.findViewById(R.id.lbl_bags);
                                lbl_perDayAmount = vehiclelayoutlist.findViewById(R.id.lbl_perDayAmount);
                                lbl_DepositeAmount = vehiclelayoutlist.findViewById(R.id.lbl_DepositeAmount);
                              //  lbl_Totalprice = vehiclelayoutlist.findViewById(R.id.lbl_Totalprice);
                                lblfuel_Name= vehiclelayoutlist.findViewById(R.id.lblfuel_Name);

                                lbl_vehName.setText(vehiclE_NAME);
                                lbl_availableqty.setText(available_QTY);
                                lbl_VehTypeName.setText(vehiclE_TYPE_NAME);
                                lbl_seats.setText(vehiclE_SEAT_NO+ " Seats");
                                lbl_doors.setText(doors + " Doors");
                                lbl_bags.setText(veh_bags + " Bags");
                                lbl_transmission_Name.setText(transmission_Name);
                                //lblfuel_Name.setText(fuel_Name);

                                String strdaily_Price=(String.format(Locale.US,"%.2f",daily_Price));
                                String strsecurityDeposit=(String.format(Locale.US,"%.2f",securityDeposit));

                                lbl_perDayAmount.setText("$"+strdaily_Price);
                                lbl_DepositeAmount.setText("$"+strsecurityDeposit);

                                vehiclelayoutlist.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View arg0)
                                    {
                                        int len = rlVehicleAvilable.getChildCount();

                                        for (int m = 0; m < len; m++)
                                        {
                                            LinearLayout linearLayout = (LinearLayout) rlVehicleAvilable.getChildAt(m);
                                            linearLayout.setBackground(getResources().getDrawable(R.drawable.top_curve_light_gray));

                                        }
                                        vehiclelayoutlist.setBackground(getResources().getDrawable(R.drawable.curve_box_dark_gray));

                                        Bundle VehicleBundle = new Bundle();
                                        VehicleBundle.putString("img_Path",img_Path.substring(2) );
                                        VehicleBundle.putInt("vehiclE_ID", vehiclE_ID);
                                        VehicleBundle.putString("vehiclE_TYPE_NAME", vehiclE_TYPE_NAME);
                                        VehicleBundle.putInt("vehiclE_TYPE_ID", vehiclE_TYPE_ID);
                                        VehicleBundle.putDouble("price", securityDeposit);
                                        VehicleBundle.putString("vehiclE_NAME", vehiclE_NAME);
                                        VehicleBundle.putString("vehiclE_SEAT_NO", vehiclE_SEAT_NO);
                                        VehicleBundle.putString("transmission", transmission);
                                        VehicleBundle.putString("v_CURR_ODOM", v_CURR_ODOM);
                                        VehicleBundle.putString("vehiclE_OPTIONS_IDS", vehiclE_OPTIONS_IDS);
                                        VehicleBundle.putString("rate_ID", rate_ID);
                                        VehicleBundle.putString("rate_Name", rate_Name);
                                        VehicleBundle.putDouble("totaL_DAYS", totaL_DAYS);
                                        VehicleBundle.putString("veh_bags", veh_bags);
                                        VehicleBundle.putString("doors", doors);
                                        VehicleBundle.putString("fuel_Name", fuel_Name);
                                        VehicleBundle.putString("transmission_Name", transmission_Name);
                                        VehicleBundle.putString("veh_Name", veh_Name);
                                        VehicleBundle.putString("year_Name", year_Name);
                                        VehicleBundle.putString("lateR_PRICE", lateR_PRICE);
                                        VehicleBundle.putInt("lateR_RATE_ID", lateR_RATE_ID);
                                        VehicleBundle.putString("lateR_RATE_NAME", lateR_RATE_NAME);
                                        VehicleBundle.putDouble("daily_Price", daily_Price);
                                        VehicleBundle.putString("available_QTY", available_QTY);
                                        VehicleBundle.putString("vehDescription", vehDescription);
                                        VehicleBundle.putString("vehicle_Make_Model_Name", vehicle_Make_Model_Name);
                                        VehicleBundle.putDouble("totalMilesAllowed", totalMilesAllowed);

                                        BookingBundle.putInt("BookingStep", 3);
                                        BookingBundle.putInt("VehicleID", vehiclE_ID);
                                        BookingBundle.putInt("vehiclE_TYPE_ID",vehiclE_TYPE_ID);

                                        Bundle Booking = new Bundle();
                                        Booking.putBundle("BookingBundle", BookingBundle);
                                        Booking.putBundle("VehicleBundle", VehicleBundle);
                                        Booking.putBundle("returnLocation", returnLocationBundle);
                                        Booking.putBundle("location", locationBundle);
                                        Booking.putBoolean("locationType", locationType);
                                        Booking.putBoolean("initialSelect", initialSelect);
                                        Booking.putString("DeliveryAndPickupModel", "");
                                        System.out.println(VehicleBundle);
                                        System.out.println(BookingBundle);
                                        NavHostFragment.findNavController(Fragment_Vehicles_Available.this)
                                                .navigate(R.id.action_Vehicles_Available_to_Select_addtional_options, Booking);
                                    }
                                });
                                rlVehicleAvilable.addView(vehiclelayoutlist);
                            }
                        }

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                @Override
                public void afterTextChanged(Editable editable)
                {
                }
            });

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    OnResponseListener getVehicleList = new OnResponseListener()
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
                            VehicleList = resultSet.getJSONArray("vehicleModel");

                            final RelativeLayout rlVehicleAvilable = getActivity().findViewById(R.id.relative_vehicle_available);
                            int len;
                            len = VehicleList.length();
                            rlVehicleAvilable.removeAllViews();

                            for (int j = 0; j < len; j++)
                            {
                                final JSONObject test = (JSONObject)VehicleList.get(j);
                                final String vehiclE_NAME = test.getString("vehiclE_NAME");
                                final String img_Path = test.getString("img_Path");
                                final int vehiclE_ID = test.getInt("vehiclE_ID");
                                final int vehiclE_TYPE_ID = test.getInt("vehiclE_TYPE_ID");
                                // final Double price = test.getDouble("price");
                                final String vehiclE_TYPE_NAME = test.getString("vehiclE_TYPE_NAME");
                                final String vehiclE_SEAT_NO = test.getString("vehiclE_SEAT_NO");
                                final String transmission = test.getString("transmission");
                                final String v_CURR_ODOM = test.getString("v_CURR_ODOM");
                                final String vehiclE_OPTIONS_IDS = test.getString("vehiclE_OPTIONS_IDS");
                                final String rate_ID = test.getString("rate_ID");
                                final String rate_Name = test.getString("rate_Name");
                                final int package_ID = test.getInt("package_ID");
                                final double totaL_DAYS = test.getDouble("totaL_DAYS");
                                final String veh_bags = test.getString("veh_bags");
                                final String doors = test.getString("doors");
                                final String fuel_Name = test.getString("fuel_Name");
                                final String transmission_Name = test.getString("transmission_Name");
                                final String veh_Name = test.getString("veh_Name");
                                final String year_Name = test.getString("year_Name");
                                final String lateR_PRICE = test.getString("lateR_PRICE");
                                final int lateR_RATE_ID = test.getInt("lateR_RATE_ID");
                                final String lateR_RATE_NAME = test.getString("lateR_RATE_NAME");
                                final double daily_Price = test.getDouble("daily_Price");
                                final String available_QTY = test.getString("available_QTY");
                                final String vehDescription = test.getString("vehDescription");
                                final String vehicle_Make_Model_Name = test.getString("vehicle_Make_Model_Name");
                                final int isDepositMandatory = test.getInt("isDepositMandatory");
                                final double securityDeposit = test.getDouble("securityDeposit");
                                final double hourlyMilesAllowed = test.getDouble("hourlyMilesAllowed");
                                final double halfDayMilesAllowed = test.getDouble("halfDayMilesAllowed");
                                final double dailyMilesAllowed = test.getDouble("dailyMilesAllowed");
                                final double weeklyMilesAllowed = test.getDouble("weeklyMilesAllowed");
                                final double monthlyMilesAllowed = test.getDouble("monthlyMilesAllowed");
                                final double totalMilesAllowed = test.getDouble("totalMilesAllowed");
                                final int lockKey = test.getInt("lockKey");

                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                lp.setMargins(0, 10, 0, 0);

                                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
                                final LinearLayout vehiclelayoutlist = (LinearLayout) inflater.inflate(R.layout.vehicle_available_list, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                vehiclelayoutlist.setId(200 + j);
                                vehiclelayoutlist.setLayoutParams(lp);

                                final ImageView imageview = (ImageView) vehiclelayoutlist.findViewById(R.id.car_imgview);

                                String url1 = serverpath+img_Path.substring(2);
                                imageLoader.displayImage(url1, imageview);

                                TextView lbl_vehName, lbl_availableqty, lbl_VehTypeName,
                                        lbl_transmission_Name, lbl_seats, lbl_doors, lbl_bags, lbl_perDayAmount, lbl_DepositeAmount,lblfuel_Name,lbl_Totalprice;

                                lbl_vehName = vehiclelayoutlist.findViewById(R.id.lbl_vehName);
                                lbl_availableqty = vehiclelayoutlist.findViewById(R.id.lbl_availableqty);
                                lbl_VehTypeName = vehiclelayoutlist.findViewById(R.id.lbl_VehTypeName);
                                lbl_transmission_Name = vehiclelayoutlist.findViewById(R.id.lbl_transmission_Name);
                                lbl_seats = vehiclelayoutlist.findViewById(R.id.lbl_seats);
                                lbl_doors = vehiclelayoutlist.findViewById(R.id.lbl_doors);
                                lbl_bags = vehiclelayoutlist.findViewById(R.id.lbl_bags);
                                lbl_perDayAmount = vehiclelayoutlist.findViewById(R.id.lbl_perDayAmount);
                                lbl_DepositeAmount = vehiclelayoutlist.findViewById(R.id.lbl_DepositeAmount);
                                lblfuel_Name= vehiclelayoutlist.findViewById(R.id.lblfuel_Name);

                                lbl_vehName.setText(vehiclE_NAME);
                                lbl_availableqty.setText(available_QTY);
                                lbl_VehTypeName.setText(vehiclE_TYPE_NAME);
                                lbl_seats.setText(vehiclE_SEAT_NO+ " Seats");
                                lbl_doors.setText(doors + " Doors");
                                lbl_bags.setText(veh_bags + " Bags");
                                lbl_transmission_Name.setText(transmission_Name);
                                //lblfuel_Name.setText(fuel_Name);

                                String strdaily_Price=(String.format(Locale.US,"%.2f",daily_Price));
                                String strsecurityDeposit=(String.format(Locale.US,"%.2f",securityDeposit));

                                lbl_perDayAmount.setText("$"+strdaily_Price);
                                lbl_DepositeAmount.setText("$"+strsecurityDeposit);

                                vehiclelayoutlist.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View arg0)
                                    {
                                        try {
                                            int len = rlVehicleAvilable.getChildCount();

                                            for (int m = 0; m < len; m++)
                                            {
                                                LinearLayout linearLayout = (LinearLayout) rlVehicleAvilable.getChildAt(m);
                                                linearLayout.setBackground(getResources().getDrawable(R.drawable.top_curve_light_gray));
                                            }
                                            vehiclelayoutlist.setBackground(getResources().getDrawable(R.drawable.curve_box_dark_gray));

                                            Bundle VehicleBundle = new Bundle();
                                            VehicleBundle.putString("img_Path", img_Path.substring(2));
                                            VehicleBundle.putInt("vehiclE_ID", vehiclE_ID);
                                            VehicleBundle.putString("vehiclE_TYPE_NAME", vehiclE_TYPE_NAME);
                                            VehicleBundle.putInt("vehiclE_TYPE_ID", vehiclE_TYPE_ID);
                                            VehicleBundle.putDouble("price", securityDeposit);
                                            VehicleBundle.putString("vehiclE_NAME", vehiclE_NAME);
                                            VehicleBundle.putString("vehiclE_SEAT_NO", vehiclE_SEAT_NO);
                                            VehicleBundle.putString("transmission", transmission);
                                            VehicleBundle.putString("v_CURR_ODOM", v_CURR_ODOM);
                                            VehicleBundle.putString("vehiclE_OPTIONS_IDS", vehiclE_OPTIONS_IDS);
                                            VehicleBundle.putString("rate_ID", rate_ID);
                                            VehicleBundle.putString("rate_Name", rate_Name);
                                            VehicleBundle.putDouble("totaL_DAYS", totaL_DAYS);
                                            VehicleBundle.putString("veh_bags", veh_bags);
                                            VehicleBundle.putString("doors", doors);
                                            VehicleBundle.putString("fuel_Name", fuel_Name);
                                            VehicleBundle.putString("transmission_Name", transmission_Name);
                                            VehicleBundle.putString("veh_Name", veh_Name);
                                            VehicleBundle.putString("year_Name", year_Name);
                                            VehicleBundle.putString("lateR_PRICE", lateR_PRICE);
                                            VehicleBundle.putInt("lateR_RATE_ID", lateR_RATE_ID);
                                            VehicleBundle.putString("lateR_RATE_NAME", lateR_RATE_NAME);
                                            VehicleBundle.putDouble("daily_Price", daily_Price);
                                            VehicleBundle.putString("available_QTY", available_QTY);
                                            VehicleBundle.putString("vehDescription", vehDescription);
                                            VehicleBundle.putString("vehicle_Make_Model_Name", vehicle_Make_Model_Name);
                                            VehicleBundle.putDouble("totalMilesAllowed", totalMilesAllowed);

                                            BookingBundle.putInt("BookingStep", 3);
                                            BookingBundle.putInt("VehicleID", vehiclE_ID);
                                            BookingBundle.putInt("vehiclE_TYPE_ID", vehiclE_TYPE_ID);

                                            Bundle Booking = new Bundle();
                                            Booking.putBundle("BookingBundle", BookingBundle);
                                            Booking.putBundle("VehicleBundle", VehicleBundle);
                                            Booking.putBundle("returnLocation", returnLocationBundle);
                                            Booking.putBundle("location", locationBundle);
                                            Booking.putBoolean("locationType", locationType);
                                            Booking.putBoolean("initialSelect", initialSelect);
                                            Booking.putString("DeliveryAndPickupModel", "");
                                            Booking.putBoolean("IsSelected", false);
                                            System.out.println(VehicleBundle);
                                            System.out.println(BookingBundle);
                                            NavHostFragment.findNavController(Fragment_Vehicles_Available.this)
                                                    .navigate(R.id.action_Vehicles_Available_to_Select_addtional_options, Booking);
                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                rlVehicleAvilable.addView(vehiclelayoutlist);
                            }
                            //rlVehicleAvilablelist.removeViewAt(0);
                        }
                        else
                        {
                          //  rlVehicleAvilablelist.removeViewAt(0);
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
        public void onError(String error) {
            System.out.println("Error-" + error);
        }
    };
}
//,"doc_Name":"14332_QRCode_20502021034350.png","doc_Details":"../Images/Company/204/Booking/QRCode/6721/14332_QRCode_20502021034350.png"


//  Totalprice=(daily_Price*totaL_DAYS + securityDeposit);
//  lbl_Totalprice.setText(String.valueOf(Totalprice));

                                /*Totalprice=(daily_Price*totaL_DAYS + securityDeposit);
                                String strTotalPrice=(String.format(Locale.US,"%.2f",Totalprice));
                                lbl_Totalprice.setText("$ "+strTotalPrice);*/
// String strDeposite=((String.format(Locale.US,"%.2f",securityDeposit)));
// String strDayPrice=((String.format(Locale.US,"%.2f",daily_Price)));
