package org.teenguard.parent.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.teenguard.child.R;

public class WebFrameActivity extends AppCompatActivity {
    // TODO: 30/11/16 http://www.html.it/pag/48934/visualizzare-pagine-web-webview/ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_frame);
        WebView myWebView = (WebView) findViewById(R.id.webview_id);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("http://192.168.1.101:8383/ParentWeb/index.html");
        //myWebView.loadUrl("http://www.html.it");
    }
}
