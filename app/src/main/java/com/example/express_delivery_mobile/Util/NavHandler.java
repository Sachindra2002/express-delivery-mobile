package com.example.express_delivery_mobile.Util;

import android.content.Context;
import android.view.MenuItem;

import com.example.express_delivery_mobile.R;

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
        }
    }
}
