package com.example.express_delivery_mobile.Service;

import com.example.express_delivery_mobile.Model.Disputes;
import com.example.express_delivery_mobile.Model.Mail;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MailClient {

    //Get list of upcoming mails for user
    @GET("get-upcoming-packages")
    Call<List<Mail>> getAllUpcomingMails(@Header("Authorization") String token);

    //Get list of upcoming mails for user
    @GET("get-outgoing-packages")
    Call<List<Mail>> getAllOutgoingMails(@Header("Authorization") String token);

    //Send Mail
    @POST("send-package")
    Call<ResponseBody> sendPackage(@Header("Authorization") String token, @Body Mail mail);

    //Cancel package request
    @POST("cancel-package/{mailId}")
    Call<ResponseBody> cancelPackage(@Header("Authorization") String token, @Path("mailId") int mailId);

    //Open dispute
    @POST("open-dispute")
    Call<ResponseBody> openDispute(@Header("Authorization") String token, @Body Disputes disputes);

}
