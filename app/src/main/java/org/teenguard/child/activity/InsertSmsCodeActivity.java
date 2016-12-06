package org.teenguard.child.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import org.teenguard.child.R;
import org.teenguard.child.datatype.MyServerResponse;
import org.teenguard.child.utils.CalendarUtils;
import org.teenguard.child.utils.FxUtils;
import org.teenguard.child.utils.JSon;
import org.teenguard.child.utils.MyApp;
import org.teenguard.child.utils.ServerApiUtils;

import static org.teenguard.child.utils.FxUtils.asyncToast;

public class InsertSmsCodeActivity extends AppCompatActivity {
    boolean wrongSmsCode = false;
    String countryCode;
    String phoneNumber;
    TextView tvPhoneNumber;
    EditText editSmsCode; //invisible, will read the code
    TextView tvSmsCode; //whill show inserted code with placeholders
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean parentConfigured = MyApp.getSharedPreferences().getBoolean("CHILD-CONFIGURED",false);
        System.out.println("InsertSmsCodeActivity parentConfigured = " + parentConfigured);
        if(parentConfigured) {//device already configured,skip all activities
            gotoLastActivity();
        }
        setContentView(R.layout.activity_insert_sms_code);
        viewBinding();

    }


  /*  public void onResume() {
        super.onResume();
        System.out.println("onresumeeeeeeee " + true);
        //gotoLastActivity();
        this.finish();
    }*/

    private void viewBinding() {
        //binding
        tvPhoneNumber = (TextView)findViewById(R.id.tvPhoneNumber);
        editSmsCode = (EditText) findViewById(R.id.editSmsCode);
        tvSmsCode = (TextView) findViewById(R.id.tvSmsCode);

        //getting extras from phone number activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            countryCode = extras.getString("countryCode");
            phoneNumber = extras.getString("phoneNumber");
            wrongSmsCode = extras.getBoolean("wrongSmsCode");
            if(wrongSmsCode) {
                FxUtils.vibe();
                FxUtils.shake(tvSmsCode);
            }
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
                    AsyncSendToServer asyncSendToServer = new AsyncSendToServer(json.getJSonString());//register
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
            MyServerResponse myServerRegisterResponse = ServerApiUtils.registerChildToServer(dataToSend);
            myServerRegisterResponse.dump();

            if(myServerRegisterResponse.getResponseCode() > 199 && myServerRegisterResponse.getResponseCode() < 300) {
                //Toast.makeText(MyApp.getContext(),"going to sms", Toast.LENGTH_LONG).show();
                //read header
                //System.out.println("saving X-SESSID");
                String xSessid = (String)myServerRegisterResponse.getHeaderEntryHM().get("X-SESSID");
                MyApp.getSharedPreferences().edit()
                        .putString("X-SESSID",xSessid)
                        .apply();
                String xSessidShared = MyApp.getSharedPreferences().getString("X-SESSID","");
                System.out.println("saved X-SESSID = " + xSessidShared);
                MyApp.dumpSharedPreferences();
                //send post beat
                JSon jSon = new JSon();

                jSon.add("time_zone", CalendarUtils.getDeviceTimezone());
                System.out.println("jSon.toString() " + jSon.getJSonString());
                MyServerResponse myServerBeatResponse = ServerApiUtils.postBeatToServer(jSon.getJSonString());
                myServerBeatResponse.dump();

                if(myServerBeatResponse.getResponseCode() > 199 && myServerBeatResponse.getResponseCode() < 300) {
                    System.out.println("InsertSmsCodeActivity setting IS-CHILD-CONFIGURED= TRUE ");
                    MyApp.getSharedPreferences().edit()
                            .putBoolean("IS-CHILD-CONFIGURED",true)
                            .apply();
                    gotoNextActivity();
                    MyApp.dumpSharedPreferences();
                    return null;
                }
            }

            if(myServerRegisterResponse.getResponseCode() == 429) {
                asyncToast(getString(R.string.too_many_request));
                //Toast.makeText(MyApp.getContext(),getString(R.string.too_many_request), Toast.LENGTH_LONG).show();
                //tvIsValidPhone.setText(getString(R.string.too_many_request));
                gotoHomeActivity();
                return null;
            }

            if(myServerRegisterResponse.getResponseCode() == 401) {
                FxUtils.asyncToast(getString(R.string.wrong_sms_code));
                wrongSmsCode = true; //enable shaking
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



    private void gotoLastActivity() {
        System.out.println("skipping to last activity");
        Intent intent = new Intent(MyApp.getContext(), ProperlySettedActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void gotoNextActivity() {
        Intent intent = new Intent(MyApp.getContext(), ProperlySettedActivity.class);
        System.out.println("closing all activities");
        //closing all previous activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("countryCode",countryCode); //quello con il +
        intent.putExtra("phoneNumber",phoneNumber);
        startActivity(intent);
        this.finish();
    }

    private void gotoHomeActivity() {
        Intent intent = new Intent(MyApp.getContext(), WelcomeActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void reloadCurrentActivity() {
        System.out.println("reload current activity");
        Intent intent = new Intent(MyApp.getContext(), InsertSmsCodeActivity.class);
        intent.putExtra("countryCode",countryCode); //quello con il +
        intent.putExtra("phoneNumber",phoneNumber);
        intent.putExtra("wrongSmsCode",true);
        startActivity(intent);
        this.finish();
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
