package com.parmissmarthome.parmis_smart_home.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.parmissmarthome.parmis_smart_home.MainActivity;
import com.parmissmarthome.parmis_smart_home.script;


/**
 * Created by Ya-Mahdi on 24/11/2014.
 */
public class mainmenudb {
    private static ParmisSHdbHelper dbHelper;
    private static SQLiteDatabase databaseReader;
    private static SQLiteDatabase databaseWriter;

    public final static String Mainmenu_TABLE_NAME = "allkeys"; // name of table
    public final static String MainMenu_ID = "id";
    public final static String MainMenu_Name = "name";
    public final static String MainMenu_RemoteKey = "RemoteKey";
    public final static String MainMenu_RemoteCode = "RemoteCode";
    public final static String MainMenu_Group = "GroupKeys";
    public final static String MainMenu_Image = "ImgPath";
    public final static String MainMenu_Vaz = "VazToggle";  //toggle or no
    public final static String MainMenu_VisMain = "VisMain";
    public final static String MainMenu_Col = "ColMain";
    public final static String MainMenu_Row = "RowMain";
    public final static String MainMenu_Parent = "ParentAll";
    public final static String MainMenu_Server = "server";
    public final static String MainMenu_SaveMe = "saveme";


    public final static String MainMenu_CreateTable =
        String.format("create table %s  (" +
                        "%s integer , " +
                        "%s text , " +
                        "%s text , " +
                        "%s integer ,"+
                        "%s text ," +
                        "%s integer ,"+
                        "%s integer ,"+
                        "%s integer ," +
                        "%s integer ," +
                        "%s integer, " +
                        "%s text, " +
                        "%s integer, " +
                        "PRIMARY KEY(%s, %s, %s));",
                Mainmenu_TABLE_NAME, MainMenu_ID, MainMenu_Name, MainMenu_RemoteKey, MainMenu_RemoteCode, MainMenu_Image,
                MainMenu_Vaz, MainMenu_VisMain,
                MainMenu_Col, MainMenu_Row, MainMenu_Parent, MainMenu_Server, MainMenu_SaveMe,
                MainMenu_ID, MainMenu_SaveMe, MainMenu_Server);
    public mainmenudb(Context context){
        if (dbHelper == null) {
            dbHelper = new ParmisSHdbHelper(context);
            databaseWriter = dbHelper.getWritableDatabase();
            databaseReader = dbHelper.getReadableDatabase();
        }
    }
    private long getparent(String Group, String server){
        Cursor result= databaseReader.rawQuery(String.format(
                "select %s  from %s where %s=0 and %s='%s' and %s= '%s'",
                MainMenu_ID, Mainmenu_TABLE_NAME, MainMenu_Parent, MainMenu_Name, Group, MainMenu_Server, server), null);
        if(result.moveToFirst()){
            return  result.getInt(result.getColumnIndex(MainMenu_ID));
        }
        else
        {
            ContentValues cv=new ContentValues();
            long id=getmaxcode(server);
            cv.put(MainMenu_ID, id);
            cv.put(MainMenu_Name, Group);
            cv.put(MainMenu_Parent, 0);
            cv.put(MainMenu_Server, server);
            if(MainActivity.isClient)
                cv.put(MainMenu_SaveMe, 1);
            else
                cv.put(MainMenu_SaveMe, 0);
            databaseWriter.insert(Mainmenu_TABLE_NAME, null, cv);
            return id;
        }
//        return 0;
    }

    public String getGroupName(Long id, String server){
        Cursor result= databaseReader.rawQuery(String.format(
                "select %s from %s where %s=%d and %s='%s' ",
                MainMenu_Name, Mainmenu_TABLE_NAME, MainMenu_ID, id, MainMenu_Server, server), null);
        if(result.moveToFirst()){
            return  result.getString(result.getColumnIndex(MainMenu_Name));
        }

        return null;
    }

