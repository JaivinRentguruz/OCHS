package com.ochs.app.flexiicar_app.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ochs.app.R;

public class Fragment_Payment_Reciept extends Fragment
{
    ImageView pdfimg,backarrow;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_receipt, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        pdfimg=view.findViewById(R.id.pdf_payment_reciept);
        backarrow=view.findViewById(R.id.back_To_trafficticket);
        pdfimg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               // NavHostFragment.findNavController(Fragment_Payment_Reciept.this)
                      //  .navigate(R.id.action_Payment_Reciept_to_Payment_Reciept_2);
            }
        });
        backarrow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               // NavHostFragment.findNavController(Fragment_Payment_Reciept.this)
                      //  .navigate(R.id.action_Payment_Reciept_to_Bills_and_Payment);
            }
        });
    }
}
