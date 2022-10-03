package com.ochs.app.flexiicar_app.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_CUSTOMER;
import static com.ochs.app.apicall.ApiEndPoint.CHANGEPASSWORD;

public class Fragment_Reset_Password extends Fragment
{
    ImageView Back;
    EditText ResetCode,NewPassword,ConfirmPassword;
    TextView lblResetPass;
    public String id="";
    Handler handler = new Handler();
    public static Context context;
    private static final Pattern PASSWORD_PATTERN =Pattern.compile("^"+
            "(?=.*[0-9])"+
            "(?=.*[a-z])"+
            "(?=.*[A-Z])"+
            ".{8,20}"+
            "$");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Back = view.findViewById(R.id.backTofrogetpass);
        ResetCode = view.findViewById(R.id.edt_ResetCode);
        NewPassword = view.findViewById(R.id.NEWPASSWORD);
        ConfirmPassword = view.findViewById(R.id.CONFIRMPASS);
        lblResetPass=view.findViewById(R.id.lblResetPass);

        Back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Reset_Password.this)
                        .navigate(R.id.action_Reset_Password_to_Forgot_Password);
            }
        });

        SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
        id = sp.getString(getString(R.string.id), "");

        AndroidNetworking.initialize(getActivity());

        lblResetPass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final String Newpassword = NewPassword.getText().toString();
                final String confirmPassword = ConfirmPassword.getText().toString();

                if(ResetCode.getText().toString().equals(""))
                {
                    ResetCode.requestFocus();
                    ResetCode.setError("Enter reset code");
                }
                else if(Newpassword.equals(""))
                {
                    NewPassword.requestFocus();
                    NewPassword.setError("Enter Your New Password");
                }
                else if (!PASSWORD_PATTERN.matcher(Newpassword).matches())
                {
                    NewPassword.setError("Password must be of 8 character,include one uppercase and lowercase letter and Number");
                }
                else if(confirmPassword.equals(""))
                {
                    ConfirmPassword.requestFocus();
                    ConfirmPassword.setError("Password can't be empty");
                }
                else
                {
                    try {
                        if (!TextUtils.isEmpty(Newpassword) && !TextUtils.isEmpty(confirmPassword))
                        {
                            if (Newpassword.equals(confirmPassword))
                            {
                                JSONObject bodyParam = new JSONObject();
                                try {
                                  //  bodyParam.accumulate("CustomerId", Integer.parseInt(id));
                                    bodyParam.accumulate("PasswordHash", NewPassword.getText().toString());
                                    bodyParam.accumulate("CurrentPasswordHash", ConfirmPassword.getText().toString());
                                    bodyParam.accumulate("Type", 2);
                                    System.out.println(bodyParam);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApiService ApiService = new ApiService(ChangePassword, RequestType.POST,
                                        CHANGEPASSWORD, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);
                            } else {
                                CustomToast.showToast(getActivity(), "Your New Password and confirmation password do not match", 1);
                            }
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //ChangePassword
    OnResponseListener ChangePassword = new OnResponseListener()
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
                            String msg = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),msg,0);
                            NavHostFragment.findNavController(Fragment_Reset_Password.this)
                                    .navigate(R.id.action_Reset_Password_to_LoginFragment);
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
