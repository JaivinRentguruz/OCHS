package com.ochs.app.flexiicar_app.login;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ochs.app.R;
import com.ochs.app.RentOption;
import com.ochs.app.SplashScreen;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OnBoarding extends AppCompatActivity
{
    SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    ImageView zero, one, two;
    ImageView[] indicators;

    int page = 0;

    private GestureDetector gestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    static ImageLoader imageLoader;
    SharedPreferences sp;
    String serverpath="";
    public String id = "";
    static ImageView section_logo_img;
    JSONObject obj;
    static String header1,header2,header3;
    static String description1,description2,description3;
    static String logo1,logo2, logo3;
    static String icon1,icon2, icon3;
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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            getWindow().setStatusBarColor(getResources().getColor(R.color.onBoardingScreen1bg));
        }

        setContentView(R.layout.activity_on_boarding);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        initImageLoader(this);
        imageLoader = ImageLoader.getInstance();

        sp = getSharedPreferences("FlexiiCar", MODE_PRIVATE);
        serverpath = sp.getString("serverPath","");
        id = sp.getString(getString(R.string.id), "");
        System.out.println("serverpath"+serverpath);

        section_logo_img = findViewById(R.id.section_logo_img);

        try {
            JSONObject object = SplashScreen.introslider.getJSONObject("resultSet");
            JSONArray array = object.getJSONArray("featureCMs");
            for (int i = 0; i <array.length() ; i++) {
                obj =  array.getJSONObject(i);
                String logoPath = obj.getString("logoPath");
                String url1 = serverpath+logoPath.substring(2);
                String icon = obj.getString("iconPath");
                String url2 = serverpath+icon.substring(2);
               // imageLoader.getInstance().displayImage(url1,section_logo_img);

          /*      if (i==0){
                    header1 = obj.getString("heading");

                } */
                switch (i){
                    case 0:
                        header1 = obj.getString("heading");
                        description1 = obj.getString("description");
                        logo1 = url1;
                        icon1 = url2;
                        break;
                    case 1:
                        header2 = obj.getString("heading");
                        description2 = obj.getString("description");
                        logo2 = url1;
                        icon2 = url2;
                        break;
                    case 2:
                        header3 = obj.getString("heading");
                        description3 = obj.getString("description");
                        logo3 = url1;
                        icon3 = url2;
                        break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        zero = (ImageView) findViewById(R.id.intro_indicator_0);
        one = (ImageView) findViewById(R.id.intro_indicator_1);
        two = (ImageView) findViewById(R.id.intro_indicator_2);
        indicators = new ImageView[]{zero, one, two};

        mViewPager = findViewById(R.id.onboarding_view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(page);
        updateIndicators(page);

        final int color1 = getResources().getColor(R.color.screen_bg_color);
        final int color2 = getResources().getColor(R.color.screen_bg_color);
        final int color3 = getResources().getColor(R.color.screen_bg_color);

        final int[] colorList = new int[]{color1, color2, color3};

        final ArgbEvaluator evaluator = new ArgbEvaluator();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

                /*
                color update
                 */
                int colorUpdate = (Integer) evaluator.evaluate(positionOffset, colorList[position], colorList[position == 2 ? position : position + 1]);
                mViewPager.setBackgroundColor(colorUpdate);

            }

            @Override
            public void onPageSelected(int position)
            {

                page = position;

                updateIndicators(page);

                switch (position)
                {
                    case 0:
                        mViewPager.setBackgroundColor(color1);
                        getWindow().setStatusBarColor(getResources().getColor(R.color.screen_bg_color));
                        break;
                    case 1:
                        mViewPager.setBackgroundColor(color2);
                        getWindow().setStatusBarColor(getResources().getColor(R.color.screen_bg_color));
                        break;
                    case 2:
                        mViewPager.setBackgroundColor(color3);
                        getWindow().setStatusBarColor(getResources().getColor(R.color.screen_bg_color));
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

        gestureDetector = new GestureDetector( this, new SwipeDetector());
    }

    //protected abstract void onSwipeRight();
    //protected abstract void onSwipeLeft();

    public class SwipeDetector extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {

            if(page == 2) {
                // Check movement along the Y-axis. If it exceeds SWIPE_MAX_OFF_PATH,
                // then dismiss the swipe.
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                {
                    return false;
                }

                //toast( "start = "+String.valueOf( e1.getX() )+" | end = "+String.valueOf( e2.getX() )  );
                //from left to right
                if (e2.getX() > e1.getX()) {
                    if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        //onSwipeRight();


                        return true;
                    }
                }

                if (e1.getX() > e2.getX())
                {
                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        //onSwipeLeft();

                        if (page == 2)
                        {
                            Intent intent = new Intent(OnBoarding.this, Login.class);
                            startActivity(intent);
                        }

                        return true;
                    }
                }

                return false;
            }
            return  true;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        // TouchEvent dispatcher.
        if (gestureDetector != null && page == 2)
        {
            if (gestureDetector.onTouchEvent(ev))
                // If the gestureDetector handles the event, a swipe has been
                // executed and no more needs to be done.
                return true;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return gestureDetector.onTouchEvent(event);
    }

    void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++)
        {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.selected_dot : R.drawable.default_dot
            );
        }
    }

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        ImageView img;

        int[] bgs = new int[]{R.drawable.terms_of_use_icon, R.drawable.refund_policy_icon, R.drawable.privacy_policy_icon};

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_on_boarding, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            TextView discription = (TextView) rootView.findViewById(R.id.section_discription);
            img = (ImageView) rootView.findViewById(R.id.section_img);

            int argument = getArguments().getInt(ARG_SECTION_NUMBER);
            if(argument == 1) {
                textView.setText(header1);
                discription.setText(description1);
                imageLoader.getInstance().displayImage(logo1,section_logo_img);
                imageLoader.getInstance().displayImage(icon1,img );
            }
            else if(argument == 2){
                textView.setText(header2);
                discription.setText(description2);
                imageLoader.getInstance().displayImage(logo2,section_logo_img);
                imageLoader.getInstance().displayImage(icon2,img );
            }
            else if(argument == 3) {
                textView.setText(header3);
                discription.setText(description3);
                imageLoader.getInstance().displayImage(logo3,section_logo_img);
                imageLoader.getInstance().displayImage(icon3,img );
            }


         //   img.setBackgroundResource(bgs[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);


            return rootView;
        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }

    }
}