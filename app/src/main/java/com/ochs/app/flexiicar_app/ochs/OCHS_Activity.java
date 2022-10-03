package com.ochs.app.flexiicar_app.ochs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ochs.app.R;

public class OCHS_Activity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_ochs);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}