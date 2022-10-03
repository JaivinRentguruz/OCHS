package com.ochs.app.fleetowner.ownerprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;

public class Fragment_List_Your_Car_Step_4 extends Fragment
{
    LinearLayout lblNext,AddLocation;
    TextView txt_City,txt_street,txt_State,txt_zip,txt_Country;
    Boolean FromMap;
    ImageView BackArrow;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_your_car_step_4, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        try {
            super.onViewCreated(view, savedInstanceState);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            lblNext = view.findViewById(R.id.lbl_next4);
            AddLocation = view.findViewById(R.id.lbl_addLocation);

            txt_City = view.findViewById(R.id.txt_City);
            txt_street = view.findViewById(R.id.lbl_street);
            txt_State = view.findViewById(R.id.txt_State);
            txt_zip = view.findViewById(R.id.lbl_zip);
            txt_Country=view.findViewById(R.id.txt_Country);
            FromMap = getArguments().getBoolean("FromMap");
            BackArrow=view.findViewById(R.id.back_to_step3);

            lblNext.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle Location = new Bundle();
                    Location.putBoolean("FromMap",false);
                    System.out.println(Location);
                    NavHostFragment.findNavController(Fragment_List_Your_Car_Step_4.this)
                            .navigate(R.id.action_ListYourCar_4_to_ListYourCar_5,Location);
                }
            });
            AddLocation.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    NavHostFragment.findNavController(Fragment_List_Your_Car_Step_4.this)
                            .navigate(R.id.action_ListYourCar_4_to_MapScreenSearch);
                }
            });
            BackArrow.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    NavHostFragment.findNavController(Fragment_List_Your_Car_Step_4.this)
                            .navigate(R.id.action_ListYourCar_4_to_ListYourCar_3);
                }
            });
            if (FromMap)
            {
                txt_City.setText(getArguments().getString("City"));
                txt_street.setText(getArguments().getString("Street"));
                txt_State.setText(getArguments().getString("State"));
                txt_zip.setText(getArguments().getString("PostalCode"));
                txt_Country.setText(getArguments().getString("Country"));
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
