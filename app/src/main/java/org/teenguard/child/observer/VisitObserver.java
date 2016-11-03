package org.teenguard.child.observer;

/**
 * Created by chris on 03/11/16.
 */

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.teenguard.child.datatype.MyServerResponse;
import org.teenguard.child.dbdao.DbLocationEventDAO;
import org.teenguard.child.dbdao.DbVisitEventDAO;
import org.teenguard.child.dbdatatype.DbLocationEvent;
import org.teenguard.child.dbdatatype.DbVisitEvent;
import org.teenguard.child.utils.Chronometer;
import org.teenguard.child.utils.MyApp;
import org.teenguard.child.utils.MyLog;
import org.teenguard.child.utils.ServerApiUtils;
import org.teenguard.child.utils.TypeConverter;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by chris on 30/10/16.
 */

public class VisitObserver implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {


    public static int VISIT_DISTANCE_METERS_THRESHOLD = 30;
    public static int VISIT_TIME_MILLISECONDS_THRESHOLD = 1*1000;
    // TODO: 31/10/16 settare valori definitivi
    /*public static int VISIT_DISTANCE_METERS_THRESHOLD = 300;
    public static int VISIT_TIME_MILLISECONDS_THRESHOLD = 5*60*1000;*/
    //visits: meno di 300mt di spostamento nei 5 minuti

    protected Chronometer chronometer;
    public boolean visitInProgress;
    protected Location previousLocation;
    protected Location newLocation;
    private GoogleApiClient googleApiClient;
    CheckChronometerThread checkChronometerThread;
    private LocationRequest mLocationRequest;
    // private DbLocationEventDAO dbLocationEventDAO = new DbLocationEventDAO();

