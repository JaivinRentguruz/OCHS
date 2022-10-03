package com.ochs.app.flexiicar_app.user;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.ADDPROFILEPICTURE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_CUSTOMER;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_SETTINGS;
import static com.ochs.app.apicall.ApiEndPoint.GETCOUNTRYLIST;
import static com.ochs.app.apicall.ApiEndPoint.REMOVEPROFILEPICTURE;
import static com.ochs.app.apicall.ApiEndPoint.STATELIST;
import static com.ochs.app.apicall.ApiEndPoint.UPDATECUSTOMERPROFILE;

public class Fragment_Update_Customer_Profile extends Fragment
{
    ImageView backimg,Img_Profile,RemoveProfilePic;
    Bundle CustomerBundle;
    Handler handler = new Handler();
    public String id="";
    public static Context context;
    EditText edtFirstName,edtLastName,edtMobileNO,edtEmail,edtStreetNo,edtUnit,edZipcode,edtCity;
   // EditText edtPhoneNo;
    TextView txt_DiscardProfile;
    Spinner Sp_Country,Sp_State;
    LinearLayout lblSubmitprofile,ll_Profile;
    public String[] Country,State;
    public int[] CountyId,StateId;
    HashMap<String, Integer> countryhashmap=new HashMap<String, Integer>();
    HashMap<String,Integer>Statehashmap=new HashMap<>();
    private int RESULT_LOAD_IMAGE = 1;
    String imagestr;
    JSONObject profilepicObj = new JSONObject();
    int cust_Country_ID, cust_State_ID;
    private Geocoder geocoder;
    private final int REQUEST_PLACE_ADDRESS = 40;
    ImageLoader imageLoader;
    String serverpath="",cust_Photo=" ";
    Bitmap bitmap = null;

