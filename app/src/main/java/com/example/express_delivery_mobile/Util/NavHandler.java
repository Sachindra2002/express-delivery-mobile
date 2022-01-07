package com.example.express_delivery_mobile.Util;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.example.express_delivery_mobile.AddAgentActivity;
import com.example.express_delivery_mobile.AddDriverActivity;
import com.example.express_delivery_mobile.AdminActivity;
import com.example.express_delivery_mobile.AdminDriverListActivity;
import com.example.express_delivery_mobile.AdminPackageListActivity;
import com.example.express_delivery_mobile.AgentDriverListActivity;
import com.example.express_delivery_mobile.AgentListActivity;
import com.example.express_delivery_mobile.DisputesActivity;
import com.example.express_delivery_mobile.EditAdminDetailsActivity;
import com.example.express_delivery_mobile.NewShipmentsActivity;
import com.example.express_delivery_mobile.R;
import com.example.express_delivery_mobile.ServiceCenterListActivity;
import com.example.express_delivery_mobile.VehicleListActivity;
import com.example.express_delivery_mobile.ViewLocalStoragePackagesActivity;

public class NavHandler {

    public static void handleCustomerNav(MenuItem item, Context context) {
        switch (item.getItemId()) {
            case R.id.nav_logout: {
                //Logout Button
                AuthHandler.logout(context);
                break;
            }
        }
    }

    public static void handleDriverNav(MenuItem item, Context context) {
        switch (item.getItemId()) {
            case R.id.nav_logout: {
                //Logout Button
                AuthHandler.logout(context);
                break;
            }
            case R.id.nav_local_packages: {
                Intent intent = new Intent(context, ViewLocalStoragePackagesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        }
    }

    public static void handleAgentNav(MenuItem item, Context context) {
        switch (item.getItemId()) {
            case R.id.nav_logout: {
                //Logout Button
                AuthHandler.logout(context);
                break;
            }
            case R.id.nav_shipments_agent: {
                Intent intent = new Intent(context, NewShipmentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_drivers: {
                Intent intent = new Intent(context, AgentDriverListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
        }
    }

    public static void handleAdminNav(MenuItem item, Context context) {
        switch (item.getItemId()) {

            case R.id.nav_home_admin: {
                Intent intent = new Intent(context, AdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_shipments_admin: {
                Intent intent = new Intent(context, AdminPackageListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_logout: {
                //Logout Button
                AuthHandler.logout(context);
                break;
            }
            case R.id.nav_centers: {
                Intent intent = new Intent(context, ServiceCenterListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_drivers: {
                Intent intent = new Intent(context, AdminDriverListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_add_driver: {
                Intent intent = new Intent(context, AddDriverActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_agents: {
                Intent intent = new Intent(context, AgentListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_add_agent: {
                Intent intent = new Intent(context, AddAgentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_disputes: {
                Intent intent = new Intent(context, DisputesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_vehicles: {
                Intent intent = new Intent(context, VehicleListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_settings_admin: {
                Intent intent = new Intent(context, EditAdminDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
        }
    }
}
