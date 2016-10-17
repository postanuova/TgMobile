package org.teenguard.child.activity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

import org.teenguard.child.observer.ContactListObserver;
import org.teenguard.child.R;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {
    private SimpleCursorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /////////
        startReadingDeviceContacts();
        //startMonitoringContactsChanges();
        ////////////

    }

    private void startMonitoringContactsChanges() {
        ContactListObserver contactListObserver = new ContactListObserver(null);
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        getContentResolver().registerContentObserver(uri,true, contactListObserver);
    }


    private void startReadingDeviceContacts() {

        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = {
                ContactsContract.Contacts._ID,              //0
                ContactsContract.Contacts.DISPLAY_NAME ,    //1
                ContactsContract.Contacts.LOOKUP_KEY
        };
        String selection = "((" + ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                + ContactsContract.Contacts.DISPLAY_NAME + " != '' ))";
        //String selection = "" ;
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " ASC";
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
        //start dumping of contacts
        dumpContacts(cursor);
        cursor.close();
    }

    private void dumpContacts(Cursor cursor) {
        String columnNamesAR [] = cursor.getColumnNames();
        for (int i = 0; i < columnNamesAR.length; i++) {
            Log.i("ContactURI column names",  i + " " + columnNamesAR[i]);
        }
        int _idIdx=cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int displayNameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        //int mimeTypeIdx = cursor.getColumnIndex(ContactsContract.Contacts.Data.MIMETYPE);
        int lookupKeyIdx = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
        while(cursor.moveToNext()) {
            int _id = cursor.getInt(_idIdx);
            String displayName = cursor.getString(displayNameIdx);
            //String mimeType = cursor.getString(mimeTypeIdx);
            String lookupKey =  cursor.getString(lookupKeyIdx);

            ArrayList<String> numberAL = getUserNumberAL(lookupKey);
            if(numberAL != null && numberAL.size()>0) {
                Log.i( "Contacts","_ID " + _id + " DISPLAY_NAME " + displayName);
                for(String number:numberAL) {
                    Log.i("number " , number);
                }
            }
        }
    }


    private ArrayList<String> getUserNumberAL(String lookupKey) {
        //Log.i("Data","reading user info for lookupKey " + lookupKey);
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        String selection =  ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ?";

        String[] selectionArgs = { lookupKey };
        String sortOrder = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY;
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(uri,projection , selection, selectionArgs, sortOrder);
        //Log.i("test","Phone cursor rows count " + cursor.getCount());
        ArrayList numberAL = new ArrayList();
        while(cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            numberAL.add(number);
        }
        cursor.close();
        return numberAL;
    }
}
/*
I/column names: 0 data_version
I/column names: 1 phonetic_name
I/column names: 2 data_set
I/column names: 3 phonetic_name_style
I/column names: 4 contact_id
I/column names: 5 lookup
I/column names: 6 phonebook_label_alt
I/column names: 7 data12
I/column names: 8 data11
I/column names: 9 data10
I/column names: 10 mimetype
I/column names: 11 data15
I/column names: 12 data14
I/column names: 13 data13
I/column names: 14 display_name_source
I/column names: 15 photo_uri
I/column names: 16 data_sync1
I/column names: 17 data_sync3
I/column names: 18 data_sync2
I/column names: 19 contact_chat_capability
I/column names: 20 data_sync4
I/column names: 21 account_type
I/column names: 22 account_type_and_data_set
I/column names: 23 custom_ringtone
I/column names: 24 photo_file_id
I/column names: 25 has_phone_number
I/column names: 26 status
I/column names: 27 data1
I/column names: 28 chat_capability
I/column names: 29 data4
I/column names: 30 data5
I/column names: 31 data2
I/column names: 32 data3
I/column names: 33 data8
I/column names: 34 phonebook_bucket
I/column names: 35 data9
I/column names: 36 data6
I/column names: 37 group_sourceid
I/column names: 38 times_used
I/column names: 39 account_name
I/column names: 40 data7
I/column names: 41 display_name
I/column names: 42 phonebook_bucket_alt
I/column names: 43 phonebook_label
I/column names: 44 raw_contact_is_user_profile
I/column names: 45 in_visible_group
I/column names: 46 display_name_alt
I/column names: 47 contact_status_res_package
I/column names: 48 is_primary
I/column names: 49 contact_status_ts
I/column names: 50 raw_contact_id
I/column names: 51 times_contacted
I/column names: 52 contact_status
I/column names: 53 status_res_package
I/column names: 54 status_icon
I/column names: 55 contact_status_icon
I/column names: 56 version
I/column names: 57 mode
I/column names: 58 last_time_contacted
I/column names: 59 contact_last_updated_timestamp
I/column names: 60 res_package
I/column names: 61 _id
I/column names: 62 name_verified
I/column names: 63 dirty
I/column names: 64 status_ts
I/column names: 65 is_super_primary
I/column names: 66 photo_thumb_uri
I/column names: 67 photo_id
I/column names: 68 send_to_voicemail
I/column names: 69 name_raw_contact_id
I/column names: 70 contact_status_label
I/column names: 71 status_label
I/column names: 72 sort_key_alt
I/column names: 73 starred
I/column names: 74 sort_key
I/column names: 75 contact_presence
I/column names: 76 sourceid
I/column names: 77 last_time_used
 */