package com.ochs.app.fleetowner.ownerlogin;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.R;

public class Fragment_Forget_Password extends Fragment
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
                    NavHostFragment.findNavController(Fragment_Forget_Password.this)
                            .navigate(R.id.action_Forget_Password_to_LoginFragment);
                }
            });

            AndroidNetworking.initialize(getActivity());
            Fragment_Forget_Password.context = getActivity();

            lblVerify.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    try
                    {
                       // if (edt_Email.getText().toString().equals(""))
                           // Toast.makeText(getActivity(), "Please Enter Your Registered email ID.", Toast.LENGTH_LONG).show();
                       // else
                            //{
                            NavHostFragment.findNavController(Fragment_Forget_Password.this)
                                    .navigate(R.id.action_LoginFragment_to_REset_Password);
                        //}

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
}
