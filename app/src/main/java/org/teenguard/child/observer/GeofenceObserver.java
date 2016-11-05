package org.teenguard.child.observer;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.teenguard.child.service.GeofenceTransitionsIntentService;
import org.teenguard.child.utils.MyApp;
import org.teenguard.child.utils.TypeConverter;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by chris on 04/11/16.
 * http://io2015codelabs.appspot.com/codelabs/geofences#3
 */

public class GeofenceObserver implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener,ResultCallback<Status> {
    private GoogleApiClient googleApiClient;
    protected ArrayList<Geofence> geofenceAL = new ArrayList<Geofence>();
    private LocationRequest mLocationRequest;
    //{ "data": { "geofences": [ { "id": "Lincontro", "latitude": 28.120483, "longitude": -16.7775494, "radius": 100, "enter": true, "leave": true }, { "id": "Ale", "latitude": 28.1250742, "longitude": -16.7779788, "radius": 100, "enter": true, "leave": true }, { "id": "SiamMall", "latitude": 28.0690565, "longitude": -16.7249978, "radius": 100, "enter": true, "leave": true }, { "id": "Michele", "latitude": 28.1251502, "longitude": -16.7394207, "radius": 100, "enter": true, "leave": true }, { "id": "ChiesaLosCristianos", "latitude": 28.0521532, "longitude": -16.7177612, "radius": 100, "enter": true, "leave": true } ] }, "t": 3600, "h": "6f4ef2a89f7a834a65c1d6bc4147a4a792504848" }


    public GeofenceObserver() {
        System.out.println("<GeofenceObserver started>");
        googleApiClient = new GoogleApiClient.Builder(MyApp.getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        populateGeofenceAL();
    }

    private  void populateGeofenceAL() {
       Geofence geofence = new Geofence.Builder()
               .setRequestId(" Lincontro")
               .setCircularRegion(28.1205434,-16.7750331,500)
               .setExpirationDuration(12 * 60 * 60 * 1000)
               .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT)
               .build();
        geofenceAL.add(geofence);

       }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("GEOFENCE observer connected");
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000) // 1 second, in milliseconds
        .setSmallestDisplacement(5);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(MyApp.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MyApp.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(this.getClass().getName(),"need to request the missing permissions: not yet implemented");
            // TODO: we must require permission https://developer.android.com/training/permissions/requesting.html
            return;
        } else { //ha tutti i diritti
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()).setResultCallback(this);//chiama onResult
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        Log.i(this.getClass().getName(),">>>completed onConnected");
    }

        public void onResult(Status status) {
            if (status.isSuccess()) {
                System.out.println("geofences added");
            } else {
                System.out.println("geofences adding error");
            }
        }

    private GeofencingRequest getGeofencingRequest() {
        System.out.println("getGeofencingRequest");
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceAL);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        System.out.println("getGeofencePendingIntent");
        Intent intent = new Intent(MyApp.getContext(),GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getService(MyApp.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("location CHANGED = " + new Date(location.getTime()));
        System.out.println("location.getLatitude() = " + location.getLatitude());
        System.out.println("location.getLongitude() = " + location.getLongitude());
        System.out.println("distance from L'incontro = " +
                TypeConverter.coordinatesToDistance(location.getLatitude(),location.getLongitude(),28.1205434,-16.7750331,'m'));
    }

    //// TODO: 05/11/16 json parsing 
    // TODO: 05/11/16 db structure 
    
    // TODO: 05/11/16 flush 
}

