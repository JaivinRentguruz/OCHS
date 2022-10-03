package com.ochs.app.flexiicar_app.login;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_CUSTOMER;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_SETTINGS;
import static com.ochs.app.apicall.ApiEndPoint.GETCOUNTRYLIST;
import static com.ochs.app.apicall.ApiEndPoint.REGISTRATION;
import static com.ochs.app.apicall.ApiEndPoint.STATELIST;

public class Fragment_Driver_Profile_4 extends Fragment
{
    ImageView BackToRegister3;
    Handler handler = new Handler();
    public static Context context;
    public String id = "";
    Bundle RegistrationBundle;
    EditText edt_cardNo,edt_CvvNo,edt_cardholderName,edt_streetName,edtxtUnitNumber,edtPincodeNo,Edtext_City;
    Spinner SP_Country,SP_State;
    public String[] Country,State;
    public int[] CountryId,StateId;
    HashMap<String, Integer> countryhashmap=new HashMap<String, Integer>();
    HashMap<String,Integer>Statehashmap=new HashMap<>();
    private Geocoder geocoder;
    private final int REQUEST_PLACE_ADDRESS = 40;
    String country, state;
    JSONArray ImageList = new JSONArray();
    TextView lblDiscard,lblNext,lblExpairyDate;
    String a;
    int keyDel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver_profile_4, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        try {
            lblNext = view.findViewById(R.id.lblNext4);
            edt_cardNo = view.findViewById(R.id.edt_cardNo);
            lblExpairyDate = view.findViewById(R.id.lblExpairyDate);
            edt_CvvNo = view.findViewById(R.id.edt_CvvNo);
            edt_cardholderName = view.findViewById(R.id.edt_cardholderName);

            edt_streetName = view.findViewById(R.id.edt_streetNameCC);
            edtxtUnitNumber = view.findViewById(R.id.edtxtUnitNumberCC);
            edtPincodeNo = view.findViewById(R.id.edtPincodeNoCC);
            Edtext_City = view.findViewById(R.id.Edtext_CityCC);
            lblDiscard=view.findViewById(R.id.lblDiscard4);

            SP_Country=view.findViewById(R.id.spi_CountryList);
            SP_State=view.findViewById(R.id.Spi_StatelistCC);

            RegistrationBundle = getArguments().getBundle("RegistrationBundle");
            ImageList = new JSONArray(RegistrationBundle.getString("ImageList"));

            edt_streetName.setText(RegistrationBundle.getString("Cust_Street"));
            edtxtUnitNumber.setText(RegistrationBundle.getString("Cust_UnitNo"));
            edtPincodeNo.setText(RegistrationBundle.getString("Cust_ZipCode"));
            Edtext_City.setText(RegistrationBundle.getString("Cust_City"));
            edt_cardholderName.setText(RegistrationBundle.getString("Cust_FName"));

            country = RegistrationBundle.getString("Cust_Country_Name");
            state = RegistrationBundle.getString("Cust_State_Name");

            BackToRegister3 = view.findViewById(R.id.BackToRegister3);

            AndroidNetworking.initialize(getActivity());
            Fragment_Driver_Profile_4.context = getActivity();

            BackToRegister3.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle Registration = new Bundle();
                    Registration.putBundle("RegistrationBundle", RegistrationBundle);
                    Registration.putBoolean("BackTo", true);
                    System.out.println(Registration);
                    NavHostFragment.findNavController(Fragment_Driver_Profile_4.this)
                            .navigate(R.id.action_DriverProfile4_to_DriverProfile3,Registration);
                }
            });

            lblExpairyDate = view.findViewById(R.id.lblExpairyDate);
            lblExpairyDate.setOnClickListener(new View.OnClickListener()
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
                                    lblExpairyDate.setText(parsedDateofBirth);
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

            lblDiscard.setOnClickListener(new View.OnClickListener()
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
                                    Intent i = new Intent(getActivity(), Login.class);
                                    startActivity(i);
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

            ImageView imgScanDrivingLicense = view.findViewById(R.id.ScancreditCard);

            imgScanDrivingLicense.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    Intent i = new Intent(getActivity(), ScanDrivingLicense.class);
                    i.putExtra("afterScanBackTo", 1);
                    startActivity(i);

                }
            });

            edt_streetName.setOnClickListener(new View.OnClickListener()
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
                        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)).build(context);
                        startActivityForResult(intent, REQUEST_PLACE_ADDRESS);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            edt_cardNo.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    boolean flag = true;
                    String eachBlock[] = edt_cardNo.getText().toString().split("-");
                    for (int i = 0; i < eachBlock.length; i++)
                    {
                        if (eachBlock[i].length() > 4) {
                            flag = false;
                        }
                    }
                    if (flag)
                    {
                        edt_cardNo.setOnKeyListener(new View.OnKeyListener()
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

                            if (((edt_cardNo.getText().length() + 1) % 5) == 0)
                            {
                                if (edt_cardNo.getText().toString().split("-").length <= 3)
                                {
                                    edt_cardNo.setText(edt_cardNo.getText() + "-");
                                    edt_cardNo.setSelection(edt_cardNo.getText().length());
                                }
                            }
                            a = edt_cardNo.getText().toString();
                        } else {
                            a = edt_cardNo.getText().toString();
                            keyDel = 0;
                        }

                    } else {
                        edt_cardNo.setText(a);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable)
                {
                    // TODO Auto-generated method stub
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {
                    // TODO Auto-generated method stub
                }
            });

            lblNext.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {

                        for (int i = 0; i < ImageList.length(); i++)
                        {
                            try {
                                System.out.println(i);

                                JSONObject imgObj = ImageList.getJSONObject(i);

                                String imgPath = imgObj.getString("fileBase64");

                                try {
                                    File imgFile = new File(imgPath);

                                    if(imgFile.exists())
                                    {
                                        Uri selectedImage = Uri.fromFile(imgFile);
                                        System.out.println("selectedImage" + selectedImage);

                                        Bitmap bitmap = getScaledBitmap(selectedImage, 400, 400);
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                                        byte[] image = stream.toByteArray();
                                        String img_str_base64 = Base64.encodeToString(image, Base64.NO_WRAP);

                                        imgObj.put("fileBase64", img_str_base64);
                                    }
                                    // ImageList.put(imgObj);
                                }catch (FileNotFoundException e)
                                {
                                    e.printStackTrace();
                                }
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }

                        if (edt_cardNo.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Valid card number",1);
                        else if (lblExpairyDate.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please correct validity",1);
                        else if (edt_CvvNo.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Enter Your CVV",1);
                        else if (edt_cardholderName.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Enter Credit Card Holder Name",1);
                        else if (edt_streetName.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Enter Your Street NO & Name",1);
                        else if (SP_Country.getSelectedItem().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Select Your Country",1);
                        else if (SP_State.getSelectedItem().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Select Your State",1);
                        else if (Edtext_City.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Select Your City",1);
                        else if (edtPincodeNo.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Enter Zip Code",1);
                        else {
                            JSONObject bodyParam = new JSONObject();
                            try {
                                bodyParam.accumulate("Cust_FName", RegistrationBundle.getString("Cust_FName"));
                                bodyParam.accumulate("Cust_Street", RegistrationBundle.getString("Cust_Street"));
                                bodyParam.accumulate("Cust_UnitNo", RegistrationBundle.getString("Cust_UnitNo"));
                                bodyParam.accumulate("Cust_City", RegistrationBundle.getString("Cust_City"));
                                bodyParam.accumulate("Cust_State_ID", RegistrationBundle.getInt("Cust_State_ID"));
                                bodyParam.accumulate("Cust_Country_ID", RegistrationBundle.getInt("Cust_Country_ID"));
                                bodyParam.accumulate("Cust_Country_Name", RegistrationBundle.getString("Cust_Country_Name"));
                                bodyParam.accumulate("Cust_State_Name", RegistrationBundle.getString("Cust_State_Name"));
                                bodyParam.accumulate("Cust_ZipCode", RegistrationBundle.getString("Cust_ZipCode"));
                                bodyParam.accumulate("Cust_DOB", RegistrationBundle.getString("Cust_DOB"));
                                bodyParam.accumulate("Cust_Email", RegistrationBundle.getString("Cust_Email"));
                                bodyParam.accumulate("Cust_MobileNo", RegistrationBundle.getString("Cust_MobileNo"));

                                bodyParam.accumulate("Licence_No", RegistrationBundle.getString("Licence_No"));
                                bodyParam.accumulate("LIssue_Date", RegistrationBundle.getString("LIssue_Date"));
                                bodyParam.accumulate("LExpiry_Date", RegistrationBundle.getString("LExpiry_Date"));
                                bodyParam.accumulate("LIssue_By", RegistrationBundle.getString("LIssue_By"));
                                bodyParam.accumulate("PasswordHash", RegistrationBundle.getString("PasswordHash"));


                                int s = countryhashmap.get(SP_Country.getSelectedItem());
                                int s1 = Statehashmap.get(SP_State.getSelectedItem());

                                bodyParam.accumulate("CustCard_PCountry", s);
                                bodyParam.accumulate("CustCard_PState", s1);
                                bodyParam.accumulate("Card_No", edt_cardNo.getText().toString());
                                bodyParam.accumulate("Card_Name", edt_cardholderName.getText().toString());
                                bodyParam.accumulate("CVS_Code", edt_CvvNo.getText().toString());
                                bodyParam.accumulate("CustCard_ExpiryDate", lblExpairyDate.getText().toString());
                                bodyParam.accumulate("CustCard_PStreet", edt_streetName.getText().toString());
                                bodyParam.accumulate("CustCard_PUnitNo", edtxtUnitNumber.getText().toString());
                                bodyParam.accumulate("CustCard_PCity", Edtext_City.getText().toString());
                                bodyParam.accumulate("CustCard_Default", 1);

                                bodyParam.accumulate("ImageList", ImageList);

                                System.out.println(bodyParam);
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                            ApiService ApiService = new ApiService(Registration, RequestType.POST,
                                    REGISTRATION, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            try
            {
                ApiService ApiService = new ApiService(CountryList, RequestType.GET,
                        GETCOUNTRYLIST, BASE_URL_SETTINGS, new HashMap<String, String>(), "");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private Bitmap getScaledBitmap(Uri selectedImage, int width, int height) throws FileNotFoundException
    {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {
            // Calculate ratios of height and width to requested one
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
    //REGISTRATION
    OnResponseListener Registration = new OnResponseListener()
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

                            int transactionId= responseJSON.getInt("transactionId");
                            Bundle Registration = new Bundle();
                            Registration.putBundle("RegistrationBundle", RegistrationBundle);
                            Registration.putInt("transactionId",transactionId);
                            System.out.println(Registration);
                            NavHostFragment.findNavController(Fragment_Driver_Profile_4.this)
                                    .navigate(R.id.action_DriverProfile4_to_Complete_Register,Registration);
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

    //Countrylist
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
                            CountryId = new int[len];

                            int position = 0;

                            for (int j = 0; j < len; j++)
                            {
                                final JSONObject test = (JSONObject) Countrylist.get(j);
                                final int country_ID = test.getInt("country_ID");
                                final String country_Name = test.getString("country_Name");
                                Country[j] = country_Name;
                                CountryId[j] = country_ID;

                                countryhashmap.put(country_Name,country_ID);

                                if(country.equals(country_Name))
                                {
                                    position = j;
                                }
                            }

                            ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(getContext().getApplicationContext(), R.layout.spinner_layout, R.id.text1, Country);
                            SP_Country.setAdapter(adapterCategories);
                            SP_Country.setSelection(position);


                            SP_Country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                            {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                                {
                                    try {
                                        i = countryhashmap.get(SP_Country.getSelectedItem());
                                        System.out.println(SP_Country.getSelectedItem());
                                        System.out.println(i + "");

                                        try {
                                            String bodyParam1 = "countryId=" + i;
                                            ApiService ApiService = new ApiService(StateList, RequestType.GET,
                                                    STATELIST, BASE_URL_SETTINGS, new HashMap<String, String>(), bodyParam1);
                                        } catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    } catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView)
                                {

                                }
                            });

                           /* String bodyParam1 = "countryId=" + CountryId[position];
                            ApiService ApiService = new ApiService(StateList, RequestType.GET,
                                    STATELIST, BASE_URL_SETTINGS, new HashMap<String, String>(), bodyParam1);*/
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
                            int position = 0;

                            for (int j = 0; j < len; j++)
                            {
                                final JSONObject test = (JSONObject) StateList.get(j);
                                final int state_ID = test.getInt("state_ID");
                                final String state_Name = test.getString("state_Name");

                                State[j] = state_Name;
                                StateId[j] = state_ID;
                                Statehashmap.put(state_Name,state_ID);

                                if(state.equals(state_Name))
                                {
                                    position = j;
                                }
                            }

                            ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(getContext().getApplicationContext(), R.layout.spinner_layout, R.id.text1, State);
                            SP_State.setAdapter(adapterCategories);
                            SP_State.setSelection(position);

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

                    edt_streetName.setText(Street);
                    Edtext_City.setText(city);
                    edtPincodeNo.setText(postalCode);
                    edtxtUnitNumber.setText(UnitNo);

                    for(int j=0;j<Country.length;j++)
                    {

                        if(Country[j].equals(country))
                        {
                            SP_Country.setSelection(j);
                            break;
                        }
                    }
                    for(int i=0;i<State.length;i++)
                    {

                        if(State[i].equals(state))
                        {
                            SP_State.setSelection(i);
                            break;
                        }
                    }



                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                //setMarker(latLng);
            }
        }
    }
}