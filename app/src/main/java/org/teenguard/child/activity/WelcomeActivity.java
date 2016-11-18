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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().hide(); //nasconde la barra
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


    private void gotoNextActivity() {
        Intent intent = new Intent(MyApp.getContext(), RoleChooseActivity.class);
        startActivity(intent);
    }


}
