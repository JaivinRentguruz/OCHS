package com.ochs.app.flexiicar_app.commonfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;

public class Fragment_Company_Polices  extends Fragment
{
    LinearLayout submitlayout,AdditionalDriver_Desc;
    ImageView imgback,arrow_AdditionalDriver;
    Bundle BookingBundle,VehicleBundle;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_company_policies, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        imgback=view.findViewById(R.id.backarrow_terms);
        arrow_AdditionalDriver=view.findViewById(R.id.arrow_AdditionalDriver);
        AdditionalDriver_Desc=view.findViewById(R.id.AdditionalDriver_Desc);

        arrow_AdditionalDriver.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AdditionalDriver_Desc.setVisibility(View.VISIBLE);
            }
        });


        imgback.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Company_Polices.this)
                        .navigate(R.id.action_Company_Policies_to_Add_Wallet_Pass);
            }
        });


    }
}

