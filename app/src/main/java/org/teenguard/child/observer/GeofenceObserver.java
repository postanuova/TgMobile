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
import org.teenguard.child.utils.MyLog;
import org.teenguard.child.utils.ServerApiUtils;
import org.teenguard.child.utils.TypeConverter;

import java.util.ArrayList;


/**
 * Created by chris on 04/11/16.
 * http://io2015codelabs.appspot.com/codelabs/geofences#3
 * https://code.tutsplus.com/tutorials/how-to-work-with-geofences-on-android--cms-26639
 */

public class GeofenceObserver implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener,ResultCallback<Status> {
    private DbGeofenceDAO dbGeofenceDAO = new DbGeofenceDAO();
    private GoogleApiClient googleApiClient;
    protected ArrayList<Geofence> deviceGeofenceAL = new ArrayList<Geofence>(); //will contain (Android) device Geofences
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
        populateDeviceGeofenceAL(); //inizialmente carico nel device le geofences preesistenti sul db
        String shaGeofencesSTR = "shaString";
        // TODO: 14/11/16 leggere shaString precedente
        AsyncGetGeofencesFromServer asyncGetGeofencesFromServer = new AsyncGetGeofencesFromServer(shaGeofencesSTR);
        asyncGetGeofencesFromServer.execute();
    }


    ///////////////////
    //////////////////////////////////
    public class AsyncGetGeofencesFromServer extends AsyncTask<String, String, String> {
        //http://www.journaldev.com/9708/android-asynctask-example-tutorial
        String dataToSend;
        public  AsyncGetGeofencesFromServer(String dataToSend) {
            this.dataToSend = dataToSend;
        }
        @Override
        protected String doInBackground(String... params) {
            ///////////////NEEDS TO BE EXECUTED IN BACKGROUND/////////////////////
            MyLog.i(this, "ASYNC GETTING GEOFENCES FROM SERVER");
            MyServerResponse myServerResponse = ServerApiUtils.getBeatFromServer();
            myServerResponse.dump();
            if (myServerResponse.getResponseCode() > 199 && myServerResponse.getResponseCode() < 300) {
                MyLog.i(this, "RECEIVED NEW GEOFENCE FROM SERVER");
                String jsonServerResponse = myServerResponse.getResponseBody();
                Gson gson = new Gson();
                BeatResponseJsonWrapper beatResponseJsonWrapper = gson.fromJson(jsonServerResponse,BeatResponseJsonWrapper.class);
                System.out.println(" NEW geofences number " + beatResponseJsonWrapper.data.geofences.size());
                //delete geofences from db
                System.out.println("delete old geofences from db");
                dbGeofenceDAO.delete();
                //remove all active geofences
                //TODO: 10/11/16 aggiunta di nuove,overwrite di quelle che già esistono...e cancellazione di quelle che non ci sono più???
                https://www.raywenderlich.com/103540/geofences-googleapiclient
                //http://stackoverflow.com/questions/16631962/android-how-to-retrieve-list-of-registered-geofences
                System.out.println(" remove all registered geofences not implemented");

                //parse geofences json from server and write geofences on db
                ArrayList<BeatResponseJsonWrapper.Geofences> serverGeofencesAL = beatResponseJsonWrapper.data.geofences;
                for (BeatResponseJsonWrapper.Geofences serverGeofence:serverGeofencesAL ) {
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
            //reloadGeofencesFrom db into device
            populateDeviceGeofenceAL();
            registerGeofences();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("completed AsyncGetGeofencesFromServer execution");
        }
    }
    ///////////////////////////////
    ///////////////////
//----------DEPRECATED, NOT ASYNC------------------
    /*public void manageGeofences() {
        //check if new data are present on server
        // TODO: 14/11/16 check if new data are present on server
        boolean newGeofencesFromServer = true;
        if(newGeofencesFromServer) {
            //read geofence json from server
            System.out.println("reading beat data from server");
            MyServerResponse myServerResponse = ServerApiUtils.getBeatFromServer();
            String jsonServerResponse = myServerResponse.getResponseBody();
            Gson gson = new Gson();
            BeatResponseJsonWrapper beatResponseJsonWrapper = gson.fromJson(jsonServerResponse,BeatResponseJsonWrapper.class);
            System.out.println(" geofences number " + beatResponseJsonWrapper.data.geofences.size());
            //delete geofences from db
            dbGeofenceDAO.delete();
            //remove all active geofences
            //TODO: 10/11/16 aggiunta di nuove,overwrite di quelle che già esistono...e cancellazione di quelle che non ci sono più???
            https://www.raywenderlich.com/103540/geofences-googleapiclient
            //http://stackoverflow.com/questions/16631962/android-how-to-retrieve-list-of-registered-geofences
            System.out.println("remove all registered geofences not implemented");

            //parse geofences json from server and write geofences on db
            ArrayList<BeatResponseJsonWrapper.Geofences> serverGeofencesAL = beatResponseJsonWrapper.data.geofences;
            for (BeatResponseJsonWrapper.Geofences serverGeofence:serverGeofencesAL ) {
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

        //read dbGeofences and populate geofencesAL
        populateGeofenceAL();
    }*/



    //--------DEPRECATED ------------
  /*  public static void parseGeofenceJsonAR() {
        class MyWrapper {
            String id;
            double latitude;
            double longitude;
            int radius;
            boolean enter;
            boolean leave;
        }
        String jsonAR = "[{ 'id': 'SiamMall', 'latitude': 28.0690565, 'longitude': -16.7249978, 'radius': 100, 'enter': true, 'leave': true }, { 'id': 'Michele', 'latitude': 28.1251502, 'longitude': -16.7394207, 'radius': 100, 'enter': true, 'leave': true }, { 'id': 'ChiesaLosCristianos', 'latitude': 28.0521532, 'longitude': -16.7177612, 'radius': 100, 'enter': true, 'leave': true } ]";
        Gson gson = new Gson();


        MyWrapper[] arr = gson.fromJson(jsonAR, MyWrapper[].class);
        System.out.println("arr.length = " + arr.length);
    }*/


    /**------DEPRECATED---------
     * this method will parse json from server and will insert geofences into db
     * @param jsonStringContainingGeofences
     */
    /*private void writeNewGeofencesOnDb(String jsonStringContainingGeofences) {
        //String jsonSTR = { "id": "SiamMall", "latitude": 28.0690565, "longitude": -16.7249978, "radius": 100, "enter": true, "leave": true }, { "id": "Michele", "latitude": 28.1251502, "longitude": -16.7394207, "radius": 100, "enter": true, "leave": true }, { "id": "ChiesaLosCristianos", "latitude": 28.0521532, "longitude": -16.7177612, "radius": 100, "enter": true, "leave": true } ] }, "t": 3600, "h": "6f4ef2a89f7a834a65c1d6bc4147a4a792504848" }
     //   String jsonAR = "[" + { "]";
        DbGeofence dbGeofence;

        dbGeofence = new DbGeofence(0,"Lincontro",28.120483,-16.7775494,500,1,1);
        dbGeofence.writeMe();
        dbGeofence = new DbGeofence(0,"SiamMall",28.0690565,-16.7249978,500,true,true);
        dbGeofence.writeMe();

        dbGeofence = new DbGeofence(0,"Michele",28.1251502,-16.7394207,500,true, true);
        dbGeofence.writeMe();

        dbGeofence = new DbGeofence(0,"Chris",28.0589617,  -16.7299850,500,true,true);
        dbGeofence.writeMe();
    }*/

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



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("GEOFENCE observer connected");
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000) // 1 second, in milliseconds
        .setSmallestDisplacement(5);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        /*if (ActivityCompat.checkSelfPermission(MyApp.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
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
            geofencingRequestBuilder.addGeofences(deviceGeofenceAL);
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
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);*/
        registerGeofences();
        Log.i(this.getClass().getName(),">>>completed onConnected");
    }

    private void registerGeofences() {

        Log.i(this.getClass().getName(),"registerGeofences");
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
            geofencingRequestBuilder.addGeofences(deviceGeofenceAL);
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
        System.out.println("GEOFENCE OBSERVER location CHANGED = " + location.getTime());
        System.out.println("server time format = " + CalendarUtils.serverTimeFormat(location.getTime()));
        System.out.println("location.getLatitude() = " + location.getLatitude());
        System.out.println("location.getLongitude() = " + location.getLongitude());
        System.out.println("location.getAccuracy() = " + location.getAccuracy());

        System.out.println("DEBUG: distance from L'incontro = " + TypeConverter.coordinatesToDistance(location.getLatitude(),location.getLongitude(),28.1205434,-16.7750331,'m'));
        System.out.println("DEBUG:distance from Michele = " + TypeConverter.coordinatesToDistance(location.getLatitude(),location.getLongitude(),28.1251502,-16.7394207,'m'));
        System.out.println("DEBUG:distance from Chris =  " + TypeConverter.coordinatesToDistance(location.getLatitude(),location.getLongitude(),28.0589617,-16.7299850,'m'));
    }




    // TODO: 05/11/16 json parsing of incoming geofences


    public void flushGeofenceTable() {
        // TODO: 13/11/16 to be used and tested
        DbGeofenceEventDAO dbGeofenceEventDAO = new DbGeofenceEventDAO();
        ArrayList <DbGeofenceEvent> dbGeofenceEventAL = dbGeofenceEventDAO.getList();
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder idToDeleteListSB = new StringBuilder(); //la usero' per cancellare gli eventi una volta inviati
        for (DbGeofenceEvent dbGeofenceEvent:dbGeofenceEventAL) {
            stringBuilder.append(dbGeofenceEvent.getSerializedData());
            stringBuilder.append(",");
            idToDeleteListSB.append(dbGeofenceEvent.getId());
        }
        String bulkGeofenceEventSTR = stringBuilder.toString();
        if(bulkGeofenceEventSTR.endsWith(",")) bulkGeofenceEventSTR = bulkGeofenceEventSTR.substring(0,bulkGeofenceEventSTR.length()-1);
        String idToDeleteListSTR = stringBuilder.toString();
        if(idToDeleteListSTR.endsWith(",")) idToDeleteListSTR = idToDeleteListSTR.substring(0,idToDeleteListSTR.length()-1);
        GeofenceTransitionsIntentService.AsyncSendToServer asyncSendToServer = new GeofenceTransitionsIntentService().new AsyncSendToServer("[" + bulkGeofenceEventSTR + "]",idToDeleteListSTR);
        asyncSendToServer.execute();
    }

   /* public static void main(String args[]){
        parseGeofenceJsonAR();
    }*/

}

