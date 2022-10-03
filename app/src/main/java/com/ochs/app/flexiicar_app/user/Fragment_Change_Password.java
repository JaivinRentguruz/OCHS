package com.ochs.app.flexiicar_app.user;

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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

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

public class Fragment_Change_Password extends Fragment
{
    LinearLayout Save_Password;
    ImageView backarrowimg;
    Handler handler = new Handler();
    public static Context context;
    public String CurrentPass = "",id="";
    EditText Edt_Currentpassword,edt_NewPassword,edt_ConfirmPassword;
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
        return inflater.inflate(R.layout.fragment_changepassword, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ((User_Profile) getActivity()).BottomnavInVisible();

        SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
        id = sp.getString(getString(R.string.id), "");

        backarrowimg = view.findViewById(R.id.backimg_changepass);
        Edt_Currentpassword = view.findViewById(R.id.edt_CurrentPass);
        edt_NewPassword = view.findViewById(R.id.edt_NewPass1);
        edt_ConfirmPassword = view.findViewById(R.id.edt_ConfirmPass1);
        Save_Password = view.findViewById(R.id.lbl_SavePassword);
        backarrowimg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Change_Password.this)
                        .navigate(R.id.action_ChangePass_to_User_Details);
            }
        });

        Save_Password.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final String password = edt_NewPassword.getText().toString();
                final String confirmPassword = edt_ConfirmPassword.getText().toString();

                if (Edt_Currentpassword.getText().toString().equals(""))
                {
                    Edt_Currentpassword.requestFocus();
                    Edt_Currentpassword.setError("Enter Your Current Password");
                }
                else if(password.equals(""))
                {
                    edt_NewPassword.requestFocus();
                    edt_NewPassword.setError("Enter Your New Password");
                }
                else if (!PASSWORD_PATTERN.matcher(password).matches())
                {
                    edt_NewPassword.requestFocus();
                    edt_NewPassword.setError("Password must be of 8 character,include one uppercase and lowercase letter and Number");
                }
                else if(confirmPassword.equals(""))
                {
                    edt_ConfirmPassword.requestFocus();
                    edt_ConfirmPassword.setError("Enter Your Confirm Password");
                }
                else {
                    if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {
                        if (password.equals(confirmPassword))
                        {
                            JSONObject bodyParam = new JSONObject();
                            try {
                                bodyParam.accumulate("CustomerId", Integer.parseInt(id));
                                bodyParam.accumulate("PasswordHash", edt_ConfirmPassword.getText().toString());
                                bodyParam.accumulate("CurrentPasswordHash", Edt_Currentpassword.getText().toString());
                                bodyParam.accumulate("Type", 1);
                                System.out.println(bodyParam);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApiService ApiService = new ApiService(ChangePassword, RequestType.POST,
                                    CHANGEPASSWORD, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);

                        } else {
                            CustomToast.showToast(getActivity(), "Your Current Password And Confirmation Password Do Not Match", 1);
                        }
                    }
                }
            }
        });
    }
    //REGISTRATION
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

                            NavHostFragment.findNavController(Fragment_Change_Password.this)
                                    .navigate(R.id.action_ChangePass_to_User_Details);
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