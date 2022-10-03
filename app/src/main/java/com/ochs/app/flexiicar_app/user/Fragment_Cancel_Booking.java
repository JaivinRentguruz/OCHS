package com.ochs.app.flexiicar_app.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Color.parseColor;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.GET_BOOKING_CANCEL_CHARGE;
import static com.ochs.app.apicall.ApiEndPoint.PROCEED_CANCELLATION;

public class Fragment_Cancel_Booking extends Fragment
{
    ImageView Back,vehicle_Image;
    TextView lbl_Process;
    Bundle ReservationBundle;
    TextView lblFullname,lblPhoneNo,lblCustEmail,lblres_status,lblCheckOutLocName,lblCheckOutDateTime,lblcheckinLocName,lblcheckIn_DateTime,
            lblVehicleFullname,lblVehicleNo,lbl_LicNo,lbl_VinNo,lblDateTime,lblCancellationCharge,lblAmountPaid,lblBalanceDue,lblReservationNo;
    String cust_Full_Name,cust_MobileNo,cust_Email,chk_Out_Location_Name,chk_In_Loc_Name,vehicle_Name,
            vehicle_No,vin_Num,lic_Num,res_Status,res_Status_BGColor;
    LinearLayout ll_resStatus;
    ImageLoader imageLoader;
    String id = "",serverpath="",domainName="";
    int reservation_ID;
    Handler handler = new Handler();
    Double TotalAmount;
    CheckBox Chk_TC;
    NestedScrollView nestedScrollView;

