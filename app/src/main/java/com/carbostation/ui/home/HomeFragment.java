package com.carbostation.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;

import com.carbostation.R;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /* Apply theme from styles.xml to fragments is done here rather than in Manifest */
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.homeLabTheme_HomeFragment);
        LayoutInflater local_inflater = inflater.cloneInContext(contextThemeWrapper);

        View root = local_inflater.inflate(R.layout.ui_fragment_home, container, false);
        ImageView home_logo = root.findViewById(R.id.home_logo);
        Button button_cancel = root.findViewById(R.id.b1);
        Button button_ok = root.findViewById(R.id.b2);
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