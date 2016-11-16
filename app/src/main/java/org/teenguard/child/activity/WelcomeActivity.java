package org.teenguard.child.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.teenguard.child.R;
import org.teenguard.child.utils.MyApp;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().hide(); //nasconde la barra
        //listener del button
        Button button = (Button) findViewById(R.id.button_accept);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("button accept clicked");
                Intent intent = new Intent(MyApp.getContext(), RoleChooseActivity.class);
                startActivity(intent);
            }
        });
    }




}
