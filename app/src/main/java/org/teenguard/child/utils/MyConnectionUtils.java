package org.teenguard.child.utils;

import org.teenguard.child.datatype.MyServerResponse;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by chris on 20/10/16.
 */
/* added
// android {
useLibrary 'org.apache.http.legacy'
        }
*/

public class MyConnectionUtils {

    public static MyServerResponse doAndroidRequest(String requestMethod,URL url, String contentType,  String data) {
        MyServerResponse myServerResponse = new MyServerResponse();
        HttpURLConnection connection = null;
        try {
            //Create connection
            //URL url = new URL(APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_ADD_CONTACTS_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Content-Type", contentType);
            connection.setUseCaches(false);
            connection.setDoInput(true);

            ////////////
            if(requestMethod.equalsIgnoreCase("DELETE")) {//java.net.ProtocolException: DELETE does not support writing
                System.out.println("overriding RequestProperty for DELETE");
                connection.setRequestProperty("X-HTTP-Method-Override", "DELETE");
            }
                ///////
            //connection.setRequestProperty("Content-Length", "" + data);
            //connection.setRequestProperty("Content-Language", "en-US");
            //Send request
            if(requestMethod.equalsIgnoreCase("POST")) {
                connection.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(data);
                wr.flush();
                wr.close();
            }
            //Get Response
            System.out.println("connection.getResponseCode() = " + connection.getResponseCode());
            myServerResponse.setResponseCode(connection.getResponseCode());
            myServerResponse.setRequestMethod(requestMethod);
            myServerResponse.setRequestUrl(url.toString());
            myServerResponse.setRequestBody(data);
            if(connection.getInputStream() != null) myServerResponse.setResponseBody(TypeConverter.inputStreamToString(connection.getInputStream()));
            if(connection.getErrorStream() != null) myServerResponse.setResponseError(TypeConverter.inputStreamToString(connection.getErrorStream()));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("doAndroidRequest : server connection failed:device is offline? ");
            return myServerResponse;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return myServerResponse;
    }


   /* public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }*/

    /**
     * deprecated
     * @param data
     * @return
     */
    /*public static String doApachePost(String data) {
        //http://stackoverflow.com/questions/16079991/send-a-string-on-android-with-httppost-without-using-namevaluepairs
        HttpClient httpclient = new DefaultHttpClient();
        String postUrl = APPLICATION_SERVER_PROTOCOL + APPLICATION_SERVER_IP_ADDRESS + APPLICATION_SERVER_REQUEST_ADD_CONTACTS_URL;
        System.out.println("postUrl = " + postUrl);
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
    }*/



}






