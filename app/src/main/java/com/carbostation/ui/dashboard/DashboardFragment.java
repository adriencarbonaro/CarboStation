package com.carbostation.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.carbostation.R;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView dashboard_title = root.findViewById(R.id.dashboard_title);
        final TextView dashboard_temp_int_value = root.findViewById(R.id.dashboard_temp_int_value);
        final TextView dashboard_temp_out_value = root.findViewById(R.id.dashboard_temp_out_value);
        dashboardViewModel.getTitle().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Log.d("A", s);
                dashboard_title.setText(s);
            }
        });
        dashboardViewModel.getTempInt().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Log.d("A", s);
                dashboard_temp_int_value.setText(s);
            }
        });
        dashboardViewModel.getTempOut().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Log.d("A", s);
                dashboard_temp_out_value.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        int cur_temp_int = Integer.parseInt(dashboardViewModel.getTextTempInt());
        int cur_temp_out = Integer.parseInt(dashboardViewModel.getTextTempOut());
        dashboardViewModel.setTextTempInt(String.valueOf(cur_temp_int+1));
        dashboardViewModel.setTextTempOut(String.valueOf(cur_temp_out+1));
        Log.d("M", String.valueOf(cur_temp_int));
        Log.d("M", String.valueOf(cur_temp_out));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("M", "resume");
        //View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        //TextView dashboard_title = root.findViewById(R.id.dashboard_title);
        //TextView dashboard_temp_int_value = root.findViewById(R.id.dashboard_temp_int_value);
        //TextView dashboard_temp_out_value = root.findViewById(R.id.dashboard_temp_out_value);
        //dashboardViewModel.setText
    }
}