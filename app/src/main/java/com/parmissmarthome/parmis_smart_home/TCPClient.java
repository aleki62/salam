package com.parmissmarthome.parmis_smart_home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parmissmarthome.parmis_smart_home.db.mainmenudb;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by YA-MAHDI on 01/11/2015.
 */
public class TCPClient {

    public boolean isBusy() {
        return busy;
    }

    private boolean busy=false;

    private String serverMessage;
    private StringBuffer srvrMessage= new StringBuffer();

    public boolean isConnected() {
        return mRun;
    }

    public static InetAddress SERVERIP;// = "192.168.4.1"; //your computer IP address
    public static int SERVERPORT = 3344;
    private OnMessageReceived mMessageListener = null;

    private boolean mRun = false;

    public void setConnected(boolean mRun) {
        this.mRun = mRun;
    }

    PrintWriter out;
    BufferedReader in;


//    private long startTime = 0L;

//    long timeInMilliseconds = 0L;
//    long timeSwapBuff = 0L;
    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(InetAddress ip, int port, OnMessageReceived listener) {
        mMessageListener = listener;
        SERVERIP= ip;
        SERVERPORT=port;
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
//            MainActivity.TimeConnectRFCenter=0;
//            for(int i=0; i<message.length-2; i++)
//                out.print(message);
//            out.println(message[message.length-1]);
            out.println(message);
            out.flush();
//            startTime= SystemClock.uptimeMillis();
            Log.e("TCP Client", "C: Sent:"+ message);
        }
        else {
            Log.e(MainActivity.Tag, "send message error...");
            mRun=false;
        }
    }

    public void stopClient(){
        mRun = false;
        out.flush();
        out.close();
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(MainActivity.Tag, "اتصال به مبدل قطع شد!!!");
    }

    public void run() {
//        Log.d(MainActivity.Tag, " رودو به ران ترد کلاینت");
        busy= true;
//        mRun = true;

        try {
            //here you must put your computer's IP address.
            Log.e(MainActivity.Tag, "آی پی آر اف: " + SERVERIP+ " Port: "+ SERVERPORT);
//            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e(MainActivity.Tag+" TCP Client", "C: Connecting...");
            MainActivity.TimeConnectRFCenter=0;
            //create a socket to make the connection with the server
            Socket socket = new Socket(SERVERIP, SERVERPORT);

            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

//                Connected=true;
                MainActivity.TimeConnectRFCenter=0;

                Log.e(MainActivity.Tag+ " TCP Client", "C: Done.");

                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

//                startTime= SystemClock.uptimeMillis();

                mRun = true;

                if(MainActivity.RegCenterLevel==2){
                    MainActivity.RegCenterLevel=3;
                    MainActivity.mTcpClient.sendMessage(
                            (String.format("*JOIN*\"%s\",\"%s\"*%s#", MainActivity.infoMe.UserSSID, MainActivity.infoMe.UserPass,
                                    MainActivity.infoMe.IPme)));
                    MainActivity.connecttossid(MainActivity.infoMe.UserSSID, MainActivity.infoMe.UserPass);
                }
                int p;
                //in this while the client listens for the messages sent by the server
                while (mRun) {
//                    srvrMessage.append(in.read());
                    serverMessage = in.readLine();

//                    p=srvrMessage.lastIndexOf("#");
//                    if(p!=-1 && srvrMessage.charAt(p+1)==0x0d && srvrMessage.charAt(p+2)==0x0a && mMessageListener != null){
                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        if (serverMessage.indexOf("mainmenuimg") == -1)
                            mMessageListener.messageReceived(serverMessage);
                        else {
//                            Log.d(MainActivity.Tag, "now111 "+serverMessage);
                            p= serverMessage.indexOf("SIZE:")+5;
                            String[] imgprm= MainActivity.splitdb(serverMessage);
//                            Log.d(MainActivity.Tag, "param "+ imgprm.toString());

                            byte[] mybytearray = new byte[Integer.parseInt(imgprm[3]/*serverMessage.substring(p)*/)];
                            InputStream is = socket.getInputStream();
//                            FileOutputStream fos = new FileOutputStream("s.pdf");
//                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                            int bytesRead = 0, currentread=0;
                            while(currentread<mybytearray.length){
                                bytesRead= is.read(mybytearray, currentread, mybytearray.length- currentread);
                                if(bytesRead>0)
                                    currentread+= bytesRead;
                            }

//                            Log.d(MainActivity.Tag, "Read Image "+ bytesRead);
//                            MainActivity.savebytetofile(mybytearray, serverMessage.substring(12, p-6)+".jpg", serverMessage.substring(12, p-6));
                            MainActivity.savebytetofile(mybytearray, imgprm[1]+".jpg", imgprm[1], imgprm[2], imgprm[3]);
//                            bos.write(mybytearray, 0, bytesRead);
//                            bos.close();

                            sendMessage(MainActivity.cmdNextMeSQL);
                        }
//                        startTime= SystemClock.uptimeMillis();
                    }
                    serverMessage = null;

                }

                Log.e(MainActivity.Tag+ " RESPONSE SERVER", "S: Received Message: '" + serverMessage + "'" + " پایان کلاینت ");

            } catch (Exception e) {

                Log.e(MainActivity.Tag+" TCP", "S: Error", e);
                mRun=false;
                busy=false;
            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                mRun=false;
                busy= false;
                out.flush();
                out.close();
                in.close();
                socket.close();
            }

        } catch (Exception e) {

            mRun=false;
            busy= false;
            Log.e(MainActivity.Tag+ " TCP", "C: Error", e);

        }

    }



    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}