package org.teenguard.child.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;

import org.teenguard.child.R;
import org.teenguard.child.dbdao.DbContactDAO;
import org.teenguard.child.dbdao.DbContactEventDAO;
import org.teenguard.child.dbdao.DbMediaDAO;
import org.teenguard.child.dbdao.DbMediaEventDAO;
import org.teenguard.child.utils.MyApp;
import org.teenguard.child.utils.MyConnectionUtils;
import org.teenguard.child.utils.MyLog;

public class ChildMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i(this,"invoked onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MyLog.i(this,"invoked onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.child_main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {

            case R.id.item_contacts_cleaner: {
                Log.i(this.getClass().getName(),"cleaning contact table");
                DbContactDAO dbContactDAO = new DbContactDAO(MyApp.getContext());
                dbContactDAO.emptyTable();
                DbContactEventDAO dbContactEventDAO = new DbContactEventDAO(MyApp.getContext());
                dbContactEventDAO.emptyTable();
                return true;
            }

            case R.id.item_media_cleaner: {
                Log.i(this.getClass().getName(),"cleaning media table");
                DbMediaDAO dbMediaDAO = new DbMediaDAO(MyApp.getContext());
                dbMediaDAO.emptyMediaTable();
                DbMediaEventDAO dbMediaEventDAO = new DbMediaEventDAO(MyApp.getContext());
                dbMediaEventDAO.emptyTable();
                return true;
            }

            case R.id.item_send_db_contact_event: {
                Log.i(this.getClass().getName(),"sending db contact event ");
                String data = "{\"id\":\"227\",\"date\":\"1476971135466\",\"first_name\":\"Aaaaaab\",\"last_name\":\"\",\"phone_numbers\":[\"147\"]}";
                MyConnectionUtils.doAndroidPost(data);
                return true;
            }


            //item_send_db_contact_event{"id":"227","date":"1476971135466","first_name":"Aaaaaab","last_name":"","phone_numbers":["147"]}

            default:
                return super.onContextItemSelected(item);
        }
    }



}
