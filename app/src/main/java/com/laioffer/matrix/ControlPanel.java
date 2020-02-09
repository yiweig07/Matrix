 package com.laioffer.matrix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;

 public class ControlPanel extends AppCompatActivity {

     DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.baseline_home_black_18dp);

        drawerLayout = findViewById(R.id.drawer_layout);

        //location tracker
        final LocationTracker mLocationtracker = new LocationTracker(this);
        drawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                        //Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(@NonNull View drawerView) {
                        final TextView user_textview = drawerView.findViewById(R.id.user_name);
                        final TextView location_textview = drawerView.findViewById(R.id.user_location);

                        //Respond when the drawer is opened
                        mLocationtracker.getLocation();
                        final double longitude = mLocationtracker.getLongitude();
                        final double latitude = mLocationtracker.getLatitude();

                        if(Config.username == null) {
                            user_textview.setText("");
                            location_textview.setText("");
                        } else {
                            user_textview.setText(Config.username);
                            location_textview.setText("Lat=" + new DecimalFormat(".##").format(latitude)
                                    + ",Lon=" + new DecimalFormat(".##").format(longitude));
                        }
                    }


                    @Override
                    public void onDrawerClosed(@NonNull View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // set item as selected to persist highlight
                menuItem.setChecked(true);
                // close drawer when item is tapped
                drawerLayout.closeDrawers();
                // Add code here to update the UI based on the item selected
                // For example, swap UI fragments here
                if(menuItem.getItemId() == R.id.drawer_logout) {
                    Config.username = null;
                    logout();
                }
                return true;
            }
        });
        //add Fragment to the activity
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, MainFragment.newInstance()).commit();
    }

     private void logout() {
        finish();
     }

     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
         return super.onOptionsItemSelected(item);
     }
 }
