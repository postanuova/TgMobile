package org.teenguard.child.utils;

import org.teenguard.child.datatype.MyServerResponse;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by chris on 21/10/16.
 */

public class ServerApiUtils {
    public final static String APPLICATION_SERVER_PROTOCOL = "http://";
    public final static String APPLICATION_SERVER_IP_ADDRESS = "92.222.83.28";
    public final static String APPLICATION_SERVER_MIMETYPE_JSON = "application/json";

    public final static String APPLICATION_SERVER_REQUEST_ADD_CONTACTS_URL = "/api.php";
    public final static String APPLICATION_SERVER_REQUEST_REMOVE_CONTACTS_URL = "/api.php";
    public final static String APPLICATION_SERVER_REQUEST_UPDATE_CONTACTS_URL = "/api.php";

    public final static String APPLICATION_SERVER_REQUEST_ADD_MEDIA_URL = "/api.php";
    public final static String APPLICATION_SERVER_REQUEST_REMOVE_MEDIA_URL = "/api.php";
    public final static String APPLICATION_SERVER_REQUEST_UPDATE_MEDIA_URL = "/api.php";


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
            URL url = new URL(APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_ADD_MEDIA_URL);
            myServerResponse = MyConnectionUtils.doAndroidRequest("POST",url,APPLICATION_SERVER_MIMETYPE_JSON,serializedData);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return myServerResponse;
    }

    public static MyServerResponse addMediaMetadataAndRawDataToServer(String serializedData) {
        MyServerResponse myServerResponse = new MyServerResponse();
        MyLog.i("ServerUtils.addMediaToServer","addMediaToServer data:" + serializedData);
        try{
            URL url = new URL(APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_ADD_MEDIA_URL);
            myServerResponse = MyConnectionUtils.doAndroidRequest("POST",url,APPLICATION_SERVER_MIMETYPE_JSON,serializedData);
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


}
