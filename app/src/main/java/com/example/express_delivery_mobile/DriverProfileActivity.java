package com.example.express_delivery_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.example.express_delivery_mobile.Service.UserClient;
import com.example.express_delivery_mobile.Util.AuthHandler;
import com.example.express_delivery_mobile.Util.NavHandler;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView fullName, _email, contact, city, dob, nic, address, vehicleNumber, vehicleType, serviceCenter, centerAddress;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;

    private String token;
    private String email;
    private String firstName;
    private String lastName;
    private String username;

    private User user;

    //Driver Retrofit client
    UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Check if authorization token is valid
        String result = AuthHandler.validate(DriverProfileActivity.this, "driver");

        if (result != null) {
            if (result.equals("unauthorized") || result.equals("expired")) return;
        }

        //Load layout only after authorization
        setContentView(R.layout.activity_driver_profile);

        fullName = findViewById(R.id.driverName);
        _email = findViewById(R.id.email);
        contact = findViewById(R.id.contact);
        city = findViewById(R.id.city);
        dob = findViewById(R.id.dob);
        nic = findViewById(R.id.nic);
        address = findViewById(R.id.address);
        vehicleNumber = findViewById(R.id.vehicleNumber);
        vehicleType = findViewById(R.id.vehicleType);
        serviceCenter = findViewById(R.id.center);
        centerAddress = findViewById(R.id.centerAddress);

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

        mProgressDialog = new ProgressDialog(this);

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        getUserDetails();
    }

    private void getUserDetails() {
        Call<User> call = userClient.getUser(token);

        //Show Progress
        mProgressDialog.setMessage("Loading Profile..");
        mProgressDialog.show();

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                if(user != null){
                    fullName.setText(user.getFirstName() + " " + user.getLastName());
                    _email.setText(String.valueOf(user.getEmail()));
                    contact.setText(String.valueOf(user.getPhoneNumber()));
                    city.setText(String.valueOf(user.getLocation()));
                    dob.setText(String.valueOf(user.getDriverDetail().getDob()));
                    nic.setText(String.valueOf(user.getDriverDetail().getNic()));
                    address.setText(String.valueOf(user.getDriverDetail().getAddress()));
                    vehicleNumber.setText(String.valueOf(user.getDriverDetail().getVehicle().getVehicleNumber()));
                    vehicleType.setText(String.valueOf(user.getDriverDetail().getVehicle().getVehicleType()));
                    serviceCenter.setText(String.valueOf(user.getServiceCentre().getCentre()));
                    centerAddress.setText(String.valueOf(user.getServiceCentre().getAddress()));
                }else {
                    Toast.makeText(DriverProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(DriverProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleDriverNav(item, DriverProfileActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
         super.onBackPressed();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
