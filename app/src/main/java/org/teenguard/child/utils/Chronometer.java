package org.teenguard.child.utils;

/**
 * Created by chris on 02/11/16.
 */

public final class Chronometer {
    public boolean isActive;

    private long begin, end;

    public void start() {
        begin = System.currentTimeMillis();
        isActive = true;
    }

    public void stop() {
        end = System.currentTimeMillis();
        isActive = false;
    }

    public long getTime() {
        return end - begin;
    }

    public long getMilliseconds() {
        return end - begin;
    }

    public double getSeconds() {
        return (end - begin) / 1000.0;
    }

    public double getMinutes() {
        return (end - begin) / 60000.0;
    }

    public double getHours() {
        return (end - begin) / 3600000.0;
    }
}
