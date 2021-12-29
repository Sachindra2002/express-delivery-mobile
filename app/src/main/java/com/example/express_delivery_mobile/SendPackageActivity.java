package com.example.express_delivery_mobile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.express_delivery_mobile.Model.Mail;
import com.example.express_delivery_mobile.Service.DriverClient;
import com.example.express_delivery_mobile.Service.MailClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.example.express_delivery_mobile.Util.AuthHandler;
import com.example.express_delivery_mobile.Util.NavHandler;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendPackageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    EditText receiverFirstName, receiverLastName, receiverPhoneNumber, receiverEmail, weight, pieces, _date, _time, description, receiverAddress, pickupAddress, totalCost;
    Spinner receiverCity, typeOfParcel, paymentMethod;
    Button sendParcel;

    DatePickerDialog datePickerDialog;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;

    private String token;
    private String email;
    private String firstName;
    private String lastName;

    //Mail Retrofit client
    MailClient mailClient = RetrofitClientInstance.getRetrofitInstance().create(MailClient.class);
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if authorization token is valid
        String result = AuthHandler.validate(SendPackageActivity.this, "customer");

        if (result != null) {
            if (result.equals("unauthorized") || result.equals("expired")) return;
        }

        //Load layout only after authorization
        setContentView(R.layout.activity_send_package);

        //Initialize Views
        receiverFirstName = findViewById(R.id.receiver_first_name);
        receiverLastName = findViewById(R.id.receiver_last_name);
        receiverPhoneNumber = findViewById(R.id.receiver_phone);
        receiverEmail = findViewById(R.id.receiver_email);
        weight = findViewById(R.id.weight);
        pieces = findViewById(R.id.pieces);
        _date = findViewById(R.id.date);
        _time = findViewById(R.id.time);
        description = findViewById(R.id.description);
        receiverAddress = findViewById(R.id.receiver_address);
        pickupAddress = findViewById(R.id.pickup_address);
        totalCost = findViewById(R.id.total_cost);
        receiverCity = findViewById(R.id.receiver_city);
        typeOfParcel = findViewById(R.id.parcel_type);
        paymentMethod = findViewById(R.id.payment_method);
        sendParcel = findViewById(R.id.button_send_package);
        mProgressDialog = new ProgressDialog(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.receiver_city, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.type_of_parcel, android.R.layout.simple_dropdown_item_1line);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.payment_method, android.R.layout.simple_dropdown_item_1line);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        receiverCity.setAdapter(adapter);
        typeOfParcel.setAdapter(adapter2);
        paymentMethod.setAdapter(adapter3);

        sendParcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitParcel();
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
        getSupportActionBar().setTitle("Send Package");

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

        _date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SendPackageActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        _time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SendPackageActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        _time.setText(i + ":" + i1);
                    }

                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        TextWatcher inputTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                calculateCost();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        weight.addTextChangedListener(inputTextWatcher);

    }

    private void calculateCost() {
        String _weight = weight.getText().toString();

        if (_weight.contains(" ")) {
            totalCost.setText("100 LKR");
        } else if (_weight.contains("1")) {
            totalCost.setText("300 LKR");
        } else if (_weight.contains("2")) {
            totalCost.setText("500 LKR");
        } else if (_weight.contains("3")) {
            totalCost.setText("800 LKR");
        } else if (_weight.contains("4")) {
            totalCost.setText("1000 LKR");
        }
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        _date.setText(sdf.format(myCalendar.getTime()));
    }


    private void submitParcel() {
        String _receiverFirstName, _receiverLastName, _receiverPhoneNumber, _receiverEmail, _weight, _pieces, __date, __time, _description, _receiverAddress,
                _pickupAddress, _totalCost, _receiverCity, _typeOfParcel, _paymentMethod;

        _receiverFirstName = receiverFirstName.getText().toString();
        _receiverLastName = receiverLastName.getText().toString();
        _receiverPhoneNumber = receiverPhoneNumber.getText().toString();
        _receiverEmail = receiverEmail.getText().toString();
        _weight = weight.getText().toString();
        _pieces = pieces.getText().toString();
        __date = _date.getText().toString();
        __time = _time.getText().toString();
        _description = description.getText().toString();
        _receiverAddress = receiverAddress.getText().toString();
        _pickupAddress = pickupAddress.getText().toString();
        _totalCost = "1000 LKR";
        _receiverCity = receiverCity.getSelectedItem().toString();
        _typeOfParcel = typeOfParcel.getSelectedItem().toString();
        _paymentMethod = paymentMethod.getSelectedItem().toString();

        if (TextUtils.isEmpty(_receiverFirstName) || TextUtils.isEmpty(_receiverLastName) || TextUtils.isEmpty(_receiverPhoneNumber) || TextUtils.isEmpty(_receiverEmail)
                || TextUtils.isEmpty(_weight) || TextUtils.isEmpty(_pieces) || TextUtils.isEmpty(__date) || TextUtils.isEmpty(__time) || TextUtils.isEmpty(_description) ||
                TextUtils.isEmpty(_receiverAddress) || TextUtils.isEmpty(_pickupAddress)) {
            Toast.makeText(this, "Please enter valid data!", Toast.LENGTH_SHORT).show();
        } else {
            //Create mail object
            Mail mail = new Mail(_pickupAddress, _receiverAddress, _receiverFirstName, _receiverLastName, _receiverPhoneNumber, _receiverEmail, _receiverCity, _typeOfParcel, _weight, _pieces, _paymentMethod, __date, __time, _totalCost, _description);
            Call<ResponseBody> call = mailClient.sendPackage(token, mail);

            //Show progress
            mProgressDialog.setMessage("Getting things ready...");
            mProgressDialog.show();

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();

                    //200 status code
                    if (response.code() == 201) {
                        Intent intent = new Intent(SendPackageActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SendPackageActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(SendPackageActivity.this, "Something! went wrong" + t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleDriverNav(item, SendPackageActivity.this);

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
