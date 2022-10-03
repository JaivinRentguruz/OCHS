package com.ochs.app.flexiicar_app.user;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import com.ochs.app.flexiicar_app.booking.Fragment_Payment_checkout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_PAYMENT;
import static com.ochs.app.apicall.ApiEndPoint.GETDEFAULTCREDITCARD;
import static com.ochs.app.apicall.ApiEndPoint.PAYMENTPROCESS;

public class Fragment_TollCharge_Image extends Fragment
{
    ImageView backarrow;
    TextView Payment_Total,Payment_TotalAmount,txt_Payment_Status;
    String serverpath="";
    Bundle PaymentBundle;
    WebView web_view;
    public String id = "";
    TextView txt_CardNo,txt_CardExDate;
    Handler handler = new Handler();
    LinearLayout lblPay;
    JSONObject creditCardJSON;
    String transactionId;
    LinearLayout llForUnpaid,llforPaid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tollcharge_image, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        try{
            backarrow=view.findViewById(R.id.back_tollcharge);
            web_view =  view.findViewById(R.id.webview3);
            Payment_Total = view.findViewById(R.id.Payment_Total1);
            txt_CardNo=view.findViewById(R.id.txtCard_No1);
            txt_CardExDate=view.findViewById(R.id.Card_ExDate1);
            lblPay=view.findViewById(R.id.Lbl_Payment);

            llforPaid=view.findViewById(R.id.llpaidTollcharge);
            llForUnpaid=view.findViewById(R.id.llUnpaidTollcharge);
            txt_Payment_Status=view.findViewById(R.id.txt_PaymentStatusToll);
            Payment_TotalAmount=view.findViewById(R.id.tollcharge_TotalAmount);
            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            serverpath = sp.getString("serverPath", "");
            id = sp.getString(getString(R.string.id), "");

            PaymentBundle = getArguments().getBundle("PaymentBundle");
            System.out.println(PaymentBundle);

            String pdfFileName = (PaymentBundle.getString("path"));
            Double TotalAmount = (PaymentBundle.getDouble("totalAmount"));
            int billStatus= (PaymentBundle.getInt("billStatus"));

            if(billStatus==1)
            {
                txt_Payment_Status.setText("PAID");
                txt_Payment_Status.setTextColor(getResources().getColor(R.color.footerButtonBC));
                llforPaid.setVisibility(View.VISIBLE);
            }
            else
            {
                txt_Payment_Status.setText("UNPAID");
                txt_Payment_Status.setTextColor(getResources().getColor(R.color.btn2));
                llForUnpaid.setVisibility(View.VISIBLE);
            }

            //forUnpaid
            Payment_Total.setText(((String.format(Locale.US, "%.2f", TotalAmount))));
            //forpaid
            Payment_TotalAmount.setText(((String.format(Locale.US, "%.2f", TotalAmount))));

            String url1 = serverpath + pdfFileName;
            System.out.println(url1);
            web_view.setWebViewClient(new WebViewClient());
            web_view.getSettings().setJavaScriptEnabled(true);
            web_view.getSettings().setLoadWithOverviewMode(true);
            web_view.getSettings().setUseWideViewPort(true);
            web_view.getSettings().setAllowFileAccess(true);
            web_view.loadUrl(url1);

            TextView changeCreditCard = view.findViewById(R.id.changeCreditCard);
            changeCreditCard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle bundle = new Bundle();
                    bundle.putInt("backTo",2);
                    bundle.putBundle("PaymentBundle",PaymentBundle);
                    NavHostFragment.findNavController(Fragment_TollCharge_Image.this)
                            .navigate(R.id.action_TollCharge_to_CardsOnAccount, bundle);
                }
            });

            backarrow.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("StatmentType",true);
                    NavHostFragment.findNavController(Fragment_TollCharge_Image.this)
                            .navigate(R.id.action_TollCharge_Image_to_Bills_and_Payment,bundle);
                }
            });
            String bodyParam1 = "";
            try
            {
                bodyParam1 += "customerId=" + id;

                System.out.println(bodyParam1);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            try {
                String creditCardJSONStr = getArguments().getString("creditcard");

                creditCardJSON = new JSONObject(creditCardJSONStr);

                String card_No = creditCardJSON.getString("card_No");
                String card_Name = creditCardJSON.getString("card_Name");
                String expiry_Date = creditCardJSON.getString("expiry_Date");

                //txtcardname.setText(card_Name);
                txt_CardNo.setText("**** **** **** " + card_No.substring(card_No.length() - 4));
                txt_CardExDate.setText(expiry_Date);

                lblPay.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        JSONObject bodyParam3 = new JSONObject();
                        try
                        {
                            bodyParam3.accumulate("ForTranId", PaymentBundle.getInt("forTranID"));
                            bodyParam3.accumulate("billID", PaymentBundle.getInt("billID"));
                            bodyParam3.accumulate("CardName", creditCardJSON.get("card_Name"));
                            bodyParam3.accumulate("CreditCardNo", creditCardJSON.getString("card_No"));
                            bodyParam3.accumulate("ExpiryDate", creditCardJSON.getString("expiry_Date"));
                            bodyParam3.accumulate("CVSNo", creditCardJSON.getString("cvS_Code"));
                            bodyParam3.accumulate("Amount",PaymentBundle.getDouble("totalAmount"));
                            bodyParam3.accumulate("Street", creditCardJSON.getString("card_PStreet"));
                            bodyParam3.accumulate("UnitNo", creditCardJSON.getString("card_PUnitNo"));
                            bodyParam3.accumulate("City", creditCardJSON.getString("card_PCity"));
                            bodyParam3.accumulate("CountryID", creditCardJSON.getInt("card_PCountry"));
                            bodyParam3.accumulate("StateID", creditCardJSON.getInt("card_PState"));
                            bodyParam3.accumulate("ZipCode", creditCardJSON.getString("card_SZipCode"));
                            bodyParam3.accumulate("TransType", 0);
                            bodyParam3.accumulate("ChargeType", 0);
                            bodyParam3.accumulate("Type", 1);
                            bodyParam3.accumulate("Remark", "");
                            bodyParam3.accumulate("CardType", "visa");
                            bodyParam3.accumulate("CountryCode", "CA");
                            bodyParam3.accumulate("StateName", "ONTARIO");
                            bodyParam3.accumulate("MobileNumber", "9921023213");
                            bodyParam3.accumulate("CurrencyISO", "USD");
                            bodyParam3.accumulate("CustomerId", Integer.parseInt(id));
                            bodyParam3.accumulate("Email", "info@customer.com");

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
                AndroidNetworking.initialize(getActivity());
                Fragment_Payment_checkout.context = getActivity();

                ApiService ApiService = new ApiService(GetDefaultCreditCard, RequestType.GET,
                        GETDEFAULTCREDITCARD, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam1);

            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }
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
                                creditCardJSON = resultSet.getJSONObject("t0050_Customer_Card_Details");


                                final int card_ID=creditCardJSON.getInt("card_ID");
                                final int customer_ID=creditCardJSON.getInt("customer_ID");
                                final String card_Type_ID=creditCardJSON.getString("card_Type_ID");
                                final String card_No=creditCardJSON.getString("card_No");
                                final String card_Name=creditCardJSON.getString("card_Name");
                                final String expiry_Date=creditCardJSON.getString("expiry_Date");
                                final String cvS_Code=creditCardJSON.getString("cvS_Code");

                                txt_CardNo.setText("**** **** **** "+card_No.substring(card_No.length()-4));
                                txt_CardExDate.setText("("+expiry_Date+")");

                                lblPay.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {

                                        JSONObject bodyParam3 = new JSONObject();
                                        try
                                        {
                                            bodyParam3.accumulate("ForTranId", PaymentBundle.getInt("forTranID"));
                                            bodyParam3.accumulate("billID", PaymentBundle.getInt("billID"));
                                            bodyParam3.accumulate("CardName", creditCardJSON.get("card_Name"));
                                            bodyParam3.accumulate("CreditCardNo", creditCardJSON.getString("card_No"));
                                            bodyParam3.accumulate("ExpiryDate", creditCardJSON.getString("expiry_Date"));
                                            bodyParam3.accumulate("CVSNo", creditCardJSON.getString("cvS_Code"));
                                            bodyParam3.accumulate("Amount",PaymentBundle.getDouble("totalAmount"));
                                            bodyParam3.accumulate("Street", creditCardJSON.getString("card_PStreet"));
                                            bodyParam3.accumulate("UnitNo", creditCardJSON.getString("card_PUnitNo"));
                                            bodyParam3.accumulate("City", creditCardJSON.getString("card_PCity"));
                                            bodyParam3.accumulate("CountryID", creditCardJSON.getInt("card_PCountry"));
                                            bodyParam3.accumulate("StateID", creditCardJSON.getInt("card_PState"));
                                            bodyParam3.accumulate("ZipCode", creditCardJSON.getString("card_SZipCode"));
                                            bodyParam3.accumulate("TransType", 0);
                                            bodyParam3.accumulate("ChargeType", 0);
                                            bodyParam3.accumulate("Type", 1);
                                            bodyParam3.accumulate("Remark", "");
                                            bodyParam3.accumulate("CardType", "visa");
                                            bodyParam3.accumulate("CountryCode", "CA");
                                            bodyParam3.accumulate("StateName", "ONTARIO");
                                            bodyParam3.accumulate("MobileNumber", "9921023213");
                                            bodyParam3.accumulate("CurrencyISO", "USD");
                                            bodyParam3.accumulate("CustomerId", Integer.parseInt(id));
                                            bodyParam3.accumulate("Email", "info@customer.com");

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
        public void onError(String error) {
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
                                paymentBundle.putInt("sendTo",2);
                                paymentBundle.putString("message",message);
                                paymentBundle.putString("transactionId",transactionId);
                                paymentBundle.putDouble("total",PaymentBundle.getDouble("totalAmount"));

                                NavHostFragment.findNavController(Fragment_TollCharge_Image.this)
                                        .navigate(R.id.action_TollCharge_to_Payment_Success, paymentBundle);
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
        public void onError(String error) {
            System.out.println("Error-" + error);
        }
    };
}

