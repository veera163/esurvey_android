package com.anyasoft.es.surveyapp.internet;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.NetworkResponse;

import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.anyasoft.es.surveyapp.logger.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by saurabh.singh on 6/8/2016.
 */
public class EncodedJSONRequest extends JsonObjectRequest {


    public EncodedJSONRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }//

    @Override
    public Priority getPriority() {
        return Priority.IMMEDIATE;
    }
}
