package com.ochs.app.flexiicar_app.login;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONObject;

import java.util.HashMap;

import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_CUSTOMER;
import static com.ochs.app.apicall.ApiEndPoint.FORGETPASSWORD;

public class Fragment_Forgot_Password extends Fragment
{
    ImageView BackToLogin;
    EditText edt_Email;
    public String Cust_Email = "";
    Handler handler = new Handler();
    public static Context context;
    String bodyParam = "";
    TextView lblVerify;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        try {
            lblVerify = view.findViewById(R.id.lblVerify);
            BackToLogin = view.findViewById(R.id.BackToLogin);
            edt_Email = view.findViewById(R.id.edt_forgetemail);

            BackToLogin.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    NavHostFragment.findNavController(Fragment_Forgot_Password.this)
                            .navigate(R.id.action_Forgot_Password_to_LoginFragment);
                }
            });

            AndroidNetworking.initialize(getActivity());
            Fragment_Forgot_Password.context = getActivity();

            lblVerify.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    try
                    {
                       if (edt_Email.getText().toString().equals(""))
                       {
                            CustomToast.showToast(getActivity(), "Please enter a email", 1);
                       }
                       else if (!Patterns.EMAIL_ADDRESS.matcher(edt_Email.getText().toString()).matches())
                       {
                           edt_Email.setError("Enter valid Email address");
                           edt_Email.requestFocus();
                       }
                        else
                            {
                            bodyParam="regEmail="+edt_Email.getText().toString();
                            ApiService ApiService = new ApiService(ForgetPassword, RequestType.GET,
                                    FORGETPASSWORD, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    OnResponseListener ForgetPassword = new OnResponseListener()
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
                            String msg = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),msg,0);

                            NavHostFragment.findNavController(Fragment_Forgot_Password.this)
                                    .navigate(R.id.action_Forgot_Password_to_Reset_Password);
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
