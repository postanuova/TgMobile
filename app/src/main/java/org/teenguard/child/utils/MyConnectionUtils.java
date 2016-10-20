package org.teenguard.child.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.teenguard.child.utils.TypeConverter.inputStreamToString;

/**
 * Created by chris on 20/10/16.
 */
/* added
// android {
useLibrary 'org.apache.http.legacy'
        }
*/

public class MyConnectionUtils {

    public final static String APPLICATION_SERVER_PROTOCOL = "http://";
    public final static String APPLICATION_SERVER_IP_ADDRESS = "92.222.83.28";
    public final static String APPLICATION_SERVER_REQUEST_ADD_CONTACTS_URL = "/api.php";
    public final static String APPLICATION_SERVER_REQUEST_REMOVE_CONTACTS_URL = "/api.php";
    public final static String APPLICATION_SERVER_REQUEST_UPDATE_CONTACTS_URL = "/api.php";
    public final static String APPLICATION_SERVER_MIMETYPE_JSON = "application/json";


 /*
    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }*/

    public static String doApachePost(String data) {
        //http://stackoverflow.com/questions/16079991/send-a-string-on-android-with-httppost-without-using-namevaluepairs
        HttpClient httpclient = new DefaultHttpClient();
        String postUrl = APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_ADD_CONTACTS_URL;
        HttpPost httppost = new HttpPost(postUrl);
        String responseString ="";

        try {
            httppost.setEntity(new StringEntity(data));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            HttpResponse resp = httpclient.execute(httppost);
            HttpEntity httpEntity = resp.getEntity();
             responseString = inputStreamToString(httpEntity.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("responseString " + responseString);
        return responseString;
    }



}
