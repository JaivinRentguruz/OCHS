package com.ochs.app.flexiicar_app.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.ochs.app.flexiicar_app.booking.Fragment_Payment_checkout;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
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
import static com.ochs.app.apicall.ApiEndPoint.GETAGREEMENTLIST;
import static com.ochs.app.apicall.ApiEndPoint.GETRESERVATIONLIST;

public class Fragment_Agreements extends Fragment
{
    ImageView BackToUserProfile;
    Handler handler = new Handler();
    public static Context context;
    public String id = "";
    ImageLoader imageLoader;
    String serverpath="";
    RelativeLayout rl_agreement;

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
        return inflater.inflate(R.layout.fragment_agreements, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ((User_Profile) getActivity()).BottomnavVisible();

        initImageLoader(getActivity());
        imageLoader = ImageLoader.getInstance();

        SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
        id = sp.getString(getString(R.string.id), "");
        serverpath = sp.getString("serverPath", "");

        BackToUserProfile=view.findViewById(R.id.BackToUserProfile);
        BackToUserProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Agreements.this)
                        .navigate(R.id.action_Agreements_to_User_Details);
            }
        });

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
        Fragment_Agreements.context = getActivity();

        ApiService ApiService = new ApiService(GetAgreementlist, RequestType.GET,
                GETAGREEMENTLIST, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);

     /*   rl_agreement = getActivity().findViewById(R.id.rl_agreement);
        rl_agreement.removeAllViews();

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final RelativeLayout reservationlayout = (RelativeLayout) inflater.inflate(R.layout.dialog_progress, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
        rl_agreement.addView(reservationlayout);*/
    }
    OnResponseListener GetAgreementlist = new OnResponseListener()
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
                            JSONObject resultSet = responseJSON.getJSONObject("resultSet");
                            final JSONArray getAgreementList = resultSet.getJSONArray("v0200_Agreement_Details");

                            final RelativeLayout rlAgreement = getActivity().findViewById(R.id.rl_agreement);
                            int len;
                            len = getAgreementList.length();

                            for (int j = 0; j < len; j++)
                            {
                                final JSONObject test = (JSONObject) getAgreementList.get(j);

                                final int agreement_ID = test.getInt("agreement_ID");
                                final int reservation_ID = test.getInt("reservation_ID");
                                final String agreement_No = test.getString("agreement_No");
                                final int vehicle_ID = test.getInt("vehicle_ID");
                                final int vehicle_Type_ID = test.getInt("vehicle_Type_ID");
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
                                final String default_Created_Date = test.getString("default_Created_Date");
                                final int customer_ID = test.getInt("customer_ID");
                                final String cust_FName = test.getString("cust_FName");
                                final String cust_LName = test.getString("cust_LName");
                                final String cust_MobileNo = test.getString("cust_MobileNo");
                                final String cust_Phoneno = test.getString("cust_Phoneno");
                                final String cust_Email = test.getString("cust_Email");
                                final String vin_Num = test.getString("vin_Num");
                                final String lic_Num = test.getString("lic_Num");
                                final String check_Out_Location_Name = test.getString("check_Out_Location_Name");
                                final String check_in_Location_Name = test.getString("check_in_Location_Name");
                                final String veh_Img_Path = test.getString("veh_Img_Path");
                                final String cust_Img_Path = test.getString("cust_Img_Path");
                                final String rate_ID = test.getString("rate_ID");
                                final String totalDays=test.getString("totalDays");
                                final String veh_Bags=test.getString("veh_Bags");
                                final String veh_Seat=test.getString("veh_Seat");
                                final String transmission_Name = test.getString("transmission_Name");
                                final String vehicle_Type_Name = test.getString("vehicle_Type_Name");
                                final String status_Name = test.getString("status_Name");
                                final String status_BGColor_Name = test.getString("status_BGColor_Name");

                                final String d_FlightNo=test.getString("d_FlightNo");
                                final String d_Airport = test.getString("d_Airport");
                                final String default_D_Time = test.getString("default_D_Time");

                                final int selfCheckOut = test.getInt("selfCheckOut");
                                final String lattitude = test.getString("lattitude");
                                final String longitude = test.getString("longitude");
                                final String model_Name = test.getString("vehicle");


                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                lp.setMargins(0, 18, 0, 0);

                                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final LinearLayout Agreementlayout = (LinearLayout) inflater.inflate(R.layout.reservation_list, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                Agreementlayout.setId(200 + j);
                                Agreementlayout.setLayoutParams(lp);
                                LinearLayout linearLayout=(LinearLayout) Agreementlayout.findViewById(R.id.ReservationListLayout);
                                final ImageView car_image = (ImageView) Agreementlayout.findViewById(R.id.vehicle_Image);
                                String url1 = serverpath+veh_Img_Path.substring(2);
                                imageLoader.displayImage(url1, car_image);

                                final ImageView Cust_Image= (ImageView) Agreementlayout.findViewById(R.id.cust_Image);
                                String url2 = serverpath+cust_Img_Path.substring(2);
                                imageLoader.displayImage(url2, Cust_Image);

                                LinearLayout llRes_StatusBG=(LinearLayout) Agreementlayout.findViewById(R.id.ll_resStatus);

                                if (status_Name.equals("Open"))
                                {
                                    llRes_StatusBG.setBackgroundTintList(ColorStateList.valueOf(parseColor(status_BGColor_Name)));
                                }
                                else {
                                    llRes_StatusBG.setBackgroundTintList(ColorStateList.valueOf(parseColor(status_BGColor_Name)));
                                }

                                TextView lblCustFullname = Agreementlayout.findViewById(R.id.lblCustFullname);
                                TextView lbl_ContactNo = Agreementlayout.findViewById(R.id.lbl_ContactNo);
                                TextView lblReservationNo = Agreementlayout.findViewById(R.id.lblReservationNo);
                                TextView lbl_CustEmail = Agreementlayout.findViewById(R.id.lbl_CustEmail);
                                TextView lbl_checkinLocName = Agreementlayout.findViewById(R.id.lbl_checkinLocName);
                                TextView txtCheckInDate = Agreementlayout.findViewById(R.id.lbl_checkIn_DateTime);
                                TextView lbl_CheckOutLocName = Agreementlayout.findViewById(R.id.lbl_CheckOutLocName);
                                TextView lbl_CheckOutDateTime = Agreementlayout.findViewById(R.id.lbl_CheckOutDateTime);
                                TextView lbl_VehicleFullname = Agreementlayout.findViewById(R.id.lbl_VehicleFullname);
                                TextView lbl_VehicleNo = Agreementlayout.findViewById(R.id.lbl_VehicleNo);
                                TextView lbl_VinNo = Agreementlayout.findViewById(R.id.lbl_VinNo);
                                TextView lbl_LicNo = Agreementlayout.findViewById(R.id.lbl_LicNo);
                                TextView lbl_Res_status= Agreementlayout.findViewById(R.id.lbl_Res_status);

                                lblCustFullname.setText(customeR_NAME);
                                lblReservationNo.setText(agreement_No);
                                lbl_ContactNo.setText(cust_MobileNo);
                                lbl_CustEmail.setText(cust_Email);
                                lbl_checkinLocName.setText(check_in_Location_Name);
                                //check_IN Date
                                SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
                                Date date1 = dateFormat1.parse(check_IN);
                                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy, HH:mm aa", Locale.US);
                                String check_INStr = sdf1.format(date1);
                                txtCheckInDate.setText(check_INStr);

                                lbl_CheckOutLocName.setText(check_Out_Location_Name);

                                //check_Out Date
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                                Date date = dateFormat.parse(check_Out);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm aa", Locale.US);
                                String check_OutStr = sdf.format(date);
                                lbl_CheckOutDateTime.setText(check_OutStr);

                                lbl_VehicleFullname.setText(vehicle);
                                lbl_VehicleNo.setText(vehicle_No);
                                lbl_LicNo.setText(lic_Num);

                               // int half = (vin_Num.length()+1)/2;

                               // String part1 = vin_Num.substring(0, half);
                               // String part2 = vin_Num.substring(half);

                               // System.out.println(part1);
                              //  System.out.println(part2);

                                lbl_VinNo.setText(vin_Num);

                                lbl_Res_status.setText(status_Name);

                                rlAgreement.addView(Agreementlayout);

                                linearLayout.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        try
                                        {
                                            Bundle AgreementsBundle=new Bundle();
                                            AgreementsBundle.putInt("reservation_ID",reservation_ID);
                                            AgreementsBundle.putInt("agreement_ID",agreement_ID);
                                            AgreementsBundle.putString("agreement_No",agreement_No);
                                            AgreementsBundle.putString("check_Out",check_Out);
                                            AgreementsBundle.putString("default_Check_Out",default_Check_Out);
                                            AgreementsBundle.putString("check_Out_Location_Name",check_Out_Location_Name);
                                            AgreementsBundle.putString("check_IN",check_IN);
                                            AgreementsBundle.putString("default_Check_In",default_Check_In);
                                            AgreementsBundle.putString("check_in_Location_Name",check_in_Location_Name);
                                            AgreementsBundle.putString("vehicle_Type_Name",vehicle_Type_Name);
                                            AgreementsBundle.putInt("vehicle_Type_ID",vehicle_Type_ID);
                                            AgreementsBundle.putInt("vehicle_ID",vehicle_ID);
                                            AgreementsBundle.putString("veh_Full_Name",veh_Full_Name);
                                            AgreementsBundle.putString("vehicle_No",vehicle_No);
                                            AgreementsBundle.putString("vehicle",vehicle);
                                            AgreementsBundle.putString("vin_Num",vin_Num);
                                            AgreementsBundle.putString("lic_Num",lic_Num);
                                            AgreementsBundle.putString("cust_MobileNo",cust_MobileNo);
                                            AgreementsBundle.putInt("customer_ID",customer_ID);
                                            AgreementsBundle.putString("cust_FName",cust_FName);
                                            AgreementsBundle.putString("cust_LName",cust_LName);
                                            AgreementsBundle.putString("customeR_NAME",customeR_NAME);
                                            AgreementsBundle.putString("cust_Phoneno",cust_Phoneno);
                                            AgreementsBundle.putString("cust_Email",cust_Email);
                                            AgreementsBundle.putString("veh_Img_Path",veh_Img_Path);
                                          //  AgreementsBundle.putString("cust_Img_Path",cust_Img_Path);
                                            AgreementsBundle.putString("rate_ID",rate_ID);
                                            AgreementsBundle.putString("totalDays",totalDays);
                                            AgreementsBundle.putString("veh_Bags",veh_Bags);
                                            AgreementsBundle.putString("veh_Seat",veh_Seat);
                                            AgreementsBundle.putString("transmission_Name",transmission_Name);
                                            AgreementsBundle.putString("d_FlightNo",d_FlightNo);
                                            AgreementsBundle.putString("d_Airport",d_Airport);
                                            AgreementsBundle.putString("default_D_Time",default_D_Time);
                                            AgreementsBundle.putString("default_Created_Date",default_Created_Date);
                                            AgreementsBundle.putString("status_Name",status_Name);
                                            AgreementsBundle.putInt("selfCheckOut",selfCheckOut);
                                            AgreementsBundle.putString("lattitude",lattitude);
                                            AgreementsBundle.putString("longitude",longitude);
                                            AgreementsBundle.putString("model_Name",model_Name);
                                            Bundle Agreements=new Bundle();
                                            Agreements.putBundle("AgreementsBundle",AgreementsBundle);
                                            System.out.println(AgreementsBundle);
                                            NavHostFragment.findNavController(Fragment_Agreements.this)
                                                    .navigate(R.id.action_Agreements_to_SummaryOfChargesForAgreements,Agreements);
                                        }
                                        catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                });
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
}