    public VisitObserver() {
        googleApiClient = new GoogleApiClient.Builder(MyApp.getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Log.i(this.getClass().getName(),"connecting to GoogleApiClient");
        googleApiClient.connect();
        chronometer = new Chronometer();
       checkChronometerThread = new CheckChronometerThread(chronometer);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(this.getClass().getName(),"<<<visitObserver onConnected");
        Log.i(this.getClass().getName(),"setting LocationRequest parameters");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setFastestInterval(VISIT_TIME_MILLISECONDS_THRESHOLD);
        mLocationRequest.setInterval(VISIT_TIME_MILLISECONDS_THRESHOLD); //aggiorna posizione ogni x secondi
        mLocationRequest.setSmallestDisplacement(VISIT_DISTANCE_METERS_THRESHOLD); //aggiorna posizione ogni x metri

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(MyApp.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MyApp.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(this.getClass().getName(),"need to request the missing permissions: not yet implemented");
            // TODO: we must require permission https://developer.android.com/training/permissions/requesting.html
            return;
        } else { //ha tutti i diritti
            previousLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (previousLocation != null) {

                visitInProgress = false;
                chronometer.start();//<<<<<<<<<<<<<<<<< avvio cronometro
                checkChronometerThread.run();
                DbVisitEvent dbVisitEvent = new DbVisitEvent();
                dbVisitEvent.setId(0);
                dbVisitEvent.setArrivalDate(previousLocation.getTime());
                dbVisitEvent.setDepartureDate(-1);
                dbVisitEvent.setLatitude(previousLocation.getLatitude());
                dbVisitEvent.setLongitude(previousLocation.getLongitude());
                dbVisitEvent.setAccuracy(previousLocation.getAccuracy());
                long id = dbVisitEvent.writeMe();
                System.out.println("-------last known location------");


                dbVisitEvent.setId(id);
                dbVisitEvent.dump();
                System.out.println("TODO:  03/11/16 riabilitare invio al server");
                /*AsyncSendToServer asyncSendToServer = new AsyncSendToServer("[" + dbVisitEvent.buildSerializedDataString() + "]", "" + dbVisitEvent.getId());
                asyncSendToServer.execute();*/

            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        Log.i(this.getClass().getName(),">>>completed onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("VisitObserver.onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("VisitObserver.onConnectionFailed()");
    }

    @Override
    public void onLocationChanged(Location location) {
        this.newLocation = location;
        System.out.println("------------------------------------------");
        System.out.println("VisitObserver.onLocationChanged()");

        double distanceBetweenLocation = TypeConverter.coordinatesToDistance(newLocation.getLatitude(),newLocation.getLongitude(),previousLocation.getLatitude(),previousLocation.getLongitude(),'m');
        System.out.println(" distance from previous (m) = " + TypeConverter.doubleTrunkTwoDigit(distanceBetweenLocation));
        long secondsBetweenLocation = (newLocation.getTime() - previousLocation.getTime())/1000;
        System.out.println("seconds from previous location = " + secondsBetweenLocation);
        System.out.println(" visitInProgress = " + visitInProgress);
          
            if ((visitInProgress) && (distanceBetweenLocation > VISIT_DISTANCE_METERS_THRESHOLD)) {
                System.out.println("<<<<<<<<< visit ended >>>>>>>>>");
                
                chronometer.stop();
                DbVisitEvent dbVisitEvent = new DbVisitEvent();
                dbVisitEvent.setId(0);
                dbVisitEvent.setArrivalDate(previousLocation.getTime());
                dbVisitEvent.setDepartureDate(newLocation.getTime());
                dbVisitEvent.setLatitude(previousLocation.getLatitude());
                dbVisitEvent.setLongitude(previousLocation.getLongitude());
                dbVisitEvent.setAccuracy(previousLocation.getAccuracy());
                long id = dbVisitEvent.writeMe();
                dbVisitEvent.setId(id);
                dbVisitEvent.dump();
                System.out.println("TODO: 03/11/16 riabilitare invio al server");
                /*AsyncSendToServer asyncSendToServer = new AsyncSendToServer("[" + dbVisitEvent.buildSerializedDataString() + "]", "" + dbVisitEvent.getId());
                asyncSendToServer.execute();*/
                previousLocation = newLocation;
            }/* else {
                System.out.println("<<<<<<<<< visit continuing >>>>>>>>>");
            }*/
        visitInProgress = false;
        System.out.println("endo of locationUpdate: visitInProgress = " + visitInProgress);
        }




    public void flushLocationTable() {
        // TODO: 02/11/16 to be used and tested
        DbLocationEventDAO  dbLocationEventDAO = new DbLocationEventDAO();
        ArrayList<DbLocationEvent> dbLocationEventAL = dbLocationEventDAO.getList();
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
        AsyncSendToServer asyncSendToServer = new AsyncSendToServer("[" + bulkLocationEventSTR + "]",idToDeleteListSTR);
        asyncSendToServer.execute();


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
            MyServerResponse myServerResponse = ServerApiUtils.addVisitToServer(dataToSend);
            myServerResponse.dump();
            if (myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                MyLog.i(this, "SENT NEW VISIT TO SERVER, DELETING  "  + idToDeleteListSTR);
                DbVisitEventDAO dbVisitEventDAO = new DbVisitEventDAO();
                dbVisitEventDAO.delete(idToDeleteListSTR);
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
    public class CheckChronometerThread implements Runnable {
        Chronometer chronometer;
        public CheckChronometerThread(Chronometer chronometer) {
            this.chronometer = chronometer;
        }
        @Override
        public void run() {
            while (true) {
                System.out.println("chronometer.getMilliseconds() = " + chronometer.getMilliseconds());
                if(newLocation == null) System.out.println("new location is null");
                if ((previousLocation !=null) && (!visitInProgress) && (previousLocation.getTime() > VISIT_TIME_MILLISECONDS_THRESHOLD)) {
                    System.out.println("VISIT STARTED at " + new Date(previousLocation.getTime()));
                    visitInProgress = true;
                    System.out.println("visitInProgress = " + visitInProgress);
                        System.out.println("<<<<<<<<< visit started >>>>>>>>>");
                        visitInProgress = true;
                        chronometer.start();
                        DbVisitEvent dbVisitEvent = new DbVisitEvent();
                        dbVisitEvent.setId(0);
                        dbVisitEvent.setArrivalDate(previousLocation.getTime());
                        dbVisitEvent.setDepartureDate(-1);
                        dbVisitEvent.setLatitude(previousLocation.getLatitude());
                        dbVisitEvent.setLongitude(previousLocation.getLongitude());
                        dbVisitEvent.setAccuracy(previousLocation.getAccuracy());
                        long id = dbVisitEvent.writeMe();
                        dbVisitEvent.setId(id);
                        dbVisitEvent.dump();
                        System.out.println("TODO: 03/11/16 riabilitare invio al server");
                /*AsyncSendToServer asyncSendToServer = new AsyncSendToServer("[" + dbVisitEvent.buildSerializedDataString() + "]", "" + dbVisitEvent.getId());
                asyncSendToServer.execute();*/
                    }
                SystemClock.sleep(5000);
                /*try {
                    SystemClock.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }
}



