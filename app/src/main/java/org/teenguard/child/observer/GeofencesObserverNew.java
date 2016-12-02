package org.teenguard.child.observer;


import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.teenguard.child.dbdao.DbGeofenceDAO;
import org.teenguard.child.utils.MyApp;

import java.util.ArrayList;


/**
 * Created by chris on 04/11/16.
 * http://io2015codelabs.appspot.com/codelabs/geofences#3
 * https://code.tutsplus.com/tutorials/how-to-work-with-geofences-on-android--cms-26639
 * dbGeofence = new DbGeofence(0,"Chris",28.0589617,  -16.7299850,500,true,true);
 */

// TODO: 25/11/16 update geofences from server json parsing of incoming geofences

public class GeofencesObserverNew implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener,ResultCallback<Status> {
    int checkInterval; //is the t-parameter of beat response
    String shaFingerPrint = "dummyBeat"; //is the h-parameter of beat response
    private DbGeofenceDAO dbGeofenceDAO = new DbGeofenceDAO();
    private GoogleApiClient googleApiClient;
    protected ArrayList<Geofence> deviceGeofenceAL = new ArrayList<Geofence>(); //will contain (Android) device Geofences
    private LocationRequest mLocationRequest;
    //{ "data": { "geofences": [ { "id": "Lincontro", "latitude": 28.120483, "longitude": -16.7775494, "radius": 100, "enter": true, "leave": true }, { "id": "Ale", "latitude": 28.1250742, "longitude": -16.7779788, "radius": 100, "enter": true, "leave": true }, { "id": "SiamMall", "latitude": 28.0690565, "longitude": -16.7249978, "radius": 100, "enter": true, "leave": true }, { "id": "Michele", "latitude": 28.1251502, "longitude": -16.7394207, "radius": 100, "enter": true, "leave": true }, { "id": "ChiesaLosCristianos", "latitude": 28.0521532, "longitude": -16.7177612, "radius": 100, "enter": true, "leave": true } ] }, "t": 3600, "h": "6f4ef2a89f7a834a65c1d6bc4147a4a792504848" }


    public GeofencesObserverNew() {
        System.out.println("<GeofenceObserverNew started>");
        googleApiClient = new GoogleApiClient.Builder(MyApp.getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        System.out.println("waiting for googleApiClient.connect()");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}