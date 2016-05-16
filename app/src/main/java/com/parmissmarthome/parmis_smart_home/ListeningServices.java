package com.parmissmarthome.parmis_smart_home;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.parmissmarthome.parmis_smart_home.db.MobileDB;
import com.parmissmarthome.parmis_smart_home.db.mainmenudb;
import com.parmissmarthome.parmis_smart_home.db.scriptdb;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ListeningServices extends Service {

    private final String INTENT_NEW_MSG = "INTENT_NEW_MSG";

    NsdManager CommonHelper;
    private NotificationManager mNM;

    public ListeningServices() {
//        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Log.v(MainActivity.Tag+" service", "ListeningServices started");
        CommonHelper= (NsdManager) getSystemService(Context.NSD_SERVICE);
        new serviceSocketThread().start();    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        Log.v(MainActivity.Tag + " service", "ListeningServices binded");
        return null;
    }


    private final NsdManager.RegistrationListener registrationListener = new NsdManager.RegistrationListener() {
        @Override
        public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
            Log.v(MainActivity.Tag+" NSD", "register failed :" + nsdServiceInfo.toString());
        }

        @Override
        public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
            Log.v(MainActivity.Tag+" NSD", "unRegister failed :" + nsdServiceInfo.toString());
        }

        @Override
        public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
            Log.v(MainActivity.Tag+" NSD", "Service Registered: " + nsdServiceInfo.toString());
        }

        @Override
        public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
            Log.v(MainActivity.Tag+" NSD", "Service unRegistered: " + nsdServiceInfo.toString());
        }
    };
    public void registerService(int port) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(MainActivity.SERVICE_NAME);
        serviceInfo.setServiceType(MainActivity.SERVICE_TYPE);
        serviceInfo.setPort(port);

        CommonHelper.registerService(serviceInfo,
                NsdManager.PROTOCOL_DNS_SD,
                registrationListener);
    }

    private class serviceSocketThread extends Thread {
        ServerSocket mServerSocket = null;

        @Override
        public void run() {
            super.run();
            try {
                mServerSocket = new ServerSocket(3348);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            int localPort = 3348;//mServerSocket.getLocalPort();
            Log.v(MainActivity.Tag + " socket", "server socket started at: " + mServerSocket.getLocalPort());
//            CommonHelper.registerNSDService(com.ya_mahdi.nsdsocketserver.ListeningService.this, localPort, registrationListener);
            registerService(localPort);
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = null;
                try {
                    socket = mServerSocket.accept();
//                    Log.e("alhabib", "اتصال کلاینت;");
//                    ListClientMobile listClientMobile= new ListClientMobile(new clientSocketThread(socket), "noname");
//                    listClientMobile.client.start();
                    clientSocketThread cst=new clientSocketThread(socket); cst.start();
                    MainActivity.listClientMobiles.add(cst);

//                    new clientSocketThread(socket).start();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
//            CommonHelper.unregisterNSDService(com.ya_mahdi.nsdsocketserver.ListeningService.this, registrationListener);
            CommonHelper.unregisterService(registrationListener);
        }
    }


/*public class ListClientMobile {
    clientSocketThread client;
    String name;

    public ListClientMobile(clientSocketThread client, String name) {
        this.client = client;
        this.name = name;
    }
}*/
    public class clientSocketThread extends Thread {
        String NameClient="noname";
        boolean acceptjoin=false;
        final Socket mSocket;
        final BufferedReader mInputReader;
        final PrintWriter mOutputStream;
        private int sendmenuitem=-1;
        Cursor sendDBcursor;
//        final OutputStream mOutputStream;

        public clientSocketThread(Socket socket) throws IOException {
            Log.v(MainActivity.Tag+" socket", "new client socket received: " + socket.toString()+ MainActivity.listClientMobiles.size());
            this.mSocket = socket;
            this.mInputReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
//            this.mOutputStream = mSocket.getOutputStream();
            mOutputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            sendmessage("*getmac#");
        }
        public void sendfile(File myFile) throws IOException {
            byte[] mybytearray = new byte[(int) myFile.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
            bis.read(mybytearray, 0, mybytearray.length);
            OutputStream os = mSocket.getOutputStream();
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
        }
        public void sendmessage(String msg){
            if (mOutputStream != null && !mOutputStream.checkError()) {
                mOutputStream.println(msg);
                mOutputStream.flush();
                Log.e(MainActivity.Tag, "answer :"+ msg);
            }
            else {
                Log.e(MainActivity.Tag, "send message error...mb");
            }

        }
        private void sendImageDB(){
            Log.d(MainActivity.Tag, "Try to send image..."+sendDBcursor.getString(sendDBcursor.getColumnIndex(mainmenudb.MainMenu_Image))+"...");
            if (!sendDBcursor.getString(sendDBcursor.getColumnIndex(mainmenudb.MainMenu_Image)).equalsIgnoreCase("null")) {
                Log.d(MainActivity.Tag, "Send Image DB"+ sendDBcursor.getString(sendDBcursor.getColumnIndex(mainmenudb.MainMenu_Image)));
                File myFile = new File(sendDBcursor.getString(sendDBcursor.getColumnIndex(mainmenudb.MainMenu_Image)));
                sendmessage(String.format("mainmenuimg,%s,%s,%d,%s,",
                        sendDBcursor.getString(sendDBcursor.getColumnIndex(mainmenudb.MainMenu_ID)) ,
                        sendDBcursor.getString(sendDBcursor.getColumnIndex(mainmenudb.MainMenu_SaveMe)),
                        myFile.length(),
                        sendDBcursor.getString(sendDBcursor.getColumnIndex(mainmenudb.MainMenu_Server))
                        ));

                try {
                    sendfile(myFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        private void sendDBtoClient(String tbl) {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.delete(0, stringBuilder.length());
            for (int i = 0; i < sendDBcursor.getColumnCount(); i++)
                stringBuilder.append(sendDBcursor.getString(i) + ',');
            sendmessage(tbl+":" + stringBuilder.toString());
        }

        @Override
        public void run() {
            super.run();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String data = mInputReader.readLine();
                    Log.d(MainActivity.Tag, "دریافت کلاینت"+ data);
                    if (data == null) throw new IOException("Connection closed");

                    if(acceptjoin && data.indexOf(MainActivity.cmdGiveMeSQL)!=-1) {
                        mainmenudb mdb= new mainmenudb(getApplicationContext());
                        sendDBcursor= mdb.queryall();
                        if (sendDBcursor.moveToFirst()) {
                            sendmenuitem = 0;
                            sendDBtoClient("mainmenudb");
                        }else sendmessage(MainActivity.cmdSendDBComplete);

//                        new sendsqltoclient(this).start();
                    }
                    else if(acceptjoin && data.indexOf(MainActivity.cmdGetImageDB)!=-1) {
                            sendImageDB();
                    }else if(acceptjoin && data.indexOf(MainActivity.cmdNextMeSQL)!=-1) {
                            if (sendDBcursor.moveToNext()) {
                                sendmenuitem++;
                                sendDBtoClient("mainmenudb");
                            }else sendmessage(MainActivity.cmdSendDBComplete);
                    }else if(acceptjoin && data.indexOf(MainActivity.cmdSendRF)!=-1) {
                        MainActivity.sendtocenterrf(data, null, Long.valueOf(-1), -1);
                    } else
                    if(acceptjoin && data.indexOf(MainActivity.cmdGetScriptDB)!=-1) {
                        scriptdb mdb= new scriptdb(getApplicationContext());
                        sendDBcursor= mdb.queryall();
                        if (sendDBcursor.moveToFirst()) {
                            sendmenuitem = 0;
                            sendDBtoClient("scriptdb");
                        }else sendmessage(MainActivity.cmdScripDBComplete);

//                        new sendsqltoclient(this).start();
                    }else if(acceptjoin && data.indexOf(MainActivity.cmdNextMeSQL)!=-1) {
                        if (sendDBcursor.moveToNext()) {
                            sendmenuitem++;
                            sendDBtoClient("scriptdb");
                        } else sendmessage(MainActivity.cmdScripDBComplete);
                    }
                    else {
                        int pp=data.indexOf("macis:");
                        if (pp!=-1) {
                            int p = MainActivity.findlistClientMobiles("noname");
                            if (p!=-1) {
                                NameClient=data.substring(pp + 6);
//                                MainActivity.listClientMobiles.get(p).name = NameClient;
                                Log.e(MainActivity.Tag, "Name is replaced with:" + NameClient);
                                MainActivity.ConfigClient cj= confirmjoin(NameClient);
                                if (cj!=null) {
                                    acceptjoin = true;
                                    sendmessage(MainActivity.cmdAccept + "," + MainActivity.infoMe.macme+","+cj.stringme());
                                }
                            }
                        }


                        /*Intent intent = new Intent();
                        intent.setAction("com.poujman.ReceiveParmisSmartHome");//String.valueOf(R.string.brdReceiverParmisSmartHome));
                        intent.putExtra("data", data);
                        sendBroadcast(intent);*/
                    }
//                    Log.d(MainActivity.Tag, "data received: " + data);

                } catch (IOException e) {
                    break;
                }
            }
            mOutputStream.flush();
            mOutputStream.close();
            try {
                mInputReader.close();
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v(MainActivity.Tag + " socket", "client socket closed: " + mSocket.toString());
            MainActivity.listClientMobiles.remove(MainActivity.findlistClientMobiles(NameClient));
        }

    private MainActivity.ConfigClient confirmjoin(String data) {
        MobileDB mobileDB=new MobileDB(getApplicationContext());
//        mobileDB.delete(null);
        MainActivity.ConfigClient res=mobileDB.acceptlater(data);
        if(res!=null)
           return res;

//        showNotification(data);
        MainActivity.isAddClient= true;

        Context context= getApplicationContext();
        Intent intent= new Intent(context, mobilejoin.class);
        intent.putExtra("mac", data);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        return null;
    }
}

    /*******************************************************************************************************************/
    private void showNotification(String mac) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = String.format("دستگاه %s قصد اضافه شدن به سیستم رادارد. آیا اجازه می دهید؟", mac);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("خانه هوشمند پارمیس")  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        mNM.notify(1, notification);
    }

/*******************************************************************************************************************/
    //  وقتی که موبایل به سرور وصل می شود اطلاعات دیتابیس برای آن ارسال می شود.
    public class sendsqltoclient extends Thread{
        private clientSocketThread cst;

        public sendsqltoclient(clientSocketThread cst) {
            this.cst = cst;
        }

        @Override
        public void run() {
            super.run();
            mainmenudb mdb= new mainmenudb(getApplicationContext());
            Cursor cursor= mdb.queryall();
            Log.e(MainActivity.Tag, "Give me SQL:" + "بله"+ cursor.getCount());
            if (cursor.moveToFirst()){
                StringBuilder stringBuilder=new StringBuilder();
                do {
                    stringBuilder.delete(0, stringBuilder.length());
                    for (int i=0; i<cursor.getColumnCount(); i++)
                        stringBuilder.append(cursor.getString(i)+',');
                    cst.sendmessage("mainmenudb:"+stringBuilder.toString());
                    if(cursor.getString(cursor.getColumnIndex(mainmenudb.MainMenu_Image))!=null) {
                        File myFile = new File(cursor.getString(cursor.getColumnIndex(mainmenudb.MainMenu_Image)));
                        cst.sendmessage("mainmenuimg:" + cursor.getString(cursor.getColumnIndex(mainmenudb.MainMenu_ID)) + " SIZE:" + myFile.length());

                        try {
                            cst.sendfile(myFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }while(cursor.moveToNext());
            }
        }
    }

/*    public class sendsqltoclient extends AsyncTask {
        private clientSocketThread cst;

        private sendsqltoclient(clientSocketThread cst) {
            this.cst = cst;
        }

        @Override
        protected clientSocketThread doInBackground(Object[] params) {
            mainmenudb mdb= new mainmenudb(getApplicationContext());
            Cursor cursor= mdb.query(null, null);
            Log.e(MainActivity.Tag, "Give me SQL:"+ "بله");
            if (cursor.moveToFirst()){
                StringBuilder stringBuilder=new StringBuilder();
                do {
                    stringBuilder.delete(0, stringBuilder.length());
                    for (int i=0; i<cursor.getColumnCount();i++)
                        stringBuilder.append(cursor.getString(i)+',');
                    cst.sendmessage("mainmenudb:"+stringBuilder.toString());
                }while(cursor.moveToNext());
            }
            return null;
        }
    }
*/
}
