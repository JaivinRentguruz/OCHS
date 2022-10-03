package com.ochs.app.flexiicar_app.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.BOOKING;

public class Fragment_Finalize_Your_Rental_For_User extends Fragment
{
    ImageView backarrow,SummaryOfChargeArrowdown,SummaryOfChargeArrow;
    Handler handler = new Handler();
    public static Context context;
    Bundle ReservationBundle;
    LinearLayout linearLayout3,TermCondition_layout,lblpay,SummaryOfChargeLayout,FlightDetailsMainLayout;
    TextView txt_PickLocName,txt_ReturnLocName,txt_PickupDate,txt_ReturnDate,txt_PickupTime,txt_ReturnTIme,txtDays,txt_vehicletype,
            txt_vehName,txt_rate,txtTotalAmount,txt_Seats,txt_Bags,txt_Automatic,txt_Doors,
            txt_AsGuestdriver,txt_driverdetails,txtDiscard,txtvehDesc,txtMileage;
    ImageView driverdetails_icon1,arrowflight_details;
    ImageLoader imageLoader;
    String serverpath="";
    int cmP_DISTANCE;

    public static void initImageLoader(Context context)
    {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.MAX_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(100 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs();
        ImageLoader.getInstance().init(config.build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_finalize_your_rental, container, false);
    }

    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState)
    {
        try {
            super.onViewCreated(view, savedInstanceState);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            ((User_Profile) getActivity()).BottomnavInVisible();

            initImageLoader(getActivity());
            imageLoader = ImageLoader.getInstance();
            ReservationBundle = getArguments().getBundle("ReservationBundle");

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            String id = sp.getString(getString(R.string.id), "");
            serverpath = sp.getString("serverPath", "");
            cmP_DISTANCE= sp.getInt("cmP_DISTANCE", 0);

            ImageView CarImage=view.findViewById(R.id.Veh_image_bg3);

            String VehicleImgstr=ReservationBundle.getString("veh_Img_Path");
            String url1 = serverpath+VehicleImgstr.substring(2);
            imageLoader.displayImage(url1,CarImage);

            linearLayout3 = view.findViewById(R.id.flight_details_1);
            TermCondition_layout = view.findViewById(R.id.lblterm_condition);
            lblpay = view.findViewById(R.id.layout_payment);
            backarrow = view.findViewById(R.id.backbtn2);
            SummaryOfChargeArrowdown = view.findViewById(R.id.SummaryofChargesArrowDown);
            SummaryOfChargeArrow= view.findViewById(R.id.img_bottomArrow);
            SummaryOfChargeLayout = view.findViewById(R.id.LinearL_SummaryOfCharges);

            txt_PickLocName = view.findViewById(R.id.txt_PickupLocation);
            txt_PickupDate = view.findViewById(R.id.txt_PickupLoationDate);
            txt_PickupTime = view.findViewById(R.id.txt_PickupLocationTime);
            txt_ReturnLocName = view.findViewById(R.id.txt_ReturnLocation);
            txt_ReturnDate = view.findViewById(R.id.txt_ReturnLocationDate);
            txt_ReturnTIme = view.findViewById(R.id.txt_ReturnLocationTime);
            txtDays = view.findViewById(R.id.textView_TotalDays);
            txt_rate = view.findViewById(R.id.txt_Vehiclerate);
            txt_Seats = view.findViewById(R.id.textView_Seats);
            txt_Bags = view.findViewById(R.id.textView_Bags);
            txt_Automatic = view.findViewById(R.id.textView_Automatic);
            txt_Doors = view.findViewById(R.id.textView_Doors);
            txtTotalAmount = view.findViewById(R.id.textview_TotalAmount);
            txtDiscard=view.findViewById(R.id.DiscardFinalizeRen);
            txt_vehName=view.findViewById(R.id.textV_VehicleModelName);
            txt_vehicletype=view.findViewById(R.id.txtV_TypeName);
            txtvehDesc=view.findViewById(R.id.txtvehDesciption);
            txtMileage= view.findViewById(R.id.txtMileage);
            txt_AsGuestdriver= view.findViewById(R.id.txt_AsGuestdriver);
            txt_driverdetails=view.findViewById(R.id.txt_driverdetails);
            txt_driverdetails.setVisibility(View.VISIBLE);
            txt_AsGuestdriver.setVisibility(View.GONE);

            //Pickup Location time date
            txt_PickLocName.setText(ReservationBundle.getString("chk_Out_Location_Name"));
            String CheckOut=(ReservationBundle.getString("check_Out"));
            txt_PickupDate.setText(CheckOut);

            //Return Location time date
            txt_ReturnLocName.setText(ReservationBundle.getString("chk_In_Loc_Name"));
            String check_IN=(ReservationBundle.getString("check_IN"));
            txt_ReturnDate.setText(check_IN);

            txt_PickLocName.setText(ReservationBundle.getString("chk_Out_Location_Name"));
            txt_ReturnLocName.setText(ReservationBundle.getString("chk_In_Loc_Name"));

            String check_Out=(ReservationBundle.getString("check_Out"));
            // Date
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
            Date date1 = dateFormat1.parse(check_Out);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm aa", Locale.US);
            String CheckOutStr = sdf1.format(date1);
            txt_PickupDate.setText(CheckOutStr);

            String check_in=(ReservationBundle.getString("check_IN"));
            //check_Out Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
            Date date = dateFormat.parse(check_in);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm aa", Locale.US);
            String CheckInStr = sdf.format(date);
            txt_ReturnDate.setText(CheckInStr);

            txt_vehName.setText(ReservationBundle.getString("vehicle_Name"));
            txt_vehicletype.setText(ReservationBundle.getString("vehicle_Type_Name"));

            txt_Automatic.setText(ReservationBundle.getString("transmission_Name"));
            txtDays.setText(ReservationBundle.getString("totaL_DAYS"));
            txt_Bags.setText(ReservationBundle.getString("veh_bags"));
            txt_Seats.setText(ReservationBundle.getString("vehiclE_SEAT_NO"));
            txt_Doors.setText(ReservationBundle.getString("doors"));

            txt_rate.setText(ReservationBundle.getString("rate_ID"));
            txtvehDesc.setText(ReservationBundle.getString("vehDescription"));
            Double totalMilesAllowed=ReservationBundle.getDouble("totalMilesAllowed");

            if(cmP_DISTANCE==1)
            {
                String Miles=((String.format(Locale.US,"%.0f",totalMilesAllowed)));
                txtMileage.setText(Miles+" MILES");
            }
            else {
                String Miles=(String.valueOf(totalMilesAllowed));
                txtMileage.setText(Miles+"kms");
            }

            FlightDetailsMainLayout= view.findViewById(R.id.FlightDetailsMainLayout);
            FlightDetailsMainLayout.setVisibility(View.GONE);

            txtDiscard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are You Sure You Want To Discard?");
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    NavHostFragment.findNavController(Fragment_Finalize_Your_Rental_For_User.this)
                                            .navigate(R.id.action_Finalize_your_rental_to_User_Details);
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

            backarrow.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int DeductibleCoverId=ReservationBundle.getInt("DeductibleCoverId");
                    double DeductibleCharge=ReservationBundle.getDouble("DeductibleCharge");

                    Bundle Booking = new Bundle();
                    ReservationBundle.putInt("BookingStep", 3);
                    Booking.putString("DeliveryAndPickupModel", "");
                    Booking.putInt("DeductibleCover_Id", DeductibleCoverId);
                    Booking.putDouble("Deductible_Charge", DeductibleCharge);
                    Booking.putBundle("ReservationBundle",ReservationBundle);
                    Booking.putBoolean("IsSelected", true);
                    NavHostFragment.findNavController(Fragment_Finalize_Your_Rental_For_User.this)
                            .navigate(R.id.action_Finalize_your_rental_to_Select_addtional_options, Booking);
                }
            });

            SummaryOfChargeArrow.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (SummaryOfChargeLayout.getVisibility() == View.GONE)
                    {
                        SummaryOfChargeLayout.setVisibility(View.VISIBLE);
                        SummaryOfChargeArrowdown.setVisibility(View.VISIBLE);
                        SummaryOfChargeArrow.setVisibility(View.GONE);
                    }
                }
            });

            SummaryOfChargeArrowdown.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(SummaryOfChargeLayout.getVisibility() == View.VISIBLE)
                    {
                        SummaryOfChargeLayout.setVisibility(View.GONE);
                        SummaryOfChargeArrow.setVisibility(View.VISIBLE);
                        SummaryOfChargeArrowdown.setVisibility(View.GONE);
                    }
                }
            });

            lblpay.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle Booking = new Bundle();
                    ReservationBundle.putInt("BookingStep", 5);
                    Booking.putBundle("ReservationBundle", ReservationBundle);
                    Booking.putBoolean("isDefaultCard", true);
                    NavHostFragment.findNavController(Fragment_Finalize_Your_Rental_For_User.this)
                            .navigate(R.id.action_Finalize_your_rental_to_Payment_checkout, Booking);
                }
            });

            final LinearLayout driverdetails1 = view.findViewById(R.id.driverdetails1);
            String cust_FName=ReservationBundle.getString("cust_FName");
            String cust_LName=ReservationBundle.getString("cust_LName");
            String cust_MobileNo=ReservationBundle.getString("cust_MobileNo");
            String cust_Email=ReservationBundle.getString("cust_Email");

            TextView driverName = view.findViewById(R.id.driverName);
            TextView driverPhone = view.findViewById(R.id.driverPhone);
            TextView driverEmail = view.findViewById(R.id.driverEmail);

            driverName.setText(cust_FName + " " + cust_LName);
            driverPhone.setText(cust_MobileNo);
            driverEmail.setText(cust_Email);

            driverdetails_icon1 = view.findViewById(R.id.driverdetails_icon1);
            driverdetails_icon1.setOnClickListener(new View.OnClickListener()
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

            arrowflight_details = view.findViewById(R.id.arrowflight_details);

            arrowflight_details.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                }
            });

            String Check_Out3=(ReservationBundle.getString("check_Out"));
            SimpleDateFormat dateFormat3 = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
            Date date3 = dateFormat3.parse(Check_Out3);
            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String CheckOutDate = sdf3.format(date3);
            SimpleDateFormat sdff = new SimpleDateFormat("HH:mm", Locale.US);
            String CheckOutTime = sdff.format(date3);

            String Check_In3=(ReservationBundle.getString("check_IN"));
            SimpleDateFormat dateFormat4 = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
            Date date4= dateFormat4.parse(Check_In3);
            SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String CheckInDate = sdf4.format(date4);
            SimpleDateFormat sdfff = new SimpleDateFormat("HH:mm", Locale.US);
            String CheckInTime = sdfff.format(date4);


            JSONObject bodyParam = new JSONObject();
            try {
                bodyParam.accumulate("ForTransId", ReservationBundle.getInt("reservation_ID"));
                bodyParam.accumulate("CustomerId", ReservationBundle.getInt("customer_ID"));
                bodyParam.accumulate("VehicleTypeId", ReservationBundle.getInt("vehicle_Type_ID"));
                bodyParam.accumulate("VehicleID", ReservationBundle.getInt("vehicle_ID"));
                bodyParam.accumulate("BookingStep", ReservationBundle.getInt("BookingStep"));

                bodyParam.accumulate("PickupLocId", ReservationBundle.getInt("check_IN_Location"));
                bodyParam.accumulate("ReturnLocId", ReservationBundle.getInt("check_Out_Location"));

                bodyParam.accumulate("PickupDate",CheckOutDate);
                bodyParam.accumulate("ReturnDate",CheckInDate);
                bodyParam.accumulate("PickupTime",CheckOutTime );
                bodyParam.accumulate("ReturnTime",CheckInTime);

                bodyParam.accumulate("DeliveryChargeLocID", ReservationBundle.getInt("DeliveryChargeLocID"));
                bodyParam.accumulate("DeliveryChargeAmount", ReservationBundle.getDouble("DeliveryChargeAmount"));
                bodyParam.accumulate("PickupChargeLocID", ReservationBundle.getInt("PickupChargeLocID"));
                bodyParam.accumulate("PickupChargeAmount", ReservationBundle.getDouble("PickupChargeAmount"));
                bodyParam.accumulate("DeductibleCoverId", ReservationBundle.getInt("DeductibleCoverId"));
                bodyParam.accumulate("DeductibleCharge", ReservationBundle.getDouble("DeductibleCharge"));
                bodyParam.accumulate("EquipmentList", new JSONArray(ReservationBundle.getString("EquipmentList")));
                bodyParam.accumulate("MiscList", new JSONArray(ReservationBundle.getString("MiscList")));
                bodyParam.accumulate("SummaryOfCharges", new JSONArray(ReservationBundle.getString("summaryOfCharges")));
               // bodyParam.accumulate("DeliveryAndPickupModel", new JSONArray(ReservationBundle.getString("DeliveryAndPickupModel")));
                System.out.println(bodyParam);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            AndroidNetworking.initialize(getActivity());
            Fragment_Finalize_Your_Rental_For_User.context = getActivity();

            ApiService ApiService = new ApiService(getTaxtDetails, RequestType.POST,
                    BOOKING, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
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

                                final JSONArray taxModel = resultSet.getJSONArray("taxModel");


                                final RelativeLayout rlSummaryOfCharge = getActivity().findViewById(R.id.rl_SummaryOfCharges);

                                int len;
                                len = getsummaryOfCharges.length();

                                for (int j = 0; j < len; j++)
                                {
                                    final JSONObject test = (JSONObject) getsummaryOfCharges.get(j);
                                    final int sortId = test.getInt("sortId");
                                    final String chargeCode = test.getString("chargeCode");
                                    final String chargeName = test.getString("chargeName");
                                    final Double chargeAmount = test.getDouble("chargeAmount");

                                    if (chargeName.equals("Estimated Total"))
                                    {
                                        txtTotalAmount.setText(((String.format(Locale.US,"%.2f",chargeAmount))));
                                        ReservationBundle.putDouble("total",chargeAmount);
                                    }
                                  /*  if (sortId==1)
                                    {
                                        txtpayNow.setText((String.format(Locale.US,"%.2f",chargeAmount)));
                                        txtPayLater.setText((String.format(Locale.US,"%.2f",chargeAmount)));
                                    }*/
                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                    lp.setMargins(0, 0, 0, 0);

                                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.vehicle_tax_details, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                    linearLayout.setId(200 + j);
                                    linearLayout.setLayoutParams(lp);

                                    TextView txt_charge, txt_chargeName;
                                    LinearLayout arrowIcon = (LinearLayout) linearLayout.findViewById(R.id.arrow_icon);

                                    if (sortId == 10)
                                    {
                                        arrowIcon.setVisibility(View.VISIBLE);
                                    }
                                    arrowIcon.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            Bundle Reservation = new Bundle();
                                            Reservation.putBundle("ReservationBundle", ReservationBundle);
                                            Reservation.putInt("BookingStep",4);
                                            Reservation.putDouble("TaxesAndFeesAmount", chargeAmount);
                                            Reservation.putString("taxModel",taxModel.toString());
                                            Reservation.putInt("TaxType",4);
                                            NavHostFragment.findNavController(Fragment_Finalize_Your_Rental_For_User.this)
                                                    .navigate(R.id.action_Finalize_your_rental_to_Total_tax_fee_details, Reservation);
                                        }
                                    });

                                    txt_charge = (TextView) linearLayout.findViewById(R.id.lbl_chargeAmount);
                                    txt_chargeName = (TextView) linearLayout.findViewById(R.id.lblchargeName);
                                    String str=String.format(Locale.US,"%.2f",chargeAmount);
                                    txt_charge.setText("USD$ "+str);
                                    txt_chargeName.setText(chargeName);

                                    if(chargeName.equals("Discount Applied"))
                                    {
                                        txt_charge.setTextColor(getResources().getColor(R.color.btn_bg_color_2));
                                    }
                                    txt_chargeName.setText(chargeName);

                                    rlSummaryOfCharge.addView(linearLayout);
                                }

                                final JSONArray getIncludeDetails = resultSet.getJSONArray("includesItem");
                                RelativeLayout rlIncludeDetails = getActivity().findViewById(R.id.rl_includeDetailsItem);
                                int len1;
                                len1 = getIncludeDetails.length();

                                for (int k = 0; k < len1; k++)
                                {
                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp.addRule(RelativeLayout.BELOW, (200 + (k / 3) - 1));
                                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                    lp.setMargins(0, 0, 0, 0);

                                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    LinearLayout linearLayout1 = (LinearLayout) inflater.inflate(R.layout.include_details, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                    linearLayout1.setId(200 + (k / 3));
                                    linearLayout1.setLayoutParams(lp);

                                    TextView txt_InsurancePro, txt_carMaintanance, txt_roadsideAssistance;

                                    txt_InsurancePro = (TextView) linearLayout1.findViewById(R.id.lbl_InsurancePro);
                                    txt_carMaintanance = (TextView) linearLayout1.findViewById(R.id.lbl_carMaintanance);
                                    txt_roadsideAssistance = (TextView) linearLayout1.findViewById(R.id.lbl_roadsideAssistance);

                                    if (k < len1 && (k % 3) == 0)
                                    {
                                        final JSONObject test = (JSONObject) getIncludeDetails.get(k);
                                        final int includesId = test.getInt("includesId");
                                        final String includesName = test.getString("includesName");

                                        txt_InsurancePro.setText(includesName);
                                        k++;
                                    }
                                    if (k < len1 && (k% 3) == 1)
                                    {
                                        final JSONObject test = (JSONObject) getIncludeDetails.get(k);
                                        final int includesId = test.getInt("includesId");
                                        final String includesName = test.getString("includesName");

                                        txt_carMaintanance.setText(includesName);
                                        k++;
                                    }
                                    if (k < len1 && (k % 3) == 2)
                                    {
                                        final JSONObject test = (JSONObject) getIncludeDetails.get(k);
                                        final int includesId = test.getInt("includesId");
                                        final String includesName = test.getString("includesName");

                                        txt_roadsideAssistance.setText(includesName);
                                        k++;
                                    }
                                    rlIncludeDetails.addView(linearLayout1);
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            String errorString = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),errorString,1);
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
