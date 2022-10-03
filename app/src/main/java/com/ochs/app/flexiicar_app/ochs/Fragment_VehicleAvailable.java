package com.ochs.app.flexiicar_app.ochs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import com.ochs.app.flexiicar_app.login.Login;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_VEHICLE;
import static com.ochs.app.apicall.ApiEndPoint.VEHICLE_LIST;

public class Fragment_VehicleAvailable extends Fragment
{
    Handler handler = new Handler();
    ImageLoader imageLoader;
    String serverpath="";
    Double Totalprice;
    LinearLayout filter_icon;
    ImageView backarrowimg;
    public int customer_ID;
    public static Context context;
    JSONArray VehicleList = new JSONArray();
    EditText edt_searchVehicleName;
    TextView lblLogout,lblDiscard;
    String default_Email="",default_Password="";
    RelativeLayout rlVehicleAvilableList;
    ProgressBar Progressbar;

    public static void initImageLoader(Context context)
    {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vehicles_available, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

       // SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
       // id = sp.getString(getString(R.string.id), "");


        edt_searchVehicleName= view.findViewById(R.id.edt_searchVehicleName);

        initImageLoader(getActivity());
        imageLoader = ImageLoader.getInstance();

        SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
        serverpath = sp.getString("serverPath", "");
        customer_ID = sp.getInt("customer_ID", 0);
        default_Email=sp.getString("default_Email","");
        default_Password=sp.getString("default_Password","");
        System.out.println(customer_ID);
        System.out.println(serverpath);

        lblLogout=view.findViewById(R.id.lblLogout);
        lblDiscard=view.findViewById(R.id.lblDiscard);
        lblDiscard.setVisibility(View.GONE);
        lblLogout.setVisibility(View.VISIBLE);

        backarrowimg = view.findViewById(R.id.Back);
        backarrowimg.setVisibility(View.GONE);

        filter_icon = view.findViewById(R.id.filter_icon);
        filter_icon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_VehicleAvailable.this)
                        .navigate(R.id.action_Vehicles_Available_to_Vehicles_FilterList);
            }
        });

        lblLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to Logout?");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                String msg = "You Have Been Successfully Logged Out!";
                                CustomToast.showToast(getActivity(),msg,0);


                                SharedPreferences.Editor editor = sp.edit();
                                editor.clear();
                                editor.apply();
                                Intent i=new Intent(getActivity(), Login.class);
                                File file=new File(getActivity().getFilesDir()+"/LoggedData.txt");
                                file.delete();
                                startActivity(i);
                                getActivity().finish();
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

        AndroidNetworking.initialize(getActivity());
        String bodyParam="";
        ApiService ApiService = new ApiService(getVehicleList, RequestType.GET,
                VEHICLE_LIST, BASE_URL_VEHICLE, new HashMap<String, String>(), bodyParam);

        rlVehicleAvilableList = getActivity().findViewById(R.id.relative_vehicle_available);
