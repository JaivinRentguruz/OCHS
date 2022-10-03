package com.ochs.app.flexiicar_app.booking;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_CUSTOMER;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_SETTINGS;
import static com.ochs.app.apicall.ApiEndPoint.DELETECREDITCARD;
import static com.ochs.app.apicall.ApiEndPoint.GETCOUNTRYLIST;
import static com.ochs.app.apicall.ApiEndPoint.STATELIST;
import static com.ochs.app.apicall.ApiEndPoint.UPDATECREDITCARD;

public class Fragment_Update_credit_card extends Fragment
{
    ImageView Back,ScanCreditCard;
    EditText edt_Card_No, edt_CVV, edt_NameofCard, edtStreetNo,edtUnitNo,edtCityName,edt_Pincode;
    Spinner Sp_CountryList,SP_StateList;
    TextView lbl_CardNumber, lblCardholderName, lbl_cardExpiryDate,lblDiscard,lblSaveCard,txtExpirydate;
    CheckBox chk_DeleteCard, chk_DefaultCard;
    Bundle CreditCardBundle;
    Handler handler = new Handler();
    public static Context context;
    public String id = "",transactionId;
    public String[] Country,State;
    public int[] CountyId,StateId;
    HashMap<String, Integer> countryhashmap=new HashMap<String, Integer>();
    HashMap<String,Integer>Statehashmap=new HashMap<>();
    Bundle BookingBundle,VehicleBundle;
    Bundle returnLocationBundle, locationBundle;
    Boolean locationType, initialSelect;
    private Geocoder geocoder;
    private final int REQUEST_PLACE_ADDRESS = 40;
    int card_PCountry,card_PState;
    String a;
    int keyDel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_update_creditcard, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        try {
            super.onViewCreated(view, savedInstanceState);

            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            String bodyParam = "";
            edt_Card_No = view.findViewById(R.id.edt_Card_No);
            txtExpirydate = view.findViewById(R.id.txtExpiryDate);
            edt_CVV = view.findViewById(R.id.edt_CVV);
            lblDiscard = view.findViewById(R.id.lblDiscard);
            edt_NameofCard = view.findViewById(R.id.edt_NameofCard);
            edtStreetNo = view.findViewById(R.id.edt_streetNumandName);
            edtUnitNo = view.findViewById(R.id.edtxtUnitNum);
            edtCityName = view.findViewById(R.id.EdtextCity);
            edt_Pincode = view.findViewById(R.id.edtPincode);
            Sp_CountryList = view.findViewById(R.id.sp_CountryListForCreditcard);
            SP_StateList = view.findViewById(R.id.Spinner_State);
            chk_DeleteCard = view.findViewById(R.id.chk_DeleteCard);
            chk_DefaultCard = view.findViewById(R.id.chk_DefaultCard);
            lblSaveCard = view.findViewById(R.id.lblSaveCard);

            lbl_CardNumber = view.findViewById(R.id.lbl_CardNumber);
            lblCardholderName = view.findViewById(R.id.lblCardholderName);
            lbl_cardExpiryDate = view.findViewById(R.id.lbl_cardExpiryDate);

            Back = view.findViewById(R.id.Back);
            ScanCreditCard = view.findViewById(R.id.ScanCard);

            ScanCreditCard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent i = new Intent(getActivity(), ScanDrivingLicense.class);
                    i.putExtra("afterScanBackTo", 3);
                    startActivity(i);
                }
            });

            AndroidNetworking.initialize(getActivity());
            Fragment_Update_credit_card.context = getActivity();

            edtStreetNo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    try {
                        if (!Places.isInitialized())
                        {
                            Places.initialize(getActivity(), getString(R.string.map_api_key), Locale.US);
                        }
                        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)).build(context);
                        startActivityForResult(intent, REQUEST_PLACE_ADDRESS);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            CreditCardBundle = getArguments().getBundle("CardBundle");
            BookingBundle = getArguments().getBundle("BookingBundle");
            VehicleBundle = getArguments().getBundle("VehicleBundle");
            returnLocationBundle = getArguments().getBundle("returnLocation");
            locationBundle = getArguments().getBundle("location");
            locationType = getArguments().getBoolean("locationType");
            initialSelect = getArguments().getBoolean("initialSelect");

            transactionId = getArguments().getString("transactionId");

            edt_Card_No.setText(CreditCardBundle.getString("card_No"));
            txtExpirydate.setText(CreditCardBundle.getString("expiry_Date"));
            edt_CVV.setText(CreditCardBundle.getString("cvS_Code"));
            edt_NameofCard.setText(CreditCardBundle.getString("card_Name"));
            edt_Pincode.setText(CreditCardBundle.getString("card_SZipCode"));

            edtStreetNo.setText(CreditCardBundle.getString("card_PStreet"));
            edtUnitNo.setText(CreditCardBundle.getString("card_PUnitNo"));
            edtCityName.setText(CreditCardBundle.getString("card_PCity"));

            String cardno = (CreditCardBundle.getString("card_No"));
            lbl_CardNumber.setText("**** ***** ***** " + cardno.substring(cardno.length() - 4));
            lblCardholderName.setText(CreditCardBundle.getString("card_Name"));
            lbl_cardExpiryDate.setText(CreditCardBundle.getString("expiry_Date"));

            card_PCountry = (CreditCardBundle.getInt("card_PCountry"));
            card_PState = (CreditCardBundle.getInt("card_PState"));

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            id = sp.getString(getString(R.string.id), "");

            Back.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("transactionId", transactionId);
                    bundle.putBundle("VehicleBundle", VehicleBundle);
                    bundle.putBundle("BookingBundle", BookingBundle);
                    bundle.putBundle("location", locationBundle);
                    bundle.putBoolean("locationType", locationType);
                    bundle.putBoolean("initialSelect", initialSelect);
                    bundle.putBundle("returnLocation", returnLocationBundle);
                    bundle.putString("transactionId", transactionId);
                    NavHostFragment.findNavController(Fragment_Update_credit_card.this)
                            .navigate(R.id.action_EditCreditCard_to_CardsOnAccount, bundle);
                }
            });

            lblDiscard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("transactionId", transactionId);
                    bundle.putBundle("VehicleBundle", VehicleBundle);
                    bundle.putBundle("BookingBundle", BookingBundle);
                    bundle.putBundle("location", locationBundle);
                    bundle.putBoolean("locationType", locationType);
                    bundle.putBoolean("initialSelect", initialSelect);
                    bundle.putBundle("returnLocation", returnLocationBundle);
                    bundle.putString("transactionId", transactionId);
                    NavHostFragment.findNavController(Fragment_Update_credit_card.this)
                            .navigate(R.id.action_EditCreditCard_to_CardsOnAccount, bundle);
                }
            });

            txtExpirydate.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
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
                                    txtExpirydate.setText(parsedDateofBirth);
                                    lbl_cardExpiryDate.setText(parsedDateofBirth);
                                } catch (Exception e)
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
                    if (flag) {
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
                        if (keyDel == 0) {

                            if (((edt_Card_No.getText().length() + 1) % 5) == 0)
                            {
                                if (edt_Card_No.getText().toString().split("-").length <= 3)
                                {
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
                    lbl_CardNumber.setText(a);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {
                    // TODO Auto-generated method stub
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
                    lblCardholderName.setText(charSequence);
                }
                @Override
                public void afterTextChanged(Editable editable)
                {

                }
            });

            chk_DeleteCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                //
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (isChecked)
                    {
                        chk_DefaultCard.setChecked(false);
                    }
                }
            });

            chk_DefaultCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                //
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (isChecked)
                    {
                        chk_DeleteCard.setChecked(false);
                    }
                }
            });

            lblSaveCard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    JSONObject bodyParam = new JSONObject();

                    if (chk_DeleteCard.isChecked())
                    {
                        if (edt_Card_No.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter CardNumber", Toast.LENGTH_LONG).show();

                        else if (txtExpirydate.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter ExpiryDate", Toast.LENGTH_LONG).show();

                        else if (edt_CVV.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter CVV", Toast.LENGTH_LONG).show();

                        else if (edt_NameofCard.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Card Holder Name", Toast.LENGTH_LONG).show();

                        else if (edt_Pincode.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Zip Code", Toast.LENGTH_LONG).show();
                        else {
                            try {
                                bodyParam.accumulate("Card_ID", CreditCardBundle.getInt("card_ID"));
                                ApiService ApiService1 = new ApiService(DeleteCreditCard, RequestType.POST,
                                        DELETECREDITCARD, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (edt_Card_No.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter CardNumber", Toast.LENGTH_LONG).show();

                        else if (txtExpirydate.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter ExpiryDate", Toast.LENGTH_LONG).show();

                        else if (edt_CVV.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter CVV", Toast.LENGTH_LONG).show();

                        else if (edt_NameofCard.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Card Holder Name", Toast.LENGTH_LONG).show();

                        else if (edt_Pincode.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Zip Code", Toast.LENGTH_LONG).show();

                        else {
                            try {
                                bodyParam.accumulate("Customer_ID", Integer.parseInt(id));
                                bodyParam.accumulate("Card_ID", CreditCardBundle.getInt("card_ID"));
                                bodyParam.accumulate("Card_Type_ID", CreditCardBundle.getString("card_Type_ID"));
                                bodyParam.accumulate("Card_No", edt_Card_No.getText().toString());
                                bodyParam.accumulate("Expiry_Date", txtExpirydate.getText().toString());
                                bodyParam.accumulate("CVS_Code", edt_CVV.getText().toString());
                                bodyParam.accumulate("Card_Name", edt_NameofCard.getText().toString());
                                bodyParam.accumulate("Card_PStreet", edtStreetNo.getText().toString());
                                bodyParam.accumulate("Card_PCity", edtCityName.getText().toString());
                                bodyParam.accumulate("Card_PUnitNo", edtUnitNo.getText().toString());
                                bodyParam.accumulate("ZipCode", edt_Pincode.getText().toString());
                                int s = countryhashmap.get(Sp_CountryList.getSelectedItem());
                                int s1 = Statehashmap.get(SP_StateList.getSelectedItem());
                                bodyParam.accumulate("Card_PCountry", s);
                                bodyParam.accumulate("Card_PState", s1);
                                System.out.println(bodyParam);

                                if (chk_DefaultCard.isChecked())
                                    bodyParam.accumulate("IsDefault", 1);
                                else
                                    bodyParam.accumulate("IsDefault", 0);
                                System.out.println(bodyParam);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            ApiService ApiService = new ApiService(UpdateCreditCard, RequestType.POST,
                                    UPDATECREDITCARD, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);
                        }
                    }
                }
            });
            try {
                ApiService ApiService = new ApiService(CountryList, RequestType.GET,
                        GETCOUNTRYLIST, BASE_URL_SETTINGS, new HashMap<String, String>(), bodyParam);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //UpdateCreditCard
    OnResponseListener UpdateCreditCard = new OnResponseListener()
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

                        if (status) {
                            String msg = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(), msg, 0);
                            Bundle bundle = new Bundle();
                            bundle.putString("transactionId", transactionId);
                            bundle.putBundle("VehicleBundle", VehicleBundle);
                            bundle.putBundle("BookingBundle", BookingBundle);
                            bundle.putBundle("location", locationBundle);
                            bundle.putBundle("returnLocation", returnLocationBundle);
                            bundle.putBoolean("locationType", locationType);
                            bundle.putBoolean("initialSelect", initialSelect);
                            bundle.putString("transactionId", transactionId);
                            NavHostFragment.findNavController(Fragment_Update_credit_card.this)
                                    .navigate(R.id.action_EditCreditCard_to_CardsOnAccount, bundle);
                        } else {
                            String msg = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(), msg, 1);
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
    //DeleteCreditCard
    OnResponseListener DeleteCreditCard = new OnResponseListener()
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
                            Bundle bundle = new Bundle();
                            bundle.putString("transactionId",transactionId);
                            bundle.putBundle("VehicleBundle",VehicleBundle);
                            bundle.putBundle("BookingBundle",BookingBundle);
                            bundle.putBundle("location", locationBundle);
                            bundle.putBundle("returnLocation", returnLocationBundle);
                            bundle.putBoolean("locationType", locationType);
                            bundle.putBoolean("initialSelect", initialSelect);
                            bundle.putString("transactionId", transactionId);
                            NavHostFragment.findNavController(Fragment_Update_credit_card.this)
                                    .navigate(R.id.action_EditCreditCard_to_CardsOnAccount,bundle);

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
            System.out.println("Error" + error);
        }
    };
    //CountryList
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

                                if(card_PCountry==(country_ID))
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

                                if(card_PState==(state_ID))
                                {
                                    position = j;
                                }
                            }

                            ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, R.id.text1, State);
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
                    edt_Pincode.setText(postalCode);
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

