package com.ochs.app.flexiicar_app.currentbookinglist;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hufsm.keyholder.mobile.KeyholderInterface;
import com.hufsm.secureaccess.ble.log.LoggingInterface;
import com.hufsm.tacs.mobile.TacsInterface;
import com.hufsm.tacs.mobile.TacsKeyring;
import com.hufsm.telematics.mobile.TelematicsInterface;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ochs.app.R;
import com.ochs.app.SplashScreen;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import com.ochs.app.flexiicar_app.booking.Booking_Activity;
import com.ochs.app.flexiicar_app.ochs.Fragment_VehicleAvailable;
import com.ochs.app.flexiicar_app.ochs.Fragment_Vehicle_LocationAndKey;
import com.ochs.app.flexiicar_app.user.User_Profile;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_HUFKEY;
import static com.ochs.app.apicall.ApiEndPoint.GET_MOBILEKEY_DETAILS;

public class Fragment_Location_And_Key extends Fragment implements TacsInterface.Callback, LoggingInterface.EventLogCallback , OnMapReadyCallback  {

    TextView Done,txtVehiclaName;
    ImageView imgback;

    Button btnLock,btnUnlock,btnBtConnect,btnBtDisConnect,btnTrunk;
    TacsInterface tacsInterface;
    TacsInterface.VehicleStatus currentStatus;
    public static final Collection<TelematicsInterface.TelematicsDataType> DEFAULT_TELEMATICS_REQUEST = TelematicsInterface.TelematicsDataType.buildRequest();
    static final String NOT_AVAILABLE_STATUS = "Not available";
    static final String NOT_CONNECTED_STATUS = "Not connected";
    final Object mutex = new Object();
    static final String TAG = Fragment_Location_And_Key.class.getSimpleName();
    TacsKeyring keyring = null;
    String MY_MOCK_ACCESS_GRANT_ID;
    Handler handler = new Handler();
    Bundle VehicleModelBundle;
    int vehiclE_ID;
    String make_Name,model_Name,currAddress,path,lic_Num;
    Double lattitude,longitude;
    int lockKey;
    ImageView Back;
    private boolean useMockMode = true;

    BluetoothAdapter mBluetoothAdapter;
    String tacsKeyring;

    ImageLoader imageLoader;
    String serverpath="";

