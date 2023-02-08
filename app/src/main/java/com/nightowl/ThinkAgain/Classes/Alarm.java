package com.nightowl.ThinkAgain.Classes;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class Alarm {

    private Context context;
    private AlarmManager alarmManager;

    public Alarm(Context context) {
        this.context = context;

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    }

    public void setAlarm(){

        Intent intent = new Intent(context,AlarmReceiver.class);

        PendingIntent pd = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pd = PendingIntent.getBroadcast(context,100,intent,PendingIntent.FLAG_IMMUTABLE);
        }else{
            pd = PendingIntent.getBroadcast(context,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()
                ,900000,pd);
    }

    public void CancelAlarm(){
            Intent intent = new Intent(context,AlarmReceiver.class);

            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pd = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pd = PendingIntent.getBroadcast(context,100,intent, PendingIntent.FLAG_MUTABLE);
            }else{
                pd = PendingIntent.getBroadcast(context,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            }

            alarmManager.cancel(pd);


    }

}
