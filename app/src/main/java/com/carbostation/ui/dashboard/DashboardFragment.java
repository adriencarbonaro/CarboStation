package com.carbostation.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Response;
import com.carbostation.R;
import com.carbostation.netatmo_api.model.Measures;
import com.carbostation.netatmo_api.model.Params;
import com.carbostation.netatmo_sample.SampleHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.carbostation.netatmo_api.NetatmoUtils.parseMeasures;


public class DashboardFragment extends Fragment {

    private String TAG="DashboardFragment";
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
        this.initDashboardListeners();
        http_client = SampleHttpClient.getInstance(getContext());
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "OnCreateView");
        View root = inflater.inflate(R.layout.ui_fragment_dashoard, container, false);
        dashboard_title = root.findViewById(R.id.dashboard_title);
        dashboard_temp_int_value = root.findViewById(R.id.dashboard_temp_in_value_current);
        dashboard_temp_out_value = root.findViewById(R.id.dashboard_temp_out_value_current);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "resume");
    }

    public void updateView(String title, String temp_in, String temp_out) {
        if (title != null) { dashboard_title.setText(title); }
        else { dashboard_title.setText(getString(R.string.dashboard_title)); }

        if (temp_in != null) { dashboard_temp_int_value.setText(temp_in); }
        else { dashboard_temp_int_value.setText(R.string.dashboard_temp_null); }

        if (temp_out != null) { dashboard_temp_out_value.setText(temp_out); }
        else { dashboard_temp_out_value.setText(getString(R.string.dashboard_temp_null)); }
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
                    updateView(title, temp_in, temp_out);
                } catch (JSONException e) {
                    e.printStackTrace();
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