package com.example.express_delivery_mobile.Service;

import com.example.express_delivery_mobile.Model.Mail;
import com.example.express_delivery_mobile.Model.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
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

    //Get list of picked up mails for driver
    @GET("get-picked-up-packages")
    Call<List<Mail>> getAllPickedUpMails(@Header("Authorization") String token);

    //Get list Transit packages
    @GET("get-transit-packages-driver")
    Call<List<Mail>> getAllTransitMails(@Header("Authorization") String token);

    //Get out for delivery packages
    @GET("get-out-for-delivery-packages-driver")
    Call<List<Mail>> getOutForDeliveryMails(@Header("Authorization") String token);

    //Get all delivered packages
    @GET("get-delivered-packages-driver")
    Call<List<Mail>> getDeliveredMails(@Header("Authorization") String token);

    //Get all delivered packages
    @GET("get-all-packages-driver")
    Call<List<Mail>> getAllDriverPackages(@Header("Authorization") String token);

    //Accepted packages
    @POST("accept-package/{mailId}")
    Call<ResponseBody> acceptPackage(@Header("Authorization") String token, @Path("mailId") int mailId);

    //Accepted packages
    @POST("reject-package/{mailId}")
    Call<ResponseBody> rejectPackage(@Header("Authorization") String token, @Path("mailId") int mailId);

    //Start package
    @POST("start-package/{mailId}")
    Call<ResponseBody> startPackage(@Header("Authorization") String token, @Path("mailId") int mailId);

    //confirm package pickup
    @POST("confirm-pickup-package/{mailId}")
    Call<ResponseBody> confirmPickupPackage(@Header("Authorization") String token, @Path("mailId") int mailId);

    //confirm package delivered
    @POST("confirm-delivered-package/{mailId}")
    Call<ResponseBody> confirmPackageDelivered(@Header("Authorization") String token, @Path("mailId") int mailId);

    //transit package to warehouse
    @POST("transit-package")
    Call<ResponseBody> transitPackage(@Header("Authorization") String token, @Body Mail mail);

    //Start out for delivery
    @POST("out-for-delivery")
    Call<ResponseBody> startDelivery(@Header("Authorization") String token, @Body Mail mail);

    //Update status
    @POST("update-status")
    Call<ResponseBody> updateStatus(@Header("Authorization") String token, @Body User user);
}
