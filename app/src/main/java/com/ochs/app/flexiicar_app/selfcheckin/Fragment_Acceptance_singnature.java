package com.ochs.app.flexiicar_app.selfcheckin;

import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.flexiicar_app.user.User_Profile;
import com.github.gcacace.signaturepad.views.SignaturePad;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class Fragment_Acceptance_singnature extends Fragment
{
    ImageView imgback;
    SignaturePad signaturePad;
    TextView lbl_clearsign,lbl_Savesign,lblNext;
    CheckBox checkBoxTC;
    Bundle AgreementsBundle;
    JSONArray ImageList = new JSONArray();
    Bitmap bitmap;
    String path;
    private static final String IMAGE_DIRECTORY ="/signdemo";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_acceptance_signature, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ((User_Profile) getActivity()).BottomnavInVisible();

        lblNext = view.findViewById(R.id.lblNext);
        imgback = view.findViewById(R.id.Back);
        signaturePad = view.findViewById(R.id.signaturePad);
        lbl_clearsign = view.findViewById(R.id.lbl_clearsign);
        checkBoxTC = view.findViewById(R.id.CheckBoxTC);
        lbl_Savesign = view.findViewById(R.id.lbl_Savesign);

        lbl_clearsign.setEnabled(false);

        try {
            AgreementsBundle = getArguments().getBundle("AgreementsBundle");
            ImageList = new JSONArray(AgreementsBundle.getString("ImageList"));

            lbl_Savesign.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    bitmap = signaturePad.getSignatureBitmap();
                    path = saveImage(bitmap);
                    try {
                        JSONObject signObj = new JSONObject();
                        signObj.put("Doc_For", 22);
                        signObj.put("fileBase64", path);
                        System.out.println(signObj);
                        ImageList.put(signObj);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    System.out.println(path);
                }
            });
            lblNext.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    try{
                        if(checkBoxTC.isChecked())
                        {
                            AgreementsBundle.putString("ImageList", ImageList.toString());
                            Bundle SelfCheckInBundle = new Bundle();
                            SelfCheckInBundle.putBundle("AgreementsBundle", AgreementsBundle);
                            System.out.println(SelfCheckInBundle);
                            NavHostFragment.findNavController(Fragment_Acceptance_singnature.this)
                                    .navigate(R.id.action_Signature_to_Self_check_In, SelfCheckInBundle);
                        }
                        else {
                            String msg = "Please accept term & condition";
                            CustomToast.showToast(getActivity(),msg,1);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            });

            imgback.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    AgreementsBundle.putString("ImageList", ImageList.toString());
                    Bundle SelfCheckInBundle=new Bundle();
                    SelfCheckInBundle.putBundle("AgreementsBundle",AgreementsBundle);
                    System.out.println(SelfCheckInBundle);
                    NavHostFragment.findNavController(Fragment_Acceptance_singnature.this)
                            .navigate(R.id.action_Signature_to_Vehicle_All_image_SelfCheckIn,SelfCheckInBundle);
                }
            });

            signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener()
            {
                @Override
                public void onStartSigning()
                {
                }
                @Override
                public void onSigned()
                {
                    lbl_clearsign.setEnabled(true);
                    signaturePad.setEnabled(true);
                }

                @Override
                public void onClear()
                {
                    lbl_clearsign.setEnabled(false);
                }
            });

            lbl_clearsign.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    signaturePad.clear();
                }
            });

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String saveImage(Bitmap myBitmap)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists())
        {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();

            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getActivity(),
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            CustomToast.showToast(getActivity(),"File Saved::--->"+f.getAbsolutePath(),1);

            Uri SignImage = Uri.fromFile(f);

            return f.getAbsolutePath();
        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e1)
        {
            e1.printStackTrace();
        }
        return "";
    }
}
