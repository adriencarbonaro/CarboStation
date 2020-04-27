package com.carbostation.ui.status;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.carbostation.R;
import com.carbostation.BuildConfig;

public class StatusFragment extends Fragment {

    private TextView status_version = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_fragment_status, container, false);
        status_version = root.findViewById(R.id.status_version);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateView(BuildConfig.VERSION_NAME);
    }

    private void updateView(String version) {
        status_version.setText(version);
    }
}