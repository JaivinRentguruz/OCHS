package com.ochs.app.flexiicar_app.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_BOOKING;
import static com.ochs.app.apicall.ApiEndPoint.BOOKING;

public class Fragment_Select_addition_Options_For_User extends Fragment
{
    LinearLayout lblconfirm,LayoutdeliveryPickupservice;
    ImageView backarrow;
    Bundle ReservationBundle;
    ToggleButton SwitchVehicleDelivery,SwitchVehicleDeliver2;
    TextView lbl_PickupLoc,lbl_PickupDateTime,lbl_returnLoc,lbl_ReturnDateTime,txt_vehicletype,txt_vehName,txtDays,txt_rate,txtTotalAmount,txt_Discard,txtMileage;
    Handler handler = new Handler();
    public static Context context;
    JSONArray EquipmentList = new JSONArray();
    JSONArray MiscList = new JSONArray();
    JSONArray SummaryOfCharges = new JSONArray();
    int cmP_DISTANCE;
    String DeliveryAndPickupModel = "";
    String CheckInStr,CheckOutStr;

    Double DeliveryChargeAmount=0.0, PickupChargeAmount=0.0;
    int DeliveryChargeLocID=0, PickupChargeLocID=0;

    ImageLoader imageLoader;
    String serverpath="",VehImage="";
    Double TotalMilesAllowed;
    JSONArray getInsuranceDEtails= new JSONArray();
    JSONArray SelectedMiscList = new JSONArray();
    JSONArray SelectedEquipmentList = new JSONArray();
    Boolean IsSelected;
    double TotalValue;

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
        return inflater.inflate(R.layout.fragment_select_additional_option, container, false);
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        try {
            super.onViewCreated(view, savedInstanceState);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            ((User_Profile) getActivity()).BottomnavInVisible();

            initImageLoader(getActivity());
            imageLoader = ImageLoader.getInstance();

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            serverpath = sp.getString("serverPath", "");
            cmP_DISTANCE= sp.getInt("cmP_DISTANCE", 0);

            lblconfirm = view.findViewById(R.id.lbl_confirm_2);
            backarrow = view.findViewById(R.id.backbtn1);
            LayoutdeliveryPickupservice = view.findViewById(R.id.layout_deliverypickupservice);
            txt_vehicletype = view.findViewById(R.id.txt_vehicleTypeName);
            txt_vehName = view.findViewById(R.id.txt_VehicleMOdelName);
            txtTotalAmount = view.findViewById(R.id.txt_TotalAmount);
            lbl_PickupLoc = view.findViewById(R.id.lbl_PickupLoc);
            lbl_PickupDateTime= view.findViewById(R.id.lbl_PickupDate);
            lbl_returnLoc = view.findViewById(R.id.lbl_returnLoc);
            lbl_ReturnDateTime = view.findViewById(R.id.lbl_ReturnDate);
            txtDays = view.findViewById(R.id.Txt_Days);
            txt_rate = view.findViewById(R.id.txt_rate1);
            txt_Discard= view.findViewById(R.id.txt_DiscardSAO);
            SwitchVehicleDelivery =view.findViewById(R.id.switch_VehicleDelivery);
            SwitchVehicleDeliver2 = view.findViewById(R.id.switch_VehicleDelivery2);
            txtMileage = view.findViewById(R.id.txtMileage);
            txtTotalAmount.setText("0");

            ReservationBundle = getArguments().getBundle("ReservationBundle");
            IsSelected = getArguments().getBoolean("IsSelected");

            DeliveryAndPickupModel = getArguments().getString("DeliveryAndPickupModel");

            ImageView Vehicle_Img=view.findViewById(R.id.Veh_image_bg1);

            String VehicleImgstr=ReservationBundle.getString("veh_Img_Path");
            String url1 = serverpath+VehicleImgstr.substring(2);
            imageLoader.displayImage(url1,Vehicle_Img);

            if (!DeliveryAndPickupModel.equals(""))
            {

                try {
                    JSONArray DPArray = new JSONArray(DeliveryAndPickupModel);

                    if (DPArray.length() > 0)
                    {
                        TextView deliveryCharge = view.findViewById(R.id.deliveryCharge);
                        deliveryCharge.setText(DPArray.getJSONObject(0).getDouble("chargeAmount") + "");

                        DeliveryChargeLocID = DPArray.getJSONObject(0).getInt("businessLocID");
                        DeliveryChargeAmount = DPArray.getJSONObject(0).getDouble("chargeAmount");

                        SwitchVehicleDelivery.setChecked(true);
                    }

                    if (DPArray.length() > 1)
                    {
                        TextView pickupCharge = view.findViewById(R.id.pickupCharge);

                        pickupCharge.setText(DPArray.getJSONObject(1).getDouble("chargeAmount") + "");

                        PickupChargeLocID = DPArray.getJSONObject(1).getInt("businessLocID");
                        PickupChargeAmount = DPArray.getJSONObject(1).getDouble("chargeAmount");

                        SwitchVehicleDeliver2.setChecked(true);
                    }

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            txt_Discard.setOnClickListener(new View.OnClickListener()
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
                                    NavHostFragment.findNavController(Fragment_Select_addition_Options_For_User.this)
                                            .navigate(R.id.action_Select_addtional_options_to_User_Details);
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

            lblconfirm.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ReservationBundle.putInt("BookingStep", 4);
                    ReservationBundle.putString("EquipmentList",EquipmentList.toString());
                    ReservationBundle.putString("MiscList",MiscList.toString());
                    ReservationBundle.putString("DeliveryAndPickupModel",DeliveryAndPickupModel);
                    ReservationBundle.putInt("DeliveryChargeLocID",DeliveryChargeLocID);
                    ReservationBundle.putDouble("DeliveryChargeAmount",DeliveryChargeAmount);
                    ReservationBundle.putInt("pickupChargeLocID",PickupChargeLocID);
                    ReservationBundle.putDouble("pickupChargeAmount",PickupChargeAmount);
                    ReservationBundle.putDouble("totalMilesAllowed",TotalMilesAllowed);
                    ReservationBundle.putString("rate_ID",txt_rate.getText().toString());
                    ReservationBundle.putString("summaryOfCharges",SummaryOfCharges.toString());
                    Bundle Reservation = new Bundle();
                    Reservation.putBundle("ReservationBundle", ReservationBundle);
                    NavHostFragment.findNavController(Fragment_Select_addition_Options_For_User.this)
                            .navigate(R.id.action_Select_addtional_options_to_Finalize_your_rental, Reservation);
                }
            });

            backarrow.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle Reservation = new Bundle();
                    Reservation.putBundle("ReservationBundle", ReservationBundle);
                    NavHostFragment.findNavController(Fragment_Select_addition_Options_For_User.this)
                            .navigate(R.id.action_Select_addtional_options_to_SummaryOfCharges, Reservation);
                }
            });

            ReservationBundle = getArguments().getBundle("ReservationBundle");

            //Pickup Location time date
            lbl_PickupLoc.setText(ReservationBundle.getString("chk_Out_Location_Name"));

            String default_Check_Out=(ReservationBundle.getString("check_Out"));
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
            Date date1 = dateFormat1.parse(default_Check_Out);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm aa", Locale.US);
            CheckOutStr = sdf1.format(date1);
            lbl_PickupDateTime.setText(CheckOutStr);

            //Return Location time date
            lbl_returnLoc.setText(ReservationBundle.getString("chk_In_Loc_Name"));

            String default_Check_In=(ReservationBundle.getString("check_IN"));
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
            Date date2 = dateFormat2.parse(default_Check_In);
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm aa", Locale.US);
            CheckInStr = sdf2.format(date2);
            lbl_ReturnDateTime.setText(CheckInStr);

            txt_vehName.setText(ReservationBundle.getString("vehicle_Name"));
            txt_vehicletype.setText(ReservationBundle.getString("vehicle_Type_Name"));

         //   double Total=(ReservationBundle.getDouble("chargeAmount"));
          //  txtTotalAmount.setText(((String.format(Locale.US,"%.2f",Total))));

            txtDays.setText(ReservationBundle.getString("totaL_DAYS"));

            SwitchVehicleDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
                {
                    if (SwitchVehicleDelivery.isChecked())
                    {
                        ReservationBundle.putString("EquipmentList", EquipmentList.toString());
                        ReservationBundle.putString("MiscList", MiscList.toString());
                        Bundle Booking = new Bundle();
                        Booking.putBundle("ReservationBundle", ReservationBundle);
                        Booking.putBoolean("fromMap", false);
                        Booking.putString("DeliveryAndPickupModel", "");
                        NavHostFragment.findNavController(Fragment_Select_addition_Options_For_User.this)
                                .navigate(R.id.action_Select_addtional_options_to_FilterByVehicleClass, Booking);
                    } else {
                        DeliveryChargeAmount = 0.0;
                        DeliveryChargeLocID = 0;
                    }
                }
            });

            SwitchVehicleDeliver2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
                {
                    if (SwitchVehicleDeliver2.isChecked())
                    {
                        //SwitchVehicleDeliver2.setEnabled(false);
                    } else {
                        PickupChargeLocID = 0;
                        PickupChargeAmount = 0.0;
                    }
                }
            });

            String Check_Out3=(ReservationBundle.getString("check_Out"));
            SimpleDateFormat dateFormat3 = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
            Date date3 = dateFormat3.parse(Check_Out3);
            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String CheckOutDate = sdf3.format(date3);
            SimpleDateFormat sdff = new SimpleDateFormat("HH:mm", Locale.US);
            String CheckOutTime = sdff.format(date3);

            String Check_In3=(ReservationBundle.getString("check_IN"));
            SimpleDateFormat dateFormat4 = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
            Date date4= dateFormat4.parse(Check_In3);
            SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String CheckInDate = sdf4.format(date4);
            SimpleDateFormat sdfff = new SimpleDateFormat("HH:mm", Locale.US);
            String CheckInTime = sdfff.format(date4);

            JSONObject bodyParam = new JSONObject();
            try {
                bodyParam.accumulate("ForTransId", ReservationBundle.getInt("reservation_ID"));
                bodyParam.accumulate("CustomerId", ReservationBundle.getInt("customer_ID"));
                bodyParam.accumulate("VehicleTypeId", ReservationBundle.getInt("vehicle_Type_ID"));
                bodyParam.accumulate("VehicleID", ReservationBundle.getInt("vehicle_ID"));
                bodyParam.accumulate("BookingStep", ReservationBundle.getInt("BookingStep"));

                bodyParam.accumulate("PickupLocId", ReservationBundle.getInt("check_IN_Location"));
                bodyParam.accumulate("ReturnLocId", ReservationBundle.getInt("check_Out_Location"));

                bodyParam.accumulate("PickupDate",CheckOutDate);
                bodyParam.accumulate("ReturnDate",CheckInDate);
                bodyParam.accumulate("PickupTime",CheckOutTime );
                bodyParam.accumulate("ReturnTime",CheckInTime);
                System.out.println(bodyParam);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            AndroidNetworking.initialize(getActivity());
            Fragment_Select_addition_Options_For_User.context = getActivity();

            ApiService ApiService = new ApiService(getTaxtDetails, RequestType.POST,
                    BOOKING, BASE_URL_BOOKING, new HashMap<String, String>(), bodyParam);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    OnResponseListener getTaxtDetails = new OnResponseListener()
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

                                //Vehicle Model
                                JSONObject vehicleModel =resultSet.getJSONObject("vehicleModel");
                                final double hourlyMilesAllowed = vehicleModel.getDouble("hourlyMilesAllowed");
                                final double halfDayMilesAllowed = vehicleModel.getDouble("halfDayMilesAllowed");
                                final double dailyMilesAllowed = vehicleModel.getDouble("dailyMilesAllowed");
                                final double weeklyMilesAllowed = vehicleModel.getDouble("weeklyMilesAllowed");
                                final double monthlyMilesAllowed = vehicleModel.getDouble("monthlyMilesAllowed");
                                final double totalMilesAllowed = vehicleModel.getDouble("totalMilesAllowed");
                                final int lockKey = vehicleModel.getInt("lockKey");

                                TotalMilesAllowed=totalMilesAllowed;

                                if(vehicleModel.has("rate_ID"))
                                {
                                    final String rate_ID = vehicleModel.getString("rate_ID");
                                    txt_rate.setText(rate_ID);
                                }

                                if(cmP_DISTANCE==1)
                                {
                                    String Miles=((String.format(Locale.US,"%.0f",totalMilesAllowed)));
                                    txtMileage.setText(Miles+" MILES");
                                }
                                else {
                                    String Miles=(String.valueOf(totalMilesAllowed));
                                    txtMileage.setText(Miles+"kms");
                                }

                                //getsummaryOfCharges
                                final JSONArray getsummaryOfCharges = resultSet.getJSONArray("summaryOfCharges");
                                int len = getsummaryOfCharges.length();

                                for (int j = 0; j < len; j++)
                                {
                                    final JSONObject test = (JSONObject) getsummaryOfCharges.get(j);

                                    final  int sortId = test.getInt("sortId");
                                    final  double chargeAmount=test.getDouble("chargeAmount");
                                    String chargeCode = "";

                                    if(test.has("chargeCode"))
                                    {
                                        chargeCode = test.getString("chargeCode");
                                    }

                                    final  String chargeName = test.getString("chargeName");

                                    JSONObject summaryOfChargesObj = new JSONObject();
                                    summaryOfChargesObj.put("sortId",sortId);
                                    summaryOfChargesObj.put("chargeCode",chargeCode);
                                    summaryOfChargesObj.put("chargeName",chargeName);
                                    summaryOfChargesObj.put("chargeAmount",chargeAmount);

                                    SummaryOfCharges.put(summaryOfChargesObj);

                                    if(chargeName.equals("Estimated Total"))
                                    {
                                        txtTotalAmount.setText((String.format(Locale.US,"%.2f",chargeAmount)));
                                        System.out.println(txtTotalAmount.getText().toString());
                                    }
                                }

                                //miscellaneousModel
                                final JSONArray getmiscellaneousModel = resultSet.getJSONArray("miscellaneousModel");
                                final RelativeLayout rlmiscellaneousModel = getActivity().findViewById(R.id.rl_miscellaneousModel);

                                int len2;
                                len2 = getmiscellaneousModel.length();

                                for (int j = 0; j < len2; j++)
                                {
                                    final JSONObject test = (JSONObject) getmiscellaneousModel.get(j);
                                    final  int miscId = test.getInt("miscId");
                                    final String miscName = test.getString("miscName");
                                    final String miscDesc=test.getString("miscDesc");
                                    final double basicValue=test.getDouble("basicValue");
                                    final int quantity=test.getInt("quantity");
                                    final double miscAmount=test.getDouble("miscAmount");
                                    final int taxableAmount=test.getInt("taxableAmount");
                                    int isOptional=test.getInt("isOptional");

                                    final JSONObject miscObj = new JSONObject();
                                    miscObj.put("miscId",miscId);
                                    miscObj.put("miscName",miscName);
                                    miscObj.put("miscDesc",miscDesc);
                                    miscObj.put("basicValue",basicValue);
                                    miscObj.put("quantity",quantity);
                                    miscObj.put("miscAmount",miscAmount);
                                    miscObj.put("taxableAmount",taxableAmount);
                                    miscObj.put("isOptional",isOptional);

                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                    lp.setMargins(0, 0, 0, 0);

                                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.miscellaneous_tax_details, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                    linearLayout.setId(200 + j);
                                    linearLayout.setLayoutParams(lp);

                                    final TextView txt_miscName,txt_miscDesc,txt_miscAmount;
                                    final ToggleButton s2=linearLayout.findViewById(R.id.switch1);

                                    txt_miscName= (TextView)linearLayout.findViewById(R.id.lbl_miscName);
                                    txt_miscDesc=(TextView)linearLayout.findViewById(R.id.lbl_miscDesc);
                                    txt_miscAmount=(TextView)linearLayout.findViewById(R.id.lbl_miscAmount);
                                    txt_miscName.setText(miscName);
                                    txt_miscDesc.setText(miscDesc);

                                    String strmiscAmount=((String.format(Locale.US,"%.2f",miscAmount)));
                                    txt_miscAmount.setText("USD$ "+strmiscAmount);

                                    if (isOptional == 1)
                                    {
                                        s2.setChecked(true);
                                        s2.setEnabled(false);
                                        s2.setBackgroundResource(R.drawable.toggle_selector_red);

                                        miscObj.put("miscId", miscId);
                                        miscObj.put("miscAmount", miscAmount);
                                        MiscList.put(miscObj);
                                    } else {
                                        s2.setChecked(false);
                                    }

                                    if(IsSelected)
                                    {
                                        SelectedMiscList = new JSONArray(ReservationBundle.getString("MiscList"));
                                        System.out.println("SelectedMiscList"+SelectedMiscList);
                                        int count = SelectedMiscList.length();

                                        for (int i = 0; i < count; i++)
                                        {
                                            JSONObject obj = SelectedMiscList.getJSONObject(i);

                                            if (obj.getInt("miscId")==miscId)
                                            {
                                                if (isOptional == 1)
                                                {
                                                    s2.setChecked(true);
                                                    s2.setEnabled(false);
                                                    s2.setBackgroundResource(R.drawable.toggle_selector_red);
                                                }
                                                else {
                                                    s2.setChecked(true);
                                                    s2.setBackgroundResource(R.drawable.toggle_selector_green);

                                                    String totalAmount = txtTotalAmount.getText().toString();
                                                    TotalValue =Double.parseDouble(totalAmount)+(miscAmount);
                                                    txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));
                                                }
                                            }
                                        }
                                    }

                                    s2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                                    {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
                                        {
                                            try {
                                                if (isChecked)
                                                {
                                                    if (isOptional == 1)
                                                    {
                                                        s2.setChecked(true);
                                                        s2.setEnabled(false);
                                                    }
                                                    else {
                                                        String totalAmount = txtTotalAmount.getText().toString();
                                                        TotalValue =Double.parseDouble(totalAmount)+(miscAmount);
                                                        txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));

                                                        for (int i=0;i<MiscList.length();i++)
                                                        {
                                                            JSONObject temp = (JSONObject) MiscList.get(i);
                                                            if(temp.get("miscId").equals(miscId))
                                                            {
                                                                MiscList.remove(i);
                                                                break;
                                                            }
                                                        }
                                                        miscObj.put("miscId",miscId);
                                                        miscObj.put("miscAmount",miscAmount);
                                                        MiscList.put(miscObj);
                                                        System.out.println(MiscList);
                                                    }
                                                }
                                                else
                                                {
                                                    if (isOptional == 1)
                                                    {
                                                        s2.setChecked(true);
                                                        s2.setEnabled(false);
                                                    }
                                                    else {
                                                        String totalAmount = txtTotalAmount.getText().toString();
                                                        TotalValue = (Double.parseDouble(totalAmount) - miscAmount) <= 0 ? 0 : Double.parseDouble(totalAmount) - miscAmount;//Double.parseDouble(totalAmount)-Double.parseDouble(TotalequipmentAmount);
                                                        txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));

                                                        for (int i = 0; i < MiscList.length(); i++)
                                                        {
                                                            JSONObject temp = (JSONObject) MiscList.get(i);
                                                            if (temp.get("miscId").equals(miscId))
                                                            {
                                                                MiscList.remove(i);
                                                                break;
                                                            }
                                                        }
                                                        System.out.println(MiscList);
                                                    }
                                                }
                                            }
                                            catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    rlmiscellaneousModel.addView(linearLayout);
                                }

                                //equipmentModel
                                final JSONArray getEquipmentdetails = resultSet.getJSONArray("equipmentModel");
                                final RelativeLayout rlEquipmentDetails = getActivity().findViewById(R.id.rl_EquipmentExtra);

                                int len1;
                                len1 = getEquipmentdetails.length();

                                for (int j = 0; j < len1; j++)
                                {
                                    final JSONObject test = (JSONObject) getEquipmentdetails.get(j);
                                    final  int equipmentTypeId = test.getInt("equipmentTypeId");
                                    final String equipmentImagePath = test.getString("equipmentImagePath");
                                    final String equipmentName=test.getString("equipmentName");
                                    final String equipmentDesc=test.getString("equipmentDesc");
                                    final int equipmentQty=test.getInt("equipmentQty");
                                    final int taxValue=test.getInt("taxValue");
                                    final int taxAmount=test.getInt("taxAmount");
                                    final double equipmentAmount=test.getDouble("equipmentAmount");

                                    final JSONObject equipmentObj = new JSONObject();
                                    equipmentObj.put("equipmentTypeId", equipmentTypeId);
                                    equipmentObj.put("equipmentImagePath", equipmentImagePath);
                                    equipmentObj.put("equipmentName", equipmentName);
                                    equipmentObj.put("equipmentDesc", equipmentDesc);
                                    equipmentObj.put("equipmentQty", equipmentQty);
                                    equipmentObj.put("taxValue", taxValue);
                                    equipmentObj.put("taxAmount", taxAmount);
                                    equipmentObj.put("equipmentAmount", equipmentAmount+"");

                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                    lp.setMargins(0, 0, 0, 0);

                                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    LinearLayout linearLayout2 = (LinearLayout) inflater.inflate(R.layout.tax_details_list, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                    linearLayout2.setId(200 + j);
                                    linearLayout2.setLayoutParams(lp);

                                    final TextView txt_equipmentName,txt_equipmentDescc,txt_equipmentAmount;

                                    txt_equipmentName= (TextView) linearLayout2.findViewById(R.id.lbl_InsuranceName);
                                    txt_equipmentDescc=(TextView)linearLayout2.findViewById(R.id.lbl_insuranceDesc);
                                    txt_equipmentAmount=(TextView)linearLayout2.findViewById(R.id.lbl_totalCharge);
                                    txt_equipmentName.setText(equipmentName);
                                    txt_equipmentDescc.setText(equipmentDesc);

                                    String strequipmentAmount=((String.format(Locale.US,"%.2f",equipmentAmount)));
                                    txt_equipmentAmount.setText("USD$ "+strequipmentAmount);

                                    final ToggleButton s1=linearLayout2.findViewById(R.id.switch2);

                                    if(IsSelected)
                                    {
                                        SelectedEquipmentList = new JSONArray(ReservationBundle.getString("EquipmentList"));
                                        System.out.println("SelectedEquipmentList"+SelectedEquipmentList);

                                        int count = SelectedEquipmentList.length();

                                        for (int i = 0; i < count; i++)
                                        {
                                            JSONObject obj = SelectedEquipmentList.getJSONObject(i);

                                            if (obj.getInt("equipmentTypeId")==equipmentTypeId)
                                            {
                                                s1.setChecked(true);

                                                String totalAmount= txtTotalAmount.getText().toString();
                                                TotalValue=Double.parseDouble(totalAmount)+equipmentAmount;
                                                System.out.println(TotalValue);
                                                txtTotalAmount.setText((String.format(Locale.US,"%.2f",TotalValue)));

                                                equipmentObj.put("equipmentQty", 1);
                                                EquipmentList.put(equipmentObj);
                                                System.out.println(EquipmentList);
                                            }
                                        }
                                    }

                                    s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                                    {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
                                        {
                                            try {
                                                if (isChecked)
                                                {
                                                    String totalAmount= txtTotalAmount.getText().toString();
                                                    TotalValue=Double.parseDouble(totalAmount)+equipmentAmount;
                                                    txtTotalAmount.setText((String.format(Locale.US,"%.2f",TotalValue)));

                                                    equipmentObj.put("equipmentQty", 1);
                                                    EquipmentList.put(equipmentObj);
                                                    System.out.println(EquipmentList);
                                                }
                                                else
                                                {
                                                    //  equipmentObj.put("equipmentQty", 0);
                                                    int count=EquipmentList.length();
                                                    for (int i=0;i<count;i++)
                                                    {
                                                        JSONObject obj=EquipmentList.getJSONObject(i);

                                                        if(obj.getInt("equipmentTypeId")==equipmentTypeId)
                                                        {
                                                            String totalAmount= txtTotalAmount.getText().toString();
                                                            TotalValue=(Double.parseDouble(totalAmount)-equipmentAmount)<=0?0:Double.parseDouble(totalAmount)-equipmentAmount;//Double.parseDouble(totalAmount)-equipmentAmount;
                                                            txtTotalAmount.setText((String.format(Locale.US,"%.2f",TotalValue)));

                                                            EquipmentList.remove(i);
                                                            i--;
                                                            count--;
                                                            System.out.println(EquipmentList);
                                                        }
                                                    }
                                                }
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    rlEquipmentDetails.addView(linearLayout2);
                                }

                                //insuranceModel
                                getInsuranceDEtails = resultSet.getJSONArray("insuranceModel");
                                final RelativeLayout rlInsuranceDetails = getActivity().findViewById(R.id.rlInsuranceCover);

                                int len4;
                                len4 = getInsuranceDEtails.length();
                                for (int j = 0; j < len4; j++)
                                {
                                    final JSONObject test = (JSONObject) getInsuranceDEtails.get(j);

                                    final  int transId = test.getInt("transId");
                                    final  int deductableId = test.getInt("deductableId");
                                    final String insuranceName = test.getString("insuranceName");
                                    final String insuranceDesc=test.getString("insuranceDesc");
                                    final int isSelected=test.getInt("isSelected");
                                    final double excessCharge=test.getDouble("excessCharge");
                                    final double perDayCharge=test.getDouble("perDayCharge");
                                    final double totalCharge=test.getDouble("totalCharge");

                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                    lp.setMargins(0, 10, 0, 0);

                                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    final LinearLayout l1 = (LinearLayout) inflater.inflate(R.layout.tax_details_list, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                    l1.setId(200 + j);
                                    l1.setLayoutParams(lp);

                                    final TextView txt_insuranceName,txt_insuranceDesc,txt_totalCharge;
                                    txt_insuranceName= (TextView)l1.findViewById(R.id.lbl_InsuranceName);
                                    txt_insuranceDesc=(TextView)l1.findViewById(R.id.lbl_insuranceDesc);
                                    txt_totalCharge=(TextView)l1.findViewById(R.id.lbl_totalCharge);

                                    txt_insuranceName.setText(insuranceName);
                                    txt_insuranceDesc.setText(insuranceDesc);
                                    txt_totalCharge.setText(((String.format(Locale.US,"%.2f",totalCharge))));

                                    final ToggleButton s3=l1.findViewById(R.id.switch2);

                                    if(isSelected == 1)
                                    {
                                        s3.setChecked(true);

                                        String totalAmount = txtTotalAmount.getText().toString();
                                        TotalValue = Double.parseDouble(totalAmount)+totalCharge;
                                        txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));

                                        ReservationBundle.putInt("DeductibleCoverId", deductableId);
                                        ReservationBundle.putDouble("DeductibleCharge", totalCharge);
                                    }
                                    else {
                                        s3.setChecked(false);
                                    }

                                    if(IsSelected)
                                    {
                                        if(isSelected == 1)
                                        {
                                            s3.setChecked(false);
                                            String totalAmount = txtTotalAmount.getText().toString();
                                            TotalValue= (Double.parseDouble(totalAmount)-totalCharge)<=0?0:Double.parseDouble(totalAmount)-totalCharge;//Double.parseDouble(totalAmount)-totalCharge;
                                            txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));
                                        }

                                        int DeductibleCoverId= getArguments().getInt("DeductibleCover_Id");
                                        double DeductibleCharge= getArguments().getDouble("Deductible_Charge");

                                        if(DeductibleCoverId==deductableId)
                                        {                                            s3.setChecked(true);
                                            String totalAmount = txtTotalAmount.getText().toString();
                                            TotalValue= Double.parseDouble(totalAmount) + DeductibleCharge;
                                            txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));

                                            ReservationBundle.putInt("DeductibleCoverId", deductableId);
                                            ReservationBundle.putDouble("DeductibleCharge", totalCharge);
                                        }
                                    }

                                    s3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                                    {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
                                        {
                                            try {
                                                if(isChecked)
                                                {
                                                    System.out.println(transId+"-"+isChecked);

                                                    String totalAmount = txtTotalAmount.getText().toString();
                                                    TotalValue= Double.parseDouble(totalAmount) + totalCharge;
                                                    txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));

                                                    ReservationBundle.putInt("DeductibleCoverId", deductableId);
                                                    ReservationBundle.putDouble("DeductibleCharge", totalCharge);

                                                    int insuranceCount = getInsuranceDEtails.length();

                                                    for(int j=0;j<insuranceCount;j++)
                                                    {
                                                        int transIdTemp = ((JSONObject)getInsuranceDEtails.get(j)).getInt("transId");

                                                        if(transId!=transIdTemp)
                                                        {
                                                            LinearLayout llTemp = (LinearLayout) rlInsuranceDetails.getChildAt(j);
                                                            ToggleButton tbTemp = llTemp.findViewById(R.id.switch2);

                                                            if(tbTemp.isChecked())
                                                            {
                                                                tbTemp.setChecked(false);
                                                            }
                                                        }
                                                    }
                                                }
                                                else {
                                                    String totalAmount = txtTotalAmount.getText().toString();
                                                    TotalValue= (Double.parseDouble(totalAmount)-totalCharge)<=0?0:Double.parseDouble(totalAmount)-totalCharge;//Double.parseDouble(totalAmount)-totalCharge;
                                                    txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));
                                                }
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    rlInsuranceDetails.addView(l1);
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
        public void onError(String error)
        {
            System.out.println("Error-" + error);
        }
    };

}

