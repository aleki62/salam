package com.parmissmarthome.parmis_smart_home.db;

import android.util.Log;

import com.parmissmarthome.parmis_smart_home.MainActivity;

import java.util.ArrayList;

/**
 * Created by YA-MAHDI on 27/01/2016.
 */
public class ScriptIsRuning {
    ArrayList<dataScriptIsRunning> datascript;

    public ScriptIsRuning() {
        this.datascript = new ArrayList<dataScriptIsRunning>();
    }

    public boolean isrunning(long scriptid){
        for (dataScriptIsRunning ds:datascript)
            if (ds.getScriptid()== scriptid)
                return true;
        return false;
    }

    public boolean add(long scriptid, ArrayList<runScriptValue> rsv){
        datascript.add(new dataScriptIsRunning(scriptid, rsv));
        Log.d(MainActivity.Tag, scriptid+ "سناریو اضافه شد");
        return true;
    }

    public void run(){
        for (int i=0;i<datascript.size(); i++){
            if (datascript.get(i).nexttime++ >= datascript.get(i).runitem.get(datascript.get(i).index).getDelay()) {
                MainActivity.sendtocenterrf(datascript.get(i).runitem.get(datascript.get(i).index).getCommand(), null, Long.valueOf(-1), -1);
                datascript.get(i).nexttime=0;

                if (++datascript.get(i).index>= datascript.get(i).runitem.size()){
                    Log.d(MainActivity.Tag, datascript.get(i).getScriptid()+ "سناریو به پایان رسید");
                    datascript.remove(i);
                }
            }
        }
    }

    private class dataScriptIsRunning {
        ArrayList<runScriptValue> runitem = new ArrayList<>();

        long scriptid;
        int nexttime = 0;

        int index = 0;

        public dataScriptIsRunning(long sid, ArrayList<runScriptValue> runitem) {
            this.runitem = runitem;
            scriptid = sid;
            nexttime = 0;
            index = 0;
        }

        public long getScriptid() {
            return scriptid;
        }

    }

}
