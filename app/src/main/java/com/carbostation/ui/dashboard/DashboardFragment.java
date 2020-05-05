package com.carbostation.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;

import com.android.volley.Response;
import com.carbostation.R;
import com.carbostation.netatmo_api.NetatmoUtils;
import com.carbostation.netatmo_api.model.Measures;
import com.carbostation.netatmo_api.model.Params;
import com.carbostation.netatmo_sample.NetatmoHTTPClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.carbostation.netatmo_api.NetatmoUtils.getFormatedDate;
import static com.carbostation.netatmo_api.NetatmoUtils.getJSONString;
import static com.carbostation.netatmo_api.NetatmoUtils.getStationName;
import static com.carbostation.netatmo_api.NetatmoUtils.parseMeasures;


public class DashboardFragment extends Fragment {

    private String TAG="DashboardFragment";

    private Button button_refresh    = null;

    private NetatmoHTTPClient http_client;
    private Response.Listener<String> dashboard_public_response;
    private Response.Listener<String> dashboard_station_response;

    private TextView dashboard_title;
    private TextView dashboard_last_update;
    private TextView dashboard_temp_in_value;
    private TextView dashboard_temp_in_min_value;
    private TextView dashboard_temp_in_max_value;
    private TextView dashboard_temp_out_value;
    private TextView dashboard_temp_out_min_value;
    private TextView dashboard_temp_out_max_value;
    private ImageView dashboard_temp_in_trend;
    private ImageView dashboard_temp_out_trend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initDashboardListeners();
        http_client = NetatmoHTTPClient.getInstance(getContext());
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Apply theme from styles.xml to fragments is done here rather than in Manifest */
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.homeLabTheme_DashboardFragment);
        LayoutInflater local_inflater = inflater.cloneInContext(contextThemeWrapper);

        View root = local_inflater.inflate(R.layout.ui_fragment_dashoard, container, false);

        /* Refresh button */
        button_refresh = root.findViewById(R.id.button_refresh);
        button_refresh.setOnClickListener(onClickHandler_refresh);

        dashboard_title              = root.findViewById(R.id.dashboard_title);
        dashboard_last_update        = root.findViewById(R.id.dashboard_last_update_value);
        dashboard_temp_in_value      = root.findViewById(R.id.dashboard_temp_in_value_current);
        dashboard_temp_in_min_value  = root.findViewById(R.id.dashboard_temp_in_value_min);
        dashboard_temp_in_max_value  = root.findViewById(R.id.dashboard_temp_in_value_max);
        dashboard_temp_out_value     = root.findViewById(R.id.dashboard_temp_out_value_current);
        dashboard_temp_out_min_value = root.findViewById(R.id.dashboard_temp_out_value_min);
        dashboard_temp_out_max_value = root.findViewById(R.id.dashboard_temp_out_value_max);
        dashboard_temp_in_trend      = root.findViewById(R.id.dashboard_temp_in_trend_icon);
        dashboard_temp_out_trend     = root.findViewById(R.id.dashboard_temp_out_trend_icon);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        http_client.getStationsData(getString(R.string.device_id), dashboard_station_response, false);
    }

    public void updateView(String last_update,
            String title, String temp_in, String temp_in_min, String temp_in_max, String temp_in_trend,
            String temp_out, String temp_out_min, String temp_out_max, String temp_out_trend) {
        /* Title */
        if (title != null) { dashboard_title.setText(title); }
        else { dashboard_title.setText(getString(R.string.text_null)); }

        /* Last update */
        if (last_update != null) { dashboard_last_update.setText(last_update); }
        else { dashboard_last_update.setText(getString(R.string.text_null)); }

        /* Temp in */
        if (temp_in != null) { dashboard_temp_in_value.setText(temp_in); }
        else { dashboard_temp_in_value.setText(R.string.text_null); }
        if (temp_in_min != null) { dashboard_temp_in_min_value.setText(temp_in_min); }
        else { dashboard_temp_in_min_value.setText(R.string.text_null); }
        if (temp_in_max != null) { dashboard_temp_in_max_value.setText(temp_in_max); }
        else { dashboard_temp_in_max_value.setText(R.string.text_null); }
        if (temp_in_trend != null) {
            switch (temp_in_trend) {
                case NetatmoUtils.TEMP_TREND_STABLE:
                    dashboard_temp_in_trend.setImageResource(R.drawable.ic_temp_trend_stable);
                    break;
                case NetatmoUtils.TEMP_TREND_UP:
                    dashboard_temp_in_trend.setImageResource(R.drawable.ic_temp_trend_up);
                    break;
                case NetatmoUtils.TEMP_TREND_DOWN:
                    dashboard_temp_in_trend.setImageResource(R.drawable.ic_temp_trend_down);
                    break;
            }
        }

        /* Temp out */
        if (temp_out != null) { dashboard_temp_out_value.setText(temp_out); }
        else { dashboard_temp_out_value.setText(getString(R.string.text_null)); }
        if (temp_out_min != null) { dashboard_temp_out_min_value.setText(temp_out_min); }
        else { dashboard_temp_out_min_value.setText(R.string.text_null); }
        if (temp_out_max != null) { dashboard_temp_out_max_value.setText(temp_out_max); }
        else { dashboard_temp_out_max_value.setText(R.string.text_null); }
        if (temp_out_trend != null) {
            switch (temp_out_trend) {
                case NetatmoUtils.TEMP_TREND_STABLE:
                    dashboard_temp_out_trend.setImageResource(R.drawable.ic_temp_trend_stable);
                    break;
                case NetatmoUtils.TEMP_TREND_UP:
                    dashboard_temp_out_trend.setImageResource(R.drawable.ic_temp_trend_up);
                    break;
                case NetatmoUtils.TEMP_TREND_DOWN:
                    dashboard_temp_out_trend.setImageResource(R.drawable.ic_temp_trend_down);
                    break;
            }
        }
    }

    /**
     * Refresh button handler.
     * Send a getStationsData request, bypassing the timing check.
     */
    private Button.OnClickListener onClickHandler_refresh = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            String msg = getString(R.string.button_refresh_text) + " " + getString(R.string.unicode_ellipsis);
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            http_client.getStationsData(getString(R.string.device_id), dashboard_station_response, true);
        }
    };

    public void initDashboardListeners() {
        /* Station data listener */
        dashboard_station_response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String title;
                String last_update;
                String temp_in;
                String temp_in_min;
                String temp_in_max;
                String temp_out;
                String temp_out_min;
                String temp_out_max;
                String temp_in_trend;
                String temp_out_trend;
                JSONObject json_response = null;

                try {
                    json_response = new JSONObject(response);

                    String[] types = {
                            Params.TYPE_TEMPERATURE,
                            Params.TYPE_MIN_TEMP,
                            Params.TYPE_MAX_TEMP,
                            Params.TYPE_TEMP_TREND,
                    };
                    title = getStationName(json_response);
                    last_update = getFormatedDate(
                        Long.valueOf(getJSONString(json_response, NetatmoUtils.KEY_TIME_SERVER)) * 1000
                    );

                    HashMap<String, Measures> measures = parseMeasures(json_response, types);

                    temp_in         = String.valueOf(measures.get(NetatmoUtils.KEY_MODULE_INDOOR).getTemperature());
                    temp_in_min     = String.valueOf(measures.get(NetatmoUtils.KEY_MODULE_INDOOR).getMinTemp());
                    temp_in_max     = String.valueOf(measures.get(NetatmoUtils.KEY_MODULE_INDOOR).getMaxTemp());
                    temp_in_trend   = String.valueOf(measures.get(NetatmoUtils.KEY_MODULE_INDOOR).getTempTrend());
                    temp_out        = String.valueOf(measures.get(NetatmoUtils.KEY_MODULE_OUTDOOR).getTemperature());
                    temp_out_min    = String.valueOf(measures.get(NetatmoUtils.KEY_MODULE_OUTDOOR).getMinTemp());
                    temp_out_max    = String.valueOf(measures.get(NetatmoUtils.KEY_MODULE_OUTDOOR).getMaxTemp());
                    temp_out_trend  = String.valueOf(measures.get(NetatmoUtils.KEY_MODULE_OUTDOOR).getTempTrend());

                    updateView(
                        last_update,
                        title,
                        temp_in,
                        temp_in_min,
                        temp_in_max,
                        temp_in_trend,
                        temp_out,
                        temp_out_min,
                        temp_out_max,
                        temp_out_trend
                    );
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