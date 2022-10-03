package com.ochs.app.flexiicar_app.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Color.parseColor;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_CUSTOMER;
import static com.ochs.app.apicall.ApiEndPoint.CURRENT_BOOKING_LIST;

public class Fragment_Current_VehicleOnRent extends Fragment
{
    Handler handler = new Handler();
    ImageLoader imageLoader;
    String serverpath="";
    public String id = "";
    public static Context context;
    TextView lblTitle;
    ImageView Back;
    RelativeLayout rl_reservationlist;
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
        return inflater.inflate(R.layout.fragment_reservation, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ((User_Profile) getActivity()).BottomnavVisible();

        initImageLoader(getActivity());
        imageLoader = ImageLoader.getInstance();

        SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
        serverpath = sp.getString("serverPath", "");
        id = sp.getString(getString(R.string.id), "");

        lblTitle=view.findViewById(R.id.lblTitle);
        lblTitle.setText("Current Vehicles on Rent");

        Back=view.findViewById(R.id.back_to_usardetails1);
        Back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(Fragment_Current_VehicleOnRent.this)
                        .navigate(R.id.action_CurrentVehicleOnRent_to_User_Details);
            }
        });

        AndroidNetworking.initialize(getActivity());

        String bodyParam = "";
        try
        {
            bodyParam+="customerId="+id;
            System.out.println(bodyParam);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        AndroidNetworking.initialize(getActivity());

        ApiService ApiService = new ApiService(getVehicleRentList, RequestType.GET,
                CURRENT_BOOKING_LIST, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);

   /*     rl_reservationlist = getActivity().findViewById(R.id.rl_reservationlist);
        rl_reservationlist.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final RelativeLayout reservationlayout = (RelativeLayout) inflater.inflate(R.layout.dialog_progress, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
        rl_reservationlist.addView(reservationlayout);*/
    }

    OnResponseListener getVehicleRentList = new OnResponseListener()
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
                            JSONArray VehicleList = resultSet.getJSONArray("v0200_Agreement_Details");

                            final RelativeLayout rlVehicleOnRent = getActivity().findViewById(R.id.rl_reservationlist);
                            int len;
                            len = VehicleList.length();
                            rlVehicleOnRent.removeAllViews();

                            for (int j = 0; j < len; j++)
                            {
                                final JSONObject test = (JSONObject)VehicleList.get(j);
                                final int agreement_ID = test.getInt("agreement_ID");
                                final int reservation_ID = test.getInt("reservation_ID");
                                final String agreement_No = test.getString("agreement_No");
                                final int vehicle_ID = test.getInt("vehicle_ID");
                                final String vehicle_No = test.getString("vehicle_No");
                                final String vehicle = test.getString("vehicle");
                                final String exC_VEHICLE = test.getString("exC_VEHICLE");
                                final String veh_Full_Name = test.getString("veh_Full_Name");
                                final String check_Out = test.getString("check_Out");
                                final String check_IN = test.getString("check_IN");
                                final String default_Check_Out = test.getString("default_Check_Out");
                                final String default_Check_In = test.getString("default_Check_In");
                                final String customeR_NAME = test.getString("customeR_NAME");
                                final String created_By = test.getString("created_By");
                                final int created_ByID = test.getInt("created_ByID");
                                final String created_Date = test.getString("created_Date");
                                final int agreement_Type = test.getInt("agreement_Type");
                                final String agreement_Type_Name = test.getString("agreement_Type_Name");
                                final int check_Out_Location = test.getInt("check_Out_Location");
                                final int check_IN_Location = test.getInt("check_IN_Location");

                                final int customer_ID = test.getInt("customer_ID");
                                final String cust_FName = test.getString("cust_FName");
                                final String cust_LName = test.getString("cust_LName");
                                final String cust_MobileNo = test.getString("cust_MobileNo");
                                final String cust_Email = test.getString("cust_Email");
                                final String vin_Num = test.getString("vin_Num");
                                final String lic_Num = test.getString("lic_Num");
                                final String cust_DOB = test.getString("cust_DOB");
                                final String veh_Img_Path = test.getString("veh_Img_Path");
                                final String status_Name = test.getString("status_Name");
                                final String status_BGColor_Name = test.getString("status_BGColor_Name");

                                final String check_in_Location_Name = test.getString("check_in_Location_Name");
                                final String check_Out_Location_Name = test.getString("check_Out_Location_Name");

                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                lp.setMargins(0, 10, 0, 0);

                                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
                                final LinearLayout vehiclelayoutlist = (LinearLayout) inflater.inflate(R.layout.reservation_list, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                vehiclelayoutlist.setId(200 + j);
                                vehiclelayoutlist.setLayoutParams(lp);

                                final ImageView car_image = (ImageView) vehiclelayoutlist.findViewById(R.id.vehicle_Image);
                                String url1 = serverpath+veh_Img_Path.substring(2);
                                imageLoader.displayImage(url1, car_image);

                              /*  final ImageView Cust_Image= (ImageView) reservationlayout.findViewById(R.id.cust_Image);
                                String url2 = serverpath+cust_Img_Path.substring(2);
                                imageLoader.displayImage(url2, Cust_Image);*/

                                LinearLayout linearLayout=(LinearLayout) vehiclelayoutlist.findViewById(R.id.ReservationListLayout);
                                LinearLayout llRes_Status=(LinearLayout) vehiclelayoutlist.findViewById(R.id.ll_resStatus);

                                if (status_Name.equals("Open"))
                                {
                                    llRes_Status.setBackgroundTintList(ColorStateList.valueOf(parseColor(status_BGColor_Name)));
                                }
                                else
                                {
                                    llRes_Status.setBackgroundTintList(ColorStateList.valueOf(parseColor(status_BGColor_Name)));
                                }


                                TextView lblCustFullname = vehiclelayoutlist.findViewById(R.id.lblCustFullname);
                                TextView lbl_ContactNo = vehiclelayoutlist.findViewById(R.id.lbl_ContactNo);
                                TextView lbl_CustEmail = vehiclelayoutlist.findViewById(R.id.lbl_CustEmail);
                                TextView lbl_checkinLocName = vehiclelayoutlist.findViewById(R.id.lbl_checkinLocName);
                                TextView lbl_checkIn_DateTime = vehiclelayoutlist.findViewById(R.id.lbl_checkIn_DateTime);
                                TextView lbl_CheckOutLocName = vehiclelayoutlist.findViewById(R.id.lbl_CheckOutLocName);
                                TextView lbl_CheckOutDateTime = vehiclelayoutlist.findViewById(R.id.lbl_CheckOutDateTime);
                                TextView lbl_VehicleFullname = vehiclelayoutlist.findViewById(R.id.lbl_VehicleFullname);
                                TextView lbl_VehicleNo = vehiclelayoutlist.findViewById(R.id.lbl_VehicleNo);
                                TextView lbl_VinNo = vehiclelayoutlist.findViewById(R.id.lbl_VinNo);
                                TextView lbl_LicNo = vehiclelayoutlist.findViewById(R.id.lbl_LicNo);

                                TextView lbl_Res_status= vehiclelayoutlist.findViewById(R.id.lbl_Res_status);
                                TextView lblReservationNo= vehiclelayoutlist.findViewById(R.id.lblReservationNo);

                                lblCustFullname.setText(cust_FName+" "+cust_LName);
                                lbl_ContactNo.setText(cust_MobileNo);
                                lblReservationNo.setText(String.valueOf(reservation_ID));
                                lbl_CustEmail.setText(cust_Email);

                                lbl_CheckOutLocName.setText(check_Out_Location_Name);

                                SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
                                Date date1 = dateFormat1.parse(check_Out);
                                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy, HH:mm aa", Locale.US);
                                String CheckOutStr = sdf1.format(date1);
                                lbl_CheckOutDateTime.setText(CheckOutStr);

                                lbl_checkinLocName.setText(check_in_Location_Name);

                                SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
                                Date date2 = dateFormat2.parse(check_IN);
                                SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy, HH:mm aa", Locale.US);
                                String checkINStr = sdf2.format(date2);
                                lbl_checkIn_DateTime.setText(checkINStr);

                                lbl_VehicleFullname.setText(veh_Full_Name);
                                lbl_VehicleNo.setText(vehicle_No);
                                lbl_VinNo.setText(vin_Num);
                                lbl_LicNo.setText(lic_Num);

                                lbl_Res_status.setText(status_Name);

                                rlVehicleOnRent.addView(vehiclelayoutlist);
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
