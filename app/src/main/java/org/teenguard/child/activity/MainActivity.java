package org.teenguard.child.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;

import org.teenguard.child.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       Log.i(this.getClass().getName(),"started onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(this.getClass().getName(),   "invoked onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.item_home: {
               Log.i(this.getClass().getName(),"selected localization on main menu");
                Intent intent= new Intent(this,MainActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.item_localization: {
               Log.i(this.getClass().getName(),"selected localization on main menu");
                Intent intent= new Intent(this,LocalizationActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.item_contacts: {
               Log.i(this.getClass().getName(),"selected contacts on main menu");
                Intent intent= new Intent(this,ContactsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.item_contacts_loader: {
               Log.i(this.getClass().getName(),"selected loader contacts on main menu");
                Intent intent= new Intent(this,ContactsLoaderActivity.class);
                startActivity(intent);
                return true;
            }

            default:
                return super.onContextItemSelected(item);
        }
    }
}