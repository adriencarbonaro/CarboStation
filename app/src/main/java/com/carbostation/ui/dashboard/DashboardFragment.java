package com.carbostation.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.carbostation.R;
import com.carbostation.TempManager;


public class DashboardFragment extends Fragment {

    private String TAG="DashboardFragment";
    static private TempManager temp_manager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Create TempManager instance */
        Log.d("[C]", "create");
        if (temp_manager == null) {
            Log.v(TAG, "create new temp");
            temp_manager = new TempManager(12, 13);
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "OnCreateView");
        View root = inflater.inflate(R.layout.ui_fragment_dashoard, container, false);
        final TextView dashboard_title = root.findViewById(R.id.dashboard_title);
        final TextView dashboard_temp_int_value = root.findViewById(R.id.dashboard_temp_int_value);
        final TextView dashboard_temp_out_value = root.findViewById(R.id.dashboard_temp_out_value);
        dashboard_title.setText("Temperatures:");
        dashboard_temp_int_value.setText(String.valueOf(temp_manager.getTempInt()));
        dashboard_temp_out_value.setText(String.valueOf(temp_manager.getTempOut()));
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(TAG, "onActivityCreated");
        if (savedInstanceState != null) {
            //Restore the fragment's state here
            Log.v(TAG, "Non null bundle");
            savedInstanceState.getInt("myint");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "onSavedInstanceState");
        outState.putInt("myint", 13);
        //Save the fragment's state here
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
}