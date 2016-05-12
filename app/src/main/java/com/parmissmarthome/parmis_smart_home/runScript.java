package com.parmissmarthome.parmis_smart_home;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

import com.parmissmarthome.parmis_smart_home.Internet.HttpManager;
import com.parmissmarthome.parmis_smart_home.Internet.RequestPackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class runScript extends Service {
    private SMSreceiver mSMSreceiver;
    private IntentFilter mIntentFilter;

    ServiceSocketThread serviceSocketThread= null;

    private List<TaskGetInternet> tasksGI;
    int checkinternet=0;
    static final int UPDATE_INTERVAL=1000;
    private Timer timer = new Timer();

    public runScript() {
    }

    @Override
    public IBinder onBind(Intent intent) {
//        return Mai
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(MainActivity.Tag, "Service Script started...????");
        tasksGI= new ArrayList<>();
//SMS event receiver
        /*mSMSreceiver = new SMSreceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSMSreceiver, mIntentFilter);
*/
        doScript();
        if (serviceSocketThread== null) {
            serviceSocketThread = new ServiceSocketThread();
            serviceSocketThread.start();
        }else
            serviceSocketThread.isrunning= true;
        return START_STICKY;
    }
    private void doScript(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
//                if(!MainActivity.wificonnect)   return;
//                Log.d(MainActivity.Tag, "time:"+checkinternet);
                if (checkinternet++>60){
                    checkinternet=0;

                    /*try {
                        requestData("");
                    }catch(Exception e){
                        e.printStackTrace();
                    }*/
                }
                if(!MainActivity.isRunApp&&!MainActivity.isLockScreen){
                    Log.d(MainActivity.Tag, "برنامه اجرا نیس");
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ComponentName cn = new ComponentName(getApplicationContext(), MainActivity.class);
                    intent.setComponent(cn);
//                    startActivity(intent);
                }

                MainActivity.scriptIsRuning.run();
//                Log.d(MainActivity.Tag, String.valueOf(counter++));
                /*if(MainActivity.mTcpClient!=null&& MainActivity.mTcpClient.isConnected())
                    switch (MainActivity.TimeConnectRFCenter++){
                        case 12:
                            MainActivity.mTcpClient.sendMessage("*GTMAC#");
                            break;
                        case 14:
                            MainActivity.mTcpClient.sendMessage("*GTMAC#");
                            break;
                        case 18:
//                            MainActivity.mTcpClient.setConnected(false);
                            MainActivity.mTcpClient.stopClient();
                            Log.d(MainActivity.Tag, "توقف در سرویس");
                            break;
                    }*/
                /*else
                if (MainActivity.TimeConnectRFCenter++>10&& (MainActivity.mTcpClient==null || !MainActivity.mTcpClient.isBusy())) {
//                    Log.d(MainActivity.Tag, "توقف در سرویس");
                    MainActivity.ConnectTask();
                }*/
            }
        }, 0, UPDATE_INTERVAL);
    }

    @Override
    public void onDestroy() {
        serviceSocketThread.stopme();
        if(timer!=null) {
            timer.cancel();
        }
        Log.d(MainActivity.Tag, "Service Script destroy ....!!!!");
        unregisterReceiver(mSMSreceiver);
        super.onDestroy();
    }


    private class ServiceSocketThread extends Thread {
        ServerSocket mServerSocket = null;
        boolean isrunning= true;

        public void stopme(){
            Log.d(MainActivity.Tag, "توقف سوکت 45");
            isrunning= false;
            /*try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
        @Override
        public void run() {
            super.run();
            try {
                mServerSocket = new ServerSocket(3345);

            } catch (IOException e) {
                e.printStackTrace();
                return;
//                throw new RuntimeException(e);
            }

            int localPort = 3345;//mServerSocket.getLocalPort();
            Log.v(MainActivity.Tag + " socket", "server socket started at: " + mServerSocket.getLocalPort());

            while (!Thread.currentThread().isInterrupted()&& isrunning) {
                Socket socket = null;
                try {
                    socket = mServerSocket.accept();
//                    Log.e("alhabib", "اتصال کلاینت;");
//                    ListClientDevice listClientDevice= new ListClientDevice(new DeviceSocketThread(socket), "noname");
//                    listClientDevice.client.start();
//                    MainActivity.listClientDevices.add(listClientDevice);

                    if (isrunning)
                        new DeviceSocketThread(socket).start();
                    else
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v(MainActivity.Tag + " socket", "server socket Stop " + mServerSocket.getLocalPort());
        }
    }

    /*public class ListClientDevice {
        DeviceSocketThread client;
        String name;

        String type;

        public ListClientDevice(DeviceSocketThread client, String name, String type) {
            this.client = client;
            this.name = name;
            this.type = type;
        }

        public ListClientDevice(DeviceSocketThread client, String name) {
            this.client = client;
            this.name = name;
        }
    }*/

    public class DeviceSocketThread extends Thread {
        String NameClient="noname";
        String DeviceType= "unknown";
        boolean ifadd=false;
        Date date=null;
        final Socket mSocket;
        final BufferedReader mInputReader;
        final PrintWriter mOutputStream;
//        final OutputStream mOutputStream;
        Timer timer= new Timer();
        public boolean yesigot= true;
        private boolean isrun=true;

        public DeviceSocketThread(Socket socket) throws IOException {
            Log.v(MainActivity.Tag+" socket", "new client socket received: " + socket.toString()+ MainActivity.listClientMobiles.size());
            this.mSocket = socket;
            this.mInputReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
//            this.mOutputStream = mSocket.getOutputStream();
            mOutputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            date= new Date();

            sendmessage("*GTMAC#");

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
//                    Log.d(MainActivity.Tag, "Yes i got: "+yesigot);
                    if (!yesigot)
//                        stopclient();
                    yesigot = false;
                }
            }, 19000, 19000);
        }
        public void sendmessage(String msg){
            if (mOutputStream != null && !mOutputStream.checkError()) {
                mOutputStream.println(msg);
                mOutputStream.flush();
                Log.e(MainActivity.Tag, "answer Device :"+ msg);
            }
            else {
                Log.e(MainActivity.Tag, "send message error...rf");
                stopclient();
            }

        }

        public void stopclient() {
            isrun=false;
            mOutputStream.flush();
            mOutputStream.close();
            try {
//                mInputReader.close();
                mSocket.close();
                Log.d(MainActivity.Tag, "Stop client device"+ NameClient+ " Type is: "+ DeviceType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            while (!Thread.currentThread().isInterrupted()&&isrun) {
                try {
//                    Log.d(MainActivity.Tag, "دریافت کلاینت");
                    String data = mInputReader.readLine();
                    if (data == null) throw new IOException("Connection closed");
//                    Log.e(MainActivity.Tag, data);
                    yesigot=true;

                    if (data.indexOf("I AM")!=-1){
                        if (!ifadd&&data.indexOf("CenterRfParmis")!=-1){
                            Log.e(MainActivity.Tag, "New device");
                            if(MainActivity.listClientDevices.size()>0&&MainActivity.listClientDevices.get(0).DeviceType=="RF") {
                                Log.d(MainActivity.Tag, "Stop old RF device...!!!");
//                                stopclient();
                                MainActivity.listClientDevices.get(0).stopclient();
                            } //else {
                                MainActivity.listClientDevices.add(0, this);
                                DeviceType = "RF";
                                ifadd = true;
//                            }
                        }else
                        if (data.indexOf("CnctRfParmis")!=-1){
                            int t=MainActivity.findlistClientDevice(NameClient, date);
                            if (t==-1) {
                                if (MainActivity.listClientDevices.size() > 0 && MainActivity.listClientDevices.get(0).DeviceType == "RF") {
                                    Log.d(MainActivity.Tag, "this is not add to list...!!!");
                                    MainActivity.listClientDevices.get(0).stopclient();
                                }
                                MainActivity.listClientDevices.add(0, this);
                                DeviceType= "RF";
                                ifadd= true;
                            }
//                            Log.d(MainActivity.Tag, "number of client: " + t);
                            sendmessage("*OKIGOT#");
                            MainActivity.setConnecttorf(true);
//                            Log.d(MainActivity.Tag, "Ok I Got");
                        }
                    }
                        int pp=data.toLowerCase().indexOf("macis:");
                        if (pp!=-1) {
//                            int p = MainActivity.findlistClientMobiles("noname");
//                            if (p!=-1) {
                                NameClient=data.substring(pp + 6);
//                                MainActivity.listClientMobiles.get(p).name = NameClient;
                                Log.e(MainActivity.Tag, "Name is replaced with:" + NameClient);
//                            }
                        }

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
            MainActivity.setConnecttorf(false);

            int p= MainActivity.findlistClientDevice(NameClient, date);
            Log.v(MainActivity.Tag + " socket", "client socket closed: " + mSocket.toString() + NameClient + p + " size: " + MainActivity.listClientDevices.size());

            if (p!=-1)
                MainActivity.listClientDevices.remove(p);
        }
    }

    protected void requestData(final String req) {
//        flag = true;
//        pb.setVisibility(View.INVISIBLE);
        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri("http://5.9.226.30:8000/rtc");
        p.setParam("mac_addr", MainActivity.infoMe.macme);
//        flag = true;
        TaskGetInternet task = new TaskGetInternet();
        task.execute(p);
    }


    private class TaskGetInternet extends AsyncTask<RequestPackage, String, String> {
        @Override
        protected void onPreExecute() {
            if (tasksGI.size() == 0) {
//                pb.setVisibility(View.VISIBLE);
            }
            tasksGI.add(this);
        }
        @Override
        protected String doInBackground(RequestPackage... params) {
            String content = HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            int begining=result.indexOf("body")+5;
            int end=result.lastIndexOf("body")-2;
            Log.d(MainActivity.Tag, "request data "+result);

            result=result.substring(begining, end);
            result=result.replace(',', ' ');
            String regex="(?=\\()|(?<=\\)\\d)";
            String [] request=result.split(regex); // now incoming request converted to string []
            for (int i=1;i<request.length;i++){
                Log.d(MainActivity.Tag, request[i]+'\n');
            }
            try {
                tasksGI.remove(this);
                if (tasksGI.size() == 0) {
//                    pb.setVisibility(View.INVISIBLE);
                }
//                if (result.contains("error"))
//                    updateDisplay("problem on connecting to service provider");
//                updateDisplay(result);
            } catch (Exception e) {
                System.out.println("error occured in " + e.toString());
//                updateDisplay("problem on sending the request");
            }
        }
    }
    private class SMSreceiver extends BroadcastReceiver
    {
        private final String TAG = this.getClass().getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent)
        {
            Bundle extras = intent.getExtras();
Log.e(MainActivity.Tag, "دریافت پیامک");
            String strMessage = "";

            if ( extras != null )
            {
                Object[] smsextras = (Object[]) extras.get( "pdus" );

                for ( int i = 0; i < smsextras.length; i++ )
                {
                    SmsMessage smsmsg = SmsMessage.createFromPdu((byte[])smsextras[i]);

                    String strMsgBody = smsmsg.getMessageBody().toString();
                    String strMsgSrc = smsmsg.getOriginatingAddress();

                    strMessage += "SMS from " + strMsgSrc + " : " + strMsgBody;

                    Log.i(TAG, strMessage);
                }

            }

        }

    }
}
