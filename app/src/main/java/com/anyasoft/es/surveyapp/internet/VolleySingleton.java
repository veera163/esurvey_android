package com.anyasoft.es.surveyapp.internet;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.anyasoft.es.surveyapp.ESurvey;

/**
 * Created by saurabh.singh on 4/7/2016.
 */
public class VolleySingleton {
    private static VolleySingleton volleySingleton = null;
    private RequestQueue mRequestQueue =null;

    private VolleySingleton(){
        mRequestQueue = Volley.newRequestQueue(ESurvey.getApplication());
    }//VolleySingleton()

    public static VolleySingleton getInstance(){
        if(null == volleySingleton){
            volleySingleton =  new VolleySingleton();
        }//if()
        return volleySingleton;
    }//getInstance
    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }//getRequestQueue()
}
