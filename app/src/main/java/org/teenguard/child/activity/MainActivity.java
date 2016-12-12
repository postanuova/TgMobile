package org.teenguard.child.activity;
//libphonenumber libreria per validazione e nazioni dei numeri telefonici

import android.content.Intent;
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
import org.teenguard.child.dbdao.DbGeofenceDAO;
import org.teenguard.child.dbdao.DbLocationEventDAO;
import org.teenguard.child.dbdao.DbMediaDAO;
import org.teenguard.child.dbdao.DbMediaEventDAO;
import org.teenguard.child.dbdao.DbVisitEventDAO;
import org.teenguard.child.utils.MyApp;
import org.teenguard.child.utils.MyLog;
import org.teenguard.parent.activity.WebFrameActivity;

//moved to ssd
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("MainActivity.onCreate");
        MyApp.dumpSharedPreferences();
        boolean isChild = MyApp.getSharedPreferences().getBoolean("IS-CHILD", false);
        boolean isChildConfigured = MyApp.getSharedPreferences().getBoolean("IS-CHILD-CONFIGURED", false);
        boolean isParent = MyApp.getSharedPreferences().getBoolean("IS-PARENT", false);
        boolean isParentConfigured = MyApp.getSharedPreferences().getBoolean("IS-PARENT-CONFIGURED", false);

        /*/////////////////////////////////////////////
        System.out.println("MainActivity.onCreate skipping");
        Intent skipIntent = new Intent(MyApp.getContext(), InsertSmsCodeActivity.class);
        startActivity(skipIntent);
        ////////////////////////////////////////////////*/

        if   ((!isChild && !isChildConfigured && !isParent && !isParentConfigured)) {//primo avvio
            Intent welcomeActivityIntent = new Intent(MyApp.getContext(), WelcomeActivity.class);
            startActivity(welcomeActivityIntent);
            System.out.println("MainActivity.onCreate starting ProperlySettedActivity");
        }

        if   ((isChild&& isChildConfigured)) {
            Intent properlySettedIntent = new Intent(MyApp.getContext(), ProperlySettedActivity.class);
            startActivity(properlySettedIntent);
            System.out.println("MainActivity.onCreate starting ProperlySettedActivity");
        }

        if((isParent && isParentConfigured)) {
            Intent webFrameIntent = new Intent(MyApp.getContext(), WebFrameActivity.class);
            startActivity(webFrameIntent);
            System.out.println("MainActivity.onCreate starting WebFrameActivity");
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("MainActivity.onResume");
     /*   /////////////////////////////////////////////
        System.out.println("MainActivity.onCreate skipping");
        Intent skipIntent = new Intent(MyApp.getContext(), InsertSmsCodeActivity.class);
        startActivity(skipIntent);
        ////////////////////////////////////////////////*/
        boolean isChild = MyApp.getSharedPreferences().getBoolean("IS-CHILD", false);
        boolean isChildConfigured = MyApp.getSharedPreferences().getBoolean("IS-CHILD-CONFIGURED", false);
        if   ((isChild == true && isChildConfigured == true)) {
            Intent properlySettedIntent = new Intent(MyApp.getContext(), ProperlySettedActivity.class);
            startActivity(properlySettedIntent);
            System.out.println("MainActivity.onCreate starting ProperlySettedActivity");
        }
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
                DbContactDAO dbContactDAO = new DbContactDAO();
                dbContactDAO.emptyTable();
                DbContactEventDAO dbContactEventDAO = new DbContactEventDAO();
                dbContactEventDAO.emptyTable();
                return true;
            }

            case R.id.item_media_cleaner: {
                Log.i(this.getClass().getName(),"cleaning media table");
                DbMediaDAO dbMediaDAO = new DbMediaDAO();
                dbMediaDAO.emptyMediaTable();
                DbMediaEventDAO dbMediaEventDAO = new DbMediaEventDAO();
                dbMediaEventDAO.emptyTable();
                return true;
            }

            case R.id.item_location_cleaner: {
                Log.i(this.getClass().getName(),"cleaning location table");
                DbLocationEventDAO dbLocationEventDAO = new DbLocationEventDAO();
                dbLocationEventDAO.emptyTable();
                return true;
            }

            case R.id.item_geofences_cleaner: {
                Log.i(this.getClass().getName(),"cleaning geofences table");
                DbGeofenceDAO dbGeofenceDAO = new DbGeofenceDAO();
                dbGeofenceDAO.delete();
                MyApp.resetSharedPreferences();
                MyApp.dumpSharedPreferences();
                return true;
            }

            case R.id.item_welcome: {
                Log.i(this.getClass().getName(),"welcome screen");
                //Intent intent = new Intent(this, WelcomeActivity.class);
                Intent intent = new Intent(this, WelcomeActivity.class);
                intent.putExtra("countryCode","34"); //quello con il +
                intent.putExtra("phoneNumber","603000000");
                String xSessidShared = MyApp.getSharedPreferences().getString("X-SESSID","");
                System.out.println("restarted: xSessidShared = " + xSessidShared);
                startActivity(intent);
                this.finish();//close current activity
                return true;
            }

            case R.id.item_clear_visit: {
                Log.i(this.getClass().getName(),"cleaning visitEvent table");
                DbVisitEventDAO dbVisitEventDAO = new DbVisitEventDAO();
                dbVisitEventDAO.delete();
                return true;
            }

            case R.id.item_parent: {
                Log.i(this.getClass().getName(),"parent_mode");
                Intent intent = new Intent(this, WebFrameActivity.class);
                startActivity(intent);
                return true;
            }

            //item_send_db_contact_event{"id":"227","date":"1476971135466","first_name":"Aaaaaab","last_name":"","phone_numbers":["147"]}

            default:
                return super.onContextItemSelected(item);
        }
    }



}
// TODO: 12/12/16 Alessandro Iannicelli TG, [11.12.16 01:47]
/*Alessandro Iannicelli TG, [11.12.16 01:35]
"date":20161211242559+0100
*la data non è tra apici

* i numeri di telefono andrebber "puliti", cioè niente + e niente spazi. Togliere tutto ciò che c'è nella string che non sia un carattere numerico


* i numeri sono in doppio array [["numero"]]

* quando i media non hanno la localizzazione, non mandare del tutto i campo "latitude", "longitude" e "accuracy"

*
E quando si tratta di foto ("media_type" = 0) non c'è bisogno di mandare "media_duration"


*/