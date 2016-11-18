package org.teenguard.child.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import org.teenguard.child.R;

public class InsertSmsCodeActivity extends AppCompatActivity {
    String countryCode;
    String phoneNumber;
    TextView tvPhoneNumber;
    EditText editSmsCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_sms_code);
        //binding
        tvPhoneNumber = (TextView)findViewById(R.id.tvPhoneNumber);
        editSmsCode = (EditText) findViewById(R.id.editSmsCode);
        //getting extras from phone number activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            countryCode = extras.getString("countryCode");
            phoneNumber = extras.getString("phoneNumber");
            System.out.println("retrieved <countryCode phoneNumber> = <" + countryCode +  " " + phoneNumber + ">");
            //populating with extra
            tvPhoneNumber.setText(countryCode + " " + phoneNumber);
        }

        //editSmsCode listener
        editSmsCode.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,int before, int count) {
                //Field2.getText().clear();
            }
        });

    }
}
