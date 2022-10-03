package com.ochs.app.flexiicar_app.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.CANCELBOOKING;

public class Fragment_Summary_Of_Charges_For_Reservation extends Fragment
{
    Handler handler = new Handler();
    public static Context context;
    Bundle ReservationBundle;
    ImageView SummaryOfChargeArrow,SummaryOfChargeArrowDown,QR_Image,BackArrow,Arrow;
    RelativeLayout rlSummaryOfCharge;
    TextView txt_PickLocName,txt_ReturnLocName,txt_PickupDate,txt_ReturnDate,txt_PickupTime,txt_ReturnTIme,txtDays,txt_vehicletype,
            lblTransmissionName,txt_vehName,txt_rate,txt_Seats,txt_Bags,txt_Doors,txt_Home,lbl_Option,txt_conformationNo,txtDiscard,txt_createdDate;
    ImageView arrowSelfcheckOut,driverdetails_icon;
    LinearLayout lblterm_condition1,LL_selfcheckout;
    LinearLayout llCancelBooking,llModify;
    int reservation_ID;
    RelativeLayout llToolbar;
    JSONArray summaryOfCharges = new JSONArray();

    ImageLoader imageLoader;
    String serverpath="",domainName="";
    double TotalAmount;
    LinearLayout AddCalender;
    public static void initImageLoader(Context context)
    {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.MAX_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(100 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        ImageLoader.getInstance().init(config.build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_summary_of_charges, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ((User_Profile) getActivity()).BottomnavInVisible();

        try {
            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            serverpath = sp.getString("serverPath", "");
            domainName= sp.getString("domainName", "");
            System.out.println("serverpath"+serverpath);
            System.out.println("domainName"+domainName);

            initImageLoader(getActivity());
            imageLoader = ImageLoader.getInstance();

            AddCalender=view.findViewById(R.id.lblAddCalender);
            llToolbar=view.findViewById(R.id.llTitle);
            llToolbar.setVisibility(View.VISIBLE);
            BackArrow=view.findViewById(R.id.backToHome);
            lblterm_condition1 = view.findViewById(R.id.term_condition);

            LL_selfcheckout = view.findViewById(R.id.Layout_SelfCheckout);
            arrowSelfcheckOut = view.findViewById(R.id.arrow_Selfcheckout);
            Arrow = view.findViewById(R.id.Arrow);
            QR_Image = view.findViewById(R.id.QR_Image);
            llCancelBooking = view.findViewById(R.id.llCancelBooking);
            llModify=view.findViewById(R.id.llModify);

            SummaryOfChargeArrowDown = view.findViewById(R.id.ArrowDownForSummary);
            SummaryOfChargeArrow= view.findViewById(R.id.ArrowForSummary);

            rlSummaryOfCharge = view.findViewById(R.id.rl_Summaryofcharge);
            txt_Home=view.findViewById(R.id.txt_Home);
            lbl_Option=view.findViewById(R.id.lbl_Option);
            txt_Home.setVisibility(View.GONE);
            txt_conformationNo=view.findViewById(R.id.lbl_conformationNo);

            txt_PickLocName = view.findViewById(R.id.lblPic_LocName);
            txt_PickupDate = view.findViewById(R.id.lblPic_LocDate);
            txt_PickupTime = view.findViewById(R.id.txtPic_LocTime);
            txt_ReturnLocName = view.findViewById(R.id.lblReturn_LocName);

            txt_ReturnDate = view.findViewById(R.id.lblReturn_Date);
            txt_ReturnTIme = view.findViewById(R.id.txtReturn_Time);
            txt_createdDate= view.findViewById(R.id.lbl_createdDate);

            txtDays = view.findViewById(R.id.lbl_TotalDay1);
            txt_rate = view.findViewById(R.id.txt_Vehiclerate1);
            txt_Seats = view.findViewById(R.id.textView_Seats1);
            txt_Bags = view.findViewById(R.id.textView_Bags1);
            lblTransmissionName = view.findViewById(R.id.lblTransmissionName);
            txt_Doors = view.findViewById(R.id.txt_door1);

            txt_vehName = view.findViewById(R.id.vehiclE_NAME);
            txt_vehicletype = view.findViewById(R.id.txtvehiclE_TYPE_NAME);

            ReservationBundle = getArguments().getBundle("ReservationBundle");

            txt_conformationNo.setText(ReservationBundle.getString("reservation_No"));
            //Pickuploc
            txt_PickLocName.setText(ReservationBundle.getString("chk_Out_Location_Name"));
            String check_Out=(ReservationBundle.getString("check_Out"));

            SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
            Date date1 = dateFormat1.parse(check_Out);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm aa", Locale.US);
            String CheckOutStr = sdf1.format(date1);

            txt_PickupDate.setText(CheckOutStr);

            txt_ReturnLocName.setText(ReservationBundle.getString("chk_In_Loc_Name"));
            String CheckIn=(ReservationBundle.getString("check_IN"));

            SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
            Date date2 = dateFormat2.parse(CheckIn);
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm aa", Locale.US);
            String CheckInStr = sdf2.format(date2);

            txt_ReturnDate.setText(CheckInStr);

            //VehDEtails
            txt_vehName.setText(ReservationBundle.getString("vehicle_Name"));
            txt_vehicletype.setText(ReservationBundle.getString("vehicle_Type_Name"));

            lblTransmissionName.setText(ReservationBundle.getString("transmission_Name"));
            txt_rate.setText(ReservationBundle.getString("rate_ID"));

            String Date=(ReservationBundle.getString("default_Created_Date"));

            txt_createdDate.setText("SEE YOU ON "+(txt_PickupDate.getText().toString()));
            reservation_ID=ReservationBundle.getInt("reservation_ID");

            ImageView Vehicle_Img=view.findViewById(R.id.Veh_image_bg4);

            String VehicleImgstr=ReservationBundle.getString("veh_Img_Path");
            String url1 = serverpath+VehicleImgstr.substring(2);
            imageLoader.displayImage(url1,Vehicle_Img);

            arrowSelfcheckOut.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    arrowSelfcheckOut.setVisibility(View.INVISIBLE);
                    if (LL_selfcheckout.getVisibility() == View.GONE)
                    {
                        LL_selfcheckout.setVisibility(View.VISIBLE);

                    } else
                        LL_selfcheckout.setVisibility(View.GONE);
                }
            });

            Arrow.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    arrowSelfcheckOut.setVisibility(View.VISIBLE);
                    LL_selfcheckout.setVisibility(View.GONE);
                }
            });

            lblterm_condition1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle Reservation = new Bundle();
                    Reservation.putInt("termcondition", 2);
                    Reservation.putBundle("ReservationBundle", ReservationBundle);
                    System.out.println(Reservation);
                    NavHostFragment.findNavController(Fragment_Summary_Of_Charges_For_Reservation.this)
                            .navigate(R.id.action_SummaryOfCharges_to_TermAndCondition,Reservation);
                }
            });

            AddCalender.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Calendar calendarEvent = Calendar.getInstance();
                    Intent i = new Intent(Intent.ACTION_EDIT);
                    i.setType("vnd.android.cursor.item/event");
                    i.putExtra("Date",calendarEvent.getTimeInMillis());
                    i.putExtra("allDay", true);
                    i.putExtra("rule", "FREQ=YEARLY");
                    i.putExtra("endTime", calendarEvent.getTimeInMillis() + 60 * 60 * 1000);
                    i.putExtra("title", "Calendar Event");
                    startActivity(i);
                }
            });

            llModify.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle Reservation=new Bundle();
                    ReservationBundle.putInt("BookingStep",3);
                    ReservationBundle.putString("vehiclE_SEAT_NO",txt_Seats.getText().toString());
                    ReservationBundle.putString("veh_bags",txt_Bags.getText().toString());
                    ReservationBundle.putString("transmission_Name",lblTransmissionName.getText().toString());
                    ReservationBundle.putString("doors",txt_Doors.getText().toString());
                    ReservationBundle.putString("totaL_DAYS",txtDays.getText().toString());
                    ReservationBundle.putDouble("chargeAmount",TotalAmount);
                    Reservation.putString("DeliveryAndPickupModel", "");
                    Reservation.putBundle("ReservationBundle",ReservationBundle);
                    Reservation.putBoolean("IsSelected", false);
                    System.out.println(Reservation);
                    NavHostFragment.findNavController(Fragment_Summary_Of_Charges_For_Reservation.this)
                            .navigate(R.id.action_SummaryOfCharges_to_Select_addtional_options,Reservation);
                }
            });

            BackArrow.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    NavHostFragment.findNavController(Fragment_Summary_Of_Charges_For_Reservation.this)
                            .navigate(R.id.action_SummaryOfCharges_to_Reservation);
                }
            });

            final LinearLayout driverdetails1 = view.findViewById(R.id.driver_details);

            TextView driverName = view.findViewById(R.id.textV_driverName);
            TextView driverPhone = view.findViewById(R.id.TextV_DriverPhoneno);
            TextView driverEmail = view.findViewById(R.id.TextV_DriverEmail);


            String cust_FName=ReservationBundle.getString("cust_FName");
            String cust_LName=ReservationBundle.getString("cust_LName");
            String cust_MobileNo=ReservationBundle.getString("cust_MobileNo");
            String cust_Phoneno=ReservationBundle.getString("cust_Phoneno");
            String cust_Email=ReservationBundle.getString("cust_Email");

            driverName.setText(cust_FName + " " + cust_LName);
            driverPhone.setText(cust_MobileNo);
            driverEmail.setText(cust_Email);

            driverdetails_icon = view.findViewById(R.id.driverdetails_icon);
            driverdetails_icon.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (driverdetails1.getVisibility() == View.GONE)
                    {
                        driverdetails1.setVisibility(View.VISIBLE);

                    } else
                        driverdetails1.setVisibility(View.GONE);

                }
            });
            rlSummaryOfCharge.setVisibility(View.VISIBLE);
            SummaryOfChargeArrowDown.setVisibility(View.VISIBLE);
            SummaryOfChargeArrow.setVisibility(View.GONE);
            SummaryOfChargeArrow.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    if (rlSummaryOfCharge.getVisibility() == View.GONE)
                    {
                        rlSummaryOfCharge.setVisibility(View.VISIBLE);
                        SummaryOfChargeArrowDown.setVisibility(View.VISIBLE);
                        SummaryOfChargeArrow.setVisibility(View.GONE);
                    }
                }
            });
            SummaryOfChargeArrowDown.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(rlSummaryOfCharge.getVisibility() == View.VISIBLE)
                    {
                        rlSummaryOfCharge.setVisibility(View.GONE);
                        SummaryOfChargeArrow.setVisibility(View.VISIBLE);
                        SummaryOfChargeArrowDown.setVisibility(View.GONE);
                    }
                }
            });

            lbl_Option.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    PopupMenu popup = new PopupMenu(getActivity(), lbl_Option);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        public boolean onMenuItemClick(MenuItem item)
                        {

                            switch (item.getItemId())
                            {
                                case R.id.CancelBooking:
                                    Bundle CancelBookingbundle = new Bundle();
                                    ReservationBundle.putString("SummaryOfCharges", summaryOfCharges.toString());
                                    ReservationBundle.putDouble("TotalAmount",TotalAmount);
                                    CancelBookingbundle.putBundle("ReservationBundle", ReservationBundle);
                                    NavHostFragment.findNavController(Fragment_Summary_Of_Charges_For_Reservation.this)
                                            .navigate(R.id.action_SummaryOfCharges_to_CancelBooking,CancelBookingbundle);
                                    break;

                               /* case R.id.Add_Driver:
                                    Bundle bundle = new Bundle();
                                    bundle.putBundle("ReservationBundle", ReservationBundle);
                                    NavHostFragment.findNavController(Fragment_Summary_Of_Charges_For_Reservation.this)
                                            .navigate(R.id.action_SummaryOfCharges_to_Add_Additional_Driver_Details,bundle);
                                    break;*/

                                case R.id.Add_Insurance:
                                    Bundle Add_Insurance = new Bundle();
                                    Add_Insurance.putBundle("ReservationBundle", ReservationBundle);
                                    NavHostFragment.findNavController(Fragment_Summary_Of_Charges_For_Reservation.this)
                                            .navigate(R.id.action_SummaryOfCharges_to_AddInsurancePolicy,Add_Insurance);
                                    break;

                                case R.id.Add_Billing:
                                    Bundle AddBillingbundle = new Bundle();
                                    AddBillingbundle.putBundle("ReservationBundle", ReservationBundle);
                                    NavHostFragment.findNavController(Fragment_Summary_Of_Charges_For_Reservation.this)
                                            .navigate(R.id.action_SummaryOfCharges_to_AddBillingDetails,AddBillingbundle);
                                    break;

                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });

            JSONObject bodyParam = new JSONObject();
            try
            {
                bodyParam.accumulate("ForTransId", ReservationBundle.getInt("reservation_ID"));
                bodyParam.accumulate("BookingStep", 6);
                System.out.println(bodyParam);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            AndroidNetworking.initialize(getActivity());
            Fragment_Summary_Of_Charges_For_Reservation.context = getActivity();

            ApiService ApiService = new ApiService(getTaxtDetails, RequestType.POST,
                    BOOKING, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        llCancelBooking.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to cancel booking?");
                  builder.setPositiveButton("Yes",
                          new DialogInterface.OnClickListener()
                          {
                              @Override
                              public void onClick(DialogInterface dialog, int which)
                              {
                                  JSONObject bodyParam = new JSONObject();
                                  try
                                  {
                                      bodyParam.accumulate("bookingId", reservation_ID);
                                      System.out.println(bodyParam);
                                  }
                                  catch (JSONException e)
                                  {
                                      e.printStackTrace();
                                  }
                                  AndroidNetworking.initialize(getActivity());
                                  Fragment_Summary_Of_Charges_For_Reservation.context = getActivity();

                                  ApiService ApiService = new ApiService(CancelBooking, RequestType.GET,
                                          CANCELBOOKING, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
                              }
                          });
                builder.setNegativeButton("cancel",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public   void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });

                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

 }
    OnResponseListener getTaxtDetails = new OnResponseListener()
    {
        @Override
        public void onSuccess(final String response, HashMap<String, String> headers)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                        System.out.println("Success");
                        System.out.println(response);

                        JSONObject responseJSON = new JSONObject(response);
                        Boolean status = responseJSON.getBoolean("status");

                        if (status)
                        {
                            try
                            {
                                JSONObject resultSet = responseJSON.getJSONObject("resultSet");

                                final JSONArray getsummaryOfCharges = resultSet.getJSONArray("summaryOfCharges");
                                final RelativeLayout rlSummaryOfCharge = getActivity().findViewById(R.id.rl_Summaryofcharge);

                                final JSONArray taxModel = resultSet.getJSONArray("taxModel");

                                if(!resultSet.isNull("t0050_Documents"))
                                {
                                    //QrImage
                                    JSONObject Documents=resultSet.getJSONObject("t0050_Documents");
                                    final int doc_ID = Documents.getInt("doc_ID");
                                    final String doc_Name = Documents.getString("doc_Name");
                                    final String doc_Details = Documents.getString("doc_Details");
                                    final String doc_Status = Documents.getString("doc_Status");
                                    final String created_Date = Documents.getString("created_Date");

                                    String url2 = serverpath+doc_Details.substring(2);
                                    System.out.println(url2);
                                    imageLoader.displayImage(url2,QR_Image);
                                }

                                if(!resultSet.isNull("vehicleModel"))
                                {
                                    //VehicleDetails
                                    JSONObject vehicleModel = resultSet.getJSONObject("vehicleModel");
                                    final String vehiclE_NAME = vehicleModel.getString("vehiclE_NAME");
                                    final String vehiclE_TYPE_NAME = vehicleModel.getString("vehiclE_TYPE_NAME");
                                    final String vehiclE_SEAT_NO = vehicleModel.getString("vehiclE_SEAT_NO");
                                    final String veh_bags = vehicleModel.getString("veh_bags");
                                    final String transmission_Name = vehicleModel.getString("transmission_Name");
                                    final String doors = vehicleModel.getString("doors");
                                    final double totaL_DAYS = vehicleModel.getDouble("totaL_DAYS");

                                    String totaLDAYSStr = (String.valueOf(totaL_DAYS));
                                    totaLDAYSStr = totaLDAYSStr.substring(0, totaLDAYSStr.length() - 2);
                                    txtDays.setText(totaLDAYSStr);

                                    txt_Seats.setText(vehiclE_SEAT_NO+" Seats");
                                    txt_Bags.setText(veh_bags+" Bags");
                                    lblTransmissionName.setText(transmission_Name);
                                    txt_Doors.setText(doors+" Doors");
                                }

                                //Summary Of Charges
                                int len;
                                len = getsummaryOfCharges.length();

                                for (int j = 0; j < len; j++)
                                {
                                    final JSONObject test = (JSONObject) getsummaryOfCharges.get(j);

                                    final int sortId = test.getInt("sortId");
                                    final String chargeCode = test.getString("chargeCode");
                                    final String chargeName = test.getString("chargeName");
                                    final Double chargeAmount = test.getDouble("chargeAmount");

                                    JSONObject summaryOfChargesObj = new JSONObject();

                                    summaryOfChargesObj.put("sortId", sortId);
                                    summaryOfChargesObj.put("chargeCode", chargeCode);
                                    summaryOfChargesObj.put("chargeName", chargeName);
                                    summaryOfChargesObj.put("chargeAmount", chargeAmount);

                                    summaryOfCharges.put(summaryOfChargesObj);

                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                    lp.setMargins(0, 0, 0, 0);

                                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.vehicle_tax_details, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                    linearLayout.setId(200 + j);
                                    linearLayout.setLayoutParams(lp);

                                    TextView lbl_chargeAmount, lblchargeName;
                                    LinearLayout arrowIcon = (LinearLayout) linearLayout.findViewById(R.id.arrow_icon);
                                    LinearLayout SummaryOfChargeLayout=(LinearLayout)linearLayout.findViewById(R.id.SummaryOfChargesLayout);

                                    if (sortId == 10)
                                    {
                                        arrowIcon.setVisibility(View.VISIBLE);
                                    }
                                    if (sortId == 12)
                                    {
                                        TotalAmount = chargeAmount;
                                        System.out.println(TotalAmount);
                                    }

                                    arrowIcon.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            Bundle Reservation = new Bundle();
                                            Reservation.putBundle("ReservationBundle", ReservationBundle);
                                            Reservation.putInt("TaxType",3);
                                            Reservation.putDouble("TaxesAndFeesAmount", chargeAmount);
                                            Reservation.putString("taxModel",taxModel.toString());
                                            NavHostFragment.findNavController(Fragment_Summary_Of_Charges_For_Reservation.this)
                                                    .navigate(R.id.action_SummaryOfCharges_to_Total_tax_fee_details, Reservation);
                                        }
                                    });

                                    lbl_chargeAmount = (TextView) linearLayout.findViewById(R.id.lbl_chargeAmount);
                                    lblchargeName = (TextView) linearLayout.findViewById(R.id.lblchargeName);
                                    String str=String.format(Locale.US,"%.2f",chargeAmount);

                                    if(chargeName.equals("Discount Applied"))
                                    {
                                        lbl_chargeAmount.setTextColor(getResources().getColor(R.color.btn_bg_color_2));

                                    }

                                    if(chargeName.equals("Balance"))
                                    {
                                        SummaryOfChargeLayout.setBackgroundColor(getResources().getColor(R.color.footerButtonBC));
                                        lblchargeName.setTextColor(getResources().getColor(R.color.screen_bg_color));
                                        lbl_chargeAmount.setTextColor(getResources().getColor(R.color.screen_bg_color));
                                    }

                                    lbl_chargeAmount.setText("USD$ "+str);
                                    lblchargeName.setText(chargeName);
                                    lblchargeName.setText(chargeName);

                                    rlSummaryOfCharge.addView(linearLayout);
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }

                        else
                        {
                            String msg = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),msg,1);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
        @Override
        public void onError(String error) {
            System.out.println("Error-" + error);
        }
    };

    OnResponseListener CancelBooking = new OnResponseListener()
    {
        @Override
        public void onSuccess(final String response, HashMap<String, String> headers)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                        System.out.println("Success");
                        System.out.println(response);

                        JSONObject responseJSON = new JSONObject(response);
                        Boolean status = responseJSON.getBoolean("status");

                        if (status)
                        {
                            String msg = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),msg,0);
                        }

                        else
                        {
                            String msg = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),msg,1);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onError(String error)
        {
            System.out.println("Error-" + error);
        }
    };
 }
/*    llModify.setOnClickListener(new View.OnClickListener()
          {
              @Override
              public void onClick(View v)
              {
                  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                  builder.setMessage("Please call our office for modifiction.");
                 /* builder.setPositiveButton("Yes",
                          new DialogInterface.OnClickListener()
                          {
                              @Override
                              public void onClick(DialogInterface dialog, int which)
                              {

                              }
                          });*/
                /*  builder.setNegativeButton("Cancel",
                          new DialogInterface.OnClickListener()
                          {
@Override
public   void onClick(DialogInterface dialog, int which)
        {
        dialog.dismiss();
        }
        });

final AlertDialog dialog = builder.create();
        dialog.show();
        }
        });
*/