package org.teenguard.child.activity;


import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.teenguard.child.R;

import java.util.Date;

//
public class LocalizationActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, ConnectionCallbacks, LocationListener {

    private static final int REQUEST_RESOLVE_ERROR = 0;
    private static final String DIALOG_ERROR = null;
    private GoogleApiClient googleApiClient;
    private boolean resolvingError;
    private LocationRequest mLocationRequest;

    protected void onCreate(Bundle savedInstanceState) {
       Log.i(this.getClass().getName(),"<<<started onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localization);

       Log.i(this.getClass().getName(),"build GoogleApiClient");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
       Log.i(this.getClass().getName(),">>>completed onCreate");
    }

    @Override
    protected void onStart() {
       Log.i(this.getClass().getName(),"<<<started onStart");
        super.onStart();
        if (!resolvingError) {
           Log.i(this.getClass().getName(),"connecting to GoogleApiClient");
            googleApiClient.connect();
        }
       Log.i(this.getClass().getName(),">>>completed onStart");
    }

    @Override
    protected void onStop() {
       Log.i(this.getClass().getName(),"<<<started onStop");
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
           Log.i(this.getClass().getName(),"disconnecting from GoogleApiClient");
            googleApiClient.disconnect();
        }
        super.onStop();
       Log.i(this.getClass().getName(),">>>completed onStop");
    }

    @Override
    public void onConnected(Bundle arg0) {
       Log.i(this.getClass().getName(),"<<<started onConnected");
       Log.i(this.getClass().getName(),"creating LocationRequest");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setSmallestDisplacement(5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           Log.i(this.getClass().getName(),"need to request the missing permissions: not yet implemented");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
       Log.i(this.getClass().getName(),"executing LocationServices.FusedLocationApi.requestLocationUpdates");
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
       Log.i(this.getClass().getName(),">>>completed onConnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
       Log.i(this.getClass().getName(),"<<<started onConnectionFailed");
        if (resolvingError) {
           Log.i(this.getClass().getName(),"resolving error=true");
            return;
        }
        if (result.hasResolution()) {
           Log.i(this.getClass().getName(),"result.hasResolution=true");
            try {
                resolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            }
            catch (IntentSender.SendIntentException e){
               Log.i(this.getClass().getName(),"SendIntentException: invoking googleApiClient.connect()");
                googleApiClient.connect();
            }
        } else {
           Log.i(this.getClass().getName(),"result.hasResolution=false");
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),this, REQUEST_RESOLVE_ERROR).show();
           Log.i(this.getClass().getName(),"setting resolvingError = true");
            resolvingError = true;
        }
       Log.i(this.getClass().getName(),">>>completed onConnectionFailed");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       Log.i(this.getClass().getName(),"<<<started onActivityResult ");
        if (requestCode == REQUEST_RESOLVE_ERROR) {
           Log.i(this.getClass().getName(),"requestCode == REQUEST_RESOLVE_ERROR");
            resolvingError = false;
            if (resultCode == RESULT_OK) {
               Log.i(this.getClass().getName(),"resultCode == RESULT_OK");
                if (!googleApiClient.isConnecting() && !googleApiClient.isConnected()) {
                   Log.i(this.getClass().getName(),"googleApiClient not connecting and not connectred: invoking googleApiClient.connect()");
                    googleApiClient.connect();
                }
            }
        }
       Log.i(this.getClass().getName(),">>>completed onActivityResult");
    }

    @Override
    public void onConnectionSuspended(int arg0) {
       Log.i(this.getClass().getName(),"<<<started onConnectionSuspended");
    /*
       Connessione sospesa
     * */
       Log.i(this.getClass().getName(),">>>completed onConnectionSuspended");
    }

    @Override
    public void onLocationChanged(final Location location){
       Log.i(this.getClass().getName(),"<<<started onLocationChanged");
       Log.i(this.getClass().getName(),"updating text");
        Date timestamp = new Date(location.getTime());
        updateText(R.id.timestamp, timestamp.toString());

        double latitude = location.getLatitude();
        updateText(R.id.latitude, String.valueOf(latitude));

        double longitude = location.getLongitude();
        updateText(R.id.longitude, String.valueOf(longitude));
       /* new AsyncTask<void,void,string>()
        {
   /*         @Override
            protected String doInBackground(Void... voids)
            {
                Geocoder coder=new Geocoder(MainActivity.this);
                try {
                    List <address>l=coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (l.size()>0)
                        return l.get(0);
                } catch (IOException e) {
                    return null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result!=null)
                    updateText(R.id.where, result);
                else
                    updateText(R.id.where, "N.A.");

            }
        }.execute();*/
       Log.i(this.getClass().getName(),">>>completed onConnectionSuspended");
    }

    private void updateText(int id, String text){
        TextView textView = (TextView) findViewById(id);
        textView.setText(text);
    }

}
