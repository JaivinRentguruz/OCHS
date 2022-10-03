package com.ochs.app.flexiicar_app.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.MyApplication;
import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.flexiicar_app.booking.Booking_Activity;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import com.ochs.app.flexiicar_app.login.Login;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.zoho.commons.LauncherModes;
import com.zoho.commons.LauncherProperties;
import com.zoho.salesiqembed.ZohoSalesIQ;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_CUSTOMER;
import static com.ochs.app.apicall.ApiEndPoint.GETCUSTOMERPROFILE;
import static com.ochs.app.apicall.ApiEndPoint.GETCUSTOMERSUMMARY;

public class Fragment_Customer_Profile extends Fragment
{
    TextView lbl_Logout,txtFName,txtLName,txtMobileNo,txtEmail,txt_OpenReservation,txt_OpenAgreement,txt_OpenTrafficticket,txt_AccountBalance;
    LinearLayout LayoutAgreements,LayoutReservation,Layoutusertimeline,Layoutbillsandaccount,
                 LayoutInsurancePolicy,LayoutDrivingLicsence,LayoutProfileDetails,LayoutCreditcard,LinearL_ChangePass,VehicleOnRent;
    ImageView backarrow,Profilepic;
    public String id = "";
    Handler handler = new Handler();
    public static Context context;
    ImageLoader imageLoader;
    String serverpath="";
    SharedPreferences sp;
    LinearLayout llCall, llEmail, llText, llchat;

