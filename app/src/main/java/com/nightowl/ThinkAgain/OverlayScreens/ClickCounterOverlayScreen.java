package com.nightowl.ThinkAgain.OverlayScreens;

import static android.content.Context.WINDOW_SERVICE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.nightowl.ThinkAgain.Classes.Alarm;
import com.nightowl.ThinkAgain.Database.DoNotOpenDatabase;
import com.nightowl.ThinkAgain.Database.SharedPref;
import com.nightowl.ThinkAgain.R;

import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;



public class ClickCounterOverlayScreen {


        // declaring required variables
        private Context context;
        private View mView;

        private int count = 0;

        private String data;
        private WindowManager.LayoutParams mParams;
        private WindowManager mWindowManager;
        private LayoutInflater layoutInflater;
        private String package_name;
        private Random random = new Random();


        private final int[] Gradient = {R.drawable.superman_gradient_bg,R.drawable.predawn_gradient_light,R.drawable.cheer_up_kid_gradient_light,
                R.drawable.nelson_gradient_light,R.drawable.midnight_city_gradinrt_dark,R.drawable.kashmir_gradient_light,R.drawable.mirage_gradient_dark,
                R.drawable.gradient_bg,R.drawable.royal_gradient_dark,R.drawable.vice_city_light};

        private DoNotOpenDatabase database;

        private CountDownTimer countDownTimer;

        public ClickCounterOverlayScreen(Context context){
            this.context=context;
            this.package_name = package_name;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // set the layout parameters of the window
                mParams = new WindowManager.LayoutParams(
                        // Shrink the window to wrap the content rather
                        // than filling the screen
                        WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                        // Display it on top of other application windows
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        // Don't let it grab the input focus
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        // Make the underlying application window visible
                        // through any transparent parts
                        PixelFormat.TRANSLUCENT);

            }
            // getting a LayoutInflater
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // inflating the view with the custom layout we created
            mView = layoutInflater.inflate(R.layout.click_counter_overlay, null);



            database = new DoNotOpenDatabase(context);

            ((TextView) mView.findViewById(R.id.counter_txt)).setText(String.valueOf(count));


            ((ImageView) mView.findViewById(R.id.exit_btn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPref pref = new SharedPref(context);
                    pref.AddCurrentApp(retriveNewApp());
                    Alarm alarm = new Alarm(context);
                    alarm.setAlarm();
                    close();
                }
            });

            ((AppCompatButton) mView.findViewById(R.id.up_btn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count = count + 1;
                    ((TextView) mView.findViewById(R.id.counter_txt)).setText(String.valueOf(count));

                    if (count == 500){
                        ((TextView) mView.findViewById(R.id.counter_txt)).setVisibility(View.INVISIBLE);
                        ((ImageView) mView.findViewById(R.id.exit_btn)).setVisibility(View.VISIBLE);
                        ((AppCompatButton) mView.findViewById(R.id.up_btn)).setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Click on exit button", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            android.view.animation.Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
            Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);

            ((TextView) mView.findViewById(R.id.notice_txt)).startAnimation(fadeOut);

            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    ((TextView) mView.findViewById(R.id.notice_txt)).startAnimation(fadeIn);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    ((TextView) mView.findViewById(R.id.notice_txt)).startAnimation(fadeOut);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            mParams.gravity = Gravity.CENTER;
            mWindowManager = (WindowManager)context.getSystemService(WINDOW_SERVICE);

        }

        @SuppressLint("SetTextI18n")
        public void open() {

            if(mView.getWindowToken()==null) {
                if(mView.getParent()==null) {
                    mWindowManager.addView(mView, mParams);


                    String messag_1 = retriveNewApp();


                    try{

                        Cursor cur = database.GetAppData(messag_1,context);

                        if (cur.getCount() != 0){

                            cur.moveToFirst();

                            data = cur.getString(2).toString();


                            ((TextView) mView.findViewById(R.id.notice_txt)).setText(data);

                        }

                    }catch (Exception e){

                        ((TextView) mView.findViewById(R.id.notice_txt)).setText("“A person who aims at nothing is sure to hit it.”");

                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    ((ImageView) mView.findViewById(R.id.exit_btn)).setVisibility(View.INVISIBLE);
                    ((TextView) mView.findViewById(R.id.counter_txt)).setVisibility(View.VISIBLE);


                    int n = random.nextInt(9);

                    ((ConstraintLayout) mView.findViewById(R.id.layout_1)).setBackground(context.getResources().getDrawable(Gradient[n]));

                }
            }

        }

        public void close() {

            try {
                // remove the view from the window
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ((WindowManager)context.getSystemService(WINDOW_SERVICE)).removeView(mView);
                }
                // invalidate the view

                mView.invalidate();
                // remove all views
                ((ViewGroup)mView.getParent()).removeAllViews();


                // the above steps are necessary when you are adding and removing
                // the view simultaneously, it might give some exceptions
            } catch (Exception e) {
                Log.d("Error2",e.getMessage());
            }
        }


        private String retriveNewApp() {

            if (Build.VERSION.SDK_INT >= 21) {
                String currentApp = null;
                UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                long time = System.currentTimeMillis();
                List<UsageStats> applist = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
                if (applist != null && applist.size() > 0) {
                    SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                    for (UsageStats usageStats : applist) {
                        mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                    }
                    if (mySortedMap != null && !mySortedMap.isEmpty()) {
                        currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    }
                }
                Log.e("TAG HAI", "Current App in 1st block foreground is: " + currentApp);

                return currentApp;

            }
            else {

                ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                String mm=(manager.getRunningTasks(1).get(0)).topActivity.getPackageName();
                Log.e("TAG HAI", "Current App in foreground is: " + mm);
                return mm;
            }
        }

    }
