package com.carbostation;

import android.os.Bundle;

import com.carbostation.netatmo_sample.SampleHttpClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    // Bottom navigation item
    private BottomNavigationView bottom_nav_view;
    private SampleHttpClient     http_client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_main);

        bottom_nav_view = findViewById(R.id.bottom_nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration app_bar_configuration = new AppBarConfiguration.Builder(
            R.id.navigation_home,
            R.id.navigation_dashboard,
            R.id.navigation_status
        ).build();
        NavController bottom_nav_controller = Navigation.findNavController(this, R.id.nav_fragment_host);
        NavigationUI.setupWithNavController(bottom_nav_view, bottom_nav_controller);

        /* Login to Netatmo API */
        http_client = SampleHttpClient.getInstance(this);
        http_client.login(
            "carbonaro.adrien@gmail.com",
            "Dekide.X9"
        );
    }

}
