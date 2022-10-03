package com.ochs.app.flexiicar_app.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.ochs.app.R;
import com.ochs.app.adapters.CustomToast;
import com.ochs.app.apicall.ApiService;
import com.ochs.app.apicall.OnResponseListener;
import com.ochs.app.apicall.RequestType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Color.*;
import static com.ochs.app.apicall.ApiEndPoint.ACTIVITYTIMELINELIST;
import static com.ochs.app.apicall.ApiEndPoint.BASE_URL_CUSTOMER;

public class Fragment_User_Timeline extends Fragment
{
    ImageView backarrow,AddUserTimeLine;
    public static Context context;
    public String id = "";
    EditText Edt_searchUsertimeline;
    Handler handler = new Handler();
    private HorizontalCalendar horizontalCalendar;
    RelativeLayout RlView;
    RelativeLayout rlActivity;
    RelativeLayout rl_ActivityTimeLineList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_user_timeline, container, false);
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            ((User_Profile) getActivity()).BottomnavVisible();

            SharedPreferences sp = getActivity().getSharedPreferences("FlexiiCar", MODE_PRIVATE);
            id = sp.getString(getString(R.string.id), "");

            AddUserTimeLine = view.findViewById(R.id.AddActivityimg);
            backarrow = view.findViewById(R.id.back_to_userprofile);
            Edt_searchUsertimeline = view.findViewById(R.id.Edt_searchUsertimeline);

            backarrow.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    NavHostFragment.findNavController(Fragment_User_Timeline.this)
                            .navigate(R.id.action_User_timeline_to_User_Details);
                }
            });
            AddUserTimeLine.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try {
                    NavHostFragment.findNavController(Fragment_User_Timeline.this)
                            .navigate(R.id.action_User_timeline_to_Poston_Your_Timeline);
                }
                    catch (Exception e)
                {
                    e.printStackTrace();
                }
                }
            });
