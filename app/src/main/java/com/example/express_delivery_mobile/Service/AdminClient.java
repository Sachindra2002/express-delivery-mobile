package com.example.express_delivery_mobile.Service;

import com.example.express_delivery_mobile.Model.Disputes;
import com.example.express_delivery_mobile.Model.Documents;
import com.example.express_delivery_mobile.Model.DriverDetail;
import com.example.express_delivery_mobile.Model.Inquiry;
import com.example.express_delivery_mobile.Model.Mail;
import com.example.express_delivery_mobile.Model.ServiceCentre;
import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.Model.Vehicle;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AdminClient {

    //Get list of service centers
    @GET("get-service-centers")
    Call<List<ServiceCentre>> getServiceCenters(@Header("Authorization") String token);

    //Get list of available vehicles
    @GET("get-available-vehicles")
    Call<List<Vehicle>> getAvailableVehicles(@Header("Authorization") String token);

    //Add Driver
    @POST("add-driver")
    Call<ResponseBody> addDriver(@Header("Authorization") String token, @Body User user);

    //Add Driver
    @POST("add-agent")
    Call<ResponseBody> addAgent(@Header("Authorization") String token, @Body User user);

    //Get list of drivers
    @GET("get-drivers-admin")
    Call<List<User>> getDrivers(@Header("Authorization") String token);

    //Get list of drivers
    @GET("get-agents")
    Call<List<User>> getAgents(@Header("Authorization") String token);

    //Get driver documents
    @GET("get-driver-documents-admin/{email}")
    Call<List<Documents>> getDriverDocuments(@Header("Authorization") String token, @Path("email") String email);

    //Download driver document
    @GET("download-admin/{fileName}")
    Call<ResponseBody> downloadDriverDocument(@Header("Authorization") String token, @Path("fileName") String fileName);

    //Get general inquiries
    @GET("get-inquiries")
    Call<List<Inquiry>> getInquiries(@Header("Authorization") String token);

    //Get package inquiries
    @GET("get-disputes")
    Call<List<Disputes>> getDisputes(@Header("Authorization") String token);

    //Respond to inquiry
    @POST("send-response-inquiry")
    Call<ResponseBody> respondInquiry(@Header("Authorization") String token, @Body Inquiry inquiry);

    //Respond to disputes
    @POST("send-response-dispute")
    Call<ResponseBody> respondDispute(@Header("Authorization") String token, @Body Disputes disputes);

    //Change Service Center
    @POST("change-driver-center")
    Call<ResponseBody> changeDriverServiceCenter(@Header("Authorization") String token, @Body User user);

    //Change driver vehicle
    @POST("change-driver-vehicle")
    Call<ResponseBody> changeDriverVehicle(@Header("Authorization") String token, @Body User user);

    //Get drivers for a service center
    @POST("get-center-drivers")
    Call<List<User>> getServiceCenterDrivers(@Header("Authorization") String token, @Body ServiceCentre serviceCentre);

    //Get packages for a service center
    @POST("get-center-packages")
    Call<List<Mail>> getServiceCenterPackages(@Header("Authorization") String token, @Body ServiceCentre serviceCentre);

    //Get all packages
    @GET("get-all-packages")
    Call<List<Mail>> getAllPackages(@Header("Authorization") String token);

    //Delete agent
    @POST("delete-agent")
    Call<ResponseBody> deleteAgent(@Header("Authorization") String token, @Body User user);

    //Delete agent
    @POST("delete-driver")
    Call<ResponseBody> deleteDriver(@Header("Authorization") String token, @Body User user);

    //Update agent service center
    @POST("update-center-agent")
    Call<ResponseBody> updateCenterAgent(@Header("Authorization") String token, @Body User user);

    //Get all vehicles
    @GET("vehicles")
    Call<List<Vehicle>> getAllVehicles(@Header("Authorization") String token);

    //Set vehicle available
    @POST("set-vehicle-available")
    Call<ResponseBody> setVehicleAvailable(@Header("Authorization") String token, @Body Vehicle vehicle);

    //Set vehicle Unavailable
    @POST("set-vehicle-unavailable")
    Call<ResponseBody> setVehicleUnavailable(@Header("Authorization") String token, @Body Vehicle vehicle);

    //Set vehicle Blacklisted
    @POST("set-vehicle-blacklist")
    Call<ResponseBody> setVehicleBlacklist(@Header("Authorization") String token, @Body Vehicle vehicle);

    //Remove vehicle Blacklisted
    @POST("remove-vehicle-blacklist")
    Call<ResponseBody> removeVehicleBlacklist(@Header("Authorization") String token, @Body Vehicle vehicle);

}
