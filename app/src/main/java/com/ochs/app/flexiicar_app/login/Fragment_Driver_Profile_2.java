package com.ochs.app.flexiicar_app.login;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.ConfirmationActivity;
import com.ochs.app.R;
import com.ochs.app.ScanDrivingLicense;
import com.ochs.app.adapters.CustomToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static com.ochs.app.apicall.ApiEndPoint.firstImage;
import static com.ochs.app.apicall.ApiEndPoint.secondImage;

public class Fragment_Driver_Profile_2 extends Fragment
{
    TextView lblExpiryDate,lblDateofBirth,lbl_IssuueDate;
    EditText edt_DriverLicenseNO,DL_IssueBy,StateandProvience;
    DatePickerDialog datePickerDialog;
    Bundle RegistrationBundle;
    public int RESULT_LOAD_IMAGE = 1;
    JSONArray ImageList = new JSONArray();
    ImageView DLBacksideImg,DLFronsideImg,BackToRegister1;
    String imgId = "";
    TextView lblDiscard,lblNext;
    Boolean BackTo;
    public static final int PERMISSION_REQUEST_CODE = 1;
    ActivityResultLauncher<Intent> activityResultLauncher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver_profile_2, container, false);
    }


    @Override
    public void setInitialSavedState(@Nullable @org.jetbrains.annotations.Nullable SavedState state) {
        super.setInitialSavedState(state);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            lblNext = view.findViewById(R.id.lblNext2);
            BackToRegister1 = view.findViewById(R.id.BackToRegister1);
            edt_DriverLicenseNO = view.findViewById(R.id.edt_DriverLicenseNO);
            DL_IssueBy = view.findViewById(R.id.DL_IssueBy);
            StateandProvience = view.findViewById(R.id.StatandProvience);
            DLBacksideImg = view.findViewById(R.id.DLBacksideImg);
            DLFronsideImg = view.findViewById(R.id.DLFronsideImg);
            lblDiscard = view.findViewById(R.id.lblDiscard2);
            lblExpiryDate = view.findViewById(R.id.lblExpiryDate);
            lblDateofBirth = view.findViewById(R.id.lblDateofBirth);
            lbl_IssuueDate = view.findViewById(R.id.lbl_IssuueDate);

            RegistrationBundle = getArguments().getBundle("RegistrationBundle");
            BackTo = getArguments().getBoolean("BackTo");
           DLBacksideImg.setImageBitmap(secondImage);
            /*  DLFronsideImg.setImageBitmap(ConfirmationActivity.Companion.getFirstimg());*/

            //DLBacksideImg.setImageBitmap();
            DLFronsideImg.setImageBitmap(firstImage);

            if(BackTo)
            {


                ImageList = new JSONArray(RegistrationBundle.getString("ImageList"));

                if (ImageList.length() > 0)
                {

                    final String doc_Details = ((JSONObject) (ImageList.get(0))).getString("fileBase64");

                    File imgFile = new File(doc_Details);

                    if (imgFile.exists())
                    {
                        Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(doc_Details), 500, 500, true);
                        DLFronsideImg.setImageBitmap(myBitmap);
                    }
                }
                if (ImageList.length() > 1)
                {
                    final String doc_Details = ((JSONObject) (ImageList.get(1))).getString("fileBase64");

                    File imgFile = new File(doc_Details);

                    if (imgFile.exists())
                    {
                        Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(doc_Details), 500, 500, true);
                        DLBacksideImg.setImageBitmap(myBitmap);
                    }
                }

                String Licence_No=RegistrationBundle.getString("Licence_No");
                edt_DriverLicenseNO.setText(Licence_No);

                String LExpiry_Date=RegistrationBundle.getString("LExpiry_Date");
                Date ExDate = new SimpleDateFormat("yyyy-mm-dd").parse(LExpiry_Date);
                SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy");
                String parsedDate = formatter.format(ExDate);
                lblExpiryDate.setText(parsedDate);

                String Cust_DOB=RegistrationBundle.getString("Cust_DOB");
                Date DateofBirth = new SimpleDateFormat("yyyy-mm-dd").parse(Cust_DOB);
                SimpleDateFormat format1 = new SimpleDateFormat("mm/dd/yyyy");
                String parsedDateofBirth = format1.format(DateofBirth);
                lblDateofBirth.setText(parsedDateofBirth);


                String LIssue_Date=RegistrationBundle.getString("LIssue_Date");
                Date DateofIssue = new SimpleDateFormat("yyyy-mm-dd").parse(LIssue_Date);
                SimpleDateFormat format2 = new SimpleDateFormat("mm/dd/yyyy");
                String parsedIssueDate = format2.format(DateofIssue);
                lbl_IssuueDate.setText(parsedIssueDate);

                String LIssue_By=RegistrationBundle.getString("LIssue_By");
                DL_IssueBy.setText(LIssue_By);

                String Cust_StateProvience=RegistrationBundle.getString("Cust_StateProvience");
                StateandProvience.setText(Cust_StateProvience);


            }

            ImageView imgScanDrivingLicense = view.findViewById(R.id.LicenceScanimg);

            imgScanDrivingLicense.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    Intent i = new Intent(getActivity(), ScanDrivingLicense.class);
                    i.putExtra("afterScanBackTo", 4);
                    startActivity(i);

                }
            });

            BackToRegister1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
                        Bundle Registration = new Bundle();
                        Registration.putBundle("RegistrationBundle", RegistrationBundle);
                        System.out.println(Registration);
                        NavHostFragment.findNavController(Fragment_Driver_Profile_2.this)
                                .navigate(R.id.action_DriverProfile2_to_DriverProfile, Registration);
                    }catch (Exception e)
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

            lblExpiryDate.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
                        // calender class's instance and get current date , month and year from calender
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR); // current year
                        int mMonth = c.get(Calendar.MONTH); // current month
                        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                        // date picker dialog
                        datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme,
                                new DatePickerDialog.OnDateSetListener()
                                {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                                    {
                                        String month, day;
                                        if (monthOfYear < 9)
                                            month = "0" + (monthOfYear + 1);
                                        else
                                            month = (monthOfYear + 1) + "";

                                        if (dayOfMonth < 10)
                                            day = "0" + dayOfMonth;
                                        else
                                            day = dayOfMonth + "";

                                        lblExpiryDate.setText(month + "/" + day + "/" + year);
                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            lblDateofBirth.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
                        // calender class's instance and get current date , month and year from calender
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR); // current year
                        int mMonth = c.get(Calendar.MONTH); // current month
                        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                        // date picker dialog
                        datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme,
                                new DatePickerDialog.OnDateSetListener()
                                {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                                    {
                                        String month, day;
                                        if (monthOfYear < 9)
                                            month = "0" + (monthOfYear + 1);
                                        else
                                            month = (monthOfYear + 1) + "";

                                        if (dayOfMonth < 10)
                                            day = "0" + dayOfMonth;
                                        else
                                            day = dayOfMonth + "";

                                        lblDateofBirth.setText(month + "/" + day + "/" + year);
                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()+ (1000 * 60 * 60 * 24 * 0));
                        datePickerDialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            lbl_IssuueDate.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
                        // calender class's instance and get current date , month and year from calender
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR); // current year
                        int mMonth = c.get(Calendar.MONTH); // current month
                        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                        // date picker dialog
                        datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        String month, day;
                                        if (monthOfYear < 9)
                                            month = "0" + (monthOfYear + 1);
                                        else
                                            month = (monthOfYear + 1) + "";

                                        if (dayOfMonth < 10)
                                            day = "0" + dayOfMonth;
                                        else
                                            day = dayOfMonth + "";

                                        lbl_IssuueDate.setText(month + "/" + day + "/" + year);
                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()+ (1000 * 60 * 60 * 24 * 0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            DLFronsideImg.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    imgId = "2";
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
/*
                    if(checkPermission()){
                        // do operation
                    }
                    else{
                        // request Permission in Activity
                        requestPermissions(getApplicationContext(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE},200);
                        // request Permission in Fragmemt
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
                    }*/
                /*    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);*/

                   /* if (SDK_INT >= Build.VERSION_CODES.R) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setData(Uri.parse(String.format("package:%s", getActivity().getPackageName())));
                            startActivityForResult(intent, RESULT_LOAD_IMAGE);
                        } catch (Exception e) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            startActivityForResult(intent, RESULT_LOAD_IMAGE);
                        }
                    } else {
                        //below android 11
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(intent, RESULT_LOAD_IMAGE);
                    }*/

                  /*  if (checkPermission()){
                        *//*Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(intent, RESULT_LOAD_IMAGE);*//*



                    } else {
                        requestPermission();
                    }*/
                }
            });



            DLBacksideImg.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    imgId = "3";
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                   /* Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);*/

                  /*  if (SDK_INT >= Build.VERSION_CODES.R) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setData(Uri.parse(String.format("package:%s", getActivity().getPackageName())));
                            startActivityForResult(intent, RESULT_LOAD_IMAGE);

                        } catch (Exception e) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            startActivityForResult(intent, RESULT_LOAD_IMAGE);
                        }
                    } else {
                        //below android 11
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(intent, RESULT_LOAD_IMAGE);
                    }*/
                }
            });

            lblNext.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
                        if (edt_DriverLicenseNO.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Enter Your Driving License NO.",1);
                        else if (lblExpiryDate.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Enter Your Driving License Expiry Date",1);
                        else if (lblDateofBirth.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Enter Your Date Of Birth",1);
                        else if (lbl_IssuueDate.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Enter License Issue Date",1);
                        else if (StateandProvience.getText().toString().equals(""))
                            CustomToast.showToast(getActivity(),"Please Enter Your State Name",1);
                        else {

                            RegistrationBundle.putString("Licence_No", edt_DriverLicenseNO.getText().toString());

                            //ExpiryDate
                            String Exdatestr = lblExpiryDate.getText().toString();
                            Date ExDate = new SimpleDateFormat("mm/dd/yyyy").parse(Exdatestr);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
                            String parsedDate = formatter.format(ExDate);

                            RegistrationBundle.putString("LExpiry_Date", parsedDate);
                            //Date Of Birth
                            String Cus_DateofBirthStr = lblDateofBirth.getText().toString();
                            Date DateofBirth = new SimpleDateFormat("mm/dd/yyyy").parse(Cus_DateofBirthStr);
                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-mm-dd");
                            String parsedDateofBirth = format1.format(DateofBirth);
                            RegistrationBundle.putString("Cust_DOB", parsedDateofBirth);
                            RegistrationBundle.putString("LIssue_By", DL_IssueBy.getText().toString());
                            //Issue Date
                            String IssueDateStr = lbl_IssuueDate.getText().toString();
                            Date DateofIssue = new SimpleDateFormat("mm/dd/yyyy").parse(IssueDateStr);
                            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-mm-dd");
                            String parsedIssueDate = format2.format(DateofIssue);
                            RegistrationBundle.putString("LIssue_Date", parsedIssueDate);
                            RegistrationBundle.putString("Cust_StateProvience", StateandProvience.getText().toString());
                            RegistrationBundle.putString("ImageList", ImageList.toString());

                            Bundle Registration = new Bundle();
                            Registration.putBundle("RegistrationBundle", RegistrationBundle);
                            Registration.putBoolean("BackTo", false);
                            System.out.println(Registration);
                            NavHostFragment.findNavController(Fragment_Driver_Profile_2.this)
                                    .navigate(R.id.action_DriverProfile2_to_DriverProfile3, Registration);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


   /* @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        // perform action when allow permission success
                        Log.e("TAG", "onRequestPermissionsResult: "+0);
                    } else {
                        //Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "onRequestPermissionsResult: "+1);
                    }
                }
                break;
        }
    }*/

/*    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        try {
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data)
            {


                Uri selectedImage = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = getScaledBitmap(selectedImage,500,500);
                    if (imgId.equals("2"))
                    {
                        JSONObject docObj = new JSONObject();
                        docObj.put("Doc_For", 6);
                        docObj.put("VhlPictureSide", 2);
                        docObj.put("fileBase64", getImagePathFromUri(selectedImage));
                        ImageList.put(docObj);
                        DLFronsideImg.setImageBitmap(bitmap);
                    }
                    if (imgId.equals("3"))
                    {
                        JSONObject docObj = new JSONObject();
                        docObj.put("Doc_For", 6);
                        docObj.put("VhlPictureSide", 3);
                        docObj.put("fileBase64", getImagePathFromUri(selectedImage));
                        ImageList.put(docObj);
                        DLBacksideImg.setImageBitmap(bitmap);
                    }
                } catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getImagePathFromUri(Uri contentUri)
    {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
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

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested one
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

 /*   private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getContext().getPackageName())));
                startActivityForResult(intent, 1);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 1);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }*/


}
