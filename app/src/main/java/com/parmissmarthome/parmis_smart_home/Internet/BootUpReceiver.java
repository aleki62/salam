package com.parmissmarthome.parmis_smart_home.Internet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by mohammad on 3/14/2016.
 */
public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /****** For Start Activity *****
        Intent i = new Intent(context, getRequest.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

        /***** For start Service  ****
        Intent myIntent = new Intent(context, getRequest.class);
        context.startService(myIntent);*/
    }
}
