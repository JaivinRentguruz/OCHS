package com.ochs.app.fleetowner.ownerprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;

public class Fragment_VehicleonRent_List extends Fragment
{
    TextView AddVehicle;
    ImageView Back_Ownerprofile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_of_my_vehicle, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        AddVehicle=view.findViewById(R.id.AddVehicle);
        Back_Ownerprofile=view.findViewById(R.id.back_ownerprofile);

        Back_Ownerprofile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(Fragment_VehicleonRent_List.this)
                        .navigate(R.id.action_VehicleOnRent_to_FleetOwnerProfile);
            }
        });
        AddVehicle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(Fragment_VehicleonRent_List.this)
                        .navigate(R.id.action_VehicleOnRent_to_ListYourCar_1);
            }
        });
    }
}