    public static void initImageLoader(Context context)
    {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.MAX_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(100 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cancel_booking, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        try {
            super.onViewCreated(view, savedInstanceState);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            ((User_Profile) getActivity()).BottomnavInVisible();

            initImageLoader(getActivity());
            imageLoader = ImageLoader.getInstance();

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            serverpath = sp.getString("serverPath", "");
            domainName= sp.getString("domainName", "");
            id = sp.getString(getString(R.string.id), "");

            ReservationBundle = getArguments().getBundle("ReservationBundle");
            reservation_ID=ReservationBundle.getInt("reservation_ID");
            TotalAmount=ReservationBundle.getDouble("TotalAmount");

            cust_Full_Name = ReservationBundle.getString("cust_Full_Name");
            cust_MobileNo = ReservationBundle.getString("cust_MobileNo");
            cust_Email = ReservationBundle.getString("cust_Email");
            chk_Out_Location_Name = ReservationBundle.getString("chk_Out_Location_Name");
            chk_In_Loc_Name = ReservationBundle.getString("chk_In_Loc_Name");
            vehicle_Name= ReservationBundle.getString("vehicle_Name");
            vehicle_No= ReservationBundle.getString("vehicle_No");
            vin_Num= ReservationBundle.getString("vin_Num");
            lic_Num= ReservationBundle.getString("lic_Num");
            res_Status= ReservationBundle.getString("res_Status");
            res_Status_BGColor= ReservationBundle.getString("res_Status_BGColor");

            lblFullname = view.findViewById(R.id.lblFullname);
            lblPhoneNo = view.findViewById(R.id.lblPhoneNo);
            lblCustEmail = view.findViewById(R.id.lblCustEmail);
            lblres_status = view.findViewById(R.id.lblres_status);
            lblCheckOutLocName = view.findViewById(R.id.lblCheckOutLocName);
            lblCheckOutDateTime = view.findViewById(R.id.lblCheckOutDateTime);
            lblcheckinLocName = view.findViewById(R.id.lblcheckinLocName);
            lblcheckIn_DateTime = view.findViewById(R.id.lblcheckIn_DateTime);
            lblVehicleFullname = view.findViewById(R.id.lblVehicleFullname);
            lblVehicleNo = view.findViewById(R.id.lblVehicleNo);
            lbl_VinNo = view.findViewById(R.id.lbl_VinNo);
            lbl_LicNo = view.findViewById(R.id.lbl_LicNo);
            ll_resStatus= view.findViewById(R.id.ll_resStatus);
            vehicle_Image= view.findViewById(R.id.vehicle_Image);
            Chk_TC= view.findViewById(R.id.Chk_TC);
            lblReservationNo= view.findViewById(R.id.lblReservationNo);

            lblDateTime= view.findViewById(R.id.lblDateTime);
            lblCancellationCharge= view.findViewById(R.id.lblCancellationCharge);
            lblAmountPaid= view.findViewById(R.id.lblAmountPaid);
            lblBalanceDue= view.findViewById(R.id.lblBalanceDue);
            nestedScrollView=view.findViewById(R.id.nestedscrollview);

            TextView lblCancellationpolicy=view.findViewById(R.id.lblCancellationpolicy);
            lblCancellationpolicy.setMovementMethod(new ScrollingMovementMethod());

            nestedScrollView.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    lblCancellationpolicy.getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
            });

            lblCancellationpolicy.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {

                    lblCancellationpolicy.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            Calendar c = Calendar .getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm a");
            String formattedDate = df.format(c.getTime());
            lblDateTime.setText(formattedDate);

            String VehicleImgstr=ReservationBundle.getString("veh_Img_Path");
            String url1 = serverpath+VehicleImgstr.substring(2);
            imageLoader.displayImage(url1,vehicle_Image);

            lblReservationNo.setText(ReservationBundle.getString("reservation_No"));

            lblFullname.setText(cust_Full_Name);
            lblPhoneNo.setText(cust_MobileNo);
            lblCustEmail.setText(cust_Email);
            lblCheckOutLocName.setText(chk_Out_Location_Name);

            //check_Out Date
            lblcheckinLocName.setText(chk_In_Loc_Name);

            String check_Out=(ReservationBundle.getString("check_Out"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
            Date date = dateFormat.parse(check_Out);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm aa", Locale.US);
            String check_OutStr = sdf.format(date);
            lblCheckOutDateTime.setText(check_OutStr);

            //check_IN Date
            String check_IN=(ReservationBundle.getString("check_IN"));
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
            Date date1 = dateFormat1.parse(check_IN);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm aa", Locale.US);
            String check_INStr = sdf1.format(date1);
            lblcheckIn_DateTime.setText(check_INStr);

            lblVehicleFullname.setText(vehicle_Name);
            lblVehicleNo.setText(vehicle_No);
            lbl_VinNo.setText(vin_Num);
            lbl_LicNo.setText(lic_Num);
            lblres_status.setText(res_Status);

            if (res_Status.equals("Close"))
            {
                ll_resStatus.setBackgroundTintList(ColorStateList.valueOf(parseColor(res_Status_BGColor)));
            }
            else
            {
                ll_resStatus.setBackgroundTintList(ColorStateList.valueOf(parseColor(res_Status_BGColor)));
            }

            Back = view.findViewById(R.id.Back);
            Back.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle Reservation = new Bundle();
                    Reservation.putBundle("ReservationBundle", ReservationBundle);
                    NavHostFragment.findNavController(Fragment_Cancel_Booking.this).
                            navigate(R.id.action_CancelBooking_to_SummaryOfCharges, Reservation);
                }
            });

            lbl_Process = view.findViewById(R.id.lbl_Process);

            String bodyParam = "";
            try
            {
                bodyParam+="reservationId="+reservation_ID;
                System.out.println(bodyParam);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            ApiService ApiService = new ApiService(GetBookingCancelCharge, RequestType.GET,
                    GET_BOOKING_CANCEL_CHARGE, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    OnResponseListener GetBookingCancelCharge = new OnResponseListener()
    {
        @Override
        public void onSuccess(final String response, HashMap<String, String> headers)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        System.out.println("Success");
                        System.out.println(response);
                        JSONObject responseJSON = new JSONObject(response);
                        Boolean status = responseJSON.getBoolean("status");
                        if (status)
                        {
                            JSONObject resultSet = responseJSON.getJSONObject("resultSet");
                            JSONObject test=resultSet.getJSONObject("data");
                           // int id=test.getInt("id");
                            int companyId=test.getInt("companyId");
                            int deposit=test.getInt("deposit");
                            int feeChargeOption=test.getInt("feeChargeOption");
                            boolean isAllowEarlyCheckin=test.getBoolean("isAllowEarlyCheckin");
                            boolean isAllowDateChanges=test.getBoolean("isAllowDateChanges");
                            boolean isAllowExtension=test.getBoolean("isAllowExtension");
                            boolean isDefault=test.getBoolean("isDefault");
                            boolean isActive=test.getBoolean("isActive");
                            int checkoutPendingDays=test.getInt("checkoutPendingDays");
                            int rate_ID=test.getInt("rate_ID");
                            int chargeNoOfDays=test.getInt("chargeNoOfDays");
                            double chargeAmount=test.getDouble("chargeAmount");
                            int chargeValue=test.getInt("chargeValue");
                            double security_Deposit=test.getDouble("security_Deposit");
                            double advance_Payment=test.getDouble("advance_Payment");
                            int reservationId=test.getInt("reservationId");
                            int chargePR=test.getInt("chargePR");
                            boolean isCancelBeforOrNoCharge=test.getBoolean("isCancelBeforOrNoCharge");

                            lblCancellationCharge.setText(String.valueOf(chargeAmount));
                            lblAmountPaid.setText(((String.format(Locale.US,"%.2f",advance_Payment))));
                            double BalanceDueAmount=(advance_Payment-chargeAmount)<=0?0:advance_Payment-chargeAmount;
                            lblBalanceDue.setText(String.valueOf(BalanceDueAmount));


                            lbl_Process.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    if(Chk_TC.isChecked())
                                    {

                                        JSONObject bodyParam = new JSONObject();
                                        try {
                                            bodyParam.accumulate("id", Integer.parseInt(id));
                                            bodyParam.accumulate("companyId", companyId);
                                            bodyParam.accumulate("deposit", deposit);
                                            bodyParam.accumulate("feeChargeOption", feeChargeOption);
                                            bodyParam.accumulate("isAllowEarlyCheckin", isAllowEarlyCheckin);
                                            bodyParam.accumulate("isAllowDateChanges", isAllowDateChanges);
                                            bodyParam.accumulate("isAllowExtension", isAllowExtension);
                                            bodyParam.accumulate("isDefault", isDefault);
                                            bodyParam.accumulate("isActive", isActive);
                                            bodyParam.accumulate("checkoutPendingDays", checkoutPendingDays);
                                            bodyParam.accumulate("rate_ID", rate_ID);
                                            bodyParam.accumulate("chargeNoOfDays", chargeNoOfDays);
                                            bodyParam.accumulate("chargeAmount", chargeAmount);
                                            bodyParam.accumulate("chargeValue", chargeValue);
                                            bodyParam.accumulate("security_Deposit", security_Deposit);
                                            bodyParam.accumulate("advance_Payment", advance_Payment);
                                            bodyParam.accumulate("reservationId", reservationId);
                                            bodyParam.accumulate("chargePR", chargePR);
                                            System.out.println(bodyParam);
                                            ApiService ApiService = new ApiService(ProceedCancellation, RequestType.POST,
                                                    PROCEED_CANCELLATION, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
                                        } catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                    else {
                                        CustomToast.showToast(getActivity(),"Please Accept Terms & Condition",1);
                                    }
                                }
                            });
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

    OnResponseListener ProceedCancellation = new OnResponseListener()
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

                            Bundle CancelBooking = new Bundle();
                            ReservationBundle.putInt("BookingStep", 5);
                            CancelBooking.putBundle("ReservationBundle", ReservationBundle);
                            CancelBooking.putBoolean("isDefaultCard", true);
                            NavHostFragment.findNavController(Fragment_Cancel_Booking.this)
                                    .navigate(R.id.action_CancelBooking_to_PayCheckoutForCancelBooking, CancelBooking);
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
