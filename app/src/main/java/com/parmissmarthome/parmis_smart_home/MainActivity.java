package com.parmissmarthome.parmis_smart_home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parmissmarthome.parmis_smart_home.Internet.HttpManager;
import com.parmissmarthome.parmis_smart_home.Internet.RequestPackage;
import com.parmissmarthome.parmis_smart_home.db.MobileDB;
import com.parmissmarthome.parmis_smart_home.db.ScriptIsRuning;
import com.parmissmarthome.parmis_smart_home.db.mainmenudb;
import com.parmissmarthome.parmis_smart_home.db.scriptdb;
import com.pushlink.android.PushLink;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String Tag="alhabib";
    public static final boolean isClient=false;
    public static String MacServer;
    static ConfigClient configClient;
    public static boolean isRunApp=false;
    public static boolean isLockScreen=false;

    public static final String cmdGiveMeSQL= "GiveMeSQL";
    public static final String cmdSendDBComplete= "SendDBComplete";
    public static final String cmdGetImageDB= "GetImageDB";
    public static final String cmdNextMeSQL= "NextMeSQL";
    public static final String cmdSendRF= "SNDRF";
    public static final String cmdAccept= "Accept";
    public static final String cmdGetScriptDB= "GetScriptDB";
    public static final String cmdNextScriptDB= "NextScriptDB";
    public static final String cmdScripDBComplete= "ScriptDBComplete";

    List<MyTask> tasks;
    private Timer timer = null;//new Timer();
    private static boolean connecttorf=false;
    private int closeconnection= 0;
//    public static MenuItem action_wifistate;

    public static ArrayList<runScript.DeviceSocketThread> listClientDevices= new ArrayList<>();
    public static final int rqt_Lamp=1;
    public static final int rqt_script=2;

    static Context context;
    public static boolean changedata=false, wificonnect=false;
    public static TCPClient mTcpClient;
    private static TCPClient sTcpClient;
    public static ProgressDialog progressBar;
    public static String GetRF="";
    public static InfoMe infoMe;
    public static int RegCenterLevel=-1;
    public static int TimeConnectRFCenter=0;
    public static String ParmisSHRFSender="ParmisSHCenter";
    public static String PassParmisSHRFSender="12345678";

    public static ScriptIsRuning scriptIsRuning= new ScriptIsRuning();

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    static WifiManager wifiManager;

    public static ArrayList<ListeningServices.clientSocketThread> listClientMobiles=new ArrayList<>();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    static SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private static File file;
    private NsdManager mNsdManager;
    public static final String SERVICE_NAME = "Server Parmis Smart Home";
    public static final String SERVICE_Client_NAME = "Client Parmis Smart Home";
    public static final String SERVICE_TYPE = "_http._tcp.";

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
//    private View mControlsView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
//    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
//            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        infoMe= new InfoMe(this);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()) wifiManager.setWifiEnabled(true);

        WifiInfo wInfo = wifiManager.getConnectionInfo();
        infoMe.macme = wInfo.getMacAddress();
        MacServer= infoMe.macme;

        if(!isClient) {
            PushLink.start(this, R.mipmap.ic_launcher, "54gtc9gt5on2jma5", infoMe.macme);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        configClient= new ConfigClient(0, 0, false, null, false, null);
        tasks = new ArrayList<>();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                mainmenudb mdb= new mainmenudb(context);
//                mdb.insertarraysocket("101", "haaaaal");
//runsms("18:89:5B:E2:9F:35,1,128,");
//                SendInternet("*SNDRF*12345678*0#");
            }
        });
        context= this;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (changedata && position == 0) {
                    changedata = false;
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    Log.d(Tag, "صفحه جابجا شد");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        infoMe.andMacAddress= infoMe.macme.replaceAll(":", "");

        infoMe.andMacAddress =
                Long.toHexString(Long.parseLong(infoMe.andMacAddress.substring(0, 2), 16) ^
                        Long.parseLong(infoMe.andMacAddress.substring(6, 8), 16))+
                        Long.toHexString(Long.parseLong(infoMe.andMacAddress.substring(2, 4), 16) ^
                                Long.parseLong(infoMe.andMacAddress.substring(8, 10), 16))+
                        Long.toHexString(Long.parseLong(infoMe.andMacAddress.substring(4, 6), 16) ^
                                Long.parseLong(infoMe.andMacAddress.substring(10), 16));
        infoMe.andMacAddress= String.valueOf((Integer.valueOf(infoMe.andMacAddress, 16)));
        if (Integer.valueOf(infoMe.andMacAddress)>16776960)
            infoMe.andMacAddress= "16776960";
//        Log.e(Tag, "Mac is:"+ infoMe.andMacAddress);

        mIntentFilter = new IntentFilter();
//        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
//        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
//        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
//        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        if (!isClient) {
            mIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
            mIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        }
        mIntentFilter.addAction("com.poujman.ReceiveParmisSmartHome");//getString(R.string.brdReceiverParmisSmartHome));
        mIntentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        mIntentFilter.addCategory(Intent.CATEGORY_HOME);
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new MyBCReceiver(mManager, mChannel, this);

        if (!isClient) {
            startService(new Intent(getBaseContext(), ListeningServices.class));
//            startService(new Intent(getBaseContext(), runScript.class));
        }
        else {
            mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        }
        registerReceiver(mReceiver, mIntentFilter);
        mContentView = findViewById(R.id.mainback);
