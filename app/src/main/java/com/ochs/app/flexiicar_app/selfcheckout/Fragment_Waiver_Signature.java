package com.ochs.app.flexiicar_app.selfcheckout;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.ochs.app.flexiicar_app.selfcheckin.Fragment_Waiver_Signature_For_SelfCheckIn;
import com.ochs.app.flexiicar_app.user.User_Profile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_CHECKIN;
import static com.ochs.app.apicall.ApiEndPoint.GETAGREEMENTREPORT;

public class Fragment_Waiver_Signature extends Fragment
{
    LinearLayout lblNext;
    ImageView imgback;
    WebView web_view;
    Bundle AgreementsBundle;
    String serverpath="";
    Handler handler = new Handler();
    TextView txtDateTime,txtDiscard;
    JSONArray ImageList = new JSONArray();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_waiver_signature, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        try{
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            ((User_Profile) getActivity()).BottomnavInVisible();

            AgreementsBundle= getArguments().getBundle("AgreementsBundle");
            web_view = view.findViewById(R.id.webview123);

            txtDateTime=view.findViewById(R.id.lbl_DateTime);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
            String datetime = dateformat.format(c.getTime());
            txtDateTime.setText(datetime);

            lblNext=view.findViewById(R.id.lblNEXT_WS);
            imgback=view.findViewById(R.id.Back);
            txtDiscard=view.findViewById(R.id.lblDiscard);
            txtDiscard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are You Sure You Want To Discard?");
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    NavHostFragment.findNavController(Fragment_Waiver_Signature.this)
                                            .navigate(R.id.action_Waiver_Signature_to_User_Details);
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
            lblNext.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle Agreements = new Bundle();
                    Agreements.putBundle("AgreementsBundle", AgreementsBundle);
                    System.out.println(Agreements);
                    NavHostFragment.findNavController(Fragment_Waiver_Signature.this)
                            .navigate(R.id.action_Waiver_Signature_to_Location_And_Key,Agreements);
                }
            });
            imgback.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
                        ImageList = new JSONArray(AgreementsBundle.getString("ImageList"));
                        Bundle Agreements = new Bundle();
                        Agreements.putBundle("AgreementsBundle", AgreementsBundle);
                        AgreementsBundle.putString("ImageList",ImageList.toString());
                        System.out.println(Agreements);
                        NavHostFragment.findNavController(Fragment_Waiver_Signature.this)
                                .navigate(R.id.action_Waiver_Signature_to_Acceptance_signature, Agreements);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            serverpath = sp.getString("serverPath", "");

            String bodyParam = "";
            try
            {
                bodyParam += "AgreementId=" + AgreementsBundle.getInt("agreement_ID");
                System.out.println(bodyParam);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            AndroidNetworking.initialize(getActivity());
            Fragment_Waiver_Signature_For_SelfCheckIn.context = getActivity();

            ApiService ApiService = new ApiService(GetAgreementReport, RequestType.GET,
                    GETAGREEMENTREPORT, BASE_URL_CHECKIN, new HashMap<String, String>(), bodyParam);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    OnResponseListener GetAgreementReport = new OnResponseListener()
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

                            JSONObject reports=resultSet.getJSONObject("reports");
                            String reportPdfPath=reports.getString("reportPdfPath");

                            String url1 = serverpath + reportPdfPath.substring(2);
                            System.out.println(url1);
                            web_view.setWebViewClient(new WebViewClient());
                            web_view.getSettings().setJavaScriptEnabled(true);
                            web_view.getSettings().setLoadWithOverviewMode(true);
                            web_view.getSettings().setUseWideViewPort(true);
                            web_view.getSettings().setAllowFileAccess(true);
                            web_view.loadUrl("https://docs.google.com/gview?embedded=true&url="+url1);
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
