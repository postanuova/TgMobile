package org.teenguard.child.utils;

/**
 *
 * @author chris
 */
public class JSon {

    private String jSonString = "{";

    public JSon() {
    }

    /** aggiunge un elemento stringa all'oggetto JSON */
    public void add(String param, String value) {
        jSonString += composePair(param, value) + ",";
    }

    /** aggiunge un elemento int all'oggetto JSON */
    public void add(String param, int value) {
        jSonString += composePair(param, Integer.toString(value)) + ",";
    }

    /** aggiunge un elemento double all'oggetto JSON */
    public void add(String param, double value) {
        jSonString += composePair(param, Double.toString(value)) + ",";
    }

    /** aggiunge un elemento long all'oggetto JSON */
    public void add(String param, long value) {
        jSonString += composePair(param, Long.toString(value)) + ",";
    }

    /** aggiunge un elemento float all'oggetto JSON */
    public void add(String param, float value) {
        jSonString += composePair(param, Float.toString(value)) + ",";
    }

   /* *//** aggiunge un elemento Calendar yyyy/mm/dd all'oggetto JSON *//*
    public void add(String param, Calendar value) {
        jSonString += composePair(param, TypeConverter.calendarToStringWithoutTime(value)) + ",";
    }*/

    /** aggiunge un elemento booleano all'oggetto JSON */
    public void add(String param, boolean value) {
        jSonString += composePair(param, Boolean.toString(value)) + ",";
    }

    /** aggiunge un array(nome_array,lista di elementi tra parentesi quadre separati da una virgola) all'oggetto JSON :  */
    public void addArray(String arrayName, String arrayValues) {
        jSonString += "\"" + arrayName + "\":" + arrayValues + ",";
    //jSonString +=  "\"" + arrayName + "\":[" + arrayValues + "]" + ",";
    }

    private String composePair(String param, String value) {
        return "\"" + param + "\":\"" + value + "\"";
    //return param + ":\"" + value + "\"";
    }

    /**
     * @return the jSonString   {"primo":"uno","secondo":"due"}
     */
    public String getJSonString() {
        //elimino l'ultima virgola e la sostituisco con '}'
        return jSonString.substring(0, jSonString.length() - 1) + "}";
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
        stringToPurify = stringToPurify.replace("\"", "``");
        stringToPurify = stringToPurify.replace("'", "`");
        stringToPurify = stringToPurify.replace("{", "");
        stringToPurify = stringToPurify.replace("}", "");
        stringToPurify = stringToPurify.replace("/\n|\r/g"," ");
        int i =10;
        char aCapo= (char)i;
        String aCapoString = String.valueOf(aCapo);
        stringToPurify = stringToPurify.replace(aCapoString," ");
        i =13;
        aCapo= (char)i;
        aCapoString = String.valueOf(aCapo);
        stringToPurify = stringToPurify.replace(aCapoString," ");
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