    public long insertrecord(String name, String RemoteKey, int RemoteCode, String Group, String image, int vaz,
                             int vis, int col, int row, String server){
        ContentValues cv=new ContentValues();
        long gid=getparent(Group, server);
        long id=getmaxcode(server);

        Log.e(MainActivity.Tag, "سرور "+ server);
        cv.put(MainMenu_ID, id);
        cv.put(MainMenu_Name, name);
        cv.put(MainMenu_RemoteKey, RemoteKey);
        cv.put(MainMenu_RemoteCode, RemoteCode);
//        cv.put(MainMenu_Group, Group);
        cv.put(MainMenu_Image, image);
        cv.put(MainMenu_Vaz,  vaz);
        cv.put(MainMenu_VisMain, vis);
        cv.put(MainMenu_Col, col);
        cv.put(MainMenu_Row, row);
        cv.put(MainMenu_Parent, gid);
        cv.put(MainMenu_Server, server);
        if(MainActivity.isClient)
            cv.put(MainMenu_SaveMe, 1);
        else
            cv.put(MainMenu_SaveMe, 0);

        try {
            return databaseWriter.insert(Mainmenu_TABLE_NAME, null, cv);
        }catch (Exception e){
            Log.d(MainActivity.Tag, "خطا اضافه کردن");
            Log.e("alhabib", e.getMessage());
            return 0;
        }
    }
    public long insertarraysocket(long id, String name, String RemoteKey, int RemoteCode, String image, int vaz, int vis, int col, int row,
                                  long parent, String server, int saveme){
//        Log.d(MainActivity.Tag, "id is :" + id);
        ContentValues cv=new ContentValues();
        Cursor res = databaseReader.rawQuery(String.format(
                "select id from %s where %s=%d and %s=%d and %s='%s'",
                Mainmenu_TABLE_NAME , MainMenu_ID, id, MainMenu_SaveMe, saveme, MainMenu_Server, server), null);
        if (res != null && res.getCount()==0) {
            Log.d("alhabib", "not found id "+ id+ "  count :"+ res.getCount());
//            long gid=getparent(Group);

            cv.put(MainMenu_ID, id);
            cv.put(MainMenu_Name, name);
            cv.put(MainMenu_RemoteKey, RemoteKey);
            cv.put(MainMenu_RemoteCode, RemoteCode);
//        cv.put(MainMenu_Group, Group);
            if (image!="null")
                cv.put(MainMenu_Image, image);
            cv.put(MainMenu_Vaz,  vaz);
            cv.put(MainMenu_VisMain, vis);
            cv.put(MainMenu_Col, col);
            cv.put(MainMenu_Row, row);
            cv.put(MainMenu_Parent, parent);
            cv.put(MainMenu_Server, server);
            cv.put(MainMenu_SaveMe, saveme);
            try {
                return databaseWriter.insert(Mainmenu_TABLE_NAME, null, cv);
            } catch (Exception e) {
                Log.d(MainActivity.Tag, "خطا اضافه کردن");
                Log.e("alhabib", e.getMessage());
                return 0;
            }
        }
//        Log.e(MainActivity.Tag, "تکراری ببود"+ res.getString(res.getColumnIndex(MainMenu_Server)));
        return -1;
    }
    public long insertScript(String name, int vis, String server){
        ContentValues cv=new ContentValues();
        long gid=getparent("سناریو", server);
        long id=getmaxcode(server);
        cv.put(MainMenu_ID, id);
        cv.put(MainMenu_Name, name);
        cv.put(MainMenu_VisMain, vis);
        cv.put(MainMenu_Parent, gid);
        cv.put(MainMenu_Vaz, script.ScriptYes);
        cv.put(MainMenu_Server, server);
        if(MainActivity.isClient)
            cv.put(MainMenu_SaveMe, 1);
        else
            cv.put(MainMenu_SaveMe, 0);

        try {
            return databaseWriter.insert(Mainmenu_TABLE_NAME, null, cv);
        }catch (Exception e){
            Log.d(MainActivity.Tag, "خطا اضافه کردن");
            Log.e("alhabib", e.getMessage());
            return 0;
        }
    }
    public void updaterecord(long id,String name, String RemoteKey, int RemoteCode, String Group, String image, int vaz, int saveme, String server){
        ContentValues cv=new ContentValues();
        cv.put(MainMenu_Name, name);
        cv.put(MainMenu_RemoteKey, RemoteKey);
        cv.put(MainMenu_RemoteCode, RemoteCode);
//        cv.put(MainMenu_Group, Group);
        cv.put(MainMenu_Image, image);
        cv.put(MainMenu_Vaz,  vaz);
//        cv.put(MainMenu_VisMain, vis);
        cv.put(MainMenu_Parent, getparent(Group, server));

        try {
            databaseWriter.update(Mainmenu_TABLE_NAME, cv, String.format("%s="+id+" and %s="+saveme+" and %s='"+server+"'",
                            MainMenu_ID, MainMenu_SaveMe, MainMenu_Server), null);
//                    new String[]{String.valueOf(id), String.valueOf(saveme), server});
        }catch (Exception e){
            Log.e("alhabib", "can't update mainmenuitem "+ name);
        }
    }
    public void updatescript(long id, int saveme,String name, String server){
        ContentValues cv=new ContentValues();
        cv.put(MainMenu_Name, name);

        try {
            databaseWriter.update(Mainmenu_TABLE_NAME, cv, String.format("%s="+id+" and %s="+saveme+" and %s='"+server+"'",
                            MainMenu_ID, MainMenu_SaveMe, MainMenu_Server), null);
//                    new String[]{String.valueOf(id), String.valueOf(saveme), server});
        }catch (Exception e){
            Log.e("alhabib", "can't update mainmenuitem "+ name);
        }
    }
    public void updateimgpath(long id, int saveme, String image, String server){
        ContentValues cv=new ContentValues();
        cv.put(MainMenu_Image, image);

        try {
            databaseWriter.update(Mainmenu_TABLE_NAME, cv, String.format("%s="+id+" and %s="+saveme+" and %s='"+server+"'",
                            MainMenu_ID, MainMenu_SaveMe, MainMenu_Server), null);
//                    new String[]{String.valueOf(id), String.valueOf(saveme), server});
        }catch (Exception e){
            Log.e("alhabib", "can't update mainmenuitem "+ id);
        }
    }

