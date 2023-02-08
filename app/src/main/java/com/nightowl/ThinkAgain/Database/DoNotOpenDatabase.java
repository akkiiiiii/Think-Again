package com.nightowl.ThinkAgain.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DoNotOpenDatabase extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "DoNotOpen";
    private final String TABLE_NAME = "AppList";
    private final String COL_1_ID = "ID";
    private final String COL_2_PACKAGE_NAME = "PACKAGE_NAME";
    private final String COL_3_Message = "MESSAGE";
    private SQLiteDatabase sqLiteDatabase;


    public DoNotOpenDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_table = "CREATE TABLE "+TABLE_NAME+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, PACKAGE_NAME text, MESSAGE text)";
        db.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String upgrade_process = "DROP TABLE IF EXISTS "+TABLE_NAME;

        db.execSQL(upgrade_process);
    }

    public String put_do_not_disturb_app(String PACKAGE, String message){
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_2_PACKAGE_NAME,PACKAGE);
        cv.put(COL_3_Message,message);
        long result = sqLiteDatabase.insert(TABLE_NAME,null,cv);

        if (result == -1){
            sqLiteDatabase.close();
            return "Something Went Wrong";
        }else{
            sqLiteDatabase.close();
            return "Added To Think Again List";
        }
    }

    public Boolean app_exist(String query){
        sqLiteDatabase = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + "  WHERE PACKAGE_NAME " + "='" + query + "'",null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0){
            return false;
        }else{
            return  true;
        }
    }

    public void delete_package(String package_name){
        sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME,"PACKAGE_NAME=?",new String[]{package_name});
        sqLiteDatabase.close();
    }


    public long get_package_count() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        return count;
    }

    public Cursor GetAppData(String package_name, Context context) {
        sqLiteDatabase = this.getReadableDatabase();

        String Query = "SELECT * FROM AppList WHERE PACKAGE_NAME = " + "'" + package_name + "'";

        return sqLiteDatabase.rawQuery(Query, null);

    }
}
