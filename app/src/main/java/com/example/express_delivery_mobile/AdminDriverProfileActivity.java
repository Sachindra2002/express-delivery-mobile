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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.express_delivery_mobile.Adapter.DriverDocumentsAdapter;
import com.example.express_delivery_mobile.Model.Documents;
import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.Service.AdminClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.example.express_delivery_mobile.Util.AuthHandler;
import com.example.express_delivery_mobile.Util.NavHandler;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDriverProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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

    private List<Documents> documents;
    private RecyclerView recyclerView;
    private DriverDocumentsAdapter driverDocumentsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    AdminClient adminClient = RetrofitClientInstance.getRetrofitInstance().create(AdminClient.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if authorization token is valid
        String result = AuthHandler.validate(AdminDriverProfileActivity.this, "admin");

        if (result != null) {
            if (result.equals("unauthorized") || result.equals("expired")) return;
        }

        //Load layout only after authorization
        setContentView(R.layout.activity_admin_driver_profile);

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

        fullName.setText(getIntent().getStringExtra("driver_name"));
        _email.setText(getIntent().getStringExtra("driver_email"));
        contact.setText(getIntent().getStringExtra("driver_telephone"));
        city.setText(getIntent().getStringExtra("driver_city"));
        dob.setText(getIntent().getStringExtra("driver_dob"));
        nic.setText(getIntent().getStringExtra("driver_nic"));
        address.setText(getIntent().getStringExtra("driver_address"));
        vehicleNumber.setText(getIntent().getStringExtra("vehicle_number"));
        vehicleType.setText(getIntent().getStringExtra("vehicle_type"));
        serviceCenter.setText(getIntent().getStringExtra("center"));
        centerAddress.setText(getIntent().getStringExtra("center_address"));

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

        //Setup documents list
        documents = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);


        // SetOnRefreshListener on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getDriverDocuments();
            }
        });

        getDriverDocuments();

    }

    private void getDriverDocuments() {
        Call<List<Documents>> call = adminClient.getDriverDocuments(token, getIntent().getStringExtra("driver_email"));

        //Show Progress
        mProgressDialog.setMessage("Loading Driver Documents..");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Documents>>() {
            @Override
            public void onResponse(Call<List<Documents>> call, Response<List<Documents>> response) {
                documents = response.body();
                System.out.println(response);
                System.out.println(response.body());
                //Handle null pointer errors
                if (documents != null) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(AdminDriverProfileActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    driverDocumentsAdapter = new DriverDocumentsAdapter(AdminDriverProfileActivity.this, documents, token, "admin", email, mProgressDialog);
                    recyclerView.setAdapter(driverDocumentsAdapter);
                    driverDocumentsAdapter.setDocuments(documents);
                } else {
                    Toast.makeText(AdminDriverProfileActivity.this, "Something went wrong" + response.toString(), Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Documents>> call, Throwable t) {
                Toast.makeText(AdminDriverProfileActivity.this, "Something went Wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleAdminNav(item, AdminDriverProfileActivity.this);

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
