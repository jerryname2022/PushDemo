package com.push.demo;

import android.os.Environment;
import android.util.Log;
import android.webkit.JavascriptInterface;

import java.io.File;

public class WebViewJavascriptInterface {

    @JavascriptInterface
    public void onHtmlLoaded(String url, String html) {

        File dir = new File(Environment.getExternalStorageDirectory(), "c26");
        FileWriterUtil.mkdir(dir);
        String file = getFileNameFromUrl(url);
        FileWriterUtil.write(new File(dir, file), html);

        Log.i(getClass().getSimpleName(), "====>url=" + url + " name " + file);
    }


    @JavascriptInterface
    public void CUSCcallback() {
        Log.i(getClass().getSimpleName(), "====>CUSCcallback=");
    }

    public static String getFileNameFromUrl(String url) {
        if (url != null) {
            String[] names = url.split("/");
            if (names != null) {
                String str = names[names.length - 1];
                if (str.lastIndexOf("?") > 0 && str.lastIndexOf("?") < str.length()) {
                    str = str.substring(0, str.lastIndexOf("?"));
                }
                return str;
            }
        }
        return null;
    }

}
