package com.example.express_delivery_mobile.Service;

import com.example.express_delivery_mobile.Model.Mail;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface MailClient {

    //Get list of upcoming mails for user
    @GET("get-upcoming-packages")
    Call<List<Mail>> getAllUpcomingMails(@Header("Authorization") String token);
}