// horizontalCalendar
            Calendar startDate = Calendar.getInstance();
            startDate.add(Calendar.MONTH, -12);

            /* end after 1 month from now */
            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.MONTH, 36);

            horizontalCalendar = new HorizontalCalendar.Builder(getActivity(), R.id.calendar_View)
                    .range(startDate, endDate)
                    .datesNumberOnScreen(7)
                    .configure()
                    .formatTopText("EEE")
                    .formatMiddleText("dd")
                    .showTopText(true)
                    .showBottomText(false)
                    .textColor(WHITE, WHITE)
                    .end()
                    .build();

            horizontalCalendar.setCalendarListener(new HorizontalCalendarListener()
            {
                @Override
                public void onDateSelected(Calendar date, int position)
                {
                    String selectedDateStr = DateFormat.format("yyyy-MM-dd", date).toString();
                     Toast.makeText(getActivity(),selectedDateStr+"",Toast.LENGTH_LONG).show();

                    JSONObject bodyParam = new JSONObject();
                    try {
                        bodyParam.accumulate("CustomerId", Integer.parseInt(id));
                        bodyParam.accumulate("SearchString", selectedDateStr);
                        bodyParam.accumulate("SearchBy", 1);
                        System.out.println("1"+bodyParam);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    ApiService ApiService = new ApiService(ActivityTimeLineList, RequestType.POST,
                            ACTIVITYTIMELINELIST, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);

             /*       rl_ActivityTimeLineList = getActivity().findViewById(R.id.rl_ActivityTimeLine);
                    rl_ActivityTimeLineList.removeAllViews();
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final RelativeLayout reservationlayout = (RelativeLayout) inflater.inflate(R.layout.dialog_progress, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                    rl_ActivityTimeLineList.addView(reservationlayout);*/
                }
            });

            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            String datetime = dateformat.format(c.getTime());

            JSONObject bodyParam = new JSONObject();
            try {
                bodyParam.accumulate("CustomerId", Integer.parseInt(id));
                bodyParam.accumulate("SearchString", datetime);
                bodyParam.accumulate("SearchBy", 1);
                System.out.println("2"+bodyParam);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            ApiService ApiService = new ApiService(ActivityTimeLineList, RequestType.POST,
                    ACTIVITYTIMELINELIST, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);

            rl_ActivityTimeLineList = getActivity().findViewById(R.id.rl_ActivityTimeLine);
            rl_ActivityTimeLineList.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final RelativeLayout reservationlayout = (RelativeLayout) inflater.inflate(R.layout.dialog_progress, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
            rl_ActivityTimeLineList.addView(reservationlayout);

// filter_IconForTimeline
            Edt_searchUsertimeline.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    JSONObject bodyParam = new JSONObject();
                    try {
                        bodyParam.accumulate("CustomerId", Integer.parseInt(id));
                        bodyParam.accumulate("SearchString", Edt_searchUsertimeline.getText().toString());
                        bodyParam.accumulate("SearchBy", 2);
                        System.out.println(bodyParam);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    ApiService ApiService = new ApiService(ActivityTimeLineList, RequestType.POST,
                            ACTIVITYTIMELINELIST, BASE_URL_CUSTOMER, new HashMap<String, String>(), bodyParam);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    OnResponseListener ActivityTimeLineList = new OnResponseListener()
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
                            try {
                                JSONObject resultSet = responseJSON.getJSONObject("resultSet");
                                final JSONArray getActivityTimeLine = resultSet.getJSONArray("v0040_Customer_Activity");
                                rlActivity = getActivity().findViewById(R.id.rl_ActivityTimeLine);
                                rlActivity.removeAllViews();
                                int len;
                                len = getActivityTimeLine.length();

                                if(getActivityTimeLine.length()>0)
                                {
                                    for (int j = 0; j < len; j++)
                                    {
                                        try {
                                            final JSONObject test = (JSONObject) getActivityTimeLine.get(j);

                                            final int activityID = test.getInt("activityID");
                                            final String activityDate = test.getString("activityDate");
                                            final int activityType = test.getInt("activityType");
                                            final String activityTitle = test.getString("activityTitle");
                                            final String description = test.getString("description");
                                            final int transID = test.getInt("transID");
                                            final int cmpID = test.getInt("cmpID");
                                            final String activityName = test.getString("activityName");
                                            final String activityColor = test.getString("activityColor");

                                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                            lp.addRule(RelativeLayout.BELOW, (200 + j - 1));
                                            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                            lp.setMargins(0, 0, 0, 0);

                                            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            final LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.list_activity_timeline, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                                            linearLayout.setId(200 + j);
                                            linearLayout.setLayoutParams(lp);

                                            TextView txt_Activityname, ActivityTime, ActivityDate, Activity_Desc, ActivityTitle;

                                            LinearLayout UpdateActivity = linearLayout.findViewById(R.id.post_on_timeline);
                                            LinearLayout linearLayoutBG = linearLayout.findViewById(R.id.LinearLayoutActivity);
                                            Activity_Desc = linearLayout.findViewById(R.id.Activity_Desc);
                                            ActivityTitle = linearLayout.findViewById(R.id.ActivityTitle);

                                            Activity_Desc.setText(description);
                                            ActivityTitle.setText(activityTitle);

                                            ImageView img = linearLayout.findViewById(R.id.circle_bg);
                                            img.setColorFilter(parseColor(activityColor), PorterDuff.Mode.SRC_IN);

                                            linearLayoutBG.setBackgroundTintList(ColorStateList.valueOf(parseColor(activityColor)));

                                            txt_Activityname = linearLayout.findViewById(R.id.lbl_ActivityName);
                                            ActivityTime = linearLayout.findViewById(R.id.lbl_ActivityTime);
                                            ActivityDate = linearLayout.findViewById(R.id.ActivityDate);
                                            RlView = linearLayout.findViewById(R.id.RlView);

                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                                            Date date = dateFormat.parse(activityDate);

                                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd  yyyy");
                                            String stractivityDate = sdf.format(date);

                                            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm aa", Locale.US);
                                            String ActivityTimeStr = sdf1.format(date);


                                            txt_Activityname.setText(activityName);
                                            ActivityTime.setText(ActivityTimeStr);
                                            ActivityDate.setText(stractivityDate);

                                            rlActivity.addView(linearLayout);

                                            UpdateActivity.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Bundle ActivityTimeLine = new Bundle();
                                                    ActivityTimeLine.putInt("activityID", activityID);
                                                    ActivityTimeLine.putString("activityDate", activityDate);
                                                    ActivityTimeLine.putInt("activityType", activityType);
                                                    ActivityTimeLine.putString("activityTitle", activityTitle);
                                                    ActivityTimeLine.putString("description", description);
                                                    ActivityTimeLine.putInt("transID", transID);
                                                    ActivityTimeLine.putInt("cmpID", cmpID);
                                                    ActivityTimeLine.putString("activityName", activityName);
                                                    Bundle ActivityBundle = new Bundle();
                                                    ActivityBundle.putBundle("ActivityTimeLine", ActivityTimeLine);
                                                    NavHostFragment.findNavController(Fragment_User_Timeline.this)
                                                            .navigate(R.id.action_User_timeline_to_Update_CutomerActivity, ActivityBundle);
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                 //   rl_ActivityTimeLineList.removeViewAt(0);
                                }

                                else {
                                    RlView.setVisibility(View.GONE);
                                }

                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                         //   rl_ActivityTimeLineList.removeViewAt(0);
                            rlActivity = getActivity().findViewById(R.id.rl_ActivityTimeLine);
                            rlActivity.removeAllViews();
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



