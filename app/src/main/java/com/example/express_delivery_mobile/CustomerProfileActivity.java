package com.example.express_delivery_mobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import com.example.express_delivery_mobile.Model.ChangePasswordRequest;
import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.example.express_delivery_mobile.Service.UserClient;
import com.example.express_delivery_mobile.Util.AuthHandler;
import com.example.express_delivery_mobile.Util.NavHandler;
import com.google.android.material.navigation.NavigationView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView fullName, _email, contact, city;
    EditText oldPassword, newPassword, confirmPassword, newPhoneNumber;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;

    private String token;
    private String email;
    private String firstName;
    private String lastName;
    private String username;

    private Button changePassword, updatePhoneNumber;

    private User user;

    //Driver Retrofit client
    UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //Check if authorization token is valid
        super.onCreate(savedInstanceState);
        String result = AuthHandler.validate(CustomerProfileActivity.this, "customer");

        if (result != null) {
            if (result.equals("unauthorized") || result.equals("expired")) return;
        }

        //Load layout only after authorization
        setContentView(R.layout.activity_customer_profile);

        fullName = findViewById(R.id.driverName);
        _email = findViewById(R.id.email);
        contact = findViewById(R.id.contact);
        city = findViewById(R.id.city);
        changePassword = findViewById(R.id.change_password);
        updatePhoneNumber = findViewById(R.id.update_number);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleChangePassword();
            }
        });

        updatePhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleUpdatePhoneNumber();
            }
        });

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

        getUserDetails();
    }

    private void handleUpdatePhoneNumber() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(CustomerProfileActivity.this, R.style.MyAlertDialogTheme);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.update_phone_number_dialog, null);
        mBuilder.setTitle("Update Phone Number");

        newPhoneNumber = v.findViewById(R.id.new_phone_number);

        mBuilder.setPositiveButton("Update Phone Number", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                changePhoneNumber();
            }
        });
        mBuilder.setView(v);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void changePhoneNumber() {
        String _new_phone_number = newPhoneNumber.getText().toString();

        if (TextUtils.isEmpty(_new_phone_number)) {
            Toast.makeText(this, "Please enter valid data!", Toast.LENGTH_SHORT).show();
        } else {
            User user = new User();
            user.setPhoneNumber(_new_phone_number);

            Call<ResponseBody> call = userClient.changePhoneNumber(token, user);
            //Show progress
            mProgressDialog.setMessage("Updating phone number...");
            mProgressDialog.show();

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();
                    //200 status code
                    if (response.code() == 200) {
                        Toast.makeText(CustomerProfileActivity.this, "phone number changed successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CustomerProfileActivity.this, CustomerProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(CustomerProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(CustomerProfileActivity.this, "Something! went wrong" + t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void handleChangePassword() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(CustomerProfileActivity.this, R.style.MyAlertDialogTheme);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.change_password_dialog, null);
        mBuilder.setTitle("Change Password");

        oldPassword = v.findViewById(R.id.old_password);
        newPassword = v.findViewById(R.id.new_password);
        confirmPassword = v.findViewById(R.id.confirm_password);

        mBuilder.setPositiveButton("Change Password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updatePassword();
            }
        });
        mBuilder.setView(v);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }

    private void updatePassword() {
        String _old_password = oldPassword.getText().toString();
        String _new_password = newPassword.getText().toString();
        String _confirm_password = confirmPassword.getText().toString();

        if (TextUtils.isEmpty(_old_password) || TextUtils.isEmpty(_new_password) || TextUtils.isEmpty(_confirm_password)) {
            Toast.makeText(this, "Please enter valid data!", Toast.LENGTH_SHORT).show();
        } else if (!_new_password.equals(_confirm_password)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
            changePasswordRequest.setOldPassword(_old_password);
            changePasswordRequest.setNewPassword(_new_password);

            Call<ResponseBody> call = userClient.changePassword(token, changePasswordRequest);
            //Show progress
            mProgressDialog.setMessage("Changing password...");
            mProgressDialog.show();

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();
                    //200 status code
                    if (response.code() == 200) {
                        Toast.makeText(CustomerProfileActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CustomerProfileActivity.this, CustomerProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(CustomerProfileActivity.this, "Old password is incorrect!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(CustomerProfileActivity.this, "Something! went wrong" + t.toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void getUserDetails() {
        Call<User> call = userClient.getUser(token);

        //Show Progress
        mProgressDialog.setMessage("Loading Profile..");
        mProgressDialog.show();

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                if (user != null) {
                    fullName.setText(user.getFirstName() + " " + user.getLastName());
                    _email.setText(String.valueOf(user.getEmail()));
                    contact.setText(String.valueOf(user.getPhoneNumber()));
                    city.setText(String.valueOf(user.getLocation()));
                } else {
                    Toast.makeText(CustomerProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(CustomerProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleDriverNav(item, CustomerProfileActivity.this);

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
