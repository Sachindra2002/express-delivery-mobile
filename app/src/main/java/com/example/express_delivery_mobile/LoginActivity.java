package com.example.express_delivery_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.express_delivery_mobile.Model.LoginCredentials;
import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.example.express_delivery_mobile.Service.UserClient;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText mEmailEditText, mPasswordEditText;
    private TextView register;
    private Button mLoginButton;
    private ProgressDialog mProgressDialog;

    private SharedPreferences sharedPreferences;

    private UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkRole();

        //Initialize views
        mEmailEditText = findViewById(R.id.input_email);
        mPasswordEditText = findViewById(R.id.input_password);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mProgressDialog = new ProgressDialog(this);

        //When login button is clicked
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        register = findViewById(R.id.textView5);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        //If there are empty fields
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter valid data!", Toast.LENGTH_SHORT).show();
        } else {

            //Create login credentials
            LoginCredentials loginCredentials = new LoginCredentials(email, password);
            Call<User> call = userClient.login(loginCredentials);

            //Show progress
            mProgressDialog.setMessage("Getting things ready...");
            mProgressDialog.show();

            call.enqueue(new Callback<User>() {

                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    mProgressDialog.dismiss();

                    //200 status code
                    if (response.isSuccessful()) {

                        Toast.makeText(LoginActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();

                        //Save authorization token and role
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("auth_token", response.body().getToken());
                        editor.putString("role", response.body().getUserRole());
                        editor.putString("email", response.body().getEmail());
                        editor.putString("firstName", response.body().getFirstName());
                        editor.putString("lastName", response.body().getLastName());
                        editor.apply();

                        //Direct user to respective home page
                        String role = response.body().getUserRole();

                        Intent homePageIntent = null;

                        if (role.equals("customer")) {
                            homePageIntent = new Intent(LoginActivity.this, MainActivity.class);
                        } else if (role.equals("admin")) {
                            homePageIntent = new Intent(LoginActivity.this, AdminActivity.class);
                        } else if (role.equals("agent")) {
                            homePageIntent = new Intent(LoginActivity.this, AgentActivity.class);
                        } else if (role.equals("driver")) {
                            homePageIntent = new Intent(LoginActivity.this, DriverActivity.class);
                        }

                        homePageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homePageIntent);
                        finish();
                    }
                    // Invalid password
                    else if (response.code() == 403) {
                        Toast.makeText(LoginActivity.this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Something! went wrong", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void checkRole() {
        sharedPreferences = LoginActivity.this.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        String role = sharedPreferences.getString("role", null);

        if (role != null) {
            if (role.equals("customer")) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (role.equals("admin")) {
                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            } else if (role.equals("agent")) {
                Intent intent = new Intent(LoginActivity.this, AgentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (role.equals("driver")) {
                Intent intent = new Intent(LoginActivity.this, DriverActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
    }

    @Override
    protected void onNightModeChanged(int mode) {
        super.onNightModeChanged(mode);
    }
}
