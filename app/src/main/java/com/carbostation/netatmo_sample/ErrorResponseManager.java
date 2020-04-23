package com.carbostation.netatmo_sample;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

public class ErrorResponseManager implements Response.ErrorListener {
    private static final String TAG = "ErrorResponseManager";
    private JSONObject _response_json;
    private SampleHttpClient.listener_type _type;

    public ErrorResponseManager(SampleHttpClient.listener_type type) {
        _response_json = null;
        _type = type;
    }

    /**
     * Callback method that an error has been occurred with the provided error code and optional
     * user-readable message.
     *
     * @param error
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        Log.v(TAG, "Error: " + error.toString());
    }
}
