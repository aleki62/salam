package com.parmissmarthome.parmis_smart_home.Internet;

import android.util.Base64;

/**
 * Created by mohammad on 2/16/2016.
 */


public class encryptor {
    public void salam(){
        String source="password";
        byte [] bytearray;
        try {
            bytearray =source.getBytes("UTF-8");
            String a=new String(Base64.encodeToString(bytearray, Base64.DEFAULT));
            System.out.println(" the encoded text is:  " + a);
            byte[] b= Base64.decode(a, Base64.DEFAULT);
            System.out.println("the decoded of a is: "+b.toString());

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public String enc(String a){
        String testValue = a;

        byte[] encodeValue = Base64.encode(testValue.getBytes(), Base64.DEFAULT);
        byte[] decodeValue = Base64.decode(encodeValue, Base64.DEFAULT);

//        Log.d("TEST123456789", "defaultValue = " + testValue);
//        Log.d("TEST123456789", "encodeValue = " + new String(encodeValue));
//        Log.d("TEST123456789", "decodeValue = " + new String(decodeValue));
        return encodeValue.toString();
    }
}