package com.example.express_delivery_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.express_delivery_mobile.Model.Disputes;
import com.example.express_delivery_mobile.Model.Inquiry;
import com.example.express_delivery_mobile.Service.AdminClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.example.express_delivery_mobile.Util.AuthHandler;
import com.example.express_delivery_mobile.Util.NavHandler;
import com.google.android.material.navigation.NavigationView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewDisputeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView customerName, customerEmail, customerContact, disputeType, dispute, date, disputeId, mailId;
    Button respond;
    EditText response;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;

    private String token;
    private String email;
    private String firstName;
    private String lastName;
    private String username;

    AdminClient adminClient = RetrofitClientInstance.getRetrofitInstance().create(AdminClient.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if authorization token is valid
        String result = AuthHandler.validate(ViewDisputeActivity.this, "admin");

        if (result != null) {
            if (result.equals("unauthorized") || result.equals("expired")) return;
        }

        //Load layout only after authorization
        setContentView(R.layout.activity_view_dispute);

        customerName = findViewById(R.id.customer_name);
        customerEmail = findViewById(R.id.customer_email);
        customerContact = findViewById(R.id.contact);
        disputeType = findViewById(R.id.type);
        dispute = findViewById(R.id.dispute);
        date = findViewById(R.id.date);
        disputeId = findViewById(R.id.dispute_id);
        mailId = findViewById(R.id.mail_id);
        respond = findViewById(R.id.respond_button);
        response = findViewById(R.id.response);

        respond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleResponse();
            }
        });

        disputeId.setText("Dispute ID #" + getIntent().getStringExtra("dispute_id"));
        mailId.setText(getIntent().getStringExtra("mail_id"));
        customerName.setText(getIntent().getStringExtra("customer_name"));
        customerEmail.setText(getIntent().getStringExtra("customer_email"));
        customerContact.setText(getIntent().getStringExtra("customer_contact"));
        disputeType.setText(getIntent().getStringExtra("dispute_type"));
        dispute.setText(getIntent().getStringExtra("dispute"));
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

    private void handleResponse() {
        String _response = response.getText().toString();

        if (TextUtils.isEmpty(_response)) {
            Toast.makeText(this, "Please enter valid data!", Toast.LENGTH_SHORT).show();
        } else {
            Disputes disputes = new Disputes();
            disputes.setDisputeId(Integer.parseInt(getIntent().getStringExtra("dispute_id")));
            disputes.setResponse(_response);

            Call<ResponseBody> call = adminClient.respondDispute(token, disputes);

            //Show progress
            mProgressDialog.setMessage("Getting things ready...");
            mProgressDialog.show();

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();

                    //200 status code
                    if (response.code() == 200) {
                        Toast.makeText(ViewDisputeActivity.this, "Successfully Responded", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ViewDisputeActivity.this, DisputesActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ViewDisputeActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(ViewDisputeActivity.this, "Something! went wrong" + t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleAdminNav(item, ViewDisputeActivity.this);

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
