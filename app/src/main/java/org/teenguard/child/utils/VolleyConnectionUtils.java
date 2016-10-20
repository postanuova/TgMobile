package org.teenguard.child.utils;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.teenguard.child.dbdatatype.DbContactEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chris on 19/10/16.
 */
//https://developer.android.com/training/volley/index.html
public class VolleyConnectionUtils {
    public final static String APPLICATION_SERVER_PROTOCOL = "http://";
    public final static String APPLICATION_SERVER_IP_ADDRESS = "92.222.83.28";
    public final static String APPLICATION_SERVER_REQUEST_ADD_CONTACTS_URL = "/api.php";
    public final static String APPLICATION_SERVER_REQUEST_REMOVE_CONTACTS_URL = "/api.php";
    public final static String APPLICATION_SERVER_REQUEST_UPDATE_CONTACTS_URL = "/api.php";
    public final static String APPLICATION_SERVER_MIMETYPE_JSON = "application/json";

    private static boolean sendNewContactEventALToServer(ArrayList<DbContactEvent> contactEventAL) {
        throw new UnsupportedOperationException("ServerUtils.sendNewContactEventALToServer() not implemented");
    }

    public static boolean sendNewContactEventToServer(DbContactEvent dbContactEvent) {
        Log.i("ServerUtils", "sending to server contactId: " + dbContactEvent.getId() + " data:" + dbContactEvent.getSerializedData());
        String url = APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_ADD_CONTACTS_URL;//+ "[" + dbContactEvent.getSerializedData() + "]";
        Log.i("ServerUtils", "request " + dbContactEvent.getId() + " data:" + dbContactEvent.getSerializedData());


        /*try {
            JSONArray jsonArray = new JSONArray(dbContactEvent.getSerializedData());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        boolean result = doRequest(Request.Method.POST,url,"[" + dbContactEvent.getSerializedData() + "]");
        return true;
    }

    public static boolean doRequest(int method, String url,final String myParam) {
        RequestQueue queue = Volley.newRequestQueue(MyApp.getContext());
        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the first 500 characters of the response string.
                MyLog.i(this, "doRequest Response is: " + response);
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyLog.i(this, "doRequest: error " + error.getMessage());
            }
            })
        {   @Override
            protected Map<String,String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Data", myParam);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                //params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("Content-Type",APPLICATION_SERVER_MIMETYPE_JSON);
                return params;
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
        //MyLog.i(this,"request enqueued " + url);
        return true;
    }



    /*public static boolean doRequest(int method, String url,final String myParam) {
        RequestQueue queue = Volley.newRequestQueue(MyApp.getContext());
        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the first 500 characters of the response string.
                MyLog.i(this, "doRequest Response is: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyLog.i(this, "doRequest: error " + error.getMessage());
            }
        })
        {   @Override
        protected Map<String,String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("Data", myParam);
            return params;
        }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                //params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("Content-Type",APPLICATION_SERVER_MIMETYPE_JSON);
                return params;
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
        //MyLog.i(this,"request enqueued " + url);
        return true;
    }
*/


    /*public static void main(String args[]) {
        doRequest(Request.Method.POST,"http://92.222.83.28/api.php");
    }*/
}