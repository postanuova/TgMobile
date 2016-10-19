package org.teenguard.child.utils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.teenguard.child.dbdatatype.DbContactEvent;

import java.util.ArrayList;

/**
 * Created by chris on 19/10/16.
 */

public class ServerUtils {
    public final static String APPLICATION_SERVER_PROTOCOL = "http://";
    public final static String APPLICATION_SERVER_IP_ADDRESS = "92.222.83.28";
    public final static String APPLICATION_SERVER_REQUEST_ADD_CONTACTS_URL = "/api.php";
    public final static String APPLICATION_SERVER_REQUEST_REMOVE_CONTACTS_URL = "/api.php";
    public final static String APPLICATION_SERVER_REQUEST_UPDATE_CONTACTS_URL = "/api.php";
    public final static String APPLICATION_SERVER_MIMETYPE_JSON = "application/json";

    private boolean sendNewContactEventALToServer(ArrayList<DbContactEvent> contactEventAL) {
        throw new UnsupportedOperationException("ServerUtils.sendNewContactEventALToServer() not implemented");
    }

    private boolean sendNewContactEventToServer(DbContactEvent dbContactEvent) {
        MyLog.i(this, "sending to server contactId: " + dbContactEvent.getId() + " data:" + dbContactEvent.getSerializedData());
        String url = APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_ADD_CONTACTS_URL + "[" + dbContactEvent.getSerializedData() + "]";
        MyLog.i(this, "request " + dbContactEvent.getId() + " data:" + dbContactEvent.getSerializedData());
        boolean result = doRequest(Request.Method.POST,url);
        return true;
    }

    private boolean doRequest(int method,String url) {

        RequestQueue queue = Volley.newRequestQueue(MyApp.getContext());
        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // Display the first 500 characters of the response string.
                MyLog.i(this, "doRequest Response is: " + response.substring(0, 500));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyLog.i(this, "doRequest: error " + error.getMessage());
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
        MyLog.i(this,"request enqueued " + url);
        return true;
    }
}