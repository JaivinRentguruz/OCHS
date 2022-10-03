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

public class Fragment_List_Your_Car_Step_5 extends Fragment
{
    LinearLayout lblNext,lbl_Equipment;
    ImageView BackArrow;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_your_car_step_5, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        lblNext=view.findViewById(R.id.lbl_Next5);
        lbl_Equipment=view.findViewById(R.id.lbl_Equipment);
        BackArrow=view.findViewById(R.id.back_to_step4);
        lbl_Equipment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(Fragment_List_Your_Car_Step_5.this)
                        .navigate(R.id.action_ListYourCar_5_to_ListYourCar_5_1);
            }
        });
        lblNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(Fragment_List_Your_Car_Step_5.this)
                        .navigate(R.id.action_ListYourCar_5_to_ListYourCar_6);
            }
        });
        BackArrow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Boolean FromMap = getArguments().getBoolean("FromMap");
                Bundle Location = new Bundle();
                Location.putBoolean("FromMap",FromMap);
                System.out.println(Location);
                NavHostFragment.findNavController(Fragment_List_Your_Car_Step_5.this)
                        .navigate(R.id.action_ListYourCar_5_to_ListYourCar_4,Location);
            }
        });

    }
}
