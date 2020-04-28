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
    private RequestQueue request_queue;


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
        request_queue.add(request);
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
        request_queue.add(request);
    }
}
