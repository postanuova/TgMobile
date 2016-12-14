package org.teenguard.child.observer;

/**
 * Created by chris on 03/11/16.
 * http://www.coderzheaven.com/2016/06/20/geofencing-in-android-a-simple-example/
 */

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.teenguard.child.datatype.MyServerResponse;
import org.teenguard.child.dbdao.DbVisitEventDAO;
import org.teenguard.child.dbdatatype.DbVisitEvent;
import org.teenguard.child.utils.CalendarUtils;
import org.teenguard.child.utils.Chronometer;
import org.teenguard.child.utils.MyApp;
import org.teenguard.child.utils.MyLog;
import org.teenguard.child.utils.ServerApiUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by chris on 30/10/16.
 */

public class VisitObserver implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {


    /*public static int VISIT_DISTANCE_METERS_THRESHOLD = 30;
    public static int VISIT_TIME_MILLISECONDS_THRESHOLD = 20*1000;*/
    // TODO: 31/10/16 settare valori definitivi
    public static int VISIT_DISTANCE_METERS_THRESHOLD = 300;
    public static int VISIT_TIME_MILLISECONDS_THRESHOLD = 5*60*1000;
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
       checkChronometerThread = new CheckChronometerThread();
        //////////////////
       flushVisitTable();
        //////////////////
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(this.getClass().getName()," <<< visitObserver onConnected");
        Log.i(this.getClass().getName(),"setting LocationRequest parameters");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setFastestInterval(VISIT_TIME_MILLISECONDS_THRESHOLD/10);
        mLocationRequest.setInterval(VISIT_TIME_MILLISECONDS_THRESHOLD/10); //aggiorna posizione ogni x secondi
        mLocationRequest.setSmallestDisplacement(VISIT_DISTANCE_METERS_THRESHOLD); //aggiorna posizione ogni x metri

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        /*if (ActivityCompat.checkSelfPermission(MyApp.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MyApp.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(this.getClass().getName(),"need to request the missing permissions: not yet implemented");
            // TODO: we must require permission https://developer.android.com/training/permissions/requesting.html
            return;
        } else { //ha tutti i diritti*/

            visitInProgress = false;
            chronometer.start();//<<<<<<<<<<<<<<<<< avvio cronometro
            checkChronometerThread.start();
           LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
       // }

        Log.i(this.getClass().getName(),">>> completed onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("VisitObserver.onConnectionSuspended()");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("VisitObserver.onConnectionFailed()");
    }

    @Override
    public void onLocationChanged(Location location) {

        chronometer = new Chronometer();
        chronometer.start();
        System.out.println("------------------------------------------");
        System.out.println("VisitObserver.onLocationChanged()");
        newLocation = location;
        if(previousLocation == null) {
            previousLocation = location;
            System.out.println("VisitObserver.onLocationChanged previousLocation == null: set previousLocation = location");
        }
        double distanceBetweenLocation = newLocation.distanceTo(previousLocation);
        System.out.println("VisitObserver.onLocationChanged distanceBetweenLocation " + distanceBetweenLocation);
        System.out.println("newLocation:      lat " + newLocation.getLatitude() + " lon " + newLocation.getLongitude());
        System.out.println("previousLocation: lat " + previousLocation.getLatitude() + " lon " + previousLocation.getLongitude());
        long secondsBetweenLocation = (newLocation.getTime() - previousLocation.getTime()) / 1000;
        System.out.println("seconds from previous location = " + secondsBetweenLocation);
        System.out.println("visitInProgress = " + visitInProgress);
        if ((distanceBetweenLocation > 1.0)) {//la location è cambiata effettivamente
            if ((visitInProgress) && (distanceBetweenLocation > VISIT_DISTANCE_METERS_THRESHOLD)) {
                System.out.println("<<<<<<<<< VISIT ENDED >>>>>>>>>");
                DbVisitEvent dbVisitEvent = new DbVisitEvent();
                dbVisitEvent.setId(0);
                dbVisitEvent.setArrivalDate(CalendarUtils.nowUTCMillis());
                if (previousLocation.getTime() > 1480000000) {
                    dbVisitEvent.setArrivalDate(previousLocation.getTime());
                }

                /*dbVisitEvent.setDepartureDate(CalendarUtils.nowUTCMillis());
                if(newLocation.getTime() >  1480000000) {*/
                dbVisitEvent.setDepartureDate(newLocation.getTime());
                // }

                dbVisitEvent.setLatitude(previousLocation.getLatitude());
                dbVisitEvent.setLongitude(previousLocation.getLongitude());
                dbVisitEvent.setAccuracy(previousLocation.getAccuracy());
                long id = dbVisitEvent.writeMe();
                dbVisitEvent.setId(id);
                dbVisitEvent.dump();
                System.out.println(" Visit duration :" + dbVisitEvent.getDurationMilliseconds() / 1000 + "s");
                System.out.println("sending visit ended");
                AsyncSendToServer asyncSendToServer = new AsyncSendToServer("[" + dbVisitEvent.buildSerializedDataString() + "]", "" + dbVisitEvent.getId());
                asyncSendToServer.execute();
                previousLocation = newLocation;
                visitInProgress = false;
                /////////////////////
                //flushVisitTable();
                //////////////////////
            }

        } else {
            System.out.println("VisitObserver.onLocationChanged same location values, not sending to server");
        }
        System.out.println("endo of locationUpdate: visitInProgress = " + visitInProgress);
        }