/*

                                int len;
                                len = getInsuranceDEtails.length();

                                for (int j = 0; j < len; j++)
                                {

                                    final JSONObject test = (JSONObject) getInsuranceDEtails.get(j);

                                    final  int transId = test.getInt("transId");
                                    final  int deductableId = test.getInt("deductableId");
                                    final  String insuranceName = test.getString("insuranceName");
                                    final String insuranceDesc=test.getString("insuranceDesc");
                                    final int isSelected=test.getInt("isSelected");
                                    final double excessCharge=test.getDouble("excessCharge");
                                    final double perDayCharge=test.getDouble("perDayCharge");
                                    final double totalCharge=test.getDouble("totalCharge");

                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                    lp.setMargins(0, 10, 0, 0);

                                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    final LinearLayout l1 = (LinearLayout) inflater.inflate(R.layout.tax_details_list, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                    l1.setId(200 + j);
                                    l1.setLayoutParams(lp);

                                    final TextView txt_insuranceName,txt_insuranceDesc,txt_totalCharge;
                                    txt_insuranceName= (TextView)l1.findViewById(R.id.lbl_InsuranceName);
                                    txt_insuranceDesc=(TextView)l1.findViewById(R.id.lbl_insuranceDesc);
                                    txt_totalCharge=(TextView)l1.findViewById(R.id.lbl_totalCharge);

                                    txt_insuranceName.setText(insuranceName);
                                    txt_insuranceDesc.setText(insuranceDesc);
                                    txt_totalCharge.setText(((String.format(Locale.US,"%.2f",totalCharge))));

                                    final ToggleButton s1=l1.findViewById(R.id.switch2);
                                    previousSwitch = s1;

                                    if(isSelected == 1)
                                    {
                                        s1.setChecked(true);
                                        previousSwitchtotal=txt_totalCharge.getText().toString();
                                        String totalAmount = txtTotalAmount.getText().toString();
                                        double TotalValue = Double.parseDouble(totalAmount)+Double.parseDouble(previousSwitchtotal) ;
                                        txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));

                                        ReservationBundle.putInt("DeductibleCoverId", deductableId);
                                        ReservationBundle.putDouble("DeductibleCharge", totalCharge);
                                    }
                                    else {
                                        previousSwitch.setChecked(false);
                                    }

                                    s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                                    {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
                                        {
                                            try {

                                                if (isChecked)
                                                {
                                                    ReservationBundle.putInt("DeductibleCoverId", deductableId);
                                                    ReservationBundle.putDouble("DeductibleCharge", totalCharge);

                                                    previousSwitch.setChecked(false);
                                                    previousSwitchtotal=txt_totalCharge.getText().toString();

                                                    String totalAmount = txtTotalAmount.getText().toString();
                                                    double TotalValue = Double.parseDouble(totalAmount)+Double.parseDouble(previousSwitchtotal) ;
                                                    txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));
                                                }
                                                else
                                                {
                                                    previousSwitchtotal=txt_totalCharge.getText().toString();
                                                    String totalAmount = txtTotalAmount.getText().toString();
                                                    double TotalValue = Double.parseDouble(totalAmount)-Double.parseDouble(previousSwitchtotal);
                                                    txtTotalAmount.setText((String.format(Locale.US, "%.2f", TotalValue)));
                                                }
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    });


                                    rlInsuranceDetails.addView(l1);

                                }*/