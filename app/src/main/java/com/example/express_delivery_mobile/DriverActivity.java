package com.example.express_delivery_mobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.express_delivery_mobile.Adapter.DriverAssignedMailAdapter;
import com.example.express_delivery_mobile.Adapter.MailAdapter;
import com.example.express_delivery_mobile.Model.DriverDetail;
import com.example.express_delivery_mobile.Model.Mail;
import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.Service.DriverClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.example.express_delivery_mobile.Util.AuthHandler;
import com.example.express_delivery_mobile.Util.NavHandler;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView home_name;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;
    private CardView acceptedMails;
    private CardView viewProfile;
    private CardView driverStatus;
    private CardView allPackages;
    Spinner spinner;

    private List<Mail> mails;

    private RecyclerView recyclerView;
    private DriverAssignedMailAdapter driverAssignedMailAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    private String token;
    private String email;
    private String firstName;
    private String lastName;

    //Driver Retrofit client
    DriverClient driverClient = RetrofitClientInstance.getRetrofitInstance().create(DriverClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Check if authorization token is valid
        String result = AuthHandler.validate(DriverActivity.this, "driver");

        if (result != null) {
            if (result.equals("unauthorized") || result.equals("expired")) return;
        }

        //Load layout only after authorization
        setContentView(R.layout.activity_driver);

        acceptedMails = findViewById(R.id.accepted_mails);
        acceptedMails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverActivity.this, DriverAcceptedMailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        viewProfile = findViewById(R.id.view_profile);
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverActivity.this, DriverProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        driverStatus = findViewById(R.id.driver_status);
        driverStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDriverStatus();
            }
        });

        allPackages = findViewById(R.id.driver_all_packages);
        allPackages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverActivity.this, ViewAllPackagesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        token = "Bearer " + sharedPreferences.getString("auth_token", null);
        email = sharedPreferences.getString("email", null);
        firstName = sharedPreferences.getString("firstName", null);
        lastName = sharedPreferences.getString("lastName", null);

        //Set user name to home screen
        home_name = findViewById(R.id.home_name);
        home_name.setText(firstName + " " + lastName);

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

        //Setup mail list
        mails = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        driverAssignedMailAdapter = new DriverAssignedMailAdapter(this, mails, token, "driver", mProgressDialog);
        recyclerView.setAdapter(driverAssignedMailAdapter);

        // SetOnRefreshListener on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getRecentAssignedMails();
            }
        });

        getRecentAssignedMails();
    }

    private void changeDriverStatus() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DriverActivity.this, R.style.MyAlertDialogTheme);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.change_status_dialog, null);
        mBuilder.setTitle("Update Status");
        spinner = (Spinner) v.findViewById(R.id.status_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        mBuilder.setPositiveButton("Update Status", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateDriverStatus();
            }
        });
        mBuilder.setView(v);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void updateDriverStatus() {
        DriverDetail driverDetail = new DriverDetail();
        driverDetail.setStatus(spinner.getSelectedItem().toString());

        User user = new User();
        user.setEmail(email);
        user.setDriverDetail(driverDetail);
        Call<ResponseBody> call = driverClient.updateStatus(token, user);

        //Show progress
        mProgressDialog.setMessage("Updating status...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Successfully accepted
                if (response.code() == 200) {
                    mProgressDialog.dismiss();
                    Toast.makeText(DriverActivity.this, "successfully updated", Toast.LENGTH_SHORT).show();

                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(DriverActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(DriverActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRecentAssignedMails() {
        Call<List<Mail>> call = driverClient.getAllAssignedMails(token);

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
                    driverAssignedMailAdapter.setMails(mails);
                } else {
                    Toast.makeText(DriverActivity.this, "Something went wrong" + response.toString(), Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Mail>> call, Throwable t) {
                Toast.makeText(DriverActivity.this, "Something went Wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleDriverNav(item, DriverActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Check if authorization token is valid
        AuthHandler.validate(DriverActivity.this, "driver");
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
