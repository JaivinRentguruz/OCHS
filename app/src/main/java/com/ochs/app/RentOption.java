package com.ochs.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.ochs.app.adapters.CustomToast;
import com.ochs.app.fleetowner.ownerlogin.FleetOwner_Login;
import com.ochs.app.flexiicar_app.login.Login;

public class RentOption extends AppCompatActivity
{

    LinearLayout lblRentCar,lblRentMyCar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_option);

        lblRentCar=findViewById(R.id.lblRentCar);
        lblRentMyCar=findViewById(R.id.lblRentMyCar);

        lblRentCar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(RentOption.this, Login.class);
                startActivity(intent);
            }
        });

        lblRentMyCar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(RentOption.this, FleetOwner_Login.class);
                startActivity(intent);
            }
        });
        if (ActivityCompat.checkSelfPermission(RentOption.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(RentOption.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(RentOption.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(RentOption.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(RentOption.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        try {
            switch (requestCode)
            {
                case LOCATION_REQUEST_CODE:
                {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {
                        binding.mapView.getMapAsync(this);
                    } else
                    {
                        CustomToast.showToast(getActivity(),"Please provide the permission",1);
                    }
                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}