package com.ochs.app.flexiicar_app.booking;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.adapters.Location;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.LOCATION_SEARCH_LIST;
import static com.ochs.app.apicall.ApiEndPoint.LOCATION_LIST;

public class Fragment_Available_location extends Fragment
{
    ImageView Back;
    public static Context context;
    public String id = "";
    Handler handler = new Handler();
    Boolean locationType, initialSelect;
    Bundle locationBundle, returnLocationBundle;
    EditText edt_searchloc;
    int flag=0;
    RelativeLayout rlToggleBtn;
    TextView lbl_Discard;
   // int vehicleClassWise;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_available_location, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
        //id = sp.getString(getString(R.string.id), "");




        initialSelect = getArguments().getBoolean("initialSelect");
        locationType = getArguments().getBoolean("locationType");
        locationBundle = getArguments().getBundle("location");
        returnLocationBundle = getArguments().getBundle("returnLocation");

        edt_searchloc=view.findViewById(R.id.edttxt_searchLoc);
        lbl_Discard=view.findViewById(R.id.lbl_Discard);
        lbl_Discard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are You Sure You Want To Cancel?");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                NavHostFragment.findNavController(Fragment_Available_location.this)
                                        .navigate(R.id.action_Available_location_to_Search_activity);
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

        Back = view.findViewById(R.id.Back);
        Back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Available_location.this)
                        .navigate(R.id.action_Available_location_to_Search_activity);
            }
        });

        rlToggleBtn = view.findViewById(R.id.rlToggleBtn);
        rlToggleBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavHostFragment.findNavController(Fragment_Available_location.this)
                        .navigate(R.id.action_Available_location_to_Search_activity);
            }
        });
        AndroidNetworking.initialize(getActivity());

        JSONObject bodyParam = new JSONObject();
        try
        {
            bodyParam.accumulate("location_ID",locationBundle.getInt("location_ID"));
            System.out.println(bodyParam);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        ApiService ApiService = new ApiService(LocationList, RequestType.GET,
                LOCATION_LIST, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);

        edt_searchloc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                JSONObject bodyParam = new JSONObject();
                try
                {
                    bodyParam.accumulate("LocationName",edt_searchloc.getText().toString());
                    System.out.println(bodyParam);
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                ApiService ApiService = new ApiService(LocationList, RequestType.POST,
                        LOCATION_SEARCH_LIST, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
            }
        });
    }

    OnResponseListener LocationList = new OnResponseListener()
    {
        @Override
        public void onSuccess(String response1, HashMap<String, String> headers)
        {

            final String response = response1;
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        JSONObject responseJSON = new JSONObject(response);
                        Boolean status = responseJSON.getBoolean("status");

                        if (status)
                        {
                            JSONObject resultSet = responseJSON.getJSONObject("resultSet");
                            final JSONArray Locationlist = resultSet.getJSONArray("v0040_Location_Master");

                            RelativeLayout rlLocationlist = getActivity().findViewById(R.id.location_available_layout);
                            rlLocationlist.removeAllViews();
                            int len;
                            len = Locationlist.length();

                            for (int j = 0; j < len; j++)
                            {

                                final JSONObject test = (JSONObject) Locationlist.get(j);

                                final Location location = new Location();

                                location.setLocationName(test.getString("location_Name"));
                                location.setCity(test.getString("city"));
                                location.setStreet(test.getString("street"));
                                location.setZipcode(test.getString("zipcode"));
                                location.setLocationId(test.getInt("location_ID"));
                                location.setLatitude(test.getDouble("latitude"));
                                location.setLongitude(test.getDouble("longitude"));
                                location.setPickLocChargePerMile(test.getDouble("pickLocChargePerMile"));

                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                lp.setMargins(0, 0, 0, 0);

                                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                LinearLayout view = (LinearLayout) inflater.inflate(R.layout.location_available_list, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                view.setId(200 + j);
                                view.setLayoutParams(lp);


                                TextView lbl_LocName,lbl_Address;
                                lbl_LocName = view.findViewById(R.id.lbl_LocName);
                                lbl_Address = view.findViewById(R.id.lbl_Address);

                                final String location_Name = location.getLocationName();
                                final String city = location.getCity();
                                final String street = location.getStreet();
                                final String zipcode = location.getZipcode();

                                LinearLayout select = view.findViewById(R.id.lblselectedlocation);
                                //  final TextView txtSelect = view.findViewById(R.id.txtSelect);

                                select.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {

                                        Bundle bundle = new Bundle();
                                        bundle.putString("location_Name", location.getLocationName());
                                        bundle.putString("street", location.getStreet());
                                        bundle.putString("city", location.getCity());
                                        bundle.putString("zipcode", location.getZipcode());
                                        bundle.putInt("location_ID", location.getLocationId());
                                        bundle.putDouble("latitude", location.getLatitude());
                                        bundle.putDouble("longitude", location.getLongitude());
                                        bundle.putDouble("longitude", location.getLongitude());
                                        bundle.putDouble("pickLocChargePerMile", location.getPickLocChargePerMile());

                                        System.out.println(bundle);

                                        System.out.println("Location Type" + locationType);

                                        if (!initialSelect)
                                        {
                                            if (locationType)
                                            {
                                                locationBundle = bundle;
                                                //locations.putBundle("returnLocation", returnLocationBundle);
                                            }
                                            else
                                            {
                                                //locations.putBundle("location", locationBundle);
                                                returnLocationBundle = bundle;
                                            }
                                        }
                                        else
                                        {
                                            locationBundle = bundle;
                                            returnLocationBundle = bundle;
                                        }

                                        RelativeLayout rlLocationlistTemp = getActivity().findViewById(R.id.location_available_layout);

                                        for (int n = 0; n < rlLocationlistTemp.getChildCount(); n++)
                                        {
                                            // final TextView txtSelectTemp = rlLocationlistTemp.getChildAt(n).findViewById(R.id.txtSelect);
                                            //txtSelectTemp.setText("SELECT");

                                        }
                                        // txtSelect.setText("CHANGE");

                                        Bundle locations = new Bundle();
                                        locations.putBundle("location", locationBundle);
                                        locations.putBoolean("locationType", locationType);
                                        locations.putBoolean("initialSelect", initialSelect);
                                        locations.putBundle("returnLocation", returnLocationBundle);
                                        locations.putDouble("latitude", location.getLatitude());
                                        locations.putDouble("longitude", location.getLongitude());
                                        locations.putDouble("pickLocChargePerMile", location.getPickLocChargePerMile());

                                        NavHostFragment.findNavController(Fragment_Available_location.this)
                                                .navigate(R.id.action_Available_location_to_Selected_location, locations);
                                    }
                                });
                                rlLocationlist.addView(view);
                                lbl_LocName.setText(location_Name);
                                lbl_Address.setText(city +","+street+","+ zipcode);
                            }
                        }
                        else
                        {
                            String errorString = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),errorString,1);
                        }
                        //Loading.hide();
                    } catch (Exception e)
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