package org.teenguard.child.datatype;

/**
 * Created by chris on 20/10/16.
 */

/**
 * this class is a container for  server responses after a post
 */
public class MyServerResponse {
    private static int responseCounter = 0;
    private  int responseNumber;
    private int responseCode;
    private String requestMethod = "unknown";
    private String requestUrl ="unknown";
    private String requestBody = "unknown";
    private String responseBody = "unknown";
    private String responseErrorBody = "unknown";

    public MyServerResponse() {
        this.responseNumber = responseCounter++;
        this.setResponseCode(-1);
        this.requestMethod = "unknown";
        this.responseBody = null;
        this.responseErrorBody = null;
    }

    public void dump() {
        System.out.println("------ MyServerResponse ------");
        System.out.println("requestMethod = " + requestMethod);
        System.out.println("requestUrl = " + getRequestUrl());
        System.out.println("requestBody = " + getRequestBody());
        System.out.println("requestMethod = " + requestMethod);
        System.out.println("responseNumber = " + responseNumber);
        System.out.println("responseCode = " + responseCode);
        System.out.println("responseBody = " + responseBody);
        System.out.println("responseError = " + responseErrorBody);
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
}
