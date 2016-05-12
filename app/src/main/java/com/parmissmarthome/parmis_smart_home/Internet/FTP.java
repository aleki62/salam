package com.parmissmarthome.parmis_smart_home.Internet;

import java.io.File;
import java.io.IOException;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

/**
 * Created by mohammad on 3/4/2016.
 */

public class FTP {
    public boolean client(String userName, String password, String address) throws FTPException, IOException, FTPIllegalReplyException
            , FTPAbortedException, FTPDataTransferException, FTPListParseException {
        FTPClient client = new FTPClient();
        client.connect("136.243.163.16");
        System.out.println("the user name is: " + userName + " password is " + password);
        client.login(userName, password);
        client.setType(FTPClient.TYPE_AUTO);
//        client.setPassive(true);
//        client.noop();
        int i=address.lastIndexOf('/');
        String FileName;
        String addr0=address;
        System.out.println("address is "+addr0);
        FileName=address.substring(i + 1);
        address=address.substring(22);
            client.download(address, new File("/storage/emulated/0/", FileName), new MyTransferListener());
            return true;
    }
}

class MyTransferListener implements FTPDataTransferListener {

    public void started() {
        System.out.println("file transfering started");
    }

    public void transferred(int length) {
        // Yet other length bytes has been transferred since the last t1 this
        // method was called
        System.out.println("file transfered");
    }

    public void completed() {
        // Transfer completed
        System.out.println("transfer has been completed");
    }

    public void aborted() {
        // Transfer aborted
        System.out.println("transfer aborted");
    }

    public void failed() {
        // Transfer failed
        System.out.println("transfer failed");
    }
}
