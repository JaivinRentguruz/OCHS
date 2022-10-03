package com.ochs.app.flexiicar_app.selfcheckin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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
import com.ochs.app.flexiicar_app.selfcheckout.Fragment_Self_Check_out;
import com.ochs.app.flexiicar_app.user.User_Profile;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_CHECKIN;
import static com.ochs.app.apicall.ApiEndPoint.GETSELFCHECKIN;
import static com.ochs.app.apicall.ApiEndPoint.UPDATESELFCHECKIN;

public class Fragment_Self_Check_In extends Fragment
{
    ImageView BackArrow,VehImg;
    Handler handler = new Handler();
    public static Context context;
    TextView txtVehicleNumber,txtvehicleName,txtExtraFuelCharge,txtExtraMileageCharge,txtExtraDayRentalCharge,
            txt_OdoMeterIn;
    EditText txtoriginalCheckInDate,txtcheck_Out_Location_Name,txtactualReturnDate,txtcheck_in_Location_Name,
    txtOdometerOut,txttotalMilesAllowed,txtTotalMilesDone,
    txtGasTankCapacity,txtGasTankOut,txtGasTankIn, txtGasTankUsed,txtGasTankCharge,txtTotalGasCharge;

    Bundle AgreementsBundle;
    JSONArray ImageList = new JSONArray();

