package org.teenguard.child.datatype;

import java.util.ArrayList;

/**
 * Created by chris on 14/11/16.
 * http://jsonviewer.stack.hu/
 */

public class BeatResponseJsonWrapper {
    public Data data;
    public int t;
    public String h;

    public class Data {
        public ArrayList<Geofences> geofences;
    }

    public class Geofences {
        public String id;
        public double latitude;
        public double longitude;
        public int radius;
        public boolean enter;
        public boolean leave;
    }
}
