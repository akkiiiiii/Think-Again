package com.nightowl.ThinkAgain.Classes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nightowl.ThinkAgain.Activity.ShowAndSelectApps;
import com.nightowl.ThinkAgain.Database.DoNotOpenDatabase;
import com.nightowl.ThinkAgain.Database.SharedPref;
import com.nightowl.ThinkAgain.R;


public class DialogManager {

    Activity activity;
    Context context;
    Dialog dialog;

    Dialog PermissionDialog;

    PreChecker preChecker;

    public DialogManager(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        preChecker = new PreChecker(context);

    }

    public void Show_Special_Access_Dialog(){

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.special_permision_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        ImageView closeDialogImg;
        AppCompatButton accessUsageBtn;
        AppCompatButton overlayUsageBtn;

        closeDialogImg = (ImageView) dialog.findViewById(R.id.close_dialog_img);
        accessUsageBtn = (AppCompatButton) dialog.findViewById(R.id.access_usage_btn);
        overlayUsageBtn = (AppCompatButton) dialog.findViewById(R.id.overlay_usage_btn);



        if (preChecker.isAccessGranted()){
            accessUsageBtn.setText("Allowed");
            accessUsageBtn.setAlpha(0.5f);
            accessUsageBtn.setEnabled(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(context)) {
                overlayUsageBtn.setText("Allowed");
                overlayUsageBtn.setAlpha(0.5f);
                overlayUsageBtn.setEnabled(false);
            }
        }


        accessUsageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                activity.startActivity(intent);

            }
        });

        overlayUsageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    activity.startActivity(intent);

                }
            }
        });

        closeDialogImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPref pref = new SharedPref(context);
                if (pref.GET_Tutorial() == 0){
                    ((ShowAndSelectApps)activity).Tap_Target1();

                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void DismissDialog(){
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void Check_Permission_Dialog(){
        if (dialog.isShowing()){
            dialog.dismiss();
            Show_Special_Access_Dialog();
        }
    }

    public Boolean Check_Dialog_Is_Visible(){

        return dialog.isShowing();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void StartService(AppCompatButton btn){
        if (!isMyServiceRunning(DoNotOpenAppService.class)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                btn.setBackgroundDrawable(context.getDrawable(R.drawable.background));
                btn.setText("On");
                context.startForegroundService(new Intent(activity,DoNotOpenAppService.class));
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void OpenPermissionDialog(){
        PermissionDialog = new Dialog(context);
        PermissionDialog.setContentView(R.layout.permission_dialouge_box);
        PermissionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        PermissionDialog.setCancelable(false);

        TextView permissionTxt = (TextView) PermissionDialog.findViewById(R.id.permission_txt);
        LottieAnimationView lottieAnimationView = (LottieAnimationView) PermissionDialog.findViewById(R.id.lottieAnimationView);
        AppCompatButton openSettingsBtn = (AppCompatButton) PermissionDialog.findViewById(R.id.open_settings_btn);
        AppCompatButton cancelBtn = (AppCompatButton) PermissionDialog.findViewById(R.id.cancel_btn);
        TextView textView;
        textView = (TextView) PermissionDialog.findViewById(R.id.textView);

        textView.setText("To keep you updated about when app is running. We need notification permission.");

        openSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PermissionDialog.dismiss();
            }
        });
        PermissionDialog.show();

    }

    public void DisablePermissionDialog(){
        if (PermissionDialog != null){
            PermissionDialog.dismiss();
        }
    }

    public void ShowUpdateDialog(){
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.update_dialog);
        dialog.setCancelable(false);

        ConstraintLayout subParent;
        ConstraintLayout txtLay;
        LottieAnimationView lottieAnimationView;
        AppCompatButton updateBtn;
        AppCompatButton notNowBtn;

        notNowBtn = (AppCompatButton) dialog.findViewById(R.id.not_now_btn);
        subParent = (ConstraintLayout) dialog.findViewById(R.id.sub_parent);
        txtLay = (ConstraintLayout) dialog.findViewById(R.id.txt_lay);
        lottieAnimationView = (LottieAnimationView) dialog.findViewById(R.id.lottieAnimationView);
        updateBtn = (AppCompatButton) dialog.findViewById(R.id.update_btn);

        notNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

            }
        });

        dialog.show();
    }

    public void ShowLockDialogForSelectApp(String package_name, ImageView lock, ImageView unlock, String app_name,TextView app_count,  AppCompatButton stop_start_service_btn){

        dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.lock_app_dialog);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ConstraintLayout parentLay;
        ConstraintLayout headingLay;
        TextView headingTxt;
        LottieAnimationView lockAnim;
        TextInputLayout messageBoxLay;
        TextInputEditText messageBox;
        CheckBox playSndCheck;
        CheckBox ShowAnimCheck;
        AppCompatButton exitBtn;
        AppCompatButton lockBtn;


        parentLay = (ConstraintLayout) dialog.findViewById(R.id.parent_lay);
        headingLay = (ConstraintLayout) dialog.findViewById(R.id.heading_lay);
        headingTxt = (TextView) dialog.findViewById(R.id.heading_txt);
        lockAnim = (LottieAnimationView) dialog.findViewById(R.id.lock_anim);
        messageBoxLay = (TextInputLayout) dialog.findViewById(R.id.message_box_lay);
        messageBox = (TextInputEditText) dialog.findViewById(R.id.message_box);
        exitBtn = (AppCompatButton) dialog.findViewById(R.id.exit_btn);
        lockBtn = (AppCompatButton) dialog.findViewById(R.id.lock_btn);


        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!messageBox.getEditableText().toString().isEmpty()){

                    DoNotOpenDatabase database = new DoNotOpenDatabase(context);
                    database.put_do_not_disturb_app(package_name,messageBox.getEditableText().toString());


                    lock.setVisibility(View.VISIBLE);
                    unlock.setVisibility(View.INVISIBLE);

                    StartService(stop_start_service_btn);

                    Toast.makeText(activity,app_name+" " + "Successfully Added to Think Again List", Toast.LENGTH_SHORT).show();

                    Vibrate();

                    app_count.setText("Locked Applications = "+String.valueOf(database.get_package_count()));


                    dialog.dismiss();
                }
                else
                {
                    messageBox.setError("Add any message here.");
                }


            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();


    }

    // Lock Dialog

    public Boolean ShowLockDialog(String package_name, ImageView lock, ImageView unlock, String app_name,TextView app_count){

        dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.lock_app_dialog);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ConstraintLayout parentLay;
        ConstraintLayout headingLay;
        TextView headingTxt;
        LottieAnimationView lockAnim;
        TextInputLayout messageBoxLay;
        TextInputEditText messageBox;
        TextInputEditText image_box;
        TextInputLayout textInputLayout;
        CheckBox playSndCheck;
        CheckBox ShowAnimCheck;
        AppCompatButton exitBtn;
        AppCompatButton lockBtn;

        final Boolean[] locked = {false};

        parentLay = (ConstraintLayout) dialog.findViewById(R.id.parent_lay);
        headingLay = (ConstraintLayout) dialog.findViewById(R.id.heading_lay);
        headingTxt = (TextView) dialog.findViewById(R.id.heading_txt);
        lockAnim = (LottieAnimationView) dialog.findViewById(R.id.lock_anim);
        messageBoxLay = (TextInputLayout) dialog.findViewById(R.id.message_box_lay);
        messageBox = (TextInputEditText) dialog.findViewById(R.id.message_box);
        exitBtn = (AppCompatButton) dialog.findViewById(R.id.exit_btn);
        lockBtn = (AppCompatButton) dialog.findViewById(R.id.lock_btn);


        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!messageBox.getEditableText().toString().isEmpty()){

                    DoNotOpenDatabase database = new DoNotOpenDatabase(context);
                    database.put_do_not_disturb_app(package_name,messageBox.getEditableText().toString());

                    locked[0] = true;

                    lock.setVisibility(View.VISIBLE);
                    unlock.setVisibility(View.INVISIBLE);

                    Toast.makeText(activity,app_name+" " + "Successfully Added to Think Again List", Toast.LENGTH_SHORT).show();

                    Vibrate();

                    app_count.setText("Locked Applications = "+String.valueOf(database.get_package_count()));


                    dialog.dismiss();
                }
                else
                {
                    messageBox.setError("Add any message here.");
                }


            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();

        return locked[0];
    }

    private void Vibrate(){
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    public void CheckAndHideDialog(){
        if (dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
