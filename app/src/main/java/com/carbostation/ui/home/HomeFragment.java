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
    private Button button_cancel    = null;
    private Button button_ok        = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_fragment_home, container, false);
        home_logo = root.findViewById(R.id.home_logo);
        button_cancel = root.findViewById(R.id.b1);
        button_ok = root.findViewById(R.id.b2);
        button_cancel.setOnClickListener(onClickHandler_cancer);
        button_ok.setOnClickListener(onClickHandler_ok);
        return root;
    }

    private Button.OnClickListener onClickHandler_cancer =
        new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Cancel", Toast.LENGTH_LONG).show();
            }
        };

    private Button.OnClickListener onClickHandler_ok =
            new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "OK", Toast.LENGTH_LONG).show();
                }
            };
}