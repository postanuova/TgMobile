package org.teenguard.child.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by chris on 11/11/16.
 */

public class CalendarUtils {

    public static long nowUTCMillis() {
        return System.currentTimeMillis();
    }

    public static String getDeviceTimezone() {
        //http://stackoverflow.com/questions/15068113/how-to-get-the-timezone-offset-in-gmtlike-gmt700-from-android-device
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        String   timeZone = new SimpleDateFormat("Z").format(calendar.getTime());
        String offset = timeZone.substring(0, 3) + timeZone.substring(3, 5);

        System.out.println("offset = " + offset);
        return offset;
    }

    public static void main(String args[]) {
        getDeviceTimezone();
    }

    /**
     *
     * @return YYYYMMDDHHMMSS+ZZZZ
     */
    public static String serverTimeFormat(long utcMillis) {
        if(utcMillis ==-1 ) utcMillis = nowUTCMillis();
        String dateTimeSTR;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkkmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateTimeSTR = sdf.format(new Date(utcMillis)) + getDeviceTimezone();
        return dateTimeSTR;
    }


}
