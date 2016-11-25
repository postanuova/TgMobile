package org.teenguard.child.templates;

import android.os.AsyncTask;

/**
 * Created by chris on 25/11/16.
 */

public class MyAsyncTask extends AsyncTask<String, String, String> {
    //http://www.journaldev.com/9708/android-asynctask-example-tutorial
    String dataToSend;

    public MyAsyncTask(String dataToSend) {
        this.dataToSend = dataToSend;
    }

    @Override
    protected String doInBackground(String... params) {
        return null;
    }

    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}