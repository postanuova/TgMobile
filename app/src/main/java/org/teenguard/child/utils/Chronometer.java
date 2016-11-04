package org.teenguard.child.utils;

/**
 * Created by chris on 02/11/16.
 */

public final class Chronometer {
    public boolean isActive;

    private long begin, end;

    public Chronometer() {
        System.out.println("chronometer created");
    }

    public void start() {
        System.out.println("chronometer started");
        begin = System.currentTimeMillis();
        isActive = true;
    }

    public void stop() {
        System.out.println("chronometer stopped");
        end = System.currentTimeMillis();
        isActive = false;
    }

    public void reset() {
        System.out.println("chronometer reset");
        begin=end=0;
        isActive = false;
    }

    public long getElapsedMilliseconds() {
        System.out.println("chronometer isActive=" + isActive);
        if (isActive) {
            return (System.currentTimeMillis() - begin);
        } else {
            return end - begin;
        }
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
