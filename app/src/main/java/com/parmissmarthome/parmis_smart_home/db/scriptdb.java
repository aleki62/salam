package com.parmissmarthome.parmis_smart_home.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.parmissmarthome.parmis_smart_home.MainActivity;
import com.parmissmarthome.parmis_smart_home.script;

import java.util.ArrayList;

/**
 * Created by YA-MAHDI on 07/11/2015.
 */
public class scriptdb {
    private static ParmisSHdbHelper dbHelper;
    private static SQLiteDatabase databaseReader;
    private static SQLiteDatabase databaseWriter;

    public final static String script_TABLE_NAME = "scriptsub"; // name of table
    public final static String script_ID = "id";
    public final static String script_Script= "script";
    public final static String script_actID = "action";
    public final static String script_Duration = "duration";
    public final static String script_actstate = "actionstate";
    public final static String script_SaveMeScript = "savemescript";
    public final static String script_SaveMeAction = "savemeaction";
    public final static String script_ServerScript = "serverscript";
    public final static String script_ServerAction = "serveraction";

    public final static String script_CreateTable =
            String.format("create table %s  (" +
                            "%s integer PRIMARY KEY, " +
                            "%s integer ," +
                            "%s integer ," +
                            "%s text ," +
                            "%s integer, " +
                            "%s integer, " +
                            "%s integer, " +// from main menu db
                            "%s text, "+
                            "%s text "+
                            ");",
                            script_TABLE_NAME, script_ID, script_Script, script_actID, script_Duration, script_actstate,
                    script_SaveMeScript, script_SaveMeAction, script_ServerScript, script_ServerAction);
    public scriptdb(Context context){
        if (dbHelper == null) {
            dbHelper = new ParmisSHdbHelper(context);
            databaseWriter = dbHelper.getWritableDatabase();
            databaseReader = dbHelper.getReadableDatabase();
        }
    }
    public long insertrecord(long scriptid, long act, String duration, int state, int savemescript, int savemeaction,
                            String serverScript, String serverAction){
        ContentValues cv=new ContentValues();
        long id=getmaxcode();
        cv.put(script_ID, id);
        cv.put(script_Script, scriptid);
        cv.put(script_actID, act);
        cv.put(script_Duration, duration);
        cv.put(script_actstate, state);
        cv.put(script_SaveMeScript, savemescript);
        cv.put(script_SaveMeAction, savemeaction);
        cv.put(script_ServerAction, serverAction);
        cv.put(script_ServerScript, serverScript);
        try {
            return databaseWriter.insert(script_TABLE_NAME, null, cv);
        }catch (Exception e){
            Log.d(MainActivity.Tag, "خطا اضافه کردنscript");
            Log.e(MainActivity.Tag, e.getMessage());
            return 0;
        }
    }
    public long insertfromssocket(long id, long scriptid, long act, String duration, int state, int savemescript, int savemeaction,
                                  String serverAction, String serverScript){
        ContentValues cv=new ContentValues();
        Cursor res = databaseReader.rawQuery( String.format("select id from %s where %s=%d and %s=%d and %s=%d and %s=%d and %s='%s' and %s='%s'",
                script_TABLE_NAME , script_Script ,scriptid, script_SaveMeScript, savemescript, script_actID, act, script_SaveMeAction, savemeaction,
                script_ServerScript, serverScript, script_ServerAction, serverAction), null);
        if (res != null && res.getCount()==0) {
            id=getmaxcode();
            cv.put(script_ID, id);
            cv.put(script_Script, scriptid);
            cv.put(script_actID, act);
            cv.put(script_Duration, duration);
            cv.put(script_actstate, state);
            cv.put(script_SaveMeScript, savemescript);
            cv.put(script_SaveMeAction, savemeaction);

            cv.put(script_ServerScript, serverAction);
            cv.put(script_ServerAction, serverAction);

            try {
                return databaseWriter.insert(script_TABLE_NAME, null, cv);
            } catch (Exception e) {
                Log.d(MainActivity.Tag, "خطا اضافه کردنscript");
                Log.e(MainActivity.Tag, e.getMessage());
                return 0;
            }
        }
        return -1;
    }
    public void updaterecord(long id, long act, String duration, int state, int savemeaction, String serverAction){
        ContentValues cv=new ContentValues();
        cv.put(script_actID, act);
        cv.put(script_Duration, duration);
        cv.put(script_actstate, state);
        cv.put(script_SaveMeAction, savemeaction);
        cv.put(script_ServerAction, serverAction);

        try {
            databaseWriter.update(script_TABLE_NAME, cv, script_ID + "=?", new String[]{String.valueOf(id)});
        }catch (Exception e){
            Log.e(MainActivity.Tag, "can't update script "+ id);
        }
    }
    public Cursor queryall() {
        Cursor result = databaseReader.rawQuery("select * from " + script_TABLE_NAME, null);
//        Log.d(MainActivity.Tag, "تعداد "+ result.getCount() );
        if (result != null) {
            result.moveToFirst();
        }
        return result;
    }

