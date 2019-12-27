package com.example.towaquiz.delegates;

import android.content.Context;
import android.util.Patterns;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlMatcher {

    private UrlMatcher(){}

    public static boolean urlMatches(String url, Context context){
        boolean result = Patterns.WEB_URL.matcher(url).matches();
        if(!result){
            AlertDialog.showDialog("the url has a wrong format: " + url, "url error", context);
        }
        return result;
    }

    public static boolean setURLConnection(String url, Context context){
        boolean result = true;
        try{
            new URL(url);
        }catch(MalformedURLException ex ){
            result = false;
            AlertDialog.showDialog("url should start with protocol: 'http:// | https://'",
                    "missing protocol", context);
        }
        return result;
    }

    public static boolean isUrlValid(String url, Context context){
        return urlMatches(url, context) && setURLConnection(url, context);
    }
}
