package com.parmissmarthome.parmis_smart_home.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.parmissmarthome.parmis_smart_home.MainActivity;

/**
 * Created by YA-MAHDI on 07/03/2016.
 */
public class MobileDB {
    private static ParmisSHdbHelper dbHelper;
    private static SQLiteDatabase databaseReader;
    private static SQLiteDatabase databaseWriter;

    public static final String Mobile_Table_Name="mobiledb";
    public static final String Mobile_ID= "id";
    public static final String Mobile_MAC= "mac";
    public static final String Mobile_ADDKEY= "addkey";
    public static final String Mobile_ADDScript= "addscript";
    public static final String Mobile_EDITKEY= "editkey";
    public static final String Mobile_EDITScript= "editscript";
    public static final String Mobile_TimeLimit= "timelimit";
    public static final String Mobile_TimeStart= "timestart";
    public static final String Mobile_TimeEnd= "timeend";
    public static final String Mobile_Phone= "phone";
    public static final String Mobile_Web= "web";
//    09039678149 cam1

    public final static String Mobile_CreateTable =
            String.format("create table %s  (" +
                    "%s integer primary key, " +
                    "%s text, "+
                    "%s integer,"+
                    "%s integer,"+
                    "%s integer,"+
                    "%s integer,"+
                    "%s integer,"+
                            "%s text, "+
                            "%s text, "+
                    "%s text," +
                            "%s integer) ",
                    Mobile_Table_Name,
                    Mobile_ID,
                    Mobile_MAC,
                    Mobile_ADDKEY,
                    Mobile_ADDScript,
                    Mobile_EDITKEY,
                    Mobile_EDITScript,
                    Mobile_TimeLimit,
                    Mobile_TimeStart,
                    Mobile_TimeEnd,
                    Mobile_Phone,
                    Mobile_Web);


    public MobileDB(Context context) {
            if (dbHelper == null) {
                dbHelper = new ParmisSHdbHelper(context);
                databaseWriter = dbHelper.getWritableDatabase();
                databaseReader = dbHelper.getReadableDatabase();
            }
    }

    public long insertrecord(String mac, int addkey, int addscript, int editkey, int editscript,
                             int timelimit, String timestart, String timeend, String phone, int web){
        ContentValues cv=new ContentValues();
        long id=getmaxcode();
        cv.put(Mobile_ID, id);
        cv.put(Mobile_MAC, mac);
        cv.put(Mobile_ADDKEY, addkey);
        cv.put(Mobile_ADDScript, addscript);
        cv.put(Mobile_EDITKEY, editkey);
        cv.put(Mobile_EDITScript,  editscript);
        cv.put(Mobile_TimeLimit, timelimit);
        cv.put(Mobile_TimeStart, timestart);
        cv.put(Mobile_TimeEnd, timeend);
        cv.put(Mobile_Phone, phone);
        cv.put(Mobile_Web, web);
        try {
            return databaseWriter.insert(Mobile_Table_Name, null, cv);
        }catch (Exception e){
            Log.d(MainActivity.Tag, "خطا اضافه کردن");
            Log.e("alhabib", e.getMessage());
            return 0;
        }
    }
    public long getmaxcode() {
        // TODO Auto-generated method stub
        try {
            Cursor res = databaseReader.rawQuery(
                    "select max(id) as mcode from " + Mobile_Table_Name, null);
            long id = 1;
            if (res != null) {
                res.moveToFirst();
                id = res.getLong(0);
                id++;
            }
            return id;
        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.e("alhabib", e.getMessage());
            return 1;
        }
    }
    public MainActivity.ConfigClient acceptlater(String mac) {
        // TODO Auto-generated method stub
        try {
            Cursor res = databaseReader.rawQuery(
                    String.format("select * from %s where %s='%s'", Mobile_Table_Name, Mobile_MAC, mac), null);
            if (res != null) {
                if(res.moveToFirst())
                    return new MainActivity.ConfigClient(res.getInt(res.getColumnIndex(Mobile_ADDKEY)),
                            res.getInt(res.getColumnIndex(Mobile_ADDScript)),
                            res.getString(res.getColumnIndex(Mobile_Phone))!= null,
                            res.getString(res.getColumnIndex(Mobile_Phone)),
                            res.getInt(res.getColumnIndex(Mobile_Web))!=0,
                            res.getString(res.getColumnIndex(Mobile_MAC)));
            }
            return null;
        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.e("alhabib", e.getMessage());
            return null;
        }
    }
    public void delete(String where){
        databaseWriter.delete(Mobile_Table_Name, where, null);
    }
}
