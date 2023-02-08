package com.nightowl.ThinkAgain.Classes;

import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.nightowl.ThinkAgain.Database.DoNotOpenDatabase;
import com.nightowl.ThinkAgain.Database.SharedPref;

public class DeviceRestartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        DoNotOpenDatabase database = new DoNotOpenDatabase(context);
        PreChecker preChecker = new PreChecker(context);

        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (preChecker.isAccessGranted() && preChecker.CanOverlay()){

                    if (database.get_package_count() > 0){

                        Intent serviceIntent = new Intent(context,
                                DoNotOpenAppService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(serviceIntent);
                        }

                    }

                }else{
                    if (database.get_package_count() > 0){
                        NotificationManage manage = new NotificationManage(context);
                        manage.CreatePerAlertChannel();
                        manage.SendPermissionNotification("Please provide permission in order to use this application.");
                    }

                }

            }

        }

    }

}
