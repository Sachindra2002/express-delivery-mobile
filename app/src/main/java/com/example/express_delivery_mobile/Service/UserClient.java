package com.example.express_delivery_mobile.Service;

import com.example.express_delivery_mobile.Model.LoginCredentials;
import com.example.express_delivery_mobile.Model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserClient {

    //Login as user
    @POST("auth/login")
    Call<User> login(@Body LoginCredentials loginCredentials);
}
