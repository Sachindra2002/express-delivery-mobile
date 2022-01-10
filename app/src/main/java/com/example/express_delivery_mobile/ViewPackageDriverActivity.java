package com.example.express_delivery_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.express_delivery_mobile.Util.AuthHandler;
import com.example.express_delivery_mobile.Util.NavHandler;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewPackageDriverActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView mailId, description, date, status, dropOffDate, pickUpDate, weight, parcelType, receiverName, receiverContact, receiverEmail, paymentMethod,
            totalCost, noPieces, pickUpAddress, dropOffAddress, customerName, customerContact, centerName, centerAddress, driverName, driverContact;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;

    private CardView driverInfo;

    private String token;
    private String email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if authorization token is valid
        String result = AuthHandler.validate(ViewPackageDriverActivity.this, "driver");

        if (result != null) {
            if (result.equals("unauthorized") || result.equals("expired")) return;
        }

        //Load layout only after authorization
        setContentView(R.layout.activity_view_package_agent);

        mailId = findViewById(R.id.package_id);
        description = findViewById(R.id.package_description);
        date = findViewById(R.id.package_date);
        status = findViewById(R.id.package_status);
        dropOffDate = findViewById(R.id.drop_off_date);
        pickUpDate = findViewById(R.id.pick_up_date);
        weight = findViewById(R.id.weight);
        parcelType = findViewById(R.id.parcel_type);
        receiverName = findViewById(R.id.receiver_name);
        receiverContact = findViewById(R.id.receiver_contact);
        receiverEmail = findViewById(R.id.receiver_email);
        paymentMethod = findViewById(R.id.payment_method);
        totalCost = findViewById(R.id.total_cost);
        noPieces = findViewById(R.id.pieces);
        pickUpAddress = findViewById(R.id.pick_up_address);
        dropOffAddress = findViewById(R.id.drop_off_address);
        customerName = findViewById(R.id.customer_name);
        customerContact = findViewById(R.id.customer_contact);
        centerName = findViewById(R.id.center);
        centerAddress = findViewById(R.id.centerAddress);
        driverName = findViewById(R.id.driver_name);
        driverContact = findViewById(R.id.driver_contact);
        driverInfo = findViewById(R.id.card_4);

        Date d = new Date(getIntent().getLongExtra("created_at", -1));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String _date = sdf.format(d);

        mailId.setText("Details for package ID #" + getIntent().getIntExtra("mail_id", 0));
        description.setText(getIntent().getStringExtra("package_description"));
        date.setText(_date);
        status.setText(getIntent().getStringExtra("package_status"));
        dropOffDate.setText(getIntent().getStringExtra("drop_off_date"));
        pickUpDate.setText(getIntent().getStringExtra("pick_up_date"));
        weight.setText(getIntent().getStringExtra("weight"));
        parcelType.setText(getIntent().getStringExtra("parcel_type"));
        receiverName.setText(getIntent().getStringExtra("receiver_name"));
        receiverContact.setText(getIntent().getStringExtra("receiver_contact"));
        receiverEmail.setText(getIntent().getStringExtra("receiver_email"));
        paymentMethod.setText(getIntent().getStringExtra("payment_method"));
        totalCost.setText(getIntent().getStringExtra("total_cost"));
        noPieces.setText(getIntent().getStringExtra("pieces"));
        pickUpAddress.setText(getIntent().getStringExtra("pick_up_address"));
        dropOffAddress.setText(getIntent().getStringExtra("drop_off_address"));
        customerName.setText(getIntent().getStringExtra("customer_name"));
        customerContact.setText(getIntent().getStringExtra("customer_contact"));
        centerName.setText(getIntent().getStringExtra("center_name"));
        centerAddress.setText(getIntent().getStringExtra("center_address"));

        if (getIntent().getStringExtra("driver_name") != null) {
            driverName.setText(getIntent().getStringExtra("driver_name"));
            driverContact.setText(getIntent().getStringExtra("driver_contact"));
        } else {
            driverInfo.setVisibility(View.INVISIBLE);
        }


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

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleDriverNav(item, ViewPackageDriverActivity.this);

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