    public void setVisMainMenu(long id, int saveme, int vaz, String server){
        ContentValues cv=new ContentValues();
        cv.put(MainMenu_VisMain, vaz);
        try {
            databaseWriter.update(Mainmenu_TABLE_NAME, cv, String.format("%s="+id+" and %s="+saveme+ " and %s='"+server+"'",
                            MainMenu_ID, MainMenu_SaveMe, MainMenu_Server), null);
//                    new String[]{String.valueOf(id), String.valueOf(saveme), server});
        }catch (Exception e){
            Log.e("alhabib", "can't update mainmenuitem "+ id);
        }

    }
    public void deleterecord(long id, int saveme, String server){
        try {
            databaseWriter.delete(Mainmenu_TABLE_NAME, String.format("%s=" + id + " and %s=" + saveme + " and %s='" + server + "'",
                            MainMenu_ID, MainMenu_SaveMe, MainMenu_Server),
                    null);
        }catch (Exception e){
            Log.e("alhabib", "can't delete mainmenuitem "+ id);
            Log.e(MainActivity.Tag, e.getMessage());
        }
    }
    public Cursor query(String where,String[] args){
//        String[] cols=new String[]{MainMenu_ID, MainMenu_Name, MainMenu_RemoteKey, MainMenu_RemoteCode, MainMenu_Image, MainMenu_Vaz, MainMenu_VisMain };
//                        ,MainMenu_Col, MainMenu_Row, MainMenu_Parent};
//        Log.d(MainActivity.Tag, "ورود ");
        String s="select t1.*, t2."+ MainMenu_Name+" as "+MainMenu_Group+
                " from "+Mainmenu_TABLE_NAME+" t1 join "+Mainmenu_TABLE_NAME+" t2 on t1."+MainMenu_Parent+"= t2."+MainMenu_ID;
//        String s= "select * from "+Mainmenu_TABLE_NAME;
        if(where!=null)s=s+" where "+ where;
//        Log.d(MainActivity.Tag, "اس "+ s);
//        Cursor result= databaseReader.query(true, Mainmenu_TABLE_NAME, cols, where, args, null, null, null, null);
        Cursor result= databaseReader.rawQuery(s, args);

        if(result!=null)
            result.moveToFirst();

        return result;
    }

