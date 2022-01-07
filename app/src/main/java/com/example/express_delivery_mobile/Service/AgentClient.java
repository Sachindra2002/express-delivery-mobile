package com.example.express_delivery_mobile.Service;

import com.example.express_delivery_mobile.Model.Documents;
import com.example.express_delivery_mobile.Model.Mail;
import com.example.express_delivery_mobile.Model.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface AgentClient {

    //Get list of assigned mails for driver
    @GET("get-new-packages")
    Call<List<Mail>> getNewShipments(@Header("Authorization") String token);

    //Get list of drivers
    @GET("get-drivers")
    Call<List<User>> getDrivers(@Header("Authorization") String token);

    //Accept package
    @POST("accept-package-agent/{mailId}")
    Call<ResponseBody> acceptPackage(@Header("Authorization") String token, @Path("mailId") int mailId);

    //Reject package
    @POST("reject-package-agent/{mailId}")
    Call<ResponseBody> rejectPackage(@Header("Authorization") String token, @Path("mailId") int mailId);

    //Get accepted packages by agent
    @GET("get-accepted-packages-agent")
    Call<List<Mail>> getAllAcceptedMails(@Header("Authorization") String token);

    //Get accepted packages by agent
    @GET("get-transit-packages-agent")
    Call<List<Mail>> getAllTransitMails(@Header("Authorization") String token);

    //Assign driver to package
    @POST("assign-driver")
    Call<ResponseBody> assignDriver(@Header("Authorization") String token, @Body Mail mail);

    //Get driver documents
    @GET("get-driver-documents/{email}")
    Call<List<Documents>> getDriverDocuments(@Header("Authorization") String token,@Path("email") String email);

    //Download driver document
    @GET("download/{fileName}")
    Call<ResponseBody> downloadDriverDocument(@Header("Authorization") String token, @Path("fileName") String fileName);
}
