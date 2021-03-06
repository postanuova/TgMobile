package org.teenguard.child.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
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

    public static byte[] fileToByteAR(String path) {
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }



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



    public static double coordinatesToDistance(double lat1, double lon1, double lat2, double lon2, char unit) {
            double theta = lon1 - lon2;
            double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
            if (unit == 'K') {
                dist = dist * 1.609344;
            }
            if (unit == 'N') {
                dist = dist * 0.8684;
            }
        if (unit == 'm') {
            dist = dist * 1.609344 * 1000;
        }

            return (dist);
        }

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/

/*::  This function converts decimal degrees to radians             :*/

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/

        private static double deg2rad(double deg) {
            return (deg * Math.PI / 180.0);
        }

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/

/*::  This function converts radians to decimal degrees             :*/

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/

        private static double rad2deg(double rad) {
            return (rad * 180.0 / Math.PI);
        }
    

    public static double doubleTrunkTwoDigit(double x) {
        x = Math.floor(x*100);
        x = x/100;
        return x;
    }

    public static String inputStreamToString(InputStream inputStream) {
        BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuffer response = new StringBuffer();
        try {
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\n');
            }
        rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
                try {
                        inputStream.close();
                        } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        //System.out.println("response.toString() = " + response.toString());
        return response.toString();
    }

    public static Bitmap base64StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static String bitMapToBase64String(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static byte[] bitmapToByteAR(Bitmap b) {
        //http://stackoverflow.com/questions/17023304/convert-bitmap-to-byte-array-without-bitmap-compressformat

        //java.lang.RuntimeException: Buffer not large enough for pixels

        int bytes = b.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        System.out.println("buffer.array().length = " + buffer.array().length);
        buffer.rewind();
        b.copyPixelsToBuffer(buffer);
        return buffer.array();
    }

    public static byte[] getBitmapBytes(Bitmap bitmap)
    {
        int chunkNumbers = 10;
        int bitmapSize = bitmap.getRowBytes() * bitmap.getHeight();
        byte[] imageBytes = new byte[bitmapSize];
        int rows, cols;
        int chunkHeight, chunkWidth;
        rows = cols = (int) Math.sqrt(chunkNumbers);
        chunkHeight = bitmap.getHeight() / rows;
        chunkWidth = bitmap.getWidth() / cols;

        int yCoord = 0;
        int bitmapsSizes = 0;

        for (int x = 0; x < rows; x++)
        {
            int xCoord = 0;
            for (int y = 0; y < cols; y++)
            {
                Bitmap bitmapChunk = Bitmap.createBitmap(bitmap, xCoord, yCoord, chunkWidth, chunkHeight);
                byte[] bitmapArray = getBytesFromBitmapChunk(bitmapChunk);
                System.arraycopy(bitmapArray, 0, imageBytes, bitmapsSizes, bitmapArray.length);
                bitmapsSizes = bitmapsSizes + bitmapArray.length;
                xCoord += chunkWidth;

                bitmapChunk.recycle();
                bitmapChunk = null;
            }
            yCoord += chunkHeight;
        }

        return imageBytes;
    }
    private static byte[] getBytesFromBitmapChunk(Bitmap bitmap)
    {
        int bitmapSize = bitmap.getRowBytes() * bitmap.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(bitmapSize);
        bitmap.copyPixelsToBuffer(byteBuffer);
        byteBuffer.rewind();
        return byteBuffer.array();
    }


    public static int booleanToInt(boolean value) {
       return (value == true) ? 1:0;
    }

    public static boolean intToBoolean(int value) {
        return (value == 1) ? true:false;
    }

 public  static  void main(String args[]) {
     System.out.println("coordinatesToDistance(28.0967195,-16.7385493,28.0953749,-16.7364082,'K') = " + coordinatesToDistance(28.0967195,-16.7385493,28.0953749,-16.7364082,'K'));
 }

}
