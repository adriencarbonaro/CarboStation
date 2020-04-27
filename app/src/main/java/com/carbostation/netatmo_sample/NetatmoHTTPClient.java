package com.carbostation.netatmo_sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

import com.android.volley.Response;
import com.carbostation.R;

import com.carbostation.netatmo_api.HTTPClient;
import com.carbostation.netatmo_api.NetatmoUtils;
import com.carbostation.netatmo_api.model.Station;

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
    Context context;

    private SharedPreferences _shared_preferences;
    private JSONObject obj;

    private static NetatmoHTTPClient INSTANCE = null;

    private NetatmoHTTPClient(Context context) {
        super(context);
        this.context = context;
        _shared_preferences = PreferenceManager.getDefaultSharedPreferences(context);
    };

    public static synchronized NetatmoHTTPClient getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new NetatmoHTTPClient(context);
        }
        return(INSTANCE);
    }

    public void refreshToken(String refresh_token, Response.Listener<String> listener) {
        refreshToken(refresh_token, listener, null);
    }

    public void requestAccessToken(String code, Response.Listener<String> listener) {
        requestAccessToken(code, listener, null);
    }
    /**
     * processOAuthResponse.
     * @param response
     */
    public void processOAuthResponse(JSONObject response){
        HashMap<String,String> parsedResponse = NetatmoUtils.parseOAuthResponse(response);
        storeTokens(
                parsedResponse.get(NetatmoUtils.KEY_REFRESH_TOKEN),
                parsedResponse.get(NetatmoUtils.KEY_ACCESS_TOKEN),
                Long.valueOf(parsedResponse.get(NetatmoUtils.KEY_EXPIRES_AT))
        );
    }

    public void processGetStationsDataResponse(JSONObject response){
        List<Station> parsedDevicesList = NetatmoUtils.parseDevicesList(response);
        SharedPreferences.Editor editor = _shared_preferences.edit();
        editor.putString("STATION_NAME", parsedDevicesList.get(0).getName());
        editor.apply();
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

        get(
            NetatmoUtils.URL_API_GET_PUBLIC_DATA,
            params,
            null,
            null
        );
    }

    public void getStationsData(String device_id, final Response.Listener<String> listener) {
        HashMap<String,String> params = new HashMap<>();
        // Replace token by getAccessToken. This is for debug because of the null token error
        params.put(NetatmoUtils.KEY_ACCESS_TOKEN, _shared_preferences.getString(NetatmoUtils.KEY_ACCESS_TOKEN, null));
        params.put(NetatmoUtils.KEY_DEVICE_ID, device_id);

        get(
                NetatmoUtils.URL_API_GET_STATIONS_DATA,
                params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "first I do whatever");
                        listener.onResponse(response);
                    }
                },
                null
        );
    }

    //-- Netatmo API credentials information ---------------------------------
    @Override
    protected String getClientId() { return context.getString(R.string.client_id); }

    @Override
    protected String getClientSecret() { return context.getString(R.string.client_secret); }

    @Override
    protected String getAppScope() { return context.getString(R.string.app_scope); }

    @Override
    protected void storeTokens(String refreshToken, String accessToken, long expiresAt) {
        SharedPreferences.Editor editor = _shared_preferences.edit();
        editor.putString(NetatmoUtils.KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(NetatmoUtils.KEY_ACCESS_TOKEN, accessToken);
        editor.putLong(NetatmoUtils.KEY_EXPIRES_AT, expiresAt);
        editor.apply();
    }

    @Override
    protected void clearTokens() {
        SharedPreferences.Editor editor = _shared_preferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    protected String getRefreshToken() {
        return _shared_preferences.getString(NetatmoUtils.KEY_REFRESH_TOKEN, null);
    }

    @Override
    protected String getAccessToken() {
        return _shared_preferences.getString(NetatmoUtils.KEY_ACCESS_TOKEN,null);
    }

    @Override
    protected long getExpiresAt() {
        return _shared_preferences.getLong(NetatmoUtils.KEY_EXPIRES_AT,0);
    }
}
