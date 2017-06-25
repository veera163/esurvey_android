package com.anyasoft.es.surveyapp.details;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.anyasoft.es.surveyapp.ESurvey;
import com.anyasoft.es.surveyapp.R;
import com.anyasoft.es.surveyapp.internet.JSONConverter;
import com.anyasoft.es.surveyapp.internet.VolleySingleton;
import com.anyasoft.es.surveyapp.location.GPSTracker;
import com.anyasoft.es.surveyapp.logger.L;
import com.anyasoft.es.surveyapp.question.QuestionModel;
import com.anyasoft.es.surveyapp.question.SurveyActivity;
import com.anyasoft.es.surveyapp.question.SurveyAllActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class PersonDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_LOCATION_SETTING = 101;
    private static final int MY_SOCKET_TIMEOUT_MS = 60*1000*2 ;
    private Button btnReset, btnSubmit;
    private TextInputLayout txtInputName;
    private TextInputLayout txtInputAge;
    private TextInputLayout txtInputCast;
    private TextInputLayout txtInputCorpName;
    private TextInputLayout txtInputWardNum;
    private TextInputLayout txtInputWardName;
    private TextInputLayout txtInputReligion;
    private TextInputLayout txtInputSubcaste;
    private TextInputLayout txtInputProfession;
    private TextInputLayout txtInputPlace;
    private TextInputLayout txtInputNumOfChildren;
    private AppCompatSpinner spnGender;
    private RequestQueue queue;
    private GPSTracker gps;
    private Location location;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtInputAge = (TextInputLayout) findViewById(R.id.textLayoutAge);
        txtInputCast = (TextInputLayout) findViewById(R.id.textLayoutCaste);
        txtInputName = (TextInputLayout) findViewById(R.id.textLayoutName);
        txtInputCorpName = (TextInputLayout) findViewById(R.id.textLayoutMunicipality);
        txtInputWardName = (TextInputLayout) findViewById(R.id.textLayoutWardName);
        txtInputWardNum = (TextInputLayout) findViewById(R.id.textLayoutWardNum);
        txtInputReligion = (TextInputLayout) findViewById(R.id.textLayoutReligion);
        txtInputSubcaste = (TextInputLayout) findViewById(R.id.textLayoutSubcaste);
        txtInputProfession = (TextInputLayout) findViewById(R.id.textLayoutprofession);
        txtInputPlace = (TextInputLayout) findViewById(R.id.textLayoutplace);
        txtInputNumOfChildren = (TextInputLayout) findViewById(R.id.textLayoutNumOfChildren);
        spnGender = (AppCompatSpinner) findViewById(R.id.spnGender);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnReset = (Button) findViewById(R.id.btnReset);
        btnSubmit.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        queue = VolleySingleton.getInstance().getRequestQueue();


    }

    public void showSettingsAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
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

    private void getLoaction() {
        if(null ==  gps){
            gps = new GPSTracker(this);
        }//

        if (!gps.canGetLocation()) {
            showSettingsAlert();
        }//
        else {
            L.d("Gettting loaction");
            location = gps.getLocation();
            if (null != location) {
                gps.stopUsingGPS();
                L.d("Latitude" + location.getLatitude());
                L.d("Longitude" + location.getLongitude());
            }//

        }//
    }//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION_SETTING) {
            Toast.makeText(this, "You turned on Location.", Toast.LENGTH_SHORT).show();

        }//if()....
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(null != gps) {
            location = gps.getLocation();
            if (location != null) {
                gps.stopUsingGPS();

                Toast.makeText(this, "Got the location", Toast.LENGTH_SHORT).show();
                L.d("Latitude" + location.getLatitude());
                L.d("Longitude" + location.getLongitude());
            }//if()....
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnReset) {
            txtInputName.getEditText().setText("");
            txtInputCast.getEditText().setText("");
            txtInputAge.getEditText().setText("");
            txtInputNumOfChildren.getEditText().setText("");
            txtInputWardName.getEditText().setText("");
            txtInputWardName.getEditText().toString();
            txtInputCorpName.getEditText().setText("");

            txtInputWardNum.getEditText().setText("");

            // String txtInputWardName.getEditText().getText().toString();;
            txtInputReligion.getEditText().setText("");

            txtInputSubcaste.getEditText().setText("");

            txtInputProfession.getEditText().setText("");

            txtInputPlace.getEditText().setText("");

            txtInputNumOfChildren.getEditText().setText("");

            spnGender.setSelection(0);
        }//
        if (v == btnSubmit) {
            sendRequest();
        }//

    }//

    private void sendRequest() {
        String name = txtInputName.getEditText().getText().toString();
        String cast = txtInputCast.getEditText().getText().toString();
        String age = txtInputAge.getEditText().getText().toString();
        String wardName = txtInputWardName.getEditText().toString();
        String corpName = txtInputCorpName.getEditText().getText().toString();

        String wardNum = txtInputWardNum.getEditText().getText().toString();

        // String txtInputWardName.getEditText().getText().toString();;
        String religion = txtInputReligion.getEditText().getText().toString();

        String subCaste = txtInputSubcaste.getEditText().getText().toString();

        String profession = txtInputProfession.getEditText().getText().toString();

        String place = txtInputPlace.getEditText().getText().toString();

        String numOfChildren = txtInputNumOfChildren.getEditText().getText().toString();

        String gender = spnGender.getSelectedItem().toString();
        if (name == null || name.equals("")) {
            txtInputName.setError("Can't be left blank");
            return;
        }//
        if (cast == null || cast.equals("")) {
            txtInputCast.setError("Can't be left blank");
            return;
        }//
        if (age == null || age.equals("")) {
            txtInputCast.setError("Can't be left blank");
            return;
        }//


       pd = new ProgressDialog(this);
        pd.setMessage("Uploading File to Server");
        pd.setCancelable(false);
        String url = "http://anyasoftindia.com/postPeopleResponse";
        JSONObject resultJson = null;
        try {
            resultJson = JSONConverter.createJSON();
            if (location != null) {
                resultJson.put("latlong", location.getLatitude() + "," + location.getLongitude());
                //resultJson.put("Latitude", location.getLatitude());
            }//
            else {
                resultJson.put("latlong", "NA");

            }//
            resultJson.put("name", name);
            resultJson.put("age", age);
            resultJson.put("cast", cast);
            resultJson.put("WardName", wardName);
            resultJson.put("WardNum", wardNum);
            resultJson.put("CorpName", corpName);
            resultJson.put("Place", place);
            resultJson.put("Profession", profession);
            resultJson.put("MumOfChildren", numOfChildren);
            resultJson.put("SubCaste", subCaste);
            resultJson.put("religion", religion);
            resultJson.put("status", "Completed");


        } catch (Exception e) {
            L.e("Error in sending file " + e.getMessage());
            return;
        }//exception

                L.d("length of file while uploading to server " + resultJson.length());


        // Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, resultJson,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
//                                mTxtDisplay.setText("Response: " + response.toString());
                        try {
                            String error = response.getString("error");
                            String message = response.getString("message");
                            L.d(error + " ---" + message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(PersonDetailsActivity.this, "Upload Successfull", Toast.LENGTH_SHORT).show();

                        QuestionModel.questionList.clear();
                        makePostGeoRequest();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                error.printStackTrace();
                L.e(error + "");
                L.e(error.getLocalizedMessage()+"");
                pd.dismiss();
                launchDialog("That didn't work! Upload fails");
            }
        });

        // Add the request to the RequestQueue.
       stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
        pd.show();
    }//
    private void makePostGeoRequest(){
        try {
            String url = "http://anyasoftindia.com/postGeoLocation";
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("status", "Completed");
            jsonRequest.put("surveyor" , ESurvey.userId);
            if(location != null){
                jsonRequest.put("latlong",location.getLatitude()+","+location.getLongitude());
            }//
            else{

            }
            //
            L.d("JSON::"+ jsonRequest.toString());

            // Request a string response from the provided URL.
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url,jsonRequest,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
//

                            if(null != response){
                                try {
                                    L.d(response.toString());

                                    JSONObject json =  new JSONObject(response.toString());
                                    if(json.has("error")){
                                        L.d(json.getString("error"));
                                    }//

                                } catch (JSONException e) {
                                    L.e(e.getMessage() + " From postGeoLocation");
                                    pd.dismiss();
                                    launchDialog("Sorry . It seems something is wrong in server." +
                                            " Please try again later");
                                    return;
                                }//catch()
                                catch(IllegalArgumentException e){
                                    L.e(e.getLocalizedMessage() +" From postGeoLocation");
                                    pd.dismiss();
                                    launchDialog("Sorry . It seems something is wrong in server." +
                                            " Please try again later");
                                    return;
                                }//catch()
                            }
                            pd.dismiss();
                            finish();



                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    L.e(error + "  From postGeoLocation");

                    launchDialog("Server can't be reached . Please check your internet connection." +
                            " Please try again later");
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
    private void launchDialog(String msg){

        final android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertBuilder.setTitle("ALERT!!");
        alertBuilder.setMessage(msg);

        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });
        android.support.v7.app.AlertDialog alert = alertBuilder.create();
        alert.show();
    }

}//



