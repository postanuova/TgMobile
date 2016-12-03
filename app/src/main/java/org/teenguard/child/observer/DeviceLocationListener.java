package org.teenguard.child.observer;

/**
 * Created by chris on 03/11/16.
 */

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.teenguard.child.datatype.MyServerResponse;
import org.teenguard.child.dbdao.DbLocationEventDAO;
import org.teenguard.child.dbdatatype.DbLocationEvent;
import org.teenguard.child.utils.CalendarUtils;
import org.teenguard.child.utils.MyApp;
import org.teenguard.child.utils.MyLog;
import org.teenguard.child.utils.ServerApiUtils;
import org.teenguard.child.utils.TypeConverter;

import java.util.ArrayList;

/**
 * Created by chris on 30/10/16.
 */

public class DeviceLocationListener implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

    public static int LOCATION_DISTANCE_METERS_THRESHOLD = 10;
    public static long LOCATION_TIME_MILLISECONDS_THRESHOLD = 100;
    // TODO: 31/10/16 settare valori definitivi
    // public static int DISTANCE_METERS_TRIGGER = 1000; definitivi
    //   public static long TIME_MILLISECONDS_TRIGGER = 300000;


    protected DbLocationEvent previousDbLocation;
    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;

    public DeviceLocationListener() {
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
        Log.i(this.getClass().getName(),"setting LocationRequest  parameters");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setFastestInterval(LOCATION_TIME_MILLISECONDS_THRESHOLD);
        mLocationRequest.setInterval(LOCATION_TIME_MILLISECONDS_THRESHOLD); //aggiorna posizione ogni x secondi
        mLocationRequest.setSmallestDisplacement(LOCATION_DISTANCE_METERS_THRESHOLD); //aggiorna posizione ogni x metri
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(MyApp.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MyApp.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(this.getClass().getName(),"need to request the missing permissions: not yet implemented");
            // TODO: we must require permission https://developer.android.com/training/permissions/requesting.html
            return;
        } else { //ha tutti i diritti
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                previousDbLocation = new DbLocationEvent(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));
                DbLocationEventDAO dbLocationEventDAO = new DbLocationEventDAO();
                long id = dbLocationEventDAO.upsert(previousDbLocation);
                System.out.println("id = " + id);
                previousDbLocation.setId(id);
                System.out.println("-------last known location------");
                previousDbLocation.dump();
                AsyncSendToServer asyncSendToServer = new AsyncSendToServer("[" + previousDbLocation.buildSerializedDataString() + "]", "" + previousDbLocation.getId());
                asyncSendToServer.execute();
            }
        }
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
        DbLocationEvent dbLocationEvent = new DbLocationEvent(location);
        dbLocationEvent.dump();
        DbLocationEventDAO  dbLocationEventDAO = new DbLocationEventDAO();
        long id = dbLocationEventDAO.upsert(dbLocationEvent);
        System.out.println("<<<<<<<<< event id = " + id);
        dbLocationEvent.setId(id);
        AsyncSendToServer asyncSendToServer = new AsyncSendToServer("[" + dbLocationEvent.buildSerializedDataString() + "]","" + dbLocationEvent.getId());
        asyncSendToServer.execute();
        double distanceBetweenLocation = TypeConverter.coordinatesToDistance(dbLocationEvent.getLatitude(),dbLocationEvent.getLongitude(),previousDbLocation.getLatitude(),previousDbLocation.getLongitude(),'m');
        System.out.println(" distance from previous (m) = " + TypeConverter.doubleTrunkTwoDigit(distanceBetweenLocation));
        long secondsBetweenLocation = (dbLocationEvent.getDate() - previousDbLocation.getDate())/1000;
        System.out.println(" seconds from previous location = " + secondsBetweenLocation);
        previousDbLocation = dbLocationEvent;
    }


    // TODO: 02/12/16 make static 
    public static void flushLocationTable() {
        // TODO: 02/11/16 to be used and tested and launched in flusher
        DbLocationEventDAO dbLocationEventDAO = new DbLocationEventDAO();
        ArrayList <DbLocationEvent> dbLocationEventAL = dbLocationEventDAO.getList();
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder idToDeleteListSB = new StringBuilder(); //la usero' per cancellare gli eventi una volta inviati
        for (DbLocationEvent dbLocationEvent:dbLocationEventAL) {
            stringBuilder.append(dbLocationEvent.buildSerializedDataString());
            stringBuilder.append(",");
            idToDeleteListSB.append(dbLocationEvent.getId());
        }
        String bulkLocationEventSTR = stringBuilder.toString();
        if(bulkLocationEventSTR.endsWith(",")) bulkLocationEventSTR = bulkLocationEventSTR.substring(0,bulkLocationEventSTR.length()-1);
        String idToDeleteListSTR = stringBuilder.toString();
        if(idToDeleteListSTR.endsWith(",")) idToDeleteListSTR = idToDeleteListSTR.substring(0,idToDeleteListSTR.length()-1);
        /*AsyncSendToServer asyncSendToServer = new AsyncSendToServer("[" + bulkLocationEventSTR + "]",idToDeleteListSTR);
        asyncSendToServer.execute();*/
        ///////not async///////
        if(bulkLocationEventSTR.length() > 0) {
            MyServerResponse myServerResponse = ServerApiUtils.addLocationToServer("[" + bulkLocationEventSTR + "]");
            myServerResponse.dump();
            if (myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                System.out.println("FLUSHED NEW LOCATION TO SERVER, DELETING  " + idToDeleteListSTR);
                dbLocationEventDAO.delete(idToDeleteListSTR);
            }
        } else {
            System.out.println("no LOCATION to flush " + CalendarUtils.currentDatetimeUTC());
        }
        ////////////////////////
    }

    //////////////////////////////////
    private class AsyncSendToServer extends AsyncTask<String, String, String> {
        //http://www.journaldev.com/9708/android-asynctask-example-tutorial
        String dataToSend;
        String idToDeleteListSTR;

        public AsyncSendToServer(String dataToSend, String idToDeleteListSTR) {
            this.dataToSend = dataToSend;
            this.idToDeleteListSTR = idToDeleteListSTR;
        }
        @Override
        protected String doInBackground(String... params) {
            ///////////////NEEDS TO BE EXECUTED IN BACKGROUND/////////////////////
            MyLog.i(this, "ASYNC SENDING NEW LOCATION TO SERVER");
            MyServerResponse myServerResponse = ServerApiUtils.addLocationToServer(dataToSend);
            myServerResponse.dump();
            if (myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                MyLog.i(this, "SENT NEW LOCATION TO SERVER, DELETING  "  + idToDeleteListSTR);
                DbLocationEventDAO  dbLocationEventDAO = new DbLocationEventDAO();
                dbLocationEventDAO.delete(idToDeleteListSTR);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("completed async execution");
        }
    }
    ///////////////////////////////

}

