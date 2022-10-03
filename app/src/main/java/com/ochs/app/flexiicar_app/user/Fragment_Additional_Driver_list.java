package com.ochs.app.flexiicar_app.user;

import android.content.Context;
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
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.GET_ADDITIONAL_DRIVER_LIST;

public class Fragment_Additional_Driver_list extends Fragment
{
    ImageView Back;
    public String id = "";
    Handler handler = new Handler();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driving_license_list, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            ((User_Profile) getActivity()).BottomnavVisible();

            initImageLoader(getActivity());
            imageLoader = ImageLoader.getInstance();

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            id = sp.getString(getString(R.string.id), "");
            serverpath = sp.getString("serverPath", "");

            Back = view.findViewById(R.id.Back);
            Back.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle Reservation=getArguments().getBundle("ReservationBundle");
                    Bundle ReservationBundle = new Bundle();
                    ReservationBundle.putBundle("ReservationBundle", Reservation);
                    NavHostFragment.findNavController(Fragment_Additional_Driver_list.this)
                            .navigate(R.id.action_Additional_Driver_List_to_SummaryOfCharges,ReservationBundle);
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
            Fragment_Driving_License_List.context = getActivity();

            ApiService ApiService = new ApiService(GetAdditionalDrivingList, RequestType.GET,
                    GET_ADDITIONAL_DRIVER_LIST, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    OnResponseListener GetAdditionalDrivingList = new OnResponseListener()
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

                            final JSONArray getadditionalDriverModel= resultSet.getJSONArray("additionalDriverModel");

                            final RelativeLayout rlDrivingLicence = getActivity().findViewById(R.id.rl_DrivingLicenceList);
                            int len;
                            len = getadditionalDriverModel.length();

                            for (int j = 0; j < len; j++)
                            {
                                final JSONObject test = (JSONObject) getadditionalDriverModel.get(j);
                                int Driver_ID=test.getInt("Driver_ID");
                                int cmp_ID=test.getInt("cmp_ID");
                                final String Driver_Name = test.getString("Driver_Name");
                                final String Driver_Last_Name = test.getString("Driver_Last_Name");
                                final String License_Num = test.getString("License_Num");
                                final String License_Expiry=test.getString("License_Expiry");
                                final String Driver_DOB = test.getString("Driver_DOB");
                                final String Phone_Num = test.getString("Phone_Num");
                                final String Created_On = test.getString("Created_On");
                                final String DL_Front_Doc_Name=test.getString("DL_Front_Doc_Name");
                                final String DL_Back_Doc_Name = test.getString("DL_Back_Doc_Name");
                                final String DriverEmail = test.getString("DriverEmail");
                                final String DefaultLicense_Expiry = test.getString("DefaultLicense_Expiry");
                                int CustomerID=test.getInt("CustomerID");

                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                lp.setMargins(0, 10, 0, 0);

                                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final LinearLayout Llayout = (LinearLayout) inflater.inflate(R.layout.list_additional_driver_list, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                Llayout.setId(200 + j);
                                Llayout.setLayoutParams(lp);

                                TextView lblFullName=Llayout.findViewById(R.id.lblFullName);
                                TextView lbl_IssueBy=Llayout.findViewById(R.id.lbl_IssueBy);
                                TextView lbl_Telephone=Llayout.findViewById(R.id.lbl_Telephone);
                                TextView lbl_DEmail=Llayout.findViewById(R.id.lbl_DEmail);
                                TextView lbl_DLNumber=Llayout.findViewById(R.id.lbl_DLNumber);
                                TextView lbl_DLExDate=Llayout.findViewById(R.id.lbl_DLExDate);
                                TextView lbl_Relation=Llayout.findViewById(R.id.lbl_Relation);

                                ImageView DL_Front_Doc_NameImage=Llayout.findViewById(R.id.DL_Front_Doc_Name);
                                ImageView DL_Back_Doc_NameImage=Llayout.findViewById(R.id.DL_Back_Doc_Name);

                               // String url1 = serverpath + DL_Front_Doc_Name.substring(2);
                               // imageLoader.displayImage(url1, DL_Front_Doc_NameImage);

                               // String url2 = serverpath + DL_Back_Doc_Name.substring(2);
                               // imageLoader.displayImage(url2, DL_Back_Doc_NameImage);

                                lblFullName.setText(Driver_Name+" "+Driver_Last_Name);
                              //  lbl_Dob.setText(Driver_DOB);
                                lbl_Telephone.setText(Phone_Num);
                                lbl_DEmail.setText(DriverEmail);
                                lbl_DLNumber.setText(License_Num);
                                lbl_DLExDate.setText(License_Expiry);
                                rlDrivingLicence.addView(Llayout);
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
