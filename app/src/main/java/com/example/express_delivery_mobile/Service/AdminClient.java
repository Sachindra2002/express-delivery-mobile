package com.example.express_delivery_mobile.Service;

import com.example.express_delivery_mobile.Model.Disputes;
import com.example.express_delivery_mobile.Model.Documents;
import com.example.express_delivery_mobile.Model.DriverDetail;
import com.example.express_delivery_mobile.Model.Inquiry;
import com.example.express_delivery_mobile.Model.ServiceCentre;
import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.Model.Vehicle;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
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
}
