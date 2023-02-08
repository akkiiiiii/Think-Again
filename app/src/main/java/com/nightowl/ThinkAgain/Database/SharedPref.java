package com.nightowl.ThinkAgain.Database;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private Context context;
    private Activity activity;

    private final String isServiceOn = "service";

    private final String ADD_TIMER = "time";
    private final String REMINDER_TYPE = "reminder";

    private final String CURRENT_APP = "current_app";

    private final String TUTORIAL = "tutorial";

    private final String Shared_prefrence = "reminder_info";


    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

    public SharedPref(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;

        editor = context.getSharedPreferences(Shared_prefrence, MODE_PRIVATE).edit();

    }

    public SharedPref(Context context) {
        this.context = context;
        editor = context.getSharedPreferences(Shared_prefrence, MODE_PRIVATE).edit();

    }

    public void AddTime(int value){
        editor.putInt(ADD_TIMER,value);
        editor.apply();
    }

    public void IsServiceOn(Boolean value){
        editor.putBoolean(isServiceOn,value);
        editor.apply();
    }

    public void AddTutorial(int value){
        editor.putInt(TUTORIAL,value);
        editor.apply();
    }

    public void AddReminder(int value){
        editor.putInt(REMINDER_TYPE,value);
        editor.apply();
    }

    public void AddCurrentApp(String value){
        editor.putString(CURRENT_APP,value);
        editor.apply();
    }

    public Boolean GetIsServiceOn(){
        prefs = context.getSharedPreferences(Shared_prefrence, MODE_PRIVATE);
        return prefs.getBoolean(isServiceOn,false);
    }

    public String GET_Current_App(){
        prefs = context.getSharedPreferences(Shared_prefrence, MODE_PRIVATE);
        return prefs.getString(CURRENT_APP,"");
    }

    public int GET_Time(){
        prefs = context.getSharedPreferences(Shared_prefrence, MODE_PRIVATE);
        return prefs.getInt(ADD_TIMER,30000);
    }

    public int GET_Tutorial(){
        prefs = context.getSharedPreferences(Shared_prefrence, MODE_PRIVATE);
        return prefs.getInt(TUTORIAL,0);
    }

    public int GET_Reminder(){
        prefs = context.getSharedPreferences(Shared_prefrence, MODE_PRIVATE);
        return prefs.getInt(REMINDER_TYPE,0);
    }

}
