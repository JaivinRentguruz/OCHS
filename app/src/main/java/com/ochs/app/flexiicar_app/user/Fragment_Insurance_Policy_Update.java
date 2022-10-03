package com.ochs.app.flexiicar_app.user;

import android.app.Activity;
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
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_CUSTOMER;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_SETTINGS;
import static com.ochs.app.apicall.ApiEndPoint.INSURANCECOMPANYLIST;
import static com.ochs.app.apicall.ApiEndPoint.UPDATECUSTOMERINSURANCE;

public class Fragment_Insurance_Policy_Update extends Fragment
{
    Handler handler = new Handler();
    public static Context context;
    public String id = "";
    EditText InsPolicyNo;
    TextView lbl_SaveInsurancePolicy,ExpiryDate,IssueDate;
    ImageView Img_UploadPolicy,BackArrow;
    ImageLoader imageLoader;
    String serverpath="";
    Bundle InsuranceBundle;
    DatePickerDialog datePickerDialog;
    Spinner SP_InsuranceCName;
    ArrayList<String> InsuranceCompanyName=new ArrayList<>();
    ArrayList<Integer>InsuranceId=new ArrayList<Integer>();
    String InsuranceCompany;
    private static int RESULT_LOAD_IMAGE = 1;
    JSONArray ImageList = new JSONArray();
    String imagestr;
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
        return inflater.inflate(R.layout.fragment_customer_insurance_update, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (getArguments().getInt("key") != 1) {
            ((User_Profile) getActivity()).BottomnavInVisible();
        }
        initImageLoader(getActivity());
        imageLoader = ImageLoader.getInstance();

        SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
        id = sp.getString(getString(R.string.id), "");
        serverpath = sp.getString("serverPath", "");

        InsPolicyNo=view.findViewById(R.id.edt_PolicyNo);
        ExpiryDate=view.findViewById(R.id.edt_ExpiryDate);
        IssueDate=view.findViewById(R.id.IpIssue_Date);
        SP_InsuranceCName=view.findViewById(R.id.sp_InsuranceCompList);
        lbl_SaveInsurancePolicy=view.findViewById(R.id.lbl_SaveInsurancePolicy);
        Img_UploadPolicy=view.findViewById(R.id.Img_UploadPolicy);
        BackArrow=view.findViewById(R.id.Back);

        InsuranceBundle = getArguments().getBundle("InsurancePolicyBundle");
        System.out.println(InsuranceBundle);

        try {
            String Expiry_Date = InsuranceBundle.getString("expiryDate");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date date = dateFormat.parse(Expiry_Date);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String Expiry_DateStr = sdf.format(date);

            String Issue_Date = InsuranceBundle.getString("insIssueDate");
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date date1 = dateFormat1.parse(Issue_Date);
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            String Issue_DateStr = sdf1.format(date1);

            InsPolicyNo.setText(InsuranceBundle.getString("policy_No"));

            InsuranceCompany =InsuranceBundle.getString("insurance_Cmp_Name");
            ExpiryDate.setText(Expiry_DateStr);
            IssueDate.setText(Issue_DateStr);

            JSONArray docArray = new JSONArray(InsuranceBundle.getString("docArray"));

            if(docArray.length()>0)
            {
                final String doc_Details = ((JSONObject) (docArray.get(0))).getString("doc_Details");
                final String doc_Name = ((JSONObject) (docArray.get(0))).getString("doc_Name");

                String url1 = serverpath + doc_Details.substring(2);
                url1 = url1.substring(0, url1.lastIndexOf("/") + 1) + doc_Name;
                imageLoader.displayImage(url1, Img_UploadPolicy);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Img_UploadPolicy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

        BackArrow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(Fragment_Insurance_Policy_Update.this).popBackStack();
               /* Bundle bundle = new Bundle();
                bundle.putInt("key",getArguments().getInt("key"));
                NavHostFragment.findNavController(Fragment_Insurance_Policy_Update.this)
                        .navigate(R.id.action_InsurancePolicyUpdate_to_InsurancePolicyList,bundle);*/
            }
        });

