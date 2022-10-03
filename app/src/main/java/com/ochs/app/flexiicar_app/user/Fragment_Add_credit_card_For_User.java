package com.ochs.app.flexiicar_app.user;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.R;
import com.ochs.app.ScanDrivingLicense;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.adapters.MonthYearPickerDialog;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.ADDCREDITCARD;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_CUSTOMER;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_SETTINGS;
import static com.ochs.app.apicall.ApiEndPoint.GETCOUNTRYLIST;
import static com.ochs.app.apicall.ApiEndPoint.STATELIST;

public class Fragment_Add_credit_card_For_User extends Fragment
{
    ImageView imgback,ScanCreditcard;
    EditText edt_Card_No, edt_CVV, edt_NameofCard,edtStreetNo,edtUnitNo,edtCityName,edt_Zipcode;
    Spinner Sp_CountryList,SP_StateList;
    LinearLayout AddCreditCardLayout;
    TextView lblDiscard,txt_CardNumber, txt_CardholderName, txt_cardExpiryDate,lblexpiryDate;
    Handler handler = new Handler();
    public static Context context;
    public String id = "";
    public String[] Country,State;
    public int[] CountyId,StateId;
    HashMap<String, Integer> countryhashmap=new HashMap<String, Integer>();
    HashMap<String,Integer>Statehashmap=new HashMap<>();
    private Geocoder geocoder;
    int backTo;
    CheckBox chk_DeleteCard, chk_DefaultCard;
    private final int REQUEST_PLACE_ADDRESS = 40;
    int cust_State_ID,cust_Country_ID;
    String a;
    int keyDel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_creditcard, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ((User_Profile) getActivity()).BottomnavInVisible();

        AndroidNetworking.initialize(getActivity());
        Fragment_Add_credit_card_For_User.context = getActivity();

        backTo = getArguments().getInt("backTo");

        String bodyParam = "";
        edt_Card_No = view.findViewById(R.id.edt_Card_NoAdd);
        lblexpiryDate = view.findViewById(R.id.lblexpiryDate);
        edt_CVV = view.findViewById(R.id.edt_CVV_Add);
        edt_NameofCard = view.findViewById(R.id.edt_NameofCardAdd);
        edtStreetNo= view.findViewById(R.id.edt_streetNoName);
        edtUnitNo= view.findViewById(R.id.edtxtUnitNumber);
        edtCityName= view.findViewById(R.id.Edtext_City);
        edt_Zipcode = view.findViewById(R.id.edtPincodeNo);
        Sp_CountryList=view.findViewById(R.id.spinner_CountryList);
        SP_StateList=view.findViewById(R.id.Spinner_Statelist);
        AddCreditCardLayout = view.findViewById(R.id.lblSaveCardAdd);
        lblDiscard= view.findViewById(R.id.lblDiscard);
        imgback=view.findViewById(R.id.Back);
        ScanCreditcard=view.findViewById(R.id.scanCreditcard);
        chk_DeleteCard = view.findViewById(R.id.DeleteCard);
        chk_DefaultCard = view.findViewById(R.id.DefaultCard);

        txt_CardNumber = view.findViewById(R.id.txt_CardNumberAdd);
        txt_CardholderName = view.findViewById(R.id.txt_CardholderNameAdd);
        txt_cardExpiryDate = view.findViewById(R.id.txt_cardExpiryDateAdd);

        SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
        id = sp.getString(getString(R.string.id), "");
        cust_State_ID = sp.getInt("cmp_State",0);
        cust_Country_ID = sp.getInt("cmp_Country",0);
        System.out.println(cust_State_ID);
        String cust_FName = sp.getString("cust_FName", "");
        String cust_LName = sp.getString("cust_LName", "");
        String cust_Street = sp.getString("cust_Street", "");
        String cust_UnitNo = sp.getString("cust_UnitNo", "");
        String cust_ZipCode = sp.getString("cust_ZipCode", "");
        String cust_City = sp.getString("cust_City", "");
        String CardOnName=cust_FName+" "+cust_LName;

        edt_NameofCard.setText(CardOnName);
        edtStreetNo.setText(cust_Street);
        edtUnitNo.setText(cust_UnitNo);
        edt_Zipcode.setText(cust_ZipCode);
        edtCityName.setText(cust_City);

        txt_CardholderName.setText(CardOnName);

