package com.ochs.app.flexiicar_app.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ochs.app.R;

public class Fragment_Traffic_Ticket extends Fragment
{
    ImageView pdfimg,backarrow;
    LinearLayout lblpay;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_traffic_ticket, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        pdfimg=view.findViewById(R.id.pdf_img);
        lblpay=view.findViewById(R.id.layout_pay1);
        backarrow=view.findViewById(R.id.back_to_billpayment);
        /*pdfimg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Traffic_Ticket.this)
                        .navigate(R.id.action_Traffic_Ticket_to_Traffic_Ticket_Image);
            }
        });
        lblpay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Traffic_Ticket.this)
                        .navigate(R.id.action_Traffic_Ticket_to_Traffic_Ticket_2);
            }
        });
        backarrow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Traffic_Ticket.this)
                        .navigate(R.id.action_Traffic_Ticket_to_Bills_and_Payment);
            }
        });*/
    }
}
