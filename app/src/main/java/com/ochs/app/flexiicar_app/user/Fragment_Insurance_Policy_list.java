package com.ochs.app.flexiicar_app.user;

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
import com.ochs.app.adapters.Helper;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import com.ochs.app.flexiicar_app.booking.Fragment_Payment_checkout;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_CUSTOMER;
import static com.ochs.app.apicall.ApiEndPoint.GETCUSTOMERINSURANCE;

public class Fragment_Insurance_Policy_list extends Fragment
{
    Handler handler = new Handler();
    public static Context context;
    public String id = "";
    ImageLoader imageLoader;
    String serverpath = "";
    ImageView Back;
    Bundle bundle = new Bundle();
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
        return inflater.inflate(R.layout.fragment_customer_insurance_list, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (getArguments().getInt("key") != 1) {
            ((User_Profile) getActivity()).BottomnavVisible();
        }
        bundle.putInt("key", getArguments().getInt("key"));
        initImageLoader(getActivity());
        imageLoader = ImageLoader.getInstance();

        SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
        id = sp.getString(getString(R.string.id), "");

        Back=view.findViewById(R.id.Back);
        Back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (getArguments().getInt("key") == 1) {
                    NavHostFragment.findNavController(Fragment_Insurance_Policy_list.this).popBackStack();
                } else {
                    NavHostFragment.findNavController(Fragment_Insurance_Policy_list.this)
                            .navigate(R.id.action_InsurancePolicyList_to_User_Details);
                }
            }
        });

        String bodyParam = "";
        try {
            bodyParam += "customerId=" + id;

            System.out.println(bodyParam);
        } catch (Exception e) {
            e.printStackTrace();
        }


            AndroidNetworking.initialize(getActivity());
            Fragment_Payment_checkout.context = getActivity();

            ApiService ApiService = new ApiService(GetCustomerInsurance, RequestType.GET,
                    GETCUSTOMERINSURANCE, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);
    }

    //GetCustomerInsurance
    OnResponseListener GetCustomerInsurance = new OnResponseListener()
    {
        @Override
        public void onSuccess(final String response, HashMap<String, String> headers)
        {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Success");
                        System.out.println(response);

                        JSONObject responseJSON = new JSONObject(response);
                        Boolean status = responseJSON.getBoolean("status");

                        if (status)
                        {
                            JSONObject resultSet = responseJSON.getJSONObject("resultSet");
                            final JSONArray getcustomerInsurance = resultSet.getJSONArray("customerInsurance");
                            final RelativeLayout rlInsurancePolicy = getActivity().findViewById(R.id.rl_InsurancePolicyList);

                            final JSONArray getInsuranceDoc = resultSet.getJSONArray("t0050_Documents");


                            int len,len1;
                            len = getcustomerInsurance.length();
                            len1 = getInsuranceDoc.length();

                            for (int j = 0; j < len; j++)
                            {
                                final JSONObject test = (JSONObject) getcustomerInsurance.get(j);

                                final int insurance_Cmp_ID = test.getInt("insurance_Cmp_ID");
                                final String insurance_Cmp_Name = test.getString("insurance_Cmp_Name");
                                final String policy_No = test.getString("policy_No");
                                final String expiryDate = test.getString("expiryDate");
                                final String insIssueDate = test.getString("insIssueDate");
                                final String imageFile = test.getString("imageList");

                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                lp.setMargins(0, 10, 0, 0);

                                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.list_customer_insurance, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                linearLayout.setId(200 + j);
                                linearLayout.setLayoutParams(lp);

                                final JSONArray docArray = new JSONArray();

                                for(int i = 0; i < len1; i++)
                                {
                                    final JSONObject test1 = (JSONObject) getInsuranceDoc.get(i);

                                    final String doc_Details = test1.getString("doc_Details");
                                    final String doc_Name = test1.getString("doc_Name");

                                    JSONObject docObj = new JSONObject();
                                    docObj.put("doc_Name", doc_Name);
                                    docObj.put("doc_Details", doc_Details);
                                    docArray.put(docObj);
                                }

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                                Date date = dateFormat.parse(expiryDate);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                String strexpiryDate = sdf.format(date);

                                SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                                Date date1 = dateFormat1.parse(insIssueDate);
                                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
                                String strinsIssueDate = sdf1.format(date1);

                                TextView lbl_policy_No=linearLayout.findViewById(R.id.lbl_policy_No);
                                TextView lbl_insurance_Cmp_Name=linearLayout.findViewById(R.id.lbl_insurance_Cmp_Name);
                                TextView ExpiryDate=linearLayout.findViewById(R.id.lbl_ExpiryDateIns);
                                TextView IssueDate=linearLayout.findViewById(R.id.lbl_IssueDateIns);

                                TextView lbl_primaryName=linearLayout.findViewById(R.id.lbl_primaryName);
                                TextView lbl_TelephoneNo=linearLayout.findViewById(R.id.lbl_TelephoneNo);
                                TextView lbl_UserEmail=linearLayout.findViewById(R.id.lbl_UserEmail);

                                TextView View_InsDetails=linearLayout.findViewById(R.id.View_InsDetails);

                                SharedPreferences sp1 = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
                                String cust_FName = sp1.getString("cust_FName", "");
                                String cust_LName = sp1.getString("cust_LName", "");
                                String cust_Email = sp1.getString("cust_Email", "");
                                String cust_MobileNo = sp1.getString("cust_MobileNo", "");

                                lbl_primaryName.setText(cust_FName+" "+cust_LName);
                                lbl_TelephoneNo.setText("Tel: "+cust_MobileNo);
                                lbl_UserEmail.setText("Email: "+cust_Email);

                                LinearLayout View_InsurancePolicy=linearLayout.findViewById(R.id.llInsurancePolicy);

                                View_InsurancePolicy.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        Bundle InsurancePolicyBundle = new Bundle();
                                        InsurancePolicyBundle.putString("insurance_Cmp_Name", insurance_Cmp_Name);
                                        InsurancePolicyBundle.putString("policy_No", policy_No);
                                        InsurancePolicyBundle.putString("expiryDate", expiryDate);
                                        InsurancePolicyBundle.putString("insIssueDate", insIssueDate);
                                        InsurancePolicyBundle.putString("docArray", docArray.toString());
                                        InsurancePolicyBundle.putInt("key", getArguments().getInt("key"));
                                        Bundle InsurancePolicy = new Bundle();
                                        InsurancePolicy.putBundle("InsurancePolicyBundle", InsurancePolicyBundle);
                                        if (getArguments().getInt("key") == 1){
                                            Helper.bundle = InsurancePolicyBundle;
                                            Helper.insuranceModel.insurance_Cmp_Name=insurance_Cmp_Name;
                                            Helper.insuranceModel.expiryDate=strexpiryDate;
                                            Helper.insuranceModel.insIssueDate=insIssueDate;
                                            Helper.insuranceModel.policy_No=policy_No;
                                            Helper.insuranceModel.insurancecompanyid = insurance_Cmp_ID;
                                            Helper.VISIBLE = true;
                                            Helper.insuranceValue = true;
                                            NavHostFragment.findNavController(Fragment_Insurance_Policy_list.this).popBackStack();
                                                    //.navigate(R.id.action_InsurancePolicyList_to_Select_addtional_options,InsurancePolicy);
                                        } else {
                                            NavHostFragment.findNavController(Fragment_Insurance_Policy_list.this)
                                                    .navigate(R.id.action_InsurancePolicyList_to_InsurancePolicyUpdate, InsurancePolicy);
                                        }
                                    }
                                });

                                if (getArguments().getInt("key") == 1) {
                                    View_InsDetails.setText("Edit");
                                }

                                View_InsDetails.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                            Bundle InsurancePolicyBundle = new Bundle();
                                            InsurancePolicyBundle.putString("insurance_Cmp_Name", insurance_Cmp_Name);
                                            InsurancePolicyBundle.putString("policy_No", policy_No);
                                            InsurancePolicyBundle.putString("expiryDate", expiryDate);
                                            InsurancePolicyBundle.putString("insIssueDate", insIssueDate);
                                            InsurancePolicyBundle.putString("docArray", docArray.toString());
                                            Bundle InsurancePolicy = new Bundle();
                                            InsurancePolicy.putBundle("InsurancePolicyBundle", InsurancePolicyBundle);
                                            InsurancePolicy.putInt("key",getArguments().getInt("key"));
                                            NavHostFragment.findNavController(Fragment_Insurance_Policy_list.this)
                                                    .navigate(R.id.action_InsurancePolicyList_to_InsurancePolicyUpdate, InsurancePolicy);
                                    }
                                });

                                if (policy_No.length()>0) {
                                    lbl_insurance_Cmp_Name.setText("Company Name: " + insurance_Cmp_Name);
                                    lbl_policy_No.setText("Number: " + policy_No);
                                    ExpiryDate.setText("Exp Date: " + strexpiryDate);
                                    IssueDate.setText("Issue Date: " + strinsIssueDate);
                                } else {
                                    lbl_insurance_Cmp_Name.setText("Company Name: " + insurance_Cmp_Name);
                                    lbl_policy_No.setText("Number: " + policy_No);
                                    ExpiryDate.setText("Exp Date: "  );
                                    IssueDate.setText("Issue Date: ");
                                    View_InsDetails.setText("ADD");
                                }

                                rlInsurancePolicy.addView(linearLayout);
                            }

                        } else {
                            String errorString = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),errorString,1);
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