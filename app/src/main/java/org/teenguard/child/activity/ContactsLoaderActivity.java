package org.teenguard.child.activity;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.teenguard.child.observer.ContactListObserver;
import org.teenguard.child.utils.Constant;
import org.teenguard.child.R;

import java.util.ArrayList;

public class ContactsLoaderActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    //Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;
    //Uri numberUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_loader);
        getLoaderManager().initLoader(0, null,this);
        startReadingDeviceContactsLoader();
    }

    private void startReadingDeviceContactsLoader() {
        getLoaderManager().restartLoader (0, null, this);
        startMonitoringContactsChanges();
    }



    private void startMonitoringContactsChanges() {
        ContactListObserver contactListObserver = new ContactListObserver(null);

        getContentResolver().registerContentObserver(Constant.CONTACTS_URI,true, contactListObserver);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i("Loader","called onCreateLoader()");
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                + Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                + Contacts.DISPLAY_NAME + " != '' ))";

        String[] projection = {
                ContactsContract.Contacts._ID,              //0
                ContactsContract.Contacts.DISPLAY_NAME ,    //1
                ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP,
                ContactsContract.Contacts.NAME_RAW_CONTACT_ID,
                ContactsContract.Contacts.LOOKUP_KEY
        };
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " ASC";
        CursorLoader cursorLoader = new CursorLoader(
                ContactsLoaderActivity.this,
                uri,
                projection,
                select,
                null,
                sortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i("Loader","called onLoadFinished()");
        Log.i("Loader","rows " + cursor.getCount() + "columns " + cursor.getColumnCount());
        dumpContacts(cursor);
        //cursor.close();
       //simpleCursorAdapter.swapCursor(data);
    }

    private void dumpContacts(Cursor cursor) {
        String columnNamesAR [] = cursor.getColumnNames();
        for (int i = 0; i < columnNamesAR.length; i++) {
            Log.i("contactUri column names",  i + " " + columnNamesAR[i]);
        }
        int _idIdx=cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int displayNameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        //int mimeTypeIdx = cursor.getColumnIndex(ContactsContract.Contacts.Data.MIMETYPE);
        int lookupKeyIdx = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
        int nameRawContactIdIdx = cursor.getColumnIndex(Contacts.NAME_RAW_CONTACT_ID);
        int lastUpdatedTimestampIdx = cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP);
        while(cursor.moveToNext()) {
            int _id = cursor.getInt(_idIdx);
            String displayName = cursor.getString(displayNameIdx);
            //String mimeType = cursor.getString(mimeTypeIdx);
            String lookupKey =  cursor.getString(lookupKeyIdx);
            String nameRawContactId = cursor.getString(nameRawContactIdIdx);
            String lastUpdatedTimestamp = cursor.getString(lastUpdatedTimestampIdx);
            ArrayList<String> numberAL = getUserNumberAL(lookupKey);
            if(numberAL != null && numberAL.size()>0) {
                Log.i( "Contacts","_ID " + _id + " DISPLAY_NAME " + displayName + " lastUpdatedTimestamp " + lastUpdatedTimestamp + " nameRawContactId " + nameRawContactId);
                for(String number:numberAL) {
                    Log.i("number " , number);
                }
            }
        }
    }

    private ArrayList<String> getUserNumberAL(String lookupKey) {
        //Log.i("Data","reading user info for lookupKey " + lookupKey);

        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        String selection =  ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ?";

        String[] selectionArgs = { lookupKey };
        String sortOrder = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY;
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(Constant.CONTACTS_CONTENT_URI,projection , selection, selectionArgs, sortOrder);
        //Log.i("test","Phone cursor rows count " + cursor.getCount());
        ArrayList numberAL = new ArrayList();
        while(cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            numberAL.add(number);
        }
        cursor.close();
        return numberAL;
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i("Loader","called onLoaderReset()");
        //simpleCursorAdapter.swapCursor(null);
    }


}
