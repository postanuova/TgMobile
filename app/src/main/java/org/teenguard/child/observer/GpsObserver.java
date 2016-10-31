package org.teenguard.child.observer;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.teenguard.child.dbdao.DbLocationDAO;
import org.teenguard.child.dbdatatype.DbLocation;
import org.teenguard.child.utils.MyApp;
import org.teenguard.child.utils.TypeConverter;

/**
 * Created by chris on 30/10/16.
 */

public class GpsObserver implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    public static int LOCATION_DISTANCE_METERS_THRESHOLD = 1;
    public static long LOCATION_TIME_MILLISECONDS_THRESHOLD = 500;
    public static int VISIT_DISTANCE_METERS_THRESHOLD = 10;
    public static int VISIT_TIME_MILLISECONDS_THRESHOLD = 10000;
    // TODO: 31/10/16 settare valori definitivi 
    // public static int DISTANCE_METERS_TRIGGER = 1000; definitivi
    //   public static long TIME_MILLISECONDS_TRIGGER = 300000;
    //visits: meno di 300mt di spostamento nei 5 minuti

    private GoogleApiClient googleApiClient;
    private Location mCurrentLocation;
    private DbLocation previousDbLocation;
    private long mLastUpdateTime;
    private LocationRequest mLocationRequest;


    public GpsObserver() {
        googleApiClient = new GoogleApiClient.Builder(MyApp.getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Log.i(this.getClass().getName(),"connecting to GoogleApiClient");
        googleApiClient.connect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("GpsObserver.onConnected()");
        Log.i(this.getClass().getName(),"<<<started onConnected");
        Log.i(this.getClass().getName(),"creating LocationRequest");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setFastestInterval(LOCATION_TIME_MILLISECONDS_THRESHOLD);
        mLocationRequest.setInterval(LOCATION_TIME_MILLISECONDS_THRESHOLD); //aggiorna posizione ogni x secondi
        mLocationRequest.setSmallestDisplacement(LOCATION_DISTANCE_METERS_THRESHOLD); //aggiorna posizione ogni x metri
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(MyApp.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MyApp.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(this.getClass().getName(),"need to request the missing permissions: not yet implemented");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else { //ha tutti i diritti
            previousDbLocation = new DbLocation(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));
            System.out.println("last location");
            previousDbLocation.dump();
        }
        Log.i(this.getClass().getName(),"executing LocationServices.FusedLocationApi.requestLocationUpdates");
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);

        Log.i(this.getClass().getName(),">>>completed onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("GpsObserver.onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("GpsObserver.onConnectionFailed()");

    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("GpsObserver.onLocationChanged()");
        mCurrentLocation = location;
        mLastUpdateTime = location.getTime();
        // TODO: 30/10/16 save location to database
        DbLocation dbLocation = new DbLocation(location);
        dbLocation.dump();
        DbLocationDAO dbLocationDAO = new DbLocationDAO();
        dbLocationDAO.upsert(dbLocation);
        //// TODO: 31/10/16 send to server
        /*if (previousDbLocation == null) {
            previousDbLocation = LocationServices.FusedLocationApi.getLastLocation();
        }*/
        double distanceBetweenLocation = TypeConverter.coordinatesToDistance(dbLocation.getLatitude(),dbLocation.getLongitude(),previousDbLocation.getLatitude(),previousDbLocation.getLongitude(),'m');
        System.out.println("distance from previous (m) = " + TypeConverter.doubleTrunkTwoDigit(distanceBetweenLocation));
        long secondsBetweenLocation = (dbLocation.getDate() - previousDbLocation.getDate())/1000;
        System.out.println("seconds from previous location = " + secondsBetweenLocation);
        previousDbLocation = dbLocation;
        if((distanceBetweenLocation < VISIT_DISTANCE_METERS_THRESHOLD) && (secondsBetweenLocation * 1000 > VISIT_TIME_MILLISECONDS_THRESHOLD)) {
            System.out.println("its a visit");
        }
    }

    
}
