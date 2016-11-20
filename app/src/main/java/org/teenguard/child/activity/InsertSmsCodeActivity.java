package org.teenguard.child.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.teenguard.child.R;
import org.teenguard.child.datatype.MyServerResponse;
import org.teenguard.child.utils.JSon;
import org.teenguard.child.utils.MyApp;
import org.teenguard.child.utils.ServerApiUtils;

public class InsertSmsCodeActivity extends AppCompatActivity {
    String countryCode;
    String phoneNumber;
    TextView tvPhoneNumber;
    EditText editSmsCode; //invisible, will read the code
    TextView tvSmsCode; //whill show inserted code with placeholders
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
                //System.out.println("afterTextChanged");
                int placeHolderNumber = 6 - editSmsCode.getText().length();
                String placeHolders = getPlaceholders(placeHolderNumber);
                tvSmsCode.setText(addSpaces(editSmsCode.getText().toString()) + placeHolders);

                System.out.println(editSmsCode.getText().length());
                if(editSmsCode.getText().length() == 6) {
                    System.out.println("<SEND CODE TO SERVER>");
                    JSon json = new JSon();
                    json.add("phone_number",countryCode + phoneNumber);
                    json.add("code",editSmsCode.getText().toString());
                    System.out.println("json.getJSonString() = " + json.getJSonString());
                    AsyncSendToServer asyncSendToServer = new AsyncSendToServer(json.getJSonString());
                    asyncSendToServer.execute();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
                //System.out.println("beforeTextChanged");
            }

            public void onTextChanged(CharSequence s, int start,int before, int count) {
                //System.out.println("onTextChanged");
            }


        });

    }


    //////////////////////////////////
    private class AsyncSendToServer extends AsyncTask<String, String, String> {
        String dataToSend;

        public AsyncSendToServer(String dataToSend) {
            this.dataToSend = dataToSend;
        }
        @Override
        protected String doInBackground(String... params) {
            MyServerResponse myServerResponse = ServerApiUtils.registerChildToServer(dataToSend);
            myServerResponse.dump();

            if(myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                //Toast.makeText(MyApp.getContext(),"going to sms", Toast.LENGTH_LONG).show();
                //read header
                String xSessid = (String)myServerResponse.getHeaderEntryHM().get("X-SESSID");
                System.out.println("myServerResponse xSessid sync = " + xSessid);
                MyApp.getPreferences().edit()
                        .putString("X-SESSID",xSessid)
                        .apply();
                String xSessidShared = MyApp.getPreferences().getString("X-SESSID","");
                System.out.println("xSessidShared = " + xSessidShared);
                //System.out.println("xSessid in async = " + xSessid);
                gotoNextActivity();
                return null;
            }

            if(myServerResponse.getResponseCode() == 429) {
                asyncToast(getString(R.string.too_many_request));
                //Toast.makeText(MyApp.getContext(),getString(R.string.too_many_request), Toast.LENGTH_LONG).show();
                //tvIsValidPhone.setText(getString(R.string.too_many_request));
                gotoHomeActivity();
                return null;
            }

            if(myServerResponse.getResponseCode() == 401) {
                asyncToast(getString(R.string.wrong_sms_code));
                //Toast.makeText(MyApp.getContext(),getString(R.string.too_many_request), Toast.LENGTH_LONG).show();
                //tvIsValidPhone.setText(getString(R.string.too_many_request));
                reloadCurrentActivity();

                return null;
            }

            asyncToast(getString(R.string.error_occurred));
            gotoHomeActivity();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("completed async execution");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

    private void asyncToast(final String message) {
        //Let this be the code in your n'th level thread from main UI thread
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            public void run() {
                Toast.makeText(MyApp.getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void gotoNextActivity() {
        Intent intent = new Intent(MyApp.getContext(), ProperlyConfiguredActivity.class);
        intent.putExtra("countryCode",countryCode); //quello con il +
        intent.putExtra("phoneNumber",phoneNumber);
        startActivity(intent);
    }

    private void gotoHomeActivity() {
        Intent intent = new Intent(MyApp.getContext(), WelcomeActivity.class);
        startActivity(intent);
    }

    private void reloadCurrentActivity() {
        System.out.println("reload current activity");
        Intent intent = new Intent(MyApp.getContext(), InsertSmsCodeActivity.class);
        intent.putExtra("countryCode",countryCode); //quello con il +
        intent.putExtra("phoneNumber",phoneNumber);
        startActivity(intent);
    }


    //////////////// CODE INSERTING //////////////
    private String getPlaceholders(int num) {
        StringBuilder placeholderSB = new StringBuilder();
        for (int i = 0; i < num; i++) {
            placeholderSB.append("_ ");
        }
        //System.out.println("placeholderSB.toString() = " + placeholderSB.toString());
        return placeholderSB.toString();
    }

    private String addSpaces(String input) {
        StringBuilder spacedInputSB = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            spacedInputSB.append(input.charAt(i) + " ");
        }
        //System.out.println("spacedInputSB= <" + spacedInputSB + ">");
        return spacedInputSB.toString();
    }
}