    public static void initImageLoader(Context context)
    {
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
        return inflater.inflate(R.layout.fragment_update_customer_profile, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ((User_Profile) getActivity()).BottomnavInVisible();

        String bodyParam = "";

        initImageLoader(getActivity());
        imageLoader = ImageLoader.getInstance();

        SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
        serverpath = sp.getString("serverPath", "");
        id = sp.getString(getString(R.string.id), "");

        backimg=view.findViewById(R.id.backimg_editprofile);
        RemoveProfilePic=view.findViewById(R.id.RemoveProfilePic);
        lblSubmitprofile=view.findViewById(R.id.lblSubmitprofile);
        edtFirstName=view.findViewById(R.id.edt_Firstname);
        edtLastName=view.findViewById(R.id.edt_LastName);
        edtMobileNO=view.findViewById(R.id.edt_MobileNo);
       // edtPhoneNo=view.findViewById(R.id.edt_PhoneNO);
        edtEmail=view.findViewById(R.id.edt_EmailBox);
        ll_Profile=view.findViewById(R.id.ll_Profile);
        edtStreetNo=view.findViewById(R.id.edt_streetNum);
        edtUnit=view.findViewById(R.id.edt_UnitNum);
        edZipcode=view.findViewById(R.id.edt_Zipcode);
        edtCity=view.findViewById(R.id.Edt_City);

        Sp_Country=view.findViewById(R.id.sp_Country);
        Sp_State=view.findViewById(R.id.Sp_State);

        txt_DiscardProfile=view.findViewById(R.id.txt_DiscardProfile);

        AndroidNetworking.initialize(getActivity());
        Fragment_Update_Customer_Profile.context = getActivity();

        backimg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Update_Customer_Profile.this)
                        .navigate(R.id.action_Edit_Personal_info_to_User_Details);
            }
        });

        txt_DiscardProfile.setOnClickListener(new View.OnClickListener()
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
                                NavHostFragment.findNavController(Fragment_Update_Customer_Profile.this)
                                        .navigate(R.id.action_Edit_Personal_info_to_User_Details);
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

        Img_Profile=view.findViewById(R.id.Img_ProfilePic);
        Img_Profile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

        ll_Profile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
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

        CustomerBundle = getArguments().getBundle("CustomerBundle");
        edtFirstName.setText(CustomerBundle.getString("cust_FName"));
        edtLastName.setText(CustomerBundle.getString("cust_LName"));
        edtMobileNO.setText(CustomerBundle.getString("cust_MobileNo"));
        //edtPhoneNo.setText(CustomerBundle.getString("cust_Phoneno"));
        edtEmail.setText(CustomerBundle.getString("cust_Email"));
        edtStreetNo.setText(CustomerBundle.getString("cust_Street"));
        edtUnit.setText(CustomerBundle.getString("cust_UnitNo"));
        edZipcode.setText(CustomerBundle.getString("cust_ZipCode"));
        edtCity.setText(CustomerBundle.getString("cust_City"));

        cust_Country_ID = CustomerBundle.getInt("cust_Country_ID");
        cust_State_ID = CustomerBundle.getInt("cust_State_ID");

        cust_Photo = CustomerBundle.getString("cust_Photo");

        String url1 = serverpath+cust_Photo.substring(2);
        imageLoader.displayImage(url1,Img_Profile);

        AndroidNetworking.initialize(getActivity());

        lblSubmitprofile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (edtFirstName.getText().toString().equals(""))
                    CustomToast.showToast(getActivity(),"Please Enter FirstName",1);
                else if (edtLastName.getText().toString().equals(""))
                    CustomToast.showToast(getActivity(),"Please Enter LastName",1);
                else if (edtMobileNO.getText().toString().equals(""))
                    CustomToast.showToast(getActivity(),"Please Enter Mobile Number",1);
                else if (edtEmail.getText().toString().equals(""))
                    CustomToast.showToast(getActivity(),"Please Enter Email",1);
                else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches())
                {
                    edtEmail.setError("Enter valid Email address");
                    edtEmail.requestFocus();
                }
                else if (edtStreetNo.getText().toString().equals(""))
                    CustomToast.showToast(getActivity(),"Please Enter Street No",1);
                else if (edZipcode.getText().toString().equals(""))
                    CustomToast.showToast(getActivity(),"Please Enter Zipcode",1);
                else if (edtCity.getText().toString().equals(""))
                    CustomToast.showToast(getActivity(),"Please Enter City",1);
                else
                {
                    JSONObject bodyParam = new JSONObject();
                    try {
                        bodyParam.accumulate("CustomerId", Integer.parseInt(id));
                        bodyParam.accumulate("cust_FName", edtFirstName.getText().toString());
                        bodyParam.accumulate("cust_LName", edtLastName.getText().toString());
                        bodyParam.accumulate("cust_Email", edtEmail.getText().toString());
                        bodyParam.accumulate("cust_MobileNo", edtMobileNO.getText().toString());
                       // bodyParam.accumulate("cust_Phoneno", edtPhoneNo.getText().toString());
                        bodyParam.accumulate("cust_Street", edtStreetNo.getText().toString());
                        bodyParam.accumulate("cust_UnitNo", edtUnit.getText().toString());
                        bodyParam.accumulate("cust_City", edtCity.getText().toString());
                        bodyParam.accumulate("cust_ZipCode",edZipcode.getText().toString());
                        int s = countryhashmap.get(Sp_Country.getSelectedItem());
                        int s1=Statehashmap.get(Sp_State.getSelectedItem());
                        bodyParam.accumulate("cust_Country_ID",s);
                        bodyParam.accumulate("cust_State_ID", s1);
                        bodyParam.accumulate("cust_Gender", CustomerBundle.getString("cust_Gender"));
                        bodyParam.accumulate("cust_DOB", CustomerBundle.getString("cust_DOB"));
                        System.out.println(bodyParam);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    ApiService ApiService = new ApiService(UpdateCustomerProfile, RequestType.POST,
                            UPDATECUSTOMERPROFILE, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);
                }
            }
        });

        RemoveProfilePic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Img_Profile.setImageBitmap(null);
                Img_Profile.setImageResource(0);
                Img_Profile.invalidate();
                bitmap=null;

                JSONObject bodyParam = new JSONObject();
                try {
                    bodyParam.accumulate("Trans_ID", Integer.parseInt(id));
                    bodyParam.accumulate("Doc_Details",CustomerBundle.getString("cust_Photo"));
                    System.out.println(bodyParam);
                    ApiService ApiService = new ApiService(RemoveCustomerProfile, RequestType.POST,
                             REMOVEPROFILEPICTURE, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        try
        {
            ApiService ApiService = new ApiService(CountryList, RequestType.GET,
                    GETCOUNTRYLIST, BASE_URL_SETTINGS, new HashMap<String, String>(), bodyParam);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data)
        {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = null;
            Uri targetUri = data.getData();
            try
            {
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(targetUri));
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                imagestr = ConvertBitmapToString(resizedBitmap);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

            try {
                bitmap = getScaledBitmap(selectedImage,500,500);
                Bitmap temp = bitmap;

                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                temp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] image=stream.toByteArray();
                String img_str_base64 = Base64.encodeToString(image, Base64.DEFAULT);

                JSONObject profilepicObj = new JSONObject();
                profilepicObj.put("Trans_ID",Integer.parseInt(id));
                profilepicObj.put("Doc_For", 20);
                profilepicObj.put("fileBase64", img_str_base64);

                System.out.println(profilepicObj);
                try
                {
                    ApiService ApiService = new ApiService(UpdateCustomerProfile, RequestType.POST,
                            ADDPROFILEPICTURE, BASE_URL_CUSTOMER, new HashMap<String, String>(), profilepicObj);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
                Img_Profile.setImageBitmap(bitmap);
        }

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
                    edtCity.setText(city);
                    edZipcode.setText(postalCode);
                    edtUnit.setText(UnitNo);
                    for (int i = 0; i < State.length; i++)
                    {
                        if (State[i].equals(state))
                        {
                            Sp_State.setSelection(i);
                            break;
                        }
                    }
                    for (int j = 0; j < Country.length; j++)
                    {
                        if (Country[j].equals(country))
                        {
                            Sp_Country.setSelection(j);
                            break;
                        }
                    }

                } catch (IOException e)
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

    //method to convert the selected image to base64 encoded string
    public static String ConvertBitmapToString(Bitmap bitmap)
    {
        String encodedImage = "";

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        try {
            encodedImage= URLEncoder.encode(Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return encodedImage;
    }

    //UpdateCustomerProfile
    OnResponseListener UpdateCustomerProfile = new OnResponseListener()
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
                           // NavHostFragment.findNavController(Fragment_Update_Customer_Profile.this)
                                  //  .navigate(R.id.action_Edit_Personal_info_to_User_Details);
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

    //RemoveCustomerProfile
    OnResponseListener RemoveCustomerProfile = new OnResponseListener()
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
                            CountyId = new int[len];
                            int position = 0;

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

                            ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_layout, R.id.text1, Country);
                            Sp_Country.setAdapter(adapterCategories);
                            Sp_Country.setSelection(position);

                            Sp_Country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                            {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                                {
                                    try {
                                        int s=countryhashmap.get(Sp_Country.getSelectedItem());
                                        System.out.println(Sp_Country.getSelectedItem());
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

                                if(cust_State_ID == (state_ID))
                                {
                                    position = j;
                                }
                            }

                            ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(getContext().getApplicationContext(), R.layout.spinner_layout, R.id.text1, State);
                            Sp_State.setAdapter(adapterCategories);
                            Sp_State.setSelection(position);
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
