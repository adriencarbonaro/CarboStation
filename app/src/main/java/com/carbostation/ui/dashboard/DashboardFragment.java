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

import static com.carbostation.netatmo_api.NetatmoUtils.getStationName;
import static com.carbostation.netatmo_api.NetatmoUtils.parseMeasures;


public class DashboardFragment extends Fragment {

    private String TAG="DashboardFragment";
    private SampleHttpClient     http_client;
    private Response.Listener<String> dashboard_public_response;
    private Response.Listener<String> dashboard_station_response;

    private TextView dashboard_title;
    private TextView dashboard_temp_in_value;
    private TextView dashboard_temp_in_min_value;
    private TextView dashboard_temp_in_max_value;
    private TextView dashboard_temp_out_value;
    private TextView dashboard_temp_out_min_value;
    private TextView dashboard_temp_out_max_value;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initDashboardListeners();
        http_client = SampleHttpClient.getInstance(getContext());
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_fragment_dashoard, container, false);

        dashboard_title = root.findViewById(R.id.dashboard_title);
        dashboard_temp_in_value      = root.findViewById(R.id.dashboard_temp_in_value_current);
        dashboard_temp_in_min_value  = root.findViewById(R.id.dashboard_temp_in_value_min);
        dashboard_temp_in_max_value  = root.findViewById(R.id.dashboard_temp_in_value_max);
        dashboard_temp_out_value     = root.findViewById(R.id.dashboard_temp_out_value_current);
        dashboard_temp_out_min_value = root.findViewById(R.id.dashboard_temp_out_value_min);
        dashboard_temp_out_max_value = root.findViewById(R.id.dashboard_temp_out_value_max);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        http_client.getStationsData(getString(R.string.device_id), dashboard_station_response);
    }

    public void updateView(String title, String temp_in, String temp_in_min, String temp_in_max,
            String temp_out, String temp_out_min, String temp_out_max) {
        /* Title */
        if (title != null) { dashboard_title.setText(title); }
        else { dashboard_title.setText(getString(R.string.dashboard_title)); }

        /* Temp in */
        if (temp_in != null) { dashboard_temp_in_value.setText(temp_in); }
        else { dashboard_temp_in_value.setText(R.string.dashboard_temp_null); }
        if (temp_in_min != null) { dashboard_temp_in_min_value.setText(temp_in_min); }
        else { dashboard_temp_in_min_value.setText(R.string.dashboard_temp_null); }
        if (temp_in_max != null) { dashboard_temp_in_max_value.setText(temp_in_max); }
        else { dashboard_temp_in_max_value.setText(R.string.dashboard_temp_null); }

        /* Temp out */
        if (temp_out != null) { dashboard_temp_out_value.setText(temp_out); }
        else { dashboard_temp_out_value.setText(getString(R.string.dashboard_temp_null)); }
        if (temp_out_min != null) { dashboard_temp_out_min_value.setText(temp_out_min); }
        else { dashboard_temp_out_min_value.setText(R.string.dashboard_temp_null); }
        if (temp_out_max != null) { dashboard_temp_out_max_value.setText(temp_out_max); }
        else { dashboard_temp_out_max_value.setText(R.string.dashboard_temp_null); }
    }

    public void initDashboardListeners() {
        /* Station data listener */
        dashboard_station_response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("HTTP", "<--          " + response);
                String title        = null;
                String temp_in      = null;
                String temp_in_min  = null;
                String temp_in_max  = null;
                String temp_out     = null;
                String temp_out_min = null;
                String temp_out_max = null;
                JSONObject json_response = null;

                try {
                    json_response = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String[] types = {
                        Params.TYPE_TEMPERATURE,
                        Params.TYPE_MIN_TEMP,
                        Params.TYPE_MAX_TEMP,
                };
                title = getStationName(json_response);

                HashMap<String, Measures> measures = parseMeasures(json_response, types);

                temp_in      = String.valueOf(measures.get(getString(R.string.device_id)).getTemperature());
                temp_in_min  = String.valueOf(measures.get(getString(R.string.device_id)).getMinTemp());
                temp_in_max  = String.valueOf(measures.get(getString(R.string.device_id)).getMaxTemp());
                temp_out     = String.valueOf(measures.get(getString(R.string.module_id)).getTemperature());
                temp_out_min = String.valueOf(measures.get(getString(R.string.module_id)).getMinTemp());
                temp_out_max = String.valueOf(measures.get(getString(R.string.module_id)).getMaxTemp());
                updateView(title, temp_in, temp_in_min, temp_in_max, temp_out, temp_out_min, temp_out_max);
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