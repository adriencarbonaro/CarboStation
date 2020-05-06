package com.carbostation.ui.status;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;

import com.android.volley.Response;
import com.carbostation.R;
import com.carbostation.BuildConfig;
import com.carbostation.netatmo_api.NetatmoUtils;
import com.carbostation.netatmo_sample.NetatmoHTTPClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;

public class StatusFragment extends Fragment {

    public static final String TAG = "StatusFragment";

    private TextView status_version          = null;
    private Switch switch_test               = null;
    private TextView  refresh_freq_value     = null;
    private SeekBar   refresh_freq_bar       = null;
    private ImageView battery_status_icon    = null;
    private TextView  battery_status         = null;
    private NetatmoHTTPClient http_client    = null;
    private Response.Listener<String> status_station_response;

    /* Preferences */
    private SharedPreferences _shared_preferences;
    private static final String KEY_REFRESH_FREQ    = "refresh_freq";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _shared_preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.initListener();
        http_client = NetatmoHTTPClient.getInstance(getContext());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /* Apply theme from styles.xml to fragments is done here rather than in Manifest */
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.homeLabTheme_StatusFragment);
        LayoutInflater local_inflater = inflater.cloneInContext(contextThemeWrapper);

        View root = local_inflater.inflate(R.layout.ui_fragment_status, container, false);

        status_version = root.findViewById(R.id.status_version);
        switch_test = root.findViewById(R.id.switch_test_value);
        switch_test.setOnCheckedChangeListener(onSwitchClickHandler);

        /* Request refresh frequency */
        refresh_freq_value = root.findViewById(R.id.settings_timing_value);
        refresh_freq_bar = root.findViewById(R.id.settings_timing_bar);
        refresh_freq_bar.setOnSeekBarChangeListener(onSeekBarChangedHandler);
        refresh_freq_bar.setProgress(getRefreshFreq());

        battery_status_icon = root.findViewById(R.id.status_battery_value_icon);
        battery_status      = root.findViewById(R.id.status_battery_value);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        http_client.getStationsData(getString(R.string.device_id), status_station_response, false);
        updateView(BuildConfig.VERSION_NAME);
    }

    private void updateView(String version) {
        status_version.setText(version);
    }

    private CompoundButton.OnCheckedChangeListener onSwitchClickHandler =
            new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) { setDefaultNightMode(MODE_NIGHT_YES); }
            else { setDefaultNightMode(MODE_NIGHT_NO); }
        }
    };

    /**
     * Handle the request refresh frequency seek bar.
     */
    private SeekBar.OnSeekBarChangeListener onSeekBarChangedHandler =
            new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            refresh_freq_value.setText(getString(R.string.settings_refresh_value, NetatmoUtils.req_freq_table[i]));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            storeRefreshFreq(seekBar.getProgress());
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

                    battery_status_icon.setImageResource(battery_drawable_id);
                    battery_status.setText(getString(R.string.battery_status, battery_value));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /* -- Preference management ----------------------------------------------------------------- */

    /**
     * Function to retrieve request refresh frequency index value from preferences.
     *
     * @return Request refresh frequency (seek bar index).
     */
    private int getRefreshFreq() { return _shared_preferences.getInt(KEY_REFRESH_FREQ, 0); }

    /**
     * Function to save seek bar index, indicating request refresh frequency.
     *
     * @param value The seek bar index.
     */
    private void storeRefreshFreq(int value) {
        SharedPreferences.Editor editor = _shared_preferences.edit();
        editor.putInt(KEY_REFRESH_FREQ, value);
        editor.apply();
    }
}