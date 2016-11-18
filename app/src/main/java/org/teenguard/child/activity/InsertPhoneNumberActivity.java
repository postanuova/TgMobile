package org.teenguard.child.activity;

import android.os.Bundle;
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
        //set title
        setTitle(R.string.phone_number_title);
        //enable back button
        //getActionBar().setHomeButtonEnabled(true);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //binding
        tvIsValidPhone = (TextView) findViewById(R.id.tvIsValidPhone);
        tvCountryCode = (TextView) findViewById(R.id.tvSelectCountryCode);
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
                        tvCountryCode.setText(name + " (" + dialCode + ")");
                        countryCodeSTR = dialCode.replace("+","");
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
                tvIsValidPhone.setText("");
                phoneNumberSTR = edtPhoneNumber.getText().toString().trim();
                if (countryCodeSTR != null && countryCodeSTR.length() > 0 && phoneNumberSTR != null && phoneNumberSTR.length() > 0 && phoneNumberSTR.length() <15) {
                    countryCodeSTR = countryCodeSTR.trim();
                    if (isValidPhoneNumber(phoneNumberSTR)) {
                        boolean status = validateUsing_libphonenumber(countryCodeSTR, phoneNumberSTR);
                        /*if (status) {
                            tvIsValidPhone.setText(getString(R.string.phone_number_valid));
                        } else {
                            tvIsValidPhone.setText(getString(R.string.phone_number_invalid));
                        }*/
                    } else {
                        tvIsValidPhone.setText(getString(R.string.phone_number_invalid));
                    }
                } else {
                    //Toast.makeText(getApplicationContext(), getString(R.string.phone_number_required), Toast.LENGTH_SHORT).show();
                    tvIsValidPhone.setText(getString(R.string.phone_number_required));
                }
            }
        });


    }

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
        if (isValid) {
            String internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            Toast.makeText(this, getString(R.string.phone_number_valid) + internationalFormat, Toast.LENGTH_LONG).show();
            tvIsValidPhone.setText(getString(R.string.phone_number_valid));
            return true;
        } else {
            Toast.makeText(this, getString(R.string.phone_number_invalid) + phoneNumber, Toast.LENGTH_LONG).show();
            tvIsValidPhone.setText(getString(R.string.phone_number_invalid));
            return false;
        }
    }
}