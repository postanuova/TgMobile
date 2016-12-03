package org.teenguard.child.observer;


import android.app.PendingIntent;
import android.content.Intent;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.teenguard.child.datatype.BeatResponseJsonWrapper;
import org.teenguard.child.datatype.MyServerResponse;
import org.teenguard.child.dbdao.DbGeofenceDAO;
import org.teenguard.child.dbdao.DbGeofenceEventDAO;
import org.teenguard.child.dbdatatype.DbGeofence;
import org.teenguard.child.dbdatatype.DbGeofenceEvent;
import org.teenguard.child.service.GeofenceTransitionsIntentService;
import org.teenguard.child.utils.CalendarUtils;
import org.teenguard.child.utils.MyApp;
import org.teenguard.child.utils.MyConnectionUtils;
import org.teenguard.child.utils.ServerApiUtils;
import org.teenguard.child.utils.TypeConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by chris on 04/11/16.
 * http://io2015codelabs.appspot.com/codelabs/geofences#3
 * https://code.tutsplus.com/tutorials/how-to-work-with-geofences-on-android--cms-26639
 * dbGeofence = new DbGeofence(0,"Chris",28.0589617,  -16.7299850,500,true,true);
 */

// TODO: 25/11/16 update geofences from server json parsing of incoming geofences

public class GeofencesObserver implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener,ResultCallback<Status> {
    public static int checkInterval=1000; //is the t-parameter of beat response
    String oldShaFingerPrint ="dummyBeat"; //is the h-parameter of beat response
    String newShaFingerPrint ="dummyBeat";
    private DbGeofenceDAO dbGeofenceDAO = new DbGeofenceDAO();
    private GoogleApiClient googleApiClient;
    protected ArrayList<Geofence> deviceGeofenceAL = new ArrayList<Geofence>(); //will contain (Android) device Geofences
    private LocationRequest mLocationRequest;
    //ca53d9673d83072c6c5dd4528f5edc74e67746cdcc4c2dedec5d2cdec2de9f16
    //{ "data": { "geofences": [ { "id": "Lincontro", "latitude": 28.120483, "longitude": -16.7775494, "radius": 100, "enter": true, "leave": true }, { "id": "Ale", "latitude": 28.1250742, "longitude": -16.7779788, "radius": 100, "enter": true, "leave": true }, { "id": "SiamMall", "latitude": 28.0690565, "longitude": -16.7249978, "radius": 100, "enter": true, "leave": true }, { "id": "Michele", "latitude": 28.1251502, "longitude": -16.7394207, "radius": 100, "enter": true, "leave": true }, { "id": "ChiesaLosCristianos", "latitude": 28.0521532, "longitude": -16.7177612, "radius": 100, "enter": true, "leave": true } ] }, "t": 3600, "h": "6f4ef2a89f7a834a65c1d6bc4147a4a792504848" }