    ImageLoader imageLoader;
    String serverpath="";
    SeekBar customSeekBar;
    LinearLayout lblAccept;
    int originalCheckInLocationId,actualReturnLocationId;
    String originalCheckInDate="",actualReturnDate="";
    ProgressDialog progressDialog;

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
        return inflater.inflate(R.layout.fragment_self_checkin, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ((User_Profile) getActivity()).BottomnavInVisible();

        try {
            initImageLoader(getActivity());
            imageLoader = ImageLoader.getInstance();

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            serverpath = sp.getString("serverPath", "");

            AgreementsBundle = getArguments().getBundle("AgreementsBundle");
            ImageList = new JSONArray(AgreementsBundle.getString("ImageList"));

            lblAccept=view.findViewById(R.id.lblAccept);
            BackArrow = view.findViewById(R.id.backimg_selfcheckIn);
            VehImg = view.findViewById(R.id.VehImage);
            customSeekBar = view.findViewById(R.id.simpleSeekBar1);

            txtVehicleNumber = view.findViewById(R.id.txtVehicleNumber);
            txtVehicleNumber = view.findViewById(R.id.txtVehicleNumber);
            txtvehicleName = view.findViewById(R.id.txtvehicleName);
            txtExtraFuelCharge = view.findViewById(R.id.ExtraFuelCharge);
            txtExtraMileageCharge = view.findViewById(R.id.ExtraMileageCharge);
            txtExtraDayRentalCharge = view.findViewById(R.id.ExtraDayRentalCharge);
            txt_OdoMeterIn = view.findViewById(R.id.txt_OdoMeterIn);
            txtOdometerOut = view.findViewById(R.id.OdometerOut);
            txttotalMilesAllowed = view.findViewById(R.id.totalMilesAllowed);
            txtTotalMilesDone = view.findViewById(R.id.TotalMilesDone);

            txtoriginalCheckInDate = view.findViewById(R.id.originalCheckInDate);
            txtcheck_Out_Location_Name = view.findViewById(R.id.txtcheck_Out_Location_Name);
            txtactualReturnDate = view.findViewById(R.id.actualReturnDate);
            txtcheck_in_Location_Name = view.findViewById(R.id.txtcheck_in_Location_Name);
            txtGasTankCapacity = view.findViewById(R.id.GasTankCapacity);
            txtGasTankOut = view.findViewById(R.id.GasTankOut);
            txtGasTankIn = view.findViewById(R.id.GasTankIn);
            txtGasTankUsed = view.findViewById(R.id.GasTankUsed);
            txtGasTankCharge = view.findViewById(R.id.GasTankCharge);
            txtTotalGasCharge = view.findViewById(R.id.TotalGasCharge);

            AssetManager am = getActivity().getApplicationContext().getAssets();
            Typeface typefaceFSSiena = Typeface.createFromAsset(am,
                    String.format(Locale.US, "fonts/%s", "DS-DIGI.TTF"));
            txt_OdoMeterIn.setTypeface(typefaceFSSiena);
            txtOdometerOut.setTypeface(typefaceFSSiena);

            txtcheck_Out_Location_Name.setText(AgreementsBundle.getString("check_Out_Location_Name"));
            txtcheck_in_Location_Name.setText(AgreementsBundle.getString("check_in_Location_Name"));

            BackArrow.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Bundle SelfCheckInBundle=new Bundle();
                    SelfCheckInBundle.putBundle("AgreementsBundle",AgreementsBundle);
                    System.out.println(SelfCheckInBundle);
                    NavHostFragment.findNavController(Fragment_Self_Check_In.this)
                            .navigate(R.id.action_Self_check_In_to_Signature,SelfCheckInBundle);
                }
            });

            lblAccept.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
                        JSONObject bodyParam = new JSONObject();
                        try
                        {
                            for(int i=0;i<ImageList.length();i++)
                            {
                                try
                                {
                                    System.out.println(i);
                                    JSONObject imgObj = ImageList.getJSONObject(i);
                                    String imgPath = imgObj.getString("fileBase64");
                                    try {
                                        File imgFile = new File(imgPath);

                                        if(imgFile!=null)
                                        {
                                            Uri selectedImage = Uri.fromFile(imgFile);
                                            System.out.println(selectedImage);

                                            Bitmap bitmap = getScaledBitmap(selectedImage, 400, 400);
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                            byte[] image = stream.toByteArray();
                                            String img_str_base64 = Base64.encodeToString(image, Base64.NO_WRAP);
                                            imgObj.put("fileBase64", img_str_base64);
                                        }
                                    }catch (FileNotFoundException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }

                            bodyParam.accumulate("AgreementId",AgreementsBundle.getInt("agreement_ID"));
                            bodyParam.accumulate("VehicleId",AgreementsBundle.getInt("vehicle_ID"));
                            bodyParam.accumulate("odometerOut",Integer.parseInt(txtOdometerOut.getText().toString()));
                            bodyParam.accumulate("odometerIn",Integer.parseInt(txt_OdoMeterIn.getText().toString()));
                            bodyParam.accumulate("gasTank",Integer.parseInt(txtGasTankOut.getText().toString().substring(0,txtGasTankOut.getText().length()-1)));
                            bodyParam.accumulate("gasTankIn",Integer.parseInt(txtGasTankIn.getText().toString().substring(0,txtGasTankIn.getText().length()-1)));
                            bodyParam.accumulate("totalMilesAllowed",Double.parseDouble(txttotalMilesAllowed.getText().toString()));
                            bodyParam.accumulate("extraMilesCharge",Double.parseDouble(txtExtraMileageCharge.getText().toString()));
                            bodyParam.accumulate("totalGasCharge",Double.parseDouble(txtTotalGasCharge.getText().toString().substring(0,txtTotalGasCharge.getText().length()-3)));
                            bodyParam.accumulate("extraDayCharge",Double.parseDouble(txtExtraDayRentalCharge.getText().toString()));

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date date = dateFormat.parse(txtoriginalCheckInDate.getText().toString());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
                            originalCheckInDate = sdf.format(date);

                            bodyParam.accumulate("originalCheckInDate",originalCheckInDate);

                            SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
                            Date date1 = dateFormat1.parse(txtactualReturnDate.getText().toString());
                            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
                            actualReturnDate = sdf1.format(date1);

                            bodyParam.accumulate("ActualReturnDate",actualReturnDate);
                            bodyParam.accumulate("originalCheckInLocationId",originalCheckInLocationId);
                            bodyParam.accumulate("actualReturnLocationId",actualReturnLocationId);

                            bodyParam.accumulate("ImageList",ImageList);
                            System.out.println("bodyParam"+bodyParam);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Please Wait...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        ApiService ApiService = new ApiService(UpdatecheckIn, RequestType.POST,
                                UPDATESELFCHECKIN, BASE_URL_CHECKIN, new HashMap<String, String>(), bodyParam);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            String bodyParam = "";

            try
            {
                bodyParam += "AgreementId=" + AgreementsBundle.getInt("agreement_ID");
                System.out.println(bodyParam);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            AndroidNetworking.initialize(getActivity());
            Fragment_Self_Check_In.context = getActivity();

            ApiService ApiService = new ApiService(GetSelfCheckIN, RequestType.GET,
                    GETSELFCHECKIN, BASE_URL_CHECKIN, new HashMap<String, String>(), bodyParam);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    OnResponseListener GetSelfCheckIN = new OnResponseListener()
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
                            try
                            {
                                //selfCheckOutObject
                                JSONObject resultSet = responseJSON.getJSONObject("resultSet");
                                JSONObject selfCheckIn=resultSet.getJSONObject("selfCheckInModel");

                                final String odometerOut = selfCheckIn.getString("odometerOut");
                                final String odometerIn = selfCheckIn.getString("odometerIn");
                                final int gasTank = selfCheckIn.getInt("gasTank");
                                final int gasTankIn = selfCheckIn.getInt("gasTankIn");
                                final int vehicleId = selfCheckIn.getInt("vehicleId");
                                final int agreementId = selfCheckIn.getInt("agreementId");
                                final String originalCheckInDate = selfCheckIn.getString("originalCheckInDate");
                                originalCheckInLocationId = selfCheckIn.getInt("originalCheckInLocationId");
                                actualReturnLocationId = selfCheckIn.getInt("actualReturnLocationId");

                                final int isUnlimitedMiles = selfCheckIn.getInt("isUnlimitedMiles");
                                final int isUnlimitedGas = selfCheckIn.getInt("isUnlimitedGas");
                                final double totalMilesAllowed = selfCheckIn.getDouble("totalMilesAllowed");
                                final double extraMilesCharge = selfCheckIn.getDouble("extraMilesCharge");
                                final double totalGasCharge = selfCheckIn.getDouble("totalGasCharge");
                                final double extraDayCharge = selfCheckIn.getDouble("extraDayCharge");
                                final double totalExtraDays = selfCheckIn.getDouble("totalExtraDays");
                                final double gasChargePerLtr = selfCheckIn.getDouble("gasChargePerLtr");
                                String vehicleName = selfCheckIn.getString("vehicleName");
                                final String vehicleNumber = selfCheckIn.getString("vehicleNumber");
                                final String vehicleImagePath = selfCheckIn.getString("vehicleImagePath");
                                final String tankCapacity = selfCheckIn.getString("tankCapacity");
                                final double extraChargePerMile = selfCheckIn.getDouble("extraChargePerMile");
                                final double extraChargePerDay = selfCheckIn.getDouble("extraChargePerDay");

                                String s = String.valueOf(gasTank);
                                txtGasTankOut.setText(s+"%");


                                if(selfCheckIn.has("actualReturnDate"))
                                {
                                    final String actualReturnDate = selfCheckIn.getString("actualReturnDate");


                                    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                                    Date date1 = dateFormat1.parse(actualReturnDate);
                                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
                                    String oactualReturnDateStr = sdf1.format(date1);

                                    txtactualReturnDate.setText(oactualReturnDateStr);

                                }
                                customSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                                {
                                    public void onProgressChanged (SeekBar seekBar,int progress, boolean fromUser)
                                    {
                                        try
                                        {
                                            txtGasTankOut.setText(String.valueOf(progress+"%"));
                                        }
                                        catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }

                                    public void onStartTrackingTouch (SeekBar seekBar)
                                    {
                                    }

                                    public void onStopTrackingTouch (SeekBar seekBar)
                                    {
                                    }
                                });

                                String url1 = serverpath + vehicleImagePath.substring(2);
                                imageLoader.displayImage(url1, VehImg);

                                txtVehicleNumber.setText(vehicleNumber);

                                vehicleName = vehicleName.substring(10);
                                txtvehicleName.setText(vehicleName);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                                Date date = dateFormat.parse(originalCheckInDate);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
                                String originalCheckInDateStr = sdf.format(date);

                                txtoriginalCheckInDate.setText(originalCheckInDateStr);

                                txt_OdoMeterIn.setText(odometerIn);
                                txtOdometerOut.setText(odometerOut);

                                txttotalMilesAllowed.setText(((String.format(Locale.US,"%.2f",totalMilesAllowed))));

                                txtGasTankCapacity.setText(tankCapacity+" Liters");
                                String GasTankOutStr =String.valueOf(gasTank);
                                String GasTankInSrr = String.valueOf(gasTankIn);

                                txtGasTankOut.setText(GasTankOutStr+"%");
                                txtGasTankIn.setText(GasTankInSrr+"%");

                                String gasChargePerLtrStr=(((String.format(Locale.US,"%.2f",gasChargePerLtr))));
                                txtGasTankCharge.setText(gasChargePerLtrStr+" US$");

                                String totalGasChargeStr=(((String.format(Locale.US,"%.2f",totalGasCharge))));
                                txtTotalGasCharge.setText(totalGasChargeStr+" US$");

                                txtExtraDayRentalCharge.setText(((String.format(Locale.US,"%.2f",extraChargePerDay))));
                                txtExtraMileageCharge.setText(((String.format(Locale.US,"%.2f",extraChargePerMile))));

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

    //UpdateCheckIN
    OnResponseListener UpdatecheckIn = new OnResponseListener()
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
                        System.out.println(response);
                        progressDialog.dismiss();

                        JSONObject responseJSON = new JSONObject(response);
                        Boolean status = responseJSON.getBoolean("status");

                        if (status)
                        {
                            Bundle SelfCheckInBundle=new Bundle();
                            AgreementsBundle.putString("originalCheckInDate",originalCheckInDate);
                            AgreementsBundle.putString("ActualReturnDate",actualReturnDate);
                            AgreementsBundle.putInt("originalCheckInLocationId",originalCheckInLocationId);
                            AgreementsBundle.putInt("actualReturnLocationId",actualReturnLocationId);
                            SelfCheckInBundle.putBundle("AgreementsBundle",AgreementsBundle);
                            SelfCheckInBundle.putString("ImageList",ImageList.toString());
                            System.out.println(SelfCheckInBundle);

                            NavHostFragment.findNavController(Fragment_Self_Check_In.this)
                                    .navigate(R.id.action_Self_check_In_to_SummaryOfChargeForSelfCheckIn,SelfCheckInBundle);

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

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested one
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
