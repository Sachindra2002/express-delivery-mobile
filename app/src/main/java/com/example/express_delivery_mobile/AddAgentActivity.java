package com.example.express_delivery_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.express_delivery_mobile.Model.DriverDetail;
import com.example.express_delivery_mobile.Model.ServiceCentre;
import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.Model.Vehicle;
import com.example.express_delivery_mobile.Service.AdminClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.example.express_delivery_mobile.Util.AuthHandler;
import com.example.express_delivery_mobile.Util.NavHandler;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAgentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;

    EditText email, firstName, lastName, phoneNumber;
    Spinner location, center;
    Button addAgent;

    private String token;
    private String __email;

    //Dropdown attributes
    private List<String> centers = new ArrayList<>();
    private List<Integer> center_ids = new ArrayList<>();
    private boolean centersLoaded;

    AdminClient adminClient = RetrofitClientInstance.getRetrofitInstance().create(AdminClient.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if authorization token is valid
        String result = AuthHandler.validate(AddAgentActivity.this, "admin");

        if (result != null) {
            if (result.equals("unauthorized") || result.equals("expired")) return;
        }

        setContentView(R.layout.activity_add_agent);
        email = findViewById(R.id.email);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        phoneNumber = findViewById(R.id.phone_number);
        location = findViewById(R.id.user_location);
        center = findViewById(R.id.center);
        addAgent = findViewById(R.id.add_agent_button);

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        token = "Bearer " + sharedPreferences.getString("auth_token", null);
        __email = sharedPreferences.getString("email", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add Agent");

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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location.setAdapter(adapter);

        //Show progress
        mProgressDialog.setMessage("Setting up form...");
        mProgressDialog.show();

        setupCenterDropdown();

        addAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleAddAgent();
            }
        });

    }

    private void handleAddAgent() {
        String _email = email.getText().toString();
        String _firstName = firstName.getText().toString();
        String _lastName = lastName.getText().toString();
        String _location = location.getSelectedItem().toString();
        String _phoneNumber = phoneNumber.getText().toString();
        String _center = center.getSelectedItem().toString();

        if (TextUtils.isEmpty(_email) || TextUtils.isEmpty(_firstName) || TextUtils.isEmpty(_lastName) || TextUtils.isEmpty(_location) ||
                TextUtils.isEmpty(_phoneNumber)) {
            Toast.makeText(this, "Please enter valid data!", Toast.LENGTH_SHORT).show();
        } else {

            ServiceCentre serviceCentre = new ServiceCentre();
            serviceCentre.setCentreId(center_ids.get(centers.indexOf(_center)));
            User user = new User();
            user.setEmail(_email);
            user.setFirstName(_firstName);
            user.setLastName(_lastName);
            user.setLocation(_location);
            user.setPhoneNumber(_phoneNumber);
            user.setServiceCentre(serviceCentre);

            Call<ResponseBody> call = adminClient.addAgent(token, user);
            //Show progress
            mProgressDialog.setMessage("Adding Agent...");
            mProgressDialog.show();

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();
                    //200 status code
                    if (response.code() == 200) {
                        Toast.makeText(AddAgentActivity.this, "Agent added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddAgentActivity.this, AdminActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        try {
                            // Capture an display specific messages
                            JSONObject obj = new JSONObject(response.errorBody().string());
                            Toast.makeText(AddAgentActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(AddAgentActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(AddAgentActivity.this, "Something! went wrong" + t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void setupCenterDropdown() {
        Call<List<ServiceCentre>> call = adminClient.getServiceCenters(token);

        call.enqueue(new Callback<List<ServiceCentre>>() {
            @Override
            public void onResponse(Call<List<ServiceCentre>> call, Response<List<ServiceCentre>> response) {
                List<ServiceCentre> centerList = response.body();
                if (centerList != null) {

                    //Configure drop down
                    for (ServiceCentre center : centerList) {
                        centers.add(center.getCentre());
                        center_ids.add(center.getCentreId());
                    }

                    //Set Adapter for dropdown
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddAgentActivity.this, android.R.layout.simple_spinner_item, centers);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    center.setAdapter(adapter);
                    centersLoaded = true;

                    if (centersLoaded) mProgressDialog.dismiss();

                } else {
                    Toast.makeText(AddAgentActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<ServiceCentre>> call, Throwable t) {
                Toast.makeText(AddAgentActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleAdminNav(item, AddAgentActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Check if authorization token is valid
        AuthHandler.validate(AddAgentActivity.this, "admin");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
