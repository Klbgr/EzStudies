package com.ezstudies.app;

import android.os.Handler;
import android.text.BoringLayout;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Webview extends WebViewClient {
    Boolean parsing = false;
    public void onPageFinished(WebView view, String url) {
        Log.d("url", url);
        view.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
        if (url.contains("fid0") && url.contains("listWeek") && !parsing){
            Log.d("parser", "parsing !!!");
            parsing = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // yourMethod();
                    CelcatParser.parse();
                }
            }, 10000);
            //CelcatParser.buildICS();
            //CelcatParser.saveICS();
        }
    }
}
