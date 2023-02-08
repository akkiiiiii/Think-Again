package com.nightowl.ThinkAgain.OverlayScreens;

import static android.content.Context.WINDOW_SERVICE;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
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

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.nightowl.ThinkAgain.Classes.Alarm;
import com.nightowl.ThinkAgain.Database.DoNotOpenDatabase;
import com.nightowl.ThinkAgain.Database.SharedPref;
import com.nightowl.ThinkAgain.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

public class DoNotUseApp {


        // declaring required variables
        private Context context;
        private View mView;

        private SharedPref pref;
        private WindowManager.LayoutParams mParams;
        private WindowManager mWindowManager;
        private LayoutInflater layoutInflater;
        private String package_name;
        private Random random = new Random();


        private final int[] Gradient = {R.drawable.superman_gradient_bg,R.drawable.predawn_gradient_light,R.drawable.cheer_up_kid_gradient_light,
        R.drawable.nelson_gradient_light,R.drawable.midnight_city_gradinrt_dark,R.drawable.kashmir_gradient_light,R.drawable.mirage_gradient_dark,
        R.drawable.gradient_bg,R.drawable.royal_gradient_dark,R.drawable.vice_city_light};

        private final int[] Animation = {R.raw.bicep,R.raw.coffe_booty,R.raw.confusing,
        R.raw.ghost_round,R.raw.sleeping_bear,R.raw.sleeping_puppy,R.raw.sleeping_squrell,
        R.raw.spread};

        private DoNotOpenDatabase database;

        private CountDownTimer countDownTimer;

        public DoNotUseApp(Context context){
            this.context=context;
            this.package_name = package_name;

            pref = new SharedPref(context);

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
            mView = layoutInflater.inflate(R.layout.overlay_remind_do_not_open_app, null);
            // set onClickListener on the remove button, which removes
            // the view from the window
//            mView.findViewById(R.id.window_close).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    close();
//                }
//            });


            database = new DoNotOpenDatabase(context);

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

            ShowTimer();

            Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
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

//            ((MotionLayout) mView.findViewById(R.id.parent_motion_lay)).transitionToStart();
//
//            ((MotionLayout) mView.findViewById(R.id.parent_motion_lay)).transitionToEnd();


            // Define the position of the
            // window within the screen
            mParams.gravity = Gravity.CENTER;
            mWindowManager = (WindowManager)context.getSystemService(WINDOW_SERVICE);

        }

        private void ShowTimer(){


            countDownTimer =  new CountDownTimer(pref.GET_Time(), 1000) {

                public void onTick(long millisUntilFinished) {

                    long sec = (millisUntilFinished / 1000) % 60;

                    ((TextView) mView.findViewById(R.id.continue_txt)).setText(String.valueOf(sec));


                }

                public void onFinish() {

                    ((ImageView) mView.findViewById(R.id.exit_btn)).setVisibility(View.VISIBLE);
                    ((TextView) mView.findViewById(R.id.continue_txt)).setVisibility(View.INVISIBLE);

                }

            };

           countDownTimer.start();

    }
        @SuppressLint("SetTextI18n")
        public void open() {

            if(mView.getWindowToken()==null) {
                if(mView.getParent()==null) {
                    mWindowManager.addView(mView, mParams);
                    countDownTimer.start();


                    String messag_1 = retriveNewApp();


                    try{

                        Cursor cur = database.GetAppData(messag_1,context);

                        if (cur.getCount() != 0){

                            cur.moveToFirst();

                            @SuppressLint("Range") String data = cur.getString(2).toString();


                            ((TextView) mView.findViewById(R.id.notice_txt)).setText(data);

                        }

                    }catch (Exception e){

                        ((TextView) mView.findViewById(R.id.notice_txt)).setText("“A person who aims at nothing is sure to hit it.”");

                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    ((ImageView) mView.findViewById(R.id.exit_btn)).setVisibility(View.INVISIBLE);
                    ((TextView) mView.findViewById(R.id.continue_txt)).setVisibility(View.VISIBLE);


                    int n = random.nextInt(8);
                    int n1 = random.nextInt(7);

                    ((ConstraintLayout) mView.findViewById(R.id.layout_1)).setBackground(context.getResources().getDrawable(Gradient[n]));

                    ((ConstraintLayout) mView.findViewById(R.id.layout_2)).setBackground(context.getResources().getDrawable(Gradient[n + 1]));

                    ((MotionLayout) mView.findViewById(R.id.parent_motion_lay)).transitionToStart();
                    ((LottieAnimationView) mView.findViewById(R.id.anim_bottom)).setAnimation(Animation[n1]);
                    ((LottieAnimationView) mView.findViewById(R.id.anim_1)).setAnimation(Animation[n1 + 1]);

                    ((MotionLayout) mView.findViewById(R.id.parent_motion_lay)).setTransitionDuration(pref.GET_Time() - 1000);
                    ((MotionLayout) mView.findViewById(R.id.parent_motion_lay)).startLayoutAnimation();

                }
            }

//            try {
//                // check if the view is already
//                // inflated or present in the window
//
//            } catch (Exception e) {
//                Log.d("Error1",e.getMessage());
//            }

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

                countDownTimer.cancel();

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
