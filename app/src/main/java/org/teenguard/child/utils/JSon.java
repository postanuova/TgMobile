package org.teenguard.child.utils;

/**
 *
 * @author chris
 */
public class JSon {

    private String jSonString = "{";

    public JSon() {
    }

    /** aggiunge un elemento stringa all'ogetto JSON */
    public void add(String param, String value) {
        jSonString += composePair(param, value) + ",";
    }

    /** aggiunge un elemento int all'ogetto JSON */
    public void add(String param, int value) {
        jSonString += composeNumericPair(param, Integer.toString(value)) + ",";
    }

    /** aggiunge un elemento double all'ogetto JSON */
    public void add(String param, double value) {
        jSonString += composeNumericPair(param, Double.toString(value)) + ",";
    }

    /** aggiunge un elemento long all'ogetto JSON */
    public void add(String param, long value) {
        jSonString += composeNumericPair(param, Long.toString(value)) + ",";
    }

    /** aggiunge un elemento float all'ogetto JSON */
    public void add(String param, float value) {
        jSonString += composeNumericPair(param, Float.toString(value)) + ",";
    }

   /* *//** aggiunge un elemento Calendar yyyy/mm/dd all'ogetto JSON *//*
    public void add(String param, Calendar value) {
        jSonString += composePair(param, TypeConverter.calendarToStringWithoutTime(value)) + ",";
    }*/

    /** aggiunge un elemento booleano all'ogetto JSON */
    public void add(String param, boolean value) {
        jSonString += composePair(param, Boolean.toString(value)) + ",";
    }

   /**
    *
     * aggiunge un array(nome_array,lista di elementi tra parentesi quadre separati da una virgola) all'ogetto JSON :
     * */
    public void addArray(String arrayName, String arrayValues) {
        jSonString += "\"" + arrayName + "\":" + arrayValues + ",";
    //jSonString +=  "\"" + arrayName + "\":[" + arrayValues + "]" + ",";
    }

    private String composePair(String param, String value) {
        return "\"" + param + "\":\"" + value + "\"";
    //return param + ":\"" + value + "\"";
    }

    private String composeNumericPair(String param, String value) {
        return "\"" + param + "\":" + value;
        //return param + ":\"" + value + "\"";
    }

    /**
     * @return the jSonString   {"primo":"uno","secondo":"due"}
     */
    public String getJSonString() {
        //elimino l'ultima virgola e la sostituisco con '}'
        return jSonString.substring(0, jSonString.length() - 1) + "}";
    }

    public String toString() {
        return getJSonString();
    }

    /**
     * @return the ArrayJSonString      primo:uno,secondo:due
     *NOTA:questo formato e' utile per popolare dinamicamente le select
     */
    public String getArrayJSonString() {
        String jsonString = getJSonString();
        jsonString = jsonString.replace("\"", "");
        jsonString = jsonString.replace("{", "");
        jsonString = jsonString.replace("}", "");
        return jsonString;
    }

    //serve per gli export
    public static String purifyFromQuoteWithSubstitution(String stringToPurify) {
        stringToPurify = stringToPurify.replace("\"", "`");
        stringToPurify = stringToPurify.replace("'", "`");
        stringToPurify = stringToPurify.replace("{", "");
        stringToPurify = stringToPurify.replace("}", "");
        stringToPurify = stringToPurify.replace("/\n|\r/g"," ");
        int i =10;
        char nextLine= (char)i;
        String nextLineString = String.valueOf(nextLine);
        stringToPurify = stringToPurify.replace(nextLineString," ");
        i =13;
        nextLine= (char)i;
        nextLineString = String.valueOf(nextLine);
        stringToPurify = stringToPurify.replace(nextLineString," ");
        return stringToPurify;
    }

    //serve per le query
    public static String purifyFromQuoteWithHTMLEncoding(String stringToPurify) {
        stringToPurify = stringToPurify.replaceAll("\"", "&quot;");
        stringToPurify = stringToPurify.replaceAll("'", "&apos;");
        stringToPurify = stringToPurify.replaceAll("\r\n", "<br/>");
        stringToPurify = stringToPurify.replaceAll("\n\r", "<br/>");
        stringToPurify = stringToPurify.replaceAll("\n", "<br/>");
        stringToPurify = stringToPurify.replaceAll("\r", "<br/>");
        return stringToPurify;
    }

    public static String purifyFromHTMLEncoding(String stringToPurify) {
        stringToPurify = stringToPurify.replace("&quot;","\"");
        stringToPurify = stringToPurify.replace("&apos;","'");
        stringToPurify = stringToPurify.replaceAll("<br/>"," ");
        return stringToPurify;
    }
}