    TextView txt_lockKey,txt_address;
    ImageView carimage,carimage2,lock,unlock;
    private MapView mapView;
    private GoogleMap googleMap;
    final int LOCATION_REQUEST_CODE = 1;
    public static Context context;

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
        return inflater.inflate(R.layout.fragment_location_and_key_2, container, false);
    }

    @Override
    public void onResume()
    {
        try
        {
            super.onResume();
            mapView.onResume();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        try {
            initImageLoader(getActivity());
            imageLoader = ImageLoader.getInstance();

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            serverpath = sp.getString("serverPath", "");

        //VehicleModelBundle = getArguments().getBundle("VehicleModelBundle");
        //vehiclE_ID = VehicleModelBundle.getInt("vehiclE_ID");
        //make_Name = VehicleModelBundle.getString("make_Name");
        //model_Name = VehicleModelBundle.getString("model_Name");

            if (getArguments()==null){
                vehiclE_ID = SplashScreen.agreement;
                model_Name = SplashScreen.model_Name;
                path = SplashScreen.veh_Img_Path;
                lic_Num = SplashScreen.lic_Num;
                if (!SplashScreen.lattitude.isEmpty()) {
                    lattitude = Double.valueOf(SplashScreen.lattitude);
                    longitude = Double.valueOf(SplashScreen.longitude);
                }
            } else {
                vehiclE_ID = getArguments().getInt("agreement_ID");
                //make_Name = getArguments().getString("make_Name");
                model_Name = getArguments().getString("model_Name");
                path = getArguments().getString("veh_Img_Path");
                lic_Num = getArguments().getString("lic_Num");
                if (!getArguments().getString("lattitude").isEmpty()) {
                    lattitude = Double.valueOf(getArguments().getString("lattitude"));
                    longitude = Double.valueOf(getArguments().getString("longitude"));
                }
            }

       // vehiclE_ID = 4681;

            mapView = view.findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);


            // Fragment_Vehicle_LocationAndKey.context = getActivity();
            MapsInitializer.initialize(this.getActivity());

            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE}, LOCATION_REQUEST_CODE);
            }
            else
            {
                mapView.getMapAsync(this);
            }

            turnOnBluetoothDevice();



 /*           VehicleModelBundle = getArguments().getBundle("VehicleModelBundle");
            vehiclE_ID = VehicleModelBundle.getInt("vehiclE_ID");
            make_Name = VehicleModelBundle.getString("make_Name");
            model_Name = VehicleModelBundle.getString("model_Name");
            currAddress = VehicleModelBundle.getString("currAddress");
            lockKey = VehicleModelBundle.getInt("lockKey");
            path = VehicleModelBundle.getString("path");

            //vehiclE_ID = 4681;

            carimage = view.findViewById(R.id.carimage);
            String url1 = serverpath+path.substring(2);
            imageLoader.displayImage(url1, carimage);

            txtVehiclaName= view.findViewById(R.id.txtVehiclaName);
            txtVehiclaName.setText(make_Name);

            txt_lockKey = view.findViewById(R.id.txt_lockKey);
            txt_lockKey.setText(String.valueOf(lockKey));

            txt_address = view.findViewById(R.id.txt_address);
            txt_address.setText(currAddress);*/

            txtVehiclaName= view.findViewById(R.id.txtVehiclaName);
            txtVehiclaName.setText(model_Name);
            txt_address = view.findViewById(R.id.txt_address);

            if (lattitude != null) {
                txt_address.setText(getLocation(getActivity(), lattitude, longitude));
            } else {
                CustomToast.showToast(getActivity(), "Vehicle Address Not Found", 1);
                txt_address.setText("Vehicle Address Not Found");
                mapView.setVisibility(View.GONE);
            }
            txt_lockKey = view.findViewById(R.id.txt_lockKey);
            txt_lockKey.setText(lic_Num);

            carimage = view.findViewById(R.id.carimage);
            String url1 = serverpath+path.substring(2);
            imageLoader.displayImage(url1, carimage);

            Done=view.findViewById(R.id.lblDone);
            Done.setText("Home");
            Done.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent i = new Intent(getContext(), Booking_Activity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });



            lock = view.findViewById(R.id.lock);
            unlock = view.findViewById(R.id.unlock);
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            lock.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    /*unlock.setVisibility(View.VISIBLE);
                    lock.setVisibility(View.GONE);*/

                    try {
                        if(!mBluetoothAdapter.isEnabled())
                        {
                            CustomToast.showToast(getActivity(), "Please Connect Bluetooth", 0);
                            unlock.setVisibility(View.VISIBLE);
                            lock.setVisibility(View.GONE);
                        }
                        else {
                            tacsInterface.vehicleAccess().lockDoors();
                            unlock.setVisibility(View.VISIBLE);
                            lock.setVisibility(View.GONE);
                        }
                    } catch (Exception e){
                        unlock.setVisibility(View.VISIBLE);
                        lock.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            });

            unlock.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                /*    lock.setVisibility(View.VISIBLE);
                    unlock.setVisibility(View.GONE);*/

                    try {
                        if(!mBluetoothAdapter.isEnabled())
                        {
                            CustomToast.showToast(getActivity(), "Please Connect Bluetooth", 0);
                            lock.setVisibility(View.VISIBLE);
                            unlock.setVisibility(View.GONE);
                        }
                        else {
                            tacsInterface.vehicleAccess().unlockDoors();
                            lock.setVisibility(View.VISIBLE);
                            unlock.setVisibility(View.GONE);
                        }
                    } catch (Exception e){
                        lock.setVisibility(View.VISIBLE);
                        unlock.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            });
            AndroidNetworking.initialize(getActivity());

            String bodyParam = "";
            try {
                bodyParam += "agreementId=" + vehiclE_ID;
                System.out.println(bodyParam);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            ApiService ApiService = new ApiService(getMobileKeyDetails, RequestType.GET,
                    GET_MOBILEKEY_DETAILS, BASE_URL_HUFKEY, new HashMap<String, String>(), bodyParam);

     /*   imgback=view.findViewById(R.id.Back);
        txtVehiclaName=view.findViewById(R.id.txtVehiclaName);

        btnLock = view.findViewById(R.id.btnLock);
        btnUnlock = view.findViewById(R.id.btnUnlock);
        btnBtConnect = view.findViewById(R.id.btnBtConnect);
        btnBtDisConnect= view.findViewById(R.id.btnBtDisConnect);
        btnTrunk = view.findViewById(R.id.btnTrunk);



        txtVehiclaName= view.findViewById(R.id.txtVehiclaName);
        txtVehiclaName.setText(model_Name);
       // txtVehiclaName.setText("Test");

        imgback.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getContext(), Booking_Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        Done=view.findViewById(R.id.lblDone);
        Done.setText("Home");
        Done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(getContext(), Booking_Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        btnBtConnect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                turnOnBluetoothDevice();
            }
        });

        btnBtDisConnect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                turnOffBluetoothDevice();
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnLock.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!mBluetoothAdapter.isEnabled())
                {
                    CustomToast.showToast(getActivity(), "Please Connect Bluetooth", 0);
                }
                else {
                    tacsInterface.vehicleAccess().lockDoors();
                }
            }
        });

        btnUnlock.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!mBluetoothAdapter.isEnabled())
                {
                    CustomToast.showToast(getActivity(), "Please Connect Bluetooth", 0);
                }
                else {
                    tacsInterface.vehicleAccess().unlockDoors();
                }
            }
        });
        AndroidNetworking.initialize(getActivity());

        String bodyParam = "";
        try {
            bodyParam += "agreementId=" + vehiclE_ID;
            System.out.println(bodyParam);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        ApiService ApiService = new ApiService(getMobileKeyDetails, RequestType.GET,
                GET_MOBILEKEY_DETAILS, BASE_URL_HUFKEY, new HashMap<String, String>(), bodyParam);*/

        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    OnResponseListener getMobileKeyDetails = new OnResponseListener()
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
                            try {
                                JSONObject resultSet = responseJSON.getJSONObject("resultSet");
                                JSONObject hufKeyDetailModelList = resultSet.getJSONObject("hufKeyDetailModel");

                                JSONObject keyringJson = hufKeyDetailModelList.getJSONObject("keyring");
                                {
                                    String userLeaseTokenTableVersion = keyringJson.getString("userLeaseTokenTableVersion");
                                    JSONArray userLeaseTokenTable = keyringJson.getJSONArray("userLeaseTokenTable");
                                    JSONArray userSorcBlobTable = keyringJson.getJSONArray("userSorcBlobTable");


                                    int len;
                                    len = userLeaseTokenTable.length();
                                    for (int j = 0; j < len; j++) {
                                        final JSONObject test = (JSONObject) userLeaseTokenTable.get(j);
                                        final String leaseId = test.getString("leaseId");
                                        final String userId = test.getString("userId");
                                        final String sorcId = test.getString("sorcId");

                                        final String leaseTokenDocumentVersion = test.getString("leaseTokenDocumentVersion");
                                        final String leaseTokenId = test.getString("leaseTokenId");
                                        final String sorcAccessKey = test.getString("sorcAccessKey");
                                        final String startTime = test.getString("startTime");
                                        final String endTime = test.getString("endTime");

                                        JSONArray serviceGrantList = test.getJSONArray("serviceGrantList");

                                        int len1;
                                        len1 = serviceGrantList.length();

                                        for (int k = 0; k < len1; k++) {
                                            final JSONObject test1 = (JSONObject) serviceGrantList.get(k);
                                            final String serviceGrantId = test1.getString("serviceGrantId");
                                            final JSONObject validators = test1.getJSONObject("validators");
                                            final String ValistartTime = validators.getString("startTime");
                                            final String ValiendTime = validators.getString("endTime");
                                        }
                                    }

                                    int len3;
                                    len3 = userSorcBlobTable.length();
                                    for (int i = 0; i < len3; i++) {
                                        final JSONObject test = (JSONObject) userSorcBlobTable.get(i);
                                        //final String sorcId = test.getString("sorcId");
                                        final String blob = test.getString("blob");
                                        final String blobMessageCounter = test.getString("blobMessageCounter");

                                    }

                                }

                                JSONObject tacsKeyringJson = hufKeyDetailModelList.getJSONObject("tacsKeyring");
                                tacsKeyring=tacsKeyringJson.toString();
                                {
                                    String tacsLeaseTokenTableVersion = tacsKeyringJson.getString("tacsLeaseTokenTableVersion");
                                    String tacsSorcBlobTableVersion = tacsKeyringJson.getString("tacsSorcBlobTableVersion");
                                    JSONArray tacsLeaseTokenTable = tacsKeyringJson.getJSONArray("tacsLeaseTokenTable");
                                    JSONArray tacsSorcBlobTable = tacsKeyringJson.getJSONArray("tacsSorcBlobTable");

                                    int len;
                                    len = tacsLeaseTokenTable.length();
                                    for (int j = 0; j < len; j++)
                                    {
                                        final JSONObject test = (JSONObject) tacsLeaseTokenTable.get(j);
                                        final String vehicleAccessGrantId = test.getString("vehicleAccessGrantId");
                                        JSONObject leaseToken = test.getJSONObject("leaseToken");
                                        final String leaseId = leaseToken.getString("leaseId");
                                        final String userId = leaseToken.getString("userId");
                                        final String sorcId = leaseToken.getString("sorcId");

                                        final String leaseTokenDocumentVersion = leaseToken.getString("leaseTokenDocumentVersion");
                                        final String leaseTokenId = leaseToken.getString("leaseTokenId");
                                        final String sorcAccessKey = leaseToken.getString("sorcAccessKey");
                                        final String startTime = leaseToken.getString("startTime");
                                        final String endTime = leaseToken.getString("endTime");

                                        MY_MOCK_ACCESS_GRANT_ID=vehicleAccessGrantId;

                                        final JSONArray serviceGrantList = leaseToken.getJSONArray("serviceGrantList");

                                        int len1;
                                        len1 = serviceGrantList.length();

                                        for (int k = 0; k < len1; k++) {
                                            final JSONObject test1 = (JSONObject) serviceGrantList.get(k);
                                            final String serviceGrantId = test1.getString("serviceGrantId");
                                            final JSONObject validators = test1.getJSONObject("validators");
                                            final String ValistartTime = validators.getString("startTime");
                                            final String ValiendTime = validators.getString("endTime");
                                        }
                                    }
                                    int len3;
                                    len3 = tacsSorcBlobTable.length();
                                    for (int i = 0; i < len3; i++)
                                    {
                                        final JSONObject test = (JSONObject) tacsSorcBlobTable.get(i);
                                        //final String sorcId = test.getString("sorcId");
                                        //final String blob = test.getString("blob");
                                        //final String blobMessageCounter = test.getString("blobMessageCounter");

                                    }
                                }
                                keyring = getMockedKeyring();
                                initializeBLESetUp();
                            }catch (Exception e)
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

    private void initializeBLESetUp()
    {

        if (tacsInterface != null)
        {
            tacsInterface.closeInterface();
        }
        tacsInterface = new TacsInterface.Builder(this, getActivity())
                .enableMockMode(useMockMode)
                .build();

        tacsInterface.logging().registerEventCallback(this);
        tacsInterface.logging().setLogLevel(LoggingInterface.LogLevel.DEBUG);

        if (tacsInterface.useAccessGrant(MY_MOCK_ACCESS_GRANT_ID, keyring))
        {
            //The key ring is loaded successfully
            Log.i(TAG, "Keyring loaded");

            if(mBluetoothAdapter.isEnabled())
            {
                btnBtDisConnect.setVisibility(View.GONE);
                btnBtConnect.setVisibility(View.VISIBLE);
                tacsInterface.searchAndConnect();
            }
        } else {
            //Invalid keyring
            Log.e(TAG, "Keyring invalid");
        }

        System.out.println("TacsInterface.ConnectionStatus:-" + TacsInterface.ConnectionStatus.UNAVAILABLE.name());
        System.out.println("KeyholderInterface.StatusCode:-" + KeyholderInterface.StatusCode.KEYHOLDER_NOT_DETECTED.name());
        System.out.println("No data");
    }

    private TacsKeyring getMockedKeyring()
    {
        Gson gson = new Gson();
        Type configType = new TypeToken<TacsKeyring>(){}.getType();
        try{
            System.out.println(tacsKeyring.toString());
            return gson.fromJson(tacsKeyring.toString(), configType);
        }catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage());
            return new TacsKeyring();
        }
    }

    void turnOnBluetoothDevice()
    {
        try {
            BluetoothAdapter BluetoothAdapter1 = BluetoothAdapter.getDefaultAdapter();
            if (!BluetoothAdapter1.isEnabled())
            {
                BluetoothAdapter1.enable();
                btnBtDisConnect.setVisibility(View.VISIBLE);
                btnBtConnect.setVisibility(View.GONE);
                tacsInterface.searchAndConnect();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    void turnOffBluetoothDevice()
    {
        BluetoothAdapter BluetoothAdapter2 = BluetoothAdapter.getDefaultAdapter();
        if (BluetoothAdapter2.isEnabled())
        {
            BluetoothAdapter2.disable();
            btnBtConnect.setVisibility(View.VISIBLE);
            btnBtDisConnect.setVisibility(View.GONE);
            tacsInterface.cancelConnection();
        }
    }
    @Override
    public void logEvent(@NonNull @NotNull LoggingInterface.LogEvent logEvent) {

    }

    @Override
    public void updateVehicleStatus(TacsInterface.VehicleStatus newStatus) {
        try {
            if (currentStatus != null && newStatus.getConnectionStatus().isConnected() && !currentStatus.getConnectionStatus().isConnected())
            {
                tacsInterface.vehicleAccess().requestCurrentVehicleStatus(); // Initial door and immobilizer status are highly useful to have
                tacsInterface.telematics().queryTelematicsData(DEFAULT_TELEMATICS_REQUEST); // Telematics may also be useful, depending on use case
            }
            currentStatus = newStatus;

            final Collection<TelematicsInterface.TelematicsData> td = newStatus.getTelematicsData();
            //   final String odometerStatus = formatTelematicsData(td, TelematicsInterface.TelematicsDataType.ODOMETER);

            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                        if (newStatus.getConnectionStatus().isError())
                        {
                            String message = newStatus.getErrorMessage();
                            message = message == null || message.isEmpty() ? "No details" : message;
                            CustomToast.showToast(getActivity(),newStatus.getConnectionStatus().name() + " : " + message, 0);
                            System.out.println(newStatus.getConnectionStatus().name() + " : " + message);
                        }
                        else {
                            switch (newStatus.getConnectionStatus())
                            {
                                case UNAVAILABLE:
                                    break;
                                case CONNECTED:
                                    CustomToast.showToast(getActivity(),newStatus.getConnectionStatus().name(), 0);
                                    System.out.println("4"+newStatus.getConnectionStatus().name());
                                    break;
                                case REMOVED:
                                    CustomToast.showToast(getActivity(),"DISCONNECTED", 0);
                                    System.out.println("5"+newStatus.getConnectionStatus().name());
                                    break;
                                default:
                                    break;
                            }
                            if(newStatus.getConnectionStatus().isConnected())
                            {
                                switch (newStatus.getDoorStatus())
                                {
                                    case DOORS_LOCKED:
                                        CustomToast.showToast(getActivity(), newStatus.getDoorStatus().name(), 0);
                                        System.out.println("6" + newStatus.getDoorStatus().name());
                                        break;
                                    case DOORS_UNLOCKED:
                                        CustomToast.showToast(getActivity(), newStatus.getDoorStatus().name(), 0);
                                        System.out.println("7" + newStatus.getDoorStatus().name());
                                        break;
                                    case DOORS_UNKNOWN:
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDeviceStatus(TacsInterface.BluetoothDeviceStatus deviceStatus, String errorMessage) {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    synchronized (mutex)
                    {
                        System.out.println("1"+deviceStatus.name());
                        switch (deviceStatus)
                        {
                            case DEVICE_AVAILABLE:
                                //
                                break;
                            case DEVICE_QUEUE_LIMIT_REACHED:
                                CustomToast.showToast(getActivity(), "Slow down, please!", 0);
                                //fallthrough
                                break;
                            case DEVICE_NOT_SUPPORTED:
                                CustomToast.showToast(getActivity(), "Device Not Supported!",0);
                                break;
                            case DEVICE_DEACTIVATED:
                                //
                                break;
                            case DEVICE_PERMISSION_ERROR:
                                // fallthrough
                                break;
                            case DEVICE_ERROR:
                                // fallthrough
                                break;
                            default:
                                //  System.out.println("3"+VehicleAccessInterface.DoorStatus.DOORS_UNKNOWN.name());
                                //  CustomToast.showToast(getActivity(), VehicleAccessInterface.DoorStatus.DOORS_UNKNOWN.name(), 0);
                                break;
                        }
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        try {
            tacsInterface.closeInterface();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        try {


            googleMap = gMap;
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setMapToolbarEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            googleMap.getUiSettings().setTiltGesturesEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setScrollGesturesEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(5));

//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(VehicleModelBundle.getDouble("latitude"), VehicleModelBundle.getDouble("longitude"))));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lattitude,longitude)));

            LatLng locationPin = new LatLng(lattitude, longitude);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(locationPin, 20);
            googleMap.animateCamera(cameraUpdate);

            googleMap.animateCamera(CameraUpdateFactory.zoomTo(5));
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(locationPin)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_marker)));
            marker.showInfoWindow();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getLocation(Context context, Double latitude, Double longitude){
    Geocoder geocoder;
    List<Address> addresses;
    geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }
    return  null;
    }

}
