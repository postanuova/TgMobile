package org.teenguard.child.observer;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

/**
 * Created by chris on 10/10/16.
 */

public class GpsObserver extends ContentObserver {



/**
 * Created by chris on 09/10/16.
 */

//http://stackoverflow.com/questions/22012274/contentobserver-onchange-method-gets-called-many-times

    public GpsObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        Log.i("GpsObserver", "onChange Old: API < 16");
        this.onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Log.i("GpsObserver", "onChange New" + " selfChange " + selfChange + " URI " + uri);
        super.onChange(selfChange);  //o this??

        //check image file changes in MediaStore
         /*   readFromMediaStore(_context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

//register for external media changes for image files
    if(mediaStoreObserver!=null){
        _context.getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,false,mediaStoreObserver);
        */
    }
}
