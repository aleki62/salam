package com.parmissmarthome.parmis_smart_home;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.parmissmarthome.parmis_smart_home.db.MobileDB;
import com.parmissmarthome.parmis_smart_home.db.mainmenudb;
import com.parmissmarthome.parmis_smart_home.db.scriptdb;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyBCReceiver extends BroadcastReceiver {
    SharedPreferences sharedPreferences;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;
    public MyBCReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                        MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        sharedPreferences= mActivity.getSharedPreferences("ParmisSmartHome_client", Context.MODE_PRIVATE);
    }
    private  boolean runsms(String sms){
//        *mac,id,state,#
        String[] psms= MainActivity.splitdb(sms);
//        MainActivity.scriptIsRuning.add(mainmenuList.get(groupPosition).getitem(childPosition).getId(),
//                dbs.getscriptvalues(mainmenuList.get(groupPosition).getitem(childPosition).getId(), null));
        MobileDB mdb= new MobileDB(mActivity);
        MainActivity.ConfigClient cc= mdb.acceptlater(psms[0]);
        if (cc!= null) {
            mainmenudb db = new mainmenudb(mActivity);
            Cursor mcursor = db.query("t1.id=" + psms[1], null);
//            Log.d(MainActivity.Tag, "sms id: "+ psms[1]+ "db id: "+ mcursor.getLong(mcursor.getColumnIndex(mainmenudb.MainMenu_ID)));

            if (mcursor.moveToFirst()) {
                switch (mcursor.getInt(mcursor.getColumnIndex(mainmenudb.MainMenu_Vaz))) {
                    case ((script.ScriptYes)):
                        if (MainActivity.scriptIsRuning.isrunning(mcursor.getInt(mcursor.getColumnIndex(mainmenudb.MainMenu_ID)))) {
                            Log.d(MainActivity.Tag, "سناریوی انتخابی در حال اجرا می باشد");
                        } else {
                            scriptdb dbs = new scriptdb(mActivity);

                            MainActivity.scriptIsRuning.add(mcursor.getInt(mcursor.getColumnIndex(mainmenudb.MainMenu_ID)),
                                    dbs.getscriptvalues(mcursor.getInt(mcursor.getColumnIndex(mainmenudb.MainMenu_ID)), null));
                        }
                        break;
                    case script.ScriptToggle:
                        MainActivity.sendtocenterrf(String.format("*SNDRF*%s*%s#",
                                mcursor.getString(mcursor.getColumnIndex(mainmenudb.MainMenu_RemoteKey)), "-1"), null, Long.valueOf(-1), -1);
                        break;
                    case script.ScriptOFF:
                        Log.d(MainActivity.Tag, "ارسال کد اندروید");
                        MainActivity.sendtocenterrf(String.format("*SNDRF*%s*%s#",
                                mcursor.getLong(mcursor.getColumnIndex(mainmenudb.MainMenu_RemoteKey))+
                                        mcursor.getLong(mcursor.getColumnIndex(mainmenudb.MainMenu_RemoteCode)), psms[2]), null, Long.valueOf(-1), -1);
                        break;
                }
            }
            return true;
        }
        return false;
    }
        @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
            String action = intent.getAction();
            if(action.contains("SMS_RECEIVED")){
//                Log.i(MainActivity.Tag, "Intent recieved: " + intent.getAction());
                Bundle extras = intent.getExtras();
                Log.e(MainActivity.Tag, "دریافت پیامک");
                String strMessage = "";

                if ( extras != null )
                {
                    Object[] smsextras = (Object[]) extras.get( "pdus" );

//                    String[] sms= new String[smsextras.length];
                    for ( int i = 0; i < smsextras.length; i++ )
                    {
                        SmsMessage smsmsg = SmsMessage.createFromPdu((byte[])smsextras[i]);

                        String strMsgBody = smsmsg.getMessageBody().toString();
                        String strMsgSrc = smsmsg.getOriginatingAddress();

                        strMessage += "SMS from " + strMsgSrc + " : " + strMsgBody;
                        if(runsms(strMsgBody))
                            abortBroadcast();
                        Log.i(MainActivity.Tag, strMessage);
                    }

                }
            }
            if (action.equals(Intent.ACTION_BOOT_COMPLETED)){
                Log.e(MainActivity.Tag, "شروع اندروید");
            }else
            if (action.equals(Intent.ACTION_SCREEN_ON))
            {
                Log.e(MainActivity.Tag, "باز شدن قفل");
//                Intent aintent = new Intent(context, MainActivity.class);
//                aintent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
//                context.startActivity(aintent);
//                MainActivity.isLockScreen= false;
            }else
            if (action.equals(Intent.ACTION_SCREEN_ON))
            {
                Log.e(MainActivity.Tag, "باز شدن قفل");
//                Intent aintent = new Intent(context, MainActivity.class);
//                aintent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
//                context.startActivity(aintent);
//                MainActivity.isLockScreen= true;
            }else
            if (action.equals("com.poujman.ReceiveParmisSmartHome")) {
                String data = intent.getStringExtra("data");
                Log.d(MainActivity.Tag, data);
                int p= data.indexOf("macis:");
                if (p!=-1){
                    data=data.substring(p + 6);
//                    if(sharedPreferences.getString(data, null)==null){
                        confirmjoin(data);
//                    }
//                    findmac(data);

                }
                /*Intent downloadActivityIntent = new Intent(context, MainActivity.class);
                downloadActivityIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(downloadActivityIntent);
*/
            }else if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
