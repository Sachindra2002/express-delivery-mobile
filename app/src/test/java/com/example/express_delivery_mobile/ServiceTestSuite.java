package com.example.express_delivery_mobile;

import static org.junit.Assert.assertTrue;

import com.example.express_delivery_mobile.Model.LoginCredentials;
import com.example.express_delivery_mobile.Model.Mail;
import com.example.express_delivery_mobile.Model.ServiceCentre;
import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.Model.Vehicle;
import com.example.express_delivery_mobile.Service.AdminClient;
import com.example.express_delivery_mobile.Service.AgentClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.example.express_delivery_mobile.Service.UserClient;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ServiceTestSuite {

    private final String ADMIN_USERNAME = "sachindra2002@gmail.com";
    private final String ADMIN_PASSWORD = "sachindraRodrigo";

    private final String AGENT_USERNAME = "david@gmail.com";
    private final String AGENT_PASSWORD = "david@gmail.com";

    private final String DRIVER_USERNAME = "trevor@gmail.com";
    private final String DRIVER_PASSWORD = "trevor@gmail.com";

    private final String CUSTOMER_USERNAME = "lahiru@gmail.com";
    private final String CUSTOMER_PASSWORD = "lahiruSilva";

    @Test
    public void testLoginAdmin() {
        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(ADMIN_USERNAME, ADMIN_PASSWORD);
        Call<User> call = userClient.login(loginCredentials);

        try {
            Response<User> response = call.execute();
            User user = response.body();

            assertTrue("Login as Admin", response.isSuccessful() && user.getUserRole().equals("admin"));

            System.out.println("Login as Admin:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoginAgent() {
        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(AGENT_USERNAME, AGENT_PASSWORD);
        Call<User> call = userClient.login(loginCredentials);

        try {
            Response<User> response = call.execute();
            User user = response.body();

            assertTrue("Login as Agent", response.isSuccessful() && user.getUserRole().equals("agent"));

            System.out.println("Login as Agent:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoginDriver() {
        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(DRIVER_USERNAME, DRIVER_PASSWORD);
        Call<User> call = userClient.login(loginCredentials);

        try {
            Response<User> response = call.execute();
            User user = response.body();

            assertTrue("Login as Driver", response.isSuccessful() && user.getUserRole().equals("driver"));

            System.out.println("Login as Driver:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoginCustomer() {
        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(CUSTOMER_USERNAME, CUSTOMER_PASSWORD);
        Call<User> call = userClient.login(loginCredentials);

        try {
            Response<User> response = call.execute();
            User user = response.body();

            assertTrue("Login as Customer", response.isSuccessful() && user.getUserRole().equals("customer"));

            System.out.println("Login as Customer:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoginWithInvalidUsername() {
        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials("std", AGENT_PASSWORD);
        Call<User> call = userClient.login(loginCredentials);

        try {
            Response<User> response = call.execute();

            assertTrue("Login with Invalid Username", response.code() == 403);

            System.out.println("Login with Invalid Username:\tPASSED");

        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    @Test
    public void testLoginWithInvalidPassword() {
        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(AGENT_USERNAME, "pass");
        Call<User> call = userClient.login(loginCredentials);

        try {
            Response<User> response = call.execute();

            assertTrue("Login with Invalid Password", response.code() == 403);

            System.out.println("Login with Invalid Password:\tPASSED");

        } catch (IOException e) {
            //e.printStackTrace();
        }

    }

    @Test
    public void testGetAllDrivers() {
        AdminClient adminClient = RetrofitClientInstance.getRetrofitInstance().create(AdminClient.class);

        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(ADMIN_USERNAME, ADMIN_PASSWORD);

        Call<User> login_call = userClient.login(loginCredentials);

        try {

            Response<User> login_response = login_call.execute();
            String token = login_response.body().getToken();

            Call<List<User>> call = adminClient.getDrivers(token);

            Response<List<User>> response = call.execute();
            List<User> drivers = response.body();

            assertTrue("Get All Drivers", response.isSuccessful());

            System.out.println("Get All Drivers:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAllAgents() {
        AdminClient adminClient = RetrofitClientInstance.getRetrofitInstance().create(AdminClient.class);

        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(ADMIN_USERNAME, ADMIN_PASSWORD);

        Call<User> login_call = userClient.login(loginCredentials);

        try {

            Response<User> login_response = login_call.execute();
            String token = login_response.body().getToken();

            Call<List<User>> call = adminClient.getAgents(token);

            Response<List<User>> response = call.execute();
            List<User> agents = response.body();

            assertTrue("Get All Agents", response.isSuccessful());

            System.out.println("Get All Agents:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAllServiceCenters() {
        AdminClient adminClient = RetrofitClientInstance.getRetrofitInstance().create(AdminClient.class);

        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(ADMIN_USERNAME, ADMIN_PASSWORD);

        Call<User> login_call = userClient.login(loginCredentials);

        try {

            Response<User> login_response = login_call.execute();
            String token = login_response.body().getToken();

            Call<List<ServiceCentre>> call = adminClient.getServiceCenters(token);

            Response<List<ServiceCentre>> response = call.execute();
            List<ServiceCentre> centers = response.body();

            assertTrue("Get All Service Centers", response.isSuccessful());

            System.out.println("Get All Service Centers:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAllVehicles() {
        AdminClient adminClient = RetrofitClientInstance.getRetrofitInstance().create(AdminClient.class);

        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(ADMIN_USERNAME, ADMIN_PASSWORD);

        Call<User> login_call = userClient.login(loginCredentials);

        try {

            Response<User> login_response = login_call.execute();
            String token = login_response.body().getToken();

            Call<List<Vehicle>> call = adminClient.getAllVehicles(token);

            Response<List<Vehicle>> response = call.execute();
            List<Vehicle> vehicles = response.body();

            assertTrue("Get All Vehicles", response.isSuccessful());

            System.out.println("Get All Vehicles:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAllPackages() {
        AdminClient adminClient = RetrofitClientInstance.getRetrofitInstance().create(AdminClient.class);

        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(ADMIN_USERNAME, ADMIN_PASSWORD);

        Call<User> login_call = userClient.login(loginCredentials);

        try {

            Response<User> login_response = login_call.execute();
            String token = login_response.body().getToken();

            Call<List<Mail>> call = adminClient.getAllPackages(token);

            Response<List<Mail>> response = call.execute();
            List<Mail> mailList = response.body();

            assertTrue("Get All Packages", response.isSuccessful());

            System.out.println("Get All Packages:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetServiceCenterPackages() {
        AgentClient agentClient = RetrofitClientInstance.getRetrofitInstance().create(AgentClient.class);

        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(AGENT_USERNAME, AGENT_PASSWORD);

        Call<User> login_call = userClient.login(loginCredentials);

        try {

            Response<User> login_response = login_call.execute();
            String token = login_response.body().getToken();

            Call<List<Mail>> call = agentClient.getAllAcceptedMails(token);

            Response<List<Mail>> response = call.execute();
            List<Mail> mailList = response.body();

            assertTrue("Get All Service Center Packages", response.isSuccessful());

            System.out.println("Get All Service Center Packages:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetServiceCenterDrivers() {
        AgentClient agentClient = RetrofitClientInstance.getRetrofitInstance().create(AgentClient.class);

        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(AGENT_USERNAME, AGENT_PASSWORD);

        Call<User> login_call = userClient.login(loginCredentials);

        try {

            Response<User> login_response = login_call.execute();
            String token = login_response.body().getToken();

            Call<List<User>> call = agentClient.getDrivers(token);

            Response<List<User>> response = call.execute();
            List<User> drivers = response.body();

            assertTrue("Get All Service Center Drivers", response.isSuccessful());

            System.out.println("Get All Service Center Drivers:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
