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

import android.icu.text.SimpleDateFormat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.carbostation.R;
import com.carbostation.netatmo_api.model.Measures;
import com.carbostation.netatmo_api.model.Module;
import com.carbostation.netatmo_api.model.Params;
import com.carbostation.netatmo_api.model.Station;

public class NetatmoUtils {

    public static final String TAG = "NetatmoUtils";

    /* API URLs */
    public static final String URL_BASE                    = "https://api.netatmo.net";
    public static final String URL_OAUTH_REQUEST_TOKEN     = URL_BASE + "/oauth2/token";
    public static final String URL_API_GET_DEVICES_LIST    = URL_BASE + "/api/devicelist";
    public static final String URL_API_GET_PUBLIC_DATA     = URL_BASE + "/api/getpublicdata";
    public static final String URL_API_GET_STATIONS_DATA   = URL_BASE + "/api/getstationsdata";

    /* API requests parameters */
    public static final String KEY_ACCESS_TOKEN            = "access_token";
    public static final String KEY_REFRESH_TOKEN           = "refresh_token";
    public static final String KEY_EXPIRES_IN              = "expires_in";
    public static final String KEY_EXPIRES_AT              = "expires_at";
    public static final String KEY_GRANT_TYPE              = "grant_type";
    public static final String KEY_CLIENT_ID               = "client_id";
    public static final String KEY_CLIENT_SECRET           = "client_secret";
    public static final String KEY_SCOPE                   = "scope";
    public static final String KEY_REDIRECT_URI            = "redirect_uri";
    public static final String KEY_STATE                   = "state";
    public static final String KEY_CODE                    = "code";
    public static final String KEY_USERNAME                = "username";
    public static final String KEY_PASSWORD                = "password";
    public static final String KEY_DEVICE_ID               = "device_id";

    public static final String KEY_PARAM_TEMPERATURE       = "temperature";

    public static final String TEMP_TREND_STABLE           = "stable";
    public static final String TEMP_TREND_UP               = "up";
    public static final String TEMP_TREND_DOWN             = "down";

    /* API JSON response keys */
    public static final String KEY_BODY                    = "body";
    public static final String KEY_BODY_ID                 = "_id";
    public static final String KEY_MODULE_INDOOR           = "NAMain";
    public static final String KEY_MODULE_OUTDOOR          = "NAModule1";
    public static final String KEY_TIME_SERVER             = "time_server";
    public static final String KEY_BATTERY_STATUS          = "battery_percent";

