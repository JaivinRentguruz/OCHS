package com.ochs.app.flexiicar_app.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ochs.app.R;
import com.ochs.app.flexiicar_app.booking.Booking_Activity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class User_Profile extends AppCompatActivity
{

    LinearLayout home_icon;
    ArrayList<String> scanData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__profile);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        ImageView usericon=findViewById(R.id.usericon);
        usericon.setImageResource(R.drawable.ic_tab_user_icon_green);

        home_icon=findViewById(R.id.home_icon);
        home_icon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent1=(new Intent(getApplicationContext(), Booking_Activity.class));
                startActivity(intent1);
            }
        });


        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19)
        { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        }

        else if(Build.VERSION.SDK_INT >= 19)
        {

            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
        try {
            scanData = getIntent().getStringArrayListExtra("scanData");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void BottomnavVisible()
    {
        LinearLayout lblcontinue1 = (LinearLayout) findViewById(R.id.lblcontinue1);
        lblcontinue1.setVisibility(View.VISIBLE);

        LinearLayout MainFragment = (LinearLayout) findViewById(R.id.MainFragment);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) MainFragment.getLayoutParams();
        params.setMargins(0, 0, 0, 115);
        MainFragment.setLayoutParams(params);
    }

    public void BottomnavInVisible()
    {
        LinearLayout lblcontinue1 = (LinearLayout) findViewById(R.id.lblcontinue1);
        lblcontinue1.setVisibility(View.GONE);

        LinearLayout MainFragment = (LinearLayout) findViewById(R.id.MainFragment);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) MainFragment.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        MainFragment.setLayoutParams(params);
    }
}