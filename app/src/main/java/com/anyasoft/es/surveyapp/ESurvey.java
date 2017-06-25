package com.anyasoft.es.surveyapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.anyasoft.es.surveyapp.domains.UserDomain;
import com.anyasoft.es.surveyapp.logger.L;
import com.google.gson.Gson;

import java.io.File;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class ESurvey extends Application {
    private static final String DIR_NAME = "eSurvey";
    public static final String FILE_NAME = "conversation.mp3";
    public static final String FILE_NAME_PHOTO = "JPEG_Temp";
    public static final String DIR_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/" + DIR_NAME;
    public static final boolean IS_DEBUG = true;
    private static final String LANGUAGE = "LANGUAGE";
    private static ESurvey mInstance;
    public static String userId = "";
    public static final String URL = "http://34.195.106.0";
    static SharedPreferences preference;
    static SharedPreferences.Editor editor;
    private static String ACCESSTOKEN = "accesstoken";
    private static String REFRESHTOKEN = "refreshtoken";
    private static String USERINFO = "userInfo";
    private static Gson gson = new Gson();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        preference = this.getSharedPreferences("Esurvey", 0);
        editor = preference.edit();
        File file = new File(DIR_PATH);
        if (!file.exists()) {
            file.mkdir();
        }//if()
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfig);
        //changeLang("te");

    }//onCreate()

    public static void setAccessToken(String accessToken, String refreshToken) {
        editor.putString(ACCESSTOKEN, accessToken);
        editor.putString(REFRESHTOKEN, refreshToken);
        editor.commit();
    }

    public static String getAccessToken() {
        return preference.getString(ACCESSTOKEN, "NA");
    }

    public static UserDomain getUser() {
        return gson.fromJson(preference.getString(USERINFO, "NA"), UserDomain.class);
    }

    public static void setUser(String user) {
        editor.putString(USERINFO, user);
        editor.commit();
    }

    public static ESurvey getmInstance() {
        return mInstance;

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        L.d("Application Terminated");
    }

    public void changeLang(String lang) {
        Configuration config = getBaseContext().getResources().getConfiguration();
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this).edit();
            ed.putString(LANGUAGE, lang);
            ed.commit();
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration conf = new Configuration(config);
            conf.locale = locale;
            getBaseContext().getResources().updateConfiguration(conf, getBaseContext().getResources()
                    .getDisplayMetrics());
        }
    }

    public String getLang() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString(LANGUAGE, "te");
    }

    public static Context getApplication() {
        return mInstance.getApplicationContext();
    }

}//ESurvey
