package com.ochs.app.fleetowner.ownerlogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;
import com.ochs.app.fleetowner.ownerprofile.Fleet_Owner_Profile;
import com.ochs.app.fleetowner.registration.Owner_Registration;

public class Fragment_Owner_login extends Fragment
{
    public static Context context;
    ProgressDialog Loading;
    public String id = "", role = "";
    TextView txtRegister,txtforgetpassword,txt_GuestUser,lbllogin;
    EditText editText_Username, editText_Password;
    Handler handler=new Handler();
    CheckBox chksavepass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            txtRegister = view.findViewById(R.id.txtRegister);
            editText_Username = view.findViewById(R.id.edt_username);
            editText_Password = view.findViewById(R.id.edt_password);
            txtforgetpassword = view.findViewById(R.id.txt_forgetPassword);
            lbllogin = view.findViewById(R.id.lbllogin);
            chksavepass = view.findViewById(R.id.checkboxSavePass);
            txt_GuestUser = view.findViewById(R.id.txt_GuestUser);
            txt_GuestUser.setVisibility(View.GONE);
            txtRegister.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), Owner_Registration.class);
                    startActivity(i);
                }
            });
            txtforgetpassword.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    NavHostFragment.findNavController(Fragment_Owner_login.this)
                            .navigate(R.id.action_LoginFragment_to_Forget_Password);
                }
            });


            lbllogin.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    if (editText_Username.getText().toString().equals(""))
                        Toast.makeText(getActivity(), "Please Enter UserName", Toast.LENGTH_LONG).show();

                    else if (editText_Password.getText().toString().equals(""))
                        Toast.makeText(getActivity(), "Please Enter Password", Toast.LENGTH_LONG).show();

                    Intent i = new Intent(getActivity(), Fleet_Owner_Profile.class);
                    startActivity(i);
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
