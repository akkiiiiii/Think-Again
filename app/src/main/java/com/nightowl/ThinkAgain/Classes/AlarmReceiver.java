package com.nightowl.ThinkAgain.Classes;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.nightowl.ThinkAgain.Database.SharedPref;

import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManage notificationManage = new NotificationManage(context);
        SharedPref pref = new SharedPref(context);
        Alarm alarm = new Alarm(context);
        notificationManage.Create_Channel();
        PreChecker preChecker = new PreChecker(context);

        if (preChecker.isAccessGranted() && preChecker.CanOverlay()){

            if (pref.GET_Current_App().equals(retriveNewApp(context))){
                notificationManage.SendAlert();

            }else
            {
                alarm.CancelAlarm();
            }

        }else {
            alarm.CancelAlarm();

        }

    }

    public String retriveNewApp(Context context) {

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
