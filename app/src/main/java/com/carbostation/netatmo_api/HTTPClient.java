/*
 * Copyright 2013 Netatmo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.carbostation.netatmo_api;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


abstract public class HTTPClient {

    //volley library part
    RequestQueue queue;


    public HTTPClient(Context context){
        request_queue = Volley.newRequestQueue(context);
    }


    /**
     * Send POST request using volley.
     * @param url
     * @param params
     * @param successListener
     * @param errorListener
     */
    protected void POST(String url, final HashMap<String,String> params, Response.Listener<String> successListener, Response.ErrorListener errorListener){

        StringRequest request = new StringRequest(Request.Method.POST, url,successListener,errorListener) {
            @Override
            protected Map<String,String> getParams(){
                return params;
            }
        };


        Log.i("HTTP", "--> [POST]   " + request.getUrl());
        queue.add(request);
    }

    /**
     * Send GET request using volley.
     * @param url
     * @param params
     * @param successListener
     * @param errorListener
     */
    protected void GET(String url, final HashMap<String,String> params, Response.Listener<String> successListener, Response.ErrorListener errorListener){

        StringRequest request = new StringRequest(Request.Method.POST, url,successListener,errorListener) {
            @Override
            protected Map<String,String> getParams(){
                return params;
            }
        };

        Log.i("HTTP", "--> [GET]    " + request.getUrl());
        queue.add(request);
    }

    /**
     * Get request using volley.
     * Since the access token is needed for each GET request to the Netatmo API,
     * we need to check if it has not expired.
     * @param url
     * @param params
     * @param successListener
     * @param errorListener
     */
    protected void get(final String url, final HashMap<String,String> params, final Response.Listener<String> successListener, final Response.ErrorListener errorListener){
        params.put("access_token", getAccessToken());
        GET(url,params,successListener,errorListener);
    }

    /**
     *
     * Once an access token has been obtained, it can be used immediately to access the REST API.
     * After a certain amount of time, the access token expires
     * and the application needs to use the refresh token to renew the access token.
     * Both the refresh token and the expiration time are obtained during the authentication phase.
     * @param refreshToken
     * @param successListener
     * @param errorListener
     */
    public void refreshToken(String refreshToken, Response.Listener<String> successListener, Response.ErrorListener errorListener){
        HashMap<String, String> params = new HashMap<>();
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", refreshToken);
        params.put("client_id", getClientId());
        params.put("client_secret", getClientSecret());

        POST(NetatmoUtils.URL_OAUTH_REQUEST_TOKEN, params, successListener, errorListener);
    }

    public void requestAccessToken(String code, Response.Listener<String> successListener, Response.ErrorListener errorListener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", getClientId());
        params.put("client_secret", getClientSecret());
        params.put("code", code);
        params.put("scope", getAppScope());
        params.put("redirect_uri", "http://www.carbostation.io/auth");

        POST(NetatmoUtils.URL_OAUTH_REQUEST_TOKEN, params, successListener, errorListener);
    }

    /**
     * Returns the list of devices owned by the user, and their modules.
     * A device is identified by its _id (which is its mac address) and each device may have one,
     * several or no modules, also identified by an _id.
     * See <a href="https://dev.netatmo.com/doc/methods/devicelist">dev.netatmo.com/doc/methods/devicelist</a> for more information.
     * @param successListener
     * @param errorListener
     */
    public void getDevicesList(Response.Listener<String> successListener, Response.ErrorListener errorListener){
        get(NetatmoUtils.URL_API_GET_DEVICES_LIST, new HashMap<String, String>(), successListener, errorListener);
    }

    /**
     * You can get your client id by creating a Netatmo app first:
     * <a href="https://dev.netatmo.com/dev/createapp">dev.netatmo.com/dev/createapp</a>
     * @return CLIENT_ID
     */
    protected abstract String getClientId();

    /**
     * You can get your client secret by creating a Netatmo app first:
     * <a href="https://dev.netatmo.com/dev/createapp">dev.netatmo.com/dev/createapp</a>
     * @return CLIENT_SECRET
     */
    protected abstract String getClientSecret();

    /**
     * The application can require different scopes depending on the action it will need to execute.
     *<a href="https://dev.netatmo.com/doc/authentication/scopes">dev.netatmo.com/doc/authentication/scopes</a>
     * @return APP_SCOPE
     */
    protected abstract String getAppScope();

    /**
     * You have to call this method to store the different tokens
     * @param refreshToken
     * @param accessToken
     * @param expiresAt
     */
    protected abstract void storeTokens(String refreshToken, String accessToken, long expiresAt);

    /**
     * Called when the user sign out
     */
    protected abstract void clearTokens();

    /**
     * Return the refresh token stored by {@link #storeTokens(String, String, long)}.
     * @return refreshToken
     */
    protected abstract String getRefreshToken();

    /**
     * Return the access token stored by {@link #storeTokens(String, String, long)}.
     * @return accessToken
     */
    protected abstract String getAccessToken();

    /**
     * Return the expiration date, of the refreshToken, stored by {@link #storeTokens(String, String, long)}.
     * @return expiresAt
     */
    protected abstract long getExpiresAt();

}