    public static HashMap<String, String> parseOAuthResponse(JSONObject response) {
        HashMap<String, String> parsedResponse = new HashMap<String, String>();

        try {
            String access_token = response.getString(NetatmoUtils.KEY_ACCESS_TOKEN);
            parsedResponse.put(NetatmoUtils.KEY_ACCESS_TOKEN, access_token);

            String refresh_token = response.getString(NetatmoUtils.KEY_REFRESH_TOKEN);
            parsedResponse.put(NetatmoUtils.KEY_REFRESH_TOKEN, refresh_token);

            String expires_in = response.getString(NetatmoUtils.KEY_EXPIRES_IN);
            Long expires_at = System.currentTimeMillis() + Long.valueOf(expires_in) * 1000;
            parsedResponse.put(NetatmoUtils.KEY_EXPIRES_AT, expires_at.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parsedResponse;
    }

    public static List<Station> parseDevicesList(JSONObject response) {
        List<Station> devices = new ArrayList<Station>();

        try {
            JSONArray JSONstations = response.getJSONObject("body").getJSONArray("devices");

            for (int i = 0; i < JSONstations.length(); i++) {
                JSONObject station = JSONstations.getJSONObject(i);
                String name = station.getString("station_name");
                String moduleName = station.getString("module_name");
                String id = station.getString("_id");

                Station newStation = new Station(name, id);
                Module newModule = new Module(moduleName, id, Module.TYPE_INDOOR);

                newStation.addModule(newModule);
                devices.add(newStation);
            }

            JSONArray JSONmodules = response.getJSONObject("body").getJSONArray("modules");

            for (int i = 0; i < JSONmodules.length(); i++) {
                JSONObject module = JSONmodules.getJSONObject(i);
                String mainDevice = module.getString("main_device");
                String name = module.getString("module_name");
                String id = module.getString("_id");
                String type = module.getString("type");

                Module newModule = new Module(name, id, type);
                for (Station station : devices) {
                    if (mainDevice.equals(station.getId())) {
                        station.addModule(newModule);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return devices;
    }

    public static String getJSONString(JSONObject jo, String s) {
        if (jo == null || s == null || !jo.has(s)) {
            return null;
        }
        try {
            return jo.getString(s);
        } catch (Exception e) {
        }

        return null;
    }

    public static HashMap<String, Measures> parseMeasures(JSONObject response, String[] types) {
        HashMap<String, Measures> result = new HashMap<>();

        try {
            JSONObject body = response.getJSONObject("body");

            // We parse all the stations
            JSONArray stations = body.getJSONArray("devices");
            for (int j = 0; j < stations.length(); j++) {
                Measures measures_device  = new Measures();
                Measures measures_modules = new Measures();
                JSONObject station = stations.getJSONObject(j);
                String current_device_id  = station.getString("type");
                String current_modules_id = station.getJSONArray("modules")
                        .getJSONObject(0).getString("type");
                JSONObject deviceData = station.getJSONObject("dashboard_data");
                JSONObject moduleData = station.getJSONArray("modules")
                        .getJSONObject(0).getJSONObject("dashboard_data");

                /* Get device data */
                for (int i = 0; i < types.length; i++) {
                    switch (types[i]){
                        case Params.TYPE_TEMPERATURE:
                            measures_device.setTemperature(getJSONString(deviceData, types[i]));
                            measures_modules.setTemperature(getJSONString(moduleData, types[i]));
                            break;
                        case Params.TYPE_CO2:
                            measures_device.setCO2(getJSONString(deviceData, types[i]));
                            measures_modules.setCO2(getJSONString(moduleData, types[i]));
                            break;
                        case Params.TYPE_HUMIDITY:
                            measures_device.setHumidity(getJSONString(deviceData, types[i]));
                            measures_modules.setHumidity(getJSONString(moduleData, types[i]));
                            break;
                        case Params.TYPE_PRESSURE:
                            measures_device.setPressure(getJSONString(deviceData, types[i]));
                            measures_modules.setPressure(getJSONString(moduleData, types[i]));
                            break;
                        case Params.TYPE_NOISE:
                            measures_device.setNoise(getJSONString(deviceData, types[i]));
                            measures_modules.setNoise(getJSONString(moduleData, types[i]));
                            break;
                        case Params.TYPE_MIN_TEMP:
                            measures_device.setMinTemp(getJSONString(deviceData, types[i]));
                            measures_modules.setMinTemp(getJSONString(moduleData, types[i]));
                            break;
                        case Params.TYPE_MAX_TEMP:
                            measures_device.setMaxTemp(getJSONString(deviceData, types[i]));
                            measures_modules.setMaxTemp(getJSONString(moduleData, types[i]));
                            break;
                        case Params.TYPE_TEMP_TREND:
                            measures_device.setTempTrend(getJSONString(deviceData, types[i]));
                            measures_modules.setTempTrend(getJSONString(moduleData, types[i]));
                            break;
                        default:
                    }
                }

                result.put(current_device_id, measures_device);
                result.put(current_modules_id, measures_modules);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String getStationName(JSONObject response) {
        try {
            return response.getJSONObject("body")
                    .getJSONArray("devices")
                    .getJSONObject(0)
                    .getString("station_name");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getStringFromObject(Object object) {
        String string = object.toString();

        if (string == null || string.equals("null")) {
            return Measures.STRING_NO_DATA;
        }

        return string;
    }

    public static String getFormatedDate(Long timestamp) {
        return new SimpleDateFormat("dd.MM.yy HH:mm").format(new Date(timestamp));
    }
}