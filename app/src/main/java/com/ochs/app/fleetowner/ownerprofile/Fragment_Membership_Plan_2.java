package com.ochs.app.fleetowner.ownerprofile;

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

public class Fragment_Membership_Plan_2 extends Fragment
{
    ImageView back;
    LinearLayout lbl_savePlan;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_membership_plan_2, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        back=view.findViewById(R.id.back_to_plan1);
        lbl_savePlan=view.findViewById(R.id.lbl_savePlan);

        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(Fragment_Membership_Plan_2.this)
                        .navigate(R.id.action_FleetOwnerProfile_to_MemberShipPlan1);
            }
        });
        lbl_savePlan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(Fragment_Membership_Plan_2.this)
                        .navigate(R.id.action_FleetOwnerProfile_to_MemberShipPlan1);
            }
        });
    }
}
