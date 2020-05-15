package com.carbostation.ui.status;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

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

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;

public class StatusFragment extends Fragment {

    public static final String TAG = "StatusFragment";

    /* UI elements */
    private TextView  status_version         = null;
    private Switch    switch_dark_mode       = null;
    private TextView  refresh_freq_value     = null;
    private SeekBar   refresh_freq_bar       = null;
    private ImageView battery_status_icon    = null;
    private TextView  battery_status         = null;

    /* HTTP */
    private NetatmoHTTPClient http_client    = null;
    private Response.Listener<String> status_station_response;

    /* Preferences */
    private SharedPreferences _shared_preferences;
    public static final String KEY_REFRESH_FREQ    = "refresh_freq";
    public static final String KEY_DARK_MODE       = "dark_mode";

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

        /* Version */
        status_version = root.findViewById(R.id.status_version);

        /* Dark mode */
        switch_dark_mode = root.findViewById(R.id.switch_dark_mode_value);
        switch_dark_mode.setOnCheckedChangeListener(onSwitchDarkModeClickHandler);
        switch_dark_mode.setChecked(getDarkMode() == MODE_NIGHT_YES);

        /* Request refresh frequency */
        int freq_value_idx = getRefreshFreq();
        refresh_freq_value = root.findViewById(R.id.settings_timing_value);
        refresh_freq_value.setText(
            getString(R.string.settings_refresh_value, NetatmoUtils.req_freq_table[freq_value_idx])
        );
        refresh_freq_bar = root.findViewById(R.id.settings_timing_bar);
        refresh_freq_bar.setOnSeekBarChangeListener(onSeekBarChangedHandler);
        refresh_freq_bar.setProgress(freq_value_idx);

        /* Module battery status */
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

    /* -- Button handlers ----------------------------------------------------------------------- */

    /**
     * Handle the dark mode switch button.
     */
    private CompoundButton.OnCheckedChangeListener onSwitchDarkModeClickHandler =
            new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            int dark_mode;
            if (b) { dark_mode = MODE_NIGHT_YES; }
            else { dark_mode = MODE_NIGHT_NO; }
            storeDarkMode(dark_mode);
            setDefaultNightMode(dark_mode);
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

    /* -- HTTP Response listener ---------------------------------------------------------------- */

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
     * Function to retrieve dark mode value from preferences.
     *
     * @return Dark mode status.
     */
    private int getDarkMode() { return _shared_preferences.getInt(KEY_DARK_MODE, MODE_NIGHT_AUTO_BATTERY); }

    /**
     * Function to retrieve request refresh frequency index value from preferences.
     *
     * @return Request refresh frequency (seek bar index).
     */
    private int getRefreshFreq() { return _shared_preferences.getInt(KEY_REFRESH_FREQ, 0); }

    /**
     * Function to save dark mode status.
     *
     * @param status dark mode status (true or false).
     */
    private void storeDarkMode(int status) {
        SharedPreferences.Editor editor = _shared_preferences.edit();
        editor.putInt(KEY_DARK_MODE, status);
        editor.apply();
    }

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