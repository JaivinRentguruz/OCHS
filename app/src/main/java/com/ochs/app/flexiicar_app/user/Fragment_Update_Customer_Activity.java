package com.ochs.app.flexiicar_app.user;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.ochs.app.flexiicar_app.booking.Fragment_Payment_checkout;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.ACTIVITYTIMELINE;
import static com.ochs.app.apicall.ApiEndPoint.ACTIVITYTYPELIST;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_CUSTOMER;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_SETTINGS;
import static com.ochs.app.apicall.ApiEndPoint.UPDATECUSTOMERACTIVITY;

public class Fragment_Update_Customer_Activity extends Fragment
{
    ImageView backarrow,cust_image1,cust_image2,cust_image3,cust_image4,Removeimg1,Removeimg2,Removeimg3,Removeimg4;
    Bundle ActivityBundle;
    public static Context context;
    public String id = "";
    Handler handler = new Handler();
    TextView lblSubmit,txtDay,Date,Time;
    public String[] Activity;
    public int[] ActivityId;
    EditText edt_ActivityTitleUpdate,edt_ActivityDescUpdate;
    HashMap<String, Integer> Activityhasmap=new HashMap<String, Integer>();
    Spinner Sp_ActiviyList;
    DatePickerDialog datePickerDialog;
    ImageLoader imageLoader;
    String serverpath="";
    private static int RESULT_LOAD_IMAGE = 1;
    JSONArray ImageList = new JSONArray();
    String imagestr;
    String ActivityTypeName;
    LinearLayout llImage1,llImage2,llImage3,llImage4;
    int imgNo;
    ImageView icon_calander;
    int cDay, cMonth, cYear,hour1,minutes1;
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

        ImageLoader.getInstance().init(config.build());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_add_customer_activity, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        try
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

            backarrow = view.findViewById(R.id.Back);
            edt_ActivityDescUpdate = view.findViewById(R.id.edt_ActivityDesc);
            edt_ActivityTitleUpdate = view.findViewById(R.id.edt_ActivityTitle);
            icon_calander= view.findViewById(R.id.icon_calander);

            Sp_ActiviyList = view.findViewById(R.id.Sp_ActivityTimeline);

            lblSubmit = view.findViewById(R.id.lblSubmit);

            cust_image1 = view.findViewById(R.id.cust_image1);
            cust_image2 = view.findViewById(R.id.cust_image2);
            cust_image3 = view.findViewById(R.id.cust_image3);
            cust_image4 = view.findViewById(R.id.cust_image4);

            Removeimg1 = view.findViewById(R.id.removeimg1);
            Removeimg2 = view.findViewById(R.id.removeimg2);
            Removeimg3 = view.findViewById(R.id.removeimg3);
            Removeimg4 = view.findViewById(R.id.removeimg4);

            llImage1= view.findViewById(R.id.llImage1);
            llImage2= view.findViewById(R.id.llImage2);
            llImage3= view.findViewById(R.id.llImage3);
            llImage4= view.findViewById(R.id.llImage4);

            txtDay = view.findViewById(R.id.txtDay);
            Date = view.findViewById(R.id.Date);
            Time = view.findViewById(R.id.Time);

            Removeimg1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    cust_image1.setImageBitmap(null);
                    cust_image1.setImageResource(0);
                    cust_image1.invalidate();
                    bitmap=null;
                    Removeimg1.setVisibility(View.GONE);
                    llImage1.setVisibility(View.VISIBLE);

