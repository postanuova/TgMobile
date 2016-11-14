package org.teenguard.child.utils;

import android.graphics.Bitmap;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.teenguard.child.datatype.BeatResponseJsonWrapper;
import org.teenguard.child.datatype.MyServerResponse;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by chris on 21/10/16.
 * http://92.222.83.28/logapi.txt
 */

public class ServerApiUtils {
    public final static String APPLICATION_SERVER_PROTOCOL = "http://";
    public final static String APPLICATION_SERVER_IP_ADDRESS = "92.222.83.28";
    public final static String APPLICATION_SERVER_MIMETYPE_JSON = "application/json";

    public final static String APPLICATION_SERVER_REQUEST_ADD_CONTACTS_URL = "/api2.php";
    public final static String APPLICATION_SERVER_REQUEST_REMOVE_CONTACTS_URL = "/api2.php";
    public final static String APPLICATION_SERVER_REQUEST_UPDATE_CONTACTS_URL = "/api2.php";

    public final static String APPLICATION_SERVER_REQUEST_ADD_MEDIA_METADATA_URL = "/api2.php";
    public final static String APPLICATION_SERVER_REQUEST_REMOVE_MEDIA_URL = "/api2.php";
    public final static String APPLICATION_SERVER_REQUEST_ADD_MEDIA_METADATA_AND_MEDIA_DATA_URL = "/api2.php";

    public final static String APPLICATION_SERVER_REQUEST_ADD_LOCATION = "/api2.php";

    public final static String APPLICATION_SERVER_REQUEST_ADD_GEOFENCE_EVENT = "/api2.php";

    public final static String APPLICATION_SERVER_REQUEST_GET_BEAT = "/child/beat.php";
    //public final static String APPLICATION_SERVER_REQUEST_GET_BEAT = "http://www.google.com/search?q=mkyong";

    public static MyServerResponse addVisitToServer(String dataToSend) {
        MyLog.i("ServerUtils.addVisitToServer"," using addLocationToServer data:" + dataToSend);
        return addLocationToServer(dataToSend);
    }


    public static MyServerResponse addGeofenceEventToServer(String serializedData) {
        MyServerResponse myServerResponse = new MyServerResponse();
        MyLog.i("ServerUtils.addGeofenceEventToServer","addGeofenceEventToServer data:" + serializedData);
        try{
            URL url = new URL(APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_ADD_GEOFENCE_EVENT);
            myServerResponse = MyConnectionUtils.doAndroidRequest("POST",url,APPLICATION_SERVER_MIMETYPE_JSON,serializedData);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return myServerResponse;
    }

    public static MyServerResponse addLocationToServer(String serializedData) {
        MyServerResponse myServerResponse = new MyServerResponse();
        MyLog.i(" ServerUtils.addLocationToServer","addLocationToServer data:" + serializedData);
        try{
            URL url = new URL(APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_ADD_LOCATION);
            myServerResponse = MyConnectionUtils.doAndroidRequest("POST",url,APPLICATION_SERVER_MIMETYPE_JSON,serializedData);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return myServerResponse;
    }

    //(String requestMethod,URL url, String contentType,  String serializedData)
    public static MyServerResponse addContactToServer(String serializedData) {
        MyServerResponse myServerResponse = new MyServerResponse();
        MyLog.i(" ServerUtils.addContactToServer","addContactToServer data:" + serializedData);
        try{
            URL url = new URL(APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_ADD_CONTACTS_URL);
            myServerResponse = MyConnectionUtils.doAndroidRequest("POST",url,APPLICATION_SERVER_MIMETYPE_JSON,serializedData);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return myServerResponse;
    }


    public static MyServerResponse updateContactIntoServer(String serializedData) {
        return addContactToServer(serializedData);
    }

    public static MyServerResponse deleteContactFromServer(String serializedData) {
        MyServerResponse myServerResponse = new MyServerResponse();
        MyLog.i("ServerUtils.deleteContactFromServer","deleteContactFromServer data:" + serializedData);
        try{
            URL url = new URL(APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_REMOVE_CONTACTS_URL + "/" +serializedData);
            myServerResponse = MyConnectionUtils.doAndroidRequest("DELETE",url ,APPLICATION_SERVER_MIMETYPE_JSON,"");
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return myServerResponse;
    }

    public static MyServerResponse addMediaMetadataToServer(String serializedData) {
        MyServerResponse myServerResponse = new MyServerResponse();
        MyLog.i("ServerUtils.addMediaToServer","addMediaToServer data:" + serializedData);
        try{
            URL url = new URL(APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_ADD_MEDIA_METADATA_URL);
            myServerResponse = MyConnectionUtils.doAndroidRequest("POST",url,APPLICATION_SERVER_MIMETYPE_JSON,serializedData);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return myServerResponse;
    }

    public static MyServerResponse addMediaMetadataAndMediaDataToServer(JSONObject headerMetadataJSON,String bodyRawData) {
        MyServerResponse myServerResponse = new MyServerResponse();

        try{
            URL url = new URL(APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_ADD_MEDIA_METADATA_AND_MEDIA_DATA_URL);
            myServerResponse = MyConnectionUtils.doAndroidMediaRequestWithHeader("POST",url,APPLICATION_SERVER_MIMETYPE_JSON,headerMetadataJSON,bodyRawData);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return myServerResponse;
    }

    public static MyServerResponse addMediaMetadataAndBitmapToServer(JSONObject headerMetadataJSON,Bitmap bitmap) {
        //http://stackoverflow.com/questions/9397076/android-sending-an-image-through-post
        MyServerResponse myServerResponse = new MyServerResponse();
        byte[] dataAR = TypeConverter.bitmapToByteAR(bitmap);
        try{
            URL url = new URL(APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_ADD_MEDIA_METADATA_AND_MEDIA_DATA_URL);
            myServerResponse = MyConnectionUtils.doAndroidMediaRequestWithHeader("POST",url,APPLICATION_SERVER_MIMETYPE_JSON,headerMetadataJSON,dataAR.toString());
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return myServerResponse;
    }


    public static MyServerResponse deleteMediaFromServer(String serializedData) {
        MyServerResponse myServerResponse = new MyServerResponse();
        MyLog.i("ServerUtils"," deleteMediaFromServer data:" + serializedData);
        try{
            URL url = new URL(APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_REMOVE_MEDIA_URL);
            myServerResponse = MyConnectionUtils.doAndroidRequest("POST",url,APPLICATION_SERVER_MIMETYPE_JSON,serializedData);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return myServerResponse;
    }

    public static MyServerResponse getBeatFromServer() {
        MyServerResponse myServerResponse = new MyServerResponse();
        System.out.println("getBeatFromServer");
        try{
            URL url = new URL(APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_GET_BEAT);
           // URL url = new URL("http://www.innovacem.com/public/glace/leggiFoto.php");
            myServerResponse = MyConnectionUtils.doAndroidRequest("GET",url,"text/html" ,"");
            myServerResponse.dump();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return myServerResponse;
    }




    public static void main (String args[]) {

        MyServerResponse myServerResponse = getBeatFromServer();
        String jsonResponse = myServerResponse.getResponseBody();
        //jsonResponse ="{data:{a:1}}";
        System.out.println("jsonResponse = " + jsonResponse);
        System.out.println("start parsing");
        Gson gson = new Gson();
        BeatResponseJsonWrapper beatResponseJsonWrapper = gson.fromJson(jsonResponse,BeatResponseJsonWrapper.class);
        System.out.println(beatResponseJsonWrapper.data.geofences.size());
    }



}
