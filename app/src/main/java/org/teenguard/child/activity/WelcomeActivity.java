package org.teenguard.child.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.teenguard.child.R;
import org.teenguard.child.utils.MyApp;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("WelcomeActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        boolean parentConfigured = MyApp.getSharedPreferences().getBoolean("PARENT-CONFIGURED",false);
        System.out.println("WelcomeActivity parentConfigured = " + parentConfigured);
        if(parentConfigured) {//device already configured,skip all activities
            gotoLastActivity();
        }
        getSupportActionBar().hide(); //nasconde la barra
        viewBinding();

    }


    private void viewBinding() {

        //listener del button
        Button acceptButton = (Button) findViewById(R.id.button_accept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("acceptButton  clicked");
                gotoNextActivity();
            }
        });

        //listener dell'immagine
        ImageView imageView = (ImageView)findViewById(R.id.ivPhoneImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("ivPhoneImage  clicked");
                gotoNextActivity();
            }
        });
    }


    private void gotoLastActivity() {
        System.out.println("skipping to last activity");
        Intent intent = new Intent(MyApp.getContext(), ProperlySettedActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void gotoNextActivity() {
        Intent intent = new Intent(MyApp.getContext(), RoleChooseActivity.class);
        startActivity(intent);
        this.finish();//close current activity
    }


}