    LauncherProperties launcherProperties;

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
        return inflater.inflate(R.layout.fragment_customer_profile, container, false);
    }

    public void onViewCreated(@NonNull View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ((User_Profile) getActivity()).BottomnavVisible();

        initImageLoader(getActivity());
        imageLoader = ImageLoader.getInstance();

        sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
        serverpath = sp.getString("serverPath","");
        id = sp.getString(getString(R.string.id), "");
        System.out.println("serverpath"+serverpath);

        lbl_Logout=view.findViewById(R.id.lbl_Logout);
        txtFName=view.findViewById(R.id.txt_FName);
        txtLName=view.findViewById(R.id.txt_LName);
        txtMobileNo=view.findViewById(R.id.lbl_MobileNO);
        txtEmail=view.findViewById(R.id.lbl_EMailAdd);
        Profilepic=view.findViewById(R.id.img_Profile);
        txt_AccountBalance=view.findViewById(R.id.txt_AccountBalance);
        txt_OpenReservation=view.findViewById(R.id.lbl_openReservation);
        txt_OpenAgreement=view.findViewById(R.id.lbl_openAgreement);
        txt_OpenTrafficticket=view.findViewById(R.id.lbl_TrafficTicket);

        LayoutAgreements=view.findViewById(R.id.lbl_Agreements);
        LayoutReservation=view.findViewById(R.id.lbl_Reservation);
        VehicleOnRent=view.findViewById(R.id.vehicle_OnRent);
        Layoutusertimeline=view.findViewById(R.id.lblActivity_timeline);
        Layoutbillsandaccount=view.findViewById(R.id.lblaccount_statement);
        LayoutInsurancePolicy=view.findViewById(R.id.lbl_insurancePolicy);
        LayoutDrivingLicsence=view.findViewById(R.id.lbl_drivinglicense);
        LinearL_ChangePass=view.findViewById(R.id.LinearL_ChangePass);
        backarrow=view.findViewById(R.id.Back);

        llCall=view.findViewById(R.id.llCall);
        llEmail=view.findViewById(R.id.llEmail);
        llText=view.findViewById(R.id.llText);
        llchat=view.findViewById(R.id.chat);
        //launcherProperties = new LauncherProperties(LauncherModes.STATIC);

        LayoutProfileDetails=view.findViewById(R.id.lbl_profileDetails);

        LayoutCreditcard=view.findViewById(R.id.lbl_creditcardAc);

        lbl_Logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
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

        LayoutAgreements.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Customer_Profile.this)
                        .navigate(R.id.action_User_Details_to_Agreements);
            }
        });

        LayoutReservation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Customer_Profile.this)
                        .navigate(R.id.action_User_Details_to_Reservation);
            }
        });
        VehicleOnRent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Customer_Profile.this)
                        .navigate(R.id.action_User_Details_to_CurrentVehicleOnRent);
            }
        });
        Layoutusertimeline.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Customer_Profile.this)
                        .navigate(R.id.action_User_Details_to_User_timeline);
            }
        });
        Layoutbillsandaccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle bundle = new Bundle();
                bundle.putBoolean("StatmentType",true);
                NavHostFragment.findNavController(Fragment_Customer_Profile.this)
                        .navigate(R.id.action_User_Details_to_Bills_and_Payment, bundle);
            }
        });
        backarrow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getActivity(), Booking_Activity.class);
                startActivity(i);
            }
        });
        LayoutInsurancePolicy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle bundle = new Bundle();
                bundle.putInt("key",0);
                NavHostFragment.findNavController(Fragment_Customer_Profile.this)
                        .navigate(R.id.action_User_Details_to_InsurancePolicyList,bundle);
            }
        });
        LayoutDrivingLicsence.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Customer_Profile.this)
                        .navigate(R.id.action_User_Details_to_DrivingLicense_Details);
            }
        });
        LayoutCreditcard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle bundle = new Bundle();
                bundle.putInt("backTo",1);
                NavHostFragment.findNavController(Fragment_Customer_Profile.this)
                        .navigate(R.id.action_User_Details_to_CardsOnAccount, bundle);
            }
        });
        LinearL_ChangePass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Customer_Profile.this)
                        .navigate(R.id.action_User_Details_to_ChangePass);
            }
        });

        llchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* ZohoSalesIQ.init(getActivity().getApplication(),"%2FNJqdRRuhG8KjvpSe8HH3iYwEaKdY%2BUMZcKdJznlSlLNP9vcMie9aZmYP0etprRl","TYtI2ZH2%2BYRvVolZYqdothUsc9AY6MzqAoVqsfePrzcvWzsTl00%2B3suhteszZhLZKRE7EDU0HR4cXsgRQ%2FOZVaF2YNmN%2BPhMgYADr7lfKGKrA889%2FjwasHfdgwkiWf80ynsKU%2BBf%2BfROZ1106fCaMw%3D%3D");
                ZohoSalesIQ.showLauncher(true);*/

                Intent intent = new Intent(getContext(), MyApplication.class);
                startActivity(intent);
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
        Fragment_Customer_Profile.context = getActivity();

        ApiService ApiService = new ApiService(GetCustomerProfile, RequestType.GET,
                GETCUSTOMERPROFILE, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);

        ApiService ApiService1 = new ApiService(GetCustomerSummary, RequestType.GET,
                GETCUSTOMERSUMMARY, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);
    }
    //GetCustomerProfile
    OnResponseListener GetCustomerProfile = new OnResponseListener()
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
                                final JSONObject customerProfile= resultSet.getJSONObject("customerProfile");

                                final int customerId=customerProfile.getInt("customerId");
                                final String cust_FName=customerProfile.getString("cust_FName");
                                final String cust_LName=customerProfile.getString("cust_LName");
                                final String cust_Email=customerProfile.getString("cust_Email");
                                final String cust_MobileNo=customerProfile.getString("cust_MobileNo");
                                final String cust_Phoneno=customerProfile.getString("cust_Phoneno");
                                final String cust_Street=customerProfile.getString("cust_Street");
                                final String cust_UnitNo=customerProfile.getString("cust_UnitNo");
                                final String cust_City=customerProfile.getString("cust_City");
                                final int cust_State_ID=customerProfile.getInt("cust_State_ID");
                                final int cust_Country_ID=customerProfile.getInt("cust_Country_ID");
                                final String cust_Gender=customerProfile.getString("cust_Gender");
                                final String cust_ZipCode=customerProfile.getString("cust_ZipCode");
                                final String cust_DOB=customerProfile.getString("cust_DOB");
                                final String cust_Photo=customerProfile.getString("cust_Photo");

                                txtFName.setText(cust_FName);
                                txtLName.setText(cust_LName);
                                txtMobileNo.setText(cust_MobileNo);
                                txtEmail.setText(cust_Email);

                                llCall.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:"+cust_MobileNo));
                                        startActivity(intent);
                                    }
                                });

                                llEmail.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                                "mailto",cust_Email, null));
                                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                                    }
                                });

                                llText.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                            Uri  urimsg=Uri.parse("smsto:" + cust_MobileNo);
                                            Intent intent = new Intent(Intent.ACTION_SENDTO,urimsg);
                                            intent.putExtra("sms_body", "");
                                            startActivity(intent);
                                    }
                                });

                                String url1 = serverpath+cust_Photo.substring(2);
                                imageLoader.getInstance().displayImage(url1,Profilepic);
                                System.out.println(url1);

                                LayoutProfileDetails.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        Bundle CustomerBundle=new Bundle();
                                        CustomerBundle.putInt("customerId",customerId);
                                        CustomerBundle.putString("cust_FName",cust_FName);
                                        CustomerBundle.putString("cust_LName",cust_LName);
                                        CustomerBundle.putString("cust_Email",cust_Email);
                                        CustomerBundle.putString("cust_MobileNo",cust_MobileNo);
                                        CustomerBundle.putString("cust_Phoneno",cust_Phoneno);
                                        CustomerBundle.putString("cust_Street",cust_Street);
                                        CustomerBundle.putString("cust_UnitNo",cust_UnitNo);
                                        CustomerBundle.putString("cust_City",cust_City);
                                        CustomerBundle.putInt("cust_State_ID",cust_State_ID);
                                        CustomerBundle.putInt("cust_Country_ID",cust_Country_ID);
                                        CustomerBundle.putString("cust_Gender",cust_Gender);
                                        CustomerBundle.putString("cust_ZipCode",cust_ZipCode);
                                        CustomerBundle.putString("cust_DOB",cust_DOB);
                                        CustomerBundle.putString("cust_Photo",cust_Photo);
                                        Bundle Customer=new Bundle();
                                        Customer.putBundle("CustomerBundle", CustomerBundle);
                                        System.out.println(Customer);
                                        NavHostFragment.findNavController(Fragment_Customer_Profile.this)
                                                .navigate(R.id.action_User_Details_to_Edit_Personal_info,Customer);
                                    }
                                });


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

    //GetCustomerSummary
    OnResponseListener GetCustomerSummary = new OnResponseListener()
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
                                final JSONObject customerProfile= resultSet.getJSONObject("customerSummary");

                                final String opened_Reservation=customerProfile.getString("opened_Reservation");
                                final String confirm_Reservation=customerProfile.getString("confirm_Reservation");
                                final String not_Show_Reservation=customerProfile.getString("not_Show_Reservation");
                                final String cancelled_Reservation=customerProfile.getString("cancelled_Reservation");
                                final String opened_Agreement=customerProfile.getString("opened_Agreement");
                                final String closed_Agreement=customerProfile.getString("closed_Agreement");
                                final String pending_Payment=customerProfile.getString("pending_Payment");
                                final String pending_Deposit=customerProfile.getString("pending_Deposit");
                                final String traffic_Ticket=customerProfile.getString("traffic_Ticket");
                                final String total_Revenue=customerProfile.getString("total_Revenue");

                                txt_OpenReservation.setText(opened_Reservation);
                                txt_OpenAgreement.setText(opened_Agreement);
                                txt_OpenTrafficticket.setText(traffic_Ticket);
                                txt_AccountBalance.setText(pending_Deposit);
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
}
