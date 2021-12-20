package com.example.express_delivery_mobile.Service;

import com.example.express_delivery_mobile.Model.Mail;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AgentClient {

    //Get list of assigned mails for driver
    @GET("get-new-packages")
    Call<List<Mail>> getNewShipments(@Header("Authorization") String token);

    @POST("accept-package-agent/{mailId}")
    Call<ResponseBody> acceptPackage(@Header("Authorization") String token, @Path("mailId") int mailId);

    @POST("reject-package-agent/{mailId}")
    Call<ResponseBody> rejectPackage(@Header("Authorization") String token, @Path("mailId") int mailId);

    @GET("get-accepted-packages-agent")
    Call<List<Mail>> getAllAcceptedMails(@Header("Authorization") String token);
}
