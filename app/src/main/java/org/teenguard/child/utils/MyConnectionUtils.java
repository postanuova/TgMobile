package org.teenguard.child.utils;

import android.provider.Settings;

import org.json.JSONObject;
import org.teenguard.child.datatype.MyServerResponse;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by chris on 20/10/16.
 */
/* added
// android {
useLibrary 'org.apache.http.legacy'
        }
*/

public class MyConnectionUtils {


    public static boolean isAirplaneModeOn(){
        return Settings.Global.getInt(MyApp.getContext().getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }


    public static MyServerResponse doAndroidRequest(String requestMethod,URL url, String contentType,  String bodyData) {
        MyServerResponse myServerResponse = new MyServerResponse();
        if(!isAirplaneModeOn()) {
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
                if (requestMethod.equalsIgnoreCase("DELETE")) {//java.net.ProtocolException: DELETE does not support writing
                    System.out.println("overriding RequestProperty for DELETE");
                    connection.setRequestProperty("X-HTTP-Method-Override", "DELETE");
                }
                ///////
                //connection.setRequestProperty("Content-Length", "" + bodyData);
                //connection.setRequestProperty("Content-Language", "en-US");
                //Send request
                if (requestMethod.equalsIgnoreCase("POST")) {
                    connection.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.writeBytes(bodyData);
                    wr.flush();
                    wr.close();
                }

                /*if(requestMethod.equalsIgnoreCase("GET")) {
                    connection.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.writeBytes(bodyData);
                    wr.flush();
                    wr.close();
                }*/

                //Get Response
                try {
                    // Will throw IOException if server responds with 401.
                    if(connection.getHeaderFields() != null) {
                        Map<String, List<String>> headerMap = connection.getHeaderFields();
                        for (Map.Entry<String, List<String>> entry : headerMap.entrySet()) {
                            if (entry.getKey() != null) {
                                List<String> currHeaderValues = entry.getValue();
                                myServerResponse.getHeaderEntryHM().put(entry.getKey(), currHeaderValues.get(0));
                            }
                        }
                    }
                    myServerResponse.setResponseCode(connection.getResponseCode());
                    System.out.println("connection.getResponseCode() = " + connection.getResponseCode());
                    myServerResponse.setRequestMethod(requestMethod);
                    myServerResponse.setRequestUrl(url.toString());
                    myServerResponse.setRequestBody(bodyData);

                    myServerResponse.setResponseMessage(connection.getResponseMessage());
                    if (connection.getInputStream() != null) {
                        myServerResponse.setResponseBody(TypeConverter.inputStreamToString(connection.getInputStream()));
                    }
                    if (connection.getErrorStream() != null) {
                        myServerResponse.setResponseError(TypeConverter.inputStreamToString(connection.getErrorStream()));
                    }
                } catch (IOException e) {
                    // Will return 401, because now connection has the correct internal state.
                    e.printStackTrace();
                    myServerResponse.setResponseCode(connection.getResponseCode());
                    System.out.println("doAndroidRequest : server connection failed:is 401 ");
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("doAndroidRequest : server connection failed:is device offline? ");
                return myServerResponse;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        } else {
            myServerResponse.setResponseMessage("DEVICE IS IN AIRPLANE MODE");
        }
        return myServerResponse;
    }

    public static MyServerResponse doAndroidMediaRequestWithHeader(String requestMethod, URL url, String contentType, JSONObject jsonObjectHeader, String bodyData) {
        MyServerResponse myServerResponse = new MyServerResponse();
        if(!isAirplaneModeOn()) {
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
                //connection.setRequestProperty("Content-Length", "" + bodyData);
                //connection.setRequestProperty("Content-Language", "en-US");
                //setting header
                connection.setRequestProperty("x-id",String.valueOf(jsonObjectHeader.get("id")));
                connection.setRequestProperty("x-date",String.valueOf(jsonObjectHeader.get("date")));
                connection.setRequestProperty("x-media_type",String.valueOf(jsonObjectHeader.get("media_type")));
                connection.setRequestProperty("x-media_duration",String.valueOf(jsonObjectHeader.get("media_duration")));
                connection.setRequestProperty("x-latitude",String.valueOf(jsonObjectHeader.get("latitude")));
                connection.setRequestProperty("x-longitude",String.valueOf(jsonObjectHeader.get("longitude")));
                connection.setRequestProperty("x-accuracy",String.valueOf(jsonObjectHeader.get("accuracy")));


                //Send request
                if(requestMethod.equalsIgnoreCase("POST")) {
                    connection.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.writeBytes(bodyData);
                    wr.flush();
                    wr.close();
                }
                //Get Response
                System.out.println("connection.getResponseCode() = " + connection.getResponseCode());
                myServerResponse.setResponseCode(connection.getResponseCode());
                myServerResponse.setRequestMethod(requestMethod);
                myServerResponse.setRequestUrl(url.toString());
                myServerResponse.setRequestBody(bodyData);
                if(connection.getInputStream() != null) myServerResponse.setResponseBody(TypeConverter.inputStreamToString(connection.getInputStream()));
                if(connection.getErrorStream() != null) myServerResponse.setResponseError(TypeConverter.inputStreamToString(connection.getErrorStream()));
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("doAndroidMediaRequestWithHeader : server connection failed:is device  offline? ");
                return myServerResponse;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        } else {
            myServerResponse.setResponseMessage("DEVICE IS IN AIRPLANE MODE");
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






