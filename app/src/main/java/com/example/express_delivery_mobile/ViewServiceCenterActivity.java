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

import com.example.express_delivery_mobile.Adapter.DriverListAdapter;
import com.example.express_delivery_mobile.Adapter.MailAdapter;
import com.example.express_delivery_mobile.Model.Mail;
import com.example.express_delivery_mobile.Model.ServiceCentre;
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

public class ViewServiceCenterActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView centerId, center, centerAddress, centerCity;

    private List<Mail> mails;
    private List<User> drivers;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView_2;
    private MailAdapter mailAdapter;
    private DriverListAdapter driverListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout swipeRefreshLayout_2;

    private String token;
    private String email;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;

    AdminClient adminClient = RetrofitClientInstance.getRetrofitInstance().create(AdminClient.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Check if authorization token is valid
        String result = AuthHandler.validate(ViewServiceCenterActivity.this, "admin");

        if (result != null) {
            if (result.equals("unauthorized") || result.equals("expired")) return;
        }

        //Load layout only after authorization
        setContentView(R.layout.activity_view_service_center);

        centerId = findViewById(R.id.center_id);
        center = findViewById(R.id.centerName);
        centerAddress = findViewById(R.id.center_address);
        centerCity = findViewById(R.id._center_city);

        centerId.setText("Service Center ID #" + getIntent().getIntExtra("center_id", 0));
        center.setText(getIntent().getStringExtra("center_name"));
        centerAddress.setText(getIntent().getStringExtra("center_address"));
        centerCity.setText(getIntent().getStringExtra("center_city"));

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        token = "Bearer " + sharedPreferences.getString("auth_token", null);
        email = sharedPreferences.getString("email", null);

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

        //Setup packages list
        mails = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);

        //Setup drivers list
        drivers = new ArrayList<>();
        recyclerView_2 = findViewById(R.id.recycler_view_2);

        // SetOnRefreshListener on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getCenterPackages();
            }
        });


        getCenterPackages();
        getCenterDrivers();

    }

    private void getCenterPackages() {
        ServiceCentre serviceCentre = new ServiceCentre();
        serviceCentre.setCentreId(getIntent().getIntExtra("center_id", 0));

        Call<List<Mail>> call = adminClient.getServiceCenterPackages(token, serviceCentre);

        //Show Progress
        mProgressDialog.setMessage("Loading Packages..");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Mail>>() {
            @Override
            public void onResponse(Call<List<Mail>> call, Response<List<Mail>> response) {
                mails = response.body();
                System.out.println(response);
                System.out.println(response.body());
                //Handle null pointer errors
                if (mails != null) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ViewServiceCenterActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    mailAdapter = new MailAdapter(ViewServiceCenterActivity.this, mails, token, "admin", mProgressDialog, email);
                    recyclerView.setAdapter(mailAdapter);
                    mailAdapter.setMails(mails);
                } else {
                    Toast.makeText(ViewServiceCenterActivity.this, "Something went wrong" + response.toString(), Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Mail>> call, Throwable t) {
                Toast.makeText(ViewServiceCenterActivity.this, "Something went Wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });

    }

    private void getCenterDrivers() {
        ServiceCentre serviceCentre = new ServiceCentre();
        serviceCentre.setCentreId(getIntent().getIntExtra("center_id", 0));

        Call<List<User>> call = adminClient.getServiceCenterDrivers(token, serviceCentre);

        //Show Progress
        mProgressDialog.setMessage("Loading Drivers..");
        mProgressDialog.show();

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                drivers = response.body();
                System.out.println(response);
                System.out.println(response.body());
                //Handle null pointer errors
                if (drivers != null) {
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ViewServiceCenterActivity.this);
                    recyclerView_2.setLayoutManager(layoutManager);
                    recyclerView_2.setHasFixedSize(true);
                    driverListAdapter = new DriverListAdapter(ViewServiceCenterActivity.this, drivers, token, "admin", mProgressDialog);
                    recyclerView_2.setAdapter(driverListAdapter);
                    driverListAdapter.setDrivers(drivers);
                } else {
                    Toast.makeText(ViewServiceCenterActivity.this, "Something went wrong" + response.toString(), Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(ViewServiceCenterActivity.this, "Something went Wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleAdminNav(item, ViewServiceCenterActivity.this);

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
