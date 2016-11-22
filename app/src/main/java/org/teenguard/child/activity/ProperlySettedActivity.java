package org.teenguard.child.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.teenguard.child.R;
import org.teenguard.child.utils.MyApp;

public class ProperlySettedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("ProperlySettedActivity started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properly_setted);
        viewBinding();
        getSupportActionBar().hide(); //nasconde la barra
        // TODO: 20/11/16 binding immagine che chiude
        // TODO: 20/11/16 visualizza now configure parent solo se non è stato già fatto (posso salvarlo tra le properties)
        // TODO: 20/11/16 close button
        // TODO: 20/11/16 eliminare barra
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
        boolean parentConfigured = MyApp.getPreferences().getBoolean("PARENT-CONFIGURED",false);
        System.out.println("parentConfigured = " + parentConfigured);
        if(parentConfigured) {
            nowConfigureParentTextView.setText("-");
            /*MyApp.getPreferences().edit()
                    .remove("PARENT-CONFIGURED")
                    .commit();
            MyApp.getPreferences().edit()
                    .putBoolean("PARENT-CONFIGURED",false)
                    .commit();
            System.out.println("DEBUG RESET parentConfigured value= " + parentConfigured);*/
        }
    }

    private void closeActivity() {
        MyApp.getPreferences().edit()
                .putBoolean("PARENT-CONFIGURED",false)// TODO: 22/11/16 rimettere true
                .commit();


        this.finish();
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




