package com.nightowl.ThinkAgain.Activity;


import static android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nightowl.ThinkAgain.Classes.DialogManager;
import com.nightowl.ThinkAgain.Classes.PreChecker;
import com.nightowl.ThinkAgain.Classes.ViewpagerStateAdpter;
import com.nightowl.ThinkAgain.Database.SharedPref;
import com.nightowl.ThinkAgain.Fragments.AboutUs;
import com.nightowl.ThinkAgain.Fragments.SettingFragment;
import com.nightowl.ThinkAgain.R;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class ShowAndSelectApps extends AppCompatActivity {

    private ViewPager2 viewpager;
    private DialogManager dialogManager;

    private long pressedTime;

    private PreChecker preChecker;

    private FirebaseRemoteConfig  mFirebaseRemoteConfig;

    private Toolbar toolbar;

    private int setting = 0;

    private final int VERSION_CODE = 2;

    private TextView appTxt;

    private SharedPref pref;
    private ImageView settingsImg;
    private ImageView moreImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_and_select_apps);


        viewpager = (ViewPager2) findViewById(R.id.viewpager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appTxt = (TextView) findViewById(R.id.app_txt);
        settingsImg = (ImageView) findViewById(R.id.settings_img);
        moreImg = (ImageView) findViewById(R.id.more_img);
        viewpager = (ViewPager2) findViewById(R.id.viewpager);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getDecorView().getWindowInsetsController().setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS);
        }


        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(86400)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        pref  = new SharedPref(this);
        preChecker = new PreChecker(ShowAndSelectApps.this);

        ViewpagerStateAdpter  adapter = new ViewpagerStateAdpter(getSupportFragmentManager(),getLifecycle());

        viewpager.setAdapter(adapter);
        dialogManager = new DialogManager(ShowAndSelectApps.this,ShowAndSelectApps.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!preChecker.isAccessGranted() || !preChecker.CanOverlay()) {

                dialogManager.Show_Special_Access_Dialog();

            }else{
                if (pref.GET_Tutorial() == 0){
                    Tap_Target1();

                }
            }
        }

        if (preChecker.isAccessGranted() && preChecker.CanOverlay()) {
            CheckPermission();
        }

        VerCheck();

        moreImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowInfoDialog(ShowAndSelectApps.this,v);
            }
        });

        settingsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingFragment setting = new SettingFragment();
                setting.show(getSupportFragmentManager(),"Show");
            }
        });

    }

    public void CheckPermission(){
        if (ContextCompat.checkSelfPermission(ShowAndSelectApps.this,Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            AskForPermission();
        }else{
            dialogManager.DisablePermissionDialog();
        }
    }

    private void AskForPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this)
                    .withPermission(Manifest.permission.POST_NOTIFICATIONS)
                    .withListener(new PermissionListener() {
                        @Override public void onPermissionGranted(PermissionGrantedResponse response) {

                            Toast.makeText(ShowAndSelectApps.this, "Thanks For Providing The Permission.", Toast.LENGTH_SHORT).show();

                        }
                        @Override public void onPermissionDenied(PermissionDeniedResponse response) {

                            dialogManager.OpenPermissionDialog();

                        }
                        @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                            token.continuePermissionRequest();
                        }
                    }).check();
        }
    }

    private void VerCheck(){
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {

                        if (task.isSuccessful()){
                            double ver_code =  mFirebaseRemoteConfig.getDouble("VER_CHECK");

                            if (ver_code != VERSION_CODE){

                                dialogManager.ShowUpdateDialog();

                            }

                        }

                    }
                });
    }

    public void Tap_Target1(){

        new MaterialTapTargetPrompt.Builder(ShowAndSelectApps.this)
                .setTarget(R.id.settings_img)
                .setPrimaryText("Settings")
                .setAutoDismiss(false)
                .setSecondaryText("Select settings according to your preference.")
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                        {
                            setting = 1;
                            Tap_Target2();
                        }
                    }
                })
                .show();

    }

    private void Tap_Target2(){

        new MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.stop_start_service_btn)
                .setPrimaryText("On/Off")
                .setAutoDismiss(false)
                .setSecondaryText("Turn On/Off the application with this button.")
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                        {
                            setting = 2;
                            Tap_Target3();

                        }
                    }
                })
                .show();

    }

    private void Tap_Target3(){

        new MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.unlock_image)
                .setPrimaryText("Lock Application")
                .setAutoDismiss(false)
                .setSecondaryText("Add Application of your choice to thing again list.")
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                        {
                            setting = 3;
                            pref.AddTutorial(1);

                        }
                    }
                })
                .show();

    }

    @Override
    public void onBackPressed() {

        if (setting != 0){
            if (pressedTime + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                finish();
            } else {
                Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            }
            pressedTime = System.currentTimeMillis();
        }

    }



    private void ShowInfoDialog(Context context, View view){

        PopupMenu popupMenu = new PopupMenu(context,view);
        popupMenu.inflate(R.menu.app_info_menu);
        popupMenu.setForceShowIcon(true);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){

                    case R.id.rate_us:
                        final String appPackageName = getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        return true;

                    case R.id.share:
                        try {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Think Again");
                            String shareMessage= "\nDownload Think Again and remove all distraction\n\n";
                            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getPackageName();
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                            startActivity(Intent.createChooser(shareIntent, "choose one"));
                        } catch(Exception e) {
                            //e.toString();
                        }
                        return true;

                    case R.id.about_us:

                        AboutUs aboutUs = new AboutUs();

                        aboutUs.show(getSupportFragmentManager(),"Show");

                        return true;

                    case R.id.tutorial:

                        Tap_Target1();
                        return true;
                }
                return false;
            }
        });




        popupMenu.show();

    }

    @Override
    protected void onResume() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (preChecker.isAccessGranted() && preChecker.CanOverlay()) {


                dialogManager.DismissDialog();
                if (pref.GET_Tutorial() == 0){
                    Tap_Target1();

                }
            }else{
                try {
                    if (dialogManager.Check_Dialog_Is_Visible()){
                        dialogManager.Check_Permission_Dialog();
                    }
                }catch (Exception e){}
            }
        }

        super.onResume();
    }
}