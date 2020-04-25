package com.carbostation.netatmo_sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.carbostation.R;

import com.carbostation.netatmo_api.NetatmoHttpClient;
import com.carbostation.netatmo_api.NetatmoUtils;
import com.carbostation.netatmo_api.model.Measures;
import com.carbostation.netatmo_api.model.Params;
import com.carbostation.netatmo_api.model.Station;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * This is just an example of how you can extend NetatmoHttpClient.
 * Tokens are stored in the shared preferences of the app, but you can store them as you wish
 * as long as they are properly returned by the getters.
 * If you want to add your own '/getmeasure' requests, this is also the place to do it.
 */
public class SampleHttpClient extends NetatmoHttpClient {

    private static final String TAG = "SampleHttpClient";
    Context context;

    SharedPreferences mSharedPreferences;
    public ResponseManager _listener_login;
    public ErrorResponseManager _error_listener_login;
    public ResponseManager _listener_get_public_data;
    public ErrorResponseManager _error_listener_get_public_data;
    public ResponseManager _listener_get_stations_data;
    public ErrorResponseManager _error_listener_get_stations_data;
    private JSONObject obj;

    public enum listener_type {
        LISTENER_LOGIN,
        LISTENER_GET_PUBLIC_DATA,
        LISTENER_GET_STATIONS_DATA,
    }

    private static SampleHttpClient INSTANCE = null;

    private SampleHttpClient(Context context) {
        super(context);
        this.context = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        _listener_login                   = new ResponseManager(listener_type.LISTENER_LOGIN);
        _error_listener_login             = new ErrorResponseManager(listener_type.LISTENER_LOGIN);
        _listener_get_public_data         = new ResponseManager(listener_type.LISTENER_GET_PUBLIC_DATA);
        _error_listener_get_public_data   = new ErrorResponseManager(listener_type.LISTENER_GET_PUBLIC_DATA);
        _listener_get_stations_data       = new ResponseManager(listener_type.LISTENER_GET_STATIONS_DATA);
        _error_listener_get_stations_data = new ErrorResponseManager(listener_type.LISTENER_GET_STATIONS_DATA);
    };

    public static synchronized SampleHttpClient getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SampleHttpClient(context);
        }
        return(INSTANCE);
    }

    public void login(String email, String password) {
        login(
            email, password,
            _listener_login,
            _error_listener_login
        );
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
    public void processGetPublicDataResponse(JSONObject response){
        //HashMap<String,String> parsedResponse = NetatmoUtils.parseMeasures(response);
        //storeTokens(parsedResponse.get(NetatmoUtils.KEY_REFRESH_TOKEN),
        //        parsedResponse.get(NetatmoUtils.KEY_ACCESS_TOKEN),
        //        Long.valueOf(parsedResponse.get(NetatmoUtils.KEY_EXPIRES_AT)));
    }
    public void processGetStationsDataResponse(JSONObject response){
        List<Station> parsedDevicesList = NetatmoUtils.parseDevicesList(response);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
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

        post(
            URL_GET_PUBLIC_DATA,
            params,
            _listener_get_public_data,
            _error_listener_get_public_data
        );
    }

    public void getStationsData(String device_id, Response.Listener<String> listener) {
        HashMap<String,String> params = new HashMap<>();
        params.put("access_token", getAccessToken());
        params.put("device_id", device_id);

        post(
            URL_GET_STATIONS_DATA,
            params,
            listener,
            _error_listener_get_stations_data
        );
    }

    //-- Netatmo API response listeners --------------------------------------
    public ResponseManager getListener(listener_type type) {
        if (type == listener_type.LISTENER_LOGIN) {
            return _listener_login;
        } else if (type == listener_type.LISTENER_GET_PUBLIC_DATA) {
            return _listener_get_public_data;
        } else {
            return null;
        }
    }

    public ErrorResponseManager getErrorListener(listener_type type) {
        if (type == listener_type.LISTENER_LOGIN) return _error_listener_login;
        else if (type == listener_type.LISTENER_GET_PUBLIC_DATA) return _error_listener_get_public_data;
        else return null;
    }

    //-- Netatmo API credentials information ---------------------------------
    @Override
    protected String getClientId() {
        return context.getString(R.string.client_id);
    }

    @Override
    protected String getClientSecret() {
        return context.getString(R.string.client_secret);
    }

    @Override
    protected String getAppScope() {
        return context.getString(R.string.app_scope);
    }

    @Override
    protected void storeTokens(String refreshToken, String accessToken, long expiresAt) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(NetatmoUtils.KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(NetatmoUtils.KEY_ACCESS_TOKEN, accessToken);
        editor.putLong(NetatmoUtils.KEY_EXPIRES_AT, expiresAt);
        editor.apply();
    }

    @Override
    protected void clearTokens() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    protected String getRefreshToken() {
        return mSharedPreferences.getString(NetatmoUtils.KEY_REFRESH_TOKEN, null);
    }

    @Override
    protected String getAccessToken() {
        return mSharedPreferences.getString(NetatmoUtils.KEY_ACCESS_TOKEN,null);
    }

    @Override
    protected long getExpiresAt() {
        Log.d("TAG", "EXPIRRR");
        return mSharedPreferences.getLong(NetatmoUtils.KEY_EXPIRES_AT,0);
    }
}
