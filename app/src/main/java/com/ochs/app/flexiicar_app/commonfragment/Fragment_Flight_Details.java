package com.ochs.app.flexiicar_app.commonfragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;
import com.ochs.app.flexiicar_app.booking.Fragment_Vehicles_FilterList;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Fragment_Flight_Details extends Fragment
{
    LinearLayout backlayout;
    ImageView backimg;
    Bundle BookingBundle, VehicleBundle,ReservationBundle;
    int forflight;
    Bundle returnLocationBundle, locationBundle;
    Boolean locationType, initialSelect;
    TextView txtDiscard;
    int cDay, cMonth, cYear,hour1,minutes1;
    TextView arrivalDateTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flight_details, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        forflight=getArguments().getInt("forflight");

        backlayout=view.findViewById(R.id.lblbacktosummary);
        backimg=view.findViewById(R.id.back_to_finalize);

        final EditText airLine = view.findViewById(R.id.airLine);
        final EditText flightNumber = view.findViewById(R.id.flightNumber);
        arrivalDateTime = view.findViewById(R.id.arrivalDateTime);
        txtDiscard=view.findViewById(R.id.txt_discardFlight);

        arrivalDateTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Calendar getDate=Calendar.getInstance();
                cDay = getDate.get (Calendar.DAY_OF_MONTH);
                cMonth = getDate.get(Calendar.MONTH);
                cYear = getDate.get(Calendar.YEAR);

                DatePickerDialog datePicker = new DatePickerDialog(getActivity(),R.style.DialogTheme, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        cYear = year;
                        cMonth = month;
                        cDay = dayOfMonth;

                        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener()
                        {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                            {

                                final String AM_PM;
                                String mm_precede = "";
                                String hh_precede = "";
                                String dd_Precede=" ";

                                if (hourOfDay > 12)
                                {
                                    hourOfDay -= 12;
                                    AM_PM = " PM";
                                }
                                else if (hourOfDay==0)
                                {
                                    hourOfDay += 12;
                                    AM_PM = " AM";
                                }
                                else if (hourOfDay == 12)
                                    AM_PM = " PM";
                                else
                                    AM_PM = " AM";

                                if (minute <10)
                                {
                                    mm_precede = "0";
                                }
                                if (hourOfDay<10)
                                {
                                    hh_precede = "0";
                                }

                                hour1 = hourOfDay;
                                minutes1 = minute;
                                if (cDay <10)
                                {
                                    dd_Precede = "0";
                                }

                                String monthName = "Jan";
                                if(cMonth == 2)
                                    monthName = "Feb";
                                else if(cMonth == 3)
                                    monthName = "Mar";
                                else if(cMonth == 4)
                                    monthName = "Apr";
                                else if(cMonth == 5)
                                    monthName = "May";
                                else if(cMonth == 6)
                                    monthName = "Jun";
                                else if(cMonth == 7)
                                    monthName = "Jul";
                                else if(cMonth == 8)
                                    monthName = "Aug";
                                else if(cMonth == 9)
                                    monthName = "Sep";
                                else if(cMonth == 10)
                                    monthName = "Oct";
                                else if(cMonth == 11)
                                    monthName = "Nov";
                                else if(cMonth == 12)
                                    monthName = "Dec";

                                arrivalDateTime.setText(dd_Precede+cDay + " " + monthName + " " + cYear + ","+hh_precede +hour1 + ":" + mm_precede +minutes1 + AM_PM);
                            }

                        },hour1,minutes1,false);
                        timePickerDialog.show();
                    }
                },cYear,cMonth,cDay);
                datePicker.show();
            }
        });

        txtDiscard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //For Booking
                if (forflight == 1)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are You Sure You Want To Cancel?");
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    NavHostFragment.findNavController(Fragment_Flight_Details.this)
                                            .navigate(R.id.action_flight_details_to_Search_activity);
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

                //For User
                if (forflight == 2)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are You Sure You Want To Cancel?");
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    NavHostFragment.findNavController(Fragment_Flight_Details.this)
                                            .navigate(R.id.action_flight_details_to_Reservation);
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
            }
        });

        backlayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //For Booking
                if (forflight == 1)
                {
                    BookingBundle = getArguments().getBundle("BookingBundle");
                    VehicleBundle = getArguments().getBundle("VehicleBundle");
                    returnLocationBundle = getArguments().getBundle("returnLocation");
                    locationBundle = getArguments().getBundle("location");
                    locationType = getArguments().getBoolean("locationType");
                    initialSelect = getArguments().getBoolean("initialSelect");
                    try {
                        JSONObject airPortModel = new JSONObject();
                        airPortModel.put("airLine", airLine.getText().toString());
                        airPortModel.put("flightNumber", flightNumber.getText().toString());
                        airPortModel.put("arrivalDateTime", arrivalDateTime.getText().toString());

                        BookingBundle.putString("airPortModel", airPortModel.toString());
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    Bundle Booking = new Bundle();
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    NavHostFragment.findNavController(Fragment_Flight_Details.this)
                            .navigate(R.id.action_flight_details_to_Finalize_your_rental, Booking);
                }
                //For User
                if (forflight == 2)
                {
                    ReservationBundle=getArguments().getBundle("ReservationBundle");
                    try
                    {
                        JSONObject airPortModel = new JSONObject();
                        airPortModel.put("airLine", airLine.getText().toString());
                        airPortModel.put("flightNumber", flightNumber.getText().toString());
                        airPortModel.put("arrivalDateTime", arrivalDateTime.getText().toString());

                        ReservationBundle.putString("airPortModel", airPortModel.toString());
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    Bundle Reservation = new Bundle();
                    Reservation.putBundle("ReservationBundle", ReservationBundle);
                    NavHostFragment.findNavController(Fragment_Flight_Details.this)
                            .navigate(R.id.action_flight_details_to_Finalize_your_rental, Reservation);
                }
            }
        });

        backimg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //For Booking
                if (forflight == 1)
                {
                    BookingBundle = getArguments().getBundle("BookingBundle");
                    VehicleBundle = getArguments().getBundle("VehicleBundle");
                    returnLocationBundle = getArguments().getBundle("returnLocation");
                    locationBundle = getArguments().getBundle("location");
                    locationType = getArguments().getBoolean("locationType");
                    initialSelect = getArguments().getBoolean("initialSelect");
                    try
                    {
                        JSONObject airPortModel = new JSONObject();
                        airPortModel.put("airLine", airLine.getText().toString());
                        airPortModel.put("flightNumber", flightNumber.getText().toString());
                        airPortModel.put("arrivalDateTime", arrivalDateTime.getText().toString());

                        BookingBundle.putString("airPortModel", airPortModel.toString());
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    Bundle Booking = new Bundle();
                    Booking.putBundle("BookingBundle", BookingBundle);
                    Booking.putBundle("VehicleBundle", VehicleBundle);
                    Booking.putBundle("returnLocation", returnLocationBundle);
                    Booking.putBundle("location", locationBundle);
                    Booking.putBoolean("locationType", locationType);
                    Booking.putBoolean("initialSelect", initialSelect);
                    NavHostFragment.findNavController(Fragment_Flight_Details.this)
                            .navigate(R.id.action_flight_details_to_Finalize_your_rental, Booking);
                }

                //For User
                if (forflight == 2)
                {
                    ReservationBundle = getArguments().getBundle("ReservationBundle");
                    try {
                        JSONObject airPortModel = new JSONObject();
                        airPortModel.put("airLine", airLine.getText().toString());
                        airPortModel.put("flightNumber", flightNumber.getText().toString());
                        airPortModel.put("arrivalDateTime", arrivalDateTime.getText().toString());

                        ReservationBundle.putString("airPortModel", airPortModel.toString());
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    Bundle Reservation = new Bundle();
                    Reservation.putBundle("ReservationBundle", ReservationBundle);
                    NavHostFragment.findNavController(Fragment_Flight_Details.this)
                            .navigate(R.id.action_flight_details_to_Finalize_your_rental, Reservation);
                }
            }
        });
    }
}

  /* if(forflight==1)
        {
            try {
                BookingBundle = getArguments().getBundle("BookingBundle");

                String StrPickupDate = (BookingBundle.getString("PickupDate"));
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                Date date1 = dateFormat1.parse(StrPickupDate);
                System.out.println(date1);

                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                String PickupDateStr = sdf1.format(date1);

                String strPickuptime = (BookingBundle.getString("PickupTime"));
                SimpleDateFormat dateFormat3 = new SimpleDateFormat("HH:mm");
                Date date3 = dateFormat3.parse(strPickuptime);
                SimpleDateFormat sdf3 = new SimpleDateFormat("hh:mm aa", Locale.US);
                String PickuptimeStr = sdf3.format(date3);

              //  arrivalDateTime.setText(PickupDateStr +","+PickuptimeStr);

            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        if (forflight == 2)
        {
            ReservationBundle = getArguments().getBundle("ReservationBundle");
            String CheckIn = (ReservationBundle.getString("default_Check_In"));
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
                Date date = dateFormat.parse(CheckIn);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm aa", Locale.US);
                String CheckInStr = sdf.format(date);

               // arrivalDateTime.setText(CheckInStr);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }*/