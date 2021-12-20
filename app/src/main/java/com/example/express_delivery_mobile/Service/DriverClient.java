package com.example.express_delivery_mobile.Service;

import com.example.express_delivery_mobile.Model.Mail;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DriverClient {

    //Get list of assigned mails for driver
    @GET("get-assigned-packages")
    Call<List<Mail>> getAllAssignedMails(@Header("Authorization") String token);

    //Get list of accepted mails for driver
    @GET("get-accepted-packages")
    Call<List<Mail>> getAllAcceptedMails(@Header("Authorization") String token);

    //Get list of started mails for driver
    @GET("get-started-packages")
    Call<List<Mail>> getAllStartedMails(@Header("Authorization") String token);

    //Accepted packages
    @POST("accept-package/{mailId}")
    Call<ResponseBody> acceptPackage(@Header("Authorization") String token, @Path("mailId") int mailId);

    //Start package
    @POST("start-package/{mailId}")
    Call<ResponseBody> startPackage(@Header("Authorization") String token, @Path("mailId") int mailId);

    //confirm package pickup
    @POST("confirm-pickup-package/{mailId}")
    Call<ResponseBody> confirmPickupPackage(@Header("Authorization") String token, @Path("mailId") int mailId);
}
