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
import android.widget.Button;
import android.widget.Spinner;
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
import com.example.express_delivery_mobile.Model.DriverDetail;
import com.example.express_delivery_mobile.Model.ServiceCentre;
import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.Model.Vehicle;
import com.example.express_delivery_mobile.Service.AdminClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.example.express_delivery_mobile.Util.AuthHandler;
import com.example.express_delivery_mobile.Util.NavHandler;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDriverProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView fullName, _email, contact, city, dob, nic, address, vehicleNumber, vehicleType, serviceCenter, centerAddress;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;
    Button editDriverDetails;
    Spinner center;
    Spinner vehicle;

    //Dropdown attributes
    private List<String> centers = new ArrayList<>();
    private List<Integer> center_ids = new ArrayList<>();

    //Dropdown attributes
    private List<String> vehicles = new ArrayList<>();
    private List<Integer> vehicle_ids = new ArrayList<>();

    private boolean centersLoaded;
    private boolean vehiclesLoaded;

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
        editDriverDetails = findViewById(R.id.edit_driver_details);

        editDriverDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AdminDriverProfileActivity.this);
                builder.setTitle("Edit Driver Details");

                //When "Accept" button is clicked
                builder.setPositiveButton("Vehicle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        changeVehicle();
                    }
                });

                //When cancel button is clicked
                builder.setNeutralButton("Service Center", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeServiceCenter();
                    }
                });

                builder.show();
            }
        });

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

    private void changeVehicle() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AdminDriverProfileActivity.this, R.style.MyAlertDialogTheme);
        LayoutInflater inflater = (LayoutInflater) AdminDriverProfileActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.update_vehicle_dialog, null);
        mBuilder.setTitle("Change Vehicle");
        vehicle = (Spinner) v.findViewById(R.id.vehicle_spinner);
        //Show progress
        mProgressDialog.setMessage("Setting up form...");
        mProgressDialog.show();

        setupVehicleDropdown();

        mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String _vehicle = vehicle.getSelectedItem().toString();
                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleId(vehicle_ids.get(vehicles.indexOf(_vehicle)));
                DriverDetail driverDetail = new DriverDetail();
                driverDetail.setDriverId(getIntent().getIntExtra("driver_id", 0));
                driverDetail.setVehicle(vehicle);
                User user = new User();
                user.setDriverDetail(driverDetail);

                Call<ResponseBody> call = adminClient.changeDriverVehicle(token, user);
                //Show progress
                mProgressDialog.setMessage("Updating vehicle...");
                mProgressDialog.show();

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        mProgressDialog.dismiss();
                        //200 status code
                        if (response.code() == 200) {
                            Toast.makeText(AdminDriverProfileActivity.this, "Vehicle updated successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AdminDriverProfileActivity.this, AdminDriverListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(AdminDriverProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        mProgressDialog.dismiss();
                        Toast.makeText(AdminDriverProfileActivity.this, "Something! went wrong" + t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mBuilder.setView(v);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void changeServiceCenter() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AdminDriverProfileActivity.this, R.style.MyAlertDialogTheme);
        LayoutInflater inflater = (LayoutInflater) AdminDriverProfileActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.update_center_dialog, null);
        mBuilder.setTitle("Change Service Center");
        center = (Spinner) v.findViewById(R.id.center_spinner);
        //Show progress
        mProgressDialog.setMessage("Setting up form...");
        mProgressDialog.show();

        setupCenterDropdown();

        mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String _center = center.getSelectedItem().toString();
                ServiceCentre serviceCentre = new ServiceCentre();
                serviceCentre.setCentreId(center_ids.get(centers.indexOf(_center)));
                User user = new User();
                user.setEmail(getIntent().getStringExtra("driver_email"));
                user.setServiceCentre(serviceCentre);

                Call<ResponseBody> call = adminClient.changeDriverServiceCenter(token, user);
                //Show progress
                mProgressDialog.setMessage("Updating service center...");
                mProgressDialog.show();

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        mProgressDialog.dismiss();
                        //200 status code
                        if (response.code() == 200) {
                            Toast.makeText(AdminDriverProfileActivity.this, "Service center updated successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AdminDriverProfileActivity.this, AdminDriverListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(AdminDriverProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        mProgressDialog.dismiss();
                        Toast.makeText(AdminDriverProfileActivity.this, "Something! went wrong" + t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mBuilder.setView(v);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminDriverProfileActivity.this, android.R.layout.simple_spinner_item, centers);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    center.setAdapter(adapter);
                    centersLoaded = true;

                    if (centersLoaded) mProgressDialog.dismiss();

                } else {
                    Toast.makeText(AdminDriverProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<ServiceCentre>> call, Throwable t) {
                Toast.makeText(AdminDriverProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    private void setupVehicleDropdown() {
        Call<List<Vehicle>> call = adminClient.getAvailableVehicles(token);

        call.enqueue(new Callback<List<Vehicle>>() {
            @Override
            public void onResponse(Call<List<Vehicle>> call, Response<List<Vehicle>> response) {
                List<Vehicle> vehicleList = response.body();
                if (vehicleList != null) {

                    //Configure drop down
                    for (Vehicle vehicle : vehicleList) {
                        vehicles.add(vehicle.getVehicleType() + " " + vehicle.getVehicleNumber());
                        vehicle_ids.add(vehicle.getVehicleId());
                    }

                    //Set Adapter for dropdown
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminDriverProfileActivity.this, android.R.layout.simple_spinner_item, vehicles);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    vehicle.setAdapter(adapter);
                    vehiclesLoaded = true;

                    if (vehiclesLoaded) mProgressDialog.dismiss();

                } else {
                    Toast.makeText(AdminDriverProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Vehicle>> call, Throwable t) {
                Toast.makeText(AdminDriverProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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
