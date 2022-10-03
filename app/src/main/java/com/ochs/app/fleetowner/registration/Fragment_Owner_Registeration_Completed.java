package com.ochs.app.fleetowner.registration;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ochs.app.R;

public class Fragment_Owner_Registeration_Completed extends Fragment
{
    LinearLayout LayoutNotNow,AllowAccess,lblexplore;
    RelativeLayout relativeLayout;
    int count = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_completed, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        lblexplore = view.findViewById(R.id.lblexplore);
        relativeLayout=view.findViewById(R.id.sucessfull_regi);
        AllowAccess=view.findViewById(R.id.AllowAccess);
        LayoutNotNow=view.findViewById(R.id.layoutNotNow);
        lblexplore.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lblexplore.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        });
        LayoutNotNow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                relativeLayout.setVisibility(View.GONE);
                lblexplore.setVisibility(View.VISIBLE);
            }
        });
        AllowAccess.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION))
                    {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                    else
                        {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                }
                else
                    {
                        Toast.makeText(getActivity(),"Permission is already granted.", Toast.LENGTH_SHORT).show();
                       // Intent i = new Intent(getActivity(), Booking_Activity.class);
                      //  i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       // startActivity(i);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
            {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }
}
