package com.carbostation.ui.status;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Response;
import com.carbostation.R;
import com.carbostation.BuildConfig;
import com.carbostation.netatmo_api.NetatmoUtils;
import com.carbostation.netatmo_sample.NetatmoHTTPClient;
import com.carbostation.ui.dashboard.DashboardFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.carbostation.netatmo_api.NetatmoUtils.getJSONString;

public class StatusFragment extends Fragment {

    public static final String TAG = "StatusFragment";

    private TextView status_version          = null;
    private Switch switch_test               = null;
    private ImageView battery_status         = null;
    private NetatmoHTTPClient http_client    = null;
    private Response.Listener<String> status_station_response;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initListener();
        http_client = NetatmoHTTPClient.getInstance(getContext());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_fragment_status, container, false);
        status_version = root.findViewById(R.id.status_version);
        switch_test = root.findViewById(R.id.switch_test_value);
        switch_test.setOnCheckedChangeListener(onSwitchClickHandler);
        battery_status = root.findViewById(R.id.status_battery_value);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        http_client.getStationsData(getString(R.string.device_id), status_station_response);
        updateView(BuildConfig.VERSION_NAME);
    }

    private void updateView(String version) {
        status_version.setText(version);
    }

    private CompoundButton.OnCheckedChangeListener onSwitchClickHandler =
            new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) Toast.makeText(getContext(), "YES", Toast.LENGTH_LONG).show();
            else   Toast.makeText(getContext(), "NO", Toast.LENGTH_LONG).show();
        }
    };

    private void initListener() {
        status_station_response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject json_response = null;
                try {
                    json_response = new JSONObject(response);

                    JSONObject  body    = json_response.getJSONObject("body");
                    JSONArray   devices = body.getJSONArray("devices");
                    JSONObject  device  = devices.getJSONObject(0);
                    JSONArray   modules = device.getJSONArray("modules");
                    JSONObject  module  = modules.getJSONObject(0);

                    String      battery_str = module.getString(NetatmoUtils.KEY_BATTERY_STATUS);
                    int battery_value = Integer.valueOf(battery_str);
                    int battery_drawable_id = R.drawable.ic_battery_null;

                    if (battery_value >  0) { battery_drawable_id = R.drawable.ic_battery_20; }
                    if (battery_value > 20) { battery_drawable_id = R.drawable.ic_battery_30; }
                    if (battery_value > 30) { battery_drawable_id = R.drawable.ic_battery_50; }
                    if (battery_value > 50) { battery_drawable_id = R.drawable.ic_battery_60; }
                    if (battery_value > 60) { battery_drawable_id = R.drawable.ic_battery_80; }
                    if (battery_value > 80) { battery_drawable_id = R.drawable.ic_battery_full; }

                    battery_status.setImageResource(battery_drawable_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}