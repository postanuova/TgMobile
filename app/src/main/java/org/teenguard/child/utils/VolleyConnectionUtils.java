package org.teenguard.child.utils;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.teenguard.child.datatype.MyServerResponse;
import org.teenguard.child.dbdatatype.DbContactEvent;

import java.util.concurrent.TimeUnit;

/**
 * Created by chris on 19/10/16.
 */
//https://developer.android.com/training/volley/index.html
    //https://www.captechconsulting.com/blogs/android-volley-library-tutorial
public class VolleyConnectionUtils {
    public final static String APPLICATION_SERVER_PROTOCOL = "http://";
    public final static String APPLICATION_SERVER_IP_ADDRESS = "92.222.83.28";
    public final static String APPLICATION_SERVER_REQUEST_ADD_CONTACTS_URL = "/api.php";
    public final static String APPLICATION_SERVER_REQUEST_DELETE_CONTACTS_URL = "/api.php";
    public final static String APPLICATION_SERVER_REQUEST_UPDATE_CONTACTS_URL = "/api.php";
    public final static String APPLICATION_SERVER_MIMETYPE_JSON = "application/json";

    public static int counter = 0;

    public static void addContactToServer(DbContactEvent dbContactEvent) {
        Log.i("ServerUtils", " sending to server contactId: " + dbContactEvent.getId() + " data:" + dbContactEvent.getSerializedData());
        String url = APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_ADD_CONTACTS_URL;//+ "[" + dbContactEvent.getSerializedData() + "]";
        Log.i("ServerUtils", "request " + dbContactEvent.getId() + " data:" + dbContactEvent.getSerializedData());
        doRequest(Request.Method.DELETE,url,"[" + dbContactEvent.getSerializedData() + "]");
    }

   /* public static MyServerResponse updateContactIntoServer(DbContactEvent dbContactEvent) {
        return addContactToServer(dbContactEvent);
    }*/

    /**
     *
 //    * @param csvId csv list of csId to flag as deleted into db
     * @return
     */
   /* public static MyServerResponse deleteContactFromServer(String csvId) {
        String url = APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_DELETE_CONTACTS_URL;
        return doRequest(Request.Method.DELETE,url,"[" + csvId + "]");
    }*/



    public static void doRequest(int method, String url, final String httpPostBody) {
        final MyServerResponse myServerResponse = new MyServerResponse();
        RequestQueue queue = Volley.newRequestQueue(MyApp.getInstance().getApplicationContext());
        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MyLog.i(this, " doRequest Response is: " + response);
                myServerResponse.setResponseBody(response);
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyLog.i(this, "doRequest: error " + error.getMessage());
                myServerResponse.setResponseError(error.getMessage());
            }
            })
        {

            @Override
            public byte[] getBody() throws AuthFailureError {
//http://stackoverflow.com/questions/22057023/android-volley-post-string-in-body
                return httpPostBody.getBytes();
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                System.out.println("server response code: = " + mStatusCode);
                myServerResponse.setResponseCode(mStatusCode);
                return super.parseNetworkResponse(response);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(5),
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
        myServerResponse.dump();
       /* myServerResponse.setCounter(counter++);
        myServerResponse.dump();
        myServerResponse.setResponseCode(666);
        //myServerResponse.setResponseBody("rrrrrrresponse");
        //return myServerResponse;*/
    }
}