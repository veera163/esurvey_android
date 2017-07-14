package com.anyasoft.es.surveyapp.survey;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.anyasoft.es.surveyapp.ESurvey;
import com.anyasoft.es.surveyapp.R;
import com.anyasoft.es.surveyapp.camera.CameraActivity;
import com.anyasoft.es.surveyapp.details.PersonDetailsFragment;
import com.anyasoft.es.surveyapp.internet.NetworkUtil;
import com.anyasoft.es.surveyapp.internet.VolleySingleton;
import com.anyasoft.es.surveyapp.location.GPSTracker;
import com.anyasoft.es.surveyapp.location.LastLocation;
import com.anyasoft.es.surveyapp.logger.L;
import com.anyasoft.es.surveyapp.media.MediaRecorderSingleton;
import com.anyasoft.es.surveyapp.question.QuestionModel;
import com.anyasoft.es.surveyapp.question.Stamps;
import com.anyasoft.es.surveyapp.question.StampsAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakeSurveyActivity extends AppCompatActivity implements
        QuestionFragment.OnQuestionFragmentInteractionListener,
        AnswerFragment.OnFragmentInteractionListener, PersonDetailsFragment.OnFragmentInteractionListener {
    private AnswerFragment answerFragment;
    private QuestionFragment questionFragment;
    private LinearLayout rootView;
    LinearLayout personaldetails_fragment;
    private boolean isDetailsQuestionAnswered = true;
    private static final int REQUEST_LOCATION_SETTING = 101;
    private static final int MY_SOCKET_TIMEOUT_MS = 60 * 1000 * 3;
    private Chronometer chronometer;
    private MediaRecorder mediaRecorder;
    private RecyclerView timeLine = null;
    private long milli = 1000;
    private StampsAdapter stampsAdapter;
    private int position = -1;
    private GPSTracker gps;
    private ProgressDialog pd;
    private Location location;
    private RequestQueue queue;
    private String mCurrentPhotoPath;
    private boolean isPhtotAvialable;
    private Bitmap imageBitmap;
    private FragmentManager fragmentManager;
    private PersonDetailsFragment detailFragment;
    private SurveyResponseManager responseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_survey);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        if (QuestionModel.questionList.size() <= 0) {
            launchErrorDialog();
            return;
        }//
        questionFragment = (QuestionFragment) getSupportFragmentManager()
                .findFragmentById(R.id.quest_fragment);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        timeLine = (RecyclerView) findViewById(R.id.listTimeLine);
        rootView = (LinearLayout) findViewById(R.id.root);
        personaldetails_fragment = (LinearLayout) findViewById(R.id.personaldetails_fragment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        timeLine.setLayoutManager(linearLayoutManager);
        Stamps.clearStamps();
        stampsAdapter = new StampsAdapter();
        timeLine.setAdapter(stampsAdapter);
        fragmentManager = getSupportFragmentManager();
        answerFragment = new AnswerFragment();
        detailFragment = new PersonDetailsFragment();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.personaldetails_fragment, detailFragment, "details");
        ft.commit();

        FragmentTransaction ft1 = fragmentManager.beginTransaction();
        ft1.add(R.id.answer_fragment, answerFragment, "answer");
        ft1.commit();
        queue = VolleySingleton.getInstance().getRequestQueue();

        initRecorder();
    }//

    private void initRecorder() {
        try {
            mediaRecorder = MediaRecorderSingleton.getInstance().getRecorder();
            launchDialog();
        } catch (IOException e) {
            e.printStackTrace();
            L.e(e.getLocalizedMessage());
        }
    }//initRecorder()...

    @Override
    public void setSecondFragment(int position) {
        this.position = position;
        //onMarked();
        /*if (position == QuestionModel.questionList.size() - 1) {
            detailFragment = new PersonDetailsFragment();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.answer_fragment, detailFragment, "details");
            ft.commit();
        }//if()
        else {
        */
        if (!answerFragment.isVisible()) {

//                answerFragment = (AnswerFragment) getSupportFragmentManager()
//                        .findFragmentById(R.id.answer_fragment);
            FragmentTransaction ft = fragmentManager.beginTransaction();
            answerFragment.setPosition(position);
            ft.replace(R.id.answer_fragment, answerFragment, "answer");
            ft.commit();
            return;
        }//
        L.d("Updating fragment");
        answerFragment.updateFragment(position);
    }//else


    //setSecondFragment()...

    @Override
    public void onSubmit() {
        L.d("Submitting the details");
        if (isDetailsQuestionAnswered) {

            if (NetworkUtil.isOnline(this)) {
                if (!getLocation()) {
                    return;
                }//if()...
            }//if()..
            else {
                if (null == location) {
                    location = LastLocation.getLastLocation(this);
                }//if()...
            }//else
            if (null == location) {
                QuestionModel.latLong = "NA";
            }//
            else {
                QuestionModel.latLong = location.getLatitude() + "," + location.getLongitude();
            }
            L.d("onSubmit():: latlong" + QuestionModel.latLong);

            launchSubmitAlertDialog();
        }//
        else {
            Snackbar.make(rootView, getString(R.string.last_quest_prompt), Snackbar.LENGTH_SHORT).show();
        }//

    }//onSubmit()

    @Override
    public void onMarked(boolean marked) {
        if (questionFragment == null) {
            L.d("Fragment is null");
            return;
        }//
        if (position == QuestionModel.questionList.size() - 1) {
            isDetailsQuestionAnswered = true;
            if (!answerFragment.isVisible()) {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.answer_fragment, answerFragment, "answer");
                ft.commit();
            }//
        }//
        QuestionModel.questionList.get(position).setBookmark(milli);
        Stamps stamp = new Stamps();
        stamp.setNum((position + 1) + "");
        stamp.setStampText(chronometer.getText().toString() + "");
        Stamps.stampList.add(stamp);
        stampsAdapter.notifyDataSetChanged();
        L.d(QuestionModel.questionList.get(position).getBookmark() + "------------from activity");

        milli = SystemClock.elapsedRealtime() - chronometer.getBase();

        questionFragment.updateAdapter();
       /* if (marked)
            questionFragment.clickOnNextQuestion(position + 1);*/
    }

    @Override
    public void gotoBack() {
        personaldetails_fragment.setVisibility(View.GONE);
        questionFragment.getView().setVisibility(View.VISIBLE);
        answerFragment.getView().setVisibility(View.VISIBLE);
        detailFragment.getView().setVisibility(View.GONE);
    }

    @Override
    public void enterPersonlaDetails() {
        personaldetails_fragment.setVisibility(View.VISIBLE);
        questionFragment.getView().setVisibility(View.GONE);
        answerFragment.getView().setVisibility(View.GONE);
        detailFragment.getView().setVisibility(View.VISIBLE);
    }

    private void launchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.media_prompt) +
                "There are " + QuestionModel.questionList.size() + " questions.");
        builder.setTitle(getString(R.string.alert));
        builder.setCancelable(false);
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mediaRecorder.start();
                chronometer.start();
            }
        });
        builder.create().show();
    }

    private void launchErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.no_survey_offline);
        builder.setTitle(getString(R.string.alert));
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.gotit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();
    }

    private void launchSubmitAlertDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.submit_dailog, null, false);
        final TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);
        final Button btnSkip = (Button) view.findViewById(R.id.btn_skip);
        final Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        final Button btnCapture = (Button) view.findViewById(R.id.btn_capture);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        txtMessage.setText(R.string.photo_prompt);
        builder.setCancelable(false);
        builder.setView(view);
        final Dialog dialog = builder.create();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }//
        });
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                try {
                    if (mediaRecorder != null) {
                        mediaRecorder.stop();
                    }//
                }//try...
                catch (Exception e) {

                }//catch()....

                sendRequest();
            }//
        });
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dispatchTakePictureIntent();
            }//
        });
        dialog.show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            MediaRecorderSingleton.getInstance().destroyObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//

    private void sendRequest() {
        if (null == responseManager) {
            responseManager = new SurveyResponseManager(new WeakReference<Context>(this));
        }//
        responseManager.storeSurveyResponse();
        makePostGeoRequest();

    }//

    private void makePostGeoRequest() {
        try {
            String url = ESurvey.URL + "/postGeoLocation";
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("status", "Completed");
            jsonRequest.put("badge_color", "red");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            Date date = new Date();
            String currentDate = dateFormat.format(date);

            jsonRequest.put("timestamp", currentDate);
            L.d("PostLocationServices ::makePostGeoLocation()" + currentDate);
            jsonRequest.put("type", "survey");
            jsonRequest.put("surveyor", ESurvey.userId);
            if (location != null) {
                jsonRequest.put("latlong", location.getLatitude() + "," + location.getLongitude());
            }//
            else {

            }
            //
            L.d("JSON::" + jsonRequest.toString());

            // Request a string response from the provided URL.
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRequest,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
//

                            if (null != response) {
                                try {
                                    L.d(response.toString());

                                    JSONObject json = new JSONObject(response.toString());
                                    if (json.has("error")) {
                                        L.d(json.getString("error"));
                                    }//

                                } catch (JSONException e) {
                                    L.e(e.getMessage() + " From postGeoLocation");
                                    return;
                                }//catch()
                                catch (IllegalArgumentException e) {
                                    L.e(e.getLocalizedMessage() + " From postGeoLocation");
                                    return;
                                }//catch()
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    L.e(error + "  From postGeoLocation");

                    //launchDialog(getString(R.string.server_not_reachable) ,getString(R.string.close));
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

    private void launchDialog(String msg, String btnPrompt) {

        final android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertBuilder.setMessage(msg);

        alertBuilder.setNegativeButton(btnPrompt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });
        android.support.v7.app.AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void launchDialogWithPhoto(String msg, Bitmap bit) {

        final android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertBuilder.setMessage(msg);

        alertBuilder.setNegativeButton(R.string.submit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendRequest();
            }//
        });
        android.support.v7.app.AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private boolean getLocation() {
        if (null == gps) {
            gps = new GPSTracker(this);
        }//

        if (!gps.canGetLocation()) {
            showSettingsAlert();
            return false;
        }//
        else {
            L.d("Getting loaction");
            location = gps.getLocation();
            if (null != location) {
                gps.stopUsingGPS();
                L.d("Latitude" + location.getLatitude());
                L.d("Longitude" + location.getLongitude());
            }//
            return true;
        }//
    }// @Override

    protected void onRestart() {
        super.onRestart();
        if (null != gps) {
            location = gps.getLocation();
            if (location != null) {
                gps.stopUsingGPS();

                Toast.makeText(this, "Got the location", Toast.LENGTH_SHORT).show();
                L.d("Latitude" + location.getLatitude());
                L.d("Longitude" + location.getLongitude());
            }//if()....
        }
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

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(this, CameraActivity.class);
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }//dispatchTakePictureIntent()...

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = ESurvey.FILE_NAME_PHOTO;
        File storageDir = new File(ESurvey.DIR_PATH);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_TAKE_PHOTO == requestCode && resultCode == RESULT_OK) {
            isPhtotAvialable = true;

            launchDialogWithPhoto(getString(R.string.capture_photo_prompt), imageBitmap);
        }//
    }//

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != responseManager) {
            responseManager.onStop();
        }//if()....
    }

}//TakeSurveyActivity...
