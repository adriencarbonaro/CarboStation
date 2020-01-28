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
import com.carbostation.TempManager;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private TempManager temp_manager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Create TempManager instance */
        Log.d("[C]", "create");
        if (temp_manager == null) {
            temp_manager = new TempManager(12, 13);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView dashboard_title = root.findViewById(R.id.dashboard_title);
        final TextView dashboard_temp_int_value = root.findViewById(R.id.dashboard_temp_int_value);
        final TextView dashboard_temp_out_value = root.findViewById(R.id.dashboard_temp_out_value);
        dashboard_title.setText("Temperatures:");
        dashboard_temp_int_value.setText(String.valueOf(temp_manager.getTempInt()));
        dashboard_temp_out_value.setText(String.valueOf(temp_manager.getTempOut()));
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("[P]", "pause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("[R]", "resume");
    }
}