/*                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // Wifi P2P is enabled
                    Toast.makeText(mActivity, "بی سیم وصل شد", Toast.LENGTH_LONG).show();
                } else {
                    // Wi-Fi P2P is not enabled
                    Toast.makeText(mActivity, "بی سیم قطع شئ", Toast.LENGTH_LONG).show();
                }*/
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                // Respond to new connection or disconnections
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                // Respond to this device's wifi state changing
            }
            if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                MainActivity.wificonnect= info.isConnected();
//                Log.e("alhabib", MainActivity.infoMe.CurrentSSID + " وصل شد");

                if (info != null && info.isConnected()) {
                    // Do your work.
                    // e.g. To ch eck the Network Name or other info:
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    MainActivity.infoMe.CurrentSSID = wifiInfo.getSSID().replaceAll("\"","");
                    Toast.makeText(mActivity, MainActivity.infoMe.CurrentSSID + " وصل شد", Toast.LENGTH_LONG).show();
                    Log.d(MainActivity.Tag, "شبکه کاربر "+ MainActivity.infoMe.UserSSID);

                    if (!MainActivity.infoMe.CurrentSSID.equals(MainActivity.ParmisSHRFSender)) {
                        int ipAddress = wifiInfo.getIpAddress();

                        String ss = String.format(Locale.getDefault(), "%d.%d.%d.%d",
                                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
                        if (ss.indexOf("0.0")==-1) {
                            MainActivity.infoMe.IPme = ss;
                            if(!MainActivity.isClient && ss.substring(ss.lastIndexOf('.'))!=".92"){
                                ss= ss.substring(0, ss.lastIndexOf('.')) + ".92";
                                MainActivity.infoMe.IPme =ss;
//
                                WifiConfiguration wifiConf = null;
                                List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
                                for (WifiConfiguration conf : configuredNetworks){
                                    if (conf.networkId == wifiInfo.getNetworkId()){
                                        wifiConf = conf;
                                        break;
                                    }
                                }                                try{
                                    setIpAssignment("STATIC", wifiConf); //or "DHCP" for dynamic setting
                                    setIpAddress(InetAddress.getByName(ss), 24, wifiConf);
                                    setGateway(InetAddress.getByName(ss.substring(0, ss.lastIndexOf('.')) + ".1"), wifiConf);
                                    setDNS(InetAddress.getByName("8.8.8.8"), wifiConf);
                                    wifiManager.updateNetwork(wifiConf); //apply the setting
                                    wifiManager.saveConfiguration(); //Save it
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                Log.d(MainActivity.Tag, "IP has been changed");
                            }
                        }
                        Log.e(MainActivity.Tag, "Ip is: " + MainActivity.infoMe.IPme + " شبکه کاربر " + MainActivity.infoMe.UserSSID);
/*                        MainActivity.infoMe.IPCenter = MainActivity.infoMe.IPme.substring(0, MainActivity.infoMe.IPme.lastIndexOf('.')) + ".91";
                        MainActivity.infoMe.PORTCenter = 3344;
//                        MainActivity.infoMe.IPCenter = "192.168.4.1";
                        if ((MainActivity.mTcpClient==null || !MainActivity.mTcpClient.isBusy()))
                            MainActivity.ConnectTask();
  */                  }else{
                        if(MainActivity.RegCenterLevel == 1){
                            MainActivity.RegCenterLevel=2;
                            MainActivity.ConnectTask();
                        }

                    }

                }else
                    if(MainActivity.mTcpClient!= null && MainActivity.mTcpClient.isConnected()){
                        Log.d(MainActivity.Tag, "اتصال به شبکه قطع شد");
                        MainActivity.mTcpClient.stopClient();
                        MainActivity.mTcpClient= null;
                    }
            }
    }

    private void confirmjoin(final String data) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    int p= MainActivity.findlistClientMobiles(data);
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            // Yes button clicked
//                            findmac(data);
                            if (p!=-1){
                                MainActivity.listClientMobiles.get(p).sendmessage("*Accept#");
                                Log.e(MainActivity.Tag, "بله");

                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            // No button clicked
                            // do nothing
//                            if (p!=-1)
//                                MainActivity.listClientMobiles.get(p).client.stop();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(String.format("دستگاه با شماره %s قصد ارتباط را دارد. آیا اجازه می دهید؟", data))
                    .setPositiveButton("بله", dialogClickListener)
                    .setNegativeButton("خیر", dialogClickListener).show();
    }

    private void findmac(String substring) {
        if (sharedPreferences.getString(substring, null)==null){
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putString(substring, substring);
            editor.commit();
        }
    }



    public static void setIpAssignment(String assign , WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
        setEnumField(wifiConf, assign, "ipAssignment");
    }

    public static void setIpAddress(InetAddress addr, int prefixLength, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
            NoSuchMethodException, ClassNotFoundException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if(linkProperties == null)return;
        Class laClass = Class.forName("android.net.LinkAddress");
        Constructor laConstructor = laClass.getConstructor(new Class[]{InetAddress.class, int.class});
        Object linkAddress = laConstructor.newInstance(addr, prefixLength);

        ArrayList mLinkAddresses = (ArrayList)getDeclaredField(linkProperties, "mLinkAddresses");
        mLinkAddresses.clear();
        mLinkAddresses.add(linkAddress);
    }

    public static void setGateway(InetAddress gateway, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
            ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if(linkProperties == null)return;
        Class routeInfoClass = Class.forName("android.net.RouteInfo");
        Constructor routeInfoConstructor = routeInfoClass.getConstructor(new Class[]{InetAddress.class});
        Object routeInfo = routeInfoConstructor.newInstance(gateway);

        ArrayList mRoutes = (ArrayList)getDeclaredField(linkProperties, "mRoutes");
        mRoutes.clear();
        mRoutes.add(routeInfo);
    }

    public static void setDNS(InetAddress dns, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
        Object linkProperties = getField(wifiConf, "linkProperties");
        if(linkProperties == null)return;

        ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>)getDeclaredField(linkProperties, "mDnses");
        mDnses.clear(); //or add a new dns address , here I just want to replace DNS1
        mDnses.add(dns);
    }

    public static Object getField(Object obj, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);
        return out;
    }

    public static Object getDeclaredField(Object obj, String name)
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

    public static void setEnumField(Object obj, String value, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }
}
