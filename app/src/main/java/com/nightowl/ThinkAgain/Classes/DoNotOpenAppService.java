package com.nightowl.ThinkAgain.Classes;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.nightowl.ThinkAgain.Database.DoNotOpenDatabase;
import com.nightowl.ThinkAgain.Database.SharedPref;
import com.nightowl.ThinkAgain.OverlayScreens.ClickCounterOverlayScreen;
import com.nightowl.ThinkAgain.OverlayScreens.DoNotUseApp;
import com.nightowl.ThinkAgain.R;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class DoNotOpenAppService extends Service {


    private final static String CHANNEL_ID = "Focus Apps";

    private View mView;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private LayoutInflater layoutInflater;
    private DoNotOpenDatabase database;
    private SharedPref pref;
    private String previous_app;

    private DoNotUseApp doNotUseApp;
    private ClickCounterOverlayScreen ClickCounterScreen;

    Timer timer;
    Timer timer_2 = new Timer();

    int delay = 0; // delay for 5 sec.
    int period = 10; // repeat every 10 secs.

    private Context context;

    private PreChecker preChecker;


    @Override
    public void onCreate() {
        super.onCreate();

        context = getBaseContext();

        pref = new SharedPref(context);

        preChecker = new PreChecker(context);


    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Create_Channel();
        Create_Running_Service_Notification();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (preChecker.CanOverlay() && preChecker.isAccessGranted()){
                SetParentSyncTimer();
            }
            else if (!preChecker.CanOverlay() || !preChecker.isAccessGranted()){
                CheckPer();
                stopSelf();
            }
        }

        return START_STICKY;
    }


    private void SetParentSyncTimer(){
        database = new DoNotOpenDatabase(this);

        Log.e("TAG HAI","FIRST TIMER STARTED");

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {

                String package_name = retriveNewApp();

                if (database.app_exist(package_name)){
                    previous_app = package_name;

                    timer.cancel();

                    new Handler(Looper.getMainLooper()).post(() -> {

                        if (pref.GET_Reminder() == 0){
                            doNotUseApp = new DoNotUseApp(context);
                            doNotUseApp.open();
                        } else if (pref.GET_Reminder() == 1) {

                            ClickCounterScreen = new ClickCounterOverlayScreen(context);
                            ClickCounterScreen.open();
                        }

                        StartParentSyncTimerAgain();

                    });
                }

            }

        }, delay, period);
    }

    private void StartParentSyncTimerAgain(){

        timer_2 = new Timer();

        timer_2.scheduleAtFixedRate(new TimerTask() {

            public void run() {

                String package_name = retriveNewApp();

                Log.e("TAG HAI", package_name);

                if (!Objects.equals(previous_app, package_name)){

                    if (!package_name.equals("android")){
                        timer_2.cancel();
                        if (pref.GET_Reminder() == 0){
                            doNotUseApp.close();

                        } else if (pref.GET_Reminder() == 1){
                            ClickCounterScreen.close();

                        }
                        SetParentSyncTimer();
                    }


                }

            }

        }, delay, period);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (timer != null){
            timer.cancel();
        }
        if (timer_2 != null){
            timer_2.cancel();
        }

        CheckPer();

    }

    private void CheckPer(){
        try{
            NotificationManage manage = new NotificationManage(context);

            if (!preChecker.CanOverlay() || !preChecker.isAccessGranted()){

                manage.CreatePerAlertChannel();
                manage.SendPermissionNotification("Please provide permission in order to use this application.");

            }else{

                String Quotes[] = {"Stay focused, go after your dreams and keep moving toward your goals.",
                        "Live life to the fullest, and focus on the positive.",
                "Stay focused on the mission.",
                "Everyone’s time is limited. What matters most is to focus on what matters most."
                ,"I don’t focus on what I’m up against. I focus on my goals and I try to ignore the rest."
                ,"The successful warrior is the average man, with laser-like focus."
                ,"It is during our darkest moments that we must focus to see the light."};

                Random random = new Random();


                manage.SendPermissionNotification(Quotes[random.nextInt(5)]);

            }
        }catch (Exception e){}
    }

    public void Create_Channel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = this.getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    public void Create_Running_Service_Notification(){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID);
        builder.setContentTitle("Think Again");
        builder.setContentText("Think again is working");
        builder.setAutoCancel(false);
        builder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE);
        builder.setOngoing(true);
        builder.setSmallIcon(R.drawable.lock);
        startForeground(23,builder.build());

    }

    private String retriveNewApp() {

        if (Build.VERSION.SDK_INT >= 21) {
            String currentApp = null;
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
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
//            Log.e("TAG HAI", "Current App in 1st block foreground is: " + currentApp);

            return currentApp;

        }
        else {

            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            String mm=(manager.getRunningTasks(1).get(0)).topActivity.getPackageName();
            Log.e("TAG HAI", "Current App in foreground is: " + mm);
            return mm;
        }
    }



//    public void Delete_Notification(){
//        manager.cancel(1);
//    }

}
