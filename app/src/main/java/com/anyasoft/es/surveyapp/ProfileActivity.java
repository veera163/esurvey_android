package com.anyasoft.es.surveyapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.anyasoft.es.surveyapp.adapter.ProfileCategAdapter;
import com.anyasoft.es.surveyapp.domains.ServierActivities;
import com.anyasoft.es.surveyapp.domains.UserDomain;
import com.anyasoft.es.surveyapp.internet.NetworkUtil;
import com.anyasoft.es.surveyapp.internet.VolleySingleton;
import com.anyasoft.es.surveyapp.location.GPSTracker;
import com.anyasoft.es.surveyapp.logger.L;
import com.anyasoft.es.surveyapp.realm.models.SurveyResponse;
import com.anyasoft.es.surveyapp.services.PostLocationServices;
import com.anyasoft.es.surveyapp.services.SyncAllSurveyServices;
import com.anyasoft.es.surveyapp.survey.SurveyManager;
import com.anyasoft.es.surveyapp.utility.AppConstant;
import com.anyasoft.es.surveyapp.utility.HttpHelper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_LOCATION_SETTING = 101;
    CardView cardStartSurvey;
    CardView cardSyncAllData;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menus, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                gotoLoginActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void gotoLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    DashBoardCalls dashBoardCalls;
    RecyclerView rec_plans;
    ViewPager rec_categ;
    private CardView cardStartSurveyAll;
    private static final int MY_SOCKET_TIMEOUT_MS = 60 * 1000 * 3;
    RequestQueue queue;
    private Location location;
    private GPSTracker gps;
    boolean isAlltime = true;
    private ProgressDialog pd;
    private Intent intent;
    private boolean isServiceStarted;
    private SurveyManager surveyManager;
    private RealmResults<SurveyResponse> allSurvey;
    private TextView txtCountSurvey;
    private int count = 0;
    private String GETUSER = "getUser";
    private String GETACTIVITIES = "getActivities";
    private UserDomain user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dashBoardCalls = new DashBoardCalls();
        rec_plans = (RecyclerView) findViewById(R.id.rec_plans);
        rec_categ = (ViewPager) findViewById(R.id.rec_categ);
        cardStartSurvey = (CardView) findViewById(R.id.card_view_back);
        cardSyncAllData = (CardView) findViewById(R.id.sync_data);
        txtCountSurvey = (TextView) findViewById(R.id.title_back_all_sync);
        rec_plans.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //rec_categ.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        cardStartSurvey.setOnClickListener(this);
        cardSyncAllData.setOnClickListener(this);
        cardStartSurveyAll = (CardView) findViewById(R.id.card_all_quest);
        cardStartSurveyAll.setOnClickListener(this);
        queue = VolleySingleton.getInstance().getRequestQueue();
        isAlltime = true;
        intent = new Intent(this, PostLocationServices.class);
        surveyManager = new SurveyManager(new WeakReference<Context>(this));
        dashBoardCalls.execute(GETUSER);
        //Snackbar.make(cardStartSurvey ,getLocaleLanguage()+"",Snackbar.LENGTH_SHORT ).show();
    }

    //
    public String getLocaleLanguage() {
        Configuration config = getBaseContext().getResources().getConfiguration();
        return config.locale.getLanguage();
    }//


    @Override
    protected void onResume() {
        super.onResume();
        countPendingSurvey();
        if (NetworkUtil.isOnline(this)) {
            if (location == null)
                getLocation();
        }//

    }//

    @Override
    public void onClick(View v) {
        if (v == cardStartSurvey) {
            isAlltime = false;
            getLocation();

        }//if()
        if (v == cardStartSurveyAll) {
            isAlltime = true;
            if (null != surveyManager) {
                surveyManager.getSurveySet();
            }//
            if (NetworkUtil.isOnline(this)) {
                makePostGeoRequest();
            }//
        }//if()
        if (v == cardSyncAllData) {
            if (NetworkUtil.isOnline(this)) {
                if (count == 0) {
                    Snackbar.make(v, getString(R.string.no_survey_to_sync), Snackbar.LENGTH_SHORT).show();
                    return;
                }//count
                startService(new Intent(this, SyncAllSurveyServices.class));
            }//
            else {
                Snackbar.make(v, getString(R.string.no_intenet_to_sync), Snackbar.LENGTH_SHORT).show();
            }//
        }//if()
    }

    public void showSettingsAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.gps_setting));

        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.gps_prompt));

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton(getString(R.string.setting), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_LOCATION_SETTING);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void getLocation() {
        if (gps == null) {
            gps = new GPSTracker(this);
        }//if


        if (!gps.canGetLocation()) {
            showSettingsAlert();
        }//
        else {
            if (!isServiceStarted) {
                startService(intent);
                isServiceStarted = true;
                L.d("GetLocation():: Service Created from Activity");
            }


            location = gps.getLocation();
            if (null != location) {
                gps.stopUsingGPS();
                L.d("LatLong" + location.getLongitude() + "," + location.getLatitude());
            }//

        }//
    }//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }//

    @Override
    protected void onRestart() {
        super.onRestart();
        if (gps == null) {
            return;
        }
        location = gps.getLocation();
        if (location != null) {
            gps.stopUsingGPS();
//            sendRequest();

        }//if()....

    }//

    private void makePostGeoRequest() {
        try {
            String url = ESurvey.URL + "/postGeoLocation";
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("status", "started");
            jsonRequest.put("badge_color", "red");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            Date date = new Date();
            String currentDate = dateFormat.format(date);

            jsonRequest.put("timestamp", currentDate);
            L.d("ProfileActivity ::makePostGeoLocation()" + currentDate);
            jsonRequest.put("type", "survey");
            jsonRequest.put("surveyor", ESurvey.userId);
            if (location != null) {
                jsonRequest.put("latlong", location.getLatitude() + "," + location.getLongitude());
            }//
            else {

            }
            //
            L.d("makePostGeoRequest():::JSON::" + jsonRequest.toString());

            // Request a string response from the provided URL.
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRequest,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
//
                            L.d(",ProfileActivity::makePostGeoLocation():: Success");
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    L.e(",ProfileActivity::makePostGeoLocation():: Error");
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }//
        catch (Exception ex) {
            L.e("Error " + ex.getMessage());
            pd.dismiss();
            return;
        }//
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
        if (surveyManager != null) {

            surveyManager.destroySurveyManager();
        }//if()..

        surveyManager = null;
    }

    public void countPendingSurvey() {
        Realm realm = Realm.getDefaultInstance();
//        allSurvey = realm.where(SurveyResponse.class)
//                .findAllAsync();
        allSurvey = realm.where(SurveyResponse.class)
                .equalTo("isSynced", false).findAllAsync();
//
        allSurvey.addChangeListener(realmChangeListener);
    }//removeCahedDataFromDataBase()...

    RealmChangeListener realmChangeListener = new RealmChangeListener() {
        @Override
        public void onChange() {
            count = allSurvey.size();
            L.d("RealmChangeListener::onChange:: size = " + count);
            if (count > 0) {
                txtCountSurvey.setText(count + getString(R.string.survey_to_sync));
            }//if()...
            else {
                txtCountSurvey.setText(R.string.no_survey_to_sync);
            }//else...
        }//onChange()...
    };//

    @Override
    protected void onPause() {
        super.onPause();
        allSurvey.removeChangeListener(realmChangeListener);
    }

    private class DashBoardCalls extends AsyncTask<String, Void, ServierActivities> {

        String currentMethod;
        Gson gson = new Gson();

        @Override
        protected ServierActivities doInBackground(String... params) {
            String s = params[0];
            currentMethod = s;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(AppConstant.BASEURL);
            stringBuilder.append(AppConstant.USERPATH);
            stringBuilder.append(ESurvey.userId);
            ESurvey.setUser(HttpHelper.sendGETRequest(stringBuilder.toString(), ESurvey.getAccessToken()));
            ProfileActivity.this.user = ESurvey.getUser();
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append(AppConstant.BASEURL);
            stringBuilder1.append(AppConstant.ACTIVITIESPATH);
            return gson.fromJson(HttpHelper.sendPOSTRequest(stringBuilder1.toString(), getParams(), ESurvey.getAccessToken()), ServierActivities.class);
        }

        private String getParams() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("surveyorPhone", "9949771559");
                //jsonObject.put("surveyorPhone", user.getPhoneNumber());
                //jsonObject.put("startDate", "01/06/2017");
                //jsonObject.put("endDate", "05/06/2017");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject.toString();
        }

        @Override
        protected void onPostExecute(ServierActivities servierActivities) {
           /* if (currentMethod.equalsIgnoreCase(GETUSER)) {
                String s1 = "{\n" +
                        "  \"statusCounts\": [\n" +
                        "    {\n" +
                        "      \"status\": \"Planned\",\n" +
                        "      \"total\": 1\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"surveyCriteria\": [\n" +
                        "    {\n" +
                        "      \"category\": \"OC\",\n" +
                        "      \"noOfSurveys\": \"30\",\n" +
                        "      \"noOfCompleted\": \"10\",\n" +
                        "      \"noOfPending\": \"20\",\n" +
                        "      \"noOfOnHold\": \"10\",\n" +
                        "      \"noOfCanceled\": \"10\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"category\": \"Muslim\",\n" +
                        "      \"noOfSurveys\": \"170\",\n" +
                        "      \"noOfCompleted\": \"10\",\n" +
                        "      \"noOfPending\": \"10\",\n" +
                        "      \"noOfOnHold\": \"10\",\n" +
                        "      \"noOfCanceled\": \"10\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}\n";*/
            //dashBoardCalls.execute(GETACTIVITIES);
            if (servierActivities != null) {
                //rec_plans.setAdapter(new ProfilePlansAdapter(servierActivities.getStatusCounts()));
                rec_categ.setAdapter(new ProfileCategAdapter(getSupportFragmentManager(), servierActivities.getSurveyCriteria()));
                ESurvey.setSurveyActivityId(servierActivities.getSurveyActivityId());
            }


        }
    }
}

