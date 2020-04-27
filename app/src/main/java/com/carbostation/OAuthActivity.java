package com.carbostation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Response;
import com.carbostation.netatmo_api.NetatmoUtils;
import com.carbostation.netatmo_sample.NetatmoHTTPClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

/**
 * Class OAuthActivity.
 *
 * This class handles authentication flow with API.
 */
public class OAuthActivity extends Activity {

    private static final String TAG = "OAuthActivity";

    /* Redirect URI query fields */
    public static final String URI_QUERY_STATE  = "state";
    public static final String URI_QUERY_CODE   = "code";

    SharedPreferences _shared_preferences;
    private NetatmoHTTPClient http_client;
    private String _state       = null;
    private String _uri         = null;

    /* Listeners */
    Response.Listener<String> OAuthResponseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            OAuthResponseHandler(response);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Load preferences */
        this._shared_preferences = PreferenceManager.getDefaultSharedPreferences(this);

        /* Unique request state string */
        this._state = this._shared_preferences.getString("state_string", null);

        http_client = NetatmoHTTPClient.getInstance(this);

        /* If activity is created because of redirect URI, we handle the redirected URI.
           Else, we begin OAuth2 flow. */
        if (!handleRedirectUri()) {
            OAuth2Flow();
        }
    }

    public void OAuth2Flow() {
        if (checkUserTokens()) {
            http_client.refreshToken(getRefreshToken(), OAuthResponseListener);
        } else
            OAuthAuthorize();
    }

    private void OAuthAuthorize() {
        /* Generate random state string and save in in preferences */
        this._state = RandomStateString.generateRandomString();
        SharedPreferences.Editor editor = this._shared_preferences.edit();
        editor.clear();
        editor.putString("state_string", this._state);
        editor.apply();

        /* Build authorization URI */
        Uri.Builder builder = new Uri.Builder()
            .scheme("https").authority("api.netatmo.com")
            .appendPath("oauth2").appendPath("authorize")
            .appendQueryParameter("client_id", getString(R.string.client_id))
            .appendQueryParameter("redirect_uri", getString(R.string.redirect_uri))
            .appendQueryParameter("scope", getString(R.string.app_scope))
            .appendQueryParameter("state", getState());
        Uri authorize_uri = builder.build();

        /* Open authorization URI in browser */
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, authorize_uri);
        startActivity(browserIntent);
    }

    /**
     * Handle Redirected URI from Netatmo API.
     * Netatmo OAuth step 3.
     *
     * @note  See authentication documentation :
     *        https://dev.netatmo.com/apidocumentation/oauth
     */
    public boolean handleRedirectUri() {
        /* Retrieve and parse redirected URI */
        Intent intent   = this.getIntent();
        String action   = intent.getAction();
        Uri data        = intent.getData();
        if (data == null) {
            return false;
        }

        RetURI ret_uri = parseUri(data);
        if (!compareStateString(ret_uri.getState())) {
            Log.d(TAG, "problem with state string");
            return false;
        }
        requestAccessToken(ret_uri.getCode());
        return true;
    }

    /**
     * Retrieve the access token with the code.
     * Netatmo OAuth step 4.
     *
     * @note  See authentication documentation :
     *        https://dev.netatmo.com/apidocumentation/oauth
     */
    private void requestAccessToken(String code) {
        http_client.requestAccessToken(code, OAuthResponseListener);
    }

    private void OAuthResponseHandler(String response) {
        try {
            Log.i("HTTP", "<--          " + response);
            HashMap<String, String> parsedResponse = NetatmoUtils.parseOAuthResponse(
                    new JSONObject(response)
            );

            storeTokens(
                    parsedResponse.get(NetatmoUtils.KEY_ACCESS_TOKEN),
                    parsedResponse.get(NetatmoUtils.KEY_REFRESH_TOKEN),
                    Long.valueOf(parsedResponse.get(NetatmoUtils.KEY_EXPIRES_AT))
            );

            /* Tokens are stored -> Go to main activity */
            Intent mainIntent = new Intent(OAuthActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean compareStateString(String ret_state) {
        return ret_state.equals(this._state);
    }

    /**
     * Parse Uri from api.
     * Check state string.
     *
     * @param uri the complete redirected uri, containing state and code.
     *
     * @note See authentication documentation :
     *       https://dev.netatmo.com/apidocumentation/oauth
     */
    private RetURI parseUri(Uri uri) {
        return new RetURI(
            uri.getQueryParameter(URI_QUERY_STATE),
            uri.getQueryParameter(URI_QUERY_CODE)
        );
    }

    /* -- Token management ---------------------------------------------------------------------- */

    /**
     * Store tokens retrieved from API in preferences.
     * @param access_token   The user access token.
     * @param refresh_token  The user refresh token.
     * @param expires_at     The expiry time of the access token.
     */
    private void storeTokens(String access_token, String refresh_token, long expires_at) {
        SharedPreferences.Editor editor = this._shared_preferences.edit();
        editor.putString(NetatmoUtils.KEY_ACCESS_TOKEN, access_token);
        editor.putString(NetatmoUtils.KEY_REFRESH_TOKEN, refresh_token);
        editor.putLong(NetatmoUtils.KEY_EXPIRES_AT, expires_at);
        editor.apply();
    }

    private boolean checkUserTokens() {
        return (getAccessToken() != null) && (getRefreshToken() != null);
    }

    private String getRefreshToken() {
        return this._shared_preferences.getString(NetatmoUtils.KEY_REFRESH_TOKEN, null);
    }

    private String getAccessToken() {
        return this._shared_preferences.getString(NetatmoUtils.KEY_ACCESS_TOKEN,null);
    }

    private long getExpiresAt() {
        return this._shared_preferences.getLong(NetatmoUtils.KEY_EXPIRES_AT,0);
    }

    private String getState() { return this._state; }

}

/**
 * Class RetURI.
 *
 * This class contains the redirected URI from API.
 */
class RetURI {
    private String _state = null;
    private String _code  = null;

    public RetURI(String state, String code) {
        this._state = state;
        this._code  = code;
    }

    public String getState() { return this._state; }
    public String getCode() { return this._code; }
}

/**
 * Class RandomStateString.
 *
 * This class generates random state string to pass to API URI.
 */
class RandomStateString {

    private static final int ASCII_CHAR_0 = 48;
    private static final int ASCII_CHAR_a = 97;
    private static final int ASCII_CHAR_A = 65;
    private static final int ASCII_CHAR_9 = ASCII_CHAR_0 + 9;
    private static final int ASCII_CHAR_z = ASCII_CHAR_a + 25;
    private static final int ASCII_CHAR_Z = ASCII_CHAR_A + 25;
    private static final int STATE_STRING_LENGTH = 32;

    public static String generateRandomString() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int tmpInt;
        int i = 0;
        while (i < STATE_STRING_LENGTH) {
            tmpInt = generator.nextInt(ASCII_CHAR_z - ASCII_CHAR_0 + 1) + ASCII_CHAR_0;
            if (!((tmpInt < ASCII_CHAR_A && tmpInt > ASCII_CHAR_9) ||
                  (tmpInt < ASCII_CHAR_a && tmpInt > ASCII_CHAR_Z))) {
                randomStringBuilder.append((char)tmpInt);
                i++;
            }
        }
        return randomStringBuilder.toString();
    }
}

