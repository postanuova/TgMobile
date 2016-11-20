package org.teenguard.child.datatype;

/**
 * Created by chris on 20/10/16.
 */

import java.util.HashMap;

/**
 * this class is a container for  server responses after a post
 */
public class MyServerResponse {
    private static int responseCounter = 0;
    private String requestMethod = "unknown";
    private String requestUrl ="unknown";
    private String requestBody = "unknown";
    private HashMap headerEntryHM = new <String,String>HashMap();
    private  int responseNumber;
    private int responseCode;
    private String responseMessage = "unknown";
    private String responseBody = "unknown";
    private String responseErrorBody = "unknown";

    public MyServerResponse() {
        this.responseNumber = responseCounter++;
        this.setResponseCode(-1);
        this.requestMethod = "unknown";
        this.responseBody = null;
        this.responseErrorBody = null;
        this.responseMessage = null;
    }

    public void shortDump() {
        System.out.println("----------- MyServerResponse SHORT (" + responseNumber + ") ------------");
        System.out.println("requestMethod = " + requestMethod);
        System.out.println("requestUrl = " + getRequestUrl());
        System.out.println("requestBody = <" + getRequestBody()+">");
        System.out.println("responseNumber = " + responseNumber);
        System.out.println("responseCode = " + responseCode);
        System.out.println("responseMessage = <" + responseMessage+">");
        System.out.println("---------------------------------------------");
    }

    public void dump() {
        System.out.println("----------- MyServerResponse (" + responseNumber + ") ------------");
        System.out.println("requestMethod = " + requestMethod);
        System.out.println("requestUrl = " + getRequestUrl());
        System.out.println("requestBody = <" + getRequestBody()+">");
        System.out.println("headerEntryHM size " + headerEntryHM.size());
        System.out.println("responseNumber = " + responseNumber);
        System.out.println("responseCode = " + responseCode);
        System.out.println("responseMessage = <" + responseMessage+">");
        if(responseBody != null) System.out.println("responseBody length = " + responseBody.length());
        System.out.println("responseBody = <" + responseBody +">");
        System.out.println("responseError = <" + responseErrorBody+">");

        System.out.println("---------------------------------------------");
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getResponseError() {
        return responseErrorBody;
    }

    public void setResponseError(String responseError) {
        this.responseErrorBody = responseError;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public int getResponseNumber() {
        return responseNumber;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }


    public HashMap getHeaderEntryHM() {
        return headerEntryHM;
    }

    public void setHeaderEntryHM(HashMap headerEntryHM) {
        this.headerEntryHM = headerEntryHM;
    }
}
