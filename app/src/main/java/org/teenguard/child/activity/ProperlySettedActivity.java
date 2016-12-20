package org.teenguard.child.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.teenguard.child.R;
import org.teenguard.child.service.ChildMonitoringService;
import org.teenguard.child.utils.MyApp;

public class ProperlySettedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("ProperlySettedActivity started");

        setContentView(R.layout.activity_properly_setted);
        viewBinding();
        getSupportActionBar().hide(); //nasconde la barra
        //  TODO: 02/12/16 implementare giro di richiesta permessi
        //  per i permessi cambiati post su beat {contact_permission:bool, location_permission:bool, photo_permission:bool}
// TODO: 14/12/16 autorelaunch activity http://chintanrathod.com/auto-restart-application-after-crash-forceclose-in-android/

    }
    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("ProperlySettedActivity.onStart");
        MyApp.dumpSharedPreferences();

        /////////////
        boolean isChild = MyApp.getSharedPreferences().getBoolean("IS-CHILD",false);
        boolean isChildConfigured = MyApp.getSharedPreferences().getBoolean("IS-CHILD-CONFIGURED",false);
        if (isChild && isChildConfigured) {
            System.out.println(" ProperlySettedActivity.onStart: starting ChildMonitoringService");
            Intent deviceMonitoringServiceIntent = new Intent(MyApp.getInstance().getApplicationContext(), ChildMonitoringService.class);
            MyApp.getInstance().getApplicationContext().startService(deviceMonitoringServiceIntent);
        }
        /////////////////
    }


    private void viewBinding() {


        Button closeButton = (Button)findViewById(R.id.btn_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            System.out.println("close button  clicked");
            closeActivity();
        }
    });
        ImageView properlySettedImage = (ImageView)findViewById(R.id.img_properly_setted);
        properlySettedImage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                System.out.println("close button  clicked");
                closeActivity();
            }
        });

        TextView nowConfigureParentTextView = (TextView)findViewById(R.id.tv_now_configure_parent);
        boolean isChildConfigured = MyApp.getSharedPreferences().getBoolean("IS-CHILD-CONFIGURED",false);
        if(isChildConfigured) {
            nowConfigureParentTextView.setText(R.string.str_release);
            /*MyApp.getSharedPreferences().edit()
                    .remove("CHILD-CONFIGURED")
                    .commit();
            MyApp.getSharedPreferences().edit()
                    .putBoolean("CHILD-CONFIGURED",false)
                    .commit();
            System.out.println("DEBUG RESET parentConfigured value= " + parentConfigured);*/
        }
    }
    @Override
    public void onBackPressed() {
       closeActivity();
    }

    private void closeActivity() {
        MyApp.getSharedPreferences().edit()
                .putBoolean("IS-CHILD",true)
                .putBoolean("IS-CHILD-CONFIGURED",true)// TODO: 22/11/16 rimettere true
                .apply();
        System.out.println("ProperlySettedActivity.closeActivity");
        MyApp.dumpSharedPreferences();
        moveTaskToBack(true); //torna alla home
        finish();
    }

    // TODO: 21/11/16 si potrebbe aggiungere un beat per la diagnostica
   /* private class AsyncSendToServer extends AsyncTask<String, String, String> {
        String dataToSend;

        public AsyncSendToServer(String dataToSend) {
            this.dataToSend = dataToSend;
        }

        @Override
        protected String doInBackground(String... params) {

                MyServerResponse myServerBeatResponse = ServerApiUtils.getBeatFromServer("");
                myServerBeatResponse.dump();

                if (myServerBeatResponse.getResponseCode() > 199 && myServerBeatResponse.getResponseCode() < 300) {

                    return null;
                }
            }

        }*/

}




