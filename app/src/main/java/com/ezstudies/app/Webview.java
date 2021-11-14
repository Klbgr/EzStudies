package com.ezstudies.app;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Webview extends WebViewClient {
    public void onPageFinished(WebView view, String url) {
        Log.d("url", url);
        view.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
    }
}
