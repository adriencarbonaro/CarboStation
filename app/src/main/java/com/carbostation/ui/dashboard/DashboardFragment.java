package com.carbostation.ui.dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Response;
import com.carbostation.R;
import com.carbostation.TempManager;
import com.carbostation.netatmo_api.model.Measures;
import com.carbostation.netatmo_api.model.Params;
import com.carbostation.netatmo_sample.ResponseManager;
import com.carbostation.netatmo_sample.SampleHttpClient;
import com.carbostation.netatmo_api.NetatmoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.carbostation.netatmo_api.NetatmoUtils.parseMeasures;


public class DashboardFragment extends Fragment {

    private String TAG="DashboardFragment";
    static private TempManager   temp_manager;
    private SampleHttpClient     http_client;
    private Response.Listener<String> dashboard_public_response;
    private Response.Listener<String> dashboard_station_response;
    private TextView dashboard_title;
    private TextView dashboard_temp_int_value;
    private TextView dashboard_temp_out_value;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Create TempManager instance */
        Log.d(TAG, "create");
        if (temp_manager == null) {
            Log.v(TAG, "create new temp");
            temp_manager = new TempManager(12, 13);
        }
        this.initDashboardListeners();
        http_client = SampleHttpClient.getInstance(getContext());
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "OnCreateView");
        View root = inflater.inflate(R.layout.ui_fragment_dashoard, container, false);
        dashboard_title = root.findViewById(R.id.dashboard_title);
        dashboard_temp_int_value = root.findViewById(R.id.dashboard_temp_int_value);
        dashboard_temp_out_value = root.findViewById(R.id.dashboard_temp_out_value);
        dashboard_title.setText("Temperatures:");
        dashboard_temp_int_value.setText(String.valueOf(temp_manager.getTempInt()));
        dashboard_temp_out_value.setText(String.valueOf(temp_manager.getTempOut()));
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(TAG, "onActivityCreated");
        http_client.getStationsData(getString(R.string.device_id), dashboard_station_response);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "pause");
        temp_manager.setTempInt(temp_manager.getTempInt() + 1);
        temp_manager.setTempOut(temp_manager.getTempOut() + 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "resume");
    }

    public void initDashboardListeners() {
        /* Station data listener */
        dashboard_station_response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                String title      = null;
                String temp_in    = null;
                String temp_out   = null;
                try {
                    JSONObject json_response = new JSONObject(response);
                    String[] types = {
                            Params.TYPE_TEMPERATURE,
                    };
                    title = json_response.getJSONObject("body")
                            .getJSONArray("devices").getJSONObject(0)
                            .getString("station_name");
                    HashMap<String, Measures> measures = parseMeasures(json_response, types);

                    temp_in  = String.valueOf(measures.get(getString(R.string.device_id)).getTemperature());
                    temp_out = String.valueOf(measures.get(getString(R.string.module_id)).getTemperature());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (title != null) {
                    dashboard_title.setText(title);
                    dashboard_temp_int_value.setText(temp_in  + " °C");
                    dashboard_temp_out_value.setText(temp_out + " °C");
                } else {
                    dashboard_title.setText("ERROR");
                    dashboard_temp_int_value.setText("ERROR");
                    dashboard_temp_out_value.setText("ERROR");
                }
            }
        };

        /* Public data listener */
        dashboard_public_response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "91 : response " + response);
            }
        };
    }
}