    public String[] querygroup(String where,String[] args){
        Cursor result= databaseReader.rawQuery("select * from " + Mainmenu_TABLE_NAME+ " where "+MainMenu_Parent+"=0", null);
        String[] items ;
            if(result.moveToFirst()) {
            items = new String[result.getCount()];
                int i = 0;
                do {
                    items[i] = result.getString(result.getColumnIndex(MainMenu_Name));
//                    Log.d(MainActivity.Tag, items[i]);
                    i++;
                } while(result.moveToNext());
//                Log.d(MainActivity.Tag, "دیت :  "+ items);
                return items;
            }
        Log.d(MainActivity.Tag, "گروه خالی");
        return new String[0];
    }
    public Cursor querygroup(String where) {
        Cursor result = databaseReader.rawQuery("select * from " + Mainmenu_TABLE_NAME+" where "+MainMenu_Parent+"=0", null);
//        Log.d(MainActivity.Tag, "تعداد "+ result.getCount() );
        if (result != null) {
            result.moveToFirst();
        }
        return result;
    }
    public Cursor queryone(String where) {
        String s= "select * from " + Mainmenu_TABLE_NAME;
        if(where!=null)s=s+" where "+ where;
        Cursor result = databaseReader.rawQuery(s, null);
//        Log.d(MainActivity.Tag, "تعداد "+ result.getCount() );
        if (result != null) {
            result.moveToFirst();
        }
        return result;
    }
    public Cursor queryall() {
        Cursor result = databaseReader.rawQuery("select * from " + Mainmenu_TABLE_NAME, null);
//        Log.d(MainActivity.Tag, "تعداد "+ result.getCount() );
        if (result != null) {
            result.moveToFirst();
        }
        return result;
    }
    public long getmaxcode(String server) {
        // TODO Auto-generated method stub
//        Log.d("alhabib", "Get Code... ");
        try {
            Cursor res = databaseReader.rawQuery(
                    "select max(id) as mcode from " + Mainmenu_TABLE_NAME, null);
//            Log.d("alhabib", "Get Code ");
            long id = 1;
            if (res != null) {
                res.moveToFirst();
                id = res.getLong(0);
                id++;
            }
//            id*=10;
//            if(MainActivity.isClient) id+=1;

            return id;
        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.e("alhabib", e.getMessage());
            return 1;
        }
    }
    public int getRemotecode() {
        // TODO Auto-generated method stub
        Log.d(MainActivity.Tag, "Get Remote Code... ");
        try {
            Cursor res = databaseReader.rawQuery(String.format(
                    "select max(%s) as mcode from %s" ,MainMenu_RemoteCode, Mainmenu_TABLE_NAME), null);
            int id = 1;
            if (res != null) {
                res.moveToFirst();
                id = res.getInt(0);
                id++;
                if(id>=255){
                    id=-1;
                    for(int i=0;i<255; i++){
                        res = databaseReader.rawQuery(String.format(
                                "select %s from %s where %s=%d",MainMenu_RemoteCode, Mainmenu_TABLE_NAME, MainMenu_RemoteCode, i), null);
                        if (!res.moveToFirst()){
                            id=i; i=300;}
                    }
                    Log.e(MainActivity.Tag, "رمیوت کد تولید شده " + id);
                }
            }
            return id;
        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.e(MainActivity.Tag, e.getMessage());
            return 1;
        }
    }
}