    public GeofencesObserver() {
        System.out.println("<GeofenceObserver started>");
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
        System.out.println("GEOFENCE observer googpleApiClient connected");
        /////////////
        System.out.println("onConnected: calling populateDeviceGeofenceAL for initial populating");
        populateDeviceGeofenceAL(); //inizialmente carico nel device le geofences preesistenti sul db
        if(!MyConnectionUtils.isAirplaneModeOn()) {
            if (deviceGeofenceAL != null && deviceGeofenceAL.size() > 0) {
                System.out.println("onConnected registering geofences");
                registerGeofences();
            } else {
                System.out.println("onConnected deviceGeofenceAL is null or zero sized");
            }
            AsyncGetGeofencesFromServer asyncGetGeofencesFromServer = new AsyncGetGeofencesFromServer();
            asyncGetGeofencesFromServer.execute(); //will load and eventually register geofences on post execution
            ////////////
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000) // 1 second, in milliseconds
                    .setSmallestDisplacement(5);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if(mLocationRequest == null) System.out.println("GeofenceObserverNew.onConnected mLocationRequest == null");
            //registerGeofences();
            Log.i(this.getClass().getName(), ">>>completed onConnected");
        } else {
            System.out.println("GeofenceObserverNew.onConnected: DEVICE IS IN AIRPLANE MODE");
        }
        startBeatTimer();
    }


    private void startBeatTimer() {
        final Timer beatTimer = new Timer();
       beatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!MyConnectionUtils.isAirplaneModeOn()) {
                    System.out.println("TIMER.run: sending beat checkInterval " + checkInterval);
                    AsyncGetGeofencesFromServer asyncGetGeofencesFromServer = new AsyncGetGeofencesFromServer();
                    asyncGetGeofencesFromServer.execute();
                } else {
                    System.out.println("GeofenceObserver.SendBeatToServerThread: DEVICE IS IN AIRPLANE MODE");
                }
                beatTimer.cancel();
                checkInterval ++;
                startBeatTimer();
            }
        },checkInterval,1000 );
        // TODO: 02/12/16 mettere  checkInterval al posto di 10000
    }

    public class AsyncGetGeofencesFromServer extends AsyncTask<String, String, String> {
        boolean receivedNewContentFromServer = false; //if setted, register new geofences on post execute
        public  AsyncGetGeofencesFromServer() {
        }
        @Override
        protected String doInBackground(String... params) {
            MyServerResponse  myServerGetResponse = ServerApiUtils.getBeatFromServer(oldShaFingerPrint);
            myServerGetResponse.shortDump();
            if (myServerGetResponse.getResponseCode() == 200) {
                System.out.println("200: RECEIVED NEW CONTENT FROM SERVER"+ CalendarUtils.currentDatetimeUTC());
                receivedNewContentFromServer = true;
                String jsonServerResponse = myServerGetResponse.getResponseBody();
                Gson gson = new Gson();
                BeatResponseJsonWrapper beatResponseJsonWrapper = gson.fromJson(jsonServerResponse, BeatResponseJsonWrapper.class);

                checkInterval = beatResponseJsonWrapper.t;
                System.out.println("checkInterval t = " + checkInterval);
                newShaFingerPrint = beatResponseJsonWrapper.h; //is the h-parameter of beat response
                System.out.println("oldShaFingerPrint = " + oldShaFingerPrint + " newShaFingerPrint h = " + newShaFingerPrint);
                System.out.println(" NEW geofences number " + beatResponseJsonWrapper.data.geofences.size());
                if (!oldShaFingerPrint.equalsIgnoreCase(newShaFingerPrint)) {
                    oldShaFingerPrint = newShaFingerPrint;
                    System.out.println("sha fingerprint changed");
                    //delete geofences from db
                    System.out.println(" delete old geofences from db");
                    dbGeofenceDAO.delete();
                    //remove all active geofences
                    //TODO: 10/11/16 aggiunta di nuove,overwrite di quelle che già esistono...e cancellazione di quelle che non ci sono più???
//www.raywenderlich.com/103540/geofences-googleapiclient
                    //http://stackoverflow.com/questions/16631962/android-how-to-retrieve-list-of-registered-geofences
                    System.out.println(" remove all registered geofences not implemented");

                    //parse geofences json from server and write geofences on db
                    ArrayList<BeatResponseJsonWrapper.Geofences> serverGeofencesAL = beatResponseJsonWrapper.data.geofences;
                    for (BeatResponseJsonWrapper.Geofences serverGeofence : serverGeofencesAL) {
                        DbGeofence dbGeofence = new DbGeofence();
                        dbGeofence.setId(0);
                        dbGeofence.setGeofenceId(serverGeofence.id);
                        dbGeofence.setLatitude(serverGeofence.latitude);
                        dbGeofence.setLongitude(serverGeofence.longitude);
                        dbGeofence.setRadius(serverGeofence.radius);
                        dbGeofence.setEnter(TypeConverter.booleanToInt(serverGeofence.enter));
                        dbGeofence.setLeave(TypeConverter.booleanToInt(serverGeofence.leave));
                        dbGeofence.writeMe();
                    }
                }
            }
            if (myServerGetResponse.getResponseCode() == 304) {
                System.out.println("304 NO NEW CONTENT FROM SERVER " + CalendarUtils.currentDatetimeUTC());
            }
            //completata l'esecuzione,invoca il onPostExecute
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (receivedNewContentFromServer) {
                //reloadGeofencesFrom db into device
                populateDeviceGeofenceAL();
                if (deviceGeofenceAL != null && deviceGeofenceAL.size() > 0) {
                    System.out.println("onPostExecute registering geofences");
                    registerGeofences();
                } else {
                    System.out.println("onPostExecute deviceGeofenceAL is null or zero sized");
                }
                System.out.println("completed AsyncGetGeofencesFromServer execution");
            }
        }
    }



    /**
     * load geofences from db
     */
    private void populateDeviceGeofenceAL() {
        //reset della lista
        deviceGeofenceAL.clear();
        System.out.println("populate deviceGeofenceAL");
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
            deviceGeofenceAL.add(geofence);
        }
        System.out.println("populateGeofenceAL deviceGeofenceAL.size() = " + deviceGeofenceAL.size());
    }


    private void registerGeofences() {
        Log.i(this.getClass().getName(),"<< registerGeofences >>");
        if (ActivityCompat.checkSelfPermission(MyApp.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MyApp.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(this.getClass().getName(),"registerGeofences :need to request the missing permissions: not yet implemented");
            // TODO: we must require permission https://developer.android.com/training/permissions/requesting.html
            return;
        } else {
            //ha tutti i diritti
            System.out.println(" registerGeofences: all rights enabled: initializing geofencingRequest");

            //costruisco l'oggetto geofencingRequest che conterrà la lista delle geofences

            GeofencingRequest.Builder geofencingRequestBuilder = new GeofencingRequest.Builder();
            geofencingRequestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
            geofencingRequestBuilder.addGeofences(deviceGeofenceAL);
            GeofencingRequest geofencingRequest = geofencingRequestBuilder.build();
            System.out.println("registerGeofences geofencingRequest.getGeofences().size() = " + geofencingRequest.getGeofences().size());
            //costruisco il pending Intent
            Intent intent = new Intent(MyApp.getContext(), GeofenceTransitionsIntentService.class);
            PendingIntent pendingIntent = PendingIntent.getService(MyApp.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);// We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()

            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    geofencingRequest,
                    pendingIntent).setResultCallback(this);//chiama onResult

        }
        if (deviceGeofenceAL == null) System.out.println("GeofenceObserverNew.registerGeofences deviceGeofenceAL is null");
        if(googleApiClient == null) System.out.println("registerGeofences.googleApiClient is null");
        if(mLocationRequest == null) System.out.println("registerGeofences.mLocationRequest is null");
        if(this == null) System.out.println("this is null");
        if(mLocationRequest != null && googleApiClient != null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        }
        else {
            System.out.println("<<<<<<< GEOFENCING NOT WORKING (BECAUSE DEVICE IS IN AIRPLANE MODE ??)>>>>>>>>>>>>");
            // TODO: 15/11/16 se il telefono è in modalità aereo si generano tantissime eccezioni
        }
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
        System.out.println("GeofenceObserverNew.onConnectionSuspended");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("GeofenceObserverNew.onConnectionFailed");

    }

    @Override
    public void onLocationChanged(Location location) {
        //////////////////////////
        //flushGeofenceEventTable(); riattivare
        //////////////////////////
        System.out.println("GEOFENCE OBSERVER location CHANGED = " + location.getTime());
        System.out.println("server time format = " + CalendarUtils.serverTimeFormat(location.getTime()) + " latitude = " + location.getLatitude() + " longitude = " + location.getLongitude() + " accuracy = " + location.getAccuracy());

        System.out.println("DEBUG: distance from L'incontro = " + TypeConverter.coordinatesToDistance(location.getLatitude(),location.getLongitude(),28.1205434,-16.7750331,'m'));
        System.out.println("DEBUG:distance from Michele = " + TypeConverter.coordinatesToDistance(location.getLatitude(),location.getLongitude(),28.1251502,-16.7394207,'m'));
        System.out.println("DEBUG:distance from Chris =  " + TypeConverter.coordinatesToDistance(location.getLatitude(),location.getLongitude(),28.0589617,-16.7299850,'m'));
    }


    public static void flushGeofenceEventTable() {
        // TODO: 25/11/16 to be used and tested
        System.out.println("FLUSHING GEOFENCE EVENT TABLE " + new Date(CalendarUtils.nowUTCMillis()).toString());
        DbGeofenceEventDAO dbGeofenceEventDAO = new DbGeofenceEventDAO();
        ArrayList<DbGeofenceEvent> dbGeofenceEventAL = dbGeofenceEventDAO.getList();
        System.out.println(" FLUSHING dbGeofenceEventAL.size() = " + dbGeofenceEventAL.size());
        if(dbGeofenceEventAL.size() > 0) {
            StringBuilder bulkGeofenceEventSB = new StringBuilder();//contiene gli eventi geofence da inviare
            StringBuilder idToDeleteListSB = new StringBuilder(); //la usero' per cancellare gli eventi una volta inviati
            for (DbGeofenceEvent dbGeofenceEvent : dbGeofenceEventAL) {
                System.out.println("dbGeofenceEvent id= " + dbGeofenceEvent.getGeofenceId() + " event" + dbGeofenceEvent.getEvent());
                bulkGeofenceEventSB.append(dbGeofenceEvent.getSerializedData());
                bulkGeofenceEventSB.append(",");
                idToDeleteListSB.append(dbGeofenceEvent.getId());
                idToDeleteListSB.append(",");
            }
            String bulkGeofenceEventSTR = bulkGeofenceEventSB.toString();
            if (bulkGeofenceEventSTR.endsWith(","))
                bulkGeofenceEventSTR = bulkGeofenceEventSTR.substring(0, bulkGeofenceEventSTR.length() - 1);
            String idToDeleteListSTR = idToDeleteListSB.toString();
            if (idToDeleteListSTR.endsWith(","))
                idToDeleteListSTR = idToDeleteListSTR.substring(0, idToDeleteListSTR.length() - 1);
            GeofenceTransitionsIntentService.AsyncSendToServer asyncSendToServer = new GeofenceTransitionsIntentService().new AsyncSendToServer("[" + bulkGeofenceEventSTR + "]", idToDeleteListSTR);
            asyncSendToServer.execute();
        } else {
            System.out.println(" no GEOFENCE events to flush " + CalendarUtils.currentDatetimeUTC());
        }
    }

}

