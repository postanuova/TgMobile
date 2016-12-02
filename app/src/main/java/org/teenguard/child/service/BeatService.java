package org.teenguard.child.service;

/**
 * Created by chris on 01/12/16.
 */

public class BeatService {
    int checkInterval; //is the t-parameter of beat response
    final static int BEAT_INTERVAL = 5000;
    String newShaFingerPrint = "newSha"; //is the h-parameter of beat response
    String oldShaFingerPrint = "oldSha";
}

    //BEAT
//get(h) server e mi risponde 304 e senza contenuto e non faccio niente (lo sha non è cambiato)
    //get(h) e mi risponde con 200 ha un contenuto salvo il nuovo h  e uso il contenuto e uso il t(in secondi) (di gestione di aggiornamento)
    //
    //post si usa durante la registrazione
//ogni t
    //


   /* public BeatService() {
        ///////////
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // TODO: 14/11/16 leggere shaString precedente
                if (!MyConnectionUtils.isAirplaneModeOn()) {
                    System.out.println("TIMER.run: executing AsyncGetGeofencesFromServer");
                    AsyncGetGeofencesFromServer asyncGetGeofencesFromServer = new AsyncGetGeofencesFromServer();
                    asyncGetGeofencesFromServer.execute("");
                    ////////////
                } else {
                    System.out.println(" GeofenceObserverNew.SendBeatToServerThread: DEVICE IS IN AIRPLANE MODE");
                }
            }
        }, 0, 5000);
        ///////////////
    }

}
*/
/*

    public class AsyncGetGeofencesFromServer extends AsyncTask<String, String, String> {
        //http://www.journaldev.com/9708/android-asynctask-example-tutorial
        @Override
        protected String doInBackground(String... params) {
            MyServerResponse myServerGetResponse = ServerApiUtils.getBeatFromServer(oldShaFingerPrint);
            myServerGetResponse.dump();
            if (myServerGetResponse.getResponseCode() == 200) {
                MyLog.i(this, "RECEIVED NEW CONTENT FROM SERVER");
                String jsonServerResponse = myServerGetResponse.getResponseBody();
                Gson gson = new Gson();
                BeatResponseJsonWrapper beatResponseJsonWrapper = gson.fromJson(jsonServerResponse, BeatResponseJsonWrapper.class);

                checkInterval = beatResponseJsonWrapper.t;
                System.out.println("checkInterval t = " + checkInterval);
                newShaFingerPrint = beatResponseJsonWrapper.h; //is the h-parameter of beat response
                System.out.println("oldShaFingerPrint =");
                System.out.println("newShaFingerPrint h = " + newShaFingerPrint);
                if (!oldShaFingerPrint.equalsIgnoreCase(newShaFingerPrint)) {
                    oldShaFingerPrint = newShaFingerPrint;
                    System.out.println(" NEW geofences number " + beatResponseJsonWrapper.data.geofences.size());
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
                } else {
                    System.out.println("AsyncGetGeofencesFromServer.doInBackground shaFingerprint h not changed");
                }
            }
            if (myServerGetResponse.getResponseCode() == 304) {
                System.out.println("304 NO NEW CONTENT FROM SERVER");
            }
            //completata l'esecuzione,invoca il onPostExecute
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //reloadGeofencesFrom db into device
            GeofenceObserverNew.populateDeviceGeofenceAL();
            if (deviceGeofenceAL != null && deviceGeofenceAL.size() > 0) {
                System.out.println("onPostExecute registering geofences");
                GeofenceObserverNew.registerGeofences();
            } else {
                System.out.println("onPostExecute deviceGeofenceAL is null or zero sized");
            }
            System.out.println("completed AsyncGetGeofencesFromServer execution");
        }
    }

    public class AsyncSendBeatToServer extends AsyncTask<String, String, String> {
        //http://www.journaldev.com/9708/android-asynctask-example-tutorial
        String dataToSend;

        public AsyncSendBeatToServer(String dataToSend) {
            this.dataToSend = dataToSend;
        }

        @Override
        protected String doInBackground(String... params) {
            System.out.println(" ASYNC SendBeatToServerThread STARTED");
            while (true) {
                System.out.println("SendBeatToServerThread.run: cycle " + new Date(Calendar.getInstance().getTimeInMillis()));
                try {
                    if (BEAT_INTERVAL > 5000) {
                        Thread.sleep(BEAT_INTERVAL);
                        // TODO: 14/11/16 moltiplicare *1000 beat interval
                    } else {
                        Thread.sleep(5000);//per sicurezza
                    }
                    System.out.println("sleeping for 5000");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //////////
                // TODO: 14/11/16 leggere shaString precedente
                if (!MyConnectionUtils.isAirplaneModeOn()) {
                    System.out.println("SendBeatToServerThread.run: sending beat");
                    AsyncGetGeofencesFromServer asyncGetGeofencesFromServer = new AsyncGetGeofencesFromServer(shaFingerPrint);
                    asyncGetGeofencesFromServer.doInBackground("");
                    ////////////
                } else {
                    System.out.println("GeofenceObserverNew.SendBeatToServerThread: DEVICE IS IN AIRPLANE MODE");
                }

            }
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

}






//testing sha fingerprint sending
    *//*private class SendBeatToServerThread extends Thread {

        public void run() {
            System.out.println(" SendBeatToServerThread STARTED");
            while (true) {
                System.out.println("SendBeatToServerThread.run: cycle " + new Date(Calendar.getInstance().getTimeInMillis()));
                try {
                    if(BEAT_INTERVAL > 5000) {
                        Thread.sleep(BEAT_INTERVAL);
                        // TODO: 14/11/16 moltiplicare *1000 check interval
                    }
                    else {
                        Thread.sleep(5000);//per sicurezza
                    }
                    System.out.println("sleeping for 5000");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //////////
                // TODO: 14/11/16 leggere shaString precedente
                if(!MyConnectionUtils.isAirplaneModeOn()) {
                    System.out.println("SendBeatToServerThread.run: sending beat");
                    GeofenceObserverNew.AsyncGetGeofencesFromServer asyncGetGeofencesFromServer = new GeofenceObserverNew.AsyncGetGeofencesFromServer(shaFingerPrint);
                    asyncGetGeofencesFromServer.execute();
                    ////////////
                } else {
                    System.out.println("GeofenceObserverNew.SendBeatToServerThread: DEVICE IS IN AIRPLANE MODE");
                }

            }
        }
    }*/



