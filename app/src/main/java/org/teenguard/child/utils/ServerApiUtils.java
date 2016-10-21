package org.teenguard.child.utils;

import android.util.Log;

import org.teenguard.child.datatype.MyServerResponse;
import org.teenguard.child.dbdatatype.DbContactEvent;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by chris on 21/10/16.
 */

public class ServerApiUtils {
    public final static String APPLICATION_SERVER_PROTOCOL = "http://";
    public final static String APPLICATION_SERVER_IP_ADDRESS = "92.222.83.28";
    public final static String APPLICATION_SERVER_REQUEST_ADD_CONTACTS_URL = "/api.php";
    public final static String APPLICATION_SERVER_REQUEST_REMOVE_CONTACTS_URL = "/api.php";
    public final static String APPLICATION_SERVER_REQUEST_UPDATE_CONTACTS_URL = "/api.php";
    public final static String APPLICATION_SERVER_MIMETYPE_JSON = "application/json";

    //(String requestMethod,URL url, String contentType,  String data)
    public static MyServerResponse addContactToServer(DbContactEvent dbContactEvent) {
        MyServerResponse myServerResponse = new MyServerResponse();
        Log.i("ServerUtils", " sending to server contactId: " + dbContactEvent.getId() + " data:" + dbContactEvent.getSerializedData());
        try{
            URL url = new URL(APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_ADD_CONTACTS_URL);
            myServerResponse = MyConnectionUtils.doAndroidRequest("POST",url,APPLICATION_SERVER_MIMETYPE_JSON,"[" + dbContactEvent.getSerializedData() + "]");
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return myServerResponse;
    }

    public static MyServerResponse updateContactIntoServer(DbContactEvent dbContactEvent) {
        return addContactToServer(dbContactEvent);
    }

    public static MyServerResponse deleteContactFromServer(String csIdList) {
        MyServerResponse myServerResponse = new MyServerResponse();
        Log.i("ServerUtils"," data:" + csIdList);
        try{
            URL url = new URL(APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_REMOVE_CONTACTS_URL + "/" +csIdList);
            myServerResponse = MyConnectionUtils.doAndroidRequest("DELETE",url ,APPLICATION_SERVER_MIMETYPE_JSON,"");
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return myServerResponse;
    }



}