    public class CheckChronometerThread extends Thread {

        public void run() {
            while (true) {
                System.out.println(" chronometer.getElapsedMilliseconds() = " + chronometer.getElapsedMilliseconds());
                System.out.println("visitInProgress = " + visitInProgress);
                if(newLocation == null) {
                    System.out.println("new location is null:setting newLocation = previousLocation");
                    newLocation = previousLocation;
                }
                if(previousLocation == null) System.out.println("previous location is null");
                if((newLocation != null) && (previousLocation != null)) {
                   // if (newLocation.distanceTo(previousLocation) < 0.1) {
                    if ((!visitInProgress) && (chronometer.getElapsedMilliseconds() > VISIT_TIME_MILLISECONDS_THRESHOLD)) {
                        System.out.println("VISIT STARTED at (previousLocation.getTime()) " + new Date(previousLocation.getTime()));
                        visitInProgress = true;

                        System.out.println("<<<<<<<<< VISIT STARTED >>>>>>>>>");
                        visitInProgress = true;
                        chronometer = new Chronometer();//fermo il cronometro
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
                        AsyncSendToServer asyncSendToServer = new AsyncSendToServer("[" + dbVisitEvent.buildSerializedDataString() + "]", "" + dbVisitEvent.getId());
                        asyncSendToServer.execute();
                  /*  } else {
                        System.out.println("CheckChronometerThread.run same location values, not sending to server");
                    }*/
                        /////////////////////
                        //flushVisitTable();
                        //////////////////////
                    }
                }
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static  void flushVisitTable() {
        System.out.println(" FLUSHING VISIT EVENT TABLE " + new Date(CalendarUtils.nowUTCMillis()).toString());
        DbVisitEventDAO dbVisitEventDAO = new DbVisitEventDAO();
        ArrayList<DbVisitEvent> dbVisitEventAL = dbVisitEventDAO.getList();
        System.out.println(" FLUSHING dbVisitEventAL.size() = " + dbVisitEventAL.size());
        if(dbVisitEventAL.size() > 0) {
            StringBuilder bulkVisitEventSB = new StringBuilder();
            StringBuilder idToDeleteListSB = new StringBuilder(); //la usero' per cancellare gli eventi una volta inviati
            for (DbVisitEvent dbVisitEvent : dbVisitEventAL) {
                bulkVisitEventSB.append(dbVisitEvent.buildSerializedDataString());
                bulkVisitEventSB.append(",");
                idToDeleteListSB.append(dbVisitEvent.getId());
                idToDeleteListSB.append(",");
            }
            String bulkVisitEventSTR = bulkVisitEventSB.toString();
            if (bulkVisitEventSTR.endsWith(","))
                bulkVisitEventSTR = bulkVisitEventSTR.substring(0, bulkVisitEventSTR.length() - 1);
            String idToDeleteListSTR = idToDeleteListSB.toString();
            if (idToDeleteListSTR.endsWith(","))
                idToDeleteListSTR = idToDeleteListSTR.substring(0, idToDeleteListSTR.length() - 1);
            // TODO: 12/12/16 testare il flushing: NetworkOnMainThreadException 
           /* AsyncSendToServer asyncSendToServer = new AsyncSendToServer("[" + bulkVisitEventSTR + "]", idToDeleteListSTR);
            asyncSendToServer.execute();*/
            ///////////NOT ASYNC VERSION/////////
            MyServerResponse myServerResponse = ServerApiUtils.addVisitToServer("[" + bulkVisitEventSTR + "]");
            myServerResponse.dump();
            if (myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                System.out.println("FLUSHING NEW VISIT TO SERVER, DELETING  "  + idToDeleteListSTR);
                 dbVisitEventDAO = new DbVisitEventDAO();
                dbVisitEventDAO.delete(idToDeleteListSTR);
            }
            ////////////////////////////////////
        }
    }

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
            MyLog.i(this, "ASYNC  SENDING NEW VISIT TO SERVER");
            MyServerResponse myServerResponse = ServerApiUtils.addVisitToServer(dataToSend);
            myServerResponse.dump();
            if (myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                MyLog.i(this, "SENT NEW VISIT TO SERVER, DELETING  "  + idToDeleteListSTR);
                DbVisitEventDAO dbVisitEventDAO = new DbVisitEventDAO();
                //System.out.println(" AsyncSendToServer VISITS: riattivare cancellazione per idToDeleteListSTR " + idToDeleteListSTR);
                dbVisitEventDAO.delete(idToDeleteListSTR);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println(" completed async execution");
        }
    }
}



