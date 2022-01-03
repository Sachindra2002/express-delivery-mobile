package com.example.express_delivery_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.express_delivery_mobile.Util.AuthHandler;
import com.example.express_delivery_mobile.Util.NavHandler;
import com.google.android.material.navigation.NavigationView;

public class ViewInquiryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView customerName, customerEmail, customerContact, inquiryType, inquiry, date, inquiryId;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;

    private String token;
    private String email;
    private String firstName;
    private String lastName;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if authorization token is valid
        String result = AuthHandler.validate(ViewInquiryActivity.this, "admin");

        if (result != null) {
            if (result.equals("unauthorized") || result.equals("expired")) return;
        }

        //Load layout only after authorization
        setContentView(R.layout.activity_view_inquiry);

        customerName = findViewById(R.id.customer_name);
        customerEmail = findViewById(R.id.customer_email);
        customerContact = findViewById(R.id.contact);
        inquiryType = findViewById(R.id.type);
        inquiry = findViewById(R.id.inquiry);
        date = findViewById(R.id.date);
        inquiryId = findViewById(R.id.inquiry_id);

        inquiryId.setText("Inquiry ID #" + getIntent().getStringExtra("inquiry_id"));
        customerName.setText(getIntent().getStringExtra("customer_name"));
        customerEmail.setText(getIntent().getStringExtra("customer_email"));
        customerContact.setText(getIntent().getStringExtra("customer_contact"));
        inquiryType.setText(getIntent().getStringExtra("inquiry_type"));
        inquiry.setText(getIntent().getStringExtra("inquiry"));
        date.setText(getIntent().getStringExtra("date"));

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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleAdminNav(item, ViewInquiryActivity.this);

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
