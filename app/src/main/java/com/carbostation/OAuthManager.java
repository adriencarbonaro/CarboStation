package com.carbostation;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

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

public class OAuthManager {

    /* Redirect URI query fields */
    public static final String URI_QUERY_STATE  = "state";
    public static final String URI_QUERY_CODE   = "code";

    private Intent _intent   = null;
    private String _state    = null;
    private String _uri      = null;

    public OAuthManager(Intent intent) {
        this._intent = intent;
        this._state  = "mycustomstate";
    }

    /**
     * Handle Redirected URI from Netatmo API.
     *
     * @note  See authentication documentation :
     *        https://dev.netatmo.com/apidocumentation/oauth
     */
    public void handleApiUri() {
        /* Retrieve and parse redirected URI */
        Intent intent   = this.getIntent();
        String action   = intent.getAction();
        Uri data        = intent.getData();
        if (data == null) {
            return;
        }

        RetURI ret_uri = parseUri(data);
        if (!checkStateString(ret_uri.getState())) {
            Log.d("AUTH", "problem with state stringe");
            return;
        }
        Log.d("AUTH", ret_uri.getCode());
        requestAccessToken();
    }

    private void requestAccessToken() {

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
        RetURI ret_uri = null;
        ret_uri = new RetURI(
            uri.getQueryParameter(URI_QUERY_STATE),
            uri.getQueryParameter(URI_QUERY_CODE)
        );
        return ret_uri;
    }

    private boolean checkStateString(String state) {
        return state.equals(this._state);
    }

    private Intent getIntent() { return this._intent; }
}
