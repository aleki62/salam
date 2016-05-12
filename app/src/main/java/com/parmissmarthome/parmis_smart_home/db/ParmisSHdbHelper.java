package com.parmissmarthome.parmis_smart_home.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.parmissmarthome.parmis_smart_home.MainActivity;


/**
 * Created by Ya-Mahdi on 24/11/2014.
 */
public class ParmisSHdbHelper extends SQLiteOpenHelper {
    private static final String dbname="ParmisSmartHome";
    private static final int version= 12;

    public ParmisSHdbHelper(Context context) {
        super(context, dbname, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(mainmenudb.MainMenu_CreateTable);
        db.execSQL(scriptdb.script_CreateTable);
        db.execSQL(MobileDB.Mobile_CreateTable);
        Log.d(MainActivity.Tag, "ساخت دیتابیس "+ mainmenudb.MainMenu_CreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + mainmenudb.Mainmenu_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + scriptdb.script_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MobileDB.Mobile_Table_Name);
        Log.d(MainActivity.Tag, "حذف دیتابیس");
        onCreate(db);
    }
}
