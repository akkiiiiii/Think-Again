package com.nightowl.ThinkAgain.Classes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.nightowl.ThinkAgain.Activity.ShowAndSelectApps;
import com.nightowl.ThinkAgain.Database.DoNotOpenDatabase;
import com.nightowl.ThinkAgain.R;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class NotificationManage {


    private final static String CHANNEL_ID = "Over App Usage";
    private final static String Require_Permission = "Permission Alert";
    private final Context context;
    public static final String ACTION_STOP_LISTEN = "action_stop_listen";
    NotificationManagerCompat manager;
    NotificationManagerCompat manager2;


    public NotificationManage(Context context) {
        this.context = context;
        manager = NotificationManagerCompat.from(context);
        manager2 = NotificationManagerCompat.from(context);
    }

    public void Create_Channel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }


    }

    public void CreatePerAlertChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Require_Permission, Require_Permission, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    public void SendPermissionNotification(String Message) {

        PendingIntent contentIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, ShowAndSelectApps.class), PendingIntent.FLAG_MUTABLE);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            contentIntent = PendingIntent.getActivity(context, 0,
                   new Intent(context, ShowAndSelectApps.class), PendingIntent.FLAG_IMMUTABLE);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Require_Permission);
        builder.setContentTitle("Think Again");
        builder.setContentText(Message);
        builder.setAutoCancel(true);
        builder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.drawable.belll);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                        (Activity) context,
                        new String [] { Manifest.permission.POST_NOTIFICATIONS },111

                );
            }
            return;
        }
        manager2.notify(2, builder.build());


    }

    public void SendAlert() {

        String messag_1 = retriveNewApp(context);
        DoNotOpenDatabase database = new DoNotOpenDatabase(context);


        try{

            Cursor cur = database.GetAppData(messag_1,context);

            if (cur.getCount() != 0){

                cur.moveToFirst();

                @SuppressLint("Range") String data = cur.getString(2).toString();


                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
                builder.setContentTitle("Think Again");
                builder.setContentText(data);
                builder.setAutoCancel(true);
                builder.setSmallIcon(R.drawable.hand);


                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(
                                (Activity) context,
                                new String [] { Manifest.permission.POST_NOTIFICATIONS },111

                        );
                    }
                    return;
                }
                manager.notify(1, builder.build());

            }

        }catch (Exception e){

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

    public void Delete_Notification(){
        manager.cancel(1);
    }

}
