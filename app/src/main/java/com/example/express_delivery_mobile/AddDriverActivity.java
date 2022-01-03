package com.example.express_delivery_mobile;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddDriverActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;

    //Dropdown attributes
    private List<String> centers = new ArrayList<>();
    private List<Integer> center_ids = new ArrayList<>();

    //Dropdown attributes
    private List<String> vehicles = new ArrayList<>();
    private List<Integer> vehicle_ids = new ArrayList<>();

    private boolean centersLoaded;
    private boolean vehiclesLoaded;

    private String token;
    private String __email;

    EditText email, firstName, lastName, phoneNumber, dob, nic, address;
    Spinner city, center, vehicle;
    Button addDriver;

    AdminClient adminClient = RetrofitClientInstance.getRetrofitInstance().create(AdminClient.class);

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if authorization token is valid
        String result = AuthHandler.validate(AddDriverActivity.this, "admin");

        if (result != null) {
            if (result.equals("unauthorized") || result.equals("expired")) return;
        }

        setContentView(R.layout.activity_add_driver);

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        token = "Bearer " + sharedPreferences.getString("auth_token", null);
        __email = sharedPreferences.getString("email", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add Driver");

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

        email = findViewById(R.id.email);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        phoneNumber = findViewById(R.id.phone_number);
        dob = findViewById(R.id.dob);
        nic = findViewById(R.id.nic);
        address = findViewById(R.id.address);
        city = findViewById(R.id.user_location);
        center = findViewById(R.id.center);
        vehicle = findViewById(R.id.vehicle);
        addDriver = findViewById(R.id.add_driver_button);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        dob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddDriverActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter);

        //Show progress
        mProgressDialog.setMessage("Setting up form...");
        mProgressDialog.show();

        setupCenterDropdown();
        setupVehicleDropdown();

        addDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDriver();
            }
        });

    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dob.setText(sdf.format(myCalendar.getTime()));
    }

    private void addDriver() {

        String _email = email.getText().toString();
        String _firstName = firstName.getText().toString();
        String _lastName = lastName.getText().toString();
        String _location = city.getSelectedItem().toString();
        String _phoneNumber = phoneNumber.getText().toString();
        String _dob = dob.getText().toString();
        String _nic = nic.getText().toString();
        String _center = center.getSelectedItem().toString();
        String _vehicle = vehicle.getSelectedItem().toString();
        String _address = address.getText().toString();

        Pattern p = Pattern.compile("^([0-9]{9}[x|X|v|V]|[0-9]{12})$");
        Matcher matcher = p.matcher(_nic);

        if (TextUtils.isEmpty(_email) || TextUtils.isEmpty(_firstName) || TextUtils.isEmpty(_lastName) || TextUtils.isEmpty(_location) ||
                TextUtils.isEmpty(_phoneNumber) || TextUtils.isEmpty(_dob) || TextUtils.isEmpty(_nic) || TextUtils.isEmpty(_address)) {
            Toast.makeText(this, "Please enter valid data!", Toast.LENGTH_SHORT).show();
        } else if (!matcher.find()) {
            Toast.makeText(this, "Invalid NIC", Toast.LENGTH_SHORT).show();
        } else {

            ServiceCentre serviceCentre = new ServiceCentre();
            serviceCentre.setCentreId(center_ids.get(centers.indexOf(_center)));

            Vehicle vehicle = new Vehicle();
            vehicle.setVehicleId(vehicle_ids.get(vehicles.indexOf(_vehicle)));

            DriverDetail driverDetail = new DriverDetail();
            driverDetail.setDob(_dob);
            driverDetail.setNic(_nic);
            driverDetail.setAddress(_address);
            driverDetail.setVehicle(vehicle);

            User user = new User();
            user.setEmail(_email);
            user.setFirstName(_firstName);
            user.setLastName(_lastName);
            user.setLocation(_location);
            user.setPhoneNumber(_phoneNumber);
            user.setDriverDetail(driverDetail);
            user.setServiceCentre(serviceCentre);

            Call<ResponseBody> call = adminClient.addDriver(token, user);
            //Show progress
            mProgressDialog.setMessage("Adding Driver...");
            mProgressDialog.show();

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();
                    //200 status code
                    if (response.code() == 200) {
                        Toast.makeText(AddDriverActivity.this, "Driver added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddDriverActivity.this, AdminActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(AddDriverActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(AddDriverActivity.this, "Something! went wrong" + t.toString(), Toast.LENGTH_SHORT).show();
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddDriverActivity.this, android.R.layout.simple_spinner_item, centers);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    center.setAdapter(adapter);
                    centersLoaded = true;

                    if (centersLoaded) mProgressDialog.dismiss();

                } else {
                    Toast.makeText(AddDriverActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<ServiceCentre>> call, Throwable t) {
                Toast.makeText(AddDriverActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddDriverActivity.this, android.R.layout.simple_spinner_item, vehicles);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    vehicle.setAdapter(adapter);
                    vehiclesLoaded = true;

                    if (vehiclesLoaded) mProgressDialog.dismiss();

                } else {
                    Toast.makeText(AddDriverActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Vehicle>> call, Throwable t) {
                Toast.makeText(AddDriverActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleAdminNav(item, AddDriverActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Check if authorization token is valid
        AuthHandler.validate(AddDriverActivity.this, "admin");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
