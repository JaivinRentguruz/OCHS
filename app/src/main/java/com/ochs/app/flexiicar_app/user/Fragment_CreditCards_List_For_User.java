package com.ochs.app.flexiicar_app.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.Card.CardChecking;
import com.ochs.app.Card.CardDetail;
import com.ochs.app.Card.CardType;
import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.flexiicar_app.commonfragment.Fragment_Term_And_Condition;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.GETDCREDITCARDLIST;


public class Fragment_CreditCards_List_For_User extends Fragment
{
    Handler handler = new Handler();
    public static Context context;
    public String id = "";
    TextView lbl_addcard;
    ImageView Back;
    int backTo;
    Bundle PaymentBundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_creditcards_list, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ((User_Profile) getActivity()).BottomnavVisible();

        SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
        id = sp.getString(getString(R.string.id), "");

        backTo = getArguments().getInt("backTo");

        lbl_addcard=view.findViewById(R.id.lbl_addcard);
        Back=view.findViewById(R.id.Back);
        Back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(backTo == 1)
                {
                    NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                            .navigate(R.id.action_CardsOnAccount_to_User_Details);
                }
                //TollCharge_Image
                else if(backTo == 2)
                {
                    PaymentBundle = getArguments().getBundle("PaymentBundle");
                    Bundle bundle = new Bundle();
                    bundle.putBundle("PaymentBundle",PaymentBundle);
                    NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                            .navigate(R.id.action_CardsOnAccount_to_TollCharge_Image,bundle);
                }
                //Payment_Reciept_2
                else if(backTo == 3)
                {
                    PaymentBundle = getArguments().getBundle("PaymentBundle");
                    Bundle bundle = new Bundle();
                    bundle.putBundle("PaymentBundle",PaymentBundle);
                    NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                            .navigate(R.id.action_CardsOnAccount_to_Payment_Reciept_2,bundle);

                }
                //Invoice_Image
                else if(backTo == 4)
                {
                    PaymentBundle = getArguments().getBundle("PaymentBundle");
                    Bundle bundle = new Bundle();
                    bundle.putBundle("PaymentBundle",PaymentBundle);
                    NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                            .navigate(R.id.action_CardsOnAccount_to_Invoice_Image,bundle);
                }
                //Traffic_Ticket_Image
                else if(backTo == 5)
                {
                    PaymentBundle = getArguments().getBundle("PaymentBundle");
                    Bundle bundle = new Bundle();
                    bundle.putBundle("PaymentBundle",PaymentBundle);
                    NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                            .navigate(R.id.action_CardsOnAccount_to_Traffic_Ticket_Image,bundle);
                }
                else if(backTo == 6)
                {
                    Bundle ReservationBundle = getArguments().getBundle("ReservationBundle");

                    String transactionId = getArguments().getString("transactionId");
                    String total = getArguments().getString("total");

                    Bundle bundle = new Bundle();
                    bundle.putString("transactionId",transactionId);
                    bundle.putString("total",total);
                    bundle.putBundle("ReservationBundle",ReservationBundle);
                    bundle.putBoolean("isDefaultCard", true);
                    NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                            .navigate(R.id.action_CardsOnAccount_to_Payment_checkout,bundle);
                }
                else if(backTo ==7)
                {
                    Bundle AgreementsBundle = getArguments().getBundle("AgreementsBundle");

                    String transactionId = getArguments().getString("transactionId");
                    String total = getArguments().getString("total");

                    Bundle bundle = new Bundle();
                    bundle.putString("transactionId",transactionId);
                    bundle.putString("total",total);
                    bundle.putBundle("AgreementsBundle",AgreementsBundle);
                    NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                            .navigate(R.id.action_CardsOnAccount_to_PaymentCheckOutSelfCheckIn,bundle);
                }
                else if(backTo ==8)
                {
                    Bundle ReservationBundle = getArguments().getBundle("ReservationBundle");

                    String transactionId = getArguments().getString("transactionId");
                    String total = getArguments().getString("total");

                    Bundle bundle = new Bundle();
                    bundle.putString("transactionId",transactionId);
                    bundle.putString("total",total);
                    bundle.putBundle("ReservationBundle",ReservationBundle);
                    bundle.putBoolean("isDefaultCard", true);
                    NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                            .navigate(R.id.action_CardsOnAccount_to_PayCheckoutForCancelBooking,bundle);
                }
            }
        });
        lbl_addcard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(backTo == 1)
                {
                    Bundle bundle = new Bundle();
                    bundle.putInt("backTo",1);
                    NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                            .navigate(R.id.action_CardsOnAccount_to_AddCreditCard,bundle);
                }
                //TollCharge_Image
                else if(backTo == 2)
                {
                    PaymentBundle = getArguments().getBundle("PaymentBundle");
                    Bundle bundle = new Bundle();
                    bundle.putBundle("PaymentBundle",PaymentBundle);
                    bundle.putInt("backTo",2);
                    NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                            .navigate(R.id.action_CardsOnAccount_to_AddCreditCard,bundle);
                }
                //Payment_Reciept_2
                else if(backTo == 3)
                {
                    PaymentBundle = getArguments().getBundle("PaymentBundle");
                    Bundle bundle = new Bundle();
                    bundle.putBundle("PaymentBundle",PaymentBundle);
                    bundle.putInt("backTo",3);
                    NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                            .navigate(R.id.action_CardsOnAccount_to_AddCreditCard,bundle);

                }
                //Invoice_Image
                else if(backTo == 4)
                {
                    PaymentBundle = getArguments().getBundle("PaymentBundle");
                    Bundle bundle = new Bundle();
                    bundle.putBundle("PaymentBundle",PaymentBundle);
                    bundle.putInt("backTo",4);
                    NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                            .navigate(R.id.action_CardsOnAccount_to_AddCreditCard,bundle);
                }
                //Traffic_Ticket_Image
                else if(backTo == 5)
                {
                    PaymentBundle = getArguments().getBundle("PaymentBundle");
                    Bundle bundle = new Bundle();
                    bundle.putBundle("PaymentBundle",PaymentBundle);
                    bundle.putInt("backTo",5);
                    NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                            .navigate(R.id.action_CardsOnAccount_to_AddCreditCard,bundle);
                }
                else if(backTo == 6)
                {
                    Bundle ReservationBundle = getArguments().getBundle("ReservationBundle");

                    String transactionId = getArguments().getString("transactionId");
                    String total = getArguments().getString("total");

                    Bundle bundle = new Bundle();
                    bundle.putString("transactionId",transactionId);
                    bundle.putString("total",total);
                    bundle.putBundle("ReservationBundle",ReservationBundle);
                    bundle.putInt("backTo",6);
                    NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                            .navigate(R.id.action_CardsOnAccount_to_AddCreditCard,bundle);
                }
                else if(backTo ==7)
                {
                    Bundle AgreementsBundle = getArguments().getBundle("AgreementsBundle");

                    String transactionId = getArguments().getString("transactionId");
                    String total = getArguments().getString("total");

                    Bundle bundle = new Bundle();
                    bundle.putString("transactionId",transactionId);
                    bundle.putString("total",total);
                    bundle.putBundle("AgreementsBundle",AgreementsBundle);
                    bundle.putInt("backTo",7);
                    NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                            .navigate(R.id.action_CardsOnAccount_to_AddCreditCard,bundle);
                }
                else if(backTo ==8)
                {
                    Bundle ReservationBundle = getArguments().getBundle("ReservationBundle");

                    String transactionId = getArguments().getString("transactionId");
                    String total = getArguments().getString("total");

                    Bundle bundle = new Bundle();
                    bundle.putString("transactionId",transactionId);
                    bundle.putString("total",total);
                    bundle.putBundle("ReservationBundle",ReservationBundle);
                    bundle.putInt("backTo",8);
                    NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                            .navigate(R.id.action_CardsOnAccount_to_AddCreditCard,bundle);
                }

            }
        });

        String bodyParam = "";
        try
        {
            bodyParam+="customerId="+id;
            System.out.println(bodyParam);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AndroidNetworking.initialize(getActivity());
        Fragment_Term_And_Condition.context = getActivity();

        ApiService ApiService = new ApiService(GetCreditcardList, RequestType.GET,
                GETDCREDITCARDLIST, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
    }

    OnResponseListener GetCreditcardList = new OnResponseListener()
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
                                final JSONArray getInsuranceDEtails = resultSet.getJSONArray("t0050_Customer_Card_Details");

                                final RelativeLayout rlCreditcard = getActivity().findViewById(R.id.rl_cards);

                                int len;
                                len = getInsuranceDEtails.length();

                               CardType[] SUPPORTED_CARD_TYPES = { CardType.VISA, CardType.MASTERCARD, CardType.DISCOVER,
                                        CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB, CardType.MAESTRO, CardType.UNIONPAY,
                                        CardType.HIPER, CardType.HIPERCARD };

                                for (int j = 0; j < len; j++)
                                {
                                    final JSONObject test = (JSONObject) getInsuranceDEtails.get(j);

                                    final int card_ID = test.getInt("card_ID");
                                    final String card_No=test.getString("card_No");

                                    //CardDetail cardDetail = new CardDetail();

                                    CardDetail.getCard(card_No.replace("-",""));
                                    Log.e("TAG", "run: "+  CardDetail.getCard("4111").name);
                                    final String card_Type_ID=test.getString("card_Type_ID");
                                    final String card_Name=test.getString("card_Name");
                                    final String address=test.getString("address");
                                    final String expiry_Date=test.getString("expiry_Date");
                                    final String cvS_Code=test.getString("cvS_Code");
                                    final int isDefault=test.getInt("isDefault");
                                    final String created_Date=test.getString("created_Date");
                                    final String card_PStreet=test.getString("card_PStreet");
                                    final String card_PCity=test.getString("card_PCity");
                                    final String card_PUnitNo=test.getString("card_PUnitNo");

                                    final int card_PCountry=test.getInt("card_PCountry");
                                    final int card_PState=test.getInt("card_PState");

                                    final String card_SStreet=test.getString("card_SStreet");
                                    final String card_SUnitNo=test.getString("card_SUnitNo");
                                    final String card_SCity=test.getString("card_SCity");
                                    final int card_SCountry=test.getInt("card_SCountry");
                                    final int card_SState=test.getInt("card_SState");
                                    final String card_SZipCode=test.getString("card_SZipCode");

                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                    lp.setMargins(0, 0, 0, 0);

                                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.list_credit_card, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                    linearLayout.setId(200 + j);
                                    linearLayout.setLayoutParams(lp);

                                    LinearLayout LLDefaultCard=(LinearLayout)linearLayout.findViewById(R.id.LLDefaultCard);
                                    LinearLayout creditcard_layout=(LinearLayout)linearLayout.findViewById(R.id.creditcard_layout);

                                    if(isDefault==1)
                                    {
                                        LLDefaultCard.setVisibility(View.VISIBLE);
                                    }

                                    TextView lblCardNumber= (TextView)linearLayout.findViewById(R.id.lbl_CardNum);
                                    TextView lbl_CardUpdate=(TextView)linearLayout.findViewById(R.id.lbl_CardUpdate);
                                    TextView lbl_CardName=(TextView)linearLayout.findViewById(R.id.lbl_CardName);
                                    TextView lbl_CardExDate=(TextView)linearLayout.findViewById(R.id.lbl_CardExDate);

                                    lblCardNumber.setText("**** ***** ***** "+card_No.substring(card_No.length()-4));
                                    lbl_CardName.setText(card_Name);
                                    lbl_CardExDate.setText(expiry_Date);

                                    lbl_CardUpdate.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            Bundle CardBundle = new Bundle();
                                            CardBundle.putInt("card_ID", card_ID);
                                            CardBundle.putString("card_No", card_No);
                                            CardBundle.putString("card_Type_ID", card_Type_ID);
                                            CardBundle.putString("card_Name", card_Name);
                                            CardBundle.putString("expiry_Date", expiry_Date);
                                            CardBundle.putString("cvS_Code", cvS_Code);
                                            CardBundle.putInt("isDefault", isDefault);
                                            CardBundle.putString("created_Date", created_Date);
                                            CardBundle.putString("card_PStreet", card_PStreet);
                                            CardBundle.putString("card_PCity", card_PCity);
                                            CardBundle.putString("card_PUnitNo", card_PUnitNo);

                                            CardBundle.putInt("card_PCountry", card_PCountry);
                                            CardBundle.putInt("card_PState", card_PState);

                                            CardBundle.putString("card_SStreet", card_SStreet);
                                            CardBundle.putString("card_SUnitNo", card_SUnitNo);
                                            CardBundle.putString("card_SCity", card_SCity);
                                            CardBundle.putInt("card_SCountry", card_SCountry);
                                            CardBundle.putInt("card_SState", card_SState);
                                            CardBundle.putString("card_SZipCode", card_SZipCode);

                                            if(backTo == 1)
                                            {
                                                Bundle bundle = new Bundle();
                                                bundle.putInt("backTo",1);
                                                bundle.putBundle("CardBundle", CardBundle);
                                                NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                                                        .navigate(R.id.action_CardsOnAccount_to_UpdateCreditCard,bundle);
                                            }
                                            //TollCharge_Image
                                            else if(backTo == 2)
                                            {
                                                PaymentBundle = getArguments().getBundle("PaymentBundle");
                                                Bundle bundle = new Bundle();
                                                bundle.putBundle("PaymentBundle",PaymentBundle);
                                                bundle.putInt("backTo",2);
                                                bundle.putBundle("CardBundle", CardBundle);
                                                NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                                                        .navigate(R.id.action_CardsOnAccount_to_UpdateCreditCard,bundle);
                                            }
                                            //Payment_Reciept_2
                                            else if(backTo == 3)
                                            {
                                                PaymentBundle = getArguments().getBundle("PaymentBundle");
                                                Bundle bundle = new Bundle();
                                                bundle.putBundle("PaymentBundle",PaymentBundle);
                                                bundle.putInt("backTo",3);
                                                bundle.putBundle("CardBundle", CardBundle);
                                                NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                                                        .navigate(R.id.action_CardsOnAccount_to_UpdateCreditCard,bundle);

                                            }
                                            //Invoice_Image
                                            else if(backTo == 4)
                                            {
                                                PaymentBundle = getArguments().getBundle("PaymentBundle");
                                                Bundle bundle = new Bundle();
                                                bundle.putBundle("PaymentBundle",PaymentBundle);
                                                bundle.putInt("backTo",4);
                                                bundle.putBundle("CardBundle", CardBundle);
                                                NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                                                        .navigate(R.id.action_CardsOnAccount_to_UpdateCreditCard,bundle);
                                            }
                                            //Traffic_Ticket_Image
                                            else if(backTo == 5)
                                            {
                                                PaymentBundle = getArguments().getBundle("PaymentBundle");
                                                Bundle bundle = new Bundle();
                                                bundle.putBundle("PaymentBundle",PaymentBundle);
                                                bundle.putInt("backTo",5);
                                                bundle.putBundle("CardBundle", CardBundle);
                                                NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                                                        .navigate(R.id.action_CardsOnAccount_to_UpdateCreditCard,bundle);
                                            }
                                            else if(backTo == 6)
                                            {
                                                Bundle ReservationBundle = getArguments().getBundle("ReservationBundle");

                                                String transactionId = getArguments().getString("transactionId");
                                                String total = getArguments().getString("total");

                                                Bundle bundle = new Bundle();
                                                bundle.putString("transactionId",transactionId);
                                                bundle.putString("total",total);
                                                bundle.putBundle("ReservationBundle",ReservationBundle);
                                                bundle.putInt("backTo",6);
                                                bundle.putBundle("CardBundle", CardBundle);
                                                NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                                                        .navigate(R.id.action_CardsOnAccount_to_UpdateCreditCard,bundle);
                                            }
                                            else if(backTo ==7)
                                            {
                                                Bundle AgreementsBundle = getArguments().getBundle("AgreementsBundle");

                                                String transactionId = getArguments().getString("transactionId");
                                                String total = getArguments().getString("total");

                                                Bundle bundle = new Bundle();
                                                bundle.putString("transactionId",transactionId);
                                                bundle.putString("total",total);
                                                bundle.putBundle("AgreementsBundle",AgreementsBundle);
                                                bundle.putInt("backTo",7);
                                                bundle.putBundle("CardBundle", CardBundle);
                                                NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                                                        .navigate(R.id.action_CardsOnAccount_to_UpdateCreditCard,bundle);
                                            }

                                            else if(backTo ==8)
                                            {
                                                Bundle ReservationBundle = getArguments().getBundle("ReservationBundle");

                                                String transactionId = getArguments().getString("transactionId");
                                                String total = getArguments().getString("total");

                                                Bundle bundle = new Bundle();
                                                bundle.putString("transactionId",transactionId);
                                                bundle.putString("total",total);
                                                bundle.putBundle("ReservationBundle",ReservationBundle);
                                                bundle.putInt("backTo",8);
                                                bundle.putBundle("CardBundle", CardBundle);
                                                NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                                                        .navigate(R.id.action_CardsOnAccount_to_UpdateCreditCard,bundle);
                                            }
                                        }
                                    });

                                    creditcard_layout.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            if(backTo == 2)
                                            {

                                                Bundle bundle = new Bundle();
                                                PaymentBundle = getArguments().getBundle("PaymentBundle");
                                                bundle.putBundle("PaymentBundle",PaymentBundle);
                                                bundle.putString("creditcard", test.toString());
                                                NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                                                        .navigate(R.id.action_CardsOnAccount_to_TollCharge_Image,bundle);
                                            }
                                            if(backTo == 3)
                                            {
                                                    Bundle bundle = new Bundle();
                                                    PaymentBundle = getArguments().getBundle("PaymentBundle");
                                                    bundle.putBundle("PaymentBundle",PaymentBundle);
                                                    bundle.putString("creditcard", test.toString());
                                                    NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                                                            .navigate(R.id.action_CardsOnAccount_to_Payment_Reciept_2,bundle);
                                            }
                                            //Invoice_Image
                                            if(backTo == 4)
                                            {
                                                Bundle bundle = new Bundle();
                                                PaymentBundle = getArguments().getBundle("PaymentBundle");
                                                bundle.putBundle("PaymentBundle",PaymentBundle);
                                                bundle.putString("creditcard", test.toString());
                                                NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                                                        .navigate(R.id.action_CardsOnAccount_to_Invoice_Image,bundle);
                                            }
                                            //Traffic_Ticket_Image
                                            if(backTo == 5)
                                            {
                                                Bundle bundle = new Bundle();
                                                PaymentBundle = getArguments().getBundle("PaymentBundle");
                                                bundle.putBundle("PaymentBundle",PaymentBundle);
                                                bundle.putString("creditcard", test.toString());
                                                NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                                                        .navigate(R.id.action_CardsOnAccount_to_Traffic_Ticket_Image,bundle);
                                            }

                                          if(backTo == 6)
                                            {
                                                Bundle ReservationBundle = getArguments().getBundle("ReservationBundle");
                                                String transactionId = getArguments().getString("transactionId");
                                                String total = getArguments().getString("total");

                                                Bundle bundle = new Bundle();
                                                bundle.putString("transactionId",transactionId);
                                                bundle.putString("total",total);
                                                bundle.putString("creditcard", test.toString());
                                                bundle.putBundle("ReservationBundle",ReservationBundle);

                                                NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                                                        .navigate(R.id.action_CardsOnAccount_to_Payment_checkout,bundle);
                                            }
                                          else if(backTo ==7)
                                          {
                                              Bundle AgreementsBundle = getArguments().getBundle("AgreementsBundle");

                                              String transactionId = getArguments().getString("transactionId");
                                              String total = getArguments().getString("total");

                                              Bundle bundle = new Bundle();
                                              bundle.putString("transactionId",transactionId);
                                              bundle.putString("total",total);
                                              bundle.putString("creditcard", test.toString());
                                              bundle.putBundle("AgreementsBundle",AgreementsBundle);
                                              NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                                                      .navigate(R.id.action_CardsOnAccount_to_PaymentCheckOutSelfCheckIn,bundle);
                                          }
                                          else if(backTo ==8)
                                          {
                                              Bundle ReservationBundle = getArguments().getBundle("ReservationBundle");

                                              String transactionId = getArguments().getString("transactionId");
                                              String total = getArguments().getString("total");

                                              Bundle bundle = new Bundle();
                                              bundle.putString("transactionId",transactionId);
                                              bundle.putString("total",total);
                                              bundle.putString("creditcard", test.toString());
                                              bundle.putBundle("ReservationBundle",ReservationBundle);
                                              bundle.putBoolean("isDefaultCard", false);
                                              NavHostFragment.findNavController(Fragment_CreditCards_List_For_User.this)
                                                      .navigate(R.id.action_CardsOnAccount_to_PayCheckoutForCancelBooking,bundle);
                                          }

                                        }
                                    });

                                    rlCreditcard.addView(linearLayout);
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
        public void onError(String error) {
            System.out.println("Error-" + error);
        }
    };

}