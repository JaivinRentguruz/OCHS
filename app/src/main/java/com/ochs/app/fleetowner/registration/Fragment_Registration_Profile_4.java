package com.ochs.app.fleetowner.registration;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.R;
import com.ochs.app.adapters.MonthYearPickerDialog;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONArray;

import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Fragment_Registration_Profile_4 extends Fragment
{
    ImageView Back;
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

        try {
           // RegistrationBundle = getArguments().getBundle("RegistrationBundle");
          //  ImageList = new JSONArray(RegistrationBundle.getString("ImageList"));
        }catch (Exception e)
        {
            e.printStackTrace();
        }

       /* edt_streetName.setText(getArguments().getBundle("RegistrationBundle").getString("Cust_Street"));
        edtxtUnitNumber.setText(getArguments().getBundle("RegistrationBundle").getString("Cust_UnitNo"));
        edtPincodeNo.setText(getArguments().getBundle("RegistrationBundle").getString("Cust_ZipCode"));
        Edtext_City.setText(getArguments().getBundle("RegistrationBundle").getString("Cust_City"));
        edt_cardholderName.setText(getArguments().getBundle("RegistrationBundle").getString("Cust_FName"));

        country = getArguments().getBundle("RegistrationBundle").getString("Cust_Country_Name");
        state = getArguments().getBundle("RegistrationBundle").getString("Cust_State_Name");*/

        Back = view.findViewById(R.id.BackToRegister3);
        AndroidNetworking.initialize(getActivity());
        Fragment_Registration_Profile_4.context = getActivity();

        Back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle Registration = new Bundle();
                Registration.putBundle("RegistrationBundle", RegistrationBundle);
                System.out.println(Registration);
                NavHostFragment.findNavController(Fragment_Registration_Profile_4.this)
                        .navigate(R.id.action_DriverProfile4_to_DriverProfile3);
            }
        });

        ImageView imgScanDrivingLicense = view.findViewById(R.id.ScancreditCard);

        imgScanDrivingLicense.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                //Intent i = new Intent(getActivity(), ScanDrivingLicense.class);
                //i.putExtra("afterScanBackTo", 4);
                //startActivity(i);

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
                try
                {
                    NavHostFragment.findNavController(Fragment_Registration_Profile_4.this)
                            .navigate(R.id.action_DriverProfile4_to_Complete_Register);

                   /* for (int i = 0; i < ImageList.length(); i++)
                    {
                        try {

                            System.out.println(i);

                            JSONObject imgObj = ImageList.getJSONObject(i);

                            String imgPath = imgObj.getString("fileBase64");

                            File imgFile = new File(imgPath);
                            Uri selectedImage = Uri.fromFile(imgFile);

                            Bitmap temp = getBitmapFromUri(selectedImage);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            temp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] image = stream.toByteArray();
                            String img_str_base64 = Base64.encodeToString(image, Base64.DEFAULT);

                            imgObj.put("fileBase64", img_str_base64);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        if (edt_cardNo.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Your Credit Card No", Toast.LENGTH_LONG).show();
                        else if (edt_ExDate.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Your Credit Card Expiry Date", Toast.LENGTH_LONG).show();
                        else if (edt_CvvNo.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Your CVV", Toast.LENGTH_LONG).show();
                        else if (edt_cardholderName.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Credit Card Holder Name", Toast.LENGTH_LONG).show();
                        else if (edt_streetName.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Your Street NO & Name", Toast.LENGTH_LONG).show();
                        else if (SP_Country.getSelectedItem().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Select Your Country", Toast.LENGTH_LONG).show();
                        else if (SP_State.getSelectedItem().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Select Your State", Toast.LENGTH_LONG).show();
                        else if (Edtext_City.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Select Your City", Toast.LENGTH_LONG).show();
                        else if (edtPincodeNo.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Zip Code", Toast.LENGTH_LONG).show();
                        else {

                            NavHostFragment.findNavController(Fragment_Driver_Profile_4.this)
                                    .navigate(R.id.action_DriverProfile4_to_Complete_Register);
                        }
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException
    {
        ParcelFileDescriptor parcelFileDescriptor =
                getActivity().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

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
                    String Street=addresses.get(0).getFeatureName();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();

                    Log.e("Address: ", "" + address);
                    Log.e("City: ", "" + city);
                    Log.e("Street: ", "" + Street);
                    Log.e("State: ", "" + state);
                    Log.e("Country: ", "" + country);
                    Log.e("APostalCode: ", "" + postalCode);

                    edt_streetName.setText(Street);
                    Edtext_City.setText(city);
                    edtPincodeNo.setText(postalCode);

                    for(int i=0;i<State.length;i++)
                    {

                        if(State[i].equals(state))
                        {
                            SP_State.setSelection(i);
                            break;
                        }
                    }

                    for(int j=0;j<Country.length;j++)
                    {

                        if(Country[j].equals(country))
                        {
                            SP_Country.setSelection(j);
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