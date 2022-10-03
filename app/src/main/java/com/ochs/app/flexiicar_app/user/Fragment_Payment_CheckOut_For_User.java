package com.ochs.app.flexiicar_app.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.ochs.app.flexiicar_app.booking.Fragment_Finalize_Your_Rental;
import com.ochs.app.flexiicar_app.booking.Fragment_Payment_checkout;
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
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_PAYMENT;
import static com.ochs.app.apicall.ApiEndPoint.BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.GETDEFAULTCREDITCARD;
import static com.ochs.app.apicall.ApiEndPoint.PAYMENTPROCESS;

public class Fragment_Payment_CheckOut_For_User extends Fragment
{
    LinearLayout lblprocess;
    TextView lbleditcard,txtcardname,txtcardNumber,txtExpiryDate,lblmessage;
    ImageView imgback;
    Handler handler = new Handler();
    public static Context context;
    public String id = "";
    Bundle ReservationBundle;
    TextView txt_PickLocName,txt_ReturnLocName,txt_PickupDate,txt_ReturnDate,txt_vehicletype,
            txt_vehName, txtAmtPayable;
    String transactionId;
    JSONObject creditCardJSON;
    ImageLoader imageLoader;
    String serverpath="";
    Boolean isDefaultCard;

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_checkout_process, container, false);
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        try {
            super.onViewCreated(view, savedInstanceState);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            ((User_Profile) getActivity()).BottomnavInVisible();

            initImageLoader(getActivity());
            imageLoader = ImageLoader.getInstance();

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            id = sp.getString(getString(R.string.id), "");
            serverpath = sp.getString("serverPath", "");
            String cust_Email = sp.getString("cust_Email", "");

            ReservationBundle = getArguments().getBundle("ReservationBundle");
            isDefaultCard = getArguments().getBoolean("isDefaultCard");

            ImageView CarImage=view.findViewById(R.id.carimage);

            String VehicleImgstr=ReservationBundle.getString("veh_Img_Path");
            String url1 = serverpath+VehicleImgstr.substring(2);
            imageLoader.displayImage(url1,CarImage);

            lblprocess = view.findViewById(R.id.lblprocess);
            lbleditcard = view.findViewById(R.id.editcard);
            imgback = view.findViewById(R.id.backimg_payment);

            txtcardname = view.findViewById(R.id.txt_CardName);
            txtcardNumber = view.findViewById(R.id.txtCardNumber);
            txtExpiryDate = view.findViewById(R.id.txt_ExpiryDate);

            txt_PickLocName = view.findViewById(R.id.textView_PickupLocation);
            txt_PickupDate = view.findViewById(R.id.textView_PickupLocationDate);
            txt_ReturnLocName = view.findViewById(R.id.textView_ReturnLocationName);
            txt_ReturnDate = view.findViewById(R.id.textView_ReturnLocationDate);

            txt_vehName = view.findViewById(R.id.textV_VehicleModelName);
            txt_vehicletype = view.findViewById(R.id.textV_VehicleTypeVName);

            txtAmtPayable = view.findViewById(R.id.txtAmtPayable);
            txt_vehName.setText(ReservationBundle.getString("vehicle_Name"));
            txt_vehicletype.setText(ReservationBundle.getString("vehicle_Type_Name"));

            txt_PickLocName.setText(ReservationBundle.getString("chk_Out_Location_Name"));
            txt_ReturnLocName.setText(ReservationBundle.getString("chk_In_Loc_Name"));

            String CheckOut=(ReservationBundle.getString("check_Out"));
            // Date
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
            Date date1 = dateFormat1.parse(CheckOut);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm aa", Locale.US);
            String CheckOutStr = sdf1.format(date1);
            txt_PickupDate.setText(CheckOutStr);

            String CheckIn=(ReservationBundle.getString("check_IN"));
            //check_Out Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
            Date date = dateFormat.parse(CheckIn);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm aa", Locale.US);
            String CheckInStr = sdf.format(date);
            txt_ReturnDate.setText(CheckInStr);

            final Double amountPayable = ReservationBundle.getDouble("total");
            txtAmtPayable.setText("$ "+((String.format(Locale.US,"%.2f",amountPayable))));

            lblmessage= view.findViewById(R.id.lblmessage);
            lblmessage.setText("Once the payment is processed successfully,\n you will get your payment reference \n number. " +
                    "Payment confirmation email will \n been sent to " +cust_Email+ "\nPlease call customer service for any errors.");

            lbleditcard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle Reservation = new Bundle();
                    Reservation.putInt("backTo",6);
                    Reservation.putString("transactionId",transactionId);
                    Reservation.putString("total",txtAmtPayable.getText().toString());
                    Reservation.putBundle("ReservationBundle",ReservationBundle);
                    System.out.println(Reservation);
                    NavHostFragment.findNavController(Fragment_Payment_CheckOut_For_User.this)
                            .navigate(R.id.action_Payment_checkout_to_CardsOnAccount, Reservation);
                }
            });

            imgback.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle Booking = new Bundle();
                    ReservationBundle.putInt("BookingStep", 4);
                    Booking.putBundle("ReservationBundle",ReservationBundle);
                    NavHostFragment.findNavController(Fragment_Payment_CheckOut_For_User.this)
                            .navigate(R.id.action_Payment_checkout_to_Finalize_your_rental, Booking);
                }
            });

            String default_Check_Out3=(ReservationBundle.getString("default_Check_Out"));
            SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            Date date3 = dateFormat3.parse(default_Check_Out3);
            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String CheckOutStr1 = sdf3.format(date3);

            String default_Check_In3=(ReservationBundle.getString("default_Check_In"));
            SimpleDateFormat dateFormat4 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            Date date4= dateFormat4.parse(default_Check_In3);
            SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String CheckInStr1 = sdf4.format(date4);

            if(!id.equals(""))
            {
                if (isDefaultCard)
                {
                    try {
                        JSONObject bodyParam = new JSONObject();
                        try {
                            bodyParam.accumulate("ForTransId",ReservationBundle.getInt("reservation_ID"));
                            bodyParam.accumulate("CustomerId", ReservationBundle.getInt("customer_ID"));
                            bodyParam.accumulate("VehicleTypeId", ReservationBundle.getInt("vehicle_Type_ID"));
                            bodyParam.accumulate("VehicleID", ReservationBundle.getInt("vehicle_ID"));
                            bodyParam.accumulate("BookingStep", ReservationBundle.getInt("BookingStep"));

                            bodyParam.accumulate("PickupLocId", ReservationBundle.getInt("check_IN_Location"));
                            bodyParam.accumulate("ReturnLocId", ReservationBundle.getInt("check_Out_Location"));

                            bodyParam.accumulate("PickupDate", CheckOutStr1);
                            bodyParam.accumulate("ReturnDate", CheckInStr1);

                            bodyParam.accumulate("DeliveryChargeLocID", ReservationBundle.getInt("DeliveryChargeLocID"));
                            bodyParam.accumulate("DeliveryChargeAmount", ReservationBundle.getDouble("DeliveryChargeAmount"));
                            bodyParam.accumulate("PickupChargeLocID", ReservationBundle.getInt("PickupChargeLocID"));
                            bodyParam.accumulate("PickupChargeAmount", ReservationBundle.getDouble("PickupChargeAmount"));
                            bodyParam.accumulate("EquipmentList", new JSONArray(ReservationBundle.getString("EquipmentList")));
                            bodyParam.accumulate("MiscList", new JSONArray(ReservationBundle.getString("MiscList")));
                            bodyParam.accumulate("SummaryOfCharges", new JSONArray(ReservationBundle.getString("summaryOfCharges")));
                            //bodyParam.accumulate("DeliveryAndPickupModel", new JSONArray(ReservationBundle.getString("DeliveryAndPickupModel")));
                            System.out.println(bodyParam);

                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        AndroidNetworking.initialize(getActivity());

                        ApiService ApiService1 = new ApiService(getTransactionId, RequestType.POST,
                                BOOKING, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                else {
                    String creditCardJSONStr = getArguments().getString("creditcard");
                    creditCardJSON = new JSONObject(creditCardJSONStr);

                    transactionId = getArguments().getString("transactionId");

                    String card_No=creditCardJSON.getString("card_No");
                    String card_Name=creditCardJSON.getString("card_Name");
                    String expiry_Date=creditCardJSON.getString("expiry_Date");

                    txtcardname.setText(card_Name);
                    txtcardNumber.setText("**** **** **** "+card_No.substring(card_No.length()-4));
                    txtExpiryDate.setText(expiry_Date);

                    lblprocess.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            JSONObject bodyParam3 = new JSONObject();
                            try
                            {
                                bodyParam3.accumulate("ForTranId",Integer.parseInt(transactionId));
                                bodyParam3.accumulate("CardName", creditCardJSON.get("card_Name"));
                                bodyParam3.accumulate("CreditCardNo", creditCardJSON.getString("card_No"));
                                bodyParam3.accumulate("ExpiryDate", creditCardJSON.getString("expiry_Date"));
                                bodyParam3.accumulate("CVSNo", creditCardJSON.getString("cvS_Code"));
                                bodyParam3.accumulate("Amount", ReservationBundle.getDouble("total"));
                                bodyParam3.accumulate("Street", creditCardJSON.getString("card_PStreet"));
                                bodyParam3.accumulate("UnitNo", creditCardJSON.getString("card_PUnitNo"));
                                bodyParam3.accumulate("City", creditCardJSON.getString("card_PCity"));
                                bodyParam3.accumulate("CountryID", creditCardJSON.getInt("card_PCountry"));
                                bodyParam3.accumulate("StateID", creditCardJSON.getInt("card_PState"));
                                bodyParam3.accumulate("ZipCode", creditCardJSON.getString("card_SZipCode"));
                                bodyParam3.accumulate("TransType", 0);
                                bodyParam3.accumulate("ChargeType", 0);
                                bodyParam3.accumulate("Type", 0);
                                bodyParam3.accumulate("Remark", "");
                                bodyParam3.accumulate("CardType", "visa");
                                bodyParam3.accumulate("CountryCode", "CA");
                                bodyParam3.accumulate("StateName", "ONTARIO");
                                bodyParam3.accumulate("MobileNumber", "9921023213");
                                bodyParam3.accumulate("CurrencyISO", "USD");
                                bodyParam3.accumulate("CustomerId", Integer.parseInt(id));
                                bodyParam3.accumulate("Email", "info@customer.com");
                                System.out.println(bodyParam3);
                                ApiService ApiService1 = new ApiService(processPayment, RequestType.POST,
                                        PAYMENTPROCESS, BASE_URL_PAYMENT, new HashMap<String, String>(), bodyParam3);
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    OnResponseListener getTransactionId = new OnResponseListener()
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
                                transactionId = responseJSON.getString("transactionId");
                                String message = responseJSON.getString("message");
                                CustomToast.showToast(getActivity(),message,0);

                                ReservationBundle.putInt("ForTransId",Integer.parseInt(transactionId));
                                CustomToast.showToast(getActivity(),message,0);


                                if(!id.equals(""))
                                {
                                    String bodyParam1 = "";
                                    try
                                    {
                                        bodyParam1 += "customerId=" + id;
                                        System.out.println(bodyParam1);
                                    } catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                    AndroidNetworking.initialize(getActivity());
                                    Fragment_Payment_checkout.context = getActivity();

                                    ApiService ApiService = new ApiService(GetDefaultCreditCard, RequestType.GET,
                                            GETDEFAULTCREDITCARD, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam1);
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
        public void onError(String error)
        {
            System.out.println("Error-" + error);
        }
    };

    OnResponseListener GetDefaultCreditCard = new OnResponseListener()
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
                                final JSONObject creditcardDetails= resultSet.getJSONObject("t0050_Customer_Card_Details");

                                creditCardJSON = creditcardDetails;

                                int card_ID=creditcardDetails.getInt("card_ID");
                                String card_No=creditcardDetails.getString("card_No");
                                String card_Name=creditcardDetails.getString("card_Name");
                                String expiry_Date=creditcardDetails.getString("expiry_Date");

                                txtcardname.setText(card_Name);
                                txtcardNumber.setText("**** **** **** "+card_No.substring(card_No.length()-4));
                                txtExpiryDate.setText(expiry_Date);

                                lblprocess.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        JSONObject bodyParam3 = new JSONObject();
                                        try
                                        {
                                            bodyParam3.accumulate("ForTransId", Integer.parseInt(transactionId));
                                            bodyParam3.accumulate("CardName", creditCardJSON.get("card_Name"));
                                            bodyParam3.accumulate("CreditCardNo", creditCardJSON.getString("card_No"));
                                            bodyParam3.accumulate("ExpiryDate", creditCardJSON.getString("expiry_Date"));
                                            bodyParam3.accumulate("CVSNo", creditCardJSON.getString("cvS_Code"));
                                            bodyParam3.accumulate("Amount", ReservationBundle.getDouble("total"));
                                            bodyParam3.accumulate("Street", creditCardJSON.getString("card_PStreet"));
                                            bodyParam3.accumulate("UnitNo", creditCardJSON.getString("card_PUnitNo"));
                                            bodyParam3.accumulate("City", creditCardJSON.getString("card_PCity"));
                                            bodyParam3.accumulate("CountryID", creditCardJSON.getInt("card_PCountry"));
                                            bodyParam3.accumulate("StateID", creditCardJSON.getInt("card_PState"));
                                            bodyParam3.accumulate("ZipCode", creditCardJSON.getString("card_SZipCode"));
                                            bodyParam3.accumulate("TransType", 0);
                                            bodyParam3.accumulate("ChargeType", 0);
                                            bodyParam3.accumulate("Type", 0);
                                            bodyParam3.accumulate("Remark", "");
                                            bodyParam3.accumulate("CardType", "visa");
                                            bodyParam3.accumulate("CountryCode", "CA");
                                            bodyParam3.accumulate("StateName", "ONTARIO");
                                            bodyParam3.accumulate("MobileNumber", "9921023213");
                                            bodyParam3.accumulate("CurrencyISO", "USD");
                                            bodyParam3.accumulate("CustomerId", Integer.parseInt(id));
                                            bodyParam3.accumulate("Email", "info@customer.com");
                                            System.out.println(bodyParam3);
                                            System.out.println(creditCardJSON);
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                        ApiService ApiService1 = new ApiService(processPayment, RequestType.POST,
                                                PAYMENTPROCESS, BASE_URL_PAYMENT, new HashMap<String, String>(), bodyParam3);
                                    }
                                });
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
        public void onError(String error)
        {
            System.out.println("Error-" + error);
        }
    };

    OnResponseListener processPayment = new OnResponseListener()
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
                                transactionId = responseJSON.getString("transactionId");
                                String message = responseJSON.getString("message");
                                CustomToast.showToast(getActivity(),message,0);

                                Bundle Reservation = new Bundle();
                                Reservation.putInt("sendTo",3);
                                Reservation.putString("message",message);
                                Reservation.putString("transactionId",transactionId);
                                Reservation.putDouble("total",ReservationBundle.getDouble("total"));
                                Reservation.putBundle("ReservationBundle", ReservationBundle);
                                NavHostFragment.findNavController(Fragment_Payment_CheckOut_For_User.this)
                                        .navigate(R.id.action_Payment_checkout_to_Payment_Success, Reservation);
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
        public void onError(String error)
        {
            System.out.println("Error-" + error);
        }
    };
}

/*  catch (Exception e1)
            {
                String bodyParam1 = "";
                try
                {
                    bodyParam1 += "customerId=" + id;

                    System.out.println(bodyParam1);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                AndroidNetworking.initialize(getActivity());
                Fragment_Payment_checkout.context = getActivity();

                ApiService ApiService = new ApiService(GetDefaultCreditCard, RequestType.GET,
                        GETDEFAULTCREDITCARD, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam1);
            }
*/
/*  lblprocess.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        JSONObject bodyParam3 = new JSONObject();
                                        try
                                        {
                                            bodyParam3.accumulate("ForTranId", Integer.parseInt(transactionId));
                                            bodyParam3.accumulate("CardName", creditCardJSON.get("card_Name"));
                                            bodyParam3.accumulate("CreditCardNo", creditCardJSON.getString("card_No"));
                                            bodyParam3.accumulate("ExpiryDate", creditCardJSON.getString("expiry_Date"));
                                            bodyParam3.accumulate("CVSNo", creditCardJSON.getString("cvS_Code"));

                                            bodyParam3.accumulate("Amount", ReservationBundle.getDouble("total"));

                                            bodyParam3.accumulate("Street", creditCardJSON.getString("card_PStreet"));
                                            bodyParam3.accumulate("UnitNo", creditCardJSON.getString("card_PUnitNo"));
                                            bodyParam3.accumulate("City", creditCardJSON.getString("card_PCity"));
                                            bodyParam3.accumulate("CountryID", creditCardJSON.getInt("card_PCountry"));
                                            bodyParam3.accumulate("StateID", creditCardJSON.getInt("card_PState"));
                                            bodyParam3.accumulate("ZipCode", creditCardJSON.getString("card_SZipCode"));
                                            bodyParam3.accumulate("TransType", 0);
                                            bodyParam3.accumulate("ChargeType", 0);
                                            bodyParam3.accumulate("Type", 0);
                                            bodyParam3.accumulate("Remark", "");
                                            bodyParam3.accumulate("CardType", "visa");
                                            bodyParam3.accumulate("CountryCode", "CA");
                                            bodyParam3.accumulate("StateName", "ONTARIO");
                                            bodyParam3.accumulate("MobileNumber", "9921023213");
                                            bodyParam3.accumulate("CurrencyISO", "USD");
                                            bodyParam3.accumulate("CustomerId", Integer.parseInt(id));
                                            bodyParam3.accumulate("Email", "info@customer.com");

                                            System.out.println(creditCardJSON);

                                            ApiService ApiService1 = new ApiService(processPayment, RequestType.POST,
                                                    PAYMENTPROCESS, BASE_URL_PAYMENT, new HashMap<String, String>(), bodyParam3);
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                })*/