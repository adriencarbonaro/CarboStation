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

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.carbostation.netatmo_api.model.Measures;
import com.carbostation.netatmo_api.model.Module;
import com.carbostation.netatmo_api.model.Params;
import com.carbostation.netatmo_api.model.Station;

public class NetatmoUtils {
    public static final String KEY_ACCESS_TOKEN  = "access_token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_EXPIRES_AT    = "expires_at";

    public static final String KEY_PARAM_TEMPERATURE = "temperature";

    public static final String KEY_BODY      = "body";
    public static final String KEY_BODY_ID   = "_id";

    public static HashMap<String, String> parseOAuthResponse(JSONObject response) {
        HashMap<String, String> parsedResponse = new HashMap<String, String>();

        try {
            String refreshToken = response.getString("refresh_token");
            parsedResponse.put("refresh_token", refreshToken);

            String accessToken = response.getString("access_token");
            parsedResponse.put("access_token", accessToken);

            String expiresIn = response.getString("expires_in");
            Long expiresAt = System.currentTimeMillis() + Long.valueOf(expiresIn) * 1000;
            parsedResponse.put("expires_at", expiresAt.toString());
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
                String current_device_id  = station.getString("_id");
                String current_modules_id = station.getJSONArray("modules")
                        .getJSONObject(0).getString("_id");
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
}