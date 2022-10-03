package com.ochs.app.flexiicar_app.selfcheckout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.flexiicar_app.user.User_Profile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Fragment_Vehicle_Image_1 extends Fragment
{
    ImageView imgback,UploadImg;
    private static final int RESULT_LOAD_IMAGE = 1;
    public String id="";
    JSONArray ImageList = new JSONArray();
    TextView txt_DateTime,txtDiscard;
    Bundle AgreementsBundle;
    Boolean BackTo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vehicle_images_1, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ((User_Profile) getActivity()).BottomnavInVisible();

        try{
            txt_DateTime=view.findViewById(R.id.txt_DateTimeForPic);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
            String datetime = dateformat.format(c.getTime());
            txt_DateTime.setText(datetime);

            LinearLayout lblNext=(LinearLayout) view.findViewById(R.id.Next);

            imgback=view.findViewById(R.id.Back);
            UploadImg= view.findViewById(R.id.FrontImage_Upload);

            AgreementsBundle = getArguments().getBundle("AgreementsBundle");
            BackTo=getArguments().getBoolean("BackTo");

            if(BackTo)
            {
                ImageList = new JSONArray(AgreementsBundle.getString("ImageList"));
                if (ImageList.length() > 0)
                {
                    final String image1 = ((JSONObject) (ImageList.get(0))).getString("fileBase64");
                    File imgFile = new File(image1);
                    if (imgFile.exists())
                    {
                        Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(image1), 300, 300, true);
                        UploadImg.setImageBitmap(myBitmap);
                        RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT);
                        UploadImg.setLayoutParams(parms);
                    }
                }
            }

            imgback.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle Agreements = new Bundle();
                    Agreements.putBundle("AgreementsBundle", AgreementsBundle);
                    NavHostFragment.findNavController(Fragment_Vehicle_Image_1.this)
                            .navigate(R.id.action_Vehicle_Image_1_to_Self_check_Out, Agreements);
                }
            });

            UploadImg.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                }
            });

            lblNext.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    AgreementsBundle.putString("ImageList", ImageList.toString());
                    Bundle Agreements = new Bundle();
                    Agreements.putBundle("AgreementsBundle", AgreementsBundle);
                    System.out.println(Agreements);
                    NavHostFragment.findNavController(Fragment_Vehicle_Image_1.this)
                            .navigate(R.id.action_Vehicle_img_1_to_Vehicle_img_2, Agreements);
                }
            });

            txtDiscard = view.findViewById(R.id.lbl_Discard);
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
                                    NavHostFragment.findNavController(Fragment_Vehicle_Image_1.this)
                                            .navigate(R.id.action_Vehicle_Image_1_to_User_Details);
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

        }catch (Exception e)
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
            Bitmap bitmap = null;
            try
            {
                bitmap = getScaledBitmap(selectedImage,400,400);
                JSONObject docObj = new JSONObject();
                docObj.put("Doc_For",9);
                docObj.put("VhlPictureSide", 1);
                docObj.put("fileBase64",getImagePathFromUri(selectedImage) );
                ImageList.put(docObj);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            UploadImg.setImageBitmap(bitmap);
            RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            UploadImg.setLayoutParams(parms);
        }
    }

    public String getImagePathFromUri(Uri contentUri)
    {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst())
        {
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

        if (height > reqHeight || width > reqWidth)
        {
            // Calculate ratios of height and width to requested one
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
