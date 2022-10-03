package com.ochs.app.fleetowner.registration;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;
import com.ochs.app.ScanDrivingLicense;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Calendar;

public class Fragment_Registration_Profile_2 extends Fragment
{
    TextView lblExpiryDate,lblDateofBirth,lbl_IssuueDate;
    EditText edt_DriverLicenseNO,DL_IssueBy,StateandProvience;
    DatePickerDialog datePickerDialog;
   // Bundle RegistrationBundle;
    public int RESULT_LOAD_IMAGE = 1;
    JSONArray ImageList = new JSONArray();
    ImageView DLBacksideImg,DLFronsideImg,Back;
    String imgId = "";
    TextView lblDiscard,lblNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver_profile_2, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            lblNext = view.findViewById(R.id.lblNext2);
            Back = view.findViewById(R.id.BackToRegister1);
            edt_DriverLicenseNO = view.findViewById(R.id.edt_DriverLicenseNO);
            DL_IssueBy = view.findViewById(R.id.DL_IssueBy);
            StateandProvience = view.findViewById(R.id.StatandProvience);
            DLBacksideImg = view.findViewById(R.id.DLBacksideImg);
            DLFronsideImg = view.findViewById(R.id.DLFronsideImg);
            lblDiscard = view.findViewById(R.id.lblDiscard2);

        //    RegistrationBundle = getArguments().getBundle("RegistrationBundle");

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

            lblDiscard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    NavHostFragment.findNavController(Fragment_Registration_Profile_2.this)
                            .navigate(R.id.action_DriverProfile2_to_CreateProfile);
                }
            });

            lblExpiryDate = view.findViewById(R.id.lblExpiryDate);
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

            lblDateofBirth = view.findViewById(R.id.lblDateofBirth);
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

            lbl_IssuueDate = view.findViewById(R.id.lbl_IssuueDate);
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
                public void onClick(View view)
                {
                    imgId = "2";
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
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
                }
            });


            lblNext.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {

                        /*if (edt_DriverLicenseNO.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Your Driving License NO", Toast.LENGTH_LONG).show();
                        else if (edt_ExpiryDateDL.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Your Driving License Expiry Date", Toast.LENGTH_LONG).show();
                        else if (Cus_DateofBirth.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Your Date Of Birth", Toast.LENGTH_LONG).show();
                        else if (IssueDate.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Issue Date", Toast.LENGTH_LONG).show();
                        else if (StateandProvience.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please Enter Your State", Toast.LENGTH_LONG).show();
                        else {

                            RegistrationBundle.putString("Licence_No", edt_DriverLicenseNO.getText().toString());

                            //ExpiryDate
                            String Exdatestr = edt_ExpiryDateDL.getText().toString();
                            Date ExDate = new SimpleDateFormat("mm/dd/yyyy").parse(Exdatestr);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
                            String parsedDate = formatter.format(ExDate);
                            RegistrationBundle.putString("LExpiry_Date", parsedDate);
                            //Date Of Birth
                            String Cus_DateofBirthStr = Cus_DateofBirth.getText().toString();
                            Date DateofBirth = new SimpleDateFormat("mm/dd/yyyy").parse(Cus_DateofBirthStr);
                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-mm-dd");
                            String parsedDateofBirth = format1.format(DateofBirth);
                            RegistrationBundle.putString("Cust_DOB", parsedDateofBirth);
                            RegistrationBundle.putString("LIssue_By", DL_IssueBy.getText().toString());
                            //Issue Date
                            String IssueDateStr = IssueDate.getText().toString();
                            Date DateofIssue = new SimpleDateFormat("mm/dd/yyyy").parse(IssueDateStr);
                            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-mm-dd");
                            String parsedIssueDate = format2.format(DateofIssue);
                            RegistrationBundle.putString("LIssue_Date", parsedIssueDate);
                            RegistrationBundle.putString("Cust_StateProvience", StateandProvience.getText().toString());
                            RegistrationBundle.putString("ImageList", ImageList.toString());
                            Bundle Registration = new Bundle();
                            Registration.putBundle("RegistrationBundle", RegistrationBundle);
                            System.out.println(Registration);*/

                            NavHostFragment.findNavController(Fragment_Registration_Profile_2.this)
                                    .navigate(R.id.action_DriverProfile2_to_DriverProfile3);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            Back.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                   // Bundle Registration = new Bundle();
                   // Registration.putBundle("RegistrationBundle", RegistrationBundle);
                 //   System.out.println(Registration);
                    NavHostFragment.findNavController(Fragment_Registration_Profile_2.this)
                            .navigate(R.id.action_DriverProfile2_to_DriverProfile);
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        try {


            if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                Bitmap bitmap = null;

                try {
                    bitmap = getBitmapFromUri(selectedImage);

                    if (imgId.equals("2"))
                    {
                        JSONObject docObj = new JSONObject();
                        docObj.put("fileBase64", getRealPathFromURI(selectedImage));
                        docObj.put("VhlPictureSide", 1);
                        ImageList.put(docObj);
                        DLFronsideImg.setImageBitmap(bitmap);
                    }

                    if (imgId.equals("3")) {
                        JSONObject docObj = new JSONObject();
                        docObj.put("fileBase64", getRealPathFromURI(selectedImage));
                        docObj.put("VhlPictureSide", 2);
                        ImageList.put(docObj);
                        DLBacksideImg.setImageBitmap(bitmap);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
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

    private String getRealPathFromURI(Uri contentURI)
    {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