                    int count=ImageList.length();
                    for (int i=0;i<count;i++)
                    {
                        try {
                            JSONObject obj=ImageList.getJSONObject(i);

                            if(obj.getInt("vhlPictureSide")==1)
                            {
                                ImageList.remove(i);
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });

            Removeimg2.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    cust_image2.setImageBitmap(null);
                    cust_image2.setImageResource(0);
                    cust_image2.invalidate();
                    bitmap=null;
                    Removeimg2.setVisibility(View.GONE);
                    llImage2.setVisibility(View.VISIBLE);

                    int count=ImageList.length();
                    for (int i=0;i<count;i++)
                    {
                        try {
                            JSONObject obj=ImageList.getJSONObject(i);

                            if(obj.getInt("vhlPictureSide")==2)
                            {
                                ImageList.remove(i);
                            }

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });
            Removeimg3.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    cust_image3.setImageBitmap(null);
                    cust_image3.setImageResource(0);
                    cust_image3.invalidate();
                    bitmap=null;
                    Removeimg3.setVisibility(View.GONE);
                    llImage3.setVisibility(View.VISIBLE);

                    int count=ImageList.length();
                    for (int i=0;i<count;i++)
                    {
                        try {
                            JSONObject obj=ImageList.getJSONObject(i);

                            if(obj.getInt("vhlPictureSide")==3)
                            {
                                ImageList.remove(i);
                            }

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });

            Removeimg4.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    cust_image4.setImageBitmap(null);
                    cust_image4.setImageResource(0);
                    cust_image4.invalidate();
                    bitmap=null;
                    Removeimg4.setVisibility(View.GONE);
                    llImage4.setVisibility(View.VISIBLE);

                    int count=ImageList.length();
                    for (int i=0;i<count;i++)
                    {
                        try {
                            JSONObject obj=ImageList.getJSONObject(i);

                            if(obj.getInt("vhlPictureSide")==4)
                            {
                                ImageList.remove(i);
                            }

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });
            cust_image1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    imgNo = 1;
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                }
            });
            cust_image2.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    imgNo = 2;
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                }
            });
            cust_image3.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    imgNo = 3;
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                }
            });
            cust_image4.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    imgNo = 4;
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                }
            });

            ActivityBundle = getArguments().getBundle("ActivityTimeLine");

            final int activityID = (ActivityBundle.getInt("activityID"));

            ActivityTypeName= (ActivityBundle.getString("activityName"));

            String ActivityDate = (ActivityBundle.getString("activityDate"));
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date date1 = dateFormat1.parse(ActivityDate);

            SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd,yyyy");
            String ActivityDateStr = sdf1.format(date1);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm aa", Locale.US);
            String ActivityTimeStr = sdf.format(date1);

            SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE");
            String ActivityDayStr = sdf2.format(date1);

            Date.setText(ActivityDateStr);
            Time.setText(ActivityTimeStr);
            txtDay.setText(ActivityDayStr);

            edt_ActivityDescUpdate.setText(ActivityBundle.getString("description"));
            edt_ActivityTitleUpdate.setText(ActivityBundle.getString("activityTitle"));

            backarrow.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    NavHostFragment.findNavController(Fragment_Update_Customer_Activity.this)
                            .navigate(R.id.action_Update_CutomerActivity_to_User_timeline);
                }
            });

            lblSubmit.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (edt_ActivityTitleUpdate.getText().toString().equals(""))
                        CustomToast.showToast(getActivity(),"Please EnterActivityTitle",1);
                    else if (edt_ActivityDescUpdate.getText().toString().equals(""))
                        CustomToast.showToast(getActivity(),"Please Enter ActivityDesc",1);
                    else {
                        JSONObject bodyParam = new JSONObject();
                        try {
                            bodyParam.accumulate("CustomerID", Integer.parseInt(id));
                            bodyParam.accumulate("ActivityID", activityID);
                            
                            String StrDateForActivity = Date.getText().toString();
                            SimpleDateFormat dateFormat1 = new SimpleDateFormat("MMM dd,yyyy");
                            java.util.Date date1 = dateFormat1.parse(StrDateForActivity);
                            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                            String ActivityDateStr = sdf1.format(date1);

                            String StrTimeForActivity = Time.getText().toString();
                            SimpleDateFormat TimeFormat = new SimpleDateFormat("hh:mm aa");
                            java.util.Date Time = TimeFormat.parse(StrTimeForActivity);
                            SimpleDateFormat Time1 = new SimpleDateFormat("HH:mm:ss");
                            String ActivityTimeStr = Time1.format(Time);

                            String TimeDate=ActivityDateStr+"T"+ActivityTimeStr;

                            bodyParam.accumulate("ActivityDate",TimeDate);

                            int s = Activityhasmap.get(Sp_ActiviyList.getSelectedItem());
                            bodyParam.accumulate("ActivityType", s);
                            bodyParam.accumulate("ActivityTitle", edt_ActivityTitleUpdate.getText().toString());
                            bodyParam.accumulate("Description", edt_ActivityDescUpdate.getText().toString());
                            bodyParam.accumulate("TransID", 0);
                            bodyParam.accumulate("ImageList", ImageList);
                            System.out.println(bodyParam);

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        AndroidNetworking.initialize(getActivity());
                        Fragment_Update_Customer_Activity.context = getActivity();

                        ApiService ApiService = new ApiService(UpdateCustomerActivity, RequestType.POST,
                                UPDATECUSTOMERACTIVITY, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);
                    }
                }
            });

            try {
                ApiService ApiService = new ApiService(ActivityTypeList, RequestType.GET,
                        ACTIVITYTYPELIST, BASE_URL_SETTINGS, new HashMap<String, String>(), bodyParam);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            try {
                bodyParam += "activityId="+activityID;

                System.out.println(bodyParam);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            AndroidNetworking.initialize(getActivity());
            Fragment_Payment_checkout.context = getActivity();

            ApiService ApiService1 = new ApiService(ActivityTimeLine, RequestType.GET,
                    ACTIVITYTIMELINE, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);
        }


        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    OnResponseListener UpdateCustomerActivity = new OnResponseListener()
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
                            NavHostFragment.findNavController(Fragment_Update_Customer_Activity.this)
                                    .navigate(R.id.action_Update_CutomerActivity_to_User_timeline);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == android.app.Activity.RESULT_OK && null != data)
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
            try
            {
                bitmap = getBitmapFromUri(selectedImage);
                Bitmap temp = bitmap;

                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                temp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] image=stream.toByteArray();
                String img_str_base64 = Base64.encodeToString(image, Base64.DEFAULT);

                if(imgNo==1)
                {
                    JSONObject ImgObj = new JSONObject();
                    ImgObj.put("fileBase64", img_str_base64);
                    ImgObj.put("Doc_For", 19);
                    ImgObj.put("vhlPictureSide", 1);

                    if(ImageList.length()>=1)
                        ImageList.remove(0);
                    ImageList.put(ImgObj);
                    cust_image1.setImageBitmap(bitmap);
                    llImage2.setVisibility(View.VISIBLE);
                    Removeimg1.setVisibility(View.VISIBLE);;
                }
                if(imgNo==2)
                {
                    JSONObject ImgObj = new JSONObject();
                    ImgObj.put("fileBase64", img_str_base64);
                    ImgObj.put("Doc_For", 19);
                    ImgObj.put("vhlPictureSide", 2);

                    if(ImageList.length()>=2)
                        ImageList.remove(1);
                    ImageList.put(ImgObj);
                    cust_image2.setImageBitmap(bitmap);
                    llImage3.setVisibility(View.VISIBLE);
                    Removeimg2.setVisibility(View.VISIBLE);
                }
                if(imgNo==3)
                {
                    if(ImageList.length()>=3)
                        ImageList.remove(2);
                    JSONObject ImgObj = new JSONObject();
                    ImgObj.put("fileBase64", img_str_base64);
                    ImgObj.put("Doc_For", 19);
                    ImgObj.put("vhlPictureSide", 3);

                    ImageList.put(ImgObj);
                    cust_image3.setImageBitmap(bitmap);
                    llImage4.setVisibility(View.VISIBLE);
                    Removeimg3.setVisibility(View.VISIBLE);
                }
                if(imgNo==4)
                {
                    if(ImageList.length()>=4)
                        ImageList.remove(4);
                    JSONObject ImgObj = new JSONObject();
                    ImgObj.put("fileBase64", img_str_base64);
                    ImgObj.put("Doc_For", 19);
                    ImgObj.put("vhlPictureSide", 4);

                    ImageList.put(ImgObj);
                    cust_image4.setImageBitmap(bitmap);
                    Removeimg4.setVisibility(View.VISIBLE);
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
    //ActivityList
    OnResponseListener ActivityTypeList = new OnResponseListener()
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
                            final JSONArray ActivityList = resultSet.getJSONArray("t0040_Activity_Type_Master");

                            int len;
                            len = ActivityList.length();

                            ActivityId = new int[len];
                            Activity = new String[len];

                            int position=0;

                            for (int j = 0; j < len; j++)
                            {
                                final JSONObject test = (JSONObject) ActivityList.get(j);
                                final int typeID = test.getInt("typeID");
                                final String name = test.getString("name");
                                final String color=test.getString("color");
                                final String code=test.getString("code");
                                final String createdOn=test.getString("createdOn");

                                Activity[j] = name;
                                ActivityId[j] = typeID;

                                Activityhasmap.put(name,typeID);

                                if(ActivityTypeName.equals(name))
                                {
                                    position = j;
                                }
                            }

                            ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, R.id.text1, Activity);
                            Sp_ActiviyList.setAdapter(adapterCategories);
                            Sp_ActiviyList.setSelection(position);
                        }
                        else
                        {
                            String errorString = responseJSON.getString("message");
                            CustomToast.showToast(getActivity(),"Please Enter ActivityDesc",1);
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
    //ActivityTimeLine
    OnResponseListener ActivityTimeLine= new OnResponseListener()
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
                            final JSONArray getActivityImageArray = resultSet.getJSONArray("t0050_Documents");
                            int len;
                            len = getActivityImageArray.length();

                            for (int j = 0; j < len; j++)
                            {
                                final JSONObject test = (JSONObject) getActivityImageArray.get(j);
                                String doc_Name = test.getString("doc_Name");
                                String doc_Details = test.getString("doc_Details");

                                if(j==0)
                                {
                                    String url1 = serverpath + doc_Details.substring(2);
                                    url1 = url1.substring(0, url1.lastIndexOf("/") + 1) + doc_Name;
                                    imageLoader.displayImage(url1, cust_image1);
                                    Removeimg1.setVisibility(View.VISIBLE);
                                    llImage1.setVisibility(View.VISIBLE);
                                }
                                if(j==1)
                                {
                                    String url1 = serverpath + doc_Details.substring(2);
                                    url1 = url1.substring(0, url1.lastIndexOf("/") + 1) + doc_Name;
                                    imageLoader.displayImage(url1, cust_image2);
                                    Removeimg2.setVisibility(View.VISIBLE);
                                    llImage2.setVisibility(View.VISIBLE);
                                }
                                if(j==2)
                                {
                                    String url1 = serverpath + doc_Details.substring(2);
                                    url1 = url1.substring(0, url1.lastIndexOf("/") + 1) + doc_Name;
                                    imageLoader.displayImage(url1, cust_image3);
                                    Removeimg3.setVisibility(View.VISIBLE);
                                    llImage3.setVisibility(View.VISIBLE);
                                }
                                if(j==3)
                                {
                                    String url1 = serverpath + doc_Details.substring(2);
                                    url1 = url1.substring(0, url1.lastIndexOf("/") + 1) + doc_Name;
                                    imageLoader.displayImage(url1, cust_image4);
                                    Removeimg4.setVisibility(View.VISIBLE);
                                    llImage4.setVisibility(View.VISIBLE);
                                }
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
}


       /*  txt_calanderUpdate.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    try {
                        // calender class's instance and get current date , month and year from calender
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR); // current year
                        int mMonth = c.get(Calendar.MONTH); // current month
                        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
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

                                        txt_calanderUpdate.setText(year + "-" + month + "-" + day);
                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


              icon_calander.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
                        // calender class's instance and get current date , month and year from calender
                        final Calendar c = Calendar.getInstance();
                        cYear = c.get(Calendar.YEAR); // current year
                        cMonth = c.get(Calendar.MONTH); // current month
                        cDay = c.get(Calendar.DAY_OF_MONTH); // current day

                        datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme,
                                new DatePickerDialog.OnDateSetListener()
                                {

                                    @Override
                                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth)
                                    {
                                        cYear = year;
                                        cMonth = monthOfYear;
                                        cDay = dayOfMonth;

                                        String  dd_Precede=" ";
                                        if (cDay <10)
                                        {
                                            dd_Precede = "0";
                                        }

                                        String monthName = "Jan";
                                        if(cMonth == 2)
                                            monthName = "Feb";
                                        else if(cMonth == 3)
                                            monthName = "Mar";
                                        else if(cMonth == 4)
                                            monthName = "Apr";
                                        else if(cMonth == 5)
                                            monthName = "May";
                                        else if(cMonth == 6)
                                            monthName = "Jun";
                                        else if(cMonth == 7)
                                            monthName = "Jul";
                                        else if(cMonth == 8)
                                            monthName = "Aug";
                                        else if(cMonth == 9)
                                            monthName = "Sep";
                                        else if(cMonth == 10)
                                            monthName = "Oct";
                                        else if(cMonth == 11)
                                            monthName = "Nov";
                                        else if(cMonth == 12)
                                            monthName = "Dec";

                                        Date.setText(monthName +" "+dd_Precede+cDay+"," + year);
                                    }
                                }, cYear,cMonth,cDay);
                        datePickerDialog.show();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });*/