        ScanCreditcard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(getActivity(), ScanDrivingLicense.class);
                i.putExtra("afterScanBackTo", 2);
                startActivity(i);
            }
        });

        imgback.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(backTo == 1)
                {
                    Bundle bundle = new Bundle();
                    bundle.putInt("backTo",1);
                    NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                            .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
                }
                //TollCharge_Image
                else if(backTo == 2)
                {
                    Bundle PaymentBundle = getArguments().getBundle("PaymentBundle");
                    Bundle bundle = new Bundle();
                    bundle.putBundle("PaymentBundle",PaymentBundle);
                    bundle.putInt("backTo",2);
                    NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                            .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
                }
                //Payment_Reciept_2
                else if(backTo == 3)
                {
                    Bundle PaymentBundle = getArguments().getBundle("PaymentBundle");
                    Bundle bundle = new Bundle();
                    bundle.putBundle("PaymentBundle",PaymentBundle);
                    bundle.putInt("backTo",3);
                    NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                            .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);

                }
                //Invoice_Image
                else if(backTo == 4)
                {
                    Bundle PaymentBundle = getArguments().getBundle("PaymentBundle");
                    Bundle bundle = new Bundle();
                    bundle.putBundle("PaymentBundle",PaymentBundle);
                    bundle.putInt("backTo",4);
                    NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                            .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
                }
                //Traffic_Ticket_Image
                else if(backTo == 5)
                {
                    Bundle PaymentBundle = getArguments().getBundle("PaymentBundle");
                    Bundle bundle = new Bundle();
                    bundle.putBundle("PaymentBundle",PaymentBundle);
                    bundle.putInt("backTo",5);
                    NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                            .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
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
                    NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                            .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
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
                    NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                            .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
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
                    NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                            .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
                }

            }
        });

        lblDiscard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(backTo == 1)
                {
                    Bundle bundle = new Bundle();
                    bundle.putInt("backTo",1);
                    NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                            .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
                }
                //TollCharge_Image
                else if(backTo == 2)
                {
                    Bundle PaymentBundle = getArguments().getBundle("PaymentBundle");
                    Bundle bundle = new Bundle();
                    bundle.putBundle("PaymentBundle",PaymentBundle);
                    bundle.putInt("backTo",2);
                    NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                            .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
                }
                //Payment_Reciept_2
                else if(backTo == 3)
                {
                    Bundle PaymentBundle = getArguments().getBundle("PaymentBundle");
                    Bundle bundle = new Bundle();
                    bundle.putBundle("PaymentBundle",PaymentBundle);
                    bundle.putInt("backTo",3);
                    NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                            .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);

                }
                //Invoice_Image
                else if(backTo == 4)
                {
                    Bundle PaymentBundle = getArguments().getBundle("PaymentBundle");
                    Bundle bundle = new Bundle();
                    bundle.putBundle("PaymentBundle",PaymentBundle);
                    bundle.putInt("backTo",4);
                    NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                            .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
                }
                //Traffic_Ticket_Image
                else if(backTo == 5)
                {
                    Bundle PaymentBundle = getArguments().getBundle("PaymentBundle");
                    Bundle bundle = new Bundle();
                    bundle.putBundle("PaymentBundle",PaymentBundle);
                    bundle.putInt("backTo",5);
                    NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                            .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
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
                    NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                            .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
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
                    NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                            .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
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
                    NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                            .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
                }
            }
        });

        edtStreetNo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    if(!Places.isInitialized())
                    {
                        Places.initialize(getActivity(), getString(R.string.map_api_key), Locale.US);
                    }
                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Arrays.asList( Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)).build(context);
                    startActivityForResult(intent, REQUEST_PLACE_ADDRESS);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        edt_NameofCard.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                txt_CardholderName.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        edt_Card_No.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                boolean flag = true;
                String eachBlock[] = edt_Card_No.getText().toString().split("-");
                for (int i = 0; i < eachBlock.length; i++)
                {
                    if (eachBlock[i].length() > 4)
                    {
                        flag = false;
                    }
                }
                if (flag)
                {
                    edt_Card_No.setOnKeyListener(new View.OnKeyListener()
                    {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event)
                        {

                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                keyDel = 1;
                            return false;
                        }
                    });
                    if (keyDel == 0)
                    {

                        if (((edt_Card_No.getText().length() + 1) % 5) == 0) {

                            if (edt_Card_No.getText().toString().split("-").length <= 3) {
                                edt_Card_No.setText(edt_Card_No.getText() + "-");
                                edt_Card_No.setSelection(edt_Card_No.getText().length());
                            }
                        }
                        a = edt_Card_No.getText().toString();
                    } else {
                        a = edt_Card_No.getText().toString();
                        keyDel = 0;
                    }

                } else {
                    edt_Card_No.setText(a);
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                // TODO Auto-generated method stub
                txt_CardNumber.setText(a);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // TODO Auto-generated method stub
            }
        });

        lblexpiryDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try{
                    MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
                    pickerDialog.setListener(new DatePickerDialog.OnDateSetListener()
                    {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int i2)
                        {
                            try {
                                String monthYearStr = year + "-" + (month + 1) + "-" + i2;
                                Date DateofBirth = new SimpleDateFormat("yyyy-MM-dd").parse(monthYearStr);
                                SimpleDateFormat format1 = new SimpleDateFormat("MM/yy");
                                String parsedDateofBirth = format1.format(DateofBirth);
                                lblexpiryDate.setText(parsedDateofBirth);
                                txt_cardExpiryDate.setText(parsedDateofBirth);
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                    pickerDialog.show(getChildFragmentManager(), "MonthYearPickerDialog");
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        AddCreditCardLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (edt_Card_No.getText().toString().equals(""))
                    CustomToast.showToast(getActivity(),"Please Enter CardNumber",1);
                else if (lblexpiryDate.getText().toString().equals(""))
                    CustomToast.showToast(getActivity(),"Please Enter ExpiryDate",1);
                else if (edt_CVV.getText().toString().equals(""))
                    CustomToast.showToast(getActivity(),"Please Enter CVV",1);
                else if (edt_NameofCard.getText().toString().equals(""))
                    CustomToast.showToast(getActivity(),"Please Enter Card Holder Name",1);
                else if (edt_Zipcode.getText().toString().equals(""))
                    CustomToast.showToast(getActivity(),"Please Enter Zip Code",1);
                else {
                    JSONObject bodyParam = new JSONObject();
                    try
                    {
                        bodyParam.accumulate("Customer_ID", Integer.parseInt(id));
                        bodyParam.accumulate("Card_ID",0);
                        bodyParam.accumulate("Card_PStreet",edtStreetNo.getText().toString() );
                        bodyParam.accumulate("Card_PCity",edtCityName.getText().toString());
                        bodyParam.accumulate("Card_PUnitNo",edtUnitNo.getText().toString() );
                        int s = countryhashmap.get(Sp_CountryList.getSelectedItem());
                        int s1=Statehashmap.get(SP_StateList.getSelectedItem());
                        bodyParam.accumulate("Card_PCountry",s);
                        bodyParam.accumulate("Card_PState", s1);
                        bodyParam.accumulate("Card_No", edt_Card_No.getText().toString());
                        bodyParam.accumulate("Expiry_Date", lblexpiryDate.getText().toString());
                        bodyParam.accumulate("CVS_Code", edt_CVV.getText().toString());
                        bodyParam.accumulate("Card_Name", edt_NameofCard.getText().toString());
                        bodyParam.accumulate("ZipCode", edt_Zipcode.getText().toString());

                        if(chk_DefaultCard.isChecked())
                            bodyParam.accumulate("IsDefault",1);
                        else
                            bodyParam.accumulate("IsDefault", 0);
                        System.out.println(bodyParam);

                        System.out.println(bodyParam);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    ApiService ApiService1 = new ApiService(AddCreditCard, RequestType.POST,
                            ADDCREDITCARD, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);
                }
            }
        });

        try
        {
            ApiService ApiService = new ApiService(CountryList, RequestType.GET,
                    GETCOUNTRYLIST, BASE_URL_SETTINGS, new HashMap<String, String>(), bodyParam);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    OnResponseListener AddCreditCard = new OnResponseListener()
    {
        @Override
        public void onSuccess(final String response, HashMap<String, String> headers)
        {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Success");
                        System.out.println(response);

                        JSONObject responseJSON = new JSONObject(response);
                        Boolean status = responseJSON.getBoolean("status");

                        if (status)
                        {
                            String msg = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),msg,0);

                            if(backTo == 1)
                            {
                                Bundle bundle = new Bundle();
                                bundle.putInt("backTo",1);
                                NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                                        .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
                            }
                            //TollCharge_Image
                            else if(backTo == 2)
                            {
                                Bundle PaymentBundle = getArguments().getBundle("PaymentBundle");
                                Bundle bundle = new Bundle();
                                bundle.putBundle("PaymentBundle",PaymentBundle);
                                bundle.putInt("backTo",2);
                                NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                                        .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
                            }
                            //Payment_Reciept_2
                            else if(backTo == 3)
                            {
                                Bundle PaymentBundle = getArguments().getBundle("PaymentBundle");
                                Bundle bundle = new Bundle();
                                bundle.putBundle("PaymentBundle",PaymentBundle);
                                bundle.putInt("backTo",3);
                                NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                                        .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);

                            }
                            //Invoice_Image
                            else if(backTo == 4)
                            {
                                Bundle PaymentBundle = getArguments().getBundle("PaymentBundle");
                                Bundle bundle = new Bundle();
                                bundle.putBundle("PaymentBundle",PaymentBundle);
                                bundle.putInt("backTo",4);
                                NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                                        .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
                            }
                            //Traffic_Ticket_Image
                            else if(backTo == 5)
                            {
                                Bundle PaymentBundle = getArguments().getBundle("PaymentBundle");
                                Bundle bundle = new Bundle();
                                bundle.putBundle("PaymentBundle",PaymentBundle);
                                bundle.putInt("backTo",5);
                                NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                                        .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
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
                                NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                                        .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
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
                                NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                                        .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
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
                                NavHostFragment.findNavController(Fragment_Add_credit_card_For_User.this)
                                        .navigate(R.id.action_AddCreditCard_to_CardsOnAccount,bundle);
                            }
                        }
                        else
                        {
                            String msg = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),msg,1);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        @Override
        public void onError(String error)
        {
            System.out.println("Error" + error);
        }
    };

    OnResponseListener CountryList = new OnResponseListener()
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
                            final JSONArray Countrylist = resultSet.getJSONArray("t0040_Country_Master");

                            int len;
                            len = Countrylist.length();

                            Country = new String[len];
                            CountyId = new int[len];
                            int position=0;

                            for (int j = 0; j < len; j++)
                            {
                                final JSONObject test = (JSONObject) Countrylist.get(j);
                                final int country_ID = test.getInt("country_ID");
                                final String country_Name = test.getString("country_Name");
                                Country[j] = country_Name;
                                CountyId[j] = country_ID;

                                countryhashmap.put(country_Name,country_ID);

                                if(cust_Country_ID==(country_ID))
                                {
                                    position = j;
                                }

                            }

                            ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(getContext().getApplicationContext(), R.layout.spinner_layout, R.id.text1, Country);
                            Sp_CountryList.setAdapter(adapterCategories);
                            Sp_CountryList.setSelection(position);

                            Sp_CountryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                            {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                                {
                                    try
                                    {
                                        int s = countryhashmap.get(Sp_CountryList.getSelectedItem());
                                        System.out.println(Sp_CountryList.getSelectedItem());
                                        System.out.println(s + "");
                                        try
                                        {
                                            String bodyParam1 = "countryId="+s;
                                            ApiService ApiService = new ApiService(StateList, RequestType.GET,
                                                    STATELIST, BASE_URL_SETTINGS, new HashMap<String, String>(), bodyParam1);
                                        } catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView)
                                {

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
    //StateList
    OnResponseListener StateList = new OnResponseListener()
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
                            final JSONArray StateList = resultSet.getJSONArray("t0040_State_Master");

                            int len;
                            len = StateList.length();
                            StateId = new int[len];
                            State = new String[len];
                            int position=0;

                            for (int j = 0; j < len; j++)
                            {
                                final JSONObject test = (JSONObject) StateList.get(j);
                                final int state_ID = test.getInt("state_ID");
                                final String state_Name = test.getString("state_Name");

                                State[j] = state_Name;
                                StateId[j] = state_ID;

                                Statehashmap.put(state_Name,state_ID);

                                if(cust_State_ID==(state_ID))
                                {
                                    position = j;
                                }
                            }

                            ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(getContext().getApplicationContext(), R.layout.spinner_layout, R.id.text1, State);
                            SP_StateList.setAdapter(adapterCategories);
                            SP_StateList.setSelection(position);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PLACE_ADDRESS && resultCode == Activity.RESULT_OK)
        {
            Place place = Autocomplete.getPlaceFromIntent(data);
            // Log.i(TAG, "Place city and postal code: " + place.getAddress().subSequence(place.getName().length(),place.getAddress().length()));
            try {
                List<Address> addresses;
                geocoder = new Geocoder(context, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String UnitNo=addresses.get(0).getFeatureName();
                    String Street=addresses.get(0).getThoroughfare();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();

                    Log.e("Address: ", "" + address);
                    Log.e("City: ", "" + city);
                    Log.e("Street: ", "" + Street);
                    Log.e("State: ", "" + state);
                    Log.e("Country: ", "" + country);
                    Log.e("APostalCode: ", "" + postalCode);
                    Log.e("UnitNO: ", "" + UnitNo);

                    edtStreetNo.setText(Street);
                    edtCityName.setText(city);
                    edt_Zipcode.setText(postalCode);
                    edtUnitNo.setText(UnitNo);

                    for(int i=0;i<State.length;i++)
                    {

                        if(State[i].equals(state))
                        {
                            SP_StateList.setSelection(i);
                            break;
                        }
                    }

                    for(int j=0;j<Country.length;j++)
                    {

                        if(Country[j].equals(country))
                        {
                            Sp_CountryList.setSelection(j);
                            break;
                        }
                    }

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                //setMarker(latLng);
            }
        }
    }
}




