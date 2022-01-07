package com.example.express_delivery_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.express_delivery_mobile.Model.DriverDetail;
import com.example.express_delivery_mobile.Model.MailTracking;
import com.example.express_delivery_mobile.Model.ServiceCentre;
import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.Util.AuthHandler;
import com.example.express_delivery_mobile.Util.NavHandler;
import com.google.android.material.navigation.NavigationView;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.util.Date;

public class TrackPackageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    String[] descriptionData = {"Order\nAccepted", "In\nTransit", "Out for\nDelivery", "Parcel\nDelivered"};
    String[] descriptionData1 = {"Order\nPlaced", "Order\nCancelled"};
    String[] descriptionData2 = {"Order\nPlaced", "Order\nRejected"};

    TextView created_at, tracking_id, pick_up_address, drop_address, status_11, status_10, parcel_description, parcel_weight, parcel_type, _pieces,
            driver_name, driver_mobile, driver_vehicle;
    CardView driver_info;
    LinearLayout ll6;
    Button call_driver;

    private String token;
    private String email;
    private String username;
    private String firstName;
    private String lastName;

    public TrackPackageActivity() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if authorization token is valid
        String result = AuthHandler.validate(TrackPackageActivity.this, "customer");

        if (result != null) {
            if (result.equals("unauthorized") || result.equals("expired")) return;
        }

        //Load layout only after authorization
        setContentView(R.layout.activity_track_package);

        created_at = findViewById(R.id.created_at);
        tracking_id = findViewById(R.id.tracking_id);
        pick_up_address = findViewById(R.id.pick_up_address);
        drop_address = findViewById(R.id.drop_address);
        parcel_description = findViewById(R.id.parcel_description);
        parcel_weight = findViewById(R.id.parcel_weight);
        parcel_type = findViewById(R.id.parcel_type);
        _pieces = findViewById(R.id.pieces);
        driver_name = findViewById(R.id.driver_name);
        driver_mobile = findViewById(R.id.driver_mobile);
        driver_vehicle = findViewById(R.id.driver_vehicle);
        driver_info = findViewById(R.id.driver_info);
        ll6 = findViewById(R.id.ll6);
        call_driver = findViewById(R.id.call_driver);

        Date d = new Date();
        d.setTime(getIntent().getLongExtra("created_at", -1));

        created_at.setText("Order placed on " + d);
        tracking_id.setText("Tracking ID #" + getIntent().getIntExtra("trackId", 0));
        pick_up_address.setText("From : " + getIntent().getStringExtra("pickUpAddress"));
        drop_address.setText("To : " + getIntent().getStringExtra("dropOffAddress"));
        parcel_description.setText(getIntent().getStringExtra("description"));
        parcel_weight.setText(getIntent().getStringExtra("weight") + "KG");
        parcel_type.setText(getIntent().getStringExtra("parcelType"));
        _pieces.setText(getIntent().getStringExtra("pieces"));

        if (getIntent().getStringExtra("driverFirstName") != null) {

            driver_name.setText(getIntent().getStringExtra("driverFirstName") + " " + getIntent().getStringExtra("driverLastName"));
            driver_mobile.setText(getIntent().getStringExtra("driverMobile"));
            driver_vehicle.setText(getIntent().getStringExtra("driverVehicleNumber"));
        } else {
            ll6.setVisibility(View.INVISIBLE);
        }
        StateProgressBar stateProgressBar = (StateProgressBar) findViewById(R.id.your_state_progress_bar_id);


        if (getIntent().getStringExtra("status") != null && getIntent().getStringExtra("status").equalsIgnoreCase("Driver Accepted")) {
            stateProgressBar.setStateDescriptionData(descriptionData);
            stateProgressBar.setForegroundColor(ContextCompat.getColor(this, R.color.yellow));
            stateProgressBar.setStateDescriptionColor(ContextCompat.getColor(this, R.color.black));
            stateProgressBar.setCurrentStateDescriptionColor(ContextCompat.getColor(this, R.color.yellow));

            stateProgressBar.enableAnimationToCurrentState(true);

            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
        } else if (getIntent().getStringExtra("status") != null && getIntent().getStringExtra("status").equalsIgnoreCase("Processing")) {
            stateProgressBar.setStateDescriptionData(descriptionData);
            stateProgressBar.setForegroundColor(ContextCompat.getColor(this, R.color.yellow));
            stateProgressBar.setStateDescriptionColor(ContextCompat.getColor(this, R.color.black));
            stateProgressBar.setCurrentStateDescriptionColor(ContextCompat.getColor(this, R.color.yellow));

            stateProgressBar.enableAnimationToCurrentState(true);

            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
        } else if (getIntent().getStringExtra("status") != null && getIntent().getStringExtra("status").equalsIgnoreCase("Assigned")) {
            stateProgressBar.setStateDescriptionData(descriptionData);
            stateProgressBar.setForegroundColor(ContextCompat.getColor(this, R.color.yellow));
            stateProgressBar.setStateDescriptionColor(ContextCompat.getColor(this, R.color.black));
            stateProgressBar.setCurrentStateDescriptionColor(ContextCompat.getColor(this, R.color.yellow));

            stateProgressBar.enableAnimationToCurrentState(true);

            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
        } else if (getIntent().getStringExtra("status") != null && getIntent().getStringExtra("status").equalsIgnoreCase("Cancelled")) {
            stateProgressBar.setStateDescriptionData(descriptionData1);
            stateProgressBar.setForegroundColor(ContextCompat.getColor(this, R.color.yellow));
            stateProgressBar.setStateDescriptionColor(ContextCompat.getColor(this, R.color.black));
            stateProgressBar.setCurrentStateDescriptionColor(ContextCompat.getColor(this, R.color.buttonRed));
            stateProgressBar.setMaxStateNumber(StateProgressBar.StateNumber.TWO);
            stateProgressBar.enableAnimationToCurrentState(true);

            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
        } else if (getIntent().getStringExtra("status") != null && getIntent().getStringExtra("status").equalsIgnoreCase("Rejected")) {
            stateProgressBar.setStateDescriptionData(descriptionData2);
            stateProgressBar.setForegroundColor(ContextCompat.getColor(this, R.color.yellow));
            stateProgressBar.setStateDescriptionColor(ContextCompat.getColor(this, R.color.black));
            stateProgressBar.setCurrentStateDescriptionColor(ContextCompat.getColor(this, R.color.buttonRed));
            stateProgressBar.setMaxStateNumber(StateProgressBar.StateNumber.TWO);
            stateProgressBar.enableAnimationToCurrentState(true);

            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
        }

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        token = "Bearer " + sharedPreferences.getString("auth_token", null);
        email = sharedPreferences.getString("email", null);
        firstName = sharedPreferences.getString("firstName", null);
        lastName = sharedPreferences.getString("lastName", null);

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

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleCustomerNav(item, TrackPackageActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
