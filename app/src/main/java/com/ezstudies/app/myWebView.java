package com.ezstudies.app;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ezstudies.app.activities.Agenda;

public class myWebView extends WebViewClient {
    private Agenda agenda;
    private Boolean parsing = false;

    public myWebView(Agenda agenda){
        this.agenda = agenda;
    }
    public void onPageFinished(WebView view, String url) {
        Log.d("url", url);
        view.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
        if (url.contains("fid0") && url.contains("listWeek") && !parsing){
            Log.d("parser", "parsing !!!");
            parsing = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    agenda.parseCelcat();
                }
            }, 10000);
        }
    }
}
