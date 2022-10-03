package com.ochs.app.flexiicar_app.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;

import org.json.JSONObject;

import java.util.regex.Pattern;

public class Fragment_Driver_Profile_3 extends Fragment
{
    ImageView BackToRegister2;
    EditText edt_CustPhoneNo,edt_CustEmailId,edt_CustPassWord,edt_CustConformPass;
    CheckBox checkBoxTC,ChkConsentMail;
    Bundle RegistrationBundle;
    TextView lblDiscard,lblNext;
    private static final Pattern PASSWORD_PATTERN =Pattern.compile("^"+
            "(?=.*[0-9])"+
            "(?=.*[a-z])"+
            "(?=.*[A-Z])"+
            ".{8,20}"+
            "$");

    Boolean BackTo;
    // "(?=.*[@#$%^&+=])"+
    //        "(?=\\S+$)"+
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver_profile_3, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        lblNext = view.findViewById(R.id.lblNext3);
        BackToRegister2 =view.findViewById(R.id.BackToRegister2);
        edt_CustPhoneNo=view.findViewById(R.id.edt_CustPhoneNo);
        edt_CustEmailId=view.findViewById(R.id.edt_CustEmailId);
        edt_CustPassWord=view.findViewById(R.id.edt_CustPassWord);
        edt_CustConformPass=view.findViewById(R.id.edt_CustConformPass);
        checkBoxTC=view.findViewById(R.id.CheckboxtTC);
        lblDiscard=view.findViewById(R.id.lblDiscard3);
        ChkConsentMail=view.findViewById(R.id.ChkConsentMail);

        RegistrationBundle = getArguments().getBundle("RegistrationBundle");
        BackTo = getArguments().getBoolean("BackTo");

        if(BackTo)
        {
            String Cust_MobileNo=RegistrationBundle.getString("Cust_MobileNo");
            edt_CustPhoneNo.setText(Cust_MobileNo);

            String Cust_Email=RegistrationBundle.getString("Cust_Email");
            edt_CustEmailId.setText(Cust_Email);

            String PasswordHash=RegistrationBundle.getString("PasswordHash");
            edt_CustPassWord.setText(PasswordHash);
            edt_CustConformPass.setText(PasswordHash);

        }

        lblNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try {
                    final String newpassword = edt_CustPassWord.getText().toString();

                    if (edt_CustPhoneNo.getText().toString().equals(""))
                        CustomToast.showToast(getActivity(),"Please Enter Your Mobile No.",1);
                    else if (edt_CustEmailId.getText().toString().equals(""))
                        CustomToast.showToast(getActivity(),"Please Enter Your Email",1);
                    else if (!Patterns.EMAIL_ADDRESS.matcher(edt_CustEmailId.getText().toString()).matches())
                    {
                        edt_CustEmailId.setError("Enter valid Email address");
                        edt_CustEmailId.requestFocus();
                    }
                    else if (edt_CustPassWord.getText().toString().equals(""))
                        CustomToast.showToast(getActivity(),"Please Enter Your Password",1);
                    else if (!PASSWORD_PATTERN.matcher(newpassword).matches())
                    {
                        edt_CustPassWord.setError("Password must be of 8 character,include one uppercase and lowercase letter and Number");
                        //"Must contain at least one number and one uppercase and lowercase letter, and at least 8 or more characters"
                        edt_CustPassWord.requestFocus();
                    }
                    else if (edt_CustConformPass.getText().toString().equals(""))
                        CustomToast.showToast(getActivity(),"Please Enter Conform Password",1);

                    else {
                        final String password = edt_CustPassWord.getText().toString();
                        final String confirmPassword = edt_CustConformPass.getText().toString();

                        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword))
                        {
                            if (password.equals(confirmPassword))
                            {
                                if(ChkConsentMail.isChecked())
                                {
                                    if (checkBoxTC.isChecked())
                                    {
                                        RegistrationBundle.putString("Cust_MobileNo", edt_CustPhoneNo.getText().toString());
                                        RegistrationBundle.putString("Cust_Email", edt_CustEmailId.getText().toString());
                                        RegistrationBundle.putString("PasswordHash", edt_CustConformPass.getText().toString());
                                        Bundle Registration = new Bundle();
                                        Registration.putBundle("RegistrationBundle", RegistrationBundle);
                                        System.out.println(Registration);
                                        NavHostFragment.findNavController(Fragment_Driver_Profile_3.this)
                                                .navigate(R.id.action_DriverProfile3_to_DriverProfile4, Registration);
                                    }
                                    else {
                                        String msg = "Please accept term & condition";
                                        CustomToast.showToast(getActivity(), msg, 1);
                                    }
                                }
                                else {
                                    String msg = "Please give consent to receive an email";
                                    CustomToast.showToast(getActivity(), msg, 1);
                                }
                            }
                            else
                            {
                                CustomToast.showToast(getActivity(),"Your password and confirmation password do not match",1);
                            }
                        }
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        BackToRegister2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try {
                    Bundle Registration = new Bundle();
                    Registration.putBundle("RegistrationBundle", RegistrationBundle);
                    Registration.putBoolean("BackTo", true);
                    System.out.println(Registration);
                    NavHostFragment.findNavController(Fragment_Driver_Profile_3.this)
                            .navigate(R.id.action_DriverProfile3_to_DriverProfile2, Registration);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        lblDiscard.setOnClickListener(new View.OnClickListener()
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
                                Intent i = new Intent(getActivity(), Login.class);
                                startActivity(i);
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
    }
}

