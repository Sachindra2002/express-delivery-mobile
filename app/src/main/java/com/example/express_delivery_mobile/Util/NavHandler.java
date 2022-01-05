package com.example.express_delivery_mobile.Util;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.example.express_delivery_mobile.AdminActivity;
import com.example.express_delivery_mobile.R;
import com.example.express_delivery_mobile.ServiceCenterListActivity;
import com.example.express_delivery_mobile.ViewLocalStoragePackagesActivity;

public class NavHandler {

    public static void handleCustomerNav(MenuItem item, Context context) {
        switch (item.getItemId()){
            case R.id.nav_logout: {
                //Logout Button
                AuthHandler.logout(context);
                break;
            }
        }
    }

    public static void handleDriverNav(MenuItem item, Context context){
        switch (item.getItemId()){
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

    public static void handleAgentNav(MenuItem item, Context context){
        switch (item.getItemId()){
            case R.id.nav_logout: {
                //Logout Button
                AuthHandler.logout(context);
                break;
            }
        }
    }

    public static void handleAdminNav(MenuItem item, Context context){
        switch (item.getItemId()){
            case R.id.nav_logout: {
                //Logout Button
                AuthHandler.logout(context);
                break;
            }

            case R.id.nav_centers: {
                Intent intent = new Intent(context, ServiceCenterListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        }
    }
}
