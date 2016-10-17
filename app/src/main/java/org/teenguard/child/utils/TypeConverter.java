package org.teenguard.child.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by chris on 09/10/16.
 */

public class TypeConverter {

    public static String byteARToHex(byte[] data) {
        return String.format("%0" + (data.length * 2) + 'x', new BigInteger(1, data)).toUpperCase();
    }

    /*public static String byteARToSHA(byte[] data) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        digest.reset();
        try {
            Log.i("Eamorr", digest.digest (password.getBytes("UTF-8")).toString());
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }*/


    public static String computeHash(String input) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        digest.reset();

        byte[] byteData = new byte[0];
        try {
            byteData = digest.digest(input.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < byteData.length; i++){
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString().toUpperCase();
    }


    public static void main(String args[]) throws Exception{
        System.out.println(byteARToHex("ciao".getBytes()));
        System.out.println(computeHash("ciao"));
    }

}
