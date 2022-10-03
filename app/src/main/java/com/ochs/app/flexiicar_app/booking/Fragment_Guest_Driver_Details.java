package com.ochs.app.flexiicar_app.booking;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;

import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_Guest_Driver_Details extends Fragment
{
    Button lblLogin;
    ImageView Back;
    Bundle BookingBundle, VehicleBundle;
    Bundle returnLocationBundle, locationBundle;
    Boolean locationType, initialSelect;
    EditText FirstName,LastName,Email,ContactNo;
    TextView lblDiscard,lblSubmit;
    Handler handler = new Handler();
    public String id = "", role = "";
    CheckBox checkBox;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guest_driver_details, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            lblSubmit = view.findViewById(R.id.lblSubmit);
            Back = view.findViewById(R.id.Back);
            FirstName=view.findViewById(R.id.GuestDriverName);
            LastName=view.findViewById(R.id.GuestDriverLastName);
            Email=view.findViewById(R.id.GuestDriverEmail);
            ContactNo=view.findViewById(R.id.GuestDriverPhoneNo);
            checkBox=view.findViewById(R.id.Checkbox);

            lblDiscard=view.findViewById(R.id.lblDiscard);

            lblLogin=view.findViewById(R.id.LoginBtn);

            BookingBundle = getArguments().getBundle("BookingBundle");
            VehicleBundle = getArguments().getBundle("VehicleBundle");
            returnLocationBundle = getArguments().getBundle("returnLocation");
            locationBundle = getArguments().getBundle("location");
            locationType = getArguments().getBoolean("locationType");
            initialSelect = getArguments().getBoolean("initialSelect");

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            id = sp.getString(getString(R.string.id), "");

            lblSubmit.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
                        if (FirstName.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(), "Enter First Name.", 1);
                        else if (LastName.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(), "Enter Last Name.", 1);
                        else if (Email.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(), "Enter Your Email.", 1);
                        else if (!Patterns.EMAIL_ADDRESS.matcher(Email.getText().toString()).matches())
                        {
                            Email.setError("Enter valid Email address");
                            Email.requestFocus();
                        }
                        else if (ContactNo.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(), "Enter Your Contact No.", 1);
                        else {
                            if(checkBox.isChecked())
                            {
                                JSONObject GuestDriverModel = new JSONObject();
                                GuestDriverModel.put("firstName", FirstName.getText().toString());
                                GuestDriverModel.put("lastName", FirstName.getText().toString());
                                GuestDriverModel.put("driverEmail", Email.getText().toString());
                                GuestDriverModel.put("driverPhoneNo", ContactNo.getText().toString());

                                Bundle Booking = new Bundle();
                                BookingBundle.putInt("BookingStep", 4);
                                Booking.putBundle("BookingBundle", BookingBundle);
                                Booking.putBundle("VehicleBundle", VehicleBundle);
                                Booking.putBundle("returnLocation", returnLocationBundle);
                                Booking.putBundle("location", locationBundle);
                                Booking.putBoolean("locationType", locationType);
                                Booking.putBoolean("initialSelect", initialSelect);
                                BookingBundle.putString("GuestDriverDetails", GuestDriverModel.toString());
                                System.out.println(BookingBundle);
                                NavHostFragment.findNavController(Fragment_Guest_Driver_Details.this)
                                        .navigate(R.id.action_Guest_driver_details_to_Finalize_your_rental, Booking);
                            }
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            Back.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle Booking = new Bundle();
                    BookingBundle.putInt("BookingStep", 4);
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    NavHostFragment.findNavController(Fragment_Guest_Driver_Details.this)
                            .navigate(R.id.action_Guest_driver_details_to_Finalize_your_rental,Booking);
                }
            });

            lblLogin.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle Booking = new Bundle();
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    Booking.putBoolean("LoginForUser",false);
                    System.out.println(BookingBundle);
                    NavHostFragment.findNavController(Fragment_Guest_Driver_Details.this)
                            .navigate(R.id.action_Guest_driver_details_to_LoginFragment,Booking);
                }
            });

            lblDiscard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are You Sure You Want To Cancel?");
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {

                                    Bundle Booking = new Bundle();
                                    BookingBundle.putInt("BookingStep", 4);
                                    Booking.putBundle("BookingBundle", BookingBundle);
                                    Booking.putBundle("VehicleBundle", VehicleBundle);
                                    Booking.putBundle("returnLocation", returnLocationBundle);
                                    Booking.putBundle("location", locationBundle);
                                    Booking.putBoolean("locationType", locationType);
                                    Booking.putBoolean("initialSelect", initialSelect);
                                    NavHostFragment.findNavController(Fragment_Guest_Driver_Details.this)
                                            .navigate(R.id.action_Guest_driver_details_to_Finalize_your_rental,Booking);
                                }
                            });
                    builder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            });

                    final AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