    public Cursor queryedit(long id) {                  //id     actid  name    dura    state
        Cursor result = databaseReader.rawQuery(String.format("select ts.*, tm.%s from %s ts join %s tm on ts.%s=tm.%s where ts.%s=%d order by ts.id",
                        mainmenudb.MainMenu_Name,
                        script_TABLE_NAME, mainmenudb.Mainmenu_TABLE_NAME, script_actID, mainmenudb.MainMenu_ID, script_Script, id),
                null);
        Log.d(MainActivity.Tag, "سناریو تعداد "+ result.getCount() );
        if (result != null) {
            result.moveToFirst();
        }
        return result;
    }
    /**********************************************************************************************/
    public static ArrayList<runScriptValue> getscriptvalues(long scriptid, String[] args){
        Cursor mdb;
        Cursor sdb= databaseReader.rawQuery("select * from " + script_TABLE_NAME+ " where "+ script_Script+ "="+ scriptid, null);

        ArrayList<runScriptValue> items ;
        if(sdb.moveToFirst()) {
            items = new ArrayList<runScriptValue>(sdb.getCount());

            Log.d(MainActivity.Tag, "Get DB Script "+ sdb.getCount()+ " ID: "+ sdb.getString(sdb.getColumnIndex(script_actID)));
            int j = 0, p=0, p1;
            String cmd;
            do {
                mdb = databaseReader.rawQuery(String.format("select * from %s where %s = %s ",
                        mainmenudb.Mainmenu_TABLE_NAME, mainmenudb.MainMenu_ID, sdb.getString(sdb.getColumnIndex(script_actID))), null);

                if(mdb.moveToFirst()) {
                    cmd=sdb.getString(sdb.getColumnIndex(script_Duration));
                    Log.d(MainActivity.Tag, "Next ID:"+ sdb.getString(sdb.getColumnIndex(script_Duration)));
                    p=0;
                    p1= cmd.indexOf(':');

//                    Log.d(MainActivity.Tag, "hours: "+ cmd.substring(p, p1));
                    j = Integer.valueOf(cmd.substring(p, p1)) * 60 * 60;
                    p=p1+1;
                    p1= cmd.indexOf(':', p);

//                    Log.d(MainActivity.Tag, "minute: "+ cmd.substring(p, p1));
                    j+= Integer.valueOf(cmd.substring(p, p1))* 60;
                    p= p1+1;
//                    Log.d(MainActivity.Tag, "Next ID:"+ mdb.getString(mdb.getColumnIndex(mainmenudb.MainMenu_RemoteKey)));
                    j+= Integer.valueOf(cmd.substring(p));
//                    Log.d(MainActivity.Tag, "second: "+ cmd.substring(p));

                    if (mdb.getInt(mdb.getColumnIndex(mainmenudb.MainMenu_Vaz)) == script.ScriptToggle) {
                        cmd = String.format("*SNDRF*%s*-1#", mdb.getString(mdb.getColumnIndex(mainmenudb.MainMenu_RemoteKey)));
                    } else if (sdb.getInt(sdb.getColumnIndex(script_actstate)) == script.ScriptON)
                        cmd = String.format("*SNDRF*%s*%s#",
                                String.valueOf(mdb.getInt(mdb.getColumnIndex(mainmenudb.MainMenu_RemoteCode)) +
                                        Integer.valueOf(mdb.getString(mdb.getColumnIndex(mainmenudb.MainMenu_RemoteKey)))),
                                "128");
                    else
                        cmd = String.format("*SNDRF*%s*%s#",
                                String.valueOf(mdb.getInt(mdb.getColumnIndex(mainmenudb.MainMenu_RemoteCode)) +
                                        Integer.valueOf(mdb.getString(mdb.getColumnIndex(mainmenudb.MainMenu_RemoteKey)))),
                                "0");

                    items.add(new runScriptValue(cmd, j));
                    Log.d(MainActivity.Tag, "Script cmd: " + cmd + " and duration:" + j);
//                i++;
                }

            } while(sdb.moveToNext());
            Log.d(MainActivity.Tag, "سناریو اضافه شد ");
            return items;
        }
        Log.d(MainActivity.Tag,"سناریو خالیه");
        return null;
    }

/**********************************************************************************************/
    public long getmaxcode() {
        // TODO Auto-generated method stub
        Log.d("alhabib", "Get Code... ");
        try {
            Cursor res = databaseReader.rawQuery(
                    "select max(id) as mcode from " + script_TABLE_NAME, null);
            Log.d(MainActivity.Tag, "Get Code ");
            long id = 1;
            if (res != null) {
                res.moveToFirst();
                id = res.getLong(0);
                id++;
            }
            return id;
        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.e(MainActivity.Tag, e.getMessage());
            return 1;
        }
    }


    public void delrecord(Long aLong) {
        databaseWriter.delete(script_TABLE_NAME, script_ID + "=?", new String[]{String.valueOf(aLong)});
    }
}