        ExpiryDate.setOnClickListener(new View.OnClickListener()
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

                                    ExpiryDate.setText(year + "-" + month + "-" + day);
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

        IssueDate.setOnClickListener(new View.OnClickListener()
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
                                    IssueDate.setText(year + "-" + month + "-" + day);
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
        String bodyParam = "";

        lbl_SaveInsurancePolicy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (InsPolicyNo.getText().toString().equals(""))
                    CustomToast.showToast(getActivity(),"Please Enter Insurance Policy No",1);
                else if (ExpiryDate.getText().toString().equals(""))
                    CustomToast.showToast(getActivity(),"Please Enter Expiry Date",1);
                else if (IssueDate.getText().toString().equals(""))
                    CustomToast.showToast(getActivity(),"Please Enter Issue Date",1);
                else if (SP_InsuranceCName.getSelectedItem().toString().equals(""))
                    CustomToast.showToast(getActivity(),"Please Select Insurance Company Name",1);
                else
                {
                    JSONObject bodyParam = new JSONObject();
                    try {
                        int Insurance_Cmp_ID=InsuranceId.get(SP_InsuranceCName.getSelectedItemPosition());
                        bodyParam.accumulate("CustomerId", Integer.parseInt(id));
                        bodyParam.accumulate("Insurance_Cmp_ID",Insurance_Cmp_ID);
                        bodyParam.accumulate("insurance_Cmp_Name",SP_InsuranceCName.getSelectedItem());
                        bodyParam.accumulate("Policy_No", InsPolicyNo.getText().toString());
                        bodyParam.accumulate("ExpiryDate", ExpiryDate.getText().toString());
                        bodyParam.accumulate("InsIssueDate",IssueDate.getText().toString());
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
                    ApiService ApiService = new ApiService(UpdateCustomerInsurance, RequestType.POST,
                            UPDATECUSTOMERINSURANCE, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);
                }
            }
        });

        try
        {
            ApiService ApiService1 = new ApiService(InsuranceCompanyList , RequestType.GET,
                    INSURANCECOMPANYLIST, BASE_URL_SETTINGS, new HashMap<String, String>(), bodyParam);
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
            try
            {
                bitmap = getScaledBitmap(selectedImage,500,500);
                Bitmap temp = bitmap;

                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                temp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] image=stream.toByteArray();
                String img_str_base64 = Base64.encodeToString(image, Base64.NO_WRAP);

                JSONObject ImgObj = new JSONObject();
                ImgObj.put("fileBase64", img_str_base64);
                ImgObj.put("Doc_For", 8);
                ImageList.put(ImgObj);

                Img_UploadPolicy.setImageBitmap(bitmap);

            } catch (Exception e)
            {
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

    //UpdateCustomerInsurance
    OnResponseListener UpdateCustomerInsurance = new OnResponseListener()
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
                            NavHostFragment.findNavController(Fragment_Insurance_Policy_Update.this).popBackStack();
                           /* NavHostFragment.findNavController(Fragment_Insurance_Policy_Update.this)
                                    .navigate(R.id.action_InsurancePolicyUpdate_to_InsurancePolicyList);*/
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

    //InsuranceCompanyList
    OnResponseListener InsuranceCompanyList = new OnResponseListener()
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
                            final JSONArray ActivityList = resultSet.getJSONArray("t0040_Insurance_Company_Details");

                            int len;
                            len = ActivityList.length();

                            InsuranceCompanyName = new ArrayList<>();
                            InsuranceId = new ArrayList<Integer>();

                            int position=0;

                            for (int j = 0; j < len; j++)
                            {
                                final JSONObject test = (JSONObject) ActivityList.get(j);
                                final int insurance_Cmp_ID = test.getInt("insurance_Cmp_ID");
                                final String insurance_Cmp_Name = test.getString("insurance_Cmp_Name");

                                InsuranceCompanyName.add(insurance_Cmp_Name);
                                InsuranceId.add(insurance_Cmp_ID);

                                if(InsuranceCompany.equals(insurance_Cmp_Name))
                                {
                                position = j;
                                }
                            }
                            ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, R.id.text1, InsuranceCompanyName);
                            SP_InsuranceCName.setAdapter(adapterCategories);
                            SP_InsuranceCName.setSelection(position);
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