//        rlVehicleAvilableList.removeAllViews();

   /*     LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final RelativeLayout reservationlayout = (RelativeLayout) inflater.inflate(R.layout.dialog_progress, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
        Progressbar=reservationlayout.findViewById(R.id.Progressbar);
        Progressbar.setVisibility(View.VISIBLE);
        rlVehicleAvilableList.addView(reservationlayout);*/

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

                    for (int j = 0; j < len; j++)
                    {
                        final JSONObject test = (JSONObject)VehicleList.get(j);
                        final int vehiclE_ID = test.getInt("vehicle_ID");
                        final String vehicle_No = test.getString("vehicle_No");
                        final String vin_Num = test.getString("vin_Num");
                        final String lic_Num = test.getString("lic_Num");
                        final int make_ID = test.getInt("make_ID");
                        final String make_Name = test.getString("make_Name");
                        final int model_ID = test.getInt("model_ID");
                        final String model_Name = test.getString("model_Name");
                        final int year_ID = test.getInt("year_ID");
                        final String year_Name = test.getString("year_Name");
                        final int transmission = test.getInt("transmission");
                        final int v_Type_ID = test.getInt("v_Type_ID");
                        final int vstatus = test.getInt("status");
                        final String v_Color = test.getString("v_Color");
                        final String v_Tank_Size = test.getString("v_Tank_Size");
                        final int fuel_Type = test.getInt("fuel_Type");
                        final String v_Curr_Odom = test.getString("v_Curr_Odom");
                        final int v_Curr_Loc_ID = test.getInt("v_Curr_Loc_ID");
                        final int v_Own_Loc_ID = test.getInt("v_Own_Loc_ID");
                        final int company_ID = test.getInt("company_ID");
                        final String plate_Issue_Date = test.getString("plate_Issue_Date");
                        final String plate_Exp_Date = test.getString("plate_Exp_Date");
                        final int rate_ID = test.getInt("rate_ID");
                        final int isActive = test.getInt("isActive");
                        final int isOnline = test.getInt("isOnline");
                        final int isSale = test.getInt("isSale");
                        final int isNew = test.getInt("isNew");
                        final String garage_Option_IDs = test.getString("garage_Option_IDs");
                        final String garage_Notes = test.getString("garage_Notes");
                        final int cmp_ID = test.getInt("cmp_ID");
                        final int created_By = test.getInt("created_By");
                        final String created_Date = test.getString("created_Date");
                        final String garage_Date = test.getString("garage_Date");
                        final int veh_Seat = test.getInt("veh_Seat");
                        final int veh_Engine = test.getInt("veh_Engine");
                        final int is_Specs_Reminder = test.getInt("is_Specs_Reminder");
                        final String garage_EmpName_Sign = test.getString("garage_EmpName_Sign");
                        final int garage_Reminder = test.getInt("garage_Reminder");
                        final int veh_DeprecPer = test.getInt("veh_DeprecPer");
                        final double veh_PayoutValue = test.getDouble("veh_PayoutValue");
                        final double veh_DownPayment = test.getDouble("veh_DownPayment");
                        final int veh_LeaseTerm = test.getInt("veh_LeaseTerm");
                        final int veh_LeaseCompID = test.getInt("veh_LeaseCompID");
                        final String inService_Date = test.getString("inService_Date");
                        final int isDrive = test.getInt("isDrive");
                        final int veh_Bags = test.getInt("veh_Bags");
                        final int vehicleCategoryID = test.getInt("vehicleCategoryID");
                        final int currentAgrID = test.getInt("currentAgrID");
                        final int isMobile = test.getInt("isMobile");
//                        final String vehicleProfilePicName = test.getString("vehicleProfilePicName");
                        final String path = test.getString("img_Path");
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        lp.setMargins(0, 10, 0, 0);

                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
                        final LinearLayout vehiclelayoutlist = (LinearLayout) inflater.inflate(R.layout.vehicle_available_list, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                        vehiclelayoutlist.setId(200 + j);
                        vehiclelayoutlist.setLayoutParams(lp);

                        final ImageView imageview = (ImageView) vehiclelayoutlist.findViewById(R.id.car_imgview);

                        String url1 = serverpath+path.substring(2);
                                imageLoader.displayImage(url1, imageview);

                        TextView txtvehiclename, txtAvailableQty, txt_VehicleTypeName,
                                txt_transmission_Name, txtseats, txtDoors, txtbags, lbl_perDayAmount, lbl_DepositeAmount,lblfuel_Name,txt_price;

                        txtvehiclename = vehiclelayoutlist.findViewById(R.id.lbl_vehName);
                        txtAvailableQty = vehiclelayoutlist.findViewById(R.id.lbl_availableqty);
                        txt_VehicleTypeName = vehiclelayoutlist.findViewById(R.id.lbl_VehTypeName);
                        txt_transmission_Name = vehiclelayoutlist.findViewById(R.id.lbl_transmission_Name);
                        txtseats = vehiclelayoutlist.findViewById(R.id.lbl_seats);
                        txtDoors = vehiclelayoutlist.findViewById(R.id.lbl_doors);
                        txtbags = vehiclelayoutlist.findViewById(R.id.lbl_bags);
                        lbl_perDayAmount = vehiclelayoutlist.findViewById(R.id.lbl_perDayAmount);
                        lbl_DepositeAmount = vehiclelayoutlist.findViewById(R.id.lbl_DepositeAmount);
                        lblfuel_Name= vehiclelayoutlist.findViewById(R.id.lblfuel_Name);
                        LinearLayout v_rate = vehiclelayoutlist.findViewById(R.id.v_rate);
                        v_rate.setVisibility(View.GONE);
                        LinearLayout vechicleDetail =vehiclelayoutlist.findViewById(R.id.vechicleDetail);
                        vechicleDetail.setVisibility(View.GONE);

                        LinearLayout vechicleDetail2 = vehiclelayoutlist.findViewById(R.id.vechicleDetail2);
                        vechicleDetail2.setVisibility(View.VISIBLE);

                        LinearLayout available = vehiclelayoutlist.findViewById(R.id.available);
                        available.setVisibility(View.GONE);

                        TextView vechno,vinno,licno;

                        vechno = vehiclelayoutlist.findViewById(R.id.vechno);
                        vinno = vehiclelayoutlist.findViewById(R.id.vinno);
                        licno = vehiclelayoutlist.findViewById(R.id.licno);

                        vechno.setText(vehicle_No);
                        vinno.setText(vin_Num);
                        licno.setText(lic_Num);

                        txtvehiclename.setText(make_Name + " "  + model_Name + " " + year_Name);

                        //txtAvailableQty.setText(available_QTY);

                        txt_VehicleTypeName.setText(make_Name);

                        String Veh_Seats=String.valueOf(veh_Seat);
                        txtseats.setText(Veh_Seats+ " Seats");

                        //  txtDoors.setText(doors + " Doors");

                        String Veh_Bags=String.valueOf(veh_Bags);
                        txtbags.setText(Veh_Bags + " Bags");

                        if(transmission==1)
                        {
                            txt_transmission_Name.setText("Automatic");
                        }
                        else {
                            txt_transmission_Name.setText("Manual");
                        }

                        //   Totalprice=(daily_Price*totaL_DAYS + securityDeposit);
                        //  txt_price.setText(String.valueOf(Totalprice));

                        //  Totalprice=(daily_Price*totaL_DAYS + securityDeposit);
                        //   txt_price.setText((String.format(Locale.US,"%.2f",Totalprice)));

                        //    String strDeposite=((String.format(Locale.US,"%.2f",securityDeposit)));
                        //   txtDeposite.setText("US$ "+strDeposite+"/ Deposite");

                        //   String strDayPrice=((String.format(Locale.US,"%.2f",daily_Price)));
                        //  txtDayPrice.setText("US$ "+strDayPrice+"/ Day");

                      //  lbl_perDayAmount.setText("$ "+daily_Price);
                       // lbl_DepositeAmount.setText("$ "+securityDeposit);


                        vehiclelayoutlist.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View arg0)
                            {

                                Bundle VehicleModel=new Bundle();
                                VehicleModel.putInt("vehiclE_ID",vehiclE_ID);
                                VehicleModel.putString("make_Name",make_Name);
                                VehicleModel.putString("model_Name",model_Name);
                                Bundle VehicleModelBundle=new Bundle();
                                VehicleModelBundle.putBundle("VehicleModelBundle",VehicleModel);
                                NavHostFragment.findNavController(Fragment_VehicleAvailable.this)
                                        .navigate(R.id.action_Vehicles_Available_to_LocationAndKey,VehicleModelBundle);
                            }
                        });


                        rlVehicleAvilable.addView(vehiclelayoutlist);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
            }
        });

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
                                final int vehiclE_ID = test.getInt("vehicle_ID");
                                final String vehicle_No = test.getString("vehicle_No");
                                final String vin_Num = test.getString("vin_Num");
                                final String lic_Num = test.getString("lic_Num");
                                final int make_ID = test.getInt("make_ID");
                                final String make_Name = test.getString("make_Name");
                                final int model_ID = test.getInt("model_ID");
                                final String model_Name = test.getString("model_Name");
                                final int year_ID = test.getInt("year_ID");
                                final String year_Name = test.getString("year_Name");
                                final int transmission = test.getInt("transmission");
                                final int v_Type_ID = test.getInt("v_Type_ID");
                                final int vstatus = test.getInt("status");
                                final String v_Color = test.getString("v_Color");
                                final String v_Tank_Size = test.getString("v_Tank_Size");
                                final int fuel_Type = test.getInt("fuel_Type");
                                final String v_Curr_Odom = test.getString("v_Curr_Odom");
                                final int v_Curr_Loc_ID = test.getInt("v_Curr_Loc_ID");
                                final int v_Own_Loc_ID = test.getInt("v_Own_Loc_ID");
                                final int company_ID = test.getInt("company_ID");
                                final String plate_Issue_Date = test.getString("plate_Issue_Date");
                                final String plate_Exp_Date = test.getString("plate_Exp_Date");
                                final int rate_ID = test.getInt("rate_ID");
                                final int isActive = test.getInt("isActive");
                                final int isOnline = test.getInt("isOnline");
                                final int isSale = test.getInt("isSale");
                                final int isNew = test.getInt("isNew");
                                final String garage_Option_IDs = test.getString("garage_Option_IDs");
                                final String garage_Notes = test.getString("garage_Notes");
                                final int cmp_ID = test.getInt("cmp_ID");
                                final int created_By = test.getInt("created_By");
                                final String created_Date = test.getString("created_Date");
                                final String garage_Date = test.getString("garage_Date");
                                final int veh_Seat = test.getInt("veh_Seat");
                                final int veh_Engine = test.getInt("veh_Engine");
                                final int is_Specs_Reminder = test.getInt("is_Specs_Reminder");
                                final String garage_EmpName_Sign = test.getString("garage_EmpName_Sign");
                                final int garage_Reminder = test.getInt("garage_Reminder");
                                final int veh_DeprecPer = test.getInt("veh_DeprecPer");
                                final double veh_PayoutValue = test.getDouble("veh_PayoutValue");
                                final double veh_DownPayment = test.getDouble("veh_DownPayment");
                                final int veh_LeaseTerm = test.getInt("veh_LeaseTerm");
                                final int veh_LeaseCompID = test.getInt("veh_LeaseCompID");
                                final String inService_Date = test.getString("inService_Date");
                                final int isDrive = test.getInt("isDrive");
                                final int veh_Bags = test.getInt("veh_Bags");
                                final int vehicleCategoryID = test.getInt("vehicleCategoryID");
                                final int currentAgrID = test.getInt("currentAgrID");
                                final int isMobile = test.getInt("isMobile");
