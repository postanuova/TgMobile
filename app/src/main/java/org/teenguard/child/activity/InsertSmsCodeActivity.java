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
    TextView tvSmsCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_sms_code);
        //binding
        tvPhoneNumber = (TextView)findViewById(R.id.tvPhoneNumber);
        editSmsCode = (EditText) findViewById(R.id.editSmsCode);
        tvSmsCode = (TextView) findViewById(R.id.tvSmsCode);
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

            public void afterTextChanged(Editable s) {
                System.out.println("afterTextChanged");
                int placeHolderNumber = 6 - editSmsCode.getText().length();
                String placeHolders = getPlaceholders(placeHolderNumber);
                tvSmsCode.setText(addSpaces(editSmsCode.getText().toString()) + placeHolders);

            }

            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
                System.out.println("beforeTextChanged");

            }

            public void onTextChanged(CharSequence s, int start,int before, int count) {
                System.out.println("onTextChanged");

            }
        });

    }

    private String getPlaceholders(int num) {
        StringBuilder placeholderSB = new StringBuilder();
        for (int i = 0; i < num; i++) {
            placeholderSB.append("_ ");
        }
        System.out.println("placeholderSB.toString() = " + placeholderSB.toString());
        return placeholderSB.toString();
    }

    private String addSpaces(String input) {
        StringBuilder spacedInputSB = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            spacedInputSB.append(input.charAt(i) + " ");
        }
        System.out.println("spacedInputSB= <" + spacedInputSB + ">");
        return spacedInputSB.toString();
    }
}
