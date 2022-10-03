package com.ochs.app.flexiicar_app.selfcheckout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.flexiicar_app.user.User_Profile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_Vehicle_Additional_Image_All extends Fragment
{
    LinearLayout llNext;
    ImageView imgback,Veh_Image1,Veh_Image2,Veh_Image3,Veh_Image4,Veh_Image5,Veh_Image6,
            Veh_Image7,Veh_Image8,Veh_Image9,Veh_Image10;
    JSONArray ImageList = new JSONArray();
    public String id="";
    public static Context context;
    Bundle AgreementsBundle;
    TextView txt_DateTime,txtDiscard;
    RadioButton radiobtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_vehicle_images_additional_all, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
       try {
           super.onViewCreated(view, savedInstanceState);
           getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
           ((User_Profile) getActivity()).BottomnavInVisible();

           txt_DateTime = view.findViewById(R.id.text_DateTime12);

           Calendar c = Calendar.getInstance();
           SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
           String datetime = dateformat.format(c.getTime());
           txt_DateTime.setText(datetime);

           llNext = view.findViewById(R.id.backNext_additionalimg);
           radiobtn= view.findViewById(R.id.radiobtn);

           Veh_Image1 = view.findViewById(R.id.car_image_1);
           Veh_Image2 = view.findViewById(R.id.car_image_2);
           Veh_Image3 = view.findViewById(R.id.car_image_3);
           Veh_Image4 = view.findViewById(R.id.car_image_4);
           Veh_Image5 = view.findViewById(R.id.car_image_5);
           Veh_Image6 = view.findViewById(R.id.car_image_6);
           Veh_Image7 = view.findViewById(R.id.car_image_7);
           Veh_Image8 = view.findViewById(R.id.car_image_8);
           Veh_Image9 = view.findViewById(R.id.car_image_9);
           Veh_Image10 = view.findViewById(R.id.car_image_10);
           imgback = view.findViewById(R.id.Back);

           SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
           id = sp.getString(getString(R.string.id), "");

           try {
               AgreementsBundle = getArguments().getBundle("AgreementsBundle");
               ImageList = new JSONArray(AgreementsBundle.getString("ImageList"));

               if (ImageList.length() > 0)
               {

                   final String doc_Details = ((JSONObject) (ImageList.get(0))).getString("fileBase64");
                   File imgFile = new File(doc_Details);
                   if (imgFile.exists())
                   {
                       Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(doc_Details), 300, 300, true);
                       Veh_Image1.setImageBitmap(myBitmap);
                   }
               }

               if (ImageList.length() > 1)
               {
                   final String doc_Details = ((JSONObject) (ImageList.get(1))).getString("fileBase64");

                   File imgFile = new File(doc_Details);

                   if (imgFile.exists())
                   {
                       Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(doc_Details), 300, 300, true);
                       Veh_Image2.setImageBitmap(myBitmap);
                   }
               }
               if (ImageList.length() > 2)
               {
                   final String doc_Details = ((JSONObject) (ImageList.get(2))).getString("fileBase64");

                   File imgFile = new File(doc_Details);

                   if (imgFile.exists())
                   {
                       Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(doc_Details), 300, 300, true);
                       Veh_Image3.setImageBitmap(myBitmap);
                   }
               }
               if (ImageList.length() > 3)
               {
                   final String doc_Details = ((JSONObject) (ImageList.get(3))).getString("fileBase64");

                   File imgFile = new File(doc_Details);

                   if (imgFile.exists()) {
                       Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(doc_Details), 300, 300, true);
                       Veh_Image4.setImageBitmap(myBitmap);
                   }
               }
               if (ImageList.length() > 4) {
                   final String doc_Details = ((JSONObject) (ImageList.get(4))).getString("fileBase64");

                   File imgFile = new File(doc_Details);

                   if (imgFile.exists()) {
                       Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(doc_Details), 300, 300, true);
                       Veh_Image5.setImageBitmap(myBitmap);
                   }
               }
               if (ImageList.length() > 5) {
                   final String doc_Details = ((JSONObject) (ImageList.get(5))).getString("fileBase64");

                   File imgFile = new File(doc_Details);

                   if (imgFile.exists()) {
                       Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(doc_Details), 300, 300, true);
                       Veh_Image6.setImageBitmap(myBitmap);
                   }
               }
               if (ImageList.length() > 6) {
                   final String doc_Details = ((JSONObject) (ImageList.get(6))).getString("fileBase64");

                   File imgFile = new File(doc_Details);

                   if (imgFile.exists()) {
                       Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(doc_Details), 300, 300, true);
                       Veh_Image7.setImageBitmap(myBitmap);
                   }
               }
               if (ImageList.length() > 7) {
                   final String doc_Details = ((JSONObject) (ImageList.get(7))).getString("fileBase64");

                   File imgFile = new File(doc_Details);

                   if (imgFile.exists()) {
                       Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(doc_Details), 300, 300, true);
                       Veh_Image8.setImageBitmap(myBitmap);
                   }
               }
               if (ImageList.length() > 8)
               {
                   final String doc_Details = ((JSONObject) (ImageList.get(8))).getString("fileBase64");

                   File imgFile = new File(doc_Details);

                   if (imgFile.exists()) {
                       Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(doc_Details), 300, 300, true);
                       Veh_Image9.setImageBitmap(myBitmap);
                   }
               }
               if (ImageList.length() > 9)
               {
                   final String doc_Details = ((JSONObject) (ImageList.get(9))).getString("fileBase64");

                   File imgFile = new File(doc_Details);

                   if (imgFile.exists())
                   {
                       Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(doc_Details), 300, 300, true);
                       Veh_Image10.setImageBitmap(myBitmap);
                   }
               }
               llNext.setOnClickListener(new View.OnClickListener()
               {
                   @Override
                   public void onClick(View v)
                   {
                       if(radiobtn.isChecked())
                       {
                           AgreementsBundle.putString("ImageList", ImageList.toString());
                           Bundle Agreements = new Bundle();
                           Agreements.putBundle("AgreementsBundle", AgreementsBundle);
                           System.out.println(Agreements);
                           NavHostFragment.findNavController(Fragment_Vehicle_Additional_Image_All.this)
                                   .navigate(R.id.action_Vehicle_Additional_image_to_Acceptance_signature, Agreements);
                       }
                       else
                       {
                           CustomToast.showToast(getActivity(),"Please Select Send Waiver Signature",1);
                       }
                   }
               });

               imgback.setOnClickListener(new View.OnClickListener()
               {
                   @Override
                   public void onClick(View v)
                   {
                       AgreementsBundle.putString("ImageList", ImageList.toString());
                       Bundle Agreements = new Bundle();
                       Agreements.putBundle("AgreementsBundle", AgreementsBundle);
                       System.out.println(Agreements);
                       NavHostFragment.findNavController(Fragment_Vehicle_Additional_Image_All.this)
                               .navigate(R.id.action_Vehicle_Additional_image_to_Vehicle_Image_10, Agreements);
                   }
               });

               txtDiscard = view.findViewById(R.id.lblDiscard);

               txtDiscard.setOnClickListener(new View.OnClickListener()
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
                                       NavHostFragment.findNavController(Fragment_Vehicle_Additional_Image_All.this)
                                               .navigate(R.id.action_Vehicle_Additional_image_to_User_Details);
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

           } catch (Exception e)
           {
               e.printStackTrace();
           }
       }catch (Exception e)
       {
           e.printStackTrace();
       }
    }
}
