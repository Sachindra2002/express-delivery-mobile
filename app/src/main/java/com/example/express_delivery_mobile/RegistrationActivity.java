package com.example.express_delivery_mobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.example.express_delivery_mobile.Service.UserClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {
    EditText email, firstName, lastName, phoneNumber, password, password2;
    TextView login;
    Spinner location;
    Button register;

    private ProgressDialog mProgressDialog;

    private UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        email = findViewById(R.id.email);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        phoneNumber = findViewById(R.id.phone_number);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password_2);
        location = findViewById(R.id.user_location);
        register = findViewById(R.id.register_button);
        login = findViewById(R.id.login);

        mProgressDialog = new ProgressDialog(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location.setAdapter(adapter);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void register() {
        String _email = email.getText().toString();
        String _firstName = firstName.getText().toString();
        String _lastName = lastName.getText().toString();
        String _phoneNumber = phoneNumber.getText().toString();
        String _location = location.getSelectedItem().toString();
        String _password = password.getText().toString();
        String _password2 = password2.getText().toString();

        if (TextUtils.isEmpty(_email) || TextUtils.isEmpty(_firstName) || TextUtils.isEmpty(_lastName) || TextUtils.isEmpty(_phoneNumber) ||
                TextUtils.isEmpty(_location) || TextUtils.isEmpty(_password) || TextUtils.isEmpty(_password2)) {
            Toast.makeText(this, "Please enter valid data!", Toast.LENGTH_SHORT).show();
        } else if (!_password.equals(_password2)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
        } else {
            User user = new User();
            user.setEmail(_email);
            user.setFirstName(_firstName);
            user.setLastName(_lastName);
            user.setPhoneNumber(_phoneNumber);
            user.setLocation(_location);
            user.setPassword(_password);
            user.setConfirmPassword(_password2);

            Call<ResponseBody> call = userClient.register(user);

            //Show progress
            mProgressDialog.setMessage("Creating new account...");
            mProgressDialog.show();

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();
                    if (response.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegistrationActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
