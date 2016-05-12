package com.parmissmarthome.parmis_smart_home;

import android.content.Context;
import android.content.SharedPreferences;

import java.net.InetAddress;

/**
 * Created by YA-MAHDI on 15/12/2015.
 */
public class InfoMe {
    String andMacAddress="", macme="";
    String CurrentSSID,
        UserSSID,
        UserPass;
    String IPme="192.168.4.1";

    InetAddress IPCenter;//= InetAddress.getByName("192.168.4.1");
    int PORTCenter=3344;

    String MacCenter="19:2f:16:84:01:bf";
    SharedPreferences shpref;

    public InfoMe(Context context) {
        shpref= context.getSharedPreferences("ParmisSmartHome_sh", Context.MODE_PRIVATE);
        UserPass= shpref.getString("UserPass", null);
        UserSSID= shpref.getString("UserSSID", null);
    }

    public void SaveMe(){
        SharedPreferences.Editor edit= shpref.edit();
        edit.putString("UserPass", UserPass);
        edit.putString("UserSSID", UserSSID);
        edit.commit();
    }
}
