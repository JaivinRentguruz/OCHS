package com.ochs.app.flexiicar_app.booking;

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
import com.ochs.app.adapters.Helper;
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
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_PAYMENT;
import static com.ochs.app.apicall.ApiEndPoint.BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.GETDEFAULTCREDITCARD;
import static com.ochs.app.apicall.ApiEndPoint.PAYMENTPROCESS;

public class Fragment_Payment_checkout extends Fragment
{
    LinearLayout lblprocess;
    TextView lbleditcard,txtcardname,txtcardNumber,txtExpiryDate,lblmessage;
    ImageView imgback,CarImage;
    Handler handler = new Handler();
    public static Context context;
    public String id = "";
    Bundle BookingBundle,VehicleBundle;;
    TextView txt_PickLocName,txt_ReturnLocName,txt_PickupDate,txt_ReturnDate,txt_PickupTime,txt_ReturnTIme,txt_vehicletype,
            txt_vehName, txtAmtPayable;
    Double amountPayable;
    String transactionId;
    JSONObject creditCardJSON;
    Bundle returnLocationBundle, locationBundle;
    Boolean locationType, initialSelect,isDefaultCard;
    ImageLoader imageLoader;
    String serverpath="",VehImage="";
    int vechicleclass;

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
        return inflater.inflate(R.layout.fragment_payment_checkout_process, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        try{
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            id = sp.getString(getString(R.string.id), "");
            serverpath = sp.getString("serverPath", "");
            String cust_Email = sp.getString("cust_Email", "");
            vechicleclass = Integer.parseInt(sp.getString("vehicleClassWise", ""));

            initImageLoader(getActivity());
            imageLoader = ImageLoader.getInstance();

            lblprocess=view.findViewById(R.id.lblprocess);
            lbleditcard=view.findViewById(R.id.editcard);
            imgback=view.findViewById(R.id.backimg_payment);

            txtcardname=view.findViewById(R.id.txt_CardName);
            txtcardNumber=view.findViewById(R.id.txtCardNumber);
            txtExpiryDate=view.findViewById(R.id.txt_ExpiryDate);
            CarImage=view.findViewById(R.id.carimage);

            txt_PickLocName = view.findViewById(R.id.textView_PickupLocation);
            txt_PickupDate = view.findViewById(R.id.textView_PickupLocationDate);
            txt_PickupTime = view.findViewById(R.id.textView_PickupLocationTime);
            txt_ReturnLocName = view.findViewById(R.id.textView_ReturnLocationName);
            txt_ReturnDate = view.findViewById(R.id.textView_ReturnLocationDate);
            txt_ReturnTIme = view.findViewById(R.id.textView_ReturnLocationTime);
            txt_vehName=view.findViewById(R.id.textV_VehicleModelName);
            txt_vehicletype=view.findViewById(R.id.textV_VehicleTypeVName);
            txtAmtPayable = view.findViewById(R.id.txtAmtPayable);

            lblmessage= view.findViewById(R.id.lblmessage);
            lblmessage.setText("Once the payment is processed successfully,\n you will get your payment reference \n number. " +
                    "Payment confirmation email will \n been sent to " +cust_Email+ "\nPlease call customer service for any errors.");

            BookingBundle = getArguments().getBundle("BookingBundle");
            VehicleBundle = getArguments().getBundle("VehicleBundle");
            returnLocationBundle = getArguments().getBundle("returnLocation");
            locationBundle = getArguments().getBundle("location");
            locationType = getArguments().getBoolean("locationType");
            initialSelect = getArguments().getBoolean("initialSelect");
            isDefaultCard = getArguments().getBoolean("isDefaultCard");

            CarImage = view.findViewById(R.id.carimage);

            VehImage = VehicleBundle.getString("img_Path");

            String url1 = serverpath + VehImage;
            imageLoader.displayImage(url1, CarImage);

            txt_vehName.setText(VehicleBundle.getString("vehiclE_NAME"));
            txt_vehicletype.setText(VehicleBundle.getString("vehiclE_TYPE_NAME"));

            txt_PickLocName.setText(BookingBundle.getString("PickupLocName"));
            txt_ReturnLocName.setText(BookingBundle.getString("ReturnLocName"));


            String StrPickupDate = (BookingBundle.getString("PickupDate"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(StrPickupDate);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String PickUpDateStr = sdf.format(date);
            txt_PickupDate.setText(PickUpDateStr);

            String StrReturnDate = (BookingBundle.getString("ReturnDate"));
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = dateFormat1.parse(StrReturnDate);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            String ReturnDateStr = sdf1.format(date1);
            txt_ReturnDate.setText(ReturnDateStr);


            String strPickUpTime = (BookingBundle.getString("PickupTime"));
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
            Date date2 = dateFormat2.parse(strPickUpTime);
            SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa", Locale.US);
            String PickUpTimeStr = sdf2.format(date2);
            txt_PickupTime.setText(PickUpTimeStr);

            String strReturntime = (BookingBundle.getString("ReturnTime"));
            SimpleDateFormat dateFormat3 = new SimpleDateFormat("HH:mm");
            Date date3 = dateFormat3.parse(strReturntime);
            SimpleDateFormat sdf3 = new SimpleDateFormat("hh:mm aa", Locale.US);
            String ReturntimeStr = sdf3.format(date3);
            txt_ReturnTIme.setText(ReturntimeStr);

            amountPayable = BookingBundle.getDouble("total");
            txtAmtPayable.setText("$ "+((String.format(Locale.US,"%.2f",amountPayable))));

            lbleditcard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("transactionId",transactionId);
                    bundle.putBundle("VehicleBundle",VehicleBundle);
                    bundle.putBundle("BookingBundle",BookingBundle);
                    bundle.putBundle("returnLocation", returnLocationBundle);
                    bundle.putBundle("location", locationBundle);
                    bundle.putBoolean("locationType", locationType);
                    bundle.putBoolean("initialSelect", initialSelect);
                    NavHostFragment.findNavController(Fragment_Payment_checkout.this)
                            .navigate(R.id.action_Payment_checkout_to_CardsOnAccount, bundle);
                }
            });

            imgback.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle bundle = new Bundle();
                    BookingBundle.putInt("BookingStep", 4);
                    bundle.putBundle("BookingBundle", BookingBundle);
                    bundle.putBundle("VehicleBundle", VehicleBundle);
                    bundle.putBundle("returnLocation", returnLocationBundle);
                    bundle.putBundle("location", locationBundle);
                    bundle.putBoolean("locationType", locationType);
                    bundle.putBoolean("initialSelect", initialSelect);
                    NavHostFragment.findNavController(Fragment_Payment_checkout.this)
                            .navigate(R.id.action_Payment_checkout_to_Finalize_your_rental,bundle);
                }
            });

            if(!id.equals(""))
            {
                if (isDefaultCard)
                {
                    JSONObject bodyParam = new JSONObject();
                    try {
                        bodyParam.accumulate("ForTransId", BookingBundle.getInt("ForTransId"));
                        bodyParam.accumulate("PickupLocId", BookingBundle.getInt("PickupLocId"));
                        bodyParam.accumulate("ReturnLocId", BookingBundle.getInt("ReturnLocId"));
                        bodyParam.accumulate("CustomerId", BookingBundle.getInt("CustomerId"));
                        bodyParam.accumulate("VehicleTypeId", BookingBundle.getInt("vehiclE_TYPE_ID"));
                        bodyParam.accumulate("VehicleID", BookingBundle.getInt("VehicleID"));
                        bodyParam.accumulate("StrFilterVehicleTypeIds", BookingBundle.getString("StrFilterVehicleTypeIds"));
                        bodyParam.accumulate("StrFilterVehicleOptionIds", BookingBundle.getString("StrFilterVehicleOptionIds"));

                        bodyParam.accumulate("PickupDate", BookingBundle.getString("PickupDate"));
                        bodyParam.accumulate("ReturnDate", BookingBundle.getString("ReturnDate"));
                        bodyParam.accumulate("PickupTime", BookingBundle.getString("PickupTime"));
                        bodyParam.accumulate("ReturnTime", BookingBundle.getString("ReturnTime"));

                        bodyParam.accumulate("FilterTransmission", BookingBundle.getInt("FilterTransmission"));
                        bodyParam.accumulate("FilterPassengers", BookingBundle.getInt("FilterPassengers"));
                        bodyParam.accumulate("BookingStep", BookingBundle.getInt("BookingStep"));
                        bodyParam.accumulate("BookingType", BookingBundle.getInt("BookingType"));
                        bodyParam.accumulate("DeliveryChargeLocID", BookingBundle.getInt("DeliveryChargeLocID"));
                        bodyParam.accumulate("DeliveryChargeAmount", BookingBundle.getDouble("DeliveryChargeAmount"));
                        bodyParam.accumulate("PickupChargeLocID", BookingBundle.getInt("PickupChargeLocID"));
                        bodyParam.accumulate("PickupChargeAmount", BookingBundle.getDouble("PickupChargeAmount"));
                        bodyParam.accumulate("EquipmentList", new JSONArray(BookingBundle.getString("EquipmentList")));
                        bodyParam.accumulate("MiscList", new JSONArray(BookingBundle.getString("MiscList")));
                        bodyParam.accumulate("SummaryOfCharges", new JSONArray(BookingBundle.getString("SummaryOfCharges")));
                        bodyParam.accumulate("VehicleClassWise",vechicleclass);
                        // bodyParam.accumulate("DeliveryAndPickupModel", new JSONArray(BookingBundle.getString("DeliveryAndPickupModel")));

                        if (Helper.insuranceValue){
                            JSONObject customerInsurance  = new JSONObject();
                            customerInsurance.accumulate("CustomerId", id);
                            customerInsurance.accumulate("Insurance_Cmp_ID", Helper.insuranceModel.insurancecompanyid);
                            customerInsurance.accumulate("Insurance_Cmp_Name", Helper.insuranceModel.insurance_Cmp_Name);
                            customerInsurance.accumulate("Policy_No", Helper.insuranceModel.policy_No);
                            customerInsurance.accumulate("InsIssueDate", Helper.insuranceModel.insIssueDate);
                            customerInsurance.accumulate("ExpiryDate", Helper.insuranceModel.expiryDate);
                            bodyParam.accumulate("customerInsurance ", customerInsurance);
                        }

                        System.out.println(bodyParam);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    AndroidNetworking.initialize(getActivity());
                    Fragment_Finalize_Your_Rental.context = getActivity();

                    ApiService ApiService1 = new ApiService(getTransactionId, RequestType.POST,
                            BOOKING, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
                }

                else {
                    try {
                        String creditCardJSONStr = getArguments().getString("creditcard");
                        creditCardJSON = new JSONObject(creditCardJSONStr);

                        transactionId = getArguments().getString("transactionId");

                        String card_No = creditCardJSON.getString("card_No");
                        String card_Name = creditCardJSON.getString("card_Name");
                        String expiry_Date = creditCardJSON.getString("expiry_Date");

                        txtcardname.setText(card_Name);
                        txtcardNumber.setText("**** **** **** " + card_No.substring(card_No.length() - 4));
                        txtExpiryDate.setText(expiry_Date);

                        lblprocess.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                JSONObject bodyParam3 = new JSONObject();
                                try {
                                    bodyParam3.accumulate("ForTransId", Integer.parseInt(transactionId));
                                    bodyParam3.accumulate("CardName", creditCardJSON.get("card_Name"));
                                    bodyParam3.accumulate("CreditCardNo", creditCardJSON.getString("card_No"));
                                    bodyParam3.accumulate("ExpiryDate", creditCardJSON.getString("expiry_Date"));
                                    bodyParam3.accumulate("CVSNo", creditCardJSON.getString("cvS_Code"));
                                    bodyParam3.accumulate("Amount", BookingBundle.getDouble("total"));
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
                                } catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
            else  {
                Bundle bundle = new Bundle();
                bundle.putString("transactionId",transactionId);
                bundle.putDouble("total",amountPayable);
                bundle.putBundle("VehicleBundle",VehicleBundle);
                bundle.putBundle("BookingBundle",BookingBundle);
                bundle.putBundle("returnLocation", returnLocationBundle);
                bundle.putBundle("location", locationBundle);
                bundle.putBoolean("locationType", locationType);
                bundle.putBoolean("initialSelect", initialSelect);
                NavHostFragment.findNavController(Fragment_Payment_checkout.this)
                        .navigate(R.id.action_Payment_checkout_to_AddCreditCardDetails, bundle);

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
                                JSONObject resultSet = responseJSON.getJSONObject("resultSet");

                                transactionId = responseJSON.getString("transactionId");
                                String message = responseJSON.getString("message");

                                BookingBundle.putInt("ForTransId",Integer.parseInt(transactionId));
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
                                else
                                {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("transactionId",transactionId);
                                    bundle.putDouble("total",amountPayable);
                                    bundle.putBundle("VehicleBundle",VehicleBundle);
                                    bundle.putBundle("BookingBundle",BookingBundle);
                                    bundle.putBundle("returnLocation", returnLocationBundle);
                                    bundle.putBundle("location", locationBundle);
                                    bundle.putBoolean("locationType", locationType);
                                    bundle.putBoolean("initialSelect", initialSelect);
                                    NavHostFragment.findNavController(Fragment_Payment_checkout.this)
                                            .navigate(R.id.action_Payment_checkout_to_AddCreditCardDetails, bundle);
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
                                            bodyParam3.accumulate("Amount", BookingBundle.getDouble("total"));
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

                                Bundle paymentBundle = new Bundle();
                                paymentBundle.putInt("sendTo",1);
                                paymentBundle.putString("message",message);
                                paymentBundle.putString("transactionId",transactionId);
                                paymentBundle.putDouble("total",BookingBundle.getDouble("total"));
                                paymentBundle.putBundle("returnLocation", returnLocationBundle);
                                paymentBundle.putBundle("location", locationBundle);
                                paymentBundle.putBoolean("locationType", locationType);
                                paymentBundle.putBoolean("initialSelect", initialSelect);
                                paymentBundle.putBundle("BookingBundle", BookingBundle);
                                paymentBundle.putBundle("VehicleBundle", VehicleBundle);
                                NavHostFragment.findNavController(Fragment_Payment_checkout.this)
                                        .navigate(R.id.action_Payment_checkout_to_Payment_Success, paymentBundle);
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
/*
                    String dateFormatPickupDate = (BookingBundle.getString("PickupDate"));
                    String strPickUpTime1 = (BookingBundle.getString("PickupTime"));
                    String PickupDateTime = dateFormatPickupDate + "T" + strPickUpTime1;

                    bodyParam.accumulate("PickupDate", PickupDateTime);

                    String dateFormatReturnDate = (BookingBundle.getString("ReturnDate"));
                    String strReturnTime1 = (BookingBundle.getString("ReturnTime"));
                    String ReturnDateTime = dateFormatReturnDate + "T" + strReturnTime1;

                    bodyParam.accumulate("ReturnDate", ReturnDateTime);*/

