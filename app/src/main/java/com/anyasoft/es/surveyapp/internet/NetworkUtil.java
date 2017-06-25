package com.anyasoft.es.surveyapp.internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

/**
 * Created by saurabh.singh on 7/2/2016.
 */
public class NetworkUtil {

    public static boolean isOnline(Context context) {
        if(null == context){
            return false;
        }//
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }//isOnline

}
