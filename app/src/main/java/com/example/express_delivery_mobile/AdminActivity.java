package com.example.express_delivery_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.express_delivery_mobile.Util.AuthHandler;
import com.example.express_delivery_mobile.Util.NavHandler;
import com.google.android.material.navigation.NavigationView;

public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView home_name;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;

    private String token;
    private String email;
    private String firstName;
    private String lastName;

    private CardView addDriver;
    private CardView viewDrivers;
    private CardView viewAgents;
    private CardView viewCenters;
    private CardView viewDisputes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if authorization token is valid
        String result = AuthHandler.validate(AdminActivity.this, "admin");

        if (result != null) {
            if (result.equals("unauthorized") || result.equals("expired")) return;
        }

        //Load layout only after authorization
        setContentView(R.layout.activity_admin);

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        token = "Bearer " + sharedPreferences.getString("auth_token", null);
        email = sharedPreferences.getString("email", null);
        firstName = sharedPreferences.getString("firstName", null);
        lastName = sharedPreferences.getString("lastName", null);

        //Set user name to home screen
        home_name = findViewById(R.id.home_name);
        home_name.setText(firstName + " " + lastName);

        addDriver = findViewById(R.id.add_driver);
        addDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, AddDriverActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        viewDrivers = findViewById(R.id.view_drivers);
        viewDrivers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, AdminDriverListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        viewAgents = findViewById(R.id.agents);
        viewAgents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, AgentListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        viewCenters = findViewById(R.id.view_centers);
        viewCenters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, ServiceCenterListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        viewDisputes = findViewById(R.id.disputes);
        viewDisputes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, DisputesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);

        //Setup navigation drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.open_nav_drawer,
                R.string.close_nav_drawer
        );

        mProgressDialog = new ProgressDialog(this);

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleAdminNav(item, AdminActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Check if authorization token is valid
        AuthHandler.validate(AdminActivity.this, "admin");
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
