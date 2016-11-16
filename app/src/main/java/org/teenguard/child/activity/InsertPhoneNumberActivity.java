package org.teenguard.child.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.teenguard.child.R;

public class InsertPhoneNumberActivity extends AppCompatActivity {
//libphone number
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_phone_number);
        //set title
        setTitle(R.string.title_phone_number);
        //enable back button
        //getActionBar().setHomeButtonEnabled(true);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //
    }
}
