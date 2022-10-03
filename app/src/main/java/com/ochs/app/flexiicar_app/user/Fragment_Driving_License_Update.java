package com.ochs.app.flexiicar_app.user;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Patterns;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Build.VERSION.SDK_INT;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_CUSTOMER;
import static com.ochs.app.apicall.ApiEndPoint.UPDATEDRIVINGLICENSE;

public class Fragment_Driving_License_Update extends Fragment
{
    EditText edt_DriverName,edt_LicenseNo,edt_IssueBy,edt_RelationShip,edt_Email,edt_Telephone;
    TextView lbl_Save,lblexDate,lblissuedate;
    Handler handler = new Handler();
    public static Context context;
    public String id="";
    Bundle LicenseBundle;
    ImageView Image_Back;
    DatePickerDialog datePickerDialog;
    ImageView img_DLFronside,img_DLBackside;
    private static int RESULT_LOAD_IMAGE = 1;
    String imagestr;
    String imgId = "";
    ImageLoader imageLoader;
    String serverpath="";
    JSONArray ImageList = new JSONArray();
    ProgressDialog progressDialog;

    public static void initImageLoader(Context context)
    {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
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
        return inflater.inflate(R.layout.fragment_driving_license_add_update, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            ((User_Profile) getActivity()).BottomnavInVisible();

            initImageLoader(getActivity());
            imageLoader = ImageLoader.getInstance();

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            serverpath = sp.getString("serverPath", "");
            id = sp.getString(getString(R.string.id), "");

            LicenseBundle = getArguments().getBundle("LicenseBundle");

            edt_DriverName = view.findViewById(R.id.edt_driverName);
            edt_LicenseNo = view.findViewById(R.id.edt_LicenseNo);
            lblexDate = view.findViewById(R.id.lblexDate);
            edt_IssueBy = view.findViewById(R.id.edt_issueBy);
            lblissuedate=view.findViewById(R.id.lblissuedate);
            edt_RelationShip = view.findViewById(R.id.edt_Relationship);
            edt_Email = view.findViewById(R.id.edt_DriverEmail);
            edt_Telephone = view.findViewById(R.id.edt_driverTelephone);
            lbl_Save = view.findViewById(R.id.lbl_Save);

            img_DLBackside= view.findViewById(R.id.img_DLBackside);
            img_DLFronside= view.findViewById(R.id.img_DLFronside);

            img_DLFronside.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    imgId = "2";

                }
            });

            img_DLBackside.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    imgId = "3";

                }
            });

            Image_Back = view.findViewById(R.id.Image_BackDL);
            Image_Back.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    NavHostFragment.findNavController(Fragment_Driving_License_Update.this)
                            .navigate(R.id.action_DrivingLicense_Update_to_DrivingLicense_Details);
                }
            });

            ImageView imgScanDrivingLicense = view.findViewById(R.id.imgScanDrivingLicense);

            imgScanDrivingLicense.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    Intent i = new Intent(getActivity(), ScanDrivingLicense.class);
                    i.putExtra("afterScanBackTo", 2);
                    startActivity(i);

                }
            });

            lblexDate.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        // calender class's instance and get current date , month and year from calender
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR); // current year
                        int mMonth = c.get(Calendar.MONTH); // current month
                        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                        // date picker dialog
                        datePickerDialog = new DatePickerDialog(getActivity(),R.style.DialogTheme,
                                new DatePickerDialog.OnDateSetListener()
                                {

                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                                    {
                                        String month, day;
                                        if(monthOfYear<9)
                                            month="0"+(monthOfYear+1);
                                        else
                                            month = (monthOfYear+1)+"";

                                        if(dayOfMonth<10)
                                            day="0"+dayOfMonth;
                                        else
                                            day=dayOfMonth+"";

                                        lblexDate.setText(year + "-" + month + "-" + day);
                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            lblissuedate.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        // calender class's instance and get current date , month and year from calender
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR); // current year
                        int mMonth = c.get(Calendar.MONTH); // current month
                        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                        // date picker dialog
                        datePickerDialog = new DatePickerDialog(getActivity(),R.style.DialogTheme,
                                new DatePickerDialog.OnDateSetListener()
                                {

                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                                    {
                                        String month, day;
                                        if(monthOfYear<9)
                                            month="0"+(monthOfYear+1);
                                        else
                                            month = (monthOfYear+1)+"";

                                        if(dayOfMonth<10)
                                            day="0"+dayOfMonth;
                                        else
                                            day=dayOfMonth+"";

                                        lblissuedate.setText(year + "-" + month + "-" + day);
                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()+ (1000 * 60 * 60 * 24 * 0));
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            String lExpiry_Date = LicenseBundle.getString("lExpiry_Date");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date date = dateFormat.parse(lExpiry_Date);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String lExpiry_DateStr = sdf.format(date);

            String lIssue_Date=LicenseBundle.getString("lIssue_Date");
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date date1 = dateFormat1.parse(lIssue_Date);
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            String lIssue_DateStr = sdf1.format(date1);

            edt_DriverName.setText(LicenseBundle.getString("driverFullName"));
            edt_LicenseNo.setText(LicenseBundle.getString("licence_No"));
            lblexDate.setText(lExpiry_DateStr);
            lblissuedate.setText(lIssue_DateStr);

            edt_IssueBy.setText(LicenseBundle.getString("lIssue_By"));
            edt_RelationShip.setText(LicenseBundle.getString("driverRelationship"));
            edt_Email.setText(LicenseBundle.getString("driverEmailId"));
            edt_Telephone.setText(LicenseBundle.getString("driverTelephone"));

            JSONArray docArray = new JSONArray(LicenseBundle.getString("docArray"));

            for(int imagecount=0;imagecount<docArray.length();imagecount++)
            {
                final String doc_Details = ((JSONObject) (docArray.get(imagecount))).getString("doc_Details");
                final String doc_Name = ((JSONObject) (docArray.get(imagecount))).getString("doc_Name");

                final int vhlPictureSide = ((JSONObject) (docArray.get(imagecount))).getInt("vhlPictureSide");

                if (vhlPictureSide==2)
                {
                    String url1 = serverpath + doc_Details.substring(2);
                    url1 = url1.substring(0, url1.lastIndexOf("/") + 1) + doc_Name;
                    imageLoader.displayImage(url1, img_DLFronside);

                }
                if (vhlPictureSide==3)
                {
                    String url2 = serverpath + doc_Details.substring(2);
                    url2 = url2.substring(0, url2.lastIndexOf("/") + 1) + doc_Name;
                    imageLoader.displayImage(url2, img_DLBackside);

                }
            }

            lbl_Save.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (edt_DriverName.getText().toString().equals(""))
                        CustomToast.showToast(getActivity(),"Please Enter Your Name",1);
                    else if (edt_LicenseNo.getText().toString().equals(""))
                        CustomToast.showToast(getActivity(),"Please Enter License No",1);
                    else if (lblexDate.getText().toString().equals(""))
                        CustomToast.showToast(getActivity(),"Please Enter Expiry Date",1);
                    else if (lblissuedate.getText().toString().equals(""))
                        CustomToast.showToast(getActivity(),"Please Enter Issue Date",1);
                    else if (edt_Email.getText().toString().equals(""))
                        CustomToast.showToast(getActivity(),"Please Enter Email",1);
                    else if (!Patterns.EMAIL_ADDRESS.matcher(edt_Email.getText().toString()).matches())
                    {
                        edt_Email.setError("Enter valid Email address");
                        edt_Email.requestFocus();
                    }
                    else if (edt_Telephone.getText().toString().equals(""))
                        CustomToast.showToast(getActivity(),"Please Enter Your Telephone No.",1);
                    else {
                        JSONObject bodyParam = new JSONObject();
                        try
                        {
                            bodyParam.accumulate("CustomerId", Integer.parseInt(id));
                            bodyParam.accumulate("LCategory", LicenseBundle.getString("lCategory"));
                            bodyParam.accumulate("Licence_No", edt_LicenseNo.getText().toString());
                            bodyParam.accumulate("LIssue_Date", lblissuedate.getText().toString());
                            bodyParam.accumulate("LExpiry_Date", lblexDate.getText().toString());
                            bodyParam.accumulate("LIssue_By", edt_IssueBy.getText().toString());
                            bodyParam.accumulate("DriverFullName", edt_DriverName.getText().toString());
                            bodyParam.accumulate("DriverRelationship", edt_RelationShip.getText().toString());
                            bodyParam.accumulate("DriverEmailId", edt_Email.getText().toString());
                            bodyParam.accumulate("DriverTelephone", edt_Telephone.getText().toString());

                            if(ImageList!=null)
                            {
                                bodyParam.accumulate("ImageList", ImageList);
                            }
                            System.out.println(bodyParam);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        ApiService ApiService = new ApiService(UpdateDrivingLicese, RequestType.POST,
                                UPDATEDRIVINGLICENSE, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Please Wait...");
                        progressDialog.setCancelable(false);
                        progressDialog.setMax(100);
                        progressDialog.show();

                    }
                }
            });
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
                String img_str_base64 = Base64.encodeToString(image, Base64.NO_WRAP);

                if(imgId.equals("2"))
                {
                    JSONObject frontImgObj = new JSONObject();
                    frontImgObj.put("VhlPictureSide", 2);
                    frontImgObj.put("Doc_For", 6);
                    frontImgObj.put("fileBase64",img_str_base64);
                    img_DLFronside.setImageBitmap(bitmap);
                    ImageList.put(frontImgObj);
                }
                else if (imgId.equals("3"))
                {
                    JSONObject backImgObj = new JSONObject();
                    backImgObj.put("VhlPictureSide", 3);
                    backImgObj.put("Doc_For", 6);
                    backImgObj.put("fileBase64", img_str_base64);
                    img_DLBackside.setImageBitmap(bitmap);
                    ImageList.put(backImgObj);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested one
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
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

    OnResponseListener UpdateDrivingLicese = new OnResponseListener()
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
                        progressDialog.dismiss();

                        JSONObject responseJSON = new JSONObject(response);
                        Boolean status = responseJSON.getBoolean("status");

                        if (status)
                        {
                            String msg = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),msg,0);

                            NavHostFragment.findNavController(Fragment_Driving_License_Update.this)
                                    .navigate(R.id.action_DrivingLicense_Update_to_DrivingLicense_Details);
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

}