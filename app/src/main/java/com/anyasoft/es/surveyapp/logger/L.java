package com.anyasoft.es.surveyapp.logger;

import android.util.Log;

import com.anyasoft.es.surveyapp.ESurvey;


public class L {

    private static final String TAG = "ESurveyLog";
    public static void d(String msg){
        if(null == msg){
            msg = "Message is null in this Line of logger.";
        }//
        if(ESurvey.IS_DEBUG){
            Log.d(TAG,msg);
        }//if()
    }
    public static void e(String msg){
        if(null == msg){
            msg = "Error message is null in this Line of logger.";
        }//
        if(ESurvey.IS_DEBUG){
            Log.e(TAG,msg);
        }//if()
    }
}
