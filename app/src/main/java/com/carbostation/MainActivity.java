package com.carbostation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.carbostation.netatmo_sample.NetatmoHTTPClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;
import static com.google.android.material.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_SELECTED;

import static com.carbostation.ui.status.StatusFragment.KEY_DARK_MODE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_main);

        /* Set dark mode according to preferences */
        SharedPreferences shared_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int dark_mode = shared_preferences.getInt(KEY_DARK_MODE, MODE_NIGHT_AUTO_BATTERY);
        setDefaultNightMode(dark_mode);
        initBottomNavBar();
    }

    /**
     * @brief Initialize bottom navigation bar.
     */
    protected void initBottomNavBar() {
        BottomNavigationView bottom_nav_view = findViewById(R.id.main_BottomNavigationView);

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
