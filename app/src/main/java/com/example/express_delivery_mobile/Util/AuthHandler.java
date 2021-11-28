package com.example.express_delivery_mobile.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.auth0.android.jwt.JWT;
import com.example.express_delivery_mobile.LoginActivity;

public class AuthHandler {
    public static String validate(Context context, String userRole){

        //Get authorization token and user role from shared preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", null);
        String role = sharedPreferences.getString("role", null);

        Log.i("USER_ROLE", userRole);
        if(token != null )Log.i("TOKEN", token);
        if(role != null) Log.i("ROLE", role);


        if(token!=null){
            JWT jwt = new JWT(token);
            boolean isExpired = jwt.isExpired(10);

            //Check if JWT has expired or user role mismatch
            if(isExpired || !role.equals(userRole)){

                if(isExpired) {
                    //Log user out
                    AuthHandler.logout(context);

                    //Direct user to login activity
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);

                    return "expired";

                }else if(userRole.equals("all")) {
                    //If validating for an intent that allows both users to use
                    return role;

                }else{
                    //Direct user to login activity
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);

                    return "unauthorized";
                }
            }

        }else{
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);

            return "unauthorized";
        }

        return null;
    }

    //Method to logout user
    public static void logout(Context context){

        //Reset authorization token and user role from shared preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", null);
        editor.putString("role", null);
        editor.apply();

        //Send user to login screen
        Intent accountIntent = new Intent(context, LoginActivity.class);
        accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(accountIntent);
        ((Activity)context).finish();
    }
}
