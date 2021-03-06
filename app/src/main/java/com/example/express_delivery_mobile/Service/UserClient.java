package com.example.express_delivery_mobile.Service;

import com.example.express_delivery_mobile.Model.ChangePasswordRequest;
import com.example.express_delivery_mobile.Model.LoginCredentials;
import com.example.express_delivery_mobile.Model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserClient {

    //Login as user
    @POST("auth/login")
    Call<User> login(@Body LoginCredentials loginCredentials);

    //Get user details of logged in user
    @GET("user")
    Call<User> getUser(@Header("Authorization") String token);

    //Register
    @POST("register")
    Call<ResponseBody> register(@Body User user);

    //Change Password
    @POST("auth/change-password")
    Call<ResponseBody> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest changePasswordRequest);

    //Change Password
    @POST("change-phone-number")
    Call<ResponseBody> changePhoneNumber(@Header("Authorization") String token, @Body User user);
}
