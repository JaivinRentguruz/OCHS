package com.ochs.app.flexiicar_app.selfcheckout;

import android.Manifest;
import android.content.Context;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import com.ochs.app.flexiicar_app.selfcheckin.Fragment_Waiver_Signature_For_SelfCheckIn;
import com.ochs.app.flexiicar_app.user.User_Profile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_CHECKOUT;
import static com.ochs.app.apicall.ApiEndPoint.GETSELFCHECKOUT;

public class Fragment_Self_Check_out extends Fragment implements View.OnClickListener
{
    private static final int RECORD_AUDIO = 100;
    LinearLayout linearLayout;
    ImageView imgback,menu_Icon,Speaker;
    Button StartRecord;
    ImageView imageViewStop,imageViewPlay;
    Chronometer chronometer;
    private int lastProgress = 0;
    private boolean isPlaying = false;
    Handler handler = new Handler();
    public static Context context;
    TextView txt_OdoMeter, txt_progressvalue,txtDiscard;
    SeekBar AudiorecordSeekbar,customSeekBar;
    private MediaPlayer mPlayer=new MediaPlayer();
    private MediaRecorder mRecorder;
    private String fileName;
    EditText Edt_Notes;
    Bundle AgreementsBundle;
    ArrayList<String> SelectedOptionsId=new ArrayList<>();
    String SelectedOptions;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_self_chekout, container, false);
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        try {
            super.onViewCreated(view, savedInstanceState);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            ((User_Profile) getActivity()).BottomnavInVisible();

            AgreementsBundle = getArguments().getBundle("AgreementsBundle");

            linearLayout = view.findViewById(R.id.backlbl_Selfcheckout);
            imgback = view.findViewById(R.id.backimg_selfcheckout);
            customSeekBar = view.findViewById(R.id.simpleSeekBar);

            txt_OdoMeter = view.findViewById(R.id.txt_OdoMeter);
            txt_progressvalue = view.findViewById(R.id.txt_progressvalue);
            Edt_Notes=view.findViewById(R.id.Edt_Notes);

            Speaker=view.findViewById(R.id.Speaker);
            menu_Icon=view.findViewById(R.id.menu_Icon);
            AudiorecordSeekbar=view.findViewById(R.id.SeekBar);
            chronometer=view.findViewById(R.id.cronometer);

            StartRecord=view.findViewById(R.id.Startrecord);
            imageViewStop=view.findViewById(R.id.imageViewStop);
            imageViewPlay=view.findViewById(R.id.imageViewPlay);

            StartRecord.setOnClickListener(this);
            imageViewStop.setOnClickListener(this);
            imageViewPlay.setOnClickListener(this);

            imgback.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Bundle Agreements=new Bundle();
                    Agreements.putBundle("AgreementsBundle",AgreementsBundle);
                    NavHostFragment.findNavController(Fragment_Self_Check_out.this)
                            .navigate(R.id.action_Self_check_Out_to_SummaryOfChargesForAgreements,Agreements);
                }
            });

            txtDiscard=view.findViewById(R.id.Discard_SelfCheckout);

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
                                    NavHostFragment.findNavController(Fragment_Self_Check_out.this)
                                            .navigate(R.id.action_Self_check_Out_to_User_Details);
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

            AssetManager am = getActivity().getApplicationContext().getAssets();
            Typeface typefaceFSSiena = Typeface.createFromAsset(am,
                    String.format(Locale.US, "fonts/%s", "DS-DIGI.TTF"));
            txt_OdoMeter.setTypeface(typefaceFSSiena);

            AndroidNetworking.initialize(getActivity());
            Fragment_Self_Check_out.context = getActivity();

            String bodyParam = "";

            try
            {
                bodyParam += "AgreementId=" + AgreementsBundle.getInt("agreement_ID");
                System.out.println(bodyParam);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            ApiService ApiService = new ApiService(GetSelfCheckOut, RequestType.GET,
                    GETSELFCHECKOUT, BASE_URL_CHECKOUT, new HashMap<String, String>(), bodyParam);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    OnResponseListener GetSelfCheckOut = new OnResponseListener()
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
                                if(resultSet.equals("t0220_Vehicle_Damage_CheckList"))
                                {
                                    JSONObject Vehicle_Damage_CheckList = resultSet.getJSONObject("t0220_Vehicle_Damage_CheckList");
                                    final String checkListOptionIDS = Vehicle_Damage_CheckList.getString("checkListOptionIDS");
                                    final String checkListOtherDamage = Vehicle_Damage_CheckList.getString("checkListOtherDamage");
                                    final int type = Vehicle_Damage_CheckList.getInt("type");
                                }
                                //selfCheckOutModel
                                JSONObject selfCheckOutModel=resultSet.getJSONObject("selfCheckOutModel");
                                final int selfCheckOut = selfCheckOutModel.getInt("selfCheckOut");
                                final int vehicleID = selfCheckOutModel.getInt("vehicleId");
                                final int agreementId = selfCheckOutModel.getInt("agreementId");
                                final String odometerOut = selfCheckOutModel.getString("odometerOut");
                                final int gasTank=selfCheckOutModel.getInt("gasTank");
                                final String imageList=selfCheckOutModel.getString("imageList");

                                String s = String.valueOf(gasTank);
                                txt_progressvalue.setText(s+"%");

                                customSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                                {
                                    public void onProgressChanged (SeekBar seekBar,int progress, boolean fromUser)
                                    {
                                        try
                                        {
                                            txt_progressvalue.setText(String.valueOf(progress+"%"));
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

                                txt_OdoMeter.setText(odometerOut);

                                linearLayout.setOnClickListener(new View.OnClickListener()
                                {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onClick(View v)
                                    {
                                        String StrSelectedOptionsId = String.join(",", SelectedOptionsId);

                                        if(Edt_Notes.getText().toString().equals(""))
                                        {
                                            CustomToast.showToast(getActivity(),"Please Enter Notes.",1);
                                        }
                                        else if(txt_progressvalue.getText().toString().equals(""))
                                        {
                                            CustomToast.showToast(getActivity(),"Please select gas tank fuel level.",1);
                                        }
                                        else {
                                            AgreementsBundle.putString("AudioFileBase64", fileName);
                                            AgreementsBundle.putString("Notes", Edt_Notes.getText().toString());
                                            AgreementsBundle.putString("gasTank", txt_progressvalue.getText().toString());
                                            AgreementsBundle.putString("CheckListOptionIds",StrSelectedOptionsId);
                                            Bundle Agreements = new Bundle();
                                            Agreements.putBundle("AgreementsBundle", AgreementsBundle);
                                            Agreements.putBoolean("BackTo",false);
                                            System.out.println(Agreements);
                                            NavHostFragment.findNavController(Fragment_Self_Check_out.this)
                                                    .navigate(R.id.action_Self_check_Out_to_Vehicle_Image_1, Agreements);
                                        }
                                    }
                                });

                                //checkListOptMasterModel
                                final JSONArray getcheckListOptMasterModel = resultSet.getJSONArray("checkListOptMasterModel");
                                final RelativeLayout rl_Accessories = getActivity().findViewById(R.id.rl_Accessories);

                                int len;
                                len = getcheckListOptMasterModel.length();

                                for (int j = 0; j < len; j++)
                                {
                                    final JSONObject test = (JSONObject) getcheckListOptMasterModel.get(j);

                                    final String chkOptionId = test.getString("chkOptionId");
                                    final String chkOptionName = test.getString("chkOptionName");
                                    String[] optionIds = chkOptionId.split(",");
                                    //  System.out.println(Arrays.toString(optionIds));

                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                    lp.setMargins(0, 0, 0, 0);

                                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    LinearLayout view = (LinearLayout) inflater.inflate(R.layout.list_selfcheckout, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                    view.setId(200 + j);
                                    view.setLayoutParams(lp);

                                    TextView txt_chkOptionName;
                                    CheckBox chkred = (CheckBox) view.findViewById(R.id.chk_red);
                                    CheckBox chkgreen = (CheckBox) view.findViewById(R.id.chk_green);
                                    CheckBox chkyellow = (CheckBox) view.findViewById(R.id.chk_yellow);
                                    // ArrayList<String> al=new Arraylist();
                                    // al.add(checkbox.getId());

                                    for (int m = 0; m < optionIds.length; m++)
                                    {
                                        for(int k = 0; k<=m; k++)
                                        {
                                            String chk=chkOptionId+"#1";

                                            chkgreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                                            {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                                                {
                                                    if (isChecked)
                                                    {
                                                        chkred.setChecked(false);
                                                        chkyellow.setChecked(false);

                                                        for (int i=0;i<chk.length();i++);
                                                        {
                                                            SelectedOptionsId.add(chk);
                                                            System.out.println(SelectedOptionsId);
                                                        }
                                                    }
                                                    else {
                                                        for (int i=0;i<chk.length();i++);
                                                        {
                                                            SelectedOptionsId.remove(chk);
                                                            System.out.println(SelectedOptionsId);
                                                        }
                                                    }
                                                }
                                            });

                                            String chk2=chkOptionId+"#2";
                                            chkyellow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                                            {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                                                {
                                                    if (isChecked)
                                                    {
                                                        chkred.setChecked(false);
                                                        chkgreen.setChecked(false);

                                                        for (int i=0;i<chk2.length();i++);
                                                        {
                                                            SelectedOptionsId.add(chk2);
                                                            System.out.println(SelectedOptionsId);
                                                        }
                                                    }
                                                    else
                                                    {
                                                        for (int i=0;i<chk2.length();i++);
                                                        {
                                                            SelectedOptionsId.remove(chk2);
                                                            System.out.println(SelectedOptionsId);
                                                        }
                                                    }
                                                }
                                            });

                                            String chk3=chkOptionId+"#3";
                                            chkred.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                                            {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                                                {
                                                    if (isChecked)
                                                    {
                                                        chkgreen.setChecked(false);
                                                        chkyellow.setChecked(false);

                                                        for (int i=0;i<chk3.length();i++);
                                                        {
                                                            SelectedOptionsId.add(chk3);
                                                            System.out.println(SelectedOptionsId);
                                                        }
                                                    }
                                                    else {
                                                        for (int i=0;i<chk3.length();i++);
                                                        {
                                                            SelectedOptionsId.remove(chk3);
                                                            System.out.println(SelectedOptionsId);
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    txt_chkOptionName = (TextView) view.findViewById(R.id.txt_chkOptionName);
                                    txt_chkOptionName.setText(chkOptionName);

                                    rl_Accessories.addView(view);
                                }
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

    @Override
    public void onClick(View view)
    {
        try {
            if (view == StartRecord)
            {
                prepareforRecording();
                startRecording();
            } else if (view == imageViewStop)
            {
                prepareforStop();
                stopRecording();
            } else if (view == imageViewPlay)
            {
                if (!isPlaying && fileName != null)
                {
                    isPlaying = true;
                    startPlaying();
                } else {
                    isPlaying = false;
                    stopPlaying();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void prepareforRecording()
    {
        imageViewStop.setVisibility(View.VISIBLE);
        imageViewStop.setImageResource(R.drawable.audio_resume);
        imageViewPlay.setVisibility(View.GONE);
    }

    private void startRecording()
    {
        try {
            //we use the MediaRecorder class to record
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Recorder");
            try{
                if(dir.mkdir())
                {
                    System.out.println("Directory created");
                } else {
                    System.out.println("Directory alreadt exist");
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
            fileName=dir + "/recorded_" + System.currentTimeMillis() +".mp3";
            mRecorder.setOutputFile(fileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
                mRecorder.start();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            lastProgress = 0;
            AudiorecordSeekbar.setProgress(0);
            stopPlaying();
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void stopPlaying()
    {
        try {
            if (mPlayer != null)
            {
                if (mPlayer.isPlaying())
                {
                    mPlayer.stop();
                    mPlayer.release();
                }
                mPlayer.release();
                mPlayer=null;
                imageViewPlay.setImageResource(R.drawable.audio_resume);
                chronometer.stop();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void prepareforStop()
    {
        imageViewStop.setVisibility(View.GONE);
        imageViewPlay.setVisibility(View.VISIBLE);
    }

    private void stopRecording()
    {
        try{
            mRecorder.stop();
            mRecorder.release();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        mRecorder = null;
        //starting the chronometer
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        //showing the play button
        CustomToast.showToast(getActivity(),"Recording saved successfully.",1);
    }

    private void startPlaying()
    {
        mPlayer = new MediaPlayer();
        Log.d("instartPlaying",fileName);
        try
        {
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare() failed");
        }
        //making the imageview pause button
        imageViewPlay.setImageResource(R.drawable.audio_pause);
        AudiorecordSeekbar.setProgress(lastProgress);
        mPlayer.seekTo(lastProgress);
        AudiorecordSeekbar.setMax(mPlayer.getDuration());
        seekUpdation();
        chronometer.start();

        /** once the audio is complete, timer is stopped here**/
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                imageViewPlay.setImageResource(R.drawable.audio_resume);
                isPlaying = false;
                chronometer.stop();
            }
        });

        /** moving the track as per the seekBar's position**/
        AudiorecordSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if( mPlayer!=null && fromUser )
                {
                    mPlayer.seekTo(progress);
                    chronometer.setBase(SystemClock.elapsedRealtime() - mPlayer.getCurrentPosition());
                    lastProgress = progress;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
    }

    Runnable runnable = new Runnable()
    {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    private void seekUpdation()
    {
        if(mPlayer != null)
        {
            int mCurrentPosition = mPlayer.getCurrentPosition() ;
            AudiorecordSeekbar.setProgress(mCurrentPosition);
            lastProgress = mCurrentPosition;
        }
        handler.postDelayed(runnable, 100);
    }
}
//   File root =  Environment.getExternalStorageDirectory();
// outFilename = Environment.getExternalStorageDirectory().getPath();
          /*  File file = new File(root.getAbsolutePath()+ "/VoiceRecorderSimplifiedCoding/Audios");
            if (!file.exists())
            {
                file.mkdirs();
            }*/
// File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
           /* fileName =root.getAbsolutePath()+"/VoiceRecorderSimplifiedCoding/Audios/"+ String.valueOf(System.currentTimeMillis() + ".mp3");
            CustomToast.showToast(getActivity(),"filename"+fileName,1);
            Log.d("filename", fileName);*/

// fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyRecording.mp3";
//  mRecorder.setOutputFile(fileName);
