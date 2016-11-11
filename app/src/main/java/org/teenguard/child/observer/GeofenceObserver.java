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

import org.teenguard.child.dbdao.DbGeofenceDAO;
import org.teenguard.child.dbdatatype.DbGeofence;
import org.teenguard.child.service.GeofenceTransitionsIntentService;
import org.teenguard.child.utils.MyApp;
import org.teenguard.child.utils.TypeConverter;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by chris on 04/11/16.
 * http://io2015codelabs.appspot.com/codelabs/geofences#3
 * https://code.tutsplus.com/tutorials/how-to-work-with-geofences-on-android--cms-26639
 */

public class GeofenceObserver implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener,ResultCallback<Status> {
    private DbGeofenceDAO dbGeofenceDAO = new DbGeofenceDAO();
    private GoogleApiClient googleApiClient;
    protected ArrayList<Geofence> geofenceAL = new ArrayList<Geofence>(); //will contain (Android) Geofences
    private LocationRequest mLocationRequest;
    //{ "data": { "geofences": [ { "id": "Lincontro", "latitude": 28.120483, "longitude": -16.7775494, "radius": 100, "enter": true, "leave": true }, { "id": "Ale", "latitude": 28.1250742, "longitude": -16.7779788, "radius": 100, "enter": true, "leave": true }, { "id": "SiamMall", "latitude": 28.0690565, "longitude": -16.7249978, "radius": 100, "enter": true, "leave": true }, { "id": "Michele", "latitude": 28.1251502, "longitude": -16.7394207, "radius": 100, "enter": true, "leave": true }, { "id": "ChiesaLosCristianos", "latitude": 28.0521532, "longitude": -16.7177612, "radius": 100, "enter": true, "leave": true } ] }, "t": 3600, "h": "6f4ef2a89f7a834a65c1d6bc4147a4a792504848" }


    public GeofenceObserver() {
        System.out.println("<GeofenceObserver  started>");
        googleApiClient = new GoogleApiClient.Builder(MyApp.getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

        boolean newGeofencesFromServer = true;
        if(newGeofencesFromServer) {
            dbGeofenceDAO.delete();
            writeNewGeofencesOnDb(null);
        }
       //TODO: 10/11/16 aggiunta di nuove,overwrite di quelle che già esistono...e cancellazione di quelle che non ci sono più???
        https://www.raywenderlich.com/103540/geofences-googleapiclient
        //http://stackoverflow.com/questions/16631962/android-how-to-retrieve-list-of-registered-geofences
        populateGeofenceAL();
    }


    /**
     * this method will parse json from server and will insert geofences into db
     * @param jsonStringContainingGeofences
     */
    private void writeNewGeofencesOnDb(String jsonStringContainingGeofences) {
        //{ "id": "SiamMall", "latitude": 28.0690565, "longitude": -16.7249978, "radius": 100, "enter": true, "leave": true }, { "id": "Michele", "latitude": 28.1251502, "longitude": -16.7394207, "radius": 100, "enter": true, "leave": true }, { "id": "ChiesaLosCristianos", "latitude": 28.0521532, "longitude": -16.7177612, "radius": 100, "enter": true, "leave": true } ] }, "t": 3600, "h": "6f4ef2a89f7a834a65c1d6bc4147a4a792504848" }
        DbGeofence dbGeofence;

        dbGeofence = new DbGeofence(0,"Lincontro",28.120483,-16.7775494,500,1,1);
        dbGeofence.writeMe();
        dbGeofence = new DbGeofence(0,"SiamMall",28.0690565,-16.7249978,500,true,true);
        dbGeofence.writeMe();

        dbGeofence = new DbGeofence(0,"Michele",28.1251502,-16.7394207,500,true, true);
        dbGeofence.writeMe();

        dbGeofence = new DbGeofence(0,"Chris",28.0589617,  -16.7299850,500,true,true);
        dbGeofence.writeMe();
    }

    /**
     * load geofences from db
     */
    private void populateGeofenceAL() {
        System.out.println("populate geofenceAL");
        DbGeofenceDAO dbGeofenceDAO = new DbGeofenceDAO();
        ArrayList <DbGeofence> dbGeofenceAL = dbGeofenceDAO.getList();  //will contain dbGeofences
        Geofence geofence;
        for (DbGeofence dbGeofence: dbGeofenceAL) {
            geofence = new Geofence.Builder()
                    .setRequestId(dbGeofence.getGeofenceId())
                    .setCircularRegion(dbGeofence.getLatitude(),dbGeofence.getLongitude(),dbGeofence.getRadius())
                    .setExpirationDuration(10*1000*1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
             geofenceAL.add(geofence);
        }
        System.out.println("populateGeofenceAL geofenceAL.size() = " + geofenceAL.size());
    }

   /* private  void populateGeofenceAL() {
       Geofence geofence = new Geofence.Builder()
               .setRequestId("Lincontro")
               //.setCircularRegion(28.1205434,-16.7750331,500)
               .setCircularRegion(28.12,-16.778,100)
               .setExpirationDuration(Geofence.NEVER_EXPIRE)
               .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT)
               .build();
        geofenceAL.add(geofence);

       }*/

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
        } else {
            //ha tutti i diritti
            System.out.println("all rights enabled: initializing geofences monitoring");
            //costruisco l'oggetto geofencingRequest che conterrà la lista delle geofences
            GeofencingRequest.Builder geofencingRequestBuilder = new GeofencingRequest.Builder();
            geofencingRequestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
            geofencingRequestBuilder.addGeofences(geofenceAL);
            GeofencingRequest geofencingRequest = geofencingRequestBuilder.build();
            System.out.println(" geofencingRequest.getGeofences().size() = " + geofencingRequest.getGeofences().size());
            //costruisco il pending Intent
            Intent intent = new Intent(MyApp.getContext(),GeofenceTransitionsIntentService.class);
            PendingIntent pendingIntent = PendingIntent.getService(MyApp.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);// We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()

            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    geofencingRequest,
                    pendingIntent).setResultCallback(this);//chiama onResult
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        Log.i(this.getClass().getName(),">>>completed onConnected");
    }


    /**
     * invocato dall'aggiunta di geofences del metodo onConnected
     * @param status
     */
        public void onResult(Status status) {
            if (status.isSuccess()) {
                System.out.println(" geofences succesfully added ");
            } else {
                System.out.println("geofences adding error: status code " + status.getStatusCode() + " status message " + status.getStatusMessage());
                System.out.println("hint: enable GPS and Enable Google position access into Settings/location service");
            }
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
        System.out.println("location.getAccuracy() = " + location.getAccuracy());

        System.out.println("distance from L'incontro = " + TypeConverter.coordinatesToDistance(location.getLatitude(),location.getLongitude(),28.1205434,-16.7750331,'m'));
        System.out.println("distance from Michele = " + TypeConverter.coordinatesToDistance(location.getLatitude(),location.getLongitude(),28.1251502,-16.7394207,'m'));
        System.out.println("distance from Chris =  " + TypeConverter.coordinatesToDistance(location.getLatitude(),location.getLongitude(),28.0589617,-16.7299850,'m'));
    }



    // TODO: 05/11/16 json parsing of incoming geofences
    // TODO: 05/11/16 flush 
}

