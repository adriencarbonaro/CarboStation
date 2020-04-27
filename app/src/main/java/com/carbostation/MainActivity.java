package com.carbostation;

import android.os.Bundle;

import com.carbostation.netatmo_sample.NetatmoHTTPClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import static com.google.android.material.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_SELECTED;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Bottom navigation item
    private BottomNavigationView bottom_nav_view;
    private NetatmoHTTPClient    http_client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_main);

        initBottomNavBar();
    }

    /**
     * @brief Initialize bottom navigation bar.
     */
    protected void initBottomNavBar() {
        bottom_nav_view = findViewById(R.id.main_BottomNavigationView);

        /* Bottom nav bar labels are visible only on selected item */
        bottom_nav_view.setLabelVisibilityMode(LABEL_VISIBILITY_SELECTED);
        NavController bottom_nav_controller = Navigation.findNavController(this, R.id.main_fragment);
        NavigationUI.setupWithNavController(bottom_nav_view, bottom_nav_controller);

        /* Setup menu controller.
         *
         * Passing each menu ID as a set of IDs
         * because each menu should be considered as top level destinations.
         */
        AppBarConfiguration app_bar_configuration = new AppBarConfiguration.Builder(
            R.id.navigation_home,
            R.id.navigation_dashboard,
            R.id.navigation_status
        ).build();
    }

}