//                                final String vehicleProfilePicName = test.getString("vehicleProfilePicName");
                                final String path = test.getString("img_Path");
                                final String currAddress = test.getString("currAddress");
                                final int lockKey = test.getInt("lockKey");
                                final String vehiclE_TYPE_NAME = test.getString("vehiclE_TYPE_NAME");
                                final Double latitude = test.getDouble("lattitude");
                                final Double longitude = test.getDouble("longitude");


                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                lp.setMargins(0, 10, 0, 0);

                                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
                                final LinearLayout vehiclelayoutlist = (LinearLayout) inflater.inflate(R.layout.vehicle_available_list, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                vehiclelayoutlist.setId(200 + j);
                                vehiclelayoutlist.setLayoutParams(lp);

                                final ImageView imageview = (ImageView) vehiclelayoutlist.findViewById(R.id.car_imgview);

                                String url1 = serverpath+path.substring(2);
                                imageLoader.displayImage(url1, imageview);
                                System.out.println(url1);

                                TextView txtvehiclename, txtAvailableQty, txt_VehicleTypeName,
                                        txt_transmission_Name, txtseats, txtDoors, txtbags, lbl_perDayAmount, lbl_DepositeAmount,lblfuel_Name,txt_price;

                                txtvehiclename = vehiclelayoutlist.findViewById(R.id.lbl_vehName);
                                txtAvailableQty = vehiclelayoutlist.findViewById(R.id.lbl_availableqty);
                                txt_VehicleTypeName = vehiclelayoutlist.findViewById(R.id.lbl_VehTypeName);
                                txt_transmission_Name = vehiclelayoutlist.findViewById(R.id.lbl_transmission_Name);
                                txtseats = vehiclelayoutlist.findViewById(R.id.lbl_seats);
                                txtDoors = vehiclelayoutlist.findViewById(R.id.lbl_doors);
                                txtbags = vehiclelayoutlist.findViewById(R.id.lbl_bags);
                                lbl_perDayAmount = vehiclelayoutlist.findViewById(R.id.lbl_perDayAmount);
                                lbl_DepositeAmount = vehiclelayoutlist.findViewById(R.id.lbl_DepositeAmount);
                                lblfuel_Name= vehiclelayoutlist.findViewById(R.id.lblfuel_Name);

                                LinearLayout v_rate = vehiclelayoutlist.findViewById(R.id.v_rate);
                                v_rate.setVisibility(View.GONE);

                                LinearLayout vechicleDetail =vehiclelayoutlist.findViewById(R.id.vechicleDetail);
                                vechicleDetail.setVisibility(View.GONE);

                                LinearLayout vechicleDetail2 = vehiclelayoutlist.findViewById(R.id.vechicleDetail2);
                                vechicleDetail2.setVisibility(View.VISIBLE);

                                LinearLayout available = vehiclelayoutlist.findViewById(R.id.available);
                                available.setVisibility(View.GONE);

                                TextView vechno,vinno,licno;

                                vechno = vehiclelayoutlist.findViewById(R.id.vechno);
                                vinno = vehiclelayoutlist.findViewById(R.id.vinno);
                                licno = vehiclelayoutlist.findViewById(R.id.licno);

                                vechno.setText(vehicle_No);
                                vinno.setText(vin_Num);
                                licno.setText(lic_Num);

                                txtvehiclename.setText(make_Name + " "  + model_Name + " " + year_Name);

                                //txtAvailableQty.setText(available_QTY);

                                txt_VehicleTypeName.setText(vehiclE_TYPE_NAME);

                                String Veh_Seats=String.valueOf(veh_Seat);
                                txtseats.setText(Veh_Seats+ " Seats");

                              //  txtDoors.setText(doors + " Doors");

                                String Veh_Bags=String.valueOf(veh_Bags);
                                txtbags.setText(Veh_Bags + " Bags");

                                if(transmission==1)
                                {
                                    txt_transmission_Name.setText("Automatic");
                                }
                                else {
                                    txt_transmission_Name.setText("Manual");
                                }
                               // lbl_perDayAmount.setText("$ "+daily_Price);
                              //  lbl_DepositeAmount.setText("$ "+securityDeposit);

                                //   Totalprice=(daily_Price*totaL_DAYS + securityDeposit);
                              //  txt_price.setText(String.valueOf(Totalprice));

                              //  Totalprice=(daily_Price*totaL_DAYS + securityDeposit);
                             //   txt_price.setText((String.format(Locale.US,"%.2f",Totalprice)));

                              //  String strTotalPrice=(String.format(Locale.US,"%.2f",Totalprice));
                                //lbl_Totalprice.setText("$ "+strTotalPrice);

                            //    String strDeposite=((String.format(Locale.US,"%.2f",securityDeposit)));
                             //   txtDeposite.setText("US$ "+strDeposite+"/ Deposite");

                             //   String strDayPrice=((String.format(Locale.US,"%.2f",daily_Price)));
                              //  txtDayPrice.setText("US$ "+strDayPrice+"/ Day");

                                vehiclelayoutlist.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View arg0)
                                    {

                                        Bundle VehicleModel=new Bundle();
                                        VehicleModel.putInt("vehiclE_ID",vehiclE_ID);
                                        VehicleModel.putString("make_Name",make_Name + " "  + model_Name + " " + year_Name);
                                        VehicleModel.putString("model_Name",model_Name);
                                        VehicleModel.putString("currAddress",currAddress);
                                        VehicleModel.putInt("lockKey",lockKey);
                                        VehicleModel.putString("path",path);
                                        VehicleModel.putDouble("latitude", latitude);
                                        VehicleModel.putDouble("longitude",longitude);
                                        Bundle VehicleModelBundle=new Bundle();
                                        VehicleModelBundle.putBundle("VehicleModelBundle",VehicleModel);
                                        NavHostFragment.findNavController(Fragment_VehicleAvailable.this)
                                                .navigate(R.id.action_Vehicles_Available_to_LocationAndKey,VehicleModelBundle);
                                    }
                                });
                                rlVehicleAvilable.addView(vehiclelayoutlist);
                            }
                          //  rlVehicleAvilableList.removeViewAt(0);
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
