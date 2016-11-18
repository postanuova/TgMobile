package org.teenguard.child.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.teenguard.child.R;

public class InsertSmsCodeActivity extends AppCompatActivity {
String countryCode;
String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_sms_code);
        //getting extras from phone number activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             countryCode = extras.getString("countryCode");
             phoneNumber = extras.getString("phoneNumber");
            System.out.println("retrieved <countryCode phoneNumber> = <" + countryCode +  " " + phoneNumber + ">");
            //The key argument here must match that used in the other activity
        }
    }
}
