package com.ochs.app.fleetowner.ownerlogin;

import android.content.Context;
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


public class Fragment_Owner_Reset_Password extends Fragment
{
    ImageView backarrowimg;
    EditText ResetCode,NewPassword,ConfirmPassword;
    LinearLayout lblResetPass;
    public String id="";
    Handler handler = new Handler();
    public static Context context;
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

        backarrowimg = view.findViewById(R.id.backTofrogetpass);
        ResetCode = view.findViewById(R.id.edt_ResetCode);
        NewPassword = view.findViewById(R.id.NEWPASSWORD);
        ConfirmPassword = view.findViewById(R.id.CONFIRMPASS);
        lblResetPass=view.findViewById(R.id.lblResetPass);

        backarrowimg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Owner_Reset_Password.this)
                        .navigate(R.id.action_Reset_Password_to_Forgot_Password);
            }
        });
        lblResetPass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final String Newpassword = NewPassword.getText().toString();
                final String confirmPassword = ConfirmPassword.getText().toString();
                if (!TextUtils.isEmpty(Newpassword) && !TextUtils.isEmpty(confirmPassword))
                {
                    if (Newpassword.equals(confirmPassword))
                    {
                        NavHostFragment.findNavController(Fragment_Owner_Reset_Password.this)
                                .navigate(R.id.action_Reset_Password_to_LoginFragment);
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Your New Password and confirmation password do not match", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
