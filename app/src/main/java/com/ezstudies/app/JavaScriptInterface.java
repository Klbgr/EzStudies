package com.ezstudies.app;

import android.webkit.JavascriptInterface;

public class JavaScriptInterface {
    private String source;
    @JavascriptInterface
    public void processHTML(String html){
        source = html;
    }

    public String getSource() {
        return source;
    }
}