//        mControlsView = findViewById(R.id.fullscreen_content_controls);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        if(!isClient)
//            delayedHide(100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Tag, "Resume ...");
        isRunApp=true;
        isLockScreen= false;

        if (isClient && mNsdManager != null) {
            mNsdManager.discoverServices(
                    SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
        }
        if (closeconnection==0) {
            if (!isClient) {
                startService(new Intent(getBaseContext(), runScript.class));
            }
            closeconnection = -1;
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    invalidateOptionsMenu();
//                    if(!isClient)
//                        delayedHide(100);
                    if (closeconnection == 0) {
                        if (!isClient) {
                            if (listClientMobiles.size() == 0) {
                                Log.d(Tag, "تعداد آراف:" + listClientDevices.size());
                                for (int i = 0; i < listClientDevices.size(); i++) {
                                    listClientDevices.get(i).stopclient();
                                    Log.d(Tag, "Stop client " + i);
                                }
                                stopService(new Intent(getBaseContext(), runScript.class));
                            }
                        }
                        timer.cancel();
                    } else if (closeconnection > 0) closeconnection--;
                }
            }, 0, 1000);
        }else closeconnection=-1;
    }

    @Override
    protected void onPause() {
//        unregisterReceiver(mReceiver);
        Log.d(Tag, "Pause ...");
        isRunApp= false;
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        isLockScreen = !powerManager.isScreenOn();

        if (isClient && mNsdManager != null) {
            mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            if (sTcpClient!=null)
                sTcpClient.stopClient();
        }

//        closeconnection=3600;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
    private void hide() {
        // Hide UI first
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
//        mControlsView.setVisibility(View.GONE);
//        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public static void ConnectTask(){
//        if (mTcpClient==null)
        if(!infoMe.IPCenter.equals("0.0.0.91")) {
//            Log.e(Tag, "ورود به تابع ساخت کلاینت"+ infoMe.IPCenter);
            new connectTask().execute("");
        }
    }

    public static void sendSmsByManager(String phone, String sms) {

        try {
            // Get the default instance of the SmsManager
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone,
                    null,
                    sms,
                    null,
                    null);
            Toast.makeText(context, "Your sms has successfully sent!",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(context,"Your sms has failed...",
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
    public static boolean sendtocenterrf(final String cmd, View view, final Long id, final int state){
        if(!isClient) {
            if (listClientDevices.size() > 0)
                if (listClientDevices.get(0).DeviceType == "RF") {
                    listClientDevices.get(0).sendmessage(cmd);
                    return true;
                }
            Log.e(Tag, "اتصال به مبدل برقرار نیست!!!!!!!!!!");
            if (view != null)
                Snackbar.make(view, "اتصال به مبدل برقرار نیست!!!!!!!!!!", Snackbar.LENGTH_LONG).show();
        }else{
            if (sTcpClient!=null)
                sTcpClient.sendMessage(cmd);
            else
                Snackbar.make(view, "اتصال به سرور برقرار نیست!!!!!!!!", Snackbar.LENGTH_LONG)
                        .setAction("پیامک", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(id!=-1)
                                    sendSmsByManager("09355669277", infoMe.macme+","+ id+ ","+ state+",");
                            }
                        })
                        .show();
        }

        return false;
    }
    NsdManager.DiscoveryListener mDiscoveryListener = new NsdManager.DiscoveryListener() {

        // Called as soon as service discovery begins.
        @Override
        public void onDiscoveryStarted(String regType) {
            Log.d(Tag, "Service discovery started");
        }

        @Override
        public void onServiceFound(NsdServiceInfo service) {
            // A service was found! Do something with it.
            Log.d(Tag, "Service discovery success : " + service);
            Log.d(Tag, "Host = "+ service.getServiceName());
            Log.d(Tag, "port = " + String.valueOf(service.getPort()));

            if (!service.getServiceType().equals(SERVICE_TYPE)) {
                // Service type is the string containing the protocol and
                // transport layer for this service.
                Log.d(Tag, "Unknown Service Type: " + service.getServiceType());
            } else if (service.getServiceName().equals(SERVICE_Client_NAME)) {
                // The name of the service tells the user what they'd be
                // connecting to. It could be "Bob's Chat App".
                Log.d(Tag, "Same machine: " + SERVICE_Client_NAME);
            } else {
                Log.d(Tag, "Diff Machine : " + service.getServiceName());
                // connect to the service and obtain serviceInfo
                mNsdManager.resolveService(service, new nsdResolveListener());//mResolveListener);
            }
        }

        @Override
        public void onServiceLost(NsdServiceInfo service) {
            // When the network service is no longer available.
            // Internal bookkeeping code goes here.
            Log.e(Tag, "service lost" + service);
            if(sTcpClient!=null && sTcpClient.isConnected()){
                sTcpClient.stopClient();
                sTcpClient= null;
            }
        }

        @Override
        public void onDiscoveryStopped(String serviceType) {
            Log.i(Tag, "Discovery stopped: " + serviceType);
        }

        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(Tag, "Discovery failed: Error code:" + errorCode);
            mNsdManager.stopServiceDiscovery(this);
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(Tag, "Discovery failed: Error code:" + errorCode);
            mNsdManager.stopServiceDiscovery(this);
        }
    };

    //    NsdManager.ResolveListener mResolveListener = new NsdManager.ResolveListener() {
    private class nsdResolveListener implements NsdManager.ResolveListener {
        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            // Called when the resolve fails. Use the error code to debug.
            Log.e(Tag, "Resolve failed " + errorCode);
            Log.e(Tag, "serivce = " + serviceInfo);
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.d(Tag, "Resolve Succeeded. " + serviceInfo);

            if (serviceInfo.getServiceName().equals(SERVICE_Client_NAME)) {
                Log.d(Tag, "Same IP.");
                return;
            }

            // Obtain port and IP
            infoMe.PORTCenter = serviceInfo.getPort();
            infoMe.IPCenter = serviceInfo.getHost();

            new ConnectToServer().execute("");
        }
    };
    /*************************************************************************************************************/
    public static Integer parseInt(String data, int defaultvalue) {
        Integer val = defaultvalue;
        try {
            val = Integer.parseInt(data);
        } catch (NumberFormatException nfe) { }
        return val;
    }
    public static long parseLong(String data, long defaultvalue) {
        long val = defaultvalue;
        try {
            val = Long.parseLong(data);
        } catch (NumberFormatException nfe) { }
        return val;
    }
    public static String[] splitdb(String string){
        String[] result= new String[20];
        int i=0, p=0, p2= string.indexOf(",");

        while(p2!=-1){
            result[i++]=string.substring(p, p2);
            p=p2+1;
            p2= string.indexOf(",", p);
        }
        return result;
    }
    public static class ConfigClient{
        int addKey=0;
        int addScript=0;
        boolean sms= false;
        boolean web= false;
        String phone;
        String mac;

        public ConfigClient(int addLamp, int addScript, boolean sms, String phone, boolean web, String mac) {
            this.addKey = addLamp;
            this.addScript = addScript;
            this.sms= sms;
            this.web= web;
            this.phone= phone;
            this.mac= mac;
        }
        public String stringme(){
            return addKey+","+ addScript+","+ (sms)+","+ phone+","+ web+ ","+ mac+",";
        }
/*        public int getAddScript() {return addScript;}
        public void setAddScript(int addScript) {this.addScript = addScript;}
        public int getAddLamp() {return addLamp;}
        public void setAddLamp(int addLamp) {this.addLamp = addLamp;}*/
    }

    public static class ConnectToServer extends AsyncTask<String,String,TCPClient> {


        @Override
        protected TCPClient doInBackground(String... message) {
            Log.d(Tag, "اتصال به تبلت");
            //we create a TCPClient object and
            sTcpClient = new TCPClient(infoMe.IPCenter, infoMe.PORTCenter, new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            sTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.d(Tag, values[0]);

            int p = values[0].indexOf("getmac");
            if (p != -1) {
                sTcpClient.sendMessage("*macis:" + infoMe.macme);
            } else if (values[0].indexOf("Accept") != -1) {

                String[] resprm= splitdb(values[0]);
                MacServer = resprm[1];//values[0].substring(values[0].indexOf(":") + 1);
                MobileDB mobileDB=new MobileDB(context);
//                configClient.addKey=parseInt(resprm[2],0);
//                configClient.addScript=  parseInt(resprm[3], 0);
                configClient= new ConfigClient(parseInt(resprm[2],0), parseInt(resprm[3], 0),
                        Boolean.valueOf(resprm[4]), resprm[5], Boolean.valueOf(resprm[6]), resprm[7]);
                if(mobileDB.acceptlater(MacServer)==null)
                    mobileDB.insertrecord(MacServer, configClient.addKey, configClient.addScript, 0, 0, 0, null, null,
                            configClient.phone, (configClient.web)?1:0);


                Log.d(Tag, "Mac Server is: " + MacServer);

                sTcpClient.sendMessage(MainActivity.cmdGiveMeSQL);

            } else {
                p = values[0].indexOf("mainmenudb");
                if (p != -1) {
                    mainmenudb mmenudb = new mainmenudb(MainActivity.context);
                    String[] strings = splitdb(values[0].substring(p + 11));
//                    (Long id, String name, String RemoteKey, int RemoteCode, String image, int vaz, int vis, int col, int row, long parent){
//                    Log.d(MainActivity.Tag, "Data : " + strings[9] + "..." + values[0].substring(p + 11));

                    p = (int) mmenudb.insertarraysocket(parseLong(strings[0], 1), strings[1], strings[2], parseInt(strings[3], 0),
                            strings[4], parseInt(strings[5], 0), parseInt(strings[6], 0), parseInt(strings[7], 0), parseInt(strings[8], 0),
                            parseLong(strings[9], 0), strings[10], parseInt(strings[11], 0));//values[0].substring(p+11).split(","));
                    Log.d(MainActivity.Tag, "Inserted: " + p + " old id: " + strings[0]);
                    if (strings[4].equalsIgnoreCase("null")) {
                        sTcpClient.sendMessage(MainActivity.cmdNextMeSQL);
                    }
                    else {
                        sTcpClient.sendMessage(MainActivity.cmdGetImageDB);
                    }
                } else if (values[0].contains(cmdSendDBComplete)) {
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    sTcpClient.sendMessage(MainActivity.cmdGetScriptDB);
                }else if(values[0].contains("scriptdb")){
                    scriptdb mmenudb = new scriptdb(MainActivity.context);
                    String[] strings = splitdb(values[0].substring(p + 9));
//                    (Long id, String name, String RemoteKey, int RemoteCode, String image, int vaz, int vis, int col, int row, long parent){
//                    Log.d(MainActivity.Tag, "Data : " + strings[9] + "..." + values[0].substring(p + 11));

                    p = (int) mmenudb.insertfromssocket(parseLong(strings[0], 1), parseLong(strings[1], 1), parseLong(strings[2], 1), strings[3], parseInt(strings[4], 0),
                            parseInt(strings[5], 0), parseInt(strings[6], 0), strings[7], strings[8]);
                    sTcpClient.sendMessage(MainActivity.cmdNextScriptDB);
                }
            }
        }
    }

    /*****************************************************************************************************************************/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainactivity, menu);
