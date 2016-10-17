package org.teenguard.child.utils;

import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;

/**
 * Created by chris on 09/10/16.
 */

public final class Constant {
    public static final Uri CONTACTS_URI = ContactsContract.Contacts.CONTENT_URI;
    public static final Uri CONTACTS_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    public static final Uri PHOTO_INTERNAL_CONTENT_URI = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
    public static final Uri PHOTO_EXTERNAL_CONTENT_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;



}
