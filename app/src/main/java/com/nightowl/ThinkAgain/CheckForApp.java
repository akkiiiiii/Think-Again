package com.nightowl.ThinkAgain;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CheckForApp extends android.app.Service {

        String CURRENT_PACKAGE_NAME = this.getPackageName();
        String lastAppPN = "";
        boolean noDelay = false;
        public static CheckForApp instance;

        @Override
        public IBinder onBind(Intent intent) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            // TODO Auto-generated method stub

            scheduleMethod();
            CURRENT_PACKAGE_NAME = getApplicationContext().getPackageName();
            Log.e("Current PN", "" + CURRENT_PACKAGE_NAME);

            instance = this;

            return START_STICKY;
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

            Toast.makeText(getApplicationContext(), currentApp, Toast.LENGTH_SHORT).show();
            return currentApp;

        }
        else {

            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            String mm=(manager.getRunningTasks(1).get(0)).topActivity.getPackageName();
            Toast.makeText(this, mm, Toast.LENGTH_SHORT).show();
            return mm;
        }
    }

        private void scheduleMethod() {
            // TODO Auto-generated method stub

            ScheduledExecutorService scheduler = Executors
                    .newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub

                    // This method will check for the Running apps after every 100ms
//                    if(30 minutes spent){
//                        stop();
//                    }else{
//                        checkRunningApps();
//                    }

                    checkRunningApps();
                }
            }, 0, 100, TimeUnit.MILLISECONDS);
        }

        public void checkRunningApps() {
            ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);
            ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
            String activityOnTop = ar.topActivity.getPackageName();
            Log.e("activity on TOp", "" + activityOnTop);

            Toast.makeText(instance, activityOnTop, Toast.LENGTH_SHORT).show();

            // Provide the packagename(s) of apps here, you want to show password activity
            if (activityOnTop.contains("com.whatsapp")  // you can make this check even better
                    || activityOnTop.contains(CURRENT_PACKAGE_NAME)) {
                // Show Password Activity

                Toast.makeText(instance, "miricale", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(instance, "no", Toast.LENGTH_SHORT).show();
                // DO nothing
            }
        }

        public static void stop() {
            if (instance != null) {
                instance.stopSelf();
            }
        }


}
