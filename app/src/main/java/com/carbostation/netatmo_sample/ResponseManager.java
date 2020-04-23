package com.carbostation.netatmo_sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.carbostation.netatmo_api.NetatmoHttpClient;
import com.carbostation.netatmo_api.model.Station;
import com.carbostation.ui.dashboard.DashboardFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ResponseManager implements Response.Listener<String> {
    private static final String TAG = "ResponseManager";
    private JSONObject _response_json;
    private SampleHttpClient.listener_type _type;
    private Context context;

    public ResponseManager(SampleHttpClient.listener_type type) {
        _response_json = null;
        _type = type;
    }

    public void processResponse(JSONObject response, SampleHttpClient.listener_type type) {
        Log.d(TAG, "Response : " + response.toString());
        if (type == SampleHttpClient.listener_type.LISTENER_LOGIN) {
            SampleHttpClient.getInstance(context).processOAuthResponse(response);
        }
        else if (type == SampleHttpClient.listener_type.LISTENER_GET_PUBLIC_DATA) {
            SampleHttpClient.getInstance(context).processGetPublicDataResponse(response);
        }
        else if (type == SampleHttpClient.listener_type.LISTENER_GET_STATIONS_DATA) {
            SampleHttpClient.getInstance(context).processGetStationsDataResponse(response);
        }
    }

    /**
     * Called when a response is received.
     *
     * @param response
     */
    @Override
    public void onResponse(String response) {
        try {
            _response_json = new JSONObject(response);
            processResponse(_response_json, _type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void displayResponse() {
        Log.d(TAG, _response_json.toString());
    }
}
