package com.carbostation.netatmo_sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


import com.android.volley.Response;
import com.carbostation.R;

import com.carbostation.netatmo_api.HTTPClient;
import com.carbostation.netatmo_api.NetatmoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * This is just an example of how you can extend HTTPClient.
 * Tokens are stored in the shared preferences of the app, but you can store them as you wish
 * as long as they are properly returned by the getters.
 * If you want to add your own '/getmeasure' requests, this is also the place to do it.
 */
public class NetatmoHTTPClient extends HTTPClient {

    private static final String TAG = "NetatmoHTTPClient";

    private static final String REQ_TAG             = "NetatmoAPI";
    private static final String REQ_REFRESH         = "[REFRESH]";
    private static final String REQ_AUTHORIZATION   = "[AUTHORIZATION]";
    private static final String REQ_PUBLIC_DATA     = "[GET_PUBLIC_DATA]";
    private static final String REQ_STATION_DATA    = "[GET_STATION_DATA]";

    private static final int REFRESH_RATE_MIN    = 2;
    private static final int MIN_IN_SEC          = 60;
    private static final int REFRESH_RATE_SEC    = REFRESH_RATE_MIN * MIN_IN_SEC;

    Context context;

    private SharedPreferences _shared_preferences;
    private JSONObject obj;

    private String _get_stations_last_response = null;
    private String _tokens_last_response       = null;

    private static NetatmoHTTPClient INSTANCE = null;

    private NetatmoHTTPClient(Context context) {
        super(context);
        this.context = context;
        _shared_preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static synchronized NetatmoHTTPClient getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new NetatmoHTTPClient(context);
        }
        return(INSTANCE);
    }

    /**
     * Once an access token has been obtained, it can be used immediately to access the REST API.
     * After a certain amount of time, the access token expires
     * and the application needs to use the refresh token to renew the access token.
     * Both the refresh token and the expiration time are obtained during the authentication phase.
     *
     * @param refresh_token   The token used to refresh the API access token.
     * @param listener        The response listener.
     */
    public void refreshToken(String refresh_token, final Response.Listener<String> listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", refresh_token);
        params.put("client_id", getClientId());
        params.put("client_secret", getClientSecret());

        Log.i(REQ_TAG, REQ_REFRESH);
        POST(
            NetatmoUtils.URL_OAUTH_REQUEST_TOKEN,
            params,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    _tokens_last_response = response;
                    listener.onResponse(response);
                }
            },
            null);
    }

    public void requestAccessToken(String code, Response.Listener<String> listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", getClientId());
        params.put("client_secret", getClientSecret());
        params.put("code", code);
        params.put("scope", getAppScope());
        params.put("redirect_uri", "http://www.carbostation.io/auth");

        Log.i(REQ_TAG, REQ_AUTHORIZATION);
        POST(NetatmoUtils.URL_OAUTH_REQUEST_TOKEN, params, listener, null);
    }

    //-- Netatmo API response listeners --------------------------------------
    public void getPublicData(
            int lat_ne, int lon_ne, int lat_sw, int lon_sw, String required_data) {
        HashMap<String,String> params = new HashMap<>();
        params.put("access_token", getAccessToken());
        params.put("lat_ne", String.valueOf(lat_ne));
        params.put("lon_ne", String.valueOf(lon_ne));
        params.put("lat_sw", String.valueOf(lat_sw));
        params.put("lon_sw", String.valueOf(lon_sw));
        params.put("required_data", required_data);
        params.put("filter", "true");

        Log.i(REQ_TAG, REQ_PUBLIC_DATA);
        GET(
            NetatmoUtils.URL_API_GET_PUBLIC_DATA,
            params,
            null,
            null
        );
    }

    /**
     * Send Netatmo API a GET request to retrieve stations data.
     * Check the last request time. If it is less than a specified time,
     * simply return the last response.
     *
     * @param device_id      The weather station MAC address.
     * @param listener       The response listener.
     * @param bypass_timer   A flag that bypasses the timing check before sending request.
     */
    public void getStationsData(String device_id, final Response.Listener<String> listener, boolean bypass_timer) {
        if (checkLastGetStationsReponse() && !bypass_timer) {
            listener.onResponse(_get_stations_last_response);
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put(NetatmoUtils.KEY_ACCESS_TOKEN, _shared_preferences.getString(NetatmoUtils.KEY_ACCESS_TOKEN, null));
            params.put(NetatmoUtils.KEY_DEVICE_ID, device_id);

            Log.i(REQ_TAG, REQ_STATION_DATA);
            GET(
                NetatmoUtils.URL_API_GET_STATIONS_DATA,
                params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        _get_stations_last_response = response;
                        listener.onResponse(response);
                    }
                },
                null
            );
        }
    }

    /**
     * Function that checks last response.
     *
     * @return true   if last reponse exists and has been stored in the last REFRESH_RATE_SEC min.
     *         false  otherwise.
     */
    private boolean checkLastGetStationsReponse() {
        if (_get_stations_last_response == null) {
            return false;
        } else {
            try {
                Long timestamp = Long.valueOf(
                    NetatmoUtils.getJSONString(
                        new JSONObject(_get_stations_last_response), NetatmoUtils.KEY_TIME_SERVER
                    )
                );
                return (System.currentTimeMillis() < ((timestamp + REFRESH_RATE_SEC) * 1000));
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    //-- Netatmo API credentials information ---------------------------------
    private String getClientId() { return context.getString(R.string.client_id); }

    private String getClientSecret() { return context.getString(R.string.client_secret); }

    private String getAppScope() { return context.getString(R.string.app_scope); }

    protected void storeTokens(String refresh_token, String access_token, long expiresAt) {
        SharedPreferences.Editor editor = _shared_preferences.edit();
        editor.putString(NetatmoUtils.KEY_REFRESH_TOKEN, refresh_token);
        editor.putString(NetatmoUtils.KEY_ACCESS_TOKEN, access_token);
        editor.putLong(NetatmoUtils.KEY_EXPIRES_AT, expiresAt);
        editor.apply();
    }

    private void clearTokens() {
        SharedPreferences.Editor editor = _shared_preferences.edit();
        editor.clear();
        editor.apply();
    }

    private String getRefreshToken() {
        return _shared_preferences.getString(NetatmoUtils.KEY_REFRESH_TOKEN, null);
    }

    private String getAccessToken() {
        return _shared_preferences.getString(NetatmoUtils.KEY_ACCESS_TOKEN,null);
    }

    private long getExpiresAt() {
        return _shared_preferences.getLong(NetatmoUtils.KEY_EXPIRES_AT,0);
    }
}
