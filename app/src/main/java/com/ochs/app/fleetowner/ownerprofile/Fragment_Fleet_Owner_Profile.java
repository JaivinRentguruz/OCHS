package com.ochs.app.fleetowner.ownerprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;

public class Fragment_Fleet_Owner_Profile extends Fragment
{
    LinearLayout MemberShipPlan,VehicleOnRent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fleet_owner_profile, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        MemberShipPlan=view.findViewById(R.id.lbl_Membershipplan);
        VehicleOnRent=view.findViewById(R.id.vehicle_OnRent);

        MemberShipPlan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(Fragment_Fleet_Owner_Profile.this)
                        .navigate(R.id.action_FleetOwnerProfile_to_MemberShipPlan1);
            }
        });

        VehicleOnRent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(Fragment_Fleet_Owner_Profile.this)
                        .navigate(R.id.action_FleetOwnerProfile_to_VehicleOnRent);
            }
        });
    }
}
