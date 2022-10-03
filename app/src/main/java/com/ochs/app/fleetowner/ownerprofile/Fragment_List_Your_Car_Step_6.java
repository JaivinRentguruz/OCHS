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

public class Fragment_List_Your_Car_Step_6 extends Fragment
{
    ImageView BackArrow;
    LinearLayout lblNext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_your_car_step_6, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        BackArrow=view.findViewById(R.id.back_to_step5);
        lblNext=view.findViewById(R.id.lblNextToVehList);

        BackArrow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(Fragment_List_Your_Car_Step_6.this)
                        .navigate(R.id.action_ListYourCar_6_to_ListYourCar_5);
            }
        });

        lblNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(Fragment_List_Your_Car_Step_6.this)
                        .navigate(R.id.action_ListYourCar_6_to_VehicleOnRent);
            }
        });
    }
}
