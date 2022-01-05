package com.example.express_delivery_mobile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;
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

import com.example.express_delivery_mobile.Adapter.LocalStoragePackagesAdapter;
import com.example.express_delivery_mobile.Model.LocalStoragePackages;
import com.example.express_delivery_mobile.Util.AuthHandler;
import com.example.express_delivery_mobile.Util.NavHandler;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class ViewLocalStoragePackagesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;

    private RecyclerView recyclerView;
    private SearchView searchView;
    private LocalStoragePackagesAdapter localStoragePackagesAdapter;

    private List<LocalStoragePackages> packages;

    private String email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_local_packages);

        //Check if authorization token is valid
        AuthHandler.validate(ViewLocalStoragePackagesActivity.this, "driver");

        //Retrieve username
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", null);

        mProgressDialog = new ProgressDialog(this);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Local Stored Packages");

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

        //Setup classes list
        packages = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        localStoragePackagesAdapter = new LocalStoragePackagesAdapter(this, packages, mProgressDialog);
        recyclerView.setAdapter(localStoragePackagesAdapter);

        getLocalPackages();

    }

    @SuppressLint("Range")
    private void getLocalPackages() {
        //Show progress
        mProgressDialog.setMessage("Loading Packages...");
        mProgressDialog.show();

        Cursor cursor = getContentResolver().query(Uri.parse("content://com.example.express_delivery_mobile.Provider/saved_packages"), null, null, null, null);

        // iteration of the cursor
        // to print whole table
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if (cursor.getString(cursor.getColumnIndex("driverEmail")).equals(email)) {
                    LocalStoragePackages localStoragePackages = new LocalStoragePackages();
                    localStoragePackages.setMailId(cursor.getInt(cursor.getColumnIndex("mailId")));
                    localStoragePackages.setDriverId(cursor.getString(cursor.getColumnIndex("driverId")));
                    localStoragePackages.setDriverEmail(cursor.getString(cursor.getColumnIndex("driverEmail")));
                    localStoragePackages.setCustomerName(cursor.getString(cursor.getColumnIndex("customerName")));
                    localStoragePackages.setCustomerContact(cursor.getString(cursor.getColumnIndex("customerContact")));
                    localStoragePackages.setCustomerEmail(cursor.getString(cursor.getColumnIndex("customerEmail")));
                    localStoragePackages.setTransportationStatus(cursor.getString(cursor.getColumnIndex("transportationStatus")));
                    localStoragePackages.setPackageStatus(cursor.getString(cursor.getColumnIndex("packageStatus")));
                    localStoragePackages.setPickUpAddress(cursor.getString(cursor.getColumnIndex("pickupAddress")));
                    localStoragePackages.setDropOffAddress(cursor.getString(cursor.getColumnIndex("dropOffAddress")));
                    localStoragePackages.setWeight(cursor.getString(cursor.getColumnIndex("weight")));
                    localStoragePackages.setParcelType(cursor.getString(cursor.getColumnIndex("parcelType")));
                    localStoragePackages.setPaymentMethod(cursor.getString(cursor.getColumnIndex("paymentMethod")));
                    localStoragePackages.setTotalCost(cursor.getString(cursor.getColumnIndex("totalCost")));

                    packages.add(localStoragePackages);
                }
                cursor.moveToNext();
            }
            localStoragePackagesAdapter.setPackages(packages);
        } else {
            Toast.makeText(this, "You have no Saved packages!", Toast.LENGTH_SHORT).show();
        }

        if (packages.size() == 0)
            Toast.makeText(this, "You have no Saved packages!", Toast.LENGTH_SHORT).show();
        mProgressDialog.dismiss();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleDriverNav(item, ViewLocalStoragePackagesActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Check if authorization token is valid
        AuthHandler.validate(ViewLocalStoragePackagesActivity.this, "driver");
    }

    @Override
    public void onBackPressed() {
         super.onBackPressed();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
