package org.teenguard.child.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;

import org.teenguard.child.R;
import org.teenguard.child.datatype.MyServerResponse;
import org.teenguard.child.utils.JSon;
import org.teenguard.child.utils.MyApp;
import org.teenguard.child.utils.ServerApiUtils;

public class InsertPhoneNumberActivity extends AppCompatActivity {
    //libphone number
    TextView tvIsValidPhone;
    EditText edtPhoneNumber;
    TextView tvCountryCode;
    TextView tvSelectCountryCode;
    String phoneNumberSTR;
    String countryCodeSTR;
    Button btnValidate;
    CountryPicker picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_phone_number);
   /*     boolean parentConfigured = MyApp.getSharedPreferences().getBoolean("CHILD-CONFIGURED",false);
        System.out.println("InsertPhoneNumberActivity parentConfigured = " + parentConfigured);
        if(parentConfigured) {//device already configured,skip all activities
            gotoLastActivity();
        }*/


        //set title
        setTitle(R.string.phone_number_title);
        //enable back button
        //getActionBar().setHomeButtonEnabled(true);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        viewBinding();

    }

    /*public void onResume() {
        super.onResume();
        System.out.println("onresumeeeeeeee " + true);
        //gotoLastActivity();
        this.finish();
    }*/



    private void viewBinding() {
        //binding
        tvIsValidPhone = (TextView) findViewById(R.id.tvIsValidPhone);
        tvCountryCode = (TextView) findViewById(R.id.tvCountryCode);
        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        btnValidate = (Button) findViewById(R.id.btnValidate);
        tvSelectCountryCode = (TextView) findViewById(R.id.tvSelectCountryCode);

        //country code picker
        tvSelectCountryCode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("tvSelectCountryCode clicked");
                String contryCodeHint = getString(R.string.country_code_hint);
                picker = CountryPicker.newInstance(contryCodeHint);
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        System.out.println("name = " + name);
                        System.out.println("code = " + code);
                        System.out.println("dialCode = " + dialCode);
                        tvSelectCountryCode.setText(name); //show country name
                        tvCountryCode.setText(dialCode);  //show dial code with +
                        countryCodeSTR = dialCode.replace("+",""); //country code without +
                        System.out.println("countryCodeSTR = " + countryCodeSTR);
                        picker.dismiss();
                    }
                });
            }
        });

        //validation button
        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 09/12/16 FORMAT phone number http://stackoverflow.com/questions/21270686/android-how-do-i-format-number-as-phone-with-parentheses
                tvIsValidPhone.setText("");
                phoneNumberSTR = edtPhoneNumber.getText().toString().trim();
                if (countryCodeSTR != null && countryCodeSTR.length() > 0 && phoneNumberSTR != null && phoneNumberSTR.length() > 0 && phoneNumberSTR.length() <15) {
                    countryCodeSTR = countryCodeSTR.trim();
                    if (isValidPhoneNumber(phoneNumberSTR)) {
                        boolean isValidatedFromLibPhoneNumber = validateUsing_libphonenumber(countryCodeSTR, phoneNumberSTR);
                        System.out.println("validateUsing_libphonenumber returned = " + isValidatedFromLibPhoneNumber);
                        if (isValidatedFromLibPhoneNumber) {
                            System.out.println("isValidPhoneNumber");
                            JSon json = new JSon();
                            json.add("phone_number",countryCodeSTR + phoneNumberSTR);
                            System.out.println("json.getJSonString() = " + json.getJSonString());
                            AsyncSendToServer asyncSendToServer = new AsyncSendToServer(json.getJSonString());
                            asyncSendToServer.execute();
                        } else {//not validated from LibPhoneNumber
                            tvIsValidPhone.setText(getString(R.string.phone_number_invalid));
                            System.out.println("cleaning edtPhoneNumber");
                            edtPhoneNumber.setText("");
                        }
                    } else {//country code or phone number not inserted (!isValidPhoneNumber)
                        System.out.println("!isValidPhoneNumber");
                        //Toast.makeText(getApplicationContext(), getString(R.string.phone_number_required), Toast.LENGTH_SHORT).show();
                        tvIsValidPhone.setText(getString(R.string.phone_number_required));
                    }
                }
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
            MyServerResponse myServerResponse = ServerApiUtils.announceChildToServer(dataToSend);
            myServerResponse.dump();
            if(myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                //Toast.makeText(MyApp.getInstance().getApplicationContext(),"going to sms", Toast.LENGTH_LONG).show();
                gotoNextActivity();
                return null;
            }
            if(myServerResponse.getResponseCode() == 429) {
                asyncToast(getString(R.string.too_many_request));
                //Toast.makeText(MyApp.getInstance().getApplicationContext(),getString(R.string.too_many_request), Toast.LENGTH_LONG).show();
                //tvIsValidPhone.setText(getString(R.string.too_many_request));
                gotoHomeActivity();
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
                Toast.makeText(MyApp.getInstance().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void gotoNextActivity() {
        Intent intent = new Intent(MyApp.getInstance().getApplicationContext(), InsertSmsCodeActivity.class);
        intent.putExtra("countryCode",tvCountryCode.getText()); //quello con il +
        intent.putExtra("phoneNumber",phoneNumberSTR);
        startActivity(intent);
        this.finish();
    }

    private void gotoHomeActivity() {
        Intent intent = new Intent(MyApp.getInstance().getApplicationContext(), WelcomeActivity.class);
        startActivity(intent);
    }

    private void gotoLastActivity() {
        System.out.println("skipping to last activity");
        Intent intent = new Intent(MyApp.getInstance().getApplicationContext(), ProperlySettedActivity.class);
        startActivity(intent);
        this.finish();
    }
    ///////////////////////VALIDATION METHOD'S ///////////////////////

    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    private boolean validateUsing_libphonenumber(String countryCode, String phNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode);
        } catch (NumberParseException e) {
            System.err.println(e);
        }

        boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
        System.out.println("isValid = " + isValid);
        if (isValid) {
            String internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            Toast.makeText(this, getString(R.string.phone_number_valid), Toast.LENGTH_LONG).show();
            tvIsValidPhone.setText(getString(R.string.phone_number_valid));
            return true;
        } else {
            Toast.makeText(this, getString(R.string.phone_number_invalid) + phoneNumber, Toast.LENGTH_LONG).show();
            tvIsValidPhone.setText(getString(R.string.phone_number_invalid));
            return false;
        }
    }
}