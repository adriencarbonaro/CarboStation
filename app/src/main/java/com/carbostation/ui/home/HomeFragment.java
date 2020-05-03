package com.carbostation.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.carbostation.R;

public class HomeFragment extends Fragment {

    private ImageView home_logo     = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_fragment_home, container, false);
        home_logo = root.findViewById(R.id.home_logo);
        return root;
    }
}