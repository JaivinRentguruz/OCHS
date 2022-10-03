package com.ochs.app.flexiicar_app.commonfragment;

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

public class Fragment_Add_Wallet_Pass extends Fragment
{
    LinearLayout submitlayout;
    ImageView imgback;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_wallet_pass, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


       /*
        submitlayout=view.findViewById(R.id.lblsubmit);
        imgback=view.findViewById(R.id.backarrow);
        submitlayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Add_Wallet_Pass.this)
                        .navigate(R.id.action_Add_Wallet_Pass_to_Company_Policies);
            }
        });
        imgback.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });*/

    }
}