//        action_wifistate= menu.findItem(R.id.action_wifistate);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
//            Intent intent= new Intent(this, AddLamp.class);
//            startActivityForResult(intent, rqt_Lamp);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem= menu.findItem(R.id.action_wifistate);
        if (connecttorf||(sTcpClient!=null&&sTcpClient.isConnected()))
            menuItem.setIcon(getResources().getDrawable(R.mipmap.ic_wificonnect));
        else
            menuItem.setIcon(getResources().getDrawable(R.mipmap.ic_wifidisconnect));
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_new) {
            Intent intent= new Intent(this, addaction.class);
            startActivityForResult(intent, rqt_Lamp);
            // Handle the camera action
        } else if (id == R.id.nav_script) {
            startActivityForResult(new Intent(this, addscript.class), rqt_script);
        } else if (id == R.id.nav_addcenter) {
            addcenterrf();
//        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

//        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(MainActivity.Tag, "برگشت از ویرایش");
        switch (requestCode) {
            case rqt_Lamp:
                if (resultCode == RESULT_OK) {
                    Log.d(Tag, "دیتا تغییر کرد");
                    mSectionsPagerAdapter.notifyDataSetChanged();
                }
                break;
            case rqt_script:
                if (resultCode == RESULT_OK) {
                    mSectionsPagerAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    public static int findlistClientMobiles(String noname) {
        for (int i=0;i<listClientMobiles.size();i++){
            if(listClientMobiles.get(i).NameClient.equals(noname))
                return i;
        }
        return -1;
    }
    public static int findlistClientDevice(String noname, Date date) {
        for (int i=0;i<listClientDevices.size();i++){
            if(listClientDevices.get(i).NameClient.equals(noname)&& listClientDevices.get(i).date!=null&& listClientDevices.get(i).date.equals(date))
                return i;
        }
        return -1;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fr=null;
            if (position==0)
                fr= frgMainMenu.newInstance();
            else
                fr= frgAllProgram.newInstance(null, null);
            return fr;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        public boolean beginFakeDrag(){
//            Log.d(MainActivity.Tag, "انتقال");
            return false;
        }
        @Override
        public int getItemPosition(Object object) {
//            return super.getItemPosition(object);
//            if (object instanceof frgAllProgram)
            return POSITION_NONE;
//            return POSITION_UNCHANGED;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
    /*        switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
      */      return null;
        }

    }
    /*********************************************************************************************************/
    public static void setConnecttorf(boolean contorf) {
        connecttorf = contorf;
    }
    /*********************************************************************************************************/
    public static Bitmap setPic(String mPhotoPath,int targetW,int targetH, String filename) {
        // Get the dimensions of the View
//        int targetW = mImageView.getWidth();
//        int targetH = mImageView.getHeight();
//        Log.d(Tag, "فایل "+ mPhotoPath);

        if(mPhotoPath==null||mPhotoPath=="") return  null;
        mPhotoPath= mPhotoPath.replace("file:","");
//        File f=new File(mPhotoPath);
//        if(f.length()>0) return null;
        try {

            if(targetH==0) targetH=1;
            if(targetW==0)targetW=1;

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//        Log.d(MainActivity.Tag, "photow:"+ photoW+", targetW:"+targetW);
//        Log.d(MainActivity.Tag, mPhotoPath);
            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath, bmOptions);

            int  x, y;
            targetH= bitmap.getHeight();
            targetW= bitmap.getWidth();
            if(targetH>targetW){
                x= 0;
                y= (bitmap.getHeight()- targetH)/2;
                targetH= targetW;
            }else{
                x= (bitmap.getWidth()- targetW)/2;
                y=0;
                targetW= targetH;
            }

            bitmap= Bitmap.createBitmap(bitmap, x, y, targetH, targetW);

            Bitmap imageRounded = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            Canvas canvas = new Canvas(imageRounded);
            Paint mpaint = new Paint();
            mpaint.setAntiAlias(true);
            mpaint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            canvas.drawRoundRect((new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight())), 8, 8, mpaint);// Round Image Corner 100 100 100 100
//            canvas.drawRoundRect((new RectF(x, y, targetH, targetW)), 8, 8, mpaint);// Round Image Corner 100 100 100 100

            if (filename!=null){
                saveToInternalStorage(imageRounded, filename);
            }
            return imageRounded;//Bitmap.createBitmap(bitmap, 0, 0, targetH, targetW);
        }catch (Exception e){

            return BitmapFactory.decodeResource(Resources.getSystem(), R.mipmap.ic_launcher);
        }
//        mImageView.setImageBitmap(bitmap);
    }
    public static Bitmap combineImages(Bitmap c, Bitmap s) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int width, height = 0;

        if(c.getWidth() > s.getWidth()) {
            width = c.getWidth() + s.getWidth();
            height = c.getHeight();
        } else {
            width = s.getWidth() + s.getWidth();
            height = c.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, 0f, 0f, null);

        // this is an extra bit I added, just incase you want to save the new image somewhere and then return the location
    /*String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";

    OutputStream os = null;
    try {
      os = new FileOutputStream(loc + tmpImg);
      cs.compress(CompressFormat.PNG, 100, os);
    } catch(IOException e) {
      Log.e("combineImages", "problem combining images", e);
    }*/

        return cs;
    }
    /**************************************************************************************************/
    public static String savebytetofile(byte[] mybyte, String fn, String id, String saveme, String server) throws IOException {
//        ContextWrapper cw = new ContextWrapper(context);
        /*// path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            Log.e(Tag, "Create Directory");
            directory.mkdir();
        }*/
        String filepath = Environment.getExternalStorageDirectory().getPath()+"/ParmisSmartHome/";
        File file = new File(filepath );
        if (!file.exists()) {
            file.mkdirs();
        }
        // Create imageDir
        File mypath = new File(filepath, fn);
//        mypath.deleteOnExit();
//        Log.d(Tag, "File smart home: "+ mypath.getAbsolutePath());
        if(mypath.exists()){
            mypath.delete();
//            Log.e(Tag, "فایل وجود دارد");
        }
        mypath.deleteOnExit();

        mypath.createNewFile();

        FileOutputStream fos = null;
//        Log.e(Tag, "save to .." + mypath.getAbsoluteFile());
        fos = new FileOutputStream(mypath);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
//        Log.d(Tag, "get image from srver :"+ mypath.getAbsoluteFile());
        bos.write(mybyte, 0, mybyte.length);
//        fos.flush();
        bos.close();

        mainmenudb db= new mainmenudb(context);
        db.updateimgpath(Long.parseLong(id), Integer.parseInt(saveme), mypath.getAbsolutePath(), server);

        return mypath.getAbsolutePath();
    }

    public static String saveToInternalStorage(Bitmap bitmapImage, String fn) throws IOException {
        /*ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        if(!directory.exists()) {
            Log.e(Tag, "Create Directory");
            directory.mkdir();
        }*/
        String directory = Environment.getExternalStorageDirectory().getPath()+"/ParmisSmartHome/";
        File file = new File(directory );
        if (!file.exists()) {
            file.mkdirs();
        }

        // Create imageDir
        File mypath=new File(directory,fn);
//        mypath.deleteOnExit();
        mypath.createNewFile();

        FileOutputStream fos = null;
        try {
//            Log.e(Tag, "save to .."+ mypath.getAbsoluteFile());
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 75, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
        return mypath.getAbsolutePath();//.getAbsolutePath();
    }
    /*************************************************************************************************************/
    public static void connecttossid(String ssid, String pass){
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", pass);

//        Log.d("alhabib", " شد اصال");
//remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }
    /*************************************************************************************************************/
    public static class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object and
            mTcpClient = new TCPClient(infoMe.IPCenter, infoMe.PORTCenter, new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            //in the arrayList we add the messaged received from server
//            arrayList.add(values[0]);
            TimeConnectRFCenter=0;
            int p= values[0].indexOf("*OK#");
            if (p!=0){
                if(RegCenterLevel==11){
                    Log.e(Tag, "دریافت اوکی");
                    RegCenterLevel=21;
//                    infoMe.IPCenter= infoMe.IPme.substring(0, infoMe.IPme.lastIndexOf('.'))+"91";
                    connecttossid(infoMe.CurrentSSID, infoMe.UserPass);

                }
            }

            p= values[0].indexOf("IP IS:");
            if (p!=-1)
                try {
                    infoMe.IPCenter= InetAddress.getByName(values[0].substring(values[0].indexOf(':')+1));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            else{
                p= values[0].indexOf("MAC IS:");
                if(p!=-1) {
                    infoMe.MacCenter = values[0].substring(values[0].indexOf(':') + 1);
                    if (progressBar != null) {
                        progressBar.dismiss();
                    }
                }
                else{

                    p= values[0].indexOf("GETRF:");
                    if(p!=-1) {
                        GetRF = new StringBuffer(values[0].substring(values[0].indexOf(':') + 1)).reverse().toString();

                        Log.e(Tag, "کد آر اف:"+ Integer.toHexString(Integer.parseInt(GetRF)));

                        progressBar.dismiss();
//                        Toast.makeText(getApplicationContext(), "Get RF: "+ GetRF,Toast.LENGTH_LONG ).show();
                    }
                    else {

                    }
                }
            }
//            Log.d(Tag, "IP is:"+infoMe.IPCenter+infoMe.MacCenter);
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
//            mAdapter.notifyDataSetChanged();
        }
    }


    /*************************************************************************************************************/
    private void addcenterrf(){
        final Dialog dialog=new Dialog(this, R.style.DialogSlideAnim);
//        Log.e(MainActivity.Tag, "ساخت دیاالگ");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.activity_add_middle);
//                dialog.setTitle(null);
/*
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = 0;
        wmlp.y = 0;
        wmlp.width=this.mViewPager.getWidth();*/

        final EditText mSSIDEt=(EditText) dialog.findViewById(R.id.txtssidname);
        mSSIDEt.setText(infoMe.UserSSID);

        final EditText mPasswordEt=(EditText) dialog.findViewById(R.id.txtssidpass);
        mPasswordEt.setText(infoMe.UserPass);

//        final EditText mIpEd=(EditText) dialog.findViewById(R.id.txtssidip);
//        mIpEd.setText(infoMe.IPme);

        Button btn= (Button) dialog.findViewById(R.id.btnconnect);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.wificonnect) {
//                if (MainActivity.mTcpClient != null) {
//                    MainActivity.mTcpClient.stopClient();
//                    Log.d(MainActivity.Tag, "توقف سرویس قدیمی");
//                }
//                    Log.d(Tag, "تلاش برای جوین");
                    MainActivity.infoMe.MacCenter = "";
                    MainActivity.infoMe.UserSSID = mSSIDEt.getText().toString();
                    MainActivity.infoMe.UserPass = mPasswordEt.getText().toString();
//                    MainActivity.infoMe.IPme = mIpEd.getText().toString();
                    try {
                        MainActivity.infoMe.IPCenter = InetAddress.getByName("192.168.4.1");
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    MainActivity.infoMe.PORTCenter= 3344;

                    MainActivity.RegCenterLevel = 1;
                    MainActivity.infoMe.SaveMe();

                    // new for automatic connect
                    connecttossid(ParmisSHRFSender, PassParmisSHRFSender);
                    dialog.dismiss();

                    /* old for manual connect to modem*/

/*                    MainActivity.RegCenterLevel = 11;
                    MainActivity.ConnectTask();

                    while(MainActivity.mTcpClient==null)
                        ;
                    while(MainActivity.mTcpClient.isConnected()==false)
                        ;

                    if (MainActivity.mTcpClient != null) {
//                        MainActivity.infoMe.IPCenter= mIpEd.getText().toString().substring(0,mIpEd.getText().toString().lastIndexOf('.'))+".91";//MainActivity.infoMe.IPme.substring(0,MainActivity.infoMe.IPme.lastIndexOf('.'))+".91";
                        MainActivity.mTcpClient.sendMessage(
                                (String.format("*JOIN*\"%s\",\"%s\"*%s#", mSSIDEt.getText().toString(), mPasswordEt.getText().toString(),
                                        MainActivity.infoMe.IPme)));
                        dialog.dismiss();
                        Toast.makeText(v.getContext(), "مبدل RF وصل شد", Toast.LENGTH_SHORT).show();
                    } else
                        Log.d(MainActivity.Tag, "نمی توان وصل شد");
*/
                } else
                    Toast.makeText(v.getContext(), "!!!اتصال اولیه برفرار نیست", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();

    }
    private void SendInternet(String cmd) {
//        pb.setVisibility(View.INVISIBLE);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String cDateTime = dateFormat.format(new Date());

        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri("http://5.9.226.30:8000/poem");
        p.setParam("noun1", infoMe.macme);
        p.setParam("noun2", cmd);
        p.setParam("noun3", "on");
        p.setParam("noun4", cDateTime);
        p.setParam("noun5", "2.0.0");
        p.setParam("noun6", infoMe.macme);
//        flag = true;

        try {
            MyTask task = new MyTask();
            task.execute(p);

        } catch (Exception e) {
            System.out.println("error occured in " + e.toString());
        }
    }

    protected class MyTask extends AsyncTask<RequestPackage, String, String> {
        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
//                pb.setVisibility(View.VISIBLE);
            }
//            Log.d(Tag, "Send Internet");
            tasks.add(this);
        }
        @Override
        protected String doInBackground(RequestPackage... params) {
            String content = HttpManager.getData(params[0]);
//            Log.d(Tag, "Send Internet1");
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            try {

                tasks.remove(this);
                if (tasks.size() == 0) {
//                    pb.setVisibility(View.INVISIBLE);
                }
                if (result.contains("error")) {
                    Toast.makeText(context, "problem on connecting to service provider", Toast.LENGTH_LONG).show();
//                        Crouton.makeText(this,"error in :",Style.ALERT);
                }
//                updateDisplay(result);
                Log.d(Tag, "اینترنت "+ result);
//                crouton(result);
            } catch (Exception e) {
                System.out.println("error occured in " + e.toString());
//                updateDisplay("problem on sending the request");
            }
        }
    }

}
